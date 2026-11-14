import SwiftUI
import UniformTypeIdentifiers

enum FileType: Int {
    case pdf, zip, csv, text, other

    init(fileName: String) {
        let url = URL(fileURLWithPath: fileName)
        guard let type = UTType(filenameExtension: url.pathExtension, conformingTo: .data) else { self = .other; return }
        if type.conforms(to: .pdf) { self = .pdf }
        else if type.conforms(to: .zip) { self = .zip }
        else if type.conforms(to: .commaSeparatedText) { self = .csv }
        else if type.conforms(to: .plainText) { self = .text }
        else { self = .other }
    }
    func backgroundColor(theme: Theme) -> UIColor {
        switch self {
        case .pdf, .other: return theme.fileContainerRed
        case .zip: return theme.fileContainerBlue
        case .csv: return theme.fileContainerGreen
        case .text: return theme.fileContainerNeutral
        }
    }
    var icon: Image {
        let name: String
        switch self { case .pdf, .other: name = "file"; case .zip: name = "zip"; case .csv: name = "csv"; case .text: name = "text" }
        // This fixed SDK-class consumer follows the approved own-resource relocation.
        return Image(name, bundle: ICMUtils.intercomBundle())
    }
}

struct FileTypeIconView: View {
    let fileName: String
    let themeProvider: any ThemeProvider
    let showIcon: Bool
    @ScaledMetric private var containerSize: CGFloat = 40
    @ScaledMetric private var iconSize: CGFloat = 20
    @ScaledMetric private var cornerRadius: CGFloat = 10

    var body: some View {
        let type = FileType(fileName: fileName)
        let background = type.backgroundColor(theme: themeProvider.theme)
        let iconColor = themeProvider.theme.fileIconColor
        let view = ZStack(alignment: .center) {
            RoundedRectangle(cornerRadius: cornerRadius, style: .continuous).fill(Color(background)).frame(width: containerSize, height: containerSize)
            if showIcon {
                type.icon.resizable(capInsets: EdgeInsets(), resizingMode: .stretch).frame(width: iconSize, height: iconSize).foregroundColor(Color(iconColor))
            }
        }.frame(width: containerSize, height: containerSize)
        if #available(iOS 26.0, *) { AnyView(view) }
        else { AnyView(view.drawingGroup(opaque: false, colorMode: .nonLinear)) }
    }
}
