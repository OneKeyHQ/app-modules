#import "ITBBlock.h"
@interface ITBNotificationChannel : ITBBlock {
    BOOL _defaultChannel;
    NSString *_label;
    NSAttributedString *_labelAttributedString;
    NSString *_name;
    NSString *_value;
}
@property (nonatomic) BOOL defaultChannel;
@property (nonatomic, copy) NSString *label;
@property (nonatomic, copy) NSAttributedString *labelAttributedString;
@property (nonatomic, copy) NSString *name;
@property (nonatomic, copy) NSString *value;
@end
@interface ITBNotificationCardBlock : ITBBlock {
    BOOL _displayPlaceholder;
    NSString *_title;
    NSAttributedString *_titleAttributedString;
    NSArray *_notificationChannels;
    NSString *_userInput;
    NSAttributedString *_userInputAttributedString;
    UIEdgeInsets _insets;
}
@property (nonatomic, copy) NSString *title;
@property (nonatomic, copy) NSAttributedString *titleAttributedString;
@property (nonatomic, copy) NSArray *notificationChannels;
@property (nonatomic, copy) NSString *userInput;
@property (nonatomic, copy) NSAttributedString *userInputAttributedString;
@property (nonatomic) BOOL displayPlaceholder;
@property (nonatomic) UIEdgeInsets insets;
- (void)setChannels:(NSDictionary *)dictionary appearance:(id)appearance;
@end
