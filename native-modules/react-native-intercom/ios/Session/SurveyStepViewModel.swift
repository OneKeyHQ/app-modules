// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import UIKit

enum SurveyStepViewState: Hashable { case `default`, didCompleteStep }

@_objcRuntimeName(_TtC18IntercomSDKPrivate19SurveyStepViewModel)
class SurveyStepViewModel {
    private let step: SurveyStep
    private weak var view: SurveyStepViewController?
    private(set) var viewState: SurveyStepViewState = .default

    init(step: SurveyStep, view: SurveyStepViewController?) {
        self.step = step
        self.view = view
    }

    func submit() {
        if step.firstQuestionWithInvalidResponse != nil {
            do {
                let currentView = view
                let questions = currentView?.questionViewController.questionViews ?? []
                withExtendedLifetime(currentView) {
                    for (index, question) in questions.enumerated() {
                        question.showValidation(shouldVoiceOverFocusOnError: index == 0)
                    }
                }
            }
            view?.scrollToFirstInvalidQuestion()
        } else {
            viewState = .didCompleteStep
            view?.update(with: .didCompleteStep)
        }
    }
}
