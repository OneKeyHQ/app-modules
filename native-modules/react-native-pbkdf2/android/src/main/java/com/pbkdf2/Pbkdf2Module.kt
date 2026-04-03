package com.pbkdf2

import android.util.Base64
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import org.spongycastle.crypto.Digest
import org.spongycastle.crypto.digests.SHA1Digest
import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.crypto.digests.SHA512Digest
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator
import org.spongycastle.crypto.params.KeyParameter

/**
 * Ported from upstream react-native-fast-pbkdf2 Pbkdf2Module.java.
 * Adapted to extend NativePbkdf2Spec (TurboModule).
 */
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
        try {
            val decodedPassword = Base64.decode(password, Base64.DEFAULT)
            val decodedSalt = Base64.decode(salt, Base64.DEFAULT)

            // Default to SHA1 (matching upstream original)
            val digest: Digest = when (hash) {
                "sha-256" -> SHA256Digest()
                "sha-512" -> SHA512Digest()
                else -> SHA1Digest()
            }

            val gen = PKCS5S2ParametersGenerator(digest)
            gen.init(decodedPassword, decodedSalt, rounds.toInt())
            val key = (gen.generateDerivedParameters(keyLength.toInt() * 8) as KeyParameter).key
            promise.resolve(Base64.encodeToString(key, Base64.DEFAULT))
        } catch (e: Exception) {
            promise.reject("PBKDF2_ERROR", e.message, e)
        }
    }
}
