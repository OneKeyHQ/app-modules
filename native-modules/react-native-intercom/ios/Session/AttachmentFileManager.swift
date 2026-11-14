import Foundation

protocol URLSessionDownloadTaskProtocol { func resume() }
extension URLSessionDownloadTask: URLSessionDownloadTaskProtocol {}

protocol URLSessionProtocol {
    func downloadTask(with url: URL, completionHandler: @escaping @Sendable (URL?, URLResponse?, Error?) -> Void) -> any URLSessionDownloadTaskProtocol
}
extension URLSession: URLSessionProtocol {
    func downloadTask(with url: URL, completionHandler: @escaping @Sendable (URL?, URLResponse?, Error?) -> Void) -> any URLSessionDownloadTaskProtocol {
        let task: URLSessionDownloadTask = downloadTask(with: url, completionHandler: completionHandler)
        return task
    }
}

enum AttachmentFileManagerError: Error { case downloadFailed, notLocalFileURL, imageConversionFailed }

// The separate three-requirement private FileManager protocol was WMO-deleted.
// This concrete implementation follows the surviving direct PreviewHelper calls.
class AttachmentFileManager {
    let fileManager: FileManager
    private let urlSession: any URLSessionProtocol
    private let callbackQueue: DispatchQueue
    init(fileManager: FileManager, urlSession: any URLSessionProtocol, callbackQueue: DispatchQueue) {
        self.fileManager = fileManager
        self.urlSession = urlSession
        self.callbackQueue = callbackQueue
    }

    func saveLocalCopyOfFiles(at urls: [URL], completion: @escaping (Result<[URL], Error>) -> Void) {
        let group = DispatchGroup()
        var savedURLs: [URL] = []
        var lastError: Error?
        for url in urls {
            group.enter()
            let finished: (Result<URL, Error>) -> Void = { result in
                switch result {
                case .success(let saved): savedURLs.append(saved)
                case .failure(let error): lastError = error
                }
                group.leave()
            }
            let destination = fileManager.temporaryDirectory.appendingPathComponent(url.lastPathComponent)
            if fileManager.fileExists(atPath: destination.path) {
                savedURLs.append(destination)
                group.leave()
            } else if url.isFileURL {
                copyLocalFile(from: url, to: destination, completion: finished)
            } else {
                downloadFile(from: url, to: destination, completion: finished)
            }
        }
        group.notify(queue: callbackQueue) {
            if let error = lastError { completion(.failure(error)) }
            else { completion(.success(savedURLs)) }
        }
    }

    func copyLocalFile(from source: URL, to destination: URL, completion: (Result<URL, Error>) -> Void) {
        let accessed = source.startAccessingSecurityScopedResource()
        do {
            if fileManager.fileExists(atPath: destination.path) { try fileManager.removeItem(at: destination) }
            try fileManager.copyItem(at: source, to: destination)
            completion(.success(destination))
            if accessed { source.stopAccessingSecurityScopedResource() }
        } catch {
            if accessed { source.stopAccessingSecurityScopedResource() }
            completion(.failure(error))
        }
    }

    private func downloadFile(from source: URL, to destination: URL, completion: @escaping (Result<URL, Error>) -> Void) {
        let task = urlSession.downloadTask(with: source) { temporaryURL, _, error in
            if let error = error {
                self.callbackQueue.async { completion(.failure(error)) }
            } else if let temporaryURL = temporaryURL {
                do {
                    try self.fileManager.moveItem(at: temporaryURL, to: destination)
                    self.callbackQueue.async { completion(.success(destination)) }
                } catch {
                    self.callbackQueue.async { completion(.failure(error)) }
                }
            } else {
                self.callbackQueue.async { completion(.failure(AttachmentFileManagerError.downloadFailed)) }
            }
        }
        task.resume()
    }
}
