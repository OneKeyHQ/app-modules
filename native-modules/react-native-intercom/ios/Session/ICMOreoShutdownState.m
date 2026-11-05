#import "ICMOreoShutdownState.h"
#import "../Identity/ICMPersistenceProxy.h"
#import "../Identity/ICMUtils.h"

@implementation ICMOreoShutdownState
@synthesize endTime = _endTime, appFingerprint = _appFingerprint, shutdownReason = _shutdownReason;
- (instancetype)initWithShutdownPeriod:(NSTimeInterval)period shutdownReason:(NSString *)reason {
    if ((self = [super init])) {
        _appFingerprint = [ICMAppInstallationFingerprint new];
        _endTime = [[[self class] currentTime] dateByAddingTimeInterval:period];
        _shutdownReason = reason;
    }
    return self;
}
- (BOOL)canSendOreoNetworkRequestsWithFingerprint:(ICMAppInstallationFingerprint *)fingerprint {
    if ([[[self class] currentTime] compare:self.endTime] != NSOrderedAscending) return YES;
    return ![fingerprint isEqual:self.appFingerprint];
}
- (NSError *)shutdownError {
    return [NSError errorWithDomain:@"io.intercom.ios.error.network.shutdown" code:0 userInfo:@{NSLocalizedDescriptionKey: self.shutdownReason ?: @""}];
}
+ (instancetype)loadFromDisk {
    @synchronized (self) {
        ICMOreoShutdownState *state = [ICMPersistenceProxy unarchiveObject:[ICMOreoShutdownState class] withFile:[ICMOreoShutdownState archivePath]];
        return state ?: [[self alloc] initWithShutdownPeriod:0 shutdownReason:@""];
    }
}
- (void)saveToDisk {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^{
        [ICMOreoShutdownState saveShutdownState:self];
    });
}
+ (void)saveShutdownState:(ICMOreoShutdownState *)state {
    @synchronized (self) {
        [ICMPersistenceProxy archiveRootObject:state toFile:[ICMOreoShutdownState archivePath]];
    }
}
+ (NSString *)archivePath { return [[ICMUtils intercomDocumentsDirectory] stringByAppendingPathComponent:@"shutdown.icm"]; }
+ (NSDate *)currentTime { return [NSDate date]; }
+ (BOOL)supportsSecureCoding { return YES; }
- (instancetype)initWithCoder:(NSCoder *)coder {
    if ((self = [super init])) {
        _endTime = [coder decodeObjectOfClass:[NSDate class] forKey:NSStringFromSelector(@selector(endTime))];
        _appFingerprint = [coder decodeObjectOfClass:[ICMAppInstallationFingerprint class] forKey:NSStringFromSelector(@selector(appFingerprint))];
        _shutdownReason = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(shutdownReason))];
    }
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:_endTime forKey:NSStringFromSelector(@selector(endTime))];
    [coder encodeObject:_appFingerprint forKey:NSStringFromSelector(@selector(appFingerprint))];
    [coder encodeObject:_shutdownReason forKey:NSStringFromSelector(@selector(shutdownReason))];
}
- (BOOL)isEqual:(id)object {
    return object == self || (object && [[object class] isEqual:[self class]] && [self isEqualToShutdownState:object]);
}
- (BOOL)isEqualToShutdownState:(ICMOreoShutdownState *)other {
    return [self.endTime isEqualToDate:other.endTime] && [self.appFingerprint isEqual:other.appFingerprint] && [self.shutdownReason isEqualToString:other.shutdownReason];
}
- (NSUInteger)hash { return self.endTime.hash ^ self.appFingerprint.hash ^ self.shutdownReason.hash; }
@end
