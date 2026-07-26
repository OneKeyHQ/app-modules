import Foundation
import XCTest
@testable import RangeDownloadLogic

final class FirmwareArtifactReaderTests: XCTestCase {
  private var root: URL!
  private var store: FirmwareArtifactStore!
  private var reader: FirmwareArtifactReader!

  override func setUpWithError() throws {
    root = FileManager.default.temporaryDirectory.appendingPathComponent(
      "firmware-artifact-reader-\(UUID().uuidString)",
      isDirectory: true
    )
    store = FirmwareArtifactStore(rootDirectory: root)
    reader = FirmwareArtifactReader(store: store)
  }

  override func tearDownWithError() throws {
    reader.closeAll()
    try? FileManager.default.removeItem(at: root)
  }

  func testReadsBoundedBinaryChunksAndAllowsEmptyReadOnlyAtEOF() throws {
    let payload = Data((0..<255).map(UInt8.init))
    let artifact = try createVerifiedArtifact(payload)
    let info = try reader.open(
      artifactRef: artifact.artifactRef,
      immutableToken: artifact.immutableToken!
    )

    XCTAssertEqual(
      try reader.read(readerId: info.readerId, offset: 7, length: 31),
      payload.subdata(in: 7..<38)
    )
    XCTAssertEqual(
      try reader.read(
        readerId: info.readerId,
        offset: Double(payload.count),
        length: 0
      ),
      Data()
    )
    XCTAssertThrowsError(
      try reader.read(readerId: info.readerId, offset: 0, length: 0)
    ) { error in
      XCTAssertEqual(error as? FirmwareArtifactReaderError, .emptyRead)
    }
  }

  func testRejectsStaleTokenUnsafeNumbersOversizedAndOutOfBoundsReads() throws {
    let payload = Data("verified firmware reader".utf8)
    let artifact = try createVerifiedArtifact(payload)

    XCTAssertThrowsError(
      try reader.open(
        artifactRef: artifact.artifactRef,
        immutableToken: UUID().uuidString.lowercased()
      )
    ) { error in
      XCTAssertEqual(
        error as? FirmwareArtifactReaderError,
        .immutableTokenMismatch
      )
    }

    let info = try reader.open(
      artifactRef: artifact.artifactRef,
      immutableToken: artifact.immutableToken!
    )
    XCTAssertThrowsError(
      try reader.read(readerId: info.readerId, offset: 0.5, length: 1)
    ) { error in
      XCTAssertEqual(error as? FirmwareArtifactReaderError, .invalidRange)
    }
    XCTAssertThrowsError(
      try reader.read(
        readerId: info.readerId,
        offset: 0,
        length: Double(FirmwareArtifactReader.maxReadBytes + 1)
      )
    ) { error in
      XCTAssertEqual(
        error as? FirmwareArtifactReaderError,
        .readLimitExceeded
      )
    }
    XCTAssertThrowsError(
      try reader.read(
        readerId: info.readerId,
        offset: Double(payload.count - 1),
        length: 2
      )
    ) { error in
      XCTAssertEqual(error as? FirmwareArtifactReaderError, .invalidRange)
    }
  }

  func testCloseIsIdempotentAndReleasesDeletionProtection() throws {
    let artifact = try createVerifiedArtifact(Data("firmware".utf8))
    let info = try reader.open(
      artifactRef: artifact.artifactRef,
      immutableToken: artifact.immutableToken!
    )

    XCTAssertThrowsError(try store.discardArtifact(artifact.artifactRef)) {
      XCTAssertEqual($0 as? FirmwareArtifactStoreError, .artifactLeased)
    }
    try reader.close(readerId: info.readerId)
    try reader.close(readerId: info.readerId)
    try store.discardArtifact(artifact.artifactRef)
  }

  private func createVerifiedArtifact(
    _ payload: Data
  ) throws -> StoredFirmwareArtifactMetadata {
    try FileManager.default.createDirectory(
      at: root,
      withIntermediateDirectories: true
    )
    let digestFixture = root.appendingPathComponent("digest-\(UUID().uuidString)")
    try payload.write(to: digestFixture)
    let digest = RangeDownloadLogic.calculateSHA256(digestFixture.path)!
    try FileManager.default.removeItem(at: digestFixture)
    let metadata = try store.createArtifact(
      artifactId: "firmware-main",
      canonicalURL: "https://downloads.example.com/firmware.bin",
      hostname: "downloads.example.com",
      route: .domain,
      expectedSize: Int64(payload.count),
      expectedSha256: digest,
      retentionClass: .staging
    )
    try payload.write(to: store.partialFileURL(for: metadata.artifactRef))
    return try store.promoteVerifiedStaging(
      artifactRef: metadata.artifactRef,
      maxBytes: Int64(payload.count)
    )
  }
}
