#import "ITBTextBlockView.h"
@interface ITBAttachmentIconView : UIView {
    UIColor *_clipColor;
}
@property (nonatomic, strong) UIColor *clipColor;
@end
@interface ITBAttachmentBlockView : ITBTextBlockView {
    ITBAttachmentIconView *_clipView;
    CGFloat _clipLeftPadding;
}
@property (nonatomic, strong) ITBAttachmentIconView *clipView;
@property (nonatomic) CGFloat clipLeftPadding;
@end
@interface ITBAttachmentListBlockView : ITBBlockView {
    NSArray *_attachmentViews;
}
@property (nonatomic, strong) NSArray *attachmentViews;
@end
