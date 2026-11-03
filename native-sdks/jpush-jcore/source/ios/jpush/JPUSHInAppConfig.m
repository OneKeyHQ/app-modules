#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JPUSHInAppConfig.h"
@implementation JPUSHInAppConfig
@synthesize msg_id = _msg_id;
@synthesize type = _type;
@synthesize bigType = _bigType;
@synthesize target = _target;
@synthesize expired = _expired;
@synthesize delay = _delay;
@synthesize interval = _interval;
@synthesize image = _image;
@synthesize localImagePath = _localImagePath;
@synthesize clickType = _clickType;
@synthesize clickAction = _clickAction;
@synthesize extras = _extras;
@synthesize interstitialConfig = _interstitialConfig;
@synthesize fullConfig = _fullConfig;
@synthesize bannerConfig = _bannerConfig;
@synthesize messageDic = _messageDic;
@synthesize height = _height;
@synthesize width = _width;
@synthesize left = _left;
@synthesize landscapeLeft = _landscapeLeft;
@synthesize right = _right;
@synthesize landscapeRight = _landscapeRight;
@synthesize top = _top;
@synthesize bottom = _bottom;
@synthesize topBottomConstraint = _topBottomConstraint;
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
// The original setter stores _bigType, while its getter derives the value from type.
- (long long)bigType {
  unsigned long long type = (unsigned long long)_type;
  if (type == 20 || type == 21)
    return 1;
  if (type == 30 || type == 31)
    return 2;
  return 0;
}
#pragma clang diagnostic pop
@end
#endif
