// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBAttachmentBlockView.h"
#import "ITBAttachmentBlock.h"
#import "ITBBlockViewsReuseManager.h"
#import <math.h>
@implementation ITBAttachmentBlockView
@synthesize clipView = _clipView, clipLeftPadding = _clipLeftPadding;
- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.clipView = [ITBAttachmentIconView new];
        [self addSubview:self.clipView];
        self.isAccessibilityElement = YES;
        self.accessibilityTraits = UIAccessibilityTraitLink;
    }
    return self;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    CGFloat x;
    if ([UIApplication sharedApplication].userInterfaceLayoutDirection == 0) x = self.clipLeftPadding;
    else x = CGRectGetWidth(self.bounds) - 12 - self.clipLeftPadding;
    CGFloat y = floorf((float)((CGRectGetHeight(self.frame) - 16) * 0.5));
    self.clipView.frame = CGRectMake(x, y, 12, 16);
}
- (void)configureWithBlock:(ITBBlock *)value {
    [super configureWithBlock:value];
    ITBAttachmentBlock *block = (ITBAttachmentBlock *)value;
    self.clipLeftPadding = block.clipMargin + 4;
    self.clipView.clipColor = block.clipColor;
    self.accessibilityLabel = self.accessibilityText;
}
@end

@implementation ITBAttachmentIconView
@synthesize clipColor = _clipColor;
- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) self.opaque = NO;
    return self;
}
- (void)setClipColor:(UIColor *)color {
    if (![color isEqual:_clipColor]) { _clipColor = color; [self setNeedsDisplay]; }
}
- (void)drawRect:(CGRect)rect {
    UIColor *color = self.clipColor ?: [UIColor colorWithWhite:0.333 alpha:1];
    UIBezierPath *path = [UIBezierPath bezierPath];
    [path moveToPoint:CGPointMake(CGRectGetMaxX(rect) * 0.41000000000000003, CGRectGetMaxY(rect) * 0.35000000000000003)];
    [path addLineToPoint:CGPointMake(CGRectGetMaxX(rect) * 0.41000000000000003, CGRectGetMaxY(rect) * 0.76)];
    [path addArcWithCenter:CGPointMake(CGRectGetMaxX(rect) * 0.48, CGRectGetMaxY(rect) * 0.76) radius:CGRectGetMaxX(rect) * 0.07 startAngle:3.141592653589793 endAngle:0 clockwise:NO];
    [path addLineToPoint:CGPointMake(CGRectGetMaxX(rect) * 0.55, CGRectGetMaxY(rect) * 0.17)];
    [path addArcWithCenter:CGPointMake(CGRectGetMaxX(rect) * 0.41000000000000003, CGRectGetMaxY(rect) * 0.17) radius:CGRectGetMaxX(rect) * 0.14 startAngle:0 endAngle:3.141592653589793 clockwise:NO];
    [path addLineToPoint:CGPointMake(CGRectGetMaxX(rect) * 0.27, CGRectGetMaxY(rect) * 0.15)];
    [path addLineToPoint:CGPointMake(CGRectGetMaxX(rect) * 0.27, CGRectGetMaxY(rect) * 0.76)];
    [path addArcWithCenter:CGPointMake(CGRectGetMaxX(rect) * 0.48, CGRectGetMaxY(rect) * 0.76) radius:CGRectGetMaxX(rect) * 0.21 startAngle:3.141592653589793 endAngle:0 clockwise:NO];
    [path addLineToPoint:CGPointMake(CGRectGetMaxX(rect) * 0.6900000000000001, CGRectGetMaxY(rect) * 0.15)];
    [color setStroke];
    path.lineWidth = 1;
    [path stroke];
}
@end

@implementation ITBAttachmentListBlockView
@synthesize attachmentViews = _attachmentViews;
- (void)prepareForReuse {
    [super prepareForReuse];
    for (ITBBlockView *view in self.attachmentViews) {
        [view removeFromSuperview];
        [[ITBBlockViewsReuseManager sharedInstance] enqueueReusableViewForReuse:view];
    }
    self.attachmentViews = nil;
}
- (void)configureWithBlock:(ITBBlock *)value {
    [super configureWithBlock:value];
    if (self.attachmentViews) {
        for (ITBBlockView *view in self.attachmentViews) {
            [view removeFromSuperview];
            [[ITBBlockViewsReuseManager sharedInstance] enqueueReusableViewForReuse:view];
        }
        self.attachmentViews = nil;
    }
    ITBAttachmentListBlock *block = (ITBAttachmentListBlock *)value;
    NSMutableArray *views = [NSMutableArray new];
    for (NSUInteger index = 0; index < block.attachments.count; ++index) {
        ITBBlock *attachment = block.attachments[index];
        ITBBlockView *view = [[ITBBlockViewsReuseManager sharedInstance] dequeueReusableViewForBlock:attachment];
        [view configureWithBlock:attachment];
        [self addSubview:view];
        [views addObject:view];
    }
    self.attachmentViews = views;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    CGFloat y = 0;
    for (NSUInteger index = 0; index < self.attachmentViews.count; ++index) {
        ITBBlockView *view = self.attachmentViews[index];
        CGFloat width = view.block.estimatedSize.width;
        CGFloat height = view.block.estimatedSize.height;
        view.frame = CGRectMake(0, y, width, height);
        y += view.block.estimatedSize.height;
    }
}
+ (CGSize)estimateSizeForBlock:(ITBBlock *)value withWidth:(CGFloat)width constraintWidth:(BOOL)constraintWidth {
    ITBAttachmentListBlock *block = (ITBAttachmentListBlock *)value;
    CGFloat totalHeight = 0, maximumWidth = 0;
    for (ITBBlock *attachment in block.attachments) {
        attachment.estimatedSize = [attachment.viewClass estimateSizeForBlock:attachment withWidth:width constraintWidth:constraintWidth];
        totalHeight += attachment.estimatedSize.height;
        CGFloat itemWidth = attachment.estimatedSize.width;
        if (maximumWidth < itemWidth) maximumWidth = itemWidth;
    }
    return CGSizeMake(maximumWidth, totalHeight);
}
@end
