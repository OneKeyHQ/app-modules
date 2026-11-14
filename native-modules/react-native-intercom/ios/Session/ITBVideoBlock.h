#import "ITBBlock.h"
@interface ITBVideoBlock : ITBBlock <NSSecureCoding> {
    NSString *_provider;
    NSString *_videoId;
    NSUInteger _loadingViewColorType;
    CGSize _videoSize;
}
@property (nonatomic, copy) NSString *provider;
@property (nonatomic, copy) NSString *videoId;
@property (nonatomic) CGSize videoSize;
@property (nonatomic) NSUInteger loadingViewColorType;
+ (BOOL)isProviderSupported:(NSString *)provider;
@end
@interface ITBVideoFileBlock : ITBBlock <NSSecureCoding> {
    NSString *_url;
    NSString *_thumbnailUrl;
    double _duration;
    CGFloat _width;
    CGFloat _height;
    CGFloat _padding;
    CGFloat _cornerRadius;
    NSUInteger _loadingViewColorType;
}
@property (nonatomic, copy) NSString *url;
@property (nonatomic, copy) NSString *thumbnailUrl;
@property (nonatomic) double duration;
@property (nonatomic) CGFloat width;
@property (nonatomic) CGFloat height;
@property (nonatomic) CGFloat padding;
@property (nonatomic) CGFloat cornerRadius;
@property (nonatomic) NSUInteger loadingViewColorType;
@end
