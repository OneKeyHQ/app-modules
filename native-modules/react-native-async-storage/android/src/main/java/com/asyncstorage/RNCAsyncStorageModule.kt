package com.asyncstorage

import android.content.Context
import android.content.SharedPreferences
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = RNCAsyncStorageModule.NAME)
class RNCAsyncStorageModule(reactContext: ReactApplicationContext) :
    NativeRNCAsyncStorageSpec(reactContext) {

    companion object {
        const val NAME = "RNCAsyncStorage"
        private const val PREFS_NAME = "RNCAsyncStorage"
    }

    private fun getPrefs(): SharedPreferences {
        return reactApplicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun getName(): String = NAME

    override fun multiGet(keys: ReadableArray, promise: Promise) {
        Thread {
            try {
                val prefs = getPrefs()
                val result = WritableNativeArray()
                for (i in 0 until keys.size()) {
                    val key = keys.getString(i)
                    val pair = WritableNativeArray()
                    pair.pushString(key)
                    if (prefs.contains(key)) {
                        pair.pushString(prefs.getString(key, null))
                    } else {
                        pair.pushNull()
                    }
                    result.pushArray(pair)
                }
                promise.resolve(result)
            } catch (e: Exception) {
                promise.reject("ASYNC_STORAGE_ERROR", e.message, e)
            }
        }.start()
    }

    override fun multiSet(keyValuePairs: ReadableArray, promise: Promise) {
        Thread {
            try {
                val editor = getPrefs().edit()
                for (i in 0 until keyValuePairs.size()) {
                    val pair = keyValuePairs.getArray(i)
                    if (pair != null && pair.size() >= 2) {
                        val key = pair.getString(0)
                        val value = pair.getString(1)
                        editor.putString(key, value)
                    }
                }
                editor.apply()
                promise.resolve(null)
            } catch (e: Exception) {
                promise.reject("ASYNC_STORAGE_ERROR", e.message, e)
            }
        }.start()
    }

    override fun multiRemove(keys: ReadableArray, promise: Promise) {
        Thread {
            try {
                val editor = getPrefs().edit()
                for (i in 0 until keys.size()) {
                    editor.remove(keys.getString(i))
                }
                editor.apply()
                promise.resolve(null)
            } catch (e: Exception) {
                promise.reject("ASYNC_STORAGE_ERROR", e.message, e)
            }
        }.start()
    }

    override fun multiMerge(keyValuePairs: ReadableArray, promise: Promise) {
        Thread {
            try {
                val prefs = getPrefs()
                val editor = prefs.edit()
                for (i in 0 until keyValuePairs.size()) {
                    val pair = keyValuePairs.getArray(i)
                    if (pair != null && pair.size() >= 2) {
                        val key = pair.getString(0)
                        val value = pair.getString(1)
                        // For merge, if key exists and both are JSON objects, deep merge.
                        // For simplicity, we overwrite (shallow merge by replacing value).
                        editor.putString(key, value)
                    }
                }
                editor.apply()
                promise.resolve(null)
            } catch (e: Exception) {
                promise.reject("ASYNC_STORAGE_ERROR", e.message, e)
            }
        }.start()
    }

    override fun getAllKeys(promise: Promise) {
        Thread {
            try {
                val prefs = getPrefs()
                val result = WritableNativeArray()
                for (key in prefs.all.keys) {
                    result.pushString(key)
                }
                promise.resolve(result)
            } catch (e: Exception) {
                promise.reject("ASYNC_STORAGE_ERROR", e.message, e)
            }
        }.start()
    }

    override fun clear(promise: Promise) {
        Thread {
            try {
                getPrefs().edit().clear().apply()
                promise.resolve(null)
            } catch (e: Exception) {
                promise.reject("ASYNC_STORAGE_ERROR", e.message, e)
            }
        }.start()
    }
}
