#import <UIKit/UIKit.h>
@interface ICPTextInputPayload : NSObject {
    NSString *_title;
    NSString *_placeholder;
    NSString *_currentText;
    NSString *_inputId;
    NSString *_messengerAppId;
    void (^_completionBlock)(UIAlertController *);
}
@property (nonatomic, strong) NSString *title;
@property (nonatomic, strong) NSString *placeholder;
@property (nonatomic, strong) NSString *currentText;
@property (nonatomic, strong) NSString *inputId;
@property (nonatomic, strong) NSString *messengerAppId;
// Parameter type follows the original callback's textFields access.
@property (nonatomic, copy) void (^completionBlock)(UIAlertController *);
- (BOOL)isEqualToTextInputPayload:(ICPTextInputPayload *)payload;
@end
