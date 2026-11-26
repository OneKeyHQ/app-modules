import Foundation

@objc(OneKeyDeviceUtilsFirstLaunch)
public final class DeviceUtilsFirstLaunch: NSObject {
    private static let lock = NSLock()
    private static var history: FirstLaunchHistory?

    /// Returns true if the persisted first-launch state file already exists on disk.
    public static func hasPersistedState() -> Bool {
        guard let directory = defaultDirectory() else { return false }
        let fileURL = directory.appendingPathComponent(FirstLaunchHistory.fileName)
        return FileManager.default.fileExists(atPath: fileURL.path)
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
            directory: directory,
            appVersion: Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String,
            previousAppVersion: legacyAppVersion,
            hasPreviousLaunch: isLegacyInstall
        )
    }

    static var value: Bool {
        withHistory(default: false) { $0.isFirstLaunch }
    }

    static var forCurrentAppVersion: Bool {
        withHistory(default: false) { $0.isFirstLaunchForCurrentAppVersion }
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
            directory: directory,
            appVersion: Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String,
            previousAppVersion: nil,
            hasPreviousLaunch: false
        )
        history = created
        return read(created)
    }
}

final class FirstLaunchHistory {
    static let fileName = "onekey-device-utils-first-launch.json"

    private struct PersistedState: Codable {
        var hasLaunched: Bool
        var highestAppVersion: String?
        var highestBundleVersion: String?
    }

    let isFirstLaunch: Bool
    let isFirstLaunchForCurrentAppVersion: Bool
    private let fileURL: URL
    private var persisted: PersistedState
    private var storageAvailable: Bool
    private var bundleResults: [String: Bool] = [:]
    private var initialBundleVersion: String?

    init(directory: URL, appVersion: String?, previousAppVersion: String?, hasPreviousLaunch: Bool) {
        fileURL = directory.appendingPathComponent(Self.fileName)
        let validPrevious = Self.validatedVersion(previousAppVersion)
        let validCurrent = Self.validatedVersion(appVersion)

        let fileExists = FileManager.default.fileExists(atPath: fileURL.path)
        let loaded = Self.readState(from: fileURL)
        if fileExists && loaded == nil {
            storageAvailable = false
            persisted = PersistedState(hasLaunched: true, highestAppVersion: validPrevious, highestBundleVersion: nil)
            isFirstLaunch = false
            isFirstLaunchForCurrentAppVersion = false
            return
        }
        var state = loaded ?? PersistedState(
            hasLaunched: false,
            highestAppVersion: validPrevious,
            highestBundleVersion: nil
        )
        var needsWrite = loaded == nil

        let computedFirstLaunch = !state.hasLaunched && !hasPreviousLaunch
        if !state.hasLaunched {
            state.hasLaunched = true
            needsWrite = true
        }

        let computedFirstAppVersion: Bool
        if let validCurrent {
            let baseline = state.highestAppVersion
            let isHigher = baseline == nil || Self.compareVersions(validCurrent, baseline!) > 0
            computedFirstAppVersion = isHigher
            if isHigher {
                state.highestAppVersion = validCurrent
                needsWrite = true
            }
        } else {
            computedFirstAppVersion = false
        }

        if needsWrite {
            if Self.writeState(state, to: fileURL, directory: directory) {
                storageAvailable = true
                persisted = state
                isFirstLaunch = computedFirstLaunch
                isFirstLaunchForCurrentAppVersion = computedFirstAppVersion
            } else {
                storageAvailable = false
                persisted = state
                isFirstLaunch = false
                isFirstLaunchForCurrentAppVersion = false
            }
        } else {
            storageAvailable = true
            persisted = state
            isFirstLaunch = computedFirstLaunch
            isFirstLaunchForCurrentAppVersion = computedFirstAppVersion
        }
    }

    func isFirstLaunchForBundle(_ bundleVersion: String?) -> Bool {
        guard let bundleVersion else {
            return initialBundleVersion.flatMap { bundleResults[$0] } ?? false
        }
        guard let validBundle = Self.validatedVersion(bundleVersion) else {
            return false
        }
        if initialBundleVersion == nil {
            initialBundleVersion = validBundle
        }
        if let cached = bundleResults[validBundle] {
            return cached
        }
        guard storageAvailable else {
            bundleResults[validBundle] = false
            return false
        }
        let baseline = persisted.highestBundleVersion
        let isHigher = baseline == nil || Self.compareVersions(validBundle, baseline!) > 0
        if isHigher {
            var next = persisted
            next.highestBundleVersion = validBundle
            guard Self.writeState(next, to: fileURL, directory: fileURL.deletingLastPathComponent()) else {
                bundleResults[validBundle] = false
                return false
            }
            persisted = next
        }
        bundleResults[validBundle] = isHigher
        return isHigher
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
        guard versionRegex.firstMatch(in: version, options: [], range: fullRange) != nil else {
            return nil
        }
        return version
    }

    private static let versionRegex: NSRegularExpression = {
        let pattern = "^[0-9]+(?:\\.[0-9]+)*(?:-[0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*)?(?:\\+[0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*)?$"
        return try! NSRegularExpression(pattern: pattern, options: [])
    }()

    private static func readState(from url: URL) -> PersistedState? {
        guard let data = try? Data(contentsOf: url),
              let decoded = try? JSONDecoder().decode(PersistedState.self, from: data) else {
            return nil
        }
        if let app = decoded.highestAppVersion, validatedVersion(app) == nil {
            return nil
        }
        if let bundle = decoded.highestBundleVersion, validatedVersion(bundle) == nil {
            return nil
        }
        return decoded
    }

    private static func writeState(_ state: PersistedState, to url: URL, directory: URL) -> Bool {
        do {
            try FileManager.default.createDirectory(at: directory, withIntermediateDirectories: true)
            let data = try JSONEncoder().encode(state)
            try data.write(to: url, options: .atomic)
            return true
        } catch {
            return false
        }
    }
}
