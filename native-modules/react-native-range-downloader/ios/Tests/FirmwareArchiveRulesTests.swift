import XCTest
@testable import RangeDownloadLogic

final class FirmwareArchiveRulesTests: XCTestCase {
  func testDiscoversRegularFilesAndIgnoresSafeDirectoryAndMacMetadata() throws {
    let validated = try FirmwareArchiveRules.validate(
      entries: [
        directoryFacts(index: 0, name: "firmware/"),
        facts(index: 1, name: "firmware/main.bin", size: 1_024),
        facts(index: 2, name: "resources/ble.bin", size: 512),
        directoryFacts(index: 3, name: "__MACOSX/"),
        facts(
          index: 4,
          name: "__MACOSX/firmware/main.bin",
          size: 32
        ),
      ],
      materializationPolicy: FirmwareArchiveRules.materializationPolicy
    )

    let materialized = validated.filter {
      $0.disposition == .materialize
    }
    XCTAssertEqual(
      materialized.map(\.entryId),
      ["firmware/main.bin", "resources/ble.bin"]
    )
    XCTAssertEqual(materialized.map(\.logicalName), ["main.bin", "ble.bin"])
    XCTAssertEqual(
      validated.filter { $0.disposition == .ignoreDirectory }.count,
      2
    )
    XCTAssertEqual(
      validated.filter { $0.disposition == .ignoreMetadata }.count,
      1
    )
    XCTAssertEqual(
      FirmwareArchiveRules.entriesRequiringIntegrityValidation(validated)
        .map(\.facts.index),
      [0, 1, 2, 3, 4]
    )
  }

  func testRejectsTraversalWindowsDeviceAdsAndPortableCollisions() {
    let unsafeNames = [
      "../main.bin",
      "/main.bin",
      "\\\\server\\share\\main.bin",
      "\\\\?\\C:\\main.bin",
      "C:\\main.bin",
      "firmware\\main.bin",
      "main.bin:stream",
      "CON",
      "firmware/con.bin",
      "firmware/AUX.txt",
      "firmware/COM1",
      "firmware/lpt9.bin",
      "firmware/NUL",
      "firmware//main.bin",
      "firmware/./main.bin",
      "firmware/main.bin.",
      "firmware/main.bin ",
      "firmware/cafe\u{301}.bin",
    ]
    for name in unsafeNames {
      XCTAssertFalse(
        try! FirmwareArchiveRules.isCanonicalPortableName(name),
        name
      )
    }

    XCTAssertThrowsError(
      try FirmwareArchiveRules.validate(
        entries: [
          facts(index: 0, name: "one/main.bin", size: 1_024),
          facts(index: 1, name: "two/MAIN.bin", size: 512),
        ],
        materializationPolicy: FirmwareArchiveRules.materializationPolicy
      )
    )
    XCTAssertThrowsError(
      try FirmwareArchiveRules.validate(
        entries: [
          facts(index: 0, name: "one/Straße.bin", size: 1_024),
          facts(index: 1, name: "two/STRASSE.bin", size: 512),
        ],
        materializationPolicy: FirmwareArchiveRules.materializationPolicy
      )
    )
    XCTAssertThrowsError(
      try FirmwareArchiveRules.validate(
        entries: [
          facts(index: 0, name: "one/ß.bin", size: 1_024),
          facts(index: 1, name: "two/ss.bin", size: 512),
        ],
        materializationPolicy: FirmwareArchiveRules.materializationPolicy
      )
    )
    XCTAssertThrowsError(
      try FirmwareArchiveRules.validate(
        entries: [
          facts(
            index: 0,
            name: "firmware/\(String(repeating: "é", count: 63)).bin",
            size: 512
          ),
        ],
        materializationPolicy: FirmwareArchiveRules.materializationPolicy
      )
    )
  }

  func testRejectsInvalidPolicyEncryptedLinkNestedArchiveAndOversize() {
    let regular = [
      facts(index: 0, name: "firmware/main.bin", size: 1_024),
    ]
    XCTAssertThrowsError(
      try FirmwareArchiveRules.validate(
        entries: regular,
        materializationPolicy: "v1-allow-list"
      )
    )
    XCTAssertThrowsError(
      try FirmwareArchiveRules.validate(
        entries: [
          facts(
            index: 0,
            name: "firmware/main.bin",
            size: 1_024,
            flags: 1
          ),
        ],
        materializationPolicy: FirmwareArchiveRules.materializationPolicy
      )
    )
    XCTAssertThrowsError(
      try FirmwareArchiveRules.validate(
        entries: [
          facts(
            index: 0,
            name: "firmware/main.bin",
            size: 1_024,
            externalAttributes: UInt32(0o120777) << 16
          ),
        ],
        materializationPolicy: FirmwareArchiveRules.materializationPolicy
      )
    )
    XCTAssertThrowsError(
      try FirmwareArchiveRules.validate(
        entries: [
          facts(
            index: 0,
            name: "__MACOSX/update.zip",
            size: 1_024
          ),
        ],
        materializationPolicy: FirmwareArchiveRules.materializationPolicy
      )
    )
    XCTAssertThrowsError(
      try FirmwareArchiveRules.validate(
        entries: [
          facts(
            index: 0,
            name: "firmware/main.bin",
            size: FirmwareArchiveRules.maximumEntryUncompressedBytes + 1
          ),
        ],
        materializationPolicy: FirmwareArchiveRules.materializationPolicy
      )
    )
  }

  func testRejectsZip64Entries() {
    XCTAssertThrowsError(
      try FirmwareArchiveRules.validate(
        entries: [
          facts(
            index: 0,
            name: "firmware/main.bin",
            size: 1_024,
            usesZip64: true
          ),
        ],
        materializationPolicy: FirmwareArchiveRules.materializationPolicy
      )
    )
  }

  func testCountsEveryCentralRecordAndCapsTotalExpansion() {
    let tooMany = (0...FirmwareArchiveRules.maximumEntryCount).map {
      directoryFacts(index: $0, name: "directory-\($0)/")
    }
    XCTAssertThrowsError(
      try FirmwareArchiveRules.validate(
        entries: tooMany,
        materializationPolicy: FirmwareArchiveRules.materializationPolicy
      )
    )

    let tooLarge = (0..<5).map {
      facts(
        index: $0,
        name: "firmware/file-\($0).bin",
        size: FirmwareArchiveRules.maximumEntryUncompressedBytes
      )
    }
    XCTAssertThrowsError(
      try FirmwareArchiveRules.validate(
        entries: tooLarge,
        materializationPolicy: FirmwareArchiveRules.materializationPolicy
      )
    )
  }

  func testAcceptsProScaleArchiveEntryCount() throws {
    let entries = (0..<1_201).map {
      facts(
        index: $0,
        name: "resources/resource-\($0).bin",
        size: 1
      )
    }
    let validated = try FirmwareArchiveRules.validate(
      entries: entries,
      materializationPolicy: FirmwareArchiveRules.materializationPolicy
    )

    XCTAssertEqual(validated.count, 1_201)
  }

  func testChildTaskIdentityMatchesAppContract() {
    XCTAssertEqual(
      FirmwareArchiveRules.childTaskId(
        parentArtifactId: "firmware-archive",
        sha256: String(repeating: "A", count: 64),
        size: 1_024
      ),
      "fw-child-7211b23ee276863b96ee8461fb17f28d146e8c40ba49376869d853904f4cf98a"
    )
  }

  private func facts(
    index: Int,
    name: String,
    size: Int64,
    flags: Int = 0,
    externalAttributes: UInt32 = UInt32(0o100644) << 16,
    usesZip64: Bool = false
  ) -> FirmwareArchiveEntryFacts {
    return FirmwareArchiveEntryFacts(
      index: index,
      name: name,
      compressedSize: max(1, size / 2),
      uncompressedSize: size,
      compressionMethod: 8,
      generalPurposeFlags: flags,
      versionMadeBy: 3 << 8,
      externalAttributes: externalAttributes,
      diskNumberStart: 0,
      usesZip64: usesZip64
    )
  }

  private func directoryFacts(
    index: Int,
    name: String
  ) -> FirmwareArchiveEntryFacts {
    return FirmwareArchiveEntryFacts(
      index: index,
      name: name,
      compressedSize: 0,
      uncompressedSize: 0,
      compressionMethod: 0,
      generalPurposeFlags: 0,
      versionMadeBy: 3 << 8,
      externalAttributes: UInt32(0o040755) << 16,
      diskNumberStart: 0
    )
  }
}
