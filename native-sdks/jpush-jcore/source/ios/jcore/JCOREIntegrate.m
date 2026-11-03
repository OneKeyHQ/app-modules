#import "JCOREIntegrate.h"
#import "JCOREClientInfo.h"
#import "JCORELog.h"
#import "RecoveredCache.h"
static NSArray *integrateInfoList, *initSdkInfoList;
static BOOL integrateAutoExtend;
#define IntegrateFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Interface/JCOREIntegrate.m"
@implementation JCOREIntegrate
+ (void)registerIntegrateInfo:(id)info {
    NSMutableArray *list = [NSMutableArray arrayWithArray:integrateInfoList];
    [list addObject:info];
    integrateInfoList = [list copy];
}
+ (void)setAutoExtend:(BOOL)value {
    integrateAutoExtend = value;
}
+ (BOOL)autoExtend {
    return integrateAutoExtend;
}
+ (instancetype)shared {
    static JCOREIntegrate *instance;
    static dispatch_once_t once;
    dispatch_once(&once, ^{
      instance = [JCOREIntegrate new];
    });
    return instance;
}
+ (NSArray *)newIntegrateList {
    return [[self shared] newIntegrateList];
}
+ (id)integrateList {
    return [integrateInfoList copy];
}
+ (BOOL)hasMultiBusiness {
    id list = [integrateInfoList copy];
    return [list isKindOfClass:[NSArray class]] && [list count] >= 2;
}
+ (BOOL)jpushOnly {
    if (![self sdkIsExistOfIndex:0])
        return NO;
    id list = [integrateInfoList copy];
    return [list isKindOfClass:[NSArray class]] && [list count] == 1;
}
+ (id)sdkVersionOfIndex:(long long)index {
    return [[[self shared] findInfoOfIndex:index] version];
}
+ (BOOL)sdkIsExistOfIndex:(long long)index {
    return [[self shared] findInfoOfIndex:index] != nil;
}
+ (void)updateIntegrateMarkWith:(unsigned char)property {
    [[self shared] updateIntegrateMarkWith:property];
}
+ (id)integrateMarkList {
    return [[self shared] integrateMarkList];
}
+ (void)cacheOriginIntegrateMark {
    [[self shared] cacheOriginIntegrateMark];
}
+ (void)cacheOriginIntegrateVersion {
    [[self shared] cacheOriginIntegrateVersion];
}
+ (id)toDisplaySDKVersion:(id)value {
    return [[self shared] toDisplaySDKVersion:value];
}
+ (unsigned int)toDecimal:(id)value {
    return [[self shared] toDecimal:value];
}
+ (id)displayVersionOfIndex:(long long)index {
    return [self toDisplaySDKVersion:[self sdkVersionOfIndex:index]];
}
+ (unsigned int)intVersionOfIndex:(long long)index {
    return [self sdkIsExistOfIndex:index] ? [self toDecimal:[self sdkVersionOfIndex:index]] : 0;
}
+ (long long)typeOfIndex:(long long)index {
    if (![self sdkIsExistOfIndex:index])
        return 0;
    id info = [[self shared] findInfoOfIndex:index];
    return info && [[info class] instancesRespondToSelector:@selector(type)]
               ? [(id<JCOREIntegrateProtocol>)info type]
               : 0;
}
+ (id)jcoreDisplayVersion {
    return [self toDisplaySDKVersion:@"50201"];
}
+ (short)jcoreBuildVersion {
    return 129;
}
+ (unsigned int)jcoreIntVersion {
    return [self toDecimal:@"50201"];
}
+ (long long)jcoreSdkIndex {
    return 0;
}
+ (id)isHasCustomSDK {
    NSMutableDictionary *result = [NSMutableDictionary new];
    const long long indices[] = {0, 5, 1, 4, 2};
    NSArray *keys =
        @[ @"sdk_ver", @"verification_sdk_ver", @"statistics_sdk_ver", @"ssp_sdk_ver", @"share_sdk_ver" ];
    for (NSUInteger i = 0; i < 5; i++)
        if ([self typeOfIndex:indices[i]] == 1)
            result[keys[i]] =
                @{@"version" : [self displayVersionOfIndex:i == 4 ? 4 : indices[i]], @"sdk_type" : @1};
    return result;
}
+ (unsigned int)integrateBusiness {
    unsigned int result = 0;
    const unsigned int bits[] = {1, 4, 8, 32, 128, 256, 512, 1024};
    for (long long i = 0; i < 8; i++)
        if ([JCOREIntegrate sdkIsExistOfIndex:i])
            result |= bits[i];
    for (id<JCOREIntegrateProtocol> info in [integrateInfoList copy])
        if (info.index > 7 && [info respondsToSelector:@selector(business)])
            result |= (unsigned int)info.business;
    return result;
}
+ (unsigned int)integrateBusinessForLogin {
    unsigned int result = 0;
    const unsigned int bits[] = {1, 4, 8, 32, 128, 256, 512, 1024};
    for (long long i = 0; i < 8; i++)
        if ([JCOREIntegrate sdkIsExistOfIndex:i]) {
            if (i == 3) {
                unsigned char flag = [JCOREClientInfo IMFlag];
                [JCORELog log:YES
                        level:0x3ff
                         flag:0x100
                      context:0
                         file:IntegrateFile
                     function:"+[JCOREIntegrate integrateBusinessForLogin]"
                         line:262
                          tag:nil
                       format:@"jcore login with im flag:%@", flag == 32 ? @"logout" : @"login"];
                result |= [JCOREClientInfo IMFlag];
            } else
                result |= bits[i];
        }
    for (id<JCOREIntegrateProtocol> info in [integrateInfoList copy])
        if (info.index > 7 && [info respondsToSelector:@selector(business)])
            result |= (unsigned int)info.business;
    return result;
}
+ (id)sdkTypes {
    NSMutableArray *result = [NSMutableArray array];
    int core = [self calculateSdkType:1 custom:0 dynamic:0];
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:IntegrateFile
         function:"+[JCOREIntegrate sdkTypes]"
             line:307
              tag:nil
           format:@"sdktype: %d - %d - %d, result: %d", 1, 0, 0, core];
    [result addObject:@(core)];
    for (id<JCOREIntegrateProtocol> info in [integrateInfoList copy]) {
        int base;
        switch (info.index) {
        case 0:
            base = 2;
            break;
        case 5:
            base = 3;
            break;
        case 8:
            base = 4;
            break;
        case 6:
            base = 5;
            break;
        default:
            continue;
        }
        int custom = [info respondsToSelector:@selector(type)] && info.type ? (info.type == 1 ? 2 : 0) : 0;
        int type = [self calculateSdkType:base custom:custom dynamic:0];
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:IntegrateFile
             function:"+[JCOREIntegrate sdkTypes]"
                 line:336
                  tag:nil
               format:@"sdktype: %d - %d - %d, result: %d", base, custom, 0, type];
        [result addObject:@(type)];
    }
    return result;
}
+ (int)calculateSdkType:(int)type custom:(int)custom dynamic:(int)dynamic {
    return ((custom & 255) << 2) | ((type & 255) << 6) | (dynamic & 255);
}
+ (id)integrateInfo {
    NSMutableDictionary *result = [NSMutableDictionary new];
    result[@"sdk_ver"] = [self displayVersionOfIndex:0] ?: @"";
    result[@"statistics_sdk_ver"] = [self displayVersionOfIndex:1] ?: @"";
    result[@"share_sdk_ver"] = [self displayVersionOfIndex:2] ?: @"";
    result[@"ssp_sdk_ver"] = [self displayVersionOfIndex:4] ?: @"";
    result[@"verification_sdk_ver"] = [self displayVersionOfIndex:5] ?: @"";
    result[@"core_sdk_ver"] = [self jcoreDisplayVersion];
    result[@"g_sdk_type"] = [self sdkTypes];
    for (id<JCOREIntegrateProtocol> info in [integrateInfoList copy])
        if (info.index > 5 && [info respondsToSelector:@selector(versionFieldName)] &&
            [info.versionFieldName isKindOfClass:[NSString class]] && info.versionFieldName.length)
            result[info.versionFieldName] = [self displayVersionOfIndex:info.index] ?: @"";
    return result;
}
+ (BOOL)tcpKeepAlive {
    if ([self sdkIsExistOfIndex:0] || [self sdkIsExistOfIndex:3])
        return YES;
    for (id<JCOREIntegrateProtocol> info in [integrateInfoList copy])
        if ([info respondsToSelector:@selector(tcpKeepAlive)] && info.tcpKeepAlive)
            return YES;
    return NO;
}
+ (BOOL)needUploadSDKVersion {
    NSArray *list = [JCOREIntegrate newIntegrateList];
    return list && list.count;
}
- (instancetype)init {
    self = [super init];
    if (self) {
        [self registerSDKWithClass:@"JPUSHClientController" ofIndex:0 property:1 type:0];
        [self registerSDKWithClass:@"JANALYTICSClientController" ofIndex:1 property:4 type:0];
        [self registerSDKWithClass:@"JSHAREClientController" ofIndex:2 property:5 type:0];
        [self registerSDKWithClass:@"JMSGClientController" ofIndex:3 property:2 type:0];
    }
    return self;
}
- (NSArray *)newIntegrateList {
    @try {
        id marks = [self integrateMarkList];
        NSArray *versions = [self integrateVersion];
        NSMutableArray *result = [NSMutableArray new];
        for (id<JCOREIntegrateProtocol> info in [integrateInfoList copy]) {
            NSString *previous = info.version;
            if ((unsigned long long)info.index < versions.count) {
                id entry = versions[(NSUInteger)info.index];
                if ([entry isKindOfClass:[NSDictionary class]] &&
                    [entry[@"version"] isKindOfClass:[NSString class]])
                    previous = entry[@"version"];
            }
            if ((unsigned long long)info.index >= [marks count] ||
                ![marks[(NSUInteger)info.index] boolValue] || ![info.version isEqualToString:previous])
                [result addObject:info];
        }
        return result.count ? [result copy] : nil;
    } @catch (NSException *exception) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:IntegrateFile
             function:"-[JCOREIntegrate newIntegrateList]"
                 line:456
                  tag:nil
               format:@"[bomb] [%s] is exception", "-[JCOREIntegrate newIntegrateList]"];
        return nil;
    }
}
- (id<JCOREIntegrateProtocol>)findInfoOfProperty:(unsigned char)property {
    for (id<JCOREIntegrateProtocol> info in [integrateInfoList copy])
        if (info.property == property)
            return info;
    return nil;
}
- (id<JCOREIntegrateProtocol>)findInfoOfIndex:(long long)index {
    for (id<JCOREIntegrateProtocol> info in [integrateInfoList copy])
        if (info.index == index)
            return info;
    return nil;
}
- (void)cacheOriginIntegrateMark {
    NSArray *list = [integrateInfoList copy];
    long long max = 0;
    for (id<JCOREIntegrateProtocol> info in list)
        if (max < info.index)
            max = info.index;
    NSMutableArray *marks = [NSMutableArray arrayWithCapacity:(NSUInteger)max];
    while (marks.count <= (unsigned long long)max)
        [marks addObject:@NO];
    for (id<JCOREIntegrateProtocol> info in list)
        marks[(NSUInteger)info.index] = @YES;
    if (marks.count)
        [JCORECacheUtilities setPersistentObject:marks forKey:@"__JCORERegisterSDKVersionKey"];
}
- (void)cacheOriginIntegrateVersion {
    @try {
        NSArray *list = [integrateInfoList copy];
        long long max = 0;
        for (id<JCOREIntegrateProtocol> info in list)
            if (max < info.index)
                max = info.index;
        NSMutableArray *versions = [NSMutableArray arrayWithCapacity:(NSUInteger)max];
        while (versions.count <= (unsigned long long)max)
            [versions addObject:@{}];
        for (id<JCOREIntegrateProtocol> info in list)
            if (info.version.length)
                versions[(NSUInteger)info.index] = @{@"version" : info.version};
        if (versions.count)
            [JCORECacheUtilities setPersistentObject:versions forKey:@"__JCORESDKVersionKey"];
    } @catch (NSException *exception) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:IntegrateFile
             function:"-[JCOREIntegrate cacheOriginIntegrateVersion]"
                 line:531
                  tag:nil
               format:@"[bomb] [%s] is exception", "-[JCOREIntegrate cacheOriginIntegrateVersion]"];
    }
}
- (id)integrateVersion {
    NSMutableArray *fallback = [NSMutableArray array];
    @try {
        id value = [JCORECacheUtilities persistentObjectForKey:@"__JCORESDKVersionKey"];
        if (value && [value isKindOfClass:[NSArray class]])
            return value;
    } @catch (NSException *exception) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:IntegrateFile
             function:"-[JCOREIntegrate integrateVersion]"
                 line:543
                  tag:nil
               format:@"[bomb] [%s] is exception", "-[JCOREIntegrate integrateVersion]"];
    }
    return fallback;
}
- (void)updateIntegrateMarkWith:(unsigned char)property {
    id<JCOREIntegrateProtocol> info = [self findInfoOfProperty:property];
    if (!info) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x40
              context:0
                 file:IntegrateFile
             function:"-[JCOREIntegrate updateIntegrateMarkWith:]"
                 line:551
                  tag:nil
               format:@"sdk integrate save failed, wrong property:%d", property];
        return;
    }
    NSMutableArray *marks = [NSMutableArray arrayWithArray:[self integrateMarkList]];
    while (marks.count <= (unsigned long long)info.index)
        [marks addObject:@NO];
    marks[(NSUInteger)info.index] = @YES;
    [JCORECacheUtilities setPersistentObject:marks forKey:@"__JCORERegisterSDKVersionKey"];
}
- (id)integrateMarkList {
    return [JCORECacheUtilities persistentObjectForKey:@"__JCORERegisterSDKVersionKey"];
}
- (void)registerSDKWithClass:(NSString *)name
                     ofIndex:(long long)index
                    property:(unsigned char)property
                        type:(long long)type {
    id version = [self getSDKWithClassName:name];
    if ([version isKindOfClass:[NSString class]] && [version length] && ![self findInfoOfIndex:index]) {
        JCOREIntegrateInfo *info = [JCOREIntegrateInfo new];
        info.index = index;
        info.property = property;
        info.version = version;
        info.type = type;
        [[self class] registerIntegrateInfo:info];
    }
}
- (id)getSDKWithClassName:(id)name {
    @try {
        Class cls = NSClassFromString(name);
        if (!cls)
            return nil;
        SEL selector = NSSelectorFromString(@"sdkVersion");
        NSInvocation *invocation =
            [NSInvocation invocationWithMethodSignature:[cls methodSignatureForSelector:selector]];
        invocation.target = cls;
        invocation.selector = NSSelectorFromString(@"sdkVersion");
        [invocation invoke];
        __unsafe_unretained id result = nil;
        [invocation getReturnValue:&result];
        return [result isKindOfClass:[NSString class]] ? [result copy] : nil;
    } @catch (NSException *exception) {
        [JCORELog log:NO
                level:0x3ff
                 flag:1
              context:0
                 file:IntegrateFile
             function:"-[JCOREIntegrate getSDKWithClassName:]"
                 line:606
                  tag:nil
               format:@"exist error sdk"];
        return nil;
    }
}
- (id)toDisplaySDKVersion:(id)value {
    if (!value)
        return nil;
    NSMutableString *result = [NSMutableString string];
    for (NSUInteger i = 0; i < [value length]; i++) {
        NSString *digit = [value substringWithRange:NSMakeRange(i, 1)];
        if (!(i & 1)) {
            [result appendString:digit];
            if (i != [value length] - 1)
                [result appendString:@"."];
        } else if (digit.intValue)
            [result appendString:digit];
    }
    return result;
}
- (unsigned int)toDecimal:(id)value {
    unsigned int result = 0;
    for (NSUInteger i = 0; i < [value length]; i++) {
        signed char c = (signed char)[value characterAtIndex:i];
        result = result * 16 + c - (c <= 64 ? 48 : 55);
    }
    return result;
}
+ (void)initSdk:(long long)index {
    NSMutableArray *list = [NSMutableArray arrayWithArray:initSdkInfoList];
    [list addObject:@(index)];
    initSdkInfoList = [list copy];
}
+ (BOOL)isSdkInit:(long long)index {
    return [initSdkInfoList containsObject:@(index)];
}
@end
