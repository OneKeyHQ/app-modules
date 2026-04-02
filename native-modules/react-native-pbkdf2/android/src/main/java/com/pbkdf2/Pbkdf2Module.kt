package com.pbkdf2

import android.util.Base64
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@ReactModule(name = Pbkdf2Module.NAME)
class Pbkdf2Module(reactContext: ReactApplicationContext) :
    NativePbkdf2Spec(reactContext) {

    companion object {
        const val NAME = "Pbkdf2"
    }

    override fun getName(): String = NAME

    override fun derive(
        password: String,
        salt: String,
        rounds: Double,
        keyLength: Double,
        hash: String,
        promise: Promise
    ) {
        Thread {
            try {
                val passwordBytes = Base64.decode(password, Base64.DEFAULT)
                val saltBytes = Base64.decode(salt, Base64.DEFAULT)
                val iterationCount = rounds.toInt()
                val keyLengthBits = keyLength.toInt() * 8

                val algorithm = when (hash.uppercase()) {
                    "SHA256", "SHA-256" -> "PBKDF2WithHmacSHA256"
                    "SHA512", "SHA-512" -> "PBKDF2WithHmacSHA512"
                    else -> "PBKDF2WithHmacSHA256"
                }

                val spec = PBEKeySpec(
                    String(passwordBytes, Charsets.UTF_8).toCharArray(),
                    saltBytes,
                    iterationCount,
                    keyLengthBits
                )
                val factory = SecretKeyFactory.getInstance(algorithm)
                val derivedKey = factory.generateSecret(spec).encoded
                promise.resolve(Base64.encodeToString(derivedKey, Base64.NO_WRAP))
            } catch (e: Exception) {
                promise.reject("PBKDF2_ERROR", e.message, e)
            }
        }.start()
    }
}
