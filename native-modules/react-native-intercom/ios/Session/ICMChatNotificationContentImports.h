#pragma once
#import <UIKit/UIKit.h>
@class ICMConversationPart;
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate27ChatNotificationContentView")))
__attribute__((swift_name("ChatNotificationContentView")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredChatNotificationContentView : UIView
- (instancetype _Null_unspecified)initWithPreventFullLayoutStyle:(BOOL)prevent blocksWidth:(CGFloat)width config:(id _Null_unspecified)config themeProvider:(id _Null_unspecified)provider timestampCalculator:(id _Null_unspecified)calculator;
@property (nonatomic, strong, null_unspecified) UILabel *participantNameAndTimeLabel;
- (void)configureFor:(ICMConversationPart * _Null_unspecified)part ticket:(id _Null_unspecified)ticket;
- (NSString * _Null_unspecified)participantNameAndTimeLabelTextFor:(ICMConversationPart * _Null_unspecified)part;
- (void)setContentAlphaTo:(CGFloat)alpha;
@end
__attribute__((swift_name("TimestampCalculator")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMTimestampCalculator : NSObject
+ (instancetype _Null_unspecified)sharedInstance;
- (NSString * _Nullable)timestampTextForUpdatedDate:(NSDate * _Nullable)date;
@end
