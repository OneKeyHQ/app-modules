#import <WebKit/WebKit.h>
@interface WKWebView (ICMAsyncJavaScript)
- (void)icm_callAsyncJavaScript:(NSString *)javascript arguments:(NSDictionary<NSString *, id> *)arguments completionHandler:(void (^)(id result, NSError *error))completion NS_SWIFT_DISABLE_ASYNC;
@end
