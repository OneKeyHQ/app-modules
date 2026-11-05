#pragma once
#import "../Identity/ICMSafeNetworkModel.h"
#import "ICMCarouselViewState.h"
@class ICMScreen;
@interface ICMCarousel : ICMSafeNetworkModel <NSSecureCoding> {
    BOOL _dismissible;
    BOOL _fetchedFromCache;
    NSString *_carouselId;
    NSArray<ICMScreen *> *_screens;
    NSString *_instanceId;
    ICMCarouselViewState *_viewState;
}
@property (nonatomic, strong) NSString *carouselId;
@property (nonatomic, strong) NSArray<ICMScreen *> *screens;
@property (nonatomic) BOOL dismissible;
@property (nonatomic, copy) NSString *instanceId;
@property (nonatomic, strong) ICMCarouselViewState *viewState;
@property (nonatomic, getter=wasFetchedFromCache) BOOL fetchedFromCache;
@property (nonatomic, readonly) NSInteger carouselType;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
- (void)setCarouselScreens:(NSArray *)screens;
- (BOOL)isEqualToCarousel:(ICMCarousel *)carousel;
@end
