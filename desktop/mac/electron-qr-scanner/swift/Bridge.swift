import Foundation

// C ABI used by the Rust N-API layer (src/lib.rs). Strings returned to Rust
// are malloc'd and must be freed with oqs_free_string.

public typealias OqsEventCallback = @convention(c) (
  UnsafeMutableRawPointer?, Int32, UnsafePointer<CChar>, UnsafePointer<CChar>, Int64
) -> Void

/// (context, token, IOSurfaceRef, width, height, generation)
public typealias OqsFrameCallback = @convention(c) (
  UnsafeMutableRawPointer?, Int64, UnsafeMutableRawPointer, Int32, Int32, Int64
) -> Void

private let imagePipeline = QrImageDecodingPipeline(engines: [VisionQrEngine()])
private let imageLock = NSLock()

/// 1: found (outText set), 0: no code, -1: error (outError set, "<code>: <reason>").
@_cdecl("oqs_decode_image")
public func oqs_decode_image(
  _ path: UnsafePointer<CChar>, _ maxPixels: Int64,
  _ outText: UnsafeMutablePointer<UnsafeMutablePointer<CChar>?>,
  _ outError: UnsafeMutablePointer<UnsafeMutablePointer<CChar>?>
) -> Int32 {
  let url = URL(fileURLWithPath: String(cString: path))
  // One decode at a time: each holds several full-size bitmaps.
  imageLock.lock()
  defer { imageLock.unlock() }
  do {
    guard let hit = try imagePipeline.decode(url: url, maxPixels: Int(maxPixels)) else {
      return 0
    }
    outText.pointee = strdup(hit.text)
    return 1
  } catch let error as QrImageDecodeError {
    outError.pointee = strdup(error.codedMessage)
    return -1
  } catch {
    outError.pointee = strdup("invalid-image: \(error.localizedDescription)")
    return -1
  }
}

@_cdecl("oqs_free_string")
public func oqs_free_string(_ value: UnsafeMutablePointer<CChar>?) {
  free(value)
}

/// sourceKind 0: camera, 1: video file at `path`. `frameCallback` is optional;
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
      return QrCameraSource(queue: queue, deviceId: nil)
    },
    emit: { kind, a, b, generation in
      a.withCString { first in
        b.withCString { second in
          callback(context, kind.rawValue, first, second, Int64(generation))
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
public func oqs_capture_start(_ handle: UnsafeMutableRawPointer) {
  Unmanaged<QrCaptureController>.fromOpaque(handle).takeUnretainedValue().start()
}

@_cdecl("oqs_capture_stop")
public func oqs_capture_stop(_ handle: UnsafeMutableRawPointer) {
  Unmanaged<QrCaptureController>.fromOpaque(handle).takeUnretainedValue().stop()
}

/// Stops capture; no callback runs after this returns. Releases the handle.
@_cdecl("oqs_capture_destroy")
public func oqs_capture_destroy(_ handle: UnsafeMutableRawPointer) {
  let controller = Unmanaged<QrCaptureController>.fromOpaque(handle)
  controller.takeUnretainedValue().destroy()
  controller.release()
}
