#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JPUSHInAppBannerView.h"
#import "JPUSHPropertyModels.h"
#import "JPUSHHelper.h"
#import "JCORELog.h"
#include <float.h>
#include <math.h>
#define BANNER_LOG(lineValue, message, ...)                                     \
  [JCORELog log:YES level:0x3ff flag:0x100 context:0                             \
      file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"               \
           "ios-jpush/JPush/JPush/Controller/InApp/JPUSHInAppBannerView.m"     \
      function:__PRETTY_FUNCTION__ line:lineValue tag:nil                        \
      format:message, ##__VA_ARGS__]
@implementation JPUSHInAppBannerView
@dynamic hash, superclass, description, debugDescription;
- (instancetype)initWithConfig:(JPUSHInAppConfig *)config
                     delegate:(id<JPUSHInAppViewDelegate>)delegate {
  if ((self = [super initWithConfig:config delegate:delegate]))
    [self configUI];
  return self;
}
- (void)configUI {
  self.config.height = 118.0;
  self.config.left = 14.0;
  self.config.right = -14.0;
  [self configBannerImageText];
  [self addSwipeGesture];
  [self addTapGesture:self];
}
- (void)addTapGesture:(UIView *)view {
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
- (void)addSwipeGesture {
  UISwipeGestureRecognizer *swipe = [[UISwipeGestureRecognizer alloc]
      initWithTarget:self action:@selector(fadeMeout)];
  swipe.direction = self.config.bannerConfig.location == 0 ? 4 : 8;
  swipe.delegate = self;
  [self addGestureRecognizer:swipe];
}
- (void)fadeMeout {
  [self dismiss];
}
- (void)showInView:(UIView *)view {
  [self initMessageViewLayout:view];
  double nanos = ((double)self.config.bannerConfig.duration / 1000.0) * 1.0e9;
  int64_t delta = nanos >= 0x1p63 ? INT64_MAX
                  : nanos <= -0x1p63 ? INT64_MIN : (int64_t)nanos;
  dispatch_after(dispatch_time(DISPATCH_TIME_NOW, delta),
      dispatch_get_main_queue(), ^{ [self dismiss]; });
  [super showInView:view];
}
- (void)dismiss {
  @try {
    if (self.superview) {
      [self.superview setNeedsUpdateConstraints];
      [self.superview updateConstraintsIfNeeded];
      NSLayoutConstraint *constraint = self.config.topBottomConstraint;
      if (!constraint) {
        [self removeFromSuperview];
        return;
      }
      if (self.config.bannerConfig.location == 0) {
        double constant = -self.config.height - 10.0;
        [UIView animateWithDuration:0.5 delay:0 usingSpringWithDamping:1.0
            initialSpringVelocity:0.5 options:0x10000 animations:^{
              constraint.constant = constant;
              [self.superview layoutSubviews];
            } completion:^(BOOL finished) { [self removeFromSuperview]; }];
      } else {
        double constant = self.config.height + 10.0;
        [UIView animateWithDuration:0.5 delay:0 usingSpringWithDamping:1.0
            initialSpringVelocity:0.5 options:0x10000 animations:^{
              constraint.constant = constant;
              [self.superview layoutSubviews];
            } completion:^(BOOL finished) { [self removeFromSuperview]; }];
      }
    }
  } @catch (NSException *exception) {
    BANNER_LOG(0x69, @"[bomb] [%s] is exception", __PRETTY_FUNCTION__);
  }
  [super dismiss];
}
- (void)configBannerImageText {
  self.backgroundColor = UIColor.clearColor;
  UIView *container = [UIView new];
  container.backgroundColor = UIColor.whiteColor;
  container.layer.cornerRadius = 17.0;
  container.layer.shadowColor = [UIColor.blackColor colorWithAlphaComponent:0.12].CGColor;
  container.layer.shadowOffset = CGSizeMake(0, 4);
  container.layer.shadowOpacity = 1.0f;
  container.layer.shadowRadius = 9.0;
  [self addSubview:container];
  container.translatesAutoresizingMaskIntoConstraints = NO;
  [self addConstraints:@[
    [NSLayoutConstraint constraintWithItem:container attribute:1 relatedBy:0
        toItem:self attribute:1 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:container attribute:2 relatedBy:0
        toItem:self attribute:2 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:container attribute:3 relatedBy:0
        toItem:self attribute:3 multiplier:1 constant:10],
    [NSLayoutConstraint constraintWithItem:container attribute:4 relatedBy:0
        toItem:self attribute:4 multiplier:1 constant:-10]
  ]];
  if (self.config.type == 11) {
    UIImageView *image = [UIImageView new];
    [self addSubview:image];
    image.userInteractionEnabled = YES;
    image.layer.masksToBounds = YES;
    image.translatesAutoresizingMaskIntoConstraints = NO;
    if (self.config.localImagePath &&
        [self.config.localImagePath isKindOfClass:NSString.class] &&
        self.config.localImagePath.length)
      image.image = [UIImage imageWithContentsOfFile:self.config.localImagePath];
    image.layer.cornerRadius = 17.0;
    [self addConstraints:@[
      [NSLayoutConstraint constraintWithItem:image attribute:1 relatedBy:0
          toItem:container attribute:1 multiplier:1 constant:0],
      [NSLayoutConstraint constraintWithItem:image attribute:3 relatedBy:0
          toItem:container attribute:3 multiplier:1 constant:0],
      [NSLayoutConstraint constraintWithItem:image attribute:7 relatedBy:0
          toItem:container attribute:7 multiplier:1 constant:0],
      [NSLayoutConstraint constraintWithItem:image attribute:8 relatedBy:0
          toItem:container attribute:8 multiplier:1 constant:0]
    ]];
    return;
  }
  UIView *textAnchor = container;
  NSInteger anchorAttribute = 1;
  double iconLeft = 0, iconSize = 0, textLeft = 20, textRight = 20;
  if (self.config.bannerConfig.smallImageLocalPath &&
      [self.config.bannerConfig.smallImageLocalPath isKindOfClass:NSString.class] &&
      self.config.bannerConfig.smallImageLocalPath.length) {
    UIImageView *image = [UIImageView new];
    [self addSubview:image];
    image.userInteractionEnabled = YES;
    image.layer.masksToBounds = YES;
    image.translatesAutoresizingMaskIntoConstraints = NO;
    image.image = [UIImage imageWithContentsOfFile:self.config.bannerConfig.smallImageLocalPath];
    image.layer.cornerRadius = 2.0;
    iconLeft = 17.0;
    iconSize = 41.0;
    [self addConstraints:@[
      [NSLayoutConstraint constraintWithItem:image attribute:1 relatedBy:0
          toItem:container attribute:1 multiplier:1 constant:17],
      [NSLayoutConstraint constraintWithItem:image attribute:10 relatedBy:0
          toItem:container attribute:10 multiplier:1 constant:0],
      [NSLayoutConstraint constraintWithItem:image attribute:7 relatedBy:0
          toItem:nil attribute:0 multiplier:1 constant:41],
      [NSLayoutConstraint constraintWithItem:image attribute:8 relatedBy:0
          toItem:nil attribute:0 multiplier:1 constant:41]
    ]];
    if (image) {
      textAnchor = image;
      anchorAttribute = 2;
      textLeft = 10;
      textRight = 23;
    }
  }
  UILabel *title = [UILabel new];
  title.numberOfLines = 1;
  [self addSubview:title];
  title.text = self.config.bannerConfig.title;
  title.textColor = [JPUSHHelper colorWithHexString:
      self.config.bannerConfig.titleStyle.color ?: @"#253044"];
  title.font = self.config.bannerConfig.titleStyle.bold
      ? [UIFont boldSystemFontOfSize:self.config.bannerConfig.titleStyle.size]
      : [UIFont systemFontOfSize:self.config.bannerConfig.titleStyle.size];
  double titleLineHeight = title.font.lineHeight;
  title.textAlignment = self.config.bannerConfig.titleStyle.align;
  title.translatesAutoresizingMaskIntoConstraints = NO;
  NSLayoutConstraint *titleTop = [NSLayoutConstraint constraintWithItem:title
      attribute:3 relatedBy:0 toItem:container attribute:3 multiplier:1 constant:15];
  [self addConstraints:@[
    [NSLayoutConstraint constraintWithItem:title attribute:1 relatedBy:0
        toItem:textAnchor attribute:anchorAttribute multiplier:1 constant:textLeft],
    titleTop,
    [NSLayoutConstraint constraintWithItem:title attribute:2 relatedBy:0
        toItem:container attribute:2 multiplier:1 constant:-textRight],
    [NSLayoutConstraint constraintWithItem:title attribute:8 relatedBy:0
        toItem:nil attribute:0 multiplier:1 constant:titleLineHeight + 5.0]
  ]];
  UILabel *content = [UILabel new];
  content.numberOfLines = 2;
  [self addSubview:content];
  content.text = self.config.bannerConfig.content;
  content.textColor = [JPUSHHelper colorWithHexString:
      self.config.bannerConfig.contentStyle.color ?: @"#253044"];
  content.font = self.config.bannerConfig.contentStyle.bold
      ? [UIFont boldSystemFontOfSize:self.config.bannerConfig.contentStyle.size]
      : [UIFont systemFontOfSize:self.config.bannerConfig.contentStyle.size];
  content.textAlignment = self.config.bannerConfig.contentStyle.align;
  double availableWidth = UIScreen.mainScreen.bounds.size.width - iconLeft - iconSize -
      textLeft - textRight - (self.config.left + self.config.left);
  CGRect bounds = [self.config.bannerConfig.content
      boundingRectWithSize:CGSizeMake(availableWidth, FLT_MAX) options:1
      attributes:@{NSFontAttributeName : content.font} context:nil];
  double measuredHeight = ceilf((float)bounds.size.height);
  double twoLines = content.font.lineHeight + content.font.lineHeight;
  float lineHeight = ceilf((float)content.font.lineHeight);
  float contentHeight = twoLines < measuredHeight ? lineHeight + lineHeight : lineHeight;
  content.translatesAutoresizingMaskIntoConstraints = NO;
  NSLayoutConstraint *contentTop = [NSLayoutConstraint constraintWithItem:content
      attribute:3 relatedBy:0 toItem:title attribute:4 multiplier:1 constant:5];
  [self addConstraints:@[
    [NSLayoutConstraint constraintWithItem:content attribute:1 relatedBy:0
        toItem:title attribute:1 multiplier:1 constant:0],
    contentTop,
    [NSLayoutConstraint constraintWithItem:content attribute:2 relatedBy:0
        toItem:title attribute:2 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:content attribute:8 relatedBy:0
        toItem:nil attribute:0 multiplier:1 constant:contentHeight]
  ]];
  self.config.height = fmax(contentTop.constant + (titleLineHeight + 5.0) +
      titleTop.constant + titleTop.constant + contentHeight + 20.0, 118.0);
}
- (BOOL)initMessageViewLayout:(UIView *)view {
  if (!view) {
    BANNER_LOG(0xf9, @"[%s] rootViewControler is nil", __PRETTY_FUNCTION__);
    return NO;
  }
  [view addSubview:self];
  self.translatesAutoresizingMaskIntoConstraints = NO;
  NSLayoutAttribute edge = self.config.bannerConfig.location == 0 ? 3 : 4;
  double initial = edge == 3 ? -self.config.height : self.config.height;
  NSLayoutConstraint *constraint = [NSLayoutConstraint constraintWithItem:self
      attribute:edge relatedBy:0 toItem:view attribute:edge multiplier:1 constant:initial];
  self.config.topBottomConstraint = constraint;
  [view addConstraints:@[
    constraint,
    [NSLayoutConstraint constraintWithItem:self attribute:1 relatedBy:0
        toItem:view attribute:1 multiplier:1 constant:[self messageLeftMargin:self]],
    [NSLayoutConstraint constraintWithItem:self attribute:2 relatedBy:0
        toItem:view attribute:2 multiplier:1 constant:[self messageRightMargin:self]],
    [NSLayoutConstraint constraintWithItem:self attribute:8 relatedBy:0
        toItem:nil attribute:8 multiplier:1 constant:self.config.height]
  ]];
  [view layoutIfNeeded];
  [view setNeedsUpdateConstraints];
  [view updateConstraintsIfNeeded];
  if (self.config.bannerConfig.duration) {
    if (self.config.bannerConfig.location == 0) {
      [UIView animateWithDuration:0.5 delay:0 usingSpringWithDamping:0.7
          initialSpringVelocity:1.0 options:0x20000 animations:^{
            constraint.constant = [self messageTopMargin];
            [view layoutSubviews];
          } completion:^(BOOL finished) {}];
    } else {
      JPUSHInAppBannerView *marginView = self;
      [UIView animateWithDuration:0.5 delay:0 usingSpringWithDamping:0.7
          initialSpringVelocity:1.0 options:0x20000 animations:^{
            constraint.constant = self.config.bottom - [marginView messageBottomMargin];
            [view layoutSubviews];
          } completion:^(BOOL finished) {}];
    }
  }
  return YES;
}
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gesture
    shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)other {
  return YES;
}
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
- (double)messageLeftMargin:(JPUSHInAppView *)view {
  UIInterfaceOrientation orientation = UIApplication.sharedApplication.statusBarOrientation;
  return (orientation == 3 || orientation == 4) ? view.config.landscapeLeft : view.config.left;
}
- (double)messageTopMargin {
  UIInterfaceOrientation orientation = UIApplication.sharedApplication.statusBarOrientation;
  if (orientation == 3 || orientation == 4)
    return 20.0;
  if (!UIApplication.sharedApplication.isStatusBarHidden)
    return UIApplication.sharedApplication.statusBarFrame.size.height;
  UIWindow *window = [self getCurrentWindow];
  double firstWidth = window.frame.size.width;
  double secondWidth = window.frame.size.width;
  return secondWidth == 896.0 || firstWidth == 812.0 ? 44.0 : 20.0;
}
- (double)messageRightMargin:(JPUSHInAppView *)view {
  UIInterfaceOrientation orientation = UIApplication.sharedApplication.statusBarOrientation;
  return (orientation == 3 || orientation == 4) ? view.config.landscapeRight : view.config.right;
}
#pragma clang diagnostic pop
- (double)messageBottomMargin {
  if (@available(iOS 11.0, *))
    return [self getCurrentWindow].safeAreaInsets.bottom;
  return 0.0;
}
- (UIWindow *)getCurrentWindow {
  return [JPUSHHelper getCurrentWinow];
}
@end
#endif
