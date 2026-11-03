#pragma once
#import <Foundation/Foundation.h>
@class JPUSHNotiInMessageConfig, JPUSHNotiInMessageManager;
@protocol JPushNotiInMessageDataSourceDelegate <NSObject>
@optional
- (BOOL)jPushInMessageNewDataComing;
- (void)jPushInMessageNeedShowNow;
@end
typedef void (^JPUSHNotiManagerCompletion)(JPUSHNotiInMessageManager *);
typedef void (^JPUSHNotiConfigCompletion)(JPUSHNotiInMessageConfig *);
@interface JPUSHNotiInMessageManager : NSObject {
  __weak id<JPushNotiInMessageDataSourceDelegate> _delegate;
  long long _priority;
  NSDate *_foregroundDate;
  long long _delayTime;
  NSArray *_datasArr;
}
@property(nonatomic, strong) NSDate *foregroundDate;
@property(nonatomic) long long delayTime;
@property(nonatomic, strong) NSArray *datasArr;
@property(nonatomic, weak) id<JPushNotiInMessageDataSourceDelegate> delegate;
@property long long priority;
+ (void)inMessageManager:(JPUSHNotiManagerCompletion)completion;
+ (void)inMessageManagerInJCoreThread:(JPUSHNotiManagerCompletion)completion;
- (void)applicationWillEnterForeground;
- (void)applicationDidBecomeActive;
- (void)getCacheDatas;
- (void)handleInMessageDic:(id)message;
- (BOOL)checkMessageDic:(id)message;
- (BOOL)cacheMessageDic:(id)message;
- (id)messageDicForCache;
- (void)getInMessageContent:(JPUSHNotiConfigCompletion)completion;
- (void)getInMessageContentWithJCoreThread:(JPUSHNotiConfigCompletion)completion;
- (void)callbackMessageConfig:(JPUSHNotiInMessageConfig *)config withCompletion:(JPUSHNotiConfigCompletion)completion;
- (void)delayTriggerMessageShow;
- (void)triggerMessageShow;
- (void)messageNeedShowNow;
- (BOOL)checkForegroundDateWithMessagDic;
- (BOOL)checkBannerStatus;
- (id)getCallBackContent:(id)message;
- (void)reportMessageStatus:(int)status withMessageID:(id)messageID msgData:(id)data;
- (BOOL)checkTimeOutWithMessageDic:(id)message;
- (void)handleRevokeMessageIdList:(id)messageIDs;
- (void)handleRevokeMessageId:(id)messageID;
- (BOOL)checkMsgRevoke:(id)messageID;
- (void)cacheRevokeMsgId:(id)messageID;
@end
