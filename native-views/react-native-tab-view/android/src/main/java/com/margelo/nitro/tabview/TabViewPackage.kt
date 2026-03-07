package com.margelo.nitro.tabview

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfoProvider
import com.facebook.react.uimanager.ViewManager

import com.margelo.nitro.tabview.views.HybridTabViewManager
import com.margelo.nitro.tabview.views.HybridBottomAccessoryViewManager

class TabViewPackage : BaseReactPackage() {
    override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
        return null
    }

    override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {
        return ReactModuleInfoProvider { HashMap() }
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return listOf(HybridTabViewManager(), HybridBottomAccessoryViewManager())
    }

    companion object {
        init {
            System.loadLibrary("tabview")
        }
    }
}
