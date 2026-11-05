import UIKit

enum BubbleType: Int { case admin, human }
struct BubbleRadius { let large: CGFloat; let small: CGFloat }
enum RectCorner: Int { case topLeading, topTrailing, bottomLeading, bottomTrailing }

extension UIView {
    static let supportsCornerConfiguration: Bool = {
        if #available(iOS 26.0, *) { return UIView.instancesRespond(to: NSSelectorFromString("setCornerConfiguration:")) }
        return false
    }()

    private func groupingCorners(_ grouping: Int, bubbleType: BubbleType) -> [RectCorner] {
        let corners: [RectCorner]
        switch grouping {
        case 0: corners = [.topLeading, .bottomLeading, .topTrailing]
        case 1: corners = [.topLeading, .bottomLeading]
        case 2: corners = [.topLeading, .bottomLeading, .bottomTrailing]
        case 3: corners = [.topLeading, .bottomLeading, .topTrailing, .bottomTrailing]
        default: corners = []
        }
        let rtl = UIView.userInterfaceLayoutDirection(for: semanticContentAttribute) == .rightToLeft
        if (bubbleType == .human && rtl) || (bubbleType == .admin && !rtl) {
            return corners.map { RectCorner(rawValue: $0.rawValue ^ 1)! }
        }
        return corners
    }

    func applyCornerRadiusForGrouping(_ grouping: Int, bubbleType: BubbleType, bubbleRadius: BubbleRadius, supportsCornerConfiguration: Bool) {
        let corners = groupingCorners(grouping, bubbleType: bubbleType)
        if #available(iOS 26.0, *), supportsCornerConfiguration {
            func radius(_ corner: RectCorner) -> UICornerRadius { .fixed(corners.contains(corner) ? bubbleRadius.large : bubbleRadius.small) }
            cornerConfiguration = .corners(topLeftRadius: radius(.topLeading), topRightRadius: radius(.topTrailing), bottomLeftRadius: radius(.bottomLeading), bottomRightRadius: radius(.bottomTrailing))
        } else {
            layer.cornerRadius = bubbleRadius.small
            roundCornersForGroupingPosition(grouping, bubbleRadius: bubbleRadius, bubbleType: bubbleType)
        }
    }

    private func roundCornersForGroupingPosition(_ grouping: Int, bubbleRadius: BubbleRadius, bubbleType: BubbleType) {
        let corners = groupingCorners(grouping, bubbleType: bubbleType)
        _ = UIView.userInterfaceLayoutDirection(for: semanticContentAttribute)
        guard bounds.width > 0, bounds.height > 0 else { return }
        for sublayer in layer.sublayers ?? [] where sublayer is CAShapeLayer || sublayer.name == "border_layer" { sublayer.removeFromSuperlayer() }
        let path = buildPath(in: bounds, curvedCorners: corners, bubbleRadius: bubbleRadius)
        let mask = CAShapeLayer()
        mask.path = path.cgPath
        layer.mask = mask
        if bubbleType == .admin {
            let border = createBorderLayer(path: path, color: DefaultThemeProvider.shared.theme.neutralBorder)
            border.name = "border_layer"
            layer.addSublayer(border)
        }
    }

    private func buildPath(in rect: CGRect, curvedCorners: [RectCorner], bubbleRadius: BubbleRadius) -> UIBezierPath {
        func radius(_ corner: RectCorner) -> CGFloat { curvedCorners.contains(corner) ? bubbleRadius.large : bubbleRadius.small }
        let tl = radius(.topLeading), tr = radius(.topTrailing), bl = radius(.bottomLeading), br = radius(.bottomTrailing)
        let path = UIBezierPath()
        path.move(to: CGPoint(x: rect.minX + tl, y: rect.minY))
        path.addLine(to: CGPoint(x: rect.maxX - tr, y: rect.minY))
        path.addArc(withCenter: CGPoint(x: rect.maxX - tr, y: rect.minY + tr), radius: tr, startAngle: -1.5707963267948966, endAngle: 0, clockwise: true)
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.maxY - br))
        path.addArc(withCenter: CGPoint(x: rect.maxX - br, y: rect.maxY - br), radius: br, startAngle: 0, endAngle: 1.5707963267948966, clockwise: true)
        path.addLine(to: CGPoint(x: rect.minX + bl, y: rect.maxY))
        path.addArc(withCenter: CGPoint(x: rect.minX + bl, y: rect.maxY - bl), radius: bl, startAngle: 1.5707963267948966, endAngle: 3.141592653589793, clockwise: true)
        path.addLine(to: CGPoint(x: rect.minX, y: rect.minY + tl))
        path.addArc(withCenter: CGPoint(x: rect.minX + tl, y: rect.minY + tl), radius: tl, startAngle: 3.141592653589793, endAngle: -1.5707963267948966, clockwise: true)
        path.close()
        return path
    }

    private func createBorderLayer(path: UIBezierPath, color: UIColor) -> CAShapeLayer {
        let transformed = UIBezierPath(cgPath: path.cgPath)
        transformed.apply(CGAffineTransform(a: 1 - 1.5 / bounds.width, b: 0, c: 0, d: 1 - 1.5 / bounds.height, tx: 0.75, ty: 0.75))
        let border = CAShapeLayer()
        border.path = transformed.cgPath
        border.fillColor = UIColor.clear.cgColor
        border.strokeColor = color.cgColor
        border.lineWidth = 1.5
        return border
    }

    func applyBorderToRoundedCorners(borderColor: UIColor) {
        guard bounds.width > 0, bounds.height > 0 else { return }
        for sublayer in layer.sublayers ?? [] where sublayer.name == "border_layer" { sublayer.removeFromSuperlayer() }
        guard let mask = layer.mask as? CAShapeLayer, let path = mask.path else { return }
        let border = createBorderLayer(path: UIBezierPath(cgPath: path), color: borderColor)
        border.name = "border_layer"
        layer.addSublayer(border)
    }
}
