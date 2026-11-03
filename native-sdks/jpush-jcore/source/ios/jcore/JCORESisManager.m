#import "JCORESisManager.h"
#import "JCOREUdpSis.h"
#import "JCOREHttpSis.h"
#import "JCORESisRequestPool.h"
#import "JCOREPropertyModels.h"
#import "JCOREBusinessStorage.h"
#import "JCOREAddress.h"
#import "JCOREAddressAnalysisController.h"
#import "JCORENetUtilities.h"
#import "JCOREIpvxHelper.h"
#import "JCORENetworkReachabilityManager.h"
#import "JCOREUtilities.h"
#import "JCORELog.h"
NSString *const JCORE_AA_SIS_TYPE = @"jcore_aa_sis_type";
const NSInteger JCORE_AA_TIME = 10;
#define JCORESisFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Sis/JCORESisManager.m"
#define JCORESisLog(flagValue, sourceLine, ...) [JCORELog log:YES level:0x3ff flag:flagValue context:0 file:JCORESisFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]
#define JCORESisBlockLog(functionValue, sourceLine, ...) [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCORESisFile function:functionValue line:sourceLine tag:nil format:__VA_ARGS__]
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
@implementation JCORESisManager
@synthesize udpSis = _udpSis, httpSis = _httpSis, httpSisCompletionArray = _httpSisCompletionArray, sisHostDomains = _sisHostDomains, defaultSisIps = _defaultSisIps, sisRequestCompletion = _sisRequestCompletion, timeout = _timeout, fetchConAddressTimer = _fetchConAddressTimer, longfetchConAddressTimer = _longfetchConAddressTimer, requestPoolArray = _requestPoolArray, tmp_defaultSisIp_sisResults = _tmp_defaultSisIp_sisResults, needWait_defaultSisIp_sisResults = _needWait_defaultSisIp_sisResults, tmp_srvRecord_sisResults = _tmp_srvRecord_sisResults, needWait_srvRecord_sisResults = _needWait_srvRecord_sisResults, httpReportAddress = _httpReportAddress, userReportAddress = _userReportAddress, httpSisAddressList = _httpSisAddressList;
+ (instancetype)shared { static JCORESisManager *manager; static dispatch_once_t once; dispatch_once(&once, ^{ manager = [[JCORESisManager alloc] init]; }); return manager; }
- (id)randamArray:(id)array {
    return [array count] > 1 ? [array sortedArrayUsingComparator:^NSComparisonResult(id first, id second) { return arc4random_uniform(2) ? [first compare:second] : [second compare:first]; }] : array;
}
- (void)setupAddressConfig {
    NSArray *domains = [self randamArray:@[@"s.jpush.cn", @"sis.jpush.io", @"easytomessage.com"]];
    NSMutableArray *hosts = [NSMutableArray array]; [hosts addObjectsFromArray:domains]; [hosts addObject:@"sis-ipv6.jpush.cn"];
    _sisHostDomains = [JCOREIpvPreferedUtilities getPreferedAddresses:hosts];
    _defaultSisIps = [self randamArray:@[@"115.120.193.179", @"113.45.40.226", @"120.46.16.242", @"120.46.84.108"]];
}
- (instancetype)init { if ((self = [super init])) { [self setupAddressConfig]; _timeout = 6; _requestPoolArray = [NSMutableArray array]; _httpSisCompletionArray = [NSMutableArray array]; } return self; }
- (void)dealloc { [self cancelSisRequest]; }
- (void)cancelSisRequest { JCORESisLog(0x100, 126, @"Action - %@", NSStringFromSelector(_cmd)); [self stop]; _sisRequestCompletion = nil; }
- (void)stop { [self stopFetchConAddressTimer]; [self stopLongFetchConAddressTimer]; [self resetRequestPool]; }
- (JCOREUdpSis *)udpSis { if (!_udpSis) _udpSis = [[JCOREUdpSis alloc] init]; return _udpSis; }
- (JCOREHttpSis *)httpSis { if (!_httpSis) _httpSis = [[JCOREHttpSis alloc] init]; return _httpSis; }
- (id)defaultSisSrvDomain { return @"_psis._udp.jpush.cn"; }
- (void)fetchConAddressWithTimeout:(double)timeout completion:(JCORESisManagerCompletion)completion {
    if (!completion) return;
    if (timeout >= 0) _timeout = timeout;
    [self stop]; id cached = [self getCacheWithUnknown];
    if ([self isSisOnProtect] && cached) { JCORESisLog(0x100, 182, @"sis cache:%@ is on protect,return", [cached description]); completion(cached, YES, nil); }
    else { _sisRequestCompletion = [completion copy]; [self startFetchConAddressTimer]; [self startLongFetchConAddressTimer]; [self sisRequestWithPriorityModule]; }
}
- (void)sisRequestWithPriorityModule {
    JCORESisLog(0x100, 202, @"Action - sisRequestWithPriorityModule");
    NSMutableArray *models = [NSMutableArray array];
    for (id host in _sisHostDomains) { JCORESisRequestPoolModel *model = [[JCORESisRequestPoolModel alloc] init]; model.isDomainString = YES; model.domain = host; [models addObject:model]; }
    JCORESisLog(0x100, 210, @"sis request from _sisHostDomains:%@", [_sisHostDomains description]);
    id previous = [JCOREBusinessStorage getLastSuccessSisIps];
    if ([previous isKindOfClass:NSArray.class] && [previous count]) {
        NSMutableArray *addresses = [NSMutableArray array]; id ipv4 = nil, ipv6 = nil;
        for (id ip in previous) { if ([JCOREAddress isIpv4:[JCOREAddress host:ip]]) ipv4 = ip; else ipv6 = ip; }
        long long type = JCOREIpvxHelper.instance.ipvxType;
        if (type == 4) { if (ipv6) [addresses addObject:ipv6]; }
        else if (type == 3) { if (ipv4) [addresses addObject:ipv4]; }
        else { if (ipv4) [addresses addObject:ipv4]; if (ipv6) [addresses addObject:ipv6]; }
        for (id ip in [addresses copy]) { JCORESisRequestPoolModel *model = [[JCORESisRequestPoolModel alloc] init]; model.isDomainString = NO; model.ip = ip; [models addObject:model]; }
        JCORESisLog(0x100, 249, @"sis request from lastSuccessSisIp:%@", [addresses description]);
    }
    JCORESisRequestPool *pool = [[JCORESisRequestPool alloc] init]; [self.requestPoolArray addObject:pool];
    __weak JCORESisManager *weakSelf = self;
    [pool requestWithDomains:[models copy] completion:^(NSArray *result, BOOL completed, NSError *error) {
        JCORESisManager *strongSelf = weakSelf;
        if (!error && result.count) {
            JCORESisBlockLog("-[JCORESisManager sisRequestWithPriorityModule]_block_invoke", 259, @"sis request from _sisHostDomains or lastSuccessSisIp success, result:%@", [result description]);
            [strongSelf callBackSisRequestCompletionSuccess:[strongSelf sortSisArray:result] type:0];
        } else {
            JCORESisBlockLog("-[JCORESisManager sisRequestWithPriorityModule]_block_invoke", 262, @"sis request from _sisHostDomains or lastSuccessSisIp failed, error:%@", [error description]);
            [strongSelf sisRequestWithSubordinateModule];
        }
    }];
}
- (void)sisRequestWithSubordinateModule { JCORESisLog(0x100, 276, @"Action - %@", NSStringFromSelector(_cmd)); [self.requestPoolArray removeAllObjects]; [self requestFromSisIp]; [self requestFromSrvRecode]; }
- (void)requestFromSisIp {
    JCORESisLog(0x100, 284, @"Action - %@", NSStringFromSelector(_cmd)); _needWait_defaultSisIp_sisResults = YES;
    NSMutableArray *models = [NSMutableArray array]; id addresses = [self sisIpsFromServer];
    if ([addresses isKindOfClass:NSArray.class] && [addresses count]) JCORESisLog(0x100, 291, @"sis request from server sisIps:%@", [addresses description]);
    else { addresses = _defaultSisIps; JCORESisLog(0x100, 300, @"sis request from default sisIps:%@", [_defaultSisIps description]); }
    for (id ip in addresses) { JCORESisRequestPoolModel *model = [[JCORESisRequestPoolModel alloc] init]; model.isDomainString = NO; model.ip = ip; [models addObject:model]; }
    if (!models.count) { _needWait_defaultSisIp_sisResults = NO; [self mergeSisResultFromSubordinateModule]; return; }
    JCORESisRequestPool *pool = [[JCORESisRequestPool alloc] init]; [self.requestPoolArray addObject:pool];
    __weak JCORESisManager *weakSelf = self;
    [pool requestWithDomains:[models copy] completion:^(NSArray *result, BOOL completed, NSError *error) {
        JCORESisManager *strongSelf = weakSelf; strongSelf.needWait_defaultSisIp_sisResults = NO;
        if (!error && result.count) { JCORESisBlockLog("-[JCORESisManager requestFromSisIp]_block_invoke", 322, @"sis request from server sisIps or default sisIps success, result:%@", [result description]); strongSelf.tmp_defaultSisIp_sisResults = [strongSelf sortSisArray:result]; }
        else JCORESisBlockLog("-[JCORESisManager requestFromSisIp]_block_invoke", 325, @"sis request from server sisIps or default sisIps failed, error:%@", [error description]);
        [strongSelf mergeSisResultFromSubordinateModule];
    }];
}
- (void)requestFromSrvRecode {
    JCORESisLog(0x100, 333, @"Action - %@", NSStringFromSelector(_cmd)); _needWait_srvRecord_sisResults = YES;
    id domain = [self defaultSisSrvDomain]; JCORESisLog(0x100, 337, @"run srv from custom or default sis srv domain:%@", [domain description]);
    id records = [self runWithSRVName:domain]; JCORESisLog(0x100, 339, @"run srv result:%@", [records description]);
    if (![records count]) { _needWait_srvRecord_sisResults = NO; [self mergeSisResultFromSubordinateModule]; return; }
    NSMutableArray *sorted = [NSMutableArray arrayWithArray:records];
    [sorted sortUsingComparator:^NSComparisonResult(id first, id second) {
        int firstPriority = [[first objectForKey:kJCORESRVResolverPriority] intValue], secondPriority = [[second objectForKey:kJCORESRVResolverPriority] intValue];
        if (firstPriority == secondPriority) {
            id firstTarget = [first objectForKey:kJCORESRVResolverTarget], secondTarget = [second objectForKey:kJCORESRVResolverTarget];
            if ([firstTarget isKindOfClass:NSString.class] && [firstTarget length] && [secondTarget isKindOfClass:NSString.class] && [secondTarget length] && [JCOREUtilities checkIPAddress:firstTarget] && ![JCOREUtilities checkIPAddress:secondTarget]) return 1;
        }
        // The original comparator returns only 0/1, rather than a three-way ordering.
        return firstPriority < secondPriority;
    }];
    JCORESisLog(0x100, 367, @"srv result Sort, result:%@", [records description]);
    NSMutableArray *models = [NSMutableArray array];
    for (id record in [sorted copy]) {
        id target = [record objectForKey:kJCORESRVResolverTarget];
        if (![target isKindOfClass:NSString.class] || ![target length]) continue;
        id addresses = [self getAddressFromDNS:target];
        for (id ip in addresses) {
            id portValue = [record objectForKey:kJCORESRVResolverPort];
            unsigned short port = portValue && [portValue respondsToSelector:@selector(unsignedShortValue)] ? [portValue unsignedShortValue] : 0;
            if (!port) port = 19000;
            JCORESisRequestPoolModel *model = [[JCORESisRequestPoolModel alloc] init]; model.isDomainString = NO; model.ip = [NSString stringWithFormat:@"%@:%d", ip, port]; [models addObject:model];
        }
    }
    if (!models.count) { JCORESisLog(0x100, 401, @"do dns from Previous srv result failed"); _needWait_srvRecord_sisResults = NO; [self mergeSisResultFromSubordinateModule]; return; }
    JCORESisLog(0x100, 407, @"do dns from Previous srv result, result:");
    for (JCORESisRequestPoolModel *model in [models copy]) JCORESisLog(0x100, 410, @"%@", model.ip);
    JCORESisRequestPool *pool = [[JCORESisRequestPool alloc] init]; [self.requestPoolArray addObject:pool];
    __weak JCORESisManager *weakSelf = self;
    [pool requestWithDomains:[models copy] completion:^(NSArray *result, BOOL completed, NSError *error) {
        JCORESisManager *strongSelf = weakSelf; strongSelf.needWait_srvRecord_sisResults = NO;
        if (!error && result.count) { JCORESisBlockLog("-[JCORESisManager requestFromSrvRecode]_block_invoke_2", 420, @"sis request from custom or default sis srv domain, result:%@", [result description]); strongSelf.tmp_srvRecord_sisResults = [strongSelf sortSisArray:result]; }
        else JCORESisBlockLog("-[JCORESisManager requestFromSrvRecode]_block_invoke_2", 423, @"sis request from custom or default sis srv failed, error:%@", [error description]);
        [strongSelf mergeSisResultFromSubordinateModule];
    }];
}
- (void)mergeSisResultFromSubordinateModule {
    JCORESisLog(0x100, 433, @"Action - %@", NSStringFromSelector(_cmd));
    JCORESisLog(0x100, 434, @"needWait_srvRecord_sisResults:%d, needWait_defaultSisIp_sisResults:%d", self.needWait_srvRecord_sisResults, self.needWait_defaultSisIp_sisResults);
    if (self.needWait_srvRecord_sisResults || self.needWait_defaultSisIp_sisResults) return;
    NSMutableArray *result = [NSMutableArray array];
    if ([self.tmp_defaultSisIp_sisResults count]) [result addObjectsFromArray:self.tmp_defaultSisIp_sisResults];
    if ([self.tmp_srvRecord_sisResults count]) [result addObjectsFromArray:self.tmp_srvRecord_sisResults];
    if (result.count) { JCORESisLog(0x100, 447, @"mergeSisResultFromSubordinateModule result:%@", result.description); [self callBackSisRequestCompletionSuccess:[result copy] type:0]; }
    else { JCORESisLog(0x100, 450, @"mergeSisResultFromSubordinateModule failed"); [self callBackSisRequestCompletionFailed:NO]; }
    self.tmp_defaultSisIp_sisResults = nil; self.tmp_srvRecord_sisResults = nil; _needWait_defaultSisIp_sisResults = YES; _needWait_srvRecord_sisResults = YES;
}
- (void)resetRequestPool { while (_requestPoolArray.count) { JCORESisRequestPool *pool = _requestPoolArray.firstObject; [_requestPoolArray removeObject:pool]; [pool endCurrentRequest]; } }
- (void)startFetchConAddressTimer { [self stopFetchConAddressTimer]; _fetchConAddressTimer = [NSTimer scheduledTimerWithTimeInterval:_timeout target:self selector:@selector(fetchConAddressTimeout) userInfo:nil repeats:NO]; }
- (void)stopFetchConAddressTimer { if (_fetchConAddressTimer.isValid) [_fetchConAddressTimer invalidate]; _fetchConAddressTimer = nil; }
- (void)fetchConAddressTimeout { JCORESisLog(0x100, 486, @"short timer timeOut"); [self stopFetchConAddressTimer]; id cached = [self getCacheWithUnknown]; if (cached && self.sisRequestCompletion) self.sisRequestCompletion(cached, YES, nil); }
- (void)startLongFetchConAddressTimer { [self stopLongFetchConAddressTimer]; _longfetchConAddressTimer = [NSTimer scheduledTimerWithTimeInterval:_timeout + _timeout target:self selector:@selector(longFetchConAddressTimeout) userInfo:nil repeats:NO]; }
- (void)stopLongFetchConAddressTimer { if (_longfetchConAddressTimer.isValid) [_longfetchConAddressTimer invalidate]; _longfetchConAddressTimer = nil; }
- (void)longFetchConAddressTimeout {
    JCORESisLog(0x100, 513, @"long timer timeOut");
    id result = [self.tmp_defaultSisIp_sisResults count] ? [self.tmp_defaultSisIp_sisResults copy] : [self.tmp_srvRecord_sisResults count] ? [self.tmp_srvRecord_sisResults copy] : nil;
    if ([result count]) [self callBackSisRequestCompletionSuccess:result type:4]; else [self callBackSisRequestCompletionFailed:YES];
}
- (void)callBackSisRequestCompletionSuccess:(id)addresses type:(int)type {
    [self stop]; id result = [self sortSisArray:addresses]; double now = [JCOREUtilities currentTimestamp]; id state = [self stringFromState:type];
    for (id address in result) {
        JCOREAddressAnalysis *analysis = [[JCOREAddressAnalysis alloc] initWithType:JCORE_AA_SIS_TYPE address:address statusCode:2 requestTime:now costTime:0.1 netType:[JCOREUtilities ctRadioType] timeTypeDic:@{state:@(now)}];
        [JCOREAddressAnalysisController.instance addAddressAnalysis:analysis];
    }
    JCORESisLog(0x100, 543, @"call back sis request success, result:%@", [addresses description]);
    if (self.sisRequestCompletion) self.sisRequestCompletion(result, NO, nil);
    _lastSucceedSisTime = now; _status = JCORENetworkReachabilityManager.sharedManager.networkReachabilityStatus;
}
- (void)callBackSisRequestCompletionFailed:(BOOL)timeout {
    NSError *error = [NSError errorWithDomain:@"sisRequest" code:-1 userInfo:@{NSLocalizedDescriptionKey:timeout ? @"timeout" : @"failed"}];
    [self stop]; JCORESisLog(0x100, 560, @"call back sis request failed, error:%@", error.description);
    if (self.sisRequestCompletion) self.sisRequestCompletion(nil, NO, error);
}
- (id)sortSisArray:(id)addresses {
    NSMutableArray *result = [NSMutableArray array];
    for (id address in addresses) {
        if ([address isKindOfClass:NSString.class]) [result addObject:address];
        else if ([address isKindOfClass:NSArray.class]) for (id element in address) [result addObject:element];
    }
    return [result copy];
}
- (id)getCacheWithSuccess { return [self getCacheWithStatus:3]; }
- (id)getCacheWithUnknown { return [self getCacheWithStatus:2]; }
- (id)getCacheWithStatus:(long long)status { return [JCOREAddressAnalysisController.instance addressListForType:JCORE_AA_SIS_TYPE statusCode:status within:10]; }
- (BOOL)isSisOnProtect { JCORESisLog(8, 597, @"Action - isSisOnProtect"); if (![self isNetworkChanged] && ![self isLastSisTooLong]) return YES; JCORESisLog(8, 599, @"sis is not on protect"); return NO; }
- (BOOL)isLastSisTooLong { double now = JCOREUtilities.currentTimestamp; return now > 0 && now - _lastSucceedSisTime > 180; }
- (BOOL)isNetworkChanged { return _status != JCORENetworkReachabilityManager.sharedManager.networkReachabilityStatus && _status != 0; }
- (void)resetSisCache { JCORESisLog(8, 615, @"Action - reset sis cache"); _lastSucceedSisTime = 0; _status = 0; }
- (id)getAddressFromDNS:(id)domain {
    id result = [JCOREAddressAnalysisController.instance getAddressFromDNS:domain timeout:2];
    if (![result isKindOfClass:NSDictionary.class] || ![result count]) return nil;
    id ipv4 = [result objectForKeyedSubscript:JCORE_IPV4_Key], ipv6 = [result objectForKeyedSubscript:JCORE_IPV6_Key];
    if ((ipv4 && ![ipv4 isKindOfClass:NSArray.class]) || (ipv6 && ![ipv6 isKindOfClass:NSArray.class]) || (![ipv6 count] && ![ipv4 count])) return nil;
    ipv4 = [self randamArray:ipv4]; ipv6 = [self randamArray:ipv6]; long long type = JCOREIpvxHelper.instance.ipvxType;
    if (type == 4) return [ipv6 count] ? ipv6 : nil;
    if (type == 3) return [ipv4 count] ? ipv4 : nil;
    NSMutableArray *addresses = [NSMutableArray array];
    if (type == 2) { if ([ipv6 count]) [addresses addObjectsFromArray:ipv6]; if ([ipv4 count]) [addresses addObjectsFromArray:ipv4]; }
    else { if ([ipv4 count]) [addresses addObjectsFromArray:ipv4]; if ([ipv6 count]) [addresses addObjectsFromArray:ipv6]; }
    return addresses.count ? [addresses copy] : nil;
}
- (void)removeDNSCacheWithDomain:(id)domain { [JCOREAddressAnalysisController.instance removeDNSCacheWithDomain:domain]; }
- (id)runWithSRVName:(id)name { return [JCOREAddressAnalysisController.instance getAddressFromSRV:name timeout:1]; }
- (void)removeResolver { [JCOREAddressAnalysisController.instance removeSrvRecode]; }
- (id)sisIpsFromServer { return [JCOREBusinessStorage getSisIpsFromServer]; }
- (id)defaultConnHost { return @"im64.jpush.cn"; }
- (id)defaultConnSrv { return @"_im64._tcp.jpush.cn"; }
- (id)defaultConnectDomainAddress {
    JCORESisLog(0x100, 741, @"Action - get con adress from default domain");
    JCORESisLog(0x100, 743, @"try to get service conn host");
    id host = [JCOREBusinessStorage getDefaultConnHost];
    if ([host isKindOfClass:NSString.class] && [host length]) JCORESisLog(0x100, 746, @"service con host:%@", host);
    else { host = [self defaultConnHost]; JCORESisLog(0x100, 749, @"get default conn host: %@", host); }
    JCORESisLog(0x100, 752, @"try to get service srv record");
    id srv = [JCOREBusinessStorage getDefaultSRVRecord];
    if ([srv isKindOfClass:NSString.class] && [srv length]) JCORESisLog(0x100, 755, @"service srv record:%@", srv);
    else { srv = [self defaultConnSrv]; JCORESisLog(0x100, 758, @"get default conn srv:%@", srv); }
    id records = [self runWithSRVName:srv]; JCORESisLog(0x100, 762, @"run srv name,result:%@", [records description]);
    NSMutableArray *addresses = [NSMutableArray array]; NSMutableDictionary *cache = [NSMutableDictionary dictionary];
    for (id record in records) {
        if (![record isKindOfClass:NSDictionary.class] || ![record count]) continue;
        id target = [record objectForKey:kJCORESRVResolverTarget];
        if (![target isKindOfClass:NSString.class] || ![target length]) target = host;
        id ips = [cache valueForKey:target];
        if (!ips) {
            ips = [self getAddressFromDNS:target];
            if ((! [ips isKindOfClass:NSArray.class] || ![ips count]) && ![target isEqualToString:host]) { ips = [self getAddressFromDNS:host]; target = host; }
            if (![ips isKindOfClass:NSArray.class] || ![ips count]) continue;
            [cache setValue:ips forKey:target];
        }
        unsigned short port = [[record objectForKey:kJCORESRVResolverPort] unsignedShortValue];
        if (!port) continue;
        for (id ip in ips) {
            if ([ip isKindOfClass:NSString.class] && [ip length]) {
                id address = [ip stringByAppendingFormat:@":%d", port];
                if (![addresses containsObject:address]) [addresses addObject:address];
            }
        }
    }
    JCORESisLog(0x100, 804, @"dns all srv result, addressArray:%@", addresses.description);
    if (!addresses.count) {
        JCORESisLog(0x100, 807, @"addressArray is nil"); JCORESisLog(0x100, 808, @"parse default connect host:%@", host);
        id ips = [self getAddressFromDNS:host];
        for (id ip in ips) {
            NSArray *ports = [@[@"7000", @"7002", @"7003", @"7004", @"7005", @"7006", @"7007", @"7008", @"7009"] sortedArrayUsingComparator:^NSComparisonResult(id first, id second) { return arc4random_uniform(2) ? [first compare:second] : [second compare:first]; }];
            for (id port in ports) [addresses addObject:[ip stringByAppendingFormat:@":%@", port]];
        }
        JCORESisLog(0x100, 824, @"parse default con host, addressArray:%@", addresses.description);
    }
    return addresses;
}
- (NSArray *)userReportAddress { id address = [JCOREBusinessStorage getGuaranteeSSLUserReportAddress]; return address ?: [JCOREBusinessStorage getGuaranteeUserReportAddress]; }
- (NSArray *)httpReportAddress { id address = [JCOREBusinessStorage getGuaranteeHttpsReportAddress]; return address ?: [JCOREBusinessStorage getGuaranteeHttpReportAddress]; }
- (void)forceRequestReportAddress { [self requestReportAddressWithForce:YES]; }
- (void)requestReportAddress { [self requestReportAddressWithForce:NO]; }
- (void)requestReportAddressWithForce:(BOOL)force {
    if (self.httpSis.requesting || (!force && !self.httpSis.expired)) return;
    JCORESisLog(0x100, 855, @"cache invalid, fetch new urls");
    NSMutableArray *addresses = [[NSMutableArray alloc] init]; id server = self.httpSis.sisIpsFromServer;
    if ([server isKindOfClass:NSArray.class] && [server count]) [addresses addObjectsFromArray:server];
    [addresses addObjectsFromArray:JCOREHttpSis.httpSisDefaultAddress];
    self.httpSisAddressList = [[JCOREIpvPreferedUtilities getPreferedAddresses:addresses] copy];
    JCORESisLog(0x100, 871, @"http sis urls:%@", self.httpSisAddressList);
    [self requestReportAddress:@0];
}
- (void)requestReportAddress:(id)index {
    id addresses = self.httpSisAddressList;
    if (![addresses isKindOfClass:NSArray.class]) { JCORESisLog(0x40, 880, @"http sis canceled, sis urls null"); return; }
    if (![index isKindOfClass:NSNumber.class]) return;
    NSUInteger position = [index unsignedIntegerValue]; if (position >= [addresses count]) return;
    id address = [addresses objectAtIndexedSubscript:position]; if (![address isKindOfClass:NSString.class] || ![address length]) return;
    __weak JCORESisManager *weakSelf = self;
    [self.httpSis requst:address result:^(BOOL success) {
        JCORESisManager *strongSelf = weakSelf;
        if (!success) { [strongSelf performSelector:@selector(requestReportAddress:) withObject:@(position + 1) afterDelay:0]; return; }
        id handlers = [strongSelf.httpSisCompletionArray copy];
        for (JCOREHttpSisCompletion *handler in handlers) {
            id result = [strongSelf cachedReportAddressList:handler.type];
            [JCORELog log:YES level:0x3ff flag:8 context:0 file:JCORESisFile function:"-[JCORESisManager requestReportAddress:]_block_invoke" line:901 tag:nil format:@"httpsis, type:%@, address list:%@", handler.type, result];
            if (handler.timer) [handler.timer invalidate];
            if (handler.completion) handler.completion(result);
            JCORESisBlockLog("-[JCORESisManager requestReportAddress:]_block_invoke", 908, @"httpsis,end report delay timer");
        }
        [strongSelf.httpSisCompletionArray removeAllObjects];
    }];
}
- (id)cachedReportAddressList:(id)key {
    if (![self.httpSis.reportAddressDict isKindOfClass:NSDictionary.class]) return nil;
    id addresses = [self reportAddressAppendSuffix:[self.httpSis.reportAddressDict objectForKeyedSubscript:key]];
    if (![addresses isKindOfClass:NSArray.class]) {
        addresses = [self reportAddressAppendSuffix:self.httpSis.normalReportAddressList];
        if (![addresses isKindOfClass:NSArray.class]) return nil;
    }
    return addresses;
}
- (void)reportAddressListForKey:(id)key completion:(void (^ __unsafe_unretained)(NSArray *))completion {
    // Inspect the original NSNull boundary before ARC copies a potential non-block.
    id input = (__bridge id)(__bridge void *)completion;
    if (![key isKindOfClass:NSString.class] || !input || [input isKindOfClass:NSNull.class]) return;
    void (^handler)(NSArray *) = input;
    if ([key isEqualToString:@"active_launch"] || [key isEqualToString:@"active_terminate"]) key = @"active_user";
    id cached = [self cachedReportAddressList:key];
    if ([cached isKindOfClass:NSArray.class] && [cached count]) {
        JCORESisLog(8, 948, @"httpsis, type:%@, address list:%@", key, cached); handler(cached);
    } else {
        JCOREHttpSisCompletion *pending = [[JCOREHttpSisCompletion alloc] init]; pending.type = key; pending.completion = handler;
        pending.timer = [NSTimer scheduledTimerWithTimeInterval:10 target:self selector:@selector(httpSisRequestTimeOut:) userInfo:pending repeats:NO];
        [self.httpSisCompletionArray addObject:pending]; JCORESisLog(0x100, 957, @"httpsis,start report delay timer");
    }
    [self requestReportAddress];
}
- (id)reportAddressAppendSuffix:(id)addresses {
    if (![addresses isKindOfClass:NSArray.class] || ![addresses count]) return nil;
    id first = [[JCOREBusinessStorage getReportAddress] objectAtIndexedSubscript:0];
    id defaults = [JCOREBusinessStorage getReportAddress];
    id guarantee = [self.httpReportAddress count] ? [self.httpReportAddress firstObject] : nil;
    id path = [NSURL URLWithString:first].path ?: @"/v3/report";
    NSMutableArray *result = [[NSMutableArray alloc] init]; BOOL containsGuarantee = NO;
    for (id address in addresses) {
        if ([address isKindOfClass:NSString.class] && [address length]) {
            id url = [address stringByAppendingString:path];
            if (url) { [result addObject:url]; containsGuarantee |= [url isEqualToString:guarantee]; }
        }
    }
    for (id address in defaults) if ([address isKindOfClass:NSString.class] && ![result containsObject:address]) [result addObject:address];
    if (!containsGuarantee && [guarantee isKindOfClass:NSString.class]) [result addObject:guarantee];
    return result.count ? [result copy] : nil;
}
- (void)httpSisRequestTimeOut:(id)timer {
    JCORESisLog(0x100, 1014, @"httpSisRequestTimeOut");
    JCOREHttpSisCompletion *pending = [timer userInfo];
    if (pending) {
        id addresses = [JCOREBusinessStorage getReportAddress];
        if (addresses && [addresses isKindOfClass:NSArray.class]) pending.completion(addresses);
        [self.httpSisCompletionArray removeObject:pending];
    }
}
- (id)stringFromState:(int)state {
    switch (state) {
        case 1: return JCOREAddressAnalysisType_sis_lastSuccess;
        case 2: return JCOREAddressAnalysisType_sis_srvRecord;
        case 3: return JCOREAddressAnalysisType_sis_lastResult;
        case 4: return JCOREAddressAnalysisType_sis_defaultSisIp;
        default: return JCOREAddressAnalysisType_sis_defaultDomain;
    }
}
@end
#pragma clang diagnostic pop
