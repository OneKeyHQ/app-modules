// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

protocol ImageDownloadTask {
    func resume()
    func cancel()
}
protocol ImageDownloadSession {
    func imageDataTask(with url: URL, completionHandler: @escaping @Sendable (Data?, URLResponse?, Error?) -> Void) -> ImageDownloadTask
}
extension URLSessionDataTask: ImageDownloadTask {}
extension URLSession: ImageDownloadSession {
    func imageDataTask(with url: URL, completionHandler: @escaping @Sendable (Data?, URLResponse?, Error?) -> Void) -> ImageDownloadTask {
        dataTask(with: url, completionHandler: completionHandler)
    }
}

final class ImageDownloader {
    typealias Completion = (Data?) -> Void
    private struct Request {
        let id: UUID
        var task: ImageDownloadTask?
        var completions: [UUID: Completion]
        var retryCount: Int
    }
    private struct Attempt {
        let requestID: UUID
        let key: String
        let url: URL
    }
    private let session: ImageDownloadSession
    private let queue: DispatchQueue
    private let maxRetries: Int
    private let retryDelay: (Int) -> Double
    private var inFlight: [String: Request] = [:]
    private var tokenToKey: [UUID: String] = [:]

    init(session: ImageDownloadSession, queue: DispatchQueue = DispatchQueue(label: "io.intercom.ImageDownloader"), maxRetries: Int = 3, retryDelay: @escaping (Int) -> Double = { exp2(Double($0) * 2) }) {
        self.session = session
        self.queue = queue
        self.maxRetries = maxRetries
        self.retryDelay = retryDelay
    }

    @discardableResult
    func download(url: URL, key: String, preflight: (() -> Data?)?, completion: @escaping Completion) -> UUID {
        let token = UUID()
        queue.async { [weak self] in
            guard let self else { completion(nil); return }
            if var request = self.inFlight[key] {
                request.completions[token] = completion
                self.inFlight[key] = request
                self.tokenToKey[token] = key
                return
            }
            let requestID = UUID()
            let data = preflight?()
            self.inFlight[key] = Request(id: requestID, task: nil, completions: [token: completion], retryCount: 0)
            self.tokenToKey[token] = key
            let attempt = Attempt(requestID: requestID, key: key, url: url)
            if let data {
                self.queue.async { [weak self] in self?.finish(attempt, data: data, response: nil, error: nil) }
            } else {
                self.startTask(attempt)
            }
        }
        return token
    }

    private func startTask(_ attempt: Attempt) {
        let task = session.imageDataTask(with: attempt.url) { [weak self] data, response, error in
            self?.handleTaskResult(attempt, data: data, response: response, error: error)
        }
        inFlight[attempt.key]?.task = task
        task.resume()
    }

    private func handleTaskResult(_ attempt: Attempt, data: Data?, response: URLResponse?, error: Error?) {
        queue.async { [weak self] in
            guard let self, var request = self.inFlight[attempt.key], request.id == attempt.requestID else { return }
            if let valid = Self.validData(data, response: response, error: error) {
                self.fanOut(key: attempt.key, data: valid)
                return
            }
            if request.retryCount <= 2 && Self.isRetriable(response: response, error: error) {
                let delay = self.retryDelay(request.retryCount)
                request.retryCount += 1
                self.inFlight[attempt.key] = request
                self.scheduleRetry(attempt, delay: delay)
                return
            }
            self.fanOut(key: attempt.key, data: nil)
        }
    }

    private func scheduleRetry(_ attempt: Attempt, delay: Double) {
        let work = { [weak self] in
            guard let self, self.inFlight[attempt.key]?.id == attempt.requestID else { return }
            self.startTask(attempt)
        }
        if delay <= 0 { queue.async { work() } }
        else { queue.asyncAfter(deadline: .now() + delay) { work() } }
    }

    private func finish(_ attempt: Attempt, data: Data?, response: URLResponse?, error: Error?) {
        guard inFlight[attempt.key]?.id == attempt.requestID else { return }
        fanOut(key: attempt.key, data: Self.validData(data, response: response, error: error))
    }

    private func fanOut(key: String, data: Data?) {
        guard let request = inFlight.removeValue(forKey: key) else { return }
        for token in request.completions.keys { tokenToKey.removeValue(forKey: token) }
        for completion in request.completions.values { completion(data) }
    }

    func cancel(_ token: UUID) {
        queue.async { [weak self] in
            guard let self, let key = self.tokenToKey.removeValue(forKey: token), var request = self.inFlight[key] else { return }
            request.completions.removeValue(forKey: token)
            if !request.completions.isEmpty { self.inFlight[key] = request }
            else {
                request.task?.cancel()
                self.inFlight.removeValue(forKey: key)
            }
        }
    }

    private static func validData(_ data: Data?, response: URLResponse?, error: Error?) -> Data? {
        guard error == nil, let data, !data.isEmpty else { return nil }
        if let http = response as? HTTPURLResponse, !(200...299).contains(http.statusCode) { return nil }
        return data
    }

    private static func isRetriable(response: URLResponse?, error: Error?) -> Bool {
        if let error {
            if let urlError = error as? URLError, urlError.code == .unsupportedURL || urlError.code == .cancelled { return false }
            return true
        }
        guard let http = response as? HTTPURLResponse else { return false }
        return (500...599).contains(http.statusCode)
    }
}
