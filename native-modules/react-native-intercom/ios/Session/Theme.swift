import UIKit

@objc(_TtC18IntercomSDKPrivate5Theme)
class Theme: NSObject {
    @objc let base: UIColor
    @objc let input: UIColor
    @objc let inputAlt: UIColor
    @objc let textDefault: UIColor
    @objc let textInversed: UIColor
    @objc let textMuted: UIColor
    @objc let textMutedExtra: UIColor
    @objc let textError: UIColor
    @objc let textSuccess: UIColor
    @objc let textNotice: UIColor
    @objc let textAlwaysLight: UIColor
    @objc let textAlwaysDark: UIColor
    @objc let alwaysWhite: UIColor
    @objc let iconDefault: UIColor
    @objc let iconInversed: UIColor
    @objc let iconMuted: UIColor
    @objc let iconSubtle: UIColor
    @objc let iconError: UIColor
    @objc let iconSuccess: UIColor
    @objc let iconWarning: UIColor
    @objc let neutralContainerAlt: UIColor
    @objc let neutralContainer: UIColor
    @objc let neutralBorder: UIColor
    @objc let neutralContainerEmphasis: UIColor
    @objc let neutralBorderEmphasis: UIColor
    @objc let imageBorder: UIColor
    @objc let errorContainer: UIColor
    @objc let errorContainerEmphasis: UIColor
    @objc let errorBorder: UIColor
    @objc let errorBorderEmphasis: UIColor
    @objc let errorFill: UIColor
    @objc let errorFillEmphasis: UIColor
    @objc let successContainer: UIColor
    @objc let successContainerEmphasis: UIColor
    @objc let successBorder: UIColor
    @objc let successBorderEmphasis: UIColor
    @objc let successFill: UIColor
    @objc let successFillEmphasis: UIColor
    @objc let warningContainer: UIColor
    @objc let warningContainerEmphasis: UIColor
    @objc let warningBorder: UIColor
    @objc let warningBorderEmphasis: UIColor
    @objc let warningFill: UIColor
    @objc let warningFillEmphasis: UIColor
    @objc let statusAwayFill: UIColor
    @objc let statusOnlineFill: UIColor
    @objc let statusWaitingOnYouFill: UIColor
    @objc let statusNotificationFill: UIColor
    @objc let statusInProgressFill: UIColor
    @objc let red500: UIColor
    @objc let blue500: UIColor
    @objc let green500: UIColor
    @objc let red100: UIColor
    @objc let blue100: UIColor
    @objc let green600: UIColor
    @objc let green100: UIColor
    @objc let fileContainerRed: UIColor
    @objc let fileContainerGreen: UIColor
    @objc let fileContainerBlue: UIColor
    @objc let fileContainerNeutral: UIColor
    @objc let fileIconColor: UIColor
    @objc let syntaxKeyword: UIColor
    @objc let syntaxString: UIColor
    @objc let syntaxNumber: UIColor
    @objc let syntaxComment: UIColor
    @objc let syntaxFunction: UIColor
    @objc let syntaxTag: UIColor
    @objc let syntaxOperator: UIColor
    let primaryForeground: UIColor
    let primaryBackground: UIColor

    @objc init(primaryForeground: UIColor, primaryBackground: UIColor) {
        base = Constants.base
        input = Constants.neutralContainer
        inputAlt = Constants.inputAlt
        textDefault = Constants.textDefault
        textInversed = Constants.textInversed
        textMuted = Constants.textMuted
        textMutedExtra = Constants.textMutedExtra
        textError = Constants.textError
        textSuccess = Constants.textSuccess
        textNotice = Constants.textNotice
        textAlwaysLight = Constants.textDefaultLight
        textAlwaysDark = Constants.textDefaultDark
        alwaysWhite = Constants.alwaysWhite
        iconDefault = Constants.textDefault
        iconInversed = Constants.textInversed
        iconMuted = Constants.textMuted
        iconSubtle = Constants.textMutedExtra
        iconError = Constants.textError
        iconSuccess = Constants.textSuccess
        iconWarning = Constants.textNotice
        neutralContainerAlt = Constants.base
        neutralContainer = Constants.neutralContainer
        neutralBorder = Constants.neutralBorder
        neutralContainerEmphasis = Constants.neutralContainerEmphasis
        neutralBorderEmphasis = Constants.neutralBorderEmphasis
        imageBorder = Constants.imageBorder
        errorContainer = Constants.errorContainer
        errorContainerEmphasis = Constants.errorContainerEmphasis
        errorBorder = Constants.errorBorder
        errorBorderEmphasis = Constants.errorBorderEmphasis
        errorFill = Constants.errorFill
        errorFillEmphasis = Constants.errorFillEmphasis
        successContainer = Constants.successContainer
        successContainerEmphasis = Constants.successContainerEmphasis
        successBorder = Constants.successBorder
        successBorderEmphasis = Constants.successBorderEmphasis
        successFill = Constants.successFill
        successFillEmphasis = Constants.successFillEmphasis
        warningContainer = Constants.warningContainer
        warningContainerEmphasis = Constants.warningContainerEmphasis
        warningBorder = Constants.warningBorder
        warningBorderEmphasis = Constants.warningBorderEmphasis
        warningFill = Constants.warningFill
        warningFillEmphasis = Constants.warningFillEmphasis
        statusAwayFill = Constants.statusAwayFill
        statusOnlineFill = Constants.statusOnlineFill
        statusWaitingOnYouFill = Constants.statusWaitingOnYouFill
        statusNotificationFill = Constants.statusNotificationFill
        statusInProgressFill = Constants.textDefault
        red500 = Constants.red500
        blue500 = Constants.blue500
        green500 = Constants.green500
        red100 = Constants.red100
        blue100 = Constants.blue100
        green600 = Constants.green600
        green100 = Constants.green100
        fileContainerRed = Constants.fileContainerRed
        fileContainerGreen = Constants.fileContainerGreen
        fileContainerBlue = Constants.fileContainerBlue
        fileContainerNeutral = Constants.fileContainerNeutral
        fileIconColor = Constants.fileIconColor
        syntaxKeyword = Constants.syntaxKeyword
        syntaxString = Constants.syntaxString
        syntaxNumber = Constants.syntaxNumber
        syntaxComment = Constants.syntaxComment
        syntaxFunction = Constants.syntaxFunction
        syntaxTag = Constants.syntaxTag
        syntaxOperator = Constants.syntaxOperator
        self.primaryForeground = primaryForeground
        self.primaryBackground = primaryBackground
        super.init()
    }

    @objc override init() { fatalError("init() has not been implemented") }

    @objc static let grey25: UIColor = UIColor(red: 0.98, green: 0.98, blue: 0.98, alpha: 1.0)
    @objc static let grey100: UIColor = UIColor(red: 0.961, green: 0.961, blue: 0.961, alpha: 1.0)
    @objc static let grey200: UIColor = UIColor(red: 0.898, green: 0.898, blue: 0.898, alpha: 1.0)
    @objc static let grey400: UIColor = UIColor(red: 0.627, green: 0.635, blue: 0.651, alpha: 1.0)
    @objc static let grey500: UIColor = UIColor(red: 0.424, green: 0.435, blue: 0.455, alpha: 1.0)
    @objc static let grey600: UIColor = UIColor(red: 0.31, green: 0.318, blue: 0.333, alpha: 1.0)
    @objc static let grey700: UIColor = UIColor(red: 0.251, green: 0.259, blue: 0.271, alpha: 1.0)
    @objc static let grey800: UIColor = UIColor(red: 0.18, green: 0.188, blue: 0.196, alpha: 1.0)
    @objc static let grey850: UIColor = UIColor(red: 0.169, green: 0.176, blue: 0.192, alpha: 1.0)
    @objc static let grey900: UIColor = UIColor(red: 0.078, green: 0.086, blue: 0.102, alpha: 1.0)
    @objc static let grey950: UIColor = UIColor(red: 0.035, green: 0.055, blue: 0.082, alpha: 1.0)

    @objc var primaryContainer: UIColor { primaryBackground }
    @objc var primaryContainerSubtle: UIColor { primarySubtle }
    @objc var primaryContainerEmphasis: UIColor { primaryEmphasis }
    @objc var primaryBorder: UIColor { primaryBackground }
    @objc var primaryAvatar: UIColor { primaryEmphasis }
    @objc var primaryText: UIColor { primaryForeground }
    @objc var primaryIcon: UIColor { primaryForeground }
    @objc var primaryOnContainer: UIColor {
        ColorHelper.suitableDynamicForegroundColor(for: primaryBackground, theme: self)
    }
    @objc var primaryOnContainerSubtle: UIColor {
        ColorHelper.suitableDynamicForegroundColor(for: primarySubtle, theme: self)
    }
    @objc var primaryOnContainerEmphasis: UIColor {
        ColorHelper.suitableDynamicForegroundColor(for: primaryEmphasis, theme: self)
    }

    private var primaryEmphasis: UIColor {
        UIColor { traits in
            self.primaryBackground.adjustingBrightness(by: traits.userInterfaceStyle == .dark ? 1.2 : 0.8)
        }
    }
    private var primarySubtle: UIColor {
        UIColor { traits in
            self.primaryBackground.adjustingBrightness(by: traits.userInterfaceStyle == .dark ? 0.8 : 1.2)
        }
    }

    private enum Constants {
        static let base: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Theme.grey900 }
            return UIColor.white
        }
        static let inputAlt: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Theme.grey900 }
            return UIColor.white
        }
        static let textDefaultLight: UIColor = Theme.grey900
        static let textDefaultDark: UIColor = Theme.grey25
        static let alwaysWhite: UIColor = UIColor.white
        static let textDefault: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Constants.textDefaultDark }
            return Constants.textDefaultLight
        }
        static let textInversed: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Constants.textDefaultLight }
            return Constants.textDefaultDark
        }
        static let textMuted: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Theme.grey500 }
            return Theme.grey400
        }
        static let textMutedExtra: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Theme.grey400 }
            return Theme.grey500
        }
        static let textError: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.976, green: 0.525, blue: 0.525, alpha: 1.0) }
            return UIColor(red: 0.698, green: 0.102, blue: 0.102, alpha: 1.0)
        }
        static let textSuccess: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.706, green: 0.937, blue: 0.702, alpha: 1.0) }
            return UIColor(red: 0.067, green: 0.439, blue: 0.063, alpha: 1.0)
        }
        static let textNotice: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.996, green: 0.906, blue: 0.604, alpha: 1.0) }
            return UIColor(red: 0.482, green: 0.4, blue: 0.133, alpha: 1.0)
        }
        static let neutralContainer: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Theme.grey850 }
            return Theme.grey100
        }
        static let neutralBorder: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Theme.grey850 }
            return Theme.grey100
        }
        static let neutralContainerEmphasis: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Theme.grey700 }
            return Theme.grey200
        }
        static let neutralBorderEmphasis: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Theme.grey700 }
            return Theme.grey200
        }
        static let imageBorder: UIColor = UIColor(red: 0.03529411764705882, green: 0.054901960784313725, blue: 0.08235294117647059, alpha: 0.10196078431372549)
        static let errorContainer: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.337, green: 0.161, blue: 0.161, alpha: 1.0) }
            return UIColor(red: 1.0, green: 0.859, blue: 0.859, alpha: 1.0)
        }
        static let errorContainerEmphasis: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.396, green: 0.161, blue: 0.161, alpha: 1.0) }
            return UIColor(red: 1.0, green: 0.8, blue: 0.8, alpha: 1.0)
        }
        static let errorBorder: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.58, green: 0.22, blue: 0.22, alpha: 1.0) }
            return UIColor(red: 0.976, green: 0.525, blue: 0.525, alpha: 1.0)
        }
        static let errorBorderEmphasis: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.965, green: 0.361, blue: 0.333, alpha: 1.0) }
            return UIColor(red: 0.965, green: 0.361, blue: 0.333, alpha: 1.0)
        }
        static let errorFill: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.976, green: 0.525, blue: 0.525, alpha: 1.0) }
            return UIColor(red: 0.875, green: 0.125, blue: 0.125, alpha: 1.0)
        }
        static let errorFillEmphasis: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.965, green: 0.361, blue: 0.333, alpha: 1.0) }
            return UIColor(red: 0.698, green: 0.102, blue: 0.102, alpha: 1.0)
        }
        static let successContainer: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.153, green: 0.275, blue: 0.145, alpha: 1.0) }
            return UIColor(red: 0.906, green: 0.973, blue: 0.902, alpha: 1.0)
        }
        static let successContainerEmphasis: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.176, green: 0.325, blue: 0.173, alpha: 1.0) }
            return UIColor(red: 0.706, green: 0.937, blue: 0.702, alpha: 1.0)
        }
        static let successBorder: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.18, green: 0.42, blue: 0.18, alpha: 1.0) }
            return UIColor(red: 0.412, green: 0.8, blue: 0.4, alpha: 1.0)
        }
        static let successBorderEmphasis: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.067, green: 0.439, blue: 0.063, alpha: 1.0) }
            return UIColor(red: 0.169, green: 0.639, blue: 0.161, alpha: 1.0)
        }
        static let successFill: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.082, green: 0.525, blue: 0.075, alpha: 1.0) }
            return UIColor(red: 0.082, green: 0.525, blue: 0.075, alpha: 1.0)
        }
        static let successFillEmphasis: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.412, green: 0.8, blue: 0.4, alpha: 1.0) }
            return UIColor(red: 0.067, green: 0.439, blue: 0.063, alpha: 1.0)
        }
        static let warningContainer: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.271, green: 0.22, blue: 0.047, alpha: 1.0) }
            return UIColor(red: 1.0, green: 0.953, blue: 0.796, alpha: 1.0)
        }
        static let warningContainerEmphasis: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.341, green: 0.275, blue: 0.059, alpha: 1.0) }
            return UIColor(red: 0.996, green: 0.906, blue: 0.604, alpha: 1.0)
        }
        static let warningBorder: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.482, green: 0.4, blue: 0.133, alpha: 1.0) }
            return UIColor(red: 0.969, green: 0.839, blue: 0.412, alpha: 1.0)
        }
        static let warningBorderEmphasis: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.867, green: 0.718, blue: 0.235, alpha: 1.0) }
            return UIColor(red: 1.0, green: 0.812, blue: 0.2, alpha: 1.0)
        }
        static let warningFill: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.969, green: 0.839, blue: 0.412, alpha: 1.0) }
            return UIColor(red: 0.976, green: 0.776, blue: 0.122, alpha: 1.0)
        }
        static let warningFillEmphasis: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.996, green: 0.906, blue: 0.604, alpha: 1.0) }
            return UIColor(red: 0.867, green: 0.718, blue: 0.235, alpha: 1.0)
        }
        static let statusAwayFill: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.969, green: 0.839, blue: 0.412, alpha: 1.0) }
            return UIColor(red: 1.0, green: 0.812, blue: 0.2, alpha: 1.0)
        }
        static let statusOnlineFill: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.412, green: 0.8, blue: 0.4, alpha: 1.0) }
            return UIColor(red: 0.082, green: 0.525, blue: 0.075, alpha: 1.0)
        }
        static let statusWaitingOnYouFill: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.961, green: 0.604, blue: 0.435, alpha: 1.0) }
            return UIColor(red: 0.812, green: 0.282, blue: 0.035, alpha: 1.0)
        }
        static let statusNotificationFill: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.976, green: 0.525, blue: 0.525, alpha: 1.0) }
            return UIColor(red: 0.875, green: 0.125, blue: 0.125, alpha: 1.0)
        }
        static let red500: UIColor = UIColor(red: 0.875, green: 0.125, blue: 0.125, alpha: 1.0)
        static let blue500: UIColor = UIColor(red: 0.086, green: 0.373, blue: 0.776, alpha: 1.0)
        static let green500: UIColor = UIColor(red: 0.082, green: 0.525, blue: 0.075, alpha: 1.0)
        static let red100: UIColor = UIColor(red: 0.976, green: 0.525, blue: 0.525, alpha: 1.0)
        static let blue100: UIColor = UIColor(red: 0.686, green: 0.784, blue: 0.949, alpha: 1.0)
        static let green600: UIColor = UIColor(red: 0.082, green: 0.525, blue: 0.075, alpha: 1.0)
        static let green100: UIColor = UIColor(red: 0.412, green: 0.8, blue: 0.4, alpha: 1.0)
        static let syntaxKeyword: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 1.0, green: 0.482, blue: 0.447, alpha: 1.0) }
            return UIColor(red: 0.843, green: 0.227, blue: 0.286, alpha: 1.0)
        }
        static let syntaxString: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Constants.blue100 }
            return UIColor(red: 0.012, green: 0.184, blue: 0.384, alpha: 1.0)
        }
        static let syntaxNumber: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.376, green: 0.647, blue: 0.98, alpha: 1.0) }
            return Constants.blue500
        }
        static let syntaxComment: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Theme.grey400 }
            return Theme.grey500
        }
        static let syntaxFunction: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.824, green: 0.659, blue: 1.0, alpha: 1.0) }
            return UIColor(red: 0.435, green: 0.259, blue: 0.757, alpha: 1.0)
        }
        static let syntaxTag: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return UIColor(red: 0.494, green: 0.906, blue: 0.529, alpha: 1.0) }
            return UIColor(red: 0.133, green: 0.525, blue: 0.227, alpha: 1.0)
        }
        static let syntaxOperator: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Theme.grey400 }
            return Theme.grey500
        }
        static let fileContainerRed: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Constants.red100 }
            return Constants.red500
        }
        static let fileContainerGreen: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Constants.green100 }
            return Constants.green600
        }
        static let fileContainerBlue: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Constants.blue100 }
            return Constants.blue500
        }
        static let fileContainerNeutral: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Theme.grey25 }
            return Theme.grey900
        }
        static let fileIconColor: UIColor = UIColor { traits in
            if traits.userInterfaceStyle == .dark { return Theme.grey900 }
            return Constants.alwaysWhite
        }
    }
}
