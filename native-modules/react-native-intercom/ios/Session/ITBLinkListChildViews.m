#import "ITBLinkListViewDependencies.h"
// Signature inferred from original call sites; the delegate protocol definition is absent.
@interface NSObject (ITBLinkListIntentDispatch)
- (void)handleIntent:(id<ITBIntent>)intent forBlock:(ITBBlock *)block fromView:(UIView *)view;
@end
@implementation ITBLinkListItemView
@synthesize link = _link, titleLabel = _titleLabel, intentOpener = _intentOpener;
@synthesize linkListBlock = _linkListBlock, separatorView = _separatorView;
- (instancetype)initWithLink:(ITBLink *)link block:(ITBLinkListBlock *)block intentOpener:(id<ITBIntentOpenerDelegate>)opener {
    self = [super init];
    if (self) {
        _link = link;
        _linkListBlock = block;
        _intentOpener = opener;
        self.translatesAutoresizingMaskIntoConstraints = NO;
        [self buildTitleLabel];
        [self buildSeparatorView];
        [self addTapGesture];
    }
    return self;
}
- (void)buildTitleLabel {
    _titleLabel = [UILabel new];
    _titleLabel.translatesAutoresizingMaskIntoConstraints = NO;
    _titleLabel.text = self.link.title;
    _titleLabel.font = self.linkListBlock.linkFont;
    _titleLabel.textColor = self.linkListBlock.linkTextColor;
    _titleLabel.numberOfLines = 2;
    [self addSubview:_titleLabel];
}
- (void)buildSeparatorView {
    _separatorView = [UIView new];
    _separatorView.translatesAutoresizingMaskIntoConstraints = NO;
    _separatorView.backgroundColor = self.linkListBlock.separatorColor;
    [self addSubview:_separatorView];
}
- (void)layoutSubviews {
    [super layoutSubviews];
    [self layoutTitleLabel];
    [self layoutSeparatorView];
}
- (void)layoutTitleLabel {
    [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeft multiplier:1 constant:self.linkListBlock.textInsets.left].active = YES;
    [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1 constant:-self.linkListBlock.textInsets.right].active = YES;
    [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1 constant:0].active = YES;
    [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1 constant:0].active = YES;
}
- (void)layoutSeparatorView {
    [NSLayoutConstraint constraintWithItem:self.separatorView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1 constant:1 / [UIScreen mainScreen].scale].active = YES;
    [NSLayoutConstraint constraintWithItem:self.separatorView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeft multiplier:1 constant:0].active = YES;
    [NSLayoutConstraint constraintWithItem:self.separatorView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1 constant:0].active = YES;
    [NSLayoutConstraint constraintWithItem:self.separatorView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1 constant:0].active = YES;
}
- (void)hideSeparator { self.separatorView.hidden = YES; }
- (void)addTapGesture {
    [self addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(linkTapped:)]];
}
- (void)linkTapped:(id)sender {
    (void)sender;
    if (!self.intentOpener) return;
    ITBLink *link = self.link;
    self.linkListBlock.selectedLink = link;
    if ([(id)self.intentOpener respondsToSelector:@selector(handleIntent:forBlock:fromView:)]) {
        id opener = self.intentOpener;
        ITBLinkListBlock *intent = self.linkListBlock;
        ITBLinkListBlock *block = self.linkListBlock;
        [opener handleIntent:intent forBlock:block fromView:self];
    }
}
@end
@implementation ITBLinkListFooterView
@synthesize link = _link, iconImage = _iconImage, intentOpener = _intentOpener;
@synthesize linkListBlock = _linkListBlock, footerContainerView = _footerContainerView;
- (instancetype)initWithLink:(ITBLink *)link block:(ITBLinkListBlock *)block intentOpener:(id<ITBIntentOpenerDelegate>)opener icon:(UIImage *)icon {
    self = [super init];
    if (self) {
        _link = link;
        _linkListBlock = block;
        _intentOpener = opener;
        _iconImage = icon;
        self.translatesAutoresizingMaskIntoConstraints = NO;
        [self buildFooterContainerView];
        [self addTapGesture];
        self.accessibilityIdentifier = @"intercom link list footer";
    }
    return self;
}
- (void)buildFooterContainerView {
    self.footerContainerView = [[ITBFooterContainerView alloc] initWithLinkBlock:self.linkListBlock];
    [self addSubview:self.footerContainerView];
}
- (void)layoutSubviews {
    [super layoutSubviews];
    [self layoutContainerView];
}
- (void)layoutContainerView {
    [NSLayoutConstraint constraintWithItem:self.footerContainerView attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeCenterY multiplier:1 constant:0].active = YES;
    NSLayoutConstraint *center = [NSLayoutConstraint constraintWithItem:self.footerContainerView attribute:NSLayoutAttributeCenterX relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeCenterX multiplier:1 constant:0];
    center.priority = 800;
    center.active = YES;
    NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:self.footerContainerView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationGreaterThanOrEqual toItem:self attribute:NSLayoutAttributeLeft multiplier:1 constant:self.linkListBlock.linkFooterInsets.left];
    left.priority = 200;
    left.active = YES;
    [NSLayoutConstraint constraintWithItem:self.footerContainerView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationLessThanOrEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1 constant:-self.linkListBlock.linkFooterInsets.right].active = YES;
}
- (void)addTapGesture {
    [self addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(linkTapped:)]];
}
- (void)linkTapped:(id)sender {
    (void)sender;
    if (!self.intentOpener) return;
    ITBLink *link = self.link;
    self.linkListBlock.selectedLink = link;
    if ([(id)self.intentOpener respondsToSelector:@selector(handleIntent:forBlock:fromView:)]) {
        id opener = self.intentOpener;
        ITBLinkListBlock *intent = self.linkListBlock;
        ITBLinkListBlock *block = self.linkListBlock;
        [opener handleIntent:intent forBlock:block fromView:self];
    }
}
@end
