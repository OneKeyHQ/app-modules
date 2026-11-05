// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import UIKit

@objc(_TtC18IntercomSDKPrivate26SurveyCustomizationOptions)
class SurveyCustomizationOptions: NSObject, DictionaryInitializable {
    @objc let backgroundColor: UIColor
    @objc let buttonColor: UIColor

    @objc init(backgroundColor: UIColor, buttonColor: UIColor) {
        self.backgroundColor = backgroundColor
        self.buttonColor = buttonColor
        super.init()
    }
    required init?(withDictionary dictionary: [String: Any]) {
        guard let background = dictionary["background_color"] as? String,
              let button = dictionary["button_color"] as? String else { return nil }
        backgroundColor = ICMColor.color(withHexString: background)!
        buttonColor = ICMColor.color(withHexString: button)!
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
}
