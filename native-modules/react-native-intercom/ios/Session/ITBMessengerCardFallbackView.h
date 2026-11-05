#import "ITBBlockView.h"
#import "ITBMessengerCardFallbackBlock.h"
#import "ITBMessengerCardIntents.h"
#import "ICPCardFallbackModel.h"
@class ICPMessengerCardFallbackView;
@interface ITBMessengerCardFallbackView : ITBBlockView <ITBOpenSheetIntent, ITBInputTextIntent, ITBMetricEventIntent, ICPAppComponentDelegate> {
    NSDictionary *_sheetParamaters;
    NSURL *_urlToOpen;
    NSString *_textInputTitle;
    NSString *_textInputPlaceholder;
    NSString *_textInputCurrentText;
    NSString *_textInputId;
    NSString *_textInputMessengerAppId;
    NSInteger _currentIntentType;
    NSDictionary *_metricPayloadDictionary;
    ICPMessengerCardFallbackView *_cardfallBackComponent;
    void (^_textInputCompletionBlock)(UIAlertController *);
}
@property (nonatomic, strong) NSDictionary *sheetParamaters;
@property (nonatomic, strong) NSURL *urlToOpen;
@property (nonatomic, strong) NSString *textInputTitle;
@property (nonatomic, strong) NSString *textInputPlaceholder;
@property (nonatomic, strong) NSString *textInputCurrentText;
@property (nonatomic, strong) NSString *textInputId;
@property (nonatomic, strong) NSString *textInputMessengerAppId;
@property (nonatomic) NSInteger currentIntentType;
@property (nonatomic, strong) NSDictionary *metricPayloadDictionary;
@property (nonatomic, strong) ICPMessengerCardFallbackView *cardfallBackComponent;
@property (nonatomic, copy) void (^textInputCompletionBlock)(UIAlertController *);
- (void)createCardFallbackComponent:(ICPCardFallbackModel *)model;
+ (id)fetchCachedCardHeightForURL:(NSString *)url;
@end
