#import "ITBIntent.h"
@class ICPTextInputPayload, UIAlertController;
@protocol ITBOpenSheetIntent <ITBIntent>
- (NSDictionary *)sheetParams;
@end
@protocol ITBInputTextIntent <ITBIntent>
- (NSString *)inputTitle;
- (NSString *)inputPlaceholder;
- (NSString *)inputCurrentText;
// The block object's parameter type is inferred from the original invocation sites.
- (void (^)(UIAlertController *))inputTextCompletionBlock;
@end
@protocol ITBMetricEventIntent <ITBIntent>
- (NSDictionary *)metricPayload;
@end
@protocol ICPAppComponentDelegate <NSObject>
- (void)reloadRequiredForComponent:(id)component;
- (void)component:(id)component openSheetWithURL:(NSURL *)url sheetParams:(NSDictionary *)params;
- (void)component:(id)component handleTextInput:(ICPTextInputPayload *)payload;
- (void)component:(id)component openURL:(NSURL *)url;
- (void)component:(id)component logMetricEvent:(NSDictionary *)event;
@end
