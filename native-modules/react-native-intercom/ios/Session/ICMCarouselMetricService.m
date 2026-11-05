#import "ICMCarouselMetricService.h"
#import "ICMMetricEvent.h"
#import "ICMMetric.h"
@implementation ICMCarouselMetricService
@synthesize metricEventSource = _metricEventSource, carouselInstanceId = _carouselInstanceId, source = _source;
+ (instancetype)sharedInstance {
    static ICMCarouselMetricService *instance;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ instance = [ICMCarouselMetricService new]; });
    return instance;
}
- (NSString *)carouselInstanceId { return _carouselInstanceId ?: @""; }
- (void)setSource:(NSInteger)source {
    _source = source;
    self.metricEventSource = source == 1 ? @"programmatic" : @"automatic";
}
+ (void)addCarouselEvent:(ICMMetricEvent *)event {
    event.eventType = 2;
    [[ICMMetric sharedInstance] addEvent:event];
}
+ (NSString *)metricContextForPermissionType:(NSInteger)type {
    if ((NSUInteger)type > 5) { return @"push_notifications_permission"; }
    static NSString *const contexts[] = {@"unknown_permission", @"push_notifications_permission", @"location_permission", @"microphone_permission", @"camera_permission", @"photos_permission"};
    return contexts[(NSUInteger)type];
}
+ (NSString *)metricEventContextFromUserActionContext:(NSInteger)context {
    switch (context) {
        case 1: return @"from_permission";
        case 2: return @"from_permission_skipped";
        case 3: return @"from_close_button";
        case 4: return @"from_swipe";
        default: return @"from_cta";
    }
}
- (void)addEventForCarouselShownWithNumberOfScreensInMessage:(NSInteger)count numberShown:(NSInteger)shown {
    NSDictionary *info = @{@"screens_in_message": @(count), @"screens_shown": @(shown), @"instance_id": self.carouselInstanceId, @"source": self.metricEventSource ?: @"unknown_source"};
    ICMMetricEvent *event = [ICMMetricEvent eventWithAction:@"opened" object:@"carousel_message" place:@"carousel_message" context:@"from_carousel" additionalInfo:info];
    [ICMCarouselMetricService addCarouselEvent:event];
}
- (void)addEventForCarouselDismissedWithNumberOfScreensInMessage:(NSInteger)count numberShown:(NSInteger)shown numberSeen:(NSInteger)seen context:(NSInteger)context {
    NSDictionary *info = @{@"screens_in_message": @(count), @"screens_shown": @(shown), @"screens_seen": @(seen), @"instance_id": self.carouselInstanceId, @"source": self.metricEventSource ?: @"unknown_source"};
    NSString *eventContext = [ICMCarouselMetricService metricEventContextFromUserActionContext:context];
    ICMMetricEvent *event = [ICMMetricEvent eventWithAction:@"dismissed" object:@"carousel_message" place:@"carousel_message" context:eventContext additionalInfo:info];
    [ICMCarouselMetricService addCarouselEvent:event];
}
- (void)addEventForCarouselCompletedWithNumberOfScreensInMessage:(NSInteger)count numberShown:(NSInteger)shown context:(NSInteger)context {
    NSString *eventContext = [ICMCarouselMetricService metricEventContextFromUserActionContext:context];
    NSDictionary *info = @{@"screens_in_message": @(count), @"screens_shown": @(shown), @"instance_id": self.carouselInstanceId, @"source": self.metricEventSource ?: @"unknown_source"};
    ICMMetricEvent *event = [ICMMetricEvent eventWithAction:@"completed" object:@"carousel_message" place:@"carousel_message" context:eventContext additionalInfo:info];
    [ICMCarouselMetricService addCarouselEvent:event];
}
- (void)addEventForCarouselDismissedWhileFetchingWithCarouselId:(NSString *)identifier context:(NSInteger)context {
    NSString *eventContext = [ICMCarouselMetricService metricEventContextFromUserActionContext:context];
    NSDictionary *info = @{@"requested_content_id": identifier, @"source": self.metricEventSource ?: @"unknown_source"};
    ICMMetricEvent *event = [ICMMetricEvent eventWithAction:@"dismissed" object:@"carousel_message_preload" place:@"carousel_message" context:eventContext additionalInfo:info];
    [ICMCarouselMetricService addCarouselEvent:event];
}
- (void)addEventForErrorWhileFetchingACarouselWithCarouselId:(NSString *)identifier statusCode:(NSNumber *)code {
    NSDictionary *info = @{@"requested_content_id": identifier, @"source": self.metricEventSource ?: @"unknown_source", @"error_code": code};
    ICMMetricEvent *event = [ICMMetricEvent eventWithAction:@"failed" object:@"carousel_message_preload" place:@"carousel_message" context:@"from_carousel" additionalInfo:info];
    [ICMCarouselMetricService addCarouselEvent:event];
}
- (void)addEventForFetchingACarouselWithCarouselId:(NSString *)identifier {
    NSDictionary *info = @{@"requested_content_id": identifier, @"source": self.metricEventSource ?: @"unknown_source"};
    ICMMetricEvent *event = [ICMMetricEvent eventWithAction:@"started" object:@"carousel_message_preload" place:@"carousel_message" context:@"from_carousel" additionalInfo:info];
    [ICMCarouselMetricService addCarouselEvent:event];
}
- (void)addEventForCarouselRetryWithCarouselId:(NSString *)identifier {
    NSDictionary *info = @{@"requested_content_id": identifier, @"source": self.metricEventSource ?: @"unknown_source"};
    ICMMetricEvent *event = [ICMMetricEvent eventWithAction:@"clicked" object:@"carousel_message_preload" place:@"carousel_message" context:@"from_retry" additionalInfo:info];
    [ICMCarouselMetricService addCarouselEvent:event];
}
- (void)addEventForFetchingACarouselWithInstanceId:(NSString *)identifier {
    self.source = 0;
    NSDictionary *info = @{@"requested_instance_id": identifier, @"source": self.metricEventSource ?: @"unknown_source"};
    ICMMetricEvent *event = [ICMMetricEvent eventWithAction:@"started" object:@"carousel_message_preload" place:@"carousel_message" context:@"from_carousel" additionalInfo:info];
    [ICMCarouselMetricService addCarouselEvent:event];
}
- (void)addEventForErrorWhileFetchingACarouselWithInstanceId:(NSString *)identifier statusCode:(NSNumber *)code {
    NSDictionary *info = @{@"requested_instance_id": identifier, @"source": self.metricEventSource ?: @"unknown_source", @"error_code": code};
    ICMMetricEvent *event = [ICMMetricEvent eventWithAction:@"failed" object:@"carousel_message_preload" place:@"carousel_message" context:@"from_carousel" additionalInfo:info];
    [ICMCarouselMetricService addCarouselEvent:event];
}
- (void)addEventForPermissionRequestViaSystemDialog:(NSInteger)type approved:(BOOL)approved {
    [self addEventForPermissionRequest:type withObject:@"carousel_permission_dialog" approved:approved];
}
- (void)addEventForPermissionRequestViaSettings:(NSInteger)type approved:(BOOL)approved {
    [self addEventForPermissionRequest:type withObject:@"carousel_permission_settings" approved:approved];
}
- (void)addEventForPermissionRequestSkipped:(NSInteger)type {
    NSString *context = [ICMCarouselMetricService metricContextForPermissionType:type];
    NSDictionary *info = @{@"instance_id": self.carouselInstanceId, @"source": self.metricEventSource ?: @"unknown_source"};
    ICMMetricEvent *event = [ICMMetricEvent eventWithAction:@"clicked" object:@"carousel_permission_skipped" place:@"carousel_message" context:context additionalInfo:info];
    [ICMCarouselMetricService addCarouselEvent:event];
}
- (void)addEventForPermissionRequest:(NSInteger)type withObject:(NSString *)object approved:(BOOL)approved {
    NSString *context = [ICMCarouselMetricService metricContextForPermissionType:type];
    NSDictionary *info = @{@"permission_granted": @(approved), @"instance_id": self.carouselInstanceId, @"source": self.metricEventSource ?: @"unknown_source"};
    ICMMetricEvent *event = [ICMMetricEvent eventWithAction:@"clicked" object:object place:@"carousel_message" context:context additionalInfo:info];
    [ICMCarouselMetricService addCarouselEvent:event];
}
@end
