#import "ICMContentStackView.h"
#import "ITBBlockView.h"
@implementation ICMContentStackView
- (instancetype)initWithBlocks:(NSArray<ITBBlock *> *)blocks context:(NSInteger)context withinWidth:(CGFloat)width blockDelegate:(OutboundContentBlockDelegate *)delegate {
    self = [super init];
    if (self) { _blocks = blocks; _context = context; _constrainedWidth = width; _blockDelegate = delegate; [self configureMainStackView]; }
    return self;
}
- (instancetype)initWithBlocks:(NSArray<ITBBlock *> *)blocks context:(NSInteger)context blockDelegate:(OutboundContentBlockDelegate *)delegate { return [self initWithBlocks:blocks context:context withinWidth:0 blockDelegate:delegate]; }
- (instancetype)initWithBlocks:(NSArray<ITBBlock *> *)blocks context:(NSInteger)context withinWidth:(CGFloat)width { return [self initWithBlocks:blocks context:context withinWidth:width blockDelegate:nil]; }
- (instancetype)initWithBlocks:(NSArray<ITBBlock *> *)blocks context:(NSInteger)context { return [self initWithBlocks:blocks context:context withinWidth:0 blockDelegate:nil]; }
- (void)configureMainStackView {
    self.distribution = UIStackViewDistributionFill;
    self.axis = UILayoutConstraintAxisVertical;
    self.alignment = UIStackViewAlignmentFill;
    NSInteger context = self.context;
    self.spacing = context == 3 ? 0 : 16;
    self.distribution = context == 3 ? UIStackViewDistributionFillEqually : UIStackViewDistributionFill;
    self.userInteractionEnabled = context != 3;
}
- (void)renderContent {
    if (self.context == 3 && self.constrainedWidth > 0) {
        for (ITBBlock *block in self.blocks) {
            @try {
                Class viewClass = block.viewClass;
                CGFloat width = self.constrainedWidth;
                block.estimatedSize = [viewClass estimateSizeForBlock:block withWidth:width constraintWidth:NO];
            } @catch (NSException *exception) { (void)exception; }
        }
    }
    if (self.blocks.count == 0) return;
    NSUInteger index = 0;
    do {
        ITBBlock *block = [self.blocks objectAtIndex:index];
        index++;
        self.nextBlock = self.blocks.count > index ? [self.blocks objectAtIndex:index] : nil;
        [self appendBlock:block];
    } while (self.blocks.count > index);
}
- (void)appendBlock:(ITBBlock *)block { [self insertBlock:block atIndex:self.arrangedSubviews.count]; }
- (void)insertBlock:(ITBBlock *)block atIndex:(NSInteger)index {
    UIView *view = [BlockViewFactoryObjC viewFor:block displayContext:1 blockDelegate:self.blockDelegate];
    view.translatesAutoresizingMaskIntoConstraints = NO;
    UIView *arranged = view;
    if (block.type == 5 && self.context != 3) {
        UIView *wrapper = [UIView new];
        wrapper.translatesAutoresizingMaskIntoConstraints = NO;
        [wrapper addSubview:view];
        [view.centerXAnchor constraintEqualToAnchor:wrapper.centerXAnchor].active = YES;
        [view.topAnchor constraintEqualToAnchor:wrapper.topAnchor].active = YES;
        [view.bottomAnchor constraintEqualToAnchor:wrapper.bottomAnchor].active = YES;
        [view.widthAnchor constraintLessThanOrEqualToAnchor:wrapper.widthAnchor].active = YES;
        arranged = wrapper;
    } else if (self.context == 3 && block.type != 5) {
        CGFloat height = block.estimatedSize.height;
        if (height <= 0 && self.constrainedWidth > 0) {
            UIView *temporary = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.constrainedWidth, 10000)];
            [temporary addSubview:view];
            NSArray *constraints = @[[view.leadingAnchor constraintEqualToAnchor:temporary.leadingAnchor], [view.trailingAnchor constraintEqualToAnchor:temporary.trailingAnchor], [view.topAnchor constraintEqualToAnchor:temporary.topAnchor]];
            [NSLayoutConstraint activateConstraints:constraints];
            [temporary layoutIfNeeded];
            height = CGRectGetHeight(view.frame);
            [NSLayoutConstraint deactivateConstraints:constraints];
            [view removeFromSuperview];
        }
        if (height > 0) [view.heightAnchor constraintEqualToConstant:height].active = YES;
    }
    [self insertArrangedSubview:arranged atIndex:index];
    if (self.context != 3) [self applyCustomSpacingToBlock:block afterView:arranged];
}
- (void)applyCustomSpacingToBlock:(ITBBlock *)block afterView:(UIView *)view {
    CGFloat spacing;
    if (block.type == 1) spacing = 16;
    else if (block.type == 0) spacing = 4;
    else if (block.type == 5) spacing = 24;
    else if (block.type == 20) spacing = self.nextBlock.type == 20 ? 24 : 32;
    else spacing = 16;
    [self setCustomSpacing:spacing afterView:view];
}
- (void)updateFirstBlockWithAppearance:(id<ITBBlockAppearance>)appearance {
    UIView *view = self.arrangedSubviews.firstObject;
    [self.blocks.firstObject updateAppearance:appearance];
    [self removeArrangedSubview:view];
    [view removeFromSuperview];
    [self insertBlock:self.blocks.firstObject atIndex:0];
}
@end
