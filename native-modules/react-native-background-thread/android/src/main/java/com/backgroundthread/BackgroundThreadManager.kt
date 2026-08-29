package com.backgroundthread

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.ReactPackage
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.ReactInstanceEventListener
import com.facebook.react.bridge.JSBundleLoader
import com.facebook.react.bridge.JavaScriptModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.common.annotations.UnstableReactNativeAPI
import com.facebook.react.defaults.DefaultComponentsRegistry
import com.facebook.react.defaults.DefaultReactHostDelegate
import com.facebook.react.defaults.DefaultTurboModuleManagerDelegate
import com.facebook.react.fabric.ComponentFactory
import com.facebook.react.interfaces.TaskInterface
import com.facebook.react.runtime.ReactHostImpl
import com.facebook.react.runtime.hermes.HermesInstance
import com.facebook.react.shell.MainReactPackage
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

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
    // OneKey patch: the prebuilt RN AAR exposes the legacy six-argument HMRClient
    // interface, while the patched JS client accepts an exact bundle URL as argument seven.
    private interface HMRClient : JavaScriptModule {
        fun setup(
            platform: String?,
            bundleEntry: String?,
            host: String?,
            port: Int,
            isEnabled: Boolean,
            scheme: String?,
            fullBundleUrlOverride: String?,
        )
    }

    // OneKey patch: background uses the same local dev-vendor HBC as main,
    // then evaluates its own modulesOnly Metro delta in the isolated runtime.
    private data class DevVendorConfig(
        val commonAssetName: String,
        val fingerprint: String,
        val backgroundHMREnabled: Boolean,
        val runtimeGeneration: Long = 0,
    )
    private var bgReactHost: ReactHostImpl? = null
    private var reactPackages: List<ReactPackage> = emptyList()
    private val backgroundRuntimeGeneration = AtomicLong(0)
    private val backgroundHMRRestartInFlight = AtomicBoolean(false)

    @Volatile
    private var lastBackgroundEntryURL: String? = null

    @Volatile
    private var lastDevVendorConfig: DevVendorConfig? = null

    // Tracks the last resumed Activity so we can replay it onto the bg
    // ReactContext as soon as the bg host finishes initializing (covers the
    // cold-start race where Activity resumes before bg host is ready).
    private var lastResumedActivityRef: WeakReference<Activity> = WeakReference(null)

    @Volatile
    private var bgRuntimePtr: Long = 0

    @Volatile
    private var mainRuntimePtr: Long = 0
    private var mainReactContext: ReactApplicationContext? = null

    // Captured lazily on the first installSharedBridgeInMainRuntime() call.
    // Used by restart(mode='ui') to soft-reload the main runtime via
    // ReactHost.reload(reason) so the bg runtime stays hot. Null when the
    // host app is not on bridgeless / NewArch — restart() falls back to a
    // full process restart in that case.
    @Volatile
    private var mainReactHost: ReactHost? = null
    private val runnerState = AtomicReference(BackgroundRunnerState.IDLE)

    @Volatile
    private var runnerStartFailureMessage: String? = null

    private enum class BackgroundRunnerState {
        IDLE,
        STARTING,
        RUNNING,
        FAILED,
        DESTROYING,
    }

    companion object {
        private const val MODULE_NAME = "background"

        // Bounded watchdog: if the bg JS thread never drains to our scheduled
        // eval (e.g. the bg entry bundle never finished evaluating), reject as a
        // RETRYABLE timeout rather than leaving the JS promise pending forever.
        // Matches the main-runtime SplitBundleLoader watchdog.
        private const val BG_SEGMENT_EVAL_TIMEOUT_MS = 30_000L

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

    /**
     * Completion contract invoked by the native (JNI) side AFTER the bg segment
     * has been evaluated into the BACKGROUND runtime. Called from the bg JS
     * thread (or synchronously on the caller thread for fail-fast paths).
     *
     * @param error null on success; a non-empty message on failure. A message
     *   prefixed with "NO_RUNTIME:" means the bg runtime is not ready yet
     *   (retryable); "IO_ERROR:" means the segment file read failed (fatal);
     *   any other message is a segment JS/Hermes eval throw (fatal).
     */
    fun interface SegmentEvalCallback {
        fun onComplete(error: String?)
    }

    // ── JNI declarations ────────────────────────────────────────────────────

    private external fun nativeInstallSharedBridge(
        runtimePtr: Long,
        isMain: Boolean,
        runtimeGeneration: Long,
    )
    private external fun nativeSetupErrorHandler(runtimePtr: Long)
    private external fun nativeDestroy()
    private external fun nativeExecuteWork(
        runtimePtr: Long,
        workId: Long,
        isMain: Boolean,
        runtimeGeneration: Long,
    )

    /**
     * Evaluate the segment at [segmentPath] into the BACKGROUND runtime on its
     * JS thread and invoke [callback] from inside that same JS-thread block,
     * strictly AFTER the segment's `__d(...)` module definitions have run. This
     * is the ordering guarantee that fixes the bg "Requiring unknown module"
     * race (the bg analogue of the main-runtime SplitBundleLoaderJSI fix).
     *
     * Returns immediately; [callback] fires later on the bg JS thread (or
     * synchronously on the calling thread for fail-fast paths such as the bg
     * runtime not being ready or the segment file failing to read).
     */
    private external fun nativeEvaluateSegmentInBackground(
        segmentPath: String,
        sourceURL: String,
        callback: SegmentEvalCallback
    )

    /**
     * Clean up after a SUCCESSFUL scheduleOnJSThread post that nonetheless can
     * never run: called from [scheduleOnJSThread]'s ptr == 0 branch (we are
     * already on the stale/torn-down JS thread, so C++ saw scheduled == true and
     * will not clean up on its own). For the given runtime ([isMain]) the native
     * side erases gPendingWork[workId] (frees the captured segment source
     * buffer) and resets drainScheduled so a recovered runtime re-arms a fresh
     * drain on the next enqueue. This is a TRANSIENT reload condition, so the
     * native side deliberately leaves the coalesced RuntimeWorkQueue's items
     * INTACT — the same runtime recovers and the main runtime has no JS-side
     * retry net, so abandoning its queued SharedRPC deliveries would lose them.
     * For the bg runtime only, it also settles every in-flight bg
     * segment eval as a retryable NO_RUNTIME failure so each JNI global ref is
     * released and the JS promise resolves immediately instead of leaking until
     * teardown or the bg watchdog. Exactly-once on the native side.
     */
    private external fun nativeDropScheduledWork(
        isMain: Boolean,
        workId: Long,
        runtimeGeneration: Long,
    )

    /**
     * Synchronously mark the SharedRPC listener for `runtimeId` as dead
     * before the underlying JS runtime is torn down. See
     * SharedRPC::invalidate (cpp/SharedRPC.cpp) for the cross-runtime
     * correctness rationale.
     */
    private external fun nativeInvalidateSharedRpc(
        runtimeId: String,
        runtimeGeneration: Long,
    ): Boolean

    /**
     * Quiesce background-native work and release JSI callbacks while this call
     * still runs on the outgoing background runtime's JS thread. Destroying
     * those callbacks from the UI or teardown worker thread is unsafe.
     */
    private external fun nativeInvalidateBackgroundRuntimeOnJSThread(
        runtimeGeneration: Long,
    ): Boolean

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
        // Capture the host the first time we see it; reload() needs it. We
        // resolve via ReactApplication.reactHost — non-null only on bridgeless
        // / NewArch builds. Bound here (not in restart()) because installShared
        // Bridge is invoked from JS bootstrap, which is also when ReactContext
        // is guaranteed live. Restart('ui') falls back to process restart if
        // this stays null.
        if (mainReactHost == null) {
            try {
                val app = context.applicationContext as? ReactApplication
                mainReactHost = app?.reactHost
                if (mainReactHost == null) {
                    BTLogger.warn("ReactHost unavailable (non-bridgeless host?); restart('ui') will fall back to process restart")
                }
            } catch (t: Throwable) {
                BTLogger.error("Failed to resolve ReactHost: ${t.message}")
            }
        }
        context.runOnJSQueueThread {
            try {
                val ptr = context.javaScriptContextHolder?.get() ?: 0L
                if (ptr != 0L) {
                    mainRuntimePtr = ptr
                    nativeInstallSharedBridge(ptr, true, 0)
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
    private fun createDevVendorBundleLoader(
        appContext: android.content.Context,
        entryURL: String,
        config: DevVendorConfig,
    ): JSBundleLoader {
        return object : JSBundleLoader() {
            override fun loadScript(delegate: com.facebook.react.bridge.JSBundleLoaderDelegate): String {
                val totalStart = System.nanoTime()
                delegate.loadScriptFromAssets(
                    appContext.assets,
                    "assets://${config.commonAssetName}",
                    false,
                )
                val assertionFile = File(appContext.cacheDir, "onekey-dev-vendor-assert-background.js")
                val quotedEntryURL = org.json.JSONObject.quote(entryURL)
                assertionFile.writeText(
                    "if(globalThis.__ONEKEY_DEV_VENDOR_FINGERPRINT__!==\"${config.fingerprint}\")" +
                        "{throw new Error(\"Dev-vendor common fingerprint mismatch\");}" +
                        "globalThis.__ONEKEY_DEV_VENDOR_FULL_BUNDLE_URL__=$quotedEntryURL;" +
                        "globalThis.__ONEKEY_RUNTIME_TARGET__=\"background\";" +
                        "globalThis.__ONEKEY_BACKGROUND_RUNTIME_GENERATION__=${config.runtimeGeneration};",
                )
                delegate.loadScriptFromFile(
                    assertionFile.absolutePath,
                    entryURL,
                    false,
                )
                val deltaFile = File(
                    appContext.cacheDir,
                    "onekey-dev-vendor-background-${config.fingerprint.take(12)}.bundle",
                )
                val temporaryFile = File.createTempFile(
                    ".${deltaFile.name}-",
                    ".tmp",
                    deltaFile.parentFile,
                )
                try {
                    val connection = java.net.URL(entryURL).openConnection() as java.net.HttpURLConnection
                    try {
                        connection.connectTimeout = 15_000
                        connection.readTimeout = 60_000
                        val statusCode = connection.responseCode
                        if (statusCode !in 200..299) {
                            throw IllegalStateException(
                                "Dev-vendor background delta returned HTTP $statusCode",
                            )
                        }
                        connection.inputStream.use { input ->
                            temporaryFile.outputStream().use { output -> input.copyTo(output) }
                        }
                    } finally {
                        connection.disconnect()
                    }
                    android.system.Os.rename(
                        temporaryFile.absolutePath,
                        deltaFile.absolutePath,
                    )
                } catch (error: Exception) {
                    temporaryFile.delete()
                    throw RuntimeException("Failed to download dev-vendor background delta", error)
                }
                delegate.loadScriptFromFile(deltaFile.absolutePath, entryURL, false)
                val totalMs = (System.nanoTime() - totalStart) / 1_000_000.0
                BTLogger.info(
                    "[DevVendor] background common.hbc + Metro delta loaded in " +
                        "${String.format("%.1f", totalMs)}ms",
                )
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

    fun startBackgroundRunnerWithEntryURL(context: ReactApplicationContext, entryURL: String) {
        ensureBackgroundRunnerWithEntryURL(context.applicationContext, entryURL)
    }

    @OptIn(UnstableReactNativeAPI::class)
    fun ensureBackgroundRunnerWithEntryURL(context: Context, entryURL: String): Boolean {
        return ensureBackgroundRunner(context, entryURL, null)
    }

    @OptIn(UnstableReactNativeAPI::class)
    fun ensureBackgroundRunnerWithDevVendor(
        context: Context,
        entryURL: String,
        commonAssetName: String,
        fingerprint: String,
        backgroundHMREnabled: Boolean,
    ): Boolean {
        return ensureBackgroundRunner(
            context,
            entryURL,
            DevVendorConfig(commonAssetName, fingerprint, backgroundHMREnabled),
        )
    }

    @OptIn(UnstableReactNativeAPI::class)
    private fun ensureBackgroundRunner(
        context: Context,
        entryURL: String,
        devVendorConfig: DevVendorConfig?,
    ): Boolean {
        if (!runnerState.compareAndSet(BackgroundRunnerState.IDLE, BackgroundRunnerState.STARTING)) {
            BTLogger.info("Background runner start coalesced: state=${getBackgroundRunnerState()}")
            return false
        }

        runnerStartFailureMessage = null
        val runtimeGeneration = backgroundRuntimeGeneration.incrementAndGet()
        val activeDevVendorConfig =
            devVendorConfig?.copy(runtimeGeneration = runtimeGeneration)
        lastBackgroundEntryURL = entryURL
        lastDevVendorConfig = devVendorConfig
        val bgStartTime = System.nanoTime()
        BTLogger.info("[SplitBundle] background runner starting with entryURL: $entryURL")

        val appContext = context.applicationContext
        try {
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
                    activeDevVendorConfig != null && isRemoteBundleUrl(entryURL) ->
                        createDevVendorBundleLoader(appContext, entryURL, activeDevVendorConfig)

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
                    if (backgroundRuntimeGeneration.get() != runtimeGeneration) {
                        BTLogger.info(
                            "[BackgroundHMR] ignored stale ReactContext generation=$runtimeGeneration",
                        )
                        return
                    }
                    val initMs = (System.nanoTime() - bgStartTime) / 1_000_000.0
                    BTLogger.info("[SplitBundle] background ReactContext initialized in ${String.format("%.1f", initMs)}ms")
                    // Replay the most recent Activity resume so TurboModules on the
                    // bg host can see getCurrentActivity()/ActivityEventListeners
                    // from the very first call, even when the bg host finishes
                    // initializing after the Activity is already resumed.
                    replayLastResumedActivityOnUi()
                    context.runOnJSQueueThread {
                        try {
                            if (
                                backgroundRuntimeGeneration.get() != runtimeGeneration ||
                                bgReactHost?.currentReactContext !== context
                            ) {
                                BTLogger.info(
                                    "[BackgroundHMR] skipped stale binding install generation=$runtimeGeneration",
                                )
                                return@runOnJSQueueThread
                            }
                            val ptr = context.javaScriptContextHolder?.get() ?: 0L
                            if (ptr != 0L) {
                                bgRuntimePtr = ptr
                                nativeInstallSharedBridge(ptr, false, runtimeGeneration)
                                nativeSetupErrorHandler(ptr)
                                BTLogger.info("SharedBridge and error handler installed in background runtime")
                            } else {
                                BTLogger.error("Background runtime pointer is 0")
                            }
                        } catch (e: Exception) {
                            BTLogger.error("Error installing bindings in background runtime: ${e.message}")
                        }
                    }
                    if (activeDevVendorConfig?.backgroundHMREnabled == true) {
                        setupBackgroundHMRClient(context, entryURL, runtimeGeneration)
                    }
                }
            })

            watchBackgroundRunnerStart(host.start(), bgStartTime, runtimeGeneration)
            return true
        } catch (t: Throwable) {
            markBackgroundRunnerFailed(t, bgStartTime, runtimeGeneration)
            return false
        }
    }

    private fun setupBackgroundHMRClient(
        context: ReactContext,
        entryURL: String,
        runtimeGeneration: Long,
    ) {
        try {
            val uri = Uri.parse(entryURL)
            val hmrRegistrationUri = uri.buildUpon()
                .path("/apps/mobile/background.bundle")
                .build()
            val scheme = hmrRegistrationUri.scheme ?: "http"
            val port = if (hmrRegistrationUri.port != -1) {
                hmrRegistrationUri.port
            } else if (scheme == "https") {
                443
            } else {
                80
            }
            val path = hmrRegistrationUri.path?.removePrefix("/")
                ?: "apps/mobile/background.bundle"
            context.getJSModule(HMRClient::class.java).setup(
                "android",
                path,
                hmrRegistrationUri.host,
                port,
                true,
                scheme,
                hmrRegistrationUri.toString(),
            )
            BTLogger.info(
                "[BackgroundHMR] client setup queued generation=$runtimeGeneration " +
                    "downloadURL=$entryURL registrationURL=$hmrRegistrationUri",
            )
        } catch (t: Throwable) {
            BTLogger.error("[BackgroundHMR] client setup failed: ${t.message}")
        }
    }

    private fun watchBackgroundRunnerStart(
        task: TaskInterface<Void>,
        bgStartTime: Long,
        runtimeGeneration: Long,
    ) {
        Thread {
            try {
                task.waitForCompletion()
                if (
                    runnerState.get() != BackgroundRunnerState.STARTING ||
                    backgroundRuntimeGeneration.get() != runtimeGeneration
                ) {
                    return@Thread
                }
                when {
                    task.isFaulted() -> markBackgroundRunnerFailed(
                        task.getError() ?: RuntimeException("Background ReactHost start faulted"),
                        bgStartTime,
                        runtimeGeneration,
                    )
                    task.isCancelled() -> markBackgroundRunnerFailed(
                        RuntimeException("Background ReactHost start cancelled"),
                        bgStartTime,
                        runtimeGeneration,
                    )
                    runnerState.compareAndSet(
                        BackgroundRunnerState.STARTING,
                        BackgroundRunnerState.RUNNING,
                    ) -> {
                        val startMs = (System.nanoTime() - bgStartTime) / 1_000_000.0
                        BTLogger.info("Background runner start task completed in ${String.format("%.1f", startMs)}ms")
                    }
                }
            } catch (t: Throwable) {
                markBackgroundRunnerFailed(t, bgStartTime, runtimeGeneration)
            }
        }.apply {
            isDaemon = true
            name = "OneKey-BgThread-StartWatch"
            start()
        }
    }

    private fun markBackgroundRunnerFailed(
        t: Throwable,
        bgStartTime: Long,
        runtimeGeneration: Long,
    ) {
        if (
            backgroundRuntimeGeneration.get() != runtimeGeneration ||
            !runnerState.compareAndSet(BackgroundRunnerState.STARTING, BackgroundRunnerState.FAILED)
        ) {
            return
        }
        val startMs = (System.nanoTime() - bgStartTime) / 1_000_000.0
        runnerStartFailureMessage = t.message ?: t.javaClass.simpleName
        bgRuntimePtr = 0
        BTLogger.error(
            "Background runner start failed after ${String.format("%.1f", startMs)}ms: " +
                runnerStartFailureMessage
        )
        val failedHost = bgReactHost
        val destroyTask = try {
            failedHost?.destroy("Background runner start failed", t as? Exception)
        } catch (destroyError: Throwable) {
            BTLogger.error("Failed to destroy faulted background runner: ${destroyError.message}")
            null
        }
        if (destroyTask == null) {
            if (failedHost == null) {
                runnerState.compareAndSet(
                    BackgroundRunnerState.FAILED,
                    BackgroundRunnerState.IDLE,
                )
            }
            return
        }
        Thread {
            try {
                destroyTask.waitForCompletion()
                if (destroyTask.isFaulted() || destroyTask.isCancelled()) {
                    BTLogger.error(
                        "Faulted background runner teardown did not complete cleanly " +
                            "(faulted=${destroyTask.isFaulted()}, " +
                            "cancelled=${destroyTask.isCancelled()}): " +
                            destroyTask.getError()?.message,
                    )
                    return@Thread
                }
                if (
                    runnerState.compareAndSet(
                        BackgroundRunnerState.FAILED,
                        BackgroundRunnerState.DESTROYING,
                    )
                ) {
                    Handler(Looper.getMainLooper()).post {
                        if (bgReactHost === failedHost) {
                            bgReactHost = null
                        }
                        runnerState.set(BackgroundRunnerState.IDLE)
                        BTLogger.info(
                            "Faulted background runner teardown completed; retry is available",
                        )
                    }
                }
            } catch (destroyError: Throwable) {
                BTLogger.error(
                    "Failed to await faulted background runner teardown: " +
                        destroyError.message,
                )
            }
        }.apply {
            isDaemon = true
            name = "OneKey-BgThread-FailedHostDestroy"
            start()
        }
    }

    /**
     * Called from C++ RuntimeExecutor to schedule work on the correct JS thread.
     * Routes to main or background runtime's JS queue thread, then calls nativeExecuteWork.
     */
    @DoNotStrip
    fun scheduleOnJSThread(
        isMain: Boolean,
        workId: Long,
        runtimeGeneration: Long,
    ): Boolean {
        val backgroundState = runnerState.get()
        val context = if (isMain) {
            mainReactContext
        } else if (
            backgroundState == BackgroundRunnerState.STARTING ||
            backgroundState == BackgroundRunnerState.RUNNING
        ) {
            bgReactHost?.currentReactContext
        } else {
            null
        }
        BTLogger.info(
            "scheduleOnJSThread: isMain=$isMain, workId=$workId, " +
                "generation=$runtimeGeneration, context=${context != null}",
        )
        if (context == null) {
            BTLogger.error("scheduleOnJSThread: context is null! isMain=$isMain, mainCtx=${mainReactContext != null}, bgHost=${bgReactHost != null}, bgCtx=${bgReactHost?.currentReactContext != null}")
            // The just-enqueued native work will never reach the JS thread.
            // Return false so the C++ coalesced scheduler
            // (callScheduleOnJSThread → scheduleRuntimeDrain's !scheduled branch)
            // does the FULL cleanup itself: erase gPendingWork[workId], leak+clear
            // the coalesced RuntimeWorkQueue, reset drainScheduled, and (bg only)
            // settle any in-flight bg eval as retryable NO_RUNTIME. No
            // nativeDropScheduledWork call is needed here.
            return false
        }
        return try {
            val posted = context.runOnJSQueueThread {
                if (
                    !isMain &&
                    (
                        backgroundRuntimeGeneration.get() != runtimeGeneration ||
                            bgReactHost?.currentReactContext !== context
                    )
                ) {
                    BTLogger.info(
                        "scheduleOnJSThread: dropped stale background workId=$workId " +
                            "generation=$runtimeGeneration active=${backgroundRuntimeGeneration.get()}",
                    )
                    nativeDropScheduledWork(isMain, workId, runtimeGeneration)
                    return@runOnJSQueueThread
                }
                // Re-read ptr only after proving this Runnable still belongs to
                // the active context and generation.
                val ptr = if (isMain) mainRuntimePtr else bgRuntimePtr
                BTLogger.info("scheduleOnJSThread runOnJSQueueThread: isMain=$isMain, workId=$workId, ptr=$ptr")
                if (ptr != 0L) {
                    try {
                        nativeExecuteWork(ptr, workId, isMain, runtimeGeneration)
                    } catch (e: Exception) {
                        BTLogger.error("Error executing work on JS thread: ${e.message}")
                    }
                } else {
                    BTLogger.error("scheduleOnJSThread: ptr is 0! isMain=$isMain")
                    // We are already on the (stale/torn-down) JS thread after a
                    // SUCCESSFUL post (C++ saw scheduled==true), so the work won't
                    // run and C++ won't clean up on its own. Drop it for THIS
                    // runtime (main or bg): erase gPendingWork[workId] (frees the
                    // source buffer) and reset drainScheduled so a recovered
                    // runtime re-arms a fresh drain. The coalesced RuntimeWork
                    // Queue items are left intact (transient reload; the same
                    // runtime recovers and main has no JS retry net).
                    // drainPendingBgEvals inside the native fn is
                    // gated to !isMain, so settling bg evals only happens for bg.
                    nativeDropScheduledWork(isMain, workId, runtimeGeneration)
                }
            }
            if (!posted) {
                BTLogger.error("scheduleOnJSThread: runOnJSQueueThread rejected workId=$workId isMain=$isMain")
            }
            posted
        } catch (e: Exception) {
            BTLogger.error("scheduleOnJSThread: failed to post workId=$workId isMain=$isMain error=${e.message}")
            false
        }
    }

    // ── Segment Registration (Phase 2.5 spike) ─────────────────────────────

    /**
     * Evaluate a HBC segment into the background runtime with completion
     * callback (fix A: eval-then-resolve).
     *
     * Previously this called `ReactContext.registerSegment(...)`, whose
     * completion fires BEFORE the segment bytecode is evaluated into the runtime
     * (registerSegment only ENQUEUES the eval). That races Metro's
     * `import().then(() => __r(moduleId))` and produces a fatal, uncatchable
     * "Requiring unknown module" — locale segments load through this bg path, so
     * a language switch could still crash. We now evaluate the segment OURSELVES
     * on the bg JS thread via [nativeEvaluateSegmentInBackground] and resolve
     * ONLY after eval completes, so eval + resolve are one atomic JS-thread turn.
     *
     * @param segmentId The segment ID (used only for the synthetic source URL)
     * @param path Absolute file path to the .seg.hbc file
     * @param onComplete Called with (code=null) on success, or
     *   (code=<contract reject code>, message) on failure. The code is one of
     *   the SHARED split-bundle contract codes so the JS loader's retryable set
     *   { SPLIT_BUNDLE_NO_RUNTIME, SPLIT_BUNDLE_TIMEOUT } classifies correctly.
     */
    fun registerSegmentInBackground(
        segmentId: Int,
        path: String,
        onComplete: (code: String?, message: String?) -> Unit
    ) {
        if (!isBackgroundStarted) {
            // Bg runtime not started yet → retryable (the loader will re-attempt
            // once the bg host is up).
            onComplete("SPLIT_BUNDLE_NO_RUNTIME", "Background runtime not started")
            return
        }

        val file = File(path)
        if (!file.exists()) {
            onComplete("SPLIT_BUNDLE_NOT_FOUND", "Segment file not found: $path")
            return
        }

        val context = bgReactHost?.currentReactContext
        if (context == null) {
            onComplete("SPLIT_BUNDLE_NO_RUNTIME", "Background ReactContext not available")
            return
        }

        // One-shot guard: native success/error AND the watchdog can each try to
        // settle; only the first wins.
        val settled = AtomicBoolean(false)
        val sourceURL = "seg-$segmentId.js"
        val segStart = System.nanoTime()

        // Bounded watchdog: if the bg JS thread never drains to our eval, reject
        // with the RETRYABLE SPLIT_BUNDLE_TIMEOUT instead of hanging forever.
        val watchdog = Handler(Looper.getMainLooper())
        val timeoutRunnable = Runnable {
            if (settled.compareAndSet(false, true)) {
                BTLogger.error("[SplitBundle] bg segment id=$segmentId eval timed out after ${BG_SEGMENT_EVAL_TIMEOUT_MS}ms (bg entry bundle likely never finished evaluating); rejecting as retryable timeout")
                onComplete("SPLIT_BUNDLE_TIMEOUT", "Bg segment eval timed out: id=$segmentId")
            }
        }
        watchdog.postDelayed(timeoutRunnable, BG_SEGMENT_EVAL_TIMEOUT_MS)

        try {
            nativeEvaluateSegmentInBackground(path, sourceURL) { error ->
                if (settled.compareAndSet(false, true)) {
                    watchdog.removeCallbacks(timeoutRunnable)
                    if (error == null) {
                        val segMs = (System.nanoTime() - segStart) / 1_000_000.0
                        BTLogger.info("[SplitBundle] bg segment id=$segmentId evaluated in ${String.format("%.1f", segMs)}ms (eval-complete)")
                        onComplete(null, null)
                    } else {
                        // Native prefixes its failures so we can map to the
                        // shared contract codes: NO_RUNTIME (retryable),
                        // IO_ERROR (fatal), else eval throw (fatal).
                        when {
                            error.startsWith("NO_RUNTIME:") ->
                                onComplete("SPLIT_BUNDLE_NO_RUNTIME", error.removePrefix("NO_RUNTIME:"))
                            error.startsWith("IO_ERROR:") ->
                                onComplete("SPLIT_BUNDLE_IO_ERROR", error.removePrefix("IO_ERROR:"))
                            else ->
                                onComplete("SPLIT_BUNDLE_EVAL_ERROR", error)
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            // nativeEvaluateSegmentInBackground itself failed to dispatch (e.g.
            // UnsatisfiedLinkError). Fail closed; do NOT fall back to the
            // race-prone registerSegment path.
            if (settled.compareAndSet(false, true)) {
                watchdog.removeCallbacks(timeoutRunnable)
                BTLogger.error("[SplitBundle] FATAL: nativeEvaluateSegmentInBackground threw for id=$segmentId: ${e.message}")
                onComplete("SPLIT_BUNDLE_NATIVE_UNAVAILABLE", "Bg native segment eval unavailable: ${e.message}")
            }
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

    // ── Restart ─────────────────────────────────────────────────────────────
    //
    // Replaces direct use of `react-native-restart` from JS. The native module
    // is the only piece that has visibility into BOTH the main and background
    // ReactHost lifecycles, so it can sequence:
    //   1. SharedRPC quiesce (mark listener alive=false, leak callback, drop
    //      executor) — synchronous, holds SharedRPC mutex internally
    //   2. JS runtime teardown — see per-mode behaviour below
    //
    // mode='ui':
    //   Soft-reload the main runtime via ReactHost.reload(reason). bg runtime
    //   keeps running. After reload, JS bootstrap re-invokes installShared
    //   Bridge() (the TurboModule entry point), which re-installs the "main"
    //   SharedRPC listener and refreshes mainRuntimePtr. Requires bridgeless /
    //   NewArch — if ReactHost was never captured (old arch host), falls back
    //   to a process restart so callers get consistent reload semantics.
    //
    // mode='all':
    //   Process-level restart (Runtime.exit + makeRestartActivityTask). OTA
    //   install/switch and resetData want a clean slate on disk; a process
    //   relaunch trivially guarantees both runtimes bootstrap from fresh
    //   bundle content without us having to sequence "tear down bg → reload
    //   main → wait for new main → restart bg". iOS goes through soft reload
    //   for mode='all' because abrupt termination is App Store-unfriendly and
    //   iOS has no clean self-relaunch; Android does, so we keep using it.

    fun restart(context: ReactApplicationContext, mode: String, reason: String, promise: com.facebook.react.bridge.Promise) {
        val isUi = mode == "ui"
        val isAll = mode == "all"
        val isBackgroundHMR = mode == "background"
        if (isBackgroundHMR) {
            restartBackgroundForHMR(context.applicationContext, reason, promise)
            return
        }
        if (!isUi && !isAll) {
            promise.reject(
                "BG_RESTART_ERROR",
                "BackgroundThread.restart: unsupported mode '$mode', expected 'ui' or 'all'"
            )
            return
        }

        BTLogger.info("restart: mode=$mode reason=$reason")

        try {
            nativeInvalidateSharedRpc("main", 0)
            if (isAll) {
                nativeInvalidateSharedRpc(
                    "background",
                    backgroundRuntimeGeneration.incrementAndGet(),
                )
            }
        } catch (t: Throwable) {
            // Invalidate is best-effort; not fatal if the JNI call somehow
            // throws (e.g. so library not loaded yet). Continue to restart.
            BTLogger.error("restart: SharedRPC invalidate failed: ${t.message}")
        }

        val host = mainReactHost
        if (isUi && host != null) {
            BTLogger.info("restart(ui): soft reload via ReactHost.reload")
            // Post to the UI thread to keep the call off the JS thread that
            // is about to be torn down; ReactHost.reload is itself thread-
            // safe but the work it kicks off (JNI teardown of the old
            // ReactInstance) is cleaner when not initiated from inside the
            // outgoing runtime's callback frame.
            //
            // Promise resolution is sequenced after reload() returns. The
            // actual *delivery* of that resolve to JS is best-effort: the
            // CallInvoker that would route it back is tied to the outgoing
            // ReactInstance which reload() is about to invalidate, and the
            // typical caller is `await restart(...)` whose continuation
            // never runs because the reload supersedes it. The position
            // matters only for the synchronous-throw case — if reload()
            // throws inline, the catch block reaches the fallback / reject
            // path instead of misreporting success.
            //
            // ReactHost.reload(reason) returns a TaskInterface<Void> that
            // completes when the new ReactInstance is up; an async fault
            // means reload failed AFTER we already invalidated the
            // SharedRPC main listener — main runtime is torn down without
            // rebuild, SharedRPC is permanently dead for "main". Watch the
            // task on a daemon thread and fall back to a process restart
            // on fault / cancellation / timeout so we converge on a known
            // good state instead of leaving the app wedged. TaskInterface
            // only exposes waitForCompletion + isFaulted/isCancelled/
            // getError (no continueWith/onError callback in RN 0.83's
            // public surface), so polling on a worker thread is what we
            // have. Timeout is generous (15s) — observed worst case
            // reload on a low-end Android is ~3s.
            Handler(Looper.getMainLooper()).post {
                val task = try {
                    host.reload("BackgroundThread.restart(ui): $reason").also {
                        promise.resolve(null)
                    }
                } catch (t: Throwable) {
                    BTLogger.error("restart(ui): ReactHost.reload threw synchronously: ${t.message}")
                    if (!triggerProcessRestart(context)) {
                        promise.reject(
                            "BG_RESTART_ERROR",
                            "restart(ui): reload failed and process restart fallback also failed: ${t.message}",
                            t,
                        )
                    }
                    // if triggerProcessRestart returned true, Runtime.exit(0)
                    // ran and this reject is unreachable
                    null
                }
                if (task != null) {
                    Thread {
                        try {
                            val completed = task.waitForCompletion(15, TimeUnit.SECONDS)
                            if (!completed || task.isFaulted() || task.isCancelled()) {
                                val err = task.getError()
                                BTLogger.error(
                                    "restart(ui): reload task did not complete cleanly " +
                                            "(completed=$completed, faulted=${task.isFaulted()}, " +
                                            "cancelled=${task.isCancelled()}): ${err?.message} — " +
                                            "falling back to process restart"
                                )
                                Handler(Looper.getMainLooper()).post {
                                    triggerProcessRestart(context)
                                }
                            } else {
                                BTLogger.info("restart(ui): reload task completed successfully")
                            }
                        } catch (t: Throwable) {
                            BTLogger.error(
                                "restart(ui): reload-task watch thread errored: ${t.message} — " +
                                        "falling back to process restart"
                            )
                            Handler(Looper.getMainLooper()).post {
                                triggerProcessRestart(context)
                            }
                        }
                    }.apply {
                        isDaemon = true
                        name = "OneKey-BgThread-ReloadWatch"
                        start()
                    }
                }
            }
            return
        }

        if (isUi) {
            BTLogger.warn("restart(ui): ReactHost unavailable, falling back to process restart")
        }
        if (!triggerProcessRestart(context)) {
            promise.reject(
                "BG_RESTART_ERROR",
                "Failed to trigger process restart (intent resolution or startActivity failed)"
            )
        }
        // if triggerProcessRestart returned true, Runtime.exit(0) ran and
        // any further code here is unreachable
    }

    private fun restartBackgroundForHMR(
        context: Context,
        reason: String,
        promise: com.facebook.react.bridge.Promise,
    ) {
        if (!BuildConfig.DEBUG) {
            promise.reject(
                "BG_RESTART_ERROR",
                "Background HMR restart is unavailable in release builds",
            )
            return
        }
        val generation = reason
            .removePrefix("onekey-bg-hmr:")
            .substringBefore(':')
            .toLongOrNull()
        if (!reason.startsWith("onekey-bg-hmr:") || generation == null) {
            promise.reject(
                "BG_RESTART_ERROR",
                "Background HMR restart is missing its runtime generation",
            )
            return
        }

        Handler(Looper.getMainLooper()).post {
            val activeGeneration = backgroundRuntimeGeneration.get()
            if (generation != activeGeneration) {
                BTLogger.info(
                    "[BackgroundHMR] ignored stale restart requested=$generation active=$activeGeneration",
                )
                promise.resolve(null)
                return@post
            }
            val entryURL = lastBackgroundEntryURL
            val config = lastDevVendorConfig
            if (
                entryURL == null ||
                config?.backgroundHMREnabled != true ||
                !backgroundHMRRestartInFlight.compareAndSet(false, true)
            ) {
                BTLogger.info("[BackgroundHMR] restart coalesced or disabled")
                promise.resolve(null)
                return@post
            }

            val outgoingHost = bgReactHost
            val outgoingContext = outgoingHost?.currentReactContext
            val invalidatedGeneration = backgroundRuntimeGeneration.incrementAndGet()
            runnerState.set(BackgroundRunnerState.DESTROYING)
            bgRuntimePtr = 0
            val runtimeInvalidated = CountDownLatch(1)
            val runtimeInvalidationSucceeded = AtomicBoolean(false)
            val invalidationScheduled = try {
                outgoingContext?.runOnJSQueueThread {
                    try {
                        nativeInvalidateBackgroundRuntimeOnJSThread(
                            invalidatedGeneration,
                        )
                        runtimeInvalidationSucceeded.set(true)
                    } catch (t: Throwable) {
                        BTLogger.error(
                            "[BackgroundHMR] JS-thread invalidation failed: ${t.message}",
                        )
                        try {
                            nativeInvalidateSharedRpc("background", invalidatedGeneration)
                            runtimeInvalidationSucceeded.set(true)
                        } catch (fallbackError: Throwable) {
                            BTLogger.error(
                                "[BackgroundHMR] fallback invalidation failed: " +
                                    fallbackError.message,
                            )
                        }
                    } finally {
                        runtimeInvalidated.countDown()
                    }
                } == true
            } catch (t: Throwable) {
                BTLogger.error(
                    "[BackgroundHMR] failed to schedule JS-thread invalidation: ${t.message}",
                )
                false
            }
            if (!invalidationScheduled) {
                try {
                    // Safe fallback for a context whose JS queue is already
                    // unavailable. This retains the old off-thread callback
                    // disposal safeguards, but still quiesces SharedRPC.
                    nativeInvalidateSharedRpc("background", invalidatedGeneration)
                    runtimeInvalidationSucceeded.set(true)
                } catch (t: Throwable) {
                    BTLogger.error(
                        "[BackgroundHMR] SharedRPC invalidate failed: ${t.message}",
                    )
                }
                runtimeInvalidated.countDown()
            }
            promise.resolve(null)

            Thread {
                try {
                    runtimeInvalidated.await()
                } catch (t: Throwable) {
                    BTLogger.error(
                        "[BackgroundHMR] outgoing runtime invalidation wait failed: ${t.message}",
                    )
                    backgroundHMRRestartInFlight.set(false)
                    return@Thread
                }
                if (!runtimeInvalidationSucceeded.get()) {
                    BTLogger.error(
                        "[BackgroundHMR] replacement blocked: runtime invalidation failed",
                    )
                    backgroundHMRRestartInFlight.set(false)
                    return@Thread
                }
                val destroyTask = try {
                    outgoingHost?.destroy("Background HMR restart", null)
                } catch (t: Throwable) {
                    BTLogger.error(
                        "[BackgroundHMR] outgoing host destroy threw: ${t.message}",
                    )
                    null
                }
                if (outgoingHost != null && destroyTask == null) {
                    backgroundHMRRestartInFlight.set(false)
                    return@Thread
                }
                if (destroyTask != null) {
                    try {
                        destroyTask.waitForCompletion()
                    } catch (t: Throwable) {
                        BTLogger.error(
                            "[BackgroundHMR] outgoing host destroy wait failed: ${t.message}",
                        )
                        backgroundHMRRestartInFlight.set(false)
                        return@Thread
                    }
                    if (destroyTask.isFaulted() || destroyTask.isCancelled()) {
                        BTLogger.error(
                            "[BackgroundHMR] outgoing host destroy did not complete cleanly " +
                                "(faulted=${destroyTask.isFaulted()}, " +
                                "cancelled=${destroyTask.isCancelled()}): " +
                                destroyTask.getError()?.message,
                        )
                        backgroundHMRRestartInFlight.set(false)
                        return@Thread
                    }
                }
                Handler(Looper.getMainLooper()).post {
                    if (bgReactHost !== outgoingHost) {
                        BTLogger.error(
                            "[BackgroundHMR] replacement aborted: outgoing host changed",
                        )
                        backgroundHMRRestartInFlight.set(false)
                        return@post
                    }
                    bgReactHost = null
                    if (
                        !runnerState.compareAndSet(
                            BackgroundRunnerState.DESTROYING,
                            BackgroundRunnerState.IDLE,
                        )
                    ) {
                        BTLogger.error(
                            "[BackgroundHMR] replacement aborted: unexpected state=" +
                                getBackgroundRunnerState(),
                        )
                        backgroundHMRRestartInFlight.set(false)
                        return@post
                    }
                    BTLogger.info("[BackgroundHMR] starting replacement background runtime")
                    ensureBackgroundRunner(context, entryURL, config)
                    backgroundHMRRestartInFlight.set(false)
                }
            }.apply {
                isDaemon = true
                name = "OneKey-BgThread-HMRRestart"
                start()
            }
        }
    }

    /**
     * Returns true if Runtime.exit(0) was reached (in which case the process
     * is now terminating and any code after the call site is unreachable).
     * Returns false if intent resolution failed or startActivity threw
     * before exit() — caller is responsible for propagating that failure
     * to the JS Promise so callers don't observe a false success.
     */
    private fun triggerProcessRestart(context: ReactApplicationContext): Boolean {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            if (intent == null) {
                BTLogger.error("triggerProcessRestart: launch intent not found for ${context.packageName}")
                return false
            }
            val mainIntent = Intent.makeRestartActivityTask(intent.component)
            context.startActivity(mainIntent)
            Runtime.getRuntime().exit(0)
            return true // unreachable; exit() does not return
        } catch (t: Throwable) {
            BTLogger.error("triggerProcessRestart: ${t.message}")
            return false
        }
    }

    // ── Lifecycle ───────────────────────────────────────────────────────────

    val isBackgroundStarted: Boolean
        get() = when (runnerState.get()) {
            BackgroundRunnerState.STARTING, BackgroundRunnerState.RUNNING -> true
            else -> false
        }

    fun getBackgroundRunnerState(): String = when (runnerState.get()) {
        BackgroundRunnerState.IDLE -> "idle"
        BackgroundRunnerState.STARTING -> "starting"
        BackgroundRunnerState.RUNNING -> "running"
        BackgroundRunnerState.FAILED -> "failed"
        BackgroundRunnerState.DESTROYING -> "destroying"
    }

    fun getBackgroundRunnerFailureMessage(): String? = runnerStartFailureMessage

    fun destroy() {
        runnerState.set(BackgroundRunnerState.DESTROYING)
        nativeDestroy()
        bgRuntimePtr = 0
        mainRuntimePtr = 0
        mainReactContext = null
        mainReactHost = null
        bgReactHost?.destroy("BackgroundThreadManager destroyed", null)
        bgReactHost = null
        runnerStartFailureMessage = null
        runnerState.set(BackgroundRunnerState.IDLE)
        lastResumedActivityRef = WeakReference(null)
    }
}
