#import "ICMCarouselReportingService.h"
#import "ICMCarousel.h"
#import "ICMHTTPClient+Carousels.h"
@implementation ICMCarouselReportingService
@synthesize hasRecordedCarouselCompletedStat = _hasRecordedCarouselCompletedStat, carouselInstanceId = _carouselInstanceId, createdViaMetadata = _createdViaMetadata;
+ (instancetype)sharedInstance {
    static ICMCarouselReportingService *instance;
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        instance = [ICMCarouselReportingService new];
        instance.createdViaMetadata = @"";
    });
    return instance;
}
- (void)carouselCompletedStatePreviouslyRecorded { self.hasRecordedCarouselCompletedStat = YES; }
- (void)configureForCarousel:(ICMCarousel *)carousel {
    self.carouselInstanceId = carousel.instanceId;
    self.createdViaMetadata = [self createdViaStringFromCarouselType:carousel.carouselType];
    self.hasRecordedCarouselCompletedStat = NO;
}
- (NSString *)createdViaStringFromCarouselType:(NSInteger)type {
    if (type == 0) return @"shown_automatically";
    if (type == 1) return @"triggered_from_code";
    return @"";
}
- (void)recordCarouselCompletedStat {
    if (self.hasRecordedCarouselCompletedStat) return;
    [self recordCarouselStatistic:@"carousel_completed" withInstanceId:self.carouselInstanceId statsMetadata:nil];
    self.hasRecordedCarouselCompletedStat = YES;
}
- (void)recordCarouselDismissedStat {
    if (!self.hasRecordedCarouselCompletedStat) [self recordCarouselStatistic:@"carousel_dismissed" withInstanceId:self.carouselInstanceId statsMetadata:nil];
}
- (void)recordScreenViewedStat:(NSString *)screenId {
    NSString *identifier = self.carouselInstanceId;
    NSDictionary *metadata = @{@"screen_id": screenId};
    [self recordCarouselStatistic:@"carousel_screen_viewed" withInstanceId:identifier statsMetadata:metadata];
}
- (void)recordPermissionGrantedStat:(NSString *)screenId {
    NSString *identifier = self.carouselInstanceId;
    NSDictionary *metadata = @{@"screen_id": screenId};
    [self recordCarouselStatistic:@"carousel_permission_granted" withInstanceId:identifier statsMetadata:metadata];
}
- (void)recordActionButtonTappedStat:(NSString *)screenId {
    NSString *identifier = self.carouselInstanceId;
    NSDictionary *metadata = @{@"screen_id": screenId};
    [self recordCarouselStatistic:@"carousel_button_action_tapped" withInstanceId:identifier statsMetadata:metadata];
}
- (void)recordCarouselStatistic:(NSString *)statistic withInstanceId:(NSString *)identifier statsMetadata:(NSDictionary *)metadata {
    NSDictionary *values = [self metadataWithAdditionalValues:metadata];
    [ICMHTTPBridge recordStatistic:statistic withInstanceId:identifier statsMetadata:values];
}
- (NSDictionary *)metadataWithAdditionalValues:(NSDictionary *)values {
    NSMutableDictionary *metadata = [@{@"created_via": self.createdViaMetadata} mutableCopy];
    if (values != nil) [metadata addEntriesFromDictionary:values];
    return metadata;
}
@end
