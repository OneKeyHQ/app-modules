#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JPUSHInAppModalView.h"
#import "JPUSHPropertyModels.h"
#import "JPUSHHelper.h"
#import "JCORELog.h"
#include <float.h>
#include <math.h>
@implementation JPUSHInAppModalView
@dynamic hash, superclass, description, debugDescription;
- (instancetype)initWithConfig:(JPUSHInAppConfig *)config
                     delegate:(id<JPUSHInAppViewDelegate>)delegate {
  if ((self = [super initWithConfig:config delegate:delegate]))
    [self configUI];
  return self;
}
- (void)configUI {
  if (self.config.type == 21)
    [self configModalImage];
  else
    [self configModalImageText];
}
- (void)showInView:(UIView *)view {
  [self initModelMessageViewLayout:view];
  [super showInView:view];
}
- (void)dismiss {
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
- (void)operateBtnAction:(UIButton *)button {
  NSInteger tag = button.tag;
  NSUInteger index = (NSUInteger)tag - 1000;
  if (tag > 999 && index < self.config.interstitialConfig.buttons.count) {
    JPUSHInAppBtnConfig *config = self.config.interstitialConfig.buttons[index];
    long long type = config.clickType;
    NSString *action = config.clickAction;
    long long part = index == 0 ? 3 : 4;
    if (self.delegate && [self.delegate respondsToSelector:
        @selector(jpushInappViewClick:clickViewPart:clickActionType:clickAction:)])
      [self.delegate jpushInappViewClick:self clickViewPart:part
          clickActionType:type clickAction:action];
    return;
  }
  [self dismiss];
}
- (void)configModalImage {
  UIView *background = [UIView new];
  background.backgroundColor = UIColor.blackColor;
  background.alpha = 0.5;
  [self addSubview:background];
  background.translatesAutoresizingMaskIntoConstraints = NO;
  [self addConstraints:@[
    [NSLayoutConstraint constraintWithItem:background attribute:9 relatedBy:0
        toItem:self attribute:9 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:background attribute:10 relatedBy:0
        toItem:self attribute:10 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:background attribute:7 relatedBy:0
        toItem:self attribute:7 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:background attribute:8 relatedBy:0
        toItem:self attribute:8 multiplier:1 constant:0]
  ]];
  UIImageView *image = [UIImageView new];
  [self addSubview:image];
  image.layer.cornerRadius = 16.0;
  image.layer.masksToBounds = YES;
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
        toItem:self attribute:1 multiplier:1 constant:30],
    [NSLayoutConstraint constraintWithItem:image attribute:2 relatedBy:0
        toItem:self attribute:2 multiplier:1 constant:-30],
    [NSLayoutConstraint constraintWithItem:image attribute:10 relatedBy:0
        toItem:self attribute:10 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:image attribute:8 relatedBy:0
        toItem:image attribute:7 multiplier:0x1.d8fd8fd8fd8fep-1 constant:0]
  ]];
  if (self.config.interstitialConfig.close_location) {
    UIButton *button = [UIButton buttonWithType:0];
    [button addTarget:self action:@selector(closeBtnAction) forControlEvents:0x40];
    [button setTitle:@"✕" forState:0];
    [button setTitleColor:[UIColor.blackColor colorWithAlphaComponent:0.48] forState:0];
    button.backgroundColor = [UIColor.whiteColor colorWithAlphaComponent:0.8];
    button.titleLabel.font = [UIFont systemFontOfSize:14.0];
    button.layer.cornerRadius = 15.0;
    [self addSubview:button];
    button.translatesAutoresizingMaskIntoConstraints = NO;
    if (self.config.interstitialConfig.close_location == 1) {
      [self addConstraints:@[
        [NSLayoutConstraint constraintWithItem:button attribute:9 relatedBy:0
            toItem:self attribute:9 multiplier:1 constant:0],
        [NSLayoutConstraint constraintWithItem:button attribute:3 relatedBy:0
            toItem:image attribute:4 multiplier:1 constant:20],
        [NSLayoutConstraint constraintWithItem:button attribute:7 relatedBy:0
            toItem:nil attribute:0 multiplier:0 constant:30],
        [NSLayoutConstraint constraintWithItem:button attribute:8 relatedBy:0
            toItem:nil attribute:0 multiplier:0 constant:30]
      ]];
    } else if (self.config.interstitialConfig.close_location == 2) {
      [self addConstraints:@[
        [NSLayoutConstraint constraintWithItem:button attribute:2 relatedBy:0
            toItem:image attribute:2 multiplier:1 constant:0],
        [NSLayoutConstraint constraintWithItem:button attribute:4 relatedBy:0
            toItem:image attribute:3 multiplier:1 constant:-5],
        [NSLayoutConstraint constraintWithItem:button attribute:7 relatedBy:0
            toItem:nil attribute:0 multiplier:0 constant:30],
        [NSLayoutConstraint constraintWithItem:button attribute:8 relatedBy:0
            toItem:nil attribute:0 multiplier:0 constant:30]
      ]];
    }
  }
}
- (void)configModalImageText {
  UIView *background = [UIView new];
  background.backgroundColor = UIColor.blackColor;
  background.alpha = 0.5;
  [self addSubview:background];
  background.translatesAutoresizingMaskIntoConstraints = NO;
  [self addConstraints:@[
    [NSLayoutConstraint constraintWithItem:background attribute:9 relatedBy:0
        toItem:self attribute:9 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:background attribute:10 relatedBy:0
        toItem:self attribute:10 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:background attribute:7 relatedBy:0
        toItem:self attribute:7 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:background attribute:8 relatedBy:0
        toItem:self attribute:8 multiplier:1 constant:0]
  ]];
  UIView *container = [UIView new];
  container.layer.cornerRadius = 15.0;
  container.layer.masksToBounds = YES;
  [self addSubview:container];
  container.translatesAutoresizingMaskIntoConstraints = NO;
  [self addConstraints:@[
    [NSLayoutConstraint constraintWithItem:container attribute:9 relatedBy:0
        toItem:self attribute:9 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:container attribute:10 relatedBy:0
        toItem:self attribute:10 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:container attribute:1 relatedBy:0
        toItem:self attribute:1 multiplier:1 constant:30],
    [NSLayoutConstraint constraintWithItem:container attribute:2 relatedBy:0
        toItem:self attribute:2 multiplier:1 constant:-30]
  ]];
  UIImageView *image = nil;
  if (self.config.localImagePath &&
      [self.config.localImagePath isKindOfClass:NSString.class] &&
      self.config.localImagePath.length) {
    image = [UIImageView new];
    [container addSubview:image];
    [self initTapGesture:image];
    image.userInteractionEnabled = YES;
    image.backgroundColor = UIColor.clearColor;
    image.image = [UIImage imageWithContentsOfFile:self.config.localImagePath];
    image.translatesAutoresizingMaskIntoConstraints = NO;
    [self addConstraints:@[
      [NSLayoutConstraint constraintWithItem:image attribute:1 relatedBy:0
          toItem:container attribute:1 multiplier:1 constant:0],
      [NSLayoutConstraint constraintWithItem:image attribute:2 relatedBy:0
          toItem:container attribute:2 multiplier:1 constant:0],
      [NSLayoutConstraint constraintWithItem:image attribute:3 relatedBy:0
          toItem:container attribute:3 multiplier:1 constant:0],
      [NSLayoutConstraint constraintWithItem:image attribute:8 relatedBy:0
          toItem:image attribute:7 multiplier:0x1.cd9cd9cd9cd9dp-2 constant:0]
    ]];
  }
  UIView *textBackground = [UIView new];
  textBackground.backgroundColor = UIColor.whiteColor;
  [container addSubview:textBackground];
  textBackground.translatesAutoresizingMaskIntoConstraints = NO;
  [self addConstraints:@[
    [NSLayoutConstraint constraintWithItem:textBackground attribute:1 relatedBy:0
        toItem:container attribute:1 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:textBackground attribute:2 relatedBy:0
        toItem:container attribute:2 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:textBackground attribute:3 relatedBy:0
        toItem:image ?: container attribute:image ? 4 : 3 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:textBackground attribute:4 relatedBy:0
        toItem:container attribute:4 multiplier:1 constant:0]
  ]];
  UILabel *title = nil;
  if (self.config.interstitialConfig.title &&
      [self.config.interstitialConfig.title isKindOfClass:NSString.class] &&
      self.config.interstitialConfig.title.length) {
    title = [UILabel new];
    title.backgroundColor = UIColor.whiteColor;
    title.textColor = [JPUSHHelper colorWithHexString:
        self.config.interstitialConfig.titleStyle.color ?: @"#ffffff"];
    title.font = self.config.interstitialConfig.titleStyle.bold
        ? [UIFont boldSystemFontOfSize:self.config.interstitialConfig.titleStyle.size]
        : [UIFont systemFontOfSize:self.config.interstitialConfig.titleStyle.size];
    [self addSubview:title];
    title.text = self.config.interstitialConfig.title;
    title.textAlignment = self.config.interstitialConfig.titleStyle.align;
    title.translatesAutoresizingMaskIntoConstraints = NO;
    [self addConstraints:@[
      [NSLayoutConstraint constraintWithItem:title attribute:1 relatedBy:0
          toItem:container attribute:1 multiplier:1 constant:22],
      [NSLayoutConstraint constraintWithItem:title attribute:3 relatedBy:0
          toItem:image ?: container attribute:image ? 4 : 3 multiplier:1 constant:20],
      [NSLayoutConstraint constraintWithItem:title attribute:2 relatedBy:0
          toItem:container attribute:2 multiplier:1 constant:-22],
      [NSLayoutConstraint constraintWithItem:title attribute:8 relatedBy:0
          toItem:nil attribute:0 multiplier:0 constant:30]
    ]];
  }
  UITextView *content = nil;
  if (self.config.interstitialConfig.content &&
      [self.config.interstitialConfig.content isKindOfClass:NSString.class] &&
      self.config.interstitialConfig.content.length) {
    content = [UITextView new];
    content.backgroundColor = UIColor.whiteColor;
    content.editable = NO;
    content.selectable = NO;
    content.textContainer.lineFragmentPadding = 0;
    content.textContainerInset = UIEdgeInsetsZero;
    content.textColor = [JPUSHHelper colorWithHexString:
        self.config.interstitialConfig.contentStyle.color ?: @"ffffff"];
    content.font = self.config.interstitialConfig.contentStyle.bold
        ? [UIFont boldSystemFontOfSize:self.config.interstitialConfig.contentStyle.size]
        : [UIFont systemFontOfSize:self.config.interstitialConfig.contentStyle.size];
    double width = UIScreen.mainScreen.bounds.size.width - 60.0 - 44.0;
    CGRect bounds = [self.config.interstitialConfig.content
        boundingRectWithSize:CGSizeMake(width, FLT_MAX) options:1
        attributes:@{NSFontAttributeName : content.font} context:nil];
    double measuredHeight = ceilf((float)bounds.size.height);
    double limit = content.font.lineHeight * 5.0;
    double height = measuredHeight;
    if (limit < measuredHeight)
      height = ceilf((float)(content.font.lineHeight * 5.0));
    content.scrollEnabled = limit < measuredHeight;
    content.textAlignment = self.config.interstitialConfig.contentStyle.align;
    [self addSubview:content];
    content.text = self.config.interstitialConfig.content;
    content.translatesAutoresizingMaskIntoConstraints = NO;
    UIView *anchor = title ?: image ?: container;
    [self addConstraints:@[
      [NSLayoutConstraint constraintWithItem:content attribute:1 relatedBy:0
          toItem:container attribute:1 multiplier:1 constant:22],
      [NSLayoutConstraint constraintWithItem:content attribute:3 relatedBy:0
          toItem:anchor attribute:(title || image) ? 4 : 3 multiplier:1
          constant:title ? 11 : 20],
      [NSLayoutConstraint constraintWithItem:content attribute:2 relatedBy:0
          toItem:container attribute:2 multiplier:1 constant:-22],
      [NSLayoutConstraint constraintWithItem:content attribute:8 relatedBy:0
          toItem:nil attribute:0 multiplier:1 constant:height]
    ]];
  }
  NSMutableArray<UIButton *> *buttons = [NSMutableArray array];
  if (self.config.interstitialConfig.buttons.count) {
    UIView *anchor = content ?: title ?: image ?: container;
    NSLayoutAttribute edge = (content || title || image) ? 4 : 3;
    for (NSUInteger index = 0; index < self.config.interstitialConfig.buttons.count; index++) {
      JPUSHInAppBtnConfig *config = self.config.interstitialConfig.buttons[index];
      UIButton *button = [UIButton buttonWithType:0];
      button.tag = (NSInteger)(index + 1000);
      [self addSubview:button];
      [buttons addObject:button];
      [button addTarget:self action:@selector(operateBtnAction:) forControlEvents:0x40];
      [button setTitle:config.text forState:0];
      [button setTitleColor:[JPUSHHelper colorWithHexString:config.style.color] forState:0];
      button.titleLabel.lineBreakMode = 4;
      button.backgroundColor = [JPUSHHelper colorWithHexString:config.bg_color];
      button.titleLabel.font = config.style.bold
          ? [UIFont boldSystemFontOfSize:config.style.size]
          : [UIFont systemFontOfSize:config.style.size];
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
      button.contentEdgeInsets = UIEdgeInsetsMake(0, 5, 0, 5);
#pragma clang diagnostic pop
      button.contentHorizontalAlignment = config.style.align == 2 ? 2
          : config.style.align == 0 ? 1 : 0;
      button.layer.cornerRadius = 25.0;
      button.layer.borderWidth = 1.5;
      button.layer.borderColor = [[JPUSHHelper colorWithHexString:@"#253044"]
          colorWithAlphaComponent:0.12].CGColor;
      button.translatesAutoresizingMaskIntoConstraints = NO;
      if (!index)
        [self addConstraint:[NSLayoutConstraint constraintWithItem:button attribute:1
            relatedBy:0 toItem:container attribute:1 multiplier:1 constant:22]];
      if (index == self.config.interstitialConfig.buttons.count - 1)
        [self addConstraint:[NSLayoutConstraint constraintWithItem:button attribute:2
            relatedBy:0 toItem:container attribute:2 multiplier:1 constant:-22]];
      [self addConstraints:@[
        [NSLayoutConstraint constraintWithItem:button attribute:3 relatedBy:0
            toItem:anchor attribute:edge multiplier:1 constant:18],
        [NSLayoutConstraint constraintWithItem:button attribute:8 relatedBy:0
            toItem:nil attribute:0 multiplier:0 constant:50]
      ]];
    }
    if (buttons.count > 1) {
      UIButton *previous = buttons[0];
      for (NSUInteger index = 1; index < buttons.count; index++) {
        UIButton *button = buttons[index];
        [self addConstraints:@[
          [NSLayoutConstraint constraintWithItem:button attribute:7 relatedBy:0
              toItem:previous attribute:7 multiplier:1 constant:0],
          [NSLayoutConstraint constraintWithItem:button attribute:1 relatedBy:0
              toItem:previous attribute:2 multiplier:1 constant:12]
        ]];
        previous = button;
      }
    }
  }
  UIView *bottomAnchor = image;
  double bottom = 0;
  if (buttons.count) {
    bottomAnchor = buttons[0];
    bottom = 20;
  } else if (content) {
    bottomAnchor = content;
    bottom = 20;
  } else if (title) {
    bottomAnchor = title;
    bottom = 20;
  }
  [self addConstraints:@[
    [NSLayoutConstraint constraintWithItem:container attribute:4 relatedBy:0
        toItem:bottomAnchor attribute:4 multiplier:1 constant:bottom]
  ]];
  if (self.config.interstitialConfig.close_location) {
    UIButton *button = [UIButton buttonWithType:0];
    [button addTarget:self action:@selector(closeBtnAction) forControlEvents:0x40];
    [button setTitle:@"✕" forState:0];
    [button setTitleColor:[UIColor.blackColor colorWithAlphaComponent:0.48] forState:0];
    button.backgroundColor = [UIColor.whiteColor colorWithAlphaComponent:0.8];
    button.titleLabel.font = [UIFont systemFontOfSize:14.0];
    button.layer.cornerRadius = 15.0;
    [self addSubview:button];
    button.translatesAutoresizingMaskIntoConstraints = NO;
    if (self.config.interstitialConfig.close_location == 1) {
      [self addConstraints:@[
        [NSLayoutConstraint constraintWithItem:button attribute:9 relatedBy:0
            toItem:container attribute:9 multiplier:1 constant:0],
        [NSLayoutConstraint constraintWithItem:button attribute:3 relatedBy:0
            toItem:container attribute:4 multiplier:1 constant:20],
        [NSLayoutConstraint constraintWithItem:button attribute:7 relatedBy:0
            toItem:nil attribute:0 multiplier:0 constant:30],
        [NSLayoutConstraint constraintWithItem:button attribute:8 relatedBy:0
            toItem:nil attribute:0 multiplier:0 constant:30]
      ]];
    } else if (self.config.interstitialConfig.close_location == 2) {
      [self addConstraints:@[
        [NSLayoutConstraint constraintWithItem:button attribute:2 relatedBy:0
            toItem:container attribute:2 multiplier:1 constant:0],
        [NSLayoutConstraint constraintWithItem:button attribute:4 relatedBy:0
            toItem:container attribute:3 multiplier:1 constant:-5],
        [NSLayoutConstraint constraintWithItem:button attribute:7 relatedBy:0
            toItem:nil attribute:0 multiplier:0 constant:30],
        [NSLayoutConstraint constraintWithItem:button attribute:8 relatedBy:0
            toItem:nil attribute:0 multiplier:0 constant:30]
      ]];
    }
  }
}
- (BOOL)initModelMessageViewLayout:(UIView *)view {
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
             "ios-jpush/JPush/JPush/Controller/InApp/JPUSHInAppModalView.m"
        function:__PRETTY_FUNCTION__ line:0x1b7 tag:nil
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
