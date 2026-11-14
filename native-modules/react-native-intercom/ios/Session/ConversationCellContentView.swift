import UIKit

// Descriptor proves the UIView constraint, associated type and read-only getter;
// its content spelling is inferred from the original conformers' stored fields.
protocol ConversationCellContentView: UIView {
    associatedtype ContentType
    var content: ContentType { get }
}
