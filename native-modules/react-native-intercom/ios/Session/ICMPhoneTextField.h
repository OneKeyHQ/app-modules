#import "ICMTextField.h"
#import "ICMPhoneValidator.h"

@interface ICMPhoneTextField : ICMTextField
@property (nonatomic, strong) ICMPhoneValidator *phoneValidator;
@property (nonatomic, strong) NSString *previousEmoji;
@property (nonatomic, weak) UIImageView *flagView;
- (void)configureInputViewFromValue:(id)value;
- (void)textFieldDidChange:(UITextField *)sender;
- (NSString *)normalizedPhoneNumber:(NSString *)number;
- (void)setInputFlagEmoji:(NSString *)emoji;
- (UIImage *)imageFromEmoji:(NSString *)emoji;
- (CGRect)accessoryRectForView:(UIView *)view bounds:(CGRect)bounds;
- (CGRect)getPhoneFieldRect:(CGRect)bounds;
@end
