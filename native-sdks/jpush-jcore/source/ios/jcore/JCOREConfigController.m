#import "JCOREClientInfo.h"
#import "JCOREConfig.h"
#import "JCORELog.h"
#include <ctype.h>
FOUNDATION_EXPORT NSString *const JCOMMON_ADVERTISING_ID;
@interface JCommonUtilities : NSObject
+ (id)objectForKey:(id)key;
@end
#define ControllerFile                                                                                       \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Controller/JCOREConfigController.m"
@implementation JCOREConfigController
@synthesize hasConfig = _hasConfig, configModel = _configModel;
+ (instancetype)sharedController {
    static JCOREConfigController *controller;
    static dispatch_once_t once;
    dispatch_once(&once, ^{
      controller = [JCOREConfigController new];
    });
    return controller;
}
+ (BOOL)setConfig:(id)config {
    return [[self sharedController] setConfig:config];
}
+ (id)config {
    return [self sharedController].configModel;
}
+ (void)setNewAdvertisingId:(id)identifier {
    [[self sharedController] setAdvertisingId:identifier];
}
- (instancetype)init {
    self = [super init];
    if (self)
        _configModel = [JCOREConfigModel new];
    return self;
}
- (void)setAdvertisingId:(id)identifier {
    id stored = [JCommonUtilities objectForKey:JCOMMON_ADVERTISING_ID];
    if ([stored isKindOfClass:[NSString class]] && [stored length] && ![self checkFormatOfAllZeroIDFA:stored])
        identifier = stored;
    if (![self isVerifyIDFAString:identifier]) {
        [JCORELog log:NO
                level:0x3ff
                 flag:1
              context:0
                 file:ControllerFile
             function:__PRETTY_FUNCTION__
                 line:77
                  tag:nil
               format:@"advertisingIdentifier无效，将不上报advertisingIdentifier值。请检查'启动SDK方法'"
                      @"中的参数值，或者是否限制广告跟踪"];
        return;
    }
    self.configModel.advertisingId = identifier;
    [JCOREClientInfo setAdvertisingId:identifier];
}
- (BOOL)setConfig:(id)config {
    if (!config) {
        [JCORELog log:NO
                level:0x3ff
                 flag:1
              context:0
                 file:ControllerFile
             function:__PRETTY_FUNCTION__
                 line:83
                  tag:nil
               format:@"config param nil"];
        return NO;
    }
    if (![config conformsToProtocol:@protocol(JCOREConfigProtocol)]) {
        [JCORELog log:NO
                level:0x3ff
                 flag:1
              context:0
                 file:ControllerFile
             function:__PRETTY_FUNCTION__
                 line:87
                  tag:nil
               format:@"config data error"];
        return NO;
    }
    id<JCOREConfigProtocol> input = config;
    if (self.hasConfig) {
        [self doResult:0 message:nil withBlock:input.completionHandler];
        return NO;
    }
    if (![self isValidAppKey:input.appKey]) {
        NSString *message;
        unsigned short code;
        if (!input.appKey) {
            message = @"APP_KEY不能为空。\n（有关APP_KEY的说明请参考开发文档“SDK集成指南”。";
            code = 1000;
        } else {
            message = [NSString
                stringWithFormat:@"“%@”不是合法的APP_KEY。\n（有关APP_KEY的说明请参考开发文档“SDK集成指南”。",
                                 input.appKey];
            code = 1001;
        }
        [self doResult:code message:message withBlock:input.completionHandler];
        [JCORELog log:NO
                level:0x3ff
                 flag:1
              context:0
                 file:ControllerFile
             function:__PRETTY_FUNCTION__
                 line:109
                  tag:nil
               format:@"ERROR\n%@", message];
        return NO;
    }
    self.hasConfig = YES;
    self.configModel.appKey = input.appKey;
    [JCOREClientInfo setAppKey:input.appKey];
    self.configModel.channel = input.channel;
    [JCOREClientInfo setChannel:input.channel];
    [self setAdvertisingId:input.advertisingId];
    self.configModel.isProduction = input.isProduction;
    [JCOREClientInfo setIsProduction:input.isProduction];
    [self doResult:0 message:nil withBlock:input.completionHandler];
    return YES;
}
- (void)doResult:(unsigned short)code message:(id)message withBlock:(JCOREConfigSetupCompletion)block {
    if (block && ![(id)block isKindOfClass:[NSNull class]]) {
        NSError *error = [NSError errorWithDomain:@"JIGUANGConfigErrorDomain"
                                             code:code
                                         userInfo:@{NSLocalizedDescriptionKey : message ?: @""}];
        if (!code)
            error = nil;
        block(error, self.configModel);
    }
}
- (BOOL)isValidAppKey:(id)key {
    if (![key isKindOfClass:[NSString class]] || [key length] != 24)
        return NO;
    NSUInteger length = [key length];
    const char *bytes = [key UTF8String];
    for (NSUInteger i = 0; i < length; i++)
        if (!isalnum(bytes[i]))
            return NO;
    return YES;
}
- (BOOL)isVerifyIDFAString:(id)identifier {
    if ([identifier isEqualToString:@""])
        return YES;
    if (!identifier || ![identifier isKindOfClass:[NSString class]] ||
        ![self confirmFormatOfIDFA:identifier] || [self checkFormatOfAllZeroIDFA:identifier])
        return NO;
    NSArray *parts = [identifier componentsSeparatedByString:@"-"];
    return parts.count == 5 && [parts[0] length] == 8 && [parts[1] length] == 4 && [parts[2] length] == 4 &&
           [parts[3] length] == 4 && [parts[4] length] == 12 && [identifier length] < 73;
}
- (BOOL)confirmFormatOfIDFA:(id)identifier {
    return [[NSPredicate
        predicateWithFormat:@"SELF MATCHES %@", @"^([0-9a-zA-Z]{1,})(([/s-][0-9a-zA-Z]{1,}){4})$"]
        evaluateWithObject:identifier];
}
- (BOOL)checkFormatOfAllZeroIDFA:(id)identifier {
    return [[NSPredicate predicateWithFormat:@"SELF MATCHES %@", @"^(0|-)+$"] evaluateWithObject:identifier];
}
@end
