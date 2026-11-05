#import "ITBBlockView.h"
#import "ITBLinkBlock.h"
#import "ITBLinkBlockModels.h"
#import "ITBAnimatedImageView.h"
@interface ITBLinkBlockAuthorView : ITBBlockView {
    ITBAnimatedImageView *_avatarView;
    UILabel *_writtenByLabel;
}
@property (nonatomic, strong) ITBAnimatedImageView *avatarView;
@property (nonatomic, strong) UILabel *writtenByLabel;
+ (NSAttributedString *)summaryAttributedStringForAuthor:(ITBLinkBlockAuthor *)author;
@end
@interface ITBLinkBlockView : ITBBlockView {
    UILabel *_titleLabel;
    UIView *_cardView;
    UIView *_containerView;
    UILabel *_descriptionLabel;
    UIView *_authorContainerView;
    ITBLinkBlockAuthorView *_authorView;
    UIEdgeInsets _cardInsets;
}
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UIView *cardView;
@property (nonatomic, strong) UIView *containerView;
@property (nonatomic, strong) UILabel *descriptionLabel;
@property (nonatomic, strong) UIView *authorContainerView;
@property (nonatomic, strong) ITBLinkBlockAuthorView *authorView;
@property (nonatomic) UIEdgeInsets cardInsets;
- (void)configureForSingleDisplay:(ITBLinkBlock *)block;
- (void)configureForInlineDisplay:(ITBLinkBlock *)block;
- (void)sizeAuthorViewToFit:(CGSize)size;
- (void)tapped:(id)sender;
@end
