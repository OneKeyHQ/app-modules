// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICPCardFallbackJSBuilder.h"
@implementation ICPCardFallbackJSBuilder
+ (NSString *)configureBridgeWithActionHandler:(NSString *)handler {
    return [NSString stringWithFormat:@"iOSHost = { handleAction: function(action) {window.webkit.messageHandlers.%@.postMessage(action);}}", handler];
}
+ (NSString *)viewportScript { return @"var meta = document.createElement('meta'); meta.setAttribute('name', 'viewport'); meta.setAttribute('initial-scale', '1.0'); meta.setAttribute('maximum-scale', '1.0'); meta.setAttribute('minimum-scale', '1.0'); meta.setAttribute('user-scalable', 'no'); document.getElementsByTagName('head')[0].appendChild(meta);"; }
+ (NSString *)disableSelectionScript { return @"document.documentElement.style.webkitUserSelect='none';"; }
+ (NSString *)disableContentCalloutScript { return @"document.documentElement.style.webkitTouchCallout='none';"; }
@end
