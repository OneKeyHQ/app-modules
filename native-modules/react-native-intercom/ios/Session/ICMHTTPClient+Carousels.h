#pragma once
#import "ICMHTTPClient.h"
#import "ICMHTTPBridge.h"
#import "ICMCarousel.h"
@class ICMBaseResponse;
@interface ICMHTTPClient (Carousels)
+ (void)getCarouselWithId:(NSString *)identifier success:(void (^)(ICMCarousel *))success error:(void (^)(NSError *))failure;
+ (void)getCarouselWithInstanceId:(NSString *)identifier success:(void (^)(ICMCarousel *))success error:(void (^)(NSError *))failure;
+ (void)recordStatistic:(NSString *)statistic withInstanceId:(NSString *)identifier statsMetadata:(NSDictionary *)metadata success:(void (^)(ICMBaseResponse *))success error:(void (^)(NSError *))failure;
@end
@interface ICMHTTPBridge (Carousels)
+ (void)getCarouselWithId:(NSString *)identifier success:(void (^)(ICMCarousel *))success error:(void (^)(NSError *))failure;
+ (void)getCarouselWithInstanceId:(NSString *)identifier success:(void (^)(ICMCarousel *))success error:(void (^)(NSError *))failure;
+ (void)recordStatistic:(NSString *)statistic withInstanceId:(NSString *)identifier statsMetadata:(NSDictionary *)metadata;
@end
