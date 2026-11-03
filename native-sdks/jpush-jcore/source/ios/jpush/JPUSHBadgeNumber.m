#import "JCOREUtilities.h"
#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCOREDataSecurity.h"
#import "JCORELog.h"
#import "JPUSHAutomatedTester.h"
#import "JPUSHBadgeNumber.h"
#import "JPUSHCache.h"

@interface JCOREClientInfo : NSObject
+ (unsigned long long)uid;
+ (NSString *)appKey;
+ (NSString *)accountID;
@end

#import "JCORENetChannel.h"
@interface JPUSHClientController : NSObject
+ (instancetype)sharedClient;
- (JPUSHBadgeNumberController *)badgeNumberManager;
@end
#define BADGE_CONTROLLER_LOG(lineNo, formatValue, ...)                                                       \
    [JCORELog log:YES                                                                                        \
            level:0x3ff                                                                                      \
             flag:2                                                                                          \
          context:0                                                                                          \
             file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/JPush/JPush/Controller/"    \
                  "JPUSHBadgeNumberController.m"                                                             \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineNo                                                                                     \
              tag:nil                                                                                        \
           format:formatValue, ##__VA_ARGS__]
#define BADGE_REPORT_LOG(flagValue, lineNo, formatValue, ...)                                                \
    [JCORELog log:YES                                                                                        \
            level:0x3ff                                                                                      \
             flag:flagValue                                                                                  \
          context:0                                                                                          \
             file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/JPush/JPush/Network/"       \
                  "Report/Http/JPUSHBadgeNumberReport.m"                                                     \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineNo                                                                                     \
              tag:nil                                                                                        \
           format:formatValue, ##__VA_ARGS__]
static NSString *const BadgeCacheKey = @"jpush_user_default_badge_number";
@implementation JPUSHBadgeNumberController
@synthesize badgeValue = _badgeValue, badgeReport = _badgeReport;
@dynamic isSendingLock;
- (instancetype)init {
    if ((self = [super init])) {
        _isSendingLock = NO;
        _isClearOut = YES;
    }
    return self;
}
- (BOOL)isSendingLock {
    return _isSendingLock;
}
- (void)setIsSendingLock:(BOOL)value {
    _isSendingLock = value;
    [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(requestTimeout) object:nil];
    [self checkAutoSend];
}
- (void)checkAutoSend {
    if (_needAutoSend) {
        _needAutoSend = NO;
        [self performSelector:@selector(becomeActive) withObject:nil afterDelay:0];
    }
}
- (void)requestTimeout {
    _isSendingLock = NO;
    [self stopDelayTimer:NO];
    [self checkAutoSend];
}
- (void)setBadge:(NSNumber *)value {
    if (_badgeValue && value.intValue == _badgeValue.intValue && _isSendingLock) {
        _needAutoSend = YES;
        return;
    }
    _isSendingLock = YES;
    [self stopDelayTimer:YES];
    if (!_badgeReport)
        _badgeReport = [[JPUSHBadgeNumberReportRequest alloc] init];
    [_badgeReport setBadgeNumber:value.unsignedIntValue];
    [_badgeReport send];
    _badgeValue = value;
    [self setBadgeInUserDefault:value];
    [self performSelector:@selector(requestTimeout) withObject:nil afterDelay:30];
}
- (void)becomeActive {
    id value = [JCORECacheChannel badgeNumberUserDefault];
    if (![value isKindOfClass:NSNumber.class])
        value = [JPUSHCache objectForKey:BadgeCacheKey];
    else
        [JCORECacheChannel setBadgeNumberUserDefault:nil];
    if (value)
        [self setBadge:value];
}
- (void)willInactive {
    [self stopDelayTimer:YES];
}
- (void)clearBadgeInUserDefault {
    if (_isClearOut)
        return;
    _isClearOut = YES;
    [JCORECacheChannel setBadgeNumberUserDefault:nil];
    [JPUSHCache removeObjectForKey:BadgeCacheKey];
}
- (void)setBadgeInUserDefault:(NSNumber *)value {
    [JPUSHCache setObject:[value copy] forKey:BadgeCacheKey];
}
- (void)startDelaySendBadge:(JPUSHBadgeNumberReportRequest *)report {
    static const NSTimeInterval sendTimeIntervals[] = {40, 100, 200, 300};
    if (_badgeReport != report)
        return;
    if (_delaySendIntervalIndex < 4) {
        [self stopDelayTimer:YES];
        NSUInteger index = _delaySendIntervalIndex++;
        _delaySendTimer = [NSTimer scheduledTimerWithTimeInterval:sendTimeIntervals[index]
                                                           target:self
                                                         selector:@selector(doSendReport:)
                                                         userInfo:nil
                                                          repeats:NO];
    } else
        BADGE_CONTROLLER_LOG(0x77, @"send badge count too more");
}
- (void)startSendSelfToSpaceAddress:(JPUSHBadgeNumberReportRequest *)report {
    if (_badgeReport != report)
        return;
    _isSendingLock = YES;
    [_badgeReport send];
}
- (void)stopDelayTimer:(BOOL)keep commandSource:(JPUSHBadgeNumberReportRequest *)report {
    if (report.badgeCount == _badgeValue.longValue)
        [self setBadgeInUserDefault:nil];
    if (_badgeReport == report)
        [self stopDelayTimer:keep];
}
- (void)stopDelayTimer:(BOOL)keep {
    if (!keep) {
        _badgeValue = nil;
        _badgeReport = nil;
        _delaySendIntervalIndex = 0;
    }
    if (_delaySendTimer.valid)
        [_delaySendTimer invalidate];
}
- (void)doSendReport:(NSTimer *)timer {
    if (_delaySendTimer != timer)
        return;
    _isSendingLock = YES;
    [_badgeReport send];
}
@end
@implementation JPUSHBadgeNumberReportRequest
@synthesize badgeCount = _badgeCount, isAgainSend = _isAgainSend, aesKey = _aesKey;
- (instancetype)init {
    if ((self = [super init])) {
        self.reportTimeStamp = (long long)([JCOREUtilities currentTimestamp] * 1000);
        self.aesKey = @"";
        NSString *key = [JCOREDataSecurity encryptKeyForHttp];
        if (key.length >= 16)
            self.aesKey = [key substringToIndex:16];
        [self handleResponse];
    }
    return self;
}
- (id)requestInfo {
    return [self badgeBody];
}
- (id)badgeBody {
    _badgeBody = [[NSMutableDictionary alloc] initWithCapacity:5];
    _badgeBody[@"uid"] = @([JCOREClientInfo uid]);
    _badgeBody[@"platform"] = @"i";
    _badgeBody[@"app_key"] = [JCOREClientInfo appKey];
    _badgeBody[@"itime"] = @((long)[JCOREUtilities currentTimestamp]);
    id account = [JCOREClientInfo accountID];
    if ([account isKindOfClass:NSString.class] && [account length])
        _badgeBody[@"account_id"] = account;
    NSMutableDictionary *content = [[NSMutableDictionary alloc] initWithCapacity:5];
    content[@"badge"] = @(_badgeCount < 0 ? 0 : _badgeCount);
    _badgeBody[@"content"] = content;
    return _badgeBody;
}
- (void)setBadgeNumber:(unsigned int)number {
    self.badgeCount = number;
}
- (void)handleResponse {
    __weak JPUSHBadgeNumberReportRequest *weakSelf = self;
    self.completionHandler = ^(id response, NSError *error) {
        NSInteger code = error.code;
        if (!error || code == 200) {
            [JPUSHAutomatedTester PostAutomatedTestStatusNotification:30];
            [[JPUSHClientController sharedClient].badgeNumberManager stopDelayTimer:NO
                                                                      commandSource:weakSelf];
            BADGE_REPORT_LOG(4, 0x55, @"set badge:%ld succeed", (long)weakSelf.badgeCount);
        } else {
            [JPUSHAutomatedTester PostAutomatedTestStatusNotification:31];
            if (code == 500) {
                [[JPUSHClientController sharedClient].badgeNumberManager stopDelayTimer:YES
                                                                          commandSource:weakSelf];
                BADGE_REPORT_LOG(4, 0x64, @"warning:badge:%ld inner error failed", (long)weakSelf.badgeCount);
            } else if (code == 401) {
                [[JPUSHClientController sharedClient].badgeNumberManager stopDelayTimer:NO
                                                                          commandSource:weakSelf];
                BADGE_REPORT_LOG(4, 0x5f, @"send badge:%ld parameter check failed!",
                                 (long)weakSelf.badgeCount);
            } else if (code == 400) {
                [[JPUSHClientController sharedClient].badgeNumberManager stopDelayTimer:NO
                                                                          commandSource:weakSelf];
                BADGE_REPORT_LOG(4, 0x5a, @"send badge:%ld parameter validate failed!",
                                 (long)weakSelf.badgeCount);
            } else {
                [[JPUSHClientController sharedClient].badgeNumberManager stopDelayTimer:YES
                                                                          commandSource:weakSelf];
                BADGE_REPORT_LOG(4, 0x6a, @"warning:badge:%ld default error:%ld", (long)weakSelf.badgeCount,
                                 (long)code);
            }
        }
    };
}
- (NSMutableURLRequest *)request {
    return [self generateReportRequest];
}
- (NSMutableURLRequest *)generateReportRequest {
    NSMutableData *json = [NSMutableData dataWithData:[JCOREUtilities jsonToData:[self badgeBody]]];
    if (!json.length)
        return nil;
    NSData *encrypted = [JCOREDataSecurity aesEncryptData:json withKey:self.aesKey iv:"0000000000000000"];
    if (!encrypted.length) {
        BADGE_REPORT_LOG(2, 0x7c, @"report canceled, body data nil");
        return nil;
    }
    NSString *bodyString = [JCOREUtilities dataBase64:encrypted];
    NSData *body = [bodyString dataUsingEncoding:NSUTF8StringEncoding];
    if (!body.length) {
        BADGE_REPORT_LOG(2, 0x82, @"report canceled, body data nil");
        return nil;
    }
    NSString *authorization = [self authorizationString];
    if (!authorization.length)
        return nil;
    [self setAddressList:[JCORENetChannel addressListWithType:@"badgeType"]];
    NSString *timestamp = [NSString stringWithFormat:@"%ld", (long)self.reportTimeStamp];
    NSString *keySource = [NSString stringWithFormat:@"%@%@", [JCOREClientInfo appKey], timestamp];
    NSString *signKey = [JCOREUtilities stringByMD5:keySource];
    NSString *signSource =
        [NSString stringWithFormat:@"%@%@%@%@", authorization, timestamp, @"1.0", bodyString];
    NSString *sign = [JCOREDataSecurity hmacSHA256WithKey:signKey content:signSource];
    BADGE_REPORT_LOG(
        0x100, 0x97,
        @"badge send: Authorization:%@,\n x-jg-version:%@,\n x-jg-timestamp:%@,\n x-jg-sign:%@,\n bodystr:%@",
        authorization, @"1.0", timestamp, sign, bodyString, keySource);
    NSMutableURLRequest *generated = [[NSMutableURLRequest alloc] init];
    generated.HTTPMethod = @"POST";
    generated.HTTPBody = body;
    [generated setValue:authorization forHTTPHeaderField:@"Authorization"];
    [generated setValue:@"1.0" forHTTPHeaderField:@"x-jg-version"];
    [generated setValue:timestamp forHTTPHeaderField:@"x-jg-timestamp"];
    [generated setValue:sign forHTTPHeaderField:@"x-jg-sign"];
    generated.timeoutInterval = 30;
    return generated;
}
- (NSString *)authorizationString {
    NSString *encrypted = [JCOREDataSecurity rsaEncrypt:self.aesKey publicKey:[self publicKey]];
    NSString *credentials = [NSString stringWithFormat:@"%@:%@", [JCOREClientInfo appKey], encrypted];
    return [NSString
        stringWithFormat:@"Basic %@",
                         [JCOREUtilities dataBase64:[credentials dataUsingEncoding:NSUTF8StringEncoding]]];
}
- (NSString *)publicKey {
    return @"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCGHnOJjf1yD7SOWt0w4q5ZqER+VMOTJ9oPh+"
           @"9TNgW8WpXwCklSLxFDM46eHWzT2TV46gZ0A6xTNVHKX4zAW621jfb6qzljTvwzM5Cxa5n2440kU8HF/"
           @"EDu04yqUpWCsNXs+jQD4R2gjGE203LW87fBivmfq0mxPvIR94UdKf10YQIDAQAB";
}
- (BOOL)isSendRequestLock {
    JPUSHBadgeNumberController *controller = [JPUSHClientController sharedClient].badgeNumberManager;
    if (![self isEqual:controller.badgeReport])
        return YES;
    return [JPUSHClientController sharedClient].badgeNumberManager.isSendingLock;
}
- (void)setIsSendRequestLock:(BOOL)value {
    [[JPUSHClientController sharedClient].badgeNumberManager setIsSendingLock:value];
}
- (BOOL)cacheRequired {
    return NO;
}
- (BOOL)startDelaySend {
    [[JPUSHClientController sharedClient].badgeNumberManager startDelaySendBadge:self];
    return YES;
}
- (NSArray *)addressList {
    return addressList;
}
- (void)setAddressList:(NSArray *)value {
    addressList = value;
}
- (RecoveryOpaqueCallback)completionHandler {
    return completionHandler;
}
- (void)setCompletionHandler:(RecoveryOpaqueCallback)value {
    completionHandler = [value copy];
}
@end
#endif
