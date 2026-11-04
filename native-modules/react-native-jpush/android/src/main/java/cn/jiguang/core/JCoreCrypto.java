package cn.jiguang.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class JCoreCrypto {
    private static final int[] MULTIPLIERS = { 8, 5, 23, 3, 13, 17, 7, 31, 29, 37 };
    private static final int[] MODULI = { 74, 88, 15, 73, 96, 49, 68, 39, 41, 91 };
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String HTTP_REPORT_IV_BASE64 = "aW9wMjAzMDQwNTA2YVBrIQ==";

    private static final int[] SM4_FK = {
        0xa3b1bac6, 0x56aa3350, 0x677d9197, 0xb27022dc
    };

    private static final int[] SM4_CK = {
        0x00070e15, 0x1c232a31, 0x383f464d, 0x545b6269,
        0x70777e85, 0x8c939aa1, 0xa8afb6bd, 0xc4cbd2d9,
        0xe0e7eef5, 0xfc030a11, 0x181f262d, 0x343b4249,
        0x50575e65, 0x6c737a81, 0x888f969d, 0xa4abb2b9,
        0xc0c7ced5, 0xdce3eaf1, 0xf8ff060d, 0x141b2229,
        0x30373e45, 0x4c535a61, 0x686f767d, 0x848b9299,
        0xa0a7aeb5, 0xbcc3cad1, 0xd8dfe6ed, 0xf4fb0209,
        0x10171e25, 0x2c333a41, 0x484f565d, 0x646b7279
    };

    private static final byte[] SM4_SBOX = {
        (byte) 0xd6, (byte) 0x90, (byte) 0xe9, (byte) 0xfe, (byte) 0xcc, (byte) 0xe1, (byte) 0x3d, (byte) 0xb7,
        (byte) 0x16, (byte) 0xb6, (byte) 0x14, (byte) 0xc2, (byte) 0x28, (byte) 0xfb, (byte) 0x2c, (byte) 0x05,
        (byte) 0x2b, (byte) 0x67, (byte) 0x9a, (byte) 0x76, (byte) 0x2a, (byte) 0xbe, (byte) 0x04, (byte) 0xc3,
        (byte) 0xaa, (byte) 0x44, (byte) 0x13, (byte) 0x26, (byte) 0x49, (byte) 0x86, (byte) 0x06, (byte) 0x99,
        (byte) 0x9c, (byte) 0x42, (byte) 0x50, (byte) 0xf4, (byte) 0x91, (byte) 0xef, (byte) 0x98, (byte) 0x7a,
        (byte) 0x33, (byte) 0x54, (byte) 0x0b, (byte) 0x43, (byte) 0xed, (byte) 0xcf, (byte) 0xac, (byte) 0x62,
        (byte) 0xe4, (byte) 0xb3, (byte) 0x1c, (byte) 0xa9, (byte) 0xc9, (byte) 0x08, (byte) 0xe8, (byte) 0x95,
        (byte) 0x80, (byte) 0xdf, (byte) 0x94, (byte) 0xfa, (byte) 0x75, (byte) 0x8f, (byte) 0x3f, (byte) 0xa6,
        (byte) 0x47, (byte) 0x07, (byte) 0xa7, (byte) 0xfc, (byte) 0xf3, (byte) 0x73, (byte) 0x17, (byte) 0xba,
        (byte) 0x83, (byte) 0x59, (byte) 0x3c, (byte) 0x19, (byte) 0xe6, (byte) 0x85, (byte) 0x4f, (byte) 0xa8,
        (byte) 0x68, (byte) 0x6b, (byte) 0x81, (byte) 0xb2, (byte) 0x71, (byte) 0x64, (byte) 0xda, (byte) 0x8b,
        (byte) 0xf8, (byte) 0xeb, (byte) 0x0f, (byte) 0x4b, (byte) 0x70, (byte) 0x56, (byte) 0x9d, (byte) 0x35,
        (byte) 0x1e, (byte) 0x24, (byte) 0x0e, (byte) 0x5e, (byte) 0x63, (byte) 0x58, (byte) 0xd1, (byte) 0xa2,
        (byte) 0x25, (byte) 0x22, (byte) 0x7c, (byte) 0x3b, (byte) 0x01, (byte) 0x21, (byte) 0x78, (byte) 0x87,
        (byte) 0xd4, (byte) 0x00, (byte) 0x46, (byte) 0x57, (byte) 0x9f, (byte) 0xd3, (byte) 0x27, (byte) 0x52,
        (byte) 0x4c, (byte) 0x36, (byte) 0x02, (byte) 0xe7, (byte) 0xa0, (byte) 0xc4, (byte) 0xc8, (byte) 0x9e,
        (byte) 0xea, (byte) 0xbf, (byte) 0x8a, (byte) 0xd2, (byte) 0x40, (byte) 0xc7, (byte) 0x38, (byte) 0xb5,
        (byte) 0xa3, (byte) 0xf7, (byte) 0xf2, (byte) 0xce, (byte) 0xf9, (byte) 0x61, (byte) 0x15, (byte) 0xa1,
        (byte) 0xe0, (byte) 0xae, (byte) 0x5d, (byte) 0xa4, (byte) 0x9b, (byte) 0x34, (byte) 0x1a, (byte) 0x55,
        (byte) 0xad, (byte) 0x93, (byte) 0x32, (byte) 0x30, (byte) 0xf5, (byte) 0x8c, (byte) 0xb1, (byte) 0xe3,
        (byte) 0x1d, (byte) 0xf6, (byte) 0xe2, (byte) 0x2e, (byte) 0x82, (byte) 0x66, (byte) 0xca, (byte) 0x60,
        (byte) 0xc0, (byte) 0x29, (byte) 0x23, (byte) 0xab, (byte) 0x0d, (byte) 0x53, (byte) 0x4e, (byte) 0x6f,
        (byte) 0xd5, (byte) 0xdb, (byte) 0x37, (byte) 0x45, (byte) 0xde, (byte) 0xfd, (byte) 0x8e, (byte) 0x2f,
        (byte) 0x03, (byte) 0xff, (byte) 0x6a, (byte) 0x72, (byte) 0x6d, (byte) 0x6c, (byte) 0x5b, (byte) 0x51,
        (byte) 0x8d, (byte) 0x1b, (byte) 0xaf, (byte) 0x92, (byte) 0xbb, (byte) 0xdd, (byte) 0xbc, (byte) 0x7f,
        (byte) 0x11, (byte) 0xd9, (byte) 0x5c, (byte) 0x41, (byte) 0x1f, (byte) 0x10, (byte) 0x5a, (byte) 0xd8,
        (byte) 0x0a, (byte) 0xc1, (byte) 0x31, (byte) 0x88, (byte) 0xa5, (byte) 0xcd, (byte) 0x7b, (byte) 0xbd,
        (byte) 0x2d, (byte) 0x74, (byte) 0xd0, (byte) 0x12, (byte) 0xb8, (byte) 0xe5, (byte) 0xb4, (byte) 0xb0,
        (byte) 0x89, (byte) 0x69, (byte) 0x97, (byte) 0x4a, (byte) 0x0c, (byte) 0x96, (byte) 0x77, (byte) 0x7e,
        (byte) 0x65, (byte) 0xb9, (byte) 0xf1, (byte) 0x09, (byte) 0xc5, (byte) 0x6e, (byte) 0xc6, (byte) 0x84,
        (byte) 0x18, (byte) 0xf0, (byte) 0x7d, (byte) 0xec, (byte) 0x3a, (byte) 0xdc, (byte) 0x4d, (byte) 0x20,
        (byte) 0x79, (byte) 0xee, (byte) 0x5f, (byte) 0x3e, (byte) 0xd7, (byte) 0xcb, (byte) 0x39, (byte) 0x48
    };

    private JCoreCrypto() {
    }

    public static String generateSeedKey(long seed) {
        int index = (int) (Math.abs(seed) % 10L);
        long value = seed * MULTIPLIERS[index] + (seed % MODULI[index]);
        return md5Hex("JCKP" + value);
    }

    public static int nextPositiveSeed24() {
        int val = SECURE_RANDOM.nextInt() & 0xFFFFFF;
        return val != 0 ? val : 1;
    }

    public static int nextRegisterSeed() {
        return Math.abs(SECURE_RANDOM.nextInt(10000000)) + 1;
    }

    public static String generateHttpSeedKey() {
        return generateSeedKey(nextRegisterSeed());
    }

    public static byte[] aes256CbcSeedEncrypt(byte[] data, long seed) {
        String key32 = generateSeedKey(seed);
        String iv16 = key32.substring(0, 16);
        return aesCbcEncrypt(data, key32, iv16, 32);
    }

    public static byte[] aes256CbcSeedDecrypt(byte[] data, long seed) {
        String key32 = generateSeedKey(seed);
        String iv16 = key32.substring(0, 16);
        return aesCbcDecrypt(data, key32, iv16, 32);
    }

    public static byte[] aes256CbcHttpEncrypt(byte[] data, String key32) {
        byte[] iv = Base64.getDecoder().decode(HTTP_REPORT_IV_BASE64);
        return doAesCbcBytes(
            Cipher.ENCRYPT_MODE,
            data,
            key32 != null ? key32.getBytes(StandardCharsets.UTF_8) : new byte[0],
            iv,
            32
        );
    }

    public static byte[] aes256CbcHttpDecrypt(byte[] data, String key32) {
        byte[] iv = Base64.getDecoder().decode(HTTP_REPORT_IV_BASE64);
        return doAesCbcBytes(
            Cipher.DECRYPT_MODE,
            data,
            key32 != null ? key32.getBytes(StandardCharsets.UTF_8) : new byte[0],
            iv,
            32
        );
    }

    public static byte[] aesCbcEncrypt(byte[] data, String key, String iv, int keyLengthBytes) {
        return doAesCbc(Cipher.ENCRYPT_MODE, data, key, iv, keyLengthBytes);
    }

    public static byte[] aesCbcDecrypt(byte[] data, String key, String iv, int keyLengthBytes) {
        return doAesCbc(Cipher.DECRYPT_MODE, data, key, iv, keyLengthBytes);
    }

    public static byte[] aesEcbEncrypt(byte[] data, String key, int keyLengthBytes) {
        return doAesEcb(Cipher.ENCRYPT_MODE, data, key, keyLengthBytes);
    }

    public static byte[] aesEcbDecrypt(byte[] data, String key, int keyLengthBytes) {
        return doAesEcb(Cipher.DECRYPT_MODE, data, key, keyLengthBytes);
    }

    private static byte[] doAesCbc(int mode, byte[] data, String key, String iv, int keyLengthBytes) {
        if (key == null) {
            return new byte[0];
        }
        return doAesCbcBytes(
            mode,
            data,
            key.getBytes(StandardCharsets.UTF_8),
            iv != null ? iv.getBytes(StandardCharsets.UTF_8) : null,
            keyLengthBytes
        );
    }

    private static byte[] doAesCbcBytes(int mode, byte[] data, byte[] rawKey, byte[] rawIv, int keyLengthBytes) {
        if (data == null || data.length == 0 || rawKey == null || rawKey.length == 0) {
            return new byte[0];
        }
        try {
            byte[] normalizedKey = new byte[keyLengthBytes];
            System.arraycopy(rawKey, 0, normalizedKey, 0, Math.min(rawKey.length, keyLengthBytes));

            byte[] normalizedIv = new byte[16];
            if (rawIv != null) {
                System.arraycopy(rawIv, 0, normalizedIv, 0, Math.min(rawIv.length, 16));
            }

            SecretKeySpec secretKey = new SecretKeySpec(normalizedKey, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(normalizedIv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(mode, secretKey, ivSpec);
            return cipher.doFinal(data);
        } catch (Throwable ignored) {
            return new byte[0];
        }
    }

    private static byte[] doAesEcb(int mode, byte[] data, String key, int keyLengthBytes) {
        if (data == null || data.length == 0 || key == null) {
            return new byte[0];
        }
        try {
            byte[] rawKey = key.getBytes(StandardCharsets.UTF_8);
            byte[] normalizedKey = new byte[keyLengthBytes];
            System.arraycopy(rawKey, 0, normalizedKey, 0, Math.min(rawKey.length, keyLengthBytes));
            SecretKeySpec secretKey = new SecretKeySpec(normalizedKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(mode, secretKey);
            return cipher.doFinal(data);
        } catch (Throwable ignored) {
            return new byte[0];
        }
    }

    public static byte[] sm4CbcSeedEncrypt(byte[] data, long seed) {
        String keyHex = generateSeedKey(seed);
        byte[] key16 = hexDecode(keyHex);
        byte[] iv16 = keyHex.substring(0, 16).getBytes(StandardCharsets.UTF_8);
        return sm4CbcEncrypt(data, key16, iv16);
    }

    public static byte[] sm4CbcSeedDecrypt(byte[] data, long seed) {
        String keyHex = generateSeedKey(seed);
        byte[] key16 = hexDecode(keyHex);
        byte[] iv16 = keyHex.substring(0, 16).getBytes(StandardCharsets.UTF_8);
        return sm4CbcDecrypt(data, key16, iv16);
    }

    public static byte[] sm4CbcEncrypt(byte[] data, byte[] key16, byte[] iv16) {
        if (data == null || key16 == null || key16.length != 16 || iv16 == null || iv16.length < 16) {
            return new byte[0];
        }
        int[] rk = sm4ExpandKey(key16, true);
        int padLen = 16 - (data.length % 16);
        byte[] padded = Arrays.copyOf(data, data.length + padLen);
        for (int i = data.length; i < padded.length; i++) {
            padded[i] = (byte) padLen;
        }
        byte[] out = new byte[padded.length];
        byte[] prev = Arrays.copyOf(iv16, 16);
        byte[] block = new byte[16];
        for (int offset = 0; offset < padded.length; offset += 16) {
            for (int i = 0; i < 16; i++) {
                block[i] = (byte) (padded[offset + i] ^ prev[i]);
            }
            byte[] encBlock = sm4CryptBlock(block, rk);
            System.arraycopy(encBlock, 0, out, offset, 16);
            prev = encBlock;
        }
        return out;
    }

    public static byte[] sm4CbcDecrypt(byte[] cipherText, byte[] key16, byte[] iv16) {
        if (cipherText == null || cipherText.length == 0 || (cipherText.length % 16) != 0
            || key16 == null || key16.length != 16 || iv16 == null || iv16.length < 16) {
            return new byte[0];
        }
        int[] rk = sm4ExpandKey(key16, false);
        byte[] out = new byte[cipherText.length];
        byte[] prev = Arrays.copyOf(iv16, 16);
        byte[] block = new byte[16];
        for (int offset = 0; offset < cipherText.length; offset += 16) {
            System.arraycopy(cipherText, offset, block, 0, 16);
            byte[] decBlock = sm4CryptBlock(block, rk);
            for (int i = 0; i < 16; i++) {
                out[offset + i] = (byte) (decBlock[i] ^ prev[i]);
            }
            System.arraycopy(block, 0, prev, 0, 16);
        }
        int padLen = out[out.length - 1] & 0xFF;
        if (padLen < 1 || padLen > 16 || padLen > out.length) {
            return new byte[0];
        }
        for (int i = out.length - padLen; i < out.length; i++) {
            if ((out[i] & 0xFF) != padLen) {
                return new byte[0];
            }
        }
        return Arrays.copyOf(out, out.length - padLen);
    }

    private static int[] sm4ExpandKey(byte[] key16, boolean forEncryption) {
        int[] k = new int[36];
        for (int i = 0; i < 4; i++) {
            k[i] = packBe32(key16, i * 4) ^ SM4_FK[i];
        }
        int[] rk = new int[32];
        for (int i = 0; i < 32; i++) {
            int t = k[i + 1] ^ k[i + 2] ^ k[i + 3] ^ SM4_CK[i];
            int tau = sm4Tau(t);
            int lPrime = tau ^ Integer.rotateLeft(tau, 13) ^ Integer.rotateLeft(tau, 23);
            k[i + 4] = k[i] ^ lPrime;
            rk[forEncryption ? i : (31 - i)] = k[i + 4];
        }
        return rk;
    }

    private static byte[] sm4CryptBlock(byte[] block16, int[] rk) {
        int[] x = new int[36];
        for (int i = 0; i < 4; i++) {
            x[i] = packBe32(block16, i * 4);
        }
        for (int i = 0; i < 32; i++) {
            int t = x[i + 1] ^ x[i + 2] ^ x[i + 3] ^ rk[i];
            int tau = sm4Tau(t);
            int l = tau
                ^ Integer.rotateLeft(tau, 2)
                ^ Integer.rotateLeft(tau, 10)
                ^ Integer.rotateLeft(tau, 18)
                ^ Integer.rotateLeft(tau, 24);
            x[i + 4] = x[i] ^ l;
        }
        byte[] out = new byte[16];
        unpackBe32(x[35], out, 0);
        unpackBe32(x[34], out, 4);
        unpackBe32(x[33], out, 8);
        unpackBe32(x[32], out, 12);
        return out;
    }

    private static int sm4Tau(int a) {
        int b0 = SM4_SBOX[(a >>> 24) & 0xFF] & 0xFF;
        int b1 = SM4_SBOX[(a >>> 16) & 0xFF] & 0xFF;
        int b2 = SM4_SBOX[(a >>> 8) & 0xFF] & 0xFF;
        int b3 = SM4_SBOX[a & 0xFF] & 0xFF;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }

    private static int packBe32(byte[] b, int offset) {
        return ((b[offset] & 0xFF) << 24)
            | ((b[offset + 1] & 0xFF) << 16)
            | ((b[offset + 2] & 0xFF) << 8)
            | (b[offset + 3] & 0xFF);
    }

    private static void unpackBe32(int val, byte[] b, int offset) {
        b[offset] = (byte) (val >>> 24);
        b[offset + 1] = (byte) (val >>> 16);
        b[offset + 2] = (byte) (val >>> 8);
        b[offset + 3] = (byte) val;
    }

    public static byte[] hexDecode(String hex) {
        if (hex == null || (hex.length() & 1) != 0) {
            return new byte[0];
        }
        int len = hex.length() >> 1;
        byte[] out = new byte[len];
        for (int i = 0; i < len; i++) {
            int hi = Character.digit(hex.charAt(i * 2), 16);
            int lo = Character.digit(hex.charAt(i * 2 + 1), 16);
            if (hi < 0 || lo < 0) {
                return new byte[0];
            }
            out[i] = (byte) ((hi << 4) | lo);
        }
        return out;
    }

    public static String hexEncode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xFF));
        }
        return sb.toString();
    }

    public static String rsaEncryptBase64(String plainText, String publicKeyBase64) {
        if (plainText == null || publicKeyBase64 == null) {
            return "";
        }
        try {
            byte[] keyBytes = Base64.getMimeDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Throwable ignored) {
            return "";
        }
    }

    public static String rsaDecryptBase64(String cipherBase64, String privateKeyBase64) {
        if (cipherBase64 == null || privateKeyBase64 == null) {
            return "";
        }
        try {
            byte[] keyBytes = Base64.getMimeDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decrypted = cipher.doFinal(Base64.getMimeDecoder().decode(cipherBase64));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Throwable ignored) {
            return "";
        }
    }

    public static byte[] gzipCompress(byte[] input) {
        if (input == null || input.length == 0) {
            return new byte[0];
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(input);
            gzip.finish();
            gzip.close();
            return out.toByteArray();
        } catch (Throwable ignored) {
            return input;
        }
    }

    public static byte[] gzipDecompress(byte[] compressed) {
        if (compressed == null || compressed.length == 0) {
            return new byte[0];
        }
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(compressed);
            GZIPInputStream gzip = new GZIPInputStream(in);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = gzip.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            gzip.close();
            return out.toByteArray();
        } catch (Throwable ignored) {
            return compressed;
        }
    }

    public static String md5Hex(String input) {
        if (input == null) {
            return "";
        }
        return digestHex("MD5", input.getBytes(StandardCharsets.UTF_8));
    }

    public static String sha1Hex(String input) {
        if (input == null) {
            return "";
        }
        return digestHex("SHA-1", input.getBytes(StandardCharsets.UTF_8));
    }

    public static String sha1Hex(byte[] input) {
        return digestHex("SHA-1", input);
    }

    private static String digestHex(String algorithm, byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] digest = md.digest(bytes);
            return hexEncode(digest);
        } catch (Throwable ignored) {
            return "";
        }
    }

    public static String hmacSha256Hex(String content, String key) {
        if (content == null || key == null) {
            return "";
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secret);
            byte[] digest = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return hexEncode(digest);
        } catch (Throwable ignored) {
            return "";
        }
    }
}
