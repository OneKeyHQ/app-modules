#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JPUSHClientController.h"
#import "JPUSHHelper.h"
#import "JPUSHMessage.h"
#import "JPUSHMessageReport.h"
#import "JPUSHPropertyRequest.h"
#import "JPUSHSocketEventController.h"
#import "JPUSHStopPush.h"
#import "JPUSHTagAliasRequest.h"
#import "JPUSHUserPropertyRequest.h"
#import "JPUSHGeofenceNetwork.h"
#import "JPUSHLiveActivity.h"
#import "JPUSHLegacyTagAlias.h"
#import "JPUSHGeofenceManager.h"
#import "JPUSHInMessageManager.h"
extern NSString *const kJCORETcpEventResponseNotification;
extern NSString *const kJPFNetworkDidReceiveMessageNotification;
#define SOCKET_LOG(flagValue, lineValue, formatValue, ...)                     \
  [JCORELog                                                                    \
           log:YES                                                             \
         level:0x3ff                                                           \
          flag:flagValue                                                       \
       context:0                                                               \
          file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"          \
               "ios-jpush/JPush/JPush/Controller/JPUSHSocketEventController.m" \
      function:__PRETTY_FUNCTION__                                             \
          line:lineValue                                                       \
           tag:nil                                                             \
        format:formatValue, ##__VA_ARGS__]
@implementation JPUSHSocketEventController
- (instancetype)init {
  if ((self = [super init]))
    [NSNotificationCenter.defaultCenter
        addObserver:self
           selector:@selector(receiveTcpEventResponse:)
               name:kJCORETcpEventResponseNotification
             object:nil];
  return self;
}
- (void)dealloc {
  [NSNotificationCenter.defaultCenter removeObserver:self];
}
- (void)receiveTcpEventResponse:(id)notification {
  if ([notification isKindOfClass:NSNotification.class]) {
    id info = [notification userInfo];
    if ([info isKindOfClass:NSDictionary.class]) {
      id data = info[@"data"];
      if ([data conformsToProtocol:@protocol(JCORETcpRespondProtocol)])
        [self parseTcpData:data];
    }
  }
}
- (void)parseTcpData:(id<JCORETcpRespondProtocol>)response {
  Class responseClass = nil;
  switch (response.protocolType) {
  case 26:
    SOCKET_LOG(0x100, 0x53, @"receive user property response cmd:%d", 26);
    responseClass = JPUSHUserPropertyResponse.class;
    break;
  case 28:
    SOCKET_LOG(0x100, 0x4b, @"receive tags response cmd:%d", 28);
    responseClass = JPUSHTagsResponse.class;
    break;
  case 29:
    SOCKET_LOG(0x100, 0x4f, @"receive alias response cmd:%d", 29);
    responseClass = JPUSHAliasResponse.class;
    break;
  case 36:
    SOCKET_LOG(0x100, 0x5a, @"receive geofence info response cmd:%d", 36);
    responseClass = JPUSHGeofenceInfoMessage.class;
    break;
  case 37:
    SOCKET_LOG(0x100, 0x5e, @"receive get geofence response cmd:%d", 37);
    responseClass = JPushGetGeofenceResponse.class;
    break;
  case 38:
    SOCKET_LOG(0x100, 0x62, @"receive get property response cmd:%d", 38);
    responseClass = JPUSHPropertyResponse.class;
    break;
  case 39:
    SOCKET_LOG(0x100, 0x66, @"receive LAPuskToken response cmd:%d", 39);
    responseClass = JPUSHLiveActivityTokenResponse.class;
    break;
  case 40:
    SOCKET_LOG(0x100, 0x6a, @"receive stop/resume push response cmd:%d", 40);
    responseClass = JPUSHStopPushOperationResponse.class;
    break;
  default:
    if (response.protocolType == 3)
      [self handleMessage:response];
    return;
  }
  JPUSHNewResponse *parsed = [[responseClass alloc] initWithResponse:response];
  if (parsed)
    [parsed handleReceipt];
}
- (void)handleMessage:(id)response {
  JPUSHMessage *message = [[JPUSHMessage alloc] initWithResponse:response];
  unsigned char type = message.messageType;
  if (type == 0) {
    [message sendReceipt];
    [self dispatchCustomPushMessage:message];
  } else if (type == 20) {
    [[[JPUSHClientController sharedClient] tagAliasMng]
        doSetTagsAliasCallbackWithRetJson:message.messageContent];
  } else if (type == 100) {
    [message sendReceipt];
    id info =
        [JCOREUtilities dataToJSON:[message.messageContent
                                       dataUsingEncoding:NSUTF8StringEncoding]];
    if ([info isKindOfClass:NSDictionary.class] &&
        [[info allKeys] containsObject:kJPGeofenceKeyword]) {
      id decoded = [self decodeGeofenceMessage:message];
      NSDictionary *content = @{
        @"geofence" : info[kJPGeofenceKeyword],
        @"message" : decoded ?: @{}
      };
      [[JPUSHGeofenceManager sharedGeoFence] handleCustomPushMessage:content];
    }
  } else if (type == 103) {
    [message sendReceipt];
    id info =
        [JCOREUtilities dataToJSON:[message.messageContent
                                       dataUsingEncoding:NSUTF8StringEncoding]];
    if ([info isKindOfClass:NSDictionary.class])
      [JPUSHInMessageManager handleNotiInMessageDic:info];
  } else if (type == 115) {
    [message sendReceipt];
    id info =
        [JCOREUtilities dataToJSON:[message.messageContent
                                       dataUsingEncoding:NSUTF8StringEncoding]];
    if ([info isKindOfClass:NSDictionary.class]) {
      unsigned long long messageID = message.messageID;
      NSMutableDictionary *content =
          [NSMutableDictionary dictionaryWithDictionary:info];
      [content setValue:[NSString stringWithFormat:@"%llu", messageID]
                 forKey:@"messageId"];
      [JPUSHInMessageManager handleInMessageDic:content];
    }
  }
}
- (void)dispatchCustomPushMessage:(JPUSHMessage *)message {
  id info =
      [JCOREUtilities dataToJSON:[message.messageContent
                                     dataUsingEncoding:NSUTF8StringEncoding]];
  if (info) {
    NSMutableDictionary *content = [NSMutableDictionary dictionary];
    [content addEntriesFromDictionary:[self getJPUSHMessageFromInfo:info]];
    if (message.messageID)
      content[@"_j_msgid"] = @(message.messageID);
    [JPUSHHelper postNotification:kJPFNetworkDidReceiveMessageNotification
                     withUserInfo:content];
    NSString *messageID =
        message.messageID
            ? [NSString stringWithFormat:@"%llu", message.messageID]
            : @"";
    id data = info[@"_j_data_"];
    JPUSHMessageReportRequest *report = [JPUSHMessageReportRequest new];
    [report setCustomPushMsgStatus:1106 messageID:messageID flag:-10000];
    if (data && [data isKindOfClass:NSString.class] && [data length])
      report.msgData = data;
    [report send];
  }
}
- (id)getJPUSHMessageFromInfo:(id)info {
  SOCKET_LOG(0x100, 0xcd, @"Action - getJCOREMessageFromInfo - info:%@", info);
  id title = info[@"title"], message = info[@"message"],
     type = info[@"content_type"], extras = info[@"extras"],
     notify = info[@"_j_notify"];
  if (!message) {
    SOCKET_LOG(2, 0xd4, @"缺少自定义消息必须字段message");
    return nil;
  }
  if ([extras isKindOfClass:NSString.class])
    extras = [JCOREUtilities
        dataToJSON:[extras dataUsingEncoding:NSUTF8StringEncoding]];
  if (![extras isKindOfClass:NSDictionary.class])
    extras = nil;
  NSMutableDictionary *content = [NSMutableDictionary dictionaryWithCapacity:5];
  if (title)
    content[@"title"] = title;
  content[@"content"] = message;
  if (type)
    content[@"content_type"] = type;
  if (extras)
    content[@"extras"] = extras;
  if (notify && [notify isKindOfClass:NSDictionary.class])
    content[@"_j_notify"] = notify;
  return content;
}
- (id)decodeGeofenceMessage:(JPUSHMessage *)message {
  id info =
      [JCOREUtilities dataToJSON:[message.messageContent
                                     dataUsingEncoding:NSUTF8StringEncoding]];
  id title = info[@"title"], body = info[@"message"],
     type = info[@"content_type"], extras = info[@"extras"];
  if (!body) {
    SOCKET_LOG(2, 0xf8, @"缺少自定义消息必须字段message");
    return nil;
  }
  if ([extras isKindOfClass:NSString.class])
    extras = [JCOREUtilities
        dataToJSON:[extras dataUsingEncoding:NSUTF8StringEncoding]];
  if (![extras isKindOfClass:NSDictionary.class])
    extras = nil;
  NSMutableDictionary *content = [NSMutableDictionary dictionaryWithCapacity:5];
  if (title)
    content[@"title"] = title;
  content[@"content"] = body;
  if (type)
    content[@"content_type"] = type;
  if (extras)
    content[@"extras"] = extras;
  if (message.messageID)
    content[@"_j_msgid"] = @(message.messageID);
  return content;
}
@end
#endif
