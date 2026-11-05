#import <Foundation/Foundation.h>
@interface ICMMetricEvent : NSObject <NSSecureCoding> {
    NSUInteger _eventType;
    NSString *_uuid;
    NSString *_owner;
    NSString *_name;
    NSNumber *_createdAt;
    NSDictionary *_metaData;
}
@property(nonatomic) NSUInteger eventType;
@property(nonatomic, copy) NSString *uuid;
@property(nonatomic, copy) NSString *owner;
@property(nonatomic, copy) NSString *name;
@property(nonatomic, strong) NSNumber *createdAt;
@property(nonatomic, strong) NSDictionary *metaData;
+ (instancetype)eventWithAction:(NSString *)action object:(NSString *)object place:(NSString *)place context:(NSString *)context;
+ (instancetype)eventWithAction:(NSString *)action object:(NSString *)object place:(NSString *)place context:(NSString *)context additionalInfo:(NSDictionary *)info;
- (instancetype)initWithAction:(NSString *)action object:(NSString *)object place:(NSString *)place context:(NSString *)context metaData:(NSDictionary *)metadata;
+ (BOOL)isNonEmptyString:(id)value;
- (NSDictionary *)eventDictionary;
+ (NSTimeInterval)currentTimestamp;
+ (NSUUID *)UUID;
- (BOOL)isEqualToMetricEvent:(ICMMetricEvent *)other;
@end
