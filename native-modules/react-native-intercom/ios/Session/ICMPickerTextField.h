#import "ICMTextField.h"

NS_ASSUME_NONNULL_BEGIN
@interface ICMPickerTextField : ICMTextField <UIPickerViewDelegate, UIPickerViewDataSource>
@property (nonatomic, strong) NSMutableArray *options;
@property (nonatomic, strong) UIPickerView *picker;
@property (nonatomic, strong) UIBarButtonItem *cancelToolbarButton;
@property (nonatomic, strong) UIBarButtonItem *entryCompleteToolbarButton;
@property (nonatomic, strong, nullable) UIColor *normalStatePlaceholderTextColor;
@property (nonatomic, strong, nullable) UIColor *selectedStateBackgroundColor;
@property (nonatomic, strong, nullable) UIColor *selectedStateTextColor;
@property (nonatomic, strong, nullable) UIColor *selectedStateBorderColor;
@property (nonatomic, strong, nullable) UIColor *selectedStateDropDownIconColor;
- (nullable instancetype)initWithProperties:(ICMRecoveredInputProperties *)properties options:(NSArray *)options delegate:(nullable id<ICMInputViewDelegate>)delegate;
- (void)configurePicker;
- (void)dropdownIcon:(nullable UIColor *)tint;
- (void)configureDropDownIconWithTintColor:(nullable UIColor *)color;
- (void)addTextFieldNotificationsToUpdateColors;
- (void)configureForEditingStateBackgroundColor:(UIColor *)color;
- (void)setNormalStateColors;
- (void)setEditingStateColors;
- (void)entryCompleteButtonTapped:(id)sender;
- (void)cancelTapped:(id)sender;
@end
NS_ASSUME_NONNULL_END
