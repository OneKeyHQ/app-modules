#import "ITBVideoBlockView.h"
#import "ITBVideoBlock.h"
#import <math.h>
#import <stdint.h>

// Preserve FCVTZS-to-int64 followed by the original format's low 32-bit consumption.
static int32_t ITBVideoFormatDimension(CGFloat value) {
    int64_t converted;
    if (isnan(value)) converted = 0;
    else if (value >= 0x1p63) converted = INT64_MAX;
    else if (value <= -0x1p63) converted = INT64_MIN;
    else converted = (int64_t)value;
    return (int32_t)(uint32_t)(uint64_t)converted;
}

@implementation ITBVideoBlockView
@synthesize webView = _webView, isLoading = _isLoading;
@synthesize currentVideoBounds = _currentVideoBounds, loadingView = _loadingView;

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.currentVideoBounds = CGRectZero;
        [self createWebView];
        [self createLoadingView];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(cleanupVideoSize)
            name:@"kITBVideoViewInvalidateVideoBoundsNotification" object:nil];
    }
    return self;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    ITBVideoBlock *block = (ITBVideoBlock *)self.block;
    CGFloat width = block.videoSize.width;
    CGFloat height = block.videoSize.height;
    self.webView.bounds = CGRectMake(0, 0, width, height);
    CGFloat centerX = CGRectGetMidX(self.bounds);
    CGFloat centerY = CGRectGetMidY(self.bounds);
    self.webView.center = CGPointMake(centerX, centerY);
    self.loadingView.bounds = self.webView.bounds;
    self.loadingView.center = self.webView.center;
    if (!CGRectEqualToRect(self.currentVideoBounds, self.webView.bounds)) [self loadVideo];
}
- (void)cleanupVideoSize {
    self.currentVideoBounds = CGRectZero;
    [self setNeedsLayout];
}
- (void)createWebView {
    self.webView = [WKWebView new];
    self.webView.scrollView.scrollEnabled = NO;
    self.webView.scrollView.scrollsToTop = NO;
    self.webView.alpha = 0;
    self.webView.navigationDelegate = self;
    [self addSubview:self.webView];
}
- (void)createLoadingView {
    self.loadingView = [ITBLoadingView new];
    [self addSubview:self.loadingView];
}
- (void)loadVideo {
    self.webView.alpha = 0;
    [self.loadingView isLoading:YES];
    CGFloat width = CGRectGetWidth(self.webView.bounds);
    CGFloat height = CGRectGetHeight(self.webView.bounds);
    NSString *provider = ((ITBVideoBlock *)self.block).provider;
    NSString *videoId = ((ITBVideoBlock *)self.block).videoId;
    NSString *html = [self videoHtmlWithSize:CGSizeMake(width, height) provider:provider videoId:videoId];
    self.uniqueIdentifier = self.block.blockUniqueIdentifier;
    self.currentVideoBounds = self.webView.bounds;
    [self.webView loadHTMLString:html baseURL:nil];
}
- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [self.webView loadHTMLString:@"" baseURL:nil];
    [self.webView stopLoading];
    self.webView.navigationDelegate = nil;
    self.webView.scrollView.delegate = nil;
    [self.webView removeFromSuperview];
    self.webView = nil;
}
- (NSString *)videoHtmlWithSize:(CGSize)size provider:(NSString *)provider videoId:(NSString *)videoId {
    NSString *format;
    if ([@"youtube" isEqualToString:provider]) format = @"<!DOCTYPE html>\n<html>\n  <head><meta name=\"viewport\" content=\"initial-scale=1.0,user-scalable=no\"/></head>\n  <body style=\"margin:0;\">\n    <!-- 1. The <iframe> (and video player) will replace this <div> tag. -->\n    <div id=\"player\"></div>\n\n    <script>\n      // 2. This code loads the IFrame Player API code asynchronously.\n      var tag = document.createElement('script');\n\n      tag.src = \"https://www.youtube.com/iframe_api\";\n      var firstScriptTag = document.getElementsByTagName('script')[0];\n      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);\n\n      // 3. This function creates an <iframe> (and YouTube player)\n      //    after the API code downloads.\n      var player;\n      function onYouTubeIframeAPIReady() {\n        player = new YT.Player('player', {\n           videoId: '%@',\n           width: '%d',\n           height: '%d',\n           playerVars: {\n               modestbranding: 0,\n               autohide: 1,\n               controls: 1,\n               showinfo: 0,\n           }\n        });\n      }\n    </script>\n  </body>\n</html>";
    else if ([@"vimeo" isEqualToString:provider]) format = @"<html><head><meta name=\"viewport\" content=\"initial-scale=1.0,user-scalable=no\"/></head><body style=\"margin:0;\"><iframe src=\"https://player.vimeo.com/video/%@\" width=\"%d\" height=\"%d\" frameborder=\"0\" scrolling=\"no\"</iframe></body></html>";
    else if ([@"wistia" isEqualToString:provider]) format = @"<html><head><meta name=\"viewport\" content=\"initial-scale=1.0,user-scalable=no\"/></head><body style=\"margin:0;\"><iframe class=\"wistia_embed\" name=\"wistia_embed\" src=\"https://fast.wistia.net/embed/iframe/%@?volumeControl=true&fullscreenButton=true&controlsVisibleOnLoad=true allowtransparency=\"true\" frameborder=\"0\" scrolling=\"no\" width=\"%d\" height=\"%d\"></iframe></body></html>";
    else if ([@"streamio" isEqualToString:provider]) format = @"<html><head><meta name=\"viewport\" content=\"initial-scale=1.0,user-scalable=no\"/></head><body style=\"margin:0;\"><iframe class=\"streamio_embed\" name=\"streamio_embed\" src=\"https://streamio.com/api/v1/videos/%@/public_show\" frameborder=\"0\" scrolling=\"no\" width=\"%d\" height=\"%d\"></iframe></body></html>";
    else if ([@"loom" isEqualToString:provider]) format = @"<html><head><meta name=\"viewport\" content=\"initial-scale=1.0,user-scalable=no\"/></head><body style=\"margin:0;\"><iframe src=\"https://www.loom.com/embed/%@\" width=\"%d\" height=\"%d\" frameborder=\"0\" webkitallowfullscreen allowfullscreen></iframe></body></html>";
    else if ([@"vidyard" isEqualToString:provider]) format = @"<html><head><meta name=\"viewport\" content=\"initial-scale=1.0,user-scalable=no\"/></head><body style=\"margin:0;\"><iframe class=\"vidyard_iframe\" src=\"https://play.vidyard.com/%@\" width=\"%d\" height=\"%d\" frameborder=\"0\" webkitallowfullscreen allowfullscreen></iframe></body></html>";
    else return nil;
    return [NSString stringWithFormat:format, videoId, ITBVideoFormatDimension(size.width), ITBVideoFormatDimension(size.height)];
}
- (void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation {
    (void)webView;
    (void)navigation;
    if (self.webView.superview == nil) [self addSubview:self.webView];
    [self.loadingView isLoading:NO];
    [UIView animateWithDuration:0.8 delay:0 options:0 animations:^{ self.webView.alpha = 1; } completion:nil];
}
- (void)webView:(WKWebView *)webView didFailNavigation:(WKNavigation *)navigation withError:(NSError *)error {
    (void)webView;
    (void)navigation;
    (void)error;
    [self.loadingView isLoading:NO];
    self.webView.alpha = 0;
    [self.loadingView handleLoadingFailure];
}
+ (CGSize)estimateSizeForBlock:(ITBBlock *)block withWidth:(CGFloat)width constraintWidth:(BOOL)constraintWidth {
    CGFloat height = (CGFloat)floorf((float)((width * 9.0) * 0.0625));
    CGFloat maxWidth = [self maxWidthForBlockContentBasedInWidth:width forBlock:block constraintWidth:constraintWidth] - 20.0;
    CGSize size = [self videoDisplaySizeFromSize:CGSizeMake(width, height) withinWidth:maxWidth];
    ((ITBVideoBlock *)block).videoSize = size;
    if (!constraintWidth) return CGSizeMake((CGFloat)floorf((float)width), (CGFloat)floorf((float)size.height) + 20.0);
    return CGSizeMake((CGFloat)floorf((float)(size.width + 20.0)), (CGFloat)floorf((float)(size.height + 20.0)));
}
+ (CGSize)videoDisplaySizeFromSize:(CGSize)size withinWidth:(CGFloat)width {
    if (CGSizeEqualToSize(size, CGSizeZero)) return CGSizeZero;
    if (!(size.width > width)) return size;
    CGFloat ratio = size.width / width;
    return CGSizeMake(width, size.height / ratio);
}
- (void)configureWithBlock:(ITBBlock *)block {
    [super configureWithBlock:block];
    ITBLoadingView *loadingView = self.loadingView;
    NSUInteger colorType = ((ITBVideoBlock *)block).loadingViewColorType;
    loadingView.loadingViewColorType = colorType;
    if (self.block.blockUniqueIdentifier.length != 0 &&
        ![self.block.blockUniqueIdentifier isEqualToString:self.uniqueIdentifier]) [self cleanupVideoSize];
}
@end
