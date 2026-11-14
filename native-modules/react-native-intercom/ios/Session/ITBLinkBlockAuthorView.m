#import "ITBLinkBlockView.h"
@implementation ITBLinkBlockAuthorView
@synthesize avatarView = _avatarView, writtenByLabel = _writtenByLabel;
- (instancetype)init {
    self = [super init];
    if (self) {
        self.clipsToBounds = YES;
        self.avatarView = [ITBAnimatedImageView new];
        self.avatarView.clipsToBounds = YES;
        [self addSubview:self.avatarView];
        self.writtenByLabel = [UILabel new];
        self.writtenByLabel.numberOfLines = 2;
        [self addSubview:self.writtenByLabel];
    }
    return self;
}
- (void)prepareForReuse {
    [super prepareForReuse];
    [self.avatarView cancelImageLoad];
    self.avatarView.image = nil;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    ITBLinkBlockAuthor *author = (ITBLinkBlockAuthor *)self.block;
    UIUserInterfaceLayoutDirection direction = [UIApplication sharedApplication].userInterfaceLayoutDirection;
    CGFloat x = 0;
    if (direction != 0)
        x = self.frame.size.width - author.avatarRadius;
    CGFloat y = CGRectGetHeight(self.bounds) * 0.5 - author.avatarRadius * 0.5;
    CGFloat width = author.avatarRadius;
    CGFloat height = author.avatarRadius;
    self.avatarView.frame = CGRectMake(x, y, width, height);
    if (direction == 0)
        x = self.avatarView.frame.size.width + 8;
    else x = 0;
    width = CGRectGetWidth(self.bounds) - (self.avatarView.frame.size.width + 8);
    height = CGRectGetHeight(self.bounds);
    self.writtenByLabel.frame = CGRectMake(x, 0, width, height);
}
- (CGSize)intrinsicContentSize {
    CGSize label = self.writtenByLabel.intrinsicContentSize;
    ITBLinkBlockAuthor *author = (ITBLinkBlockAuthor *)self.block;
    CGFloat radius = author.avatarRadius;
    CGFloat width = label.width + (radius + 8);
    return CGSizeMake(width, radius < label.height ? label.height : radius);
}
- (CGSize)sizeThatFits:(CGSize)size {
    ITBLinkBlockAuthor *author = (ITBLinkBlockAuthor *)self.block;
    CGSize label = [self.writtenByLabel sizeThatFits:CGSizeMake(size.width - author.avatarRadius - 8, size.height)];
    CGFloat width = label.width + (author.avatarRadius + 8);
    CGFloat radius = author.avatarRadius;
    return CGSizeMake(width, label.height < radius ? radius : label.height);
}
+ (NSAttributedString *)summaryAttributedStringForAuthor:(ITBLinkBlockAuthor *)author {
    NSMutableAttributedString *summary = [author.writtenByAttributedString mutableCopy];
    if (author.linkUpdatedAtAttributedString.length) {
        [summary appendAttributedString:[[NSAttributedString alloc] initWithString:@"\n" attributes:@{}]];
        [summary appendAttributedString:author.linkUpdatedAtAttributedString];
    }
    return summary;
}
+ (CGSize)estimateSizeForBlock:(ITBBlock *)block withWidth:(CGFloat)width constraintWidth:(BOOL)constraintWidth {
    ITBLinkBlockAuthor *author = (ITBLinkBlockAuthor *)block;
    if (!author.writtenByAttributedString.length) return CGSizeZero;
    CGFloat innerWidth = width - author.avatarRadius - 8;
    NSAttributedString *summary = [self summaryAttributedStringForAuthor:author];
    CGRect rect = [summary boundingRectWithSize:CGSizeMake(innerWidth, CGFLOAT_MAX) options:1 context:nil];
    CGFloat radius = author.avatarRadius;
    CGFloat height = radius < rect.size.height ? rect.size.height : radius;
    if (constraintWidth) width = rect.size.width + (author.avatarRadius + 8);
    return CGSizeMake(width, height);
}
- (void)configureWithBlock:(ITBBlock *)block {
    [super configureWithBlock:block];
    ITBLinkBlockAuthor *author = (ITBLinkBlockAuthor *)block;
    __weak ITBLinkBlockAuthorView *weakSelf = self;
    [self.avatarView loadImageWithURLString:author.avatar actionsBeforeNetworkLoad:^{
        UIImage *initials = author.initialsImage;
        weakSelf.avatarView.image = initials;
    } completion:nil animationOptions:0x500000];
    [self.avatarView loadImageWithURLString:author.avatar actionsBeforeNetworkLoad:nil completion:nil];
    self.writtenByLabel.attributedText = [[self class] summaryAttributedStringForAuthor:author];
    self.avatarView.layer.cornerRadius = author.avatarRadius * 0.5;
}
@end
