#import "JCOREClientInfo.h"
#import "JCOREUtilities.h"
#import "RecoveredCache.h"
#import "JCORELog.h"
@interface JCORENetUtilities : NSObject
+ (unsigned short)generateRid;
@end
@interface JCOREIntegrate : NSObject
+ (NSString *)displayVersionOfIndex:(NSUInteger)index;
+ (NSString *)jcoreDisplayVersion;
+ (unsigned int)integrateBusiness;
@end

static NSString *clientAppKey, *clientChannel, *clientAdvertisingId, *clientPassword, *clientRegistrationId;
static NSString *clientAccountId, *clientDeviceToken, *clientVoipToken, *clientUploadedDeviceToken, *clientUploadedVoipToken;
static NSString *clientCuid, *clientOrgId, *clientDataSourceId;
static NSNumber *clientShouldUploadAdvertisingId;
static BOOL clientProduction, clientDeviceTokenUpdated, clientVoipTokenUpdated;
static unsigned long long clientUid;
static unsigned int clientSessionId;
#define ClientFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Interface/JCOREClientInfo.m"
@implementation JCOREClientInfo
+ (id)appKey { @synchronized([self class]) { return clientAppKey ?: @""; } }
+ (void)setAppKey:(id)value { @synchronized([self class]) { clientAppKey = [value lowercaseString]; } }
+ (id)channel { return clientChannel ?: @" "; }
+ (void)setChannel:(id)value { clientChannel = value; }
+ (id)advertisingId {
    if (!clientAdvertisingId || !clientAdvertisingId.length) {
        id cached = [JCORECacheUtilities persistentObjectForKey:@"kAdvertisingIdentifier"];
        if ([cached isKindOfClass:[NSString class]] && [cached length]) clientAdvertisingId = cached;
        else return clientAdvertisingId ?: @"";
    }
    return clientAdvertisingId;
}
+ (void)setAdvertisingId:(id)value {
    if ([value isKindOfClass:[NSString class]] && ![value isEqualToString:clientAdvertisingId]) {
        clientAdvertisingId = value;
        if ([value length]) { [JCORECacheUtilities setPersistentObject:value forKey:@"kAdvertisingIdentifier"]; [self setShouldUploadAdvertisingId:YES]; }
    }
}
+ (BOOL)shouldUploadAdvertisingId {
    if ([clientShouldUploadAdvertisingId isKindOfClass:[NSNumber class]]) return clientShouldUploadAdvertisingId.boolValue;
    id cached = [JCORECacheUtilities persistentObjectForKey:@"kShouldUploadAdvertisingIdentifier"];
    if (![cached isKindOfClass:[NSNumber class]]) return NO;
    clientShouldUploadAdvertisingId = cached; return [cached boolValue];
}
+ (void)setShouldUploadAdvertisingId:(BOOL)value {
    // Original RAM state is always YES, while persistent state stores the argument.
    clientShouldUploadAdvertisingId = @YES;
    [JCORECacheUtilities setPersistentObject:@(value) forKey:@"kShouldUploadAdvertisingIdentifier"];
}
+ (BOOL)isProduction { return clientProduction; }
+ (void)setIsProduction:(BOOL)value { clientProduction = value; }
+ (long long)apsType {
    NSURL *url = [[[NSBundle mainBundle] URLForResource:@"embedded" withExtension:@"mobileprovision"] copy];
    if (!url) return -1;
    NSString *profile = [NSString stringWithContentsOfURL:url encoding:NSISOLatin1StringEncoding error:NULL];
    if (!profile) { [JCORELog log:NO level:0x3ff flag:1 context:0 file:ClientFile function:"+[JCOREClientInfo apsType]" line:151 tag:nil format:@"Read the certificate file error,unable to RegisterForRemoteNotifications"]; return -2; }
    NSRange marker = [profile rangeOfString:@"aps-environment" options:NSCaseInsensitiveSearch];
    if (marker.location == NSNotFound) { [JCORELog log:YES level:0x3ff flag:2 context:0 file:ClientFile function:"+[JCOREClientInfo apsType]" line:158 tag:nil format:@"APNs is not available,please check your provisioning profile and certification"]; return -3; }
    NSUInteger remaining = profile.length - marker.location;
    NSRange closing = [profile rangeOfString:@"</string>" options:NSCaseInsensitiveSearch range:NSMakeRange(marker.location, MIN((NSUInteger)100, remaining))];
    NSString *value = [profile substringWithRange:NSMakeRange(marker.location, closing.location != NSNotFound ? closing.location - marker.location : MIN((NSUInteger)50, remaining))];
    if ([value rangeOfString:@"development" options:NSCaseInsensitiveSearch].location != NSNotFound) return 1;
    if ([value rangeOfString:@"production" options:NSCaseInsensitiveSearch].location != NSNotFound) return 2;
    return -4;
}
+ (long long)buildType { return [self isProduction] ? 2 : 1; }
+ (id)password {
    if (!clientPassword || !clientPassword.length) { id value = [JCORECacheUtilities persistentObjectForKey:@"kPasswordKey"]; if ([value isKindOfClass:[NSString class]] && [value length]) clientPassword = value; }
    return clientPassword;
}
+ (void)setPassword:(id)value { clientPassword = value; [JCORECacheUtilities setPersistentObject:value forKey:@"kPasswordKey"]; }
+ (unsigned long long)uid {
    if (!clientUid) { id value = [JCORECacheUtilities persistentObjectForKey:@"kUIDKey"]; if ([value isKindOfClass:[NSNumber class]] || [value isKindOfClass:[NSString class]]) clientUid = [value longLongValue]; }
    return clientUid;
}
+ (void)setUid:(unsigned long long)value { clientUid = value; [JCORECacheUtilities setPersistentObject:@(value) forKey:@"kUIDKey"]; }
+ (id)registrationID {
    if (!clientRegistrationId) { id value = [JCORECacheUtilities persistentObjectForKey:@"kRegistrationId"]; if ([value isKindOfClass:[NSString class]] && [value length]) clientRegistrationId = value; }
    return clientRegistrationId;
}
+ (void)setRegistrationID:(id)value { clientRegistrationId = value; [JCORECacheUtilities setPersistentObject:value forKey:@"kRegistrationId"]; }
+ (unsigned short)rid { return [JCORENetUtilities generateRid]; }
+ (void)setSessionID:(unsigned int)value { clientSessionId = value; }
+ (unsigned int)sessionID { return clientSessionId; }
+ (id)registrationCurrentInfo:(BOOL)status {
    return @{@"udid":[JCOREUtilities fakeIMEI] ?: @"", @"bundle_id":[JCOREUtilities bundleID] ?: @"", @"appkey":[self appKey] ?: @"", @"platform":@1, @"apkversion":[JCOREUtilities appVersion] ?: @"", @"systemversion":[JCOREUtilities systemVersion] ?: @"", @"modelnumber":[JCOREUtilities modelName] ?: @"", @"channel":[self channel] ?: @"", @"sdkver":@{@"PushSDKVer":[JCOREIntegrate displayVersionOfIndex:0] ?: @"", @"StatisticSDKVer":[JCOREIntegrate displayVersionOfIndex:1] ?: @"", @"ShareSDKVer":[JCOREIntegrate displayVersionOfIndex:2] ?: @"", @"CoreSDKVer":[JCOREIntegrate jcoreDisplayVersion] ?: @""}, @"resolution":[JCOREUtilities currentResolution] ?: @"", @"business":@([JCOREIntegrate integrateBusiness]), @"devicetoken":[self deviceToken] ?: @"", @"idfa":[self advertisingId] ?: @"", @"build_type":@([self buildType]), @"aps_type":@([self apsType]), @"device_id_status":@(status), @"device_id":[JCOREUtilities deviceID:[self appKey]] ?: @""};
}
+ (id)registrationInfo {
    id cached = [JCORECacheUtilities persistentObjectForKey:@"__JCORERegisterInfoKey"];
    id result = [cached isKindOfClass:[NSData class]] ? [JCOREUtilities dataToJSON:cached] : nil;
    return [result isKindOfClass:[NSDictionary class]] ? result : [self registrationCurrentInfo:[JCOREUtilities deviceID:nil] != nil];
}
+ (void)setRegistrationInfo:(id)value {
    if ([value isKindOfClass:[NSDictionary class]]) { id flag = value[@"device_id_status"]; BOOL status = [flag isKindOfClass:[NSNumber class]] ? [flag boolValue] : NO; [JCORECacheUtilities setPersistentObject:[self registrationCurrentInfo:status] forKey:@"__JCORERegisterInfoKey"]; }
}
+ (id)accountID { if (!clientAccountId) { id value = [JCORECacheUtilities persistentObjectForKey:@"_JiguangPersistenceAccountIDKey"]; if ([value isKindOfClass:[NSString class]]) clientAccountId = value; } return clientAccountId ?: @""; }
+ (void)setAccountID:(id)value { clientAccountId = value; [JCORECacheUtilities setPersistentObject:value forKey:@"_JiguangPersistenceAccountIDKey"]; }
+ (id)deviceToken { return clientDeviceToken ?: @""; }
+ (void)setDeviceToken:(id)value { clientDeviceToken = value; }
+ (id)voipToken { return clientVoipToken ?: @""; }
+ (void)setVoipToken:(id)value { clientVoipToken = value; }
+ (id)uploadedDeviceToken { if (!clientUploadedDeviceToken) { id value = [JCORECacheUtilities persistentObjectForKey:@"kUploadedDeviceTokenKey"]; if ([value isKindOfClass:[NSString class]]) clientUploadedDeviceToken = value; } return clientUploadedDeviceToken ?: @""; }
+ (void)setUploadedDeviceToken:(id)value { clientUploadedDeviceToken = value; [JCORECacheUtilities setPersistentObject:value forKey:@"kUploadedDeviceTokenKey"]; }
+ (void)setDeviceTokenUpdated:(BOOL)value { clientDeviceTokenUpdated = value; }
+ (BOOL)deviceTokenUpdated { return clientDeviceTokenUpdated; }
+ (void)setUploadedVoipToken:(id)value { clientUploadedVoipToken = value; [JCORECacheUtilities setPersistentObject:value forKey:@"kUploadedVoipTokenKey"]; }
+ (id)uploadedVoipToken { if (!clientUploadedVoipToken) { id value = [JCORECacheUtilities persistentObjectForKey:@"kUploadedVoipTokenKey"]; if ([value isKindOfClass:[NSString class]]) clientUploadedVoipToken = value; } return clientUploadedVoipToken ?: @""; }
+ (void)setVoipTokenUpdated:(BOOL)value { clientVoipTokenUpdated = value; }
+ (BOOL)voipTokenUpdated { return clientVoipTokenUpdated; }
+ (id)cuid { if (!clientCuid) { id value = [JCORECacheUtilities persistentObjectForKey:@"kJiguangCUID"]; if ([value isKindOfClass:[NSString class]] && [value length]) clientCuid = value; } return clientCuid; }
+ (void)setCuid:(id)value {
    NSString *old = [self cuid]; [JCORELog log:YES level:0x3ff flag:8 context:0 file:ClientFile function:"+[JCOREClientInfo setCuid:]" line:410 tag:nil format:@"history cuid: %@, current cuid: %@", old, value];
    if (!value || !old || ![old isEqualToString:value]) { clientCuid = value; [JCORECacheUtilities setPersistentObject:value forKey:@"kJiguangCUID"]; }
}
+ (id)orgId { if (!clientOrgId) { id value = [JCORECacheUtilities persistentObjectForKey:@"kJiguangOrgId"]; if ([value isKindOfClass:[NSString class]] && [value length]) clientOrgId = value; } return clientOrgId; }
+ (void)setOrgId:(id)value {
    NSString *old = [self orgId]; [JCORELog log:YES level:0x3ff flag:8 context:0 file:ClientFile function:"+[JCOREClientInfo setOrgId:]" line:432 tag:nil format:@"history orgId: %@, current orgId: %@", old, value];
    if (!value || !old || ![old isEqualToString:value]) { clientOrgId = value; [JCORECacheUtilities setPersistentObject:value forKey:@"kJiguangOrgId"]; }
}
+ (id)dataSourceId { if (!clientDataSourceId) { id value = [JCORECacheUtilities persistentObjectForKey:@"kDataSourceIDKey"]; if ([value isKindOfClass:[NSString class]] && [value length]) clientDataSourceId = value; } return clientDataSourceId; }
+ (void)setDataSourceId:(id)value {
    NSString *old = [self dataSourceId]; [JCORELog log:YES level:0x3ff flag:8 context:0 file:ClientFile function:"+[JCOREClientInfo setDataSourceId:]" line:454 tag:nil format:@"history dataSourceId: %@, current dataSourceId: %@", old, value];
    if (!value || !old || ![old isEqualToString:value]) { clientDataSourceId = value; [JCORECacheUtilities setPersistentObject:clientOrgId forKey:@"kDataSourceIDKey"]; }
}
+ (void)setIMFlag:(unsigned char)value {
    [JCORELog log:YES level:0x3ff flag:8 context:0 file:ClientFile function:"+[JCOREClientInfo setIMFlag:]" line:468 tag:nil format:@"set im flag with:%@", value == 32 ? @"logout" : @"login"];
    [[NSUserDefaults standardUserDefaults] setObject:@(value) forKey:@"__jmsg_imState"]; [[NSUserDefaults standardUserDefaults] synchronize];
}
+ (unsigned char)IMFlag {
    id value = [[NSUserDefaults standardUserDefaults] objectForKey:@"__jmsg_imState"];
    if (!value) { [JCORELog log:YES level:0x3ff flag:8 context:0 file:ClientFile function:"+[JCOREClientInfo IMFlag]" line:476 tag:nil format:@"login JCore with IM loginOut"]; return 64; }
    if (![value intValue]) { value = @32; [self setIMFlag:32]; }
    [JCORELog log:YES level:0x3ff flag:8 context:0 file:ClientFile function:"+[JCOREClientInfo IMFlag]" line:483 tag:nil format:@"current login flag is:%@", @([value intValue])];
    return [value shortValue];
}
@end
