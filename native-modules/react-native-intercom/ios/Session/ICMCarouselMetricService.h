#pragma once
#import <Foundation/Foundation.h>
@class ICMMetricEvent;
@interface ICMCarouselMetricService : NSObject {
    NSString *_carouselInstanceId;
    NSInteger _source;
    NSString *_metricEventSource;
}
@property (nonatomic, strong) NSString *metricEventSource;
@property (nonatomic, copy) NSString *carouselInstanceId;
@property (nonatomic) NSInteger source;
+ (instancetype)sharedInstance;
+ (void)addCarouselEvent:(ICMMetricEvent *)event;
+ (NSString *)metricContextForPermissionType:(NSInteger)type;
+ (NSString *)metricEventContextFromUserActionContext:(NSInteger)context;
- (void)addEventForCarouselShownWithNumberOfScreensInMessage:(NSInteger)count numberShown:(NSInteger)shown;
- (void)addEventForCarouselDismissedWithNumberOfScreensInMessage:(NSInteger)count numberShown:(NSInteger)shown numberSeen:(NSInteger)seen context:(NSInteger)context;
- (void)addEventForCarouselCompletedWithNumberOfScreensInMessage:(NSInteger)count numberShown:(NSInteger)shown context:(NSInteger)context;
- (void)addEventForCarouselDismissedWhileFetchingWithCarouselId:(NSString *)identifier context:(NSInteger)context;
- (void)addEventForErrorWhileFetchingACarouselWithCarouselId:(NSString *)identifier statusCode:(NSNumber *)code;
- (void)addEventForFetchingACarouselWithCarouselId:(NSString *)identifier;
- (void)addEventForCarouselRetryWithCarouselId:(NSString *)identifier;
- (void)addEventForFetchingACarouselWithInstanceId:(NSString *)identifier;
- (void)addEventForErrorWhileFetchingACarouselWithInstanceId:(NSString *)identifier statusCode:(NSNumber *)code;
- (void)addEventForPermissionRequestViaSystemDialog:(NSInteger)type approved:(BOOL)approved;
- (void)addEventForPermissionRequestViaSettings:(NSInteger)type approved:(BOOL)approved;
- (void)addEventForPermissionRequestSkipped:(NSInteger)type;
- (void)addEventForPermissionRequest:(NSInteger)type withObject:(NSString *)object approved:(BOOL)approved;
@end
