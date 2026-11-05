#import "ICMScreenActionViewController.h"
#import "ICMPermissionScreenActionView.h"
#import "ICMAutoLayout.h"
@implementation ICMScreenActionViewController
- (instancetype)initWithScreenActions:(NSArray<ICMScreenAction *> *)actions screenDelegate:(id<ICMScreenDelegate>)delegate {
    self = [super init];
    if (self) { _screenActions = actions; _screenDelegate = delegate; }
    return self;
}
- (void)viewDidLoad { [super viewDidLoad]; self.view.backgroundColor = UIColor.clearColor; [self setupScreenActionView]; }
- (void)setupScreenActionView {
    Class viewClass = [self isPermissionAction] ? ICMPermissionScreenActionView.class : ICMScreenActionView.class;
    self.screenActionView = [[viewClass alloc] initWithScreenActions:self.screenActions screenDelegate:self.screenDelegate];
    [self.view addSubview:self.screenActionView];
    [ICMAutoLayout pinView:self.screenActionView edges:14 toSameEdgesOfView:self.view];
    NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:self.screenActionView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTop multiplier:1 constant:5];
    top.priority = 999; top.active = YES;
}
- (BOOL)isPermissionAction {
    for (ICMScreenAction *action in self.screenActions) if ([action isMemberOfClass:ICMPermissionScreenAction.class]) return YES;
    return NO;
}
- (void)viewDidLayoutSubviews { [self calculatePreferredSize]; }
- (void)calculatePreferredSize {
    CGFloat width = self.view.bounds.size.width;
    CGFloat height = UILayoutFittingCompressedSize.height;
    self.preferredContentSize = [self.view systemLayoutSizeFittingSize:CGSizeMake(width, height)];
}
@end
