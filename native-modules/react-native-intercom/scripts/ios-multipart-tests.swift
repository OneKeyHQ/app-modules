import Foundation

private final class RejectNetwork: URLProtocol, @unchecked Sendable {
    override class func canInit(with request: URLRequest) -> Bool { true }
    override class func canonicalRequest(for request: URLRequest) -> URLRequest { request }
    override func startLoading() { fatalError("Multipart fixture must never resume a task") }
    override func stopLoading() {}
}

private final class RecordingSession: URLSession, @unchecked Sendable {
    let backend = URLSession(configuration: {
        let config = URLSessionConfiguration.ephemeral
        config.protocolClasses = [RejectNetwork.self]
        return config
    }())
    var capturedRequest: URLRequest?
    var capturedBody: Data?
    override func uploadTask(with request: URLRequest, from bodyData: Data?) -> URLSessionUploadTask {
        capturedRequest = request
        capturedBody = bodyData
        return backend.uploadTask(with: request, from: bodyData!)
    }
}

@main struct MultipartTests {
    static func main() {
        var count = 0
        func check(_ condition: @autoclosure () -> Bool) {
            precondition(condition(), "Multipart assertion \(count + 1)")
            count += 1
        }
        func boundary(_ request: URLRequest) -> String {
            String(request.value(forHTTPHeaderField: "Content-Type")!
                .dropFirst("multipart/form-data; boundary=".count))
        }
        let url = URL(string: "https://synthetic.invalid/upload?x=1")!
        let binary = Data([0, 255, 13, 10, 34])
        let request = MultipartTaskBuilder.createMultipartRequest(url: url,
            parameters: ["name": "雪\r\nvalue"], fileData: binary,
            fileName: "a\"\r\nb.bin", mimeType: "application/octet-stream",
            fieldName: "raw\"field", existingHeaders: ["Content-Type": "old", "Content-Length": "1", "X-Test": "fixture"])
        let delimiter = boundary(request)
        check(request.url == url)
        check(request.httpMethod == "POST")
        check(request.timeoutInterval == 60)
        check(request.cachePolicy == .useProtocolCachePolicy)
        check(delimiter.hasPrefix("Boundary-"))
        check(UUID(uuidString: String(delimiter.dropFirst(9))) != nil)
        check(request.value(forHTTPHeaderField: "X-Test") == "fixture")
        let body = request.httpBody!
        check(request.value(forHTTPHeaderField: "Content-Length") == String(body.count))
        var expected = Data("--\(delimiter)\r\nContent-Disposition: form-data; name=\"name\"\r\n\r\n雪\r\nvalue\r\n--\(delimiter)\r\nContent-Disposition: form-data; name=\"raw\"field\"; filename=\"a\"\r\nb.bin\"\r\nContent-Type: application/octet-stream\r\n\r\n".utf8)
        expected.append(binary)
        expected.append(contentsOf: "\r\n--\(delimiter)--\r\n".utf8)
        check(body == expected)
        let empty = MultipartTaskBuilder.createMultipartRequest(url: url, parameters: [:],
            fileData: Data(), fileName: "", mimeType: "", fieldName: "", existingHeaders: [:])
        let emptyBoundary = boundary(empty)
        check(empty.httpBody == Data("--\(emptyBoundary)\r\nContent-Disposition: form-data; name=\"\"; filename=\"\"\r\nContent-Type: \r\n\r\n\r\n--\(emptyBoundary)--\r\n".utf8))
        check(emptyBoundary != delimiter)
        // URLSession factory initializers return a private class; this fixture needs its own override.
        let session = RecordingSession.perform(NSSelectorFromString("new"))!.takeRetainedValue() as! RecordingSession
        let task = MultipartTaskBuilder.createUploadTask(with: session, url: url,
            parameters: [:], fileData: binary, fileName: "fixture", mimeType: "x/y")
        check(task.state == .suspended)
        check(session.capturedRequest!.httpBody == nil)
        check(session.capturedBody!.range(of: Data("name=\"file\"; filename=\"fixture\"".utf8)) != nil)
        check(session.capturedBody!.range(of: binary) != nil)
        check(session.capturedRequest!.value(forHTTPHeaderField: "Content-Length") == String(session.capturedBody!.count))
        let customTask = MultipartTaskBuilder.createUploadTask(with: session, url: url,
            parameters: ["a": "b"], fileData: Data(), fileName: "fixture", mimeType: "x/y", fieldName: "custom")
        check(customTask.state == .suspended)
        check(session.capturedBody!.range(of: Data("name=\"custom\"; filename=".utf8)) != nil)
        check(NSClassFromString("ICMMultipartTaskBuilder") == MultipartTaskBuilder.self)
        check(MultipartTaskBuilder().isKind(of: NSObject.self))
        task.cancel()
        customTask.cancel()
        session.backend.invalidateAndCancel()
        print("{\"status\":\"passed\",\"testCount\":\(count),\"networkTasksResumed\":0}")
    }
}
