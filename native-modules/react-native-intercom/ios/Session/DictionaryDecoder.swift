// Original DictionaryInitializable/DictionaryDecoder; see docs/IOS_PROVENANCE.md.
import Foundation

protocol DictionaryInitializable {
    init?(withDictionary: [String: Any])
}

extension DictionaryInitializable where Self: Decodable {
    init?(withDictionary dictionary: [String: Any]) {
        do {
            self = try DictionaryDecoder().decode(Self.self, from: dictionary, with: .convertFromSnakeCase)
        } catch {
            ICMLogger.log("Failed to initialize \(Self.self) with dictionary: \(error)", type: .debug)
            return nil
        }
    }
}

class DictionaryDecoder {
    func decode<Value: Decodable>(_ type: Value.Type, from dictionary: [String: Any],
                                  with strategy: JSONDecoder.KeyDecodingStrategy) throws -> Value {
        let data = try JSONSerialization.data(withJSONObject: dictionary, options: [])
        let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .epochString
        decoder.keyDecodingStrategy = strategy
        return try decoder.decode(type, from: data)
    }
}

extension JSONDecoder.DateDecodingStrategy {
    static var epochString: JSONDecoder.DateDecodingStrategy {
        .custom { decoder in
            let container = try decoder.singleValueContainer()
            let timestamp = try container.decode(String.self)
            guard let value = Double(timestamp) else {
                throw DecodingError.dataCorruptedError(in: container, debugDescription: "Invalid timestamp string: \(timestamp)")
            }
            return Date(timeIntervalSince1970: value)
        }
    }
}
