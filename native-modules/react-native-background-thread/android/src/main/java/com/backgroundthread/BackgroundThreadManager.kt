package com.backgroundthread

import android.net.Uri
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

        context.runOnJSQueueThread {
            try {
                if (context.hasCatalystInstance()) {
                    context.catalystInstance.registerSegment(segmentId, path)
                    BTLogger.info("Segment registered in background runtime: id=$segmentId, path=$path")
                    onComplete(null)
                } else {
                    onComplete(IllegalStateException("Background CatalystInstance not available for segment registration"))
                }
            } catch (e: Exception) {
                BTLogger.error("Failed to register segment in background runtime: ${e.message}")
                onComplete(e)
            }
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
    }
}
