// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import UIKit

enum SurveySource: Hashable { case programmatic, automatic }
enum SurveyDismissType { case swipe, closeButton }
enum SurveyViewState: Equatable {
    enum TransitionStyle { case forward, back, none }
    case survey(Survey)
    case step(SurveyStep, transitionStyle: TransitionStyle)
    case loadingStep(transitionStyle: TransitionStyle)
    case errorSurvey(message: String, retry: Bool)
    case errorStep(SurveyError, transitionStyle: TransitionStyle)
    case `default`, loadingSurvey, dismiss
}

@objc(_TtC18IntercomSDKPrivate15SurveyViewModel)
class SurveyViewModel: NSObject {
    private(set) var progress = Progress()
    private var metricService: SurveyMetricService?
    private(set) var appearance: SurveyAppearance?
    private let service: SurveyService
    let config: any ICMConfigProtocol
    private let surveyId: String
    private(set) var survey: Survey?
    private let source: SurveySource
    private weak var view: SurveyViewController?
    private var currentStepIndex = 0
    private var completedSteps = 0
    var viewState: SurveyViewState = .default {
        didSet { view?.update(with: viewState) }
    }

    init(view: SurveyViewController?, surveyInfo: any SurveyInfo, service: SurveyService,
         metricService: SurveyMetricService?, config: any ICMConfigProtocol) {
        self.view = view
        self.service = service
        surveyId = surveyInfo.id
        source = surveyInfo is Survey ? .automatic : .programmatic
        self.metricService = metricService
        self.config = config
        super.init()
        if let survey = surveyInfo as? Survey { setSurvey(survey) }
        else {
            NotificationCenter.default.addObserver(self, selector: #selector(configDidLoad),
                name: NSNotification.Name("kICMIntercomConfigChangedNotification"), object: nil)
        }
    }
    override init() { fatalError("init() has not been implemented") }
    deinit { NotificationCenter.default.removeObserver(self) }

    private func setSurvey(_ survey: Survey) {
        self.survey = survey
        if metricService == nil { metricService = SurveyMetricService(metricInstance: ICMMetric.sharedInstance(), survey: survey) }
        appearance = SurveyAppearance(format: survey.format, backgroundColor: survey.customizationOptions.backgroundColor,
            buttonColor: survey.customizationOptions.buttonColor)
    }

    func viewDidCompleteSetup() {
        metricService?.addOpenedMetric(source: source)
        if let survey, let first = survey.steps.first {
            let units = first.stepType == .question ? first.questions.count : 1
            progress.totalUnitCount = Int64(survey.stepCount * units)
            viewState = .step(first, transitionStyle: .none)
        } else {
            viewState = .errorSurvey(message: TranslationManager.messenger.translation(forKey: "spaces_error_title"), retry: false)
        }
    }

    private func step(for index: Int) -> SurveyStep? {
        guard let survey, index >= 0, index < survey.steps.count else { return nil }
        return survey.steps[index]
    }

    func questionUpdated() {
        guard let survey, currentStepIndex >= 0, currentStepIndex < survey.steps.count else { return }
        let step = survey.steps[currentStepIndex]
        progress.completedUnitCount = Int64(completedSteps * step.questions.count + step.completedQuestionCount)
    }

    func stepCompleted() {
        guard let survey else { return }
        if let current = step(for: currentStepIndex), current.stepType == .content || current.stepType == .question {
            completedSteps += 1
        }
        if step(for: currentStepIndex + 1) != nil { advanceToNextStep(transition: .forward) }
        else if survey.isComplete {
            viewState = .dismiss
            metricService?.addClosedFromDoneMetric()
            SurveyService.currentlyDisplayingSurvey = false
        } else { submitSurvey(transition: .forward) }
    }

    private func advanceToNextStep(transition: SurveyViewState.TransitionStyle) {
        guard let survey else { return }
        guard let next = step(for: currentStepIndex + 1) else {
            viewState = .dismiss
            SurveyService.currentlyDisplayingSurvey = false
            return
        }
        let units = next.stepType == .question ? next.questions.count : 1
        progress = Progress()
        progress.totalUnitCount = Int64(survey.stepCount * units)
        progress.completedUnitCount = Int64(completedSteps * units)
        viewState = .step(next, transitionStyle: transition)
        currentStepIndex += 1
        if survey.isComplete { metricService?.addCompletedMetric() }
    }

    func getSurvey() {
        SurveyMetricService.addFetchingSurveyMetric(id: surveyId, metricInstance: ICMMetric.sharedInstance())
        service.getSurvey(id: surveyId) { [weak self] result in
            switch result {
            case .success(let survey):
                self?.setSurvey(survey)
                self?.viewState = .survey(survey)
            case .failure(let error):
                guard let self else { return }
                let key = error == .contentNotAvailable ? "page_not_found" : "spaces_error_title"
                self.viewState = .errorSurvey(message: TranslationManager.helpCenter.translation(forKey: key), retry: error == .serverError)
            }
        }
    }

    @objc func configDidLoad() {
        if viewState == .loadingSurvey { getSurvey() }
    }
    @objc func cancelSurveyLoadStillLoading() {
        if viewState == .loadingSurvey {
            viewState = .errorSurvey(message: TranslationManager.messenger.translation(forKey: "spaces_error_title"), retry: false)
        }
    }

    func submitSurvey(transition: SurveyViewState.TransitionStyle) {
        viewState = .loadingStep(transitionStyle: transition)
        guard let survey else { return }
        service.submitSurvey(survey) { [weak self] result in
            switch result {
            case .success: self?.advanceToNextStep(transition: .none)
            case .failure(let error): self?.viewState = .errorStep(error, transitionStyle: .none)
            }
        }
    }
    func dismissSurvey() { viewState = .dismiss }

    func surveyDismissed(from source: SurveyDismissType) {
        SurveyService.currentlyDisplayingSurvey = false
        guard let survey else { return }
        service.dismissSurvey(survey, completionHandler: nil)
        if survey.isComplete {
            if source == .closeButton { metricService?.addClosedFromCloseButtonMetric() }
            else { metricService?.addClosedFromSwipeMetric() }
        } else if let metricService {
            let object: String
            let count: Int
            switch viewState {
            case .errorStep: object = "step_error"; count = metricService.survey.steps.count
            case .loadingStep: object = "step_load"; count = metricService.survey.steps.count
            default: object = "survey"; count = metricService.survey.steps.count - 1
            }
            metricService.addDismissedMetric(object: object,
                dismissalType: source == .swipe ? .original1 : .original0, stepsComplete: count)
        }
    }

    func actionButtonClicked(action: SurveyStepAction, stepId: String) {
        guard let survey else { return }
        let rawURL: String?
        if let iosUri = action.iosUri {
            service.actionButtonClicked(survey, stepId: stepId, completionHandler: nil)
            metricService?.addClosedFromDeepLinkMetric()
            rawURL = iosUri
        } else {
            metricService?.addClosedFromExternalLinkMetric()
            rawURL = action.webUrl
        }
        guard let rawURL, let url = URL(string: rawURL) else { return }
        let opener = LinkOpener.shared
        if opener.isUniversalLink(url) || opener.isCustomSchemeURL(url) {
            SurveyService.currentlyDisplayingSurvey = false
        }
        opener.openLink(url)
    }
}
