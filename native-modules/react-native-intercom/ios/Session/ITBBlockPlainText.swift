import Foundation

extension ITBBlock {
    func plainTextIncludingListItems() -> String? {
        guard let list = self as? ITBListBlock else { return plainTextRepresentation() }
        let blocks = list.textBlocks ?? []
        let strings: [String] = blocks.compactMap { $0.plainTextRepresentation() }
        return strings.isEmpty ? nil : strings.joined(separator: "\n")
    }
}
