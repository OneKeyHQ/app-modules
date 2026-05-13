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
import javax.crypto.spec.GCMParameterSpec
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
        private const val CIPHER_GCM_ALGORITHM = "AES/GCM/NoPadding"
        private const val GCM_AUTH_TAG_LENGTH_BITS = 128
        // AES-GCM nonce length: NIST SP 800-38D recommends 96 bits (12 bytes).
        // CryptoKit on iOS enforces 12 bytes for the default nonce, so locking
        // Android to the same length avoids the platform mismatch where
        // GCMParameterSpec would otherwise accept a non-12-byte nonce that
        // CryptoKit cannot decrypt.
        private const val GCM_NONCE_LENGTH_BYTES = 12
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

        // Reject empty strings at every native entry point. Callers MUST
        // supply concrete bytes for every argument; an empty hex string is
        // almost always an upstream bug (forgotten parameter, miswired AAD,
        // truncated input) that we want to fail loudly instead of letting
        // it sneak past the cipher layer.
        private fun requireNonEmpty(value: String?, method: String, paramName: String): String {
            if (value.isNullOrEmpty()) {
                throw IllegalArgumentException("$method: $paramName must not be empty")
            }
            return value
        }

        // Numeric arguments from the JS side arrive as Double. Coerce them to
        // a positive 32-bit integer, but reject NaN, infinities, fractional
        // values, and anything outside Int range up front — otherwise toInt()
        // would silently truncate (1.5 -> 1) or saturate (NaN -> 0), and the
        // `<= 0` guard alone would let either path slip through into cipher /
        // KDF code that subsequently treats the value as a buffer size.
        private fun requirePositiveInt(value: Double, method: String, paramName: String): Int {
            if (!value.isFinite()
                || value <= 0.0
                || value != Math.floor(value)
                || value > Int.MAX_VALUE.toDouble()
            ) {
                throw IllegalArgumentException(
                    "$method: $paramName must be a positive finite integer (<= ${Int.MAX_VALUE})"
                )
            }
            return value.toInt()
        }
    }

    override fun getName(): String = NAME

    override fun encrypt(data: String, key: String, iv: String, algorithm: String, promise: Promise) {
        try {
            requireNonEmpty(data, "encrypt", "data")
            requireNonEmpty(key, "encrypt", "key")
            requireNonEmpty(iv, "encrypt", "iv")
            requireNonEmpty(algorithm, "encrypt", "algorithm")
            val cipherAlgorithm = if (algorithm.lowercase().contains("cbc")) CIPHER_CBC_ALGORITHM else CIPHER_CTR_ALGORITHM
            val result = encryptImpl(data, key, iv, cipherAlgorithm)
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun decrypt(base64: String, key: String, iv: String, algorithm: String, promise: Promise) {
        try {
            requireNonEmpty(base64, "decrypt", "ciphertext")
            requireNonEmpty(key, "decrypt", "key")
            requireNonEmpty(iv, "decrypt", "iv")
            requireNonEmpty(algorithm, "decrypt", "algorithm")
            val cipherAlgorithm = if (algorithm.lowercase().contains("cbc")) CIPHER_CBC_ALGORITHM else CIPHER_CTR_ALGORITHM
            val result = decryptImpl(base64, key, iv, cipherAlgorithm)
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun aesGcmEncrypt(data: String, key: String, nonce: String, aad: String, promise: Promise) {
        try {
            requireNonEmpty(data, "aesGcmEncrypt", "data")
            requireNonEmpty(key, "aesGcmEncrypt", "key")
            requireNonEmpty(nonce, "aesGcmEncrypt", "nonce")
            requireNonEmpty(aad, "aesGcmEncrypt", "aad")
            val result = aesGcmEncryptImpl(data, key, nonce, aad)
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun aesGcmDecrypt(ciphertextWithTag: String, key: String, nonce: String, aad: String, promise: Promise) {
        try {
            requireNonEmpty(ciphertextWithTag, "aesGcmDecrypt", "ciphertextWithTag")
            requireNonEmpty(key, "aesGcmDecrypt", "key")
            requireNonEmpty(nonce, "aesGcmDecrypt", "nonce")
            requireNonEmpty(aad, "aesGcmDecrypt", "aad")
            val result = aesGcmDecryptImpl(ciphertextWithTag, key, nonce, aad)
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun pbkdf2(password: String, salt: String, cost: Double, length: Double, algorithm: String, promise: Promise) {
        try {
            requireNonEmpty(password, "pbkdf2", "password")
            requireNonEmpty(salt, "pbkdf2", "salt")
            requireNonEmpty(algorithm, "pbkdf2", "algorithm")
            val costInt = requirePositiveInt(cost, "pbkdf2", "cost")
            val lengthInt = requirePositiveInt(length, "pbkdf2", "length")
            val result = pbkdf2Impl(password, salt, costInt, lengthInt, algorithm)
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun hmac256(data: String, key: String, promise: Promise) {
        try {
            requireNonEmpty(data, "hmac256", "data")
            requireNonEmpty(key, "hmac256", "key")
            val result = hmacX(data, key, HMAC_SHA_256)
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun hmac512(data: String, key: String, promise: Promise) {
        try {
            requireNonEmpty(data, "hmac512", "data")
            requireNonEmpty(key, "hmac512", "key")
            val result = hmacX(data, key, HMAC_SHA_512)
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun sha1(text: String, promise: Promise) {
        try {
            requireNonEmpty(text, "sha1", "text")
            val result = shaX(text, "SHA-1")
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun sha256(text: String, promise: Promise) {
        try {
            requireNonEmpty(text, "sha256", "text")
            val result = shaX(text, "SHA-256")
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject("-1", e.message)
        }
    }

    override fun sha512(text: String, promise: Promise) {
        try {
            requireNonEmpty(text, "sha512", "text")
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
            val keyLength = requirePositiveInt(length, "randomKey", "length")
            val key = ByteArray(keyLength)
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

    private fun decodeGcmNonce(hexNonce: String): ByteArray {
        val nonceBytes = Hex.decode(hexNonce)
        if (nonceBytes.size != GCM_NONCE_LENGTH_BYTES) {
            throw IllegalArgumentException(
                "AES-GCM nonce must be exactly $GCM_NONCE_LENGTH_BYTES bytes (got ${nonceBytes.size})"
            )
        }
        return nonceBytes
    }

    private fun aesGcmEncryptImpl(text: String, hexKey: String, hexNonce: String, aad: String): String {
        val secretKey = SecretKeySpec(Hex.decode(hexKey), KEY_ALGORITHM)
        val cipher = Cipher.getInstance(CIPHER_GCM_ALGORITHM)
        val gcmSpec = GCMParameterSpec(GCM_AUTH_TAG_LENGTH_BITS, decodeGcmNonce(hexNonce))
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
        cipher.updateAAD(Hex.decode(aad))
        return bytesToHex(cipher.doFinal(Hex.decode(text)))
    }

    private fun aesGcmDecryptImpl(ciphertextWithTag: String, hexKey: String, hexNonce: String, aad: String): String {
        val encrypted = Hex.decode(ciphertextWithTag)
        val tagBytes = GCM_AUTH_TAG_LENGTH_BITS / 8
        if (encrypted.size < tagBytes) {
            throw IllegalArgumentException("AES-GCM ciphertext must include the ${tagBytes}-byte authentication tag")
        }

        val secretKey = SecretKeySpec(Hex.decode(hexKey), KEY_ALGORITHM)
        val cipher = Cipher.getInstance(CIPHER_GCM_ALGORITHM)
        val gcmSpec = GCMParameterSpec(GCM_AUTH_TAG_LENGTH_BITS, decodeGcmNonce(hexNonce))
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
        cipher.updateAAD(Hex.decode(aad))
        return bytesToHex(cipher.doFinal(encrypted))
    }
}
