#import "JCommonCrashEvent.h"
#import "JCommonCrashStacktrace.h"
@implementation JCommonCrashEvent
@dynamic hash, superclass, description, debugDescription;
- (instancetype)init {
    return [super init];
}
- (NSMutableDictionary *)serialize {
    if (!self.timestamp)
        self.timestamp = [self getIsoDate:[NSDate date]];
    NSMutableDictionary *result =
        [@{@"timestamp" : self.timestamp, @"platform" : self.platform, @"level" : @"error"} mutableCopy];
    [self addOptionalListProperties:result];
    return result;
}
- (id)getIsoDate:(NSDate *)date {
    NSDateFormatter *formatter = [NSDateFormatter new];
    formatter.locale = [NSLocale localeWithLocaleIdentifier:@"en_US_POSIX"];
    formatter.timeZone = [NSTimeZone timeZoneWithAbbreviation:@"UTC"];
    formatter.dateFormat = @"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    return [formatter stringFromDate:date];
}
- (void)addOptionalListProperties:(id)content {
    [self addThreads:content];
    [self addExceptions:content];
    [self addSimpleProperties:content];
}
- (void)addExceptions:(id)content {
    NSMutableArray *values = [NSMutableArray new];
    for (id exception in self.exceptions)
        [values addObject:[exception serialize]];
    if (values.count)
        [content setValue:@{@"values" : values} forKey:@"exception"];
}
- (void)addThreads:(id)content {
    NSMutableArray *values = [NSMutableArray new];
    for (id thread in self.threads)
        [values addObject:[thread serialize]];
    if (values.count)
        [content setValue:@{@"values" : values} forKey:@"threads"];
}
- (void)addSimpleProperties:(id)content {
    [content setValue:self.sdk forKey:@"sdk"];
    [content setValue:self.releaseName forKey:@"release"];
    [content setValue:self.context forKey:@"contexts"];
    [content setValue:self.type forKey:@"type"];
    [content setValue:[self.stacktrace serialize] forKey:@"stacktrace"];
}
@end
