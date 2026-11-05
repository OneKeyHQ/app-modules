// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import UIKit

@objc(_TtC18IntercomSDKPrivate28SurveyQuestionViewController)
class SurveyQuestionViewController: UIViewController {
    weak var questionViewDelegate: (any SurveyQuestionViewDelegate)?
    var questionViews: [SurveyQuestionView] = []
    weak var surveyScrollingDelegate: SurveyStepViewController?
    private let questionStackView = UIStackView()
    private let questions: [any SurveyQuestion]
    private let surveyAppearance: SurveyAppearance
    weak var blockDelegate: OutboundContentBlockDelegate?

    init(with questions: [any SurveyQuestion], surveyAppearance: SurveyAppearance,
         blockDelegate: OutboundContentBlockDelegate?) {
        self.questions = questions
        self.surveyAppearance = surveyAppearance
        self.blockDelegate = blockDelegate
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(nibName: String?, bundle: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }

    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.backButtonDisplayMode = .minimal
        setupQuestionStackView()
        setupQuestions()
    }

    private func setupQuestionStackView() {
        questionStackView.accessibilityIdentifier = "Question StackView"
        questionStackView.translatesAutoresizingMaskIntoConstraints = false
        questionStackView.axis = .vertical
        questionStackView.alignment = .leading
        questionStackView.spacing = 16
        view!.addSubview(questionStackView)
        questionStackView.leadingAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.leadingAnchor).isActive = true
        questionStackView.topAnchor.constraint(equalTo: view!.topAnchor).isActive = true
        let bottom = questionStackView.bottomAnchor.constraint(equalTo: view!.bottomAnchor)
        bottom.priority = UILayoutPriority(999)
        bottom.isActive = true
        let center = questionStackView.centerXAnchor.constraint(equalTo: view!.centerXAnchor)
        center.priority = UILayoutPriority(999)
        center.isActive = true
    }

    private func setupQuestions() {
        let originalQuestions = questions
        let count = originalQuestions.count
        let appearance = surveyAppearance
        let stack = questionStackView
        for (index, question) in originalQuestions.enumerated() {
            let questionDelegate = questionViewDelegate
            let scrollingDelegate = surveyScrollingDelegate
            let blocksDelegate = blockDelegate
            let questionView = SurveyQuestionView(question: question, questionIndex: index,
                questionCount: count, surveyAppearance: appearance, questionDelegate: questionDelegate,
                surveyScrollingDelegate: scrollingDelegate, blockDelegate: blocksDelegate,
                themeProvider: DefaultThemeProvider.shared)
            stack.addArrangedSubview(questionView)
            questionView.widthAnchor.constraint(equalTo: stack.widthAnchor).isActive = true
            questionViews.append(questionView)
        }
    }

    func questionView(for question: any SurveyQuestion) -> SurveyQuestionView? {
        let currentViews = questionViews
        for candidate in currentViews {
            if candidate.question.id == question.id { return candidate }
        }
        return nil
    }

    override func viewDidLayoutSubviews() {
        let width = view!.bounds.width
        preferredContentSize = view!.systemLayoutSizeFitting(
            CGSize(width: width, height: UIView.layoutFittingCompressedSize.height))
    }
}
