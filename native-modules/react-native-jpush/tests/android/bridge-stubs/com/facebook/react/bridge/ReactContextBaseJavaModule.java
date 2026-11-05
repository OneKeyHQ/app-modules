package com.facebook.react.bridge;
import android.app.Activity;

public abstract class ReactContextBaseJavaModule implements NativeModule {
    private final ReactApplicationContext reactContext;

    public ReactContextBaseJavaModule(ReactApplicationContext reactContext) {
        this.reactContext = reactContext;
    }

    protected final ReactApplicationContext getReactApplicationContext() {
        return reactContext;
    }

    protected final Activity getCurrentActivity() {
        return reactContext != null ? reactContext.getCurrentActivity() : null;
    }
}
