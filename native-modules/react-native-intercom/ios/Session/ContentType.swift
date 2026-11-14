import Foundation

// Original observed object/tag0 and ITBBlock/tag1 payloads; private case spellings are unrecovered.
enum ContentType {
    case object(NSObject)
    case block(ITBBlock)
}
