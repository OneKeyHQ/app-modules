#import "ITBLinkListBlockView.h"
#import "ITBLinkListViewDependencies.h"
#import <math.h>
@implementation ITBLinkListBlockView
@synthesize linkListBlock = _linkListBlock, linkItemViewArray = _linkItemViewArray, titleLabel = _titleLabel;
- (void)configureWithBlock:(ITBBlock *)block {
    [super configureWithBlock:block];
    self.accessibilityIdentifier = @"intercom link list";
    self.linkListBlock = (ITBLinkListBlock *)block;
    [self buildLinks];
}
- (void)buildLinks {
    [self cleanupLinkViews];
    NSMutableArray *views = [NSMutableArray new];
    for (ITBLink *link in self.linkListBlock.links) {
        ITBLinkListItemView *view = [[ITBLinkListItemView alloc] initWithLink:link block:self.linkListBlock intentOpener:self.intentOpener];
        [self addSubview:view];
        [views addObject:view];
    }
    if (self.linkListBlock.footerLink) {
        ITBLinkListFooterView *view = [[ITBLinkListFooterView alloc] initWithLink:self.linkListBlock.footerLink block:self.linkListBlock intentOpener:self.intentOpener icon:self.linkListBlock.footerLinkIcon];
        [self addSubview:view];
        [views addObject:view];
    }
    self.linkItemViewArray = views;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    [self layoutLinkViews];
}
- (void)layoutLinkViews {
    UIView *previous = nil;
    for (NSUInteger index = 0; index < self.linkItemViewArray.count; index++) {
        UIView *view = [self.linkItemViewArray objectAtIndex:index];
        [NSLayoutConstraint deactivateConstraints:view.constraints];
        [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1 constant:self.bounds.size.width].active = YES;
        CGFloat height = [self determineLinkViewHeight:view atIndex:index];
        [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1 constant:height].active = YES;
        [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeft multiplier:1 constant:0].active = YES;
        [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:previous ?: self attribute:previous ? NSLayoutAttributeBottom : NSLayoutAttributeTop multiplier:1 constant:0].active = YES;
        previous = view;
    }
}
+ (CGSize)estimateSizeForBlock:(ITBBlock *)block withWidth:(CGFloat)width constraintWidth:(BOOL)constraintWidth {
    (void)constraintWidth;
    ITBLinkListBlock *list = (ITBLinkListBlock *)block;
    CGFloat height = 0;
    for (ITBLink *link in list.links) {
        CGFloat innerWidth = fma(list.textInsets.left, -2, width);
        height += [self calculateLinkHeight:link.title linkListBlock:list withinWidth:innerWidth];
    }
    if (list.footerLink) {
        NSString *title = list.footerLink.title;
        CGFloat innerWidth = fma(list.linkFooterInsets.left, -2, width);
        innerWidth -= list.linkFooterIconSize.width;
        innerWidth += -7;
        height += [self calculateLinkHeight:title linkListBlock:list withinWidth:innerWidth];
    }
    return CGSizeMake(width, height);
}
- (CGFloat)determineLinkViewHeight:(UIView *)view atIndex:(NSInteger)index {
    if ([view isKindOfClass:[ITBLinkListItemView class]]) {
        ITBLink *link = [self.linkListBlock.links objectAtIndex:index];
        NSString *title = link.title;
        ITBLinkListBlock *list = self.linkListBlock;
        CGFloat width = self.bounds.size.width;
        width = fma(self.linkListBlock.textInsets.left, -2, width);
        return [ITBLinkListBlockView calculateLinkHeight:title linkListBlock:list withinWidth:width];
    }
    if ([view isKindOfClass:[ITBLinkListFooterView class]]) {
        NSString *title = self.linkListBlock.footerLink.title;
        ITBLinkListBlock *list = self.linkListBlock;
        CGFloat width = self.bounds.size.width;
        width = fma(self.linkListBlock.linkFooterInsets.left, -2, width);
        width -= self.linkListBlock.linkFooterIconSize.width;
        width += -7;
        return [ITBLinkListBlockView calculateLinkHeight:title linkListBlock:list withinWidth:width];
    }
    return self.linkListBlock.defaultLinkHeight;
}
+ (CGFloat)calculateLinkHeight:(NSString *)title linkListBlock:(ITBLinkListBlock *)block withinWidth:(CGFloat)width {
    NSMutableParagraphStyle *style = [NSMutableParagraphStyle new];
    style.lineBreakMode = 0;
    CGFloat maxHeight = block.maxLinkHeight;
    NSDictionary *attributes = @{NSParagraphStyleAttributeName:style, NSFontAttributeName:block.linkFont};
    CGRect text = [title boundingRectWithSize:CGSizeMake(width, maxHeight) options:1 attributes:attributes context:nil];
    maxHeight = block.maxLinkHeight;
    CGFloat threshold = fma(block.textInsets.top, -2, maxHeight);
    return text.size.height > threshold ? block.maxLinkHeight : block.defaultLinkHeight;
}
- (void)prepareForReuse { [self cleanupLinkViews]; }
- (void)cleanupLinkViews {
    for (UIView *view in self.linkItemViewArray) [view removeFromSuperview];
    self.linkItemViewArray = nil;
}
@end
