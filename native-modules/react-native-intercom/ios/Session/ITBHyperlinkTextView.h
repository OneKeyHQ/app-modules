#import <UIKit/UIKit.h>
@interface ITBHyperlinkTextView : UITextView
- (void)setupLinkedTextView;
- (void)handleLinkTapGestureRecognizer:(UITapGestureRecognizer *)recognizer;
@end
