// Source recovery modifications: see docs/IOS_PROVENANCE.md.
// SOURCE_RESTORED_UNLINKED: ICMOreoEngine has no product implementation yet.
#import "ICMAppIdentity.h"
#import "ICMOreoEngine.h"

@implementation ICMAppIdentity
@synthesize appId = _appId, apiKey = _apiKey, engine = _engine;
- (ICMOreoEngine *)engine {
    @synchronized(self) {
        if (!_engine) _engine = [[ICMOreoEngine alloc] initWithApiKey:_apiKey appId:_appId];
        return _engine;
    }
}
- (BOOL)isEqual:(id)object {
    @synchronized(self) {
        if (self == object) return YES;
        if (![object isKindOfClass:[ICMAppIdentity class]]) return NO;
        ICMAppIdentity *other = object;
        return ((!other.appId && !self.appId) || [other.appId isEqualToString:self.appId])
            && ((!other.apiKey && !self.apiKey) || [other.apiKey isEqualToString:self.apiKey]);
    }
}
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.appId forKey:@"appId"];
    [coder encodeObject:self.apiKey forKey:@"apiKey"];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    if ((self = [super init])) {
        self.appId = [coder decodeObjectOfClass:[NSString class] forKey:@"appId"];
        self.apiKey = [coder decodeObjectOfClass:[NSString class] forKey:@"apiKey"];
    }
    return self;
}
- (BOOL)setApiKey:(NSString *)apiKey appId:(NSString *)appId {
    @synchronized(self) {
        BOOL changed = ![apiKey isEqualToString:self.apiKey] || ![appId isEqualToString:self.appId];
        self.apiKey = apiKey;
        self.appId = appId;
        if (changed) {
            [_engine ignoreAllPendingRequests];
            _engine = [[ICMOreoEngine alloc] initWithApiKey:apiKey appId:appId];
        }
        return changed;
    }
}
- (BOOL)identityExists {
    @synchronized(self) { return _appId.length > 0 && _apiKey.length > 0; }
}
@end
