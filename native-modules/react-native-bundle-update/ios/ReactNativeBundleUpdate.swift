import NitroModules
import ReactNativeNativeLogger
import Foundation
import ReactNativeBundleCrypto
import ReactNativeRangeDownloader
import SSZipArchive
import MMKV
import UIKit

// Public static store for AppDelegate access (called before JS starts)
@objcMembers
public class BundleUpdateStore: NSObject {
    private static let bundlePrefsKey = "currentBundleVersion"
    private static let nativeVersionKey = "nativeVersion"
    private static let nativeBuildNumberKey = "nativeBuildNumber"
    private static let mainBundleEntryFileName = "main.jsbundle.hbc"
    private static let backgroundBundleEntryFileName = "background.bundle"
    private static let commonBundleEntryFileName = "common.bundle"
    private static let metadataRequiresBackgroundBundleKey = "requiresBackgroundBundle"
    private static let metadataBackgroundProtocolVersionKey = "backgroundProtocolVersion"
    private static let metadataRequiresCommonBundleKey = "requiresCommonBundle"
    private static let metadataBundleFormatKey = "bundleFormat"
    private static let bundleFormatThreeBundle = "three-bundle"
    private static let supportedBackgroundProtocolVersion = "1"

    // In-memory cache for validatedCurrentBundleInfo. Without this, every
    // bundleURL / common / main / background path getter on startup re-runs
    // the whole validation pipeline (signature + sha256 of every entry file).
    // Cache key is currentBundleVersion; invalidated on any mutation of the
    // current bundle (setCurrentUpdateBundleData / clearBundle /
    // resetToBuiltInBundle / clearAllJSBundleData / native version change).
    private static var cachedValidatedBundleInfo: (
        bundleDirPath: String,
        currentBundleVersion: String,
        metadata: [String: Any]
    )?
    private static let cachedValidatedBundleInfoLock = NSLock()

    // Lazy per-version cache for the web-embed subtree. getWebEmbedPath is
    // called every time a WebView is created, but the subtree contents are
    // immutable for a given currentBundleVersion. Stores the version that
    // most recently passed full sha256 sweep over web-embed/**; invalidated
    // alongside cachedValidatedBundleInfo on any bundle mutation.
    private static var cachedWebEmbedVerifiedVersion: String?
    private static let cachedWebEmbedVerifiedVersionLock = NSLock()

    public static func invalidateValidatedBundleInfoCache() {
        cachedValidatedBundleInfoLock.lock()
        cachedValidatedBundleInfo = nil
        cachedValidatedBundleInfoLock.unlock()
        cachedWebEmbedVerifiedVersionLock.lock()
        cachedWebEmbedVerifiedVersion = nil
        cachedWebEmbedVerifiedVersionLock.unlock()
    }

    /// True when the current bundle's metadata declares the three-bundle /
    /// split-thread layout (or explicitly opts in via requiresCommonBundle).
    /// SplitBundleLoader queries this reflectively to decide whether an empty
    /// per-segment sha256 is a back-compat skip (older formats) or a hard
    /// fail (three-bundle, which always ships per-segment hashes).
    public static func currentBundleRequiresPerSegmentHash() -> Bool {
        guard let info = validatedCurrentBundleInfo() else { return false }
        if metadataBoolValue(info.metadata, key: metadataRequiresCommonBundleKey) {
            return true
        }
        let format = metadataStringValue(info.metadata, key: metadataBundleFormatKey) ?? ""
        return format.lowercased() == bundleFormatThreeBundle
    }

    public static func documentDirectory() -> String {
        NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0]
    }

    public static func downloadBundleDir() -> String {
        let dir = (documentDirectory() as NSString).appendingPathComponent("onekey-bundle-download")
        let fm = FileManager.default
        if !fm.fileExists(atPath: dir) {
            try? fm.createDirectory(atPath: dir, withIntermediateDirectories: true)
        }
        return dir
    }

    public static func bundleDir() -> String {
        let dir = (documentDirectory() as NSString).appendingPathComponent("onekey-bundle")
        let fm = FileManager.default
        if !fm.fileExists(atPath: dir) {
            try? fm.createDirectory(atPath: dir, withIntermediateDirectories: true)
        }
        return dir
    }

    public static func ascDir() -> String {
        let dir = (bundleDir() as NSString).appendingPathComponent("asc")
        let fm = FileManager.default
        if !fm.fileExists(atPath: dir) {
            try? fm.createDirectory(atPath: dir, withIntermediateDirectories: true)
        }
        return dir
    }

    public static func signatureFilePath(_ version: String) -> String {
        return (ascDir() as NSString).appendingPathComponent("\(version)-signature.asc")
    }

    public static func writeSignatureFile(_ version: String, signature: String) {
        let path = signatureFilePath(version)
        let existed = FileManager.default.fileExists(atPath: path)
        try? signature.write(toFile: path, atomically: true, encoding: .utf8)
        let fileSize = (try? FileManager.default.attributesOfItem(atPath: path)[.size] as? UInt64) ?? 0
        OneKeyLog.info("BundleUpdate", "writeSignatureFile: version=\(version), existed=\(existed), size=\(fileSize), path=\(path)")
    }

    public static func readSignatureFile(_ version: String) -> String {
        let path = signatureFilePath(version)
        guard FileManager.default.fileExists(atPath: path) else {
            OneKeyLog.debug("BundleUpdate", "readSignatureFile: not found for version=\(version)")
            return ""
        }
        let content = (try? String(contentsOfFile: path, encoding: .utf8)) ?? ""
        OneKeyLog.debug("BundleUpdate", "readSignatureFile: version=\(version), size=\(content.count)")
        return content
    }

    public static func deleteSignatureFile(_ version: String) {
        let path = signatureFilePath(version)
        try? FileManager.default.removeItem(atPath: path)
    }

    public static func getCurrentNativeVersion() -> String {
        Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? ""
    }

    public static func currentBundleVersion() -> String? {
        UserDefaults.standard.string(forKey: bundlePrefsKey)
    }

    public static func setCurrentBundleVersion(_ version: String) {
        UserDefaults.standard.set(version, forKey: bundlePrefsKey)
        UserDefaults.standard.synchronize()
    }

    public static func currentBundleDir() -> String? {
        guard let folderName = currentBundleVersion() else { return nil }
        return (bundleDir() as NSString).appendingPathComponent(folderName)
    }

    public static func getWebEmbedPath() -> String {
        guard let bundleInfo = validatedCurrentBundleInfo() else { return "" }
        // Lazy full-tree sha256 sweep for web-embed/**. The startup hot path
        // only validates the JS entry bundles (validateEntryBundlesSha256), so
        // without this, a tampered web-embed asset would slip through. Result
        // is cached per currentBundleVersion and invalidated on any bundle
        // mutation, so cost is paid once per (re)install.
        if !ensureWebEmbedVerified(
            bundleDirPath: bundleInfo.bundleDirPath,
            currentBundleVersion: bundleInfo.currentBundleVersion,
            metadata: bundleInfo.metadata,
        ) {
            return ""
        }
        return (bundleInfo.bundleDirPath as NSString).appendingPathComponent("web-embed")
    }

    private static func ensureWebEmbedVerified(
        bundleDirPath: String,
        currentBundleVersion: String,
        metadata: [String: Any],
    ) -> Bool {
        cachedWebEmbedVerifiedVersionLock.lock()
        if cachedWebEmbedVerifiedVersion == currentBundleVersion {
            cachedWebEmbedVerifiedVersionLock.unlock()
            return true
        }
        cachedWebEmbedVerifiedVersionLock.unlock()

        if !validateWebEmbedSha256(bundleDirPath: bundleDirPath, metadata: metadata) {
            OneKeyLog.error("BundleUpdate", "validateWebEmbedSha256 failed for \(currentBundleVersion)")
            return false
        }

        cachedWebEmbedVerifiedVersionLock.lock()
        cachedWebEmbedVerifiedVersion = currentBundleVersion
        cachedWebEmbedVerifiedVersionLock.unlock()
        return true
    }

    /// Walks bundleDirPath/web-embed/** and verifies every file's sha256
    /// against the metadata entry (key is the bundle-relative path). Also
    /// rejects files on disk that aren't listed in metadata, and metadata
    /// entries whose backing file is missing. Returns true when web-embed
    /// is absent from both disk and metadata (bundles without web-embed).
    static func validateWebEmbedSha256(
        bundleDirPath: String,
        metadata: [String: Any],
    ) -> Bool {
        let fm = FileManager.default
        let webEmbedDir = (bundleDirPath as NSString).appendingPathComponent("web-embed")
        let allEntries = fileMetadataEntries(from: metadata)
        let webEmbedEntries = allEntries.filter { $0.key.hasPrefix("web-embed/") }

        var dirExists: ObjCBool = false
        let dirPresent = fm.fileExists(atPath: webEmbedDir, isDirectory: &dirExists) && dirExists.boolValue
        if !dirPresent {
            // No web-embed in this bundle is fine, as long as metadata agrees.
            if !webEmbedEntries.isEmpty {
                OneKeyLog.error("BundleUpdate", "[web-embed-verify] metadata lists web-embed entries but directory is missing")
                return false
            }
            return true
        }

        let bundleDirWithSlash = bundleDirPath.hasSuffix("/") ? bundleDirPath : bundleDirPath + "/"
        guard let enumerator = fm.enumerator(atPath: webEmbedDir) else {
            OneKeyLog.error("BundleUpdate", "[web-embed-verify] failed to enumerate web-embed directory")
            return false
        }
        while let entry = enumerator.nextObject() as? String {
            let fullPath = (webEmbedDir as NSString).appendingPathComponent(entry)
            var isDir: ObjCBool = false
            if fm.fileExists(atPath: fullPath, isDirectory: &isDir), isDir.boolValue { continue }
            if entry.hasSuffix(".DS_Store") { continue }

            let relativePath = fullPath.replacingOccurrences(of: bundleDirWithSlash, with: "")
            guard let expected = webEmbedEntries[relativePath], !expected.isEmpty else {
                OneKeyLog.error("BundleUpdate", "[web-embed-verify] file on disk not in metadata: \(relativePath)")
                return false
            }
            guard let actual = calculateSHA256(fullPath) else {
                OneKeyLog.error("BundleUpdate", "[web-embed-verify] failed to hash file: \(relativePath)")
                return false
            }
            if !BundleCryptoCore.secureEqualHex(expected, actual) {
                OneKeyLog.error("BundleUpdate", "[web-embed-verify] sha256 mismatch for \(relativePath)")
                return false
            }
        }

        for key in webEmbedEntries.keys {
            let expectedFilePath = bundleDirWithSlash + key
            if !fm.fileExists(atPath: expectedFilePath) {
                OneKeyLog.error("BundleUpdate", "[web-embed-verify] file listed in metadata but missing on disk: \(key)")
                return false
            }
        }
        return true
    }

    /// Subtype of the most recent calculateSHA256 failure on this thread, or
    /// nil if the last call succeeded. Surfaces FILE_NOT_FOUND /
    /// FILE_DISAPPEARED / IO_<NSError code> so analytics can split the
    /// previously opaque "Failed to calculate SHA256" bucket (mixpanel:
    /// 91.3% of verifyPackage failures are Android; iOS shares the
    /// calculator and inherits the same blind spot for its 14 ASC + 2
    /// verifyPackage Promise-destroyed cases). Keep this list in sync
    /// with the setSHA256Failure(...) call sites in calculateSHA256
    /// below — Android's wider taxonomy (FILE_TRUNCATED / OOM /
    /// UNEXPECTED_<class>) does not apply here because the iOS reader
    /// surfaces every disk error as a single `IO_<NSError code>`.
    ///
    /// Note: 0-byte files are NOT treated as a failure. They hash to the
    /// well-known empty-content SHA256 and the caller's expected/actual
    /// comparison handles legitimate vs. corrupt-empty cases. Rejecting
    /// empty files here would make any OTA bundle that legitimately
    /// contains a 0-byte file (touched marker, blank locale fallback)
    /// fail validateAllFilesInDir / validateWebEmbedSha256 / launch entry
    /// verification — all of which share this calculator.
    public static func lastSHA256FailureReason() -> String? {
        return Thread.current.threadDictionary[kSHA256FailureKey] as? String
    }
    private static let kSHA256FailureKey = "so.onekey.bundleupdate.sha256.failure"
    private static func setSHA256Failure(_ reason: String?) {
        if let reason = reason {
            Thread.current.threadDictionary[kSHA256FailureKey] = reason
        } else {
            Thread.current.threadDictionary.removeObject(forKey: kSHA256FailureKey)
        }
    }

    /// Thin wrapper over the shared BundleCryptoCore.sha256OfFile. The streaming
    /// CommonCrypto implementation now lives in react-native-bundle-crypto; this
    /// preserves the existing per-thread failureReason channel
    /// (lastSHA256FailureReason) that analytics call sites read after a nil
    /// result. failureReason taxonomy (FILE_NOT_FOUND / FILE_DISAPPEARED /
    /// IO_<code>) is identical because the core was ported verbatim from here.
    public static func calculateSHA256(_ filePath: String) -> String? {
        let result = BundleCryptoCore.sha256OfFile(filePath)
        setSHA256Failure(result.sha256 == nil ? (result.failureReason ?? "MISMATCH") : nil)
        return result.sha256
    }

    public static func getNativeVersion() -> String? {
        UserDefaults.standard.string(forKey: nativeVersionKey)
    }

    public static func setNativeVersion(_ version: String) {
        UserDefaults.standard.set(version, forKey: nativeVersionKey)
        UserDefaults.standard.synchronize()
    }

    public static func getCurrentNativeBuildNumber() -> String {
        Bundle.main.infoDictionary?["CFBundleVersion"] as? String ?? ""
    }

    public static func getNativeBuildNumber() -> String? {
        UserDefaults.standard.string(forKey: nativeBuildNumberKey)
    }

    public static func setNativeBuildNumber(_ buildNumber: String) {
        UserDefaults.standard.set(buildNumber, forKey: nativeBuildNumberKey)
        UserDefaults.standard.synchronize()
    }

    public static func clearNativeVersionPrefs() {
        let ud = UserDefaults.standard
        ud.removeObject(forKey: nativeVersionKey)
        ud.removeObject(forKey: nativeBuildNumberKey)
    }

    public static func getBuiltinBundleVersion() -> String {
        Bundle.main.infoDictionary?["BUNDLE_VERSION"] as? String ?? ""
    }

    // MARK: - Pre-launch pending task processing

    private static var didProcessPreLaunchTask = false

    private static func failPendingTask(mmkv: MMKV, taskDict: [String: Any], reason: String) {
        var updatedTask = taskDict
        updatedTask["status"] = "failed"
        updatedTask["lastError"] = reason
        if let jsonData = try? JSONSerialization.data(withJSONObject: updatedTask),
           let jsonStr = String(data: jsonData, encoding: .utf8) {
            mmkv.set(jsonStr, forKey: "onekey_pending_install_task")
        }
    }

    /// Checks MMKV for a pending bundle-switch task and applies it before JS runtime starts.
    /// Only handles the happy path (status=pending, bundle exists, env matches).
    /// All complex logic (retry, download, error handling) remains in JS layer.
    public static func processPreLaunchPendingTask() {
        guard !didProcessPreLaunchTask else { return }
        didProcessPreLaunchTask = true

        do {
            // 1. Read MMKV (same pattern as isDevSettingsEnabled)
            MMKV.initialize(rootDir: nil)
            guard let mmkv = MMKV(mmapID: "onekey-app-setting"),
                  let taskJson = mmkv.string(forKey: "onekey_pending_install_task"),
                  let data = taskJson.data(using: .utf8),
                  let taskDict = try JSONSerialization.jsonObject(with: data) as? [String: Any]
            else { return }

            // 2. Validate task fields
            guard taskDict["status"] as? String == "pending",
                  taskDict["action"] as? String == "switch-bundle",
                  taskDict["type"] as? String == "jsbundle-switch"
            else { return }

            let now = Int64(Date().timeIntervalSince1970 * 1000)
            guard let expiresAtNum = taskDict["expiresAt"] as? NSNumber else { return }
            let expiresAt = expiresAtNum.int64Value
            guard expiresAt > now else { return }
            if let nextRetryAtNum = taskDict["nextRetryAt"] as? NSNumber {
                if nextRetryAtNum.int64Value > now { return }
            }

            // 3. Verify scheduledEnv matches current state (including buildNumber)
            let currentAppVersion = getCurrentNativeVersion()
            guard taskDict["scheduledEnvAppVersion"] as? String == currentAppVersion else { return }

            let scheduledBuildNumber = taskDict["scheduledEnvBuildNumber"] as? String ?? ""
            let currentBuildNumber = getCurrentNativeBuildNumber()
            if !scheduledBuildNumber.isEmpty && !currentBuildNumber.isEmpty && scheduledBuildNumber != currentBuildNumber {
                OneKeyLog.info("BundleUpdate", "processPreLaunchPendingTask: buildNumber changed from \(scheduledBuildNumber) to \(currentBuildNumber), skipping stale task")
                return
            }

            let currentBundleVersionStr: String
            if let currentFolder = currentBundleVersion() {
                guard let parsedVersion = bundleVersion(inFolder: currentFolder, appVersion: currentAppVersion) else { return }
                currentBundleVersionStr = parsedVersion
            } else {
                currentBundleVersionStr = getBuiltinBundleVersion()
            }
            guard taskDict["scheduledEnvBundleVersion"] as? String == currentBundleVersionStr else { return }

            // 4. Extract payload
            guard let payload = taskDict["payload"] as? [String: Any],
                  let appVersion = payload["appVersion"] as? String,
                  let bundleVersion = payload["bundleVersion"] as? String,
                  let signature = payload["signature"] as? String,
                  !appVersion.isEmpty, !bundleVersion.isEmpty, !signature.isEmpty
            else { return }
            guard appVersion.isSafeVersionString, bundleVersion.isSafeVersionString else {
                failPendingTask(mmkv: mmkv, taskDict: taskDict, reason: "ERR_VERIFY_FAILED:Invalid version string")
                return
            }
            guard appVersion == currentAppVersion else {
                OneKeyLog.warn("BundleUpdate", "processPreLaunchPendingTask: target appVersion (\(appVersion)) != currentAppVersion (\(currentAppVersion))")
                failPendingTask(mmkv: mmkv, taskDict: taskDict, reason: "ERR_ENV_CHANGED:Target appVersion mismatch")
                return
            }

            // 5. Verify bundle directory, canonical path confinement, and required files
            let folderName = "\(appVersion)-\(bundleVersion)"
            let bundleDirPath = (bundleDir() as NSString).appendingPathComponent(folderName)
            let resolvedBundleDir = (bundleDirPath as NSString).resolvingSymlinksInPath
            let baseBundleDir = (bundleDir() as NSString).resolvingSymlinksInPath
            guard resolvedBundleDir.hasPrefix(baseBundleDir + "/") else {
                OneKeyLog.error("BundleUpdate", "processPreLaunchPendingTask: bundle path outside bundle directory: \(resolvedBundleDir)")
                failPendingTask(mmkv: mmkv, taskDict: taskDict, reason: "ERR_VERIFY_FAILED:Bundle path outside bundle directory")
                return
            }
            let sigPath = (signatureFilePath(folderName) as NSString).resolvingSymlinksInPath
            let baseAscDir = (ascDir() as NSString).resolvingSymlinksInPath
            guard sigPath.hasPrefix(baseAscDir + "/") else {
                OneKeyLog.error("BundleUpdate", "processPreLaunchPendingTask: signature path outside asc directory: \(sigPath)")
                failPendingTask(mmkv: mmkv, taskDict: taskDict, reason: "ERR_VERIFY_FAILED:Signature path outside asc directory")
                return
            }
            guard FileManager.default.fileExists(atPath: bundleDirPath) else { return }
            let entryFilePath = (bundleDirPath as NSString).appendingPathComponent("main.jsbundle.hbc")
            let metadataFilePath = (bundleDirPath as NSString).appendingPathComponent("metadata.json")
            guard FileManager.default.fileExists(atPath: entryFilePath),
                  FileManager.default.fileExists(atPath: metadataFilePath) else {
                OneKeyLog.warn("BundleUpdate", "processPreLaunchPendingTask: bundle dir exists but required files missing: \(bundleDirPath)")
                failPendingTask(mmkv: mmkv, taskDict: taskDict, reason: "ERR_VERIFY_FAILED:Required bundle files missing")
                return
            }

            #if ALLOW_SKIP_GPG_VERIFICATION
            let skipGPG = isDevSettingsEnabled() && isSkipGPGEnabled()
            #else
            let skipGPG = false
            #endif
            if !skipGPG && !validateMetadataFileSha256(folderName, signature: signature, expectedAppVersion: appVersion, expectedBundleVersion: bundleVersion) {
                OneKeyLog.error("BundleUpdate", "processPreLaunchPendingTask: GPG signature verification failed for \(folderName)")
                failPendingTask(mmkv: mmkv, taskDict: taskDict, reason: "ERR_VERIFY_FAILED:Bundle signature verification failed")
                return
            }

            guard let metadata = getMetadataFileContent(folderName) else {
                OneKeyLog.error("BundleUpdate", "processPreLaunchPendingTask: failed to read metadata.json for \(folderName)")
                failPendingTask(mmkv: mmkv, taskDict: taskDict, reason: "ERR_VERIFY_FAILED:Failed to read metadata.json")
                return
            }
            if !validateAllFilesInDir(bundleDirPath, metadata: metadata, appVersion: appVersion, bundleVersion: bundleVersion) {
                OneKeyLog.error("BundleUpdate", "processPreLaunchPendingTask: extracted file verification failed for \(folderName)")
                failPendingTask(mmkv: mmkv, taskDict: taskDict, reason: "ERR_VERIFY_FAILED:Extracted file verification failed")
                return
            }
            if !validateBundlePairCompatibility(bundleDirPath: bundleDirPath, metadata: metadata) {
                OneKeyLog.error("BundleUpdate", "processPreLaunchPendingTask: bundle pair compatibility check failed for \(folderName)")
                failPendingTask(mmkv: mmkv, taskDict: taskDict, reason: "ERR_VERIFY_FAILED:Bundle pair compatibility check failed")
                return
            }

            // 6. Apply: write signature file BEFORE flipping currentBundleVersion
            writeSignatureFile(folderName, signature: signature)
            let ud = UserDefaults.standard
            ud.set(folderName, forKey: bundlePrefsKey)
            setNativeVersion(currentAppVersion)
            setNativeBuildNumber(getCurrentNativeBuildNumber())
            ud.synchronize()

            // 7. Update MMKV task status → applied_waiting_verify
            // Do NOT set runningStartedAt — a falsy value lets JS skip the
            // 10-minute grace period and verify alignment immediately on boot.
            var updatedTask = taskDict
            updatedTask["status"] = "applied_waiting_verify"
            updatedTask.removeValue(forKey: "runningStartedAt")
            let jsonData = try JSONSerialization.data(withJSONObject: updatedTask)
            if let jsonStr = String(data: jsonData, encoding: .utf8) {
                mmkv.set(jsonStr, forKey: "onekey_pending_install_task")
            }

            OneKeyLog.info("BundleUpdate", "processPreLaunchPendingTask: switched to \(folderName)")
        } catch {
            OneKeyLog.error("BundleUpdate", "processPreLaunchPendingTask failed: \(error)")
        }
    }

    public static func getMetadataFilePath(_ currentBundleVersion: String) -> String? {
        let path = (bundleDir() as NSString)
            .appendingPathComponent(currentBundleVersion)
        let metadataPath = (path as NSString).appendingPathComponent("metadata.json")
        guard FileManager.default.fileExists(atPath: metadataPath) else { return nil }
        return metadataPath
    }

    public static func getMetadataFileContent(_ currentBundleVersion: String) -> [String: Any]? {
        guard let path = getMetadataFilePath(currentBundleVersion),
              let data = FileManager.default.contents(atPath: path),
              let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else { return nil }
        return json
    }

    private static func isReservedMetadataKey(_ key: String) -> Bool {
        key == metadataRequiresBackgroundBundleKey ||
        key == metadataBackgroundProtocolVersionKey ||
        key == metadataRequiresCommonBundleKey ||
        key == metadataBundleFormatKey ||
        key == BundleStorageSchemaPolicy.metadataKey
    }

    private static func metadataStringValue(
        _ metadata: [String: Any],
        key: String,
    ) -> String? {
        if let value = metadata[key] as? String {
            return value
        }
        if let value = metadata[key] as? NSNumber {
            return value.stringValue
        }
        if let value = metadata[key] as? Bool {
            return value ? "true" : "false"
        }
        return nil
    }

    private static func metadataBoolValue(
        _ metadata: [String: Any],
        key: String,
    ) -> Bool {
        if let value = metadata[key] as? Bool {
            return value
        }
        if let value = metadata[key] as? NSNumber {
            return value.boolValue
        }
        if let value = metadata[key] as? String {
            return ["1", "true", "yes"].contains(value.lowercased())
        }
        return false
    }

    private static func validateStorageSchemaCompatibility(_ metadata: [String: Any]) -> Bool {
        let declaredVersion = metadataStringValue(metadata, key: BundleStorageSchemaPolicy.metadataKey)
        let compatible = BundleStorageSchemaPolicy.isCompatible(declaredVersion: declaredVersion) { key in
            UserDefaults.standard.string(forKey: key)
        }
        if !compatible {
            OneKeyLog.error("BundleUpdate", "Bundle storage schema is incompatible with the native migration state")
        }
        return compatible
    }

    static func fileMetadataEntries(
        from metadata: [String: Any],
    ) -> [String: String] {
        var entries: [String: String] = [:]
        for (key, value) in metadata {
            if isReservedMetadataKey(key) {
                continue
            }
            guard let hash = value as? String else {
                continue
            }
            entries[key] = hash
        }
        return entries
    }

    /// Returns true if OneKey developer mode (DevSettings) is enabled.
    /// Reads the persisted value from MMKV storage written by the JS ServiceDevSetting layer.
    public static func isDevSettingsEnabled() -> Bool {
        // Ensure MMKV is initialized (safe to call multiple times)
        MMKV.initialize(rootDir: nil)
        guard let mmkv = MMKV(mmapID: "onekey-app-dev-setting") else { return false }
        return mmkv.bool(forKey: "onekey_developer_mode_enabled", defaultValue: false)
    }

    /// Returns true if the skip-GPG-verification toggle is enabled in developer settings.
    /// Reads the persisted value from MMKV storage (key: onekey_bundle_skip_gpg_verification,
    /// instance: onekey-app-dev-setting).
    /// Gated by ALLOW_SKIP_GPG_VERIFICATION compile flag — always returns false in production builds.
    public static func isSkipGPGEnabled() -> Bool {
        #if ALLOW_SKIP_GPG_VERIFICATION
        MMKV.initialize(rootDir: nil)
        guard let mmkv = MMKV(mmapID: "onekey-app-dev-setting") else { return false }
        return mmkv.bool(forKey: "onekey_bundle_skip_gpg_verification", defaultValue: false)
        #else
        return false
        #endif
    }

    public static func bundleVersion(inFolder folder: String, appVersion: String) -> String? {
        let prefix = "\(appVersion)-"
        guard appVersion.isSafeVersionString, folder.hasPrefix(prefix) else { return nil }
        let bundleVersion = String(folder.dropFirst(prefix.count))
        return bundleVersion.isSafeVersionString ? bundleVersion : nil
    }

    public static func readMetadataFileSha256(
        _ signature: String,
        expectedAppVersion: String? = nil,
        expectedBundleVersion: String? = nil,
        expectedAppType: String? = "ios",
        expectedFileName: String = "metadata.json"
    ) -> String? {
        guard !signature.isEmpty else { return nil }

        // GPG cleartext signature verification is required. The actual PGP
        // verification + JSON-body sha256 extraction lives in the shared
        // BundleCryptoCore (react-native-bundle-crypto); preserve the previous
        // "failure => nil" semantics here.
        let r = BundleCryptoCore.verifyGpgCleartext(
            signature,
            expectedAppVersion: expectedAppVersion,
            expectedBundleVersion: expectedBundleVersion,
            expectedAppType: expectedAppType,
            expectedFileName: expectedFileName
        )
        guard r.valid, let sha256 = r.sha256 else {
            OneKeyLog.error("BundleUpdate", "readMetadataFileSha256: GPG verification failed (\(r.reason ?? "unknown")), rejecting unsigned content")
            return nil
        }
        return sha256
    }

    public static func validateMetadataFileSha256(_ currentBundleVersion: String, signature: String, expectedAppVersion: String, expectedBundleVersion: String) -> Bool {
        guard let metadataFilePath = getMetadataFilePath(currentBundleVersion) else {
            OneKeyLog.debug("BundleUpdate", "metadataFilePath is null")
            return false
        }
        guard expectedAppVersion.isSafeVersionString,
              expectedBundleVersion.isSafeVersionString,
              currentBundleVersion == "\(expectedAppVersion)-\(expectedBundleVersion)" else { return false }
        guard let extractedSha256 = readMetadataFileSha256(
            signature,
            expectedAppVersion: expectedAppVersion,
            expectedBundleVersion: expectedBundleVersion,
            expectedAppType: "ios"
        ), !extractedSha256.isEmpty else {
            return false
        }
        guard let calculatedSha256 = calculateSHA256(metadataFilePath) else { return false }
        return BundleCryptoCore.secureEqualHex(calculatedSha256, extractedSha256)
    }

    /// Maps the verified metadata's relativePath -> sha256 entries into the
    /// plain BundleCryptoCore.DirHash list consumed by verifyDirAgainstHashes.
    /// Metadata parsing is orchestration and stays here; the crypto core only
    /// hashes/compares.
    private static func dirHashEntries(from metadata: [String: Any]) -> [BundleCryptoCore.DirHash] {
        fileMetadataEntries(from: metadata).map {
            BundleCryptoCore.DirHash(relativePath: $0.key, sha256: $0.value)
        }
    }

    public static func validateAllFilesInDir(_ dirPath: String, metadata: [String: Any], appVersion: String, bundleVersion: String) -> Bool {
        // The full per-file enumeration + sha256 + constant-time compare logic
        // now lives in the shared BundleCryptoCore.verifyDirAgainstHashes. The
        // dirPath passed by every caller is bundleDir()/<appVersion>-<bundleVersion>,
        // which the core normalizes the same way the old jsBundleDir prefix did,
        // so relativePath derivation (and thus the verification result) is
        // identical. metadata -> entries mapping stays here as orchestration.
        return BundleCryptoCore.verifyDirAgainstHashes(
            dirPath: dirPath,
            entries: dirHashEntries(from: metadata),
        )
    }

    static func validateBundlePairCompatibility(
        bundleDirPath: String,
        metadata: [String: Any],
    ) -> Bool {
        if !validateStorageSchemaCompatibility(metadata) {
            return false
        }
        let mainBundlePath = (bundleDirPath as NSString)
            .appendingPathComponent(mainBundleEntryFileName)
        guard FileManager.default.fileExists(atPath: mainBundlePath) else {
            OneKeyLog.error(
                "BundleUpdate",
                "bundle pair invalid: main.jsbundle.hbc is missing at \(mainBundlePath)",
            )
            return false
        }

        // Three-bundle (union build / split thread) mode requires a
        // common.bundle shipped alongside main.jsbundle.hbc. Without it
        // the entry-only main bundle references moduleIds that only exist in
        // the common bundle and the runtime crashes on first require().
        let bundleFormat = metadataStringValue(metadata, key: metadataBundleFormatKey) ?? ""
        let requiresCommonBundle =
            metadataBoolValue(metadata, key: metadataRequiresCommonBundleKey) ||
            bundleFormat.lowercased() == bundleFormatThreeBundle
        if requiresCommonBundle {
            let commonBundlePath = (bundleDirPath as NSString)
                .appendingPathComponent(commonBundleEntryFileName)
            guard FileManager.default.fileExists(atPath: commonBundlePath) else {
                OneKeyLog.error(
                    "BundleUpdate",
                    "requiresCommonBundle is true but common.bundle is missing at \(commonBundlePath)",
                )
                return false
            }
        }

        let requiresBackgroundBundle = metadataBoolValue(
            metadata,
            key: metadataRequiresBackgroundBundleKey,
        )
        if !requiresBackgroundBundle {
            return true
        }

        let protocolVersion = metadataStringValue(
            metadata,
            key: metadataBackgroundProtocolVersionKey,
        ) ?? ""
        if protocolVersion.isEmpty || protocolVersion != supportedBackgroundProtocolVersion {
            OneKeyLog.error(
                "BundleUpdate",
                "backgroundProtocolVersion mismatch: expected=\(supportedBackgroundProtocolVersion), actual=\(protocolVersion)",
            )
            return false
        }

        let backgroundBundlePath = (bundleDirPath as NSString)
            .appendingPathComponent(backgroundBundleEntryFileName)
        guard FileManager.default.fileExists(atPath: backgroundBundlePath) else {
            OneKeyLog.error(
                "BundleUpdate",
                "requiresBackgroundBundle is true but background.bundle is missing at \(backgroundBundlePath)",
            )
            return false
        }

        return true
    }

    private static func validatedCurrentBundleInfo() -> (
        bundleDirPath: String,
        currentBundleVersion: String,
        metadata: [String: Any]
    )? {
        processPreLaunchPendingTask()
        guard let currentBundleVer = currentBundleVersion() else {
            OneKeyLog.warn("BundleUpdate", "getJsBundlePath: no currentBundleVersion stored")
            invalidateValidatedBundleInfoCache()
            return nil
        }

        // Memo cache: avoid re-running signature + entry-bundle sha256 for every
        // bundleURL / common / main / background path getter on startup.
        cachedValidatedBundleInfoLock.lock()
        if let cached = cachedValidatedBundleInfo, cached.currentBundleVersion == currentBundleVer {
            cachedValidatedBundleInfoLock.unlock()
            // Migration state can change while the native bundle cache survives
            // a JS runtime reload. Recheck the ledger without repeating hashes.
            if !validateStorageSchemaCompatibility(cached.metadata) {
                invalidateValidatedBundleInfoCache()
                return nil
            }
            return cached
        }
        cachedValidatedBundleInfoLock.unlock()

        let currentAppVersion = getCurrentNativeVersion()
        guard let prevNativeVersion = getNativeVersion() else {
            OneKeyLog.warn("BundleUpdate", "getJsBundlePath: prevNativeVersion is nil")
            return nil
        }

        OneKeyLog.info("BundleUpdate", "currentAppVersion: \(currentAppVersion), currentBundleVersion: \(currentBundleVer), prevNativeVersion: \(prevNativeVersion)")

        if currentAppVersion != prevNativeVersion {
            OneKeyLog.info("BundleUpdate", "currentAppVersion is not equal to prevNativeVersion \(currentAppVersion) \(prevNativeVersion)")
            let ud = UserDefaults.standard
            if let cbv = ud.string(forKey: "currentBundleVersion") {
                deleteSignatureFile(cbv)
                ud.removeObject(forKey: "currentBundleVersion")
            }
            clearNativeVersionPrefs()
            ud.synchronize()
            return nil
        }

        let currentBuildNumber = getCurrentNativeBuildNumber()
        let prevBuildNumber = getNativeBuildNumber()
        if !currentBuildNumber.isEmpty, let prev = prevBuildNumber, !prev.isEmpty, currentBuildNumber != prev {
            OneKeyLog.info("BundleUpdate", "buildNumber changed from \(prev) to \(currentBuildNumber), clearing bundle data")
            let ud = UserDefaults.standard
            if let cbv = ud.string(forKey: "currentBundleVersion") {
                deleteSignatureFile(cbv)
                ud.removeObject(forKey: "currentBundleVersion")
            }
            clearNativeVersionPrefs()
            ud.synchronize()
            return nil
        }

        guard let folderName = currentBundleDir(),
              FileManager.default.fileExists(atPath: folderName) else {
            OneKeyLog.warn("BundleUpdate", "getJsBundlePath: currentBundleDir does not exist")
            return nil
        }

        let signature = readSignatureFile(currentBundleVer)
        OneKeyLog.debug("BundleUpdate", "getJsBundlePath: signatureLength=\(signature.count)")

        #if ALLOW_SKIP_GPG_VERIFICATION
        let skipGPG = isDevSettingsEnabled() && isSkipGPGEnabled()
        if skipGPG {
            OneKeyLog.warn("BundleUpdate", "Startup SHA256 validation skipped (DevSettings + skip-GPG enabled)")
        }
        #else
        let skipGPG = false
        #endif
        guard let currentBundleVersion = bundleVersion(inFolder: currentBundleVer, appVersion: currentAppVersion) else { return nil }
        if !skipGPG && !validateMetadataFileSha256(currentBundleVer, signature: signature, expectedAppVersion: currentAppVersion, expectedBundleVersion: currentBundleVersion) {
            OneKeyLog.warn("BundleUpdate", "getJsBundlePath: validateMetadataFileSha256 failed, signatureLength=\(signature.count)")
            return nil
        }

        guard let metadata = getMetadataFileContent(currentBundleVer) else {
            OneKeyLog.warn("BundleUpdate", "getJsBundlePath: getMetadataFileContent returned nil")
            return nil
        }

        // Startup hot path: only verify entry-bundle SHA-256
        // (main + common + background as required by metadata flags). The
        // full-tree sha256 sweep already runs at install time
        // (validateAllFilesInDir in installBundle), so re-doing it on every
        // launch costs hundreds of ms per startup with no security gain
        // (sandboxed app data + signed metadata.json bind every file's
        // expected hash). Per-segment integrity is checked at loadSegment
        // time by SplitBundleLoader.
        if !validateEntryBundlesSha256(bundleDirPath: folderName, metadata: metadata) {
            OneKeyLog.info("BundleUpdate", "validateEntryBundlesSha256 failed on startup")
            return nil
        }

        if !validateBundlePairCompatibility(bundleDirPath: folderName, metadata: metadata) {
            return nil
        }

        let result = (folderName, currentBundleVer, metadata)
        cachedValidatedBundleInfoLock.lock()
        cachedValidatedBundleInfo = result
        cachedValidatedBundleInfoLock.unlock()
        return result
    }

    /// Verifies SHA-256 of just the entry bundles required to boot the JS
    /// runtime (main + common + background, gated by metadata flags). Runs in
    /// place of the legacy validateAllFilesInDir on the startup hot path.
    static func validateEntryBundlesSha256(
        bundleDirPath: String,
        metadata: [String: Any],
    ) -> Bool {
        let fileEntries = fileMetadataEntries(from: metadata)

        var entriesToCheck: [String] = [mainBundleEntryFileName]
        let bundleFormat = metadataStringValue(metadata, key: metadataBundleFormatKey) ?? ""
        if metadataBoolValue(metadata, key: metadataRequiresCommonBundleKey) ||
            bundleFormat.lowercased() == bundleFormatThreeBundle {
            entriesToCheck.append(commonBundleEntryFileName)
        }
        if metadataBoolValue(metadata, key: metadataRequiresBackgroundBundleKey) {
            entriesToCheck.append(backgroundBundleEntryFileName)
        }

        for entry in entriesToCheck {
            let path = (bundleDirPath as NSString).appendingPathComponent(entry)
            guard FileManager.default.fileExists(atPath: path) else {
                OneKeyLog.error("BundleUpdate", "[entry-verify] missing entry file: \(entry)")
                return false
            }
            guard let expected = fileEntries[entry], !expected.isEmpty else {
                OneKeyLog.error("BundleUpdate", "[entry-verify] no metadata sha256 for entry: \(entry)")
                return false
            }
            guard let actual = calculateSHA256(path) else {
                OneKeyLog.error("BundleUpdate", "[entry-verify] failed to hash entry: \(entry)")
                return false
            }
            if !BundleCryptoCore.secureEqualHex(expected, actual) {
                OneKeyLog.error("BundleUpdate", "[entry-verify] sha256 mismatch for entry: \(entry)")
                return false
            }
        }
        return true
    }

    private static func currentBundleEntryPath(_ entryFileName: String) -> String? {
        guard let bundleInfo = validatedCurrentBundleInfo() else {
            return nil
        }

        let entryPath = (bundleInfo.bundleDirPath as NSString).appendingPathComponent(entryFileName)
        guard FileManager.default.fileExists(atPath: entryPath) else {
            OneKeyLog.info("BundleUpdate", "\(entryFileName) does not exist")
            return nil
        }
        return entryPath
    }

    public static func currentBundleMainJSBundle() -> String? {
        currentBundleEntryPath(mainBundleEntryFileName)
    }

    public static func currentBundleBackgroundJSBundle() -> String? {
        currentBundleEntryPath(backgroundBundleEntryFileName)
    }

    public static func currentBundleCommonJSBundle() -> String? {
        currentBundleEntryPath(commonBundleEntryFileName)
    }

    // Fallback data management
    static func getFallbackUpdateBundleDataPath() -> String {
        let path = (bundleDir() as NSString).appendingPathComponent("fallbackUpdateBundleData.json")
        if !FileManager.default.fileExists(atPath: path) {
            FileManager.default.createFile(atPath: path, contents: nil)
        }
        return path
    }

    static func readFallbackUpdateBundleDataFile() -> [[String: String]] {
        let path = getFallbackUpdateBundleDataPath()
        guard let content = try? String(contentsOfFile: path, encoding: .utf8),
              !content.isEmpty,
              let data = content.data(using: .utf8),
              let arr = try? JSONSerialization.jsonObject(with: data) as? [[String: String]] else {
            return []
        }
        return arr
    }

    static func writeFallbackUpdateBundleDataFile(_ data: [[String: String]]) {
        let path = getFallbackUpdateBundleDataPath()
        guard let jsonData = try? JSONSerialization.data(withJSONObject: data),
              let jsonString = String(data: jsonData, encoding: .utf8) else { return }
        try? jsonString.write(toFile: path, atomically: true, encoding: .utf8)
    }

    public static func clearUpdateBundleData() {
        didProcessPreLaunchTask = true
        invalidateValidatedBundleInfoCache()
        let bDir = bundleDir()
        let fm = FileManager.default
        if fm.fileExists(atPath: bDir) {
            // This also deletes asc/ directory containing all signature files
            try? fm.removeItem(atPath: bDir)
        }
        let ud = UserDefaults.standard
        // Legacy cleanup: remove signature from UserDefaults if present
        if let cbv = currentBundleVersion() {
            ud.removeObject(forKey: cbv)
        }
        ud.removeObject(forKey: bundlePrefsKey)
        ud.synchronize()
    }
}

// Version string sanitization
private extension String {
    var isSafeVersionString: Bool {
        BundleDiffReconstruction.isSafeVersionComponent(self)
    }
}

/// URLSession delegate that handles download progress and HTTPS redirect validation
private class DownloadDelegate: NSObject, URLSessionDownloadDelegate {
    static let maxDownloadBytes: Int64 = 512 * 1024 * 1024

    /// Called with progress percentage (0-100) during download
    var onProgress: ((Int) -> Void)?

    /// Continuation to bridge delegate callbacks → async/await
    private var continuation: CheckedContinuation<(URL, URLResponse), Error>?
    private var tempFileURL: URL?
    private var exceededMaxBytes = false
    /// Resume data captured from the most recent failure so the caller can
    /// persist it for the next attempt. Populated in didCompleteWithError
    /// when the system supplies NSURLSessionDownloadTaskResumeData.
    private(set) var lastResumeData: Data?
    private let lock = NSLock()
    private var cancelledByClear = false
    private var rejectedInsecureRedirect = false
    private var runCompleted = true
    private var completionWaiters: [UUID: CheckedContinuation<Bool, Never>] = [:]

    var wasCancelledByClear: Bool {
        lock.lock()
        defer { lock.unlock() }
        return cancelledByClear
    }

    func markCancelledByClear() {
        lock.lock()
        cancelledByClear = true
        lock.unlock()
    }

    var wasInsecureRedirectRejected: Bool {
        lock.lock()
        defer { lock.unlock() }
        return rejectedInsecureRedirect
    }

    func setContinuation(_ cont: CheckedContinuation<(URL, URLResponse), Error>) -> Bool {
        lock.lock()
        if cancelledByClear {
            lock.unlock()
            cont.resume(throwing: BundleUpdateError(.cancelled, .download))
            return false
        }
        continuation = cont
        lock.unlock()
        return true
    }

    func finishRun() {
        lock.lock()
        runCompleted = true
        let waiters = Array(completionWaiters.values)
        completionWaiters.removeAll()
        lock.unlock()
        waiters.forEach { $0.resume(returning: true) }
    }

    func waitForRunCompletion(timeout: TimeInterval) async -> Bool {
        await withCheckedContinuation { continuation in
            let id = UUID()
            lock.lock()
            if runCompleted {
                lock.unlock()
                continuation.resume(returning: true)
                return
            }
            completionWaiters[id] = continuation
            lock.unlock()
            DispatchQueue.global().asyncAfter(deadline: .now() + timeout) { [weak self] in
                guard let self else { return }
                self.lock.lock()
                let waiter = self.completionWaiters.removeValue(forKey: id)
                self.lock.unlock()
                waiter?.resume(returning: false)
            }
        }
    }

    // MARK: - URLSessionDownloadDelegate

    private var prevProgress: Int = -1

    func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask, didWriteData bytesWritten: Int64, totalBytesWritten: Int64, totalBytesExpectedToWrite: Int64) {
        if totalBytesWritten > Self.maxDownloadBytes || totalBytesExpectedToWrite > Self.maxDownloadBytes {
            exceededMaxBytes = true
            downloadTask.cancel()
            return
        }
        guard totalBytesExpectedToWrite > 0 else { return }
        let progress = Int((totalBytesWritten * 100) / totalBytesExpectedToWrite)
        if progress != prevProgress {
            OneKeyLog.info("BundleUpdate", "download progress: \(progress)% (\(totalBytesWritten)/\(totalBytesExpectedToWrite))")
            prevProgress = progress
            onProgress?(progress)
        }
    }

    func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask,
                    didResumeAtOffset fileOffset: Int64, expectedTotalBytes: Int64) {
        if fileOffset > Self.maxDownloadBytes || expectedTotalBytes > Self.maxDownloadBytes {
            exceededMaxBytes = true
            downloadTask.cancel()
        }
    }

    func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask, didFinishDownloadingTo location: URL) {
        if exceededMaxBytes { return }
        if let attrs = try? FileManager.default.attributesOfItem(atPath: location.path),
           let fileSize = attrs[.size] as? Int64,
           fileSize > Self.maxDownloadBytes {
            exceededMaxBytes = true
            return
        }
        // Copy to a temp location because the file at `location` is deleted after this method returns
        let tempPath = NSTemporaryDirectory() + UUID().uuidString
        let dest = URL(fileURLWithPath: tempPath)
        do {
            try FileManager.default.copyItem(at: location, to: dest)
            tempFileURL = dest
        } catch {
            OneKeyLog.error("BundleUpdate", "Failed to copy downloaded file: \(error)")
        }
    }

    // MARK: - URLSessionTaskDelegate

    func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?) {
        lock.lock()
        let cont = continuation
        continuation = nil
        lock.unlock()

        if exceededMaxBytes {
            self.lastResumeData = nil
            cont?.resume(throwing: BundleUpdateError(.security, .download))
            return
        }

        if let error = error {
            // iOS attaches partial download bytes via userInfo so the next
            // attempt can finish from the cut point instead of byte 0. Mixpanel
            // shows ~5,940 of our failures (NSURL -1005 / -1001) carry ~11KB of
            // resume data each — previously discarded.
            let nsError = error as NSError
            if let resumeData = nsError.userInfo[NSURLSessionDownloadTaskResumeData] as? Data, resumeData.count > 0 {
                self.lastResumeData = resumeData
                OneKeyLog.info("BundleUpdate", "download error captured resumeData: \(resumeData.count) bytes")
            } else {
                self.lastResumeData = nil
            }
            cont?.resume(throwing: error)
        } else if let tempURL = tempFileURL, let response = task.response {
            self.lastResumeData = nil
            cont?.resume(returning: (tempURL, response))
        } else {
            self.lastResumeData = nil
            cont?.resume(throwing: BundleUpdateError(.storage, .download))
        }
    }

    func urlSession(_ session: URLSession, task: URLSessionTask, willPerformHTTPRedirection response: HTTPURLResponse, newRequest request: URLRequest, completionHandler: @escaping (URLRequest?) -> Void) {
        if request.url?.scheme?.lowercased() != "https" {
            OneKeyLog.error("BundleUpdate", "Blocked redirect to non-HTTPS URL")
            lock.lock()
            rejectedInsecureRedirect = true
            lock.unlock()
            completionHandler(nil)
        } else {
            completionHandler(request)
        }
    }

    /// Reset state for reuse
    func reset() {
        lock.lock()
        continuation = nil
        cancelledByClear = false
        rejectedInsecureRedirect = false
        runCompleted = false
        lock.unlock()
        tempFileURL = nil
        exceededMaxBytes = false
        onProgress = nil
        prevProgress = -1
        lastResumeData = nil
    }

    /// Reset per-attempt download state while preserving `cancelledByClear` and `runCompleted`
    /// so a fallback download within the same run cannot clear an active cancellation signal.
    func resetForAttempt() {
        lock.lock()
        continuation = nil
        rejectedInsecureRedirect = false
        lock.unlock()
        tempFileURL = nil
        exceededMaxBytes = false
        onProgress = nil
        prevProgress = -1
        lastResumeData = nil
    }
}

// Listener support
private struct BundleListener {
    let id: Double
    let callback: (BundleDownloadEvent) -> Void
}

private enum BundleUpdateCode: String {
    case network = "NETWORK_ERROR"
    case timeout = "TIMEOUT"
    case http = "HTTP_ERROR"
    case missing = "PACKAGE_MISSING"
    case storage = "STORAGE_ERROR"
    case integrity = "INTEGRITY_FAILED"
    case incompatible = "INCOMPATIBLE"
    case install = "INSTALL_FAILED"
    case cancelled = "CANCELLED"
    case unknown = "UNKNOWN"
    case invalidArgument = "INVALID_ARGUMENT"
    case security = "SECURITY_REJECTED"
}

private enum BundleUpdateStep: String {
    case download, downloadASC, verifyASC, verifyPackage, install, cleanup
}

private struct BundleUpdateError: Error, CustomStringConvertible {
    let code: BundleUpdateCode
    let step: BundleUpdateStep
    let httpStatus: Int?

    init(_ code: BundleUpdateCode, _ step: BundleUpdateStep, httpStatus: Int? = nil) {
        self.code = code
        self.step = step
        self.httpStatus = code == .http && (100...599).contains(httpStatus ?? -1) ? httpStatus : nil
    }

    var description: String {
        let status = httpStatus.map { ",\"httpStatus\":\($0)" } ?? ""
        return "ONEKEY_UPDATE_ERROR:{\"code\":\"\(code.rawValue)\",\"step\":\"\(step.rawValue)\"\(status)}"
    }
}

private func bundleUpdateError(_ error: Error, step: BundleUpdateStep) -> BundleUpdateError {
    if let typed = error as? BundleUpdateError {
        return BundleUpdateError(typed.code, step, httpStatus: typed.httpStatus)
    }
    if error is CancellationError {
        return BundleUpdateError(.cancelled, step)
    }
    if error is RangeDownloadSizeLimitError {
        return BundleUpdateError(.security, step)
    }
    if let httpError = error as? RangeDownloader.HTTPStatusError {
        return BundleUpdateError(.http, step, httpStatus: httpError.statusCode)
    }
    let nsError = error as NSError
    if nsError.domain == NSURLErrorDomain {
        switch nsError.code {
        case URLError.timedOut.rawValue:
            return BundleUpdateError(.timeout, step)
        case URLError.cannotFindHost.rawValue,
             URLError.cannotConnectToHost.rawValue,
             URLError.networkConnectionLost.rawValue,
             URLError.notConnectedToInternet.rawValue,
             URLError.dnsLookupFailed.rawValue,
             URLError.cancelled.rawValue:
            return BundleUpdateError(.network, step)
        case URLError.secureConnectionFailed.rawValue,
             URLError.serverCertificateHasBadDate.rawValue,
             URLError.serverCertificateNotYetValid.rawValue,
             URLError.serverCertificateUntrusted.rawValue,
             URLError.serverCertificateHasUnknownRoot.rawValue,
             URLError.clientCertificateRejected.rawValue,
             URLError.clientCertificateRequired.rawValue:
            return BundleUpdateError(.security, step)
        case URLError.badURL.rawValue, URLError.unsupportedURL.rawValue:
            return BundleUpdateError(.invalidArgument, step)
        default:
            return BundleUpdateError(.unknown, step)
        }
    }
    if nsError.domain == NSCocoaErrorDomain || nsError.domain == NSPOSIXErrorDomain {
        return BundleUpdateError(.storage, step)
    }
    return BundleUpdateError(.unknown, step)
}

class ReactNativeBundleUpdate: HybridReactNativeBundleUpdateSpec {

    private func updatePromise<T>(step: BundleUpdateStep, work: @escaping () async throws -> T) -> Promise<T> {
        return Promise.async {
            do {
                return try await work()
            } catch {
                OneKeyLog.error("BundleUpdate", "\(step.rawValue) failed: \(error)")
                throw bundleUpdateError(error, step: step)
            }
        }
    }

    private final class ProcessState {
        static let shared = ProcessState()

        let queue = DispatchQueue(label: "so.onekey.bundleupdate.state")
        var clearingDownloads = false
        var activeDownload: DownloadRun?
        var activeVerifications: [String: BundleVerificationRun] = [:]
        var diffFallbackVersions: [String: String] = [:]
        var fullBundleParams: [String: (downloadUrl: String, fileSize: Double, sha256: String)] = [:]
        var verifiedDiffRecords: [BundleDiffReconstruction.ReleaseIdentity: BundleDiffReconstruction.VerifiedDiffRecord] = [:]
    }

    private final class DownloadRun {
        let owner: ObjectIdentifier
        let targetKey: String
        let delegate = DownloadDelegate()
        let session: URLSession
        var filePath: String?
        var rangeTaskId: String?
        var published = false

        init(owner: ReactNativeBundleUpdate, targetKey: String) {
            self.owner = ObjectIdentifier(owner)
            self.targetKey = targetKey
            let config = URLSessionConfiguration.default
            config.tlsMinimumSupportedProtocolVersion = .TLSv12
            session = URLSession(configuration: config, delegate: delegate, delegateQueue: nil)
            delegate.reset()
        }
    }

    private let process = ProcessState.shared
    private let listenerQueue = DispatchQueue(label: "so.onekey.bundleupdate.listeners")
    private var listeners: [BundleListener] = []
    private var nextListenerId: Double = 1

    private func isDiffFallbackMarked(_ key: String, sha256: String) -> Bool {
        process.queue.sync { process.diffFallbackVersions[key] == sha256 }
    }

    private func markDiffFallback(_ key: String, sha256: String) {
        process.queue.sync { process.diffFallbackVersions[key] = sha256 }
    }

    private func recordFullBundleParams(key: String, downloadUrl: String, fileSize: Double, sha256: String) {
        process.queue.sync {
            process.fullBundleParams[key] = (downloadUrl: downloadUrl, fileSize: fileSize, sha256: sha256)
        }
    }

    private func recordedFullBundleParams(key: String) -> (downloadUrl: String, fileSize: Double, sha256: String)? {
        process.queue.sync { process.fullBundleParams[key] }
    }

    private func recordedVerifiedDiff(appVersion: String, bundleVersion: String) -> BundleDiffReconstruction.VerifiedDiffRecord? {
        process.queue.sync {
            process.verifiedDiffRecords[.init(appVersion: appVersion, bundleVersion: bundleVersion)]
        }
    }

    private func acquireVerificationSlot(_ key: String, step: BundleUpdateStep) throws -> BundleVerificationRun {
        try process.queue.sync {
            if process.clearingDownloads {
                throw BundleUpdateError(.cancelled, step)
            }
            if process.activeVerifications[key] != nil || process.activeDownload?.targetKey == key {
                throw BundleUpdateError(.invalidArgument, step)
            }
            let run = BundleVerificationRun()
            process.activeVerifications[key] = run
            return run
        }
    }

    private func publishVerification(_ run: BundleVerificationRun, _ publish: () throws -> Void = {}) throws {
        try process.queue.sync { try run.publish(publish) }
    }

    private func releaseVerificationSlot(_ key: String, run: BundleVerificationRun) {
        process.queue.sync {
            if process.activeVerifications[key] === run {
                process.activeVerifications.removeValue(forKey: key)
            }
            run.finish()
        }
    }

    private func acquireDownloadSlot(
        targetKey: String,
        step: BundleUpdateStep,
        verificationRun: BundleVerificationRun? = nil
    ) throws -> DownloadRun {
        try process.queue.sync {
            if process.clearingDownloads || verificationRun?.wasCancelledByClear == true {
                throw BundleUpdateError(.cancelled, step)
            }
            if process.activeDownload != nil ||
                (process.activeVerifications[targetKey] != nil && process.activeVerifications[targetKey] !== verificationRun) {
                throw BundleUpdateError(.invalidArgument, step)
            }
            let run = DownloadRun(owner: self, targetKey: targetKey)
            process.activeDownload = run
            return run
        }
    }

    private func completeDownload(_ run: DownloadRun, publish: () throws -> Void = {}) throws {
        try process.queue.sync {
            guard !run.delegate.wasCancelledByClear else { throw BundleUpdateError(.cancelled, .download) }
            try publish()
            run.published = true
        }
    }

    private func releaseDownloadSlot(_ run: DownloadRun) {
        run.session.finishTasksAndInvalidate()
        process.queue.sync {
            if process.activeDownload === run {
                process.activeDownload = nil
            }
            run.delegate.finishRun()
        }
    }

    /// OCDS §5.7: the highest progress percent already reported to JS for the
    /// in-flight download. Used to floor single-stream progress so the bar never
    /// moves backward at the concurrent→single-stream seam. Guarded by
    /// `listenerQueue`. Reset to 0 only on a genuine restart (permanent fallback).
    private var lastReportedProgress: Int = 0
    private var didEnterBackgroundObserver: NSObjectProtocol?

    override init() {
        super.init()
        registerBackgroundSnapshotObserver()
    }

    deinit {
        if let token = didEnterBackgroundObserver {
            NotificationCenter.default.removeObserver(token)
        }
    }

    /// On iOS, force-quit (user swipes the app off the App Switcher) cannot
    /// fire `URLSession`'s `didCompleteWithError` — SIGKILL leaves no time
    /// for callbacks. The kill is, however, *always* preceded by the app
    /// transitioning to the background. We hook that transition and kick
    /// off `cancel(byProducingResumeData:)` for any in-flight downloads so
    /// the resume blob lands on disk before the app can be terminated.
    /// Memory-pressure kills follow the same chain (the OS only reaps
    /// backgrounded apps under memory pressure), so this also covers OOM
    /// termination.
    ///
    /// Both `URLSession.getAllTasks(_:)` and `cancel(byProducingResumeData:)`
    /// deliver their results *asynchronously* via a closure — the resume
    /// data does not pop out synchronously. We wrap the work in a
    /// `beginBackgroundTask` window that extends the ~5s of guaranteed
    /// background runtime to ~30s so the closures actually have time to
    /// fire and write the few-KB blob before suspension. Persistence is
    /// best-effort: if iOS reaps us before the writer closure runs (very
    /// short background windows, expiration), the next launch simply
    /// re-downloads from scratch — a resume miss is correct, just slower.
    private func registerBackgroundSnapshotObserver() {
        didEnterBackgroundObserver = NotificationCenter.default.addObserver(
            forName: UIApplication.didEnterBackgroundNotification,
            object: nil,
            queue: .main
        ) { [weak self] _ in
            self?.snapshotResumeDataForBackgrounding()
        }
    }

    private func snapshotResumeDataForBackgrounding() {
        let snapshot: (DownloadRun, String)? = process.queue.sync {
            guard !process.clearingDownloads,
                  let run = process.activeDownload,
                  run.owner == ObjectIdentifier(self),
                  let filePath = run.filePath else { return nil }
            return (run, filePath)
        }
        guard let (run, filePath) = snapshot else { return }

        let resumeDataPath = "\(filePath).resume"
        let bgTaskName = "BundleUpdateResumeSnapshot"

        // bgTaskId is mutated from three escaping closures: the
        // beginBackgroundTask expiration handler (called on main),
        // session.getAllTasks's completion (NOT guaranteed to be main),
        // and group.notify on .main. Wrap reads/writes in a tiny holder
        // serialized through a dedicated queue so we cannot double-end
        // the background task or leak it. `endOnce` guarantees endBackgroundTask
        // is invoked exactly once across whichever closure reaches it first.
        final class BgTaskHolder {
            private let q = DispatchQueue(label: "so.onekey.bundleupdate.bgtask")
            private var id: UIBackgroundTaskIdentifier = .invalid
            func set(_ newId: UIBackgroundTaskIdentifier) {
                q.sync { id = newId }
            }
            func endOnce() {
                q.sync {
                    if id != .invalid {
                        UIApplication.shared.endBackgroundTask(id)
                        id = .invalid
                    }
                }
            }
        }
        let bgTask = BgTaskHolder()
        let started = UIApplication.shared.beginBackgroundTask(withName: bgTaskName) {
            // Expiration handler — system is reclaiming us. Best-effort end.
            bgTask.endOnce()
        }
        bgTask.set(started)
        OneKeyLog.info("BundleUpdate", "didEnterBackground: snapshotting resumeData for \(filePath)")

        run.session.getAllTasks { [weak self] tasks in
            let group = DispatchGroup()
            for task in tasks {
                guard let dl = task as? URLSessionDownloadTask, dl.state == .running else { continue }
                group.enter()
                dl.cancel(byProducingResumeData: { data in
                    defer { group.leave() }
                    if let data = data, data.count > 0 {
                        guard let self else { return }
                        self.process.queue.sync {
                            guard self.process.activeDownload === run,
                                  !self.process.clearingDownloads,
                                  run.filePath == filePath,
                                  !run.delegate.wasCancelledByClear else { return }
                            do {
                                let dir = (resumeDataPath as NSString).deletingLastPathComponent
                                if !FileManager.default.fileExists(atPath: dir) {
                                    try FileManager.default.createDirectory(atPath: dir, withIntermediateDirectories: true)
                                }
                                try data.write(to: URL(fileURLWithPath: resumeDataPath), options: .atomic)
                                OneKeyLog.info("BundleUpdate", "didEnterBackground: persisted resumeData (\(data.count) bytes) at \(resumeDataPath)")
                            } catch {
                                OneKeyLog.warn("BundleUpdate", "didEnterBackground: failed to persist resumeData: \(error)")
                            }
                        }
                    } else {
                        OneKeyLog.info("BundleUpdate", "didEnterBackground: cancel produced no resumeData (task may have just completed)")
                    }
                })
            }
            group.notify(queue: .main) {
                bgTask.endOnce()
            }
        }
    }

    private func sendEvent(type: String, progress: Int = 0, message: String = "") {
        let event = BundleDownloadEvent(type: type, progress: Double(progress), message: message)
        let currentListeners = listenerQueue.sync { self.listeners }
        for listener in currentListeners {
            do {
                listener.callback(event)
            } catch {
                OneKeyLog.error("BundleUpdate", "Error sending event: \(error)")
            }
        }
    }

    func addDownloadListener(callback: @escaping (BundleDownloadEvent) -> Void) throws -> Double {
        return listenerQueue.sync {
            let id = nextListenerId
            nextListenerId += 1
            listeners.append(BundleListener(id: id, callback: callback))
            OneKeyLog.debug("BundleUpdate", "addDownloadListener: id=\(id), totalListeners=\(listeners.count)")
            return id
        }
    }

    func removeDownloadListener(id: Double) throws {
        listenerQueue.sync {
            listeners.removeAll { $0.id == id }
            OneKeyLog.debug("BundleUpdate", "removeDownloadListener: id=\(id), totalListeners=\(listeners.count)")
        }
    }

    private func downloadVerifiedFile(
        downloadUrl: String,
        filePath: String,
        sha256: String,
        rangeTaskId: String,
        run: DownloadRun,
        step: BundleUpdateStep,
        emitErrorEvents: Bool
    ) async throws {
        let runDelegate = run.delegate
        let resumeDataPath = "\(filePath).resume"

        self.sendEvent(type: "update/start")
        // §5.7: a fresh download starts the progress floor at 0; the
        // concurrent path raises it as bytes land (below), and a transient
        // single-stream fallback keeps it.
        try process.queue.sync {
            guard !runDelegate.wasCancelledByClear else { throw BundleUpdateError(.cancelled, step) }
            run.filePath = filePath
            run.rangeTaskId = rangeTaskId
        }
        listenerQueue.sync { lastReportedProgress = 0 }
        if runDelegate.wasCancelledByClear {
            throw BundleUpdateError(.cancelled, step)
        }

        let progressListenerId = RangeDownloader.shared.addListener { [weak self] event in
            guard let self = self,
                  event.channel.stringValue == DownloadChannel.bundle.stringValue,
                  event.taskId == rangeTaskId,
                  event.type == "progress",
                  !runDelegate.wasCancelledByClear else { return }
            let floored: Int = self.listenerQueue.sync {
                let clamped = max(self.lastReportedProgress, Int(event.progress))
                self.lastReportedProgress = clamped
                return clamped
            }
            self.sendEvent(type: "update/downloading", progress: floored)
        }
        defer { RangeDownloader.shared.removeListener(progressListenerId) }

        let maxConcurrentAttempts = 3
        var concurrentAttempt = 0
        var rangeClass: RangeDownloadClass = .fallbackPermanent
        var rangeReason: String?
        var rangeKind: RangeFallbackClass?
        var rangeError: Error?
        while true {
            concurrentAttempt += 1
            (rangeClass, _, rangeReason, rangeKind, rangeError) = await RangeDownloader.shared.download(
                channel: .bundle,
                taskId: rangeTaskId,
                urlString: downloadUrl,
                filePath: filePath,
                expectedSha256: nil,
                segmentCount: nil,
                minConcurrentBytes: nil,
                maxTotalBytes: DownloadDelegate.maxDownloadBytes,
                isCancelled: { runDelegate.wasCancelledByClear }
            )
            if runDelegate.wasCancelledByClear {
                throw BundleUpdateError(.cancelled, step)
            }
            if rangeClass == .completed {
                break
            }
            if let rangeError {
                let updateError = bundleUpdateError(rangeError, step: step)
                if updateError.code != .network && updateError.code != .timeout {
                    if emitErrorEvents {
                        self.sendEvent(type: "update/error", message: updateError.description)
                    }
                    throw updateError
                }
            }
            if rangeKind == .redirectRejected || rangeKind == .checksumMismatch {
                let updateError = BundleUpdateError(rangeKind == .redirectRejected ? .security : .integrity, step)
                if emitErrorEvents {
                    self.sendEvent(type: "update/error", message: updateError.description)
                }
                throw updateError
            }
            if rangeClass == .fallbackTransient,
               concurrentAttempt < maxConcurrentAttempts {
                OneKeyLog.info("BundleUpdate", "downloadBundle: concurrent transient fallback (\(rangeReason ?? "")), resuming surviving segments (attempt \(concurrentAttempt)/\(maxConcurrentAttempts))")
                try? await Task.sleep(nanoseconds: 1_500_000_000)
                if runDelegate.wasCancelledByClear {
                    throw BundleUpdateError(.cancelled, step)
                }
                continue
            }
            break
        }
        if runDelegate.wasCancelledByClear {
            throw BundleUpdateError(.cancelled, step)
        }

        if rangeClass != .completed {
            if rangeClass == .fallbackTransient {
                OneKeyLog.info("BundleUpdate", "downloadBundle: concurrent transient fallback persists (\(rangeReason ?? "")), using single-stream (segments preserved for resume)")
            } else {
                OneKeyLog.info("BundleUpdate", "downloadBundle: concurrent permanent fallback (\(rangeReason ?? "")), using single-stream")
                RangeDownloader.shared.discardArtifacts(filePath: filePath)
            }
        } else {
            OneKeyLog.info("BundleUpdate", "downloadBundle: concurrent finished, verifying SHA256...")
            guard let downloadedHash = BundleUpdateStore.calculateSHA256(filePath) else {
                throw BundleUpdateError(.storage, step)
            }
            if !BundleCryptoCore.secureEqualHex(downloadedHash, sha256) {
                try? FileManager.default.removeItem(atPath: filePath)
                let err = BundleUpdateError(.integrity, step)
                if emitErrorEvents {
                    self.sendEvent(type: "update/error", message: err.description)
                }
                throw err
            }
            try? FileManager.default.removeItem(atPath: resumeDataPath)
            if runDelegate.wasCancelledByClear {
                throw BundleUpdateError(.cancelled, step)
            }
            return
        }

        guard let url = URL(string: downloadUrl) else {
            OneKeyLog.error("BundleUpdate", "downloadBundle: invalid URL: \(downloadUrl)")
            throw BundleUpdateError(.invalidArgument, step)
        }

        let session = run.session
        let persistedResumeData: Data? = {
            guard FileManager.default.fileExists(atPath: resumeDataPath),
                  let data = try? Data(contentsOf: URL(fileURLWithPath: resumeDataPath)),
                  data.count > 0 else { return nil }
            return data
        }()

        OneKeyLog.info("BundleUpdate", "downloadBundle: starting single-stream download (resumeBytes=\(persistedResumeData?.count ?? 0))...")

        let request = URLRequest(url: url)
        let delegate = runDelegate
        let progressFloor = (rangeClass == .fallbackTransient)
            ? self.listenerQueue.sync { self.lastReportedProgress }
            : 0
        self.listenerQueue.sync { self.lastReportedProgress = progressFloor }
        delegate.onProgress = { [weak self, weak delegate] progress in
            guard let self, let delegate, !delegate.wasCancelledByClear else { return }
            let floored: Int = self.listenerQueue.sync {
                let clamped = max(self.lastReportedProgress, progress)
                self.lastReportedProgress = clamped
                return clamped
            }
            self.sendEvent(type: "update/downloading", progress: floored)
        }

        if runDelegate.wasCancelledByClear {
            throw BundleUpdateError(.cancelled, step)
        }

        let downloadOutcome: Result<(URL, URLResponse), Error>
        do {
            let value = try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<(URL, URLResponse), Error>) in
                process.queue.sync {
                    guard delegate.setContinuation(continuation) else { return }
                    let task: URLSessionDownloadTask
                    if let resumeData = persistedResumeData {
                        task = session.downloadTask(withResumeData: resumeData)
                    } else {
                        task = session.downloadTask(with: request)
                    }
                    task.resume()
                }
            }
            downloadOutcome = .success(value)
        } catch {
            downloadOutcome = .failure(error)
        }

        switch downloadOutcome {
        case .failure(let error):
            if let resumeData = delegate.lastResumeData {
                do {
                    let dir = (resumeDataPath as NSString).deletingLastPathComponent
                    if !FileManager.default.fileExists(atPath: dir) {
                        try FileManager.default.createDirectory(atPath: dir, withIntermediateDirectories: true)
                    }
                    try resumeData.write(to: URL(fileURLWithPath: resumeDataPath), options: .atomic)
                    OneKeyLog.info("BundleUpdate", "downloadBundle: persisted resumeData (\(resumeData.count) bytes) at \(resumeDataPath)")
                } catch {
                    OneKeyLog.warn("BundleUpdate", "downloadBundle: failed to persist resumeData: \(error)")
                }
            } else {
                try? FileManager.default.removeItem(atPath: resumeDataPath)
            }
            let nsError = error as NSError
            OneKeyLog.error("BundleUpdate", "downloadBundle: download failed: \(nsError.domain) \(nsError.code) \(nsError.localizedDescription)")
            let intentionallyCancelled = delegate.wasCancelledByClear
            let updateError = intentionallyCancelled
                ? BundleUpdateError(.cancelled, step)
                : (delegate.wasInsecureRedirectRejected
                    ? BundleUpdateError(.security, step)
                    : bundleUpdateError(error, step: step))
            if !intentionallyCancelled && emitErrorEvents {
                self.sendEvent(type: "update/error", message: updateError.description)
            }
            throw updateError

        case .success(let (tempURL, response)):
            defer { try? FileManager.default.removeItem(at: tempURL) }
            guard !runDelegate.wasCancelledByClear else { throw BundleUpdateError(.cancelled, step) }
            try self.validateManagedArchive(tempURL.path, step: step)
            try? FileManager.default.removeItem(atPath: resumeDataPath)

            if let httpResponse = response as? HTTPURLResponse,
               let responseUrl = httpResponse.url,
               responseUrl.scheme?.lowercased() != "https" {
                OneKeyLog.error("BundleUpdate", "downloadBundle: redirected to non-HTTPS URL: \(responseUrl)")
                throw BundleUpdateError(.security, step)
            }

            guard let httpResponse = response as? HTTPURLResponse,
                  httpResponse.statusCode == 200 || httpResponse.statusCode == 206 else {
                let statusCode = (response as? HTTPURLResponse)?.statusCode ?? -1
                OneKeyLog.error("BundleUpdate", "downloadBundle: HTTP error, statusCode=\(statusCode)")
                let updateError = BundleUpdateError(.http, step, httpStatus: statusCode)
                if emitErrorEvents {
                    self.sendEvent(type: "update/error", message: updateError.description)
                }
                throw updateError
            }

            OneKeyLog.info("BundleUpdate", "downloadBundle: download finished, HTTP \(httpResponse.statusCode), moving to destination...")

            let destDir = (filePath as NSString).deletingLastPathComponent
            if !FileManager.default.fileExists(atPath: destDir) {
                try FileManager.default.createDirectory(atPath: destDir, withIntermediateDirectories: true)
            }
            if FileManager.default.fileExists(atPath: filePath) {
                try FileManager.default.removeItem(atPath: filePath)
            }
            try FileManager.default.moveItem(at: tempURL, to: URL(fileURLWithPath: filePath))

            OneKeyLog.info("BundleUpdate", "downloadBundle: verifying SHA256...")
            guard let downloadedHash = BundleUpdateStore.calculateSHA256(filePath) else {
                throw BundleUpdateError(.storage, step)
            }
            if !BundleCryptoCore.secureEqualHex(downloadedHash, sha256) {
                let reason = BundleUpdateStore.lastSHA256FailureReason() ?? "MISMATCH"
                try? FileManager.default.removeItem(atPath: filePath)
                OneKeyLog.error("BundleUpdate", "downloadBundle: SHA256 verification failed after download, reason=\(reason)")
                let err = BundleUpdateError(.integrity, step)
                if emitErrorEvents {
                    self.sendEvent(type: "update/error", message: err.description)
                }
                throw err
            }

            RangeDownloader.shared.discardArtifacts(filePath: filePath)

            if runDelegate.wasCancelledByClear {
                throw BundleUpdateError(.cancelled, step)
            }
        }
    }

    func downloadBundle(params: BundleDownloadParams) throws -> Promise<BundleDownloadResult> {
        return updatePromise(step: .download) { [weak self] in
            guard let self = self else { throw BundleUpdateError(.unknown, .download) }

            let appVersion = params.latestVersion
            let bundleVersion = params.bundleVersion
            let downloadUrl = params.downloadUrl
            let sha256 = BundleDiffReconstruction.normalizeSha256(params.sha256) ?? params.sha256.lowercased()

            OneKeyLog.info("BundleUpdate", "downloadBundle: appVersion=\(appVersion), bundleVersion=\(bundleVersion), fileSize=\(params.fileSize), url=\(downloadUrl)")

            guard appVersion.isSafeVersionString, bundleVersion.isSafeVersionString else {
                OneKeyLog.error("BundleUpdate", "downloadBundle: invalid version string format: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
                throw BundleUpdateError(.invalidArgument, .download)
            }

            guard downloadUrl.hasPrefix("https://") else {
                OneKeyLog.error("BundleUpdate", "downloadBundle: URL is not HTTPS: \(downloadUrl)")
                throw BundleUpdateError(.security, .download)
            }

            let bundleKey = "\(appVersion)-\(bundleVersion)"

            let run = try self.acquireDownloadSlot(targetKey: bundleKey, step: .download)
            let runDelegate = run.delegate
            defer { self.releaseDownloadSlot(run) }

            self.recordFullBundleParams(key: bundleKey, downloadUrl: downloadUrl, fileSize: params.fileSize, sha256: sha256)

            let downloadDir = BundleUpdateStore.downloadBundleDir()
            let fileName = "\(bundleKey).zip"
            let filePath = (downloadDir as NSString).appendingPathComponent(fileName)
            let resumeDataPath = "\(filePath).resume"

            let fullResult = BundleDownloadResult(
                downloadUrl: downloadUrl,
                latestVersion: appVersion,
                bundleVersion: bundleVersion,
                sha256: sha256,
                diffDownloadUrl: nil,
                diffFileSize: nil,
                diffSha256: nil,
                diffSignature: nil
            )

            OneKeyLog.info("BundleUpdate", "downloadBundle: filePath=\(filePath)")

            // Check if full file already exists and is valid
            if FileManager.default.fileExists(atPath: filePath) ||
                (try? FileManager.default.destinationOfSymbolicLink(atPath: filePath)) != nil {
                OneKeyLog.info("BundleUpdate", "downloadBundle: file already exists, verifying SHA256...")
                if self.verifyBundleSHA256(filePath, sha256: sha256) {
                    OneKeyLog.info("BundleUpdate", "downloadBundle: existing file SHA256 valid, skipping download")
                    try? FileManager.default.removeItem(atPath: resumeDataPath)
                    try self.completeDownload(run)
                    self.sendEvent(type: "update/complete")
                    return fullResult
                } else {
                    OneKeyLog.warn("BundleUpdate", "downloadBundle: existing file SHA256 mismatch, re-downloading")
                    try FileManager.default.removeItem(atPath: filePath)
                    try? FileManager.default.removeItem(atPath: resumeDataPath)
                }
            }

            let diffDir = (downloadDir as NSString).appendingPathComponent(BundleDiffReconstruction.diffDownloadDirName)
            let diffFileName = "\(bundleKey)\(BundleDiffReconstruction.diffZipExtension)"
            let diffFilePath = (diffDir as NSString).appendingPathComponent(diffFileName)
            let diffMetaPath = (diffDir as NSString).appendingPathComponent("\(bundleKey)\(BundleDiffReconstruction.diffMetaExtension)")
            let existingDesc = BundleDiffReconstruction.readDescriptor(
                fromPath: diffMetaPath,
                expectedAppVersion: appVersion,
                expectedBundleVersion: bundleVersion
            )
            let isFallbackMarked = self.isDiffFallbackMarked(bundleKey, sha256: sha256) ||
                (existingDesc?.fallbackRequired == true &&
                 BundleDiffReconstruction.constantTimeEqualHex(existingDesc?.sha256 ?? "", sha256))

            // Priority 1: Incremental diff download if usable diff params are provided
            let usableDiff = BundleDiffReconstruction.isUsableDiffParams(
                diffDownloadUrl: params.diffDownloadUrl,
                diffSha256: params.diffSha256,
                diffSignature: params.diffSignature,
                isFallbackMarked: isFallbackMarked
            )
            if usableDiff,
               let diffUrl = params.diffDownloadUrl,
               let diffSha256 = BundleDiffReconstruction.normalizeSha256(params.diffSha256),
               let diffSignature = params.diffSignature {
                if !FileManager.default.fileExists(atPath: diffDir) {
                    try? FileManager.default.createDirectory(atPath: diffDir, withIntermediateDirectories: true)
                }
                let diffResumeDataPath = "\(diffFilePath).resume"
                let diffDescriptor = BundleDiffReconstruction.descriptorPreservingVerifiedSignature(
                    newDescriptor: BundleDiffReconstruction.DiffArchiveDescriptor(
                        appVersion: appVersion,
                        bundleVersion: bundleVersion,
                        downloadUrl: downloadUrl,
                        fileSize: params.fileSize,
                        sha256: sha256,
                        diffSha256: diffSha256,
                        signature: nil
                    ),
                    existingDescriptor: existingDesc
                )
                let diffResult = BundleDownloadResult(
                    downloadUrl: downloadUrl,
                    latestVersion: appVersion,
                    bundleVersion: bundleVersion,
                    sha256: sha256,
                    diffDownloadUrl: diffUrl,
                    diffFileSize: params.diffFileSize,
                    diffSha256: diffSha256,
                    diffSignature: diffSignature
                )

                if FileManager.default.fileExists(atPath: diffFilePath) {
                    if self.verifyBundleSHA256(diffFilePath, sha256: diffSha256) {
                        OneKeyLog.info("BundleUpdate", "downloadBundle: existing diff file SHA256 valid, skipping download")
                        try? FileManager.default.removeItem(atPath: diffResumeDataPath)
                        try self.completeDownload(run) {
                            guard BundleDiffReconstruction.writeDescriptor(diffDescriptor, toPath: diffMetaPath) else {
                                throw BundleUpdateError(.storage, .download)
                            }
                        }
                        self.sendEvent(type: "update/complete")
                        return diffResult
                    } else {
                        try? FileManager.default.removeItem(atPath: diffFilePath)
                        try? FileManager.default.removeItem(atPath: diffResumeDataPath)
                        try? FileManager.default.removeItem(atPath: diffMetaPath)
                    }
                }

                do {
                    try await self.downloadVerifiedFile(
                        downloadUrl: diffUrl,
                        filePath: diffFilePath,
                        sha256: diffSha256,
                        rangeTaskId: BundleDiffReconstruction.rangeTaskId(bundleKey: bundleKey, isDiff: true),
                        run: run,
                        step: .download,
                        emitErrorEvents: false
                    )
                    try self.completeDownload(run) {
                        guard BundleDiffReconstruction.writeDescriptor(diffDescriptor, toPath: diffMetaPath) else {
                            throw BundleUpdateError(.storage, .download)
                        }
                    }
                    self.sendEvent(type: "update/complete")
                    OneKeyLog.info("BundleUpdate", "downloadBundle: diff download completed successfully, appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
                    return diffResult
                } catch {
                    let intentionallyCancelled = runDelegate.wasCancelledByClear
                    let updateError = intentionallyCancelled
                        ? BundleUpdateError(.cancelled, .download)
                        : bundleUpdateError(error, step: .download)
                    if updateError.code == .cancelled || updateError.code == .storage {
                        throw updateError
                    }
                    if BundleDiffReconstruction.isRetryableDiffDownloadError(code: updateError.code.rawValue) {
                        OneKeyLog.warn("BundleUpdate", "downloadBundle: transient diff download interruption for \(bundleKey), preserving resume progress: \(updateError)")
                        BundleDiffReconstruction.writeDescriptor(diffDescriptor, toPath: diffMetaPath)
                        self.sendEvent(type: "update/error", message: updateError.description)
                        throw updateError
                    }
                    OneKeyLog.warn("BundleUpdate", "downloadBundle: diff download failed for \(bundleKey), falling back to full bundle: \(error)")
                    self.markDiffFallback(bundleKey, sha256: sha256)
                    try? FileManager.default.removeItem(atPath: diffFilePath)
                    try? FileManager.default.removeItem(atPath: diffResumeDataPath)
                    try? FileManager.default.removeItem(atPath: diffMetaPath)
                    RangeDownloader.shared.discardArtifacts(filePath: diffFilePath)
                    runDelegate.resetForAttempt()
                }
            }

            // Priority 2: Full bundle download
            try await self.downloadVerifiedFile(
                downloadUrl: downloadUrl,
                filePath: filePath,
                sha256: sha256,
                rangeTaskId: BundleDiffReconstruction.rangeTaskId(bundleKey: bundleKey, isDiff: false),
                run: run,
                step: .download,
                emitErrorEvents: true
            )
            try self.completeDownload(run)
            self.sendEvent(type: "update/complete")
            OneKeyLog.info("BundleUpdate", "downloadBundle: completed successfully, appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
            return fullResult
        }
    }

    private func validateManagedArchive(_ path: String, step: BundleUpdateStep) throws {
        guard let attrs = try? FileManager.default.attributesOfItem(atPath: path),
              attrs[.type] as? FileAttributeType == .typeRegular,
              let size = attrs[.size] as? Int64 else {
            throw BundleUpdateError(.storage, step)
        }
        guard size <= DownloadDelegate.maxDownloadBytes else {
            throw BundleUpdateError(.security, step)
        }
    }

    private func verifyBundleSHA256(_ bundlePath: String, sha256: String) -> Bool {
        guard (try? validateManagedArchive(bundlePath, step: .download)) != nil,
              let calculated = BundleUpdateStore.calculateSHA256(bundlePath) else {
            OneKeyLog.error("BundleUpdate", "verifyBundleSHA256: failed to calculate SHA256 for: \(bundlePath)")
            return false
        }
        let isValid = BundleCryptoCore.secureEqualHex(calculated, sha256)
        OneKeyLog.debug("BundleUpdate", "verifyBundleSHA256: path=\(bundlePath), expected=\(sha256.prefix(16))..., calculated=\(calculated.prefix(16))..., valid=\(isValid)")
        return isValid
    }

    func downloadBundleASC(params: BundleDownloadASCParams) throws -> Promise<Void> {
        return updatePromise(step: .downloadASC) {
            let appVersion = params.latestVersion
            let bundleVersion = params.bundleVersion
            let signature = params.signature

            OneKeyLog.info("BundleUpdate", "downloadBundleASC: appVersion=\(appVersion), bundleVersion=\(bundleVersion), signatureLength=\(signature.count)")

            guard appVersion.isSafeVersionString, bundleVersion.isSafeVersionString else {
                OneKeyLog.error("BundleUpdate", "downloadBundleASC: invalid version string format: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
                throw BundleUpdateError(.invalidArgument, .downloadASC)
            }
            guard signature.utf8.count <= 64 * 1024 else {
                OneKeyLog.error("BundleUpdate", "downloadBundleASC: signature exceeds maximum allowed size (\(signature.utf8.count))")
                throw BundleUpdateError(.invalidArgument, .downloadASC)
            }

            let storageKey = "\(appVersion)-\(bundleVersion)"
            let sigPath = (BundleUpdateStore.signatureFilePath(storageKey) as NSString).resolvingSymlinksInPath
            let baseAscDir = (BundleUpdateStore.ascDir() as NSString).resolvingSymlinksInPath
            guard sigPath.hasPrefix(baseAscDir + "/") else {
                OneKeyLog.error("BundleUpdate", "downloadBundleASC: signature path outside asc directory: \(sigPath)")
                throw BundleUpdateError(.security, .downloadASC)
            }

        }
    }

    private func extractZipWithSafetyChecks(zipPath: String, to destination: String, step: BundleUpdateStep) throws {
        let maxZipFileSize: UInt64 = 512 * 1024 * 1024 // 512 MB
        let maxUncompressedSize: UInt64 = 512 * 1024 * 1024
        if let attrs = try? FileManager.default.attributesOfItem(atPath: zipPath),
           let fileSize = attrs[.size] as? UInt64 {
            OneKeyLog.info("BundleUpdate", "\(step.rawValue): zip file size=\(fileSize) bytes")
            if fileSize > maxZipFileSize {
                OneKeyLog.error("BundleUpdate", "\(step.rawValue): zip file too large (\(fileSize) > \(maxZipFileSize))")
                throw BundleUpdateError(.security, step)
            }
        }

        var payloadError: NSError?
        let payloadSize = SSZipArchive.payloadSizeForArchive(atPath: zipPath, error: &payloadError)
        if payloadSize.uint64Value > maxUncompressedSize {
            OneKeyLog.error("BundleUpdate", "\(step.rawValue): zip payload size exceeds limit (payloadSize=\(payloadSize), max=\(maxUncompressedSize))")
            throw BundleUpdateError(.security, step)
        }
        if payloadError != nil || payloadSize.uint64Value == 0 {
            OneKeyLog.error("BundleUpdate", "\(step.rawValue): zip payload size invalid or empty (payloadSize=\(payloadSize), error=\(String(describing: payloadError)))")
            throw BundleUpdateError(.integrity, step)
        }

        if FileManager.default.fileExists(atPath: destination) {
            try? FileManager.default.removeItem(atPath: destination)
        }

        OneKeyLog.info("BundleUpdate", "\(step.rawValue): extracting zip to \(destination)...")
        do {
            try SSZipArchive.unzipFile(atPath: zipPath, toDestination: destination, overwrite: true, password: nil)
            OneKeyLog.info("BundleUpdate", "\(step.rawValue): extraction completed")
        } catch {
            let nsErr = error as NSError
            OneKeyLog.error("BundleUpdate", "\(step.rawValue): unzip failed: domain=\(nsErr.domain) code=\(nsErr.code) desc=\(nsErr.localizedDescription)")
            try? FileManager.default.removeItem(atPath: destination)
            throw BundleUpdateError(.storage, step)
        }

        if !BundleCryptoCore.validateExtractedPathSafety(destination) {
            OneKeyLog.error("BundleUpdate", "\(step.rawValue): path traversal or symlink attack detected")
            try? FileManager.default.removeItem(atPath: destination)
            throw BundleUpdateError(.security, step)
        }

        let maxZipEntries = 4096
        if let enumerator = FileManager.default.enumerator(atPath: destination) {
            var entryCount = 0
            var totalUncompressedBytes: UInt64 = 0
            while let relPath = enumerator.nextObject() as? String {
                entryCount += 1
                if entryCount > maxZipEntries {
                    OneKeyLog.error("BundleUpdate", "\(step.rawValue): extracted entry count exceeds maximum allowed (\(maxZipEntries))")
                    try? FileManager.default.removeItem(atPath: destination)
                    throw BundleUpdateError(.security, step)
                }
                let fullPath = (destination as NSString).appendingPathComponent(relPath)
                if let attrs = try? FileManager.default.attributesOfItem(atPath: fullPath),
                   let fileSize = attrs[.size] as? UInt64 {
                    totalUncompressedBytes += fileSize
                    if totalUncompressedBytes > maxUncompressedSize {
                        OneKeyLog.error("BundleUpdate", "\(step.rawValue): extracted size exceeds maximum allowed (\(maxUncompressedSize))")
                        try? FileManager.default.removeItem(atPath: destination)
                        throw BundleUpdateError(.security, step)
                    }
                }
            }
        }
    }

    private func verifyAndExtractFullZip(
        filePath: String,
        diffMetaPath: String,
        stagingDir: String,
        destination: String,
        appVersion: String,
        bundleVersion: String,
        sha256: String,
        signature: String,
        isInstalled: Bool,
        skipGPG: Bool,
        verificationContext: BundleVerificationRun
    ) throws {
        if verificationContext.wasCancelledByClear {
            throw BundleUpdateError(.cancelled, .verifyASC)
        }

        try validateManagedArchive(filePath, step: .verifyASC)
        let expectedMetadataSha: String?
        if skipGPG {
            expectedMetadataSha = nil
        } else {
            guard let metadataSha = BundleUpdateStore.readMetadataFileSha256(
                signature,
                expectedAppVersion: appVersion,
                expectedBundleVersion: bundleVersion,
                expectedAppType: "ios"
            ), !metadataSha.isEmpty else {
                throw BundleUpdateError(.integrity, .verifyASC)
            }
            expectedMetadataSha = metadataSha

            guard let calculated = BundleUpdateStore.calculateSHA256(filePath) else {
                throw BundleUpdateError(.storage, .verifyASC)
            }
            guard BundleCryptoCore.secureEqualHex(calculated, sha256) else {
                throw BundleUpdateError(.integrity, .verifyASC)
            }
        }

        let fm = FileManager.default
        let bundlePath = isInstalled ? destination : stagingDir
        if !isInstalled { BundleDiffReconstruction.retainStagingDirectory(stagingDir) }
        defer {
            if !isInstalled { BundleDiffReconstruction.releaseStagingDirectory(stagingDir) }
        }
        if verificationContext.wasCancelledByClear {
            throw BundleUpdateError(.cancelled, .verifyASC)
        }
        if !isInstalled {
            let stagingParent = (stagingDir as NSString).deletingLastPathComponent
            try fm.createDirectory(atPath: stagingParent, withIntermediateDirectories: true)
            try extractZipWithSafetyChecks(zipPath: filePath, to: stagingDir, step: .verifyASC)
        }

        let metadataPath = (bundlePath as NSString).appendingPathComponent("metadata.json")
        guard fm.fileExists(atPath: metadataPath) else {
            throw BundleUpdateError(isInstalled ? .integrity : .missing, .verifyASC)
        }
        if let expectedMetadataSha {
            guard let actualMetadataSha = BundleUpdateStore.calculateSHA256(metadataPath),
                  BundleCryptoCore.secureEqualHex(actualMetadataSha, expectedMetadataSha) else {
                throw BundleUpdateError(.integrity, .verifyASC)
            }
        }
        guard let data = fm.contents(atPath: metadataPath),
              let metadata = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
            throw BundleUpdateError(isInstalled ? .integrity : .missing, .verifyASC)
        }
        guard BundleCryptoCore.validateExtractedPathSafety(bundlePath),
              BundleUpdateStore.validateAllFilesInDir(bundlePath, metadata: metadata, appVersion: appVersion, bundleVersion: bundleVersion) else {
            throw BundleUpdateError(.integrity, .verifyASC)
        }
        guard BundleUpdateStore.validateBundlePairCompatibility(bundleDirPath: bundlePath, metadata: metadata) else {
            throw BundleUpdateError(isInstalled ? .integrity : .incompatible, .verifyASC)
        }

        let folderName = "\(appVersion)-\(bundleVersion)"
        try publishVerification(verificationContext) {
            if !isInstalled {
                try BundleCryptoCore.safeRename(fromPath: stagingDir, toPath: destination)
            }
            process.verifiedDiffRecords.removeValue(forKey: .init(appVersion: appVersion, bundleVersion: bundleVersion))
            if (diffMetaPath as NSString).resolvingSymlinksInPath
                .hasPrefix((BundleUpdateStore.downloadBundleDir() as NSString).resolvingSymlinksInPath + "/") {
                try? fm.removeItem(atPath: diffMetaPath)
            }
        }
        OneKeyLog.info("BundleUpdate", "verifyBundleASC: full bundle verified for \(folderName)")
    }

    private func collectCandidateOtaDirs(appVersion: String, excludingFolder: String) -> [String] {
        let fm = FileManager.default
        let bundleDir = BundleUpdateStore.bundleDir()
        var candidates: [String] = []
        var seen = Set<String>()

        if let activeDir = BundleUpdateStore.currentBundleDir(),
           let activeFolder = BundleUpdateStore.currentBundleVersion(),
           activeFolder != excludingFolder,
           activeFolder != BundleDiffReconstruction.stagingRootDirName,
           BundleUpdateStore.bundleVersion(inFolder: activeFolder, appVersion: appVersion) != nil {
            var isDir: ObjCBool = false
            if fm.fileExists(atPath: activeDir, isDirectory: &isDir), isDir.boolValue {
                candidates.append(activeDir)
                seen.insert(activeFolder)
            }
        }

        if let contents = try? fm.contentsOfDirectory(atPath: bundleDir) {
            for name in contents.sorted(by: >) {
                if name == "asc" ||
                   name == BundleDiffReconstruction.stagingRootDirName ||
                   name == excludingFolder ||
                   seen.contains(name) {
                    continue
                }
                guard BundleUpdateStore.bundleVersion(inFolder: name, appVersion: appVersion) != nil else {
                    continue
                }
                let fullPath = (bundleDir as NSString).appendingPathComponent(name)
                var isDir: ObjCBool = false
                if fm.fileExists(atPath: fullPath, isDirectory: &isDir), isDir.boolValue {
                    candidates.append(fullPath)
                    seen.insert(name)
                }
            }
        }
        return candidates
    }

    private func copyBuiltinBundleFile(relativePath: String, destinationPath: String, expectedHash: String) -> Bool {
        guard let resourcePath = Bundle.main.resourcePath else { return false }
        let fm = FileManager.default
        let candidateRelativePaths = BundleDiffReconstruction.builtinCandidateRelativePaths(for: relativePath)
        for candidateRel in candidateRelativePaths {
            guard let sourcePath = BundleDiffReconstruction.confinedExistingFilePath(
                baseDir: resourcePath,
                relativePath: candidateRel
            ) else { continue }
            guard let actualHash = BundleUpdateStore.calculateSHA256(sourcePath),
                  BundleCryptoCore.secureEqualHex(actualHash, expectedHash) else {
                continue
            }
            do {
                if fm.fileExists(atPath: destinationPath) {
                    try fm.removeItem(atPath: destinationPath)
                }
                try fm.copyItem(atPath: sourcePath, toPath: destinationPath)
                return true
            } catch {
                try? fm.removeItem(atPath: destinationPath)
            }
        }
        // Fallback for Xcode folder reference `web-embed` resolved via Bundle.main.path(forResource:ofType:)
        if relativePath.hasPrefix("web-embed/"),
           let webEmbedBaseDir = Bundle.main.path(forResource: "web-embed", ofType: nil) {
            let subPath = String(relativePath.dropFirst("web-embed/".count))
            if let sourcePath = BundleDiffReconstruction.confinedExistingFilePath(
                baseDir: webEmbedBaseDir,
                relativePath: subPath
            ),
               let actualHash = BundleUpdateStore.calculateSHA256(sourcePath),
               BundleCryptoCore.secureEqualHex(actualHash, expectedHash) {
                do {
                    if fm.fileExists(atPath: destinationPath) {
                        try fm.removeItem(atPath: destinationPath)
                    }
                    try fm.copyItem(atPath: sourcePath, toPath: destinationPath)
                    return true
                } catch {
                    try? fm.removeItem(atPath: destinationPath)
                }
            }
        }
        return false
    }

    private func reconstructAndVerifyDiffZip(
        diffFilePath: String,
        diffMetaPath: String,
        stagingDir: String,
        destination: String,
        appVersion: String,
        bundleVersion: String,
        descriptor: BundleDiffReconstruction.DiffArchiveDescriptor,
        diffSignature: String,
        signature: String,
        isInstalled: Bool,
        skipGPG: Bool,
        verificationContext: BundleVerificationRun
    ) throws {
        let fm = FileManager.default
        let folderName = "\(appVersion)-\(bundleVersion)"
        let diffSha256 = descriptor.diffSha256

        if verificationContext.wasCancelledByClear {
            throw BundleUpdateError(.cancelled, .verifyASC)
        }

        var expectedDiffManifestSha256: String?
        var expectedMetadataSha256: String?
        if !skipGPG {
            guard let diffSha = BundleUpdateStore.readMetadataFileSha256(
                diffSignature,
                expectedAppVersion: appVersion,
                expectedBundleVersion: bundleVersion,
                expectedAppType: "ios",
                expectedFileName: "diff-manifest.json"
            ), !diffSha.isEmpty else {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: diffSignature GPG verification failed")
                throw BundleUpdateError(.integrity, .verifyASC)
            }
            guard let metaSha = BundleUpdateStore.readMetadataFileSha256(
                signature,
                expectedAppVersion: appVersion,
                expectedBundleVersion: bundleVersion,
                expectedAppType: "ios",
                expectedFileName: "metadata.json"
            ), !metaSha.isEmpty else {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: metadata signature GPG verification failed")
                throw BundleUpdateError(.integrity, .verifyASC)
            }
            expectedDiffManifestSha256 = diffSha.lowercased()
            expectedMetadataSha256 = metaSha.lowercased()
        }

        try validateManagedArchive(diffFilePath, step: .verifyASC)
        // a. Verify .diff.zip SHA-256 before checking isInstalled so a corrupted
        // managed diff archive cannot pass ASC verification.
        guard let actualDiffZipSha = BundleUpdateStore.calculateSHA256(diffFilePath),
              BundleCryptoCore.secureEqualHex(actualDiffZipSha, diffSha256) else {
            throw BundleDiffReconstructionError.integrityFailed("diff zip sha256 mismatch")
        }

        func publishVerifiedDiff(metadataSha: String, promote: Bool) throws {
            let verifiedDescriptor = BundleDiffReconstruction.DiffArchiveDescriptor(
                appVersion: appVersion,
                bundleVersion: bundleVersion,
                downloadUrl: descriptor.downloadUrl,
                fileSize: descriptor.fileSize,
                sha256: descriptor.sha256,
                diffSha256: diffSha256,
                signature: signature
            )
            try publishVerification(verificationContext) {
                guard BundleDiffReconstruction.writeDescriptor(verifiedDescriptor, toPath: diffMetaPath) else {
                    throw BundleUpdateError(.storage, .verifyASC)
                }
                if promote {
                    do {
                        try BundleCryptoCore.safeRename(fromPath: stagingDir, toPath: destination)
                    } catch {
                        BundleDiffReconstruction.writeDescriptor(descriptor, toPath: diffMetaPath)
                        throw BundleDiffReconstructionError.fallbackRequired("failed to promote diff staging: \(error)")
                    }
                }
                let release = BundleDiffReconstruction.ReleaseIdentity(appVersion: appVersion, bundleVersion: bundleVersion)
                process.verifiedDiffRecords[release] = .init(
                    fullSha256: descriptor.sha256,
                    diffSha256: diffSha256,
                    metadataSha256: metadataSha
                )
            }
        }

        if isInstalled {
            if !skipGPG && !BundleUpdateStore.validateMetadataFileSha256(folderName, signature: signature, expectedAppVersion: appVersion, expectedBundleVersion: bundleVersion) {
                throw BundleUpdateError(.integrity, .verifyASC)
            }
            let installedMetaPath = (destination as NSString).appendingPathComponent("metadata.json")
            guard let installedMetaSha = BundleUpdateStore.calculateSHA256(installedMetaPath),
                  let metadata = BundleUpdateStore.getMetadataFileContent(folderName),
                  BundleCryptoCore.validateExtractedPathSafety(destination),
                  BundleUpdateStore.validateAllFilesInDir(destination, metadata: metadata, appVersion: appVersion, bundleVersion: bundleVersion),
                  BundleUpdateStore.validateBundlePairCompatibility(bundleDirPath: destination, metadata: metadata) else {
                throw BundleUpdateError(.integrity, .verifyASC)
            }
            try publishVerifiedDiff(metadataSha: installedMetaSha, promote: false)
            return
        }

        BundleDiffReconstruction.retainStagingDirectory(stagingDir)
        defer { BundleDiffReconstruction.releaseStagingDirectory(stagingDir) }

        // b. Extract .diff.zip into isolated staging directory with full zip safety checks
        do {
            let stagingParent = (stagingDir as NSString).deletingLastPathComponent
            if !fm.fileExists(atPath: stagingParent) {
                try fm.createDirectory(atPath: stagingParent, withIntermediateDirectories: true)
            }
            try extractZipWithSafetyChecks(zipPath: diffFilePath, to: stagingDir, step: .verifyASC)
        } catch let err as BundleUpdateError where err.code == .security {
            throw err
        } catch {
            throw BundleDiffReconstructionError.fallbackRequired("diff zip extraction failed: \(error)")
        }

        if verificationContext.wasCancelledByClear {
            throw BundleUpdateError(.cancelled, .verifyASC)
        }

        // c. Verify diff-manifest.json byte cap and streaming SHA-256 BEFORE reading into memory
        let diffManifestPath = (stagingDir as NSString).appendingPathComponent("diff-manifest.json")
        var isManifestDir: ObjCBool = false
        guard fm.fileExists(atPath: diffManifestPath, isDirectory: &isManifestDir),
              !isManifestDir.boolValue,
              let manifestAttrs = try? fm.attributesOfItem(atPath: diffManifestPath),
              let manifestSize = manifestAttrs[.size] as? UInt64 else {
            throw BundleDiffReconstructionError.fallbackRequired("diff-manifest.json missing")
        }
        guard manifestSize <= BundleDiffReconstruction.maxControlJsonBytes else {
            throw BundleDiffReconstructionError.securityRejected("diff-manifest.json exceeds size cap (\(manifestSize))")
        }
        if !skipGPG, let expectedDiffManifestSha256 {
            guard let actualManifestSha = BundleUpdateStore.calculateSHA256(diffManifestPath),
                  BundleCryptoCore.secureEqualHex(actualManifestSha.lowercased(), expectedDiffManifestSha256) else {
                throw BundleDiffReconstructionError.integrityFailed("diff-manifest.json sha256 mismatch")
            }
        }
        guard let diffManifestData = fm.contents(atPath: diffManifestPath) else {
            throw BundleDiffReconstructionError.fallbackRequired("diff-manifest.json unreadable")
        }

        // d & e. Parse & validate diff-manifest.json and verify metadata.json streaming SHA-256 BEFORE reading into memory
        let manifest = try BundleDiffReconstruction.parseAndValidateManifest(
            jsonData: diffManifestData,
            expectedAppType: "ios",
            expectedAppVersion: appVersion,
            expectedBundleVersion: bundleVersion
        )

        let stagedMetadataPath = (stagingDir as NSString).appendingPathComponent("metadata.json")
        var isMetaDir: ObjCBool = false
        guard fm.fileExists(atPath: stagedMetadataPath, isDirectory: &isMetaDir),
              !isMetaDir.boolValue,
              let metaAttrs = try? fm.attributesOfItem(atPath: stagedMetadataPath),
              let metaSize = metaAttrs[.size] as? UInt64 else {
            throw BundleDiffReconstructionError.fallbackRequired("staged metadata.json missing")
        }
        guard metaSize <= BundleDiffReconstruction.maxControlJsonBytes else {
            throw BundleDiffReconstructionError.securityRejected("staged metadata.json exceeds size cap (\(metaSize))")
        }
        guard let actualMetadataSha = BundleUpdateStore.calculateSHA256(stagedMetadataPath)?.lowercased() else {
            throw BundleDiffReconstructionError.fallbackRequired("failed to hash staged metadata.json")
        }
        if !skipGPG, let expectedMetadataSha256 {
            guard BundleCryptoCore.secureEqualHex(actualMetadataSha, expectedMetadataSha256) else {
                throw BundleDiffReconstructionError.integrityFailed("metadata.json gpg sha256 mismatch")
            }
        }
        guard BundleCryptoCore.secureEqualHex(actualMetadataSha, manifest.targetMetadataSha256) else {
            throw BundleDiffReconstructionError.integrityFailed("metadata.json targetMetadataSha256 mismatch")
        }
        guard let metadataData = fm.contents(atPath: stagedMetadataPath),
              let targetMetadata = try? JSONSerialization.jsonObject(with: metadataData) as? [String: Any] else {
            throw BundleDiffReconstructionError.fallbackRequired("staged metadata.json invalid JSON")
        }

        let targetFileEntries = BundleUpdateStore.fileMetadataEntries(from: targetMetadata)
        try BundleDiffReconstruction.validateManifestAgainstTargetMetadata(
            manifest: manifest,
            targetFileEntries: targetFileEntries
        )

        if verificationContext.wasCancelledByClear {
            throw BundleUpdateError(.cancelled, .verifyASC)
        }

        // f & g. Verify changedFiles and reconstruct reusedFiles
        let candidateDirs = collectCandidateOtaDirs(appVersion: appVersion, excludingFolder: folderName)
        try BundleDiffReconstruction.reconstruct(
            stagingDir: stagingDir,
            manifest: manifest,
            candidateBundleDirs: candidateDirs,
            sha256OfFile: { BundleUpdateStore.calculateSHA256($0) },
            builtinCopy: { relPath, destPath, expectedHash in
                self.copyBuiltinBundleFile(relativePath: relPath, destinationPath: destPath, expectedHash: expectedHash)
            }
        )

        if verificationContext.wasCancelledByClear {
            throw BundleUpdateError(.cancelled, .verifyASC)
        }

        // h. Delete diff-manifest.json before full directory validation
        try? fm.removeItem(atPath: diffManifestPath)

        // i. Validate all files in .diff-staging and bundle compatibility
        guard BundleCryptoCore.validateExtractedPathSafety(stagingDir) else {
            throw BundleUpdateError(.security, .verifyASC)
        }
        guard BundleUpdateStore.validateAllFilesInDir(stagingDir, metadata: targetMetadata, appVersion: appVersion, bundleVersion: bundleVersion) else {
            throw BundleDiffReconstructionError.fallbackRequired("final diff-staging file verification failed")
        }
        guard BundleUpdateStore.validateBundlePairCompatibility(bundleDirPath: stagingDir, metadata: targetMetadata) else {
            throw BundleUpdateError(.incompatible, .verifyASC)
        }

        try publishVerifiedDiff(metadataSha: actualMetadataSha, promote: true)
        OneKeyLog.info("BundleUpdate", "verifyBundleASC: diff reconstruction succeeded for \(folderName)")
    }

    func verifyBundleASC(params: BundleVerifyASCParams) throws -> Promise<Void> {
        return updatePromise(step: .verifyASC) { [weak self] in
            guard let self = self else { throw BundleUpdateError(.unknown, .verifyASC) }
            let sha256 = BundleDiffReconstruction.normalizeSha256(params.sha256) ?? params.sha256.lowercased()
            let appVersion = params.latestVersion
            let bundleVersion = params.bundleVersion
            let signature = params.signature

            guard appVersion.isSafeVersionString, bundleVersion.isSafeVersionString else {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: invalid version string format: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
                throw BundleUpdateError(.invalidArgument, .verifyASC)
            }

            let folderName = "\(appVersion)-\(bundleVersion)"
            let verificationContext = try self.acquireVerificationSlot(folderName, step: .verifyASC)
            defer {
                self.releaseVerificationSlot(folderName, run: verificationContext)
            }

            let downloadDir = BundleUpdateStore.downloadBundleDir()
            let baseDownloadDir = (downloadDir as NSString).resolvingSymlinksInPath

            let fullFilePath = (downloadDir as NSString).appendingPathComponent("\(folderName).zip")
            let resolvedFullPath = (fullFilePath as NSString).resolvingSymlinksInPath
            guard resolvedFullPath.hasPrefix(baseDownloadDir + "/") else {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: ZIP outside download directory: \(resolvedFullPath)")
                throw BundleUpdateError(.security, .verifyASC)
            }

            let diffDir = (downloadDir as NSString).appendingPathComponent(BundleDiffReconstruction.diffDownloadDirName)
            let diffFilePath = (diffDir as NSString).appendingPathComponent("\(folderName)\(BundleDiffReconstruction.diffZipExtension)")
            let diffMetaPath = (diffDir as NSString).appendingPathComponent("\(folderName)\(BundleDiffReconstruction.diffMetaExtension)")
            let resolvedDiffPath = (diffFilePath as NSString).resolvingSymlinksInPath

            let isInstalled = BundleUpdateStore.currentBundleVersion() == folderName ||
                BundleUpdateStore.readFallbackUpdateBundleDataFile().contains {
                    $0["appVersion"] == appVersion && $0["bundleVersion"] == bundleVersion
                }
            let destination = (BundleUpdateStore.bundleDir() as NSString).appendingPathComponent(folderName)
            let resolvedDestination = (destination as NSString).resolvingSymlinksInPath
            let baseBundleDir = (BundleUpdateStore.bundleDir() as NSString).resolvingSymlinksInPath
            guard resolvedDestination.hasPrefix(baseBundleDir + "/") else {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: destination outside bundle directory: \(resolvedDestination)")
                throw BundleUpdateError(.security, .verifyASC)
            }
            let stagingRootDir = (BundleUpdateStore.bundleDir() as NSString).appendingPathComponent(BundleDiffReconstruction.stagingRootDirName)
            BundleDiffReconstruction.sweepStagingDirectory(stagingRootDir)
            let runId = UUID().uuidString.replacingOccurrences(of: "-", with: "")
            let stagingDir = (stagingRootDir as NSString).appendingPathComponent("\(folderName)-\(runId)")
            let resolvedStagingDir = (stagingDir as NSString).resolvingSymlinksInPath
            guard resolvedStagingDir.hasPrefix(baseBundleDir + "/") else {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: staging outside bundle directory: \(resolvedStagingDir)")
                throw BundleUpdateError(.security, .verifyASC)
            }

            #if ALLOW_SKIP_GPG_VERIFICATION
            let skipGPG = BundleUpdateStore.isDevSettingsEnabled() && BundleUpdateStore.isSkipGPGEnabled()
            #else
            let skipGPG = false
            #endif

            let fm = FileManager.default
            if fm.fileExists(atPath: resolvedFullPath) {
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: verifying full bundle appVersion=\(appVersion), bundleVersion=\(bundleVersion), file=\(fullFilePath)")
                try self.verifyAndExtractFullZip(
                    filePath: fullFilePath,
                    diffMetaPath: diffMetaPath,
                    stagingDir: stagingDir,
                    destination: destination,
                    appVersion: appVersion,
                    bundleVersion: bundleVersion,
                    sha256: sha256,
                    signature: signature,
                    isInstalled: isInstalled,
                    skipGPG: skipGPG,
                    verificationContext: verificationContext
                )
                return
            }

            guard resolvedDiffPath.hasPrefix(baseDownloadDir + "/"),
                  (diffMetaPath as NSString).resolvingSymlinksInPath.hasPrefix(baseDownloadDir + "/") else {
                throw BundleUpdateError(.security, .verifyASC)
            }
            let normalizedDiffSha256 = BundleDiffReconstruction.normalizeSha256(params.diffSha256)
            let hasDiffSig = !(params.diffSignature ?? "").isEmpty
            let persistedDesc = BundleDiffReconstruction.readDescriptor(
                fromPath: diffMetaPath,
                expectedAppVersion: appVersion,
                expectedBundleVersion: bundleVersion
            )

            func downloadAndVerifyFullFallback(expectedDiffSha: String?) async throws {
                if verificationContext.wasCancelledByClear {
                    throw BundleUpdateError(.cancelled, .verifyASC)
                }
                guard let fallbackParams = BundleDiffReconstruction.resolveFallbackDownloadParams(
                    expectedFullSha256: sha256,
                    expectedDiffSha256: expectedDiffSha,
                    explicitDownloadUrl: params.downloadUrl,
                    explicitFileSize: params.fileSize,
                    inMemoryFullParams: self.recordedFullBundleParams(key: folderName),
                    descriptor: persistedDesc
                ) else {
                    try? fm.removeItem(atPath: diffFilePath)
                    try? fm.removeItem(atPath: "\(diffFilePath).resume")
                    try? fm.removeItem(atPath: diffMetaPath)
                    RangeDownloader.shared.discardArtifacts(filePath: diffFilePath)
                    throw BundleUpdateError(.missing, .verifyASC)
                }
                let run = try self.acquireDownloadSlot(
                    targetKey: folderName, step: .verifyASC, verificationRun: verificationContext
                )
                defer { self.releaseDownloadSlot(run) }
                let fallbackDiffSha = expectedDiffSha ?? persistedDesc?.diffSha256 ?? sha256
                guard BundleDiffReconstruction.writeDescriptor(
                    BundleDiffReconstruction.DiffArchiveDescriptor(
                        appVersion: appVersion,
                        bundleVersion: bundleVersion,
                        downloadUrl: fallbackParams.downloadUrl,
                        fileSize: fallbackParams.fileSize,
                        sha256: sha256,
                        diffSha256: fallbackDiffSha,
                        signature: persistedDesc?.signature,
                        fallbackRequired: true
                    ),
                    toPath: diffMetaPath
                ) else {
                    throw BundleUpdateError(.storage, .verifyASC)
                }
                self.markDiffFallback(folderName, sha256: sha256)
                try? fm.removeItem(atPath: diffFilePath)
                try? fm.removeItem(atPath: "\(diffFilePath).resume")
                RangeDownloader.shared.discardArtifacts(filePath: diffFilePath)

                try await self.downloadVerifiedFile(
                    downloadUrl: fallbackParams.downloadUrl,
                    filePath: fullFilePath,
                    sha256: sha256,
                    rangeTaskId: BundleDiffReconstruction.rangeTaskId(bundleKey: folderName, isDiff: false),
                    run: run,
                    step: .verifyASC,
                    emitErrorEvents: true
                )
                try self.verifyAndExtractFullZip(
                    filePath: fullFilePath,
                    diffMetaPath: diffMetaPath,
                    stagingDir: stagingDir,
                    destination: destination,
                    appVersion: appVersion,
                    bundleVersion: bundleVersion,
                    sha256: sha256,
                    signature: signature,
                    isInstalled: isInstalled,
                    skipGPG: skipGPG,
                    verificationContext: verificationContext
                )
                self.sendEvent(type: "update/complete")
            }

            if persistedDesc?.fallbackRequired == true || self.isDiffFallbackMarked(folderName, sha256: sha256) {
                if let persistedDesc {
                    guard persistedDesc.sha256 == sha256,
                          params.diffSha256 == nil || normalizedDiffSha256 == persistedDesc.diffSha256 else {
                        throw BundleUpdateError(.integrity, .verifyASC)
                    }
                }
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: resuming interrupted full-bundle fallback for \(folderName)")
                try await downloadAndVerifyFullFallback(expectedDiffSha: normalizedDiffSha256)
                return
            }

            if fm.fileExists(atPath: resolvedDiffPath) {
                guard let persistedDesc,
                      let diffSha256 = normalizedDiffSha256,
                      BundleDiffReconstruction.matchesDownloadRecord(
                        suppliedFullSha256: sha256,
                        suppliedDiffSha256: diffSha256,
                        descriptor: persistedDesc
                      ),
                      skipGPG || hasDiffSig else {
                    throw BundleUpdateError(.integrity, .verifyASC)
                }
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: attempting incremental diff reconstruction for \(folderName)")
                do {
                    try self.reconstructAndVerifyDiffZip(
                        diffFilePath: diffFilePath,
                        diffMetaPath: diffMetaPath,
                        stagingDir: stagingDir,
                        destination: destination,
                        appVersion: appVersion,
                        bundleVersion: bundleVersion,
                        descriptor: persistedDesc,
                        diffSignature: params.diffSignature ?? "",
                        signature: signature,
                        isInstalled: isInstalled,
                        skipGPG: skipGPG,
                        verificationContext: verificationContext
                    )
                    return
                } catch let error as BundleUpdateError {
                    throw error
                } catch let error as BundleDiffReconstructionError {
                    switch error {
                    case .integrityFailed(let reason):
                        OneKeyLog.error("BundleUpdate", "verifyBundleASC: diff integrity failed (\(reason))")
                        throw BundleUpdateError(.integrity, .verifyASC)
                    case .securityRejected(let reason):
                        OneKeyLog.error("BundleUpdate", "verifyBundleASC: diff security rejected (\(reason))")
                        throw BundleUpdateError(.security, .verifyASC)
                    case .fallbackRequired(let reason):
                        OneKeyLog.warn("BundleUpdate", "verifyBundleASC: diff reconstruction failed (\(reason)), falling back to full bundle")
                        try await downloadAndVerifyFullFallback(expectedDiffSha: diffSha256)
                        return
                    }
                }
            }

            throw BundleUpdateError(.missing, .verifyASC)
        }
    }

    func verifyBundle(params: BundleVerifyParams) throws -> Promise<Void> {
        return updatePromise(step: .verifyPackage) { [weak self] in
            guard let self else { throw BundleUpdateError(.unknown, .verifyPackage) }
            let sha256 = BundleDiffReconstruction.normalizeSha256(params.sha256) ?? params.sha256.lowercased()
            let appVersion = params.latestVersion
            let bundleVersion = params.bundleVersion

            guard appVersion.isSafeVersionString, bundleVersion.isSafeVersionString else {
                OneKeyLog.error("BundleUpdate", "verifyBundle: invalid version string format: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
                throw BundleUpdateError(.invalidArgument, .verifyPackage)
            }

            let folderName = "\(appVersion)-\(bundleVersion)"
            let verification = try self.acquireVerificationSlot(folderName, step: .verifyPackage)
            defer { self.releaseVerificationSlot(folderName, run: verification) }
            let downloadDir = BundleUpdateStore.downloadBundleDir()
            let baseDownloadDir = (downloadDir as NSString).resolvingSymlinksInPath
            let filePath = (downloadDir as NSString).appendingPathComponent("\(folderName).zip")
            let resolvedFilePath = (filePath as NSString).resolvingSymlinksInPath
            guard resolvedFilePath.hasPrefix(baseDownloadDir + "/") else {
                OneKeyLog.error("BundleUpdate", "verifyBundle: ZIP outside download directory: \(resolvedFilePath)")
                throw BundleUpdateError(.security, .verifyPackage)
            }

            if FileManager.default.fileExists(atPath: resolvedFilePath) {
                try self.validateManagedArchive(filePath, step: .verifyPackage)
                OneKeyLog.info("BundleUpdate", "verifyBundle: appVersion=\(appVersion), bundleVersion=\(bundleVersion), file=\(filePath)")

                guard let calculated = BundleUpdateStore.calculateSHA256(filePath) else {
                    OneKeyLog.error("BundleUpdate", "verifyBundle: failed to calculate SHA256 for file=\(filePath)")
                    throw BundleUpdateError(.storage, .verifyPackage)
                }
                guard BundleCryptoCore.secureEqualHex(calculated.lowercased(), sha256) else {
                    OneKeyLog.error("BundleUpdate", "verifyBundle: SHA256 mismatch, expected=\(sha256.prefix(16))..., got=\(calculated.prefix(16))...")
                    throw BundleUpdateError(.integrity, .verifyPackage)
                }

                OneKeyLog.info("BundleUpdate", "verifyBundle: SHA256 verified OK for appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
                try self.publishVerification(verification)
                return
            }

            let diffDir = (downloadDir as NSString).appendingPathComponent(BundleDiffReconstruction.diffDownloadDirName)
            let diffFilePath = (diffDir as NSString).appendingPathComponent("\(folderName)\(BundleDiffReconstruction.diffZipExtension)")
            let diffMetaPath = (diffDir as NSString).appendingPathComponent("\(folderName)\(BundleDiffReconstruction.diffMetaExtension)")
            let resolvedDiffPath = (diffFilePath as NSString).resolvingSymlinksInPath
            guard resolvedDiffPath.hasPrefix(baseDownloadDir + "/"),
                  (diffMetaPath as NSString).resolvingSymlinksInPath.hasPrefix(baseDownloadDir + "/") else {
                throw BundleUpdateError(.security, .verifyPackage)
            }

            let descriptor = BundleDiffReconstruction.readDescriptor(
                fromPath: diffMetaPath,
                expectedAppVersion: appVersion,
                expectedBundleVersion: bundleVersion
            )
            let bundleDir = BundleUpdateStore.bundleDir()
            let bundleDirPath = (bundleDir as NSString).appendingPathComponent(folderName)
            guard (bundleDirPath as NSString).resolvingSymlinksInPath
                .hasPrefix((bundleDir as NSString).resolvingSymlinksInPath + "/") else {
                throw BundleUpdateError(.security, .verifyPackage)
            }
            let metadataPath = (bundleDirPath as NSString).appendingPathComponent("metadata.json")

            #if ALLOW_SKIP_GPG_VERIFICATION
            let skipGPG = BundleUpdateStore.isDevSettingsEnabled() && BundleUpdateStore.isSkipGPGEnabled()
            #else
            let skipGPG = false
            #endif

            guard FileManager.default.fileExists(atPath: resolvedDiffPath) else {
                throw BundleUpdateError(.missing, .verifyPackage)
            }
            try self.validateManagedArchive(diffFilePath, step: .verifyPackage)
            let targetMetadataSize: UInt64?
            do {
                targetMetadataSize = try BundleDiffReconstruction.targetMetadataSizeIfPresent(bundleDirPath)
            } catch BundleDiffReconstructionError.integrityFailed {
                throw BundleUpdateError(.integrity, .verifyPackage)
            }

            if let targetMetadataSize {
                guard targetMetadataSize <= BundleDiffReconstruction.maxControlJsonBytes else {
                    throw BundleUpdateError(.security, .verifyPackage)
                }
                let verifiedRecord = self.recordedVerifiedDiff(appVersion: appVersion, bundleVersion: bundleVersion)
                if !skipGPG {
                    guard let expectedMetaSha = BundleDiffReconstruction.resolveTrustedMetadataSha256ForPackageVerify(
                        suppliedFullSha256: sha256,
                        suppliedDiffSha256: params.diffSha256,
                        suppliedSignature: params.signature,
                        inMemoryVerified: verifiedRecord,
                        descriptor: descriptor,
                        persistedAscSignature: BundleUpdateStore.readSignatureFile(folderName),
                        verifySignatureAndExtractMetadataSha256: { sig in
                            BundleUpdateStore.readMetadataFileSha256(
                                sig,
                                expectedAppVersion: appVersion,
                                expectedBundleVersion: bundleVersion,
                                expectedAppType: "ios",
                                expectedFileName: "metadata.json"
                            )
                        }
                    ) else {
                        OneKeyLog.error("BundleUpdate", "verifyBundle: missing or invalid trust anchor for reconstructed bundle \(folderName)")
                        throw BundleUpdateError(.integrity, .verifyPackage)
                    }
                    guard let actualMetaSha = BundleUpdateStore.calculateSHA256(metadataPath),
                          BundleCryptoCore.secureEqualHex(actualMetaSha.lowercased(), expectedMetaSha) else {
                        throw BundleUpdateError(.integrity, .verifyPackage)
                    }
                } else if BundleDiffReconstruction.resolveExpectedDiffSha256ForPackageVerify(
                    suppliedFullSha256: sha256,
                    suppliedDiffSha256: params.diffSha256,
                    descriptor: descriptor
                ) == nil {
                    throw BundleUpdateError(.integrity, .verifyPackage)
                }
                guard let metadata = BundleUpdateStore.getMetadataFileContent(folderName),
                      BundleCryptoCore.validateExtractedPathSafety(bundleDirPath),
                      BundleUpdateStore.validateAllFilesInDir(bundleDirPath, metadata: metadata, appVersion: appVersion, bundleVersion: bundleVersion) else {
                    throw BundleUpdateError(.integrity, .verifyPackage)
                }
                guard BundleUpdateStore.validateBundlePairCompatibility(bundleDirPath: bundleDirPath, metadata: metadata) else {
                    throw BundleUpdateError(.incompatible, .verifyPackage)
                }
                OneKeyLog.info("BundleUpdate", "verifyBundle: diff-reconstructed bundle verified OK for appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
                try self.publishVerification(verification)
                return
            }

            // Before reconstruction, verify the downloaded archive against its record.
            guard let expectedDiffSha = BundleDiffReconstruction.resolveExpectedDiffSha256ForPackageVerify(
                suppliedFullSha256: sha256,
                suppliedDiffSha256: params.diffSha256,
                descriptor: descriptor
            ) else {
                OneKeyLog.error("BundleUpdate", "verifyBundle: missing or mismatched expected diffSha256 for \(folderName)")
                throw BundleUpdateError(.integrity, .verifyPackage)
            }
            guard let calculatedDiffSha = BundleUpdateStore.calculateSHA256(diffFilePath) else {
                throw BundleUpdateError(.storage, .verifyPackage)
            }
            guard BundleCryptoCore.secureEqualHex(calculatedDiffSha.lowercased(), expectedDiffSha) else {
                throw BundleUpdateError(.integrity, .verifyPackage)
            }
            OneKeyLog.info("BundleUpdate", "verifyBundle: downloaded diff archive verified OK for \(folderName)")
            try self.publishVerification(verification)
        }
    }

    func installBundle(params: BundleInstallParams) throws -> Promise<Void> {
        // Invalidate up front so any concurrent reader misses the cache while
        // the install is running. We invalidate again after the async body
        // commits so a same-version reinstall (or any reader that re-cached
        // pre-commit data inside the race window) can't leave stale entries.
        BundleUpdateStore.invalidateValidatedBundleInfoCache()
        return updatePromise(step: .install) {
            let appVersion = params.latestVersion
            let bundleVersion = params.bundleVersion
            let signature = params.signature

            OneKeyLog.info("BundleUpdate", "installBundle: appVersion=\(appVersion), bundleVersion=\(bundleVersion), signatureLength=\(signature.count)")

            guard appVersion.isSafeVersionString, bundleVersion.isSafeVersionString else {
                OneKeyLog.error("BundleUpdate", "installBundle: invalid version string format: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
                throw BundleUpdateError(.invalidArgument, .install)
            }

            // GPG verification skipped only when both DevSettings and skip-GPG toggle are enabled
            #if ALLOW_SKIP_GPG_VERIFICATION
            let skipGPG = BundleUpdateStore.isDevSettingsEnabled() && BundleUpdateStore.isSkipGPGEnabled()
            #else
            let skipGPG = false
            #endif
            OneKeyLog.info("BundleUpdate", "installBundle: GPG check: skipGPG=\(skipGPG)")

            let folderName = "\(appVersion)-\(bundleVersion)"
            let currentFolderName = BundleUpdateStore.currentBundleVersion()
            let previousActiveBundleVersion = currentFolderName
            OneKeyLog.info("BundleUpdate", "installBundle: target=\(folderName), current=\(currentFolderName ?? "nil")")

            // Verify bundle directory confinement and existence
            let bundleDirPath = (BundleUpdateStore.bundleDir() as NSString).appendingPathComponent(folderName)
            let resolvedBundleDir = (bundleDirPath as NSString).resolvingSymlinksInPath
            let baseBundleDir = (BundleUpdateStore.bundleDir() as NSString).resolvingSymlinksInPath
            guard resolvedBundleDir.hasPrefix(baseBundleDir + "/") else {
                OneKeyLog.error("BundleUpdate", "installBundle: bundle path outside bundle directory: \(resolvedBundleDir)")
                throw BundleUpdateError(.security, .install)
            }
            let sigPath = (BundleUpdateStore.signatureFilePath(folderName) as NSString).resolvingSymlinksInPath
            let baseAscDir = (BundleUpdateStore.ascDir() as NSString).resolvingSymlinksInPath
            guard sigPath.hasPrefix(baseAscDir + "/") else {
                OneKeyLog.error("BundleUpdate", "installBundle: signature path outside asc directory: \(sigPath)")
                throw BundleUpdateError(.security, .install)
            }

            guard FileManager.default.fileExists(atPath: bundleDirPath) else {
                OneKeyLog.error("BundleUpdate", "installBundle: bundle directory not found: \(bundleDirPath)")
                throw BundleUpdateError(.missing, .install)
            }

            let mainBundlePath = (bundleDirPath as NSString).appendingPathComponent("main.jsbundle.hbc")
            let metadataJsonPath = (bundleDirPath as NSString).appendingPathComponent("metadata.json")
            guard FileManager.default.fileExists(atPath: mainBundlePath),
                  FileManager.default.fileExists(atPath: metadataJsonPath) else {
                OneKeyLog.error("BundleUpdate", "installBundle: required bundle files missing in \(bundleDirPath)")
                throw BundleUpdateError(.missing, .install)
            }

            if !skipGPG {
                guard !signature.isEmpty,
                      BundleUpdateStore.validateMetadataFileSha256(folderName, signature: signature, expectedAppVersion: appVersion, expectedBundleVersion: bundleVersion) else {
                    OneKeyLog.error("BundleUpdate", "installBundle: GPG signature verification failed for \(folderName)")
                    throw BundleUpdateError(.integrity, .install)
                }
            }

            guard let metadata = BundleUpdateStore.getMetadataFileContent(folderName) else {
                OneKeyLog.error("BundleUpdate", "installBundle: failed to read metadata.json content for \(folderName)")
                throw BundleUpdateError(.missing, .install)
            }
            if !BundleUpdateStore.validateAllFilesInDir(bundleDirPath, metadata: metadata, appVersion: appVersion, bundleVersion: bundleVersion) {
                OneKeyLog.error("BundleUpdate", "installBundle: extracted file verification failed for \(folderName)")
                throw BundleUpdateError(.integrity, .install)
            }
            if !BundleUpdateStore.validateBundlePairCompatibility(bundleDirPath: bundleDirPath, metadata: metadata) {
                OneKeyLog.error("BundleUpdate", "installBundle: bundle pair compatibility check failed for \(folderName)")
                throw BundleUpdateError(.incompatible, .install)
            }

            if !signature.isEmpty {
                BundleUpdateStore.writeSignatureFile(folderName, signature: signature)
            }
            let ud = UserDefaults.standard
            ud.set(folderName, forKey: "currentBundleVersion")
            let currentNativeVersion = BundleUpdateStore.getCurrentNativeVersion()
            BundleUpdateStore.setNativeVersion(currentNativeVersion)
            let currentBuildNumber = BundleUpdateStore.getCurrentNativeBuildNumber()
            BundleUpdateStore.setNativeBuildNumber(currentBuildNumber)
            ud.synchronize()

            // Manage fallback data
            var fallbackData = BundleUpdateStore.readFallbackUpdateBundleDataFile()

            if let current = currentFolderName,
               let curBundleVersion = BundleUpdateStore.bundleVersion(inFolder: current, appVersion: currentNativeVersion) {
                let curSignature = BundleUpdateStore.readSignatureFile(current)
                if !curSignature.isEmpty {
                    fallbackData.append([
                        "appVersion": currentNativeVersion,
                        "bundleVersion": curBundleVersion,
                        "signature": curSignature
                    ])
                }
            }

            // Keep max 3 fallback entries
            if fallbackData.count > 3 {
                let shifted = fallbackData.removeFirst()
                if let shiftApp = shifted["appVersion"], let shiftBundle = shifted["bundleVersion"] {
                    let dirName = "\(shiftApp)-\(shiftBundle)"
                    if dirName != previousActiveBundleVersion &&
                        shiftBundle != previousActiveBundleVersion &&
                        dirName != folderName {
                        BundleUpdateStore.deleteSignatureFile(dirName)
                        let oldPath = (BundleUpdateStore.bundleDir() as NSString).appendingPathComponent(dirName)
                        if FileManager.default.fileExists(atPath: oldPath) {
                            try? FileManager.default.removeItem(atPath: oldPath)
                        }
                    } else {
                        OneKeyLog.info("BundleUpdate", "installBundle: skipping disk deletion of evicted fallback \(dirName) because it is still in use")
                    }
                }
            }

            BundleUpdateStore.writeFallbackUpdateBundleDataFile(fallbackData)
            ud.synchronize()

            // Second invalidate: closes the race window between the up-front
            // invalidate and the actual currentBundleVersion write. Without
            // this, a reader entering validatedCurrentBundleInfo() between
            // the two could re-cache pre-install state and leave it stale.
            BundleUpdateStore.invalidateValidatedBundleInfoCache()

            OneKeyLog.info("BundleUpdate", "installBundle: completed successfully, installed version=\(folderName), fallbackCount=\(fallbackData.count)")
        }
    }

    private func clearManagedFiles(
        includeInstalled: Bool,
        includeDownloads: Bool = true,
        onCleared: () -> Void = {}
    ) async throws {
        let (activeRun, activePath, activeTaskId, activeVerifications) = try process.queue.sync {
            if process.clearingDownloads { throw BundleUpdateError(.invalidArgument, .cleanup) }
            process.clearingDownloads = true
            let run = process.activeDownload
            if run?.published == false { run?.delegate.markCancelledByClear() }
            let verifications = Array(process.activeVerifications.values)
            verifications.forEach { $0.cancel() }
            return (run, run?.filePath, run?.rangeTaskId, verifications)
        }
        defer { process.queue.sync { process.clearingDownloads = false } }

        activeRun?.session.invalidateAndCancel()
        if let activePath, let activeTaskId {
            await RangeDownloader.shared.cancel(channel: .bundle, taskId: activeTaskId, filePath: activePath)
        }
        if let activeRun, !(await activeRun.delegate.waitForRunCompletion(timeout: 65)) {
            throw BundleUpdateError(.unknown, .cleanup)
        }
        for verification in activeVerifications {
            if !(await verification.waitForCompletion(timeout: 65)) {
                throw BundleUpdateError(.unknown, .cleanup)
            }
        }
        process.queue.sync {
            process.diffFallbackVersions.removeAll()
            process.fullBundleParams.removeAll()
            process.verifiedDiffRecords.removeAll()
        }

        let fm = FileManager.default
        if includeDownloads {
            let downloadDir = BundleUpdateStore.downloadBundleDir()
            if fm.fileExists(atPath: downloadDir) {
                try fm.removeItem(atPath: downloadDir)
            }
        }
        let bundleDir = BundleUpdateStore.bundleDir()
        if includeInstalled, fm.fileExists(atPath: bundleDir) {
            try fm.removeItem(atPath: bundleDir)
        } else {
            let stagingRootDir = (bundleDir as NSString).appendingPathComponent(BundleDiffReconstruction.stagingRootDirName)
            if fm.fileExists(atPath: stagingRootDir) {
                try fm.removeItem(atPath: stagingRootDir)
            }
        }
        onCleared()
    }

    func clearDownload() throws -> Promise<Void> {
        return updatePromise(step: .cleanup) { [weak self] in
            guard let self else { return }
            try await self.clearManagedFiles(includeInstalled: false)
        }
    }

    func clearBundle() throws -> Promise<Void> {
        BundleUpdateStore.invalidateValidatedBundleInfoCache()
        return updatePromise(step: .cleanup) { [weak self] in
            guard let self else { return }
            try await self.clearManagedFiles(includeInstalled: true)
        }
    }

    /// Classifies only non-current stems for best-effort stale cleanup.
    /// Current-version stems are matched by the exact native-version prefix.
    private static func appVersionFromStem(_ stem: String) -> String? {
        guard let lastDash = stem.range(of: "-", options: .backwards),
              lastDash.lowerBound > stem.startIndex else { return nil }
        let appVersion = String(stem[stem.startIndex..<lastDash.lowerBound])
        return appVersion.isEmpty ? nil : appVersion
    }

    /// Prunes every artifact whose appVersion != the running native binary
    /// version: stale onekey-bundle/* dirs, onekey-bundle-download/* stages
    /// (.zip / .partial / .progress / .resume), orphan asc signatures, and
    /// lingering fallback entries. Hard-refuses to delete the current
    /// appVersion's artifacts and the active currentBundleVersion. Tolerates
    /// already-missing files. Returns the count of deleted version directories.
    func pruneStaleAppVersionBundles() throws -> Promise<Double> {
        BundleUpdateStore.invalidateValidatedBundleInfoCache()
        return Promise.async {
            let fm = FileManager.default
            let currentAppV = BundleUpdateStore.getCurrentNativeVersion()
            // Safety net: never delete the bundle backing the active pointer.
            let currentBundleVersion = BundleUpdateStore.currentBundleVersion()
            OneKeyLog.info("BundleUpdate", "pruneStaleAppVersionBundles: currentAppV=\(currentAppV), currentBundleVersion=\(currentBundleVersion ?? "nil")")

            // Without a known native version we cannot decide what is stale;
            // bail out rather than risk deleting the wrong artifacts.
            guard !currentAppV.isEmpty else {
                OneKeyLog.warn("BundleUpdate", "pruneStaleAppVersionBundles: empty currentAppV, skipping")
                return 0
            }

            /// True when this entry stem must be kept: its appVersion matches
            /// the running binary, it IS the active currentBundleVersion, or it
            /// is unparseable (leave foreign names alone).
            func shouldKeep(stem: String) -> Bool {
                if let active = currentBundleVersion, stem == active { return true }
                if BundleUpdateStore.bundleVersion(inFolder: stem, appVersion: currentAppV) != nil { return true }
                guard let appV = Self.appVersionFromStem(stem) else { return true }
                return appV == currentAppV
            }

            var deletedDirCount = 0

            // 1. onekey-bundle/* extracted dirs
            let bundleDir = BundleUpdateStore.bundleDir()
            if let contents = try? fm.contentsOfDirectory(atPath: bundleDir) {
                for name in contents {
                    // Skip non-version entries (asc dir, fallback json, etc.)
                    if name == "asc" || name == "fallbackUpdateBundleData.json" { continue }
                    let fullPath = (bundleDir as NSString).appendingPathComponent(name)
                    var isDir: ObjCBool = false
                    guard fm.fileExists(atPath: fullPath, isDirectory: &isDir), isDir.boolValue else { continue }
                    if name == BundleDiffReconstruction.stagingRootDirName {
                        BundleDiffReconstruction.sweepStagingDirectory(fullPath)
                        continue
                    }
                    if shouldKeep(stem: name) { continue }
                    do {
                        try fm.removeItem(atPath: fullPath)
                        deletedDirCount += 1
                        OneKeyLog.info("BundleUpdate", "pruneStaleAppVersionBundles: deleted stale bundle dir \(name)")
                    } catch {
                        OneKeyLog.warn("BundleUpdate", "pruneStaleAppVersionBundles: failed to delete bundle dir \(name): \(error)")
                    }
                }
            }

            // 2. onekey-bundle-download/* stages (zip / partial / progress / resume) and .diff/* stages
            let downloadDir = BundleUpdateStore.downloadBundleDir()
            if let contents = try? fm.contentsOfDirectory(atPath: downloadDir) {
                for name in contents {
                    let fullPath = (downloadDir as NSString).appendingPathComponent(name)
                    if name == BundleDiffReconstruction.diffDownloadDirName {
                        if let diffEntries = try? fm.contentsOfDirectory(atPath: fullPath) {
                            for diffName in diffEntries {
                                var diffStem = diffName
                                for suffix in [".meta.json", ".resume", ".progress", ".partial", ".diff.zip"] {
                                    if diffStem.hasSuffix(suffix) {
                                        diffStem = String(diffStem.dropLast(suffix.count))
                                    }
                                }
                                if shouldKeep(stem: diffStem) { continue }
                                let diffEntryPath = (fullPath as NSString).appendingPathComponent(diffName)
                                try? fm.removeItem(atPath: diffEntryPath)
                            }
                        }
                        continue
                    }
                    // Strip the trailing extension chain to recover the
                    // "{appV}-{bV}" stem (e.g. "6.3.0-123.zip.partial").
                    var stem = name
                    for suffix in [".resume", ".progress", ".partial", ".zip"] {
                        if stem.hasSuffix(suffix) {
                            stem = String(stem.dropLast(suffix.count))
                        }
                    }
                    if shouldKeep(stem: stem) { continue }
                    do {
                        try fm.removeItem(atPath: fullPath)
                        OneKeyLog.info("BundleUpdate", "pruneStaleAppVersionBundles: deleted stale download \(name)")
                    } catch {
                        OneKeyLog.warn("BundleUpdate", "pruneStaleAppVersionBundles: failed to delete download \(name): \(error)")
                    }
                }
            }

            // 3. onekey-bundle/asc/*-signature.asc orphan signatures
            let ascDir = BundleUpdateStore.ascDir()
            if let contents = try? fm.contentsOfDirectory(atPath: ascDir) {
                let suffix = "-signature.asc"
                for name in contents {
                    guard name.hasSuffix(suffix) else { continue }
                    let stem = String(name.dropLast(suffix.count))
                    if shouldKeep(stem: stem) { continue }
                    let fullPath = (ascDir as NSString).appendingPathComponent(name)
                    do {
                        try fm.removeItem(atPath: fullPath)
                        OneKeyLog.info("BundleUpdate", "pruneStaleAppVersionBundles: deleted orphan asc \(name)")
                    } catch {
                        OneKeyLog.warn("BundleUpdate", "pruneStaleAppVersionBundles: failed to delete asc \(name): \(error)")
                    }
                }
            }

            // 4. Persisted fallback list: drop entries whose appVersion != currentAppV.
            // Fixes the latent leak where stale fallback entries linger after a
            // native upgrade. Reuses the existing read/write helpers.
            let fallbackData = BundleUpdateStore.readFallbackUpdateBundleDataFile()
            let prunedFallback = fallbackData.filter { entry in
                guard let appV = entry["appVersion"], !appV.isEmpty else { return true }
                return appV == currentAppV
            }
            if prunedFallback.count != fallbackData.count {
                BundleUpdateStore.writeFallbackUpdateBundleDataFile(prunedFallback)
                OneKeyLog.info("BundleUpdate", "pruneStaleAppVersionBundles: pruned fallback entries \(fallbackData.count) -> \(prunedFallback.count)")
            }

            OneKeyLog.info("BundleUpdate", "pruneStaleAppVersionBundles: completed, deletedDirCount=\(deletedDirCount)")
            return Double(deletedDirCount)
        }
    }

    func resetToBuiltInBundle() throws -> Promise<Void> {
        BundleUpdateStore.invalidateValidatedBundleInfoCache()
        return updatePromise(step: .cleanup) {
            OneKeyLog.info("BundleUpdate", "resetToBuiltInBundle: clearing currentBundleVersion preference...")
            let ud = UserDefaults.standard
            if let cbv = ud.string(forKey: "currentBundleVersion") {
                ud.removeObject(forKey: cbv)
                ud.removeObject(forKey: "currentBundleVersion")
                OneKeyLog.info("BundleUpdate", "resetToBuiltInBundle: removed currentBundleVersion=\(cbv)")
            } else {
                OneKeyLog.info("BundleUpdate", "resetToBuiltInBundle: no currentBundleVersion set, already using built-in bundle")
            }
            ud.synchronize()
            OneKeyLog.info("BundleUpdate", "resetToBuiltInBundle: completed, app will use built-in bundle on next restart")
        }
    }

    func clearAllJSBundleData() throws -> Promise<TestResult> {
        BundleUpdateStore.invalidateValidatedBundleInfoCache()
        return updatePromise(step: .cleanup) { [weak self] in
            guard let self else { throw BundleUpdateError(.unknown, .cleanup) }
            try await self.clearManagedFiles(includeInstalled: true, includeDownloads: false) {
                let ud = UserDefaults.standard
                if let cbv = ud.string(forKey: "currentBundleVersion") {
                    ud.removeObject(forKey: cbv)
                    ud.removeObject(forKey: "currentBundleVersion")
                    OneKeyLog.info("BundleUpdate", "clearAllJSBundleData: removed currentBundleVersion=\(cbv)")
                }
                BundleUpdateStore.clearNativeVersionPrefs()
                ud.synchronize()
            }
            OneKeyLog.info("BundleUpdate", "clearAllJSBundleData: completed successfully")
            return TestResult(success: true, message: "Successfully cleared all JS bundle data")
        }
    }

    func getFallbackUpdateBundleData() throws -> Promise<[FallbackBundleInfo]> {
        return Promise.async {
            let data = BundleUpdateStore.readFallbackUpdateBundleDataFile()
            let result = data.compactMap { dict -> FallbackBundleInfo? in
                guard let appVersion = dict["appVersion"],
                      let bundleVersion = dict["bundleVersion"],
                      let signature = dict["signature"] else { return nil }
                return FallbackBundleInfo(appVersion: appVersion, bundleVersion: bundleVersion, signature: signature)
            }
            OneKeyLog.info("BundleUpdate", "getFallbackUpdateBundleData: found \(result.count) fallback entries")
            return result
        }
    }

    func setCurrentUpdateBundleData(params: BundleSwitchParams) throws -> Promise<Void> {
        BundleUpdateStore.invalidateValidatedBundleInfoCache()
        return updatePromise(step: .install) {
            guard params.appVersion.isSafeVersionString, params.bundleVersion.isSafeVersionString else {
                OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: invalid version string format: appVersion=\(params.appVersion), bundleVersion=\(params.bundleVersion)")
                throw BundleUpdateError(.invalidArgument, .install)
            }

            let bundleVersion = "\(params.appVersion)-\(params.bundleVersion)"
            OneKeyLog.info("BundleUpdate", "setCurrentUpdateBundleData: switching to \(bundleVersion)")

            // Verify bundle directory and signature confinement
            let bundleDirPath = (BundleUpdateStore.bundleDir() as NSString).appendingPathComponent(bundleVersion)
            let resolvedBundleDir = (bundleDirPath as NSString).resolvingSymlinksInPath
            let baseBundleDir = (BundleUpdateStore.bundleDir() as NSString).resolvingSymlinksInPath
            guard resolvedBundleDir.hasPrefix(baseBundleDir + "/") else {
                OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: bundle path outside bundle directory: \(resolvedBundleDir)")
                throw BundleUpdateError(.security, .install)
            }
            let sigPath = (BundleUpdateStore.signatureFilePath(bundleVersion) as NSString).resolvingSymlinksInPath
            let baseAscDir = (BundleUpdateStore.ascDir() as NSString).resolvingSymlinksInPath
            guard sigPath.hasPrefix(baseAscDir + "/") else {
                OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: signature path outside asc directory: \(sigPath)")
                throw BundleUpdateError(.security, .install)
            }

            // Verify the bundle directory actually exists
            guard FileManager.default.fileExists(atPath: bundleDirPath) else {
                OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: bundle directory not found: \(bundleDirPath)")
                throw BundleUpdateError(.missing, .install)
            }

            // Verify GPG signature is valid (skipped when both DevSettings and skip-GPG toggle are enabled)
            #if ALLOW_SKIP_GPG_VERIFICATION
            let skipGPGSwitch = BundleUpdateStore.isDevSettingsEnabled() && BundleUpdateStore.isSkipGPGEnabled()
            #else
            let skipGPGSwitch = false
            #endif
            OneKeyLog.info("BundleUpdate", "setCurrentUpdateBundleData: GPG check: skipGPG=\(skipGPGSwitch)")
            if !skipGPGSwitch {
                guard !params.signature.isEmpty,
                      BundleUpdateStore.validateMetadataFileSha256(bundleVersion, signature: params.signature, expectedAppVersion: params.appVersion, expectedBundleVersion: params.bundleVersion) else {
                    OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: GPG signature verification failed")
                    throw BundleUpdateError(.integrity, .install)
                }
                OneKeyLog.info("BundleUpdate", "setCurrentUpdateBundleData: GPG signature verified OK")
            } else {
                OneKeyLog.warn("BundleUpdate", "setCurrentUpdateBundleData: GPG signature verification skipped (DevSettings + skip-GPG enabled)")
            }

            guard let metadata = BundleUpdateStore.getMetadataFileContent(bundleVersion) else {
                OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: failed to read metadata.json content")
                throw BundleUpdateError(.missing, .install)
            }
            if !BundleUpdateStore.validateAllFilesInDir(bundleDirPath, metadata: metadata, appVersion: params.appVersion, bundleVersion: params.bundleVersion) {
                OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: extracted file verification failed")
                throw BundleUpdateError(.integrity, .install)
            }
            if !BundleUpdateStore.validateBundlePairCompatibility(bundleDirPath: bundleDirPath, metadata: metadata) {
                OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: bundle pair compatibility check failed")
                throw BundleUpdateError(.incompatible, .install)
            }

            if !params.signature.isEmpty {
                BundleUpdateStore.writeSignatureFile(bundleVersion, signature: params.signature)
            }
            let ud = UserDefaults.standard
            ud.set(bundleVersion, forKey: "currentBundleVersion")
            let currentNativeVersion = BundleUpdateStore.getCurrentNativeVersion()
            BundleUpdateStore.setNativeVersion(currentNativeVersion)
            let currentBuildNumber = BundleUpdateStore.getCurrentNativeBuildNumber()
            BundleUpdateStore.setNativeBuildNumber(currentBuildNumber)
            ud.synchronize()
            BundleUpdateStore.invalidateValidatedBundleInfoCache()
            OneKeyLog.info("BundleUpdate", "setCurrentUpdateBundleData: switched to \(bundleVersion)")
        }
    }

    func getWebEmbedPath() throws -> String {
        let path = BundleUpdateStore.getWebEmbedPath()
        OneKeyLog.debug("BundleUpdate", "getWebEmbedPath: \(path)")
        return path
    }

    func getWebEmbedPathAsync() throws -> Promise<String> {
        return Promise.async {
            let path = BundleUpdateStore.getWebEmbedPath()
            OneKeyLog.debug("BundleUpdate", "getWebEmbedPathAsync: \(path)")
            return path
        }
    }

    func getJsBundlePath() throws -> String {
        let path = BundleUpdateStore.currentBundleMainJSBundle() ?? ""
        OneKeyLog.debug("BundleUpdate", "getJsBundlePath: \(path.isEmpty ? "(empty/no bundle)" : path)")
        return path
    }

    func getJsBundlePathAsync() throws -> Promise<String> {
        return Promise.async {
            let path = BundleUpdateStore.currentBundleMainJSBundle() ?? ""
            OneKeyLog.info("BundleUpdate", "getJsBundlePathAsync: \(path.isEmpty ? "(empty/no bundle)" : path)")
            return path
        }
    }

    func getBackgroundJsBundlePath() throws -> String {
        let path = BundleUpdateStore.currentBundleBackgroundJSBundle() ?? ""
        OneKeyLog.debug("BundleUpdate", "getBackgroundJsBundlePath: \(path.isEmpty ? "(empty/no bundle)" : path)")
        return path
    }

    func getBackgroundJsBundlePathAsync() throws -> Promise<String> {
        return Promise.async {
            let path = BundleUpdateStore.currentBundleBackgroundJSBundle() ?? ""
            OneKeyLog.info("BundleUpdate", "getBackgroundJsBundlePathAsync: \(path.isEmpty ? "(empty/no bundle)" : path)")
            return path
        }
    }

    func getNativeAppVersion() throws -> Promise<String> {
        return Promise.async {
            let version = BundleUpdateStore.getCurrentNativeVersion()
            OneKeyLog.info("BundleUpdate", "getNativeAppVersion: \(version)")
            return version
        }
    }

    func getNativeBuildNumber() throws -> Promise<String> {
        return Promise.async {
            let buildNumber = BundleUpdateStore.getCurrentNativeBuildNumber()
            OneKeyLog.info("BundleUpdate", "getNativeBuildNumber: \(buildNumber)")
            return buildNumber
        }
    }

    func getBuiltinBundleVersion() throws -> Promise<String> {
        return Promise.async {
            let version = BundleUpdateStore.getBuiltinBundleVersion()
            OneKeyLog.info("BundleUpdate", "getBuiltinBundleVersion: \(version)")
            return version
        }
    }

    func testVerification() throws -> Promise<Bool> {
        return Promise.async {
            let testSignature = """
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
            let gpg = BundleCryptoCore.verifyGpgCleartext(testSignature)
            let result = gpg.valid ? gpg.sha256 : nil
            let isValid = result == "2ada9c871104fc40649fa3de67a7d8e33faadc18e9abd587e8bb85be0a003eba"
            OneKeyLog.info("BundleUpdate", "testVerification: GPG verification result: \(isValid)")
            return isValid
        }
    }

    func testSkipVerification() throws -> Promise<Bool> {
        return Promise.async {
            #if ALLOW_SKIP_GPG_VERIFICATION
            let result = BundleUpdateStore.isDevSettingsEnabled() && BundleUpdateStore.isSkipGPGEnabled()
            #else
            let result = false
            #endif
            OneKeyLog.info("BundleUpdate", "testSkipVerification: result=\(result)")
            return result
        }
    }

    func isSkipGpgVerificationAllowed() throws -> Bool {
        #if ALLOW_SKIP_GPG_VERIFICATION
        let result = true
        #else
        let result = false
        #endif
        OneKeyLog.info("BundleUpdate", "isSkipGpgVerificationAllowed: result=\(result)")
        return result
    }

    func isBundleExists(appVersion: String, bundleVersion: String) throws -> Promise<Bool> {
        return Promise.async {
            guard appVersion.isSafeVersionString, bundleVersion.isSafeVersionString else {
                OneKeyLog.error("BundleUpdate", "isBundleExists: invalid version string format: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid version string format"])
            }
            let folderName = "\(appVersion)-\(bundleVersion)"
            let path = (BundleUpdateStore.bundleDir() as NSString).appendingPathComponent(folderName)
            let resolvedPath = (path as NSString).resolvingSymlinksInPath
            let baseBundleDir = (BundleUpdateStore.bundleDir() as NSString).resolvingSymlinksInPath
            guard resolvedPath.hasPrefix(baseBundleDir + "/") else {
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle path outside bundle directory"])
            }
            let exists = FileManager.default.fileExists(atPath: path)
            OneKeyLog.info("BundleUpdate", "isBundleExists: appVersion=\(appVersion), bundleVersion=\(bundleVersion), exists=\(exists)")
            return exists
        }
    }

    func verifyExtractedBundle(appVersion: String, bundleVersion: String) throws -> Promise<Void> {
        return updatePromise(step: .verifyPackage) {
            OneKeyLog.info("BundleUpdate", "verifyExtractedBundle: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
            guard appVersion.isSafeVersionString, bundleVersion.isSafeVersionString else {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: invalid version string format: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
                throw BundleUpdateError(.invalidArgument, .verifyPackage)
            }
            let folderName = "\(appVersion)-\(bundleVersion)"
            let bundlePath = (BundleUpdateStore.bundleDir() as NSString).appendingPathComponent(folderName)
            let resolvedPath = (bundlePath as NSString).resolvingSymlinksInPath
            let baseBundleDir = (BundleUpdateStore.bundleDir() as NSString).resolvingSymlinksInPath
            guard resolvedPath.hasPrefix(baseBundleDir + "/") else {
                throw BundleUpdateError(.security, .verifyPackage)
            }
            guard FileManager.default.fileExists(atPath: bundlePath) else {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: bundle directory not found: \(bundlePath)")
                throw BundleUpdateError(.missing, .verifyPackage)
            }
            let metadataJsonPath = (bundlePath as NSString).appendingPathComponent("metadata.json")
            guard FileManager.default.fileExists(atPath: metadataJsonPath) else {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: metadata.json not found in \(bundlePath)")
                throw BundleUpdateError(.missing, .verifyPackage)
            }
            guard let data = FileManager.default.contents(atPath: metadataJsonPath),
                  let metadata = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: failed to parse metadata.json")
                throw BundleUpdateError(.integrity, .verifyPackage)
            }
            OneKeyLog.info("BundleUpdate", "verifyExtractedBundle: parsing metadata and validating files...")
            if !BundleUpdateStore.validateAllFilesInDir(bundlePath, metadata: metadata, appVersion: appVersion, bundleVersion: bundleVersion) {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: file integrity check failed")
                throw BundleUpdateError(.integrity, .verifyPackage)
            }
            if !BundleUpdateStore.validateBundlePairCompatibility(bundleDirPath: bundlePath, metadata: metadata) {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: bundle pair compatibility check failed")
                throw BundleUpdateError(.incompatible, .verifyPackage)
            }
            OneKeyLog.info("BundleUpdate", "verifyExtractedBundle: all files verified OK, fileCount=\(metadata.count)")
        }
    }

    func listLocalBundles() throws -> Promise<[LocalBundleInfo]> {
        return Promise.async {
            let bundleDir = BundleUpdateStore.bundleDir()
            let appVersion = BundleUpdateStore.getCurrentNativeVersion()
            guard !appVersion.isEmpty else { return [] }
            let fm = FileManager.default
            guard let contents = try? fm.contentsOfDirectory(atPath: bundleDir) else {
                OneKeyLog.info("BundleUpdate", "listLocalBundles: bundle directory empty or not found")
                return []
            }
            var results: [LocalBundleInfo] = []
            for name in contents {
                if name == BundleDiffReconstruction.stagingRootDirName { continue }
                let fullPath = (bundleDir as NSString).appendingPathComponent(name)
                var isDir: ObjCBool = false
                guard fm.fileExists(atPath: fullPath, isDirectory: &isDir), isDir.boolValue else { continue }
                guard let bundleVersion = BundleUpdateStore.bundleVersion(inFolder: name, appVersion: appVersion) else { continue }
                results.append(LocalBundleInfo(appVersion: appVersion, bundleVersion: bundleVersion))
            }
            OneKeyLog.info("BundleUpdate", "listLocalBundles: found \(results.count) bundles")
            return results
        }
    }

    func listAscFiles() throws -> Promise<[AscFileInfo]> {
        return Promise.async {
            let ascDir = BundleUpdateStore.ascDir()
            let fm = FileManager.default
            guard let contents = try? fm.contentsOfDirectory(atPath: ascDir) else {
                OneKeyLog.info("BundleUpdate", "listAscFiles: asc directory empty or not found")
                return []
            }
            var results: [AscFileInfo] = []
            for name in contents {
                let fullPath = (ascDir as NSString).appendingPathComponent(name)
                var isDir: ObjCBool = false
                guard fm.fileExists(atPath: fullPath, isDirectory: &isDir), !isDir.boolValue else { continue }
                let attrs = try? fm.attributesOfItem(atPath: fullPath)
                let fileSize = Double((attrs?[.size] as? UInt64) ?? 0)
                results.append(AscFileInfo(fileName: name, filePath: fullPath, fileSize: fileSize))
            }
            OneKeyLog.info("BundleUpdate", "listAscFiles: found \(results.count) files")
            return results
        }
    }

    func getSha256FromFilePath(filePath: String) throws -> Promise<String> {
        return Promise.async {
            OneKeyLog.info("BundleUpdate", "getSha256FromFilePath: filePath=\(filePath)")
            guard !filePath.isEmpty else {
                OneKeyLog.warn("BundleUpdate", "getSha256FromFilePath: empty filePath")
                return ""
            }

            // Restrict to bundle-related directories only
            let resolvedPath = (filePath as NSString).resolvingSymlinksInPath
            let bundleDir = (BundleUpdateStore.bundleDir() as NSString).resolvingSymlinksInPath
            let downloadDir = (BundleUpdateStore.downloadBundleDir() as NSString).resolvingSymlinksInPath
            // Enforce a separator boundary so e.g. "onekey-bundle-evil/x" is not
            // accepted as confined under "onekey-bundle". The base dir itself is allowed.
            func isConfined(to base: String) -> Bool {
                return resolvedPath == base || resolvedPath.hasPrefix(base + "/")
            }
            guard isConfined(to: bundleDir) || isConfined(to: downloadDir) else {
                OneKeyLog.error("BundleUpdate", "getSha256FromFilePath: path outside allowed directories: \(resolvedPath)")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "File path outside allowed bundle directories"])
            }

            let sha256 = BundleUpdateStore.calculateSHA256(filePath) ?? ""
            OneKeyLog.info("BundleUpdate", "getSha256FromFilePath: sha256=\(sha256.isEmpty ? "(empty)" : String(sha256.prefix(16)) + "...")")
            return sha256
        }
    }

    func testDeleteJsBundle(appVersion: String, bundleVersion: String) throws -> Promise<TestResult> {
        return Promise.async {
            #if DEBUG
            guard appVersion.isSafeVersionString, bundleVersion.isSafeVersionString else {
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid version string"])
            }
            OneKeyLog.info("BundleUpdate", "testDeleteJsBundle: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
            let folderName = "\(appVersion)-\(bundleVersion)"
            let jsBundlePath = (BundleUpdateStore.bundleDir() as NSString)
                .appendingPathComponent(folderName)
            let resolvedDir = (jsBundlePath as NSString).resolvingSymlinksInPath
            let baseBundleDir = (BundleUpdateStore.bundleDir() as NSString).resolvingSymlinksInPath
            guard resolvedDir.hasPrefix(baseBundleDir + "/") else {
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle path outside bundle directory"])
            }
            let path = (jsBundlePath as NSString).appendingPathComponent("main.jsbundle.hbc")
            let resolvedPath = (path as NSString).resolvingSymlinksInPath
            guard resolvedPath.hasPrefix(baseBundleDir + "/") else {
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle path outside bundle directory"])
            }

            if FileManager.default.fileExists(atPath: path) {
                try FileManager.default.removeItem(atPath: path)
                OneKeyLog.info("BundleUpdate", "testDeleteJsBundle: deleted \(path)")
                return TestResult(success: true, message: "Deleted jsBundle: \(path)")
            }
            OneKeyLog.warn("BundleUpdate", "testDeleteJsBundle: file not found: \(path)")
            return TestResult(success: false, message: "jsBundle not found: \(path)")
            #else
            throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Test fault-injection methods are disabled in release builds"])
            #endif
        }
    }

    func testDeleteJsRuntimeDir(appVersion: String, bundleVersion: String) throws -> Promise<TestResult> {
        return Promise.async {
            #if DEBUG
            guard appVersion.isSafeVersionString, bundleVersion.isSafeVersionString else {
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid version string"])
            }
            OneKeyLog.info("BundleUpdate", "testDeleteJsRuntimeDir: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
            let folderName = "\(appVersion)-\(bundleVersion)"
            let dirPath = (BundleUpdateStore.bundleDir() as NSString).appendingPathComponent(folderName)
            let resolvedDir = (dirPath as NSString).resolvingSymlinksInPath
            let baseBundleDir = (BundleUpdateStore.bundleDir() as NSString).resolvingSymlinksInPath
            guard resolvedDir.hasPrefix(baseBundleDir + "/") else {
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle path outside bundle directory"])
            }

            if FileManager.default.fileExists(atPath: dirPath) {
                try FileManager.default.removeItem(atPath: dirPath)
                OneKeyLog.info("BundleUpdate", "testDeleteJsRuntimeDir: deleted \(dirPath)")
                return TestResult(success: true, message: "Deleted js runtime directory: \(dirPath)")
            }
            OneKeyLog.warn("BundleUpdate", "testDeleteJsRuntimeDir: directory not found: \(dirPath)")
            return TestResult(success: false, message: "js runtime directory not found: \(dirPath)")
            #else
            throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Test fault-injection methods are disabled in release builds"])
            #endif
        }
    }

    func testDeleteMetadataJson(appVersion: String, bundleVersion: String) throws -> Promise<TestResult> {
        return Promise.async {
            #if DEBUG
            guard appVersion.isSafeVersionString, bundleVersion.isSafeVersionString else {
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid version string"])
            }
            OneKeyLog.info("BundleUpdate", "testDeleteMetadataJson: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
            let folderName = "\(appVersion)-\(bundleVersion)"
            let metadataPath = (BundleUpdateStore.bundleDir() as NSString)
                .appendingPathComponent(folderName)
            let resolvedDir = (metadataPath as NSString).resolvingSymlinksInPath
            let baseBundleDir = (BundleUpdateStore.bundleDir() as NSString).resolvingSymlinksInPath
            guard resolvedDir.hasPrefix(baseBundleDir + "/") else {
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle path outside bundle directory"])
            }
            let path = (metadataPath as NSString).appendingPathComponent("metadata.json")
            let resolvedPath = (path as NSString).resolvingSymlinksInPath
            guard resolvedPath.hasPrefix(baseBundleDir + "/") else {
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle path outside bundle directory"])
            }

            if FileManager.default.fileExists(atPath: path) {
                try FileManager.default.removeItem(atPath: path)
                OneKeyLog.info("BundleUpdate", "testDeleteMetadataJson: deleted \(path)")
                return TestResult(success: true, message: "Deleted metadata.json: \(path)")
            }
            OneKeyLog.warn("BundleUpdate", "testDeleteMetadataJson: file not found: \(path)")
            return TestResult(success: false, message: "metadata.json not found: \(path)")
            #else
            throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Test fault-injection methods are disabled in release builds"])
            #endif
        }
    }

    func testWriteEmptyMetadataJson(appVersion: String, bundleVersion: String) throws -> Promise<TestResult> {
        return Promise.async {
            #if DEBUG
            guard appVersion.isSafeVersionString, bundleVersion.isSafeVersionString else {
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid version string"])
            }
            OneKeyLog.info("BundleUpdate", "testWriteEmptyMetadataJson: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
            let folderName = "\(appVersion)-\(bundleVersion)"
            let jsRuntimeDir = (BundleUpdateStore.bundleDir() as NSString).appendingPathComponent(folderName)
            let resolvedDir = (jsRuntimeDir as NSString).resolvingSymlinksInPath
            let baseBundleDir = (BundleUpdateStore.bundleDir() as NSString).resolvingSymlinksInPath
            guard resolvedDir.hasPrefix(baseBundleDir + "/") else {
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle path outside bundle directory"])
            }
            let metadataPath = (jsRuntimeDir as NSString).appendingPathComponent("metadata.json")
            let resolvedPath = (metadataPath as NSString).resolvingSymlinksInPath
            guard resolvedPath.hasPrefix(baseBundleDir + "/") else {
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle path outside bundle directory"])
            }

            if !FileManager.default.fileExists(atPath: jsRuntimeDir) {
                try FileManager.default.createDirectory(atPath: jsRuntimeDir, withIntermediateDirectories: true)
            }

            let emptyJson: [String: Any] = [:]
            let data = try JSONSerialization.data(withJSONObject: emptyJson, options: .prettyPrinted)
            try data.write(to: URL(fileURLWithPath: metadataPath), options: .atomic)

            OneKeyLog.info("BundleUpdate", "testWriteEmptyMetadataJson: created \(metadataPath)")
            return TestResult(success: true, message: "Created empty metadata.json: \(metadataPath)")
            #else
            throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Test fault-injection methods are disabled in release builds"])
            #endif
        }
    }
}

