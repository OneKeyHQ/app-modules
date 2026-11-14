#import "ICMHTTPBridge.h"
#import "ICMHTTPClient+Metrics.h"
#import "ICMMetric.h"
#import "ICMMetricEvent.h"
#import "ICMDiagnosticSubscriber.h"
#import "ICMConfig.h"
#import <UIKit/UIKit.h>
@implementation ICMHTTPBridge (OriginalMetrics)
+ (void)submitMetrics {
    __block UIBackgroundTaskIdentifier task = UIBackgroundTaskInvalid;
    void (^end)(void) = ^{
        if (task != UIBackgroundTaskInvalid) [[UIApplication sharedApplication] endBackgroundTask:task];
    };
    task = [[UIApplication sharedApplication] beginBackgroundTaskWithName:@"io.intercom.ios.metrics" expirationHandler:end];
    NSSet *events = [[ICMMetric sharedInstance] events];
    NSSet *ops = [[ICMMetric sharedInstance] opsEvents];
    NSSet *eventDictionaries = [events valueForKey:NSStringFromSelector(@selector(eventDictionary))];
    NSSet *opsDictionaries = [ops valueForKey:NSStringFromSelector(@selector(eventDictionary))];
    [ICMHTTPClient submitMetrics:eventDictionaries.allObjects opsMetrics:opsDictionaries.allObjects success:^{
        [[ICMMetric sharedInstance] clearEvents:events opsEvents:ops];
        end();
    } error:^(NSError *error) { (void)error; end(); }];
}
+ (void)submitDiagnostics {
    if (![[ICMConfig sharedConfig].crashReportingConfig[@"enabled"] boolValue]) return;
    __block UIBackgroundTaskIdentifier task = UIBackgroundTaskInvalid;
    void (^end)(void) = ^{
        if (task != UIBackgroundTaskInvalid) {
            [[UIApplication sharedApplication] endBackgroundTask:task];
            task = UIBackgroundTaskInvalid;
        }
    };
    task = [[UIApplication sharedApplication] beginBackgroundTaskWithName:@"io.intercom.ios.diagnostics" expirationHandler:end];
    ICMRecoveredDiagnosticSubscriber *subscriber = [ICMRecoveredDiagnosticSubscriber shared];
    NSDictionary *snapshot = [subscriber fetchStoredPayloadsSnapshot];
    NSArray *ids = snapshot[@"ids"];
    NSArray *payloads = snapshot[@"payloads"];
    if (payloads.count) {
        [ICMHTTPClient submitDiagnostics:payloads success:^{ [subscriber clearPayloadsWithIds:ids]; end(); }
                                  error:^(NSError *error) { (void)error; end(); }];
    } else end();
}
@end
