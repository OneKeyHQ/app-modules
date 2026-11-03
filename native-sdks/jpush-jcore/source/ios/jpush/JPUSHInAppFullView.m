#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JPUSHInAppFullView.h"
#import "JPUSHPropertyModels.h"
#import "JPUSHHelper.h"
#import "JCORELog.h"
@interface JPUSHInAppFullView () {
  int _countDownNumber;
  UIButton *_countDownBtn;
  NSTimer *_countDownTimer;
}
@end
@implementation JPUSHInAppFullView
@synthesize countDownBtn = _countDownBtn, countDownTimer = _countDownTimer,
            countDownNumber = _countDownNumber;
@dynamic hash, superclass, description, debugDescription;
- (instancetype)initWithConfig:(JPUSHInAppConfig *)config
                     delegate:(id<JPUSHInAppViewDelegate>)delegate {
  if ((self = [super initWithConfig:config delegate:delegate]))
    [self configUI];
  return self;
}
- (void)showInView:(UIView *)view {
  [self initFullMessageViewLayout:view];
  if (self.config.type == 30) {
    self.countDownNumber = (int)(self.config.fullConfig.duration / 1000);
    [self invalidCountDownTimer];
    self.countDownTimer = [NSTimer scheduledTimerWithTimeInterval:1.0
        target:self selector:@selector(countDown) userInfo:nil repeats:YES];
  }
  [super showInView:view];
}
- (void)invalidCountDownTimer {
  if (self.countDownTimer && self.countDownTimer.isValid) {
    [self.countDownTimer invalidate];
    self.countDownTimer = nil;
  }
}
- (void)countDown {
  self.countDownNumber = (int)((unsigned int)self.countDownNumber - 1);
  if (self.countDownNumber > 0) {
    [self.countDownBtn setTitle:[NSString stringWithFormat:@"%ds %@",
         self.countDownNumber, self.config.fullConfig.text] forState:0];
    return;
  }
  [self invalidCountDownTimer];
  [self dismiss];
}
- (void)dismiss {
  [self invalidCountDownTimer];
  [self removeFromSuperview];
  [super dismiss];
}
- (void)initTapGesture:(UIView *)view {
  if (view) {
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
        initWithTarget:self action:@selector(inAppTapAction)];
    tap.delegate = self;
    [view addGestureRecognizer:tap];
  }
}
- (void)inAppTapAction {
  if (self.delegate && [self.delegate respondsToSelector:
      @selector(jpushInappViewClick:clickViewPart:clickActionType:clickAction:)])
    [self.delegate jpushInappViewClick:self clickViewPart:0
        clickActionType:self.config.clickType clickAction:self.config.clickAction];
}
- (void)closeBtnAction {
  if (self.delegate && [self.delegate respondsToSelector:
      @selector(jpushInappViewClick:clickViewPart:clickActionType:clickAction:)])
    [self.delegate jpushInappViewClick:self clickViewPart:1
        clickActionType:3 clickAction:@""];
}
- (void)countDownBtnAction {
  if (self.delegate && [self.delegate respondsToSelector:
      @selector(jpushInappViewClick:clickViewPart:clickActionType:clickAction:)])
    [self.delegate jpushInappViewClick:self clickViewPart:2
        clickActionType:3 clickAction:@""];
}
- (void)configUI {
  UIView *container = [UIView new];
  container.backgroundColor = UIColor.clearColor;
  [self addSubview:container];
  container.translatesAutoresizingMaskIntoConstraints = NO;
  [self addConstraints:@[
    [NSLayoutConstraint constraintWithItem:container attribute:9 relatedBy:0
        toItem:self attribute:9 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:container attribute:10 relatedBy:0
        toItem:self attribute:10 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:container attribute:7 relatedBy:0
        toItem:self attribute:7 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:container attribute:8 relatedBy:0
        toItem:self attribute:8 multiplier:1 constant:0]
  ]];
  UIImageView *image = [UIImageView new];
  [self addSubview:image];
  [self initTapGesture:image];
  image.userInteractionEnabled = YES;
  image.backgroundColor = UIColor.clearColor;
  if (self.config.localImagePath &&
      [self.config.localImagePath isKindOfClass:NSString.class] &&
      self.config.localImagePath.length)
    image.image = [UIImage imageWithContentsOfFile:self.config.localImagePath];
  image.translatesAutoresizingMaskIntoConstraints = NO;
  [self addConstraints:@[
    [NSLayoutConstraint constraintWithItem:image attribute:1 relatedBy:0
        toItem:container attribute:1 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:image attribute:2 relatedBy:0
        toItem:container attribute:2 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:image attribute:3 relatedBy:0
        toItem:container attribute:3 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:image attribute:4 relatedBy:0
        toItem:container attribute:4 multiplier:1 constant:0]
  ]];
  long long type = self.config.type;
  UIButton *button = [UIButton buttonWithType:0];
  CGFloat width, height;
  if (type == 31) {
    [self addSubview:button];
    [button addTarget:self action:@selector(closeBtnAction) forControlEvents:0x40];
    [button setTitle:@"✕" forState:0];
    [button setTitleColor:UIColor.whiteColor forState:0];
    button.backgroundColor = [UIColor.blackColor colorWithAlphaComponent:0.48];
    button.titleLabel.font = [UIFont boldSystemFontOfSize:15.0];
    button.layer.cornerRadius = 15.0;
    width = 30.0;
    height = 30.0;
  } else {
    [self addSubview:button];
    self.countDownBtn = button;
    [button addTarget:self action:@selector(countDownBtnAction) forControlEvents:0x40];
    [button setTitle:[NSString stringWithFormat:@"%ds %@",
        (int)(self.config.fullConfig.duration / 1000), self.config.fullConfig.text]
        forState:0];
    [button setTitleColor:[JPUSHHelper colorWithHexString:self.config.fullConfig.style.color]
        forState:0];
    button.backgroundColor = [JPUSHHelper colorWithHexString:self.config.fullConfig.bg_color];
    button.titleLabel.font = self.config.fullConfig.style.bold
        ? [UIFont boldSystemFontOfSize:self.config.fullConfig.style.size]
        : [UIFont systemFontOfSize:self.config.fullConfig.style.size];
    button.layer.cornerRadius = 19.0;
    button.layer.borderWidth = 1.5;
    button.layer.borderColor = [[JPUSHHelper colorWithHexString:@"#253044"]
        colorWithAlphaComponent:0.12].CGColor;
    width = 94.0;
    height = 38.0;
  }
  button.translatesAutoresizingMaskIntoConstraints = NO;
  [self addConstraints:@[
    [NSLayoutConstraint constraintWithItem:button attribute:2 relatedBy:0
        toItem:container attribute:2 multiplier:1 constant:-17.0],
    [NSLayoutConstraint constraintWithItem:button attribute:3 relatedBy:0
        toItem:container attribute:3 multiplier:1 constant:44.0],
    [NSLayoutConstraint constraintWithItem:button attribute:7 relatedBy:0
        toItem:nil attribute:0 multiplier:0 constant:width],
    [NSLayoutConstraint constraintWithItem:button attribute:8 relatedBy:0
        toItem:nil attribute:0 multiplier:0 constant:height]
  ]];
}
- (BOOL)initFullMessageViewLayout:(UIView *)view {
  @try {
    [view addSubview:self];
    self.translatesAutoresizingMaskIntoConstraints = NO;
    [view addConstraints:@[
      [NSLayoutConstraint constraintWithItem:self attribute:1 relatedBy:0
          toItem:view attribute:1 multiplier:1 constant:self.config.left],
      [NSLayoutConstraint constraintWithItem:self attribute:2 relatedBy:0
          toItem:view attribute:2 multiplier:1 constant:self.config.right],
      [NSLayoutConstraint constraintWithItem:self attribute:3 relatedBy:0
          toItem:view attribute:3 multiplier:1 constant:self.config.top],
      [NSLayoutConstraint constraintWithItem:self attribute:4 relatedBy:0
          toItem:view attribute:4 multiplier:1 constant:self.config.bottom]
    ]];
    [view layoutIfNeeded];
    return YES;
  } @catch (NSException *exception) {
    [JCORELog log:YES level:0x3ff flag:0x100 context:0
        file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"
             "ios-jpush/JPush/JPush/Controller/InApp/JPUSHInAppFullView.m"
        function:__PRETTY_FUNCTION__ line:0xc7 tag:nil
        format:@"[bomb][%s] is exception", __PRETTY_FUNCTION__];
    return NO;
  }
}
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gesture
    shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)other {
  return YES;
}
@end
#endif
