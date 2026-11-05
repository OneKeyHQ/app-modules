#import "ITBBlockCollectionViewCell.h"
// Only the three observed optional messages survive; the original private protocol metadata was erased.
@protocol ITBBlockListViewControllerDelegate <NSObject>
@optional
- (void)setupScrollView:(UIScrollView *)scrollView;
- (void)didCalculateBlocksSize:(CGSize)size;
- (void)scrollViewDidScroll:(UIScrollView *)scrollView;
@end
@interface ITBBlockListViewController : UIViewController <UICollectionViewDataSource, UICollectionViewDelegateFlowLayout, ITBBlockViewDelegate> {
    __weak id<ITBIntentOpenerDelegate> _intentOpener;
    __weak id<ITBBlockListViewControllerDelegate> _delegate;
    __weak id<ITBImagePreviewDelegate> _imagePreviewHandler;
    NSArray *_blocks;
    UICollectionView *_collectionView;
    UICollectionViewFlowLayout *_collectionViewLayout;
}
@property (nonatomic, weak) id<ITBIntentOpenerDelegate> intentOpener;
@property (nonatomic, weak) id<ITBBlockListViewControllerDelegate> delegate;
@property (nonatomic, weak) id<ITBImagePreviewDelegate> imagePreviewHandler;
@property (nonatomic, strong) NSArray *blocks;
@property (nonatomic, strong) UICollectionView *collectionView;
@property (nonatomic, strong) UICollectionViewFlowLayout *collectionViewLayout;
- (instancetype)initWithBlocks:(NSArray *)blocks insets:(UIEdgeInsets)insets;
- (CGFloat)availableLayoutWidthForWidth:(CGFloat)width;
- (void)calculateSizeForWidth:(CGFloat)width;
- (void)reloadCollectionView;
@end
