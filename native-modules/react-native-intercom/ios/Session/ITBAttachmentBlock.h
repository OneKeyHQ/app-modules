#import "ITBTextBlock.h"
#import "ITBUpload.h"
@interface ITBAttachmentBlock : ITBTextBlock {
    UIColor *_clipColor;
    CGFloat _clipMargin;
    NSString *_url;
    ITBUpload *_upload;
}
@property (nonatomic, strong) UIColor *clipColor;
@property (nonatomic) CGFloat clipMargin;
@property (nonatomic, copy) NSString *url;
@property (nonatomic, strong) ITBUpload *upload;
@end
@interface ITBAttachmentListBlock : ITBBlock {
    NSArray *_attachments;
}
@property (nonatomic, strong) NSArray *attachments;
+ (NSArray *)attachmentsFromArray:(NSArray *)array appearance:(id)appearance;
@end
