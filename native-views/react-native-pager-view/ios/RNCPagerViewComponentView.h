#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import <React/RCTViewComponentView.h>

NS_ASSUME_NONNULL_BEGIN

@protocol RNCPagerViewNestedOverlayObserver <NSObject>
- (void)outerPagerDidUpdateOverlayOwner:(BOOL)active transitioning:(BOOL)transitioning;
@end

@interface RNCPagerViewComponentView : RCTViewComponentView

- (void)addNestedOverlayObserver:(id<RNCPagerViewNestedOverlayObserver>)observer;
- (void)removeNestedOverlayObserver:(id<RNCPagerViewNestedOverlayObserver>)observer;

@end

NS_ASSUME_NONNULL_END
