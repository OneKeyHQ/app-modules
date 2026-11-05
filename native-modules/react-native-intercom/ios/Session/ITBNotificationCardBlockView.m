// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBNotificationCardBlockView.h"
#import <math.h>
@implementation ITBNotificationCardBlockView
@synthesize containerView = _containerView, userInputView = _userInputView, notificationTitle = _notificationTitle;
@synthesize channelsView = _channelsView, userInputLabel = _userInputLabel, containerInsets = _containerInsets;
@synthesize displayChannelsView = _displayChannelsView, displayPlaceholder = _displayPlaceholder;
- (instancetype)init {
    self = [super init];
    if (self) {
        _containerView = [UIView new];
        _notificationTitle = [UILabel new];
        _notificationTitle.numberOfLines = 0;
        _notificationTitle.alpha = 0.5;
        _channelsView = [UIView new];
        _channelsView.clipsToBounds = YES;
        _userInputView = [UIView new];
        _userInputLabel = [UILabel new];
        _userInputLabel.alpha = 0.5;
        [_userInputView addSubview:_userInputLabel];
        [_containerView addSubview:_notificationTitle];
        [_containerView addSubview:_channelsView];
        [_containerView addSubview:_userInputView];
        [self addSubview:_containerView];
    }
    return self;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    CGSize size = self.frame.size;
    CGFloat y = self.containerInsets.top;
    CGFloat top = self.containerInsets.top;
    CGFloat height = size.height - (top + self.containerInsets.bottom);
    CGFloat x = self.containerInsets.left;
    CGFloat left = self.containerInsets.left;
    CGFloat width = size.width - (left + self.containerInsets.right);
    CGRect container = CGRectMake(x, y, width, height);
    self.containerView.frame = container;
    NSAttributedString *title = self.notificationTitle.attributedText;
    CGRect titleRect = [title boundingRectWithSize:CGSizeMake(CGRectGetWidth(container), CGFLOAT_MAX) options:1 context:nil];
    self.notificationTitle.frame = CGRectMake(0, 0, CGRectGetWidth(container), titleRect.size.height);
    CGFloat inputHeight = self.displayPlaceholder ? 44 : 19;
    CGRect channels = CGRectZero;
    CGFloat inputY;
    if (self.displayChannelsView) {
        channels = CGRectMake(0, CGRectGetMaxY(titleRect) + 10, CGRectGetWidth(container), 20);
        inputY = CGRectGetMaxY(channels) + 10;
        inputY += 5;
    } else inputY = CGRectGetMaxY(titleRect) + 10;
    self.channelsView.frame = channels;
    CGRect input = CGRectMake(0, inputY, CGRectGetWidth(container), inputHeight);
    self.userInputView.frame = input;
    if (self.displayChannelsView)
        [self configureChannelsViewWithChannels:((ITBNotificationCardBlock *)self.block).notificationChannels];
    CGRect label;
    if (self.displayPlaceholder)
        label = CGRectMake(10, 12.5, CGRectGetWidth(input) - 20, CGRectGetHeight(input) - 25);
    else label = CGRectMake(0, 0, CGRectGetWidth(input), CGRectGetHeight(input));
    self.userInputLabel.frame = CGRectIntegral(label);
}
- (void)configureChannelsViewWithChannels:(NSArray *)channels {
    CGFloat offset = 0;
    for (ITBNotificationChannel *channel in channels) {
        UILabel *label = [UILabel new];
        label.alpha = 0.5;
        [self.channelsView addSubview:label];
        label.attributedText = channel.labelAttributedString;
        [label sizeToFit];
        CGRect frame = label.frame;
        UIUserInterfaceLayoutDirection direction = [UIApplication sharedApplication].userInterfaceLayoutDirection;
        if (direction == 0) frame.origin.x = offset;
        else frame.origin.x = CGRectGetWidth(self.channelsView.bounds) - offset - CGRectGetWidth(label.frame);
        label.frame = CGRectIntegral(frame);
        if (offset == 0) [self addUnderlineToLabel:label];
        CGFloat advance;
        if (direction == 0) advance = CGRectGetMaxX(frame);
        else advance = CGRectGetWidth(self.channelsView.bounds) - CGRectGetMinX(frame);
        offset += advance + 25;
    }
}
- (void)addUnderlineToLabel:(UILabel *)label {
    UIView *underline = [UIView new];
    underline.alpha = 0.5;
    [self.channelsView addSubview:underline];
    NSRange range = NSMakeRange(0, 1);
    NSDictionary *attributes = [label.attributedText attributesAtIndex:0 effectiveRange:&range];
    underline.backgroundColor = [attributes objectForKey:NSForegroundColorAttributeName];
    CGFloat x = CGRectGetMinX(label.frame);
    CGFloat width = CGRectGetWidth(label.frame);
    CGFloat y = CGRectGetMaxY(label.frame) - 1;
    underline.frame = CGRectMake(x, y, width, 1);
}
+ (CGSize)estimateSizeForBlock:(ITBBlock *)block withWidth:(CGFloat)width constraintWidth:(BOOL)constraintWidth {
    ITBNotificationCardBlock *card = (ITBNotificationCardBlock *)block;
    if (!card.titleAttributedString.length) return CGSizeZero;
    CGFloat innerWidth = width - card.insets.left;
    innerWidth -= card.insets.right;
    CGRect title = CGRectIntegral([card.titleAttributedString boundingRectWithSize:CGSizeMake(innerWidth, CGFLOAT_MAX) options:1 context:nil]);
    CGFloat height = title.size.height + card.insets.top;
    height += card.insets.bottom;
    height = fma((double)(unsigned int)[[self class] shouldDisplayChannelsView:card], 35, height);
    height += card.displayPlaceholder ? 54 : 29;
    if (constraintWidth) {
        CGFloat channels = [[self class] calculateChannelViewWidthForChannels:card.notificationChannels inBoundingSize:CGSizeMake(innerWidth, CGFLOAT_MAX)];
        CGRect input = [card.userInputAttributedString boundingRectWithSize:CGSizeMake(innerWidth, CGFLOAT_MAX) options:1 context:nil];
        CGFloat result = channels < input.size.width ? input.size.width : channels;
        if (result < title.size.width) result = title.size.width;
        width = card.insets.left + result;
        width = card.insets.right + width;
    }
    return CGSizeMake(width, height);
}
+ (CGFloat)calculateChannelViewWidthForChannels:(NSArray *)channels inBoundingSize:(CGSize)size {
    if (!channels.count) return 0;
    CGFloat sum = 0;
    for (ITBNotificationChannel *channel in channels)
        sum += [channel.labelAttributedString boundingRectWithSize:size options:1 context:nil].size.width;
    return fma((double)(NSUInteger)(channels.count - 1), 25, sum);
}
- (void)configureUserInputWithBlock:(ITBNotificationCardBlock *)block {
    self.userInputLabel.attributedText = block.userInputAttributedString;
    self.displayPlaceholder = block.displayPlaceholder;
    if (block.displayPlaceholder) {
        UIColor *color = [UIColor colorWithRed:0.855 green:0.875 blue:0.89 alpha:1];
        self.userInputView.layer.borderColor = color.CGColor;
        self.userInputView.layer.borderWidth = 0.5;
        self.userInputView.layer.cornerRadius = 4;
    }
}
- (void)configureWithBlock:(ITBBlock *)block {
    [super configureWithBlock:block];
    ITBNotificationCardBlock *card = (ITBNotificationCardBlock *)block;
    self.notificationTitle.attributedText = card.titleAttributedString;
    [self configureUserInputWithBlock:card];
    self.containerInsets = card.insets;
    self.displayChannelsView = [[self class] shouldDisplayChannelsView:card];
}
+ (BOOL)shouldDisplayChannelsView:(ITBNotificationCardBlock *)block {
    return block.displayPlaceholder && block.notificationChannels.count > 1;
}
@end
