package com.margelo.nitro.chartwebview

import android.view.View
import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfoProvider
import com.facebook.react.uimanager.ViewManager

import com.margelo.nitro.chartwebview.views.HybridChartWebviewManager

/**
 * View manager subclass that adds a teardown hook on top of the nitrogen-generated
 * [HybridChartWebviewManager] (which is DO-NOT-MODIFY). The base manager removes
 * the view from its private table on drop but never tears the WebView down, so the
 * non-pooled (private) path would leak a Chromium renderer + JavascriptInterface
 * per mount/unmount. On drop we reach the host via the container ([HostAware]) and
 * call [HybridChartWebview.dispose].
 */
class TeardownChartWebviewManager : HybridChartWebviewManager() {
  override fun onDropViewInstance(view: View) {
    (view as? HostAware)?.chartHost?.dispose()
    super.onDropViewInstance(view)
  }
}

/** Implemented by the host's container view so the manager can reach the host on drop. */
interface HostAware {
  val chartHost: HybridChartWebview
}

class ChartWebviewPackage : BaseReactPackage() {
    override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
        return null
    }

    override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {
        return ReactModuleInfoProvider { HashMap() }
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        // Use the teardown-aware subclass so the non-pooled WebView is destroyed on
        // host drop (the base manager never tears it down).
        return listOf(TeardownChartWebviewManager())
    }

    companion object {
        init {
            System.loadLibrary("chartwebview")
        }
    }
}
