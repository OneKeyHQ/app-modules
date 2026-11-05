#import "ITBHyperlinkTextView.h"

@implementation ITBHyperlinkTextView
- (instancetype)initWithFrame:(CGRect)frame textContainer:(NSTextContainer *)container {
    self = [super initWithFrame:frame textContainer:container];
    if (self) [self setupLinkedTextView];
    return self;
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super initWithCoder:coder];
    if (self) [self setupLinkedTextView];
    return self;
}
- (void)awakeFromNib {
    [super awakeFromNib];
    [self setupLinkedTextView];
}
- (void)setupLinkedTextView {
    UITapGestureRecognizer *recognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleLinkTapGestureRecognizer:)];
    recognizer.cancelsTouchesInView = NO;
    recognizer.delaysTouchesBegan = NO;
    recognizer.delaysTouchesEnded = NO;
    [self addGestureRecognizer:recognizer];
}
- (void)handleLinkTapGestureRecognizer:(UITapGestureRecognizer *)recognizer {
    CGPoint point = [recognizer locationInView:self];
    UITextPosition *first = [self closestPositionToPoint:point];
    UITextPosition *last = [self positionFromPosition:first offset:1];
    if (!last) {
        first = [self positionFromPosition:first offset:-1];
        last = [self positionFromPosition:first offset:1];
        if (!last) return;
    }
    UITextRange *range = [self textRangeFromPosition:first toPosition:last];
    NSUInteger start = [self offsetFromPosition:self.beginningOfDocument toPosition:range.start];
    NSUInteger end = [self offsetFromPosition:self.beginningOfDocument toPosition:range.end];
    if (start == NSNotFound || start == end || end > self.attributedText.length) return;
    NSRange characterRange = NSMakeRange(start, end - start);
    NSMutableAttributedString *substring = [[self.attributedText attributedSubstringFromRange:characterRange] mutableCopy];
    id link = [substring attribute:NSLinkAttributeName atIndex:0 effectiveRange:nil];
    [substring addAttribute:NSBackgroundColorAttributeName value:UIColor.redColor range:NSMakeRange(0, 1)];
    if (!link) return;
    NSURL *url;
    if ([link isKindOfClass:[NSURL class]]) url = link;
    else if ([link isKindOfClass:[NSString class]]) url = [NSURL URLWithString:link];
    else return;
    if (self.delegate && [self.delegate respondsToSelector:@selector(textView:shouldInteractWithURL:inRange:interaction:)]) {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
        if (![self.delegate textView:self shouldInteractWithURL:url inRange:characterRange interaction:UITextItemInteractionInvokeDefaultAction]) return;
#pragma clang diagnostic pop
    }
    [UIApplication.sharedApplication openURL:url options:@{} completionHandler:nil];
}
@end
