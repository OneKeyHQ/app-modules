#import "ITBTextBlock.h"
#import "ICMBlockAppearance.h"

@implementation ITBCodeBlock
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    UIFont *font = [appearance codeFont];
    UIColor *color = [appearance codeTextColor];
    self = [super initWithDictionary:dictionary appearance:appearance font:font textColor:color];
    if (self) {
        self.backgroundColor = [appearance codeBackgroundColor];
        self.layoutInsets = [appearance codeInsets];
    }
    return self;
}
- (void)updateAppearance:(id)appearance font:(UIFont *)font textColor:(UIColor *)color {
    (void)font;
    (void)color;
    UIFont *codeFont = [appearance codeFont];
    UIColor *codeColor = [appearance codeTextColor];
    [super updateAppearance:appearance font:codeFont textColor:codeColor];
    self.backgroundColor = [appearance codeBackgroundColor];
    self.layoutInsets = [appearance codeInsets];
}
- (NSInteger)type { return 9; }
@end
