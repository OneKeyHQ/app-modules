import Foundation

protocol HomeMetricServiceProtocol {
    func addLauncherClickedMetric()
    func addHomeViewedMetric()
    func addWeRunOnIntercomClickedMetric()
    func addMessagesSpaceClickedMetric()
    func addHelpSpaceClickedMetric()
    func addExternalLinkClickedMetric()
}

@objc(_TtC18IntercomSDKPrivate17HomeMetricService)
class HomeMetricService: NSObject, HomeMetricServiceProtocol {
    private let metricInstance: (any ICMMetricProtocol)?

    required init(metricInstance: (any ICMMetricProtocol)?) {
        self.metricInstance = metricInstance
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }

    private func add(action: String, object: String, context: String) {
        let event = ICMMetricEvent(action: action, object: object, place: "messenger", context: context, additionalInfo: nil)
        event?.eventType = 0
        metricInstance?.addEvent(event)
    }
    func addLauncherClickedMetric() { add(action: "clicked", object: "launcher", context: "no_context") }
    func addHomeViewedMetric() { add(action: "viewed", object: "home", context: "no_context") }
    func addWeRunOnIntercomClickedMetric() { add(action: "clicked", object: "we_run_on_intercom", context: "home") }
    func addMessagesSpaceClickedMetric() { add(action: "clicked", object: "messages", context: "home") }
    func addHelpSpaceClickedMetric() { add(action: "clicked", object: "help", context: "home") }
    func addExternalLinkClickedMetric() { add(action: "clicked", object: "external_link", context: "home") }

    @objc class func addLauncherClickedMetric() {
        let metric = ICMMetric.sharedInstance()
        let service = self.init(metricInstance: metric)
        service.addLauncherClickedMetric()
    }
}
