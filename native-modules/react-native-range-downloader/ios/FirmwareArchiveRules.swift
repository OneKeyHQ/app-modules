import Foundation

struct FirmwareArchiveEntryFacts: Equatable {
  let index: Int
  let name: String
  let compressedSize: Int64
  let uncompressedSize: Int64
  let compressionMethod: Int
  let generalPurposeFlags: Int
  let versionMadeBy: Int
  let externalAttributes: UInt32
  let diskNumberStart: Int
  let usesZip64: Bool

  init(
    index: Int,
    name: String,
    compressedSize: Int64,
    uncompressedSize: Int64,
    compressionMethod: Int,
    generalPurposeFlags: Int,
    versionMadeBy: Int,
    externalAttributes: UInt32,
    diskNumberStart: Int,
    usesZip64: Bool = false
  ) {
    self.index = index
    self.name = name
    self.compressedSize = compressedSize
    self.uncompressedSize = uncompressedSize
    self.compressionMethod = compressionMethod
    self.generalPurposeFlags = generalPurposeFlags
    self.versionMadeBy = versionMadeBy
    self.externalAttributes = externalAttributes
    self.diskNumberStart = diskNumberStart
    self.usesZip64 = usesZip64
  }
}

enum FirmwareArchiveEntryDisposition: Equatable {
  case materialize
  case ignoreDirectory
  case ignoreMetadata
}

struct FirmwareArchiveValidatedEntry: Equatable {
  let facts: FirmwareArchiveEntryFacts
  let entryId: String
  let logicalName: String
  let disposition: FirmwareArchiveEntryDisposition
}

enum FirmwareArchiveRuleError: Error, Equatable, LocalizedError {
  case invalidPolicy
  case invalidEntry
  case unsupportedEntry
  case limitExceeded

  var code: String {
    switch self {
    case .invalidPolicy:
      return "ARTIFACT_ARCHIVE_INVALID_POLICY"
    case .invalidEntry:
      return "ARTIFACT_ARCHIVE_INVALID_ENTRY"
    case .unsupportedEntry:
      return "ARTIFACT_ARCHIVE_UNSUPPORTED_ENTRY"
    case .limitExceeded:
      return "ARTIFACT_ARCHIVE_LIMIT_EXCEEDED"
    }
  }

  var errorDescription: String? {
    return code
  }
}

enum FirmwareArchiveRules {
  static let materializationPolicy = "v3-resource-files-v1"
  static let maximumEntryCount = 4_096
  static let maximumNameBytes = 1_024
  static let maximumLogicalNameBytes = 128
  static let maximumEntryUncompressedBytes: Int64 = 128 * 1024 * 1024
  static let maximumTotalUncompressedBytes: Int64 = 512 * 1024 * 1024
  static let maximumCompressionRatio: Int64 = 1_000
  static let extractionBufferBytes = 64 * 1024

  private enum EntryKind {
    case file
    case directory
  }

  private static let encryptedFlag = 1 << 0
  private static let unixHostSystems = Set([3, 19])
  private static let unixFileTypeMask: UInt32 = 0o170000
  private static let unixRegularFile: UInt32 = 0o100000
  private static let unixDirectory: UInt32 = 0o040000
  private static let dosDirectoryFlag: UInt32 = 0x10
  private static let nestedArchiveExtensions = Set([
    "7z", "bz2", "cab", "gz", "rar", "tar", "tgz", "xz", "zip",
  ])
  private static let windowsDeviceNames = Set([
    "AUX", "CON", "NUL", "PRN",
    "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
    "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9",
  ])

  static func validate(
    entries: [FirmwareArchiveEntryFacts],
    materializationPolicy requestedPolicy: String
  ) throws -> [FirmwareArchiveValidatedEntry] {
    guard requestedPolicy == materializationPolicy else {
      throw FirmwareArchiveRuleError.invalidPolicy
    }
    guard !entries.isEmpty else {
      throw FirmwareArchiveRuleError.invalidEntry
    }
    guard entries.count <= maximumEntryCount else {
      throw FirmwareArchiveRuleError.limitExceeded
    }

    var seenIndexes = Set<Int>()
    var seenNames = Set<String>()
    var pathCollisionKeys = Set<String>()
    var logicalNameCollisionKeys = Set<String>()
    var totalDeclared: Int64 = 0
    var materializedCount = 0
    var validated: [FirmwareArchiveValidatedEntry] = []
    validated.reserveCapacity(entries.count)

    for facts in entries {
      let kind = try entryKind(facts)
      let entryId = try canonicalEntryId(
        facts.name,
        isDirectory: kind == .directory
      )
      guard let logicalName = entryId.split(separator: "/").last.map(
        String.init
      ) else {
        throw FirmwareArchiveRuleError.invalidEntry
      }
      guard facts.index >= 0,
            seenIndexes.insert(facts.index).inserted,
            seenNames.insert(facts.name).inserted,
            pathCollisionKeys.insert(
              portableCollisionKey(entryId)
            ).inserted else {
        throw FirmwareArchiveRuleError.invalidEntry
      }
      guard facts.diskNumberStart == 0,
            !facts.usesZip64,
            facts.generalPurposeFlags & encryptedFlag == 0,
            facts.compressionMethod == 0 || facts.compressionMethod == 8 else {
        throw FirmwareArchiveRuleError.unsupportedEntry
      }

      let disposition: FirmwareArchiveEntryDisposition
      switch kind {
      case .directory:
        guard facts.uncompressedSize == 0,
              facts.compressedSize >= 0,
              (facts.compressionMethod == 0 &&
                facts.compressedSize == 0) ||
              (facts.compressionMethod == 8 &&
                facts.compressedSize <= 64) else {
          throw FirmwareArchiveRuleError.invalidEntry
        }
        disposition = .ignoreDirectory
      case .file:
        guard !isNestedArchive(entryId) else {
          throw FirmwareArchiveRuleError.unsupportedEntry
        }
        guard facts.compressedSize >= 0,
              facts.uncompressedSize > 0 else {
          throw FirmwareArchiveRuleError.invalidEntry
        }
        guard facts.uncompressedSize <= maximumEntryUncompressedBytes else {
          throw FirmwareArchiveRuleError.limitExceeded
        }
        try validateCompressionRatio(
          compressedSize: facts.compressedSize,
          uncompressedSize: facts.uncompressedSize
        )
        totalDeclared = try checkedAdd(
          totalDeclared,
          facts.uncompressedSize,
          limit: maximumTotalUncompressedBytes
        )
        if isIgnoredMetadata(entryId) {
          disposition = .ignoreMetadata
        } else {
          guard logicalName.utf8.count <= maximumLogicalNameBytes else {
            throw FirmwareArchiveRuleError.invalidEntry
          }
          guard logicalNameCollisionKeys.insert(
            portableCollisionKey(logicalName)
          ).inserted else {
            throw FirmwareArchiveRuleError.invalidEntry
          }
          disposition = .materialize
          materializedCount += 1
        }
      }

      validated.append(
        FirmwareArchiveValidatedEntry(
          facts: facts,
          entryId: entryId,
          logicalName: logicalName,
          disposition: disposition
        )
      )
    }

    guard materializedCount > 0 else {
      throw FirmwareArchiveRuleError.invalidEntry
    }
    return validated
  }

  static func entriesRequiringIntegrityValidation(
    _ entries: [FirmwareArchiveValidatedEntry]
  ) -> [FirmwareArchiveValidatedEntry] {
    return entries.sorted { $0.facts.index < $1.facts.index }
  }

  static func isCanonicalPortableName(_ name: String) throws -> Bool {
    let normalizedName = name.precomposedStringWithCanonicalMapping
    guard !name.isEmpty,
          name.utf8.count <= maximumNameBytes,
          name.utf8.elementsEqual(normalizedName.utf8),
          !name.hasPrefix("/"),
          !name.hasPrefix("\\"),
          !name.contains("\\"),
          !name.unicodeScalars.contains(where: {
            $0.value == 0 || $0.value < 0x20 || $0.value == 0x7f
          }),
          !name.contains(":") else {
      return false
    }

    let components = name.split(
      separator: "/",
      omittingEmptySubsequences: false
    )
    return !components.isEmpty && components.allSatisfy { component in
      !component.isEmpty &&
        component != "." &&
        component != ".." &&
        !component.hasSuffix(".") &&
        !component.hasSuffix(" ") &&
        !isWindowsDeviceName(String(component))
    }
  }

  static func childTaskId(
    parentArtifactId: String,
    sha256: String,
    size: Int64
  ) -> String {
    let identity = "\(parentArtifactId)\0\(sha256.lowercased())\0\(size)"
    return "fw-child-\(StoredFirmwareArtifactMetadata.sha256(identity))"
  }

  private static func canonicalEntryId(
    _ name: String,
    isDirectory: Bool
  ) throws -> String {
    guard name.utf8.count <= maximumNameBytes else {
      throw FirmwareArchiveRuleError.invalidEntry
    }
    let logicalName: String
    if isDirectory {
      guard name.hasSuffix("/"), !name.hasSuffix("//") else {
        throw FirmwareArchiveRuleError.invalidEntry
      }
      logicalName = String(name.dropLast())
    } else {
      guard !name.hasSuffix("/") else {
        throw FirmwareArchiveRuleError.invalidEntry
      }
      logicalName = name
    }
    guard try isCanonicalPortableName(logicalName) else {
      throw FirmwareArchiveRuleError.invalidEntry
    }
    return logicalName
  }

  private static func entryKind(
    _ facts: FirmwareArchiveEntryFacts
  ) throws -> EntryKind {
    let hostSystem = (facts.versionMadeBy >> 8) & 0xff
    let hasDirectoryName = facts.name.hasSuffix("/")
    let hasDOSDirectoryFlag =
      facts.externalAttributes & dosDirectoryFlag != 0

    if unixHostSystems.contains(hostSystem) {
      let mode = (facts.externalAttributes >> 16) & unixFileTypeMask
      switch mode {
      case unixDirectory:
        guard hasDirectoryName else {
          throw FirmwareArchiveRuleError.invalidEntry
        }
        return .directory
      case unixRegularFile:
        guard !hasDirectoryName, !hasDOSDirectoryFlag else {
          throw FirmwareArchiveRuleError.invalidEntry
        }
        return .file
      case 0:
        return hasDirectoryName || hasDOSDirectoryFlag ? .directory : .file
      default:
        throw FirmwareArchiveRuleError.unsupportedEntry
      }
    }
    return hasDirectoryName || hasDOSDirectoryFlag ? .directory : .file
  }

  private static func isWindowsDeviceName(_ component: String) -> Bool {
    let basename = component.split(separator: ".", maxSplits: 1).first ?? ""
    return windowsDeviceNames.contains(String(basename).uppercased())
  }

  private static func portableCollisionKey(_ name: String) -> String {
    return name
      .precomposedStringWithCanonicalMapping
      .uppercased()
      .lowercased()
      .precomposedStringWithCanonicalMapping
  }

  private static func isIgnoredMetadata(_ name: String) -> Bool {
    return name.split(separator: "/").contains {
      portableCollisionKey(String($0)) == "__macosx"
    }
  }

  private static func isNestedArchive(_ name: String) -> Bool {
    guard let basename = name.split(separator: "/").last,
          let dot = basename.lastIndex(of: "."),
          dot != basename.startIndex,
          dot < basename.index(before: basename.endIndex) else {
      return false
    }
    let extensionName = basename[basename.index(after: dot)...]
    return nestedArchiveExtensions.contains(extensionName.lowercased())
  }

  private static func validateCompressionRatio(
    compressedSize: Int64,
    uncompressedSize: Int64
  ) throws {
    guard compressedSize > 0 else {
      throw FirmwareArchiveRuleError.limitExceeded
    }
    if compressedSize <= Int64.max / maximumCompressionRatio,
       uncompressedSize > compressedSize * maximumCompressionRatio {
      throw FirmwareArchiveRuleError.limitExceeded
    }
  }

  private static func checkedAdd(
    _ left: Int64,
    _ right: Int64,
    limit: Int64
  ) throws -> Int64 {
    let (sum, overflow) = left.addingReportingOverflow(right)
    guard !overflow, sum <= limit else {
      throw FirmwareArchiveRuleError.limitExceeded
    }
    return sum
  }
}
