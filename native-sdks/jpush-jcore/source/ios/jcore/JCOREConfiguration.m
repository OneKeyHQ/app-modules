#import "JCOREConfiguration.h"
#import "JCORECrypto.h"
#if __has_include(<UIKit/UIKit.h>)
#import <UIKit/UIKit.h>
#endif

static NSString *const kJCoreKeyUid = @"com.onekey.jcore.uid";
static NSString *const kJCoreKeyPassword = @"com.onekey.jcore.password";
static NSString *const kJCoreKeyRegistrationId = @"com.onekey.jcore.registration_id";
static NSString *const kJCoreKeyDeviceId = @"com.onekey.jcore.device_id";
static NSString *const kJCoreKeyFakeImei = @"com.onekey.jcore.fake_imei";
static NSString *const kJCoreKeyDeviceToken = @"com.onekey.jcore.device_token";
static NSString *const kJCoreKeyVoipToken = @"com.onekey.jcore.voip_token";
static NSString *const kJCoreKeyAuth = @"com.onekey.jcore.collection_auth";
static NSString *const kJCoreKeyPushStopped = @"com.onekey.jcore.push_stopped";
static NSString *const kJCoreKeyTags = @"com.onekey.jcore.tags";
static NSString *const kJCoreKeyAlias = @"com.onekey.jcore.alias";
static NSString *const kJCoreKeyProperties = @"com.onekey.jcore.properties";
static NSString *const kJCoreKeyMobileNumber = @"com.onekey.jcore.mobile_number";
static NSString *const kJCoreKeyDeviceSeed = @"com.onekey.jcore.device_seed";

static NSString *const kJCoreKeySisIps = @"com.onekey.jcore.sis.ips";
static NSString *const kJCoreKeySisSslIps = @"com.onekey.jcore.sis.ssl_ips";
static NSString *const kJCoreKeySisServerIps = @"com.onekey.jcore.sis.sis_ips";
static NSString *const kJCoreKeySisHttpReport = @"com.onekey.jcore.sis.http_report";
static NSString *const kJCoreKeySisHttpsReport = @"com.onekey.jcore.sis.https_report";
static NSString *const kJCoreKeySisUser = @"com.onekey.jcore.sis.user";
static NSString *const kJCoreKeySisSslUser = @"com.onekey.jcore.sis.ssl_user";
static NSString *const kJCoreKeySisTcpReport = @"com.onekey.jcore.sis.tcp_report";
static NSString *const kJCoreKeySisCertVer = @"com.onekey.jcore.sis.cert_ver";
static NSString *const kJCoreKeySisDataReport = @"com.onekey.jcore.sis.data_report";
static NSString *const kJCoreKeySisLastSuccess = @"com.onekey.jcore.sis.last_success_ips";
static NSString *const kJCoreKeySisFailedIps = @"com.onekey.jcore.sis.failed_ips";

@implementation JGInforCollectionAuthItems
@end

@implementation JGInforCollectionAuth

+ (void)JCollectionAuth:(void (^)(JGInforCollectionAuthItems *authInfo))authBlock {
    JGInforCollectionAuthItems *items = [[JGInforCollectionAuthItems alloc] init];
    items.isAuth = [JCOREConfiguration shared].collectionAuthorized;
    if (authBlock != nil) {
        authBlock(items);
    }
    [JCOREConfiguration shared].collectionAuthorized = items.isAuth;
}

@end

@implementation JGCOREAPI

+ (void)ci:(BOOL)enable {
    [JCOREConfiguration shared].collectionAuthorized = enable;
}

+ (void)printLog:(BOOL)enable {
    [JCOREConfiguration shared].debugEnabled = enable;
    [JCOREConfiguration shared].logOff = !enable;
}

+ (void)preferIPv6:(BOOL)preferIPv6 {
    [JCOREConfiguration shared].preferIPv6 = preferIPv6;
}

@end

@interface JCOREConfiguration () {
    dispatch_queue_t _stateQueue;
    uint16_t _requestCounter;
    NSArray<NSString *> *_ips;
    NSArray<NSString *> *_sslIps;
    NSArray<NSString *> *_sisIps;
    NSArray<NSString *> *_httpReportAddresses;
    NSArray<NSString *> *_httpsReportAddresses;
    NSArray<NSString *> *_userAddresses;
    NSArray<NSString *> *_sslUserAddresses;
    NSDictionary<NSString *, NSArray<NSString *> *> *_tcpReportAddresses;
    NSInteger _certVer;
    BOOL _dataReport;
    NSMutableArray<NSString *> *_lastSuccessSisIpsMutable;
    NSMutableArray<NSString *> *_failedIpsMutable;
}
@end

@implementation JCOREConfiguration

+ (instancetype)shared {
    static JCOREConfiguration *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[JCOREConfiguration alloc] init];
    });
    return instance;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        _stateQueue = dispatch_queue_create("cn.jiguang.jcore.config", DISPATCH_QUEUE_SERIAL);
        _requestCounter = (uint16_t)(arc4random_uniform(30000) + 100);
        _channel = @"App Store";
        _isProduction = YES;
        _buildType = 1;
        _apsType = 1;
        _encryptionAlgorithm = 1;
        _heartbeatInterval = 290.0;
        _reconnectBaseInterval = 3.0;
        _requestTimeoutInterval = 20.0;
        _requestResendInterval = 10.0;
        _connectTimeoutInterval = 5.0;
        _sisHostDomains = @[ @"s.jpush.cn", @"sis.jpush.io", @"easytomessage.com", @"sis-ipv6.jpush.cn" ];
        _sisDefaultIps = @[ @"115.120.193.179", @"113.45.40.226", @"120.46.16.242", @"120.46.84.108" ];
        _httpsSisUrls = @[ @"https://tsis.jpush.cn", @"https://tsis-ipv6.jpush.cn" ];
        _defaultConnectHost = @"im64.jpush.cn";
        _defaultConnectPorts = @[ @7000, @7002, @7003, @7004, @7005, @7006, @7007, @7008, @7009 ];

        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSNumber *savedAuth = [defaults objectForKey:kJCoreKeyAuth];
        _collectionAuthorized = savedAuth != nil ? savedAuth.boolValue : YES;
        _pushStopped = [defaults boolForKey:kJCoreKeyPushStopped];
        _uid = (uint64_t)[defaults integerForKey:kJCoreKeyUid];
        _password = [defaults stringForKey:kJCoreKeyPassword];
        _registrationID = [defaults stringForKey:kJCoreKeyRegistrationId];
        _deviceID = [defaults stringForKey:kJCoreKeyDeviceId];
        _deviceTokenHex = [defaults stringForKey:kJCoreKeyDeviceToken];
        _voipTokenHex = [defaults stringForKey:kJCoreKeyVoipToken];
        _isNewDevice = (_uid == 0 || _registrationID.length == 0);

        NSInteger encType = [defaults integerForKey:@"kJIGUANGEncryptType"];
        if (encType == 1) {
            _encryptionAlgorithm = 2;
        }

        _ips = [defaults stringArrayForKey:kJCoreKeySisIps] ?: @[];
        _sslIps = [defaults stringArrayForKey:kJCoreKeySisSslIps] ?: @[];
        _sisIps = [defaults stringArrayForKey:kJCoreKeySisServerIps] ?: @[];
        _httpReportAddresses = [defaults stringArrayForKey:kJCoreKeySisHttpReport] ?: @[];
        _httpsReportAddresses = [defaults stringArrayForKey:kJCoreKeySisHttpsReport] ?: @[];
        _userAddresses = [defaults stringArrayForKey:kJCoreKeySisUser] ?: @[];
        _sslUserAddresses = [defaults stringArrayForKey:kJCoreKeySisSslUser] ?: @[];
        _tcpReportAddresses = [defaults dictionaryForKey:kJCoreKeySisTcpReport] ?: @{};
        NSInteger savedCertVer = [defaults integerForKey:kJCoreKeySisCertVer];
        _certVer = savedCertVer > 0 ? savedCertVer : 1;
        _dataReport = [defaults boolForKey:kJCoreKeySisDataReport];
        _lastSuccessSisIpsMutable = [NSMutableArray arrayWithArray:([defaults stringArrayForKey:kJCoreKeySisLastSuccess] ?: @[])];
        _failedIpsMutable = [NSMutableArray arrayWithArray:([defaults stringArrayForKey:kJCoreKeySisFailedIps] ?: @[])];
    }
    return self;
}

- (void)configureWithAppKey:(NSString *)appKey
                    channel:(NSString *)channel
               isProduction:(BOOL)isProduction
      advertisingIdentifier:(NSString *)advertisingId {
    dispatch_sync(_stateQueue, ^{
        self->_appKey = [appKey copy];
        if (channel.length > 0) {
            self->_channel = [channel copy];
        }
        self->_isProduction = isProduction;
        self->_buildType = isProduction ? 1 : 0;
        self->_apsType = isProduction ? 1 : 0;
        self->_advertisingIdentifier = [advertisingId copy];
    });
}

- (void)setCustomConnectHost:(NSString *)host
                        port:(uint16_t)port
                      useSSL:(BOOL)useSSL {
    dispatch_sync(_stateQueue, ^{
        if (host.length > 0 && port > 0) {
            self->_customConnectHost = [host copy];
            self->_customConnectPort = port;
            self->_customConnectUseSSL = useSSL;
        } else {
            self->_customConnectHost = nil;
            self->_customConnectPort = 0;
            self->_customConnectUseSSL = NO;
        }
    });
}

- (void)clearCustomConnectEndpoint {
    [self setCustomConnectHost:nil port:0 useSSL:NO];
    self.customConnectAddresses = nil;
}

- (void)setCollectionAuthorized:(BOOL)collectionAuthorized {
    _collectionAuthorized = collectionAuthorized;
    [[NSUserDefaults standardUserDefaults] setBool:collectionAuthorized forKey:kJCoreKeyAuth];
}

- (void)setPushStopped:(BOOL)pushStopped {
    _pushStopped = pushStopped;
    [[NSUserDefaults standardUserDefaults] setBool:pushStopped forKey:kJCoreKeyPushStopped];
}

- (void)setDeviceTokenHex:(NSString *)deviceTokenHex {
    _deviceTokenHex = [deviceTokenHex copy];
    if (deviceTokenHex.length > 0) {
        [[NSUserDefaults standardUserDefaults] setObject:deviceTokenHex forKey:kJCoreKeyDeviceToken];
    } else {
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:kJCoreKeyDeviceToken];
    }
}

- (void)setVoipTokenHex:(NSString *)voipTokenHex {
    _voipTokenHex = [voipTokenHex copy];
    if (voipTokenHex.length > 0) {
        [[NSUserDefaults standardUserDefaults] setObject:voipTokenHex forKey:kJCoreKeyVoipToken];
    } else {
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:kJCoreKeyVoipToken];
    }
}

- (void)updateRegistrationWithUid:(uint64_t)uid
                         password:(NSString *)password
                   registrationID:(NSString *)registrationID {
    [self updateRegistrationWithUid:uid password:password registrationID:registrationID deviceID:nil];
}

- (void)updateRegistrationWithUid:(uint64_t)uid
                         password:(NSString *)password
                   registrationID:(NSString *)registrationID
                         deviceID:(NSString *)deviceID {
    dispatch_sync(_stateQueue, ^{
        self->_uid = uid;
        self->_password = password.length > 0 ? [password copy] : nil;
        self->_registrationID = registrationID.length > 0 ? [registrationID copy] : nil;
        if (deviceID.length > 0) {
            self->_deviceID = [deviceID copy];
        }
        self->_isNewDevice = (uid == 0 || registrationID.length == 0);

        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        if (uid > 0) {
            [defaults setInteger:(NSInteger)uid forKey:kJCoreKeyUid];
        } else {
            [defaults removeObjectForKey:kJCoreKeyUid];
        }
        if (password.length > 0) {
            [defaults setObject:password forKey:kJCoreKeyPassword];
        } else {
            [defaults removeObjectForKey:kJCoreKeyPassword];
        }
        if (registrationID.length > 0) {
            [defaults setObject:registrationID forKey:kJCoreKeyRegistrationId];
        } else {
            [defaults removeObjectForKey:kJCoreKeyRegistrationId];
        }
        if (deviceID.length > 0) {
            [defaults setObject:deviceID forKey:kJCoreKeyDeviceId];
        }
    });
}

- (void)resetRegistration {
    dispatch_sync(_stateQueue, ^{
        self->_uid = 0;
        self->_password = nil;
        self->_registrationID = nil;
        self->_deviceID = nil;
        self->_isNewDevice = YES;
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        [defaults removeObjectForKey:kJCoreKeyUid];
        [defaults removeObjectForKey:kJCoreKeyPassword];
        [defaults removeObjectForKey:kJCoreKeyRegistrationId];
        [defaults removeObjectForKey:kJCoreKeyDeviceId];
        [defaults removeObjectForKey:kJCoreKeyFakeImei];
    });
}

- (void)updateWithSisDictionary:(NSDictionary *)sisDict {
    if (![sisDict isKindOfClass:[NSDictionary class]]) {
        return;
    }
    dispatch_sync(_stateQueue, ^{
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        if ([sisDict[@"ips"] isKindOfClass:[NSArray class]]) {
            self->_ips = [sisDict[@"ips"] copy];
            [defaults setObject:self->_ips forKey:kJCoreKeySisIps];
        }
        if ([sisDict[@"ssl_ips"] isKindOfClass:[NSArray class]]) {
            self->_sslIps = [sisDict[@"ssl_ips"] copy];
            [defaults setObject:self->_sslIps forKey:kJCoreKeySisSslIps];
        }
        if ([sisDict[@"sis_ips"] isKindOfClass:[NSArray class]]) {
            self->_sisIps = [sisDict[@"sis_ips"] copy];
            [defaults setObject:self->_sisIps forKey:kJCoreKeySisServerIps];
        }
        if ([sisDict[@"http_report"] isKindOfClass:[NSArray class]]) {
            self->_httpReportAddresses = [sisDict[@"http_report"] copy];
            [defaults setObject:self->_httpReportAddresses forKey:kJCoreKeySisHttpReport];
        }
        if ([sisDict[@"https_report"] isKindOfClass:[NSArray class]]) {
            self->_httpsReportAddresses = [sisDict[@"https_report"] copy];
            [defaults setObject:self->_httpsReportAddresses forKey:kJCoreKeySisHttpsReport];
        }
        if ([sisDict[@"user"] isKindOfClass:[NSArray class]]) {
            self->_userAddresses = [sisDict[@"user"] copy];
            [defaults setObject:self->_userAddresses forKey:kJCoreKeySisUser];
        }
        if ([sisDict[@"ssl_user"] isKindOfClass:[NSArray class]]) {
            self->_sslUserAddresses = [sisDict[@"ssl_user"] copy];
            [defaults setObject:self->_sslUserAddresses forKey:kJCoreKeySisSslUser];
        }
        if ([sisDict[@"tcp_report"] isKindOfClass:[NSDictionary class]]) {
            self->_tcpReportAddresses = [sisDict[@"tcp_report"] copy];
            [defaults setObject:self->_tcpReportAddresses forKey:kJCoreKeySisTcpReport];
        }
        if ([sisDict[@"cert_ver"] respondsToSelector:@selector(integerValue)]) {
            self->_certVer = [sisDict[@"cert_ver"] integerValue];
            [defaults setInteger:self->_certVer forKey:kJCoreKeySisCertVer];
        }
        if (sisDict[@"data_report"] != nil && [sisDict[@"data_report"] respondsToSelector:@selector(boolValue)]) {
            self->_dataReport = [sisDict[@"data_report"] boolValue];
            [defaults setBool:self->_dataReport forKey:kJCoreKeySisDataReport];
        }
    });
}

- (NSArray<NSString *> *)ips {
    __block NSArray<NSString *> *res = nil;
    dispatch_sync(_stateQueue, ^{ res = [self->_ips copy] ?: @[]; });
    return res;
}

- (NSArray<NSString *> *)sslIps {
    __block NSArray<NSString *> *res = nil;
    dispatch_sync(_stateQueue, ^{ res = [self->_sslIps copy] ?: @[]; });
    return res;
}

- (NSArray<NSString *> *)sisIps {
    __block NSArray<NSString *> *res = nil;
    dispatch_sync(_stateQueue, ^{ res = [self->_sisIps copy] ?: @[]; });
    return res;
}

- (NSArray<NSString *> *)httpReportAddresses {
    __block NSArray<NSString *> *res = nil;
    dispatch_sync(_stateQueue, ^{ res = [self->_httpReportAddresses copy] ?: @[]; });
    return res;
}

- (NSArray<NSString *> *)httpsReportAddresses {
    __block NSArray<NSString *> *res = nil;
    dispatch_sync(_stateQueue, ^{ res = [self->_httpsReportAddresses copy] ?: @[]; });
    return res;
}

- (NSArray<NSString *> *)userAddresses {
    __block NSArray<NSString *> *res = nil;
    dispatch_sync(_stateQueue, ^{ res = [self->_userAddresses copy] ?: @[]; });
    return res;
}

- (NSArray<NSString *> *)sslUserAddresses {
    __block NSArray<NSString *> *res = nil;
    dispatch_sync(_stateQueue, ^{ res = [self->_sslUserAddresses copy] ?: @[]; });
    return res;
}

- (NSDictionary<NSString *,NSArray<NSString *> *> *)tcpReportAddresses {
    __block NSDictionary *res = nil;
    dispatch_sync(_stateQueue, ^{ res = [self->_tcpReportAddresses copy] ?: @{}; });
    return res;
}

- (NSInteger)certVer {
    __block NSInteger res = 1;
    dispatch_sync(_stateQueue, ^{ res = self->_certVer; });
    return res;
}

- (BOOL)dataReport {
    __block BOOL res = NO;
    dispatch_sync(_stateQueue, ^{ res = self->_dataReport; });
    return res;
}

- (NSArray<NSString *> *)lastSuccessSisIps {
    __block NSArray<NSString *> *res = nil;
    dispatch_sync(_stateQueue, ^{ res = [self->_lastSuccessSisIpsMutable copy] ?: @[]; });
    return res;
}

- (NSArray<NSString *> *)failedIps {
    __block NSArray<NSString *> *res = nil;
    dispatch_sync(_stateQueue, ^{ res = [self->_failedIpsMutable copy] ?: @[]; });
    return res;
}

- (void)addFailedIp:(NSString *)ipPort {
    if (ipPort.length == 0) {
        return;
    }
    dispatch_sync(_stateQueue, ^{
        if (![self->_failedIpsMutable containsObject:ipPort]) {
            [self->_failedIpsMutable addObject:ipPort];
            [[NSUserDefaults standardUserDefaults] setObject:[self->_failedIpsMutable copy] forKey:kJCoreKeySisFailedIps];
        }
    });
}

- (void)clearFailedIps {
    dispatch_sync(_stateQueue, ^{
        [self->_failedIpsMutable removeAllObjects];
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:kJCoreKeySisFailedIps];
    });
}

- (void)addLastSuccessSisIp:(NSString *)ipPort {
    if (ipPort.length == 0) {
        return;
    }
    dispatch_sync(_stateQueue, ^{
        if (![self->_lastSuccessSisIpsMutable containsObject:ipPort]) {
            [self->_lastSuccessSisIpsMutable addObject:ipPort];
            [[NSUserDefaults standardUserDefaults] setObject:[self->_lastSuccessSisIpsMutable copy] forKey:kJCoreKeySisLastSuccess];
        }
    });
}

- (void)resetSisCache {
    dispatch_sync(_stateQueue, ^{
        self->_ips = @[];
        self->_sslIps = @[];
        self->_sisIps = @[];
        self->_httpReportAddresses = @[];
        self->_httpsReportAddresses = @[];
        self->_userAddresses = @[];
        self->_sslUserAddresses = @[];
        self->_tcpReportAddresses = @{};
        [self->_lastSuccessSisIpsMutable removeAllObjects];
        [self->_failedIpsMutable removeAllObjects];

        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        [defaults removeObjectForKey:kJCoreKeySisIps];
        [defaults removeObjectForKey:kJCoreKeySisSslIps];
        [defaults removeObjectForKey:kJCoreKeySisServerIps];
        [defaults removeObjectForKey:kJCoreKeySisHttpReport];
        [defaults removeObjectForKey:kJCoreKeySisHttpsReport];
        [defaults removeObjectForKey:kJCoreKeySisUser];
        [defaults removeObjectForKey:kJCoreKeySisSslUser];
        [defaults removeObjectForKey:kJCoreKeySisTcpReport];
        [defaults removeObjectForKey:kJCoreKeySisLastSuccess];
        [defaults removeObjectForKey:kJCoreKeySisFailedIps];
    });
}

- (NSString *)deviceSeedLocked {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *seed = [defaults stringForKey:kJCoreKeyDeviceSeed];
    if (seed.length == 0) {
#if __has_include(<UIKit/UIKit.h>)
        NSString *idfv = [UIDevice currentDevice].identifierForVendor.UUIDString;
        seed = idfv.length > 0 ? idfv : [NSUUID UUID].UUIDString;
#else
        seed = [NSUUID UUID].UUIDString;
#endif
        [defaults setObject:seed forKey:kJCoreKeyDeviceSeed];
    }
    return seed;
}

- (NSString *)fakeIMEI {
    __block NSString *imei = nil;
    dispatch_sync(_stateQueue, ^{
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        imei = [defaults stringForKey:kJCoreKeyFakeImei];
        if (imei.length == 0) {
            NSString *seed = [self deviceSeedLocked];
            imei = [JCORECrypto md5Hex:[NSString stringWithFormat:@"imei:%@", seed]];
            [defaults setObject:imei forKey:kJCoreKeyFakeImei];
        }
    });
    return imei ?: @"00000000000000000000000000000000";
}

- (NSString *)resolveDeviceIDWithOutHasExisting:(BOOL *)outHasExisting {
    __block NSString *devId = nil;
    __block BOOL existed = NO;
    dispatch_sync(_stateQueue, ^{
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *saved = self->_deviceID ?: [defaults stringForKey:kJCoreKeyDeviceId];
        if (saved.length > 0) {
            existed = YES;
            devId = saved;
        } else {
            existed = NO;
            NSString *seed = [self deviceSeedLocked];
            devId = [JCORECrypto md5Hex:[NSString stringWithFormat:@"dev:%@:%@", self->_appKey ?: @"jcore", seed]];
            self->_deviceID = [devId copy];
            [defaults setObject:devId forKey:kJCoreKeyDeviceId];
        }
    });
    if (outHasExisting != NULL) {
        *outHasExisting = existed;
    }
    return devId ?: @"";
}

- (NSString *)bundleIdentifierString {
    NSString *bundleId = [NSBundle mainBundle].bundleIdentifier;
    return bundleId.length > 0 ? bundleId : @"so.onekey.app.wallet";
}

- (NSString *)appVersionString {
    NSDictionary *info = [NSBundle mainBundle].infoDictionary;
    NSString *ver = info[@"CFBundleShortVersionString"] ?: info[@"CFBundleVersion"];
    return ver.length > 0 ? ver : @"1.0.0";
}

- (NSString *)osVersionString {
#if __has_include(<UIKit/UIKit.h>)
    NSString *ver = [UIDevice currentDevice].systemVersion;
    return ver.length > 0 ? ver : @"17.0";
#else
    return @"17.0";
#endif
}

- (NSString *)modelNameString {
#if __has_include(<UIKit/UIKit.h>)
    NSString *model = [UIDevice currentDevice].model;
    return model.length > 0 ? model : @"iPhone";
#else
    return @"iPhone";
#endif
}

- (NSString *)fallbackRegistrationID {
    __block NSString *result = nil;
    dispatch_sync(_stateQueue, ^{
        if (self->_registrationID.length > 0) {
            result = self->_registrationID;
            return;
        }
        NSString *seed = [self deviceSeedLocked];
        NSString *hash = [JCORECrypto md5Hex:[NSString stringWithFormat:@"%@:%@", self->_appKey ?: @"jpush", seed]];
        result = [@"1a0018970a" stringByAppendingString:[hash substringToIndex:MIN((NSUInteger)10, hash.length)]];
    });
    return result ?: @"";
}

- (void)ensureLocalRegistrationFallbackIfNeeded {
    dispatch_sync(_stateQueue, ^{
        if (self->_registrationID.length > 0 && self->_uid > 0) {
            return;
        }
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *seed = [self deviceSeedLocked];
        NSString *hash = [JCORECrypto md5Hex:[NSString stringWithFormat:@"%@:%@", self->_appKey ?: @"jpush", seed]];
        if (self->_uid == 0) {
            uint64_t derivedUid = 0;
            for (NSUInteger i = 0; i < MIN((NSUInteger)12, hash.length); i++) {
                unichar c = [hash characterAtIndex:i];
                uint8_t nibble = (c >= '0' && c <= '9') ? (c - '0') : (10 + (tolower(c) - 'a'));
                derivedUid = (derivedUid << 4) | (nibble & 0x0F);
            }
            self->_uid = (derivedUid & 0x7FFFFFFFFFFFFFFFULL) ?: 10000001ULL;
            [defaults setInteger:(NSInteger)self->_uid forKey:kJCoreKeyUid];
        }
        if (self->_password.length == 0) {
            self->_password = [hash substringToIndex:MIN((NSUInteger)16, hash.length)];
            [defaults setObject:self->_password forKey:kJCoreKeyPassword];
        }
        if (self->_registrationID.length == 0) {
            self->_registrationID = [@"1a0018970a" stringByAppendingString:[hash substringToIndex:MIN((NSUInteger)10, hash.length)]];
            [defaults setObject:self->_registrationID forKey:kJCoreKeyRegistrationId];
        }
    });
}

- (NSSet<NSString *> *)storedTags {
    NSArray<NSString *> *array = [[NSUserDefaults standardUserDefaults] stringArrayForKey:kJCoreKeyTags];
    return array != nil ? [NSSet setWithArray:array] : [NSSet set];
}

- (void)saveStoredTags:(NSSet<NSString *> *)tags {
    NSArray<NSString *> *allObjects = tags.allObjects ?: @[];
    [[NSUserDefaults standardUserDefaults] setObject:allObjects forKey:kJCoreKeyTags];
}

- (NSString *)storedAlias {
    return [[NSUserDefaults standardUserDefaults] stringForKey:kJCoreKeyAlias];
}

- (void)saveStoredAlias:(NSString *)alias {
    if (alias.length > 0) {
        [[NSUserDefaults standardUserDefaults] setObject:alias forKey:kJCoreKeyAlias];
    } else {
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:kJCoreKeyAlias];
    }
}

- (NSDictionary<NSString *,id> *)storedProperties {
    NSDictionary *dict = [[NSUserDefaults standardUserDefaults] dictionaryForKey:kJCoreKeyProperties];
    return dict ?: @{};
}

- (void)saveStoredProperties:(NSDictionary<NSString *,id> *)properties {
    if (properties.count > 0) {
        [[NSUserDefaults standardUserDefaults] setObject:properties forKey:kJCoreKeyProperties];
    } else {
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:kJCoreKeyProperties];
    }
}

- (NSString *)storedMobileNumber {
    return [[NSUserDefaults standardUserDefaults] stringForKey:kJCoreKeyMobileNumber];
}

- (void)saveStoredMobileNumber:(NSString *)mobileNumber {
    if (mobileNumber.length > 0) {
        [[NSUserDefaults standardUserDefaults] setObject:mobileNumber forKey:kJCoreKeyMobileNumber];
    } else {
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:kJCoreKeyMobileNumber];
    }
}

- (uint16_t)nextRequestId {
    __block uint16_t rid = 1;
    dispatch_sync(_stateQueue, ^{
        self->_requestCounter += 1;
        if (self->_requestCounter == 0 || self->_requestCounter == UINT16_MAX) {
            self->_requestCounter = 1;
        }
        rid = self->_requestCounter;
    });
    return rid;
}

- (void)log:(NSString *)format, ... {
    if (self.logOff || !self.debugEnabled || format == nil) {
        return;
    }
    va_list args;
    va_start(args, format);
    NSString *message = [[NSString alloc] initWithFormat:format arguments:args];
    va_end(args);
    NSLog(@"[JIGUANG] %@", message);
}

@end

@implementation JCOREPersistence
@end
