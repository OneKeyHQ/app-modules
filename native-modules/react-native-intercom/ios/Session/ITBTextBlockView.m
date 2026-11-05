#import "ITBTextBlockView.h"
#import "ITBTextBlock.h"
#import <math.h>

@interface NSObject (ITBTextBlockIntentDispatch)
- (void)handleIntent:(id<ITBIntent>)intent forBlock:(ITBBlock *)block fromView:(UIView *)view;
@end

@implementation ITBTextBlockViewIntent
@synthesize intentUrl = _intentUrl;
- (ITBIntentType)intentType { return ITBIntentTypeUrl; }
@end

static ITBTextBlockView *measuringTextBlockView;
@implementation ITBTextBlockView
@synthesize textView = _textView, layoutInsets = _layoutInsets;
- (void)dealloc {
    self.textView.delegate = nil;
    self.textView = nil;
}
- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) [self createTextView];
    return self;
}
- (NSString *)accessibilityText { return self.textView.text; }
- (void)createTextView {
    self.textView = [ITBHyperlinkTextView new];
    self.textView.delegate = self;
    self.textView.backgroundColor = UIColor.clearColor;
    self.textView.scrollsToTop = NO;
    self.textView.editable = NO;
    self.textView.scrollEnabled = NO;
    self.textView.translatesAutoresizingMaskIntoConstraints = NO;
    [self addSubview:self.textView];
}
- (void)layoutSubviews {
    [self.textView.leadingAnchor constraintEqualToAnchor:self.leadingAnchor constant:self.layoutInsets.left].active = YES;
    [self.textView.topAnchor constraintEqualToAnchor:self.topAnchor constant:self.layoutInsets.top].active = YES;
    [self.textView.trailingAnchor constraintEqualToAnchor:self.trailingAnchor constant:-self.layoutInsets.right].active = YES;
    [self.textView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor constant:-self.layoutInsets.bottom].active = YES;
}
+ (void)setup { measuringTextBlockView = [ITBTextBlockView new]; }
+ (void)tearDown {
    measuringTextBlockView.textView.attributedText = nil;
    measuringTextBlockView.textView = nil;
    measuringTextBlockView = nil;
}
+ (CGSize)estimateSizeForBlock:(ITBBlock *)block withWidth:(CGFloat)width constraintWidth:(BOOL)constraintWidth {
    __attribute__((objc_precise_lifetime)) ITBTextBlock *retainedBlock = (ITBTextBlock *)block;
    __weak ITBTextBlock *weakBlock = retainedBlock;
    if (!measuringTextBlockView) [self setup];
    CGFloat maxWidth = [self maxWidthForBlockContentBasedInWidth:width forBlock:weakBlock constraintWidth:constraintWidth];
    if (weakBlock.attributedString.length == 0) return CGSizeMake(constraintWidth ? maxWidth : width, 39);
    measuringTextBlockView.textView.textContainerInset = weakBlock.textInsets;
    measuringTextBlockView.textView.attributedText = weakBlock.attributedString;
    measuringTextBlockView.textView.layoutMargins = weakBlock.layoutInsets;
    measuringTextBlockView.textView.textContainer.lineFragmentPadding = weakBlock.textInsets.left == 0 && weakBlock.textInsets.right == 0 ? 0 : 5;
    CGFloat left = weakBlock.layoutInsets.left;
    CGFloat right = weakBlock.layoutInsets.right;
    CGFloat horizontal = left + right;
    CGSize size = [measuringTextBlockView.textView sizeThatFits:CGSizeMake(maxWidth - horizontal, CGFLOAT_MAX)];
    CGFloat top = weakBlock.layoutInsets.top;
    CGFloat bottom = weakBlock.layoutInsets.bottom;
    CGFloat measuredWidth = horizontal + size.width;
    CGFloat measuredHeight = size.height + (top + bottom);
    return CGSizeMake(constraintWidth ? (CGFloat)ceilf((float)measuredWidth) : width, (CGFloat)ceilf((float)measuredHeight));
}
- (void)configureWithBlock:(ITBBlock *)block {
    [super configureWithBlock:block];
    __weak ITBTextBlock *weakBlock = (ITBTextBlock *)block;
    self.textView.selectable = [(ITBTextBlock *)block isTextSelectable];
    self.layoutInsets = weakBlock.layoutInsets;
    self.textView.layoutMargins = weakBlock.layoutInsets;
    self.textView.textContainerInset = weakBlock.textInsets;
    self.textView.textContainer.lineFragmentPadding = weakBlock.textInsets.left == 0 && weakBlock.textInsets.right == 0 ? 0 : 5;
    NSDictionary *attributes = @{NSForegroundColorAttributeName:weakBlock.linkTextColor, NSUnderlineColorAttributeName:weakBlock.linkTextColor};
    self.textView.linkTextAttributes = attributes;
    self.textView.attributedText = weakBlock.attributedString;
    self.textView.backgroundColor = weakBlock.backgroundColor;
    self.accessibilityLabel = weakBlock.attributedString.string;
}
- (BOOL)textView:(UITextView *)textView shouldInteractWithURL:(NSURL *)url inRange:(NSRange)range interaction:(UITextItemInteraction)interaction {
    (void)textView;
    (void)range;
    (void)interaction;
    ITBTextBlockViewIntent *intent = [ITBTextBlockViewIntent new];
    intent.intentUrl = url;
    [(id)self.intentOpener handleIntent:intent forBlock:self.block fromView:self];
    return NO;
}
@end
