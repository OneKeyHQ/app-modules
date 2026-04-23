import NitroModules
import ReactNativeNativeLogger
import Foundation
import CommonCrypto
import Gopenpgp
import SSZipArchive
import MMKV

// OneKey GPG public key for signature verification
private let GPG_PUBLIC_KEY = """
-----BEGIN PGP PUBLIC KEY BLOCK-----

mQINBGJATGwBEADL1K7b8dzYYzlSsvAGiA8mz042pygB7AAh/uFUycpNQdSzuoDE
VoXq/QsXCOsGkMdFLwlUjarRaxFX6RTV6S51LOlJFRsyGwXiMz08GSNagSafQ0YL
Gi+aoemPh6Ta5jWgYGIUWXavkjJciJYw43ACMdVmIWos94bA41Xm93dq9C3VRpl+
EjvGAKRUMxJbH8r13TPzPmfN4vdrHLq+us7eKGJpwV/VtD9vVHAi0n48wGRq7DQw
IUDU2mKy3wmjwS38vIIu4yQyeUdl4EqwkCmGzWc7Cv2HlOG6rLcUdTAOMNBBX1IQ
iHKg9Bhh96MXYvBhEL7XHJ96S3+gTHw/LtrccBM+eiDJVHPZn+lw2HqX994DueLV
tAFDS+qf3ieX901IC97PTHsX6ztn9YZQtSGBJO3lEMBdC4ez2B7zUv4bgyfU+KvE
zHFIK9HmDehx3LoDAYc66nhZXyasiu6qGPzuxXu8/4qTY8MnhXJRBkbWz5P84fx1
/Db5WETLE72on11XLreFWmlJnEWN4UOARrNn1Zxbwl+uxlSJyM+2GTl4yoccG+WR
uOUCmRXTgduHxejPGI1PfsNmFpVefAWBDO7SdnwZb1oUP3AFmhH5CD1GnmLnET+l
/c+7XfFLwgSUVSADBdO3GVS4Cr9ux4nIrHGJCrrroFfM2yvG8AtUVr16PQARAQAB
tCJvbmVrZXlocSBkZXZlbG9wZXIgPGRldkBvbmVrZXkuc28+iQJUBBMBCAA+FiEE
62iuVE8f3YzSZGJPs2mmepC/OHsFAmJATGwCGwMFCQeGH0QFCwkIBwIGFQoJCAsC
BBYCAwECHgECF4AACgkQs2mmepC/OHtgvg//bsWFMln08ZJjf5od/buJua7XYb3L
jWq1H5rdjJva5TP1UuQaDULuCuPqllxb+h+RB7g52yRG/1nCIrpTfveYOVtq/mYE
D12KYAycDwanbmtoUp25gcKqCrlNeSE1EXmPlBzyiNzxJutE1DGlvbY3rbuNZLQi
UTFBG3hk6JgsaXkFCwSmF95uATAaItv8aw6eY7RWv47rXhQch6PBMCir4+a/v7vs
lXxQtcpCqfLtjrloq7wvmD423yJVsUGNEa7/BrwFz6/GP6HrUZc6JgvrieuiBE4n
ttXQFm3dkOfD+67MLMO3dd7nPhxtjVEGi+43UH3/cdtmU4JFX3pyCQpKIlXTEGp2
wqim561auKsRb1B64qroCwT7aACwH0ZTgQS8rPifG3QM8ta9QheuOsjHLlqjo8jI
fpqe0vKYUlT092joT0o6nT2MzmLmHUW0kDqD9p6JEJEZUZpqcSRE84eMTFNyu966
xy/rjN2SMJTFzkNXPkwXYrMYoahGez1oZfLzV6SQ0+blNc3aATt9aQW6uaCZtMw1
ibcfWW9neHVpRtTlMYCoa2reGaBGCv0Nd8pMcyFUQkVaes5cQHkh3r5Dba+YrVvp
l4P8HMbN8/LqAv7eBfj3ylPa/8eEPWVifcum2Y9TqherN1C2JDqWIpH4EsApek3k
NMK6q0lPxXjZ3PaJAlQEEwEIAD4CGwMFCwkIBwIGFQoJCAsCBBYCAwECHgECF4AW
IQTraK5UTx/djNJkYk+zaaZ6kL84ewUCactdeAUJDxpqwwAKCRCzaaZ6kL84e8TX
EACtuZUT79PZx964iUf6T04IZ/SFqftMdIPrvCOpyYUkzFfTjufZSP7S5dmut/dl
VLQnPjip0ZGeHeSX2ersXmmp7Ny2zqZr858ZIdLpamkEg6hRi5LWOOK4clnKzTLe
OGWlA6WzF3cb4YB4NiNOX1yxxtggZrndyMxLfSU27aZ4h98/g5j/o/FRCt0OzibH
IGKl+tUayKEEtq7+CrxWHwCXY+wFeeJFm2yhEMqeAZlVpsvGgtfWevQwHaRcld99
5ousZOOqsCkl1J7rCeaIFowIEA3TzH0FWIQGahGiHN/+zwc7iSIL9gNEq4/AYJWK
80jPqyrRDia7VfZA/SULbWaPmmqrn/Y8qYl3jDvT/6BuwXFAgK9pz5NkWggkjAMX
nGylez9tZBfv+Bymv5RTRAHey49noF/6ZcF5fidtXAS2tfhuRIlOUfEY+QyB3lXj
kxeOOAGJ2ejTVBVIJnfoSFSsG+LH1tvzbDJvNQcMh0oQD849fip+6O0Ae3KfNZpw
aNkIdxThvBU0XCPgmyEXll/mkS5QlUQUo+EwbZOjr6xGmi310DgJo3Ry1dfZ8qBq
F3DD6NK40bkfw8I6Qjwf/IXd921ZbKe88UMjVBTpm2IH3WXR51My9LN/2gzV9zL+
7odaaXfd+u2x9RuZ1caLXSv4Qyc/7Le1d2T4LpevA7GwMrkCDQRiQExsARAAzVHg
3dsGTAqQd5jCxABJ69SQfBjh6Do1yCl/01uYkdwSKipdMi/SccJBuizc/Y2Fe8Oj
CPgkWQr9luk/3KjSntMjh9ySx5VJbAi2IX2X/w6Ze9hky3DeEdxRRlV0meTTGupP
qeLqHJEUh9uqi6zr++mqLQYbucH/6VQTlK0Y3zr3plZHIBf0ybChGih2zdKE0k/T
4YJgd8hwbRdGEQMwmmH7uZY+WRBRzNrhoSPE5DhK3DCn5kvWtdKXIkg+TVL38UhL
9TDkaCoUlch/mf5IJW1RnyUZ50RbB7jBeyg8XHE5zYarDmvhOskV2ADcym1h5teZ
vYsYyyxdBMzUBBLYt2mdbDjj5fUIe9DSbikTD+DY6B6gk8G6tVSe7aZT8z4BFmJL
hx4BHSktk3tirjynXCvoQ4FB0DdSxvK5zXsw5Eb8iNGaPPhIr+W5AteM37SPBBKg
zWRwgehGTfsHx94eNW58kMqWq3DzcfW427qUbBvwzEOBO64eWgOKMINCyfqbtkpT
WqosMa128JRjai/O45RL2+/owCFHzomSqhTew4Ex5CGcFpM0pTQiNPgz4REJZDsx
7CXNe48eDJvjGjDVIpmfL5/59hc/L36HHj+PnFoqtkp2rnMij4ZEZ7iUDTzyXbne
cZ4uBKdextLGoAOoorvd3sFcsJURkfF/hJrkk3sAEQEAAYkCPAQYAQgAJhYhBOto
rlRPH92M0mRiT7NppnqQvzh7BQJiQExsAhsMBQkHhh9EAAoJELNppnqQvzh7RQYP
/iZVbIahALzpPI+hTg9vmvybKddaaIdkYq7aWXyqfeXlDrs6imGBsDUjQZMEWxgr
Z/3VqGCzsUSwuubP/bkTzJtx0mKkhMTrzr2fITVvfuNVvfPcEkthL/gxo2+6A3Ph
WMwdZUAvnaCVcs35IkFI2xyZZkMqdWdGeuf6QES85ZmAtuLgyk+I1XCbY8aeu0/O
51NyD81Lcc5yYlN8beaufDA0nJtNUDG3GVA+hdSklComO2Q89b4KqiyiWlF26BDn
OkVKDTmIv6834IytU+STznDzt22yJ2XJmX9k0hOsvPKb13ZQVVBljatGiE11F/He
Xit9ckUtqpC2KFG8EiIwpNtRvZXSl3etUvPYKTeAmo988QSYJZLQ3HqswTybSw6Q
3Ixq7d0xRQCziPZzek5CaxlGMqjssBzv8ZqEoWFnZoEJDO9xMRL6A8fVnkeeK+Ry
dQXaCdBX3HtQ6vVD964omzE+XkIJm0w30YVbXRwPEWjtw7kKH78GSSR95u4j/hZr
VJBPNrCzFPHh6KQrBx6aB8OzIipGzZbrY8GuoLOz1ODX2XfmwJ2a9iy8xp2tgVe6
QdeJQoSnAkx1MsC2Mn4BfzhgvC4eLf6pnmiREKpkf5ClKiNJJxP0fnN7hmm4/R3y
krJzFvwzZF9h3I61P96qxn/URA+DuSo/ZDl0KV6eOONU
=HlTQ
-----END PGP PUBLIC KEY BLOCK-----
"""

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
        return format == "three-bundle"
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
            if !expected.secureCompare(actual) {
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

    public static func calculateSHA256(_ filePath: String) -> String? {
        guard let fileHandle = FileHandle(forReadingAtPath: filePath) else { return nil }
        defer { fileHandle.closeFile() }

        var context = CC_SHA256_CTX()
        CC_SHA256_Init(&context)
        while autoreleasepool(invoking: {
            let data = fileHandle.readData(ofLength: 8192)
            if data.count > 0 {
                data.withUnsafeBytes { CC_SHA256_Update(&context, $0.baseAddress, CC_LONG(data.count)) }
                return true
            }
            return false
        }) {}
        var hash = [UInt8](repeating: 0, count: Int(CC_SHA256_DIGEST_LENGTH))
        CC_SHA256_Final(&hash, &context)
        return hash.map { String(format: "%02x", $0) }.joined()
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

        // GPG cleartext signature verification is required
        guard let sha256 = verifyGPGAndExtractSha256(signature) else {
            OneKeyLog.error("BundleUpdate", "readMetadataFileSha256: GPG verification failed, rejecting unsigned content")
            return nil
        }
        return sha256
    }

    /// Verify a PGP cleartext-signed message and extract the sha256 from the signed JSON body.
    /// Uses Gopenpgp framework (vendored xcframework).
    /// Returns nil if verification fails.
    public static func verifyGPGAndExtractSha256(_ signature: String) -> String? {
        // Check if this looks like a PGP signed message
        guard signature.contains("-----BEGIN PGP SIGNED MESSAGE-----") else {
            return nil
        }

        // 1. Load public key
        guard let pubKey = CryptoKey(fromArmored: GPG_PUBLIC_KEY) else {
            OneKeyLog.error("BundleUpdate", "Failed to parse GPG public key")
            return nil
        }

        // 2. Get PGP handle
        guard let pgp = CryptoPGP() else {
            OneKeyLog.error("BundleUpdate", "Failed to create PGPHandle")
            return nil
        }

        // 3. Build verify handle: pgp.verify().verificationKey(pubKey).new()
        guard let verifyBuilder = pgp.verify() else {
            OneKeyLog.error("BundleUpdate", "Failed to get verify builder")
            return nil
        }
        guard let builderWithKey = verifyBuilder.verificationKey(pubKey) else {
            OneKeyLog.error("BundleUpdate", "Failed to set verification key")
            return nil
        }

        let verifyHandle: any CryptoPGPVerifyProtocol
        do {
            verifyHandle = try builderWithKey.new()
        } catch {
            OneKeyLog.error("BundleUpdate", "Failed to create verify handle: \(error.localizedDescription)")
            return nil
        }

        // 4. Verify cleartext
        guard let signatureData = signature.data(using: .utf8) else {
            return nil
        }

        let cleartextResult: CryptoVerifyCleartextResult
        do {
            cleartextResult = try verifyHandle.verifyCleartext(signatureData)
        } catch {
            OneKeyLog.error("BundleUpdate", "GPG verification error: \(error.localizedDescription)")
            return nil
        }

        // 5. Check signature error
        do {
            try cleartextResult.signatureError()
        } catch {
            OneKeyLog.error("BundleUpdate", "GPG signature invalid: \(error.localizedDescription)")
            return nil
        }

        // 6. Get cleartext
        guard let cleartextData = cleartextResult.cleartext() else {
            OneKeyLog.error("BundleUpdate", "Failed to extract cleartext from GPG result")
            return nil
        }

        guard let text = String(data: cleartextData, encoding: .utf8) else {
            return nil
        }

        // 7. Parse JSON and extract sha256
        guard let jsonData = text.data(using: .utf8),
              let json = try? JSONSerialization.jsonObject(with: jsonData) as? [String: Any],
              let sha256 = json["sha256"] as? String else {
            OneKeyLog.error("BundleUpdate", "Failed to parse cleartext JSON")
            return nil
        }

        OneKeyLog.info("BundleUpdate", "GPG verification succeeded, sha256: \(sha256)")
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
        return calculatedSha256.secureCompare(extractedSha256)
    }

    public static func validateExtractedPathSafety(_ destination: String) -> Bool {
        let fm = FileManager.default
        let resolvedDestination = (destination as NSString).resolvingSymlinksInPath

        guard let enumerator = fm.enumerator(atPath: destination) else { return true }
        while let file = enumerator.nextObject() as? String {
            let fullPath = (destination as NSString).appendingPathComponent(file)
            if let attrs = enumerator.fileAttributes,
               attrs[.type] as? FileAttributeType == .typeSymbolicLink {
                OneKeyLog.error("BundleUpdate", "Symlink detected in extracted bundle: \(file)")
                return false
            }
            let resolvedPath = (fullPath as NSString).resolvingSymlinksInPath
            if !resolvedPath.hasPrefix(resolvedDestination) {
                OneKeyLog.error("BundleUpdate", "Path traversal detected in extracted bundle: \(file)")
                return false
            }
        }
        return true
    }

    public static func validateAllFilesInDir(_ dirPath: String, metadata: [String: Any], appVersion: String, bundleVersion: String) -> Bool {
        let parentBundleDir = bundleDir()
        let folderName = "\(appVersion)-\(bundleVersion)"
        let jsBundleDir = (parentBundleDir as NSString).appendingPathComponent(folderName) + "/"
        let fm = FileManager.default
        let fileEntries = fileMetadataEntries(from: metadata)

        guard let enumerator = fm.enumerator(atPath: dirPath) else { return false }
        while let file = enumerator.nextObject() as? String {
            if file.contains("metadata.json") || file.contains(".DS_Store") { continue }
            let fullPath = (dirPath as NSString).appendingPathComponent(file)
            var isDir: ObjCBool = false
            if fm.fileExists(atPath: fullPath, isDirectory: &isDir), isDir.boolValue { continue }

            let relativePath = fullPath.replacingOccurrences(of: jsBundleDir, with: "")
            guard let expectedSHA256 = fileEntries[relativePath] else {
                OneKeyLog.error("BundleUpdate", "[bundle-verify] File on disk not found in metadata: \(relativePath)")
                return false
            }
            guard let actualSHA256 = calculateSHA256(fullPath) else {
                OneKeyLog.error("BundleUpdate", "[bundle-verify] Failed to calculate SHA256 for file: \(relativePath)")
                return false
            }
            if !expectedSHA256.secureCompare(actualSHA256) {
                OneKeyLog.error("BundleUpdate", "[bundle-verify] SHA256 mismatch for \(relativePath)")
                return false
            }
        }

        // Verify completeness
        for key in fileEntries.keys {
            let expectedFilePath = jsBundleDir + key
            if !fm.fileExists(atPath: expectedFilePath) {
                OneKeyLog.error("BundleUpdate", "[bundle-verify] File listed in metadata but missing on disk: \(key)")
                return false
            }
        }
        return true
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
            bundleFormat == "three-bundle"
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
            bundleFormat == "three-bundle" {
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
            if !expected.secureCompare(actual) {
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

// Constant-time string comparison to prevent timing attacks on hash comparisons
private extension String {
    func secureCompare(_ other: String) -> Bool {
        let lhs = Array(self.utf8)
        let rhs = Array(other.utf8)
        guard lhs.count == rhs.count else { return false }
        var result: UInt8 = 0
        for i in 0..<lhs.count {
            result |= lhs[i] ^ rhs[i]
        }
        return result == 0
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
            cont?.resume(throwing: error)
        } else if let tempURL = tempFileURL, let response = task.response {
            cont?.resume(returning: (tempURL, response))
        } else {
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

    override init() {
        super.init()
        urlSession = createURLSession()
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
            defer { self.stateQueue.sync { self.isDownloading = false } }

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
                    DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) { [weak self] in
                        self?.sendEvent(type: "update/complete")
                    }
                    return result
                } else {
                    OneKeyLog.warn("BundleUpdate", "downloadBundle: existing file SHA256 mismatch, re-downloading")
                    try? FileManager.default.removeItem(atPath: filePath)
                }
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

            self.sendEvent(type: "update/start")
            OneKeyLog.info("BundleUpdate", "downloadBundle: starting download...")

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

            let (tempURL, response) = try await withCheckedThrowingContinuation { (continuation: CheckedContinuation<(URL, URLResponse), Error>) in
                delegate.setContinuation(continuation)
                let task = session.downloadTask(with: request)
                task.resume()
            }

            // Verify HTTPS was maintained (no HTTP redirect)
            if let httpResponse = response as? HTTPURLResponse,
               let responseUrl = httpResponse.url,
               responseUrl.scheme?.lowercased() != "https" {
                OneKeyLog.error("BundleUpdate", "downloadBundle: redirected to non-HTTPS URL: \(responseUrl)")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Download was redirected to non-HTTPS URL"])
            }

            guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 else {
                let statusCode = (response as? HTTPURLResponse)?.statusCode ?? -1
                OneKeyLog.error("BundleUpdate", "downloadBundle: HTTP error, statusCode=\(statusCode)")
                self.sendEvent(type: "update/error", message: "HTTP error \(statusCode)")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Download failed with HTTP \(statusCode)"])
            }

            OneKeyLog.info("BundleUpdate", "downloadBundle: download finished, HTTP 200, moving to destination...")

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
                try? FileManager.default.removeItem(atPath: filePath)
                OneKeyLog.error("BundleUpdate", "downloadBundle: SHA256 verification failed after download")
                self.sendEvent(type: "update/error", message: "Bundle signature verification failed")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle signature verification failed"])
            }

            self.sendEvent(type: "update/complete")
            OneKeyLog.info("BundleUpdate", "downloadBundle: completed successfully, appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
            return result
        }
    }

    private func verifyBundleSHA256(_ bundlePath: String, sha256: String) -> Bool {
        guard let calculated = BundleUpdateStore.calculateSHA256(bundlePath) else {
            OneKeyLog.error("BundleUpdate", "verifyBundleSHA256: failed to calculate SHA256 for: \(bundlePath)")
            return false
        }
        let isValid = calculated.secureCompare(sha256)
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
                guard let calculated = BundleUpdateStore.calculateSHA256(filePath),
                      calculated.secureCompare(sha256) else {
                    OneKeyLog.error("BundleUpdate", "verifyBundleASC: SHA256 verification failed for file=\(filePath)")
                    throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Bundle signature verification failed"])
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
                OneKeyLog.error("BundleUpdate", "verifyBundleASC: unzip failed: \(error.localizedDescription)")
                try? FileManager.default.removeItem(atPath: destination)
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to unzip bundle: \(error.localizedDescription)"])
            }

            // Validate extracted paths (symlinks, path traversal)
            OneKeyLog.info("BundleUpdate", "verifyBundleASC: validating extracted path safety...")
            if !BundleUpdateStore.validateExtractedPathSafety(destination) {
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
            guard calculated.secureCompare(sha256) else {
                OneKeyLog.error("BundleUpdate", "verifyBundle: SHA256 mismatch, expected=\(sha256.prefix(16))..., got=\(calculated.prefix(16))...")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "SHA256 verification failed"])
            }

            OneKeyLog.info("BundleUpdate", "verifyBundle: SHA256 verified OK for appVersion=\(appVersion), bundleVersion=\(bundleVersion)")
        }
    }

    func installBundle(params: BundleInstallParams) throws -> Promise<Void> {
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
            let result = BundleUpdateStore.verifyGPGAndExtractSha256(testSignature)
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
            guard resolvedPath.hasPrefix(bundleDir) || resolvedPath.hasPrefix(downloadDir) else {
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
