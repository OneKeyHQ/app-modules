#import <UIKit/UIKit.h>
@interface ICPCardFallbackModel : NSObject {
    BOOL _webviewLoaded;
    BOOL _showCardBorder;
    NSURL *_fallbackURL;
    UIColor *_loadingSpinnerColor;
    UIImage *_loadingSpinnerImage;
    UIColor *_appPrimaryColor;
    NSString *_cardId;
    CGSize _cardSize;
    UIEdgeInsets _cardInsets;
}
@property (nonatomic, strong) NSURL *fallbackURL;
@property (nonatomic, strong) UIColor *loadingSpinnerColor;
@property (nonatomic, strong) UIImage *loadingSpinnerImage;
@property (nonatomic) UIEdgeInsets cardInsets;
@property (nonatomic) CGSize cardSize;
@property (nonatomic) BOOL webviewLoaded;
@property (nonatomic) BOOL showCardBorder;
@property (nonatomic, strong) UIColor *appPrimaryColor;
@property (nonatomic, strong) NSString *cardId;
- (instancetype)initWithInsets:(UIEdgeInsets)insets cardSize:(CGSize)size loadingImage:(UIImage *)image loadingColor:(UIColor *)color fallbackURL:(NSURL *)url showCardBorder:(BOOL)showBorder appPrimaryColor:(UIColor *)primaryColor;
- (BOOL)isEqualToCardFallbackModel:(ICPCardFallbackModel *)model;
+ (NSString *)uniqueIdentifier:(NSURL *)url;
@end
