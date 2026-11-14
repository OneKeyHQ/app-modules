import Foundation

@objc(ICMMultipartTaskBuilder)
final class MultipartTaskBuilder: NSObject {
    @objc(createUploadTaskWith:url:parameters:fileData:fileName:mimeType:)
    static func createUploadTask(with session: URLSession, url: URL,
                                 parameters: [String: String], fileData: Data,
                                 fileName: String, mimeType: String) -> URLSessionUploadTask {
        createUploadTask(with: session, url: url, parameters: parameters,
                         fileData: fileData, fileName: fileName, mimeType: mimeType,
                         fieldName: "file")
    }

    @objc(createUploadTaskWith:url:parameters:fileData:fileName:mimeType:fieldName:)
    static func createUploadTask(with session: URLSession, url: URL,
                                 parameters: [String: String], fileData: Data,
                                 fileName: String, mimeType: String,
                                 fieldName: String) -> URLSessionUploadTask {
        let (request, body) = requestAndBody(url: url, parameters: parameters,
                                           fileData: fileData, fileName: fileName,
                                           mimeType: mimeType, fieldName: fieldName,
                                           existingHeaders: [:])
        return session.uploadTask(with: request, from: body)
    }

    @objc(createMultipartRequestWithUrl:parameters:fileData:fileName:mimeType:fieldName:existingHeaders:)
    static func createMultipartRequest(url: URL, parameters: [String: String],
                                       fileData: Data, fileName: String,
                                       mimeType: String, fieldName: String,
                                       existingHeaders: [String: String]) -> URLRequest {
        var (request, body) = requestAndBody(url: url, parameters: parameters,
                                           fileData: fileData, fileName: fileName,
                                           mimeType: mimeType, fieldName: fieldName,
                                           existingHeaders: existingHeaders)
        request.httpBody = body
        return request
    }

    private static func requestAndBody(url: URL, parameters: [String: String],
                                       fileData: Data, fileName: String,
                                       mimeType: String, fieldName: String,
                                       existingHeaders: [String: String]) -> (URLRequest, Data) {
        var request = URLRequest(url: url, cachePolicy: .useProtocolCachePolicy,
                                 timeoutInterval: 60)
        request.httpMethod = "POST"
        for (name, value) in existingHeaders {
            request.setValue(value, forHTTPHeaderField: name)
        }
        let boundary = "Boundary-" + UUID().uuidString
        request.setValue("multipart/form-data; boundary=" + boundary,
                         forHTTPHeaderField: "Content-Type")
        let body = createBody(parameters: parameters, fileData: fileData,
                              fileName: fileName, mimeType: mimeType,
                              boundary: boundary, fieldName: fieldName)
        request.setValue(String(body.count), forHTTPHeaderField: "Content-Length")
        return (request, body)
    }

    private static func createBody(parameters: [String: String], fileData: Data,
                                    fileName: String, mimeType: String,
                                    boundary: String, fieldName: String) -> Data {
        var body = Data()
        for (name, value) in parameters {
            body.append(contentsOf: "--\(boundary)\r\n".utf8)
            body.append(contentsOf: "Content-Disposition: form-data; name=\"\(name)\"\r\n\r\n".utf8)
            body.append(contentsOf: "\(value)\r\n".utf8)
        }
        body.append(contentsOf: "--\(boundary)\r\n".utf8)
        body.append(contentsOf: "Content-Disposition: form-data; name=\"\(fieldName)\"; filename=\"\(fileName)\"\r\n".utf8)
        body.append(contentsOf: "Content-Type: \(mimeType)\r\n\r\n".utf8)
        body.append(fileData)
        body.append(contentsOf: "\r\n--\(boundary)--\r\n".utf8)
        return body
    }
}
