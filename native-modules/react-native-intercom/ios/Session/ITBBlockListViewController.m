#import "ITBBlockListViewController.h"
#import "ICMBlockLoader.h"
#import <math.h>
@implementation ITBBlockListViewController
@synthesize intentOpener = _intentOpener, delegate = _delegate, imagePreviewHandler = _imagePreviewHandler, blocks = _blocks, collectionView = _collectionView, collectionViewLayout = _collectionViewLayout;
- (instancetype)initWithBlocks:(NSArray *)blocks insets:(UIEdgeInsets)insets {
    if ((self = [super init])) {
        _blocks = blocks;
        UICollectionViewFlowLayout *layout = [UICollectionViewFlowLayout new];
        layout.sectionInset = insets;
        layout.minimumLineSpacing = 0;
        _collectionViewLayout = layout;
    }
    return self;
}
- (void)loadView {
    [super loadView];
    self.collectionView = [[UICollectionView alloc] initWithFrame:self.view.bounds collectionViewLayout:self.collectionViewLayout];
    self.collectionView.backgroundColor = [UIColor clearColor];
    self.collectionView.delegate = self;
    self.collectionView.dataSource = self;
    [self.view addSubview:self.collectionView];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    [self.collectionView registerClass:[ITBBlockCollectionViewCell class] forCellWithReuseIdentifier:@"BlockCell"];
    if ([self.delegate respondsToSelector:@selector(setupScrollView:)]) [self.delegate setupScrollView:self.collectionView];
}
- (void)viewWillLayoutSubviews {
    [super viewWillLayoutSubviews];
    self.collectionView.frame = self.view.bounds;
    [self calculateSizeForWidth:[self availableLayoutWidthForWidth:CGRectGetWidth(self.view.bounds)]];
}
- (CGFloat)availableLayoutWidthForWidth:(CGFloat)width {
    return (width - self.collectionViewLayout.sectionInset.left) - self.collectionViewLayout.sectionInset.right;
}
- (void)calculateSizeForWidth:(CGFloat)width {
    CGFloat rounded = ceil(width);
    ITBBlock *first = self.blocks.firstObject;
    if (first.estimatedSize.width == rounded) return;
    CGSize size = [ITBBlockUtils calculateSizeForBlocks:self.blocks withinWidth:rounded constraintWidth:NO];
    [self.collectionViewLayout invalidateLayout];
    if ([self.delegate respondsToSelector:@selector(didCalculateBlocksSize:)]) [self.delegate didCalculateBlocksSize:size];
}
- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView { (void)collectionView; return 1; }
- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section { (void)collectionView; (void)section; return self.blocks.count; }
- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    ITBBlockCollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"BlockCell" forIndexPath:indexPath];
    cell.intentOpener = self.intentOpener;
    cell.imagePreviewHandler = self.imagePreviewHandler;
    cell.reloadDelegate = self;
    [cell configureWithBlock:self.blocks[indexPath.item]];
    return cell;
}
- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)layout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    (void)collectionView; (void)layout;
    ITBBlock *block = self.blocks[indexPath.item];
    return block.estimatedSize;
}
- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    if ([self.delegate respondsToSelector:@selector(scrollViewDidScroll:)]) [self.delegate scrollViewDidScroll:scrollView];
}
- (void)reloadRequiredForBlock:(ITBBlock *)block {
    NSUInteger index = [self.blocks indexOfObject:block];
    if (index == NSNotFound) return;
    Class viewClass = block.viewClass;
    CGFloat width = [self availableLayoutWidthForWidth:CGRectGetWidth(self.view.bounds)];
    block.estimatedSize = [viewClass estimateSizeForBlock:block withWidth:width constraintWidth:NO];
    if (block.type == 18) [self.collectionView performBatchUpdates:nil completion:nil];
    else [self.collectionView reloadItemsAtIndexPaths:@[[NSIndexPath indexPathForItem:index inSection:0]]];
}
- (UIViewController *)viewControllerForModalPresentation { return self; }
- (void)reloadCollectionView { [self.collectionView reloadData]; }
@end
