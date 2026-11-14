#import "ITBBlock.h"
#import "ITBIntent.h"
@interface ITBButtonBlock : ITBBlock <ITBIntent> {
    NSString *_url;
    UIFont *_font;
    UIColor *_textColor;
    UIColor *_backgroundColor;
}
@property (nonatomic, copy) NSString *url;
@property (nonatomic, strong) UIFont *font;
@property (nonatomic, strong) UIColor *textColor;
@property (nonatomic, strong) UIColor *backgroundColor;
@end
@interface ITBFacebookBlock : ITBButtonBlock
@end
@interface ITBTwitterBlock : ITBButtonBlock
@end
