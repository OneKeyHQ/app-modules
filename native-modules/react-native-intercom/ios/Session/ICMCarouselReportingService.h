#pragma once
#import <Foundation/Foundation.h>
@class ICMCarousel;
@interface ICMCarouselReportingService : NSObject {
    BOOL _hasRecordedCarouselCompletedStat;
    NSString *_carouselInstanceId;
    NSString *_createdViaMetadata;
}
@property (nonatomic) BOOL hasRecordedCarouselCompletedStat;
@property (nonatomic, copy) NSString *carouselInstanceId;
@property (nonatomic, copy) NSString *createdViaMetadata;
+ (instancetype)sharedInstance;
- (void)carouselCompletedStatePreviouslyRecorded;
- (void)configureForCarousel:(ICMCarousel *)carousel;
- (void)recordCarouselCompletedStat;
- (void)recordCarouselDismissedStat;
- (void)recordScreenViewedStat:(NSString *)screenId;
- (void)recordPermissionGrantedStat:(NSString *)screenId;
- (void)recordActionButtonTappedStat:(NSString *)screenId;
- (void)recordCarouselStatistic:(NSString *)statistic withInstanceId:(NSString *)identifier statsMetadata:(NSDictionary *)metadata;
- (NSDictionary *)metadataWithAdditionalValues:(NSDictionary *)values;
- (NSString *)createdViaStringFromCarouselType:(NSInteger)type;
@end
