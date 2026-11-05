// Source recovery modifications: see docs/IOS_PROVENANCE.md.
// SOURCE_RESTORED_UNLINKED: Engine, Config and HTTPBridge are not restored.
#import "ICMIdentityStore.h"
#import "ICMIdentityStoreDependencies.h"
#import "../Identity/ICMPersistenceProxy.h"
#import "../Models/ICMUserAttributes.h"
#import <UIKit/UIKit.h>

@implementation ICMIdentityStore
@synthesize appIdentity = _appIdentity, userIdentity = _userIdentity, previousUserIdentity = _previousUserIdentity;
@synthesize deviceTokenData = _deviceTokenData, isUserAuthenticatedWithAPI = _isUserAuthenticatedWithAPI;
@synthesize previousUserIdentityDeviceToken = _previousUserIdentityDeviceToken;

+ (instancetype)currentIdentity {
    static ICMIdentityStore *identity;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ identity = [ICMIdentityStore identityFromDisk] ?: [ICMIdentityStore new]; });
    return identity;
}
- (instancetype)init {
    if ((self = [super init])) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(clearPreviousIdentity) name:UIApplicationDidEnterBackgroundNotification object:nil];
    }
    return self;
}
- (void)dealloc { [[NSNotificationCenter defaultCenter] removeObserver:self]; }
+ (instancetype)identityFromDisk {
    @synchronized(self) {
        ICMIdentityStore *identity = [ICMPersistenceProxy unarchiveObject:[ICMIdentityStore class] withFile:[self archivePath]];
        ICMIdentityStore *backup = [ICMPersistenceProxy unarchiveObject:[ICMIdentityStore class] withFile:[self backupArchivePath]];
        if (identity) {
            if (![backup isEqual:identity]) [ICMPersistenceProxy archiveRootObject:identity toFile:[self backupArchivePath]];
        } else if (backup) {
            identity = backup;
            [ICMPersistenceProxy archiveRootObject:identity toFile:[self archivePath]];
        }
        return identity;
    }
}
+ (BOOL)saveIdentityStore:(ICMIdentityStore *)store {
    @synchronized(self) {
        BOOL written = [ICMPersistenceProxy archiveRootObject:store toFile:[ICMIdentityStore archivePath]];
        if (written) [ICMPersistenceProxy archiveRootObject:store toFile:[ICMIdentityStore backupArchivePath]];
        return written;
    }
}
+ (NSString *)archivePath { return [[ICMUtils intercomDocumentsDirectory] stringByAppendingPathComponent:@"identity.icm"]; }
+ (NSString *)backupArchivePath { return [[ICMUtils intercomDocumentsDirectory] stringByAppendingPathComponent:@"identity.bak.icm"]; }
- (BOOL)saveToDisk {
    @synchronized(self) {
        ICMIdentityStore *stored = [ICMIdentityStore identityFromDisk];
        if (stored && [stored isEqual:self]) return YES;
        return [ICMIdentityStore saveIdentityStore:self];
    }
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[ICMIdentityStore class]]) return NO;
    ICMIdentityStore *other = object;
    return [other.appIdentity isEqual:self.appIdentity] && [other.userIdentity isEqual:self.userIdentity]
        && ((!other.deviceTokenData && !self.deviceTokenData) || [other.deviceTokenData isEqual:self.deviceTokenData])
        && other.isUserAuthenticatedWithAPI == self.isUserAuthenticatedWithAPI;
}
- (ICMAppIdentity *)appIdentity {
    if (!_appIdentity) _appIdentity = [ICMAppIdentity new];
    return _appIdentity;
}
- (ICMUserIdentity *)userIdentity {
    if (!_userIdentity) _userIdentity = [ICMUserIdentity new];
    return _userIdentity;
}
- (ICMDeviceTokenData *)deviceTokenData {
    if (!_deviceTokenData) _deviceTokenData = [ICMDeviceTokenData new];
    return _deviceTokenData;
}
+ (NSString *)hexTokenFromDeviceToken:(NSData *)token {
    const signed char *bytes = token.bytes;
    NSMutableString *result = [NSMutableString string];
    for (NSUInteger i = 0; i < token.length; i++) [result appendFormat:@"%02.2hhX", bytes[i]];
    return result;
}
- (BOOL)setDeviceTokenData:(NSData *)token environment:(NSUInteger)environment failure:(void (^)(NSError *))failure {
    NSString *hexToken = [[self class] hexTokenFromDeviceToken:token];
    if (!hexToken) {
        void (^reportFailure)(void) = ^{ if (failure) failure([ICMRecoveredErrorProcessor generateInvalidDeviceTokenError]); };
        if ([NSThread isMainThread]) reportFailure();
        else dispatch_async(dispatch_get_main_queue(), reportFailure);
    }
    if ([hexToken isEqualToString:self.deviceTokenData.deviceToken] && environment == self.deviceTokenData.deviceTokenEnvironment) return NO;
    self.deviceTokenData.deviceToken = hexToken;
    self.deviceTokenData.deviceTokenEnvironment = environment;
    return YES;
}
- (BOOL)registerUnidentifiedUser {
    @synchronized(self) {
        BOOL registered = [self.userIdentity registerUnidentifiedUser];
        if (registered) [self saveToDisk];
        self.previousUserIdentity = nil;
        return registered;
    }
}
- (void)usePreviousIdentity {
    self.userIdentity = self.previousUserIdentity;
    self.previousUserIdentity = nil;
    [self saveToDisk];
}
- (BOOL)registerIdentifiedUserWithProperties:(ICMUserAttributes *)properties {
    @synchronized(self) {
        if (!self.userIdentity.identityExists && [self.previousUserIdentity matchesIdentity:properties.registrationAttributes]) {
            [self usePreviousIdentity];
            return NO;
        }
        BOOL registered = [self.userIdentity registerIdentifiedUserWithProperties:properties.registrationAttributes];
        if (registered) [self saveToDisk];
        self.previousUserIdentity = nil;
        return registered;
    }
}
- (void)resetUserIdentity {
    @synchronized(self) {
        if (self.userIdentity.identityExists) self.previousUserIdentity = self.userIdentity;
        self.userIdentity = [ICMUserIdentity new];
        self.deviceTokenData.deviceTokenSubmitted = NO;
        [[ICMRecoveredKeychain shared] clearAll];
        if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] ICMIdentityStore: Keychain cleared on identity reset");
        [self saveToDisk];
    }
}
- (void)updateUserIdentityWith:(ICMUserIdentity *)identity {
    @synchronized(self) { [self.userIdentity updateWithIdentity:identity]; [self saveToDisk]; }
}
- (void)updateDeviceTokenSubmitted:(BOOL)submitted {
    @synchronized(self) {
        if (submitted != self.deviceTokenData.deviceTokenSubmitted) {
            self.deviceTokenData.deviceTokenSubmitted = submitted;
            [self saveToDisk];
        }
    }
}
- (void)setPreviousUserIdentity:(ICMUserIdentity *)identity {
    if (_previousUserIdentity && identity != _previousUserIdentity) [self clearPreviousIdentityWithDelay:0];
    _previousUserIdentity = identity;
    _previousUserIdentityDeviceToken = self.deviceTokenData.deviceToken;
    [self clearPreviousIdentityWithDelay:[[ICMConfig sharedConfig] softResetTimeout]];
}
- (void)clearPreviousIdentityWithDelay:(NSTimeInterval)delay {
    ICMUserIdentity *previous = self.previousUserIdentity;
    if (!previous) return;
    void (^clear)(void) = ^{
        @synchronized(self) {
            if (self->_previousUserIdentity != previous) return;
            self->_previousUserIdentity = nil;
            __block UIBackgroundTaskIdentifier task = UIBackgroundTaskInvalid;
            void (^complete)(void) = ^{
                if (task != UIBackgroundTaskInvalid) [[UIApplication sharedApplication] endBackgroundTask:task];
            };
            task = [[UIApplication sharedApplication] beginBackgroundTaskWithName:@"io.intercom.ios.delete_device_token_task" expirationHandler:complete];
            NSString *token = self->_previousUserIdentityDeviceToken;
            self->_previousUserIdentityDeviceToken = nil;
            if (token.length) [ICMHTTPBridge deleteDeviceToken:token forUserIdentity:previous completion:complete];
            else complete();
        }
    };
    if (delay == 0) clear();
    else dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delay * NSEC_PER_SEC)), dispatch_get_main_queue(), clear);
}
- (void)clearPreviousIdentity { [self clearPreviousIdentityWithDelay:0]; }
- (BOOL)updateSecureOptions:(NSDictionary *)options {
    @synchronized(self) {
        BOOL equal = [self.userIdentity.secureOptions isEqualToDictionary:options];
        if (!equal) {
            if (self.previousUserIdentity && ![self.previousUserIdentity.secureOptions isEqual:options]) [self clearPreviousIdentity];
            self.userIdentity.secureOptions = options;
            [self saveToDisk];
        }
        return !equal;
    }
}
- (NSArray *)userFingerprint {
    @synchronized(self) {
        ICMUserIdentity *user = self.userIdentity;
        if (!user.identityExists) return nil;
        return @[user.anonymousId ?: [NSNull null], user.userId ?: [NSNull null], user.email ?: [NSNull null]];
    }
}
- (NSString *)userJwtForFingerprint:(NSArray *)fingerprint {
    @synchronized(self) {
        if (![self.userFingerprint isEqualToArray:fingerprint]) return nil;
        id jwt = self.userIdentity.secureOptions[@"intercom_user_jwt"];
        return [jwt isKindOfClass:[NSString class]] ? jwt : @"";
    }
}
- (BOOL)applyRefreshedUserJwt:(NSString *)jwt replacing:(NSString *)staleJwt forFingerprint:(NSArray *)fingerprint {
    @synchronized(self) {
        BOOL matches = [[self userJwtForFingerprint:fingerprint] isEqualToString:staleJwt];
        if (matches) {
            NSMutableDictionary *options = [self.userIdentity.secureOptions mutableCopy] ?: [NSMutableDictionary new];
            options[@"intercom_user_jwt"] = jwt;
            self.userIdentity.secureOptions = options;
            [self saveToDisk];
        }
        return matches;
    }
}
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.appIdentity forKey:@"appIdentity"];
    [coder encodeObject:self.userIdentity forKey:@"userIdentity"];
    [coder encodeObject:self.deviceTokenData forKey:@"deviceTokenData"];
    [coder encodeBool:self.isUserAuthenticatedWithAPI forKey:@"isUserAuthenticatedWithAPI"];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    if ((self = [self init])) {
        self.appIdentity = [coder decodeObjectOfClass:[ICMAppIdentity class] forKey:@"appIdentity"];
        self.userIdentity = [coder decodeObjectOfClass:[ICMUserIdentity class] forKey:@"userIdentity"];
        self.deviceTokenData = [coder decodeObjectOfClass:[ICMDeviceTokenData class] forKey:@"deviceTokenData"];
        self.isUserAuthenticatedWithAPI = [coder decodeBoolForKey:@"isUserAuthenticatedWithAPI"];
        [self createDeviceTokenDataIfNoneExists:coder];
    }
    return self;
}
- (void)createDeviceTokenDataIfNoneExists:(NSCoder *)coder {
    NSString *token = [coder decodeObjectOfClass:[NSString class] forKey:@"deviceToken"];
    if (token) {
        self.deviceTokenData.deviceToken = token;
        self.deviceTokenData.deviceTokenEnvironment = [coder decodeIntegerForKey:@"deviceTokenEnvironment"];
    }
}
@end
