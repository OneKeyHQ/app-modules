#import "JPUSHMessageReport.h"
#import "JCORELog.h"
@interface JCOREService : NSObject
+ (void)sendOpreationContent:(id)content;
@end
#define REPORT_LOG(lineNo, errorValue)                                                                       \
    [JCORELog log:YES                                                                                        \
            level:0x3ff                                                                                      \
             flag:0x100                                                                                      \
          context:0                                                                                          \
             file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/JPush/JPush/Network/"       \
                  "Report/Http/JPUSHMessageReport.m"                                                         \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineNo                                                                                     \
              tag:nil                                                                                        \
           format:@"message report error: %@", errorValue]
@implementation JPUSHMessageReportRequest
@synthesize messageStateType = _messageStateType, messageID = _messageID, flag = _flag, msgData = _msgData;
- (instancetype)init {
    if ((self = [super init])) {
        _msgData = @"";
        [self handleResponse];
    }
    return self;
}
- (void)setActiveType:(int)type messageID:(NSString *)value flag:(int)valueFlag {
    _messageStateType = (unsigned int)type;
    _messageID = value;
    _flag = valueFlag;
}
- (void)setCustomPushMsgStatus:(int)type messageID:(NSString *)value flag:(int)valueFlag {
    _messageStateType = (unsigned int)type;
    _messageID = value;
    _flag = valueFlag;
}
- (void)setNotiInMessageType:(int)type messageID:(NSString *)value {
    _messageStateType = (unsigned int)type;
    _messageID = value;
    _flag = -10000;
}
- (void)setInAppType:(int)type messageID:(NSString *)value {
    _messageStateType = (unsigned int)type;
    _messageID = value;
    _flag = -10000;
}
- (id)requestInfo {
    if (!self.msgData)
        self.msgData = @"";
    if (_flag == -10000)
        return @{
            @"itime" : @(self.reportTimeStamp),
            @"type" : @"msg_status",
            @"result" : @(_messageStateType),
            @"msg_id" : _messageID,
            @"_j_data_" : self.msgData
        };
    return @{
        @"itime" : @(self.reportTimeStamp),
        @"type" : @"msg_status",
        @"result" : @(_messageStateType),
        @"msg_id" : _messageID,
        @"flag" : @(_flag),
        @"_j_data_" : self.msgData
    };
}
- (void)handleResponse {
    self.completionHandler = ^(id response, NSError *error) {
        REPORT_LOG(0x5f, error);
    };
}
@end
@implementation JPUSHMessageoperationReportRequest
- (instancetype)init {
    if ((self = [super init]))
        [self handleResponse];
    return self;
}
- (void)handleResponse {
    self.completionHandler = ^(id response, NSError *error) {
        REPORT_LOG(0xaf, error);
    };
}
- (id)requestInfo {
    return [NSMutableDictionary
        dictionaryWithDictionary:@{@"type" : @"analysis_info", @"code" : @2025, @"msg" : @"msg click"}];
}
- (void)send {
    if ([JCOREService respondsToSelector:@selector(sendOpreationContent:)])
        [JCOREService sendOpreationContent:self.requestInfo];
}
@end
@implementation JPUSHVoipMessageReport
@synthesize messageID = _messageID;
- (instancetype)init {
    if ((self = [super init]))
        [self handleResponse];
    return self;
}
- (void)handleResponse {
    self.completionHandler = ^(id response, NSError *error) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/JPush/JPush/Network/"
                      "Report/Http/JPUSHVoipMessageReport.m"
             function:__PRETTY_FUNCTION__
                 line:0x17
                  tag:nil
               format:@"message report error: %@", error];
    };
}
- (id)requestInfo {
    return @{
        @"business" : @1,
        @"itime" : @(self.reportTimeStamp),
        @"mid" : self.messageID ?: @"",
        @"platform" : @1,
        @"source" : @2,
        @"type" : @"apns_recv"
    };
}
@end
