import UIKit

@_objcRuntimeName(_TtC18IntercomSDKPrivate25HomescreenHeaderViewModel)
class HomescreenHeaderViewModel {
    private weak var view: HomescreenHeaderViewController?
    private var interactor: HomescreenHeaderInteractor
    private var imageLoader: ImageLoader
    private let config: any ICMConfigProtocol
    init(view: HomescreenHeaderViewController, interactor: HomescreenHeaderInteractor, imageLoader: ImageLoader, config: any ICMConfigProtocol) {
        self.interactor = interactor
        self.imageLoader = imageLoader
        self.view = view
        self.config = config
    }
    func logo(for style: UIUserInterfaceStyle) -> URL? {
        guard let raw = config.modules else { return nil }
        let modules = (raw as AnyObject) as! ConfigModules
        let light = modules.home.header.content.logoUrl
        let dark = modules.customizationConfig.messengerLogoDarkURL
        if style == .dark, let dark { return dark }
        return light
    }
    func viewDidLoad() {
        guard let view else { return }
        defer { withExtendedLifetime(view) {} }
        if let info = interactor.getHomeHeaderViewInfoFromConfig() { setViewState(for: info) }
        else {
            view.state = .noHeader
            view.configureForNoHeader()
            view.delegate?.headerDidUpdate(.noHeader)
        }
        interactor.headerUpdatingDelegate = self
    }
    func setViewState(for info: HomescreenHeaderViewInfo) {
        guard let view else { return }
        defer { withExtendedLifetime(view) {} }
        let header = info.header
        let state: HomescreenHeaderViewState
        if !header.isExpandedHeader {
            state = .miniHeader(header.content.greeting?.content, info.backgroundColor, info.foregroundColor)
            view.state = state
            view.configureMiniHeader(header.content.greeting?.content, backgroundColor: info.backgroundColor, foregroundColor: info.foregroundColor)
        } else if header.background.type != .image {
            state = .expandedHeader(header)
            view.state = state
            view.configureExpandedHeader(header, overrideForegroundColor: nil)
        } else {
            let cached: ImageLoaderCachedImage?
            if let url = header.background.imageUrl { cached = ImageLoaderCachedImage(imageLoader.imageLoader.cachedImage(for: url)) }
            else { cached = nil }
            if cached != nil {
                state = .expandedHeader(header)
                view.state = state
                view.configureExpandedHeader(header, overrideForegroundColor: nil)
            } else {
                state = .waitingForImage(header, info.backgroundColor, info.foregroundColor)
                view.state = state
                view.configureExpandedHeader(header, overrideForegroundColor: info.foregroundColor)
            }
        }
        view.delegate?.headerDidUpdate(state)
    }
}
