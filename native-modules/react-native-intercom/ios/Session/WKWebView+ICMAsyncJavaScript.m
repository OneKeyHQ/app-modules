#import "WKWebView+ICMAsyncJavaScript.h"
@implementation WKWebView (ICMAsyncJavaScript)
- (void)icm_callAsyncJavaScript:(NSString *)javascript arguments:(NSDictionary<NSString *, id> *)arguments completionHandler:(void (^)(id result, NSError *error))completion {
    [self callAsyncJavaScript:javascript arguments:arguments inFrame:nil inContentWorld:[WKContentWorld pageWorld] completionHandler:completion];
}
@end
