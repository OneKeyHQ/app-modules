// Source recovery modifications: see docs/ARTICLE_RECOVERY.md.
#import "ICMWebHelpCenterJSBuilder.h"
@implementation ICMWebHelpCenterJSBuilder
+ (NSString *)configureBridgeWithActionHandler:(NSString *)handler {
    return [NSString stringWithFormat:@"iOSHost = { handleAction: function(action) {window.webkit.messageHandlers.%@.postMessage(action);}}", handler];
}
@end
