package com.margelo.nitro.reactnativedeviceutils

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfoProvider

class ReactNativeDeviceUtilsPackage : BaseReactPackage() {
    override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
        // Set the React context for the DeviceUtils module
        ReactNativeDeviceUtils.setReactContext(reactContext)
        return null
    }

    override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {
        return ReactModuleInfoProvider { HashMap() }
    }

    companion object {
        init {
            System.loadLibrary("reactnativedeviceutils")
        }
    }
}


