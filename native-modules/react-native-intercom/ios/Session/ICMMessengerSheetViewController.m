#import "ICMMessengerSheetViewController.h"
#import "ICMNavigationController.h"
#import "ICMErrorView.h"
#import "ICMAutoLayout.h"
#import "ICMBlockAppearanceDependencies.h"
#import "ICMHTTPBridge.h"
#import "ICMLinkOpenerImports.h"
#import "../Identity/ICMSheet.h"
@implementation ICMMessengerSheetViewController
- (instancetype)initWithSheetURL:(NSURL *)url params:(NSDictionary *)params delegate:(id<ICMMessengerSheetViewControllerDelegate>)delegate {
    if ((self = [super init])) { _sheetURL = url; _sheetParams = params; _delegate = delegate; }
    return self;
}
- (void)loadView { [super loadView]; [self fetchSheetData]; [self createWebview]; }
- (void)viewDidLoad { [super viewDidLoad]; [(ICMNavigationController *)self.navigationController displayCloseButton]; }
- (void)viewIsAppearing:(BOOL)animated { [super viewIsAppearing:animated]; [self setupProgressview]; }
- (void)createWebview {
    self.sheetWebview = [[WKWebView alloc] initWithFrame:CGRectZero configuration:[WKWebViewConfiguration new]];
    self.sheetWebview.opaque = NO;
    self.sheetWebview.translatesAutoresizingMaskIntoConstraints = NO;
    self.sheetWebview.allowsBackForwardNavigationGestures = NO;
    [self.sheetWebview addObserver:self forKeyPath:NSStringFromSelector(@selector(estimatedProgress)) options:NSKeyValueObservingOptionNew context:nil];
    self.sheetWebview.accessibilityIdentifier = @"intercom messenger sheet";
    self.sheetWebview.navigationDelegate = self;
    WKUserScript *script = [[WKUserScript alloc] initWithSource:@"iOSHost = { handleAction: function(action) {window.webkit.messageHandlers.handleAction.postMessage(action);}}" injectionTime:WKUserScriptInjectionTimeAtDocumentStart forMainFrameOnly:YES];
    [self.sheetWebview.configuration.userContentController addUserScript:script];
    [self.sheetWebview.configuration.userContentController addScriptMessageHandler:self name:@"handleAction"];
    [self.view addSubview:self.sheetWebview];
    [self.sheetWebview becomeFirstResponder];
    [ICMAutoLayout pinView:self.sheetWebview edges:0xe toSameEdgesOfView:self.view];
    [ICMAutoLayout pinView:self.sheetWebview edges:1 toSameEdgesOfView:self.view inset:CGRectGetHeight(self.navigationController.navigationBar.frame)];
}
- (void)setupProgressview {
    self.progressView = [[UIProgressView alloc] initWithProgressViewStyle:UIProgressViewStyleBar];
    self.progressView.progressTintColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryText;
    self.progressView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.navigationController.navigationBar addSubview:self.progressView];
    [ICMAutoLayout constrainView:self.progressView toSize:CGSizeMake(self.navigationController.navigationBar.frame.size.width, 2)];
    [ICMAutoLayout pinView:self.progressView edges:0xa toSameEdgesOfView:self.navigationController.navigationBar];
    [ICMAutoLayout pinView:self.progressView edges:4 toSameEdgesOfView:self.navigationController.navigationBar];
}
- (void)showErrorView {
    if (!self.errorView) {
        self.errorView = [[ICMErrorView alloc] initWithTitle:[[ICMTranslationManager messenger] translationForKey:@"couldnt_load_content"] message:[[ICMTranslationManager messenger] translationForKey:@"give_it_another_try"] showActionButton:YES actionButtonTitle:[[ICMTranslationManager messenger] translationForKey:@"reload"]];
        self.errorView.translatesAutoresizingMaskIntoConstraints = NO;
        self.errorView.delegate = self;
        [self.view addSubview:self.errorView];
        [self layoutErrorView];
    }
    [self showProgressViewWithProgress:1];
    [self hideProgressView];
    self.errorView.hidden = NO;
}
- (void)layoutErrorView {
    [self.errorView.leftAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.leftAnchor constant:10].active = YES;
    [self.errorView.rightAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.rightAnchor constant:-10].active = YES;
    [ICMAutoLayout centerView:self.errorView inContainerOnAxis:NSLayoutAttributeCenterY];
}
- (void)fetchSheetData {
    __weak typeof(self) weakSelf = self;
    [self showProgressViewWithProgress:0.1f];
    [ICMHTTPBridge fetchMessengerSheetData:self.sheetParams success:^(ICMSheet *response) {
        weakSelf.errorView.hidden = YES;
        [weakSelf loadSheet:response.requestBody];
    } failure:^(NSError *error) { (void)error; [weakSelf showErrorView]; }];
}
- (void)loadSheet:(id)body {
    if (!self.sheetURL) {
        NSLog(@"[Intercom] ❌ ERROR - Messenger App Sheet does not have a valid URL");
        [self showErrorView];
        return;
    }
    NSDictionary *wrapper = @{@"intercom_data": [self convertJSONToString:body]};
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:self.sheetURL];
    request.HTTPMethod = @"POST";
    NSError *error = nil;
    request.HTTPBody = [NSJSONSerialization dataWithJSONObject:wrapper options:0 error:&error];
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    if (error) [self showErrorView];
    else {
        [request setValue:@"iOSIntercomWebView" forHTTPHeaderField:@"MobileClient"];
        [self.sheetWebview loadRequest:request];
    }
}
- (NSString *)convertJSONToString:(id)object {
    NSError *error = nil;
    NSData *data = [NSJSONSerialization dataWithJSONObject:object options:0 error:&error];
    if (error) { NSLog(@"[Intercom] ERROR - parssing JSON into data: %@", error); return @""; }
    return [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
}
- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context {
    if ([keyPath isEqualToString:NSStringFromSelector(@selector(estimatedProgress))]) {
        [self showProgressViewWithProgress:(float)self.sheetWebview.estimatedProgress];
        if (!(self.sheetWebview.estimatedProgress < 1)) [self hideProgressView];
    } else [super observeValueForKeyPath:keyPath ofObject:object change:change context:context];
}
- (void)showProgressViewWithProgress:(float)progress { self.progressView.alpha = 1; [self.progressView setProgress:progress animated:YES]; }
- (void)hideProgressView {
    [UIView animateWithDuration:0.3 delay:0.5 options:UIViewAnimationOptionCurveEaseOut animations:^{ self.progressView.alpha = 0; } completion:^(BOOL finished) {
        (void)finished;
        [self.progressView setProgress:0 animated:NO];
    }];
}
- (void)slowlyIncrementProgressView { [self showProgressViewWithProgress:self.progressView.progress + 0.1f]; }
- (void)userContentController:(WKUserContentController *)controller didReceiveScriptMessage:(WKScriptMessage *)message {
    (void)controller;
    id parsed = [self parseScriptMessage:message];
    if ([message.name isEqualToString:@"handleAction"]) {
        id type = parsed[@"type"];
        id payload = parsed[@"payload"];
        if ([type isEqualToString:@"submit-sheet"]) [self handleSubmitSheet:payload];
        else if ([type isEqualToString:@"close"]) [self closeSheet];
        else if ([type isEqualToString:@"set-title"]) [self setSheetTitle:payload];
    }
}
- (id)parseScriptMessage:(WKScriptMessage *)message {
    if ([message.body isKindOfClass:[NSDictionary class]]) return message.body;
    NSData *data = [message.body dataUsingEncoding:NSUTF8StringEncoding];
    NSError *error = nil;
    id object = [NSJSONSerialization JSONObjectWithData:data options:0 error:&error];
    if (error) {
        if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] Error parsing Messenger Sheet Response JSON");
        return @{};
    }
    return object;
}
+ (NSDictionary *)filteredSheetPayload:(id)payload {
    id values = payload[@"sheet_values"];
    return values ? @{@"sheet_values": values} : @{};
}
- (void)handleSubmitSheet:(id)payload {
    NSDictionary *filtered = [ICMMessengerSheetViewController filteredSheetPayload:payload];
    self.progressTimer = [NSTimer scheduledTimerWithTimeInterval:0.2 target:self selector:@selector(slowlyIncrementProgressView) userInfo:nil repeats:YES];
    __weak typeof(self) weakSelf = self;
    [ICMHTTPBridge submitMessengerSheet:filtered uri:self.sheetParams[@"uri"] success:^{
        [weakSelf.progressTimer invalidate];
        [weakSelf hideProgressView];
        [weakSelf closeSheetAndReloadConversation];
    } failure:^(NSError *error) {
        (void)error;
        [weakSelf.progressTimer invalidate];
        [weakSelf hideProgressView];
        [weakSelf showSheetSubmissionError:payload];
    }];
}
- (void)setSheetTitle:(id)payload { self.navigationItem.title = payload[@"title"]; }
- (void)showSheetSubmissionError:(id)payload {
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:[[ICMTranslationManager messenger] translationForKey:@"couldnt_complete_action"] message:[[ICMTranslationManager messenger] translationForKey:@"give_it_another_try"] preferredStyle:UIAlertControllerStyleAlert];
    [alert addAction:[UIAlertAction actionWithTitle:[[ICMTranslationManager messenger] translationForKey:@"cancel"] style:UIAlertActionStyleCancel handler:nil]];
    __weak typeof(self) weakSelf = self;
    [alert addAction:[UIAlertAction actionWithTitle:[[ICMTranslationManager messenger] translationForKey:@"reload"] style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) { (void)action; [weakSelf handleSubmitSheet:payload]; }]];
    [self presentViewController:alert animated:YES completion:nil];
}
- (void)webView:(WKWebView *)webView decidePolicyForNavigationAction:(WKNavigationAction *)action decisionHandler:(void (^)(WKNavigationActionPolicy))decisionHandler {
    (void)webView;
    NSMutableURLRequest *request = [action.request mutableCopy];
    if ([request.allHTTPHeaderFields valueForKey:@"MobileClient"]) decisionHandler(WKNavigationActionPolicyAllow);
    else if (action.navigationType == WKNavigationTypeOther) decisionHandler(WKNavigationActionPolicyAllow);
    else {
        decisionHandler(WKNavigationActionPolicyCancel);
        if ([[ICMLinkOpener shared] isUniversalLink:request.URL]) [[ICMLinkOpener shared] openLink:request.URL];
        else { [request setValue:@"iOSIntercomWebView" forHTTPHeaderField:@"MobileClient"]; [self.sheetWebview loadRequest:request]; }
    }
}
- (void)didSelectErrorAction { [self fetchSheetData]; }
- (void)closeSheet { [self.delegate messengerSheetDidClose:self]; }
- (void)closeSheetAndReloadConversation { [self.delegate messengerSheetDidCloseAndTriggerReload:self]; }
- (void)dealloc {
    if (self.isViewLoaded) [self.sheetWebview removeObserver:self forKeyPath:NSStringFromSelector(@selector(estimatedProgress))];
}
@end
