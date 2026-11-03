#pragma once
#import "RecoveredModels.h"
@interface JCommonCrashEvent : NSObject <JCommonCrashSerializableProtocol> {
    JCommonCrashId *_eventId;
    NSString *_type;
    NSString *_timestamp;
    NSString *_startTimestamp;
    NSString *_platform;
    NSString *_releaseName;
    NSDictionary *_sdk;
    NSDictionary *_user;
    NSDictionary *_context;
    NSArray *_exceptions;
    NSArray *_threads;
    JCommonCrashStacktrace *_stacktrace;
}
@property(strong, nonatomic) JCommonCrashId *eventId;
@property(strong, nonatomic) NSString *type;
@property(strong, nonatomic) NSString *timestamp;
@property(strong, nonatomic) NSString *startTimestamp;
@property(copy, nonatomic) NSString *platform;
@property(copy, nonatomic) NSString *releaseName;
@property(strong, nonatomic) NSDictionary *sdk;
@property(strong, nonatomic) NSDictionary *user;
@property(strong, nonatomic) NSDictionary *context;
@property(strong, nonatomic) NSArray *exceptions;
@property(strong, nonatomic) NSArray *threads;
@property(strong, nonatomic) JCommonCrashStacktrace *stacktrace;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
- (NSMutableDictionary *)serialize;
- (id)getIsoDate:(NSDate *)date;
- (void)addOptionalListProperties:(id)content;
- (void)addExceptions:(id)content;
- (void)addThreads:(id)content;
- (void)addSimpleProperties:(id)content;
@end
