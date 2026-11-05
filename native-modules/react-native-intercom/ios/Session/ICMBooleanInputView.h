#import <UIKit/UIKit.h>
#import "ICMInputViewProtocol.h"
#import "ICMFormInputDependencies.h"
@interface ICMBooleanInputView : UIView <ICMSubmittableInputViewProtocol>
@property (nonatomic) BOOL isRequired;
@property (nonatomic, strong) NSNumber *value;
@property (nonatomic, strong) UIButton *trueButton;
@property (nonatomic, strong) UIButton *falseButton;
@property (nonatomic, strong) UIView *dividerView;
@property (nonatomic, weak) id<ICMInputViewDelegate> inputViewDelegate;
@property (nonatomic) BOOL showLoading;
@property (nonatomic) BOOL verticalLayout;
@property (nonatomic) BOOL disableAfterSelect;
@property (nonatomic, readonly) BOOL fieldIsEditable;
@property (strong) id inputValue;
- (instancetype)initWithProperties:(ICMRecoveredInputProperties *)properties delegate:(id<ICMInputViewDelegate>)delegate options:(NSUInteger)options;
- (void)buildButtons;
- (UIButton *)buildBooleanButton:(NSString *)title backgroundImage:(UIImage *)image backgroundImageSelected:(UIImage *)selected tapSelector:(SEL)selector;
- (void)buildTrueButton;
- (void)buildFalseButton;
- (void)buildDivider;
- (void)layoutButtons;
- (void)trueButtonTapped;
- (void)falseButtonTapped;
- (void)setTrueButtonSelected:(BOOL)enabled;
- (void)setFalseButtonSelected:(BOOL)enabled;
- (void)showLoadingState:(UIButton *)button;
- (void)showTrueButtonLoadingState;
- (void)showFalseButtonLoadingState;
- (void)resetLoadingState;
- (void)configureDefaults;
@end
