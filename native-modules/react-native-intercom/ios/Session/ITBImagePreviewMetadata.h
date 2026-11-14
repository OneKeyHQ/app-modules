#import <UIKit/UIKit.h>
@interface ITBImagePreviewMetadata : NSObject {
    UIView *_sourceView;
    UIImage *_previewImage;
    UIViewController *_presentingViewController;
    void (^_imagePreviewerActionBlock)(void);
}
@property (nonatomic, strong) UIView *sourceView;
@property (nonatomic, strong) UIImage *previewImage;
@property (nonatomic, strong) UIViewController *presentingViewController;
@property (nonatomic, copy) void (^imagePreviewerActionBlock)(void);
@end
