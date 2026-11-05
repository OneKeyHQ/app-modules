// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import UIKit

@objc(_TtC18IntercomSDKPrivate20SurveyViewController)
class SurveyViewController: UIViewController, ICMContentViewController, ICMErrorViewDelegate {
    @objc weak var contentDelegate: (any ICMContentViewControllerDelegate)?
    private lazy var backgroundGradientLayer: CAGradientLayer = {
        let layer = CAGradientLayer()
        let appearance = viewModel!.appearance
        let theme = themeProvider.theme
        let color: UIColor
        if let appearance, (UIColor.white.luminance + 0.05) / (appearance.backgroundColor.luminance + 0.05) < 4.51 {
            color = theme.textAlwaysDark
        } else { color = theme.textAlwaysLight }
        layer.colors = [color.withAlphaComponent(0.4).cgColor, color.withAlphaComponent(0).cgColor,
                        color.withAlphaComponent(0).cgColor, color.withAlphaComponent(0.4).cgColor]
        return layer
    }()
    private let pageViewController = UIPageViewController(transitionStyle: .scroll, navigationOrientation: .horizontal, options: nil)
    private let pageContainer = UIView()
    private let progressView = UIProgressView()
    private var senderView: SurveySenderView?
    private var stepViewControllers: [SurveyStepViewController] = []
    var viewModel: SurveyViewModel?
    private var dismissable = true
    private var showProgressBar = false
    private var requiresConfirmationForDismissal = false
    private let themeProvider: any ThemeProvider = DefaultThemeProvider.shared
    private static let headerHeight: CGFloat = {
        if #available(iOS 26, *) { return 80 }
        return 56
    }()

    @objc(initWith:)
    init(with surveyInfo: any SurveyInfo) {
        super.init(nibName: nil, bundle: nil)
        let service = SurveyService()
        let config = ICMConfig.shared()!
        viewModel = SurveyViewModel(view: self, surveyInfo: surveyInfo, service: service, metricService: nil, config: config)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(nibName: String?, bundle: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    @objc var prefersTransparentNavigationBar: Bool { true }

    override func viewDidLoad() {
        super.viewDidLoad()
        view!.accessibilityIdentifier = "intercom survey"
        navigationItem.backButtonDisplayMode = .minimal
        if let survey = viewModel!.survey { viewModel!.viewState = .survey(survey) }
        else { loadSurvey() }
    }
    @objc func reload() { loadSurvey() }
    private func loadSurvey() {
        viewModel!.viewState = .loadingSurvey
        if viewModel!.config.receivedFromServer() { viewModel!.getSurvey() }
        else { viewModel!.perform(#selector(SurveyViewModel.cancelSurveyLoadStillLoading), with: nil, afterDelay: 5) }
    }
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        backgroundGradientLayer.frame = view!.bounds
    }
    func update(with state: SurveyViewState) { DispatchQueue.main.async { self.render(state) } }
    private func render(_ state: SurveyViewState) {
        switch state {
        case .default: return
        case .loadingSurvey:
            if let delegate = contentDelegate { delegate.showLoadingView(with: ICMAssets.surveyLoadingImage()) }
        case .dismiss: contentDelegate?.closeContentModal()
        case .errorSurvey(let message, let retry):
            contentDelegate?.configureDismissEnabled(true)
            if let delegate = contentDelegate {
                delegate.contentLoadingDidFail(message, showActionButton: retry,
                    errorMessageSubtitle: TranslationManager.messenger.translation(forKey: "spaces_error_description"),
                    actionButtonTitle: TranslationManager.messenger.translation(forKey: "reload"))
            }
        case .loadingStep: showLoadingScreen()
        case .errorStep(let error, let transition):
            contentDelegate?.configureDismissEnabled(true)
            progressView.alpha = 0
            showErrorScreen(error, animated: transition == .forward)
        case .survey(let survey):
            dismissable = survey.dismissable
            showProgressBar = survey.showProgressBar
            if let sender = survey.sender {
                let background = viewModel!.appearance?.backgroundColor ?? themeProvider.theme.neutralContainerAlt
                let text = ColorHelper.suitableForegroundColor(for: background, theme: DefaultThemeProvider.shared.theme)
                senderView = SurveySenderView(avatarURL: sender.avatarURL, name: sender.name,
                    config: ICMConfig.shared(), textColor: text)
            }
            setupSurveyViews()
        case .step(let step, let transition):
            if showProgressBar && (step.stepType == .content || step.stepType == .question) {
                progressView.alpha = 1
                progressView.observedProgress = viewModel!.progress
                progressView.isAccessibilityElement = viewModel!.progress.completedUnitCount != 0
            } else {
                progressView.alpha = 0
                progressView.isAccessibilityElement = false
            }
            let controller = stepViewController(for: step)
            pageViewController.setViewControllers([controller], direction: transition == .back ? .reverse : .forward,
                animated: transition != .none, completion: nil)
            requiresConfirmationForDismissal = step.stepType != .thankYou
            contentDelegate?.configureDismissEnabled(dismissable,
                swipeDismissalRequiresConfirmation: step.stepType != .thankYou,
                didAttemptDismiss: { [weak self] in self?.didAttemptDismiss(from: .swipe) })
            if progressView.isAccessibilityElement { UIAccessibility.post(notification: .screenChanged, argument: progressView) }
        }
    }

    private func setupSurveyViews() {
        setupBackground()
        contentDelegate?.contentDidLoad(withDismissEnabled: dismissable)
        if let appearance = viewModel!.appearance {
            contentDelegate?.configureCloseButton?(forBackgroundColor: appearance.backgroundColor)
        }
        setupProgressView()
        setupPageContainer()
        setupSenderView()
        setupAccessibility()
        viewModel!.viewDidCompleteSetup()
    }
    private func setupBackground() {
        view!.backgroundColor = viewModel!.appearance?.backgroundColor
        view!.layer.insertSublayer(backgroundGradientLayer, at: 0)
    }
    private func setupProgressView() {
        progressView.observedProgress = viewModel!.progress
        progressView.translatesAutoresizingMaskIntoConstraints = false
        view!.addSubview(progressView)
        let constraints0 = [
            progressView.topAnchor.constraint(equalTo: view!.topAnchor, constant: Self.headerHeight),
            progressView.leadingAnchor.constraint(equalTo: view!.leadingAnchor),
            progressView.trailingAnchor.constraint(equalTo: view!.trailingAnchor),
            progressView.heightAnchor.constraint(equalToConstant: 8)
        ]
        for constraint in constraints0 { constraint.isActive = true }
        progressView.alpha = 0
        if let appearance = viewModel!.appearance {
            let ratio = (UIColor.white.luminance + 0.05) / (appearance.backgroundColor.luminance + 0.05)
            progressView.trackTintColor = ratio < 4.51 ? UIColor(white: 0, alpha: 0.1) : UIColor(white: 1, alpha: 0.2)
            let buttonColor = appearance.buttonColor
            let tint: UIColor
            if buttonColor.isEqual(appearance.backgroundColor) {
                if buttonColor.isWhite { tint = UIColor(white: 1, alpha: 0.8) }
                else if buttonColor.isBlack { tint = UIColor(white: 0, alpha: 0.5) }
                else { tint = buttonColor }
            } else { tint = buttonColor }
            progressView.progressTintColor = tint
        }
    }
    private func setupPageContainer() {
        pageContainer.translatesAutoresizingMaskIntoConstraints = false
        view!.addSubview(pageContainer)
        let constraints1 = [
            pageContainer.topAnchor.constraint(equalTo: progressView.bottomAnchor),
            pageContainer.bottomAnchor.constraint(equalTo: view!.bottomAnchor),
            pageContainer.leadingAnchor.constraint(equalTo: view!.leadingAnchor),
            pageContainer.trailingAnchor.constraint(equalTo: view!.trailingAnchor)
        ]
        for constraint in constraints1 { constraint.isActive = true }
        addChild(pageViewController)
        pageContainer.addSubview(pageViewController.view!)
        pageViewController.view!.frame = pageContainer.bounds
        let constraints2 = [
            pageViewController.view!.heightAnchor.constraint(equalTo: pageContainer.heightAnchor),
            pageViewController.view!.widthAnchor.constraint(equalTo: pageContainer.widthAnchor)
        ]
        for constraint in constraints2 { constraint.isActive = true }
        pageViewController.didMove(toParent: self)
    }
    private func setupSenderView() {
        guard let sender = senderView else { return }
        sender.translatesAutoresizingMaskIntoConstraints = false
        view!.addSubview(sender)
        let constraints3 = [
            sender.topAnchor.constraint(equalTo: view!.topAnchor),
            sender.leadingAnchor.constraint(equalTo: view!.leadingAnchor),
            sender.trailingAnchor.constraint(equalTo: view!.trailingAnchor),
            sender.heightAnchor.constraint(equalToConstant: Self.headerHeight)
        ]
        for constraint in constraints3 { constraint.isActive = true }
    }
    private func setupAccessibility() {
        if let navigation = navigationController as? ICMNavigationController {
            navigation.view!.accessibilityElements = [view].compactMap { $0 }
        }
    }

    private func loadingImage() -> UIImage? {
        let image = ICMAssets.surveyLoadingImage()
        if let appearance = viewModel!.appearance {
            let ratio = (UIColor.white.luminance + 0.05) / (appearance.backgroundColor.luminance + 0.05)
            if !(ratio < 4.51) { return image }
        }
        return image?.withTintColor(themeProvider.theme.base.withAlphaComponent(0.35))
    }
    private func showLoadingScreen() {
        let image = loadingImage()
        let text = TranslationManager.messenger.translation(forKey: "loading")
        let loading = LoadingViewController(image: image, accessibilityText: text)
        loading.view!.backgroundColor = .clear
        pageViewController.setViewControllers([loading],
            direction: view!.effectiveUserInterfaceLayoutDirection == .rightToLeft ? .reverse : .forward,
            animated: true, completion: nil)
    }
    private func showErrorScreen(_ error: SurveyError, animated: Bool) {
        let actionTitle = TranslationManager.messenger.translation(forKey: "reload")
        let titleTranslator = TranslationManager.helpCenter
        let title = titleTranslator.translation(forKey: error == .contentNotAvailable ? "page_not_found" : "spaces_error_title")
        let subtitle = TranslationManager.messenger.translation(forKey: "spaces_error_description")
        let controller = ErrorViewController(title: title, subtitle: subtitle, showActionButton: error == .serverError,
            actionButtonTitle: actionTitle, themeProvider: DefaultThemeProvider.shared, icon: nil)
        if let appearance = viewModel!.appearance {
            controller.configureForBackgroundColor(backgroundColor: appearance.backgroundColor, buttonColor: appearance.buttonColor)
        }
        controller.errorDelegate = self
        pageViewController.setViewControllers([controller],
            direction: view!.effectiveUserInterfaceLayoutDirection == .rightToLeft ? .reverse : .forward,
            animated: animated, completion: nil)
    }
    private func stepViewController(for step: SurveyStep) -> SurveyStepViewController {
        for controller in stepViewControllers {
            if controller.step === step { return controller }
        }
        addNewStepViewController(step)
        return stepViewController(for: step)
    }
    private func addNewStepViewController(_ step: SurveyStep) {
        guard let appearance = viewModel!.appearance else { return }
        let controller = SurveyStepViewController(step: step, surveyAppearance: appearance,
            linkOpener: LinkOpener.shared, themeProvider: DefaultThemeProvider.shared)
        controller.stepDelegate = self
        stepViewControllers.append(controller)
    }
    private func didAttemptDismiss(from source: SurveyDismissType) {
        let controller = UIAlertController(title: nil,
            message: TranslationManager.messenger.translation(forKey: "dismiss_survey_confirmation"), preferredStyle: .actionSheet)
        if let popover = controller.popoverPresentationController, let navigation = navigationController as? ICMNavigationController {
            popover.barButtonItem = navigation.closeButton()
        }
        let dismiss = UIAlertAction(title: TranslationManager.messenger.translation(forKey: "surveys_dismiss"), style: .destructive) { [weak self] _ in
            self?.viewModel!.dismissSurvey()
            self?.viewModel!.surveyDismissed(from: source)
        }
        let cancel = UIAlertAction(title: TranslationManager.messenger.translation(forKey: "surveys_continue"), style: .cancel, handler: nil)
        controller.addAction(cancel)
        controller.addAction(dismiss)
        present(controller, animated: true, completion: nil)
    }
    @objc func closeButtonTapped() {
        if requiresConfirmationForDismissal { didAttemptDismiss(from: .closeButton) }
        else {
            viewModel!.dismissSurvey()
            viewModel!.surveyDismissed(from: .closeButton)
        }
    }
    @objc func viewDidDismissBySwipe() {
        viewModel!.dismissSurvey()
        viewModel!.surveyDismissed(from: .swipe)
    }
    @objc func didSelectErrorAction() { viewModel!.submitSurvey(transition: .none) }
}
