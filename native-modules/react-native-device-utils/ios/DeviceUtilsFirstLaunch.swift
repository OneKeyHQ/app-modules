import Foundation

@objc(OneKeyDeviceUtilsFirstLaunch)
public final class DeviceUtilsFirstLaunch: NSObject {
    private static let lock = NSLock()
    private static var history: FirstLaunchHistory?

    /// Returns true if the persisted launched marker already exists on disk.
    public static func hasPersistedState() -> Bool {
        guard let directory = defaultDirectory() else { return false }
        let launchedURL = directory
            .appendingPathComponent(FirstLaunchHistory.directoryName, isDirectory: true)
            .appendingPathComponent(FirstLaunchHistory.launchedFileName)
        return FileManager.default.fileExists(atPath: launchedURL.path)
    }

    /// Object-returning variant of `hasPersistedState()` safe for dynamic invocation
    /// via `NSClassFromString("OneKeyDeviceUtilsFirstLaunch")` + `perform(_:)`.
    @objc(hasPersistedStateNumber)
    public static func hasPersistedStateNumber() -> NSNumber {
        NSNumber(value: hasPersistedState())
    }

    /// Initializes the process-wide first-launch state from the host app (e.g. AppDelegate).
    /// Accepts an Objective-C `NSDictionary` so Swift hosts can invoke it dynamically via
    /// `NSClassFromString` + `perform(_:with:)` without importing Nitro C++ headers.
    /// Supported keys:
    /// - `"isLegacyInstall"`: `NSNumber` / `Bool` (default `false`)
    /// - `"legacyAppVersion"`: `NSString` / `String` (optional)
    @objc(initializeWithOptions:)
    public static func initialize(options: NSDictionary?) {
        let isLegacyInstall = (options?["isLegacyInstall"] as? NSNumber)?.boolValue ?? false
        let legacyAppVersion = options?["legacyAppVersion"] as? String
        initialize(isLegacyInstall: isLegacyInstall, legacyAppVersion: legacyAppVersion)
    }

    /// Swift-friendly initializer for hosts that can import this module directly.
    public static func initialize(
        isLegacyInstall: Bool = false,
        legacyAppVersion: String? = nil
    ) {
        lock.lock()
        defer { lock.unlock() }
        guard history == nil else { return }
        guard let directory = defaultDirectory() else { return }
        history = FirstLaunchHistory(
            baseDirectory: directory,
            appVersion: Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String,
            previousAppVersion: legacyAppVersion,
            hasPreviousLaunch: isLegacyInstall
        )
    }

    /// Deletes persisted first-launch files and clears in-memory state so a subsequent
    /// JS restart within the same native process observes a fresh launch.
    /// Returns true if persisted files were removed (or already absent), false on failure.
    @objc @discardableResult public static func reset() -> Bool {
        lock.lock()
        defer { lock.unlock() }
        guard let directory = defaultDirectory() else { return false }
        let storeDir = directory.appendingPathComponent(FirstLaunchHistory.directoryName, isDirectory: true)
        if FileManager.default.fileExists(atPath: storeDir.path) {
            do {
                try FileManager.default.removeItem(at: storeDir)
            } catch {
                return false
            }
        }
        history = nil
        return true
    }

    static var value: Bool {
        withHistory(default: false) { $0.isFirstLaunch }
    }

    static var forCurrentAppVersion: Bool {
        withHistory(default: false) { $0.isFirstLaunchForCurrentAppVersion }
    }

    static func initForBundle(_ bundleVersion: String) {
        withHistory(default: ()) { $0.initForBundle(bundleVersion) }
    }

    static func forBundle(_ bundleVersion: String?) -> Bool {
        withHistory(default: false) { $0.isFirstLaunchForBundle(bundleVersion) }
    }

    private static func defaultDirectory() -> URL? {
        FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask).first
    }

    private static func withHistory<Result>(default fallback: Result, _ read: (FirstLaunchHistory) -> Result) -> Result {
        lock.lock()
        defer { lock.unlock() }
        if let existing = history {
            return read(existing)
        }
        guard let directory = defaultDirectory() else {
            return fallback
        }
        let created = FirstLaunchHistory(
            baseDirectory: directory,
            appVersion: Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String,
            previousAppVersion: nil,
            hasPreviousLaunch: false
        )
        history = created
        return read(created)
    }
}

final class FirstLaunchHistory {
    static let directoryName = "1k_first_launch"
    static let launchedFileName = "launched"
    static let appVersionFileName = "app_ver"
    static let bundleVersionFileName = "bundle_ver"

    let isFirstLaunch: Bool
    let isFirstLaunchForCurrentAppVersion: Bool
    private let storeDirectory: URL
    private var bundleResults: [String: Bool] = [:]
    private var currentBundleVersion: String?

    init(baseDirectory: URL, appVersion: String?, previousAppVersion: String?, hasPreviousLaunch: Bool) {
        let dir = baseDirectory.appendingPathComponent(Self.directoryName, isDirectory: true)
        storeDirectory = dir

        let launchedURL = dir.appendingPathComponent(Self.launchedFileName)
        let launchedExists = FileManager.default.fileExists(atPath: launchedURL.path)
        let legacyBaseline = launchedExists ? nil : Self.validatedVersion(previousAppVersion)

        // Seed or evaluate app_ver BEFORE writing `launched` so an interrupted
        // startup never loses the legacy app-version baseline.
        let appVerURL = dir.appendingPathComponent(Self.appVersionFileName)
        if let validCurrent = Self.validatedVersion(appVersion) {
            isFirstLaunchForCurrentAppVersion = Self.evaluateVersionFile(
                at: appVerURL,
                directory: dir,
                currentVersion: validCurrent,
                legacyBaseline: legacyBaseline
            )
        } else {
            if let legacyBaseline, !FileManager.default.fileExists(atPath: appVerURL.path) {
                _ = Self.writeString(legacyBaseline, to: appVerURL, directory: dir)
            }
            isFirstLaunchForCurrentAppVersion = false
        }

        if launchedExists {
            isFirstLaunch = false
        } else {
            let wrote = Self.writeString("1", to: launchedURL, directory: dir)
            isFirstLaunch = wrote && !hasPreviousLaunch
        }
    }

    func initForBundle(_ bundleVersion: String) {
        guard let validBundle = Self.validatedVersion(bundleVersion) else {
            return
        }
        currentBundleVersion = validBundle
        _ = resolveBundle(validBundle)
    }

    func isFirstLaunchForBundle(_ bundleVersion: String?) -> Bool {
        guard let bundleVersion else {
            return currentBundleVersion.flatMap { bundleResults[$0] } ?? false
        }
        guard let validBundle = Self.validatedVersion(bundleVersion) else {
            return false
        }
        return resolveBundle(validBundle)
    }

    private func resolveBundle(_ validBundle: String) -> Bool {
        if let cached = bundleResults[validBundle] {
            return cached
        }
        let result = Self.evaluateVersionFile(
            at: storeDirectory.appendingPathComponent(Self.bundleVersionFileName),
            directory: storeDirectory,
            currentVersion: validBundle,
            legacyBaseline: nil
        )
        bundleResults[validBundle] = result
        return result
    }

    private static func evaluateVersionFile(
        at fileURL: URL,
        directory: URL,
        currentVersion: String,
        legacyBaseline: String?
    ) -> Bool {
        let exists = FileManager.default.fileExists(atPath: fileURL.path)
        let existing: String?
        if exists {
            guard let raw = try? String(contentsOf: fileURL, encoding: .utf8),
                  let valid = validatedVersion(raw) else {
                // Corrupted file: fail closed without overwriting.
                return false
            }
            existing = valid
        } else {
            existing = nil
        }

        let baseline = existing ?? legacyBaseline
        let isHigher = baseline == nil || compareVersions(currentVersion, baseline!) > 0
        if isHigher {
            return writeString(currentVersion, to: fileURL, directory: directory)
        }
        if existing == nil, let baseline {
            _ = writeString(baseline, to: fileURL, directory: directory)
        }
        return false
    }

    private static func writeString(_ value: String, to url: URL, directory: URL) -> Bool {
        do {
            try FileManager.default.createDirectory(at: directory, withIntermediateDirectories: true)
            var dirURL = directory
            var values = URLResourceValues()
            values.isExcludedFromBackup = true
            try dirURL.setResourceValues(values)
            try Data(value.utf8).write(to: url, options: .atomic)
            return true
        } catch {
            return false
        }
    }

    static func compareVersions(_ a: String, _ b: String) -> Int {
        func stripBuild(_ v: String) -> Substring {
            if let plus = v.firstIndex(of: "+") {
                return v[..<plus]
            }
            return v[...]
        }
        func splitPre(_ v: Substring) -> (Substring, Substring?) {
            if let dash = v.firstIndex(of: "-") {
                return (v[..<dash], v[v.index(after: dash)...])
            }
            return (v, nil)
        }
        func isDigits(_ s: Substring) -> Bool {
            !s.isEmpty && s.utf8.allSatisfy { $0 >= 48 && $0 <= 57 }
        }
        func compareUnsigned(_ lhs: Substring, _ rhs: Substring) -> Int {
            let normA = lhs.drop { $0 == "0" }
            let normB = rhs.drop { $0 == "0" }
            if normA.count != normB.count {
                return normA.count < normB.count ? -1 : 1
            }
            if normA == normB { return 0 }
            return normA < normB ? -1 : 1
        }
        func compareIdent(_ lhs: Substring, _ rhs: Substring) -> Int {
            let aNum = isDigits(lhs)
            let bNum = isDigits(rhs)
            if aNum && bNum {
                return compareUnsigned(lhs, rhs)
            }
            if aNum != bNum {
                return aNum ? -1 : 1
            }
            if lhs == rhs { return 0 }
            return lhs < rhs ? -1 : 1
        }

        let (mainA, preA) = splitPre(stripBuild(a))
        let (mainB, preB) = splitPre(stripBuild(b))
        let segA = mainA.split(separator: ".", omittingEmptySubsequences: false)
        let segB = mainB.split(separator: ".", omittingEmptySubsequences: false)
        let maxLen = max(segA.count, segB.count)
        let zero: Substring = "0"

        for i in 0..<maxLen {
            let partA = i < segA.count ? segA[i] : zero
            let partB = i < segB.count ? segB[i] : zero
            let cmp = compareIdent(partA, partB)
            if cmp != 0 { return cmp }
        }

        switch (preA, preB) {
        case (nil, nil):
            return 0
        case (nil, .some):
            return 1
        case (.some, nil):
            return -1
        case (.some(let pa), .some(let pb)):
            let preSegA = pa.split(separator: ".", omittingEmptySubsequences: false)
            let preSegB = pb.split(separator: ".", omittingEmptySubsequences: false)
            let maxPre = max(preSegA.count, preSegB.count)
            for i in 0..<maxPre {
                if i >= preSegA.count { return -1 }
                if i >= preSegB.count { return 1 }
                let cmp = compareIdent(preSegA[i], preSegB[i])
                if cmp != 0 { return cmp }
            }
            return 0
        }
    }

    static func validatedVersion(_ version: String?) -> String? {
        guard let version, (1...120).contains(version.utf8.count) else { return nil }
        let fullRange = NSRange(version.startIndex..<version.endIndex, in: version)
        guard let match = versionRegex.firstMatch(in: version, options: [], range: fullRange),
              match.range == fullRange else {
            return nil
        }
        return version
    }

    private static let versionRegex: NSRegularExpression = {
        let pattern = "\\A[0-9]+(?:\\.[0-9]+)*(?:-[0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*)?(?:\\+[0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*)?\\z"
        return try! NSRegularExpression(pattern: pattern, options: [])
    }()
}
