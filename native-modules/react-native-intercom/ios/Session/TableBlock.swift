import UIKit

@objc(_TtC18IntercomSDKPrivate10TableBlock)
class TableBlock: ITBBlock {
    @objc var rows: [TableRow]
    @objc var title: String?

    override init!(dictionary: [AnyHashable: Any]!, appearance: any ITBBlockAppearance) {
        guard let input = dictionary?["rows"] as? [Any] else { return nil }
        rows = input.compactMap { element in
            guard let row = element as? [AnyHashable: Any] else { return nil }
            return TableRow(dictionary: row, appearance: appearance)
        }
        guard !rows.isEmpty else { return nil }
        title = dictionary?["caption"] as? String
        super.init(dictionary: dictionary, appearance: appearance)
    }

    required init?(coder: NSCoder) {
        guard let rows = coder.decodeObject(of: [NSArray.self, TableRow.self], forKey: "rows") as? [TableRow] else { return nil }
        self.rows = rows
        title = coder.decodeObject(of: NSString.self, forKey: "title") as String?
        super.init(coder: coder)
    }

    override class var supportsSecureCoding: Bool { true }
    override func type() -> Int { 23 }
    override func viewClass() -> AnyClass! { ITBBlockView.self }

    override func setValue(_ value: Any?, forKey key: String) {
        if key == "rows" { return }
        super.setValue(value, forKey: key)
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? TableBlock else { return false }
        guard (rows as NSArray).isEqual(other.rows as NSArray) else { return false }
        guard title == other.title else { return false }
        return super.isEqual(object)
    }

    override func copy(with zone: NSZone? = nil) -> Any {
        guard let result = super.copy(with: zone) as? TableBlock else { return Optional<TableBlock>.none as Any }
        result.rows = rows.map { ($0.copy() as? TableRow) ?? $0 }
        result.title = title
        return Optional.some(result) as Any
    }

    override func encode(with coder: NSCoder) {
        super.encode(with: coder)
        coder.encode(rows, forKey: "rows")
        coder.encode(title, forKey: "title")
    }
}

@objc(_TtC18IntercomSDKPrivate8TableRow)
class TableRow: NSObject, NSSecureCoding, NSCopying {
    @objc var cells: [TableCell]
    static var supportsSecureCoding: Bool { true }

    override init() {
        cells = []
        super.init()
    }

    @objc(initWithDictionary:appearance:)
    init(dictionary: [AnyHashable: Any], appearance: any ITBBlockAppearance) {
        let values = dictionary["cells"] as? [Any] ?? []
        cells = values.compactMap { element in
            guard let cell = element as? [AnyHashable: Any] else { return nil }
            return TableCell(dictionary: cell, appearance: appearance)
        }
        super.init()
    }

    required init?(coder: NSCoder) {
        guard let cells = coder.decodeObject(of: [NSArray.self, TableCell.self], forKey: "cells") as? [TableCell] else { return nil }
        self.cells = cells
        super.init()
    }

    func encode(with coder: NSCoder) { coder.encode(cells, forKey: "cells") }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? TableRow else { return false }
        return (cells as NSArray).isEqual(other.cells as NSArray)
    }

    func copy(with zone: NSZone? = nil) -> Any {
        let result = TableRow()
        result.cells = cells.map { ($0.copy() as? TableCell) ?? $0 }
        return result
    }
}

@objc(_TtC18IntercomSDKPrivate9TableCell)
class TableCell: NSObject, NSSecureCoding, NSCopying {
    @objc var content: [ITBBlock]
    static var supportsSecureCoding: Bool { true }

    override init() {
        content = []
        super.init()
    }

    @objc(initWithDictionary:appearance:)
    init(dictionary: [AnyHashable: Any], appearance: any ITBBlockAppearance) {
        let values = dictionary["content"] as? [Any] ?? []
        content = values.compactMap { element in
            guard let block = element as? [AnyHashable: Any] else { return nil }
            return ICMBlockLoader.block(from: block, appearance: appearance)
        }
        super.init()
    }

    required init?(coder: NSCoder) {
        guard let content = coder.decodeObject(of: [NSArray.self, ITBBlock.self], forKey: "content") as? [ITBBlock] else { return nil }
        self.content = content
        super.init()
    }

    func encode(with coder: NSCoder) { coder.encode(content, forKey: "content") }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? TableCell else { return false }
        return (content as NSArray).isEqual(other.content as NSArray)
    }

    func copy(with zone: NSZone? = nil) -> Any {
        let result = TableCell()
        result.content = content.map { ($0.copy() as? ITBBlock) ?? $0 }
        return result
    }
}
