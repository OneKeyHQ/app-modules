import Foundation

struct MemorySample: Codable {
  let elapsedMilliseconds: Int
  let rssBytes: Int64
}

struct MemoryReport: Codable {
  let schemaVersion: Int
  let platform: String
  let label: String
  let pid: Int32
  let intervalMilliseconds: Int
  let baselineBytes: Int64
  let peakBytes: Int64
  let peakDeltaBytes: Int64
  let samples: [MemorySample]
}

enum ProbeError: Error, LocalizedError {
  case invalidArguments
  case processUnavailable

  var errorDescription: String? {
    switch self {
    case .invalidArguments:
      return "usage: memory-probe-ios <pid> <label> <duration-seconds> <output-json> [interval-ms]"
    case .processUnavailable:
      return "target process is unavailable"
    }
  }
}

func residentBytes(pid: Int32) throws -> Int64 {
  let process = Process()
  process.executableURL = URL(fileURLWithPath: "/bin/ps")
  process.arguments = ["-o", "rss=", "-p", String(pid)]
  let pipe = Pipe()
  process.standardOutput = pipe
  process.standardError = FileHandle.nullDevice
  try process.run()
  process.waitUntilExit()
  guard process.terminationStatus == 0,
        let text = String(
          data: pipe.fileHandleForReading.readDataToEndOfFile(),
          encoding: .utf8
        ),
        let kibibytes = Int64(text.trimmingCharacters(in: .whitespacesAndNewlines))
  else {
    throw ProbeError.processUnavailable
  }
  return kibibytes * 1024
}

func median(_ values: [Int64]) -> Int64 {
  let sorted = values.sorted()
  guard !sorted.isEmpty else { return 0 }
  return sorted[sorted.count / 2]
}

func main() throws {
  let arguments = CommandLine.arguments
  guard arguments.count == 5 || arguments.count == 6,
        let pid = Int32(arguments[1]),
        let duration = Double(arguments[3]),
        duration > 0 else {
    throw ProbeError.invalidArguments
  }
  let intervalMilliseconds = arguments.count == 6
    ? max(20, Int(arguments[5]) ?? 100)
    : 100
  let started = Date()
  let deadline = started.addingTimeInterval(duration)
  var samples: [MemorySample] = []
  repeat {
    let rss = try residentBytes(pid: pid)
    samples.append(MemorySample(
      elapsedMilliseconds: Int(Date().timeIntervalSince(started) * 1_000),
      rssBytes: rss
    ))
    Thread.sleep(forTimeInterval: Double(intervalMilliseconds) / 1_000)
  } while Date() < deadline

  let baselineWindow = Array(samples.prefix(max(1, min(10, samples.count))))
  let baseline = median(baselineWindow.map(\.rssBytes))
  let peak = samples.map(\.rssBytes).max() ?? baseline
  let report = MemoryReport(
    schemaVersion: 1,
    platform: "ios",
    label: arguments[2],
    pid: pid,
    intervalMilliseconds: intervalMilliseconds,
    baselineBytes: baseline,
    peakBytes: peak,
    peakDeltaBytes: max(0, peak - baseline),
    samples: samples
  )
  let data = try JSONEncoder().encode(report)
  try data.write(
    to: URL(fileURLWithPath: arguments[4]),
    options: .atomic
  )
}

do {
  try main()
} catch {
  FileHandle.standardError.write(
    Data("\(error.localizedDescription)\n".utf8)
  )
  exit(2)
}
