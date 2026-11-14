#import <Foundation/Foundation.h>
@protocol ICMMetricProtocol
- (void)addEvent:(id)event;
@end
@interface ICMMetric : NSObject <NSSecureCoding, ICMMetricProtocol>
@property(nonatomic, strong) NSMutableSet *metricEvents;
@property(nonatomic, strong) NSMutableSet *opsMetricEvents;
+ (instancetype)sharedInstance;
- (void)save;
- (void)addEvent:(id)event;
- (void)addOpsEvent:(id)event;
- (NSSet *)events;
- (NSSet *)opsEvents;
- (void)clearAllEvents;
- (void)clearEvents:(NSSet *)events opsEvents:(NSSet *)opsEvents;
+ (instancetype)currentMetrics;
+ (instancetype)metricsFromDisk;
+ (void)saveMetrics:(ICMMetric *)metrics;
+ (NSString *)archivePath;
- (BOOL)isEqualToMetric:(ICMMetric *)other;
@end
