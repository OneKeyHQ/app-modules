import Foundation

protocol CitationTapDelegate: AnyObject {
    func didTapCitations(_ citations: [ITBInlineCitation])
}
