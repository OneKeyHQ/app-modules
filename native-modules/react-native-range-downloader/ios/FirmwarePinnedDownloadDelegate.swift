import Foundation

enum FirmwarePinnedDownloadDelegate {
  static func stream(
    session: URLSession,
    request: URLRequest,
    destination: URL,
    append: Bool,
    maxBytes: Int64,
    deadlineAt: TimeInterval,
    validateResponse: (HTTPURLResponse) throws -> Void,
    onBytes: @escaping (Int64) -> Void
  ) async throws -> HTTPURLResponse {
    try Task.checkCancellation()
    try checkDeadline(deadlineAt)
    let existingSize = append ? destination.fileSize : 0
    let (bytes, response) = try await session.bytes(for: request)
    try Task.checkCancellation()
    try checkDeadline(deadlineAt)
    guard let http = response as? HTTPURLResponse else {
      throw FirmwareArtifactTransferError.protocolInvalid(
        "Pinned request returned no HTTP response"
      )
    }
    try validateResponse(http)
    if !FileManager.default.fileExists(atPath: destination.path) {
      FileManager.default.createFile(atPath: destination.path, contents: nil)
    }
    let handle = try FileHandle(forWritingTo: destination)
    defer { try? handle.close() }
    if append {
      try handle.seekToEnd()
    } else {
      try handle.truncate(atOffset: 0)
    }

    var written = existingSize
    var buffer = Data()
    buffer.reserveCapacity(bufferSize)
    for try await byte in bytes {
      try Task.checkCancellation()
      try checkDeadline(deadlineAt)
      buffer.append(byte)
      if buffer.count >= bufferSize {
        written += Int64(buffer.count)
        guard written <= maxBytes else {
          throw FirmwareArtifactTransferError.sizeLimitExceeded
        }
        try handle.write(contentsOf: buffer)
        onBytes(Int64(buffer.count))
        buffer.removeAll(keepingCapacity: true)
      }
    }
    if !buffer.isEmpty {
      written += Int64(buffer.count)
      guard written <= maxBytes else {
        throw FirmwareArtifactTransferError.sizeLimitExceeded
      }
      try handle.write(contentsOf: buffer)
      onBytes(Int64(buffer.count))
    }
    try handle.synchronize()
    return http
  }

  private static let bufferSize = 64 * 1024

  private static func checkDeadline(_ deadlineAt: TimeInterval) throws {
    guard Date().timeIntervalSince1970 < deadlineAt else {
      throw FirmwareArtifactTransferError.deadlineExceeded
    }
  }
}
