#import "JPushWebReport.h"
#import "JCORELog.h"
@implementation JPushWebReport
@synthesize messageID = _messageID, dataStr = _dataStr, result = _result;
- (instancetype)init {
  if ((self = [super init])) {
    self.result = 0;
    self.dataStr = @"";
    [self handleResponse];
  }
  return self;
}
- (void)handleResponse {
  self.completionHandler = ^(id response, NSError *error) {
    if (error) {
      [JCORELog log:YES level:0x3ff flag:0x100 context:0
          file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"
               "ios-jpush/JPush/JPush/Network/Report/Http/JPushWebReport.m"
          function:__PRETTY_FUNCTION__ line:0x19 tag:nil
          format:@"message report error: %@", error];
    }
  };
}
- (id)requestInfo {
  return @{
    @"itime" : @(self.reportTimeStamp), @"type" : @"msg_status",
    @"result" : @(self.result), @"msg_id" : self.messageID ?: @"",
    @"data" : self.dataStr ?: @""
  };
}
@end
