import Foundation

enum BundleDiffReconstructionError: Error, Equatable {
    case securityRejected(String)
    case integrityFailed(String)
    case fallbackRequired(String)
}

struct BundleDiffManifest: Equatable {
    let schemaVersion: Int
    let appType: String
    let appVersion: String
    let baseAppVersion: String
    let baseMetadataSha256: String
    let targetBundleVersion: String
    let targetMetadataSha256: String
    let hashAlgorithm: String
    let changedFiles: [String: String]
    let reusedFiles: [String: String]
    let deletedFiles: [String]
}

enum BundleDiffReconstruction {
    static let expectedSchemaVersion = 1
    static let expectedHashAlgorithm = "sha256"
    static let maxBundleEntries = 4096
    static let maxBundleUncompressedBytes: UInt64 = 512 * 1024 * 1024
    static let maxControlJsonBytes: UInt64 = 16 * 1024 * 1024
    static let stagingRootDirName = ".diff-staging"
    static let diffDownloadDirName = ".diff"
    static let diffZipExtension = ".diff.zip"
    static let diffMetaExtension = ".diff.zip.meta.json"

    // Shared by all native module instances in this process.
    private static let stagingLock = NSLock()
    private static var liveStagingDirectories = Set<String>()

    static func retainStagingDirectory(_ path: String) {
        stagingLock.lock()
        defer { stagingLock.unlock() }
        liveStagingDirectories.insert(path)
    }

    static func releaseStagingDirectory(_ path: String) {
        stagingLock.lock()
        defer { stagingLock.unlock() }
        try? FileManager.default.removeItem(atPath: path)
        liveStagingDirectories.remove(path)
    }

    static func sweepStagingDirectory(_ root: String) {
        stagingLock.lock()
        defer { stagingLock.unlock() }
        let fm = FileManager.default
        guard let attributes = try? fm.attributesOfItem(atPath: root),
              attributes[.type] as? FileAttributeType == .typeDirectory,
              let entries = try? fm.contentsOfDirectory(atPath: root) else { return }
        for entry in entries {
            let path = (root as NSString).appendingPathComponent(entry)
            if !liveStagingDirectories.contains(path) {
                try? fm.removeItem(atPath: path)
            }
        }
    }

    struct DiffArchiveDescriptor: Equatable {
        let appVersion: String
        let bundleVersion: String
        let downloadUrl: String
        let fileSize: Double
        let sha256: String
        let diffSha256: String
        let signature: String?
        let fallbackRequired: Bool

        init(
            appVersion: String,
            bundleVersion: String,
            downloadUrl: String,
            fileSize: Double,
            sha256: String,
            diffSha256: String,
            signature: String?,
            fallbackRequired: Bool = false
        ) {
            self.appVersion = appVersion
            self.bundleVersion = bundleVersion
            self.downloadUrl = downloadUrl
            self.fileSize = fileSize
            self.sha256 = sha256
            self.diffSha256 = diffSha256
            self.signature = signature
            self.fallbackRequired = fallbackRequired
        }
    }

    static func isRetryableDiffDownloadError(code: String, httpStatus: Int? = nil) -> Bool {
        switch code {
        case "NETWORK_ERROR", "TIMEOUT":
            return true
        case "HTTP_ERROR":
            return isRetryableHTTPStatus(httpStatus)
        default:
            return false
        }
    }

    static func isRetryableHTTPStatus(_ status: Int?) -> Bool {
        switch status {
        case 408, 416, 429:
            return true
        case 501, 505:
            return false
        case let status?:
            return (500...599).contains(status)
        case nil:
            return false
        }
    }

    struct SingleStreamResumeSnapshot {
        private(set) var rejected = false

        mutating func invalidateIfRejected(httpStatus: Int?, archivePath: String) throws -> Bool {
            guard httpStatus == 416 else { return false }
            rejected = true
            let resumePath = "\(archivePath).resume"
            let files = FileManager.default
            if files.fileExists(atPath: resumePath) {
                try files.removeItem(atPath: resumePath)
            }
            return true
        }
    }

    static func shouldContinueAfterRangeError(
        code: String, httpStatus: Int?, rangeIsTransient: Bool, hasRetryBudget: Bool
    ) -> Bool {
        guard isRetryableDiffDownloadError(code: code, httpStatus: httpStatus) else { return false }
        return code != "HTTP_ERROR" || (rangeIsTransient && hasRetryBudget)
    }

    static func rangeTaskId(bundleKey: String, isDiff: Bool) -> String {
        return isDiff ? "diff:\(bundleKey)" : "full:\(bundleKey)"
    }

    static func rangeTaskId(forFilePath filePath: String) -> String {
        let url = URL(fileURLWithPath: filePath)
        let fileName = url.lastPathComponent
        let parentName = url.deletingLastPathComponent().lastPathComponent
        if parentName == diffDownloadDirName && fileName.hasSuffix(diffZipExtension) {
            let bundleKey = String(fileName.dropLast(diffZipExtension.count))
            return rangeTaskId(bundleKey: bundleKey, isDiff: true)
        }
        if fileName.hasSuffix(".zip") {
            let bundleKey = String(fileName.dropLast(4))
            return rangeTaskId(bundleKey: bundleKey, isDiff: false)
        }
        return rangeTaskId(bundleKey: url.deletingPathExtension().lastPathComponent, isDiff: false)
    }

    static func descriptorPreservingVerifiedSignature(
        newDescriptor: DiffArchiveDescriptor,
        existingDescriptor: DiffArchiveDescriptor?
    ) -> DiffArchiveDescriptor {
        guard let existing = existingDescriptor,
              existing.appVersion == newDescriptor.appVersion,
              existing.bundleVersion == newDescriptor.bundleVersion,
              constantTimeEqualHex(existing.sha256, newDescriptor.sha256),
              constantTimeEqualHex(existing.diffSha256, newDescriptor.diffSha256) else {
            return newDescriptor
        }
        let mergedSignature: String?
        if let newSig = newDescriptor.signature, !newSig.isEmpty {
            mergedSignature = newSig
        } else if let oldSig = existing.signature, !oldSig.isEmpty {
            mergedSignature = oldSig
        } else {
            mergedSignature = nil
        }
        return DiffArchiveDescriptor(
            appVersion: newDescriptor.appVersion,
            bundleVersion: newDescriptor.bundleVersion,
            downloadUrl: newDescriptor.downloadUrl,
            fileSize: newDescriptor.fileSize > 0 ? newDescriptor.fileSize : existing.fileSize,
            sha256: newDescriptor.sha256,
            diffSha256: newDescriptor.diffSha256,
            signature: mergedSignature,
            fallbackRequired: newDescriptor.fallbackRequired || existing.fallbackRequired
        )
    }

    static func isHex64(_ value: String?) -> Bool {
        guard let value = value, value.count == 64 else { return false }
        return value.unicodeScalars.allSatisfy {
            ($0 >= "0" && $0 <= "9") || ($0 >= "a" && $0 <= "f") || ($0 >= "A" && $0 <= "F")
        }
    }

    static func normalizeSha256(_ value: String?) -> String? {
        guard let trimmed = value?.trimmingCharacters(in: .whitespacesAndNewlines),
              isHex64(trimmed) else {
            return nil
        }
        return trimmed.lowercased()
    }

    static func isSafeVersionComponent(_ version: String) -> Bool {
        let allowedChars = CharacterSet.alphanumerics.union(CharacterSet(charactersIn: ".-_"))
        guard !version.isEmpty,
              !version.hasPrefix("."),
              !version.hasSuffix("."),
              version.unicodeScalars.allSatisfy({ allowedChars.contains($0) }) else {
            return false
        }
        return true
    }

    @discardableResult
    static func writeDescriptor(_ descriptor: DiffArchiveDescriptor, toPath path: String) -> Bool {
        let fm = FileManager.default
        let parentDir = (path as NSString).deletingLastPathComponent
        if !parentDir.isEmpty && !fm.fileExists(atPath: parentDir) {
            try? fm.createDirectory(atPath: parentDir, withIntermediateDirectories: true)
        }
        var dict: [String: Any] = [
            "appVersion": descriptor.appVersion,
            "bundleVersion": descriptor.bundleVersion,
            "downloadUrl": descriptor.downloadUrl,
            "fileSize": descriptor.fileSize,
            "sha256": descriptor.sha256.lowercased(),
            "diffSha256": descriptor.diffSha256.lowercased(),
            "fallbackRequired": descriptor.fallbackRequired,
        ]
        if let sig = descriptor.signature, !sig.isEmpty {
            dict["signature"] = sig
        }
        do {
            let data = try JSONSerialization.data(withJSONObject: dict)
            try data.write(to: URL(fileURLWithPath: path), options: .atomic)
            return true
        } catch {
            return false
        }
    }

    static func readDescriptor(
        fromPath path: String,
        expectedAppVersion: String,
        expectedBundleVersion: String
    ) -> DiffArchiveDescriptor? {
        let fm = FileManager.default
        guard fm.fileExists(atPath: path),
              let attrs = try? fm.attributesOfItem(atPath: path),
              attrs[.type] as? FileAttributeType == .typeRegular,
              let fileSize = attrs[.size] as? UInt64,
              fileSize <= 256 * 1024,
              let data = fm.contents(atPath: path),
              let obj = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
            return nil
        }
        guard let appVersion = obj["appVersion"] as? String,
              let bundleVersion = obj["bundleVersion"] as? String,
              appVersion == expectedAppVersion,
              bundleVersion == expectedBundleVersion,
              let downloadUrl = obj["downloadUrl"] as? String,
              downloadUrl.hasPrefix("https://"),
              let sha256 = normalizeSha256(obj["sha256"] as? String),
              let diffSha256 = normalizeSha256(obj["diffSha256"] as? String) else {
            return nil
        }
        let sizeNum = (obj["fileSize"] as? NSNumber)?.doubleValue ?? 0
        let signature = obj["signature"] as? String
        let fallbackRequired = (obj["fallbackRequired"] as? Bool) ?? false
        return DiffArchiveDescriptor(
            appVersion: appVersion,
            bundleVersion: bundleVersion,
            downloadUrl: downloadUrl,
            fileSize: sizeNum,
            sha256: sha256,
            diffSha256: diffSha256,
            signature: (signature?.isEmpty == false) ? signature : nil,
            fallbackRequired: fallbackRequired
        )
    }

    struct ReleaseIdentity: Hashable {
        let appVersion: String
        let bundleVersion: String
    }

    static func targetMetadataSizeIfPresent(_ targetPath: String) throws -> UInt64? {
        let fm = FileManager.default
        let targetAttributes: [FileAttributeKey: Any]
        do {
            targetAttributes = try fm.attributesOfItem(atPath: targetPath)
        } catch let error as CocoaError where error.code == .fileReadNoSuchFile || error.code == .fileNoSuchFile {
            return nil
        }
        guard targetAttributes[.type] as? FileAttributeType == .typeDirectory else {
            throw BundleDiffReconstructionError.integrityFailed("bundle target is not a directory")
        }
        let metadataPath = (targetPath as NSString).appendingPathComponent("metadata.json")
        guard let attributes = try? fm.attributesOfItem(atPath: metadataPath),
              attributes[.type] as? FileAttributeType == .typeRegular,
              let size = attributes[.size] as? UInt64 else {
            throw BundleDiffReconstructionError.integrityFailed("bundle target has no regular metadata control file")
        }
        return size
    }

    struct VerifiedDiffRecord {
        let fullSha256: String
        let diffSha256: String
        let metadataSha256: String
    }

    static func matchesDownloadRecord(
        suppliedFullSha256: String,
        suppliedDiffSha256: String?,
        descriptor: DiffArchiveDescriptor
    ) -> Bool {
        guard !descriptor.fallbackRequired,
              let normalizedFull = normalizeSha256(suppliedFullSha256),
              constantTimeEqualHex(descriptor.sha256, normalizedFull) else { return false }
        guard let suppliedDiffSha256 else { return true }
        guard let normalizedDiff = normalizeSha256(suppliedDiffSha256) else { return false }
        return constantTimeEqualHex(descriptor.diffSha256, normalizedDiff)
    }

    static func resolveExpectedDiffSha256ForPackageVerify(
        suppliedFullSha256: String,
        suppliedDiffSha256: String?,
        descriptor: DiffArchiveDescriptor?
    ) -> String? {
        guard let descriptor, matchesDownloadRecord(
            suppliedFullSha256: suppliedFullSha256,
            suppliedDiffSha256: suppliedDiffSha256,
            descriptor: descriptor
        ) else { return nil }
        return descriptor.diffSha256
    }

    static func resolveTrustedMetadataSha256ForPackageVerify(
        suppliedFullSha256: String,
        suppliedDiffSha256: String? = nil,
        suppliedSignature: String?,
        inMemoryVerified: VerifiedDiffRecord?,
        descriptor: DiffArchiveDescriptor?,
        persistedAscSignature: String?,
        verifySignatureAndExtractMetadataSha256: (String) -> String?
    ) -> String? {
        guard let normalizedFull = normalizeSha256(suppliedFullSha256) else { return nil }
        if let descriptor, !matchesDownloadRecord(
            suppliedFullSha256: normalizedFull,
            suppliedDiffSha256: suppliedDiffSha256,
            descriptor: descriptor
        ) { return nil }

        if let inMemoryVerified,
           constantTimeEqualHex(inMemoryVerified.fullSha256, normalizedFull) {
            let expectedDiff = suppliedDiffSha256 ?? descriptor?.diffSha256
            if expectedDiff == nil || normalizeSha256(expectedDiff) == inMemoryVerified.diffSha256 {
                return normalizeSha256(inMemoryVerified.metadataSha256)
            }
        }

        guard let descriptor else { return nil }
        for signature in [suppliedSignature, descriptor.signature, persistedAscSignature] {
            guard let signature, !signature.isEmpty else { continue }
            if let extracted = normalizeSha256(verifySignatureAndExtractMetadataSha256(signature)) {
                return extracted
            }
        }
        return nil
    }

    static func resolveFallbackDownloadParams(
        expectedFullSha256: String,
        expectedDiffSha256: String?,
        explicitDownloadUrl: String?,
        explicitFileSize: Double?,
        inMemoryFullParams: (downloadUrl: String, fileSize: Double, sha256: String)?,
        descriptor: DiffArchiveDescriptor?
    ) -> (downloadUrl: String, fileSize: Double)? {
        guard let normFull = normalizeSha256(expectedFullSha256) else {
            return nil
        }
        let normDiff: String?
        if let rawDiff = expectedDiffSha256, !rawDiff.isEmpty {
            guard let validDiff = normalizeSha256(rawDiff) else { return nil }
            normDiff = validDiff
        } else {
            normDiff = nil
        }
        if let url = explicitDownloadUrl, url.hasPrefix("https://") {
            return (downloadUrl: url, fileSize: explicitFileSize ?? 0)
        }
        if let inMem = inMemoryFullParams,
           inMem.downloadUrl.hasPrefix("https://"),
           constantTimeEqualHex(inMem.sha256, normFull) {
            return (downloadUrl: inMem.downloadUrl, fileSize: inMem.fileSize)
        }
        if let desc = descriptor,
           desc.downloadUrl.hasPrefix("https://"),
           constantTimeEqualHex(desc.sha256, normFull),
           (normDiff == nil || constantTimeEqualHex(desc.diffSha256, normDiff!)) {
            return (downloadUrl: desc.downloadUrl, fileSize: desc.fileSize)
        }
        return nil
    }

    static func isUsableDiffParams(
        diffDownloadUrl: String?,
        diffSha256: String?,
        diffSignature: String?,
        isFallbackMarked: Bool
    ) -> Bool {
        guard !isFallbackMarked else { return false }
        guard let urlStr = diffDownloadUrl, !urlStr.isEmpty,
              urlStr.hasPrefix("https://"),
              let url = URL(string: urlStr),
              url.scheme?.lowercased() == "https",
              url.host?.isEmpty == false else {
            return false
        }
        guard isHex64(diffSha256) else { return false }
        guard let sig = diffSignature, !sig.isEmpty else { return false }
        return true
    }

    static func isSafeRelativeBundlePath(_ relPath: String) -> Bool {
        if relPath.isEmpty || relPath.hasPrefix("/") || relPath.contains("\\") || relPath.contains("\0") {
            return false
        }
        if relPath == "metadata.json" || relPath == "diff-manifest.json" || relPath == ".DS_Store" {
            return false
        }
        let components = relPath.split(separator: "/", omittingEmptySubsequences: false)
        if components.isEmpty { return false }
        for component in components {
            if component.isEmpty || component == "." || component == ".." {
                return false
            }
        }
        return true
    }

    static func constantTimeEqualHex(_ a: String, _ b: String) -> Bool {
        let lhs = Array(a.lowercased().utf8)
        let rhs = Array(b.lowercased().utf8)
        guard lhs.count == rhs.count else { return false }
        var diff: UInt8 = 0
        for i in 0..<lhs.count {
            diff |= lhs[i] ^ rhs[i]
        }
        return diff == 0
    }

    /// Maps an OTA metadata.json key to ordered built-in iOS Bundle.main candidate relative paths:
    /// 1. `main.jsbundle.hbc` -> `["main.jsbundle.hbc", "main.jsbundle"]` (Xcode renames HBC entry to main.jsbundle)
    /// 2. `assets/assets/<subpath>` -> `["assets/assets/<subpath>", "assets/<subpath>"]` (dist/assets moves assets/ dir, while standard RN bundle outputs single assets/<subpath>)
    /// 3. `assets/<subpath>` -> `["assets/<subpath>", "assets/assets/<subpath>", "<subpath>"]`
    /// 4. `web-embed/<subpath>` -> `["web-embed/<subpath>"]` (Xcode folder reference `OneKeyWallet/web-embed` in Bundle.main)
    static func builtinCandidateRelativePaths(for relativePath: String) -> [String] {
        guard isSafeRelativeBundlePath(relativePath) else { return [] }
        if relativePath == "main.jsbundle.hbc" {
            return ["main.jsbundle.hbc", "main.jsbundle"]
        }
        if relativePath.hasPrefix("assets/assets/") {
            let strippedOne = String(relativePath.dropFirst("assets/".count))
            let strippedTwo = String(relativePath.dropFirst("assets/assets/".count))
            var result = [relativePath]
            if isSafeRelativeBundlePath(strippedOne) { result.append(strippedOne) }
            if isSafeRelativeBundlePath(strippedTwo) { result.append(strippedTwo) }
            return result
        }
        if relativePath.hasPrefix("assets/") {
            let nested = "assets/" + relativePath
            let stripped = String(relativePath.dropFirst("assets/".count))
            var result = [relativePath]
            if isSafeRelativeBundlePath(nested) { result.append(nested) }
            if isSafeRelativeBundlePath(stripped) { result.append(stripped) }
            return result
        }
        return [relativePath]
    }

    static func parseAndValidateManifest(
        jsonData: Data,
        expectedAppType: String,
        expectedAppVersion: String,
        expectedBundleVersion: String
    ) throws -> BundleDiffManifest {
        let rawObj: Any
        do {
            rawObj = try JSONSerialization.jsonObject(with: jsonData)
        } catch {
            throw BundleDiffReconstructionError.fallbackRequired("diff-manifest.json is not valid JSON")
        }
        guard let dict = rawObj as? [String: Any] else {
            throw BundleDiffReconstructionError.fallbackRequired("diff-manifest.json root is not an object")
        }

        guard let schemaNum = dict["schemaVersion"] as? NSNumber,
              CFGetTypeID(schemaNum) != CFBooleanGetTypeID(),
              schemaNum.intValue == expectedSchemaVersion,
              schemaNum.doubleValue == Double(expectedSchemaVersion) else {
            throw BundleDiffReconstructionError.fallbackRequired("unsupported diff-manifest schemaVersion")
        }

        guard let appType = dict["appType"] as? String, appType == expectedAppType else {
            throw BundleDiffReconstructionError.integrityFailed("diff-manifest appType mismatch")
        }
        guard let appVersion = dict["appVersion"] as? String, appVersion == expectedAppVersion else {
            throw BundleDiffReconstructionError.integrityFailed("diff-manifest appVersion mismatch")
        }
        guard let targetBundleVersion = dict["targetBundleVersion"] as? String,
              targetBundleVersion == expectedBundleVersion else {
            throw BundleDiffReconstructionError.integrityFailed("diff-manifest targetBundleVersion mismatch")
        }
        guard let hashAlgorithm = dict["hashAlgorithm"] as? String,
              hashAlgorithm == expectedHashAlgorithm else {
            throw BundleDiffReconstructionError.fallbackRequired("unsupported diff-manifest hashAlgorithm")
        }
        guard let targetMetadataSha256 = dict["targetMetadataSha256"] as? String,
              isHex64(targetMetadataSha256) else {
            throw BundleDiffReconstructionError.integrityFailed("invalid diff-manifest targetMetadataSha256")
        }

        let baseAppVersion = dict["baseAppVersion"] as? String ?? ""
        let baseMetadataSha256 = (dict["baseMetadataSha256"] as? String ?? "").lowercased()

        let changedFiles = try parseHashMap(dict["changedFiles"], fieldName: "changedFiles")
        let reusedFiles = try parseHashMap(dict["reusedFiles"], fieldName: "reusedFiles")

        for relPath in changedFiles.keys where reusedFiles[relPath] != nil {
            throw BundleDiffReconstructionError.fallbackRequired("overlapping path in changedFiles and reusedFiles: \(relPath)")
        }

        var deletedFiles: [String] = []
        if let rawDeleted = dict["deletedFiles"] {
            guard let arr = rawDeleted as? [String] else {
                throw BundleDiffReconstructionError.fallbackRequired("invalid deletedFiles array")
            }
            for item in arr {
                guard isSafeRelativeBundlePath(item) else {
                    throw BundleDiffReconstructionError.securityRejected("unsafe path in deletedFiles: \(item)")
                }
            }
            deletedFiles = arr
        }

        return BundleDiffManifest(
            schemaVersion: expectedSchemaVersion,
            appType: appType,
            appVersion: appVersion,
            baseAppVersion: baseAppVersion,
            baseMetadataSha256: baseMetadataSha256,
            targetBundleVersion: targetBundleVersion,
            targetMetadataSha256: targetMetadataSha256.lowercased(),
            hashAlgorithm: hashAlgorithm,
            changedFiles: changedFiles,
            reusedFiles: reusedFiles,
            deletedFiles: deletedFiles
        )
    }

    private static func parseHashMap(_ rawValue: Any?, fieldName: String) throws -> [String: String] {
        guard let rawMap = rawValue as? [String: Any] else {
            throw BundleDiffReconstructionError.fallbackRequired("missing or invalid \(fieldName)")
        }
        var parsed: [String: String] = [:]
        for (relPath, rawHash) in rawMap {
            guard isSafeRelativeBundlePath(relPath) else {
                throw BundleDiffReconstructionError.securityRejected("unsafe relative path in \(fieldName): \(relPath)")
            }
            guard let hash = rawHash as? String, isHex64(hash) else {
                throw BundleDiffReconstructionError.fallbackRequired("invalid sha256 in \(fieldName) for \(relPath)")
            }
            parsed[relPath] = hash.lowercased()
        }
        return parsed
    }

    /// Resolves a relative file path inside `baseDir`, returning nil if any path
    /// component is a symlink or escapes `baseDir`.
    static func confinedExistingFilePath(baseDir: String, relativePath: String) -> String? {
        guard isSafeRelativeBundlePath(relativePath) else { return nil }
        let fm = FileManager.default
        let resolvedBase = (baseDir as NSString).resolvingSymlinksInPath
        let fullPath = (baseDir as NSString).appendingPathComponent(relativePath)
        // Reject symlinks on the target file itself or any intermediate directory inside baseDir.
        var current = baseDir
        for component in relativePath.split(separator: "/") {
            current = (current as NSString).appendingPathComponent(String(component))
            if let attrs = try? fm.attributesOfItem(atPath: current),
               attrs[.type] as? FileAttributeType == .typeSymbolicLink {
                return nil
            }
        }
        var isDir: ObjCBool = false
        guard fm.fileExists(atPath: fullPath, isDirectory: &isDir), !isDir.boolValue else {
            return nil
        }
        let resolvedFull = (fullPath as NSString).resolvingSymlinksInPath
        guard resolvedFull.hasPrefix(resolvedBase + "/") else {
            return nil
        }
        return fullPath
    }

    static func confinedDestinationFilePath(baseDir: String, relativePath: String) -> String? {
        guard isSafeRelativeBundlePath(relativePath) else { return nil }
        let fm = FileManager.default
        let resolvedBase = (baseDir as NSString).resolvingSymlinksInPath
        var current = baseDir
        let components = relativePath.split(separator: "/")
        for (idx, component) in components.enumerated() {
            current = (current as NSString).appendingPathComponent(String(component))
            if fm.fileExists(atPath: current),
               let attrs = try? fm.attributesOfItem(atPath: current),
               attrs[.type] as? FileAttributeType == .typeSymbolicLink {
                return nil
            }
            if idx < components.count - 1 {
                var isDir: ObjCBool = false
                if fm.fileExists(atPath: current, isDirectory: &isDir), !isDir.boolValue {
                    return nil
                }
            }
        }
        let fullPath = (baseDir as NSString).appendingPathComponent(relativePath)
        let resolvedFull = (fullPath as NSString).resolvingSymlinksInPath
        guard resolvedFull.hasPrefix(resolvedBase + "/") else {
            return nil
        }
        return fullPath
    }

    static func validateManifestAgainstTargetMetadata(
        manifest: BundleDiffManifest,
        targetFileEntries: [String: String],
        maxEntries: Int = maxBundleEntries
    ) throws {
        let totalFiles = manifest.changedFiles.count + manifest.reusedFiles.count
        if totalFiles > maxEntries || targetFileEntries.count > maxEntries {
            throw BundleDiffReconstructionError.securityRejected(
                "bundle entry count exceeds maximum allowed (\(maxEntries))"
            )
        }
        if totalFiles != targetFileEntries.count {
            throw BundleDiffReconstructionError.fallbackRequired(
                "diff-manifest file count (\(totalFiles)) != metadata.json file count (\(targetFileEntries.count))"
            )
        }
        for (relPath, expectedHash) in targetFileEntries {
            guard isSafeRelativeBundlePath(relPath) else {
                throw BundleDiffReconstructionError.securityRejected("unsafe relative path in metadata.json: \(relPath)")
            }
            guard isHex64(expectedHash) else {
                throw BundleDiffReconstructionError.fallbackRequired("invalid sha256 in metadata.json for \(relPath)")
            }
            let manifestHash = manifest.changedFiles[relPath] ?? manifest.reusedFiles[relPath]
            guard let manifestHash, constantTimeEqualHex(manifestHash, expectedHash) else {
                throw BundleDiffReconstructionError.fallbackRequired("diff-manifest partition mismatch for \(relPath)")
            }
        }
        for deletedPath in manifest.deletedFiles {
            if targetFileEntries[deletedPath] != nil ||
               manifest.changedFiles[deletedPath] != nil ||
               manifest.reusedFiles[deletedPath] != nil {
                throw BundleDiffReconstructionError.fallbackRequired("deletedFiles entry present in target bundle: \(deletedPath)")
            }
        }
    }

    static func reconstruct(
        stagingDir: String,
        manifest: BundleDiffManifest,
        candidateBundleDirs: [String],
        sha256OfFile: (String) -> String?,
        builtinCopy: (_ relativePath: String, _ destinationPath: String, _ expectedHash: String) -> Bool,
        maxEntries: Int = maxBundleEntries,
        maxTotalBytes: UInt64 = maxBundleUncompressedBytes
    ) throws {
        let fm = FileManager.default
        let totalDeclaredEntries = manifest.changedFiles.count + manifest.reusedFiles.count
        if totalDeclaredEntries > maxEntries {
            throw BundleDiffReconstructionError.securityRejected(
                "reconstructed entry count exceeds maximum allowed (\(maxEntries))"
            )
        }

        var cumulativeEntries = 0
        var cumulativeBytes: UInt64 = 0

        let metadataPath = (stagingDir as NSString).appendingPathComponent("metadata.json")
        if fm.fileExists(atPath: metadataPath),
           let attrs = try? fm.attributesOfItem(atPath: metadataPath),
           let metaSize = attrs[.size] as? UInt64 {
            cumulativeEntries += 1
            if cumulativeEntries > maxEntries {
                throw BundleDiffReconstructionError.securityRejected(
                    "reconstructed entry count exceeds maximum allowed (\(maxEntries))"
                )
            }
            let (nextBytes, overflow) = cumulativeBytes.addingReportingOverflow(metaSize)
            if overflow || nextBytes > maxTotalBytes {
                throw BundleDiffReconstructionError.securityRejected(
                    "reconstructed bundle size exceeds maximum allowed (\(maxTotalBytes))"
                )
            }
            cumulativeBytes = nextBytes
        }

        // 1. Verify every changedFile exists in stagingDir and matches expected sha256.
        for (relPath, expectedHash) in manifest.changedFiles {
            cumulativeEntries += 1
            if cumulativeEntries > maxEntries {
                throw BundleDiffReconstructionError.securityRejected(
                    "reconstructed entry count exceeds maximum allowed (\(maxEntries))"
                )
            }
            guard let stagedPath = confinedExistingFilePath(baseDir: stagingDir, relativePath: relPath) else {
                throw BundleDiffReconstructionError.fallbackRequired("changed file missing or unsafe: \(relPath)")
            }
            if let attrs = try? fm.attributesOfItem(atPath: stagedPath),
               let fileSize = attrs[.size] as? UInt64 {
                let (nextBytes, overflow) = cumulativeBytes.addingReportingOverflow(fileSize)
                if overflow || nextBytes > maxTotalBytes {
                    throw BundleDiffReconstructionError.securityRejected(
                        "reconstructed bundle size exceeds maximum allowed (\(maxTotalBytes))"
                    )
                }
                cumulativeBytes = nextBytes
            }
            guard let actualHash = sha256OfFile(stagedPath),
                  constantTimeEqualHex(actualHash, expectedHash) else {
                throw BundleDiffReconstructionError.fallbackRequired("changed file sha256 mismatch: \(relPath)")
            }
        }

        // 2. Reconstruct every reusedFile from local OTA dirs or built-in app bundle.
        for (relPath, expectedHash) in manifest.reusedFiles {
            cumulativeEntries += 1
            if cumulativeEntries > maxEntries {
                throw BundleDiffReconstructionError.securityRejected(
                    "reconstructed entry count exceeds maximum allowed (\(maxEntries))"
                )
            }
            guard let destPath = confinedDestinationFilePath(baseDir: stagingDir, relativePath: relPath) else {
                throw BundleDiffReconstructionError.securityRejected("unsafe destination path for reused file: \(relPath)")
            }
            if fm.fileExists(atPath: destPath) {
                try? fm.removeItem(atPath: destPath)
            }
            let destParent = (destPath as NSString).deletingLastPathComponent
            do {
                try fm.createDirectory(atPath: destParent, withIntermediateDirectories: true)
            } catch {
                throw BundleDiffReconstructionError.fallbackRequired("failed to create staging dir for \(relPath)")
            }

            var copied = false
            for candidateDir in candidateBundleDirs {
                guard let sourcePath = confinedExistingFilePath(baseDir: candidateDir, relativePath: relPath) else {
                    continue
                }
                guard let sourceHash = sha256OfFile(sourcePath),
                      constantTimeEqualHex(sourceHash, expectedHash) else {
                    continue
                }
                if let attrs = try? fm.attributesOfItem(atPath: sourcePath),
                   let sourceSize = attrs[.size] as? UInt64 {
                    let (projectedBytes, overflow) = cumulativeBytes.addingReportingOverflow(sourceSize)
                    if overflow || projectedBytes > maxTotalBytes {
                        throw BundleDiffReconstructionError.securityRejected(
                            "reconstructed bundle size exceeds maximum allowed (\(maxTotalBytes))"
                        )
                    }
                }
                do {
                    try fm.copyItem(atPath: sourcePath, toPath: destPath)
                    if let copiedHash = sha256OfFile(destPath),
                       constantTimeEqualHex(copiedHash, expectedHash) {
                        copied = true
                        break
                    }
                    try? fm.removeItem(atPath: destPath)
                } catch {
                    try? fm.removeItem(atPath: destPath)
                }
            }

            if !copied {
                if builtinCopy(relPath, destPath, expectedHash),
                   let copiedHash = sha256OfFile(destPath),
                   constantTimeEqualHex(copiedHash, expectedHash) {
                    copied = true
                } else {
                    try? fm.removeItem(atPath: destPath)
                }
            }

            if !copied {
                throw BundleDiffReconstructionError.fallbackRequired("reused file unavailable or hash mismatch: \(relPath)")
            }

            if let attrs = try? fm.attributesOfItem(atPath: destPath),
               let copiedSize = attrs[.size] as? UInt64 {
                let (nextBytes, overflow) = cumulativeBytes.addingReportingOverflow(copiedSize)
                if overflow || nextBytes > maxTotalBytes {
                    try? fm.removeItem(atPath: destPath)
                    throw BundleDiffReconstructionError.securityRejected(
                        "reconstructed bundle size exceeds maximum allowed (\(maxTotalBytes))"
                    )
                }
                cumulativeBytes = nextBytes
            }
        }
    }
}
