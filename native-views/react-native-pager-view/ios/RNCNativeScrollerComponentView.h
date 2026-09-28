#import <React/RCTViewComponentView.h>

NS_ASSUME_NONNULL_BEGIN

@interface RNCNativeScrollerComponentView : RCTViewComponentView
@property (nonatomic, readonly) UIScrollView *scrollView;
- (void)stopScrolling;
- (void)setPagerStickyHeight:(CGFloat)height forOwner:(id)owner;
@end

// Native inset ownership stays separate from Fabric/Yoga content layout.
@protocol RNCNativeScrollerInsetCoordinator <NSObject>
- (void)nativeScroller:(RNCNativeScrollerComponentView *)scroller
    callerInsetChangedFrom:(UIEdgeInsets)previous to:(UIEdgeInsets)next;
@end

NS_ASSUME_NONNULL_END
