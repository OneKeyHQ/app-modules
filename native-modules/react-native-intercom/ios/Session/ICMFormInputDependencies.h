#import "ICMBlockAppearanceDependencies.h"
#import "ICMUtils+Fonts.h"
#import "ICMFeedbackGenerator.h"

NS_ASSUME_NONNULL_BEGIN

__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate15InputProperties")))
__attribute__((swift_name("InputProperties")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredInputProperties : NSObject
@property (nonatomic, readonly, nullable) id initialValue;
@property (nonatomic, readonly) BOOL allowSubmission;
@property (nonatomic, readonly) BOOL valueIsEditable;
- (nonnull instancetype)initWithInitialValue:(nullable id)value allowSubmission:(BOOL)allowSubmission valueIsEditable:(BOOL)valueIsEditable;
@end

@interface ICMRecoveredTheme (OriginalFormInputColors)
@property (nonatomic, readonly) UIColor *inputAlt;
@property (nonatomic, readonly) UIColor *neutralBorderEmphasis;
@property (nonatomic, readonly) UIColor *textError;
@property (nonatomic, readonly) UIColor *iconMuted;
@property (nonatomic, readonly) UIColor *successContainer;
@end

NS_ASSUME_NONNULL_END
