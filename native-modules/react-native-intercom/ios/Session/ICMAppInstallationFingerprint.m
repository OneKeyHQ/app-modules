#import "ICMAppInstallationFingerprint.h"
#import "ICMIdentityStore.h"
#import "../Identity/ICMUtils.h"

@implementation ICMAppInstallationFingerprint
@synthesize appId = _appId, bundleIdentifier = _bundleIdentifier, appVersion = _appVersion, intercomVersion = _intercomVersion;
- (instancetype)init {
    if ((self = [super init])) {
        _appId = [ICMIdentityStore currentIdentity].appIdentity.appId;
        _appVersion = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleShortVersionString"];
        _intercomVersion = [ICMAppInstallationFingerprint currentIntercomVersion];
        _bundleIdentifier = [NSBundle mainBundle].bundleIdentifier;
    }
    return self;
}
+ (NSString *)currentIntercomVersion { return [ICMUtils intercomVersion]; }
+ (BOOL)supportsSecureCoding { return YES; }
- (instancetype)initWithCoder:(NSCoder *)coder {
    if ((self = [super init])) {
        _appId = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(appId))];
        _appVersion = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(appVersion))];
        _intercomVersion = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(intercomVersion))];
        _bundleIdentifier = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(bundleIdentifier))];
    }
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:_appId forKey:NSStringFromSelector(@selector(appId))];
    [coder encodeObject:_appVersion forKey:NSStringFromSelector(@selector(appVersion))];
    [coder encodeObject:_intercomVersion forKey:NSStringFromSelector(@selector(intercomVersion))];
    [coder encodeObject:_bundleIdentifier forKey:NSStringFromSelector(@selector(bundleIdentifier))];
}
- (BOOL)isEqual:(id)object {
    return object == self || (object && [[object class] isEqual:[self class]] && [self isEqualToFingerprint:object]);
}
- (BOOL)isEqualToFingerprint:(ICMAppInstallationFingerprint *)other {
    return (self.appId == other.appId || [self.appId isEqualToString:other.appId]) &&
        (self.appVersion == other.appVersion || [self.appVersion isEqualToString:other.appVersion]) &&
        (self.intercomVersion == other.intercomVersion || [self.intercomVersion isEqualToString:other.intercomVersion]) &&
        (self.bundleIdentifier == other.bundleIdentifier || [self.bundleIdentifier isEqualToString:other.bundleIdentifier]);
}
- (NSUInteger)hash { return self.appId.hash ^ self.appVersion.hash ^ self.intercomVersion.hash ^ self.bundleIdentifier.hash; }
@end
