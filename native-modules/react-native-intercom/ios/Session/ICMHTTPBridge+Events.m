#import "ICMHTTPBridge.h"
#import "ICMHTTPClient+Events.h"
#import "ICMPresentationManager.h"
#import "../Identity/ICMUtils.h"
@implementation ICMHTTPBridge (OriginalEvents)
+ (void)logEventWithName:(NSString *)name metadata:(NSDictionary *)metadata sentFromBackground:(BOOL)background {
    [ICMHTTPClient logEventWithName:name metadata:metadata sentFromBackground:background carouselVisible:[[ICMPresentationManager sharedInstance] isContentCurrentlyPresented] success:^(ICMBaseResponse *response) {
        if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - Successfully logged event.");
        [ICMHTTPBridge handleBaseResponse:response];
    } error:nil];
}
+ (void)logBackgroundDisabledError {
    NSLog(@"[Intercom] ERROR - Your request was not sent because the app is in the background. Please contact Intercom to enable background requests.");
}
@end
