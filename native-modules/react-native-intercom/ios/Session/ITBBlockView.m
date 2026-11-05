#import "ITBBlockView.h"
@implementation ITBBlockView
@synthesize block = _block, uniqueIdentifier = _uniqueIdentifier, delegate = _delegate;
@synthesize intentOpener = _intentOpener, imagePreviewHandler = _imagePreviewHandler;
- (void)dealloc { [self setDelegate:nil]; }
- (void)prepareForReuse {
    [self setBlock:nil];
    [self removeConstraints:self.constraints];
}
+ (CGFloat)maxWidthForBlockContentBasedInWidth:(CGFloat)width forBlock:(ITBBlock *)block constraintWidth:(BOOL)constraintWidth {
    (void)block; (void)constraintWidth;
    return width;
}
// Original abstract/base view 0x76728 returns CGSizeZero; concrete view subclasses calculate layout.
+ (CGSize)estimateSizeForBlock:(ITBBlock *)block withWidth:(CGFloat)width constraintWidth:(BOOL)constraintWidth {
    (void)block; (void)width; (void)constraintWidth;
    return CGSizeZero;
}
- (void)configureWithBlock:(ITBBlock *)block { [self setBlock:block]; }
@end
