package com.facebook.react.bridge;
public class Arguments {
    public static WritableMap createMap() {
        return new ReadableNativeMap();
    }

    public static WritableArray createArray() {
        return new ReadableNativeMap.SimpleWritableArray();
    }
}
