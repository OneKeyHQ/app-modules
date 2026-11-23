package com.facebook.react.bridge;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadableNativeMap implements WritableMap {
    private final HashMap<String, Object> map = new HashMap<String, Object>();

    public ReadableNativeMap() {}

    public ReadableNativeMap(Map<String, Object> initial) {
        if (initial != null) {
            map.putAll(initial);
        }
    }

    @Override
    public boolean hasKey(String name) {
        return map.containsKey(name);
    }

    @Override
    public boolean getBoolean(String name) {
        Object v = map.get(name);
        return v instanceof Boolean && (Boolean) v;
    }

    @Override
    public int getInt(String name) {
        Object v = map.get(name);
        return v instanceof Number ? ((Number) v).intValue() : 0;
    }

    @Override
    public String getString(String name) {
        Object v = map.get(name);
        return v != null ? String.valueOf(v) : null;
    }

    @Override
    public ReadableArray getArray(String name) {
        Object v = map.get(name);
        return v instanceof ReadableArray ? (ReadableArray) v : null;
    }

    @Override
    public ReadableMap getMap(String name) {
        Object v = map.get(name);
        return v instanceof ReadableMap ? (ReadableMap) v : null;
    }

    @Override
    public HashMap<String, Object> toHashMap() {
        return new HashMap<String, Object>(map);
    }

    @Override
    public void putString(String key, String value) {
        map.put(key, value);
    }

    @Override
    public void putInt(String key, int value) {
        map.put(key, value);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        map.put(key, value);
    }

    @Override
    public void putArray(String key, ReadableArray value) {
        map.put(key, value);
    }

    @Override
    public void putMap(String key, ReadableMap value) {
        map.put(key, value);
    }

    public static class SimpleWritableArray implements WritableArray {
        private final List<Object> items = new ArrayList<Object>();

        @Override
        public int size() {
            return items.size();
        }

        @Override
        public String getString(int index) {
            Object v = items.get(index);
            return v != null ? String.valueOf(v) : null;
        }

        @Override
        public int getInt(int index) {
            Object v = items.get(index);
            return v instanceof Number ? ((Number) v).intValue() : 0;
        }

        @Override
        public void pushString(String value) {
            items.add(value);
        }
    }
}
