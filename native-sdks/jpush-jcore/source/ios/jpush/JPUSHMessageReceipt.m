#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCOREUtilities.h"
#import "JPUSHMessage.h"
@implementation JPUSHMessageReceipt
@synthesize statusCode = statusCode;
@dynamic algorithm, debugDescription, description, hash, msgData, protocolType,
    rid, superclass, uid, version;
- (instancetype)initWith:(JPUSHMessage *)message {
  if (!self)
    return nil;
  self.version = 3;
  self.protocolType = message.protocolType;
  self.rid = message.rid;
  self.bodyData = [message.bodyData copy];
  if (message.messageContent && message.messageContent.length) {
    id info =
        [JCOREUtilities dataToJSON:[message.messageContent
                                       dataUsingEncoding:NSUTF8StringEncoding]];
    if ([info objectForKey:@"code"])
      self.statusCode = [[info objectForKey:@"code"] intValue];
    else
      self.statusCode = 0;
  } else
    self.statusCode = 0;
  self.msgData = message.msgData;
  return self;
}
@end
#endif
