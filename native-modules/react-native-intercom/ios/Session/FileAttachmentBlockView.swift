import SwiftUI
import UIKit

@objc(_TtC18IntercomSDKPrivate23FileAttachmentBlockView)
class FileAttachmentBlockView: UIView, ConversationCellContentView {
    let content: ITBAttachmentBlock
    private weak var blockDelegate: (any ConversationCellBlockDelegate)?
    private let width: CGFloat
    private let groupingPosition: Int
    private let bubbleType: BubbleType
    private let themeProvider: any ThemeProvider
    private lazy var fileTapGesture = UITapGestureRecognizer(target: self, action: #selector(fileTapped(_:)))
    private var hostingController: UIHostingController<AnyView>?

    init?(content: ITBBlock, delegate: (any ConversationCellBlockDelegate)?, width: CGFloat?, groupingPosition: Int, bubbleType: BubbleType, themeProvider: any ThemeProvider) {
        guard let content = content as? ITBAttachmentBlock else { return nil }
        self.content = content
        self.blockDelegate = delegate
        self.width = width ?? 268
        self.groupingPosition = groupingPosition
        self.bubbleType = bubbleType
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        configureSwiftUIView()
        isUserInteractionEnabled = true
        addGestureRecognizer(fileTapGesture)
        configureAccessibility()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    override var intrinsicContentSize: CGSize {
        guard let host = hostingController else { return .zero }
        return host.view!.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize)
    }
    override func layoutSubviews() {
        super.layoutSubviews()
        if let host = hostingController {
            host.view!.applyCornerRadiusForGrouping(groupingPosition, bubbleType: bubbleType, bubbleRadius: BubbleRadius(large: 16, small: 4), supportsCornerConfiguration: UIView.supportsCornerConfiguration)
        }
        if let host = hostingController { host.view!.applyBorderToRoundedCorners(borderColor: themeProvider.theme.neutralBorder) }
    }
    @objc private func fileTapped(_ sender: Any) { blockDelegate?.attachmentTapped(with: content) }

    private func getFileName() -> String {
        if let attributed = content.attributedString, !attributed.string.isEmpty { return attributed.string }
        if let string = content.url, let url = URL(string: string) { return url.lastPathComponent }
        return TranslationManager.messenger.translation(forKey: "file")
    }
    private func configureAccessibility() {
        isAccessibilityElement = true
        accessibilityLabel = "File attachment: " + getFileName()
        accessibilityHint = "Double tap to open"
    }
    private func configureSwiftUIView() {
        let content = FileAttachmentContentView(fileName: getFileName(), width: width, themeProvider: themeProvider)
        let host = UIHostingController(rootView: AnyView(content))
        hostingController = host
        host.view!.backgroundColor = .clear
        host.view!.translatesAutoresizingMaskIntoConstraints = false
        host.view!.clipsToBounds = true
        addSubview(host.view!)
        host.view!.translatesAutoresizingMaskIntoConstraints = false
        host.view!.topAnchor.constraint(equalTo: topAnchor).isActive = true
        host.view!.translatesAutoresizingMaskIntoConstraints = false
        host.view!.leftAnchor.constraint(equalTo: leftAnchor).isActive = true
        host.view!.translatesAutoresizingMaskIntoConstraints = false
        host.view!.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
        host.view!.translatesAutoresizingMaskIntoConstraints = false
        host.view!.rightAnchor.constraint(equalTo: rightAnchor).isActive = true
    }
}

struct FileAttachmentContentView: View {
    let fileName: String
    let width: CGFloat
    let themeProvider: any ThemeProvider
    @ScaledMetric private var scaledFileNameFontSize: CGFloat = 15
    @ScaledMetric private var scaledFileExtensionFontSize: CGFloat = 13

    private var fileExtension: String { URL(fileURLWithPath: fileName).pathExtension.uppercased() }
    var body: some View {
        let view = HStack(alignment: .center, spacing: 8) {
            FileTypeIconView(fileName: fileName, themeProvider: themeProvider, showIcon: true)
            VStack(alignment: .leading, spacing: 2) {
                Text(fileName).font(.system(size: scaledFileNameFontSize, weight: .regular, design: .default)).fontWeight(.medium).foregroundColor(Color(themeProvider.theme.textDefault)).lineLimit(1).truncationMode(.tail)
                Text(fileExtension).font(.system(size: scaledFileExtensionFontSize, weight: .regular, design: .default)).foregroundColor(Color(themeProvider.theme.textMuted)).lineLimit(1)
            }
            Spacer()
        }.padding(.horizontal, 12).padding(.vertical, 12).frame(width: width, height: nil, alignment: .center).background(Color(themeProvider.theme.neutralContainer))
        if #available(iOS 26.0, *) { AnyView(view) }
        else { AnyView(view.drawingGroup(opaque: false, colorMode: .nonLinear)) }
    }
}
