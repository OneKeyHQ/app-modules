#pragma once
#import "RecoveredModels.h"
@interface JCommonCrashStacktrace : NSObject <JCommonCrashSerializableProtocol> {
    NSArray *_frames;
}
@property(strong, nonatomic) NSArray *frames;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
- (void)fixDuplicateFrames;
- (NSMutableDictionary *)serialize;
@end
