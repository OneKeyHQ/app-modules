#import <UIKit/UIKit.h>

@interface ICMFeedbackGenerator : NSObject
+ (void)feedbackWithType:(NSUInteger)type;
+ (void)generateNotificationFeedbackWithType:(UINotificationFeedbackType)type;
+ (void)generateSelectionFeedback;
@end
