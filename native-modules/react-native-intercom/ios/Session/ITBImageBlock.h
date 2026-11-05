#import "ITBBlock.h"
#import "ITBIntent.h"
@class ITBUpload;
@interface ITBImageBlock : ITBBlock <ITBIntent> {
    NSString *_url;
    NSString *_linkUrl;
    CGFloat _width;
    CGFloat _height;
    CGFloat _padding;
    CGFloat _cornerRadius;
    ITBUpload *_upload;
    NSUInteger _loadingViewColorType;
    CGFloat _inlineImagePadding;
    CGFloat _singleImagePadding;
    CGFloat _inlineImageCornerRadius;
    CGFloat _singleImageCornerRadius;
    CGSize _imageSize;
}
@property (nonatomic, copy) NSString *url;
@property (nonatomic, copy) NSString *linkUrl;
@property (nonatomic) CGFloat width;
@property (nonatomic) CGFloat height;
@property (nonatomic) CGFloat padding;
@property (nonatomic) CGFloat cornerRadius;
@property (nonatomic, strong) ITBUpload *upload;
@property (nonatomic) NSUInteger loadingViewColorType;
@property (nonatomic) CGFloat inlineImagePadding;
@property (nonatomic) CGFloat singleImagePadding;
@property (nonatomic) CGFloat inlineImageCornerRadius;
@property (nonatomic) CGFloat singleImageCornerRadius;
@property (nonatomic) CGSize imageSize;
@end
