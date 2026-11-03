#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCOREDecoder.h"
#import "JCORELog.h"
#import "JPUSHCache.h"
#import "JPUSHUserPropertyController.h"
#import "JPUSHUserPropertyRequest.h"
@interface JPUSHClientController : NSObject
+ (instancetype)sharedClient;
- (JPUSHUserPropertyController *)userPropertyManager;
@end
#define USER_REQUEST_LOG(asyncValue, flagValue, lineNo, formatValue, ...)                                    \
    [JCORELog log:asyncValue                                                                                 \
            level:0x3ff                                                                                      \
             flag:flagValue                                                                                  \
          context:0                                                                                          \
             file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/JPush/JPush/Network/"       \
                  "Request/JPUSHUserProperty.m"                                                              \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineNo                                                                                     \
              tag:nil                                                                                        \
           format:formatValue, ##__VA_ARGS__]
@implementation JPUSHUserPropertyRequest
@synthesize type = _type, content = _content;
- (instancetype)init {
    if ((self = [super init])) {
        self.controlFlag = 23;
        self.version = 0;
        self.protocolType = 26;
    }
    return self;
}
- (NSData *)bodyData {
    NSMutableData *data = [[NSMutableData alloc] init];
    [data jcore_appendInt8:_type];
    [data jcore_appendInt8:1];
    [data jcore_appendString:_content];
    USER_REQUEST_LOG(YES, 0x80, 0x21, @"try to upload sdk,property:%d content:%@", _type, _content);
    return data;
}
@end
@implementation JPUSHUserPropertyResponse
- (void)handleReceipt {
    unsigned short offset = 2;
    self.statusCode = [JCOREDecoder uInt16FromBuffer:self.bodyData offset:&offset];
    if (self.statusCode) {
        offset = 2;
        self.errorDescription = [JCOREDecoder stringFromBuffer:self.bodyData offset:&offset];
    }
    if (!self.statusCode) {
        JPUSHUserPropertyRequest *request =
            [[JPUSHClientController sharedClient].userPropertyManager findRequest:self.rid];
        if ([request isKindOfClass:JPUSHUserPropertyRequest.class]) {
            if ([request type] == 7)
                [self updateUserProperty:request];
            USER_REQUEST_LOG(YES, 0x80, 0x3e, @"sdk version upload success property:%d", [request type]);
        }
    } else
        USER_REQUEST_LOG(NO, 1, 0x42, @"property upload failed, error code: %d", self.statusCode);
    [[JPUSHClientController sharedClient].userPropertyManager handleUserPropertyResponse:self];
}
- (void)updateUserProperty:(JPUSHUserPropertyRequest *)request {
    id stored = [JCORECacheChannel userProperty];
    if (![stored isKindOfClass:NSArray.class])
        stored = [JPUSHCache objectForFile:@"__JPUSHUserPropertyKey"];
    else
        [JCORECacheChannel setUserProperty:nil];
    NSMutableArray *values = [NSMutableArray arrayWithArray:stored];
    NSArray *snapshot = [values copy];
    for (id entry in snapshot) {
        if (![entry isKindOfClass:NSDictionary.class])
            continue;
        id type = entry[@"type"];
        if (![type isKindOfClass:NSNumber.class] || [type unsignedShortValue] != request.type)
            continue;
        NSUInteger index = [values indexOfObject:entry];
        if ([request.content isEqualToString:entry[@"content"]]) {
            NSMutableDictionary *updated = [NSMutableDictionary dictionaryWithDictionary:entry];
            updated[@"upload"] = @1;
            if (index < values.count) {
                [values replaceObjectAtIndex:index withObject:updated];
                [JPUSHCache setObject:values forFile:@"__JPUSHUserPropertyKey"];
            }
        }
        break;
    }
}
@end
#endif
