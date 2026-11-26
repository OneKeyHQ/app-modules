import Foundation
import StoreKit

enum SkanCoarse {
    case low
    case medium
    case high
}

struct SkanMergeOutcome {
    let fineValue: Int
    let coarse: SkanCoarse
}

enum SkanConversionError: Error, Equatable, LocalizedError {
    case invalidValue(String)
    case unsupported
    case timedOut
    case reportInProgress
    case reportFailed(String)

    var errorDescription: String? {
        switch self {
        case .invalidValue(let name):
            return "\(name) must be an integer from 0 to 63"
        case .unsupported:
            return "SKAdNetwork conversion values need iOS 16.1 or later"
        case .timedOut:
            return "SKAdNetwork did not answer the conversion value update"
        case .reportInProgress:
            return "SKAdNetwork has not completed the previous conversion value update"
        case .reportFailed(let message):
            return "SKAdNetwork rejected the conversion value update: \(message)"
        }
    }
}

/// Merges milestone bits into the SKAdNetwork fine conversion value.
///
/// Main and background React runtimes each create their own HybridObject, so
/// the shared instance and its serial queue are process-wide. The queue keeps
/// read, merge, report and persist in order so each call includes the bits
/// from prior successful reports.
final class SkanConversionStore {
    typealias Reporter = (
        _ fineValue: Int,
        _ coarse: SkanCoarse,
        _ completion: @escaping (Error?) -> Void
    ) -> Void

    static let fineValueKey = "so.onekey.deviceutils.skan.fineValue"
    static let maxValue = 63

    static let shared = SkanConversionStore(
        defaults: .standard,
        reporter: SkanConversionStore.systemReporter
    )

    private let defaults: UserDefaults
    private let reporter: Reporter
    private let reportTimeout: DispatchTimeInterval
    private let queue = DispatchQueue(label: "so.onekey.deviceutils.skan", qos: .utility)
    private var timedOutReport: DispatchSemaphore?

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

        guard timedOutReport == nil else {
            throw SkanConversionError.reportInProgress
        }

        let previous = defaults.integer(forKey: Self.fineValueKey) & Self.maxValue
        let fineValue = previous | bits
        let coarse = Self.coarse(for: fineValue, highMask: highMask, mediumMask: mediumMask)

        // An unchanged value can be the first update in a later StoreKit window.
        // The completion handler arrives on a StoreKit queue, never this one,
        // so waiting here keeps later calls ordered behind this report.
        let semaphore = DispatchSemaphore(value: 0)
        var reportError: Error?
        reporter(fineValue, coarse) { [weak self] error in
            reportError = error
            semaphore.signal()
            self?.queue.async { [weak self] in
                guard let self, self.timedOutReport === semaphore else { return }
                if error == nil {
                    self.defaults.set(fineValue, forKey: Self.fineValueKey)
                }
                self.timedOutReport = nil
            }
        }
        guard semaphore.wait(timeout: .now() + reportTimeout) == .success else {
            // StoreKit has no cancellation API. Keep subsequent reports gated
            // until the callback reconciles the accepted value on this queue.
            timedOutReport = semaphore
            throw SkanConversionError.timedOut
        }
        if let reportError {
            if let skanError = reportError as? SkanConversionError {
                throw skanError
            }
            throw SkanConversionError.reportFailed(reportError.localizedDescription)
        }

        defaults.set(fineValue, forKey: Self.fineValueKey)
        return SkanMergeOutcome(fineValue: fineValue, coarse: coarse)
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
