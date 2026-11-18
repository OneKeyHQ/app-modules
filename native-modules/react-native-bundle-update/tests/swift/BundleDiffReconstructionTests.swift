import CommonCrypto
import Foundation
import XCTest
@testable import BundleDiffReconstruction

final class BundleDiffReconstructionTests: XCTestCase {
    private func sha256Hex(data: Data) -> String {
        var digest = [UInt8](repeating: 0, count: Int(CC_SHA256_DIGEST_LENGTH))
        data.withUnsafeBytes { bytes in
            _ = CC_SHA256(bytes.baseAddress, CC_LONG(data.count), &digest)
        }
        return digest.map { String(format: "%02x", $0) }.joined()
    }

    private func sha256OfFile(_ path: String) -> String? {
        guard let data = FileManager.default.contents(atPath: path) else { return nil }
        return sha256Hex(data: data)
    }

    private func withTempDir(_ body: (URL) throws -> Void) throws {
        let dir = FileManager.default.temporaryDirectory.appendingPathComponent(UUID().uuidString, isDirectory: true)
        try FileManager.default.createDirectory(at: dir, withIntermediateDirectories: true)
        defer { try? FileManager.default.removeItem(at: dir) }
        try body(dir)
    }

    func testIsUsableDiffParamsValidation() {
        let validHash = String(repeating: "a", count: 64)
        XCTAssertTrue(BundleDiffReconstruction.isUsableDiffParams(
            diffDownloadUrl: "https://cdn.example.com/diff.zip",
            diffSha256: validHash,
            diffSignature: "-----BEGIN PGP SIGNED MESSAGE-----",
            isFallbackMarked: false
        ))
        XCTAssertFalse(BundleDiffReconstruction.isUsableDiffParams(
            diffDownloadUrl: "https://cdn.example.com/diff.zip",
            diffSha256: validHash,
            diffSignature: "sig",
            isFallbackMarked: true
        ))
        XCTAssertFalse(BundleDiffReconstruction.isUsableDiffParams(
            diffDownloadUrl: "http://cdn.example.com/diff.zip",
            diffSha256: validHash,
            diffSignature: "sig",
            isFallbackMarked: false
        ))
        XCTAssertFalse(BundleDiffReconstruction.isUsableDiffParams(
            diffDownloadUrl: "https://cdn.example.com/diff.zip",
            diffSha256: "short",
            diffSignature: "sig",
            isFallbackMarked: false
        ))
        XCTAssertFalse(BundleDiffReconstruction.isUsableDiffParams(
            diffDownloadUrl: "https://cdn.example.com/diff.zip",
            diffSha256: validHash,
            diffSignature: "",
            isFallbackMarked: false
        ))
    }

    func testParseAndValidateManifestRejectsInvalidAndUnsafeInputs() throws {
        let hashA = String(repeating: "a", count: 64)
        let hashB = String(repeating: "b", count: 64)
        let hashMeta = String(repeating: "c", count: 64)

        let validJson = """
        {
          "schemaVersion": 1,
          "appType": "ios",
          "appVersion": "6.8.0",
          "baseAppVersion": "6.8.0",
          "baseMetadataSha256": "\(hashA)",
          "targetBundleVersion": "20260102",
          "targetMetadataSha256": "\(hashMeta.uppercased())",
          "hashAlgorithm": "sha256",
          "changedFiles": {
            "main.jsbundle.hbc": "\(hashA)"
          },
          "reusedFiles": {
            "segments/chunk.seg.hbc": "\(hashB)"
          },
          "deletedFiles": ["segments/old.seg.hbc"]
        }
        """.data(using: .utf8)!

        let manifest = try BundleDiffReconstruction.parseAndValidateManifest(
            jsonData: validJson,
            expectedAppType: "ios",
            expectedAppVersion: "6.8.0",
            expectedBundleVersion: "20260102"
        )
        XCTAssertEqual(manifest.targetMetadataSha256, hashMeta)
        XCTAssertEqual(manifest.changedFiles["main.jsbundle.hbc"], hashA)
        XCTAssertEqual(manifest.reusedFiles["segments/chunk.seg.hbc"], hashB)
        XCTAssertEqual(manifest.deletedFiles, ["segments/old.seg.hbc"])

        let badAppType = String(data: validJson, encoding: .utf8)!
            .replacingOccurrences(of: "\"ios\"", with: "\"android\"")
            .data(using: .utf8)!
        XCTAssertThrowsError(try BundleDiffReconstruction.parseAndValidateManifest(
            jsonData: badAppType,
            expectedAppType: "ios",
            expectedAppVersion: "6.8.0",
            expectedBundleVersion: "20260102"
        )) { error in
            XCTAssertEqual(error as? BundleDiffReconstructionError, .integrityFailed("diff-manifest appType mismatch"))
        }

        let badSchema = String(data: validJson, encoding: .utf8)!
            .replacingOccurrences(of: "\"schemaVersion\": 1", with: "\"schemaVersion\": 2")
            .data(using: .utf8)!
        XCTAssertThrowsError(try BundleDiffReconstruction.parseAndValidateManifest(
            jsonData: badSchema,
            expectedAppType: "ios",
            expectedAppVersion: "6.8.0",
            expectedBundleVersion: "20260102"
        )) { error in
            XCTAssertEqual(error as? BundleDiffReconstructionError, .fallbackRequired("unsupported diff-manifest schemaVersion"))
        }

        let boolSchema = String(data: validJson, encoding: .utf8)!
            .replacingOccurrences(of: "\"schemaVersion\": 1", with: "\"schemaVersion\": true")
            .data(using: .utf8)!
        XCTAssertThrowsError(try BundleDiffReconstruction.parseAndValidateManifest(
            jsonData: boolSchema,
            expectedAppType: "ios",
            expectedAppVersion: "6.8.0",
            expectedBundleVersion: "20260102"
        )) { error in
            XCTAssertEqual(error as? BundleDiffReconstructionError, .fallbackRequired("unsupported diff-manifest schemaVersion"))
        }

        let traversal = String(data: validJson, encoding: .utf8)!
            .replacingOccurrences(of: "segments/chunk.seg.hbc", with: "../evil.js")
            .data(using: .utf8)!
        XCTAssertThrowsError(try BundleDiffReconstruction.parseAndValidateManifest(
            jsonData: traversal,
            expectedAppType: "ios",
            expectedAppVersion: "6.8.0",
            expectedBundleVersion: "20260102"
        )) { error in
            XCTAssertEqual(error as? BundleDiffReconstructionError, .securityRejected("unsafe relative path in reusedFiles: ../evil.js"))
        }
    }

    func testReconstructUsesCandidateOrderAndBuiltinFallback() throws {
        try withTempDir { root in
            let fm = FileManager.default
            let staging = root.appendingPathComponent("staging", isDirectory: true)
            let activeOta = root.appendingPathComponent("activeOta", isDirectory: true)
            let olderOta = root.appendingPathComponent("olderOta", isDirectory: true)
            let builtin = root.appendingPathComponent("builtin", isDirectory: true)

            try fm.createDirectory(at: staging, withIntermediateDirectories: true)
            try fm.createDirectory(at: activeOta.appendingPathComponent("segments"), withIntermediateDirectories: true)
            try fm.createDirectory(at: olderOta.appendingPathComponent("segments"), withIntermediateDirectories: true)
            try fm.createDirectory(at: builtin, withIntermediateDirectories: true)

            let changedData = Data("new-main".utf8)
            let changedHash = sha256Hex(data: changedData)
            try changedData.write(to: staging.appendingPathComponent("main.jsbundle.hbc"))

            let segData = Data("reused-segment".utf8)
            let segHash = sha256Hex(data: segData)
            // Active OTA has wrong content for seg, older OTA has right content
            try Data("corrupt".utf8).write(to: activeOta.appendingPathComponent("segments/a.seg.hbc"))
            try segData.write(to: olderOta.appendingPathComponent("segments/a.seg.hbc"))

            let commonData = Data("builtin-common".utf8)
            let commonHash = sha256Hex(data: commonData)
            try commonData.write(to: builtin.appendingPathComponent("common.bundle"))

            let manifest = BundleDiffManifest(
                schemaVersion: 1,
                appType: "ios",
                appVersion: "6.8.0",
                baseAppVersion: "6.8.0",
                baseMetadataSha256: changedHash,
                targetBundleVersion: "20260102",
                targetMetadataSha256: changedHash,
                hashAlgorithm: "sha256",
                changedFiles: ["main.jsbundle.hbc": changedHash],
                reusedFiles: [
                    "segments/a.seg.hbc": segHash,
                    "common.bundle": commonHash,
                ],
                deletedFiles: []
            )

            try BundleDiffReconstruction.reconstruct(
                stagingDir: staging.path,
                manifest: manifest,
                candidateBundleDirs: [activeOta.path, olderOta.path],
                sha256OfFile: { self.sha256OfFile($0) },
                builtinCopy: { relPath, destPath, expectedHash in
                    let src = builtin.appendingPathComponent(relPath)
                    guard let h = self.sha256OfFile(src.path), h == expectedHash else { return false }
                    try? fm.copyItem(at: src, to: URL(fileURLWithPath: destPath))
                    return true
                }
            )

            XCTAssertEqual(sha256OfFile(staging.appendingPathComponent("segments/a.seg.hbc").path), segHash)
            XCTAssertEqual(sha256OfFile(staging.appendingPathComponent("common.bundle").path), commonHash)

            // Missing reused file triggers fallbackRequired
            let missingManifest = BundleDiffManifest(
                schemaVersion: 1,
                appType: "ios",
                appVersion: "6.8.0",
                baseAppVersion: "6.8.0",
                baseMetadataSha256: changedHash,
                targetBundleVersion: "20260102",
                targetMetadataSha256: changedHash,
                hashAlgorithm: "sha256",
                changedFiles: ["main.jsbundle.hbc": changedHash],
                reusedFiles: ["missing.bundle": String(repeating: "f", count: 64)],
                deletedFiles: []
            )
            XCTAssertThrowsError(try BundleDiffReconstruction.reconstruct(
                stagingDir: staging.path,
                manifest: missingManifest,
                candidateBundleDirs: [activeOta.path, olderOta.path],
                sha256OfFile: { self.sha256OfFile($0) },
                builtinCopy: { _, _, _ in false }
            )) { error in
                XCTAssertEqual(
                    error as? BundleDiffReconstructionError,
                    .fallbackRequired("reused file unavailable or hash mismatch: missing.bundle")
                )
            }
        }
    }
}
