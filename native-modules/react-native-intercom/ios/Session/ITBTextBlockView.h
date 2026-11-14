#import "ITBBlockView.h"
#import "ITBHyperlinkTextView.h"
#import "ITBIntent.h"

@interface ITBTextBlockViewIntent : NSObject <ITBIntent> {
    NSURL *_intentUrl;
}
@property (nonatomic, strong) NSURL *intentUrl;
@end

@interface ITBTextBlockView : ITBBlockView <UITextViewDelegate> {
    ITBHyperlinkTextView *_textView;
    UIEdgeInsets _layoutInsets;
}
@property (nonatomic, strong) ITBHyperlinkTextView *textView;
@property (nonatomic) UIEdgeInsets layoutInsets;
- (NSString *)accessibilityText;
- (void)createTextView;
+ (void)setup;
+ (void)tearDown;
@end
