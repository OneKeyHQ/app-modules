package com.margelo.nitro.reactnativebundlecrypto

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise
import com.margelo.nitro.nativelogger.OneKeyLog
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.security.MessageDigest
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection
import org.bouncycastle.openpgp.PGPSignatureList
import org.bouncycastle.openpgp.PGPUtil
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider
import org.json.JSONObject

// ============================================================================
// react-native-bundle-crypto (Android)
//
// P1 migration: faithfully ported the BouncyCastle GPG cleartext verify,
// detached ASC verify, java MessageDigest sha256 (with failureReason taxonomy),
// per-entry dir hashing, constant-time compare, symlink/traversal guard, and
// safe file ops from ReactNativeBundleUpdate.kt / ReactNativeAppUpdate.kt into
// this dedicated security module. Verification failures always return
// invalid + reason — never a false pass (unchanged from source).
//
// Single source of truth for the OneKey GPG public key lives here (below).
// This module does NOT expose the public key to JS.
//
// Cleartext form (verifyGpgCleartext) <- ReactNativeBundleUpdate.kt
//                                        verifyGPGAndExtractSha256
//                                        (body JSON -> sha256)
// Detached form  (verifyDetachedAsc)  <- ReactNativeAppUpdate.kt
//                                        verifyAscContentAndExtractSha256
//                                        (cleartext-signed SHA256SUMS, first
//                                        hex token)
// Both forms are PGP CLEARTEXT SIGNED MESSAGEs; they share the same manual
// BouncyCastle cleartext parsing and differ only in body extraction.
// ============================================================================

// OneKey GPG public key for signature verification.
// SINGLE AUTHORITATIVE COPY for the bundle-crypto module.
private const val GPG_PUBLIC_KEY = """-----BEGIN PGP PUBLIC KEY BLOCK-----

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
=HlTQ
-----END PGP PUBLIC KEY BLOCK-----"""

@DoNotStrip
class ReactNativeBundleCrypto : HybridReactNativeBundleCryptoSpec() {

  // Single BouncyCastle provider instance for this module (mirrors source).
  private val bcProvider = BouncyCastleProvider()

  // MARK: - GPG cleartext (bundle form): body JSON -> sha256
  // Ported verbatim from ReactNativeBundleUpdate.verifyGPGAndExtractSha256.

  override fun verifyGpgCleartext(signedMessage: String): Promise<VerifyGpgCleartextResult> {
    return Promise.async {
      if (!signedMessage.contains("-----BEGIN PGP SIGNED MESSAGE-----")) {
        return@async VerifyGpgCleartextResult(false, null, "NOT_PGP_SIGNED_MESSAGE")
      }

      try {
        // Parse the public key
        val pubKeyStream = PGPUtil.getDecoderStream(GPG_PUBLIC_KEY.byteInputStream())
        val pgpPubKeyRingCollection = PGPPublicKeyRingCollection(pubKeyStream, JcaKeyFingerprintCalculator())

        // Extract cleartext and signature from the PGP signed message manually
        // (BouncyCastle's cleartext handling requires manual parsing).
        val lines = signedMessage.lines()
        val hashHeaderIdx = lines.indexOfFirst { it.startsWith("Hash:") }
        val sigStartIdx = lines.indexOfFirst { it == "-----BEGIN PGP SIGNATURE-----" }
        val sigEndIdx = lines.indexOfFirst { it == "-----END PGP SIGNATURE-----" }

        if (hashHeaderIdx < 0 || sigStartIdx < 0 || sigEndIdx < 0) {
          OneKeyLog.error("BundleCrypto", "Invalid PGP cleartext signed message format")
          return@async VerifyGpgCleartextResult(false, null, "INVALID_FORMAT")
        }

        // The cleartext body is between the Hash header blank line and the PGP SIGNATURE block.
        val bodyStartIdx = hashHeaderIdx + 2 // skip Hash: line and the blank line after it
        val bodyLines = lines.subList(bodyStartIdx, sigStartIdx)
        // Remove trailing empty line that PGP adds.
        val cleartextBody = bodyLines.joinToString("\r\n").trimEnd()

        // The signature block.
        val sigBlock = lines.subList(sigStartIdx, sigEndIdx + 1).joinToString("\n")

        // Decode the signature.
        val sigInputStream = PGPUtil.getDecoderStream(sigBlock.byteInputStream())
        val sigFactory = JcaPGPObjectFactory(sigInputStream)
        val signatureList = sigFactory.nextObject()

        if (signatureList !is PGPSignatureList || signatureList.isEmpty) {
          OneKeyLog.error("BundleCrypto", "No PGP signature found in message")
          return@async VerifyGpgCleartextResult(false, null, "NO_SIGNATURE")
        }

        val pgpSignature = signatureList[0]
        val keyId = pgpSignature.keyID

        // Find the public key.
        val publicKey = pgpPubKeyRingCollection.getPublicKey(keyId)
        if (publicKey == null) {
          OneKeyLog.error("BundleCrypto", "Public key not found for keyId: ${java.lang.Long.toHexString(keyId)}")
          return@async VerifyGpgCleartextResult(false, null, "PUBKEY_NOT_FOUND")
        }

        // Verify the signature.
        pgpSignature.init(JcaPGPContentVerifierBuilderProvider().setProvider(bcProvider), publicKey)

        // Dash-unescape the cleartext per RFC 4880 Section 7.1.
        val unescapedLines = cleartextBody.lines().map { line ->
          if (line.startsWith("- ")) line.substring(2) else line
        }
        val dataToVerify = unescapedLines.joinToString("\r\n").toByteArray(Charsets.UTF_8)
        pgpSignature.update(dataToVerify)

        if (!pgpSignature.verify()) {
          OneKeyLog.error("BundleCrypto", "GPG signature verification failed")
          return@async VerifyGpgCleartextResult(false, null, "SIGNATURE_INVALID")
        }

        // Signature verified, parse the cleartext JSON.
        val json = JSONObject(cleartextBody.trim())
        val sha256 = json.optString("sha256", "")
        if (sha256.isEmpty()) {
          OneKeyLog.error("BundleCrypto", "No sha256 field in signed cleartext JSON")
          return@async VerifyGpgCleartextResult(false, null, "NO_SHA256_FIELD")
        }

        OneKeyLog.info("BundleCrypto", "GPG cleartext verification succeeded")
        VerifyGpgCleartextResult(true, sha256, null)
      } catch (e: Exception) {
        OneKeyLog.error("BundleCrypto", "GPG verification error: ${e.javaClass.simpleName}: ${e.message}")
        VerifyGpgCleartextResult(false, null, "ERROR_${e.javaClass.simpleName}")
      }
    }
  }

  // MARK: - Detached ASC (APK form): cleartext-signed SHA256SUMS -> first hex token
  // Ported verbatim from ReactNativeAppUpdate.verifyAscContentAndExtractSha256.
  // The on-disk SHA256SUMS.asc is itself a PGP CLEARTEXT SIGNED MESSAGE; the
  // only difference from verifyGpgCleartext is the body parsing (no JSON — first
  // whitespace token must be a 64-char lowercase hex sha256).

  override fun verifyDetachedAsc(ascContent: String): Promise<VerifyDetachedAscResult> {
    return Promise.async {
      if (!ascContent.contains("-----BEGIN PGP SIGNED MESSAGE-----")) {
        return@async VerifyDetachedAscResult(false, null, "NOT_PGP_SIGNED_MESSAGE")
      }

      try {
        val lines = ascContent.lines()
        val hashHeaderIdx = lines.indexOfFirst { it.startsWith("Hash:") }
        val sigStartIdx = lines.indexOfFirst { it == "-----BEGIN PGP SIGNATURE-----" }
        val sigEndIdx = lines.indexOfFirst { it == "-----END PGP SIGNATURE-----" }
        if (hashHeaderIdx < 0 || sigStartIdx < 0 || sigEndIdx < 0) {
          return@async VerifyDetachedAscResult(false, null, "INVALID_FORMAT")
        }

        val bodyStartIdx = hashHeaderIdx + 2
        val bodyLines = lines.subList(bodyStartIdx, sigStartIdx)
        val cleartextBody = bodyLines.joinToString("\r\n").trimEnd()
        val sigBlock = lines.subList(sigStartIdx, sigEndIdx + 1).joinToString("\n")

        // Verify GPG signature.
        val sigInputStream = PGPUtil.getDecoderStream(sigBlock.byteInputStream())
        val sigFactory = JcaPGPObjectFactory(sigInputStream)
        val signatureList = sigFactory.nextObject()
        if (signatureList !is PGPSignatureList || signatureList.isEmpty) {
          return@async VerifyDetachedAscResult(false, null, "NO_SIGNATURE")
        }

        val pgpSignature = signatureList[0]
        val pubKeyStream = PGPUtil.getDecoderStream(GPG_PUBLIC_KEY.byteInputStream())
        val pgpPubKeyRingCollection = PGPPublicKeyRingCollection(pubKeyStream, JcaKeyFingerprintCalculator())
        val publicKey = pgpPubKeyRingCollection.getPublicKey(pgpSignature.keyID)
          ?: return@async VerifyDetachedAscResult(false, null, "PUBKEY_NOT_FOUND")

        pgpSignature.init(JcaPGPContentVerifierBuilderProvider().setProvider(bcProvider), publicKey)
        val unescapedLines = cleartextBody.lines().map { line ->
          if (line.startsWith("- ")) line.substring(2) else line
        }
        val dataToVerify = unescapedLines.joinToString("\r\n").toByteArray(Charsets.UTF_8)
        pgpSignature.update(dataToVerify)
        if (!pgpSignature.verify()) {
          return@async VerifyDetachedAscResult(false, null, "SIGNATURE_INVALID")
        }

        // Extract SHA256 from verified cleartext.
        val sha256 = cleartextBody.trim().split("\\s+".toRegex())[0].lowercase()
        if (sha256.length != 64 || !sha256.all { it in '0'..'9' || it in 'a'..'f' }) {
          return@async VerifyDetachedAscResult(false, null, "SHA256_TOKEN_INVALID")
        }

        OneKeyLog.info("BundleCrypto", "ASC detached verification succeeded")
        VerifyDetachedAscResult(true, sha256, null)
      } catch (e: Exception) {
        OneKeyLog.error("BundleCrypto", "ASC verification error: ${e.javaClass.simpleName}: ${e.message}")
        VerifyDetachedAscResult(false, null, "ERROR_${e.javaClass.simpleName}")
      }
    }
  }

  // MARK: - sha256 (java MessageDigest streaming) — ported from calculateSHA256.
  // failureReason taxonomy preserved verbatim (FILE_NOT_FOUND / FILE_DISAPPEARED
  // / FILE_TRUNCATED / PERMISSION_DENIED / OOM / IO_<class> / UNEXPECTED_<class>).

  override fun sha256OfFile(filePath: String): Promise<Sha256Result> {
    return Promise.async {
      val computed = calculateSHA256(filePath)
      if (computed.sha256 != null) {
        Sha256Result(computed.sha256, null)
      } else {
        Sha256Result(null, computed.failureReason)
      }
    }
  }

  private data class Sha256Internal(val sha256: String?, val failureReason: String?)

  private fun calculateSHA256(filePath: String): Sha256Internal {
    val file = File(filePath)
    if (!file.exists()) {
      OneKeyLog.error("BundleCrypto", "calculateSHA256: file not found: $filePath")
      return Sha256Internal(null, "FILE_NOT_FOUND")
    }
    return try {
      val digest = MessageDigest.getInstance("SHA-256")
      BufferedInputStream(FileInputStream(filePath)).use { bis ->
        val buffer = ByteArray(8192)
        var count: Int
        while (bis.read(buffer).also { count = it } > 0) {
          digest.update(buffer, 0, count)
        }
      }
      Sha256Internal(bytesToHex(digest.digest()), null)
    } catch (e: java.io.FileNotFoundException) {
      OneKeyLog.error("BundleCrypto", "calculateSHA256: file disappeared during read: ${e.message}")
      Sha256Internal(null, "FILE_DISAPPEARED")
    } catch (e: java.io.EOFException) {
      OneKeyLog.error("BundleCrypto", "calculateSHA256: truncated file: ${e.message}")
      Sha256Internal(null, "FILE_TRUNCATED")
    } catch (e: SecurityException) {
      OneKeyLog.error("BundleCrypto", "calculateSHA256: permission denied: ${e.message}")
      Sha256Internal(null, "PERMISSION_DENIED")
    } catch (e: OutOfMemoryError) {
      OneKeyLog.error("BundleCrypto", "calculateSHA256: OutOfMemoryError on ${file.length()} bytes")
      Sha256Internal(null, "OOM")
    } catch (e: java.io.IOException) {
      OneKeyLog.error("BundleCrypto", "calculateSHA256: ${e.javaClass.simpleName}: ${e.message}")
      Sha256Internal(null, "IO_${e.javaClass.simpleName}")
    } catch (e: Exception) {
      OneKeyLog.error("BundleCrypto", "calculateSHA256: ${e.javaClass.simpleName}: ${e.message}")
      Sha256Internal(null, "UNEXPECTED_${e.javaClass.simpleName}")
    }
  }

  private fun bytesToHex(bytes: ByteArray): String {
    return bytes.joinToString("") { "%02x".format(it) }
  }

  // MARK: - Constant-time hex compare — ported from secureCompare.

  override fun secureEqualHex(a: String, b: String): Boolean {
    return secureCompare(a, b)
  }

  private fun secureCompare(a: String, b: String): Boolean {
    val aBytes = a.toByteArray(Charsets.UTF_8)
    val bBytes = b.toByteArray(Charsets.UTF_8)
    if (aBytes.size != bBytes.size) return false
    var result = 0
    for (i in aBytes.indices) {
      result = result or (aBytes[i].toInt() xor bBytes[i].toInt())
    }
    return result == 0
  }

  // MARK: - Per-entry directory hash verification — ported from
  // validateAllFilesInDir / validateFilesRecursive. Caller passes the expected
  // relativePath -> sha256 entries (JS derives these from verified metadata).
  // Verifies every on-disk file matches AND every expected entry exists.

  override fun verifyDirAgainstHashes(
    dirPath: String,
    entries: Array<DirHashEntry>
  ): Promise<Boolean> {
    return Promise.async {
      val dir = File(dirPath)
      if (!dir.exists() || !dir.isDirectory) {
        OneKeyLog.error("BundleCrypto", "[bundle-verify] dir not found: $dirPath")
        return@async false
      }
      val jsBundleDir = dir.absolutePath + "/"
      val expected = HashMap<String, String>()
      for (entry in entries) expected[entry.relativePath] = entry.sha256

      if (!validateFilesRecursive(dir, expected, jsBundleDir)) return@async false

      // Verify completeness.
      for ((relativePath, _) in expected) {
        val expectedFile = File(jsBundleDir + relativePath)
        if (!expectedFile.exists()) {
          OneKeyLog.error("BundleCrypto", "[bundle-verify] File listed in metadata but missing on disk: $relativePath")
          return@async false
        }
      }
      true
    }
  }

  private fun validateFilesRecursive(dir: File, expected: Map<String, String>, jsBundleDir: String): Boolean {
    val files = dir.listFiles() ?: return true
    for (file in files) {
      if (file.isDirectory) {
        if (!validateFilesRecursive(file, expected, jsBundleDir)) return false
      } else {
        if (file.name.contains("metadata.json") || file.name.contains(".DS_Store")) continue
        val relativePath = file.absolutePath.replace(jsBundleDir, "")
        val expectedSHA256 = expected[relativePath]
        if (expectedSHA256 == null) {
          OneKeyLog.error("BundleCrypto", "[bundle-verify] File on disk not found in metadata: $relativePath")
          return false
        }
        val actualSHA256 = calculateSHA256(file.absolutePath).sha256
        if (actualSHA256 == null) {
          OneKeyLog.error("BundleCrypto", "[bundle-verify] Failed to calculate SHA256 for file: $relativePath")
          return false
        }
        if (!secureCompare(expectedSHA256, actualSHA256)) {
          OneKeyLog.error("BundleCrypto", "[bundle-verify] SHA256 mismatch for $relativePath")
          return false
        }
      }
    }
    return true
  }

  // MARK: - Hash a whole dir into relativePath -> sha256 entries (new helper,
  // same recursion/skip rules as verifyDirAgainstHashes).

  override fun hashDir(dirPath: String): Promise<Array<DirHashEntry>> {
    return Promise.async {
      val dir = File(dirPath)
      if (!dir.exists() || !dir.isDirectory) {
        return@async emptyArray<DirHashEntry>()
      }
      val jsBundleDir = dir.absolutePath + "/"
      val results = ArrayList<DirHashEntry>()
      hashFilesRecursive(dir, jsBundleDir, results)
      results.toTypedArray()
    }
  }

  private fun hashFilesRecursive(dir: File, jsBundleDir: String, out: ArrayList<DirHashEntry>) {
    val files = dir.listFiles() ?: return
    for (file in files) {
      if (file.isDirectory) {
        hashFilesRecursive(file, jsBundleDir, out)
      } else {
        if (file.name.contains("metadata.json") || file.name.contains(".DS_Store")) continue
        val relativePath = file.absolutePath.replace(jsBundleDir, "")
        val sha256 = calculateSHA256(file.absolutePath).sha256
          ?: throw Exception("HASH_FAILED:$relativePath")
        out.add(DirHashEntry(relativePath, sha256))
      }
    }
  }

  // MARK: - Symlink / path-traversal guard — ported from the unzip-time guard
  // in ReactNativeBundleUpdate.unzipFile (canonicalPath prefix check + symlink
  // detection via Files.isSymbolicLink), consolidated into a standalone verb.

  override fun validateExtractedPathSafety(destination: String): Promise<Boolean> {
    return Promise.async {
      val destDir = File(destination)
      if (!destDir.exists()) {
        // Nothing extracted yet — nothing unsafe to find. Mirrors iOS (empty
        // enumerator => true).
        return@async true
      }
      val resolvedDestination = destDir.canonicalPath
      validatePathSafetyRecursive(destDir, resolvedDestination)
    }
  }

  private fun validatePathSafetyRecursive(dir: File, resolvedDestination: String): Boolean {
    val files = dir.listFiles() ?: return true
    for (file in files) {
      if (Files.isSymbolicLink(file.toPath())) {
        OneKeyLog.error("BundleCrypto", "Symlink detected in extracted bundle: ${file.name}")
        return false
      }
      val resolvedPath = file.canonicalPath
      if (resolvedPath != resolvedDestination && !resolvedPath.startsWith("$resolvedDestination/")) {
        OneKeyLog.error("BundleCrypto", "Path traversal detected in extracted bundle: ${file.name}")
        return false
      }
      if (file.isDirectory) {
        if (!validatePathSafetyRecursive(file, resolvedDestination)) return false
      }
    }
    return true
  }

  // MARK: - Safe file verbs — atomic write / safe rename / version-dir listing.
  // Consolidates the write/move/enumerate patterns from ReactNativeBundleUpdate
  // (atomic temp-file write, remove-then-move, directory enumeration) into
  // explicit APIs.

  override fun atomicWriteFile(filePath: String, contentUtf8: String): Promise<Unit> {
    return Promise.async {
      val target = File(filePath)
      target.parentFile?.mkdirs()
      // Write to a temp file in the same directory, then atomically rename.
      val tmp = File.createTempFile("bundle-crypto-", ".tmp", target.parentFile)
      try {
        tmp.writeText(contentUtf8, Charsets.UTF_8)
        // Files.move with ATOMIC_MOVE / REPLACE_EXISTING gives the same-volume
        // atomic replace semantics as the source's atomic write.
        Files.move(
          tmp.toPath(),
          target.toPath(),
          java.nio.file.StandardCopyOption.REPLACE_EXISTING,
          java.nio.file.StandardCopyOption.ATOMIC_MOVE
        )
      } catch (e: java.nio.file.AtomicMoveNotSupportedException) {
        // Fallback for filesystems without atomic move support.
        Files.move(tmp.toPath(), target.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING)
      } finally {
        if (tmp.exists()) tmp.delete()
      }
    }
  }

  override fun safeRename(fromPath: String, toPath: String): Promise<Unit> {
    return Promise.async {
      val from = File(fromPath)
      val to = File(toPath)
      to.parentFile?.mkdirs()
      // Same pattern as downloadBundle: remove existing target, then move.
      if (to.exists()) {
        if (!to.deleteRecursively()) {
          throw Exception("safeRename: failed to remove existing target: $toPath")
        }
      }
      try {
        Files.move(
          from.toPath(),
          to.toPath(),
          java.nio.file.StandardCopyOption.ATOMIC_MOVE
        )
      } catch (e: java.nio.file.AtomicMoveNotSupportedException) {
        Files.move(from.toPath(), to.toPath())
      }
    }
  }

  override fun listVersionDirs(parentDir: String): Promise<Array<String>> {
    return Promise.async {
      val dir = File(parentDir)
      val children = dir.listFiles() ?: return@async emptyArray<String>()
      children.filter { it.isDirectory }.map { it.name }.toTypedArray()
    }
  }
}
