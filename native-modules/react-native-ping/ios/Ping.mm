#import "Ping.h"
#import "GBPing/GBPing.h"
#import "LHNetwork/LHNetwork.h"
#import "LHNetwork/LHDefinition.h"

@interface Ping ()
@property (nonatomic, strong) dispatch_queue_t queue;
@end

@implementation Ping

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativePingSpecJSI>(params);
}

+ (NSString *)moduleName
{
    return @"RNReactNativePing";
}

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

- (dispatch_queue_t)methodQueue
{
    if (!_queue) {
        _queue = dispatch_queue_create("com.onekey.RNPing", DISPATCH_QUEUE_SERIAL);
    }
    return _queue;
}

- (void)start:(NSString *)ipAddress
       option:(JS::NativeRNReactNativePing::SpecStartOption &)option
      resolve:(RCTPromiseResolveBlock)resolve
       reject:(RCTPromiseRejectBlock)reject
{
    __block GBPing *ping = [[GBPing alloc] init];
    ping.timeout = 1.0;
    ping.payloadSize = 56;
    ping.pingPeriod = 0.9;
    ping.host = ipAddress;

    unsigned long long timeout = 1000;
    if (option.timeout().has_value()) {
        timeout = (unsigned long long)option.timeout().value();
        ping.timeout = (NSTimeInterval)timeout;
    }

    if (option.payloadSize().has_value()) {
        unsigned long long payloadSize = (unsigned long long)option.payloadSize().value();
        ping.payloadSize = payloadSize;
    }

    dispatch_queue_t capturedQueue = _queue;

    [ping setupWithBlock:^(BOOL success, NSError *_Nullable err) {
        if (!success) {
            reject(@(err.code).stringValue, err.domain, err);
            return;
        }
        [ping startPingingWithBlock:^(GBPingSummary *summary) {
            if (!ping) {
                return;
            }
            resolve(@(@(summary.rtt * 1000).intValue));
            [ping stop];
            ping = nil;
        } fail:^(NSError *_Nonnull error) {
            if (!ping) {
                return;
            }
            reject(@(error.code).stringValue, error.domain, error);
            [ping stop];
            ping = nil;
        }];
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(timeout * NSEC_PER_MSEC)), capturedQueue, ^{
            if (!ping) {
                return;
            }
            ping = nil;
            DEFINE_NSError(timeoutError, PingUtil_Message_Timeout)
            reject(@(timeoutError.code).stringValue, timeoutError.domain, timeoutError);
        });
    }];
}

@end
