#pragma once
#import <Foundation/Foundation.h>
@interface JPUSHNotificationController : NSObject
- (void)start;
- (void)didLogin:(id)notification;
- (void)handleCmdControl:(id)notification;
- (void)handleCheckNotiAuth:(id)content;
- (void)checkCachedUndoNotification;
- (void)cacheUndoNotification:(id)entry;
- (void)handleUndoContent:(id)content;
- (void)handleUndoNotification:(id)messageID;
- (void)removeNotification:(id)identifier withNeedFind:(BOOL)find;
- (void)handleOtherNotification:(id)messageID;
- (void)reportMsgStatusWithMessageId:(id)messageID;
- (id)identifierFromNotification:(id)notification;
- (id)userInfoFromNotification:(id)notification;
- (void)invokeMethod:(id)target name:(id)name param:(id)param result:(void *)result;
@end
