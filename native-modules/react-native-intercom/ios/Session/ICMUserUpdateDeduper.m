// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMUserUpdateDeduper.h"
#import "ICMUserUpdateRequest.h"
#import "ICMConfig.h"
#import "../Identity/ICMPersistenceProxy.h"
#import "../Identity/ICMUtils.h"
@implementation ICMUserUpdateDeduper
@synthesize hasUnsavedChanges = _hasUnsavedChanges, previouslyUpdatedAttributes = _previouslyUpdatedAttributes, previouslyUpdatedAttributesStartTime = _previouslyUpdatedAttributesStartTime;
- (instancetype)init {
    self = [super init];
    if (self) _hasUnsavedChanges = YES;
    return self;
}
+ (instancetype)instance { return [ICMUserUpdateDeduper deduperFromDisk] ?: [ICMUserUpdateDeduper new]; }
- (void)storeUserUpdate {
    @synchronized (self) {
        if (self.hasUnsavedChanges) {
            self.hasUnsavedChanges = NO;
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^{ [ICMUserUpdateDeduper saveDeduper:self]; });
        }
    }
}
- (BOOL)isDuplicateUserUpdate:(ICMUserUpdateRequest *)update {
    @synchronized (self) {
        if (update.newSession || update.isInternalUpdate) return NO;
        if (!self.previouslyUpdatedAttributes || !([self currentTimestamp] - self.previouslyUpdatedAttributesStartTime <= [ICMConfig sharedConfig].userUpdateDupCacheMaxAge)) {
            NSDictionary *attributes = update.attributes;
            [self savePreviouslyUpdatedUserAttributes:attributes previouslyUpdatedAttributesStartTime:[self currentTimestamp]];
            return NO;
        }
        NSDictionary *merged = [ICMUserUpdateRequest addUserAttributes:update.attributes toAttributes:self.previouslyUpdatedAttributes];
        BOOL duplicate = [merged isEqual:self.previouslyUpdatedAttributes];
        [self savePreviouslyUpdatedUserAttributes:merged previouslyUpdatedAttributesStartTime:self.previouslyUpdatedAttributesStartTime];
        return duplicate;
    }
}
- (void)clearStoredUserUpdates {
    [self savePreviouslyUpdatedUserAttributes:nil previouslyUpdatedAttributesStartTime:0];
    [self storeUserUpdate];
}
- (NSTimeInterval)currentTimestamp { return [[NSDate date] timeIntervalSince1970]; }
- (void)savePreviouslyUpdatedUserAttributes:(NSDictionary *)attributes previouslyUpdatedAttributesStartTime:(NSTimeInterval)time {
    @synchronized (self) {
        BOOL changed = ![attributes isEqual:_previouslyUpdatedAttributes] || time != _previouslyUpdatedAttributesStartTime;
        _previouslyUpdatedAttributes = attributes;
        _previouslyUpdatedAttributesStartTime = time;
        self.hasUnsavedChanges = self.hasUnsavedChanges || changed;
    }
}
+ (instancetype)deduperFromDisk {
    @synchronized (self) { return [ICMPersistenceProxy unarchiveObject:[ICMUserUpdateDeduper class] withFile:[ICMUserUpdateDeduper archivePath]]; }
}
+ (void)saveDeduper:(ICMUserUpdateDeduper *)deduper {
    @synchronized (self) { [ICMPersistenceProxy archiveRootObject:deduper toFile:[ICMUserUpdateDeduper archivePath]]; }
}
+ (NSString *)archivePath { return [[ICMUtils intercomCachesDirectory] stringByAppendingPathComponent:@"updates.icm"]; }
+ (BOOL)supportsSecureCoding { return YES; }
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (self) {
        NSSet *classes = [NSSet setWithObjects:[NSDictionary class], [NSString class], [NSNumber class], [NSDate class], [NSArray class], nil];
        _previouslyUpdatedAttributes = [coder decodeObjectOfClasses:classes forKey:NSStringFromSelector(@selector(previouslyUpdatedAttributes))];
        _previouslyUpdatedAttributesStartTime = [coder decodeDoubleForKey:NSStringFromSelector(@selector(previouslyUpdatedAttributesStartTime))];
    }
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder {
    if (self.previouslyUpdatedAttributes && self.previouslyUpdatedAttributesStartTime != 0) {
        [coder encodeObject:self.previouslyUpdatedAttributes forKey:NSStringFromSelector(@selector(previouslyUpdatedAttributes))];
        [coder encodeDouble:self.previouslyUpdatedAttributesStartTime forKey:NSStringFromSelector(@selector(previouslyUpdatedAttributesStartTime))];
    }
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (!object || ![[object class] isEqual:[self class]]) return NO;
    return [self isEqualToDeduper:object];
}
- (BOOL)isEqualToDeduper:(ICMUserUpdateDeduper *)deduper {
    if (self.previouslyUpdatedAttributesStartTime != deduper.previouslyUpdatedAttributesStartTime) return NO;
    return (!self.previouslyUpdatedAttributes && !deduper.previouslyUpdatedAttributes) || [self.previouslyUpdatedAttributes isEqual:deduper.previouslyUpdatedAttributes];
}
- (NSUInteger)hash { return self.previouslyUpdatedAttributes.hash ^ @(self.previouslyUpdatedAttributesStartTime).hash; }
@end
