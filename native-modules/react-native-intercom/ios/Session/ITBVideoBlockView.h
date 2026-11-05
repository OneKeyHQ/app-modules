#import "ITBBlockView.h"
#import "ITBLoadingView.h"
#import <WebKit/WebKit.h>

@interface ITBVideoBlockView : ITBBlockView <WKNavigationDelegate> {
    BOOL _isLoading;
    WKWebView *_webView;
    ITBLoadingView *_loadingView;
    CGRect _currentVideoBounds;
}
@property (nonatomic, strong) WKWebView *webView;
@property (nonatomic) BOOL isLoading;
@property (nonatomic) CGRect currentVideoBounds;
@property (nonatomic, strong) ITBLoadingView *loadingView;
- (void)cleanupVideoSize;
- (void)createWebView;
- (void)createLoadingView;
- (void)loadVideo;
- (NSString *)videoHtmlWithSize:(CGSize)size provider:(NSString *)provider videoId:(NSString *)videoId;
+ (CGSize)videoDisplaySizeFromSize:(CGSize)size withinWidth:(CGFloat)width;
@end
