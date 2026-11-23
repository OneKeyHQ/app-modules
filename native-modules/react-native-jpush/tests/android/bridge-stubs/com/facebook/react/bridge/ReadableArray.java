package com.facebook.react.bridge;
public interface ReadableArray {
    int size();
    String getString(int index);
    int getInt(int index);
}
