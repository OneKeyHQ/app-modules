import UIKit

enum HomescreenHeaderViewState {
    case expandedHeader(HomeHeader)
    case miniHeader(String?, UIColor, UIColor)
    case waitingForImage(HomeHeader, UIColor, UIColor)
    case noHeader
}

// Payload order is proven; standalone record labels/layout were optimized away.
struct HomescreenHeaderViewInfo {
    let header: HomeHeader
    let backgroundColor: UIColor
    let foregroundColor: UIColor
}

// Concrete cached-image conversion; case labels are descriptive recovered source names.
enum ImageLoaderCachedImage {
    case image(UIImage)
    case animatedImage(AnimatedImage)
    init?(_ object: Any?) {
        if let image = object as? UIImage { self = .image(image) }
        else if let image = object as? AnimatedImage { self = .animatedImage(image) }
        else { return nil }
    }
}

protocol HomescreenHeaderViewDelegate: AnyObject {
    func headerDidUpdate(_ state: HomescreenHeaderViewState)
}
