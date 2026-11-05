import Foundation

@objc(_TtC18IntercomSDKPrivate33NavigationBarManagerMetricService)
class NavigationBarManagerMetricService: NSObject {
    private let metricInstance: ICMMetricProtocol?

    required init(metricInstance: ICMMetricProtocol?) {
        self.metricInstance = metricInstance
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }

    func addHelpSpaceClickedMetric(isNewConversation: Bool) {
        let context = isNewConversation ? "in_new_conversation" : "in_conversation"
        let event = ICMMetricEvent(action: "clicked", object: "help", place: "conversation", context: context,
                                   additionalInfo: nil)
        event?.eventType = 0
        metricInstance?.addEvent(event)
    }

    @objc class func addHelpSpaceClickedMetric(isNewConversation: Bool) {
        let metric = ICMMetric.sharedInstance()
        let service = self.init(metricInstance: metric)
        service.addHelpSpaceClickedMetric(isNewConversation: isNewConversation)
    }
}
