#import "ICPMessengerCardFallbackView.h"
#import "ICPCardFallbackJSBuilder.h"
#import "ICPLoadingView.h"
#import "ICPTextInputPayload.h"
@implementation ICPMessengerCardFallbackView
@synthesize webView = _webView, activityIndicator = _activityIndicator, cardFallback = _cardFallback;
@synthesize delegate = _delegate, textInputPayload = _textInputPayload, shadowView = _shadowView, backgroundView = _backgroundView;
- (instancetype)initWithCardFallbackModel:(ICPCardFallbackModel *)model componentDelegate:(id<ICPAppComponentDelegate>)delegate {
    self = [super init];
    if (self) {
        _cardFallback = model;
        _delegate = delegate;
        [self createWebview];
        self.backgroundColor = [UIColor clearColor];
    }
    return self;
}
- (void)loadWebview {
    NSString *current = self.webView.URL.absoluteString;
    NSString *next = self.cardFallback.fallbackURL.absoluteString;
    if ([current isEqualToString:next] && self.cardFallback.webviewLoaded) return;
    [self setupActivityIndicator];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:self.cardFallback.fallbackURL];
    [self.webView loadRequest:request];
}
- (void)updateCardFallbackModel:(ICPCardFallbackModel *)model {
    self.cardFallback = model;
    [self setNeedsLayout];
    [self layoutIfNeeded];
}
- (void)createWebview {
    WKUserContentController *controller = [WKUserContentController new];
    [self injectJSScriptsIntoWebview:controller];
    WKWebViewConfiguration *configuration = [WKWebViewConfiguration new];
    configuration.userContentController = controller;
    self.webView = [[WKWebView alloc] initWithFrame:CGRectZero configuration:configuration];
    self.webView.navigationDelegate = self;
    self.webView.UIDelegate = self;
    self.webView.accessibilityIdentifier = @"intercom card webview fallback";
    self.webView.allowsBackForwardNavigationGestures = NO;
    self.webView.scrollView.scrollEnabled = NO;
    self.webView.allowsLinkPreview = NO;
    self.webView.layer.cornerRadius = 3;
    self.webView.clipsToBounds = YES;
    self.webView.backgroundColor = [UIColor whiteColor];
    [self addSubview:self.webView];
    if (self.cardFallback.showCardBorder) [self createShadowBackgroundView];
    [self bringSubviewToFront:self.webView];
}
- (void)createShadowBackgroundView {
    self.backgroundView = [UIView new];
    self.backgroundView.backgroundColor = [UIColor whiteColor];
    self.backgroundView.layer.cornerRadius = 3;
    UIColor *shadow = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.4];
    self.backgroundView.layer.shadowColor = shadow.CGColor;
    self.backgroundView.layer.shadowOffset = CGSizeZero;
    self.backgroundView.layer.shadowOpacity = 0.4f;
    self.backgroundView.layer.shadowRadius = 1;
    self.backgroundView.layer.masksToBounds = NO;
    [self addSubview:self.backgroundView];
    self.shadowView = [UIView new];
    self.shadowView.backgroundColor = [self.cardFallback.appPrimaryColor colorWithAlphaComponent:0.5];
    self.shadowView.layer.cornerRadius = 3;
    [self addSubview:self.shadowView];
}
- (void)injectJSScriptsIntoWebview:(WKUserContentController *)controller {
    WKUserScript *viewport = [[WKUserScript alloc] initWithSource:[ICPCardFallbackJSBuilder viewportScript] injectionTime:WKUserScriptInjectionTimeAtDocumentEnd forMainFrameOnly:YES];
    [controller addUserScript:viewport];
    WKUserScript *selection = [[WKUserScript alloc] initWithSource:[ICPCardFallbackJSBuilder disableSelectionScript] injectionTime:WKUserScriptInjectionTimeAtDocumentEnd forMainFrameOnly:YES];
    [controller addUserScript:selection];
    WKUserScript *callout = [[WKUserScript alloc] initWithSource:[ICPCardFallbackJSBuilder disableContentCalloutScript] injectionTime:WKUserScriptInjectionTimeAtDocumentEnd forMainFrameOnly:YES];
    [controller addUserScript:callout];
    WKUserScript *bridge = [[WKUserScript alloc] initWithSource:[ICPCardFallbackJSBuilder configureBridgeWithActionHandler:@"handleAction"] injectionTime:WKUserScriptInjectionTimeAtDocumentStart forMainFrameOnly:YES];
    [controller addUserScript:bridge];
    [controller addScriptMessageHandler:self name:@"handleAction"];
}
- (void)setupActivityIndicator {
    if (self.activityIndicator) return;
    UIImage *image = self.cardFallback.loadingSpinnerImage;
    UIColor *color = self.cardFallback.loadingSpinnerColor;
    self.activityIndicator = [[ICPLoadingView alloc] initWithLoadingImage:image tintColor:color];
    [self addSubview:self.activityIndicator];
}
- (void)layoutSubviews {
    [super layoutSubviews];
    CGFloat x = self.cardFallback.cardInsets.left;
    CGFloat y = self.cardFallback.cardInsets.top + 2;
    CGFloat width = self.cardFallback.cardSize.width;
    width -= self.cardFallback.cardInsets.left;
    width -= self.cardFallback.cardInsets.right;
    CGFloat height = self.cardFallback.cardSize.height;
    height -= self.cardFallback.cardInsets.bottom;
    height += -2;
    self.webView.frame = CGRectMake(x, y, width, height);
    CGRect webFrame = self.webView.frame;
    webFrame.origin.y += -2;
    webFrame.size.height += 2;
    self.shadowView.frame = webFrame;
    self.backgroundView.frame = self.shadowView.frame;
    CGFloat indicatorWidth = self.activityIndicator.intrinsicContentSize.width;
    CGFloat indicatorHeight = self.activityIndicator.intrinsicContentSize.height;
    self.activityIndicator.frame = CGRectMake(0, 0, indicatorWidth, indicatorHeight);
    self.activityIndicator.center = self.webView.center;
}
- (void)storeCardHeight:(id)height {
    if (self.cardFallback) {
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *key = self.cardFallback.cardId;
        [defaults setObject:height forKey:key];
    }
}
+ (id)fetchCachedCardHeightForURL:(NSString *)url {
    return [[NSUserDefaults standardUserDefaults] objectForKey:url];
}
- (void)userContentController:(WKUserContentController *)controller didReceiveScriptMessage:(WKScriptMessage *)message {
    (void)controller;
    id data = [self parseScriptMessage:message];
    if (![message.name isEqualToString:@"handleAction"]) return;
    NSString *type = data[@"type"];
    if ([type isEqualToString:@"INPUT_TEXT"]) [self handleTextInput:data[@"payload"]];
    else if ([type isEqualToString:@"OPEN_SHEET"]) [self openSheetFromURL:data];
    else if ([type isEqualToString:@"HEIGHT_CHANGE_EVENT"]) [self adjustWebviewHeight:[data valueForKeyPath:@"payload.height"]];
    else if ([type isEqualToString:@"OPEN_URL"]) {
        NSURL *url = [NSURL URLWithString:[data valueForKeyPath:@"payload.url"]];
        [self openBrowserFromURL:url];
    } else if ([type isEqualToString:@"METRIC_EVENT"]) [self handleMetricEvent:data[@"payload"]];
}
- (id)parseScriptMessage:(WKScriptMessage *)message {
    if ([message.body isKindOfClass:[NSDictionary class]]) return message.body;
    NSData *data = [message.body dataUsingEncoding:NSUTF8StringEncoding];
    NSError *error = nil;
    id decoded = [NSJSONSerialization JSONObjectWithData:data options:0 error:&error];
    if (error) {
        NSLog(@"Error parsing Messenger Card Response JSON");
        return @{};
    }
    return decoded;
}
- (void)handleTextInput:(NSDictionary *)dictionary {
    self.textInputPayload = [ICPTextInputPayload new];
    self.textInputPayload.title = dictionary[@"label"];
    self.textInputPayload.placeholder = dictionary[@"placeholder"];
    self.textInputPayload.currentText = dictionary[@"currentValue"];
    self.textInputPayload.inputId = dictionary[@"id"];
    self.textInputPayload.messengerAppId = [NSString stringWithFormat:@"%@", dictionary[@"messengerAppId"]];
    __weak ICPMessengerCardFallbackView *weakSelf = self;
    self.textInputPayload.completionBlock = ^(UIAlertController *alert) {
        ICPMessengerCardFallbackView *receiver = weakSelf;
        NSString *text = alert.textFields.firstObject.text;
        WKWebView *web = weakSelf.webView;
        NSString *inputId = weakSelf.textInputPayload.inputId;
        [receiver sendText:text toWebView:web withInputId:inputId];
    };
    id<ICPAppComponentDelegate> delegate = self.delegate;
    ICPTextInputPayload *payload = self.textInputPayload;
    [delegate component:self handleTextInput:payload];
}
- (void)sendText:(NSString *)text toWebView:(WKWebView *)view withInputId:(NSString *)inputId {
    NSString *encoded = [text stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet alphanumericCharacterSet]];
    NSString *script = [NSString stringWithFormat:@"window.fallbackApp.bridgeEvent('%@', { value:'%@', id:'%@'})", @"UPDATE_TEXT", encoded, inputId];
    [view evaluateJavaScript:script completionHandler:nil];
}
- (void)openSheetFromURL:(NSDictionary *)dictionary {
    NSURL *url = [NSURL URLWithString:[dictionary valueForKeyPath:@"payload.url"]];
    id params = [dictionary valueForKeyPath:@"payload.params"];
    [self.delegate component:self openSheetWithURL:url sheetParams:params];
}
- (void)adjustWebviewHeight:(id)height {
    double incoming = [height integerValue];
    if (incoming == self.cardFallback.cardSize.height) return;
    CGFloat width = self.cardFallback.cardSize.width;
    CGFloat newHeight = [height integerValue];
    self.cardFallback.cardSize = CGSizeMake(width, newHeight);
    [self storeCardHeight:height];
    [self.delegate reloadRequiredForComponent:self];
    [self setNeedsLayout];
}
- (void)openBrowserFromURL:(NSURL *)url {
    if (!url) return;
    [self.delegate component:self openURL:url];
}
- (void)handleMetricEvent:(NSDictionary *)event { [self.delegate component:self logMetricEvent:event]; }
- (void)webView:(WKWebView *)webView didStartProvisionalNavigation:(WKNavigation *)navigation {
    (void)webView; (void)navigation;
    [self.activityIndicator startSpinning];
}
- (void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation {
    (void)webView; (void)navigation;
    [self.activityIndicator stopSpinning];
    self.webView.hidden = NO;
    self.cardFallback.webviewLoaded = YES;
}
- (void)webView:(WKWebView *)webView didFailProvisionalNavigation:(WKNavigation *)navigation withError:(NSError *)error {
    (void)webView; (void)navigation; (void)error;
    [self.activityIndicator stopSpinning];
}
- (void)webView:(WKWebView *)webView didFailNavigation:(WKNavigation *)navigation withError:(NSError *)error {
    (void)webView; (void)navigation; (void)error;
    [self.activityIndicator stopSpinning];
}
- (void)webView:(WKWebView *)webView decidePolicyForNavigationAction:(WKNavigationAction *)action decisionHandler:(void (^)(WKNavigationActionPolicy))decisionHandler {
    (void)webView;
    NSString *host = self.cardFallback.fallbackURL.host;
    NSString *destination = action.request.URL.host;
    if ([host isEqualToString:destination]) decisionHandler(WKNavigationActionPolicyAllow);
    else {
        [self openBrowserFromURL:action.request.URL];
        decisionHandler(WKNavigationActionPolicyCancel);
    }
}
- (void)dealloc {
    [self.webView stopLoading];
    self.webView.navigationDelegate = nil;
    self.webView.UIDelegate = nil;
    [self.webView removeFromSuperview];
    self.webView = nil;
}
- (void)removeFromSuperview {
    [super removeFromSuperview];
    [self.webView.configuration.userContentController removeScriptMessageHandlerForName:@"handleAction"];
}
@end
