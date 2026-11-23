package com.facebook.react.modules.core;
import com.facebook.react.bridge.JavaScriptModule;
public class DeviceEventManagerModule {
    public interface RCTDeviceEventEmitter extends JavaScriptModule {
        void emit(String eventName, Object data);
    }
}
