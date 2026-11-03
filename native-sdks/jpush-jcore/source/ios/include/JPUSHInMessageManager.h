#pragma once
#import "JPUSHNotiInMessageManager.h"
#import "JPUSHInAppManager.h"
#import "JPUSHInAppView.h"
#import "JPUSHNotiInMessageView.h"
@class JPUSHNotiInMessageH5Manager, JPushInAppMessage;
@protocol JPUSHNotiInMessageDelegate, JPUSHInAppMessageDelegate;
typedef void (^JPUSHInMessageResult)(BOOL);
@interface JPUSHInMessageManager : NSObject
    <JPushNotiInMessageDataSourceDelegate, JPushNotiInMessageViewDelegate,
     JPUSHInAppViewDelegate, JPushInAppDataSourceDelegate> {
  BOOL _hasShowFullView;
  BOOL _hasShowModelView;
  BOOL _isShowProcess;
  __weak id<JPUSHNotiInMessageDelegate> _notiInMessageDelegate;
  __weak id<JPUSHInAppMessageDelegate> _inAppMessageDelegate;
  JPUSHNotiInMessageManager *_notiManager;
  JPUSHInAppManager *_inAppManager;
  JPUSHNotiInMessageH5Manager *_h5Manager;
  NSMutableSet *_tempMessageViews;
  UIView *_bannerView;
  __weak NSTimer *_nextMessageViewTimer;
  NSString *_currentPage;
  NSTimer *_delayShowTimer;
  long long _inappShowCursor;
}
@property(strong) JPUSHNotiInMessageManager *notiManager;
@property(nonatomic, strong) JPUSHInAppManager *inAppManager;
@property(nonatomic, strong) JPUSHNotiInMessageH5Manager *h5Manager;
@property(nonatomic, strong) NSMutableSet *tempMessageViews;
@property(nonatomic, strong) UIView *bannerView;
@property(nonatomic, weak) NSTimer *nextMessageViewTimer;
@property(nonatomic, copy) NSString *currentPage;
@property(nonatomic) BOOL hasShowFullView;
@property(nonatomic) BOOL hasShowModelView;
@property(nonatomic, strong) NSTimer *delayShowTimer;
@property(nonatomic) long long inappShowCursor;
@property(nonatomic) BOOL isShowProcess;
@property(nonatomic, weak) id<JPUSHNotiInMessageDelegate> notiInMessageDelegate;
@property(nonatomic, weak) id<JPUSHInAppMessageDelegate> inAppMessageDelegate;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
+ (instancetype)shareManager;
+ (void)handleNotiInMessageDic:(id)message;
+ (void)handleInMessageDic:(id)message;
+ (void)pageEnterTo:(id)page;
+ (void)pageLeave:(id)page;
- (id)copyWithZone:(NSZone *)zone;
- (void)handleInMessageDic:(id)message;
- (void)handleRevokeMessageId:(id)messageID;
- (void)handleInAppDic:(id)message;
- (void)pageEnterTo:(id)page;
- (void)pageLeave:(id)page;
- (void)dismissLastPageInappView;
- (void)invalidateDelayShowTimer;
- (void)startDelayShowTimer;
- (void)pageDelayShowInapp;
- (void)startNextMessageViewTimer:(double)delay;
- (void)invalidateNextMessageViewTimer;
- (void)nextInappViewShow;
- (void)beginShowInApp;
- (void)showInappProcess;
- (void)configInAppView:(JPUSHInAppConfig *)config;
- (void)showInWindowWithConfig:(JPUSHInAppConfig *)config;
- (void)asyncDownloadMouldWithConfig:(JPUSHNotiInMessageConfig *)config;
- (BOOL)messageWithConfig:(JPUSHNotiInMessageConfig *)config;
- (BOOL)replaceImagepathWithConfig:(JPUSHNotiInMessageConfig *)config resultBlock:(JPUSHInMessageResult)result;
- (void)replacefilepathWithDownloadResult:(BOOL)success withFilepathArr:(id)paths
    messageConfig:(JPUSHNotiInMessageConfig *)config resultBlock:(JPUSHInMessageResult)result;
- (BOOL)fadeInBannerMessage:(JPUSHNotiInMessageView *)view;
- (BOOL)fadeInCurrentNotification:(JPUSHNotiInMessageView *)view;
- (void)showMessageWithConfig:(JPUSHNotiInMessageConfig *)config;
- (void)prepareNotificationToBeShown:(JPUSHNotiInMessageView *)view resultBlock:(JPUSHInMessageResult)result;
- (void)clearTopBannerStatus;
- (void)saveTopBannerStatus:(JPUSHNotiInMessageView *)view;
- (void)reportNotiMessageStatus:(int)status withMessageConfig:(JPUSHNotiInMessageConfig *)config;
- (void)reportMessageStatus:(int)status withMessageID:(id)messageID withMsgData:(id)data;
- (void)reportInappStatus:(int)status withConfig:(id)config;
- (void)reportInappStatusOnJcoreThread:(id)param;
- (UIWindow *)getCurrentWindow;
- (JPushInAppMessage *)convertToInAppMsg:(id)config clickAction:(id)action;
- (BOOL)initMessageViewLayout:(JPUSHNotiInMessageView *)view;
- (double)messageLeftMargin:(JPUSHNotiInMessageView *)view;
- (double)messageTopMargin;
- (double)messageRightMargin:(JPUSHNotiInMessageView *)view;
- (void)dissmissMessageView:(JPUSHNotiInMessageView *)view;
- (void)fadeOutCurrentNotification:(JPUSHNotiInMessageView *)view;
- (void)urlOrDeeplinkJump:(id)action completion:(JPUSHInMessageResult)completion;
@end
