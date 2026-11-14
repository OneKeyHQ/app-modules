#pragma once
#import "../Identity/ICMSafeNetworkModel.h"
@interface ICMCarouselViewState : ICMSafeNetworkModel <NSSecureCoding> {
    NSInteger _currentIndex;
    NSInteger _nextIndex;
    NSInteger _progressIndex;
}
@property (nonatomic) NSInteger currentIndex;
@property (nonatomic) NSInteger nextIndex;
@property (nonatomic) NSInteger progressIndex;
- (BOOL)isEqualToCarouselViewState:(ICMCarouselViewState *)state;
@end
