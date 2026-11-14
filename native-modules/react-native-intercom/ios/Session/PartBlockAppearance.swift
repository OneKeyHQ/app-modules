import UIKit

@objc(_TtC18IntercomSDKPrivate27AIAnswerPartBlockAppearance)
class AIAnswerPartBlockAppearance: ICMAdminBlockAppearance {
    override init() {
        super.init()
        textInsets = .zero
        codeInsets = .zero
        linkInsets = .zero
    }
}

@objc(_TtC18IntercomSDKPrivate19PartBlockAppearance)
class PartBlockAppearance: ICMAdminBlockAppearance {
    override init() {
        super.init()
        textInsets = .zero
        inlineImageCornerRadius = 6
        inlineImagePadding = 4
        codeInsets = .zero
        messengerCardInsets = .zero
    }
}

@objc(_TtC18IntercomSDKPrivate27PersonalPartBlockAppearance)
class PersonalPartBlockAppearance: PartBlockAppearance {
    private let themeProvider: ThemeProvider

    override init() {
        themeProvider = DefaultThemeProvider.shared
        super.init()
        let textTheme = themeProvider.theme
        textColor = ColorHelper.suitableDynamicForegroundColor(for: textTheme.primaryBackground, theme: textTheme)
        let linkTheme = themeProvider.theme
        linkTextColor = ColorHelper.suitableDynamicForegroundColor(for: linkTheme.primaryBackground, theme: linkTheme)
    }
}
