#import "ITBBlockView.h"
@interface ITBTypingBlock : ITBBlock {
    UIColor *_dotColor;
}
@property (nonatomic, strong) UIColor *dotColor;
@end
@interface ITBTypingDotView : UIView {
    CAAnimationGroup *_animationGroup;
}
@property (nonatomic, strong) CAAnimationGroup *animationGroup;
- (instancetype)initWithAnimationDelay:(double)delay dotSize:(CGFloat)size;
- (void)startAnimating;
- (void)stopAnimating;
- (void)cleanUp;
- (void)animationDidStop:(CAAnimation *)animation finished:(BOOL)finished;
@end
@interface ITBTypingView : UIView {
    NSArray *_typingDots;
    CGFloat _dotPadding;
}
@property (nonatomic, strong) NSArray *typingDots;
@property (nonatomic) CGFloat dotPadding;
- (instancetype)initWithFrame:(CGRect)frame dotSize:(CGFloat)size dotPadding:(CGFloat)padding;
@end
@interface ITBTypingBlockView : ITBBlockView {
    ITBTypingView *_typingView;
}
@property (nonatomic, strong) ITBTypingView *typingView;
@end
