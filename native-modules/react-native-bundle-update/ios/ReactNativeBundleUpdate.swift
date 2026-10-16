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
            if !validateEntryBundlesSha256(bundleDirPath: bundleDirPath, metadata: metadata) {
                OneKeyLog.error("BundleUpdate", "processPreLaunchPendingTask: entry bundle SHA256 verification failed for \(folderName)")
                failPendingTask(mmkv: mmkv, taskDict: taskDict, reason: "ERR_VERIFY_FAILED:Entry bundle SHA256 verification failed")
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

    private static func fileMetadataEntries(
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
        expectedAppType: String? = "ios"
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
            expectedAppType: expectedAppType
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
        let allowedChars = CharacterSet.alphanumerics.union(CharacterSet(charactersIn: ".-_"))
        return !self.isEmpty && self.unicodeScalars.allSatisfy { allowedChars.contains($0) }
    }
}

/// URLSession delegate that handles download progress and HTTPS redirect validation
private class DownloadDelegate: NSObject, URLSessionDownloadDelegate {
    private static let maxDownloadBytes: Int64 = 512 * 1024 * 1024

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

    // Serial queue protects mutable state (listeners, nextListenerId, isDownloading)
    private let stateQueue = DispatchQueue(label: "so.onekey.bundleupdate.state")
    private var listeners: [BundleListener] = []
    private var nextListenerId: Double = 1
    private var isDownloading = false
    private var clearingDownloads = false
    private var activeRunDelegate: DownloadDelegate?
    private var urlSession: URLSession?
    private var downloadDelegate: DownloadDelegate?
    private var downloadFilePath: String?
    private var downloadSha256: String?

    private func createURLSession() -> URLSession {
        let config = URLSessionConfiguration.default
        config.tlsMinimumSupportedProtocolVersion = .TLSv12
        let delegate = DownloadDelegate()
        self.downloadDelegate = delegate
        return URLSession(configuration: config, delegate: delegate, delegateQueue: nil)
    }

    /// Path of the bundle being downloaded right now (mirrors filePath in
    /// downloadBundle). Set on entry, cleared on exit. Single value because
    /// isDownloading enforces at most one in-flight download per process.
    /// Read by the background-snapshot handler so it knows where to drop the
    /// `.resume` sidecar.
    private var activeDownloadFilePath: String?
    /// OCDS §5.7: the highest progress percent already reported to JS for the
    /// in-flight download. Used to floor single-stream progress so the bar never
    /// moves backward at the concurrent→single-stream seam. Guarded by
    /// `stateQueue`. Reset to 0 only on a genuine restart (permanent fallback).
    private var lastReportedProgress: Int = 0
    private var didEnterBackgroundObserver: NSObjectProtocol?

    override init() {
        super.init()
        urlSession = createURLSession()
        registerBackgroundSnapshotObserver()
    }

    deinit {
        if let token = didEnterBackgroundObserver {
            NotificationCenter.default.removeObserver(token)
        }
        // URLSession retains its delegate strongly until invalidated.
        // Without this call the session (and its DownloadDelegate) would
        // leak past module deallocation — relevant in dev hot-reload and
        // any future test harness that spins up multiple module instances.
        urlSession?.invalidateAndCancel()
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
        // Read both isDownloading AND activeDownloadFilePath inside the
        // same stateQueue.sync block. A concurrent downloadBundle entry
        // flips isDownloading=true at the start of the Promise body but
        // only writes activeDownloadFilePath later (after the early
        // guards / version + URL validation). Without this paired read,
        // didEnterBackground could observe isDownloading=true while
        // activeDownloadFilePath is still nil from a previous run, or —
        // on the unwind via defer — see isDownloading=false alongside a
        // not-yet-cleared path.
        let snapshot: (URLSession?, DownloadDelegate?, String?) = self.stateQueue.sync {
            guard self.isDownloading, !self.clearingDownloads else { return (nil, nil, nil) }
            return (self.urlSession, self.activeRunDelegate, self.activeDownloadFilePath)
        }
        guard let session = snapshot.0, let runDelegate = snapshot.1,
              let filePath = snapshot.2 else { return }

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

        session.getAllTasks { [weak self] tasks in
            let group = DispatchGroup()
            for task in tasks {
                guard let dl = task as? URLSessionDownloadTask, dl.state == .running else { continue }
                group.enter()
                dl.cancel(byProducingResumeData: { data in
                    if let data = data, data.count > 0 {
                        self?.stateQueue.sync {
                            guard self?.activeRunDelegate === runDelegate,
                                  self?.clearingDownloads == false,
                                  !runDelegate.wasCancelledByClear else { return }
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
                    group.leave()
                })
            }
            group.notify(queue: .main) {
                bgTask.endOnce()
            }
        }
    }

    private func sendEvent(type: String, progress: Int = 0, message: String = "") {
        let event = BundleDownloadEvent(type: type, progress: Double(progress), message: message)
        let currentListeners = stateQueue.sync { self.listeners }
        for listener in currentListeners {
            do {
                listener.callback(event)
            } catch {
                OneKeyLog.error("BundleUpdate", "Error sending event: \(error)")
            }
        }
    }

    func addDownloadListener(callback: @escaping (BundleDownloadEvent) -> Void) throws -> Double {
        return stateQueue.sync {
            let id = nextListenerId
            nextListenerId += 1
            listeners.append(BundleListener(id: id, callback: callback))
            OneKeyLog.debug("BundleUpdate", "addDownloadListener: id=\(id), totalListeners=\(listeners.count)")
            return id
        }
    }

    func removeDownloadListener(id: Double) throws {
        stateQueue.sync {
            listeners.removeAll { $0.id == id }
            OneKeyLog.debug("BundleUpdate", "removeDownloadListener: id=\(id), totalListeners=\(listeners.count)")
        }
    }

    func downloadBundle(params: BundleDownloadParams) throws -> Promise<BundleDownloadResult> {
        return updatePromise(step: .download) { [weak self] in
            guard let self = self else { throw BundleUpdateError(.unknown, .download) }

            let (alreadyDownloading, runDelegate, runSession) = self.stateQueue.sync { () -> (Bool, DownloadDelegate?, URLSession?) in
                if self.isDownloading || self.clearingDownloads { return (true, nil, nil) }
                self.isDownloading = true
                self.downloadDelegate?.reset()
                self.activeRunDelegate = self.downloadDelegate
                return (false, self.downloadDelegate, self.urlSession)
            }
            guard !alreadyDownloading else {
                OneKeyLog.warn("BundleUpdate", "downloadBundle: rejected, already downloading")
                throw BundleUpdateError(.invalidArgument, .download)
            }
            guard let runDelegate, let runSession else {
                throw BundleUpdateError(.unknown, .download)
            }
            defer {
                // Clear isDownloading + the snapshot anchor under the same
                // queue so didEnterBackground's paired read can't observe
                // a half-cleared state (isDownloading=true while
                // activeDownloadFilePath=nil, or vice versa).
                self.stateQueue.sync {
                    runDelegate.finishRun()
                    if self.activeRunDelegate === runDelegate {
                        self.isDownloading = false
                        self.activeDownloadFilePath = nil
                        self.activeRunDelegate = nil
                    }
                }
            }

            let appVersion = params.latestVersion
            let bundleVersion = params.bundleVersion
            let downloadUrl = params.downloadUrl
            let sha256 = params.sha256

            OneKeyLog.info("BundleUpdate", "downloadBundle: appVersion=\(appVersion), bundleVersion=\(bundleVersion), fileSize=\(params.fileSize), url=\(downloadUrl)")

            guard appVersion.isSafeVersionString, bundleVersion.isSafeVersionString else {
                OneKeyLog.error("BundleUpdate", "downloadBundle: invalid version string format: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
                throw BundleUpdateError(.invalidArgument, .download)
            }

            guard downloadUrl.hasPrefix("https://") else {
                OneKeyLog.error("BundleUpdate", "downloadBundle: URL is not HTTPS: \(downloadUrl)")
                throw BundleUpdateError(.security, .download)
            }

            let fileName = "\(appVersion)-\(bundleVersion).zip"
            let filePath = (BundleUpdateStore.downloadBundleDir() as NSString).appendingPathComponent(fileName)
            // Persisted resume blob from a previous failed attempt. Lives next
            // to the bundle so it shares fate (delete-with-bundle) without
            // polluting the bundle dir's cache lookup.
            let resumeDataPath = "\(filePath).resume"

            let result = BundleDownloadResult(
                downloadUrl: downloadUrl,
                latestVersion: appVersion,
                bundleVersion: bundleVersion,
                sha256: sha256
            )

            OneKeyLog.info("BundleUpdate", "downloadBundle: filePath=\(filePath)")

            // Check if file already exists and is valid
            if FileManager.default.fileExists(atPath: filePath) {
                OneKeyLog.info("BundleUpdate", "downloadBundle: file already exists, verifying SHA256...")
                if self.verifyBundleSHA256(filePath, sha256: sha256) {
                    OneKeyLog.info("BundleUpdate", "downloadBundle: existing file SHA256 valid, skipping download")
                    // A valid completed bundle invalidates any stale resume blob.
                    try? FileManager.default.removeItem(atPath: resumeDataPath)
                    DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) { [weak self] in
                        guard !runDelegate.wasCancelledByClear else { return }
                        self?.sendEvent(type: "update/complete")
                    }
                    if runDelegate.wasCancelledByClear {
                        throw BundleUpdateError(.cancelled, .download)
                    }
                    return result
                } else {
                    OneKeyLog.warn("BundleUpdate", "downloadBundle: existing file SHA256 mismatch, re-downloading")
                    try? FileManager.default.removeItem(atPath: filePath)
                    // Hash-failed bundle means the resume blob is also poisoned.
                    try? FileManager.default.removeItem(atPath: resumeDataPath)
                }
            }

            // === Concurrent + background multi-range download (shared core) ===
            // Delegate to react-native-range-downloader's RangeDownloader (the
            // 8-range background session that originated in this module). On
            // success the bundle is fully assembled at filePath (downloads
            // continue even if the app is suspended). A `.fallback` outcome —
            // Range unsupported, server returned 200, file too small, or a
            // transient error — makes us hand off to the single-stream path
            // below; the range downloader keeps its `.segN` files for the next
            // attempt (its background session resumes them by taskId).
            self.sendEvent(type: "update/start")
            // §5.7: a fresh downloadBundle starts the progress floor at 0; the
            // concurrent path raises it as bytes land (below), and a transient
            // single-stream fallback keeps it.
            self.stateQueue.sync {
                self.activeDownloadFilePath = filePath
                self.lastReportedProgress = 0
            }
            if runDelegate.wasCancelledByClear {
                throw BundleUpdateError(.cancelled, .download)
            }

            // Stable, unique task id for this bundle. Same value across retry
            // attempts so the range downloader can resume its `.segN` files, and
            // it lets the progress listener below filter events to THIS download.
            let rangeTaskId = "\(appVersion)-\(bundleVersion)"

            // Bridge RangeDownloader progress events back into this module's
            // `update/downloading` stream. We only react to "progress" events on
            // the bundle channel for our own task id; "start"/"complete"/
            // "fallback" are surfaced by this method's own sendEvent calls and
            // the download outcome below, so we don't double-emit them here.
            let progressListenerId = RangeDownloader.shared.addListener { [weak self] event in
                guard let self = self,
                      event.channel.stringValue == DownloadChannel.bundle.stringValue,
                      event.taskId == rangeTaskId,
                      event.type == "progress",
                      !runDelegate.wasCancelledByClear else { return }
                // §5.7: record the concurrent floor so a later single-stream
                // transient fallback resumes the bar from here, not from 0.
                let floored: Int = self.stateQueue.sync {
                    let clamped = max(self.lastReportedProgress, Int(event.progress))
                    self.lastReportedProgress = clamped
                    return clamped
                }
                self.sendEvent(type: "update/downloading", progress: floored)
            }
            defer { RangeDownloader.shared.removeListener(progressListenerId) }

            // A `.fallback` outcome covers two very different situations whose
            // recovery must NOT be the same:
            //   • Permanent — Range unsupported / file too small / server 200 /
            //     SHA mismatch. The downloader has already cleaned its `.segN`
            //     segments, so none survive; single-stream is the only way.
            //   • Transient — a network drop or app suspend interrupted a segment
            //     mid-flight ("segment N missing/truncated"). The downloader
            //     RETAINS the segments that DID complete; the next concurrent
            //     attempt resumes only the missing one(s).
            // This method used to `discardArtifacts` on EVERY fallback and restart
            // single-stream from byte 0, so any background/lock/network blip threw
            // away tens of MB and the user saw progress reset to 0. We now retry
            // the concurrent path while resumable segments survive, and fall back
            // to single-stream only as a last resort — WITHOUT deleting those
            // segments, so a later attempt can still resume them.
            let maxConcurrentAttempts = 3
            var concurrentAttempt = 0
            // OCDS §4: consume the EXPLICIT typed class from the in-process core
            // (`RangeDownloadClass`) instead of inferring transient-vs-permanent
            // from whether `.segN` happens to survive on disk. The wire enum stays
            // `completed | fallback` until nitrogen regen; we call the in-process
            // `download(...)` directly, so we get the precise class here.
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
                    // SHA256 is verified by this module right after assembly (below),
                    // so we don't double-hash inside the range downloader.
                    expectedSha256: nil,
                    segmentCount: nil,
                    minConcurrentBytes: nil
                )
                if runDelegate.wasCancelledByClear {
                    throw BundleUpdateError(.cancelled, .download)
                }
                if rangeClass == .completed {
                    break // completed
                }
                if let rangeError {
                    let updateError = bundleUpdateError(rangeError, step: .download)
                    if updateError.code != .network && updateError.code != .timeout {
                        self.sendEvent(type: "update/error", message: updateError.description)
                        throw updateError
                    }
                }
                if rangeKind == .redirectRejected || rangeKind == .checksumMismatch {
                    let updateError = BundleUpdateError(rangeKind == .redirectRejected ? .security : .integrity, .download)
                    self.sendEvent(type: "update/error", message: updateError.description)
                    throw updateError
                }
                // §4: a Transient class is resumable — retry the concurrent path.
                // The core keeps `.segN`; the next call resumes only what's missing.
                if rangeClass == .fallbackTransient,
                   concurrentAttempt < maxConcurrentAttempts {
                    OneKeyLog.info("BundleUpdate", "downloadBundle: concurrent transient fallback (\(rangeReason ?? "")), resuming surviving segments (attempt \(concurrentAttempt)/\(maxConcurrentAttempts))")
                    // Brief backoff so a flapping network / suspend settles before
                    // we re-probe and resume the missing segment(s).
                    try? await Task.sleep(nanoseconds: 1_500_000_000)
                    if runDelegate.wasCancelledByClear {
                        throw BundleUpdateError(.cancelled, .download)
                    }
                    continue
                }
                break // permanent, or transient retries exhausted
            }
            if runDelegate.wasCancelledByClear {
                throw BundleUpdateError(.cancelled, .download)
            }

            if rangeClass != .completed {
                if rangeClass == .fallbackTransient {
                    // §4 Transient that outlived our concurrent retries: KEEP the
                    // segments so the next downloadBundle call's concurrent path can
                    // resume them. Single-stream is just this call's safety net, and
                    // its progress is floored (below) so the bar never drops to 0.
                    OneKeyLog.info("BundleUpdate", "downloadBundle: concurrent transient fallback persists (\(rangeReason ?? "")), using single-stream (segments preserved for resume)")
                } else {
                    // §4 Permanent (segments already cleaned by the downloader):
                    // clear any residue and hand off to single-stream from a clean
                    // slot; this is a genuine restart, so the progress floor resets.
                    OneKeyLog.info("BundleUpdate", "downloadBundle: concurrent permanent fallback (\(rangeReason ?? "")), using single-stream")
                    RangeDownloader.shared.discardArtifacts(filePath: filePath)
                }
                // fall through to the single-stream path below
            } else {
                OneKeyLog.info("BundleUpdate", "downloadBundle: concurrent finished, verifying SHA256...")
                guard let downloadedHash = BundleUpdateStore.calculateSHA256(filePath) else {
                    throw BundleUpdateError(.storage, .download)
                }
                if !BundleCryptoCore.secureEqualHex(downloadedHash, sha256) {
                    let reason = BundleUpdateStore.lastSHA256FailureReason() ?? "MISMATCH"
                    try? FileManager.default.removeItem(atPath: filePath)
                    self.sendEvent(type: "update/error", message: BundleUpdateError(.integrity, .download).description)
                    throw BundleUpdateError(.integrity, .download)
                }
                try? FileManager.default.removeItem(atPath: resumeDataPath)
                if runDelegate.wasCancelledByClear {
                    throw BundleUpdateError(.cancelled, .download)
                }
                self.sendEvent(type: "update/complete")
                OneKeyLog.info("BundleUpdate", "downloadBundle: concurrent completed successfully, appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
                return result
            }

            // Download the file
            guard let url = URL(string: downloadUrl) else {
                OneKeyLog.error("BundleUpdate", "downloadBundle: invalid URL: \(downloadUrl)")
                throw BundleUpdateError(.invalidArgument, .download)
            }

            let session = runSession

            // Resume blob from a prior failed attempt; iOS rebuilds the byte
            // offset internally so we never need a Range header on this path.
            let persistedResumeData: Data? = {
                guard FileManager.default.fileExists(atPath: resumeDataPath),
                      let data = try? Data(contentsOf: URL(fileURLWithPath: resumeDataPath)),
                      data.count > 0 else { return nil }
                return data
            }()

            // update/start already emitted before the concurrent attempt above.
            OneKeyLog.info("BundleUpdate", "downloadBundle: starting single-stream download (resumeBytes=\(persistedResumeData?.count ?? 0))...")

            let request = URLRequest(url: url)

            // Use delegate-based download for real progress reporting
            let delegate = runDelegate
            // OCDS §5.7: reported progress is monotonic non-decreasing within a
            // run, and resets only on a GENUINE restart. The single-stream
            // delegate emits its own 0→100, which would jump the bar backward at
            // the concurrent→single-stream seam. Floor it: a transient fallback
            // KEEPS the floor (the concurrent path already reported ~N%), a
            // permanent fallback is a genuine restart so the floor resets to 0.
            let progressFloor = (rangeClass == .fallbackTransient)
                ? self.stateQueue.sync { self.lastReportedProgress }
                : 0
            self.stateQueue.sync { self.lastReportedProgress = progressFloor }
            delegate.onProgress = { [weak self] progress in
                guard let self = self, !runDelegate.wasCancelledByClear else { return }
                let floored: Int = self.stateQueue.sync {
                    let clamped = max(self.lastReportedProgress, progress)
                    self.lastReportedProgress = clamped
                    return clamped
                }
                self.sendEvent(type: "update/downloading", progress: floored)
            }

            // Anchor for the background-snapshot handler. Set BEFORE
            // task.resume() so a foreground→background transition that
            // races the very first bytes still finds a path to write the
            // resume blob to. Pair the write with the same stateQueue the
            // snapshot reader uses, so isDownloading=true and a non-nil
            // activeDownloadFilePath always go together.
            self.stateQueue.sync { self.activeDownloadFilePath = filePath }
            if runDelegate.wasCancelledByClear {
                throw BundleUpdateError(.cancelled, .download)
            }

            let downloadOutcome: Result<(URL, URLResponse), Error>
            do {
                let value = try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<(URL, URLResponse), Error>) in
                    guard delegate.setContinuation(continuation) else { return }
                    let task: URLSessionDownloadTask
                    if let resumeData = persistedResumeData {
                        task = session.downloadTask(withResumeData: resumeData)
                    } else {
                        task = session.downloadTask(with: request)
                    }
                    task.resume()
                }
                downloadOutcome = .success(value)
            } catch {
                downloadOutcome = .failure(error)
            }

            switch downloadOutcome {
            case .failure(let error):
                // Persist the freshly captured resume blob for the next call.
                // If iOS gave us nothing usable, clear any stale blob so a
                // future attempt isn't held back by an unresumable cut point.
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
                    ? BundleUpdateError(.cancelled, .download)
                    : (delegate.wasInsecureRedirectRejected
                        ? BundleUpdateError(.security, .download)
                        : bundleUpdateError(error, step: .download))
                if !intentionallyCancelled {
                    self.sendEvent(type: "update/error", message: updateError.description)
                }
                throw updateError

            case .success(let (tempURL, response)):
                // Successful completion ⇒ no longer need the resume blob.
                try? FileManager.default.removeItem(atPath: resumeDataPath)

                // Verify HTTPS was maintained (no HTTP redirect)
                if let httpResponse = response as? HTTPURLResponse,
                   let responseUrl = httpResponse.url,
                   responseUrl.scheme?.lowercased() != "https" {
                    OneKeyLog.error("BundleUpdate", "downloadBundle: redirected to non-HTTPS URL: \(responseUrl)")
                    throw BundleUpdateError(.security, .download)
                }

                // 206 is acceptable when the OS finished a Range-resumed task on
                // our behalf; everything else non-200 is a real error.
                guard let httpResponse = response as? HTTPURLResponse,
                      httpResponse.statusCode == 200 || httpResponse.statusCode == 206 else {
                    let statusCode = (response as? HTTPURLResponse)?.statusCode ?? -1
                    OneKeyLog.error("BundleUpdate", "downloadBundle: HTTP error, statusCode=\(statusCode)")
                    let updateError = BundleUpdateError(.http, .download, httpStatus: statusCode)
                    self.sendEvent(type: "update/error", message: updateError.description)
                    throw updateError
                }

                OneKeyLog.info("BundleUpdate", "downloadBundle: download finished, HTTP \(httpResponse.statusCode), moving to destination...")

                // Move downloaded file to destination
                let destDir = (filePath as NSString).deletingLastPathComponent
                if !FileManager.default.fileExists(atPath: destDir) {
                    try FileManager.default.createDirectory(atPath: destDir, withIntermediateDirectories: true)
                }
                if FileManager.default.fileExists(atPath: filePath) {
                    try FileManager.default.removeItem(atPath: filePath)
                }
                try FileManager.default.moveItem(at: tempURL, to: URL(fileURLWithPath: filePath))

                // Verify SHA256
                OneKeyLog.info("BundleUpdate", "downloadBundle: verifying SHA256...")
                guard let downloadedHash = BundleUpdateStore.calculateSHA256(filePath) else {
                    throw BundleUpdateError(.storage, .download)
                }
                if !BundleCryptoCore.secureEqualHex(downloadedHash, sha256) {
                    let reason = BundleUpdateStore.lastSHA256FailureReason() ?? "MISMATCH"
                    try? FileManager.default.removeItem(atPath: filePath)
                    OneKeyLog.error("BundleUpdate", "downloadBundle: SHA256 verification failed after download, reason=\(reason)")
                    self.sendEvent(type: "update/error", message: BundleUpdateError(.integrity, .download).description)
                    throw BundleUpdateError(.integrity, .download)
                }

                // A verified single-stream file is authoritative — drop any
                // concurrent `.segN` segments we intentionally kept across a
                // transient fallback so they don't linger as orphans.
                RangeDownloader.shared.discardArtifacts(filePath: filePath)

                if runDelegate.wasCancelledByClear {
                    throw BundleUpdateError(.cancelled, .download)
                }
                self.sendEvent(type: "update/complete")
                OneKeyLog.info("BundleUpdate", "downloadBundle: completed successfully, appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
                return result
            }
        }
    }

    private func verifyBundleSHA256(_ bundlePath: String, sha256: String) -> Bool {
        guard let calculated = BundleUpdateStore.calculateSHA256(bundlePath) else {
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

    func verifyBundleASC(params: BundleVerifyASCParams) throws -> Promise<Void> {
        return updatePromise(step: .verifyASC) {
            let sha256 = params.sha256
            let appVersion = params.latestVersion
            let bundleVersion = params.bundleVersion
            let signature = params.signature

            guard appVersion.isSafeVersionString, bundleVersion.isSafeVersionString else {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: invalid version string format: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
                throw BundleUpdateError(.invalidArgument, .verifyASC)
            }

            let filePath = (BundleUpdateStore.downloadBundleDir() as NSString)
                .appendingPathComponent("\(appVersion)-\(bundleVersion).zip")
            let resolvedFilePath = (filePath as NSString).resolvingSymlinksInPath
            let baseDownloadDir = (BundleUpdateStore.downloadBundleDir() as NSString).resolvingSymlinksInPath
            guard resolvedFilePath.hasPrefix(baseDownloadDir + "/") else {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: ZIP outside download directory: \(resolvedFilePath)")
                throw BundleUpdateError(.security, .verifyASC)
            }
            guard FileManager.default.fileExists(atPath: resolvedFilePath) else {
                throw BundleUpdateError(.missing, .verifyASC)
            }
            OneKeyLog.info("BundleUpdate", "verifyBundleASC: appVersion=\(appVersion), bundleVersion=\(bundleVersion), file=\(filePath), signatureLength=\(signature.count)")

            let folderName = "\(appVersion)-\(bundleVersion)"
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

            // GPG verification skipped only when both DevSettings and skip-GPG toggle are enabled
            #if ALLOW_SKIP_GPG_VERIFICATION
            let skipGPG = BundleUpdateStore.isDevSettingsEnabled() && BundleUpdateStore.isSkipGPGEnabled()
            #else
            let skipGPG = false
            #endif
            OneKeyLog.info("BundleUpdate", "verifyBundleASC: GPG check: skipGPG=\(skipGPG)")

            if !skipGPG {
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: pre-verifying GPG signature before extraction...")
                guard let preVerifiedSha256 = BundleUpdateStore.readMetadataFileSha256(
                    signature,
                    expectedAppVersion: appVersion,
                    expectedBundleVersion: bundleVersion,
                    expectedAppType: "ios"
                ), !preVerifiedSha256.isEmpty else {
                    OneKeyLog.error("BundleUpdate", "verifyBundleASC: pre-extraction GPG signature verification failed")
                    throw BundleUpdateError(.integrity, .verifyASC)
                }

                OneKeyLog.info("BundleUpdate", "verifyBundleASC: verifying SHA256 of downloaded file...")
                guard let calculated = BundleUpdateStore.calculateSHA256(filePath) else {
                    throw BundleUpdateError(.storage, .verifyASC)
                }
                if !BundleCryptoCore.secureEqualHex(calculated, sha256) {
                    // Keep the detailed hash reason in the local log only.
                    let reason = BundleUpdateStore.lastSHA256FailureReason() ?? "MISMATCH"
                    OneKeyLog.error("BundleUpdate", "verifyBundleASC: SHA256 verification failed for file=\(filePath), reason=\(reason)")
                    throw BundleUpdateError(.integrity, .verifyASC)
                }
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: SHA256 verified OK")
            } else {
                OneKeyLog.warn("BundleUpdate", "verifyBundleASC: SHA256 + GPG verification skipped (DevSettings enabled)")
            }

            if isInstalled {
                if !skipGPG && !BundleUpdateStore.validateMetadataFileSha256(folderName, signature: signature, expectedAppVersion: appVersion, expectedBundleVersion: bundleVersion) {
                    throw BundleUpdateError(.integrity, .verifyASC)
                }
                guard let metadata = BundleUpdateStore.getMetadataFileContent(folderName),
                      BundleCryptoCore.validateExtractedPathSafety(destination),
                      BundleUpdateStore.validateAllFilesInDir(destination, metadata: metadata, appVersion: appVersion, bundleVersion: bundleVersion),
                      BundleUpdateStore.validateBundlePairCompatibility(bundleDirPath: destination, metadata: metadata) else {
                    throw BundleUpdateError(.integrity, .verifyASC)
                }
                return
            }

            // Check zip file size and uncompressed payload size before extraction (decompression bomb protection)
            let maxZipFileSize: UInt64 = 512 * 1024 * 1024 // 512 MB
            let maxUncompressedSize: UInt64 = 512 * 1024 * 1024
            if let attrs = try? FileManager.default.attributesOfItem(atPath: filePath),
               let fileSize = attrs[.size] as? UInt64 {
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: zip file size=\(fileSize) bytes")
                if fileSize > maxZipFileSize {
                    OneKeyLog.error("BundleUpdate", "verifyBundleASC: zip file too large (\(fileSize) > \(maxZipFileSize))")
                    throw BundleUpdateError(.security, .verifyASC)
                }
            }

            var payloadError: NSError?
            let payloadSize = SSZipArchive.payloadSizeForArchive(atPath: filePath, error: &payloadError)
            if payloadError != nil || payloadSize.uint64Value == 0 || payloadSize.uint64Value > maxUncompressedSize {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: zip payload size invalid or exceeds limit (payloadSize=\(payloadSize), error=\(String(describing: payloadError)))")
                throw BundleUpdateError(.security, .verifyASC)
            }

            // Clean up any existing destination directory before unzipping so leftover files cannot persist
            if FileManager.default.fileExists(atPath: destination) {
                try? FileManager.default.removeItem(atPath: destination)
            }

            // Unzip using SSZipArchive
            OneKeyLog.info("BundleUpdate", "verifyBundleASC: extracting zip to \(destination)...")
            do {
                try SSZipArchive.unzipFile(atPath: filePath, toDestination: destination, overwrite: true, password: nil)
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: extraction completed")
            } catch {
                // Keep the file path in the local log only.
                let nsErr = error as NSError
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: unzip failed: domain=\(nsErr.domain) code=\(nsErr.code) desc=\(nsErr.localizedDescription)")
                try? FileManager.default.removeItem(atPath: destination)
                throw BundleUpdateError(.storage, .verifyASC)
            }

            // Validate extracted paths (symlinks, path traversal)
            OneKeyLog.info("BundleUpdate", "verifyBundleASC: validating extracted path safety...")
            if !BundleCryptoCore.validateExtractedPathSafety(destination) {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: path traversal or symlink attack detected")
                try? FileManager.default.removeItem(atPath: destination)
                throw BundleUpdateError(.security, .verifyASC)
            }

            // Enforce extracted entry count and total uncompressed size limits
            let maxZipEntries = 4096
            if let enumerator = FileManager.default.enumerator(atPath: destination) {
                var entryCount = 0
                var totalUncompressedBytes: UInt64 = 0
                while let relPath = enumerator.nextObject() as? String {
                    entryCount += 1
                    if entryCount > maxZipEntries {
                        OneKeyLog.error("BundleUpdate", "verifyBundleASC: extracted entry count exceeds maximum allowed (\(maxZipEntries))")
                        try? FileManager.default.removeItem(atPath: destination)
                        throw BundleUpdateError(.security, .verifyASC)
                    }
                    let fullPath = (destination as NSString).appendingPathComponent(relPath)
                    if let attrs = try? FileManager.default.attributesOfItem(atPath: fullPath),
                       let fileSize = attrs[.size] as? UInt64 {
                        totalUncompressedBytes += fileSize
                        if totalUncompressedBytes > maxUncompressedSize {
                            OneKeyLog.error("BundleUpdate", "verifyBundleASC: extracted size exceeds maximum allowed (\(maxUncompressedSize))")
                            try? FileManager.default.removeItem(atPath: destination)
                            throw BundleUpdateError(.security, .verifyASC)
                        }
                    }
                }
            }

            let metadataJsonPath = (destination as NSString).appendingPathComponent("metadata.json")
            guard FileManager.default.fileExists(atPath: metadataJsonPath) else {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: metadata.json not found after extraction")
                try? FileManager.default.removeItem(atPath: destination)
                throw BundleUpdateError(.missing, .verifyASC)
            }

            let currentBundleVersion = "\(appVersion)-\(bundleVersion)"
            if !skipGPG {
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: validating GPG signature for metadata...")
                if !BundleUpdateStore.validateMetadataFileSha256(currentBundleVersion, signature: signature, expectedAppVersion: appVersion, expectedBundleVersion: bundleVersion) {
                    OneKeyLog.error("BundleUpdate", "verifyBundleASC: GPG signature verification failed")
                    try? FileManager.default.removeItem(atPath: destination)
                    throw BundleUpdateError(.integrity, .verifyASC)
                }
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: GPG signature verified OK")
            } else {
                OneKeyLog.warn("BundleUpdate", "verifyBundleASC: GPG verification skipped (DevSettings enabled)")
            }

            OneKeyLog.info("BundleUpdate", "verifyBundleASC: validating all extracted files against metadata...")
            guard let metadata = BundleUpdateStore.getMetadataFileContent(currentBundleVersion) else {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: failed to read metadata.json content")
                try? FileManager.default.removeItem(atPath: destination)
                throw BundleUpdateError(.missing, .verifyASC)
            }

            if !BundleUpdateStore.validateAllFilesInDir(destination, metadata: metadata, appVersion: appVersion, bundleVersion: bundleVersion) {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: file integrity check failed")
                try? FileManager.default.removeItem(atPath: destination)
                throw BundleUpdateError(.integrity, .verifyASC)
            }
            if !BundleUpdateStore.validateBundlePairCompatibility(bundleDirPath: destination, metadata: metadata) {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: bundle pair compatibility check failed")
                try? FileManager.default.removeItem(atPath: destination)
                throw BundleUpdateError(.incompatible, .verifyASC)
            }

            OneKeyLog.info("BundleUpdate", "verifyBundleASC: all verifications passed, appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
        }
    }

    func verifyBundle(params: BundleVerifyParams) throws -> Promise<Void> {
        return updatePromise(step: .verifyPackage) {
            let sha256 = params.sha256
            let appVersion = params.latestVersion
            let bundleVersion = params.bundleVersion

            guard appVersion.isSafeVersionString, bundleVersion.isSafeVersionString else {
                OneKeyLog.error("BundleUpdate", "verifyBundle: invalid version string format: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
                throw BundleUpdateError(.invalidArgument, .verifyPackage)
            }

            let filePath = (BundleUpdateStore.downloadBundleDir() as NSString)
                .appendingPathComponent("\(appVersion)-\(bundleVersion).zip")
            let resolvedFilePath = (filePath as NSString).resolvingSymlinksInPath
            let baseDownloadDir = (BundleUpdateStore.downloadBundleDir() as NSString).resolvingSymlinksInPath
            guard resolvedFilePath.hasPrefix(baseDownloadDir + "/") else {
                OneKeyLog.error("BundleUpdate", "verifyBundle: ZIP outside download directory: \(resolvedFilePath)")
                throw BundleUpdateError(.security, .verifyPackage)
            }
            guard FileManager.default.fileExists(atPath: resolvedFilePath) else {
                throw BundleUpdateError(.missing, .verifyPackage)
            }
            OneKeyLog.info("BundleUpdate", "verifyBundle: appVersion=\(appVersion), bundleVersion=\(bundleVersion), file=\(filePath)")

            // Verify SHA256 of the downloaded file
            guard let calculated = BundleUpdateStore.calculateSHA256(filePath) else {
                OneKeyLog.error("BundleUpdate", "verifyBundle: failed to calculate SHA256 for file=\(filePath)")
                throw BundleUpdateError(.storage, .verifyPackage)
            }
            guard BundleCryptoCore.secureEqualHex(calculated, sha256) else {
                OneKeyLog.error("BundleUpdate", "verifyBundle: SHA256 mismatch, expected=\(sha256.prefix(16))..., got=\(calculated.prefix(16))...")
                throw BundleUpdateError(.integrity, .verifyPackage)
            }

            OneKeyLog.info("BundleUpdate", "verifyBundle: SHA256 verified OK for appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
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
            if !BundleUpdateStore.validateEntryBundlesSha256(bundleDirPath: bundleDirPath, metadata: metadata) {
                OneKeyLog.error("BundleUpdate", "installBundle: entry bundle SHA256 verification failed for \(folderName)")
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

    private func clearManagedFiles(includeInstalled: Bool) async throws {
        let (alreadyClearing, oldSession, activeDelegate, activePath) = stateQueue.sync { () -> (Bool, URLSession?, DownloadDelegate?, String?) in
            if clearingDownloads { return (true, nil, nil, nil) }
            clearingDownloads = true
            activeRunDelegate?.markCancelledByClear()
            return (false, urlSession, activeRunDelegate, activeDownloadFilePath)
        }
        guard !alreadyClearing else { throw BundleUpdateError(.invalidArgument, .cleanup) }
        defer {
            stateQueue.sync {
                urlSession = createURLSession()
                clearingDownloads = false
            }
        }
        oldSession?.invalidateAndCancel()
        if let activePath {
            let taskId = URL(fileURLWithPath: activePath).deletingPathExtension().lastPathComponent
            await RangeDownloader.shared.cancel(channel: .bundle, taskId: taskId, filePath: activePath)
        }
        if let activeDelegate, !(await activeDelegate.waitForRunCompletion(timeout: 65)) {
            throw BundleUpdateError(.unknown, .cleanup)
        }
        let downloadDir = BundleUpdateStore.downloadBundleDir()
        if FileManager.default.fileExists(atPath: downloadDir) {
            try FileManager.default.removeItem(atPath: downloadDir)
        }
        if includeInstalled {
            let bundleDir = BundleUpdateStore.bundleDir()
            if FileManager.default.fileExists(atPath: bundleDir) {
                try FileManager.default.removeItem(atPath: bundleDir)
            }
        }
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

            // 2. onekey-bundle-download/* stages (zip / partial / progress / resume)
            let downloadDir = BundleUpdateStore.downloadBundleDir()
            if let contents = try? fm.contentsOfDirectory(atPath: downloadDir) {
                for name in contents {
                    // Strip the trailing extension chain to recover the
                    // "{appV}-{bV}" stem (e.g. "6.3.0-123.zip.partial").
                    var stem = name
                    for suffix in [".resume", ".progress", ".partial", ".zip"] {
                        if stem.hasSuffix(suffix) {
                            stem = String(stem.dropLast(suffix.count))
                        }
                    }
                    if shouldKeep(stem: stem) { continue }
                    let fullPath = (downloadDir as NSString).appendingPathComponent(name)
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
        return updatePromise(step: .cleanup) {
            OneKeyLog.info("BundleUpdate", "clearAllJSBundleData: starting...")
            let bundleDir = BundleUpdateStore.bundleDir()
            if FileManager.default.fileExists(atPath: bundleDir) {
                try FileManager.default.removeItem(atPath: bundleDir)
                OneKeyLog.info("BundleUpdate", "clearAllJSBundleData: deleted bundle dir")
            }
            let ud = UserDefaults.standard
            if let cbv = ud.string(forKey: "currentBundleVersion") {
                ud.removeObject(forKey: cbv)
                ud.removeObject(forKey: "currentBundleVersion")
                OneKeyLog.info("BundleUpdate", "clearAllJSBundleData: removed currentBundleVersion=\(cbv)")
            }
            BundleUpdateStore.clearNativeVersionPrefs()
            ud.synchronize()

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
            if !BundleUpdateStore.validateEntryBundlesSha256(bundleDirPath: bundleDirPath, metadata: metadata) {
                OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: entry bundle SHA256 verification failed")
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

