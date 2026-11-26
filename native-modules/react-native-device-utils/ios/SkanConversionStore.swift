import Foundation
import StoreKit

enum SkanCoarse: String {
    case low
    case medium
    case high
}

struct SkanMergeOutcome: Equatable {
    let fineValue: Int
    let coarse: SkanCoarse
    let updated: Bool
}

enum SkanConversionError: Error, Equatable, LocalizedError {
    case invalidValue(String)
    case unsupported
    case timedOut
    case reportFailed(String)

    var errorDescription: String? {
        switch self {
        case .invalidValue(let name):
            return "\(name) must be an integer from 0 to 63"
        case .unsupported:
            return "SKAdNetwork conversion values need iOS 16.1 or later"
        case .timedOut:
            return "SKAdNetwork did not answer the conversion value update"
        case .reportFailed(let message):
            return "SKAdNetwork rejected the conversion value update: \(message)"
        }
    }
}

/// Merges milestone bits into the SKAdNetwork fine conversion value.
///
/// Main and background React runtimes each create their own HybridObject, so
/// the shared instance and its serial queue are process-wide. That queue makes
/// read, merge, report and persist one step: two runtimes can never report a
/// smaller value after a larger one. The value is persisted only after
/// SKAdNetwork accepts it, so a failed update is retried by the next call.
final class SkanConversionStore {
    typealias Reporter = (
        _ fineValue: Int,
        _ coarse: SkanCoarse,
        _ completion: @escaping (Error?) -> Void
    ) -> Void

    static let fineValueKey = "so.onekey.deviceutils.skan.fineValue"
    static let coarseValueKey = "so.onekey.deviceutils.skan.coarseValue"
    static let maxValue = 63

    static let shared = SkanConversionStore(
        defaults: .standard,
        reporter: SkanConversionStore.systemReporter
    )

    private let defaults: UserDefaults
    private let reporter: Reporter
    private let reportTimeout: DispatchTimeInterval
    private let queue = DispatchQueue(label: "so.onekey.deviceutils.skan", qos: .utility)

    init(
        defaults: UserDefaults,
        reporter: @escaping Reporter,
        reportTimeout: DispatchTimeInterval = .seconds(30)
    ) {
        self.defaults = defaults
        self.reporter = reporter
        self.reportTimeout = reportTimeout
    }

    static func coarse(for fineValue: Int, highMask: Int, mediumMask: Int) -> SkanCoarse {
        if fineValue & highMask != 0 {
            return .high
        }
        if fineValue & mediumMask != 0 {
            return .medium
        }
        return .low
    }

    static func validated(_ value: Double, name: String) throws -> Int {
        guard value.isFinite, value.rounded() == value, value >= 0, value <= Double(maxValue) else {
            throw SkanConversionError.invalidValue(name)
        }
        return Int(value)
    }

    /// Completion runs on the store's private queue.
    func mark(
        bits: Double,
        highMask: Double,
        mediumMask: Double,
        completion: @escaping (Result<SkanMergeOutcome, Error>) -> Void
    ) {
        queue.async { [self] in
            completion(Result { try markOnQueue(bits: bits, highMask: highMask, mediumMask: mediumMask) })
        }
    }

    private func markOnQueue(bits: Double, highMask: Double, mediumMask: Double) throws -> SkanMergeOutcome {
        let bits = try Self.validated(bits, name: "bits")
        let highMask = try Self.validated(highMask, name: "coarseRule.highMask")
        let mediumMask = try Self.validated(mediumMask, name: "coarseRule.mediumMask")

        let hasReported = defaults.object(forKey: Self.fineValueKey) != nil
        let previous = defaults.integer(forKey: Self.fineValueKey) & Self.maxValue
        let fineValue = previous | bits
        let coarse = Self.coarse(for: fineValue, highMask: highMask, mediumMask: mediumMask)
        let previousCoarse = defaults.string(forKey: Self.coarseValueKey)
        if hasReported && fineValue == previous && previousCoarse == coarse.rawValue {
            return SkanMergeOutcome(fineValue: fineValue, coarse: coarse, updated: false)
        }

        // The completion handler arrives on a StoreKit queue, never this one,
        // so waiting here keeps later calls ordered behind this report.
        let semaphore = DispatchSemaphore(value: 0)
        var reportError: Error?
        reporter(fineValue, coarse) { error in
            reportError = error
            semaphore.signal()
        }
        guard semaphore.wait(timeout: .now() + reportTimeout) == .success else {
            throw SkanConversionError.timedOut
        }
        if let reportError {
            if let skanError = reportError as? SkanConversionError {
                throw skanError
            }
            throw SkanConversionError.reportFailed(reportError.localizedDescription)
        }

        defaults.set(fineValue, forKey: Self.fineValueKey)
        defaults.set(coarse.rawValue, forKey: Self.coarseValueKey)
        return SkanMergeOutcome(fineValue: fineValue, coarse: coarse, updated: true)
    }

    private static let systemReporter: Reporter = { fineValue, coarse, completion in
        guard #available(iOS 16.1, *) else {
            completion(SkanConversionError.unsupported)
            return
        }
        let coarseValue: SKAdNetwork.CoarseConversionValue
        switch coarse {
        case .low: coarseValue = .low
        case .medium: coarseValue = .medium
        case .high: coarseValue = .high
        }
        // lockWindow stays false: TikTok asks advertisers not to lock windows early.
        SKAdNetwork.updatePostbackConversionValue(
            fineValue,
            coarseValue: coarseValue,
            lockWindow: false,
            completionHandler: completion
        )
    }
}
