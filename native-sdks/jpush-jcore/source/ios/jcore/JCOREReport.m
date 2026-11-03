#import "JCOREReport.h"
#import "JCOREDataSecurity.h"
#import "JCOREUtilities.h"
#import "JCOREClientInfo.h"
#import "JCOREIntegrate.h"
#import "JCORENetUtilities.h"
#import "JCORELog.h"

@interface JCOREReport () {
    NSMutableURLRequest *_request;
    NSData *_packetData;
    id _requestInfo;
    NSString *_reportFlag;
    unsigned char _controlFlag;
    BOOL _accountIDRequired;
    BOOL _cacheRequired;
    unsigned int _seed;
    RecoveryOpaqueCallback _completionHandler;
    NSArray *_addressList;
    NSArray *_types;
    NSString *_aesKey;
}
@end
#define JCOREReportFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Http Event/JCOREReport.m"
#pragma clang diagnostic push
// Original getters ignore these setter-backed scalar slots.
#pragma clang diagnostic ignored "-Wunused-property-ivar"
@implementation JCOREReport
@dynamic hash, superclass, description, debugDescription;
@synthesize requestInfo = _requestInfo, completionHandler = _completionHandler, controlFlag = _controlFlag;
@synthesize accountIDRequired = _accountIDRequired, cacheRequired = _cacheRequired, addressList = _addressList;
@synthesize types = _types, seed = _seed, aesKey = _aesKey;
- (BOOL)accountIDRequired { return YES; }
- (BOOL)cacheRequired { return YES; }
- (instancetype)init {
    self = [super init];
    if (self) _aesKey = [JCOREDataSecurity encryptKeyForHttp:&_seed];
    return self;
}
- (void)setRequest:(NSMutableURLRequest *)request { _request = request; }
- (NSMutableURLRequest *)request { return _request ?: [self generateUrlRequest]; }
- (void)setRequestInfo:(id)information {
    _requestInfo = information;
    NSMutableArray *types = [[NSMutableArray alloc] init];
    if ([information isKindOfClass:NSDictionary.class]) {
        id content = information[@"content"];
        if ([content isKindOfClass:NSArray.class]) {
            for (id item in content) { NSString *type = [self parseTypeFromContent:item]; if (type) [types addObject:type]; }
        } else { NSString *type = [self parseTypeFromContent:information]; if (type) [types addObject:type]; }
    } else if ([information isKindOfClass:NSArray.class]) {
        for (id item in information) { NSString *type = [self parseTypeFromContent:item]; if (type) [types addObject:type]; }
    }
    _types = [types copy];
}
- (NSString *)parseTypeFromContent:(id)content {
    if (![content isKindOfClass:NSDictionary.class]) return nil;
    NSString *type = content[@"type"];
    if ([type isEqualToString:@"active_launch"] || [type isEqualToString:@"active_terminate"]) type = @"active_user";
    return type;
}
- (void)setReportFlag:(NSString *)flag { _reportFlag = flag; }
- (NSString *)reportFlag {
    if (!_reportFlag) {
        double timestamp = [JCOREUtilities currentTimestamp];
        int rid = [JCORENetUtilities generateRid];
        _reportFlag = [NSString stringWithFormat:@"%d:%llu", rid, (unsigned long long)timestamp];
    }
    return _reportFlag;
}
- (BOOL)isCancelReport { return [JCOREClientInfo uid] == 0; }
- (NSDictionary *)peripheralData {
    NSMutableDictionary *information = [[NSMutableDictionary alloc] init];
    information[@"platform"] = @"i";
    information[@"uid"] = @((unsigned long long)[JCOREClientInfo uid]);
    information[@"app_key"] = [JCOREClientInfo appKey] ?: @"";
    information[@"channel"] = [JCOREClientInfo channel] ?: @"";
    information[@"app_version"] = [JCOREUtilities appVersion];
    [information addEntriesFromDictionary:[JCOREIntegrate integrateInfo]];
    return [information copy];
}
- (NSData *)packetData {
    if (_packetData) return _packetData;
    NSMutableDictionary *packet = [[NSMutableDictionary alloc] init];
    if ([_requestInfo isKindOfClass:NSDictionary.class] && _requestInfo[@"content"]) {
        id content = _requestInfo[@"content"];
        if ([content isKindOfClass:NSDictionary.class] || [content isKindOfClass:NSArray.class]) {
            [packet addEntriesFromDictionary:[self peripheralData]];
            [packet setObject:[content isKindOfClass:NSArray.class] ? content : @[content] forKey:@"content"];
        }
    } else {
        NSMutableArray *content = [[NSMutableArray alloc] init];
        NSArray *items = nil;
        if ([_requestInfo isKindOfClass:NSDictionary.class]) items = @[_requestInfo];
        else if ([_requestInfo isKindOfClass:NSArray.class]) items = _requestInfo;
        for (id item in items) {
            NSMutableDictionary *entry = [[NSMutableDictionary alloc] initWithDictionary:item];
            if (self.accountIDRequired && [[JCOREClientInfo accountID] length] && [item count]) [entry setObject:[JCOREClientInfo accountID] forKey:@"account_id"];
            [content addObject:[entry copy]];
        }
        [packet addEntriesFromDictionary:[self peripheralData]];
        [packet setObject:[content copy] forKey:@"content"];
    }
    _requestInfo = [packet copy];
    NSString *rendered = [NSString stringWithFormat:@"即将上报数据包内容:%@", packet];
    [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCOREReportFile function:__PRETTY_FUNCTION__ line:182 tag:nil format:@"%@", rendered];
    NSData *data = [JCOREUtilities gzip:[JCOREUtilities jsonToData:packet]];
    NSData *encrypted = [JCOREDataSecurity aes256CBCEncryptData:data withKey:_aesKey];
    if (!encrypted) {
        [JCORELog log:YES level:0x3ff flag:2 context:0 file:JCOREReportFile function:__PRETTY_FUNCTION__ line:188 tag:nil format:@"report canceled, body data nil"];
        return nil;
    }
    long long uid = [packet[@"uid"] longLongValue];
    NSData *result = [encrypted copy];
    if (uid) { _packetData = result; return _packetData; }
    return result;
}
- (NSMutableURLRequest *)generateUrlRequest {
    NSData *data = self.packetData;
    if (!data) return nil;
    NSString *authorization = [self authorizationString:data];
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    [request setValue:[JCOREClientInfo appKey] forHTTPHeaderField:@"X-App-Key"];
    request.HTTPBody = data;
    [request setValue:authorization forHTTPHeaderField:@"Authorization"];
    if (!_request) {
        request.HTTPMethod = @"POST";
        [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
        request.timeoutInterval = 30;
    } else {
        request.HTTPMethod = _request.HTTPMethod;
        [request setValue:_request.allHTTPHeaderFields[@"Content-Type"] forHTTPHeaderField:@"Content-Type"];
        request.timeoutInterval = _request.timeoutInterval;
    }
    return request;
}
- (NSString *)authorizationString:(NSData *)data {
    unsigned long long uid = [JCOREClientInfo uid];
    NSString *password = [JCOREClientInfo password];
    if (!uid || !password.length) [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:JCOREReportFile function:__PRETTY_FUNCTION__ line:236 tag:nil format:@"上报时 pwd或者uid非法"];
    NSString *md5 = [JCOREUtilities stringByMD5:password];
    NSString *bodyHash = [JCOREDataSecurity sha1:data];
    NSString *digest = [JCOREDataSecurity sha1:[NSString stringWithFormat:@"%llu%@%@", uid, md5, bodyHash]];
    NSString *token = [NSString stringWithFormat:@"%llu:%@:%@", uid, digest, self.aesKey];
    return [NSString stringWithFormat:@"Basic %@", [JCOREDataSecurity base64:[token dataUsingEncoding:NSUTF8StringEncoding]]];
}
@end
#pragma clang diagnostic pop
