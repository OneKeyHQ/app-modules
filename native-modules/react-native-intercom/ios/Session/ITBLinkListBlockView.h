#import "ITBBlockView.h"
#import "ITBLinkListBlock.h"
@interface ITBLinkListBlockView : ITBBlockView {
    ITBLinkListBlock *_linkListBlock;
    NSArray *_linkItemViewArray;
    UILabel *_titleLabel;
}
@property (nonatomic, strong) ITBLinkListBlock *linkListBlock;
@property (nonatomic, strong) NSArray *linkItemViewArray;
@property (nonatomic, strong) UILabel *titleLabel;
- (void)buildLinks;
- (void)layoutLinkViews;
- (CGFloat)determineLinkViewHeight:(UIView *)view atIndex:(NSInteger)index;
- (void)cleanupLinkViews;
+ (CGFloat)calculateLinkHeight:(NSString *)title linkListBlock:(ITBLinkListBlock *)block withinWidth:(CGFloat)width;
@end
