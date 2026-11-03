#import "JCommonUtilities.h"
#import "JCOREUtilities.h"
#import "RecoveredCache.h"
#import "JCOREDataSecurity.h"
#import "JCORELog.h"
#import <objc/message.h>

JCOMMON_DATA_TYPE JCOMMON_ADVERTISING_ID = @"advertisingId";
#define JCommonFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/JCommonUtilities.m"
#define JCommonLog(sourceFunction, sourceLine, message, ...) [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCommonFile function:sourceFunction line:sourceLine tag:nil format:message, ##__VA_ARGS__]

static NSString *JCommonDecryptReflectionName(NSString *encoded) {
    NSData *data = [[NSData alloc] initWithBase64EncodedString:encoded options:0];
    data = [JCOREDataSecurity AES256DecryptData:data withKey:@"JCOMMON_IDFA_SECURITY_AES_KEY_ONE_TEMP_"];
    return [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
}

@implementation JCommonUtilities
+ (id)objectForKey:(id)key {
    if (![key isKindOfClass:NSString.class] || ![key length] || ![key isEqualToString:@"advertisingId"]) return nil;
    return [self advertisingId];
}
+ (NSString *)advertisingId {
    static NSString *idfaString;
    const char *function = "+[JCommonUtilities advertisingId]";
    JCommonLog(function, 44, @"idfa version");
    if (idfaString) return idfaString;
    double now = [JCOREUtilities currentTimestamp];
    id time = [JCORECacheUtilities cacheObjectForKey:@"kJCommonCacheIdfaTimeKey"];
    if (!time || ![time isKindOfClass:NSNumber.class]) JCommonLog(function, 62, @"get idfa - no cache");
    else if (now - [time doubleValue] <= 259200.0) {
        id cached = [JCORECacheUtilities cacheObjectForKey:@"kJCommonIdfaValueKey"];
        if (cached && [cached isKindOfClass:NSString.class]) {
            JCommonLog(function, 70, @"get idfa - from cache, %@", cached);
            idfaString = cached;
            return cached;
        }
    } else JCommonLog(function, 66, @"get idfa - cache expire");
    JCommonLog(function, 84, @"get idfa - api");
    NSString *value = [self getIdfa];
    [JCORECacheUtilities setCacheObject:@(now) forKey:@"kJCommonLastGetIdfaApiTimeKey"];
    if (![value isKindOfClass:NSString.class] || !value.length || [self checkFormatOfAllZeroIDFA:value]) return @"";
    idfaString = value;
    [JCORECacheUtilities setCacheObject:value forKey:@"kJCommonIdfaValueKey"];
    [JCORECacheUtilities setCacheObject:@(now) forKey:@"kJCommonCacheIdfaTimeKey"];
    return idfaString;
}
+ (BOOL)hasIdfaCache {
    id value = [JCORECacheUtilities cacheObjectForKey:@"kJCommonIdfaValueKey"];
    return value && [value isKindOfClass:NSString.class];
}
+ (BOOL)idfaCacheExpired {
    if (![self hasIdfaCache]) return NO;
    double now = [JCOREUtilities currentTimestamp];
    id time = [JCORECacheUtilities cacheObjectForKey:@"kJCommonCacheIdfaTimeKey"];
    return time && [time isKindOfClass:NSNumber.class] && now - [time doubleValue] > 259200.0;
}
+ (NSString *)getIdfa {
    NSString *value = [self getIDFAWithReflection];
    JCommonLog("+[JCommonUtilities getIdfa]", 132, @"[%s] idfaString is %@", "+[JCommonUtilities getIdfa]", value);
    return value;
}
+ (BOOL)judgeIdfaAuthorized {
    @try {
        NSDictionary *info = NSBundle.mainBundle.infoDictionary;
        if (!info) return NO;
        id description = [info objectForKey:@"NSUserTrackingUsageDescription"];
        return [description isKindOfClass:NSString.class] && [description length] != 0;
    } @catch (NSException *exception) {
        JCommonLog("+[JCommonUtilities judgeIdfaAuthorized]", 147, @"[boom][%s] is exception", "+[JCommonUtilities judgeIdfaAuthorized]");
        return NO;
    }
}
+ (NSString *)getIDFAWithReflection {
    NSString *className = JCommonDecryptReflectionName(@"Ruzf9L5/poJ4BsIH1iYubCEbH07IqBpDT6K+mIg5K6k=");
    NSString *sharedName = JCommonDecryptReflectionName(@"Mc920iDsaxKiXwQndA537g==");
    NSString *identifierName = JCommonDecryptReflectionName(@"1WVKFme559ZPgV7bwm276NsyzD6fX1SxMlVp2RnslLU=");
    NSString *stringName = JCommonDecryptReflectionName(@"f8h74bH52udN7gkI1D3h1A==");
    Class managerClass = NSClassFromString(className);
    SEL selector = NSSelectorFromString(sharedName);
    if (managerClass && [managerClass respondsToSelector:selector]) {
        id manager = ((id (*)(id, SEL, SEL))objc_msgSend)(managerClass, @selector(performSelector:), selector);
        if (manager) {
            selector = NSSelectorFromString(identifierName);
            if ([manager respondsToSelector:selector]) {
                id identifier = ((id (*)(id, SEL, SEL))objc_msgSend)(manager, @selector(performSelector:), selector);
                if (identifier) {
                    selector = NSSelectorFromString(stringName);
                    if ([identifier respondsToSelector:selector]) {
                        id result = ((id (*)(id, SEL, SEL))objc_msgSend)(identifier, @selector(performSelector:), selector);
                        return [result isKindOfClass:NSString.class] ? result : @"";
                    }
                }
            }
        }
    }
    return @"";
}
+ (unsigned long long)attAuthStatus {
    if (@available(iOS 14.0, *)) {
        NSString *className = JCommonDecryptReflectionName(@"BqXwWpZWsPq4N+DBX806xf++8QcH1oXYt8D65NHSkHM=");
        NSString *methodName = JCommonDecryptReflectionName(@"JBCztpN4JYVErmIlemOrKNGzGtTjL5M5NtoZJByvBIM=");
        const char *function = "+[JCommonUtilities attAuthStatus]";
        if (!className.length || !methodName.length) { JCommonLog(function, 238, @"IDFA tracking status check failed: decryption error"); return 0; }
        Class cls = NSClassFromString(className);
        if (!cls) { JCommonLog(function, 245, @"IDFA tracking status check failed: class not found"); return 0; }
        SEL selector = NSSelectorFromString(methodName);
        if (![cls respondsToSelector:selector]) { JCommonLog(function, 265, @"IDFA tracking status check failed: selector not responding"); return 0; }
        unsigned long long status;
        @try {
            status = ((unsigned long long (*)(id, SEL, SEL))objc_msgSend)(cls, @selector(performSelector:), selector);
        } @catch (NSException *exception) {
            JCommonLog(function, 260, @"IDFA tracking status check exception: %@", exception);
            return 0;
        }
        return status > 3 ? 0 : status;
    }
    NSString *className = JCommonDecryptReflectionName(@"XTGaSUn1H+xXUU8PURgFvAplnvxlWpQYt3E+t5tC");
    NSString *sharedName = JCommonDecryptReflectionName(@"OvRBSHgrbiKC5JC5QiQSS+aQZQY3rrD4rtm0Yjm7Nyo=");
    NSString *trackingName = JCommonDecryptReflectionName(@"ke25iWyABnneh1nYkDhHzEaCQnkNC/3rtvOGXPOotQA=");
    Class cls = NSClassFromString(className);
    SEL selector = NSSelectorFromString(sharedName);
    if (cls && [cls respondsToSelector:selector]) {
        id manager = ((id (*)(id, SEL, SEL))objc_msgSend)(cls, @selector(performSelector:), selector);
        if (manager) {
            selector = NSSelectorFromString(trackingName);
            if ([manager respondsToSelector:selector]) {
                id value = ((id (*)(id, SEL, SEL))objc_msgSend)(manager, @selector(performSelector:), selector);
                if ([value isKindOfClass:NSNumber.class]) return [value boolValue] ? 3 : 2;
            }
        }
    }
    return 2;
}
+ (BOOL)checkFormatOfAllZeroIDFA:(id)identifier {
    return [[NSPredicate predicateWithFormat:@"SELF MATCHES %@", @"^(0|-)+$"] evaluateWithObject:identifier];
}
@end
