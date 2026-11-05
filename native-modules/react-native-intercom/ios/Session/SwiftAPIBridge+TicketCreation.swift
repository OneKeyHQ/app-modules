import Foundation

extension SwiftAPIBridge {
    func getTicketForm(request: TicketFormRequest) async throws -> TicketFormResponse {
        try await withCheckedThrowingContinuation { continuation in
            httpClient.getTicketForm(with: request, success: { continuation.resume(returning: $0) }, error: { error in
                if let error { continuation.resume(throwing: error) }
                else { continuation.resume(throwing: APIError(errorType: .unknownError, statusCode: -1, additionalInfo: nil)) }
            })
        }
    }
    func createTicket(for identifier: String, conversationId: String?, attributes: [[AnyHashable: Any]], completionHandler: @escaping (Result<Ticket, APIError>) -> Void) {
        httpClient.createTicketTypeId(identifier, conversationId: conversationId, attributes: attributes, success: { completionHandler(.success($0)) }, error: { completionHandler(.failure(Self.apiError(from: $0 as NSError?))) })
    }
}
