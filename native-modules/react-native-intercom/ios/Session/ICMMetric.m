#import "ICMMetric.h"
#import "ICMMetricEvent.h"
#import "ICMOpsMetricEvent.h"
#import "ICMConfig.h"
#import "../Identity/ICMPersistenceProxy.h"
#import "../Identity/ICMUtils.h"

@implementation ICMMetric
+ (instancetype)sharedInstance {
    static ICMMetric *shared;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ shared = [self currentMetrics]; });
    return shared;
}
- (void)save {
    ICMMetric *snapshot = [ICMMetric new];
    @synchronized ([self class]) {
        if ([self.metricEvents isKindOfClass:[NSSet class]]) snapshot.metricEvents = [self.metricEvents mutableCopy];
        if ([self.opsMetricEvents isKindOfClass:[NSSet class]]) snapshot.opsMetricEvents = [self.opsMetricEvents mutableCopy];
    }
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^{ [ICMMetric saveMetrics:snapshot]; });
}
- (void)addEvent:(id)event {
    @synchronized ([self class]) {
        if ([ICMConfig sharedConfig].metricsEnabled && [event isKindOfClass:[ICMMetricEvent class]]) {
            if (![self.metricEvents isKindOfClass:[NSMutableSet class]]) self.metricEvents = [NSMutableSet set];
            [self.metricEvents addObject:event];
            [self save];
        }
    }
}
- (void)addOpsEvent:(id)event {
    @synchronized ([self class]) {
        if ([ICMConfig sharedConfig].metricsEnabled && [event isKindOfClass:[ICMOpsMetricEvent class]]) {
            if (![self.opsMetricEvents isKindOfClass:[NSMutableSet class]]) self.opsMetricEvents = [NSMutableSet set];
            [self.opsMetricEvents addObject:event];
            [self save];
        }
    }
}
- (NSSet *)events {
    @synchronized ([self class]) {
        if (![self.metricEvents isKindOfClass:[NSMutableSet class]]) self.metricEvents = [NSMutableSet set];
        return [self.metricEvents copy];
    }
}
- (NSSet *)opsEvents {
    @synchronized ([self class]) {
        if (![self.opsMetricEvents isKindOfClass:[NSMutableSet class]]) self.opsMetricEvents = [NSMutableSet set];
        return [self.opsMetricEvents copy];
    }
}
- (void)clearAllEvents {
    @synchronized ([self class]) {
        if (self.metricEvents.count) { self.metricEvents = [NSMutableSet set]; [self save]; }
        if (self.opsMetricEvents.count) { self.opsMetricEvents = [NSMutableSet set]; [self save]; }
    }
}
- (void)clearEvents:(NSSet *)events opsEvents:(NSSet *)opsEvents {
    @synchronized ([self class]) {
        if (self.metricEvents.count && events.count) { [self.metricEvents minusSet:events]; [self save]; }
        if (self.opsMetricEvents.count && opsEvents.count) { [self.opsMetricEvents minusSet:opsEvents]; [self save]; }
    }
}
+ (instancetype)currentMetrics { return [self metricsFromDisk] ?: [ICMMetric new]; }
+ (instancetype)metricsFromDisk {
    @synchronized (self) {
        NSSet *classes = [NSSet setWithObjects:[ICMMetric class], [NSSet class], [ICMMetricEvent class], [ICMOpsMetricEvent class], nil];
        return [ICMPersistenceProxy unarchiveObjectOfClasses:classes withFile:[ICMMetric archivePath]];
    }
}
+ (void)saveMetrics:(ICMMetric *)metrics { [ICMPersistenceProxy archiveRootObject:metrics toFile:[ICMMetric archivePath]]; }
+ (NSString *)archivePath { return [[ICMUtils intercomDocumentsDirectory] stringByAppendingPathComponent:@"env.icm"]; }
+ (BOOL)supportsSecureCoding { return YES; }
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (self) {
        id events = [coder decodeObjectOfClasses:[NSSet setWithObjects:[NSSet class], [ICMMetricEvent class], nil] forKey:NSStringFromSelector(@selector(metricEvents))];
        if ([events isKindOfClass:[NSSet class]]) self.metricEvents = [events mutableCopy];
        id ops = [coder decodeObjectOfClasses:[NSSet setWithObjects:[NSSet class], [ICMOpsMetricEvent class], nil] forKey:NSStringFromSelector(@selector(opsMetricEvents))];
        if ([ops isKindOfClass:[NSSet class]]) self.opsMetricEvents = [ops mutableCopy];
    }
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.metricEvents forKey:NSStringFromSelector(@selector(metricEvents))];
    [coder encodeObject:self.opsMetricEvents forKey:NSStringFromSelector(@selector(opsMetricEvents))];
}
- (BOOL)isEqual:(id)other {
    if (other == self) return YES;
    if (!other || ![[other class] isEqual:[self class]]) return NO;
    return [self isEqualToMetric:other];
}
- (BOOL)isEqualToMetric:(ICMMetric *)other {
    if (!((!self.events && !other.events) || [self.events isEqual:other.events])) return NO;
    return (!self.opsEvents && !other.opsEvents) || [self.opsEvents isEqual:other.opsEvents];
}
- (NSUInteger)hash { return self.events.hash; }
@end
