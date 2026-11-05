#import <UIKit/UIKit.h>
#import "ITBBlock.h"
@protocol ITBIntentOpenerDelegate, ITBImagePreviewDelegate;
@protocol ITBBlockViewDelegate <NSObject>
- (void)reloadRequiredForBlock:(ITBBlock *)block;
@optional
- (UIViewController *)viewControllerForModalPresentation;
@end
@protocol ITBBlockViewProtocol <NSObject>
- (void)configureWithBlock:(ITBBlock *)block;
+ (CGSize)estimateSizeForBlock:(ITBBlock *)block withWidth:(CGFloat)width constraintWidth:(BOOL)constraintWidth;
@end
@interface ITBBlockView : UIView <ITBBlockViewProtocol> {
    __weak ITBBlock *_block;
    NSString *_uniqueIdentifier;
    __weak id<ITBBlockViewDelegate> _delegate;
    __weak id<ITBIntentOpenerDelegate> _intentOpener;
    __weak id<ITBImagePreviewDelegate> _imagePreviewHandler;
}
@property (nonatomic, weak) ITBBlock *block;
@property (nonatomic, copy) NSString *uniqueIdentifier;
@property (nonatomic, weak) id<ITBBlockViewDelegate> delegate;
@property (nonatomic, weak) id<ITBIntentOpenerDelegate> intentOpener;
@property (nonatomic, weak) id<ITBImagePreviewDelegate> imagePreviewHandler;
- (void)prepareForReuse;
+ (CGFloat)maxWidthForBlockContentBasedInWidth:(CGFloat)width forBlock:(ITBBlock *)block constraintWidth:(BOOL)constraintWidth;
@end
