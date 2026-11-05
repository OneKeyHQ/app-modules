#import "ITBBlockCollectionViewCell.h"
#import "ITBBlockViewsReuseManager.h"
@implementation ITBBlockCollectionViewCell
@synthesize reloadDelegate = _reloadDelegate, intentOpener = _intentOpener, imagePreviewHandler = _imagePreviewHandler, view = _view, block = _block;
- (void)layoutSubviews {
    [super layoutSubviews];
    self.view.frame = self.contentView.bounds;
}
- (void)dealloc { [self cleanupView]; }
- (void)cleanupView {
    if (!self.view) return;
    [self.view removeFromSuperview];
    [[ITBBlockViewsReuseManager sharedInstance] enqueueReusableViewForReuse:self.view];
    self.view = nil;
}
- (void)configureWithBlock:(ITBBlock *)block {
    self.block = block;
    if (self.view && [self.view.block class] == [block class]) {
        [self.view prepareForReuse];
    } else {
        [self cleanupView];
        self.view = [[ITBBlockViewsReuseManager sharedInstance] dequeueReusableViewForBlock:block];
        self.view.intentOpener = self.intentOpener;
        self.view.imagePreviewHandler = self.imagePreviewHandler;
        [self.contentView addSubview:self.view];
    }
    self.view.delegate = self.reloadDelegate;
    [self.view configureWithBlock:block];
    CGSize size = block.estimatedSize;
    self.view.frame = CGRectMake(0, 0, size.width, size.height);
}
@end
