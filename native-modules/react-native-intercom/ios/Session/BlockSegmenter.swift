import Foundation

enum BlockSegment {
    case group([ITBBlock])
    case standalone(ITBBlock)
}

struct BlockSegmenter {
    static let separatedTypes: Set<Int> = {
        var result: Set<Int> = []
        result.insert(23)
        result.insert(9)
        return result
    }()

    static func segment(_ blocks: [ITBBlock]) -> [BlockSegment] {
        var result: [BlockSegment] = []
        var pending: [ITBBlock] = []
        for block in blocks {
            let type = block.type()
            if separatedTypes.contains(type) {
                if !pending.isEmpty { result.append(.group(pending)); pending = [] }
                result.append(.standalone(block))
            } else { pending.append(block) }
        }
        if !pending.isEmpty { result.append(.group(pending)) }
        return result
    }
}
