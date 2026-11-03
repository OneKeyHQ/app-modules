#import "JPUSHPropertyModels.h"
#import <TargetConditionals.h>

#if !TARGET_OS_SIMULATOR

@implementation JPUSHBannerConfig
@synthesize small_image = _small_image;
@synthesize smallImageLocalPath = _smallImageLocalPath;
@synthesize title = _title;
@synthesize titleStyle = _titleStyle;
@synthesize content = _content;
@synthesize contentStyle = _contentStyle;
@synthesize location = _location;
@synthesize duration = _duration;
@end

@implementation JPUSHFullConfig
@synthesize text = _text;
@synthesize style = _style;
@synthesize duration = _duration;
@synthesize bg_color = _bg_color;
@end

@implementation JPUSHInAppBtnConfig
@synthesize text = _text;
@synthesize clickType = _clickType;
@synthesize clickAction = _clickAction;
@synthesize style = _style;
@synthesize bg_color = _bg_color;
@end

@implementation JPUSHInAppStyleConfig
@synthesize bold = _bold;
@synthesize size = _size;
@synthesize align = _align;
@synthesize color = _color;
@end

@implementation JPUSHIntegrateInfo
@synthesize index = _index;
@synthesize property = _property;
@synthesize version = _version;
@dynamic type, regIndex, loginIndex, business, loginVersion, registerVersion, tcpKeepAlive, versionFieldName, hash, superclass, description, debugDescription;
@end

@implementation JPUSHInterstitialConfig
@synthesize title = _title;
@synthesize titleStyle = _titleStyle;
@synthesize content = _content;
@synthesize contentStyle = _contentStyle;
@synthesize buttons = _buttons;
@synthesize close_location = _close_location;
@end

@implementation JPUSHPropertyReqItem
@synthesize request = _request;
@synthesize propertyCompletion = _propertyCompletion;
@synthesize seq = _seq;
@synthesize hasResend = _hasResend;
@synthesize isTimeout = _isTimeout;
@end

@implementation JPUSHRegion
@synthesize identifier = _identifier;
@synthesize expirationTime = _expirationTime;
@synthesize circularRegion = _circularRegion;
@synthesize repeat = _repeat;
@synthesize weekRepeatNum = _weekRepeatNum;
@synthesize dayRepeatNum = _dayRepeatNum;
@synthesize repeatTime = _repeatTime;
@synthesize type = _type;
@synthesize messageDict = _messageDict;
@synthesize apnsDict = _apnsDict;
@synthesize jpushFenceRegionStatus = _jpushFenceRegionStatus;
@end

@implementation JPUSHTagOrAliasReqItem
@synthesize ifTag = _ifTag;
@synthesize createTime = _createTime;
@synthesize request = _request;
@synthesize tagsCompletion = _tagsCompletion;
@synthesize tagValidCompletion = _tagValidCompletion;
@synthesize aliasCompletion = _aliasCompletion;
@synthesize seq = _seq;
@synthesize hasResend = _hasResend;
@synthesize tagsBufSet = _tagsBufSet;
@synthesize isTimeout = _isTimeout;
@synthesize isBind = _isBind;
@end

@implementation JPUSHUserPropetyReqItem
@synthesize request = _request;
@synthesize seq = _seq;
@synthesize completion = _completion;
@end

@implementation JPushLiveActivityTokenReqItem
@synthesize reportType = _reportType;
@synthesize attributeType = _attributeType;
@synthesize pushToStartToken = _pushToStartToken;
@synthesize liveActivityAlias = _liveActivityAlias;
@synthesize pushToken = _pushToken;
@synthesize seq = _seq;
@synthesize request = _request;
@synthesize completion = _completion;
@synthesize isTimeout = _isTimeout;
@end

#endif
