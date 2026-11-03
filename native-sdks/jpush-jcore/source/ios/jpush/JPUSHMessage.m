#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCOREDecoder.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JPUSHMessage.h"
#import "RecoveredCoreService.h"
@interface JPUSHMessage () {
  unsigned char _messageType;
  unsigned long long _messageID;
  NSString *_messageContent;
}
@end
@implementation JPUSHMessage
@synthesize messageType = _messageType, messageID = _messageID,
            messageContent = _messageContent;
- (instancetype)initWithResponse:(id<JCORETcpRespondProtocol>)response {
  if ((self = [super initWithResponse:response])) {
    unsigned short offset = 0;
    NSData *data = response.bodyData;
    _messageType = [JCOREDecoder uInt8FromBuffer:data offset:&offset];
    _messageID = [JCOREDecoder uInt64FromBuffer:data offset:&offset];
    unsigned short stringOffset = 9;
    NSArray *parts = [[JCOREDecoder stringFromBuffer:data offset:&stringOffset]
        componentsSeparatedByString:@"\n"];
    _messageContent = parts.count ? parts[parts.count - 1] : nil;
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"
                  "ios-jpush/JPush/JPush/Network/Request/JPUSHMessage.m"
         function:__PRETTY_FUNCTION__
             line:0x3b
              tag:nil
           format:@"_messageContent:%@", _messageContent ?: @""];
    id info = [JCOREUtilities dictionaryWithJsonString:_messageContent];
    if ([info isKindOfClass:NSDictionary.class] && info) {
      id data = [info objectForKey:@"_j_data_"];
      if ([data isKindOfClass:NSString.class] && data)
        self.msgData = data;
    }
  }
  return self;
}
- (void)sendReceipt {
  JPUSHMessageReceipt *receipt = [[JPUSHMessageReceipt alloc] initWith:self];
  [JCOREService sendReceipt:receipt];
}
@end
#endif
