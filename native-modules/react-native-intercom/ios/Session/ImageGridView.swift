import SwiftUI

// The two original injected-loader requirements were deleted; this is the concrete default transport.
struct ImageLoader {
    let imageLoader: ITBImageLoader
}

struct ImageGridView: View {
    @Environment(\.layoutDirection) private var layoutDirection
    let images: [String]
    let maxWidth: CGFloat
    let groupingPosition: Int
    let bubbleType: BubbleType
    let themeProvider: ThemeProvider
    let imageLoader: ImageLoader
    let onImageTap: (Int, [String]) -> Void

    private var rows: [[Int]] {
        if images.isEmpty { return [] }
        if images.count == 4 { return [[0, 1], [2, 3]] }
        var result: [[Int]] = []
        var index = 0
        while index < images.count {
            result.append(Array(index..<min(index + 3, images.count)))
            index += 3
        }
        return result
    }
    var body: some View {
        let count = rows.count
        let rtl = layoutDirection == .rightToLeft
        return VStack(alignment: .trailing, spacing: 2) {
            ForEach(Array(rows.enumerated()), id: \.offset) { row in
                rowView(row.element, rowIndex: row.offset, totalRows: count, rtl: rtl)
            }
        }
    }
    private func rowView(_ row: [Int], rowIndex: Int, totalRows: Int, rtl: Bool) -> some View {
        let size = (maxWidth - CGFloat(row.count - 1) * 2) / CGFloat(row.count)
        return HStack(alignment: .top, spacing: 2) {
            ForEach(Array(row.enumerated()), id: \.element) { item in
                ImageGridCell(imageURL: images[item.element], size: size,
                              configuration: ImageCellConfiguration(corners: corners(row: rowIndex, column: item.offset, totalRows: totalRows, columnCount: row.count, rtl: rtl), bubbleRadius: BubbleRadius(large: 16, small: 4), borderColor: themeProvider.theme.imageBorder),
                              themeProvider: themeProvider, imageLoader: imageLoader,
                              onTap: { onImageTap(item.element, images) })
            }
        }
        .environment(\.layoutDirection, .leftToRight)
        .frame(maxWidth: .infinity, alignment: .trailing)
    }
    private func corners(row: Int, column: Int, totalRows: Int, columnCount: Int, rtl: Bool) -> [RectCorner] {
        let base: [RectCorner]
        switch groupingPosition {
        case 0: base = [.topLeading, .bottomLeading, .topTrailing]
        case 1: base = [.topLeading, .bottomLeading]
        case 2: base = [.topLeading, .bottomLeading, .bottomTrailing]
        case 3: base = [.topLeading, .bottomLeading, .topTrailing, .bottomTrailing]
        default: base = []
        }
        let flip = (bubbleType == .human && rtl) || (bubbleType == .admin && !rtl)
        let grouped = flip ? base.map { RectCorner(rawValue: $0.rawValue ^ 1)! } : base
        if images.count == 1 { return grouped }
        var outer: [RectCorner] = []
        if row == 0 && column == 0 { outer.append(.topLeading) }
        if row == 0 && column == columnCount - 1 { outer.append(.topTrailing) }
        if row == totalRows - 1 && column == 0 { outer.append(.bottomLeading) }
        if row == totalRows - 1 && column == columnCount - 1 { outer.append(.bottomTrailing) }
        return outer.filter { grouped.contains($0) }
    }
}

private struct ImageCellConfiguration {
    let corners: [RectCorner]
    let bubbleRadius: BubbleRadius
    let borderColor: UIColor
}

private struct ImageGridCell: View {
    @State private var loadedImage: UIImage?
    @State private var isLoading = true
    let imageURL: String
    let size: CGFloat
    let configuration: ImageCellConfiguration
    let themeProvider: ThemeProvider
    let imageLoader: ImageLoader
    let onTap: () -> Void

    init(imageURL: String, size: CGFloat, configuration: ImageCellConfiguration, themeProvider: ThemeProvider, imageLoader: ImageLoader, onTap: @escaping () -> Void) {
        self.imageURL = imageURL
        self.size = size
        self.configuration = configuration
        self.themeProvider = themeProvider
        self.imageLoader = imageLoader
        self.onTap = onTap
    }
    var body: some View {
        let shape = RoundedCornersShape(corners: configuration.corners, bubbleRadius: configuration.bubbleRadius)
        return ZStack {
            if let image = loadedImage {
                Image(uiImage: image).resizable(capInsets: EdgeInsets(), resizingMode: .stretch)
                    .scaledToFill().frame(width: size, height: size).clipped()
            } else {
                Rectangle().fill(Color(themeProvider.theme.neutralContainer)).frame(width: size, height: size)
            }
        }
        .frame(width: size, height: size)
        .redacted(reason: isLoading ? .placeholder : [])
        .clipShape(shape)
        .overlay(shape.stroke(Color(configuration.borderColor), lineWidth: 1))
        .contentShape(Rectangle())
        .onTapGesture(count: 1, perform: onTap)
        .onAppear(perform: loadImage)
    }
    private func loadImage() {
        guard let url = URL(string: imageURL) else { isLoading = false; return }
        let cached = imageLoader.imageLoader.cachedImage(for: url)
        if let image = cached as? UIImage {
            loadedImage = image
            isLoading = false
            return
        }
        if let animated = cached as? AnimatedImage {
            loadedImage = animated.coverImage
            isLoading = false
            return
        }
        let identifier = imageLoader.imageLoader.loadImage(withURLString: imageURL) { object in
            if let image = object as? UIImage { loadedImage = image }
            else if let animated = object as? AnimatedImage { loadedImage = animated.coverImage }
            isLoading = false
        }!
        let _: UUID = identifier
    }
}

struct RoundedCornersShape: Shape {
    let corners: [RectCorner]
    let bubbleRadius: BubbleRadius
    func path(in rect: CGRect) -> Path {
        let tl = corners.contains(.topLeading) ? bubbleRadius.large : bubbleRadius.small
        let tr = corners.contains(.topTrailing) ? bubbleRadius.large : bubbleRadius.small
        let br = corners.contains(.bottomTrailing) ? bubbleRadius.large : bubbleRadius.small
        let bl = corners.contains(.bottomLeading) ? bubbleRadius.large : bubbleRadius.small
        var path = Path()
        path.move(to: CGPoint(x: rect.minX + tl, y: rect.minY))
        path.addLine(to: CGPoint(x: rect.maxX - tr, y: rect.minY))
        path.addArc(center: CGPoint(x: rect.maxX - tr, y: rect.minY + tr), radius: tr, startAngle: .radians(-1.5707963267948966), endAngle: .zero, clockwise: false)
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.maxY - br))
        path.addArc(center: CGPoint(x: rect.maxX - br, y: rect.maxY - br), radius: br, startAngle: .zero, endAngle: .radians(1.5707963267948966), clockwise: false)
        path.addLine(to: CGPoint(x: rect.minX + bl, y: rect.maxY))
        path.addArc(center: CGPoint(x: rect.minX + bl, y: rect.maxY - bl), radius: bl, startAngle: .radians(1.5707963267948966), endAngle: .radians(3.141592653589793), clockwise: false)
        path.addLine(to: CGPoint(x: rect.minX, y: rect.minY + tl))
        path.addArc(center: CGPoint(x: rect.minX + tl, y: rect.minY + tl), radius: tl, startAngle: .radians(3.141592653589793), endAngle: .radians(4.71238898038469), clockwise: false)
        path.closeSubpath()
        return path
    }
}
