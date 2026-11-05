#import "ICMCarouselViewController.h"
#import "ICMCarouselService.h"
#import "ICMCarouselMetricService.h"
#import "ICMCarouselReportingService.h"
#import "ICMPersistentStore.h"
#import "ICMConfig.h"
#import "ICMAssets.h"
#import "ICMAutoLayout.h"
#import "ICMRootDestinationDependencies.h"
#import "ICMColorHelperImports.h"
#import "UIPageViewController+Intercom.h"
@interface ICMRecoveredTheme (CarouselIndicatorColors)
@property (nonatomic, readonly) UIColor *iconMuted;
@property (nonatomic, readonly) UIColor *iconInversed;
@end
@implementation ICMCarouselViewController
- (BOOL)prefersTransparentNavigationBar { return YES; }
- (instancetype)initWithCarousel:(ICMCarousel *)carousel {
    self = [super init];
    if (self) { _carousel = carousel; [self observeNotifications]; }
    return self;
}
- (void)observeNotifications {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(permissionSettingsWillOpen:) name:@"kICMPermissionSettingsWillOpen" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(configDidLoad) name:@"kICMIntercomConfigChangedNotification" object:nil];
}
- (void)dealloc { [[NSNotificationCenter defaultCenter] removeObserver:self]; }
- (void)setCurrentIndex:(NSInteger)index { self.carousel.viewState.currentIndex = index; self.carousel.viewState.progressIndex = index; }
- (NSInteger)currentIndex { return self.carousel.viewState.currentIndex; }
- (void)setNextIndex:(NSInteger)index { self.carousel.viewState.nextIndex = index; }
- (NSInteger)nextIndex { return self.carousel.viewState.nextIndex; }
- (NSInteger)progressIndex { return self.carousel.viewState.progressIndex; }
- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.accessibilityIdentifier = @"intercom carousel";
    [self setupForCarousel];
    UIAccessibilityPostNotification(UIAccessibilityScreenChangedNotification, self.carousel);
}
- (void)setupForCarousel {
    switch (self.carousel.carouselType) {
        case 0: [self setupForAutomaticCarousel]; break;
        case 1: [self setupForProgrammaticCarousel]; break;
        default: {
            id<ICMContentViewControllerDelegate> delegate = self.contentDelegate;
            NSString *title = [[ICMTranslationManager messenger] translationForKey:@"spaces_error_title"];
            NSString *subtitle = [[ICMTranslationManager messenger] translationForKey:@"spaces_error_description"];
            [delegate contentLoadingDidFail:title showActionButton:NO errorMessageSubtitle:subtitle actionButtonTitle:nil];
            break;
        }
    }
}
- (void)setupForAutomaticCarousel { self.carouselFetchState = 0; [ICMCarouselMetricService sharedInstance].source = 0; [self configureForCarousel]; }
- (void)setupForProgrammaticCarousel {
    [ICMCarouselMetricService sharedInstance].source = 1;
    if ([ICMConfig sharedConfig].receivedFromServer) [self loadCarousel];
    else [self waitForConfigToLoadCarousel];
}
- (void)waitForConfigToLoadCarousel {
    [self showLoadingIndicator]; self.carouselFetchState = 3;
    [self performSelector:@selector(cancelCarouselLoadStillLoading) withObject:nil afterDelay:5];
}
- (void)cancelCarouselLoadStillLoading {
    if (self.carouselFetchState != 3) return;
    self.carouselFetchState = 2;
    id<ICMContentViewControllerDelegate> delegate = self.contentDelegate;
    NSString *title = [[ICMTranslationManager messenger] translationForKey:@"spaces_error_title"];
    NSString *subtitle = [[ICMTranslationManager messenger] translationForKey:@"spaces_error_description"];
    [delegate contentLoadingDidFail:title showActionButton:NO errorMessageSubtitle:subtitle actionButtonTitle:nil];
    NSLog(@"[Intercom] An attempt was made to display a Carousel without an Intercom user being registered. Please ensure you register a user before attempting to display a Carousel.");
}
- (void)setupScreens {
    if (self.carousel.wasFetchedFromCache) {
        if (self.carousel.screens.count == 1) [[ICMCarouselReportingService sharedInstance] carouselCompletedStatePreviouslyRecorded];
    } else {
        [[ICMCarouselMetricService sharedInstance] addEventForCarouselShownWithNumberOfScreensInMessage:self.carousel.screens.count numberShown:self.screens.count];
        if (self.carousel.screens.count == 1) [[ICMCarouselReportingService sharedInstance] recordCarouselCompletedStat];
    }
    [self setupScreenViewControllers]; [self setupPageControl];
    [self configureOverlayViewsColorsForScreenBackgroundColor:[self viewControllerAtIndex:0].backgroundColor];
}
- (void)setupPageViewController {
    self.pageViewController = [[UIPageViewController alloc] initWithTransitionStyle:UIPageViewControllerTransitionStyleScroll navigationOrientation:UIPageViewControllerNavigationOrientationHorizontal options:nil];
    self.pageViewController.dataSource = self;
    self.pageViewController.delegate = self;
    [self addViewControllerAsChildViewController:self.pageViewController];
}
- (void)addViewControllerAsChildViewController:(UIViewController *)controller {
    [self addChildViewController:controller];
    [self.view addSubview:controller.view];
    controller.view.frame = self.view.bounds;
    controller.view.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    [controller didMoveToParentViewController:self];
}
- (void)setupScreenViewControllers {
    self.screenViewControllerArray = [NSMutableArray array];
    for (ICMScreen *screen in self.screens) {
        ICMScreenViewController *controller = [[ICMScreenViewController alloc] initWithScreen:screen screenDelegate:self];
        [self.screenViewControllerArray addObject:controller];
    }
    if (self.screenViewControllerArray.count == 0) return;
    ICMScreenViewController *selected = self.screenViewControllerArray.firstObject;
    if (self.currentIndex >= 1) selected = self.screenViewControllerArray[self.currentIndex];
    UIPageViewControllerNavigationDirection direction = [UIPageViewController localizedDirectionFor:UIPageViewControllerNavigationDirectionForward interfaceDirection:self.view.effectiveUserInterfaceLayoutDirection];
    [self.pageViewController setViewControllers:@[selected] direction:direction animated:NO completion:nil];
}
- (void)setupPageControl {
    self.pageControl = [UIPageControl new];
    self.pageControl.accessibilityIdentifier = @"intercom carousel page indicator";
    [self.view addSubview:self.pageControl];
    [ICMAutoLayout centerView:self.pageControl inContainerOnAxis:NSLayoutAttributeCenterX];
    [ICMAutoLayout constrainView:self.pageControl toHeight:32];
    [self.pageControl.bottomAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.bottomAnchor constant:-0.0].active = YES;
    self.pageControl.numberOfPages = self.screenViewControllerArray.count;
    self.pageControl.currentPage = self.currentIndex;
    [self.pageControl addTarget:self action:@selector(pageControlValueChanged:) forControlEvents:UIControlEventValueChanged];
    if (self.screenViewControllerArray.count == 1) self.pageControl.hidden = YES;
}
- (void)configureForCarousel {
    NSUInteger count = self.carousel.screens.count;
    id<ICMContentViewControllerDelegate> delegate = self.contentDelegate;
    if (count == 0) {
        NSString *title = [[ICMTranslationManager messenger] translationForKey:@"spaces_error_title"];
        NSString *subtitle = [[ICMTranslationManager messenger] translationForKey:@"spaces_error_description"];
        [delegate contentLoadingDidFail:title showActionButton:NO errorMessageSubtitle:subtitle actionButtonTitle:nil];
    } else {
        [delegate contentDidLoadWithDismissEnabled:self.carousel.dismissible];
        [ICMCarouselMetricService sharedInstance].carouselInstanceId = self.carousel.instanceId;
        [self setupPageViewController];
        [[ICMCarouselReportingService sharedInstance] configureForCarousel:self.carousel];
        self.screens = self.carousel.screens;
        [self setupScreens];
    }
}
- (void)configureOverlayViewsColorsForScreenBackgroundColor:(UIColor *)color {
    [self configurePageControlForScreenBackgroundColor:color];
    [self configureCarouselBackgroundColorForScreenBackgroundColor:color];
    BOOL responds = [self.contentDelegate respondsToSelector:@selector(configureCloseButtonForBackgroundColor:)];
    if (responds) [self.contentDelegate configureCloseButtonForBackgroundColor:color];
}
- (void)configurePageControlForScreenBackgroundColor:(UIColor *)color {
    BOOL light = [ICMRecoveredColorHelper isLightColor:color];
    ICMRecoveredTheme *theme = [ICMRecoveredDefaultThemeProvider shared].theme;
    UIColor *indicatorColor = light ? theme.iconMuted : theme.iconInversed;
    self.pageControl.pageIndicatorTintColor = [indicatorColor colorWithAlphaComponent:0.5];
    theme = nil;
    ICMRecoveredTheme *currentTheme = [ICMRecoveredDefaultThemeProvider shared].theme;
    UIColor *currentColor = light ? currentTheme.iconMuted : currentTheme.iconInversed;
    self.pageControl.currentPageIndicatorTintColor = [currentColor colorWithAlphaComponent:0.9];
}
- (void)configureCarouselBackgroundColorForScreenBackgroundColor:(UIColor *)color { self.view.backgroundColor = color; }
- (void)updateOverlayViews {
    self.currentIndex = self.nextIndex;
    self.pageControl.currentPage = self.currentIndex;
    [self configureOverlayViewsColorsForScreenBackgroundColor:self.screenViewControllerArray[self.currentIndex].backgroundColor];
}
- (void)showLoadingIndicator {
    id<ICMContentViewControllerDelegate> delegate = self.contentDelegate;
    UIImage *image = [ICMAssets carouselLoadingImage];
    [delegate showLoadingViewWithImage:image];
}
- (void)showLoading { [self showLoadingIndicator]; }
- (void)loadCarousel { [self showLoadingIndicator]; [self fetchCarousel]; }
- (void)fetchCarousel {
    [[ICMCarouselMetricService sharedInstance] addEventForFetchingACarouselWithCarouselId:self.carousel.carouselId];
    self.carouselFetchState = 1;
    __weak ICMCarouselViewController *weakSelf = self;
    [ICMCarouselService getCarouselWithId:self.carousel.carouselId success:^(ICMCarousel *carousel) {
        if (weakSelf == nil) return;
        weakSelf.carouselFetchState = 0;
        dispatch_async(dispatch_get_main_queue(), ^{
            weakSelf.carousel = carousel;
            [weakSelf configureForCarousel];
        });
    } failure:^(NSError *error) {
        if (weakSelf == nil) return;
        weakSelf.carouselFetchState = 2;
        NSNumber *status = @(error.code);
        [[ICMCarouselMetricService sharedInstance] addEventForErrorWhileFetchingACarouselWithCarouselId:weakSelf.carousel.carouselId statusCode:status];
        dispatch_async(dispatch_get_main_queue(), ^{ [weakSelf carouselFetchDidFailWithStatusCode:status.intValue]; });
    }];
}
- (void)carouselFetchDidFailWithStatusCode:(int)status {
    NSString *key = status == 401 || status == 404 ? @"page_not_found" : @"spaces_error_title";
    NSString *title = [[ICMTranslationManager messenger] translationForKey:key];
    NSString *subtitle = [[ICMTranslationManager messenger] translationForKey:@"spaces_error_description"];
    [self.contentDelegate contentLoadingDidFail:title showActionButton:NO errorMessageSubtitle:subtitle actionButtonTitle:nil];
}
- (void)configDidLoad { if (self.carouselFetchState == 3) [self loadCarousel]; }
- (void)reload { [[ICMCarouselMetricService sharedInstance] addEventForCarouselRetryWithCarouselId:self.carousel.carouselId]; [self loadCarousel]; }
- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerAfterViewController:(UIViewController *)controller {
    (void)pageViewController;
    NSUInteger index = [self.screenViewControllerArray indexOfObject:(id)controller];
    return index == NSNotFound ? nil : [self viewControllerAtIndex:(NSInteger)(index + 1)];
}
- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerBeforeViewController:(UIViewController *)controller {
    (void)pageViewController;
    NSUInteger index = [self.screenViewControllerArray indexOfObject:(id)controller];
    return index == NSNotFound ? nil : [self viewControllerAtIndex:(NSInteger)(index - 1)];
}
- (void)pageViewController:(UIPageViewController *)pageViewController willTransitionToViewControllers:(NSArray<UIViewController *> *)pending {
    (void)pageViewController;
    UIViewController *controller = pending.firstObject;
    self.nextIndex = [self.screenViewControllerArray indexOfObject:(id)controller];
    [self recordCarouselStats];
}
- (void)pageViewController:(UIPageViewController *)pageViewController didFinishAnimating:(BOOL)finished previousViewControllers:(NSArray<UIViewController *> *)previous transitionCompleted:(BOOL)completed {
    (void)pageViewController; (void)finished; (void)previous;
    if (completed) [self updateOverlayViews];
}
- (void)goToNextScreenFromContext:(NSInteger)context {
    ICMScreenViewController *controller = [self fetchNextScreenViewControllerAtIndex:(NSInteger)((NSUInteger)self.currentIndex + 1)];
    if (controller != nil) [self navigateToScreen:controller];
    else [self completeCarouselFromContext:context];
}
- (void)completeCarouselFromContext:(NSInteger)context {
    [self.contentDelegate closeContentModal];
    [[ICMCarouselReportingService sharedInstance] recordCarouselCompletedStat];
    [[ICMCarouselMetricService sharedInstance] addEventForCarouselCompletedWithNumberOfScreensInMessage:self.carousel.screens.count numberShown:self.screens.count context:context];
    [ICMCarouselService cleanupCarouselCache]; [ICMCarouselService clearPresentedCarouselInstanceId];
}
- (BOOL)shouldAutoProgressScreen { return self.carousel.wasFetchedFromCache; }
- (void)permissionWasGrantedForScreen:(NSString *)identifier { [[ICMCarouselReportingService sharedInstance] recordPermissionGrantedStat:identifier]; }
- (void)actionButtonWasTappedForScreen:(NSString *)identifier { [[ICMCarouselReportingService sharedInstance] recordActionButtonTappedStat:identifier]; }
- (void)pageControlValueChanged:(UIPageControl *)sender {
    ICMScreenViewController *controller = [self fetchNextScreenViewControllerAtIndex:sender.currentPage];
    [self navigateToScreen:controller];
}
- (void)navigateToScreen:(ICMScreenViewController *)controller {
    NSInteger next = self.nextIndex;
    NSInteger current = self.currentIndex;
    UIPageViewControllerNavigationDirection direction = next <= current ? UIPageViewControllerNavigationDirectionReverse : UIPageViewControllerNavigationDirectionForward;
    direction = [UIPageViewController localizedDirectionFor:direction interfaceDirection:self.view.effectiveUserInterfaceLayoutDirection];
    __weak ICMCarouselViewController *weakSelf = self;
    [self.pageViewController setViewControllers:@[controller] direction:direction animated:YES completion:^(BOOL finished) { (void)finished; [weakSelf updateOverlayViews]; }];
    [self recordCarouselStats];
}
- (ICMScreenViewController *)fetchNextScreenViewControllerAtIndex:(NSInteger)index {
    ICMScreenViewController *controller = [self viewControllerAtIndex:index];
    self.nextIndex = index;
    return controller;
}
- (ICMScreenViewController *)viewControllerAtIndex:(NSInteger)index {
    NSUInteger count = self.screenViewControllerArray.count;
    if (count == 0) return nil;
    count = self.screenViewControllerArray.count;
    if (index < 0 || (NSUInteger)index >= count) return nil;
    return self.screenViewControllerArray[index];
}
- (void)viewDidDismissBySwipe { [self carouselDismissed:4]; }
- (void)closeButtonTapped { [self carouselDismissed:3]; [self.contentDelegate closeContentModal]; }
- (void)carouselDismissed:(NSInteger)context {
    NSInteger state = self.carouselFetchState;
    if (state == 0) [self fetchedCarouselDismissed:context];
    else if (state == 1) [[ICMCarouselMetricService sharedInstance] addEventForCarouselDismissedWhileFetchingWithCarouselId:self.carousel.carouselId context:context];
    [ICMCarouselService cleanupCarouselCache]; [ICMCarouselService clearPresentedCarouselInstanceId];
}
- (void)fetchedCarouselDismissed:(NSInteger)context {
    BOOL viewedLast = [self hasViewedLastScreen];
    ICMCarouselMetricService *metrics = [ICMCarouselMetricService sharedInstance];
    NSUInteger count = self.carousel.screens.count;
    NSUInteger shown = self.screens.count;
    if (viewedLast) {
        [metrics addEventForCarouselCompletedWithNumberOfScreensInMessage:count numberShown:shown context:context];
        [[ICMCarouselReportingService sharedInstance] recordCarouselCompletedStat];
    } else {
        NSInteger seen = (NSInteger)((NSUInteger)self.progressIndex + 1);
        [metrics addEventForCarouselDismissedWithNumberOfScreensInMessage:count numberShown:shown numberSeen:seen context:context];
        [[ICMCarouselReportingService sharedInstance] recordCarouselDismissedStat];
    }
}
- (void)recordCarouselStats {
    NSInteger next = self.nextIndex;
    if ((NSUInteger)next == self.screenViewControllerArray.count - 1) [[ICMCarouselReportingService sharedInstance] recordCarouselCompletedStat];
}
- (BOOL)hasViewedLastScreen { return (NSUInteger)self.progressIndex == self.screens.count - 1; }
- (void)permissionSettingsWillOpen:(NSNotification *)notification { (void)notification; [ICMPersistentStore storeCarousel:self.carousel]; }
@end
