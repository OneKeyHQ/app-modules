#import <UIKit/UIKit.h>
#import <WebKit/WebKit.h>
#import "ICMPresentationProtocols.h"
@class ICMMessengerSheetViewController;
@protocol ICMMessengerSheetViewControllerDelegate <NSObject>
- (void)messengerSheetDidClose:(ICMMessengerSheetViewController *)sheet;
- (void)messengerSheetDidCloseAndTriggerReload:(ICMMessengerSheetViewController *)sheet;
@end

@class ICMErrorView;
@interface ICMMessengerSheetViewController : UIViewController <WKNavigationDelegate, WKScriptMessageHandler, ICMErrorViewDelegate> {
    NSURL *_sheetURL;
    WKWebView *_sheetWebview;
    id<ICMMessengerSheetViewControllerDelegate> _delegate;
    NSDictionary *_sheetParams;
    UIProgressView *_progressView;
    ICMErrorView *_errorView;
    NSTimer *_progressTimer;
}
@property (nonatomic, strong) NSURL *sheetURL;
@property (nonatomic, strong) WKWebView *sheetWebview;
@property (nonatomic, strong) id<ICMMessengerSheetViewControllerDelegate> delegate;
@property (nonatomic, strong) NSDictionary *sheetParams;
@property (nonatomic, strong) UIProgressView *progressView;
@property (nonatomic, strong) ICMErrorView *errorView;
@property (nonatomic, strong) NSTimer *progressTimer;
- (instancetype)initWithSheetURL:(NSURL *)url params:(NSDictionary *)params delegate:(id<ICMMessengerSheetViewControllerDelegate>)delegate;
- (void)createWebview;
- (void)setupProgressview;
- (void)showErrorView;
- (void)layoutErrorView;
- (void)fetchSheetData;
- (void)loadSheet:(id)body;
- (NSString *)convertJSONToString:(id)object;
- (void)showProgressViewWithProgress:(float)progress;
- (void)hideProgressView;
- (void)slowlyIncrementProgressView;
- (id)parseScriptMessage:(WKScriptMessage *)message;
+ (NSDictionary *)filteredSheetPayload:(id)payload;
- (void)handleSubmitSheet:(id)payload;
- (void)setSheetTitle:(id)payload;
- (void)showSheetSubmissionError:(id)payload;
- (void)didSelectErrorAction;
- (void)closeSheet;
- (void)closeSheetAndReloadConversation;
@end
