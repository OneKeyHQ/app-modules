package so.onekey.app.wallet.lite.keys

import android.content.Context

class KeysNativeProvider {
    companion object {
        init {
            System.loadLibrary("keys")
        }
    }

    external fun getLiteSecureChannelInitParams(context: Context): String
}