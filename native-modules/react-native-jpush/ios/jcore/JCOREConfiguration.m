#import "JCOREConfiguration.h"
#import "JCORECrypto.h"
#import <objc/message.h>
#include <errno.h>
#if __has_include(<UIKit/UIKit.h>)
#import <UIKit/UIKit.h>
#endif

NSString *const JCOREPolicyDidChangeNotification = @"JCOREPolicyDidChangeNotification";

static NSString *const kJCoreKeyUid = @"com.onekey.jcore.uid";
static NSString *const kJCoreKeyPassword = @"com.onekey.jcore.password";
static NSString *const kJCoreKeyRegistrationId = @"com.onekey.jcore.registration_id";
static NSString *const kJCoreKeyDeviceId = @"com.onekey.jcore.device_id";
static NSString *const kJCoreKeyRegistrations = @"com.onekey.jcore.registrations_by_app_key";
static NSString *const kJCoreKeyLastConfiguredAppKey = @"com.onekey.jcore.last_configured_app_key";
static NSString *const kJCoreLegacyRegistrationRetired = @"legacyRegistrationRetired";
static NSString *const kJCoreKeyFakeImei = @"com.onekey.jcore.fake_imei";
static NSString *const kJCoreKeyDeviceToken = @"com.onekey.jcore.device_token";
static NSString *const kJCoreKeyVoipToken = @"com.onekey.jcore.voip_token";
static NSString *const kJCoreKeyAuth = @"com.onekey.jcore.collection_auth";
static NSString *const kJCoreKeyPushStopped = @"com.onekey.jcore.push_stopped";
static NSString *const kJCoreKeyPendingResumeAppKey = @"com.onekey.jcore.pending_push_resume_app_key";
static NSString *const kJCoreKeyTags = @"com.onekey.jcore.tags";
static NSString *const kJCoreKeyAlias = @"com.onekey.jcore.alias";
static NSString *const kJCoreKeyProperties = @"com.onekey.jcore.properties";
static NSString *const kJCoreKeyMobileNumber = @"com.onekey.jcore.mobile_number";
static NSString *const kJCoreKeyDeviceSeed = @"com.onekey.jcore.device_seed";

static NSString *const kJCoreKeySisIps = @"com.onekey.jcore.sis.ips";
static NSString *const kJCoreKeySisSslIps = @"com.onekey.jcore.sis.ssl_ips";
static NSString *const kJCoreKeySisCustomSslIps = @"com.onekey.jcore.sis.custom_ssl_ips";
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

static NSString *JCoreNonEmptyString(id value) {
    return [value isKindOfClass:NSString.class] && [value length] > 0 ? value : nil;
}

static NSDictionary *JCoreStoredRegistration(NSDictionary *registrations, NSString *appKey) {
    id registration = appKey.length > 0 ? registrations[appKey] : nil;
    return [registration isKindOfClass:NSDictionary.class] ? registration : nil;
}

static NSString *JCorePreSetupRegistrationID(NSUserDefaults *defaults) {
    NSDictionary *registrations = [defaults dictionaryForKey:kJCoreKeyRegistrations];
    NSString *lastAppKey = [defaults stringForKey:kJCoreKeyLastConfiguredAppKey];
    if (lastAppKey.length > 0) {
        return JCoreNonEmptyString(JCoreStoredRegistration(registrations, lastAppKey)[@"registrationID"]);
    }
    return registrations == nil ? [defaults stringForKey:kJCoreKeyRegistrationId] : nil;
}

static NSDictionary *JCoreTokenRecord(id storedToken) {
    if ([storedToken isKindOfClass:NSDictionary.class]) {
        return storedToken;
    }
    NSString *token = JCoreNonEmptyString(storedToken);
    return token != nil ? @{@"hex": token} : nil;
}

static NSDictionary *JCorePersistToken(NSString *key, NSString *token, NSNumber *production) {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    if (token.length == 0) {
        [defaults removeObjectForKey:key];
        return nil;
    }
    NSDictionary *record = production != nil ? @{@"hex": token, @"production": production} : @{@"hex": token};
    [defaults setObject:record forKey:key];
    return record;
}

static uint64_t JCoreRegistrationUID(id value) {
    if ([value isKindOfClass:NSNumber.class] && CFGetTypeID((__bridge CFTypeRef)value) != CFBooleanGetTypeID()) {
        value = [value stringValue];
    }
    NSString *digits = JCoreNonEmptyString(value);
    if (digits.length == 0 || digits.length > 20 ||
        [digits rangeOfCharacterFromSet:[[NSCharacterSet characterSetWithCharactersInString:@"0123456789"] invertedSet]].location != NSNotFound) {
        return 0;
    }
    errno = 0;
    uint64_t uid = strtoull(digits.UTF8String, NULL, 10);
    return errno == ERANGE ? 0 : uid;
}

static NSDictionary *JCoreRegistration(uint64_t uid, NSString *password, NSString *registrationID, NSString *deviceID) {
    if (uid == 0 || password.length == 0 || registrationID.length == 0) {
        return nil;
    }
    return @{@"uid": @(uid), @"password": password, @"registrationID": registrationID, @"deviceID": deviceID ?: @""};
}

static NSDictionary *JCoreReadLegacyRegistration(NSString *appKey) {
    // JCore 5.2.1 uses a lowercase AppKey directory. Restrict this read to the expected hex path.
    if (appKey.length != 24 ||
        [appKey rangeOfCharacterFromSet:[[NSCharacterSet characterSetWithCharactersInString:@"0123456789abcdef"] invertedSet]].location != NSNotFound) {
        return nil;
    }
    NSURL *libraryURL = [[NSFileManager defaultManager] URLsForDirectory:NSLibraryDirectory inDomains:NSUserDomainMask].firstObject;
    NSURL *fileURL = [[libraryURL URLByAppendingPathComponent:appKey isDirectory:YES]
        URLByAppendingPathComponent:@"io.jiguang.plist" isDirectory:NO];
    NSFileHandle *file = [NSFileHandle fileHandleForReadingFromURL:fileURL error:nil];
    if (file == nil) {
        return nil;
    }
    const NSUInteger maxBytes = 1024 * 1024;
    NSData *ciphertext = [file readDataUpToLength:maxBytes + 1 error:nil];
    [file closeFile];
    if (ciphertext.length == 0 || ciphertext.length > maxBytes) {
        return nil;
    }
    // The legacy format is JSON encrypted with AES-128 ECB and PKCS#7 padding.
    // This vendor format constant is used only for decoding the old app-local cache.
    NSData *json = [JCORECrypto aesEcbDecrypt:ciphertext key:@"JPUSH_SECURITY_AES_KEY_ONE_TEMP_" keySize:16];
    if (json.length == 0) {
        return nil;
    }
    id decoded = [NSJSONSerialization JSONObjectWithData:json options:0 error:nil];
    if (![decoded isKindOfClass:NSDictionary.class]) {
        return nil;
    }
    return JCoreRegistration(JCoreRegistrationUID(decoded[@"kUIDKey"]), JCoreNonEmptyString(decoded[@"kPasswordKey"]),
                             JCoreNonEmptyString(decoded[@"kRegistrationId"]), nil);
}

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
    NSUInteger _policyGeneration;
    NSString *_preSetupRegistrationID;
    BOOL _hasAPNsEnvironment;
    NSDictionary *_deviceTokenRecord;
    NSDictionary *_voipTokenRecord;
    NSArray<NSString *> *_ips;
    NSArray<NSString *> *_sslIps;
    BOOL _cachedSslIpsRequireTls;
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

@synthesize collectionAuthorized = _collectionAuthorized;
@synthesize pushStopped = _pushStopped;
@synthesize appKey = _appKey;

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
        _buildType = 2;
        _apsType = 2;
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
        _debugEnabled = YES;
        _logOff = NO;
        _localLogEnabled = YES;
        _forceLocalLogWhenLogOff = NO;

        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        _preSetupRegistrationID = [JCorePreSetupRegistrationID(defaults) copy];
        NSNumber *savedAuth = [defaults objectForKey:kJCoreKeyAuth];
        _collectionAuthorized = savedAuth != nil ? savedAuth.boolValue : YES;
        _pushStopped = [defaults boolForKey:kJCoreKeyPushStopped];
        _deviceTokenRecord = JCoreTokenRecord([defaults objectForKey:kJCoreKeyDeviceToken]);
        _voipTokenRecord = JCoreTokenRecord([defaults objectForKey:kJCoreKeyVoipToken]);
        _isNewDevice = YES;

        NSInteger encType = [defaults integerForKey:@"kJIGUANGEncryptType"];
        if (encType == 1) {
            _encryptionAlgorithm = 2;
        }

        _ips = [defaults stringArrayForKey:kJCoreKeySisIps] ?: @[];
        _sslIps = [defaults stringArrayForKey:kJCoreKeySisSslIps] ?: @[];
        NSNumber *customSis = [defaults objectForKey:kJCoreKeySisCustomSslIps];
        _cachedSslIpsRequireTls = customSis == nil || customSis.boolValue;
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
    __block NSUInteger invalidatedGeneration = 0;
    dispatch_sync(_stateQueue, ^{
        @synchronized (self) {
            if (self->_appKey.length > 0 &&
                (![self->_appKey isEqualToString:appKey] || self->_isProduction != isProduction)) {
                invalidatedGeneration = ++self->_policyGeneration;
            }
            [self setAppKeyLocked:appKey];
            if (channel.length > 0) {
                self->_channel = [channel copy];
            }
            self->_isProduction = isProduction;
            self->_buildType = isProduction ? 2 : 1;
            self->_apsType = isProduction ? 2 : 1;
            self->_advertisingIdentifier = [advertisingId copy];
            if (appKey.length > 0 && !self->_hasAPNsEnvironment) {
                self->_hasAPNsEnvironment = YES;
                if (self->_deviceTokenRecord != nil && self->_deviceTokenRecord[@"production"] == nil) {
                    self->_deviceTokenRecord = JCorePersistToken(kJCoreKeyDeviceToken,
                        JCoreNonEmptyString(self->_deviceTokenRecord[@"hex"]), @(isProduction));
                }
                if (self->_voipTokenRecord != nil && self->_voipTokenRecord[@"production"] == nil) {
                    self->_voipTokenRecord = JCorePersistToken(kJCoreKeyVoipToken,
                        JCoreNonEmptyString(self->_voipTokenRecord[@"hex"]), @(isProduction));
                }
            }
        }
    });
    [self notifyConfigurationInvalidated:invalidatedGeneration];
}

- (NSString *)appKey {
    @synchronized (self) { return _appKey; }
}

- (void)setAppKey:(NSString *)appKey {
    __block NSUInteger invalidatedGeneration = 0;
    dispatch_sync(_stateQueue, ^{
        @synchronized (self) {
            if (self->_appKey.length > 0 && ![self->_appKey isEqualToString:appKey]) {
                invalidatedGeneration = ++self->_policyGeneration;
            }
            [self setAppKeyLocked:appKey];
        }
    });
    [self notifyConfigurationInvalidated:invalidatedGeneration];
}

- (void)notifyConfigurationInvalidated:(NSUInteger)generation {
    if (generation != 0) {
        [[NSNotificationCenter defaultCenter] postNotificationName:JCOREPolicyDidChangeNotification object:self
            userInfo:@{@"generation": @(generation)}];
    }
}

- (void)setAppKeyLocked:(NSString *)appKey {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    id pending = [defaults objectForKey:kJCoreKeyPendingResumeAppKey];
    if ([pending isKindOfClass:NSNumber.class]) {
        if (appKey.length > 0) [defaults setObject:appKey forKey:kJCoreKeyPendingResumeAppKey];
    } else if (pending != nil && ![pending isEqual:appKey]) {
        [defaults removeObjectForKey:kJCoreKeyPendingResumeAppKey];
    }
    NSString *storageKey = appKey.lowercaseString;
    BOOL changed = ![(_appKey.lowercaseString ?: @"") isEqualToString:(storageKey ?: @"")];
    _appKey = [appKey copy];
    _preSetupRegistrationID = nil;
    if (!changed) {
        return;
    }

    NSDictionary *registrations = [defaults dictionaryForKey:kJCoreKeyRegistrations];
    NSDictionary *storedRegistration = JCoreStoredRegistration(registrations, storageKey);
    NSDictionary *registration = storedRegistration;
    if (storageKey.length > 0 && registrations == nil) {
        // The original source SDK stored one tuple with no AppKey. Adopt it once.
        registration = JCoreRegistration(JCoreRegistrationUID([defaults objectForKey:kJCoreKeyUid]),
            [defaults stringForKey:kJCoreKeyPassword], [defaults stringForKey:kJCoreKeyRegistrationId],
            [defaults stringForKey:kJCoreKeyDeviceId]);
    }
    BOOL legacyRetired = [storedRegistration[kJCoreLegacyRegistrationRetired] isEqual:@YES] ||
        (storedRegistration != nil && storedRegistration.count == 0);
    if (storageKey.length > 0 && !legacyRetired) {
        NSMutableDictionary *legacy = [JCoreReadLegacyRegistration(storageKey) mutableCopy];
        if (legacy != nil) {
            legacy[kJCoreLegacyRegistrationRetired] = @YES;
            registration = legacy;
        }
    }
    _uid = JCoreRegistrationUID(registration[@"uid"]);
    _password = JCoreNonEmptyString(registration[@"password"]);
    _registrationID = JCoreNonEmptyString(registration[@"registrationID"]);
    _deviceID = JCoreNonEmptyString(registration[@"deviceID"]);
    _isNewDevice = (_uid == 0 || _password.length == 0 || _registrationID.length == 0);
    if (_isNewDevice) {
        _uid = 0;
        _password = nil;
        _registrationID = nil;
    }
    if (storageKey.length > 0 && registration != storedRegistration && registration != nil) {
        [self persistRegistrationLocked:registration];
    } else if (storageKey.length > 0 && registrations == nil) {
        [defaults setObject:@{} forKey:kJCoreKeyRegistrations];
    }
    if (storageKey.length > 0) {
        [defaults setObject:storageKey forKey:kJCoreKeyLastConfiguredAppKey];
        [defaults removeObjectForKey:kJCoreKeyUid];
        [defaults removeObjectForKey:kJCoreKeyPassword];
        [defaults removeObjectForKey:kJCoreKeyRegistrationId];
        [defaults removeObjectForKey:kJCoreKeyDeviceId];
    }
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

- (BOOL)collectionAuthorized {
    @synchronized (self) { return _collectionAuthorized; }
}

- (BOOL)pushStopped {
    @synchronized (self) { return _pushStopped; }
}

- (NSUInteger)policyGeneration {
    @synchronized (self) { return _policyGeneration; }
}

- (BOOL)policyAllowsPush {
    @synchronized (self) { return _collectionAuthorized && !_pushStopped; }
}

- (BOOL)allowsPolicyGeneration:(NSUInteger)generation {
    @synchronized (self) {
        return _collectionAuthorized && !_pushStopped && _policyGeneration == generation;
    }
}

- (void)setCollectionAuthorized:(BOOL)collectionAuthorized {
    NSUInteger generation;
    @synchronized (self) {
        if (_collectionAuthorized == collectionAuthorized) { return; }
        _collectionAuthorized = collectionAuthorized;
        generation = ++_policyGeneration;
        [[NSUserDefaults standardUserDefaults] setBool:collectionAuthorized forKey:kJCoreKeyAuth];
    }
    [[NSNotificationCenter defaultCenter] postNotificationName:JCOREPolicyDidChangeNotification object:self userInfo:@{@"generation": @(generation)}];
}

- (void)setPushStopped:(BOOL)pushStopped {
    NSUInteger generation;
    @synchronized (self) {
        if (pushStopped) {
            [[NSUserDefaults standardUserDefaults] removeObjectForKey:kJCoreKeyPendingResumeAppKey];
        }
        if (_pushStopped == pushStopped) { return; }
        _pushStopped = pushStopped;
        generation = ++_policyGeneration;
        [[NSUserDefaults standardUserDefaults] setBool:pushStopped forKey:kJCoreKeyPushStopped];
    }
    [[NSNotificationCenter defaultCenter] postNotificationName:JCOREPolicyDidChangeNotification object:self userInfo:@{@"generation": @(generation)}];
}

- (void)resumePushLocally {
    NSUInteger generation;
    @synchronized (self) {
        if (!_pushStopped) { return; }
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        id pending = _appKey.length > 0 ? (id)_appKey : @YES;
        [defaults setObject:pending forKey:kJCoreKeyPendingResumeAppKey];
        _pushStopped = NO;
        generation = ++_policyGeneration;
        [defaults setBool:NO forKey:kJCoreKeyPushStopped];
    }
    [[NSNotificationCenter defaultCenter] postNotificationName:JCOREPolicyDidChangeNotification object:self
        userInfo:@{@"generation": @(generation)}];
}

- (BOOL)hasPendingPushResumeForAppKey:(NSString *)appKey {
    @synchronized (self) {
        NSString *pending = [[NSUserDefaults standardUserDefaults] stringForKey:kJCoreKeyPendingResumeAppKey];
        return _collectionAuthorized && !_pushStopped && [_appKey isEqualToString:appKey] && [pending isEqualToString:appKey];
    }
}

- (void)acknowledgePushResumeForAppKey:(NSString *)appKey policyGeneration:(NSUInteger)generation {
    @synchronized (self) {
        if (generation == _policyGeneration && [self hasPendingPushResumeForAppKey:appKey]) {
            [[NSUserDefaults standardUserDefaults] removeObjectForKey:kJCoreKeyPendingResumeAppKey];
        }
    }
}

- (void)setDeviceTokenHex:(NSString *)deviceTokenHex {
    @synchronized (self) {
        _deviceTokenRecord = JCorePersistToken(kJCoreKeyDeviceToken, [deviceTokenHex copy],
            _hasAPNsEnvironment ? @(_isProduction) : nil);
    }
}

- (NSString *)deviceTokenHex {
    @synchronized (self) { return [self tokenForCurrentEnvironment:_deviceTokenRecord]; }
}

- (void)setVoipTokenHex:(NSString *)voipTokenHex {
    @synchronized (self) {
        _voipTokenRecord = JCorePersistToken(kJCoreKeyVoipToken, [voipTokenHex copy],
            _hasAPNsEnvironment ? @(_isProduction) : nil);
    }
}

- (NSString *)voipTokenHex {
    @synchronized (self) { return [self tokenForCurrentEnvironment:_voipTokenRecord]; }
}

- (NSString *)tokenForCurrentEnvironment:(NSDictionary *)record {
    id production = record[@"production"];
    if (_hasAPNsEnvironment && ![production isEqual:@(_isProduction)]) {
        return nil;
    }
    return JCoreNonEmptyString(record[@"hex"]);
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
        [self updateRegistrationLockedWithUid:uid password:password registrationID:registrationID deviceID:deviceID];
    });
}

- (BOOL)commitRegistrationWithUid:(uint64_t)uid
                         password:(NSString *)password
                   registrationID:(NSString *)registrationID
                         deviceID:(NSString *)deviceID
                 policyGeneration:(NSUInteger)generation {
    __block BOOL committed = NO;
    dispatch_sync(_stateQueue, ^{
        @synchronized (self) {
            if (![self allowsPolicyGeneration:generation]) return;
            [self updateRegistrationLockedWithUid:uid password:password registrationID:registrationID deviceID:deviceID];
            committed = YES;
        }
    });
    return committed;
}

- (void)updateRegistrationLockedWithUid:(uint64_t)uid
                               password:(NSString *)password
                         registrationID:(NSString *)registrationID
                               deviceID:(NSString *)deviceID {
    if (deviceID.length > 0) {
        _deviceID = [deviceID copy];
    }
    NSDictionary *registration = JCoreRegistration(uid, password, registrationID, _deviceID);
    _uid = registration != nil ? uid : 0;
    _password = [registration[@"password"] copy];
    _registrationID = [registration[@"registrationID"] copy];
    _isNewDevice = registration == nil;
    NSMutableDictionary *updated = [registration mutableCopy] ?: [NSMutableDictionary dictionary];
    updated[kJCoreLegacyRegistrationRetired] = @YES;
    [self persistRegistrationLocked:updated];
}

- (void)persistRegistrationLocked:(NSDictionary *)registration {
    if (_appKey.length == 0) {
        return;
    }
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSMutableDictionary *registrations = [[defaults dictionaryForKey:kJCoreKeyRegistrations] mutableCopy] ?: [NSMutableDictionary dictionary];
    NSString *storageKey = _appKey.lowercaseString;
    NSDictionary *previous = registrations[storageKey];
    if ([previous isKindOfClass:NSDictionary.class] && [previous[kJCoreLegacyRegistrationRetired] isEqual:@YES]) {
        NSMutableDictionary *updated = [registration mutableCopy];
        updated[kJCoreLegacyRegistrationRetired] = @YES;
        registration = updated;
    }
    registrations[storageKey] = registration;
    [defaults setObject:registrations forKey:kJCoreKeyRegistrations];
}

- (BOOL)resetRegistrationForPolicyGeneration:(NSUInteger)generation {
    __block BOOL reset = NO;
    dispatch_sync(_stateQueue, ^{
        @synchronized (self) {
            if (![self allowsPolicyGeneration:generation]) return;
            [self resetRegistrationLocked];
            reset = YES;
        }
    });
    return reset;
}

- (void)resetRegistration {
    dispatch_sync(_stateQueue, ^{
        [self resetRegistrationLocked];
    });
}

- (void)resetRegistrationLocked {
    _uid = 0;
    _password = nil;
    _registrationID = nil;
    _preSetupRegistrationID = nil;
    _deviceID = nil;
    _isNewDevice = YES;
    [self persistRegistrationLocked:@{kJCoreLegacyRegistrationRetired: @YES}];
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:kJCoreKeyFakeImei];
}

- (void)updateWithSisDictionary:(NSDictionary *)sisDict {
    dispatch_sync(_stateQueue, ^{
        [self updateSisCacheLocked:sisDict customSis:NO refreshingAddresses:@[] udpServer:nil];
    });
}

- (BOOL)commitSisDictionary:(NSDictionary *)sisDict
                  addresses:(NSArray<NSString *> *)addresses
                  udpServer:(NSString *)udpServer
                  customSis:(BOOL)customSis
                     appKey:(NSString *)appKey
           policyGeneration:(NSUInteger)generation {
    __block BOOL committed = NO;
    dispatch_sync(_stateQueue, ^{
        @synchronized (self) {
            if (![self allowsPolicyGeneration:generation] || ![self->_appKey isEqualToString:appKey]) return;
            [self updateSisCacheLocked:sisDict customSis:customSis refreshingAddresses:addresses udpServer:udpServer];
            committed = YES;
        }
    });
    return committed;
}

- (void)updateSisCacheLocked:(NSDictionary *)sisDict
                   customSis:(BOOL)customSis
           refreshingAddresses:(NSArray<NSString *> *)addresses
                     udpServer:(NSString *)udpServer {
    if (![sisDict isKindOfClass:[NSDictionary class]]) {
        return;
    }
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    if ([sisDict[@"ips"] isKindOfClass:[NSArray class]]) {
        self->_ips = [sisDict[@"ips"] copy];
        [defaults setObject:self->_ips forKey:kJCoreKeySisIps];
    }
    if ([sisDict[@"ssl_ips"] isKindOfClass:[NSArray class]]) {
        self->_sslIps = [sisDict[@"ssl_ips"] copy];
        [defaults setObject:self->_sslIps forKey:kJCoreKeySisSslIps];
        self->_cachedSslIpsRequireTls = customSis;
        [defaults setBool:customSis forKey:kJCoreKeySisCustomSslIps];
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
    if (addresses.count > 0) {
        [self->_failedIpsMutable removeObjectsInArray:addresses];
        [defaults setObject:[self->_failedIpsMutable copy] forKey:kJCoreKeySisFailedIps];
    }
    if (udpServer.length > 0 && ![self->_lastSuccessSisIpsMutable containsObject:udpServer]) {
        [self->_lastSuccessSisIpsMutable addObject:udpServer];
        [defaults setObject:[self->_lastSuccessSisIpsMutable copy] forKey:kJCoreKeySisLastSuccess];
    }
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

- (BOOL)cachedSslIpsRequireTls {
    __block BOOL requiresTls;
    dispatch_sync(_stateQueue, ^{ requiresTls = self->_cachedSslIpsRequireTls; });
    return requiresTls;
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
        self->_cachedSslIpsRequireTls = YES;
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
        [defaults removeObjectForKey:kJCoreKeySisCustomSslIps];
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
        NSString *saved = self->_deviceID;
        if (saved.length > 0) {
            existed = YES;
            devId = saved;
        } else {
            existed = NO;
            NSString *seed = [self deviceSeedLocked];
            devId = [JCORECrypto md5Hex:[NSString stringWithFormat:@"dev:%@:%@", self->_appKey ?: @"jcore", seed]];
            self->_deviceID = [devId copy];
            NSDictionary *registration = JCoreRegistration(self->_uid, self->_password, self->_registrationID, devId);
            [self persistRegistrationLocked:registration ?: @{@"deviceID": devId}];
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
        result = self->_registrationID ?: self->_preSetupRegistrationID;
    });
    return result.length > 0 ? result : @"";
}

- (void)ensureLocalRegistrationFallbackIfNeeded {
    // Registration ID, UID, and password must only be populated by server Register (cmd=0) or explicit persistence.
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

+ (void)setLocalLogEnabled:(BOOL)enabled {
    JCOREConfiguration *config = [self shared];
    config.localLogEnabled = NO;
    config.forceLocalLogWhenLogOff = NO;
    (void)enabled;
}

+ (void)dispatchLogWithLevel:(NSString *)level selectorName:(NSString *)selectorName message:(NSString *)message {
    if (message == nil) {
        return;
    }
    JCOREConfiguration *config = [self shared];
    if (config.logOff) {
        return;
    }

    Class oneKeyLogClass = NSClassFromString(@"ReactNativeNativeLogger.OneKeyLog") ?: NSClassFromString(@"OneKeyLog");
    SEL selector = NSSelectorFromString(selectorName);
    if (oneKeyLogClass != Nil && [oneKeyLogClass respondsToSelector:selector]) {
        void (*logFunc)(id, SEL, NSString *, NSString *) = (void (*)(id, SEL, NSString *, NSString *))objc_msgSend;
        logFunc(oneKeyLogClass, selector, @"RN-JPush", message);
        return;
    }
    if (config.debugEnabled) {
        NSLog(@"[RN-JPush] [%@] %@", level, message);
    }
}

+ (void)logDebug:(NSString *)message {
    [self dispatchLogWithLevel:@"DEBUG" selectorName:@"debug::" message:message];
}

+ (void)logInfo:(NSString *)message {
    [self dispatchLogWithLevel:@"INFO" selectorName:@"info::" message:message];
}

+ (void)logWarn:(NSString *)message {
    [self dispatchLogWithLevel:@"WARN" selectorName:@"warn::" message:message];
}

+ (void)logError:(NSString *)message {
    [self dispatchLogWithLevel:@"ERROR" selectorName:@"error::" message:message];
}

+ (NSArray<NSString *> *)recentLogs {
    return @[];
}

+ (void)clearRecentLogs {
}

- (void)log:(NSString *)format, ... {
    if (self.logOff || !self.debugEnabled || format == nil) {
        return;
    }
    va_list args;
    va_start(args, format);
    NSString *message = [[NSString alloc] initWithFormat:format arguments:args];
    va_end(args);
    [JCOREConfiguration logDebug:message];
}

@end

@implementation JCOREPersistence
@end
