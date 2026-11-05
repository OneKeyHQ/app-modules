#import "ICMTextField.h"
@interface ICMDateTextField : ICMTextField <ICMSubmittableInputViewProtocol, UIPickerViewDelegate>
@property (nonatomic) BOOL allowTimeSelection;
@property (nonatomic, strong) NSDateFormatter *dateFormatter;
@property (nonatomic, strong) UIDatePicker *datePicker;
@property (nonatomic, strong) UIBarButtonItem *cancelToolbarButton;
@property (nonatomic, strong) UIBarButtonItem *entryCompleteToolbarButton;
- (instancetype)initWithProperties:(ICMRecoveredInputProperties *)properties allowTimeSelection:(BOOL)allowTimeSelection delegate:(id<ICMInputViewDelegate>)delegate;
- (instancetype)initWithProperties:(ICMRecoveredInputProperties *)properties allowTimeSelection:(BOOL)allowTimeSelection delegate:(id<ICMInputViewDelegate>)delegate dateFormatter:(NSDateFormatter *)dateFormatter;
+ (NSDateFormatter *)defaultDateFormatter;
- (void)configurePicker;
- (void)configureDateIcon;
- (void)textFieldDidBeginEditing:(id)sender;
- (void)dateChanged:(UIDatePicker *)picker;
- (NSString *)formattedStringFromDate:(NSDate *)date;
- (void)entryCompleteButtonTapped:(id)sender;
- (void)cancelTapped:(id)sender;
@end
