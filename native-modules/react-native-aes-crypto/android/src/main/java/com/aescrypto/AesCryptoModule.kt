package com.aescrypto

import android.util.Base64
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import org.spongycastle.crypto.Digest
import org.spongycastle.crypto.digests.SHA1Digest
import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.crypto.digests.SHA512Digest
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator
import org.spongycastle.crypto.params.KeyParameter
import org.spongycastle.util.encoders.Hex

/**
 * Ported from upstream react-native-aes-crypto Aes.java.
 * Adapted to extend NativeAesCryptoSpec (TurboModule).
 */
@ReactModule(name = AesCryptoModule.NAME)
class AesCryptoModule(reactContext: ReactApplicationContext) :
    NativeAesCryptoSpec(reactContext) {

    companion object {
        const val NAME = "AesCrypto"
        private const val CIPHER_CBC_ALGORITHM = "AES/CBC/PKCS7Padding"
        private const val CIPHER_CTR_ALGORITHM = "AES/CTR/PKCS5Padding"
        private const val HMAC_SHA_256 = "HmacSHA256"
        private const val HMAC_SHA_512 = "HmacSHA512"
        private const val KEY_ALGORITHM = "AES"

        private val emptyIvSpec = IvParameterSpec(ByteArray(16) { 0x00 })

        @JvmStatic
        fun bytesToHex(bytes: ByteArray): String {
            val hexArray = "0123456789abcdef".toCharArray()
            val hexChars = CharArray(bytes.size * 2)
            for (j in bytes.indices) {
                val v = bytes[j].toInt() and 0xFF
                hexChars[j * 2] = hexArray[v ushr 4]
                hexChars[j * 2 + 1] = hexArray[v and 0x0F]
            }
            return String(hexChars)
        }
    }

    override fun getName(): String = NAME

    override fun encrypt(data: String, key: String, iv: String, algorithm: String, promise: Promise) {
        try {
            val cipherAlgorithm = if (algorithm.lowercase().contains("cbc")) CIPHER_CBC_ALGORITHM else CIPHER_CTR_ALGORITHM
            val result = encryptImpl(data, key, iv, cipherAlgorithm)
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun decrypt(base64: String, key: String, iv: String, algorithm: String, promise: Promise) {
        try {
            val cipherAlgorithm = if (algorithm.lowercase().contains("cbc")) CIPHER_CBC_ALGORITHM else CIPHER_CTR_ALGORITHM
            val result = decryptImpl(base64, key, iv, cipherAlgorithm)
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun pbkdf2(password: String, salt: String, cost: Double, length: Double, algorithm: String, promise: Promise) {
        try {
            val result = pbkdf2Impl(password, salt, cost.toInt(), length.toInt(), algorithm)
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun hmac256(data: String, key: String, promise: Promise) {
        try {
            val result = hmacX(data, key, HMAC_SHA_256)
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun hmac512(data: String, key: String, promise: Promise) {
        try {
            val result = hmacX(data, key, HMAC_SHA_512)
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun sha1(text: String, promise: Promise) {
        try {
            val result = shaX(text, "SHA-1")
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun sha256(text: String, promise: Promise) {
        try {
            val result = shaX(text, "SHA-256")
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun sha512(text: String, promise: Promise) {
        try {
            val result = shaX(text, "SHA-512")
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun randomUuid(promise: Promise) {
        try {
            promise.resolve(UUID.randomUUID().toString())
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun randomKey(length: Double, promise: Promise) {
        try {
            val key = ByteArray(length.toInt())
            SecureRandom().nextBytes(key)
            promise.resolve(bytesToHex(key))
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    // --- Private helpers (ported from upstream Aes.java) ---

    private fun shaX(data: String, algorithm: String): String {
        val md = MessageDigest.getInstance(algorithm)
        md.update(Hex.decode(data))
        return bytesToHex(md.digest())
    }

    private fun pbkdf2Impl(pwd: String, salt: String, cost: Int, length: Int, algorithm: String): String {
        val algorithmDigest: Digest = when {
            algorithm.equals("sha1", ignoreCase = true) -> SHA1Digest()
            algorithm.equals("sha256", ignoreCase = true) -> SHA256Digest()
            algorithm.equals("sha512", ignoreCase = true) -> SHA512Digest()
            else -> SHA512Digest()
        }
        val gen = PKCS5S2ParametersGenerator(algorithmDigest)
        gen.init(Hex.decode(pwd), Hex.decode(salt), cost)
        val key = (gen.generateDerivedParameters(length) as KeyParameter).key
        return bytesToHex(key)
    }

    private fun hmacX(text: String, key: String, algorithm: String): String {
        val contentData = Hex.decode(text)
        val akHexData = Hex.decode(key)
        val mac = Mac.getInstance(algorithm)
        val secretKey = SecretKeySpec(akHexData, algorithm)
        mac.init(secretKey)
        return bytesToHex(mac.doFinal(contentData))
    }

    private fun encryptImpl(text: String, hexKey: String, hexIv: String?, algorithm: String): String? {
        if (text.isEmpty()) return null

        val key = Hex.decode(hexKey)
        val secretKey = SecretKeySpec(key, KEY_ALGORITHM)
        val cipher = Cipher.getInstance(algorithm)
        val ivSpec = if (hexIv == null || hexIv.isEmpty()) emptyIvSpec else IvParameterSpec(Hex.decode(hexIv))
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        val encrypted = cipher.doFinal(Hex.decode(text))
        return bytesToHex(encrypted)
    }

    private fun decryptImpl(ciphertext: String, hexKey: String, hexIv: String?, algorithm: String): String? {
        if (ciphertext.isEmpty()) return null

        val key = Hex.decode(hexKey)
        val secretKey = SecretKeySpec(key, KEY_ALGORITHM)
        val cipher = Cipher.getInstance(algorithm)
        val ivSpec = if (hexIv == null || hexIv.isEmpty()) emptyIvSpec else IvParameterSpec(Hex.decode(hexIv))
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        val decrypted = cipher.doFinal(Hex.decode(ciphertext))
        return bytesToHex(decrypted)
    }
}
