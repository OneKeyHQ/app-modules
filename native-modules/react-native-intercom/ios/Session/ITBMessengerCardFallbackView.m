#import "ITBMessengerCardFallbackView.h"
#import "ICPMessengerCardFallbackView.h"
#import "ICPTextInputPayload.h"
// Delegate signatures are inferred from original call sites; the opener protocol definition is absent.
@interface NSObject (ITBFallbackDelegateCalls)
- (void)reloadRequiredForBlock:(ITBBlock *)block;
- (void)handleIntent:(id<ITBIntent>)intent forBlock:(ITBBlock *)block fromView:(UIView *)view;
@end
@implementation ITBMessengerCardFallbackView
@synthesize sheetParamaters = _sheetParamaters, urlToOpen = _urlToOpen;
@synthesize textInputTitle = _textInputTitle, textInputPlaceholder = _textInputPlaceholder;
@synthesize textInputCurrentText = _textInputCurrentText, textInputId = _textInputId;
@synthesize textInputMessengerAppId = _textInputMessengerAppId, currentIntentType = _currentIntentType;
@synthesize metricPayloadDictionary = _metricPayloadDictionary, cardfallBackComponent = _cardfallBackComponent;
@synthesize textInputCompletionBlock = _textInputCompletionBlock;
- (instancetype)init {
    self = [super init];
    if (self) self.backgroundColor = [UIColor clearColor];
    return self;
}
- (void)prepareForReuse {
    [self.cardfallBackComponent removeFromSuperview];
    self.cardfallBackComponent = nil;
    [super prepareForReuse];
}
- (void)configureWithBlock:(ITBBlock *)block {
    [super configureWithBlock:block];
    ITBMessengerCardFallbackBlock *fallback = (ITBMessengerCardFallbackBlock *)self.block;
    UIEdgeInsets insets = fallback.cardInsets;
    CGFloat width = fallback.width;
    CGFloat height = fallback.height;
    UIImage *image = fallback.loadingSpinnerImage;
    UIColor *color = fallback.loadingSpinnerColor;
    NSURL *url = fallback.fallbackURL;
    BOOL showBorder = fallback.showCardBorder;
    UIColor *primary = fallback.appPrimaryColor;
    ICPCardFallbackModel *model = [[ICPCardFallbackModel alloc] initWithInsets:insets cardSize:CGSizeMake(width, height) loadingImage:image loadingColor:color fallbackURL:url showCardBorder:showBorder appPrimaryColor:primary];
    if (self.cardfallBackComponent) [self.cardfallBackComponent updateCardFallbackModel:model];
    else [self createCardFallbackComponent:model];
    [self.cardfallBackComponent loadWebview];
    [self setNeedsLayout];
    [self layoutIfNeeded];
}
- (void)createCardFallbackComponent:(ICPCardFallbackModel *)model {
    self.cardfallBackComponent = [[ICPMessengerCardFallbackView alloc] initWithCardFallbackModel:model componentDelegate:self];
    self.cardfallBackComponent.translatesAutoresizingMaskIntoConstraints = NO;
    self.cardfallBackComponent.frame = self.frame;
    [self addSubview:self.cardfallBackComponent];
}
+ (CGSize)estimateSizeForBlock:(ITBBlock *)block withWidth:(CGFloat)width constraintWidth:(BOOL)constraintWidth {
    (void)constraintWidth;
    ITBMessengerCardFallbackBlock *fallback = (ITBMessengerCardFallbackBlock *)block;
    width = [self maxWidthForBlockContentBasedInWidth:width forBlock:block constraintWidth:YES];
    fallback.width = (NSInteger)width;
    id cached = [self fetchCachedCardHeightForURL:fallback.blockUniqueIdentifier];
    NSNumber *height = cached && [cached integerValue] ? [NSNumber numberWithInteger:[cached integerValue]] : [NSNumber numberWithInteger:150];
    fallback.height = height.integerValue;
    CGFloat resultHeight = height.floatValue;
    resultHeight = fallback.cardInsets.top + resultHeight;
    resultHeight += fallback.cardInsets.bottom;
    return CGSizeMake(width, resultHeight);
}
+ (id)fetchCachedCardHeightForURL:(NSString *)url {
    return [[NSUserDefaults standardUserDefaults] objectForKey:url];
}
+ (CGFloat)maxWidthForBlockContentBasedInWidth:(CGFloat)width forBlock:(ITBBlock *)block constraintWidth:(BOOL)constraintWidth {
    (void)block; (void)constraintWidth;
    return width > 366 ? 366 : width;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    CGFloat width = CGRectGetWidth(self.frame);
    CGFloat height = CGRectGetHeight(self.frame);
    self.cardfallBackComponent.frame = CGRectMake(0, 0, width, height);
}
- (void)reloadRequiredForComponent:(id)component {
    (void)component;
    id delegate = self.delegate;
    ITBBlock *block = self.block;
    [delegate reloadRequiredForBlock:block];
}
- (void)component:(id)component openSheetWithURL:(NSURL *)url sheetParams:(NSDictionary *)params {
    (void)component;
    self.sheetParamaters = params;
    self.urlToOpen = url;
    self.currentIntentType = 3;
    id opener = self.intentOpener;
    ITBBlock *block = self.block;
    [opener handleIntent:self forBlock:block fromView:self];
}
- (void)component:(id)component handleTextInput:(ICPTextInputPayload *)payload {
    (void)component;
    self.textInputId = payload.inputId;
    self.textInputTitle = payload.title;
    self.textInputCurrentText = payload.currentText;
    self.textInputPlaceholder = payload.placeholder;
    self.currentIntentType = 4;
    self.textInputCompletionBlock = payload.completionBlock;
    self.textInputMessengerAppId = payload.messengerAppId;
    id opener = self.intentOpener;
    ITBBlock *block = self.block;
    [opener handleIntent:self forBlock:block fromView:self];
}
- (void)component:(id)component openURL:(NSURL *)url {
    (void)component;
    self.currentIntentType = 0;
    self.urlToOpen = url;
    id opener = self.intentOpener;
    ITBBlock *block = self.block;
    [opener handleIntent:self forBlock:block fromView:self];
}
- (void)component:(id)component logMetricEvent:(NSDictionary *)event {
    (void)component;
    self.currentIntentType = 5;
    self.metricPayloadDictionary = event;
    id opener = self.intentOpener;
    ITBBlock *block = self.block;
    [opener handleIntent:self forBlock:block fromView:self];
}
- (ITBIntentType)intentType { return (ITBIntentType)self.currentIntentType; }
- (NSURL *)intentUrl { return self.urlToOpen; }
- (NSDictionary *)sheetParams { return self.sheetParamaters; }
- (NSString *)inputTitle { return self.textInputTitle; }
- (NSString *)inputPlaceholder { return self.textInputPlaceholder; }
- (NSString *)inputCurrentText { return self.textInputCurrentText; }
- (void (^)(UIAlertController *))inputTextCompletionBlock { return self.textInputCompletionBlock; }
- (NSString *)messengerAppId { return self.textInputMessengerAppId; }
- (NSDictionary *)metricPayload { return self.metricPayloadDictionary; }
- (void)dealloc { self.cardfallBackComponent = nil; }
@end
