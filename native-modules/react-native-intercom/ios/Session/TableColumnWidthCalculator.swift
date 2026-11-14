import CoreGraphics

// Only the specialized static function survives; the namespace shape is inferred.
enum TableColumnWidthCalculator {
    static func columnWidths(contentWidths: [[CGFloat]], minColumnWidth: CGFloat, maxColumnWidth: CGFloat, horizontalPadding: CGFloat) -> [CGFloat] {
        let columnCount = contentWidths.map(\.count).max() ?? 0
        if columnCount <= 0 { return [] }
        return (0..<columnCount).map { column in
            let widths = contentWidths.compactMap { row -> CGFloat? in column < row.count ? row[column] : nil }
            var maximum = widths.first ?? 0
            for width in widths.dropFirst() { if maximum < width { maximum = width } }
            let padded = maximum + (horizontalPadding + horizontalPadding)
            let lower = padded <= minColumnWidth ? minColumnWidth : padded
            return maxColumnWidth < lower ? maxColumnWidth : lower
        }
    }
}
