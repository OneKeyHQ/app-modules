import UIKit

// The original optimized default path writes directly to UIPasteboard. Private protocol spelling is unrecovered.
struct CodeBlockCopier {
    func copyCode(_ text: String) {
        UIPasteboard.general.string = text
    }
}
