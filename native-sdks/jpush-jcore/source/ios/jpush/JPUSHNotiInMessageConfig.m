#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JPUSHNotiInMessageConfig.h"
#import <UIKit/UIKit.h>
@implementation JPUSHNotiInMessageConfig
@synthesize height = _height;
@synthesize width = _width;
@synthesize left = _left;
@synthesize landscapeLeft = _landscapeLeft;
@synthesize right = _right;
@synthesize landscapeRight = _landscapeRight;
@synthesize top = _top;
@synthesize bottom = _bottom;
@synthesize toH5WhRatio = _toH5WhRatio;
@synthesize backgroudColor = _backgroudColor;
@synthesize viewController = _viewController;
@synthesize window = _window;
@synthesize duration = _duration;
@synthesize dissmissDuration = _dissmissDuration;
@synthesize dismissingEnabled = _dismissingEnabled;
@synthesize showTimer = _showTimer;
@synthesize messageDic = _messageDic;
@synthesize msg_id = _msg_id;
@synthesize filepath = _filepath;
@synthesize mouldID = _mouldID;
@synthesize networkPath = _networkPath;
@synthesize h5Dic = _h5Dic;
@synthesize networkH5Dic = _networkH5Dic;
@synthesize localimagepaths = _localimagepaths;
@synthesize callbackContent = _callbackContent;
@synthesize inapp_display_direct = _inapp_display_direct;
@synthesize isfixed = _isfixed;
@synthesize msgData = _msgData;
@synthesize exposureStartTime = _exposureStartTime;
- (instancetype)init {
  if ((self = [super init])) {
    _backgroudColor = [UIColor clearColor];
    _msg_id = @"";
    _inapp_display_direct = 0;
    _left = 8.0;
    _landscapeLeft = 100.0;
    _right = -8.0;
    _landscapeRight = -100.0;
    _height = 94.0;
    _bottom = -20.0;
    _toH5WhRatio = -1.0;
    _duration = 0.5;
    _dismissingEnabled = YES;
    _isfixed = NO;
    _dissmissDuration = 0.5;
    _showTimer = 5.0;
    _top = -1.0;
  }
  return self;
}
@end
#endif
