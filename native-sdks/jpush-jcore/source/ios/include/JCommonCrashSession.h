#pragma once
#import "RecoveredModels.h"
@interface JCommonCrashSession : NSObject <JCommonCrashSerializableProtocol, NSCopying> {
    NSNumber *_init;
    NSUUID *_sessionId;
    NSDate *_started;
    NSUInteger _status;
    NSUInteger _errors;
    NSUInteger _sequence;
    NSDate *_timestamp;
    NSString *_releaseName;
    NSString *_distinctId;
    NSNumber *_duration;
}
@property(strong, nonatomic) NSUUID *sessionId;
@property(strong, nonatomic) NSDate *started;
@property(nonatomic) NSUInteger status;
@property(nonatomic) NSUInteger errors;
@property(nonatomic) NSUInteger sequence;
@property(strong, nonatomic) NSDate *timestamp;
@property(copy, nonatomic) NSString *releaseName;
@property(strong, nonatomic) NSString *distinctId;
@property(readonly, nonatomic) NSNumber *duration;
@property(readonly, copy, nonatomic) NSNumber *flagInit;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
- (instancetype)initWithReleaseName:(NSString *)name;
- (instancetype)initDefault;
- (NSMutableDictionary *)serialize;
- (id)copyWithZone:(NSZone *)zone;
@end
