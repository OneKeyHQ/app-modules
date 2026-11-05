package com.facebook.react.bridge;
public interface WritableMap extends ReadableMap {
    void putString(String key, String value);
    void putInt(String key, int value);
    void putBoolean(String key, boolean value);
    void putArray(String key, ReadableArray value);
    void putMap(String key, ReadableMap value);
}
