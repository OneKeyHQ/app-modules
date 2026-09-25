import Foundation
import ReactNativeBundleCrypto
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

  private func withTemporaryDirectory(_ body: (URL) throws -> Void) throws {
    let root = FileManager.default.temporaryDirectory.appendingPathComponent(UUID().uuidString, isDirectory: true)
    try FileManager.default.createDirectory(at: root, withIntermediateDirectories: true)
    defer { try? FileManager.default.removeItem(at: root) }
    try body(root)
  }
}
