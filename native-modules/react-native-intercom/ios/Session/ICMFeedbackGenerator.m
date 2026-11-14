#import "ICMFeedbackGenerator.h"

@implementation ICMFeedbackGenerator
+ (void)feedbackWithType:(NSUInteger)type {
    switch (type) {
        case 0: [self generateSelectionFeedback]; break;
        case 1: [self generateNotificationFeedbackWithType:UINotificationFeedbackTypeError]; break;
        case 2: [self generateNotificationFeedbackWithType:UINotificationFeedbackTypeSuccess]; break;
        case 3: [self generateNotificationFeedbackWithType:UINotificationFeedbackTypeWarning]; break;
        default: break;
    }
}
+ (void)generateNotificationFeedbackWithType:(UINotificationFeedbackType)type {
    UINotificationFeedbackGenerator *generator = [UINotificationFeedbackGenerator new];
    [generator notificationOccurred:type];
}
+ (void)generateSelectionFeedback {
    UISelectionFeedbackGenerator *generator = [UISelectionFeedbackGenerator new];
    [generator selectionChanged];
}
@end
