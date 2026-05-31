import NitroModules
import ReactNativeNativeLogger
import Foundation
import CommonCrypto
import Gopenpgp

// ============================================================================
// react-native-bundle-crypto (iOS)
//
// P1 migration: faithfully ported the GPG/sha256/path-safety/secure-file logic
// from ReactNativeBundleUpdate.swift (cleartext GPG, CommonCrypto sha256,
// per-entry dir hashing, constant-time compare, symlink/traversal guard,
// atomic write / safe rename / version-dir enumeration) into this dedicated
// security module. Verification failures always return invalid + reason —
// never a false pass (unchanged from source).
//
// Single source of truth for the OneKey GPG public key lives here (below).
// This module does NOT expose the public key to JS.
//
// Cleartext form  (verifyGpgCleartext)  <- ReactNativeBundleUpdate.swift
//                                          verifyGPGAndExtractSha256 (CryptoPGP
//                                          verifyCleartext, body JSON -> sha256)
// Detached form   (verifyDetachedAsc)   <- NEW iOS impl, aligned to Android
//                                          ReactNativeAppUpdate.kt
//                                          verifyAscContentAndExtractSha256
//                                          (cleartext-signed SHA256SUMS, first
//                                          hex token). The on-disk SHA256SUMS.asc
//                                          is a PGP CLEARTEXT SIGNED MESSAGE, so
//                                          Gopenpgp verifyCleartext is reused;
//                                          only the body parsing differs.
// ============================================================================

// OneKey GPG public key for signature verification.
// SINGLE AUTHORITATIVE COPY for the bundle-crypto module.
private let GPG_PUBLIC_KEY = """
-----BEGIN PGP PUBLIC KEY BLOCK-----

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
=HlTQ
-----END PGP PUBLIC KEY BLOCK-----
"""

class ReactNativeBundleCrypto: HybridReactNativeBundleCryptoSpec {

  // MARK: - GPG cleartext (bundle form): body JSON -> sha256
  // Ported verbatim from ReactNativeBundleUpdate.verifyGPGAndExtractSha256.

  func verifyGpgCleartext(signedMessage: String) throws -> Promise<VerifyGpgCleartextResult> {
    return Promise.async {
      // Check if this looks like a PGP signed message
      guard signedMessage.contains("-----BEGIN PGP SIGNED MESSAGE-----") else {
        return VerifyGpgCleartextResult(valid: false, sha256: nil, reason: "NOT_PGP_SIGNED_MESSAGE")
      }

      // 1. Load public key
      guard let pubKey = CryptoKey(fromArmored: GPG_PUBLIC_KEY) else {
        OneKeyLog.error("BundleCrypto", "Failed to parse GPG public key")
        return VerifyGpgCleartextResult(valid: false, sha256: nil, reason: "PUBKEY_PARSE_FAILED")
      }

      // 2. Get PGP handle
      guard let pgp = CryptoPGP() else {
        OneKeyLog.error("BundleCrypto", "Failed to create PGPHandle")
        return VerifyGpgCleartextResult(valid: false, sha256: nil, reason: "PGP_HANDLE_FAILED")
      }

      // 3. Build verify handle: pgp.verify().verificationKey(pubKey).new()
      guard let verifyBuilder = pgp.verify() else {
        OneKeyLog.error("BundleCrypto", "Failed to get verify builder")
        return VerifyGpgCleartextResult(valid: false, sha256: nil, reason: "VERIFY_BUILDER_FAILED")
      }
      guard let builderWithKey = verifyBuilder.verificationKey(pubKey) else {
        OneKeyLog.error("BundleCrypto", "Failed to set verification key")
        return VerifyGpgCleartextResult(valid: false, sha256: nil, reason: "VERIFY_KEY_FAILED")
      }

      let verifyHandle: any CryptoPGPVerifyProtocol
      do {
        verifyHandle = try builderWithKey.new()
      } catch {
        OneKeyLog.error("BundleCrypto", "Failed to create verify handle: \(error.localizedDescription)")
        return VerifyGpgCleartextResult(valid: false, sha256: nil, reason: "VERIFY_HANDLE_FAILED")
      }

      // 4. Verify cleartext
      guard let signatureData = signedMessage.data(using: .utf8) else {
        return VerifyGpgCleartextResult(valid: false, sha256: nil, reason: "ENCODING_FAILED")
      }

      let cleartextResult: CryptoVerifyCleartextResult
      do {
        cleartextResult = try verifyHandle.verifyCleartext(signatureData)
      } catch {
        OneKeyLog.error("BundleCrypto", "GPG verification error: \(error.localizedDescription)")
        return VerifyGpgCleartextResult(valid: false, sha256: nil, reason: "VERIFY_ERROR")
      }

      // 5. Check signature error
      do {
        try cleartextResult.signatureError()
      } catch {
        OneKeyLog.error("BundleCrypto", "GPG signature invalid: \(error.localizedDescription)")
        return VerifyGpgCleartextResult(valid: false, sha256: nil, reason: "SIGNATURE_INVALID")
      }

      // 6. Get cleartext
      guard let cleartextData = cleartextResult.cleartext() else {
        OneKeyLog.error("BundleCrypto", "Failed to extract cleartext from GPG result")
        return VerifyGpgCleartextResult(valid: false, sha256: nil, reason: "CLEARTEXT_EXTRACT_FAILED")
      }

      guard let text = String(data: cleartextData, encoding: .utf8) else {
        return VerifyGpgCleartextResult(valid: false, sha256: nil, reason: "CLEARTEXT_DECODE_FAILED")
      }

      // 7. Parse JSON and extract sha256
      guard let jsonData = text.data(using: .utf8),
            let json = try? JSONSerialization.jsonObject(with: jsonData) as? [String: Any],
            let sha256 = json["sha256"] as? String else {
        OneKeyLog.error("BundleCrypto", "Failed to parse cleartext JSON")
        return VerifyGpgCleartextResult(valid: false, sha256: nil, reason: "JSON_PARSE_FAILED")
      }

      OneKeyLog.info("BundleCrypto", "GPG cleartext verification succeeded")
      return VerifyGpgCleartextResult(valid: true, sha256: sha256, reason: nil)
    }
  }

  // MARK: - Detached ASC (APK form): cleartext-signed SHA256SUMS -> first hex token
  // NEW iOS implementation. There was no detached verify on iOS before; this
  // aligns to Android ReactNativeAppUpdate.verifyAscContentAndExtractSha256.
  // The SHA256SUMS.asc on disk is a PGP CLEARTEXT SIGNED MESSAGE, so the same
  // Gopenpgp verifyCleartext path is reused; only the body parsing differs
  // (no JSON — the verified body's first whitespace token must be a 64-char
  // lowercase hex sha256).

  func verifyDetachedAsc(ascContent: String) throws -> Promise<VerifyDetachedAscResult> {
    return Promise.async {
      guard ascContent.contains("-----BEGIN PGP SIGNED MESSAGE-----") else {
        return VerifyDetachedAscResult(valid: false, sha256: nil, reason: "NOT_PGP_SIGNED_MESSAGE")
      }

      guard let pubKey = CryptoKey(fromArmored: GPG_PUBLIC_KEY) else {
        OneKeyLog.error("BundleCrypto", "Failed to parse GPG public key")
        return VerifyDetachedAscResult(valid: false, sha256: nil, reason: "PUBKEY_PARSE_FAILED")
      }
      guard let pgp = CryptoPGP() else {
        return VerifyDetachedAscResult(valid: false, sha256: nil, reason: "PGP_HANDLE_FAILED")
      }
      guard let verifyBuilder = pgp.verify() else {
        return VerifyDetachedAscResult(valid: false, sha256: nil, reason: "VERIFY_BUILDER_FAILED")
      }
      guard let builderWithKey = verifyBuilder.verificationKey(pubKey) else {
        return VerifyDetachedAscResult(valid: false, sha256: nil, reason: "VERIFY_KEY_FAILED")
      }
      let verifyHandle: any CryptoPGPVerifyProtocol
      do {
        verifyHandle = try builderWithKey.new()
      } catch {
        return VerifyDetachedAscResult(valid: false, sha256: nil, reason: "VERIFY_HANDLE_FAILED")
      }

      guard let signatureData = ascContent.data(using: .utf8) else {
        return VerifyDetachedAscResult(valid: false, sha256: nil, reason: "ENCODING_FAILED")
      }

      let cleartextResult: CryptoVerifyCleartextResult
      do {
        cleartextResult = try verifyHandle.verifyCleartext(signatureData)
      } catch {
        OneKeyLog.error("BundleCrypto", "ASC verification error: \(error.localizedDescription)")
        return VerifyDetachedAscResult(valid: false, sha256: nil, reason: "VERIFY_ERROR")
      }

      do {
        try cleartextResult.signatureError()
      } catch {
        OneKeyLog.error("BundleCrypto", "ASC signature invalid: \(error.localizedDescription)")
        return VerifyDetachedAscResult(valid: false, sha256: nil, reason: "SIGNATURE_INVALID")
      }

      guard let cleartextData = cleartextResult.cleartext(),
            let text = String(data: cleartextData, encoding: .utf8) else {
        return VerifyDetachedAscResult(valid: false, sha256: nil, reason: "CLEARTEXT_EXTRACT_FAILED")
      }

      // Mirror Android: first whitespace token of the verified SHA256SUMS body,
      // lowercased, must be exactly 64 hex chars.
      let sha256 = text
        .trimmingCharacters(in: .whitespacesAndNewlines)
        .split(whereSeparator: { $0 == " " || $0 == "\t" || $0 == "\n" || $0 == "\r" })
        .first
        .map { String($0).lowercased() } ?? ""
      let isHex = sha256.count == 64 && sha256.unicodeScalars.allSatisfy {
        ($0 >= "0" && $0 <= "9") || ($0 >= "a" && $0 <= "f")
      }
      guard isHex else {
        OneKeyLog.error("BundleCrypto", "ASC body sha256 token invalid")
        return VerifyDetachedAscResult(valid: false, sha256: nil, reason: "SHA256_TOKEN_INVALID")
      }

      OneKeyLog.info("BundleCrypto", "ASC detached verification succeeded")
      return VerifyDetachedAscResult(valid: true, sha256: sha256, reason: nil)
    }
  }

  // MARK: - sha256 (CommonCrypto streaming) — ported from calculateSHA256
  // + lastSHA256FailureReason. failureReason taxonomy preserved verbatim
  // (FILE_NOT_FOUND / FILE_DISAPPEARED / IO_<code>).

  func sha256OfFile(filePath: String) throws -> Promise<Sha256Result> {
    return Promise.async {
      let fm = FileManager.default
      if !fm.fileExists(atPath: filePath) {
        OneKeyLog.error("BundleCrypto", "sha256OfFile: file not found: \(filePath)")
        return Sha256Result(sha256: nil, failureReason: "FILE_NOT_FOUND")
      }
      guard let fileHandle = FileHandle(forReadingAtPath: filePath) else {
        OneKeyLog.error("BundleCrypto", "sha256OfFile: open failed (file disappeared between stat and open): \(filePath)")
        return Sha256Result(sha256: nil, failureReason: "FILE_DISAPPEARED")
      }
      defer { fileHandle.closeFile() }

      var context = CC_SHA256_CTX()
      CC_SHA256_Init(&context)
      var threwError: Error?
      // safeRead routes reads through FileHandle.read(upToCount:) on iOS 13.4+,
      // surfacing disk failures as throwing NSError. On pre-13.4 OS versions the
      // fallback raises NSFileHandleOperationException (uncatchable in Swift) —
      // accepted on the floor since 13.4+ is the long-standing deployment target.
      while autoreleasepool(invoking: {
        do {
          let data = try Self.safeRead(fileHandle: fileHandle, length: 8192)
          if data.count > 0 {
            data.withUnsafeBytes { CC_SHA256_Update(&context, $0.baseAddress, CC_LONG(data.count)) }
            return true
          }
          return false
        } catch {
          threwError = error
          return false
        }
      }) {}
      if let err = threwError {
        let nsErr = err as NSError
        // Keep failure tag low-cardinality (`IO_<code>`); full detail logged locally.
        OneKeyLog.error("BundleCrypto", "sha256OfFile: read failed: \(nsErr.domain) \(nsErr.code) \(nsErr.localizedDescription)")
        return Sha256Result(sha256: nil, failureReason: "IO_\(nsErr.code)")
      }
      var hash = [UInt8](repeating: 0, count: Int(CC_SHA256_DIGEST_LENGTH))
      CC_SHA256_Final(&hash, &context)
      let hex = hash.map { String(format: "%02x", $0) }.joined()
      return Sha256Result(sha256: hex, failureReason: nil)
    }
  }

  /// Throwing wrapper around FileHandle.read(upToCount:) so disk I/O failures
  /// become catchable Swift errors rather than NSFileHandleOperationException.
  private static func safeRead(fileHandle: FileHandle, length: Int) throws -> Data {
    if #available(iOS 13.4, macOS 10.15.4, *) {
      return try fileHandle.read(upToCount: length) ?? Data()
    } else {
      return fileHandle.readData(ofLength: length)
    }
  }

  // MARK: - Constant-time hex compare — ported from String.secureCompare.

  func secureEqualHex(a: String, b: String) throws -> Bool {
    return Self.secureCompare(a, b)
  }

  private static func secureCompare(_ a: String, _ b: String) -> Bool {
    let lhs = Array(a.utf8)
    let rhs = Array(b.utf8)
    guard lhs.count == rhs.count else { return false }
    var result: UInt8 = 0
    for i in 0..<lhs.count {
      result |= lhs[i] ^ rhs[i]
    }
    return result == 0
  }

  private static func calculateSHA256Sync(_ filePath: String) -> String? {
    let fm = FileManager.default
    guard fm.fileExists(atPath: filePath),
          let fileHandle = FileHandle(forReadingAtPath: filePath) else { return nil }
    defer { fileHandle.closeFile() }
    var context = CC_SHA256_CTX()
    CC_SHA256_Init(&context)
    var threwError: Error?
    while autoreleasepool(invoking: {
      do {
        let data = try Self.safeRead(fileHandle: fileHandle, length: 8192)
        if data.count > 0 {
          data.withUnsafeBytes { CC_SHA256_Update(&context, $0.baseAddress, CC_LONG(data.count)) }
          return true
        }
        return false
      } catch {
        threwError = error
        return false
      }
    }) {}
    if threwError != nil { return nil }
    var hash = [UInt8](repeating: 0, count: Int(CC_SHA256_DIGEST_LENGTH))
    CC_SHA256_Final(&hash, &context)
    return hash.map { String(format: "%02x", $0) }.joined()
  }

  // MARK: - Per-entry directory hash verification — ported from
  // validateAllFilesInDir. Caller passes the expected relativePath -> sha256
  // entries (the JS layer derives these from verified metadata). Verifies every
  // on-disk file matches AND every expected entry exists (completeness).

  func verifyDirAgainstHashes(dirPath: String, entries: [DirHashEntry]) throws -> Promise<Bool> {
    return Promise.async {
      let fm = FileManager.default
      var isDir: ObjCBool = false
      guard fm.fileExists(atPath: dirPath, isDirectory: &isDir), isDir.boolValue else {
        OneKeyLog.error("BundleCrypto", "[bundle-verify] dir not found: \(dirPath)")
        return false
      }
      // Normalize the directory prefix so relative paths match.
      let normalizedDir = (dirPath as NSString).hasSuffix("/") ? dirPath : dirPath + "/"

      var expected: [String: String] = [:]
      for entry in entries { expected[entry.relativePath] = entry.sha256 }

      guard let enumerator = fm.enumerator(atPath: dirPath) else { return false }
      while let file = enumerator.nextObject() as? String {
        if file.contains("metadata.json") || file.contains(".DS_Store") { continue }
        let fullPath = (dirPath as NSString).appendingPathComponent(file)
        var entryIsDir: ObjCBool = false
        if fm.fileExists(atPath: fullPath, isDirectory: &entryIsDir), entryIsDir.boolValue { continue }

        let relativePath = fullPath.replacingOccurrences(of: normalizedDir, with: "")
        guard let expectedSHA256 = expected[relativePath] else {
          OneKeyLog.error("BundleCrypto", "[bundle-verify] File on disk not found in metadata: \(relativePath)")
          return false
        }
        guard let actualSHA256 = Self.calculateSHA256Sync(fullPath) else {
          OneKeyLog.error("BundleCrypto", "[bundle-verify] Failed to calculate SHA256 for file: \(relativePath)")
          return false
        }
        if !Self.secureCompare(expectedSHA256, actualSHA256) {
          OneKeyLog.error("BundleCrypto", "[bundle-verify] SHA256 mismatch for \(relativePath)")
          return false
        }
      }

      // Verify completeness: every expected entry must exist on disk.
      for (relativePath, _) in expected {
        let expectedFilePath = normalizedDir + relativePath
        if !fm.fileExists(atPath: expectedFilePath) {
          OneKeyLog.error("BundleCrypto", "[bundle-verify] File listed in metadata but missing on disk: \(relativePath)")
          return false
        }
      }
      return true
    }
  }

  // MARK: - Hash a whole dir into relativePath -> sha256 entries (new helper,
  // same enumeration/skip rules as verifyDirAgainstHashes).

  func hashDir(dirPath: String) throws -> Promise<[DirHashEntry]> {
    return Promise.async {
      let fm = FileManager.default
      let normalizedDir = (dirPath as NSString).hasSuffix("/") ? dirPath : dirPath + "/"
      var results: [DirHashEntry] = []
      guard let enumerator = fm.enumerator(atPath: dirPath) else { return [] }
      while let file = enumerator.nextObject() as? String {
        if file.contains("metadata.json") || file.contains(".DS_Store") { continue }
        let fullPath = (dirPath as NSString).appendingPathComponent(file)
        var entryIsDir: ObjCBool = false
        if fm.fileExists(atPath: fullPath, isDirectory: &entryIsDir), entryIsDir.boolValue { continue }
        guard let sha256 = Self.calculateSHA256Sync(fullPath) else {
          OneKeyLog.error("BundleCrypto", "hashDir: failed to hash \(file)")
          throw NSError(domain: "BundleCrypto", code: -1, userInfo: [NSLocalizedDescriptionKey: "HASH_FAILED"])
        }
        let relativePath = fullPath.replacingOccurrences(of: normalizedDir, with: "")
        results.append(DirHashEntry(relativePath: relativePath, sha256: sha256))
      }
      return results
    }
  }

  // MARK: - Symlink / path-traversal guard — ported from
  // validateExtractedPathSafety.

  func validateExtractedPathSafety(destination: String) throws -> Promise<Bool> {
    return Promise.async {
      let fm = FileManager.default
      let resolvedDestination = (destination as NSString).resolvingSymlinksInPath

      guard let enumerator = fm.enumerator(atPath: destination) else { return true }
      while let file = enumerator.nextObject() as? String {
        let fullPath = (destination as NSString).appendingPathComponent(file)
        if let attrs = enumerator.fileAttributes,
           attrs[.type] as? FileAttributeType == .typeSymbolicLink {
          OneKeyLog.error("BundleCrypto", "Symlink detected in extracted bundle: \(file)")
          return false
        }
        let resolvedPath = (fullPath as NSString).resolvingSymlinksInPath
        if !resolvedPath.hasPrefix(resolvedDestination) {
          OneKeyLog.error("BundleCrypto", "Path traversal detected in extracted bundle: \(file)")
          return false
        }
      }
      return true
    }
  }

  // MARK: - Safe file verbs — atomic write / safe rename / version-dir listing.
  // Consolidates the FileManager write/move/enumerate patterns scattered through
  // ReactNativeBundleUpdate (writeSignatureFile atomic write, downloadBundle
  // remove-then-move, listLocalBundles contentsOfDirectory) into explicit APIs.

  func atomicWriteFile(filePath: String, contentUtf8: String) throws -> Promise<Void> {
    return Promise.async {
      let dir = (filePath as NSString).deletingLastPathComponent
      let fm = FileManager.default
      if !fm.fileExists(atPath: dir) {
        try fm.createDirectory(atPath: dir, withIntermediateDirectories: true)
      }
      // Foundation's atomically:true writes to a temp file then renames into place.
      try contentUtf8.write(toFile: filePath, atomically: true, encoding: .utf8)
    }
  }

  func safeRename(fromPath: String, toPath: String) throws -> Promise<Void> {
    return Promise.async {
      let fm = FileManager.default
      let destDir = (toPath as NSString).deletingLastPathComponent
      if !fm.fileExists(atPath: destDir) {
        try fm.createDirectory(atPath: destDir, withIntermediateDirectories: true)
      }
      // Same pattern as downloadBundle: remove existing target, then move.
      if fm.fileExists(atPath: toPath) {
        try fm.removeItem(atPath: toPath)
      }
      try fm.moveItem(at: URL(fileURLWithPath: fromPath), to: URL(fileURLWithPath: toPath))
    }
  }

  func listVersionDirs(parentDir: String) throws -> Promise<[String]> {
    return Promise.async {
      let fm = FileManager.default
      guard let contents = try? fm.contentsOfDirectory(atPath: parentDir) else {
        return []
      }
      var results: [String] = []
      for name in contents {
        let fullPath = (parentDir as NSString).appendingPathComponent(name)
        var isDir: ObjCBool = false
        guard fm.fileExists(atPath: fullPath, isDirectory: &isDir), isDir.boolValue else { continue }
        results.append(name)
      }
      return results
    }
  }
}
