#import "ICMWebHelpCenterJSBuilder.h"
@implementation ICMWebHelpCenterJSBuilder
+ (NSString *)configureBridgeWithActionHandler:(NSString *)handler {
    return [NSString stringWithFormat:@"iOSHost = { handleAction: function(action) {window.webkit.messageHandlers.%@.postMessage(action);}}", handler];
}
@end
