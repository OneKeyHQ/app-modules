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
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

@ReactModule(name = AesCryptoModule.NAME)
class AesCryptoModule(reactContext: ReactApplicationContext) :
    NativeAesCryptoSpec(reactContext) {

    companion object {
        const val NAME = "AesCrypto"
    }

    override fun getName(): String = NAME

    private fun cipherTransformation(algorithm: String): String {
        return when (algorithm.lowercase()) {
            "aes-128-cbc", "aes-192-cbc", "aes-256-cbc" -> "AES/CBC/PKCS5Padding"
            "aes-128-ecb", "aes-192-ecb", "aes-256-ecb" -> "AES/ECB/PKCS5Padding"
            else -> "AES/CBC/PKCS5Padding"
        }
    }

    override fun encrypt(data: String, key: String, iv: String, algorithm: String, promise: Promise) {
        Thread {
            try {
                val keyBytes = hexToBytes(key)
                val ivBytes = hexToBytes(iv)
                val transformation = cipherTransformation(algorithm)
                val cipher = Cipher.getInstance(transformation)
                val secretKey = SecretKeySpec(keyBytes, "AES")
                if (transformation.contains("ECB")) {
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
                } else {
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(ivBytes))
                }
                val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
                promise.resolve(Base64.encodeToString(encrypted, Base64.NO_WRAP))
            } catch (e: Exception) {
                promise.reject("AES_CRYPTO_ERROR", e.message, e)
            }
        }.start()
    }

    override fun decrypt(base64: String, key: String, iv: String, algorithm: String, promise: Promise) {
        Thread {
            try {
                val keyBytes = hexToBytes(key)
                val ivBytes = hexToBytes(iv)
                val transformation = cipherTransformation(algorithm)
                val cipher = Cipher.getInstance(transformation)
                val secretKey = SecretKeySpec(keyBytes, "AES")
                if (transformation.contains("ECB")) {
                    cipher.init(Cipher.DECRYPT_MODE, secretKey)
                } else {
                    cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(ivBytes))
                }
                val decrypted = cipher.doFinal(Base64.decode(base64, Base64.NO_WRAP))
                promise.resolve(String(decrypted, Charsets.UTF_8))
            } catch (e: Exception) {
                promise.reject("AES_CRYPTO_ERROR", e.message, e)
            }
        }.start()
    }

    override fun pbkdf2(password: String, salt: String, cost: Double, length: Double, algorithm: String, promise: Promise) {
        Thread {
            try {
                val saltBytes = salt.toByteArray(Charsets.UTF_8)
                val iterationCount = cost.toInt()
                val keyLength = length.toInt() * 8
                val hmacAlgorithm = when (algorithm.uppercase()) {
                    "SHA256", "SHA-256" -> "PBKDF2WithHmacSHA256"
                    "SHA512", "SHA-512" -> "PBKDF2WithHmacSHA512"
                    else -> "PBKDF2WithHmacSHA1"
                }
                val spec = PBEKeySpec(password.toCharArray(), saltBytes, iterationCount, keyLength)
                val factory = SecretKeyFactory.getInstance(hmacAlgorithm)
                val keyBytes = factory.generateSecret(spec).encoded
                promise.resolve(bytesToHex(keyBytes))
            } catch (e: Exception) {
                promise.reject("AES_CRYPTO_ERROR", e.message, e)
            }
        }.start()
    }

    override fun hmac256(base64: String, key: String, promise: Promise) {
        Thread {
            try {
                val mac = Mac.getInstance("HmacSHA256")
                val secretKey = SecretKeySpec(hexToBytes(key), "HmacSHA256")
                mac.init(secretKey)
                val result = mac.doFinal(Base64.decode(base64, Base64.NO_WRAP))
                promise.resolve(bytesToHex(result))
            } catch (e: Exception) {
                promise.reject("AES_CRYPTO_ERROR", e.message, e)
            }
        }.start()
    }

    override fun hmac512(base64: String, key: String, promise: Promise) {
        Thread {
            try {
                val mac = Mac.getInstance("HmacSHA512")
                val secretKey = SecretKeySpec(hexToBytes(key), "HmacSHA512")
                mac.init(secretKey)
                val result = mac.doFinal(Base64.decode(base64, Base64.NO_WRAP))
                promise.resolve(bytesToHex(result))
            } catch (e: Exception) {
                promise.reject("AES_CRYPTO_ERROR", e.message, e)
            }
        }.start()
    }

    override fun sha1(text: String, promise: Promise) {
        Thread {
            try {
                val digest = MessageDigest.getInstance("SHA-1")
                val result = digest.digest(text.toByteArray(Charsets.UTF_8))
                promise.resolve(bytesToHex(result))
            } catch (e: Exception) {
                promise.reject("AES_CRYPTO_ERROR", e.message, e)
            }
        }.start()
    }

    override fun sha256(text: String, promise: Promise) {
        Thread {
            try {
                val digest = MessageDigest.getInstance("SHA-256")
                val result = digest.digest(text.toByteArray(Charsets.UTF_8))
                promise.resolve(bytesToHex(result))
            } catch (e: Exception) {
                promise.reject("AES_CRYPTO_ERROR", e.message, e)
            }
        }.start()
    }

    override fun sha512(text: String, promise: Promise) {
        Thread {
            try {
                val digest = MessageDigest.getInstance("SHA-512")
                val result = digest.digest(text.toByteArray(Charsets.UTF_8))
                promise.resolve(bytesToHex(result))
            } catch (e: Exception) {
                promise.reject("AES_CRYPTO_ERROR", e.message, e)
            }
        }.start()
    }

    override fun randomUuid(promise: Promise) {
        Thread {
            try {
                promise.resolve(UUID.randomUUID().toString())
            } catch (e: Exception) {
                promise.reject("AES_CRYPTO_ERROR", e.message, e)
            }
        }.start()
    }

    override fun randomKey(length: Double, promise: Promise) {
        Thread {
            try {
                val bytes = ByteArray(length.toInt())
                SecureRandom().nextBytes(bytes)
                promise.resolve(bytesToHex(bytes))
            } catch (e: Exception) {
                promise.reject("AES_CRYPTO_ERROR", e.message, e)
            }
        }.start()
    }

    private fun hexToBytes(hex: String): ByteArray {
        val len = hex.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hex[i], 16) shl 4) + Character.digit(hex[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }
}
