// Required original dependencies; declarations do not supply missing implementations.
#import "ICMConversationPartDependencies.h"
#import "ITBImageBlock.h"
#import "ITBAnimatedImageView.h"
#import "ITBLoadingView.h"
#import "ITBUploadProgressButton.h"
#import "ITBImagePreviewMetadata.h"
#import "ITBImageUtils.h"
// These delegate signatures are inferred from original selector call sites.
@interface NSObject (ITBImageViewDelegateCalls)
- (void)reloadRequiredForBlock:(ITBBlock *)block;
- (UIViewController *)viewControllerForModalPresentation;
- (void)handleIntent:(id<ITBIntent>)intent forBlock:(ITBBlock *)block fromView:(UIView *)view;
- (void)blockView:(UIView *)view showImagePreviewWithMetadata:(ITBImagePreviewMetadata *)metadata;
@end
