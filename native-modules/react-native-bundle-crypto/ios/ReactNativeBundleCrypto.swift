import NitroModules
import Foundation

// ============================================================================
// react-native-bundle-crypto (iOS) — Nitro HybridObject thin shell
//
// P3a refactor: the real GPG/sha256/path-safety/secure-file logic now lives in
// BundleCryptoCore (plain, synchronous, throwing static functions). This class
// is a thin shell: each Nitro method calls the matching BundleCryptoCore.xxx and
// assembles the generated Nitro Result struct (VerifyGpgCleartextResult etc.).
// secureEqualHex is synchronous and returns the core result directly.
//
// The public Nitro interface, Result field names, and fail-safe behavior are
// UNCHANGED from the previous in-method implementation. There is a single copy
// of the logic (in BundleCryptoCore) — this shell never duplicates it.
//
// The GPG public key, `import Gopenpgp`, and CommonCrypto usage all live in
// BundleCryptoCore (single source of truth). This module does NOT expose the
// public key to JS.
// ============================================================================

class ReactNativeBundleCrypto: HybridReactNativeBundleCryptoSpec {

  // MARK: - GPG cleartext (bundle form): body JSON -> sha256

  func verifyGpgCleartext(signedMessage: String) throws -> Promise<VerifyGpgCleartextResult> {
    return Promise.async {
      let r = BundleCryptoCore.verifyGpgCleartext(signedMessage)
      return VerifyGpgCleartextResult(valid: r.valid, sha256: r.sha256, reason: r.reason)
    }
  }

  // MARK: - Detached ASC (APK form): cleartext-signed SHA256SUMS -> first hex token

  func verifyDetachedAsc(ascContent: String) throws -> Promise<VerifyDetachedAscResult> {
    return Promise.async {
      let r = BundleCryptoCore.verifyDetachedAsc(ascContent)
      return VerifyDetachedAscResult(valid: r.valid, sha256: r.sha256, reason: r.reason)
    }
  }

  // MARK: - sha256 (CommonCrypto streaming)

  func sha256OfFile(filePath: String) throws -> Promise<Sha256Result> {
    return Promise.async {
      let r = BundleCryptoCore.sha256OfFile(filePath)
      return Sha256Result(sha256: r.sha256, failureReason: r.failureReason)
    }
  }

  // MARK: - Constant-time hex compare (synchronous)

  func secureEqualHex(a: String, b: String) throws -> Bool {
    return BundleCryptoCore.secureEqualHex(a, b)
  }

  // MARK: - Per-entry directory hash verification

  func verifyDirAgainstHashes(dirPath: String, entries: [DirHashEntry]) throws -> Promise<Bool> {
    return Promise.async {
      let coreEntries = entries.map {
        BundleCryptoCore.DirHash(relativePath: $0.relativePath, sha256: $0.sha256)
      }
      return BundleCryptoCore.verifyDirAgainstHashes(dirPath: dirPath, entries: coreEntries)
    }
  }

  // MARK: - Hash a whole dir into relativePath -> sha256 entries

  func hashDir(dirPath: String) throws -> Promise<[DirHashEntry]> {
    return Promise.async {
      let hashes = try BundleCryptoCore.hashDir(dirPath)
      return hashes.map { DirHashEntry(relativePath: $0.relativePath, sha256: $0.sha256) }
    }
  }

  // MARK: - Symlink / path-traversal guard

  func validateExtractedPathSafety(destination: String) throws -> Promise<Bool> {
    return Promise.async {
      return BundleCryptoCore.validateExtractedPathSafety(destination)
    }
  }

  // MARK: - Safe file verbs — atomic write / safe rename / version-dir listing.

  func atomicWriteFile(filePath: String, contentUtf8: String) throws -> Promise<Void> {
    return Promise.async {
      try BundleCryptoCore.atomicWriteFile(filePath: filePath, contentUtf8: contentUtf8)
    }
  }

  func safeRename(fromPath: String, toPath: String) throws -> Promise<Void> {
    return Promise.async {
      try BundleCryptoCore.safeRename(fromPath: fromPath, toPath: toPath)
    }
  }

  func listVersionDirs(parentDir: String) throws -> Promise<[String]> {
    return Promise.async {
      return BundleCryptoCore.listVersionDirs(parentDir)
    }
  }
}
