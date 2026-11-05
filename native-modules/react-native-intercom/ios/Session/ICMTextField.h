#import <UIKit/UIKit.h>
#import "ICMInputViewProtocol.h"
#import "ICMFormInputDependencies.h"
@class ICMSubmitButton;

NS_ASSUME_NONNULL_BEGIN
@interface ICMTextField : UITextField <ICMSubmittableInputViewProtocol>
@property (strong, nullable) id inputValue;
@property (nonatomic) BOOL isRequired;
@property (nonatomic) BOOL allowSubmission;
@property (nonatomic) BOOL fieldIsEditable;
@property (nonatomic) NSInteger characterLimit;
@property (nonatomic) NSUInteger cursorInset;
@property (nonatomic, strong, nullable) ICMSubmitButton *submitButton;
@property (nonatomic, weak, nullable) id<ICMInputViewDelegate> inputViewDelegate;
@property (nonatomic, strong, nullable) id value;
- (nullable instancetype)initWithProperties:(ICMRecoveredInputProperties *)properties delegate:(nullable id<ICMInputViewDelegate>)delegate;
- (void)configureDefaults;
- (NSArray *)validators;
- (BOOL)notRequiredAndEmpty;
- (CGRect)getTextFieldRect:(CGRect)bounds;
- (BOOL)isClearButtonVisible;
- (void)createSubmitButton;
- (void)submitButtonTapped;
- (void)addTextFieldNotifications;
- (void)didBeginEditing;
- (void)textDidChange;
- (void)didEndEditing;
- (void)showValidInputCheckmarkIfRelevant;
@end
NS_ASSUME_NONNULL_END
