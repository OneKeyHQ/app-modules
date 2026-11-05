// Tests inject Security calls and +shared; no host Keychain item is read or changed.
#import <Foundation/Foundation.h>
#import <Security/Security.h>
#import <objc/runtime.h>
#import "ICMUserIdentity.h"
#import "ICMUtils.h"
#import "ICMDeviceTokenData.h"
#import "ICMPersistenceProxy.h"
#import "ICMBaseURLConfiguration.h"

static NSMutableArray<NSString *> *passed;
static NSDictionary *lastQuery, *lastAttributes;
static OSStatus updateStatus, addStatus, deleteStatus, copyStatus;
static NSUInteger updates, adds, deletes, reads;
static NSData *returnedData;
static NSDictionary *fixtureInfo;

@interface FixtureInfoBundle : NSObject
- (id)objectForInfoDictionaryKey:(NSString *)key;
@end
@implementation FixtureInfoBundle
- (id)objectForInfoDictionaryKey:(NSString *)key { return fixtureInfo[key]; }
@end
static id testMainBundle(id receiver, SEL selector) {
    (void)receiver; (void)selector;
    return [FixtureInfoBundle new];
}

// Source calls bind to this test symbol, isolating original path logic from host data.
NSArray<NSString *> *NSSearchPathForDirectoriesInDomains(NSSearchPathDirectory directory, NSSearchPathDomainMask domain, BOOL expandTilde) {
    (void)domain; (void)expandTilde;
    NSString *root = NSProcessInfo.processInfo.environment[@"ICM_TEST_DIRECTORY"];
    if (!root.length) abort();
    return @[[root stringByAppendingPathComponent:[NSString stringWithFormat:@"search-path-%lu", (unsigned long)directory]]];
}

// Linked into the test executable to replace the real Security entry points.
OSStatus SecItemUpdate(CFDictionaryRef query, CFDictionaryRef attributes) {
    updates++;
    lastQuery = [(__bridge NSDictionary *)query copy];
    lastAttributes = [(__bridge NSDictionary *)attributes copy];
    return updateStatus;
}
OSStatus SecItemAdd(CFDictionaryRef query, CFTypeRef *result) {
    (void)result;
    adds++;
    lastQuery = [(__bridge NSDictionary *)query copy];
    return addStatus;
}
OSStatus SecItemDelete(CFDictionaryRef query) {
    deletes++;
    lastQuery = [(__bridge NSDictionary *)query copy];
    return deleteStatus;
}
OSStatus SecItemCopyMatching(CFDictionaryRef query, CFTypeRef *result) {
    reads++;
    lastQuery = [(__bridge NSDictionary *)query copy];
    if (result) *result = returnedData ? CFBridgingRetain(returnedData) : NULL;
    return copyStatus;
}

@interface TestKeychain : NSObject <ICMIdentityKeychainStoring>
@property (nonatomic, strong) NSMutableDictionary<NSString *, NSString *> *values;
@property (nonatomic, strong) NSMutableArray<NSString *> *deleted;
@property (nonatomic) BOOL failWrites;
@property (nonatomic) BOOL failDeletes;
@end
@implementation TestKeychain
- (instancetype)init {
    if ((self = [super init])) {
        _values = [NSMutableDictionary new];
        _deleted = [NSMutableArray new];
    }
    return self;
}
- (NSString *)stringForKey:(NSString *)key { return self.values[key]; }
- (BOOL)setString:(NSString *)value forKey:(NSString *)key {
    if (self.failWrites) return NO;
    self.values[key] = value;
    return YES;
}
- (BOOL)deleteValueForKey:(NSString *)key {
    [self.deleted addObject:key];
    if (self.failDeletes) return NO;
    [self.values removeObjectForKey:key];
    return YES;
}
- (BOOL)clearAll { [self.values removeAllObjects]; return YES; }
@end

@interface TestArchiveException : NSObject <NSSecureCoding>
@end
@implementation TestArchiveException
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    (void)coder;
    @throw [NSException exceptionWithName:@"SyntheticArchiveException" reason:@"synthetic fixture" userInfo:nil];
}
- (instancetype)initWithCoder:(NSCoder *)coder { (void)coder; return [super init]; }
@end

static TestKeychain *codingKeychain;
static id testShared(id receiver, SEL selector) { (void)receiver; (void)selector; return codingKeychain; }
static void check(BOOL condition, NSString *name) {
    if (!condition) { fprintf(stderr, "FAIL: %s\n", name.UTF8String); exit(1); }
    [passed addObject:name];
}

int main(void) {
    @autoreleasepool {
        passed = [NSMutableArray new];
        Class keychainClass = NSClassFromString(@"_TtC18IntercomSDKPrivate11ICMKeychain");
        check(keychainClass != Nil, @"original Swift keychain runtime class name");
        ICMRecoveredKeychain *keychain = [[keychainClass alloc] initWithServiceIdentifier:@"synthetic-fixture.intercom-sdk.identity"];
        updateStatus = errSecSuccess;
        check([keychain setString:@"synthetic" forKey:@"hmac"] && updates == 1 && adds == 0, @"keychain updates before adding");
        check([lastQuery[(__bridge id)kSecClass] isEqual:(__bridge id)kSecClassGenericPassword] && [lastQuery[(__bridge id)kSecAttrService] isEqual:@"synthetic-fixture.intercom-sdk.identity"] && [lastQuery[(__bridge id)kSecAttrAccount] isEqual:@"hmac"] && lastQuery.count == 3, @"keychain base query exact fields");
        check([lastAttributes[(__bridge id)kSecValueData] isEqual:[@"synthetic" dataUsingEncoding:NSUTF8StringEncoding]] && [lastAttributes[(__bridge id)kSecAttrAccessible] isEqual:(__bridge id)kSecAttrAccessibleAfterFirstUnlock] && lastAttributes.count == 2, @"keychain update data and accessibility");
        updateStatus = errSecItemNotFound; addStatus = errSecSuccess;
        check([keychain setString:@"synthetic" forKey:@"hmac"] && adds == 1 && lastQuery.count == 5, @"keychain not found adds merged item");
        addStatus = errSecAuthFailed;
        check(![keychain setString:@"synthetic" forKey:@"hmac"], @"keychain add failure returns false");
        NSUInteger priorAdds = adds;
        updateStatus = errSecAuthFailed;
        check(![keychain setString:@"synthetic" forKey:@"hmac"] && adds == priorAdds, @"keychain other update error does not add");
        deleteStatus = errSecItemNotFound;
        check([keychain deleteValueForKey:@"hmac"] && lastQuery.count == 3, @"keychain delete not found succeeds");
        deleteStatus = errSecAuthFailed;
        check(![keychain deleteValueForKey:@"hmac"], @"keychain delete failure returns false");
        deleteStatus = errSecSuccess;
        check([keychain clearAll] && lastQuery.count == 2 && !lastQuery[(__bridge id)kSecAttrAccount], @"keychain clear uses only service and class");
        returnedData = [@"synthetic-value" dataUsingEncoding:NSUTF8StringEncoding]; copyStatus = errSecSuccess;
        check([[keychain stringForKey:@"hmac"] isEqual:@"synthetic-value"] && reads == 1, @"keychain string UTF8 read");
        check([lastQuery[(__bridge id)kSecReturnData] isEqual:@YES] && [lastQuery[(__bridge id)kSecMatchLimit] isEqual:(__bridge id)kSecMatchLimitOne] && lastQuery.count == 5, @"keychain read return data and match limit");
        copyStatus = errSecItemNotFound;
        check([keychain stringForKey:@"hmac"] == nil, @"keychain read failure nil");
        copyStatus = errSecSuccess;
        const unsigned char invalidUTF8[] = {0xff}; returnedData = [NSData dataWithBytes:invalidUTF8 length:1];
        check([keychain stringForKey:@"hmac"] == nil, @"keychain invalid UTF8 nil");

        ICMUserIdentity *identity = [ICMUserIdentity new];
        check(!identity.identityExists && !identity.isIdentified && !identity.isUnidentified, @"identity empty state");
        check([identity.dictionaryRepresentation isEqual:@{@"user": @{@"type": @"user"}}], @"identity default nested dictionary type");
        check([identity registerUnidentifiedUser] && identity.isUnidentified && [[[NSUUID alloc] initWithUUIDString:identity.anonymousId] isKindOfClass:[NSUUID class]], @"unidentified registration generates UUID");
        NSString *anonymous = identity.anonymousId;
        check(![identity registerUnidentifiedUser] && identity.anonymousId == anonymous, @"duplicate anonymous registration leaves identity");
        identity.intercomId = @"previous";
        check([identity registerIdentifiedUserWithProperties:@{@1: @"synthetic-user", @0: @"synthetic@example.invalid", @2: @"synthetic-intercom"}] && identity.isIdentified && identity.anonymousId == anonymous && [identity.intercomId isEqual:@"synthetic-intercom"], @"identified registration retains anonymous id and numeric identifiers");
        check(![identity registerIdentifiedUserWithProperties:@{@1: @"changed"}] && [identity.userId isEqual:@"synthetic-user"], @"duplicate identified registration leaves identity");
        check(!identity.dictionaryRepresentation[@"user"][@"intercom_id"], @"anonymous id suppresses intercom id in dictionary");
        check([identity matchesIdentity:@{@1: @"synthetic-user", @0: @"synthetic@example.invalid"}] && ![identity matchesIdentity:@{@1: @"wrong"}] && ![identity matchesIdentity:@{}], @"identity matching identifiers and empty input");
        check([identity matchesIdentity:@{@99: @"ignored"}], @"unknown numeric matching keys retain original permissiveness");
        ICMUserIdentity *mapped = [ICMUserIdentity instanceFromDictionary:@{@"anonymous_id": @"anonymous", @"user_id": @"user", @"encrypted_user_id": @"encrypted", @"intercom_id": @"intercom", @"unknown": @"ignored"}];
        check([mapped.anonymousId isEqual:@"anonymous"] && [mapped.userId isEqual:@"user"] && [mapped.encryptedUserId isEqual:@"encrypted"] && [mapped.intercomId isEqual:@"intercom"], @"KVC snake case mappings ignore unknown key");
        [mapped setValue:@2 forUndefinedKey:@"user_id"];
        check([mapped.userId isEqual:@"user"], @"snake case mapping ignores nonstring value");
        ICMUserIdentity *update = [ICMUserIdentity new];
        mapped.hasConversations = YES;
        mapped.secureOptions = @{@"other": @"retained"};
        [mapped updateWithIdentity:update];
        check([mapped.intercomId isEqual:@"intercom"] && mapped.userId == nil && mapped.anonymousId == nil && mapped.hasConversations && [mapped.secureOptions isEqual:@{@"other": @"retained"}], @"identity update selective preservation and clearing");
        [mapped clearIntercomId];
        check(mapped.intercomId == nil, @"clear intercom id");
        check([[ICMUserIdentity new] isEqual:[ICMUserIdentity new]], @"identity equality nullable fields");

        TestKeychain *fake = [TestKeychain new];
        NSDictionary *options = @{@"hmac": @"synthetic-hmac", @"intercom_user_jwt": @"synthetic-jwt", @"other": @"retained"};
        check([[ICMUserIdentity nonSensitiveSecureOptionsFrom:options persistingSensitiveTo:fake] isEqual:@{@"other": @"retained"}] && fake.values.count == 2, @"sensitive options successfully migrate out of archive");
        fake.failWrites = YES;
        check([[ICMUserIdentity nonSensitiveSecureOptionsFrom:options persistingSensitiveTo:fake] isEqual:options], @"failed writes retain sensitive archive fallback");
        fake.failDeletes = YES;
        check([ICMUserIdentity nonSensitiveSecureOptionsFrom:@{@"hmac": @"", @"intercom_user_jwt": @42} persistingSensitiveTo:fake] == nil && fake.deleted.count == 2, @"empty nonstring values remove archive despite delete failure");
        check([[ICMUserIdentity secureOptionsByMergingArchivedDict:options withKeychain:fake] isEqual:options], @"merge nonempty keychain values");
        fake.values[@"hmac"] = @"synthetic-new";
        check([[ICMUserIdentity secureOptionsByMergingArchivedDict:options withKeychain:fake][@"hmac"] isEqual:@"synthetic-new"], @"keychain overrides archived sensitive value");
        [fake.values removeAllObjects];
        check([[ICMUserIdentity secureOptionsByMergingArchivedDict:options withKeychain:fake] isEqual:options], @"archive fallback when keychain empty");
        check([ICMUserIdentity secureOptionsByMergingArchivedDict:nil withKeychain:fake] == nil, @"empty merged secure options remain nil");
        check(![ICMUtils debugLoggingEnabled], @"logging defaults disabled");
        [ICMUtils setDebugLoggingEnabled:YES];
        check([ICMUtils debugLoggingEnabled], @"logging setter updates state");
        [ICMUtils setDebugLoggingEnabled:NO];

        codingKeychain = [TestKeychain new];
        codingKeychain.failWrites = YES;
        Method sharedMethod = class_getClassMethod(keychainClass, @selector(shared));
        IMP originalShared = method_setImplementation(sharedMethod, (IMP)testShared);
        identity.secureOptions = options;
        identity.hasConversations = YES;
        identity.encryptedUserId = @"synthetic-encrypted";
        NSError *error = nil;
        NSData *archive = [NSKeyedArchiver archivedDataWithRootObject:identity requiringSecureCoding:YES error:&error];
        check(archive != nil && error == nil, @"secure coder writes real archive");
        ICMUserIdentity *decoded = [NSKeyedUnarchiver unarchivedObjectOfClass:[ICMUserIdentity class] fromData:archive error:&error];
        check(decoded && [decoded isEqual:identity] && error == nil, @"secure coder roundtrip with failed-keychain archive fallback");
        method_setImplementation(sharedMethod, originalShared);
        ICMDeviceTokenData *token = [ICMDeviceTokenData new], *otherToken = [ICMDeviceTokenData new];
        check(token.deviceToken == nil && token.deviceTokenEnvironment == 0 && !token.deviceTokenSubmitted, @"device token empty defaults");
        check([token isEqual:otherToken], @"device token empty equality");
        NSMutableString *tokenString = [NSMutableString stringWithString:@"synthetic-token"];
        token.deviceToken = tokenString;
        [tokenString appendString:@"-changed"];
        check(token.deviceToken == tokenString && [token.deviceToken isEqual:@"synthetic-token-changed"], @"device token setter retains mutable string");
        otherToken.deviceToken = tokenString;
        token.deviceTokenEnvironment = 2;
        check(![token isEqual:otherToken], @"device token equality includes environment");
        otherToken.deviceTokenEnvironment = 2;
        token.deviceTokenSubmitted = YES;
        check(![token isEqual:otherToken], @"device token equality includes submitted flag");
        otherToken.deviceTokenSubmitted = YES;
        check([token isEqual:otherToken], @"device token matching fields equality");
        token.deviceTokenEnvironment = NSUIntegerMax;
        NSData *tokenArchive = [NSKeyedArchiver archivedDataWithRootObject:token requiringSecureCoding:YES error:&error];
        ICMDeviceTokenData *decodedToken = [NSKeyedUnarchiver unarchivedObjectOfClass:[ICMDeviceTokenData class] fromData:tokenArchive error:&error];
        check(decodedToken && [decodedToken isEqual:token] && decodedToken.deviceTokenEnvironment == NSUIntegerMax && error == nil, @"device token secure coding preserves unsigned environment bits");
        NSString *testDirectory = NSProcessInfo.processInfo.environment[@"ICM_TEST_DIRECTORY"];
        check(testDirectory.length > 0, @"persistence fixture has explicit worktree directory");
        [[NSFileManager defaultManager] createDirectoryAtPath:testDirectory withIntermediateDirectories:YES attributes:nil error:&error];
        NSString *tokenPath = [testDirectory stringByAppendingPathComponent:@"synthetic-token.icm"];
        check([ICMPersistenceProxy archiveRootObject:token toFile:tokenPath] && [[NSFileManager defaultManager] fileExistsAtPath:tokenPath], @"persistence proxy writes real archive file");
        check([[ICMPersistenceProxy unarchiveObject:[ICMDeviceTokenData class] withFile:tokenPath] isEqual:token], @"persistence proxy reads real archive with allowed class");
        check([ICMPersistenceProxy unarchiveObject:[NSString class] withFile:tokenPath] == nil, @"persistence proxy rejects wrong allowed class");
        check(![ICMPersistenceProxy archiveRootObject:nil toFile:tokenPath] && ![ICMPersistenceProxy archiveRootObject:token toFile:@""], @"persistence proxy rejects nil object and empty path");
        check(![ICMPersistenceProxy archiveRootObject:token toFile:[testDirectory stringByAppendingPathComponent:@"missing-parent/token.icm"]], @"persistence proxy does not create parent directories");
        check(![ICMPersistenceProxy archiveRootObject:[TestArchiveException new] toFile:tokenPath], @"persistence proxy catches encoder exception");
        check([[ICMPersistenceProxy unarchiveObject:[ICMDeviceTokenData class] withFile:tokenPath] isEqual:token], @"failed encoder preserves previous file");
        NSString *corruptPath = [testDirectory stringByAppendingPathComponent:@"synthetic-corrupt.icm"];
        [@"synthetic-corrupt" writeToFile:corruptPath atomically:YES encoding:NSUTF8StringEncoding error:nil];
        check([ICMPersistenceProxy unarchiveObject:[ICMDeviceTokenData class] withFile:corruptPath] == nil, @"persistence proxy corrupt file returns nil");
        check([ICMPersistenceProxy unarchiveObjectOfClasses:[NSSet set] withFile:tokenPath] == nil && [ICMPersistenceProxy unarchiveObject:Nil withFile:tokenPath] == nil, @"persistence proxy requires nonempty allowed classes");
        check([ICMPersistenceProxy unarchiveObject:[ICMDeviceTokenData class] withFile:[testDirectory stringByAppendingPathComponent:@"missing.icm"]] == nil, @"persistence proxy missing file returns nil");
        NSString *legacyPath = [ICMUtils intercomLegacyDocumentsPath];
        NSString *documentsPath = [ICMUtils intercomDocumentsPath];
        NSString *cachePath = [ICMUtils intercomCachesPath];
        check([legacyPath hasPrefix:testDirectory] && [documentsPath hasSuffix:@"search-path-5/PrivateDocuments/io.intercom.ios"] && [cachePath hasSuffix:@"search-path-13/io.intercom.ios"], @"directory helpers preserve original path components in isolated fixture");
        [[NSFileManager defaultManager] removeItemAtPath:legacyPath error:nil];
        [[NSFileManager defaultManager] removeItemAtPath:documentsPath error:nil];
        check([ICMUtils migrateDocumentsIfNeeded], @"migration no legacy data succeeds");
        [[NSFileManager defaultManager] createDirectoryAtPath:legacyPath withIntermediateDirectories:YES attributes:nil error:nil];
        NSString *legacyMarker = [legacyPath stringByAppendingPathComponent:@"synthetic-marker"];
        [@"synthetic-data" writeToFile:legacyMarker atomically:YES encoding:NSUTF8StringEncoding error:nil];
        check([ICMUtils migrateDocumentsIfNeeded] && ![[NSFileManager defaultManager] fileExistsAtPath:legacyPath] && [[NSFileManager defaultManager] fileExistsAtPath:[documentsPath stringByAppendingPathComponent:@"synthetic-marker"]], @"migration copies real data before removing legacy directory");
        [[NSFileManager defaultManager] createDirectoryAtPath:legacyPath withIntermediateDirectories:YES attributes:nil error:nil];
        [@"synthetic-old" writeToFile:legacyMarker atomically:YES encoding:NSUTF8StringEncoding error:nil];
        check([ICMUtils migrateDocumentsIfNeeded] && [[NSFileManager defaultManager] fileExistsAtPath:legacyMarker], @"migration leaves legacy data when new directory already exists");
        check([[ICMUtils intercomDocumentsDirectory] isEqual:documentsPath] && [[NSFileManager defaultManager] fileExistsAtPath:documentsPath], @"documents directory returns real migrated path");
        check([[ICMUtils intercomCachesDirectory] isEqual:cachePath] && [[NSFileManager defaultManager] fileExistsAtPath:cachePath], @"cache directory creates isolated real directory");
        Method mainBundleMethod = class_getClassMethod([NSBundle class], @selector(mainBundle));
        IMP originalMainBundle = method_setImplementation(mainBundleMethod, (IMP)testMainBundle);
        fixtureInfo = @{};
        check([[ICMBaseURLConfiguration baseUrlForAppId:@"synthetic"].absoluteString isEqual:@"https://synthetic-ios.mobile-messenger.intercom.com/messenger/mobile/"], @"base URL default region and app subdomain");
        check([[ICMBaseURLConfiguration baseUrlForAppId:nil].host isEqual:@"ios.mobile-messenger.intercom.com"] && [[ICMBaseURLConfiguration baseUrlForAppId:@"###"].host isEqual:@"ios.mobile-messenger.intercom.com"], @"base URL nil and fully removed app id use ios");
        check([[ICMBaseURLConfiguration baseUrlForAppId:@"A_b/ C!"].host isEqual:@"AbC-ios.mobile-messenger.intercom.com"], @"base URL sanitizes app id without lowercasing");
        NSString *longApp = [@"a" stringByPaddingToLength:70 withString:@"a" startingAtIndex:0];
        NSString *subdomain = [[[ICMBaseURLConfiguration baseUrlForAppId:longApp].host componentsSeparatedByString:@"."] firstObject];
        check(subdomain.length == 63 && [subdomain isEqual:[longApp substringToIndex:63]], @"base URL truncates whole subdomain before suffix");
        fixtureInfo = @{@"IntercomRegion": @"eu"};
        check([[ICMBaseURLConfiguration getRegion] isEqual:@"mobile-messenger.eu.intercom.io"], @"base URL lowercase EU region");
        fixtureInfo = @{@"IntercomRegion": @"aU"};
        check([[ICMBaseURLConfiguration getRegion] isEqual:@"mobile-messenger.au.intercom.io"], @"base URL mixed case AU region");
        fixtureInfo = @{@"IntercomRegion": @"unknown"};
        check([[ICMBaseURLConfiguration getRegion] isEqual:@"mobile-messenger.intercom.com"], @"base URL unknown region defaults US");
        fixtureInfo = @{@"IntercomCustomURL": @"api.synthetic.invalid", @"IntercomRegion": @"EU"};
        check([[ICMBaseURLConfiguration customLinkForBaseURL] isEqual:@"api.synthetic.invalid"] && [[ICMBaseURLConfiguration baseUrlForAppId:@"synthetic"].host isEqual:@"synthetic-ios.api.synthetic.invalid"], @"custom bare hostname overrides region");
        fixtureInfo = @{@"IntercomCustomURL": @"https://synthetic.invalid"};
        check([ICMBaseURLConfiguration customLinkForBaseURL] == nil, @"custom hostname rejects URL scheme");
        fixtureInfo = @{@"IntercomCustomURL": @"a..synthetic.invalid"};
        check([ICMBaseURLConfiguration customLinkForBaseURL] == nil, @"custom hostname rejects consecutive dots");
        fixtureInfo = @{@"IntercomCustomURL": @"-synthetic.invalid"};
        check([ICMBaseURLConfiguration customLinkForBaseURL] == nil, @"custom hostname rejects leading hyphen");
        fixtureInfo = @{@"IntercomCustomURL": @42};
        check([ICMBaseURLConfiguration customLinkForBaseURL] == nil, @"custom hostname ignores nonstring plist value");
        fixtureInfo = @{@"IntercomRegion": @42};
        BOOL regionThrew = NO;
        @try { [ICMBaseURLConfiguration getRegion]; } @catch (NSException *exception) { regionThrew = [exception.name isEqual:NSInvalidArgumentException]; }
        check(regionThrew, @"region retains original nonstring exception boundary");
        method_setImplementation(mainBundleMethod, originalMainBundle);
        NSDictionary *receipt = @{@"status": @"passed", @"testCount": @(passed.count), @"tests": passed, @"scope": @"Identity state/coding and injected Security calls; no host Keychain accesses; original differential pending", @"securityCallsInjected": @YES};
        NSData *json = [NSJSONSerialization dataWithJSONObject:receipt options:NSJSONWritingPrettyPrinted error:nil];
        fwrite(json.bytes, 1, json.length, stdout); fputc('\n', stdout);
    }
    return 0;
}
