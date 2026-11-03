#pragma once
#import <Foundation/Foundation.h>
#import "RecoveredProtocols.h"
#import "JPUSHService.h"

NS_ASSUME_NONNULL_BEGIN

@class CLCircularRegion, JPUSHInAppStyleConfig, JPUSHLiveActivityTokenReport, JPUSHNewRequest, NSArray, NSData, NSDictionary, NSMutableSet, NSString;

@interface JPUSHBannerConfig : NSObject {
    NSString *_small_image;
    NSString *_smallImageLocalPath;
    NSString *_title;
    JPUSHInAppStyleConfig *_titleStyle;
    NSString *_content;
    JPUSHInAppStyleConfig *_contentStyle;
    long long _location;
    long long _duration;
}
@property (nullable, copy, nonatomic) NSString *small_image;
@property (nullable, copy, nonatomic) NSString *smallImageLocalPath;
@property (nullable, copy, nonatomic) NSString *title;
@property (nullable, strong, nonatomic) JPUSHInAppStyleConfig *titleStyle;
@property (nullable, copy, nonatomic) NSString *content;
@property (nullable, strong, nonatomic) JPUSHInAppStyleConfig *contentStyle;
@property (nonatomic) long long location;
@property (nonatomic) long long duration;
@end

@interface JPUSHFullConfig : NSObject {
    NSString *_text;
    JPUSHInAppStyleConfig *_style;
    long long _duration;
    NSString *_bg_color;
}
@property (nullable, copy, nonatomic) NSString *text;
@property (nullable, strong, nonatomic) JPUSHInAppStyleConfig *style;
@property (nonatomic) long long duration;
@property (nullable, copy, nonatomic) NSString *bg_color;
@end

@interface JPUSHInAppBtnConfig : NSObject {
    NSString *_text;
    long long _clickType;
    NSString *_clickAction;
    JPUSHInAppStyleConfig *_style;
    NSString *_bg_color;
}
@property (nullable, copy, nonatomic) NSString *text;
@property (nonatomic) long long clickType;
@property (nullable, copy, nonatomic) NSString *clickAction;
@property (nullable, strong, nonatomic) JPUSHInAppStyleConfig *style;
@property (nullable, copy, nonatomic) NSString *bg_color;
@end

@interface JPUSHInAppStyleConfig : NSObject {
    BOOL _bold;
    double _size;
    long long _align;
    NSString *_color;
}
@property (nonatomic) BOOL bold;
@property (nonatomic) double size;
@property (nonatomic) long long align;
@property (nullable, copy, nonatomic) NSString *color;
@end

@interface JPUSHIntegrateInfo : NSObject <JCOREIntegrateProtocol> {
    unsigned char _property;
    long long _index;
    NSString *_version;
}
@property (nonatomic) long long index;
@property (nonatomic) unsigned char property;
@property (nullable, copy, nonatomic) NSString *version;
@property (nonatomic) long long type;
@property (nonatomic) long long regIndex;
@property (nonatomic) long long loginIndex;
@property (nonatomic) long long business;
@property (nonatomic) unsigned char loginVersion;
@property (nonatomic) unsigned char registerVersion;
@property (nonatomic) BOOL tcpKeepAlive;
@property (nullable, copy, nonatomic) NSString *versionFieldName;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (nullable, readonly, copy) NSString *description;
@property (nullable, readonly, copy) NSString *debugDescription;
@end

@interface JPUSHInterstitialConfig : NSObject {
    NSString *_title;
    JPUSHInAppStyleConfig *_titleStyle;
    NSString *_content;
    JPUSHInAppStyleConfig *_contentStyle;
    NSArray *_buttons;
    long long _close_location;
}
@property (nullable, copy, nonatomic) NSString *title;
@property (nullable, strong, nonatomic) JPUSHInAppStyleConfig *titleStyle;
@property (nullable, copy, nonatomic) NSString *content;
@property (nullable, strong, nonatomic) JPUSHInAppStyleConfig *contentStyle;
@property (nullable, strong, nonatomic) NSArray *buttons;
@property (nonatomic) long long close_location;
@end

@interface JPUSHPropertyReqItem : NSObject {
    BOOL _hasResend;
    BOOL _isTimeout;
    JPUSHNewRequest *_request;
    JPUSHPropertiesOperationCompletion _propertyCompletion;
    long long _seq;
}
@property (nullable, strong, nonatomic) JPUSHNewRequest *request;
@property (nullable, copy, nonatomic) JPUSHPropertiesOperationCompletion propertyCompletion;
@property (nonatomic) long long seq;
@property (nonatomic) BOOL hasResend;
@property (nonatomic) BOOL isTimeout;
@end

@interface JPUSHRegion : NSObject {
    BOOL _repeat;
    unsigned char _type;
    int _weekRepeatNum;
    int _dayRepeatNum;
    NSString *_identifier;
    long long _expirationTime;
    CLCircularRegion *_circularRegion;
    double _repeatTime;
    NSDictionary *_messageDict;
    NSDictionary *_apnsDict;
    long long _jpushFenceRegionStatus;
}
@property (nullable, strong, nonatomic) NSString *identifier;
@property (nonatomic) long long expirationTime;
@property (nullable, strong, nonatomic) CLCircularRegion *circularRegion;
@property (nonatomic) BOOL repeat;
@property (nonatomic) int weekRepeatNum;
@property (nonatomic) int dayRepeatNum;
@property (nonatomic) double repeatTime;
@property (nonatomic) unsigned char type;
@property (nullable, strong, nonatomic) NSDictionary *messageDict;
@property (nullable, strong, nonatomic) NSDictionary *apnsDict;
@property (nonatomic) long long jpushFenceRegionStatus;
@end

@interface JPUSHTagOrAliasReqItem : NSObject {
    BOOL _ifTag;
    BOOL _hasResend;
    BOOL _isTimeout;
    BOOL _isBind;
    double _createTime;
    JPUSHNewRequest *_request;
    JPUSHTagsOperationCompletion _tagsCompletion;
    JPUSHTagValidOperationCompletion _tagValidCompletion;
    JPUSHAliasOperationCompletion _aliasCompletion;
    long long _seq;
    NSMutableSet *_tagsBufSet;
}
@property (nonatomic) BOOL ifTag;
@property (nonatomic) double createTime;
@property (nullable, strong, nonatomic) JPUSHNewRequest *request;
@property (nullable, copy, nonatomic) JPUSHTagsOperationCompletion tagsCompletion;
@property (nullable, copy, nonatomic) JPUSHTagValidOperationCompletion tagValidCompletion;
@property (nullable, copy, nonatomic) JPUSHAliasOperationCompletion aliasCompletion;
@property (nonatomic) long long seq;
@property (nonatomic) BOOL hasResend;
@property (nullable, strong, nonatomic) NSMutableSet *tagsBufSet;
@property (nonatomic) BOOL isTimeout;
@property (nonatomic) BOOL isBind;
@end

@interface JPUSHUserPropetyReqItem : NSObject {
    JPUSHNewRequest *_request;
    unsigned long long _seq;
    void (^_completion)(NSError *_Nullable error);
}
@property (nullable, strong, nonatomic) JPUSHNewRequest *request;
@property (nonatomic) unsigned long long seq;
@property (nullable, copy, nonatomic) void (^completion)(NSError *_Nullable error);
@end

@interface JPushLiveActivityTokenReqItem : NSObject {
    BOOL _isTimeout;
    unsigned long long _reportType;
    NSString *_attributeType;
    NSData *_pushToStartToken;
    NSString *_liveActivityAlias;
    NSData *_pushToken;
    long long _seq;
    JPUSHLiveActivityTokenReport *_request;
    JPUSHLiveActivityTokenCompletion _completion;
}
@property (nonatomic) unsigned long long reportType;
@property (nullable, copy, nonatomic) NSString *attributeType;
@property (nullable, strong, nonatomic) NSData *pushToStartToken;
@property (nullable, copy, nonatomic) NSString *liveActivityAlias;
@property (nullable, strong, nonatomic) NSData *pushToken;
@property (nonatomic) long long seq;
@property (nullable, strong, nonatomic) JPUSHLiveActivityTokenReport *request;
@property (nullable, copy, nonatomic) JPUSHLiveActivityTokenCompletion completion;
@property (nonatomic) BOOL isTimeout;
@end

NS_ASSUME_NONNULL_END
