#import "JCORERegister.h"
#import "JCOREIntegrate.h"
#import "JCOREUtilities.h"
#import "JCOREClientInfo.h"
#import "JCORELog.h"
#define JCORERegisterFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Tcp Event/JCORERegister.m"
#define JCORERegisterLog(flagValue, sourceLine, ...) [JCORELog log:YES level:0x3ff flag:flagValue context:0 file:JCORERegisterFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]
@implementation JCORERegister
@synthesize isNewDevice = _isNewDevice;
- (instancetype)init { if ((self = [super init])) { self.controlFlag = 14; self.protocolType = 0; self.version = 30; } return self; }
- (NSData *)bodyData {
    NSMutableData *data = [[NSMutableData alloc] init];
    NSString *key = [NSString stringWithFormat:@"%@$$ $$%@$$%@", [JCOREUtilities fakeIMEI], [JCOREUtilities bundleID], [JCOREClientInfo appKey]];
    NSString *integrate = [self integrateVersionString];
    BOOL newDevice = self.isNewDevice;
    NSString *client = [NSString stringWithFormat:@"%@$$%@$$ $$ $$%@$$%@$$ $$ $$ $$ $$%ld", [JCOREUtilities systemVersion], [JCOREUtilities modelName], [JCOREClientInfo channel], integrate, (long)newDevice];
    NSString *token = [NSString stringWithFormat:@"%@$$%@", [JCOREClientInfo deviceToken], [JCOREClientInfo voipToken]];
    NSString *advertising = [JCOREClientInfo advertisingId];
    NSString *message = [NSString stringWithFormat:@"\n----- register info -----\nAppkey: %@\ntoken:%@  \n advertisingIdentifier: %@\n", [JCOREClientInfo appKey], token, advertising];
    JCORERegisterLog(4, 51, @"%@", message);
    JCORERegisterLog(8, 54, @"Register info - strKey:%@ clientInfo:%@, deviceToken:%@, aps-Environment:%ld, configApsEnvironment:%ld advertisingIdentifier:%@", key, client, token, (long)[JCOREClientInfo apsType], (long)[JCOREClientInfo buildType], advertising);
    [data jcore_appendString:key];
    [data jcore_appendString:[JCOREUtilities appVersion]];
    [data jcore_appendString:client];
    BOOL pushOnly = [JCOREIntegrate jpushOnly];
    [data jcore_appendString:token.length ? token : (pushOnly ? @" " : @"")];
    [data jcore_appendString:advertising.length ? advertising : @" "];
    [data jcore_appendInt8:(unsigned char)[JCOREClientInfo buildType]];
    [data jcore_appendInt8:(unsigned char)[JCOREClientInfo apsType]];
    [data jcore_appendInt8:1];
    NSString *deviceID = [JCOREUtilities deviceID:nil];
    BOOL exists = deviceID != nil;
    NSString *selected = deviceID ?: [JCOREUtilities deviceID:[JCOREClientInfo appKey]];
    NSString *device = [NSString stringWithFormat:@"%ld$$%@", (long)exists, selected];
    JCORERegisterLog(0x100, 80, @"deviceIDStateAndID: %@", device);
    [data jcore_appendString:device];
    [data jcore_appendInt32:[JCOREIntegrate integrateBusiness]];
    NSString *account = [JCOREClientInfo accountID];
    [data jcore_appendString:account];
    JCORERegisterLog(0x100, 86, @"business:%d ,accountID:%@", [JCOREIntegrate integrateBusiness], account);
    [JCOREClientInfo setRegistrationInfo:@{@"device_id_status":@((NSInteger)exists)}];
    [JCOREIntegrate cacheOriginIntegrateMark];
    return [data copy];
}
- (NSString *)integrateVersionString {
    NSString *core = [JCOREIntegrate jcoreDisplayVersion];
    id v0 = [JCOREIntegrate displayVersionOfIndex:0] ?: @"", v1 = [JCOREIntegrate displayVersionOfIndex:1] ?: @"", v2 = [JCOREIntegrate displayVersionOfIndex:2] ?: @"";
    id v4 = [JCOREIntegrate displayVersionOfIndex:4] ?: @"", v5 = [JCOREIntegrate displayVersionOfIndex:5] ?: @"", v6 = [JCOREIntegrate displayVersionOfIndex:6] ?: @"", v7 = [JCOREIntegrate displayVersionOfIndex:7] ?: @"";
    NSString *string = [NSString stringWithFormat:@"%@|%@|%@|%@|%@|%@|%@|%@", v0, v1, v2, core, v4, v5, v6, v7];
    if ([JCOREIntegrate autoExtend]) {
        long long maximum = 7;
        for (id info in [JCOREIntegrate integrateList]) {
            BOOL useRegistration = [info respondsToSelector:@selector(regIndex)] && [info regIndex] > 0;
            long long index = useRegistration ? [info regIndex] : [info index];
            if (maximum < index) maximum = useRegistration ? [info regIndex] : [info index];
        }
        for (unsigned long long index = 0; maximum >= 0 && index != (unsigned long long)maximum + 1; index++) if (index > 7) string = [string stringByAppendingFormat:@"|%@", [JCOREIntegrate displayVersionOfIndex:(long long)index] ?: @""];
    }
    return string;
}
@end
