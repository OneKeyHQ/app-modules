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

    static func isHex64(_ value: String?) -> Bool {
        guard let value = value, value.count == 64 else { return false }
        return value.unicodeScalars.allSatisfy {
            ($0 >= "0" && $0 <= "9") || ($0 >= "a" && $0 <= "f") || ($0 >= "A" && $0 <= "F")
        }
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

    static func reconstruct(
        stagingDir: String,
        manifest: BundleDiffManifest,
        candidateBundleDirs: [String],
        sha256OfFile: (String) -> String?,
        builtinCopy: (_ relativePath: String, _ destinationPath: String, _ expectedHash: String) -> Bool
    ) throws {
        let fm = FileManager.default

        // 1. Verify every changedFile exists in stagingDir and matches expected sha256.
        for (relPath, expectedHash) in manifest.changedFiles {
            guard let stagedPath = confinedExistingFilePath(baseDir: stagingDir, relativePath: relPath) else {
                throw BundleDiffReconstructionError.fallbackRequired("changed file missing or unsafe: \(relPath)")
            }
            guard let actualHash = sha256OfFile(stagedPath),
                  constantTimeEqualHex(actualHash, expectedHash) else {
                throw BundleDiffReconstructionError.fallbackRequired("changed file sha256 mismatch: \(relPath)")
            }
        }

        // 2. Reconstruct every reusedFile from local OTA dirs or built-in app bundle.
        for (relPath, expectedHash) in manifest.reusedFiles {
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
        }
    }
}
