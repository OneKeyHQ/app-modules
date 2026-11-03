#import "JCOREHttpChannelController.h"
#import "JCOREHttpRequestController.h"
#import "JCOREReport.h"
#import "JCORETcpReport.h"
#import "JCOREThread.h"
#import "JCOREBusinessStorage.h"
#import "JCORENetUtilities.h"
#import "JCORENetworkReachabilityManager.h"
#import "RecoveredCoreService.h"
#import "RecoveredCache.h"
#import "JCORELog.h"
@interface JCORESisManager : NSObject
+ (instancetype)shared;
- (void)reportAddressListForKey:(NSString *)key completion:(void (^)(NSArray *addresses))completion;
@end
extern NSString *const kJCOREHttpReportHistoryNotification;
#define JCOREHttpChannelFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Controller/Network/JCOREHttpChannelController.m"
#define JCOREHttpChannelLog(sourceFunction, sourceLine, logFlag, asynchronous, ...) [JCORELog log:asynchronous level:0x3ff flag:logFlag context:0 file:JCOREHttpChannelFile function:sourceFunction line:sourceLine tag:nil format:__VA_ARGS__]
@implementation JCOREHttpChannelController
@synthesize stopService = _stopService, sessionController = _sessionController, requestList = _requestList;
@synthesize tcpReportController = _tcpReportController, lastSuccessReportAddress = _lastSuccessReportAddress;
- (instancetype)init {
    self = [super init];
    if (self) { _sessionController = [[JCOREURLSessionController alloc] init]; _tcpReportController = [[JCORETcpReportController alloc] init]; }
    return self;
}
- (void)invalidate { if (_sessionController) [_sessionController invalidate]; }
- (void)stopHttpService { JCOREHttpChannelLog(__PRETTY_FUNCTION__, 286, 0x100, YES, @"stopHttpService"); self.stopService = YES; }
- (void)resumeHttpService { JCOREHttpChannelLog(__PRETTY_FUNCTION__, 291, 0x100, YES, @"resumeHttpService"); self.stopService = NO; }
- (void)send:(id)input {
    if (![JCOREThread isMainThread]) { [JCOREThread performSel:@selector(send:) target:self withObject:input]; return; }
    if (![input conformsToProtocol:@protocol(JCOREHttpProtocol)]) {
        JCOREHttpChannelLog(__PRETTY_FUNCTION__, 301, 0x20, NO, @"send request object error, be canceled"); return;
    }
    id<JCOREHttpProtocol> object = input;
    if (!object.requestInfo && !object.request) { JCOREHttpChannelLog(__PRETTY_FUNCTION__, 305, 0x20, NO, @"send request object nil, be canceled"); return; }
    if (object.request) {
        BOOL hasAddress;
        if ([object respondsToSelector:@selector(addressList)]) {
            id addresses = object.addressList;
            hasAddress = [addresses isKindOfClass:NSArray.class] && [addresses count] != 0;
        } else hasAddress = object.request.URL.absoluteString != nil;
        if (hasAddress && object.reportFlag) {
            if ([self isRequesting:object.reportFlag]) return;
            [self addRequest:object.reportFlag];
            [self doRequest:object];
            return;
        }
    }
    [self doReport:object];
}
- (NSArray *)tcpReportAddressesWithTypes:(NSArray *)types {
    if (![types isKindOfClass:NSArray.class] || !types.count) return nil;
    NSArray *addresses = nil;
    for (NSString *type in types) {
        if (![type isKindOfClass:NSString.class] || !type.length) continue;
        NSDictionary *dictionary = [JCOREBusinessStorage getTcpReportAddressDict];
        id list = [dictionary objectForKey:type];
        if (![list isKindOfClass:NSArray.class] || ![list count]) list = [dictionary objectForKey:[JCOREService canStartTcpSSL] ? @"ssl" : @"normal"];
        addresses = [self sharedArray:addresses and:list];
    }
    JCOREHttpChannelLog(__PRETTY_FUNCTION__, 356, 0x100, YES, @"tcp report address list:%@, type:%@", addresses, types);
    return addresses;
}
- (void)doRequest:(id)input {
    if (![JCOREThread isMainThread]) { [JCOREThread performSel:@selector(doRequest:) target:self withObject:input]; return; }
    if (![input conformsToProtocol:@protocol(JCOREHttpProtocol)]) { JCOREHttpChannelLog(__PRETTY_FUNCTION__, 366, 0x20, NO, @"send request object error, be canceled"); return; }
    if (self.stopService) { JCOREHttpChannelLog(__PRETTY_FUNCTION__, 372, 0x100, YES, @"http service has stopped, return"); return; }
    id<JCOREHttpProtocol> object = input;
    void (^httpFallback)(void) = ^{
        const char *function = "-[JCOREHttpChannelController doRequest:]_block_invoke";
        if ([object isKindOfClass:JCOREReport.class] && [self isOnlySupportTcpReport:((JCOREReport *)object).types]) return;
        if (![JCORENetworkReachabilityManager sharedManager].reachable) {
            JCOREHttpChannelLog(function, 387, 0x100, YES, @"http request:network is unreachable, wait for network - %@", object);
            [[JCORENetworkReachabilityManager sharedManager] addHttpReachablityStatusChangeBlock:^(NSInteger status) {
                const char *callbackFunction = "-[JCOREHttpChannelController doRequest:]_block_invoke_2";
                JCOREHttpChannelLog(callbackFunction, 389, 0x100, YES, @"http request:network is unreachable, wait for network callback");
                if (![JCORENetworkReachabilityManager sharedManager].reachable) {
                    if ([object respondsToSelector:@selector(addressList)]) JCOREHttpChannelLog(callbackFunction, 392, 0x100, YES, @"http request:network is not Reachable, return: %@", object.addressList);
                    if (object.request.URL.absoluteString) JCOREHttpChannelLog(callbackFunction, 395, 0x100, YES, @"http request:network is not Reachable, return: %@", object.request.URL.absoluteString);
                    if (object.completionHandler) {
                        NSError *error = [NSError errorWithDomain:@"JIGUANGHttpErrorDomain" code:-1009 userInfo:@{NSLocalizedDescriptionKey: @"没有网络状态，请求被丢弃"}];
                        object.completionHandler(nil, error);
                    }
                } else {
                    JCOREHttpChannelLog(callbackFunction, 403, 0x100, YES, @"http request:network isreachable, retry - %@", object);
                    [JCOREThread performSel:@selector(doRequest:) target:self withObject:object];
                }
            }];
            return;
        }
        JCOREHttpRequestController *controller = [[JCOREHttpRequestController alloc] init];
        controller.requestId = object.reportFlag ?: @"";
        controller.sessionController = self.sessionController;
        __weak JCOREHttpRequestController *weakController = controller;
        controller.completionHandler = ^(id response, NSError *error) {
            if (object.reportFlag) [self removeRequest:object.reportFlag];
            if (object.completionHandler) object.completionHandler(response, error);
            if (!error) {
                JCOREHttpRequestController *completed = weakController;
                if (completed && completed.urlRequest.URL.absoluteString) [self saveSuccessfulAddress:completed.urlRequest.URL.absoluteString];
            }
        };
        NSMutableURLRequest *request = object.request;
        controller.urlRequest = request;
        if ([object respondsToSelector:@selector(addressList)]) {
            id addresses = object.addressList;
            if ([addresses isKindOfClass:NSArray.class] && [addresses count]) controller.addressList = [JCOREIpvPreferedUtilities getPreferedAddresses:addresses];
        }
        if (![controller.addressList isKindOfClass:NSArray.class]) {
            NSString *url = request.URL.absoluteString;
            if ([url isKindOfClass:NSString.class]) controller.addressList = @[url];
        }
        JCOREHttpChannelLog(function, 445, 0x100, YES, @"https report: report addresslist: %@", controller.addressList);
        [controller send];
    };
    if ([JCOREService getTcpReportStatus] && [object isKindOfClass:JCOREReport.class]) {
        JCOREReport *report = (JCOREReport *)object;
        NSData *data = report.packetData;
        NSUInteger length = data.length;
        JCOREHttpChannelLog(__PRETTY_FUNCTION__, 456, 0x100, YES, @"http report body size:%ld", (long)length);
        if (length - 1 < 0x77d7) {
            JCORETcpReport *tcp = [[JCORETcpReport alloc] init];
            tcp.bodyData = data;
            tcp.sid = report.seed;
            tcp.reportAddressList = [JCOREIpvPreferedUtilities getPreferedIpAddresses:[self tcpReportAddressesWithTypes:report.types]];
            NSArray *types = report.types;
            unsigned short rid = tcp.rid;
            tcp.completionHandler = ^(id response, NSError *error) {
                const char *function = "-[JCOREHttpChannelController doRequest:]_block_invoke";
                JCOREHttpChannelLog(function, 469, 0x100, YES, @"tcp send report result:%@, type:%@, rid:0x%x", error ? error.localizedDescription : @"success", types, rid);
                if (error) {
                    JCOREHttpChannelLog(function, 471, 0x100, YES, @"tcp send failed, change to http report, error:%ld,%@", (long)error.code, error.localizedDescription);
                    httpFallback();
                } else {
                    if (object.reportFlag) [self removeRequest:object.reportFlag];
                    if (object.completionHandler) object.completionHandler(response, nil);
                }
            };
            [self.tcpReportController send:tcp];
            return;
        }
    }
    httpFallback();
}
- (NSArray *)onlySupportTcpReportTypeList { return @[@"sdk_jid"]; }
- (BOOL)isOnlySupportTcpReport:(NSArray *)types {
    if (!types || ![types isKindOfClass:NSArray.class] || !types.count) return NO;
    for (id type in [self onlySupportTcpReportTypeList]) if ([types containsObject:type]) return YES;
    return NO;
}
- (void)doReport:(id)input {
    if (![JCOREThread isMainThread]) { [JCOREThread performSel:@selector(doReport:) target:self withObject:input]; return; }
    if (![input conformsToProtocol:@protocol(JCOREHttpProtocol)]) { JCOREHttpChannelLog(__PRETTY_FUNCTION__, 516, 0x20, NO, @"send request object error, be canceled"); return; }
    id<JCOREHttpProtocol> object = input;
    JCOREReport *report = [[JCOREReport alloc] init];
    report.accountIDRequired = object.accountIDRequired;
    report.cacheRequired = object.cacheRequired;
    report.requestInfo = object.requestInfo;
    report.reportFlag = object.reportFlag;
    if (report.reportFlag) { if ([self isRequesting:report.reportFlag]) return; [self addRequest:report.reportFlag]; }
    if (report.cacheRequired) [JCORECacheUtilities setReportObject:report.requestInfo forKey:report.reportFlag];
    if (self.stopService) { JCOREHttpChannelLog(__PRETTY_FUNCTION__, 537, 0x100, YES, @"http service has stopped, return"); return; }
    __weak JCOREReport *weakReport = report;
    report.completionHandler = ^(id response, NSError *error) {
        JCOREReport *completed = weakReport;
        NSString *result = error ? [NSString stringWithFormat:@"failed, error code:%ld, msg:%@", (long)error.code, error.localizedDescription] : @"success";
        id information = completed.requestInfo;
        NSUInteger line = [information isKindOfClass:NSDictionary.class] && information[@"content"] ? 549 : 552;
        JCOREHttpChannelLog("-[JCOREHttpChannelController doReport:]_block_invoke", line, 8, YES, @"send report:%@ end, result:%@", completed.requestInfo, result);
        if (object.completionHandler) object.completionHandler(response, error);
        if (!error) {
            [JCORECacheUtilities setReportObject:nil forKey:completed.reportFlag];
            [NSNotificationCenter.defaultCenter postNotificationName:kJCOREHttpReportHistoryNotification object:nil];
        } else if (completed.cacheRequired) [JCORECacheUtilities setReportObject:completed.requestInfo forKey:completed.reportFlag];
    };
    void (^finishAddresses)(NSArray *) = ^(NSArray *addresses) {
        if (![report isCancelReport]) {
            if ([addresses isKindOfClass:NSArray.class] && addresses.count) report.addressList = [addresses copy];
            else JCOREHttpChannelLog("-[JCOREHttpChannelController doReport:]_block_invoke", 584, 0x100, YES, @"report array : %@", addresses);
            [self doRequest:report];
        } else {
            if (report.reportFlag) [self removeRequest:report.reportFlag];
            NSError *error = [NSError errorWithDomain:@"JIGUANGHttpErrorDomain" code:-999 userInfo:@{NSLocalizedDescriptionKey: @"uid error, request temporarily canceled"}];
            report.completionHandler(NSData.data, error);
        }
    };
    __block NSArray *combined = nil;
    __block NSInteger received = 0;
    NSArray *types = [report.types copy];
    for (NSString *type in types) {
        [[JCORESisManager shared] reportAddressListForKey:type completion:^(NSArray *addresses) {
            received++;
            combined = [self sharedArray:combined and:addresses];
            combined = [self adjustAddressPriorityForReport:combined];
            JCOREHttpChannelLog("-[JCOREHttpChannelController doReport:]_block_invoke", 597, 0x100, YES, @"report address list:%@,type:%@", combined, type);
            if ((NSUInteger)received == types.count) finishAddresses(combined);
        }];
    }
    if (!types.count) {
        if (report.reportFlag) [self removeRequest:report.reportFlag];
        NSError *error = [NSError errorWithDomain:@"JIGUANGHttpErrorDomain" code:-999 userInfo:@{NSLocalizedDescriptionKey: @"type error, request temporarily canceled"}];
        report.completionHandler(NSData.data, error);
    }
}
- (BOOL)requesting { return self.requestList && self.requestList.count != 0; }
- (BOOL)isRequesting:(NSString *)identifier {
    if (!identifier || !identifier.length) return NO;
    id list = [self.requestList copy];
    return [list isKindOfClass:NSArray.class] && [list count] && [list containsObject:identifier];
}
- (void)addRequest:(NSString *)identifier {
    if (identifier && identifier.length && ![self isRequesting:identifier]) {
        NSMutableArray *list = [[NSMutableArray alloc] initWithArray:self.requestList];
        [list addObject:identifier]; self.requestList = [list copy];
    }
}
- (void)removeRequest:(NSString *)identifier {
    if (identifier && identifier.length && [self isRequesting:identifier]) {
        NSMutableArray *list = [[NSMutableArray alloc] initWithArray:self.requestList];
        [list removeObject:identifier]; self.requestList = [list copy];
    }
}
- (NSArray *)sharedArray:(NSArray *)left and:(NSArray *)right {
    id result = left;
    if ([right isKindOfClass:NSArray.class]) {
        result = right;
        if ([left isKindOfClass:NSArray.class]) {
            NSMutableArray *shared = [[NSMutableArray alloc] init];
            [right enumerateObjectsUsingBlock:^(id value, NSUInteger index, BOOL *stop) { if ([left containsObject:value]) [shared addObject:value]; }];
            return [shared copy];
        }
    }
    return [result copy];
}
- (NSArray *)adjustAddressPriorityForReport:(NSArray *)addresses {
    NSString *last = [self getLastSuccessfulAddress];
    if (last && addresses.count > 1 && [addresses containsObject:last]) {
        NSMutableArray *priority = [NSMutableArray arrayWithArray:addresses];
        [priority removeObject:last]; [priority insertObject:last atIndex:0]; return priority;
    }
    return addresses;
}
- (void)saveSuccessfulAddress:(NSString *)address {
    if (!address || ![address isKindOfClass:NSString.class] || !address.length || [self.lastSuccessReportAddress isEqualToString:address]) return;
    NSString *path = [NSURL URLWithString:@"https://stats.jpush.cn/v3/report"].relativePath;
    if (path && [address hasSuffix:path]) {
        JCOREHttpChannelLog(__PRETTY_FUNCTION__, 713, 0x100, YES, @"https report: save last success report address: %@", address);
        self.lastSuccessReportAddress = address;
    }
}
- (NSString *)getLastSuccessfulAddress { return self.lastSuccessReportAddress; }
@end
