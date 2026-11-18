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

    func testValidateManifestAgainstTargetMetadataAndFull8CategoryReconstruction() throws {
        try withTempDir { root in
            let fm = FileManager.default
            let staging = root.appendingPathComponent("staging", isDirectory: true)
            let builtin = root.appendingPathComponent("builtin", isDirectory: true)
            try fm.createDirectory(at: staging, withIntermediateDirectories: true)
            try fm.createDirectory(at: builtin, withIntermediateDirectories: true)

            let baseFiles: [String: Data] = [
                "common.bundle": Data("common-v680".utf8),
                "main.jsbundle.hbc": Data("main-v680".utf8),
                "background.bundle": Data("bg-v680".utf8),
                "module-id-map.json": Data("{}".utf8),
                "segments/seg_1.seg.hbc": Data("seg-1".utf8),
                "segments-background/seg_bg_1.seg.hbc": Data("seg-bg-1".utf8),
                "assets/node_modules/icon.png": Data("icon".utf8),
                "web-embed/index.html": Data("<html></html>".utf8),
            ]
            for (rel, data) in baseFiles {
                let builtinRel = rel == "main.jsbundle.hbc" ? "main.jsbundle" : rel
                let url = builtin.appendingPathComponent(builtinRel)
                try fm.createDirectory(at: url.deletingLastPathComponent(), withIntermediateDirectories: true)
                try data.write(to: url)
            }

            let changedSegmentData = Data("seg-1-hotfix".utf8)
            let changedSegmentUrl = staging.appendingPathComponent("segments/seg_1.seg.hbc")
            try fm.createDirectory(at: changedSegmentUrl.deletingLastPathComponent(), withIntermediateDirectories: true)
            try changedSegmentData.write(to: changedSegmentUrl)

            var targetFileEntries: [String: String] = [:]
            var reusedFiles: [String: String] = [:]
            for (rel, data) in baseFiles {
                if rel == "segments/seg_1.seg.hbc" {
                    targetFileEntries[rel] = sha256Hex(data: changedSegmentData)
                } else {
                    let h = sha256Hex(data: data)
                    targetFileEntries[rel] = h
                    reusedFiles[rel] = h
                }
            }
            let changedFiles = ["segments/seg_1.seg.hbc": sha256Hex(data: changedSegmentData)]

            let manifest = BundleDiffManifest(
                schemaVersion: 1,
                appType: "ios",
                appVersion: "6.8.0",
                baseAppVersion: "6.8.0",
                baseMetadataSha256: String(repeating: "a", count: 64),
                targetBundleVersion: "7701200",
                targetMetadataSha256: String(repeating: "b", count: 64),
                hashAlgorithm: "sha256",
                changedFiles: changedFiles,
                reusedFiles: reusedFiles,
                deletedFiles: ["segments/removed.seg.hbc"]
            )

            // 1. Valid bijective partition passes
            try BundleDiffReconstruction.validateManifestAgainstTargetMetadata(
                manifest: manifest,
                targetFileEntries: targetFileEntries
            )

            // 2. Omitting a file from targetFileEntries or mismatching hash throws fallbackRequired
            var incompleteTarget = targetFileEntries
            incompleteTarget.removeValue(forKey: "web-embed/index.html")
            XCTAssertThrowsError(try BundleDiffReconstruction.validateManifestAgainstTargetMetadata(
                manifest: manifest,
                targetFileEntries: incompleteTarget
            ))

            var tamperedTarget = targetFileEntries
            tamperedTarget["web-embed/index.html"] = String(repeating: "f", count: 64)
            XCTAssertThrowsError(try BundleDiffReconstruction.validateManifestAgainstTargetMetadata(
                manifest: manifest,
                targetFileEntries: tamperedTarget
            ))

            // 3. Reconstruct all 8 categories (including main.jsbundle.hbc -> main.jsbundle builtin fallback)
            try BundleDiffReconstruction.reconstruct(
                stagingDir: staging.path,
                manifest: manifest,
                candidateBundleDirs: [],
                sha256OfFile: { self.sha256OfFile($0) },
                builtinCopy: { relPath, destPath, expectedHash in
                    let candidates = relPath == "main.jsbundle.hbc" ? [relPath, "main.jsbundle"] : [relPath]
                    for candidate in candidates {
                        let src = builtin.appendingPathComponent(candidate)
                        if let h = self.sha256OfFile(src.path), h == expectedHash {
                            try? fm.copyItem(at: src, to: URL(fileURLWithPath: destPath))
                            return true
                        }
                    }
                    return false
                }
            )

            for (rel, expectedHash) in targetFileEntries {
                let reconstructedPath = staging.appendingPathComponent(rel).path
                XCTAssertEqual(sha256OfFile(reconstructedPath), expectedHash, "Mismatch for \(rel)")
            }

            // 4. Verify builtinCandidateRelativePaths for special iOS locations
            XCTAssertEqual(
                BundleDiffReconstruction.builtinCandidateRelativePaths(for: "main.jsbundle.hbc"),
                ["main.jsbundle.hbc", "main.jsbundle"]
            )
            XCTAssertEqual(
                BundleDiffReconstruction.builtinCandidateRelativePaths(for: "assets/assets/packages/icon.png"),
                ["assets/assets/packages/icon.png", "assets/packages/icon.png", "packages/icon.png"]
            )
            XCTAssertEqual(
                BundleDiffReconstruction.builtinCandidateRelativePaths(for: "assets/node_modules/icon.png"),
                ["assets/node_modules/icon.png", "assets/assets/node_modules/icon.png", "node_modules/icon.png"]
            )
            XCTAssertEqual(
                BundleDiffReconstruction.builtinCandidateRelativePaths(for: "web-embed/index.html"),
                ["web-embed/index.html"]
            )
        }
    }
}
