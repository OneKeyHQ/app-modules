// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import Foundation

@objc(_TtP18IntercomSDKPrivate10SurveyInfo_)
protocol SurveyInfo {
    var id: String { get }
}

@objc(_TtC18IntercomSDKPrivate8SurveyId)
class SurveyId: NSObject, SurveyInfo {
    @objc var id: String
    @objc init(id: String) {
        self.id = id
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
}
