#pragma once
#import "ICMInAppAttributes.h"
#import "ICMReactions.h"
@class ITBAnimatedImageView;
@interface ICMReactionsViewController : UIViewController <UIGestureRecognizerDelegate>
@property (nonatomic, strong, readonly) ICMReactionsSet *reactionSet;
@property (nonatomic, weak) id<ICMReactionsViewControllerDelegate> delegate;
@property (nonatomic, strong) NSArray *optionViews;
@property (nonatomic, strong) UIView *topSeparator;
@property (nonatomic, strong) ITBAnimatedImageView *selectedView;
@property (nonatomic, strong) UIColor *separatorColor;
@property (nonatomic, strong) UILabel *reactionsLabel;
- (instancetype)initWithReactionSet:(ICMReactionsSet *)set separatorColor:(UIColor *)color reactionsLabel:(UILabel *)label delegate:(id<ICMReactionsViewControllerDelegate>)delegate;
- (void)displayOption:(ICMReactionsOption *)option inView:(ITBAnimatedImageView *)view;
- (void)updateReactionViews;
- (UIImage *)grayscaleEmoji:(ICMReactionsOption *)option inView:(ITBAnimatedImageView *)view;
- (void)didPan:(UILongPressGestureRecognizer *)gesture;
- (BOOL)touchPointShouldCancelSelection:(CGPoint)point;
- (void)determineSelectedView;
- (void)resetSelectedView;
- (void)updateViewsWithBigSelectedView:(BOOL)big;
- (NSString *)grayscaleImageCacheKey:(NSString *)key;
- (void)performSpringAnimation:(void (^)(void))animation;
- (void)prepareForAnimatingIn;
- (void)animateInOptions;
@end
