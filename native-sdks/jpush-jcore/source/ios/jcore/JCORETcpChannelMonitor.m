#import "JCORETcpChannelController.h"
#include <arpa/inet.h>
@implementation JCORETcpChannelMonitor
+ (void)monitor:(id)data completion:(JCORETcpChannelAck)completion {
    JCORETcpChannelMonitor *monitor = [[JCORETcpChannelMonitor alloc] init];
    monitor->_completion = completion;
    monitor->_rid = [JCORETcpChannelMonitor parseRid:data];
    monitor->_data = [data copy];
}
- (void)dealloc { [NSNotificationCenter.defaultCenter removeObserver:self]; }
- (instancetype)init {
    if ((self = [super init])) {
        [NSNotificationCenter.defaultCenter addObserver:self selector:@selector(receiveAckNotification:) name:@"kJCORETcpChannelResponseNotification" object:nil];
        _sentTimer = [NSTimer scheduledTimerWithTimeInterval:10 target:self selector:@selector(onAckTimeout:) userInfo:nil repeats:NO];
    }
    return self;
}
- (void)stopSentTimer { if (_sentTimer && _sentTimer.isValid) { [_sentTimer invalidate]; _sentTimer = nil; } }
- (void)onAckTimeout:(id)timer { [self performSelector:@selector(stopSentTimer) withObject:nil afterDelay:0]; if (_completion) _completion(6001, _rid, _data); }
+ (unsigned short)parseRid:(id)data {
    if (![data isKindOfClass:NSData.class]) return 0;
    const void *bytes = [data bytes];
    NSUInteger length = [data length];
    // The original accepts 10/11 bytes before reading offsets 10 and 11.
    if ((length & 0xfffe) <= 9 || length < 12) return 0;
    unsigned short raw;
    memcpy(&raw, (const char *)bytes + 10, sizeof(raw));
    return ntohs(raw);
}
- (unsigned short)parseStatusCode:(id)data {
    if (![data isKindOfClass:NSData.class]) return 0xffff;
    const void *bytes = [data bytes];
    NSUInteger length = [data length];
    if ((length & 0xfffc) <= 19 || length < 22) return 0xffff;
    unsigned short raw;
    memcpy(&raw, (const char *)bytes + 20, sizeof(raw));
    return ntohs(raw);
}
- (void)receiveAckNotification:(id)notification {
    if (!notification || ![[notification userInfo] isKindOfClass:NSDictionary.class]) return;
    id data = [notification userInfo][@"data"];
    unsigned short rid = [JCORETcpChannelMonitor parseRid:data];
    if (rid == _rid) { [self performSelector:@selector(stopSentTimer) withObject:nil afterDelay:0]; if (_completion) _completion([self parseStatusCode:data], rid, data); }
}
@end
