package com.backgroundthread

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.facebook.react.ReactPackage
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.ReactInstanceEventListener
import com.facebook.react.bridge.JSBundleLoader
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.common.annotations.UnstableReactNativeAPI
import com.facebook.react.defaults.DefaultComponentsRegistry
import com.facebook.react.defaults.DefaultReactHostDelegate
import com.facebook.react.defaults.DefaultTurboModuleManagerDelegate
import com.facebook.react.fabric.ComponentFactory
import com.facebook.react.runtime.ReactHostImpl
import com.facebook.react.runtime.hermes.HermesInstance
import com.facebook.react.shell.MainReactPackage
import java.io.File
import java.lang.ref.WeakReference

/**
 * Singleton manager for the background React Native runtime.
 * Mirrors iOS BackgroundThreadManager.
 *
 * Responsibilities:
 * - Manages background ReactHostImpl lifecycle
 * - Installs SharedBridge into main and background runtimes
 * - Cross-runtime communication via SharedRPC onWrite notifications
 */
class BackgroundThreadManager private constructor() {

    private var bgReactHost: ReactHostImpl? = null
    private var reactPackages: List<ReactPackage> = emptyList()

    // Tracks the last resumed Activity so we can replay it onto the bg
    // ReactContext as soon as the bg host finishes initializing (covers the
    // cold-start race where Activity resumes before bg host is ready).
    private var lastResumedActivityRef: WeakReference<Activity> = WeakReference(null)

    @Volatile
    private var bgRuntimePtr: Long = 0

    @Volatile
    private var mainRuntimePtr: Long = 0
    private var mainReactContext: ReactApplicationContext? = null
    private var isStarted = false

    companion object {
        private const val MODULE_NAME = "background"

        init {
            System.loadLibrary("background_thread")
        }

        @Volatile
        private var instance: BackgroundThreadManager? = null

        @JvmStatic
        fun getInstance(): BackgroundThreadManager {
            return instance ?: synchronized(this) {
                instance ?: BackgroundThreadManager().also { instance = it }
            }
        }
    }

    // ── JNI declarations ────────────────────────────────────────────────────

    private external fun nativeInstallSharedBridge(runtimePtr: Long, isMain: Boolean)
    private external fun nativeSetupErrorHandler(runtimePtr: Long)
    private external fun nativeDestroy()
    private external fun nativeExecuteWork(runtimePtr: Long, workId: Long)

    // ── SharedBridge ────────────────────────────────────────────────────────

    /**
     * Install SharedBridge HostObject into the main (UI) runtime.
     * Call this from installSharedBridge().
     */
    fun setReactPackages(packages: List<ReactPackage>) {
        reactPackages = packages.toList()
    }

    fun installSharedBridgeInMainRuntime(context: ReactApplicationContext) {
        mainReactContext = context
        context.runOnJSQueueThread {
            try {
                val ptr = context.javaScriptContextHolder?.get() ?: 0L
                if (ptr != 0L) {
                    mainRuntimePtr = ptr
                    nativeInstallSharedBridge(ptr, true)
                    BTLogger.info("SharedBridge installed in main runtime")
                } else {
                    BTLogger.warn("Main runtime pointer is 0, cannot install SharedBridge")
                }
            } catch (e: Exception) {
                BTLogger.error("Error installing SharedBridge in main runtime: ${e.message}")
            }
        }
    }

    // ── Background runner lifecycle ─────────────────────────────────────────

    private fun isRemoteBundleUrl(entryURL: String): Boolean {
        return entryURL.startsWith("http://") || entryURL.startsWith("https://")
    }

    private fun resolveLocalBundlePath(entryURL: String): String? {
        if (entryURL.startsWith("file://")) {
            return Uri.parse(entryURL).path
        }
        if (entryURL.startsWith("/")) {
            return entryURL
        }
        return null
    }

    /**
     * Creates a JSBundleLoader that loads two bundles sequentially from Android assets:
     * first the common bundle (polyfills + shared modules), then the
     * entry-specific bundle (entry-only modules + require(entryId)).
     */
    private fun createSequentialAssetBundleLoader(
        appContext: android.content.Context,
        commonAssetName: String,
        entryAssetName: String
    ): JSBundleLoader {
        return object : JSBundleLoader() {
            override fun loadScript(delegate: com.facebook.react.bridge.JSBundleLoaderDelegate): String {
                val totalStart = System.nanoTime()

                // Step 1: Load common bundle (polyfills + shared modules)
                val commonStart = System.nanoTime()
                delegate.loadScriptFromAssets(appContext.assets, "assets://$commonAssetName", false)
                val commonMs = (System.nanoTime() - commonStart) / 1_000_000.0
                BTLogger.info("[SplitBundle] common bundle loaded from assets in ${String.format("%.1f", commonMs)}ms: $commonAssetName")

                // Step 2: Load entry-specific bundle
                val entryStart = System.nanoTime()
                delegate.loadScriptFromAssets(appContext.assets, "assets://$entryAssetName", false)
                val entryMs = (System.nanoTime() - entryStart) / 1_000_000.0
                BTLogger.info("[SplitBundle] entry bundle loaded from assets in ${String.format("%.1f", entryMs)}ms: $entryAssetName")

                val totalMs = (System.nanoTime() - totalStart) / 1_000_000.0
                BTLogger.info("[SplitBundle] sequential asset load total: ${String.format("%.1f", totalMs)}ms (common=${String.format("%.1f", commonMs)}ms + entry=${String.format("%.1f", entryMs)}ms)")

                return "assets://$entryAssetName"
            }
        }
    }

    /**
     * Creates a JSBundleLoader that loads two bundles sequentially from local files:
     * first the common bundle, then the entry-specific bundle.
     */
    private fun createSequentialFileBundleLoader(
        commonPath: String,
        entryPath: String,
        entrySourceURL: String
    ): JSBundleLoader {
        return object : JSBundleLoader() {
            override fun loadScript(delegate: com.facebook.react.bridge.JSBundleLoaderDelegate): String {
                val totalStart = System.nanoTime()

                // Step 1: Load common bundle (polyfills + shared modules)
                val commonFile = File(commonPath)
                if (!commonFile.exists()) {
                    BTLogger.error("Common bundle file does not exist: $commonPath")
                    throw RuntimeException("Common bundle file does not exist: $commonPath")
                }
                BTLogger.info("[SplitBundle] common bundle file: ${commonFile.length() / 1024}KB")
                val commonStart = System.nanoTime()
                delegate.loadScriptFromFile(commonFile.absolutePath, "common.bundle", false)
                val commonMs = (System.nanoTime() - commonStart) / 1_000_000.0
                BTLogger.info("[SplitBundle] common bundle loaded from file in ${String.format("%.1f", commonMs)}ms: $commonPath")

                // Step 2: Load entry-specific bundle
                val entryFile = File(entryPath)
                if (!entryFile.exists()) {
                    BTLogger.error("Entry bundle file does not exist: $entryPath")
                    throw RuntimeException("Entry bundle file does not exist: $entryPath")
                }
                BTLogger.info("[SplitBundle] entry bundle file: ${entryFile.length() / 1024}KB")
                val entryStart = System.nanoTime()
                delegate.loadScriptFromFile(entryFile.absolutePath, entrySourceURL, false)
                val entryMs = (System.nanoTime() - entryStart) / 1_000_000.0
                BTLogger.info("[SplitBundle] entry bundle loaded from file in ${String.format("%.1f", entryMs)}ms: $entryPath")

                val totalMs = (System.nanoTime() - totalStart) / 1_000_000.0
                BTLogger.info("[SplitBundle] sequential file load total: ${String.format("%.1f", totalMs)}ms (common=${String.format("%.1f", commonMs)}ms + entry=${String.format("%.1f", entryMs)}ms)")

                return entrySourceURL
            }
        }
    }

    /**
     * Check if common.bundle exists in the Android assets directory.
     */
    private fun hasCommonBundleInAssets(appContext: android.content.Context): Boolean {
        return try {
            appContext.assets.open("common.bundle").close()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Resolve the common bundle path for OTA (file-based) loading.
     * Looks for common.bundle in the same directory as the entry bundle.
     */
    private fun resolveCommonBundlePath(entryBundlePath: String): String? {
        val entryFile = File(entryBundlePath)
        val parentDir = entryFile.parentFile ?: return null
        val commonFile = File(parentDir, "common.bundle")
        return if (commonFile.exists()) commonFile.absolutePath else null
    }

    private fun createDownloadedBundleLoader(appContext: android.content.Context, entryURL: String): JSBundleLoader {
        return object : JSBundleLoader() {
            override fun loadScript(delegate: com.facebook.react.bridge.JSBundleLoaderDelegate): String {
                val tempFile = File(appContext.cacheDir, "background.bundle")
                try {
                    java.net.URL(entryURL).openStream().use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    BTLogger.info("Background bundle downloaded to ${tempFile.absolutePath}")
                } catch (e: Exception) {
                    BTLogger.error("Failed to download background bundle: ${e.message}")
                    throw RuntimeException("Failed to download background bundle from $entryURL", e)
                }
                delegate.loadScriptFromFile(tempFile.absolutePath, entryURL, false)
                return entryURL
            }
        }
    }

    private fun createLocalFileBundleLoader(localPath: String, sourceURL: String): JSBundleLoader {
        return object : JSBundleLoader() {
            override fun loadScript(delegate: com.facebook.react.bridge.JSBundleLoaderDelegate): String {
                val bundleFile = File(localPath)
                if (!bundleFile.exists()) {
                    BTLogger.error("Background bundle file does not exist: $localPath")
                    throw RuntimeException("Background bundle file does not exist: $localPath")
                }
                delegate.loadScriptFromFile(bundleFile.absolutePath, sourceURL, false)
                return sourceURL
            }
        }
    }

    @OptIn(UnstableReactNativeAPI::class)
    fun startBackgroundRunnerWithEntryURL(context: ReactApplicationContext, entryURL: String) {
        if (isStarted) {
            BTLogger.warn("Background runner already started")
            return
        }
        val bgStartTime = System.nanoTime()
        BTLogger.info("[SplitBundle] background runner starting with entryURL: $entryURL")

        val appContext = context.applicationContext
        val packages =
            if (reactPackages.isNotEmpty()) {
                reactPackages
            } else {
                BTLogger.warn("No ReactPackages registered for background runtime; call setReactPackages(...) from host before start. Falling back to MainReactPackage only.")
                listOf(MainReactPackage())
            }

        val localBundlePath = resolveLocalBundlePath(entryURL)
        val bundleLoader =
            when {
                // Debug mode: remote URL — use single bundle (Metro dev server)
                isRemoteBundleUrl(entryURL) -> createDownloadedBundleLoader(appContext, entryURL)

                // OTA / local file path — try sequential loading with common bundle
                localBundlePath != null -> {
                    val commonPath = resolveCommonBundlePath(localBundlePath)
                    if (commonPath != null) {
                        BTLogger.info("Using sequential file bundle loader: common=$commonPath, entry=$localBundlePath")
                        createSequentialFileBundleLoader(commonPath, localBundlePath, entryURL)
                    } else {
                        BTLogger.info("No common bundle found for OTA path, using single bundle: $localBundlePath")
                        createLocalFileBundleLoader(localBundlePath, entryURL)
                    }
                }

                // Assets-based loading — try sequential loading with common.bundle in assets
                entryURL.startsWith("assets://") -> {
                    val entryAssetName = entryURL.removePrefix("assets://")
                    if (hasCommonBundleInAssets(appContext)) {
                        BTLogger.info("Using sequential asset bundle loader: common=common.bundle, entry=$entryAssetName")
                        createSequentialAssetBundleLoader(appContext, "common.bundle", entryAssetName)
                    } else {
                        BTLogger.info("No common.bundle in assets, using single bundle: $entryURL")
                        JSBundleLoader.createAssetLoader(appContext, entryURL, true)
                    }
                }

                // Bare filename (e.g. "background.bundle") — treat as asset
                else -> {
                    if (hasCommonBundleInAssets(appContext)) {
                        BTLogger.info("Using sequential asset bundle loader: common=common.bundle, entry=$entryURL")
                        createSequentialAssetBundleLoader(appContext, "common.bundle", entryURL)
                    } else {
                        BTLogger.info("No common.bundle in assets, using single bundle: assets://$entryURL")
                        JSBundleLoader.createAssetLoader(appContext, "assets://$entryURL", true)
                    }
                }
            }

        val delegate = DefaultReactHostDelegate(
            jsMainModulePath = MODULE_NAME,
            jsBundleLoader = bundleLoader,
            reactPackages = packages,
            jsRuntimeFactory = HermesInstance(),
            turboModuleManagerDelegateBuilder = DefaultTurboModuleManagerDelegate.Builder(),
        )

        val componentFactory = ComponentFactory()
        DefaultComponentsRegistry.register(componentFactory)

        val host = ReactHostImpl(
            appContext,
            delegate,
            componentFactory,
            true,  /* allowPackagerServerAccess */
            false, /* useDevSupport */
        )
        bgReactHost = host

        host.addReactInstanceEventListener(object : ReactInstanceEventListener {
            override fun onReactContextInitialized(context: ReactContext) {
                val initMs = (System.nanoTime() - bgStartTime) / 1_000_000.0
                BTLogger.info("[SplitBundle] background ReactContext initialized in ${String.format("%.1f", initMs)}ms")
                // Replay the most recent Activity resume so TurboModules on the
                // bg host can see getCurrentActivity()/ActivityEventListeners
                // from the very first call, even when the bg host finishes
                // initializing after the Activity is already resumed.
                replayLastResumedActivityOnUi()
                context.runOnJSQueueThread {
                    try {
                        val ptr = context.javaScriptContextHolder?.get() ?: 0L
                        if (ptr != 0L) {
                            bgRuntimePtr = ptr
                            nativeInstallSharedBridge(ptr, false)
                            nativeSetupErrorHandler(ptr)
                            BTLogger.info("SharedBridge and error handler installed in background runtime")
                        } else {
                            BTLogger.error("Background runtime pointer is 0")
                        }
                    } catch (e: Exception) {
                        BTLogger.error("Error installing bindings in background runtime: ${e.message}")
                    }
                }
            }
        })

        host.start()
        isStarted = true
    }

    /**
     * Called from C++ RuntimeExecutor to schedule work on the correct JS thread.
     * Routes to main or background runtime's JS queue thread, then calls nativeExecuteWork.
     */
    @DoNotStrip
    fun scheduleOnJSThread(isMain: Boolean, workId: Long) {
        val context = if (isMain) mainReactContext else bgReactHost?.currentReactContext
        BTLogger.info("scheduleOnJSThread: isMain=$isMain, workId=$workId, context=${context != null}")
        if (context == null) {
            BTLogger.error("scheduleOnJSThread: context is null! isMain=$isMain, mainCtx=${mainReactContext != null}, bgHost=${bgReactHost != null}, bgCtx=${bgReactHost?.currentReactContext != null}")
        }
        context?.runOnJSQueueThread {
            // Re-read ptr inside the block — if a reload happened between
            // scheduling and execution, the old ptr may be stale.
            val ptr = if (isMain) mainRuntimePtr else bgRuntimePtr
            BTLogger.info("scheduleOnJSThread runOnJSQueueThread: isMain=$isMain, workId=$workId, ptr=$ptr")
            if (ptr != 0L) {
                try {
                    nativeExecuteWork(ptr, workId)
                } catch (e: Exception) {
                    BTLogger.error("Error executing work on JS thread: ${e.message}")
                }
            } else {
                BTLogger.error("scheduleOnJSThread: ptr is 0! isMain=$isMain")
            }
        }
    }

    // ── Segment Registration (Phase 2.5 spike) ─────────────────────────────

    /**
     * Register a HBC segment in the background runtime.
     * Uses CatalystInstance.registerSegment() on the background ReactContext.
     *
     * @param segmentId The segment ID to register
     * @param path Absolute file path to the .seg.hbc file
     * @throws IllegalStateException if background runtime is not started
     * @throws IllegalArgumentException if segment file does not exist
     */
    /**
     * Register a HBC segment in the background runtime with completion callback.
     * Dispatches to the background JS queue thread and invokes the callback
     * only after registerSegment has actually executed.
     *
     * @param segmentId The segment ID to register
     * @param path Absolute file path to the .seg.hbc file
     * @param onComplete Called with null on success, or an Exception on failure
     */
    fun registerSegmentInBackground(segmentId: Int, path: String, onComplete: (Exception?) -> Unit) {
        if (!isStarted) {
            onComplete(IllegalStateException("Background runtime not started"))
            return
        }

        val file = File(path)
        if (!file.exists()) {
            onComplete(IllegalArgumentException("Segment file not found: $path"))
            return
        }

        val context = bgReactHost?.currentReactContext
        if (context == null) {
            onComplete(IllegalStateException("Background ReactContext not available"))
            return
        }

        // Use ReactContext.registerSegment which works in both bridge
        // and bridgeless modes.
        try {
            context.registerSegment(segmentId, path) {
                BTLogger.info("Segment registered in background runtime: id=$segmentId, path=$path")
                onComplete(null)
            }
        } catch (e: Exception) {
            BTLogger.error("Failed to register segment in background runtime: ${e.message}")
            onComplete(e)
        }
    }

    // ── Activity lifecycle bridge (selective, reflection-based) ─────────────
    //
    // Goal: let a small, explicit allowlist of native modules on the bg
    // ReactHost observe Activity-related state (getCurrentActivity(),
    // onActivityResult, onNewIntent, and — for the same allowlist —
    // onHostResume/onHostPause/onHostDestroy) WITHOUT triggering any side
    // effects on other modules that happen to live on the same ReactContext.
    //
    // Why not use ReactHost.onHostResume / onActivityResult directly?
    //   Those internally fan out to ALL LifecycleEventListeners and
    //   ActivityEventListeners registered on the ReactContext, which would
    //   cause every bg TurboModule that tracks host lifecycle (BroadcastReceivers,
    //   sensors, keyboard observers, etc.) to receive a second set of callbacks
    //   in addition to the UI host. That doubles resource registration, opens
    //   requestCode collisions on Activity results, and can tear down the bg
    //   ReactContext during rotation. We need fine-grained control, which RN
    //   does not expose, so we operate on the underlying fields directly.
    //
    // What we actually do:
    //   - Reflect-write bg ReactContext's `mCurrentActivity` so
    //     getCurrentActivity() returns the correct Activity for modules that
    //     bother to query it. Modules that don't query it are unaffected.
    //   - Read `mActivityEventListeners` via reflection and invoke ONLY the
    //     listeners whose class FQCN matches an entry in
    //     `bgActivityBridgeListenerClassAllowlist` (for onActivityResult/onNewIntent).
    //   - Read `mLifecycleEventListeners` via reflection and invoke
    //     onHostResume/onHostPause/onHostDestroy on listeners whose class
    //     FQCN matches the same allowlist. Non-allowlisted listeners are
    //     never fired on the bg host — preserving the pre-existing baseline
    //     that bg was "never resumed".
    //   - Deliberately do NOT touch `mLifecycleState` — setting it to RESUMED
    //     would cause `addLifecycleEventListener(...)` to auto-fire
    //     `onHostResume()` on EVERY newly-registered listener (including
    //     non-allowlisted ones), reintroducing the double-dispatch we're
    //     trying to avoid. Instead we fire onHostResume manually on each
    //     dispatchActivityResumed, which covers the common case; a listener
    //     registered strictly between two resume events will catch up on
    //     the next cycle.
    //
    // Trade-off:
    //   We rely on RN-internal field names `mCurrentActivity`,
    //   `mActivityEventListeners`, `mLifecycleEventListeners` on
    //   `com.facebook.react.bridge.ReactContext`. An RN upgrade that
    //   renames or restructures these fields will cause reflection to fail
    //   (caught and logged via BTLogger → OneKeyLog). Mitigate with a
    //   dev-build smoke assertion at the call site.
    //
    // Thread safety:
    //   ReactContext.onHostResume / onActivityResult are @ThreadConfined(UI);
    //   we match that by bouncing to the main looper. The underlying sets
    //   are CopyOnWriteArraySet, so iterating them off-thread would still be
    //   safe, but UI-thread is the documented contract and we stick to it.

    // ── Allowlist (registered externally by the host app) ───────────────────
    //
    // FQCN prefixes of ActivityEventListener / LifecycleEventListener
    // implementations that are allowed to receive Activity-bound events
    // (onActivityResult, onNewIntent, onHostResume/Pause/Destroy) on the
    // bg ReactHost. Anything not matching a registered prefix is fully
    // ignored on bg, preserving the pre-existing baseline.
    //
    // The bg-thread module deliberately ships an EMPTY default — the host
    // application is the only place that knows which third-party modules
    // are bg-eligible. Register entries early in Application.onCreate via
    // addBgActivityBridgeListenerClassPrefix(...) so the allowlist is populated
    // before the first Activity lifecycle callback can fire.
    //
    // Adding a prefix is a cross-runtime change. Verify:
    //   1. The module's listener is idempotent and safe to fire.
    //   2. ActivityResult requestCodes do not collide with other modules.
    //   3. Any LifecycleEventListener side-effects are double-host safe.
    @Volatile
    private var bgActivityBridgeListenerClassAllowlist: Set<String> = emptySet()

    /** Add a single FQCN-prefix entry to the allowlist (idempotent). */
    @Synchronized
    fun addBgActivityBridgeListenerClassPrefix(prefix: String) {
        if (prefix.isEmpty()) return
        if (bgActivityBridgeListenerClassAllowlist.contains(prefix)) return
        bgActivityBridgeListenerClassAllowlist = bgActivityBridgeListenerClassAllowlist + prefix
        BTLogger.info("addBgActivityBridgeListenerClassPrefix: $prefix " +
            "(total=${bgActivityBridgeListenerClassAllowlist.size})")
    }

    /** Replace the allowlist wholesale. Intended for test setup / reload. */
    @Synchronized
    fun setBgActivityBridgeListenerClassAllowlist(prefixes: Set<String>) {
        bgActivityBridgeListenerClassAllowlist = prefixes.toSet()
        BTLogger.info("setBgActivityBridgeListenerClassAllowlist: ${prefixes.size} entries")
    }

    /** Inspect the current allowlist (snapshot). */
    fun getBgActivityBridgeListenerClassAllowlist(): Set<String> =
        bgActivityBridgeListenerClassAllowlist

    fun dispatchActivityResumed(activity: Activity) {
        lastResumedActivityRef = WeakReference(activity)
        runOnUiThread {
            writeBgCurrentActivity(activity)
            dispatchLifecycleEventToAllowlisted(LifecycleEvent.RESUME)
        }
    }

    fun dispatchActivityPaused(activity: Activity) {
        // Keep mCurrentActivity — Activity is still valid until destroyed,
        // and clearing it between pause/resume would flap getCurrentActivity().
        // LifecycleEventListener.onHostPause() IS fired for allowlisted
        // modules so they can quiesce work (mirrors RN's UI-host behaviour).
        runOnUiThread {
            dispatchLifecycleEventToAllowlisted(LifecycleEvent.PAUSE)
        }
    }

    fun dispatchActivityDestroyed(activity: Activity) {
        if (lastResumedActivityRef.get() === activity) {
            lastResumedActivityRef = WeakReference(null)
        }
        runOnUiThread {
            val ctx = bgReactHost?.currentReactContext ?: return@runOnUiThread
            // Only clear if the bg context's tracked Activity IS the one
            // being destroyed. Guards against a stale clear when the host
            // Activity is swapped (e.g. multi-Activity deep-link flows).
            if (ctx.currentActivity === activity) {
                writeBgCurrentActivity(null)
                dispatchLifecycleEventToAllowlisted(LifecycleEvent.DESTROY)
            }
        }
    }

    private enum class LifecycleEvent { RESUME, PAUSE, DESTROY }

    /**
     * Iterate bg ReactContext's LifecycleEventListener set and fire the
     * requested callback on listeners whose class FQCN matches the
     * allowlist. Modules outside the allowlist are completely unaffected
     * (they stay on the pre-existing "never-resumed on bg" baseline).
     *
     * Note on edge case: a LifecycleEventListener registered AFTER the
     * corresponding dispatch event will miss that event, because we don't
     * touch mLifecycleState (doing so would cause RN's
     * addLifecycleEventListener to auto-fire onHostResume on EVERY new
     * listener, including non-allowlisted ones). The next resume/pause/
     * destroy cycle picks it up. This is consistent with how modules that
     * register late behave against the UI host anyway.
     */
    private fun dispatchLifecycleEventToAllowlisted(event: LifecycleEvent) {
        val listeners = readBgLifecycleListeners() ?: return
        for (l in listeners) {
            if (!isBgListenerAllowed(l)) continue
            try {
                when (event) {
                    LifecycleEvent.RESUME -> l.onHostResume()
                    LifecycleEvent.PAUSE -> l.onHostPause()
                    LifecycleEvent.DESTROY -> l.onHostDestroy()
                }
            } catch (t: Throwable) {
                BTLogger.error("bg lifecycle ${event.name} dispatch (${l.javaClass.name}): ${t.message}")
            }
        }
    }

    fun dispatchActivityResult(
        activity: Activity,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        runOnUiThread {
            val listeners = readBgActivityListeners() ?: return@runOnUiThread
            for (l in listeners) {
                if (!isBgListenerAllowed(l)) continue
                try {
                    l.onActivityResult(activity, requestCode, resultCode, data)
                } catch (t: Throwable) {
                    BTLogger.error("bg onActivityResult dispatch (${l.javaClass.name}): ${t.message}")
                }
            }
        }
    }

    fun dispatchNewIntent(intent: Intent) {
        runOnUiThread {
            val listeners = readBgActivityListeners() ?: return@runOnUiThread
            for (l in listeners) {
                if (!isBgListenerAllowed(l)) continue
                try {
                    l.onNewIntent(intent)
                } catch (t: Throwable) {
                    BTLogger.error("bg onNewIntent dispatch (${l.javaClass.name}): ${t.message}")
                }
            }
        }
    }

    /**
     * Called when the bg ReactContext becomes available so we can install
     * the last resumed Activity right away, covering the window where the
     * host Activity resumes before the bg host finishes initializing.
     */
    private fun replayLastResumedActivityOnUi() {
        val activity = lastResumedActivityRef.get() ?: return
        runOnUiThread {
            if (writeBgCurrentActivity(activity)) {
                BTLogger.info("replayLastResumedActivityOnUi: mCurrentActivity=${activity.javaClass.simpleName}")
            }
            // Fire RESUME for allowlisted modules whose listener was
            // registered before the bg ReactContext finished initializing.
            dispatchLifecycleEventToAllowlisted(LifecycleEvent.RESUME)
        }
    }

    /**
     * Reflect-write `mCurrentActivity` on the bg ReactContext so
     * TurboModules on that host see a non-null getCurrentActivity().
     *
     * Returns true on success.
     */
    private fun writeBgCurrentActivity(activity: Activity?): Boolean {
        val ctx = bgReactHost?.currentReactContext ?: return false
        return try {
            val field = findField(ctx.javaClass, "mCurrentActivity") ?: run {
                BTLogger.error("writeBgCurrentActivity: mCurrentActivity field not found (RN upgrade?)")
                return false
            }
            field.isAccessible = true
            field.set(ctx, if (activity != null) WeakReference(activity) else null)
            true
        } catch (t: Throwable) {
            BTLogger.error("writeBgCurrentActivity: ${t.message}")
            false
        }
    }

    /**
     * Reflect-read bg ReactContext's `mActivityEventListeners` set so we
     * can iterate it without going through ReactContext.onActivityResult
     * (which would fan out to every listener unconditionally).
     */
    @Suppress("UNCHECKED_CAST")
    private fun readBgActivityListeners(): Collection<com.facebook.react.bridge.ActivityEventListener>? {
        val ctx = bgReactHost?.currentReactContext ?: return null
        return try {
            val field = findField(ctx.javaClass, "mActivityEventListeners") ?: run {
                BTLogger.error("readBgActivityListeners: field not found (RN upgrade?)")
                return null
            }
            field.isAccessible = true
            field.get(ctx) as? Collection<com.facebook.react.bridge.ActivityEventListener>
        } catch (t: Throwable) {
            BTLogger.error("readBgActivityListeners: ${t.message}")
            null
        }
    }

    /**
     * Reflect-read bg ReactContext's `mLifecycleEventListeners` set. Same
     * rationale as readBgActivityListeners — we iterate ourselves so we
     * can apply the allowlist filter, avoiding a fan-out to every
     * LifecycleEventListener on the bg ReactContext.
     */
    @Suppress("UNCHECKED_CAST")
    private fun readBgLifecycleListeners(): Collection<com.facebook.react.bridge.LifecycleEventListener>? {
        val ctx = bgReactHost?.currentReactContext ?: return null
        return try {
            val field = findField(ctx.javaClass, "mLifecycleEventListeners") ?: run {
                BTLogger.error("readBgLifecycleListeners: field not found (RN upgrade?)")
                return null
            }
            field.isAccessible = true
            field.get(ctx) as? Collection<com.facebook.react.bridge.LifecycleEventListener>
        } catch (t: Throwable) {
            BTLogger.error("readBgLifecycleListeners: ${t.message}")
            null
        }
    }

    /**
     * Walks the class hierarchy to find a declared field by name.
     * `mCurrentActivity` and `mActivityEventListeners` live on
     * `ReactContext`, but bg runs `BridgelessReactContext` which subclasses
     * `ReactApplicationContext` which subclasses `ReactContext`, so we
     * can't use getDeclaredField directly on the runtime class.
     */
    private fun findField(cls: Class<*>, name: String): java.lang.reflect.Field? {
        var c: Class<*>? = cls
        while (c != null) {
            try { return c.getDeclaredField(name) } catch (_: NoSuchFieldException) {}
            c = c.superclass
        }
        return null
    }

    private fun isBgListenerAllowed(listener: Any): Boolean {
        val fqcn = listener.javaClass.name
        val list = bgActivityBridgeListenerClassAllowlist
        for (prefix in list) {
            if (fqcn.startsWith(prefix)) return true
        }
        return false
    }

    private fun runOnUiThread(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            Handler(Looper.getMainLooper()).post(block)
        }
    }

    // ── Lifecycle ───────────────────────────────────────────────────────────

    val isBackgroundStarted: Boolean get() = isStarted

    fun destroy() {
        nativeDestroy()
        bgRuntimePtr = 0
        mainRuntimePtr = 0
        mainReactContext = null
        bgReactHost?.destroy("BackgroundThreadManager destroyed", null)
        bgReactHost = null
        isStarted = false
        lastResumedActivityRef = WeakReference(null)
    }
}
