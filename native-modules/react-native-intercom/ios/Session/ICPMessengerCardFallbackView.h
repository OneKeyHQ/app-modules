#import <UIKit/UIKit.h>
#import <WebKit/WebKit.h>
#import "ICPCardFallbackModel.h"
#import "ITBMessengerCardIntents.h"
@class ICPLoadingView, ICPTextInputPayload;
@interface ICPMessengerCardFallbackView : UIView <WKNavigationDelegate, WKUIDelegate, WKScriptMessageHandler> {
    WKWebView *_webView;
    ICPLoadingView *_activityIndicator;
    ICPCardFallbackModel *_cardFallback;
    __weak id<ICPAppComponentDelegate> _delegate;
    ICPTextInputPayload *_textInputPayload;
    UIView *_shadowView;
    UIView *_backgroundView;
}
@property (nonatomic, strong) WKWebView *webView;
@property (nonatomic, strong) ICPLoadingView *activityIndicator;
@property (nonatomic, strong) ICPCardFallbackModel *cardFallback;
@property (nonatomic, weak) id<ICPAppComponentDelegate> delegate;
@property (nonatomic, strong) ICPTextInputPayload *textInputPayload;
@property (nonatomic, strong) UIView *shadowView;
@property (nonatomic, strong) UIView *backgroundView;
- (instancetype)initWithCardFallbackModel:(ICPCardFallbackModel *)model componentDelegate:(id<ICPAppComponentDelegate>)delegate;
- (void)loadWebview;
- (void)updateCardFallbackModel:(ICPCardFallbackModel *)model;
- (void)createWebview;
- (void)createShadowBackgroundView;
- (void)injectJSScriptsIntoWebview:(WKUserContentController *)controller;
- (void)setupActivityIndicator;
- (void)storeCardHeight:(id)height;
+ (id)fetchCachedCardHeightForURL:(NSString *)url;
- (id)parseScriptMessage:(WKScriptMessage *)message;
- (void)handleTextInput:(NSDictionary *)dictionary;
- (void)sendText:(NSString *)text toWebView:(WKWebView *)view withInputId:(NSString *)inputId;
- (void)openSheetFromURL:(NSDictionary *)dictionary;
- (void)adjustWebviewHeight:(id)height;
- (void)openBrowserFromURL:(NSURL *)url;
- (void)handleMetricEvent:(NSDictionary *)event;
@end
