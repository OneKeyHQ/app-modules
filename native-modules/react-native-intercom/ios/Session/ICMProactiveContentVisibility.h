// Objective-C alias for the genuine recovered Swift visibility state.
#import <Foundation/Foundation.h>
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate29ICMProactiveContentVisibility")))
@interface ICMRecoveredProactiveContentVisibility : NSObject
+ (instancetype)shared;
@property (nonatomic) BOOL carouselsEnabled;
@property (nonatomic) BOOL surveysEnabled;
- (void)setProactiveVisibleWithCarousels:(BOOL)carousels surveys:(BOOL)surveys;
@end
