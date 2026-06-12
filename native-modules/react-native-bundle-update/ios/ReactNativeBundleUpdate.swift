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
            if let cbv = currentBundleVersion(),
               let dashRange = cbv.range(of: "-", options: .backwards) {
                currentBundleVersionStr = String(cbv[dashRange.upperBound...])
            } else {
                currentBundleVersionStr = getBuiltinBundleVersion()
            }
            guard taskDict["scheduledEnvBundleVersion"] as? String == currentBundleVersionStr else { return }

            // 4. Extract payload
            guard let payload = taskDict["payload"] as? [String: Any],
                  let appVersion = payload["appVersion"] as? String,
                  let bundleVersion = payload["bundleVersion"] as? String,
                  let signature = payload["signature"] as? String,
                  !appVersion.isEmpty, !bundleVersion.isEmpty, !signature.isEmpty,
                  appVersion.isSafeVersionString, bundleVersion.isSafeVersionString
            else { return }

            // 5. Verify bundle directory and entry file exist
            let folderName = "\(appVersion)-\(bundleVersion)"
            let bundleDirPath = (bundleDir() as NSString).appendingPathComponent(folderName)
            guard FileManager.default.fileExists(atPath: bundleDirPath) else { return }
            let entryFilePath = (bundleDirPath as NSString).appendingPathComponent("main.jsbundle.hbc")
            guard FileManager.default.fileExists(atPath: entryFilePath) else {
                OneKeyLog.warn("BundleUpdate", "processPreLaunchPendingTask: bundle dir exists but entry file missing: \(entryFilePath)")
                return
            }

            // 6. Apply: set currentBundleVersion (same as installBundle)
            let ud = UserDefaults.standard
            ud.set(folderName, forKey: bundlePrefsKey)
            if !signature.isEmpty {
                writeSignatureFile(folderName, signature: signature)
            }
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
        key == metadataBundleFormatKey
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

    public static func readMetadataFileSha256(_ signature: String) -> String? {
        guard !signature.isEmpty else { return nil }

        // GPG cleartext signature verification is required. The actual PGP
        // verification + JSON-body sha256 extraction lives in the shared
        // BundleCryptoCore (react-native-bundle-crypto); preserve the previous
        // "failure => nil" semantics here.
        let r = BundleCryptoCore.verifyGpgCleartext(signature)
        guard r.valid, let sha256 = r.sha256 else {
            OneKeyLog.error("BundleUpdate", "readMetadataFileSha256: GPG verification failed (\(r.reason ?? "unknown")), rejecting unsigned content")
            return nil
        }
        return sha256
    }

    public static func validateMetadataFileSha256(_ currentBundleVersion: String, signature: String) -> Bool {
        guard let metadataFilePath = getMetadataFilePath(currentBundleVersion) else {
            OneKeyLog.debug("BundleUpdate", "metadataFilePath is null")
            return false
        }
        guard let extractedSha256 = readMetadataFileSha256(signature), !extractedSha256.isEmpty else {
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
        let devSettingsEnabled = isDevSettingsEnabled()
        if devSettingsEnabled {
            OneKeyLog.warn("BundleUpdate", "Startup SHA256 validation skipped (DevSettings enabled)")
        }
        #else
        let devSettingsEnabled = false
        #endif
        if !devSettingsEnabled && !validateMetadataFileSha256(currentBundleVer, signature: signature) {
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
    /// Called with progress percentage (0-100) during download
    var onProgress: ((Int) -> Void)?

    /// Continuation to bridge delegate callbacks → async/await
    private var continuation: CheckedContinuation<(URL, URLResponse), Error>?
    private var tempFileURL: URL?
    /// Resume data captured from the most recent failure so the caller can
    /// persist it for the next attempt. Populated in didCompleteWithError
    /// when the system supplies NSURLSessionDownloadTaskResumeData.
    private(set) var lastResumeData: Data?
    private let lock = NSLock()

    func setContinuation(_ cont: CheckedContinuation<(URL, URLResponse), Error>) {
        lock.lock()
        continuation = cont
        lock.unlock()
    }

    // MARK: - URLSessionDownloadDelegate

    private var prevProgress: Int = -1

    func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask, didWriteData bytesWritten: Int64, totalBytesWritten: Int64, totalBytesExpectedToWrite: Int64) {
        guard totalBytesExpectedToWrite > 0 else { return }
        let progress = Int((totalBytesWritten * 100) / totalBytesExpectedToWrite)
        if progress != prevProgress {
            OneKeyLog.info("BundleUpdate", "download progress: \(progress)% (\(totalBytesWritten)/\(totalBytesExpectedToWrite))")
            prevProgress = progress
            onProgress?(progress)
        }
    }

    func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask, didFinishDownloadingTo location: URL) {
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
            cont?.resume(throwing: NSError(domain: "BundleUpdate", code: -1,
                userInfo: [NSLocalizedDescriptionKey: "Download completed without file"]))
        }
    }

    func urlSession(_ session: URLSession, task: URLSessionTask, willPerformHTTPRedirection response: HTTPURLResponse, newRequest request: URLRequest, completionHandler: @escaping (URLRequest?) -> Void) {
        if request.url?.scheme?.lowercased() != "https" {
            OneKeyLog.error("BundleUpdate", "Blocked redirect to non-HTTPS URL")
            completionHandler(nil)
        } else {
            completionHandler(request)
        }
    }

    /// Reset state for reuse
    func reset() {
        lock.lock()
        continuation = nil
        lock.unlock()
        tempFileURL = nil
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

class ReactNativeBundleUpdate: HybridReactNativeBundleUpdateSpec {

    // Serial queue protects mutable state (listeners, nextListenerId, isDownloading)
    private let stateQueue = DispatchQueue(label: "so.onekey.bundleupdate.state")
    private var listeners: [BundleListener] = []
    private var nextListenerId: Double = 1
    private var isDownloading = false
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
        guard let session = self.urlSession else { return }
        // Read both isDownloading AND activeDownloadFilePath inside the
        // same stateQueue.sync block. A concurrent downloadBundle entry
        // flips isDownloading=true at the start of the Promise body but
        // only writes activeDownloadFilePath later (after the early
        // guards / version + URL validation). Without this paired read,
        // didEnterBackground could observe isDownloading=true while
        // activeDownloadFilePath is still nil from a previous run, or —
        // on the unwind via defer — see isDownloading=false alongside a
        // not-yet-cleared path.
        let snapshot: (Bool, String?) = self.stateQueue.sync {
            (self.isDownloading, self.activeDownloadFilePath)
        }
        let (stillDownloading, snapshotPath) = snapshot
        guard stillDownloading, let filePath = snapshotPath else { return }

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

        session.getAllTasks { tasks in
            let group = DispatchGroup()
            for task in tasks {
                guard let dl = task as? URLSessionDownloadTask, dl.state == .running else { continue }
                group.enter()
                dl.cancel(byProducingResumeData: { data in
                    if let data = data, data.count > 0 {
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
        return Promise.async { [weak self] in
            guard let self = self else { throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Module deallocated"]) }

            let alreadyDownloading = self.stateQueue.sync { () -> Bool in
                if self.isDownloading { return true }
                self.isDownloading = true
                return false
            }
            guard !alreadyDownloading else {
                OneKeyLog.warn("BundleUpdate", "downloadBundle: rejected, already downloading")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Already downloading"])
            }
            defer {
                // Clear isDownloading + the snapshot anchor under the same
                // queue so didEnterBackground's paired read can't observe
                // a half-cleared state (isDownloading=true while
                // activeDownloadFilePath=nil, or vice versa).
                self.stateQueue.sync {
                    self.isDownloading = false
                    self.activeDownloadFilePath = nil
                }
            }

            let appVersion = params.latestVersion
            let bundleVersion = params.bundleVersion
            let downloadUrl = params.downloadUrl
            let sha256 = params.sha256

            OneKeyLog.info("BundleUpdate", "downloadBundle: appVersion=\(appVersion), bundleVersion=\(bundleVersion), fileSize=\(params.fileSize), url=\(downloadUrl)")

            guard appVersion.isSafeVersionString, bundleVersion.isSafeVersionString else {
                OneKeyLog.error("BundleUpdate", "downloadBundle: invalid version string format: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid version string format"])
            }

            guard downloadUrl.hasPrefix("https://") else {
                OneKeyLog.error("BundleUpdate", "downloadBundle: URL is not HTTPS: \(downloadUrl)")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle download URL must use HTTPS"])
            }

            let fileName = "\(appVersion)-\(bundleVersion).zip"
            let filePath = (BundleUpdateStore.downloadBundleDir() as NSString).appendingPathComponent(fileName)
            // Persisted resume blob from a previous failed attempt. Lives next
            // to the bundle so it shares fate (delete-with-bundle) without
            // polluting the bundle dir's cache lookup.
            let resumeDataPath = "\(filePath).resume"

            let result = BundleDownloadResult(
                downloadedFile: filePath,
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
                        self?.sendEvent(type: "update/complete")
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
            self.stateQueue.sync { self.activeDownloadFilePath = filePath }

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
                guard event.channel.stringValue == DownloadChannel.bundle.stringValue,
                      event.taskId == rangeTaskId,
                      event.type == "progress" else { return }
                self?.sendEvent(type: "update/downloading", progress: Int(event.progress))
            }

            let (rangeOutcome, _, rangeReason) = await RangeDownloader.shared.download(
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
            RangeDownloader.shared.removeListener(progressListenerId)

            if rangeOutcome.stringValue == RangeDownloadOutcome.fallback.stringValue {
                // Concurrent path unavailable (Range unsupported / file too small /
                // server returned 200 / transient error). Hand off to the
                // single-stream path, leaving a clean slot.
                OneKeyLog.info("BundleUpdate", "downloadBundle: concurrent fallback (\(rangeReason ?? "")), using single-stream")
                RangeDownloader.shared.discardArtifacts(filePath: filePath)
                // fall through to the single-stream path below
            } else {
                OneKeyLog.info("BundleUpdate", "downloadBundle: concurrent finished, verifying SHA256...")
                if !self.verifyBundleSHA256(filePath, sha256: sha256) {
                    let reason = BundleUpdateStore.lastSHA256FailureReason() ?? "MISMATCH"
                    try? FileManager.default.removeItem(atPath: filePath)
                    self.sendEvent(type: "update/error", message: "SHA256_\(reason)")
                    throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle SHA256 verification failed: \(reason)"])
                }
                try? FileManager.default.removeItem(atPath: resumeDataPath)
                self.sendEvent(type: "update/complete")
                OneKeyLog.info("BundleUpdate", "downloadBundle: concurrent completed successfully, appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
                return result
            }

            // Download the file
            guard let url = URL(string: downloadUrl) else {
                OneKeyLog.error("BundleUpdate", "downloadBundle: invalid URL: \(downloadUrl)")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid URL"])
            }

            guard let session = self.urlSession else {
                OneKeyLog.error("BundleUpdate", "downloadBundle: URLSession not initialized")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "URLSession not initialized"])
            }

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
            guard let delegate = self.downloadDelegate else {
                OneKeyLog.error("BundleUpdate", "downloadBundle: download delegate not initialized")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Download delegate not initialized"])
            }
            delegate.reset()
            delegate.onProgress = { [weak self] progress in
                self?.sendEvent(type: "update/downloading", progress: progress)
            }

            // Anchor for the background-snapshot handler. Set BEFORE
            // task.resume() so a foreground→background transition that
            // races the very first bytes still finds a path to write the
            // resume blob to. Pair the write with the same stateQueue the
            // snapshot reader uses, so isDownloading=true and a non-nil
            // activeDownloadFilePath always go together.
            self.stateQueue.sync { self.activeDownloadFilePath = filePath }

            let downloadOutcome: Result<(URL, URLResponse), Error>
            do {
                let value = try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<(URL, URLResponse), Error>) in
                    delegate.setContinuation(continuation)
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
                self.sendEvent(type: "update/error", message: "\(nsError.domain) \(nsError.code)")
                // Rethrow a sanitized NSError. The original `error` is a
                // URLSession/system error whose `localizedDescription` may
                // include the temp file path (e.g. NSURLErrorFailingURLString
                // userInfo, or the ~/Library/.../CFNetworkDownload_*.tmp
                // path on cancel-with-resume) — Promise rejections surface
                // that string to JS, which would re-leak the paths the
                // event-payload sanitization just stripped. Keep the full
                // detail in OneKeyLog (above) and emit only `domain code`.
                throw NSError(
                    domain: nsError.domain,
                    code: nsError.code,
                    userInfo: [NSLocalizedDescriptionKey: "Download failed: \(nsError.domain) \(nsError.code)"]
                )

            case .success(let (tempURL, response)):
                // Successful completion ⇒ no longer need the resume blob.
                try? FileManager.default.removeItem(atPath: resumeDataPath)

                // Verify HTTPS was maintained (no HTTP redirect)
                if let httpResponse = response as? HTTPURLResponse,
                   let responseUrl = httpResponse.url,
                   responseUrl.scheme?.lowercased() != "https" {
                    OneKeyLog.error("BundleUpdate", "downloadBundle: redirected to non-HTTPS URL: \(responseUrl)")
                    throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Download was redirected to non-HTTPS URL"])
                }

                // 206 is acceptable when the OS finished a Range-resumed task on
                // our behalf; everything else non-200 is a real error.
                guard let httpResponse = response as? HTTPURLResponse,
                      httpResponse.statusCode == 200 || httpResponse.statusCode == 206 else {
                    let statusCode = (response as? HTTPURLResponse)?.statusCode ?? -1
                    OneKeyLog.error("BundleUpdate", "downloadBundle: HTTP error, statusCode=\(statusCode)")
                    self.sendEvent(type: "update/error", message: "HTTP error \(statusCode)")
                    throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Download failed with HTTP \(statusCode)"])
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
                if !self.verifyBundleSHA256(filePath, sha256: sha256) {
                    let reason = BundleUpdateStore.lastSHA256FailureReason() ?? "MISMATCH"
                    try? FileManager.default.removeItem(atPath: filePath)
                    OneKeyLog.error("BundleUpdate", "downloadBundle: SHA256 verification failed after download, reason=\(reason)")
                    self.sendEvent(type: "update/error", message: "SHA256_\(reason)")
                    throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle SHA256 verification failed: \(reason)"])
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
        return Promise.async {
            let appVersion = params.latestVersion
            let bundleVersion = params.bundleVersion
            let signature = params.signature

            OneKeyLog.info("BundleUpdate", "downloadBundleASC: appVersion=\(appVersion), bundleVersion=\(bundleVersion), signatureLength=\(signature.count)")

            let storageKey = "\(appVersion)-\(bundleVersion)"
            BundleUpdateStore.writeSignatureFile(storageKey, signature: signature)

            OneKeyLog.info("BundleUpdate", "downloadBundleASC: stored signature for key=\(storageKey)")
        }
    }

    func verifyBundleASC(params: BundleVerifyASCParams) throws -> Promise<Void> {
        return Promise.async {
            let filePath = params.downloadedFile
            let sha256 = params.sha256
            let appVersion = params.latestVersion
            let bundleVersion = params.bundleVersion
            let signature = params.signature

            OneKeyLog.info("BundleUpdate", "verifyBundleASC: appVersion=\(appVersion), bundleVersion=\(bundleVersion), file=\(filePath), signatureLength=\(signature.count)")

            // GPG verification skipped only when both DevSettings and skip-GPG toggle are enabled
            #if ALLOW_SKIP_GPG_VERIFICATION
            let skipGPG = BundleUpdateStore.isDevSettingsEnabled() && BundleUpdateStore.isSkipGPGEnabled()
            #else
            let skipGPG = false
            #endif
            OneKeyLog.info("BundleUpdate", "verifyBundleASC: GPG check: skipGPG=\(skipGPG)")

            if !skipGPG {
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: verifying SHA256 of downloaded file...")
                let calculated = BundleUpdateStore.calculateSHA256(filePath)
                let isValid = calculated != nil && BundleCryptoCore.secureEqualHex(calculated!, sha256)
                if !isValid {
                    // Promote the SHA256 subtype (FILE_TRUNCATED / OOM /
                    // IO_<class> / MISMATCH) into the thrown message so
                    // analytics' extractUpdateErrorCode can split this
                    // bucket the same way the download stage already does.
                    let reason = BundleUpdateStore.lastSHA256FailureReason() ?? "MISMATCH"
                    OneKeyLog.error("BundleUpdate", "verifyBundleASC: SHA256 verification failed for file=\(filePath), reason=\(reason)")
                    throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle SHA256 verification failed: \(reason)"])
                }
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: SHA256 verified OK")
            } else {
                OneKeyLog.warn("BundleUpdate", "verifyBundleASC: SHA256 + GPG verification skipped (DevSettings enabled)")
            }

            let folderName = "\(appVersion)-\(bundleVersion)"
            let destination = (BundleUpdateStore.bundleDir() as NSString).appendingPathComponent(folderName)

            // Check zip file size before extraction (decompression bomb protection)
            let maxZipFileSize: UInt64 = 512 * 1024 * 1024 // 512 MB
            if let attrs = try? FileManager.default.attributesOfItem(atPath: filePath),
               let fileSize = attrs[.size] as? UInt64 {
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: zip file size=\(fileSize) bytes")
                if fileSize > maxZipFileSize {
                    OneKeyLog.error("BundleUpdate", "verifyBundleASC: zip file too large (\(fileSize) > \(maxZipFileSize))")
                    throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Zip file exceeds maximum allowed size"])
                }
            }

            // Unzip using SSZipArchive
            OneKeyLog.info("BundleUpdate", "verifyBundleASC: extracting zip to \(destination)...")
            do {
                try SSZipArchive.unzipFile(atPath: filePath, toDestination: destination, overwrite: true, password: nil)
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: extraction completed")
            } catch {
                // SSZipArchive's NSError.localizedDescription often embeds the
                // failing file path which on iOS includes the install UUID
                // (/var/mobile/Containers/Data/Application/<UUID>/...). Keep
                // the rich detail in OneKeyLog (local-only), but expose only
                // a code-shaped tag in the thrown message so the JS-side
                // analytics layer can never reflect that path back to the
                // server. Subtypes intentionally low-cardinality so
                // extractUpdateErrorCode picks them up cleanly:
                //   IO_<NSError code>
                let nsErr = error as NSError
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: unzip failed: domain=\(nsErr.domain) code=\(nsErr.code) desc=\(nsErr.localizedDescription)")
                try? FileManager.default.removeItem(atPath: destination)
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to unzip bundle: IO_\(nsErr.code)"])
            }

            // Validate extracted paths (symlinks, path traversal)
            OneKeyLog.info("BundleUpdate", "verifyBundleASC: validating extracted path safety...")
            if !BundleCryptoCore.validateExtractedPathSafety(destination) {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: path traversal or symlink attack detected")
                try? FileManager.default.removeItem(atPath: destination)
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Path traversal or symlink attack detected"])
            }

            let metadataJsonPath = (destination as NSString).appendingPathComponent("metadata.json")
            guard FileManager.default.fileExists(atPath: metadataJsonPath) else {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: metadata.json not found after extraction")
                try? FileManager.default.removeItem(atPath: destination)
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to read metadata.json"])
            }

            let currentBundleVersion = "\(appVersion)-\(bundleVersion)"
            if !skipGPG {
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: validating GPG signature for metadata...")
                if !BundleUpdateStore.validateMetadataFileSha256(currentBundleVersion, signature: signature) {
                    OneKeyLog.error("BundleUpdate", "verifyBundleASC: GPG signature verification failed")
                    try? FileManager.default.removeItem(atPath: destination)
                    throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle signature verification failed"])
                }
                OneKeyLog.info("BundleUpdate", "verifyBundleASC: GPG signature verified OK")
            } else {
                OneKeyLog.warn("BundleUpdate", "verifyBundleASC: GPG verification skipped (DevSettings enabled)")
            }

            OneKeyLog.info("BundleUpdate", "verifyBundleASC: validating all extracted files against metadata...")
            guard let metadata = BundleUpdateStore.getMetadataFileContent(currentBundleVersion) else {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: failed to read metadata.json content")
                try? FileManager.default.removeItem(atPath: destination)
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to read metadata.json after extraction"])
            }

            if !BundleUpdateStore.validateAllFilesInDir(destination, metadata: metadata, appVersion: appVersion, bundleVersion: bundleVersion) {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: file integrity check failed")
                try? FileManager.default.removeItem(atPath: destination)
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Extracted files verification against metadata failed"])
            }
            if !BundleUpdateStore.validateBundlePairCompatibility(bundleDirPath: destination, metadata: metadata) {
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: bundle pair compatibility check failed")
                try? FileManager.default.removeItem(atPath: destination)
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle pair compatibility check failed"])
            }

            OneKeyLog.info("BundleUpdate", "verifyBundleASC: all verifications passed, appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
        }
    }

    func verifyBundle(params: BundleVerifyParams) throws -> Promise<Void> {
        return Promise.async {
            let filePath = params.downloadedFile
            let sha256 = params.sha256
            let appVersion = params.latestVersion
            let bundleVersion = params.bundleVersion

            OneKeyLog.info("BundleUpdate", "verifyBundle: appVersion=\(appVersion), bundleVersion=\(bundleVersion), file=\(filePath)")

            // Verify SHA256 of the downloaded file
            guard let calculated = BundleUpdateStore.calculateSHA256(filePath) else {
                OneKeyLog.error("BundleUpdate", "verifyBundle: failed to calculate SHA256 for file=\(filePath)")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to calculate SHA256"])
            }
            guard BundleCryptoCore.secureEqualHex(calculated, sha256) else {
                OneKeyLog.error("BundleUpdate", "verifyBundle: SHA256 mismatch, expected=\(sha256.prefix(16))..., got=\(calculated.prefix(16))...")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "SHA256 verification failed"])
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
        return Promise.async {
            let appVersion = params.latestVersion
            let bundleVersion = params.bundleVersion
            let signature = params.signature

            OneKeyLog.info("BundleUpdate", "installBundle: appVersion=\(appVersion), bundleVersion=\(bundleVersion), signatureLength=\(signature.count)")

            // GPG verification skipped only when both DevSettings and skip-GPG toggle are enabled
            #if ALLOW_SKIP_GPG_VERIFICATION
            let skipGPG = BundleUpdateStore.isDevSettingsEnabled() && BundleUpdateStore.isSkipGPGEnabled()
            #else
            let skipGPG = false
            #endif
            OneKeyLog.info("BundleUpdate", "installBundle: GPG check: skipGPG=\(skipGPG)")

            let folderName = "\(appVersion)-\(bundleVersion)"
            let currentFolderName = BundleUpdateStore.currentBundleVersion()
            OneKeyLog.info("BundleUpdate", "installBundle: target=\(folderName), current=\(currentFolderName ?? "nil")")

            // Verify bundle directory exists
            let bundleDirPath = (BundleUpdateStore.bundleDir() as NSString).appendingPathComponent(folderName)
            guard FileManager.default.fileExists(atPath: bundleDirPath) else {
                OneKeyLog.error("BundleUpdate", "installBundle: bundle directory not found: \(bundleDirPath)")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle directory not found: \(folderName)"])
            }

            let ud = UserDefaults.standard
            ud.set(folderName, forKey: "currentBundleVersion")
            if !signature.isEmpty {
                BundleUpdateStore.writeSignatureFile(folderName, signature: signature)
            }
            let currentNativeVersion = BundleUpdateStore.getCurrentNativeVersion()
            BundleUpdateStore.setNativeVersion(currentNativeVersion)
            let currentBuildNumber = BundleUpdateStore.getCurrentNativeBuildNumber()
            BundleUpdateStore.setNativeBuildNumber(currentBuildNumber)
            ud.synchronize()

            // Manage fallback data
            var fallbackData = BundleUpdateStore.readFallbackUpdateBundleDataFile()

            if let current = currentFolderName,
               let dashRange = current.range(of: "-", options: .backwards) {
                let curAppVersion = String(current[current.startIndex..<dashRange.lowerBound])
                let curBundleVersion = String(current[dashRange.upperBound...])
                let curSignature = BundleUpdateStore.readSignatureFile(current)
                if !curSignature.isEmpty {
                    fallbackData.append([
                        "appVersion": curAppVersion,
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
                    BundleUpdateStore.deleteSignatureFile(dirName)
                    let oldPath = (BundleUpdateStore.bundleDir() as NSString).appendingPathComponent(dirName)
                    if FileManager.default.fileExists(atPath: oldPath) {
                        try? FileManager.default.removeItem(atPath: oldPath)
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

    func clearDownload() throws -> Promise<Void> {
        return Promise.async { [weak self] in
            OneKeyLog.info("BundleUpdate", "clearDownload: clearing download directory and cancelling downloads...")
            let downloadDir = BundleUpdateStore.downloadBundleDir()
            if FileManager.default.fileExists(atPath: downloadDir) {
                try FileManager.default.removeItem(atPath: downloadDir)
                OneKeyLog.info("BundleUpdate", "clearDownload: download directory deleted")
            } else {
                OneKeyLog.info("BundleUpdate", "clearDownload: download directory does not exist, skipping")
            }
            // Cancel all in-flight downloads by invalidating the session
            self?.urlSession?.invalidateAndCancel()
            self?.urlSession = self?.createURLSession()
            self?.stateQueue.sync { self?.isDownloading = false }
            OneKeyLog.info("BundleUpdate", "clearDownload: completed")
        }
    }

    func clearBundle() throws -> Promise<Void> {
        BundleUpdateStore.invalidateValidatedBundleInfoCache()
        return Promise.async { [weak self] in
            OneKeyLog.info("BundleUpdate", "clearBundle: clearing download and bundle directories...")
            // Clear download directory
            let downloadDir = BundleUpdateStore.downloadBundleDir()
            if FileManager.default.fileExists(atPath: downloadDir) {
                try FileManager.default.removeItem(atPath: downloadDir)
                OneKeyLog.info("BundleUpdate", "clearBundle: download directory deleted")
            }
            // Clear installed bundle directory
            let bundleDir = BundleUpdateStore.bundleDir()
            if FileManager.default.fileExists(atPath: bundleDir) {
                try FileManager.default.removeItem(atPath: bundleDir)
                OneKeyLog.info("BundleUpdate", "clearBundle: bundle directory deleted")
            }
            // Cancel all in-flight downloads by invalidating the session
            self?.urlSession?.invalidateAndCancel()
            self?.urlSession = self?.createURLSession()
            self?.stateQueue.sync { self?.isDownloading = false }
            OneKeyLog.info("BundleUpdate", "clearBundle: completed")
        }
    }

    /// Parses an "{appVersion}-{bundleVersion}" folder/file stem into its
    /// appVersion component using the SAME last-dash split as listLocalBundles
    /// and the installBundle fallback logic. Returns nil when the stem has no
    /// dash or an empty appVersion, so callers leave unrecognized entries
    /// untouched.
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
        return Promise.async {
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
        return Promise.async {
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
        return Promise.async {
            let bundleVersion = "\(params.appVersion)-\(params.bundleVersion)"
            OneKeyLog.info("BundleUpdate", "setCurrentUpdateBundleData: switching to \(bundleVersion)")

            // Verify the bundle directory actually exists
            let bundleDirPath = (BundleUpdateStore.bundleDir() as NSString).appendingPathComponent(bundleVersion)
            guard FileManager.default.fileExists(atPath: bundleDirPath) else {
                OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: bundle directory not found: \(bundleDirPath)")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle directory not found"])
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
                      BundleUpdateStore.validateMetadataFileSha256(bundleVersion, signature: params.signature) else {
                    OneKeyLog.error("BundleUpdate", "setCurrentUpdateBundleData: GPG signature verification failed")
                    throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle signature verification failed"])
                }
                OneKeyLog.info("BundleUpdate", "setCurrentUpdateBundleData: GPG signature verified OK")
            } else {
                OneKeyLog.warn("BundleUpdate", "setCurrentUpdateBundleData: GPG signature verification skipped (DevSettings + skip-GPG enabled)")
            }

            let ud = UserDefaults.standard
            ud.set(bundleVersion, forKey: "currentBundleVersion")
            if !params.signature.isEmpty {
                BundleUpdateStore.writeSignatureFile(bundleVersion, signature: params.signature)
            }
            ud.synchronize()
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
            let folderName = "\(appVersion)-\(bundleVersion)"
            let path = (BundleUpdateStore.bundleDir() as NSString).appendingPathComponent(folderName)
            let exists = FileManager.default.fileExists(atPath: path)
            OneKeyLog.info("BundleUpdate", "isBundleExists: appVersion=\(appVersion), bundleVersion=\(bundleVersion), exists=\(exists)")
            return exists
        }
    }

    func verifyExtractedBundle(appVersion: String, bundleVersion: String) throws -> Promise<Void> {
        return Promise.async {
            OneKeyLog.info("BundleUpdate", "verifyExtractedBundle: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
            let folderName = "\(appVersion)-\(bundleVersion)"
            let bundlePath = (BundleUpdateStore.bundleDir() as NSString).appendingPathComponent(folderName)
            guard FileManager.default.fileExists(atPath: bundlePath) else {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: bundle directory not found: \(bundlePath)")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle directory not found"])
            }
            let metadataJsonPath = (bundlePath as NSString).appendingPathComponent("metadata.json")
            guard FileManager.default.fileExists(atPath: metadataJsonPath) else {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: metadata.json not found in \(bundlePath)")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "metadata.json not found"])
            }
            guard let data = FileManager.default.contents(atPath: metadataJsonPath),
                  let metadata = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: failed to parse metadata.json")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to parse metadata.json"])
            }
            OneKeyLog.info("BundleUpdate", "verifyExtractedBundle: parsing metadata and validating files...")
            if !BundleUpdateStore.validateAllFilesInDir(bundlePath, metadata: metadata, appVersion: appVersion, bundleVersion: bundleVersion) {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: file integrity check failed")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "File integrity check failed"])
            }
            if !BundleUpdateStore.validateBundlePairCompatibility(bundleDirPath: bundlePath, metadata: metadata) {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: bundle pair compatibility check failed")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle pair compatibility check failed"])
            }
            OneKeyLog.info("BundleUpdate", "verifyExtractedBundle: all files verified OK, fileCount=\(metadata.count)")
        }
    }

    func listLocalBundles() throws -> Promise<[LocalBundleInfo]> {
        return Promise.async {
            let bundleDir = BundleUpdateStore.bundleDir()
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
                guard let lastDash = name.range(of: "-", options: .backwards),
                      lastDash.lowerBound > name.startIndex else { continue }
                let appVersion = String(name[name.startIndex..<lastDash.lowerBound])
                let bundleVersion = String(name[lastDash.upperBound...])
                if !appVersion.isEmpty && !bundleVersion.isEmpty {
                    results.append(LocalBundleInfo(appVersion: appVersion, bundleVersion: bundleVersion))
                }
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
            OneKeyLog.info("BundleUpdate", "testDeleteJsBundle: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
            let folderName = "\(appVersion)-\(bundleVersion)"
            let jsBundlePath = (BundleUpdateStore.bundleDir() as NSString)
                .appendingPathComponent(folderName)
            let path = (jsBundlePath as NSString).appendingPathComponent("main.jsbundle.hbc")

            if FileManager.default.fileExists(atPath: path) {
                try FileManager.default.removeItem(atPath: path)
                OneKeyLog.info("BundleUpdate", "testDeleteJsBundle: deleted \(path)")
                return TestResult(success: true, message: "Deleted jsBundle: \(path)")
            }
            OneKeyLog.warn("BundleUpdate", "testDeleteJsBundle: file not found: \(path)")
            return TestResult(success: false, message: "jsBundle not found: \(path)")
        }
    }

    func testDeleteJsRuntimeDir(appVersion: String, bundleVersion: String) throws -> Promise<TestResult> {
        return Promise.async {
            OneKeyLog.info("BundleUpdate", "testDeleteJsRuntimeDir: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
            let folderName = "\(appVersion)-\(bundleVersion)"
            let dirPath = (BundleUpdateStore.bundleDir() as NSString).appendingPathComponent(folderName)

            if FileManager.default.fileExists(atPath: dirPath) {
                try FileManager.default.removeItem(atPath: dirPath)
                OneKeyLog.info("BundleUpdate", "testDeleteJsRuntimeDir: deleted \(dirPath)")
                return TestResult(success: true, message: "Deleted js runtime directory: \(dirPath)")
            }
            OneKeyLog.warn("BundleUpdate", "testDeleteJsRuntimeDir: directory not found: \(dirPath)")
            return TestResult(success: false, message: "js runtime directory not found: \(dirPath)")
        }
    }

    func testDeleteMetadataJson(appVersion: String, bundleVersion: String) throws -> Promise<TestResult> {
        return Promise.async {
            OneKeyLog.info("BundleUpdate", "testDeleteMetadataJson: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
            let folderName = "\(appVersion)-\(bundleVersion)"
            let metadataPath = (BundleUpdateStore.bundleDir() as NSString)
                .appendingPathComponent(folderName)
            let path = (metadataPath as NSString).appendingPathComponent("metadata.json")

            if FileManager.default.fileExists(atPath: path) {
                try FileManager.default.removeItem(atPath: path)
                OneKeyLog.info("BundleUpdate", "testDeleteMetadataJson: deleted \(path)")
                return TestResult(success: true, message: "Deleted metadata.json: \(path)")
            }
            OneKeyLog.warn("BundleUpdate", "testDeleteMetadataJson: file not found: \(path)")
            return TestResult(success: false, message: "metadata.json not found: \(path)")
        }
    }

    func testWriteEmptyMetadataJson(appVersion: String, bundleVersion: String) throws -> Promise<TestResult> {
        return Promise.async {
            OneKeyLog.info("BundleUpdate", "testWriteEmptyMetadataJson: appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
            let folderName = "\(appVersion)-\(bundleVersion)"
            let jsRuntimeDir = (BundleUpdateStore.bundleDir() as NSString).appendingPathComponent(folderName)
            let metadataPath = (jsRuntimeDir as NSString).appendingPathComponent("metadata.json")

            if !FileManager.default.fileExists(atPath: jsRuntimeDir) {
                try FileManager.default.createDirectory(atPath: jsRuntimeDir, withIntermediateDirectories: true)
            }

            let emptyJson: [String: Any] = [:]
            let data = try JSONSerialization.data(withJSONObject: emptyJson, options: .prettyPrinted)
            try data.write(to: URL(fileURLWithPath: metadataPath), options: .atomic)

            OneKeyLog.info("BundleUpdate", "testWriteEmptyMetadataJson: created \(metadataPath)")
            return TestResult(success: true, message: "Created empty metadata.json: \(metadataPath)")
        }
    }
}
