#import "ITBLinkListBlockView.h"
@interface ITBLinkListItemView : UIView {
    ITBLink *_link;
    UILabel *_titleLabel;
    id<ITBIntentOpenerDelegate> _intentOpener;
    ITBLinkListBlock *_linkListBlock;
    UIView *_separatorView;
}
@property (nonatomic, strong) ITBLink *link;
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) id<ITBIntentOpenerDelegate> intentOpener;
@property (nonatomic, strong) ITBLinkListBlock *linkListBlock;
@property (nonatomic, strong) UIView *separatorView;
- (instancetype)initWithLink:(ITBLink *)link block:(ITBLinkListBlock *)block intentOpener:(id<ITBIntentOpenerDelegate>)opener;
- (void)buildTitleLabel;
- (void)buildSeparatorView;
- (void)layoutTitleLabel;
- (void)layoutSeparatorView;
- (void)addTapGesture;
- (void)linkTapped:(id)sender;
- (void)hideSeparator;
@end
@interface ITBLinkListFooterView : UIView {
    ITBLink *_link;
    UIImage *_iconImage;
    id<ITBIntentOpenerDelegate> _intentOpener;
    ITBLinkListBlock *_linkListBlock;
    UIView *_footerContainerView;
}
@property (nonatomic, strong) ITBLink *link;
@property (nonatomic, strong) UIImage *iconImage;
@property (nonatomic, strong) id<ITBIntentOpenerDelegate> intentOpener;
@property (nonatomic, strong) ITBLinkListBlock *linkListBlock;
@property (nonatomic, strong) UIView *footerContainerView;
- (instancetype)initWithLink:(ITBLink *)link block:(ITBLinkListBlock *)block intentOpener:(id<ITBIntentOpenerDelegate>)opener icon:(UIImage *)icon;
- (void)buildFooterContainerView;
- (void)layoutContainerView;
- (void)addTapGesture;
- (void)linkTapped:(id)sender;
@end
@interface ITBFooterContainerView : UIView {
    UIImageView *_iconImageView;
    UILabel *_titleLabel;
    UIImage *_iconImage;
    ITBLinkListBlock *_linkListBlock;
}
@property (nonatomic, strong) UIImageView *iconImageView;
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UIImage *iconImage;
@property (nonatomic, strong) ITBLinkListBlock *linkListBlock;
- (instancetype)initWithLinkBlock:(ITBLinkListBlock *)block;
- (void)buildIconImageView;
- (void)buildTitleLabel;
- (void)layoutIconImageView;
- (void)layoutTitleLabel;
@end
