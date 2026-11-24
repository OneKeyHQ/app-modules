import Foundation
import ReactNativeBundleUpdate
import SSZipArchive
import XCTest

final class BundleDiffZipExtractorTests: XCTestCase {
    private func withArchive(
        entries: [(String, Data)],
        run: (URL, URL) throws -> Void
    ) throws {
        let root = FileManager.default.temporaryDirectory.appendingPathComponent(UUID().uuidString)
        try FileManager.default.createDirectory(at: root, withIntermediateDirectories: false)
        defer { try? FileManager.default.removeItem(at: root) }

        let archive = root.appendingPathComponent("diff.zip")
        let writer = SSZipArchive(path: archive.path)
        XCTAssertTrue(writer.open())
        for (name, data) in entries {
            XCTAssertTrue(writer.write(data, filename: name, withPassword: nil))
        }
        XCTAssertTrue(writer.close())
        try run(archive, root.appendingPathComponent("staging"))
    }

    func testExtractsNestedFilesAtExactLimits() throws {
        let payload = Data("payload".utf8)
        try withArchive(entries: [("assets/chunk", payload), ("metadata.json", payload)]) { archive, staging in
            XCTAssertEqual(BundleDiffZipExtractor.extract(
                archivePath: archive.path, destination: staging.path, maxBytes: 14, maxEntries: 3
            ), .success)
            XCTAssertEqual(try Data(contentsOf: staging.appendingPathComponent("assets/chunk")), payload)
            XCTAssertEqual(try Data(contentsOf: staging.appendingPathComponent("metadata.json")), payload)
        }
    }

    func testConflictingEntriesAreInvalidArchivesAndRemoveStaging() throws {
        let payload = Data([1])
        for names in [["chunk", "chunk"], ["chunk/part", "chunk"], ["chunk", "chunk/part"]] {
            try withArchive(entries: names.map { ($0, payload) }) { archive, staging in
                XCTAssertEqual(BundleDiffZipExtractor.extract(
                    archivePath: archive.path, destination: staging.path, maxBytes: 3, maxEntries: 3
                ), .invalidArchive)
                XCTAssertFalse(FileManager.default.fileExists(atPath: staging.path))
            }
        }
    }

    func testRejectsDeclaredPayloadAboveBudgetAndRemovesStaging() throws {
        let payload = Data("payload".utf8)
        try withArchive(entries: [("one", payload), ("two", payload)]) { archive, staging in
            XCTAssertEqual(BundleDiffZipExtractor.extract(
                archivePath: archive.path, destination: staging.path, maxBytes: 13, maxEntries: 2
            ), .securityRejected)
            XCTAssertFalse(FileManager.default.fileExists(atPath: staging.path))
        }
    }

    func testRejectsActualPayloadAboveBudgetWithUnderstatedSize() throws {
        let payload = Data(repeating: 0x61, count: 64)
        try withArchive(entries: [("chunk", payload)]) { archive, staging in
            var bytes = try Data(contentsOf: archive)
            let centralHeader = try XCTUnwrap(bytes.range(of: Data([0x50, 0x4b, 0x01, 0x02])))
            let sizeOffset = centralHeader.lowerBound + 24
            // Replace the central-directory size, including a ZIP64 sentinel if present.
            bytes.replaceSubrange(sizeOffset..<(sizeOffset + 4), with: [1, 0, 0, 0])
            try bytes.write(to: archive)
            XCTAssertEqual(BundleDiffZipExtractor.extract(
                archivePath: archive.path, destination: staging.path, maxBytes: 128, maxEntries: 1
            ), .invalidArchive)
            XCTAssertEqual(BundleDiffZipExtractor.extract(
                archivePath: archive.path, destination: staging.path, maxBytes: 16, maxEntries: 1
            ), .securityRejected)
            XCTAssertFalse(FileManager.default.fileExists(atPath: staging.path))
        }
    }

    func testCountsArchiveEntriesIncludingIgnoredResourceForks() throws {
        let payload = Data([1])
        try withArchive(entries: [("chunk", payload), ("__MACOSX/ignored", payload)]) { archive, staging in
            XCTAssertEqual(BundleDiffZipExtractor.extract(
                archivePath: archive.path, destination: staging.path, maxBytes: 2, maxEntries: 1
            ), .securityRejected)
            XCTAssertFalse(FileManager.default.fileExists(atPath: staging.path))
        }
    }

    func testCountsImplicitDirectoriesBeforeCreatingThem() throws {
        try withArchive(entries: [("a/b/chunk", Data([1]))]) { archive, staging in
            XCTAssertEqual(BundleDiffZipExtractor.extract(
                archivePath: archive.path, destination: staging.path, maxBytes: 1, maxEntries: 2
            ), .securityRejected)
            XCTAssertFalse(FileManager.default.fileExists(atPath: staging.path))
        }
    }

    func testRejectsTraversalBeforeWritingOutsideStaging() throws {
        try withArchive(entries: [("../escaped", Data([1]))]) { archive, staging in
            XCTAssertEqual(BundleDiffZipExtractor.extract(
                archivePath: archive.path, destination: staging.path, maxBytes: 1, maxEntries: 1
            ), .securityRejected)
            XCTAssertFalse(FileManager.default.fileExists(atPath: staging.path))
            XCTAssertFalse(FileManager.default.fileExists(
                atPath: staging.deletingLastPathComponent().appendingPathComponent("escaped").path
            ))
        }
    }
}
