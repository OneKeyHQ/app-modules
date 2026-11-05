#import "ITBLinkListViewDependencies.h"
@implementation ITBFooterContainerView
@synthesize iconImageView = _iconImageView, titleLabel = _titleLabel, iconImage = _iconImage, linkListBlock = _linkListBlock;
- (instancetype)initWithLinkBlock:(ITBLinkListBlock *)block {
    self = [super init];
    if (self) {
        _linkListBlock = block;
        self.translatesAutoresizingMaskIntoConstraints = NO;
        [self buildIconImageView];
        [self buildTitleLabel];
    }
    return self;
}
- (void)buildIconImageView {
    _iconImageView = [[UIImageView alloc] initWithImage:self.linkListBlock.footerLinkIcon];
    _iconImageView.translatesAutoresizingMaskIntoConstraints = NO;
    _iconImageView.tintColor = self.linkListBlock.linkTextColor;
    [self addSubview:_iconImageView];
}
- (void)buildTitleLabel {
    _titleLabel = [UILabel new];
    _titleLabel.translatesAutoresizingMaskIntoConstraints = NO;
    _titleLabel.text = self.linkListBlock.footerLink.title;
    _titleLabel.font = self.linkListBlock.linkFont;
    _titleLabel.textColor = self.linkListBlock.linkTextColor;
    _titleLabel.numberOfLines = 2;
    [self addSubview:_titleLabel];
}
- (void)layoutSubviews {
    [super layoutSubviews];
    [self layoutIconImageView];
    [self layoutTitleLabel];
}
- (CGSize)intrinsicContentSize {
    CGFloat width = self.titleLabel.intrinsicContentSize.width;
    width += self.iconImageView.intrinsicContentSize.width;
    width += 7;
    CGFloat height = self.titleLabel.intrinsicContentSize.height;
    height += self.iconImageView.intrinsicContentSize.height;
    return CGSizeMake(width, height);
}
- (void)layoutIconImageView {
    [NSLayoutConstraint constraintWithItem:self.iconImageView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1 constant:self.linkListBlock.linkFooterIconSize.height].active = YES;
    [NSLayoutConstraint constraintWithItem:self.iconImageView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1 constant:self.linkListBlock.linkFooterIconSize.width].active = YES;
    [NSLayoutConstraint constraintWithItem:self.iconImageView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeft multiplier:1 constant:0].active = YES;
    [NSLayoutConstraint constraintWithItem:self.iconImageView attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeCenterY multiplier:1 constant:0].active = YES;
}
- (void)layoutTitleLabel {
    [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1 constant:0].active = YES;
    [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1 constant:0].active = YES;
    [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.iconImageView attribute:NSLayoutAttributeRight multiplier:1 constant:7].active = YES;
    [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1 constant:0].active = YES;
}
@end
