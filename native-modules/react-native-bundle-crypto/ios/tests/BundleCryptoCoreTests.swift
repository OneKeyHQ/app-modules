import Foundation
@testable import ReactNativeBundleCrypto
import XCTest

final class BundleCryptoCoreTests: XCTestCase {
  private let expectedSignedHash = "2ada9c871104fc40649fa3de67a7d8e33faadc18e9abd587e8bb85be0a003eba"

  // Existing release-key test vector, also used by BundleUpdate.testVerification.
  private let signedMetadata = """
    -----BEGIN PGP SIGNED MESSAGE-----
    Hash: SHA256

    {
      "fileName": "metadata.json",
      "sha256": "2ada9c871104fc40649fa3de67a7d8e33faadc18e9abd587e8bb85be0a003eba",
      "size": 158590,
      "generatedAt": "2025-09-19T07:49:13.000Z"
    }
    -----BEGIN PGP SIGNATURE-----

    iQJCBAEBCAAsFiEE62iuVE8f3YzSZGJPs2mmepC/OHsFAmjNJ1IOHGRldkBvbmVr
    ZXkuc28ACgkQs2mmepC/OHs6Rw/9FKHl5aNsE7V0IsFf/l+h16BYKFwVsL69alMk
    CFLna8oUn0+tyECF6wKBKw5pHo5YR27o2pJfYbAER6dygDF6WTZ1lZdf5QcBMjGA
    LCeXC0hzUBzSSOH4bKBTa3fHp//HdSV1F2OnkymbXqYN7WXvuQPLZ0nV6aU88hCk
    HgFifcvkXAnWKoosUtj0Bban/YBRyvmQ5C2akxUPEkr4Yck1QXwzJeNRd7wMXHjH
    JFK6lJcuABiB8wpJDXJkFzKs29pvHIK2B2vdOjU2rQzKOUwaKHofDi5C4+JitT2b
    2pSeYP3PAxXYw6XDOmKTOiC7fPnfLjtcPjNYNFCezVKZT6LKvZW9obnW8Q9LNJ4W
    okMPgHObkabv3OqUaTA9QNVfI/X9nvggzlPnaKDUrDWTf7n3vlrdexugkLtV/tJA
    uguPlI5hY7Ue5OW7ckWP46hfmq1+UaIdeUY7dEO+rPZDz6KcArpaRwBiLPBhneIr
    /X3KuMzS272YbPbavgCZGN9xJR5kZsEQE5HhPCbr6Nf0qDnh+X8mg0tAB/U6F+ZE
    o90sJL1ssIaYvST+VWVaGRr4V5nMDcgHzWSF9Q/wm22zxe4alDaBdvOlUseW0iaM
    n2DMz6gqk326W6SFynYtvuiXo7wG4Cmn3SuIU8xfv9rJqunpZGYchMd7nZektmEJ
    91Js0rQ=
    =A/Ii
    -----END PGP SIGNATURE-----
    """

  func testSignedMetadataAcceptsReleaseKeyAndRejectsTampering() {
    let verified = BundleCryptoCore.verifyGpgCleartext(signedMetadata)
    XCTAssertTrue(verified.valid)
    XCTAssertEqual(verified.sha256, expectedSignedHash)
    XCTAssertNil(verified.reason)

    let missingBinding = BundleCryptoCore.verifyGpgCleartext(
      signedMetadata, expectedAppVersion: "5.12.0"
    )
    XCTAssertFalse(missingBinding.valid)
    XCTAssertNil(missingBinding.sha256)
    XCTAssertEqual(missingBinding.reason, "METADATA_APPVERSION_MISMATCH")
    XCTAssertEqual(
      BundleCryptoCore.verifyGpgCleartext(signedMetadata, expectedAppType: "mobile").reason,
      "METADATA_APPTYPE_MISMATCH"
    )
    XCTAssertEqual(
      BundleCryptoCore.verifyGpgCleartext(signedMetadata, expectedBundleVersion: "20250919").reason,
      "METADATA_BUNDLEVERSION_MISMATCH"
    )

    let wrongAsc = BundleCryptoCore.verifyDetachedAsc(
      signedMetadata,
      expectedFileName: "OneKey.apk"
    )
    XCTAssertFalse(wrongAsc.valid)
    XCTAssertNil(wrongAsc.sha256)
    XCTAssertEqual(wrongAsc.reason, "SHA256_TOKEN_INVALID")

    XCTAssertEqual(
      BundleCryptoCore.extractDetachedAscSha256(expectedSignedHash, expectedFileName: nil),
      expectedSignedHash
    )
    XCTAssertEqual(
      BundleCryptoCore.extractDetachedAscSha256(expectedSignedHash, expectedFileName: "OneKey.apk"),
      ""
    )
    XCTAssertEqual(
      BundleCryptoCore.extractDetachedAscSha256("\(expectedSignedHash)  *OneKey.apk", expectedFileName: "OneKey.apk"),
      expectedSignedHash
    )
    XCTAssertEqual(
      BundleCryptoCore.extractDetachedAscSha256("\(expectedSignedHash)  *Other.apk", expectedFileName: "OneKey.apk"),
      ""
    )

    let tampered = signedMetadata.replacingOccurrences(of: "158590", with: "158591")
    let rejected = BundleCryptoCore.verifyGpgCleartext(tampered)
    XCTAssertFalse(rejected.valid)
    XCTAssertNil(rejected.sha256)
    XCTAssertNotNil(rejected.reason)

    let unsigned = BundleCryptoCore.verifyGpgCleartext("{\"sha256\":\"\(expectedSignedHash)\"}")
    XCTAssertFalse(unsigned.valid)
    XCTAssertNil(unsigned.sha256)
    XCTAssertEqual(unsigned.reason, "NOT_PGP_SIGNED_MESSAGE")
  }

  func testExpectedFileNameValidationForDiffManifest() {
    let rejectedByDefaultMismatch = BundleCryptoCore.verifyGpgCleartext(
      signedMetadata,
      expectedFileName: "diff-manifest.json"
    )
    XCTAssertFalse(rejectedByDefaultMismatch.valid)
    XCTAssertNil(rejectedByDefaultMismatch.sha256)
    XCTAssertEqual(rejectedByDefaultMismatch.reason, "INVALID_METADATA_FILENAME")

    let testPublicKey = """
      -----BEGIN PGP PUBLIC KEY BLOCK-----

      mQENBGr9TcEBCADqhPZEGynHy8F6Zs0n/ayUQZgTTocdnZupAbatptQDf4ajCYNk
      AmE44GUZhaoUaRepBDlrEr4aEjkcfL1Zx9iGoRu1jbiEBSJnZAdhN8wMQZsG7usG
      ydg/H9XparEPyHq6aqYu5Eq5OIJ99Vbe5sY0fEYVnBJHQSRpuH09B/RZYj+Up3aj
      Vs84InZGAlpRABsjW6cKDGxI4qqxd6lcuPzzqKZBp0NqeEzIE/I24GbGA4BJTd8S
      9gaJXB9Totuxy/uv+4+NLcAG4BDrQz0H0KXMrU9UmSUl4ZWp/iA13aHoZdoEsdv2
      aA8HQ3yb5qHElwEAvRapspzYL8MFZm2ak3NHABEBAAG0HlRlc3QgU2lnbmVyIDx0
      ZXN0QGV4YW1wbGUuY29tPokBUgQTAQgAPBYhBJtb/2ZyaVZpVQloADBHSIn7JD1z
      BQJq/U3BAxsvBAULCQgHAgIiAgYVCgkICwIEFgIDAQIeBwIXgAAKCRAwR0iJ+yQ9
      c2xACADEewfGtSOEtSkNCx1r23tfx2iBmAPy5sd0cuIszgg4qlbF01pU9OXJM9/d
      slQd+DGFbE2fKSnO0Zu1K84pRULcDCKETf0uXEYYH/K+t4k4PSFnyq3bA5DOHX7w
      xPq8jV/4CNcKwpQ40mhmv85q46mJO4ll/NQEN4ZFQliJswyZodfpjzVKz695usOp
      W0FD+Sgy2x6iauNrxBtm7ILNWWFCRW9J7+Y7Uc/RMNRpj9BuHk/xkh2QQt9twd95
      r2i2Fzn8IuFa2budqTzwea87p9VQdYnzmthHGt6Xzety4frFUBThwix85VIpoROj
      DCy0Hhtw6dfpxwWXGeFERjK0xCbs
      =ITXe
      -----END PGP PUBLIC KEY BLOCK-----
      """

    let signedDiffManifest = """
      -----BEGIN PGP SIGNED MESSAGE-----
      Hash: SHA256

      {
        "fileName": "diff-manifest.json",
        "sha256": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
        "appType": "ios",
        "appVersion": "6.8.0",
        "bundleVersion": "20260101"
      }
      -----BEGIN PGP SIGNATURE-----

      iQEzBAEBCAAdFiEEm1v/ZnJpVmlVCWgAMEdIifskPXMFAmr9TcIACgkQMEdIifsk
      PXML3Af/VKdIMeq/vP6i5dfv8SpjQcDwuNlYuQD9pFGqXtG2AADSmufv/9ojc3LZ
      QvGzd8Q9EVl+l1AfKSLTV/3kgsVUsEQP+iJCQVXN4OCZvrfEQiXX9VxMM2Rkevmu
      XaIoQlURsGOBuLyTZS4ECm/iFUaEp8s0ajCKUNAHcKiXBzwgqYThGF+FM3ahbIb0
      5u5IvfVdRnxHBPf9Nd+lH6EFnzIvKHuDMyIR7J9PAfuMnt0AZ1dZ8bHpj6A5yDIu
      2/vYjpGvHnqMzobhznsEEty5yLxP57SatrF0FXgRe6hM41MNQlLDKY1QiHn9Sqsg
      XHxbRRW2slQi8HEQtq1BY3MPmO0G9Q==
      =vF1w
      -----END PGP SIGNATURE-----
      """

    let acceptedDiff = BundleCryptoCore.verifyGpgCleartextWithKey(
      signedDiffManifest,
      expectedAppVersion: "6.8.0",
      expectedBundleVersion: "20260101",
      expectedAppType: "ios",
      expectedFileName: "diff-manifest.json",
      publicKeyArmored: testPublicKey
    )
    XCTAssertTrue(acceptedDiff.valid)
    XCTAssertEqual(acceptedDiff.sha256, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
    XCTAssertNil(acceptedDiff.reason)

    let rejectedDefault = BundleCryptoCore.verifyGpgCleartextWithKey(
      signedDiffManifest,
      expectedAppVersion: "6.8.0",
      expectedBundleVersion: "20260101",
      expectedAppType: "ios",
      publicKeyArmored: testPublicKey
    )
    XCTAssertFalse(rejectedDefault.valid)
    XCTAssertNil(rejectedDefault.sha256)
    XCTAssertEqual(rejectedDefault.reason, "INVALID_METADATA_FILENAME")
  }

  func testDirectoryHashesRejectChangedMissingAndUnlistedFiles() throws {
    try withTemporaryDirectory { root in
      let bundle = root.appendingPathComponent("bundle", isDirectory: true)
      let nested = bundle.appendingPathComponent("nested/bundle", isDirectory: true)
      try FileManager.default.createDirectory(at: nested, withIntermediateDirectories: true)
      let file = nested.appendingPathComponent("index.js")
      try "abc".write(to: file, atomically: true, encoding: .utf8)
      try "ignored".write(to: bundle.appendingPathComponent("metadata.json"), atomically: true, encoding: .utf8)

      let hashes = try BundleCryptoCore.hashDir(bundle.path)
      XCTAssertEqual(hashes.count, 1)
      XCTAssertEqual(hashes.first?.relativePath, "nested/bundle/index.js")
      XCTAssertEqual(hashes.first?.sha256, "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad")
      XCTAssertTrue(BundleCryptoCore.verifyDirAgainstHashes(dirPath: bundle.path, entries: hashes))

      try "changed".write(to: file, atomically: true, encoding: .utf8)
      XCTAssertFalse(BundleCryptoCore.verifyDirAgainstHashes(dirPath: bundle.path, entries: hashes))
      try FileManager.default.removeItem(at: file)
      XCTAssertFalse(BundleCryptoCore.verifyDirAgainstHashes(dirPath: bundle.path, entries: hashes))

      try "abc".write(to: file, atomically: true, encoding: .utf8)
      try "unlisted".write(to: bundle.appendingPathComponent("evil-metadata.json"), atomically: true, encoding: .utf8)
      XCTAssertFalse(BundleCryptoCore.verifyDirAgainstHashes(dirPath: bundle.path, entries: hashes))
    }
  }

  func testRelativeMetadataAndListedLegacyFilesAreHashed() throws {
    try withTemporaryDirectory { root in
      let nested = root.appendingPathComponent("assets", isDirectory: true)
      try FileManager.default.createDirectory(at: nested, withIntermediateDirectories: true)
      try Data("control".utf8).write(to: root.appendingPathComponent("metadata.json"))
      try Data("abc".utf8).write(to: nested.appendingPathComponent("metadata.json"))
      try Data("abc".utf8).write(to: root.appendingPathComponent(".DS_Store"))

      let hashes = try BundleCryptoCore.hashDir(root.path)
      XCTAssertEqual(hashes.map(\.relativePath), ["assets/metadata.json"])
      let sha = try XCTUnwrap(hashes.first?.sha256)
      XCTAssertFalse(BundleCryptoCore.verifyDirAgainstHashes(dirPath: root.path, entries: []))
      XCTAssertTrue(BundleCryptoCore.verifyDirAgainstHashes(dirPath: root.path, entries: hashes))
      XCTAssertTrue(BundleCryptoCore.verifyDirAgainstHashes(
        dirPath: root.path, entries: hashes + [.init(relativePath: ".DS_Store", sha256: sha)]
      ))
      XCTAssertFalse(BundleCryptoCore.verifyDirAgainstHashes(
        dirPath: root.path, entries: hashes + [.init(relativePath: ".DS_Store", sha256: expectedSignedHash)]
      ))
      XCTAssertFalse(BundleCryptoCore.verifyDirAgainstHashes(
        dirPath: root.path, entries: hashes + [.init(relativePath: "assets", sha256: sha)]
      ))
    }
  }

  func testExtractedPathSafetyRejectsSymlinks() throws {
    try withTemporaryDirectory { root in
      let destination = root.appendingPathComponent("bundle", isDirectory: true)
      let outside = root.appendingPathComponent("outside.txt")
      try FileManager.default.createDirectory(at: destination, withIntermediateDirectories: true)
      try "outside".write(to: outside, atomically: true, encoding: .utf8)
      try "inside".write(to: destination.appendingPathComponent("index.js"), atomically: true, encoding: .utf8)
      XCTAssertTrue(BundleCryptoCore.validateExtractedPathSafety(destination.path))

      try FileManager.default.createSymbolicLink(
        at: destination.appendingPathComponent("escape"),
        withDestinationURL: outside
      )
      XCTAssertFalse(BundleCryptoCore.validateExtractedPathSafety(destination.path))
    }
  }

  func testDirectoryHashesRejectExpectedPathsOutsideBundle() throws {
    try withTemporaryDirectory { root in
      let bundle = root.appendingPathComponent("bundle", isDirectory: true)
      let outside = root.appendingPathComponent("outside.txt")
      try FileManager.default.createDirectory(at: bundle, withIntermediateDirectories: true)
      try "outside".write(to: outside, atomically: true, encoding: .utf8)
      let wrongHash = String(repeating: "0", count: 64)

      XCTAssertFalse(BundleCryptoCore.verifyDirAgainstHashes(
        dirPath: bundle.path,
        entries: [.init(relativePath: "../outside.txt", sha256: wrongHash)]
      ))
      XCTAssertFalse(BundleCryptoCore.verifyDirAgainstHashes(
        dirPath: bundle.path,
        entries: [.init(relativePath: outside.path, sha256: wrongHash)]
      ))
      try FileManager.default.createSymbolicLink(
        at: bundle.appendingPathComponent("link.txt"),
        withDestinationURL: outside
      )
      XCTAssertFalse(BundleCryptoCore.verifyDirAgainstHashes(
        dirPath: bundle.path,
        entries: [.init(relativePath: "link.txt", sha256: wrongHash)]
      ))
      XCTAssertFalse(BundleCryptoCore.verifyDirAgainstHashes(
        dirPath: bundle.path,
        entries: [.init(relativePath: "", sha256: wrongHash)]
      ))
    }
  }

  private func withTemporaryDirectory(_ body: (URL) throws -> Void) throws {
    let root = FileManager.default.temporaryDirectory.appendingPathComponent(UUID().uuidString, isDirectory: true)
    try FileManager.default.createDirectory(at: root, withIntermediateDirectories: true)
    defer { try? FileManager.default.removeItem(at: root) }
    try body(root)
  }
}
