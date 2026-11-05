#import "ITBBlock.h"
@interface ITBMessengerCardFallbackBlock : ITBBlock {
    BOOL _showCardBorder;
    NSInteger _height;
    NSInteger _width;
    NSURL *_fallbackURL;
    UIColor *_loadingSpinnerColor;
    UIImage *_loadingSpinnerImage;
    UIColor *_appPrimaryColor;
    UIEdgeInsets _cardInsets;
}
@property (nonatomic) NSInteger height;
@property (nonatomic) NSInteger width;
@property (nonatomic, strong) NSURL *fallbackURL;
@property (nonatomic, strong) UIColor *loadingSpinnerColor;
@property (nonatomic, strong) UIImage *loadingSpinnerImage;
@property (nonatomic) UIEdgeInsets cardInsets;
@property (nonatomic) BOOL showCardBorder;
@property (nonatomic, strong) UIColor *appPrimaryColor;
@end
