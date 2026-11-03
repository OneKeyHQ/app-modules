#import "JCommonCrashStacktrace.h"
@implementation JCommonCrashStacktrace
@dynamic hash, superclass, description, debugDescription;
- (void)fixDuplicateFrames {
    if (self.frames.count < 2)
        return;
    id last = self.frames.lastObject;
    id previous = self.frames[self.frames.count - 2];
    if ([last isKindOfClass:[JCommonCrashFrame class]] &&
        [[last symbolAddress] isEqualToString:[previous symbolAddress]]) {
        NSMutableArray *frames = [self.frames mutableCopy];
        [frames removeObjectAtIndex:self.frames.count - 2];
        self.frames = frames;
    }
}
- (NSMutableDictionary *)serialize {
    NSMutableDictionary *result = [NSMutableDictionary new];
    NSMutableArray *frames = [NSMutableArray new];
    for (id frame in self.frames)
        if ([[frame serialize] allKeys].count)
            [frames addObject:[frame serialize]];
    if (frames.count)
        [result setValue:frames forKey:@"frames"];
    return result;
}
@end
