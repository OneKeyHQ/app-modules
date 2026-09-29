import Foundation

/// A per-view hysteresis observer. A nil result means no bridge event is needed.
struct NativeListScrollPositionTracker {
  private var start: Double?
  private var end: Double?
  private var previous: Bool?

  mutating func configure(json: String) {
    start = nil
    end = nil
    previous = nil
    guard let data = json.data(using: .utf8),
          let values = try? JSONSerialization.jsonObject(with: data) as? [String: Double],
          let start = values["start"], let end = values["end"],
          start.isFinite, end.isFinite, start >= 0, end > start else { return }
    self.start = start
    self.end = end
  }

  mutating func resetDelivery() { previous = nil }

  mutating func update(offset: Double) -> Bool? {
    guard let start, let end, offset.isFinite else { return nil }
    let next = previous == true ? offset > start : offset >= end
    guard previous != next else { return nil }
    previous = next
    return next
  }
}
