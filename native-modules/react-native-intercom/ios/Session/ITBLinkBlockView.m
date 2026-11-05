#import "ITBLinkBlockView.h"
#import "ITBIntent.h"
// Signature inferred from original call sites; the delegate protocol definition is absent.
@interface NSObject (ITBLinkIntentDispatch)
- (void)handleIntent:(id<ITBIntent>)intent forBlock:(ITBBlock *)block fromView:(UIView *)view;
@end
@implementation ITBLinkBlockView
@synthesize titleLabel = _titleLabel, cardView = _cardView, containerView = _containerView;
@synthesize descriptionLabel = _descriptionLabel, authorContainerView = _authorContainerView;
@synthesize authorView = _authorView, cardInsets = _cardInsets;
- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        _cardView = [UIView new];
        [self addSubview:_cardView];
        _containerView = [UIView new];
        [_cardView addSubview:_containerView];
        _titleLabel = [UILabel new];
        _titleLabel.numberOfLines = 0;
        [_containerView addSubview:_titleLabel];
        _descriptionLabel = [UILabel new];
        _descriptionLabel.numberOfLines = 0;
        [_containerView addSubview:_descriptionLabel];
        _authorContainerView = [UIView new];
        _authorView = [ITBLinkBlockAuthorView new];
        [_authorContainerView addSubview:_authorView];
        [_containerView addSubview:_authorContainerView];
        [self addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapped:)]];
        self.accessibilityLabel = @"intercom link";
    }
    return self;
}
- (void)configureForSingleDisplay:(ITBLinkBlock *)block {
    (void)block;
    self.cardInsets = UIEdgeInsetsZero;
    self.cardView.backgroundColor = [UIColor clearColor];
    self.cardView.layer.cornerRadius = 0;
}
- (void)configureForInlineDisplay:(ITBLinkBlock *)block {
    self.cardInsets = block.textInsets;
    self.cardView.backgroundColor = [UIColor whiteColor];
    self.cardView.layer.cornerRadius = 4;
}
- (void)prepareForReuse {
    [super prepareForReuse];
    [self.authorView prepareForReuse];
}
- (void)sizeAuthorViewToFit:(CGSize)size {
    CGRect frame = self.authorContainerView.frame;
    frame.size = [self.authorView sizeThatFits:size];
    self.authorContainerView.frame = frame;
    self.authorView.frame = CGRectMake(0, 0, CGRectGetWidth(frame), CGRectGetHeight(frame));
}
- (void)layoutSubviews {
    [super layoutSubviews];
    ITBLinkBlock *block = (ITBLinkBlock *)self.block;
    CGFloat x = self.cardInsets.left;
    CGFloat y = self.cardInsets.top;
    CGFloat width = CGRectGetWidth(self.bounds) - self.cardInsets.left;
    width -= self.cardInsets.right;
    CGFloat height = CGRectGetHeight(self.bounds) - self.cardInsets.top;
    height -= self.cardInsets.bottom;
    self.cardView.frame = CGRectMake(x, y, width, height);
    x = block.insets.left;
    y = block.insets.top;
    width = CGRectGetWidth(self.cardView.bounds) - block.insets.left;
    width -= block.insets.right;
    height = CGRectGetHeight(self.cardView.bounds) - block.insets.top;
    height -= block.insets.bottom;
    self.containerView.frame = CGRectMake(x, y, width, height);
    CGSize title = [self.titleLabel sizeThatFits:CGSizeMake(CGRectGetWidth(self.containerView.bounds), CGFLOAT_MAX)];
    self.titleLabel.frame = CGRectMake(0, 0, CGRectGetWidth(self.containerView.bounds), title.height);
    CGSize description = [self.descriptionLabel sizeThatFits:CGSizeMake(CGRectGetWidth(self.containerView.bounds), CGFLOAT_MAX)];
    if (self.descriptionLabel.text.length) {
        y = CGRectGetMaxY(self.titleLabel.frame) + 12;
        width = CGRectGetWidth(self.containerView.bounds);
    } else {
        y = CGRectGetMaxY(self.titleLabel.frame);
        width = 0;
        description.height = 0;
    }
    self.descriptionLabel.frame = CGRectMake(0, y, width, description.height);
    [self sizeAuthorViewToFit:CGSizeMake(CGRectGetWidth(self.containerView.bounds), CGFLOAT_MAX)];
    x = 0;
    if ([UIApplication sharedApplication].userInterfaceLayoutDirection != 0)
        x = CGRectGetWidth(self.containerView.bounds) - CGRectGetWidth(self.authorContainerView.bounds);
    y = CGRectGetMaxY(self.descriptionLabel.frame) + 12;
    width = CGRectGetWidth(self.containerView.bounds);
    height = CGRectGetHeight(self.authorContainerView.frame);
    self.authorContainerView.frame = CGRectMake(x, y, width, height);
}
- (void)tapped:(id)sender {
    (void)sender;
    ITBBlock *block = self.block;
    [(id)self.intentOpener handleIntent:(id<ITBIntent>)block forBlock:block fromView:self];
}
- (void)configureWithBlock:(ITBBlock *)block {
    [super configureWithBlock:block];
    ITBLinkBlock *link = (ITBLinkBlock *)block;
    self.titleLabel.attributedText = link.titleAttributedString;
    self.descriptionLabel.attributedText = link.linkDescriptionAttributedString;
    [self.authorView configureWithBlock:link.author];
    if (link.displayMode == 0) [self configureForInlineDisplay:link];
    else [self configureForSingleDisplay:link];
}
+ (CGSize)estimateSizeForBlock:(ITBBlock *)block withWidth:(CGFloat)width constraintWidth:(BOOL)constraintWidth {
    ITBLinkBlock *link = (ITBLinkBlock *)block;
    CGFloat innerWidth = width - link.insets.left;
    innerWidth -= link.insets.right;
    CGFloat vertical = link.insets.top;
    vertical += link.insets.bottom;
    if (link.displayMode == 0) {
        CGFloat extra = link.textInsets.top;
        extra += link.textInsets.bottom;
        vertical += extra;
        extra = link.textInsets.left;
        extra += link.textInsets.right;
        innerWidth -= extra;
    }
    CGRect title = CGRectIntegral([link.titleAttributedString boundingRectWithSize:CGSizeMake(innerWidth, CGFLOAT_MAX) options:1 context:nil]);
    CGRect description = CGRectIntegral([link.linkDescriptionAttributedString boundingRectWithSize:CGSizeMake(innerWidth, CGFLOAT_MAX) options:1 context:nil]);
    CGSize author = [ITBLinkBlockAuthorView estimateSizeForBlock:link.author withWidth:width constraintWidth:constraintWidth];
    if (constraintWidth) {
        CGFloat contentWidth = title.size.width;
        if (contentWidth < description.size.width) contentWidth = description.size.width;
        if (contentWidth < author.width) contentWidth = author.width;
        if (link.displayMode == 0) {
            CGFloat extra = link.textInsets.left;
            extra += link.textInsets.right;
            contentWidth += extra;
        }
        width = contentWidth + link.insets.left;
        width += link.insets.right;
    }
    CGFloat height = vertical + title.size.height;
    if (description.size.height > 0) height += description.size.height + 12;
    if (!(author.height == CGSizeZero.height && author.width == CGSizeZero.width)) height += author.height + 12;
    return CGSizeMake(width, height);
}
@end
