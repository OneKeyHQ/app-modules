import Foundation

/// Decides which detected texts the live view reports to JS. Thread-safe:
/// the session queue starts and stops capture while the metadata queue
/// reports detections.
///
/// - Nothing is reported while capture is stopped.
/// - Every start and stop begins a new generation; reports carry it so JS can
///   drop late events from an older one.
/// - The same text is reported at most once per `repeatInterval` within a
///   generation. Animated (multi-part) codes change text every frame, so each
///   new part passes immediately.
final class QrFrameTextGate {
  struct Report: Equatable {
    let text: String
    let generation: Int
  }

  let repeatInterval: TimeInterval
  private let lock = NSLock()
  private var generation = 0
  private var capturing = false
  private var lastText: String?
  private var lastTextAt: TimeInterval = 0

  init(repeatInterval: TimeInterval = 1.0) {
    self.repeatInterval = repeatInterval
  }

  /// Starts a new generation; capture stays stopped until `setCapturing`.
  @discardableResult
  func nextGeneration() -> Int {
    lock.lock()
    defer { lock.unlock() }
    generation += 1
    capturing = false
    lastText = nil
    return generation
  }

  var currentGeneration: Int {
    lock.lock()
    defer { lock.unlock() }
    return generation
  }

  func setCapturing(_ value: Bool) {
    lock.lock()
    capturing = value
    lock.unlock()
  }

  func resetRepeatFilter() {
    lock.lock()
    lastText = nil
    lock.unlock()
  }

  /// Texts from one detection callback, in order, filtered for reporting.
  func filter(_ texts: [String], now: TimeInterval, generation origin: Int? = nil) -> [Report] {
    lock.lock()
    defer { lock.unlock() }
    guard capturing, origin == nil || origin == generation else { return [] }
    var reports: [Report] = []
    for text in texts where !text.isEmpty {
      if text == lastText, now - lastTextAt < repeatInterval { continue }
      lastText = text
      lastTextAt = now
      reports.append(Report(text: text, generation: generation))
    }
    return reports
  }
}
