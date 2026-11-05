#import "ICMUserUpdateRequest.h"
#import "ICMConfig.h"
@implementation ICMUserUpdateRequest
@synthesize newSession = _newSession, sessionId = _sessionId, internalUpdate = _internalUpdate, attributes = _attributes, sentFromBackgroundAppState = _sentFromBackgroundAppState;
- (instancetype)init {
    self = [super init];
    if (self) {
        _newSession = NO;
        _attributes = @{};
        _sentFromBackgroundAppState = YES;
    }
    return self;
}
- (instancetype)initWithNewSession:(BOOL)newSession internalUpdate:(BOOL)internalUpdate sentFromBackgroundAppState:(BOOL)background {
    self = [self init];
    if (self) {
        _newSession = newSession;
        _internalUpdate = internalUpdate;
        _sentFromBackgroundAppState = background;
    }
    return self;
}
- (instancetype)initWithNewSession:(BOOL)newSession sessionId:(NSString *)sessionId internalUpdate:(BOOL)internalUpdate sentFromBackgroundAppState:(BOOL)background {
    self = [self initWithNewSession:newSession internalUpdate:internalUpdate sentFromBackgroundAppState:background];
    if (self) _sessionId = [sessionId copy];
    return self;
}
- (instancetype)initWithAttributes:(NSDictionary *)attributes sentFromBackgroundAppState:(BOOL)background {
    self = [self init];
    if (self) { _attributes = attributes; _sentFromBackgroundAppState = background; }
    return self;
}
- (void)updateWithUserUpdate:(ICMUserUpdateRequest *)update {
    self.newSession = self.newSession || update.newSession;
    self.sessionId = self.sessionId ?: update.sessionId;
    self.internalUpdate = self.isInternalUpdate || update.isInternalUpdate;
    self.sentFromBackgroundAppState = self.sentFromBackgroundAppState && update.sentFromBackgroundAppState;
    self.attributes = [ICMUserUpdateRequest addUserAttributes:update.attributes toAttributes:self.attributes];
}
- (BOOL)canBeUpdatedWithUpdate:(ICMUserUpdateRequest *)update {
    if ([ICMUserUpdateRequest userAttributes:update.attributes willOverwriteAttributes:self.attributes]) return NO;
    return !(update.newSession && self.newSession);
}
- (BOOL)isEmptyUpdate { return !self.isInternalUpdate && self.attributes.count == 0; }
- (NSTimeInterval)maxDelay {
    return self.isInternalUpdate ? [ICMConfig sharedConfig].pingDelay : [ICMConfig sharedConfig].batchUserUpdatePeriod;
}
- (void)addUserAttributes:(NSDictionary *)attributes {
    NSMutableDictionary *merged = [self.attributes mutableCopy];
    [merged addEntriesFromDictionary:attributes];
    self.attributes = merged;
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (!object || ![[object class] isEqual:[self class]]) return NO;
    return [self isEqualToUpdate:object];
}
- (BOOL)isEqualToUpdate:(ICMUserUpdateRequest *)update {
    if (self.newSession != update.newSession) return NO;
    if (self.isInternalUpdate != update.isInternalUpdate) return NO;
    if (self.sentFromBackgroundAppState != update.sentFromBackgroundAppState) return NO;
    if (self.sessionId != update.sessionId && ![self.sessionId isEqualToString:update.sessionId]) return NO;
    return [self.attributes isEqual:update.attributes];
}
- (NSUInteger)hash {
    return self.attributes.hash ^ @(self.newSession).hash ^ @(self.isInternalUpdate).hash ^ @(self.sentFromBackgroundAppState).hash ^ self.sessionId.hash;
}
+ (NSDictionary *)addUserAttributes:(NSDictionary *)attributes toAttributes:(NSDictionary *)previous {
    NSMutableDictionary *result = [previous mutableCopy];
    [result addEntriesFromDictionary:attributes];
    id currentCustom = attributes[@"custom_attributes"];
    id oldCustom = previous[@"custom_attributes"];
    if (!currentCustom) return result;
    if ([currentCustom isKindOfClass:[NSDictionary class]] && [currentCustom count] > 0) {
        NSMutableDictionary *mergedCustom = [oldCustom mutableCopy] ?: [NSMutableDictionary new];
        [mergedCustom addEntriesFromDictionary:currentCustom];
        result[@"custom_attributes"] = mergedCustom;
    } else if (oldCustom) {
        result[@"custom_attributes"] = oldCustom;
    } else {
        [result removeObjectForKey:@"custom_attributes"];
    }
    return result;
}
+ (BOOL)userAttributes:(NSDictionary *)attributes willOverwriteAttributes:(NSDictionary *)previous {
    id oldCustom = previous[@"custom_attributes"];
    if (oldCustom) {
        id currentCustom = attributes[@"custom_attributes"];
        if ([currentCustom isKindOfClass:[NSDictionary class]]) {
            for (id key in oldCustom) {
                if (currentCustom[key] && ![currentCustom[key] isEqual:oldCustom[key]]) return YES;
            }
        }
    }
    for (id key in previous) {
        if (![key isEqualToString:@"custom_attributes"] && attributes[key] && ![attributes[key] isEqual:previous[key]]) return YES;
    }
    return NO;
}
@end
