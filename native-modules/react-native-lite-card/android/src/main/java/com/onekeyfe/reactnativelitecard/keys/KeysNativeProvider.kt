package com.onekeyfe.reactnativelitecard.keys

import android.content.Context

class KeysNativeProvider {
    companion object {
        init {
            System.loadLibrary("keys")
        }
    }

    external fun getLiteSecureChannelInitParams(context: Context): String
}