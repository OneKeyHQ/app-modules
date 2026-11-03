#import "JCORELogin.h"
#import "JCOREIntegrate.h"
#import "JCOREUtilities.h"
#import "JCOREClientInfo.h"
#import "JCOREBusinessStorage.h"
#import "JCORELog.h"
#import <UIKit/UIKit.h>
#define JCORELoginFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Tcp Event/JCORELogin.m"
#define JCORELoginLog(sourceLine, ...) [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCORELoginFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]
@implementation JCORELogin
- (instancetype)init { if ((self = [super init])) { self.controlFlag = 14; self.protocolType = 1; self.version = 27; } return self; }
- (NSData *)bodyData {
    NSMutableData *buffer = [[NSMutableData alloc] init];
    uint32_t header = 105;
    [buffer appendBytes:&header length:4];
    [buffer jcore_appendString:[JCOREUtilities stringByMD5:[JCOREClientInfo password]]];
    id data = buffer;
    [self appendIntegrateVersionTo:&data];
    [data jcore_appendString:[JCOREClientInfo appKey]];
    [data jcore_appendInt8:1];
    unsigned int business = [JCOREIntegrate integrateBusinessForLogin];
    [data jcore_appendInt32:business];
    unsigned char generation = [JCOREUtilities ctGenerationNumber];
    [data jcore_appendInt8:generation];
    NSString *deviceID = [JCOREUtilities deviceID:[JCOREClientInfo appKey]];
    [data jcore_appendString:deviceID];
    id location = [JCOREBusinessStorage getLocation];
    NSData *locationData = [location isKindOfClass:NSDictionary.class] && [location count] ? [JCOREUtilities jsonToData:location] : [NSData data];
    [data jcore_appendDataWithLength:locationData];
    NSString *account = [JCOREClientInfo accountID];
    [data jcore_appendString:account];
    JCORELoginLog(64, @"jcore login with business:%d, login type:%d, deviceID:%@, lbs:%@, accountID:%@", business, generation, deviceID, location, account);
    unsigned char state = [self getAppState];
    [data jcore_appendInt8:state];
    JCORELoginLog(68, @"JCORELogin appState is [%hhu]", state);
    NSNumber *version = [JCOREIntegrate sdkVersionOfIndex:0];
    BOOL type = version && version.integerValue >= 50900 ? [JCOREIntegrate isSdkInit:0] : [JCOREIntegrate sdkIsExistOfIndex:0];
    [data jcore_appendInt8:type];
    JCORELoginLog(79, @"JCORELogin sdkType is [%hhu]", type);
    id types = [JCOREIntegrate sdkTypes];
    NSString *joined = [types isKindOfClass:NSArray.class] ? [types componentsJoinedByString:@"|"] : nil;
    [data jcore_appendString:joined ?: @""];
    JCORELoginLog(87, @"JCORELogin sdkVerType is %@", joined ?: @"");
    return [data copy];
}
- (void)appendIntegrateVersionTo:(id __autoreleasing *)data {
    if (![*data isKindOfClass:NSMutableData.class]) return;
    unsigned int core = [JCOREIntegrate jcoreIntVersion];
    unsigned int v0 = [JCOREIntegrate intVersionOfIndex:0], v1 = [JCOREIntegrate intVersionOfIndex:1], v2 = [JCOREIntegrate intVersionOfIndex:2];
    unsigned int v4 = [JCOREIntegrate intVersionOfIndex:4], v5 = [JCOREIntegrate intVersionOfIndex:5], v6 = [JCOREIntegrate intVersionOfIndex:6], v7 = [JCOREIntegrate intVersionOfIndex:7];
    NSString *string = [NSString stringWithFormat:@"%@|%@|%@|%@|%@|%@|%@|%@", core ? @(core) : @"", v0 ? @(v0) : @"", v1 ? @(v1) : @"", v5 ? @(v5) : @"", v2 ? @(v2) : @"", v4 ? @(v4) : @"", v6 ? @(v6) : @"", v7 ? @(v7) : @""];
    if (![JCOREIntegrate autoExtend]) { JCORELoginLog(140, @"SDKVersionInfo:%@", string); [*data jcore_appendString:string]; return; }
    long long maximum = 7;
    for (id info in [JCOREIntegrate integrateList]) {
        BOOL useLogin = [info respondsToSelector:@selector(loginIndex)] && [info loginIndex] > 0;
        long long index = useLogin ? [info loginIndex] : [info index];
        if (maximum < index) maximum = useLogin ? [info loginIndex] : [info index];
    }
    for (unsigned long long index = 0; maximum >= 0 && index != (unsigned long long)maximum + 1; index++) {
        if (index > 7) { unsigned int value = [JCOREIntegrate intVersionOfIndex:(long long)index]; string = [string stringByAppendingFormat:@"|%@", value ? @(value) : @""]; }
    }
    JCORELoginLog(165, @"SDKVersionInfo:%@", string);
    [*data jcore_appendString:string];
}
- (unsigned char)getAppState {
    __block unsigned char state = 1;
    @try {
        if (NSThread.isMainThread) {
            UIApplicationState value = UIApplication.sharedApplication.applicationState;
            state = value == 1 ? 0 : value == 0 ? 1 : 2;
        } else dispatch_sync(dispatch_get_main_queue(), ^{
            @try {
                UIApplicationState value = UIApplication.sharedApplication.applicationState;
                state = value == 1 ? 0 : value == 0 ? 1 : 2;
            } @catch (NSException *exception) { JCORELoginLog(118, @"[bomb][%s] is exception", __PRETTY_FUNCTION__); }
        });
    } @catch (NSException *exception) { JCORELoginLog(104, @"[bomb] [%s] is exception", __PRETTY_FUNCTION__); }
    return state;
}
@end
