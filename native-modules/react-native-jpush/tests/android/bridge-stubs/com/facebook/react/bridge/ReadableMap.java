package com.facebook.react.bridge;
import java.util.HashMap;
public interface ReadableMap {
    boolean hasKey(String name);
    boolean getBoolean(String name);
    int getInt(String name);
    String getString(String name);
    ReadableArray getArray(String name);
    ReadableMap getMap(String name);
    HashMap<String, Object> toHashMap();
}
