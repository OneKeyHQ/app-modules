#import "ICMLoadingTableViewCell.h"
@implementation ICMLoadingTableViewCell
- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)identifier {
    self = [super initWithStyle:style reuseIdentifier:identifier];
    if (self) { self.selectionStyle = UITableViewCellSelectionStyleNone; [self setupLoadingView]; }
    return self;
}
- (void)setupLoadingView {
    self.loadingView = [ICMNativeLoadingSpinnerView new];
    self.loadingView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.contentView addSubview:self.loadingView];
    [NSLayoutConstraint activateConstraints:@[
        [self.loadingView.topAnchor constraintEqualToAnchor:self.contentView.topAnchor constant:16],
        [self.loadingView.bottomAnchor constraintEqualToAnchor:self.contentView.bottomAnchor],
        [self.loadingView.centerXAnchor constraintEqualToAnchor:self.contentView.centerXAnchor]
    ]];
}
- (void)startSpinning { [self.loadingView startSpinning]; }
@end
