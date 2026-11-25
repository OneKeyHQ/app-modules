import Foundation

// C ABI used by the Rust N-API layer (src/lib.rs). Strings returned to Rust
// are malloc'd and must be freed with oqs_free_string.

public typealias OqsEventCallback = @convention(c) (
  UnsafeMutableRawPointer?, Int32, UnsafePointer<UInt8>?, Int, UnsafePointer<CChar>, Int64
) -> Void

/// (context, token, IOSurfaceRef, width, height, generation)
public typealias OqsFrameCallback = @convention(c) (
  UnsafeMutableRawPointer?, Int64, UnsafeMutableRawPointer, Int32, Int32, Int64
) -> Void

/// Engine bitmask shared with src/lib.rs: 1 Vision, 2 ZXing-C++.
private func makePipeline(engines: Int32) -> QrImageDecodingPipeline {
  var list: [QrImageEngine] = []
  if engines & 1 != 0 { list.append(VisionQrEngine()) }
  if engines & 2 != 0 { list.append(ZXingQrEngine()) }
  return QrImageDecodingPipeline(engines: list)
}
private var pipelines: [Int32: QrImageDecodingPipeline] = [:]
private let imageLock = NSLock()

/// 1: found (outText set), 0: no code, -1: error (outError set, "<code>: <reason>").
@_cdecl("oqs_decode_image")
public func oqs_decode_image(
  _ path: UnsafePointer<CChar>, _ maxPixels: Int64, _ engines: Int32,
  _ outText: UnsafeMutablePointer<UnsafeMutablePointer<UInt8>?>,
  _ outLength: UnsafeMutablePointer<Int>,
  _ outError: UnsafeMutablePointer<UnsafeMutablePointer<CChar>?>
) -> Int32 {
  let url = URL(fileURLWithPath: String(cString: path))
  // One decode at a time: each holds several full-size bitmaps.
  imageLock.lock()
  defer { imageLock.unlock() }
  let pipeline = pipelines[engines] ?? makePipeline(engines: engines)
  pipelines[engines] = pipeline
  do {
    guard let hit = try pipeline.decode(url: url, maxPixels: Int(maxPixels)) else {
      return 0
    }
    let bytes = Array(hit.text.utf8)
    guard let buffer = malloc(bytes.count)?.assumingMemoryBound(to: UInt8.self) else {
      outError.pointee = strdup("resource-limit: could not allocate QR text")
      return -1
    }
    bytes.withUnsafeBufferPointer {
      if let base = $0.baseAddress { buffer.initialize(from: base, count: $0.count) }
    }
    outText.pointee = buffer
    outLength.pointee = bytes.count
    return 1
  } catch let error as QrImageDecodeError {
    outError.pointee = strdup(error.codedMessage)
    return -1
  } catch {
    outError.pointee = strdup("invalid-image: \(error.localizedDescription)")
    return -1
  }
}

/// 1: ok (outJson set to `{width, height, symbols}`), -1: error (outError set).
@_cdecl("oqs_decode_image_symbols")
public func oqs_decode_image_symbols(
  _ path: UnsafePointer<CChar>, _ maxPixels: Int64, _ engines: Int32,
  _ outJson: UnsafeMutablePointer<UnsafeMutablePointer<UInt8>?>,
  _ outLength: UnsafeMutablePointer<Int>,
  _ outError: UnsafeMutablePointer<UnsafeMutablePointer<CChar>?>
) -> Int32 {
  let url = URL(fileURLWithPath: String(cString: path))
  imageLock.lock()
  defer { imageLock.unlock() }
  let pipeline = pipelines[engines] ?? makePipeline(engines: engines)
  pipelines[engines] = pipeline
  do {
    let hit = try pipeline.decodeSymbols(url: url, maxPixels: Int(maxPixels))
    let symbols: [[String: Any]] = hit.symbols.map { sym in
      var item: [String: Any] = [
        "text": sym.text,
        "centerX": sym.centerX,
        "centerY": sym.centerY,
      ]
      if let x = sym.boundsX, let y = sym.boundsY,
        let w = sym.boundsWidth, let h = sym.boundsHeight
      {
        item["bounds"] = ["x": x, "y": y, "width": w, "height": h]
      }
      return item
    }
    let payload: [String: Any] = [
      "width": hit.width,
      "height": hit.height,
      "symbols": symbols,
    ]
    let data = try JSONSerialization.data(withJSONObject: payload)
    let bytes = Array(data)
    guard let buffer = malloc(bytes.count)?.assumingMemoryBound(to: UInt8.self) else {
      outError.pointee = strdup("resource-limit: could not allocate symbols JSON")
      return -1
    }
    bytes.withUnsafeBufferPointer {
      if let base = $0.baseAddress { buffer.initialize(from: base, count: $0.count) }
    }
    outJson.pointee = buffer
    outLength.pointee = bytes.count
    return 1
  } catch let error as QrImageDecodeError {
    outError.pointee = strdup(error.codedMessage)
    return -1
  } catch {
    outError.pointee = strdup("invalid-image: \(error.localizedDescription)")
    return -1
  }
}

/// JSON array of {id, position, label}; free with oqs_free_string.
@_cdecl("oqs_list_cameras")
public func oqs_list_cameras() -> UnsafeMutablePointer<CChar>? {
  let cameras = QrCameraSource.availableCameras().map {
    ["id": $0.id, "position": $0.position, "label": $0.label]
  }
  guard let data = try? JSONSerialization.data(withJSONObject: cameras),
    let json = String(data: data, encoding: .utf8)
  else { return strdup("[]") }
  return strdup(json)
}

@_cdecl("oqs_free_bytes")
public func oqs_free_bytes(_ value: UnsafeMutablePointer<UInt8>?) {
  free(value)
}

@_cdecl("oqs_free_string")
public func oqs_free_string(_ value: UnsafeMutablePointer<CChar>?) {
  free(value)
}

/// sourceKind 0: camera (`path`: optional device id), 1: video file at `path`. `frameCallback` is optional;
/// when set, every lent frame must be returned with oqs_capture_release_frame.
@_cdecl("oqs_capture_create")
public func oqs_capture_create(
  _ context: UnsafeMutableRawPointer?, _ callback: OqsEventCallback,
  _ frameCallback: OqsFrameCallback?,
  _ sourceKind: Int32, _ path: UnsafePointer<CChar>?, _ loop: Bool
) -> UnsafeMutableRawPointer {
  let filePath = path.map { String(cString: $0) }
  let controller = QrCaptureController(
    usesCamera: sourceKind == 0,
    makeSource: { queue in
      if sourceKind == 1, let filePath {
        return QrFileSource(url: URL(fileURLWithPath: filePath), loop: loop, queue: queue)
      }
      // For a camera, `path` carries the optional device id.
      return QrCameraSource(queue: queue, deviceId: filePath)
    },
    emit: { kind, a, b, generation in
      Array(a.utf8).withUnsafeBufferPointer { first in
        b.withCString { second in
          callback(context, kind.rawValue, first.baseAddress, first.count, second, Int64(generation))
        }
      }
    },
    emitFrame: frameCallback.map { frameCallback in
      { token, surface, width, height, generation in
        frameCallback(
          context, token, surface, Int32(width), Int32(height), Int64(generation))
      }
    })
  return Unmanaged.passRetained(controller).toOpaque()
}

@_cdecl("oqs_capture_release_frame")
public func oqs_capture_release_frame(_ handle: UnsafeMutableRawPointer, _ token: Int64) {
  Unmanaged<QrCaptureController>.fromOpaque(handle).takeUnretainedValue()
    .previewPool.release(token)
}

@_cdecl("oqs_capture_lent_frames")
public func oqs_capture_lent_frames(_ handle: UnsafeMutableRawPointer) -> Int32 {
  Int32(
    Unmanaged<QrCaptureController>.fromOpaque(handle).takeUnretainedValue()
      .previewPool.lentCount)
}

@_cdecl("oqs_capture_start")
public func oqs_capture_start(_ handle: UnsafeMutableRawPointer) -> Int64 {
  Int64(Unmanaged<QrCaptureController>.fromOpaque(handle).takeUnretainedValue().start())
}

@_cdecl("oqs_capture_stop")
public func oqs_capture_stop(_ handle: UnsafeMutableRawPointer) {
  Unmanaged<QrCaptureController>.fromOpaque(handle).takeUnretainedValue().stop()
}

@_cdecl("oqs_capture_set_multi_symbol")
public func oqs_capture_set_multi_symbol(_ handle: UnsafeMutableRawPointer, _ enabled: Bool) {
  Unmanaged<QrCaptureController>.fromOpaque(handle).takeUnretainedValue().setMultiSymbol(enabled)
}

@_cdecl("oqs_capture_set_paused")
public func oqs_capture_set_paused(_ handle: UnsafeMutableRawPointer, _ paused: Bool) {
  Unmanaged<QrCaptureController>.fromOpaque(handle).takeUnretainedValue().setPaused(paused)
}

@_cdecl("oqs_capture_focus")
public func oqs_capture_focus(_ handle: UnsafeMutableRawPointer, _ x: Double, _ y: Double) -> Bool {
  Unmanaged<QrCaptureController>.fromOpaque(handle).takeUnretainedValue().focus(x: x, y: y)
}

/// Stops capture; no callback runs after this returns. Releases the handle.
@_cdecl("oqs_capture_destroy")
public func oqs_capture_destroy(_ handle: UnsafeMutableRawPointer) {
  let controller = Unmanaged<QrCaptureController>.fromOpaque(handle)
  controller.takeUnretainedValue().destroy()
  controller.release()
}
