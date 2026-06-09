import Foundation
import NitroModules
import SSZipArchive

/// Nitro HybridObject implementation of the OneKey zip-archive module.
///
/// This is a 1:1 port of the previous Objective-C `ZipArchive.mm` TurboModule.
/// The underlying archive engine is unchanged: it still wraps `SSZipArchive`
/// (`unzipFileAtPath:` / `createZipFileAtPath:` / `payloadSizeForArchiveAtPath:`)
/// so the on-disk extraction/compression behaviour stays byte-for-byte identical.
final class ReactNativeZipArchive: HybridReactNativeZipArchiveSpec {

  // MARK: - isPasswordProtected

  func isPasswordProtected(file: String) throws -> Promise<Bool> {
    return Promise.async {
      return SSZipArchive.isFilePasswordProtected(atPath: file)
    }
  }

  // MARK: - unzip

  func unzip(from: String, to: String) throws -> Promise<String> {
    return Promise.async {
      var error: NSError?
      let success = SSZipArchive.unzipFile(
        atPath: from,
        toDestination: to,
        preserveAttributes: false,
        overwrite: true,
        password: nil,
        error: &error,
        delegate: nil
      )

      if success {
        return to
      }
      // Preserve the underlying NSError (domain + code) so callers can tell a
      // corrupt archive apart from a wrong password or an I/O failure. Only fall
      // back to the generic "ZipArchive" error when SSZipArchive reported failure
      // without populating an error.
      throw error ?? NSError(
        domain: "ZipArchive",
        code: -1,
        userInfo: [NSLocalizedDescriptionKey: "unable to unzip"]
      )
    }
  }

  // MARK: - unzipWithPassword

  func unzipWithPassword(from: String, to: String, password: String) throws -> Promise<String> {
    return Promise.async {
      var error: NSError?
      let success = SSZipArchive.unzipFile(
        atPath: from,
        toDestination: to,
        preserveAttributes: false,
        overwrite: true,
        password: password,
        error: &error,
        delegate: nil
      )

      if success {
        return to
      }
      // Preserve the underlying NSError (domain + code) so callers can tell a
      // corrupt archive apart from a wrong password or an I/O failure. Only fall
      // back to the generic "ZipArchive" error when SSZipArchive reported failure
      // without populating an error.
      throw error ?? NSError(
        domain: "ZipArchive",
        code: -1,
        userInfo: [NSLocalizedDescriptionKey: "unable to unzip"]
      )
    }
  }

  // MARK: - zipFolder

  func zipFolder(from: String, to: String) throws -> Promise<String> {
    return Promise.async {
      let success = SSZipArchive.createZipFile(
        atPath: to,
        withContentsOfDirectory: from,
        keepParentDirectory: false,
        withPassword: nil,
        andProgressHandler: nil
      )

      if success {
        return to
      }
      throw NSError(
        domain: "ZipArchive",
        code: -1,
        userInfo: [NSLocalizedDescriptionKey: "unable to zip"]
      )
    }
  }

  // MARK: - zipFiles

  func zipFiles(files: [String], to: String) throws -> Promise<String> {
    return Promise.async {
      let success = SSZipArchive.createZipFile(atPath: to, withFilesAtPaths: files)

      if success {
        return to
      }
      throw NSError(
        domain: "ZipArchive",
        code: -1,
        userInfo: [NSLocalizedDescriptionKey: "unable to zip"]
      )
    }
  }

  // MARK: - getUncompressedSize

  func getUncompressedSize(path: String) throws -> Promise<Double> {
    return Promise.async {
      // Swift imports `+payloadSizeForArchiveAtPath:error:` as
      // payloadSizeForArchive(atPath:error:) -> NSNumber (non-throwing, keeps the
      // NSErrorPointer because the return is unannotated/non-nullable).
      var error: NSError?
      let wantedFileSize = SSZipArchive.payloadSizeForArchive(atPath: path, error: &error)
      if let error {
        // Contract: return -1 on failure. Log the underlying error so a missing
        // file can be told apart from a corrupt archive when investigating.
        NSLog("[ZipArchive] getUncompressedSize failed for \(path): \(error)")
        return -1
      }
      return wantedFileSize.doubleValue
    }
  }
}
