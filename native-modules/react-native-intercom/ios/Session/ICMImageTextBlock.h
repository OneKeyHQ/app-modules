#import "ITBBlock.h"
@interface ICMImageTextBlock : ITBBlock <NSSecureCoding> {
    NSString *_title;
    NSString *_url;
    CGFloat _width;
    CGFloat _height;
    UIColor *_textColor;
    UIColor *_titleTextColor;
    NSUInteger _loadingViewColorType;
}
@property (nonatomic, copy) NSString *title;
@property (nonatomic, copy) NSString *url;
@property (nonatomic) CGFloat width;
@property (nonatomic) CGFloat height;
@property (nonatomic, strong) UIColor *textColor;
@property (nonatomic, strong) UIColor *titleTextColor;
@property (nonatomic) NSUInteger loadingViewColorType;
+ (BOOL)validateNumber:(id)value;
@end
