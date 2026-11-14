#import <Foundation/Foundation.h>
@interface ICMOpsMetricEvent : NSObject <NSSecureCoding>
@property(nonatomic, copy) NSString *type;
@property(nonatomic, copy) NSString *name;
@property(nonatomic, copy) NSNumber *value;
+ (instancetype)eventWithType:(NSString *)type name:(NSString *)name value:(NSNumber *)value;
- (instancetype)eventWithType:(NSString *)type name:(NSString *)name value:(NSNumber *)value;
+ (BOOL)isNonEmptyString:(id)value;
+ (BOOL)isNonNullNumber:(id)value;
- (NSDictionary *)eventDictionary;
- (BOOL)isEqualToMetricEvent:(ICMOpsMetricEvent *)other;
@end
