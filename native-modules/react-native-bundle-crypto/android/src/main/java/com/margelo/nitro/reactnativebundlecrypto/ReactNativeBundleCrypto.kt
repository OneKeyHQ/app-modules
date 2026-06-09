package com.margelo.nitro.reactnativebundlecrypto

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise

// ============================================================================
// react-native-bundle-crypto (Android) — Nitro HybridObject thin shell
//
// P3a refactor: the real BouncyCastle GPG/ASC verify, MessageDigest sha256,
// per-entry dir hashing, constant-time compare, symlink/traversal guard, and
// safe file ops now live in BundleCryptoCore (plain functions, plain Kotlin
// types). This class is a thin shell: each Nitro method calls the matching
// BundleCryptoCore.xxx and assembles the generated Nitro Result struct
// (VerifyGpgCleartextResult etc.). secureEqualHex is synchronous and returns
// the core result directly.
//
// The public Nitro interface, Result field names, and fail-safe behavior are
// UNCHANGED from the previous in-method implementation. There is a single copy
// of the logic (in BundleCryptoCore) — this shell never duplicates it.
//
// The GPG public key + BouncyCastle provider live in BundleCryptoCore (single
// source of truth). This module does NOT expose the public key to JS.
// ============================================================================

@DoNotStrip
class ReactNativeBundleCrypto : HybridReactNativeBundleCryptoSpec() {

  // MARK: - GPG cleartext (bundle form): body JSON -> sha256

  override fun verifyGpgCleartext(signedMessage: String): Promise<VerifyGpgCleartextResult> {
    return Promise.async {
      val r = BundleCryptoCore.verifyGpgCleartext(signedMessage)
      VerifyGpgCleartextResult(r.valid, r.sha256, r.reason)
    }
  }

  // MARK: - Detached ASC (APK form): cleartext-signed SHA256SUMS -> first hex token

  override fun verifyDetachedAsc(ascContent: String): Promise<VerifyDetachedAscResult> {
    return Promise.async {
      val r = BundleCryptoCore.verifyDetachedAsc(ascContent)
      VerifyDetachedAscResult(r.valid, r.sha256, r.reason)
    }
  }

  // MARK: - sha256 (java MessageDigest streaming)

  override fun sha256OfFile(filePath: String): Promise<Sha256Result> {
    return Promise.async {
      val r = BundleCryptoCore.sha256OfFile(filePath)
      Sha256Result(r.sha256, r.failureReason)
    }
  }

  // MARK: - Constant-time hex compare (synchronous)

  override fun secureEqualHex(a: String, b: String): Boolean {
    return BundleCryptoCore.secureEqualHex(a, b)
  }

  // MARK: - Per-entry directory hash verification

  override fun verifyDirAgainstHashes(
    dirPath: String,
    entries: Array<DirHashEntry>
  ): Promise<Boolean> {
    return Promise.async {
      val coreEntries = entries.map { BundleCryptoCore.DirHash(it.relativePath, it.sha256) }
      BundleCryptoCore.verifyDirAgainstHashes(dirPath, coreEntries)
    }
  }

  // MARK: - Hash a whole dir into relativePath -> sha256 entries

  override fun hashDir(dirPath: String): Promise<Array<DirHashEntry>> {
    return Promise.async {
      BundleCryptoCore.hashDir(dirPath)
        .map { DirHashEntry(it.relativePath, it.sha256) }
        .toTypedArray()
    }
  }

  // MARK: - Symlink / path-traversal guard

  override fun validateExtractedPathSafety(destination: String): Promise<Boolean> {
    return Promise.async {
      BundleCryptoCore.validateExtractedPathSafety(destination)
    }
  }

  // MARK: - Safe file verbs — atomic write / safe rename / version-dir listing.

  override fun atomicWriteFile(filePath: String, contentUtf8: String): Promise<Unit> {
    return Promise.async {
      BundleCryptoCore.atomicWriteFile(filePath, contentUtf8)
    }
  }

  override fun safeRename(fromPath: String, toPath: String): Promise<Unit> {
    return Promise.async {
      BundleCryptoCore.safeRename(fromPath, toPath)
    }
  }

  override fun listVersionDirs(parentDir: String): Promise<Array<String>> {
    return Promise.async {
      BundleCryptoCore.listVersionDirs(parentDir).toTypedArray()
    }
  }
}
