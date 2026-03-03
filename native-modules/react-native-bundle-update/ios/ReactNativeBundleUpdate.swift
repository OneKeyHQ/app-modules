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

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
P2Q5dClenjjjVA==
=F0np
-----END PGP PUBLIC KEY BLOCK-----
"""

// Public static store for AppDelegate access (called before JS starts)
public class BundleUpdateStore {
    private static let bundlePrefsKey = "currentBundleVersion"
    private static let nativeVersionKey = "nativeVersion"

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
        guard let dir = currentBundleDir() else { return "" }
        return (dir as NSString).appendingPathComponent("web-embed")
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

    public static func getMetadataFilePath(_ currentBundleVersion: String) -> String? {
        let path = (bundleDir() as NSString)
            .appendingPathComponent(currentBundleVersion)
        let metadataPath = (path as NSString).appendingPathComponent("metadata.json")
        guard FileManager.default.fileExists(atPath: metadataPath) else { return nil }
        return metadataPath
    }

    public static func getMetadataFileContent(_ currentBundleVersion: String) -> [String: String]? {
        guard let path = getMetadataFilePath(currentBundleVersion),
              let data = FileManager.default.contents(atPath: path),
              let json = try? JSONSerialization.jsonObject(with: data) as? [String: String] else { return nil }
        return json
    }

    /// Returns true if OneKey developer mode (DevSettings) is enabled.
    /// Reads the persisted value from MMKV storage written by the JS ServiceDevSetting layer.
    public static func isDevSettingsEnabled() -> Bool {
        // Ensure MMKV is initialized (safe to call multiple times)
        MMKV.initialize(rootDir: nil)
        guard let mmkv = MMKV(mmapID: "onekey-app-setting") else { return false }
        return mmkv.bool(forKey: "onekey_developer_mode_enabled", defaultValue: false)
    }

    /// Returns true if the skip-GPG-verification toggle is enabled in developer settings.
    /// Reads the persisted value from MMKV storage (key: onekey_bundle_skip_gpg_verification,
    /// instance: onekey-app-setting).
    public static func isSkipGPGEnabled() -> Bool {
        MMKV.initialize(rootDir: nil)
        guard let mmkv = MMKV(mmapID: "onekey-app-setting") else { return false }
        return mmkv.bool(forKey: "onekey_bundle_skip_gpg_verification", defaultValue: false)
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

    public static func validateAllFilesInDir(_ dirPath: String, metadata: [String: String], appVersion: String, bundleVersion: String) -> Bool {
        let parentBundleDir = bundleDir()
        let folderName = "\(appVersion)-\(bundleVersion)"
        let jsBundleDir = (parentBundleDir as NSString).appendingPathComponent(folderName) + "/"
        let fm = FileManager.default

        guard let enumerator = fm.enumerator(atPath: dirPath) else { return false }
        while let file = enumerator.nextObject() as? String {
            if file.contains("metadata.json") || file.contains(".DS_Store") { continue }
            let fullPath = (dirPath as NSString).appendingPathComponent(file)
            var isDir: ObjCBool = false
            if fm.fileExists(atPath: fullPath, isDirectory: &isDir), isDir.boolValue { continue }

            let relativePath = fullPath.replacingOccurrences(of: jsBundleDir, with: "")
            guard let expectedSHA256 = metadata[relativePath] else {
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
        for key in metadata.keys {
            let expectedFilePath = jsBundleDir + key
            if !fm.fileExists(atPath: expectedFilePath) {
                OneKeyLog.error("BundleUpdate", "[bundle-verify] File listed in metadata but missing on disk: \(key)")
                return false
            }
        }
        return true
    }

    public static func currentBundleMainJSBundle() -> String? {
        guard let currentBundleVer = currentBundleVersion() else {
            OneKeyLog.warn("BundleUpdate", "getJsBundlePath: no currentBundleVersion stored")
            return nil
        }

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
                ud.removeObject(forKey: cbv)
                ud.removeObject(forKey: "currentBundleVersion")
            }
            ud.synchronize()
            return nil
        }

        guard let folderName = currentBundleDir(),
              FileManager.default.fileExists(atPath: folderName) else {
            OneKeyLog.warn("BundleUpdate", "getJsBundlePath: currentBundleDir does not exist")
            return nil
        }

        let signature = UserDefaults.standard.string(forKey: currentBundleVer) ?? ""
        OneKeyLog.debug("BundleUpdate", "getJsBundlePath: signatureLength=\(signature.count)")

        let devSettingsEnabled = isDevSettingsEnabled()
        if devSettingsEnabled {
            OneKeyLog.warn("BundleUpdate", "Startup SHA256 validation skipped (DevSettings enabled)")
        }
        if !devSettingsEnabled && !validateMetadataFileSha256(currentBundleVer, signature: signature) {
            OneKeyLog.warn("BundleUpdate", "getJsBundlePath: validateMetadataFileSha256 failed, signatureLength=\(signature.count)")
            return nil
        }

        guard let metadata = getMetadataFileContent(currentBundleVer) else {
            OneKeyLog.warn("BundleUpdate", "getJsBundlePath: getMetadataFileContent returned nil")
            return nil
        }

        if let dashRange = currentBundleVer.range(of: "-", options: .backwards) {
            let appVer = String(currentBundleVer[currentBundleVer.startIndex..<dashRange.lowerBound])
            let bundleVer = String(currentBundleVer[dashRange.upperBound...])
            if !validateAllFilesInDir(folderName, metadata: metadata, appVersion: appVer, bundleVersion: bundleVer) {
                OneKeyLog.info("BundleUpdate", "validateAllFilesInDir failed on startup")
                return nil
            }
        }

        let mainJSBundle = (folderName as NSString).appendingPathComponent("main.jsbundle.hbc")
        guard FileManager.default.fileExists(atPath: mainJSBundle) else {
            OneKeyLog.info("BundleUpdate", "mainJSBundleFile does not exist")
            return nil
        }
        return mainJSBundle
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
        let bDir = bundleDir()
        let fm = FileManager.default
        if fm.fileExists(atPath: bDir) {
            try? fm.removeItem(atPath: bDir)
        }
        let ud = UserDefaults.standard
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
            UserDefaults.standard.set(signature, forKey: storageKey)
            UserDefaults.standard.synchronize()

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
            let devSettings = BundleUpdateStore.isDevSettingsEnabled()
            let skipGPGToggle = BundleUpdateStore.isSkipGPGEnabled()
            let skipGPG = devSettings && skipGPGToggle
            OneKeyLog.info("BundleUpdate", "verifyBundleASC: GPG check: devSettings=\(devSettings), skipGPGToggle=\(skipGPGToggle), skipGPG=\(skipGPG)")

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
        return Promise.async {
            let appVersion = params.latestVersion
            let bundleVersion = params.bundleVersion
            let signature = params.signature

            OneKeyLog.info("BundleUpdate", "installBundle: appVersion=\(appVersion), bundleVersion=\(bundleVersion), signatureLength=\(signature.count)")

            // GPG verification skipped only when both DevSettings and skip-GPG toggle are enabled
            let devSettings = BundleUpdateStore.isDevSettingsEnabled()
            let skipGPGToggle = BundleUpdateStore.isSkipGPGEnabled()
            let skipGPG = devSettings && skipGPGToggle
            OneKeyLog.info("BundleUpdate", "installBundle: GPG check: devSettings=\(devSettings), skipGPGToggle=\(skipGPGToggle), skipGPG=\(skipGPG)")

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
                ud.set(signature, forKey: folderName)
            }
            let currentNativeVersion = BundleUpdateStore.getCurrentNativeVersion()
            BundleUpdateStore.setNativeVersion(currentNativeVersion)
            ud.synchronize()

            // Manage fallback data
            var fallbackData = BundleUpdateStore.readFallbackUpdateBundleDataFile()

            if let current = currentFolderName,
               let dashRange = current.range(of: "-", options: .backwards) {
                let curAppVersion = String(current[current.startIndex..<dashRange.lowerBound])
                let curBundleVersion = String(current[dashRange.upperBound...])
                let curSignature = ud.string(forKey: current) ?? ""
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
                    ud.removeObject(forKey: dirName)
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

    func clearBundle() throws -> Promise<Void> {
        return Promise.async { [weak self] in
            OneKeyLog.info("BundleUpdate", "clearBundle: clearing download directory and cancelling downloads...")
            let downloadDir = BundleUpdateStore.downloadBundleDir()
            if FileManager.default.fileExists(atPath: downloadDir) {
                try FileManager.default.removeItem(atPath: downloadDir)
                OneKeyLog.info("BundleUpdate", "clearBundle: download directory deleted")
            } else {
                OneKeyLog.info("BundleUpdate", "clearBundle: download directory does not exist, skipping")
            }
            // Cancel all in-flight downloads by invalidating the session
            self?.urlSession?.invalidateAndCancel()
            self?.urlSession = self?.createURLSession()
            self?.stateQueue.sync { self?.isDownloading = false }
            OneKeyLog.info("BundleUpdate", "clearBundle: completed")
        }
    }

    func clearAllJSBundleData() throws -> Promise<TestResult> {
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
            ud.removeObject(forKey: "nativeVersion")
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
            let devSettings = BundleUpdateStore.isDevSettingsEnabled()
            let skipGPGToggle = BundleUpdateStore.isSkipGPGEnabled()
            OneKeyLog.info("BundleUpdate", "setCurrentUpdateBundleData: GPG check: devSettings=\(devSettings), skipGPGToggle=\(skipGPGToggle)")
            if !(devSettings && skipGPGToggle) {
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
            ud.set(params.signature, forKey: bundleVersion)
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

    func getJsBundlePath() throws -> Promise<String> {
        return Promise.async {
            let path = BundleUpdateStore.currentBundleMainJSBundle() ?? ""
            OneKeyLog.info("BundleUpdate", "getJsBundlePath: \(path.isEmpty ? "(empty/no bundle)" : path)")
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
                  let metadata = try? JSONSerialization.jsonObject(with: data) as? [String: String] else {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: failed to parse metadata.json")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to parse metadata.json"])
            }
            OneKeyLog.info("BundleUpdate", "verifyExtractedBundle: parsing metadata and validating files...")
            if !BundleUpdateStore.validateAllFilesInDir(bundlePath, metadata: metadata, appVersion: appVersion, bundleVersion: bundleVersion) {
                OneKeyLog.error("BundleUpdate", "verifyExtractedBundle: file integrity check failed")
                throw NSError(domain: "BundleUpdate", code: -1, userInfo: [NSLocalizedDescriptionKey: "File integrity check failed"])
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
