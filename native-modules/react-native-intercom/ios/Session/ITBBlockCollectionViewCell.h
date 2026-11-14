#import "ITBBlockView.h"
@interface ITBBlockCollectionViewCell : UICollectionViewCell {
    __weak id<ITBBlockViewDelegate> _reloadDelegate;
    __weak id<ITBIntentOpenerDelegate> _intentOpener;
    __weak id<ITBImagePreviewDelegate> _imagePreviewHandler;
    ITBBlockView *_view;
    ITBBlock *_block;
}
@property (nonatomic, strong) ITBBlockView *view;
@property (nonatomic, strong) ITBBlock *block;
@property (nonatomic, weak) id<ITBBlockViewDelegate> reloadDelegate;
@property (nonatomic, weak) id<ITBIntentOpenerDelegate> intentOpener;
@property (nonatomic, weak) id<ITBImagePreviewDelegate> imagePreviewHandler;
- (void)cleanupView;
- (void)configureWithBlock:(ITBBlock *)block;
@end
