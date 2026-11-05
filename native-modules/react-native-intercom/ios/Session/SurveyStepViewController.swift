import UIKit

@objc(_TtC18IntercomSDKPrivate24SurveyStepViewController)
class SurveyStepViewController: UIViewController, SurveyQuestionViewDelegate {
    weak var stepDelegate: SurveyViewController?
    let step: SurveyStep
    private let themeProvider: any ThemeProvider
    private let scrollView = UIScrollView()
    private let scrollViewContentView = UIView()
    private let questionContainer = UIView()
    let questionViewController: SurveyQuestionViewController
    private var questionContainerHeightConstraint: NSLayoutConstraint?
    private let contentStackView: ICMContentStackView
    private let buttonStackView = UIStackView()
    private var submitButton: UIButton?
    var viewModel: SurveyStepViewModel?
    private let surveyAppearance: SurveyAppearance
    var keyboardOverlapFrame: CGRect?
    private let linkOpener: any LinkOpenerProtocol
    private let blockDelegate = OutboundContentBlockDelegate(context: .survey)

    init(step: SurveyStep, surveyAppearance: SurveyAppearance,
         linkOpener: any LinkOpenerProtocol, themeProvider: any ThemeProvider) {
        self.step = step
        self.surveyAppearance = surveyAppearance
        self.linkOpener = linkOpener
        self.themeProvider = themeProvider
        contentStackView = ICMContentStackView(blocks: step.contentBlocks, context: 1, blockDelegate: blockDelegate)!
        questionViewController = SurveyQuestionViewController(with: step.questions,
            surveyAppearance: surveyAppearance, blockDelegate: blockDelegate)
        super.init(nibName: nil, bundle: nil)
        if viewModel == nil { viewModel = SurveyStepViewModel(step: step, view: self) }
        questionViewController.questionViewDelegate = self
        questionViewController.surveyScrollingDelegate = self
    }

    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(nibName: String?, bundle: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }

    override func viewDidLoad() {
        super.viewDidLoad()
        view!.backgroundColor = .clear
        navigationItem.backButtonDisplayMode = .minimal
        setupScrollView()
        setupContent()
        setupQuestionContainer()
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow(_:)), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide(_:)), name: UIResponder.keyboardWillHideNotification, object: nil)
        setupButtonStackView()
        setupActionButtons()
        setupSubmitButton()
        setupAccessibility()
    }

    private func setupScrollView() {
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        view!.addSubview(scrollView)
        let constraints0 = [
            scrollView.leadingAnchor.constraint(equalTo: view!.leadingAnchor),
            scrollView.trailingAnchor.constraint(equalTo: view!.trailingAnchor),
            scrollView.topAnchor.constraint(equalTo: view!.topAnchor),
            scrollView.bottomAnchor.constraint(equalTo: view!.bottomAnchor)
        ]
        for constraint in constraints0 { constraint.isActive = true }
        scrollViewContentView.translatesAutoresizingMaskIntoConstraints = false
        scrollViewContentView.accessibilityIdentifier = "intercom survey step content scroll view"
        scrollView.addSubview(scrollViewContentView)
        let constraints1 = [
            scrollViewContentView.centerXAnchor.constraint(equalTo: view!.centerXAnchor),
            scrollViewContentView.topAnchor.constraint(equalTo: scrollView.topAnchor),
            scrollViewContentView.bottomAnchor.constraint(equalTo: scrollView.bottomAnchor),
            scrollViewContentView.heightAnchor.constraint(greaterThanOrEqualTo: view!.heightAnchor)
        ]
        for constraint in constraints1 { constraint.isActive = true }
        let leading = scrollViewContentView.leadingAnchor.constraint(greaterThanOrEqualTo: view!.leadingAnchor, constant: 24)
        leading.priority = UILayoutPriority(999)
        leading.isActive = true
        let width = scrollViewContentView.widthAnchor.constraint(equalToConstant: 400)
        width.priority = UILayoutPriority(998)
        width.isActive = true
        let height = scrollViewContentView.heightAnchor.constraint(equalTo: view!.heightAnchor)
        height.priority = UILayoutPriority(505)
        height.isActive = true
    }

    private func setupContent() {
        contentStackView.translatesAutoresizingMaskIntoConstraints = false
        scrollViewContentView.addSubview(contentStackView)
        let constraints2 = [
            contentStackView.topAnchor.constraint(equalTo: scrollViewContentView.topAnchor, constant: 20),
            contentStackView.centerXAnchor.constraint(equalTo: scrollViewContentView.centerXAnchor),
            contentStackView.trailingAnchor.constraint(equalTo: scrollViewContentView.safeAreaLayoutGuide.trailingAnchor)
        ]
        for constraint in constraints2 { constraint.isActive = true }
        contentStackView.renderContent()
    }

    private func setupQuestionContainer() {
        questionContainer.translatesAutoresizingMaskIntoConstraints = false
        scrollViewContentView.addSubview(questionContainer)
        let constraints3 = [
            questionContainer.topAnchor.constraint(equalTo: contentStackView.bottomAnchor, constant: 40),
            questionContainer.leadingAnchor.constraint(equalTo: scrollViewContentView.safeAreaLayoutGuide.leadingAnchor),
            questionContainer.trailingAnchor.constraint(equalTo: scrollViewContentView.safeAreaLayoutGuide.trailingAnchor)
        ]
        for constraint in constraints3 { constraint.isActive = true }
        addChild(questionViewController)
        questionContainer.addSubview(questionViewController.view!)
        questionViewController.view!.frame = questionContainer.bounds
        let constraints4 = [
            questionViewController.view!.heightAnchor.constraint(equalTo: questionContainer.heightAnchor),
            questionViewController.view!.widthAnchor.constraint(equalTo: questionContainer.widthAnchor)
        ]
        for constraint in constraints4 { constraint.isActive = true }
        questionViewController.didMove(toParent: self)
        questionContainerHeightConstraint = questionContainer.heightAnchor.constraint(equalToConstant: 100)
        questionContainerHeightConstraint?.isActive = true
    }

    override func preferredContentSizeDidChange(forChildContentContainer container: any UIContentContainer) {
        super.preferredContentSizeDidChange(forChildContentContainer: container)
        questionContainerHeightConstraint?.constant = container.preferredContentSize.height
    }

    private func setupButtonStackView() {
        view!.addSubview(buttonStackView)
        buttonStackView.alignment = .center
        buttonStackView.axis = .vertical
        buttonStackView.spacing = 26
        buttonStackView.translatesAutoresizingMaskIntoConstraints = false
        let constraints5 = [
            buttonStackView.centerXAnchor.constraint(equalTo: view!.centerXAnchor),
            buttonStackView.topAnchor.constraint(greaterThanOrEqualTo: questionContainer.bottomAnchor, constant: 40),
            buttonStackView.bottomAnchor.constraint(equalTo: scrollViewContentView.safeAreaLayoutGuide.bottomAnchor)
        ]
        for constraint in constraints5 { constraint.isActive = true }
        let unactivatedTop = buttonStackView.topAnchor.constraint(equalTo: questionContainer.bottomAnchor, constant: 40)
        unactivatedTop.priority = UILayoutPriority(500)
    }

    private func setupActionButtons() {
        for (index, action) in step.actions.enumerated() {
            let button = primaryButton(title: action.title)
            button.tag = index
            if let rawURL = action.iosUri ?? action.webUrl, let url = URL(string: rawURL), linkOpener.isExternalLink(url) {
                let foreground = ColorHelper.suitableForegroundColor(for: surveyAppearance.buttonColor, theme: themeProvider.theme)
                button.setImage(ICMAssets.externalLinkIcon()!.withTintColor(foreground), for: .normal)
                let direction = view!.effectiveUserInterfaceLayoutDirection
                var configuration = button.configuration
                configuration?.contentInsets = direction == .rightToLeft
                    ? NSDirectionalEdgeInsets(top: 0, leading: 0, bottom: 0, trailing: 9)
                    : NSDirectionalEdgeInsets(top: 0, leading: 9, bottom: 0, trailing: 0)
                button.configuration = configuration
            }
            button.accessibilityIdentifier = "survey action button " + String(index)
            button.addTarget(self, action: #selector(actionButtonTapped(_:)), for: .touchUpInside)
        }
    }

    private func primaryButton(title: String) -> UIButton {
        let button = ICMActionButton(title: title, style: 0, backgroundColor: surveyAppearance.buttonColor,
            foregroundColor: ColorHelper.suitableForegroundColor(for: surveyAppearance.buttonColor, theme: themeProvider.theme))!
        buttonStackView.addArrangedSubview(button)
        button.titleLabel?.adjustsFontForContentSizeCategory = true
        button.titleLabel?.font = .preferredFont(forTextStyle: .headline)
        button.translatesAutoresizingMaskIntoConstraints = false
        button.heightAnchor.constraint(equalToConstant: 48).isActive = true
        let width = button.widthAnchor.constraint(equalToConstant: 400)
        width.priority = UILayoutPriority(998)
        width.isActive = true
        button.widthAnchor.constraint(lessThanOrEqualToConstant: 400).isActive = true
        let trailing = button.trailingAnchor.constraint(equalTo: scrollViewContentView.trailingAnchor)
        trailing.priority = UILayoutPriority(999)
        trailing.isActive = true
        return button
    }

    private func secondaryButton(title: String) -> UIButton {
        let button = UIButton()
        button.setTitle(title, for: .normal)
        if let label = button.titleLabel {
            label.font = .systemFont(ofSize: UIFont.preferredFont(forTextStyle: .subheadline).fontDescriptor.pointSize, weight: .semibold)
        }
        button.setTitleColor(ColorHelper.suitableForegroundColor(for: surveyAppearance.buttonColor, theme: themeProvider.theme), for: .normal)
        buttonStackView.addArrangedSubview(button)
        return button
    }

    private func setupSubmitButton() {
        submitButton = step.actions.isEmpty ? primaryButton(title: step.buttonText) : secondaryButton(title: step.buttonText)
        submitButton?.addTarget(self, action: #selector(submitButtonTapped), for: .touchUpInside)
        submitButton?.accessibilityIdentifier = "survey step submit button"
    }

    @objc private func submitButtonTapped() {
        view!.endEditing(true)
        viewModel!.submit()
    }

    @objc(actionButtonTappedWithButton:) private func actionButtonTapped(_ button: UIButton) {
        guard let delegate = stepDelegate else { return }
        let actions = step.actions
        let index = button.tag
        let action = actions[index]
        let id = step.id
        withExtendedLifetime(delegate) {
            delegate.viewModel!.actionButtonClicked(action: action, stepId: id)
        }
    }

    func update(with state: SurveyStepViewState) {
        DispatchQueue.main.async {
            if state == .didCompleteStep, let delegate = self.stepDelegate {
                withExtendedLifetime(delegate) { delegate.viewModel!.stepCompleted() }
            }
        }
    }

    func valueUpdated() {
        guard let delegate = stepDelegate else { return }
        withExtendedLifetime(delegate) { delegate.viewModel!.questionUpdated() }
    }

    private func setupAccessibility() {
        let rotor = UIAccessibilityCustomRotor(name: TranslationManager.messenger.translation(forKey: "surveys_questions")) { [weak self] predicate in
            self?.rotorResult(predicate)
        }
        view!.accessibilityCustomRotors = [rotor]
        if step.stepType == .thankYou, let first = contentStackView.arrangedSubviews.first {
            UIAccessibility.post(notification: .screenChanged, argument: first)
        }
    }

    private func rotorResult(_ predicate: UIAccessibilityCustomRotorSearchPredicate) -> UIAccessibilityCustomRotorItemResult? {
        let question: SurveyQuestionView?
        if let direct = predicate.currentItem.targetElement as? SurveyQuestionView { question = direct }
        else { question = (predicate.currentItem.targetElement as? UIView).flatMap { questionAncestor(of: $0) } }
        let questions = questionViewController.questionViews
        let current = question.flatMap { current in questions.firstIndex { $0.isEqual(current) } }
        let index: Int
        switch predicate.searchDirection {
        case .next: index = current.map { $0 + 1 } ?? 0
        case .previous: index = current.map { $0 - 1 } ?? 0
        @unknown default: return nil
        }
        guard index >= 0, index < questions.count else { return nil }
        return UIAccessibilityCustomRotorItemResult(targetElement: questions[index].contentStackView, targetRange: nil)
    }

    private func questionAncestor(of view: UIView) -> SurveyQuestionView? {
        var candidate = view.superview
        while let current = candidate {
            if let question = current as? SurveyQuestionView { return question }
            candidate = current.superview
        }
        return nil
    }

    @objc(keyboardWillShowWithSender:) private func keyboardWillShow(_ notification: NSNotification) {
        guard let frame = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? CGRect,
              let screen = view!.window?.windowScene?.screen else { return }
        let converted = view!.convert(frame, from: screen.coordinateSpace)
        let intersection = converted.intersection(view!.bounds)
        let height: CGFloat
        if intersection.isNull { keyboardOverlapFrame = nil; height = 0 }
        else { keyboardOverlapFrame = intersection; height = intersection.height }
        scrollView.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: height, right: 0)
        scrollView.scrollIndicatorInsets = UIEdgeInsets(top: 0, left: 0, bottom: height, right: 0)
        UIResponder.internalCurrentFirstResponder = nil
        UIApplication.shared.sendAction(#selector(UIResponder.findFirstResponder(sender:)), to: nil, from: nil, for: nil)
        if let responder = UIResponder.internalCurrentFirstResponder as? UIView, let question = questionAncestor(of: responder) {
            scrollSurvey(to: question)
        }
    }

    @objc(keyboardWillBeHiddenWithSender:) private func keyboardWillHide(_ notification: NSNotification) {
        keyboardOverlapFrame = nil
        scrollView.contentInset = .zero
        scrollView.scrollIndicatorInsets = .zero
    }

    func scrollToFirstInvalidQuestion() {
        guard let question = step.firstQuestionWithInvalidResponse,
              let questionView = questionViewController.questionView(for: question) else { return }
        let rectangle = questionView.convert(questionView.bounds, to: scrollView as any UICoordinateSpace)
        let y: CGFloat
        if rectangle.midY < scrollView.bounds.height / 2 { y = 0 }
        else if scrollView.contentSize.height - scrollView.bounds.height / 2 < rectangle.midY {
            y = scrollView.contentSize.height - scrollView.bounds.height + scrollView.adjustedContentInset.bottom
        } else if scrollView.bounds.height < rectangle.height { y = rectangle.origin.y - 20 }
        else { y = rectangle.origin.y + rectangle.height / 2 - scrollView.bounds.height / 2 }
        scrollView.setContentOffset(CGPoint(x: 0, y: y), animated: true)
    }

    func scrollSurvey(to target: UIView) {
        guard let overlap = keyboardOverlapFrame else { return }
        let rectangle = target.convert(target.bounds, to: scrollView as any UICoordinateSpace)
        let visible = CGRect(x: 0, y: 0, width: scrollView.bounds.width, height: scrollView.bounds.height - overlap.height)
        let y = visible.contains(rectangle) ? 0 : (rectangle.maxY - scrollView.bounds.height) + (overlap.height + 20)
        scrollView.setContentOffset(CGPoint(x: 0, y: y), animated: true)
    }
}
