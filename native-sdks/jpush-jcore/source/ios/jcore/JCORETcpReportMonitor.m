#import "JCORETcpReportTransport.h"
#import "JCOREDecoder.h"
#include <arpa/inet.h>
@implementation JCORETcpReportMonitor
+ (void)monitor:(id)data completion:(JCORETcpReportAck)completion {
    JCORETcpReportMonitor *monitor = [[JCORETcpReportMonitor alloc] init];
    monitor->_completion = completion;
    monitor->_rid = [monitor parseRequestRid:data];
    monitor->_data = [data copy];
}
- (void)dealloc { [NSNotificationCenter.defaultCenter removeObserver:self]; }
- (instancetype)init {
    if ((self = [super init])) {
        [NSNotificationCenter.defaultCenter addObserver:self selector:@selector(receiveAckNotification:) name:@"kJCORETcpReportResponseNotification" object:nil];
        _sentTimer = [NSTimer scheduledTimerWithTimeInterval:15 target:self selector:@selector(onAckTimeout:) userInfo:nil repeats:NO];
    }
    return self;
}
- (void)stopSentTimer { if (_sentTimer && _sentTimer.isValid) { [_sentTimer invalidate]; _sentTimer = nil; } }
- (void)onAckTimeout:(id)timer { [self performSelector:@selector(stopSentTimer) withObject:nil afterDelay:0]; if (_completion) _completion(2002, _rid, _data); }
- (unsigned short)parseRequestRid:(id)data {
    if (![data isKindOfClass:NSData.class]) return 0;
    const void *bytes = [data bytes];
    NSUInteger length = [data length];
    // The original's >34 check reads bytes 35 and 36; reject its undefined short-buffer cases.
    if ((unsigned short)length <= 34 || length < 37) return 0;
    unsigned short raw;
    memcpy(&raw, (const char *)bytes + 35, sizeof(raw));
    return ntohs(raw);
}
- (unsigned short)parseResponseRid:(id)data {
    if (![data isKindOfClass:NSData.class]) return 0;
    const void *bytes = [data bytes];
    NSUInteger length = [data length];
    if ((unsigned short)length <= 14 || length < 17) return 0;
    unsigned short raw;
    memcpy(&raw, (const char *)bytes + 15, sizeof(raw));
    return ntohs(raw);
}
- (unsigned short)parseStatusCode:(id)data {
    if (![data isKindOfClass:NSData.class]) return 0xffff;
    unsigned short offset = 17;
    return [JCOREDecoder uInt16FromBuffer:data offset:&offset] & 0xff;
}
- (unsigned char)parseResponseVer:(id)data {
    if (![data isKindOfClass:NSData.class]) return 0;
    unsigned short offset = 2;
    return [JCOREDecoder uInt8FromBuffer:data offset:&offset];
}
- (void)receiveAckNotification:(id)notification {
    if (!notification || ![[notification userInfo] isKindOfClass:NSDictionary.class]) return;
    id data = [notification userInfo][@"data"];
    if ([data length] != 19) { if (_completion) _completion(2004, _rid, _data); return; }
    if ([self parseResponseVer:data] != 1) { if (_completion) _completion(2005, _rid, _data); return; }
    unsigned short rid = [self parseResponseRid:data];
    if (rid != _rid) return;
    [self performSelector:@selector(stopSentTimer) withObject:nil afterDelay:0];
    if (_completion) _completion([self parseStatusCode:data], rid, data);
}
@end
