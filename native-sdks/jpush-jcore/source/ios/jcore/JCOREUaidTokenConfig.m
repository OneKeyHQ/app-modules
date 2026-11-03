#import "JCOREUaidTokenConfig.h"
#import "JCOREClientInfo.h"
#import "JCOREDataSecurity.h"
#import "JCORELog.h"
#import "JCOREThread.h"
#import "JCOREUaidTokenGetRequest.h"
#import "JCOREUaidTokenPushRequest.h"
#import "JCOREUaidTokenUrlRequest.h"
#import "JCOREUtilities.h"
#import "RecoveredCache.h"
@interface JCommonService : NSObject
+ (void)trackUAIDWithCode:(long long)code;
@end
#define UaidFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Interface/JCOREUaidTokenConfig.m"
#define UaidLog(lineValue, fmt, ...)                                                                         \
    [JCORELog log:YES                                                                                        \
            level:0x3ff                                                                                      \
             flag:0x100                                                                                      \
          context:0                                                                                          \
             file:UaidFile                                                                                   \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineValue                                                                                  \
              tag:nil                                                                                        \
           format:fmt, ##__VA_ARGS__]
@implementation JCOREUaidTokenConfig
- (void)handleConfigContentWithResponse:(id)response completion:(void (^)(BOOL, NSDictionary *))completion {
    self.isFinish = YES;
    if (!response || ![response isKindOfClass:[NSDictionary class]]) {
        UaidLog(39, @"[%s] reponse is bad", __PRETTY_FUNCTION__);
        [JCommonService trackUAIDWithCode:1010];
        return;
    }
    if (![JCOREThread isMainThread]) {
        UaidLog(44, @"[%s]thread is not JCoreThread", __PRETTY_FUNCTION__);
        return;
    }
    UaidLog(48, @"[%s] config content is %@", __PRETTY_FUNCTION__, response);
    if (![[response allKeys] containsObject:@"code"] || [response[@"code"] integerValue] != 2000) {
        UaidLog(51, @"[%s] values is bad", __PRETTY_FUNCTION__);
        [JCommonService trackUAIDWithCode:1009];
        return;
    }
    if (![[response allKeys] containsObject:@"data"] ||
        (![[response[@"data"] allKeys] containsObject:@"token_switch"] &&
         ![[response allKeys] containsObject:@"next_time"])) {
        UaidLog(56, @"[%s] values is bad", __PRETTY_FUNCTION__);
        [JCommonService trackUAIDWithCode:1010];
        return;
    }
    id value = response[@"data"][@"token_switch"];
    if (!value || ![value isKindOfClass:[NSNumber class]]) {
        UaidLog(62, @"[%s] token_switch value is bad", __PRETTY_FUNCTION__);
        [JCommonService trackUAIDWithCode:1010];
        return;
    }
    _statusSwitch = [value boolValue];
    BOOL enabled = _statusSwitch;
    id next = response[@"data"][@"next_time"];
    if (!next || ![next isKindOfClass:[NSNumber class]])
        next = @604800;
    NSString *date =
        [JCOREUtilities dateToString:[NSDate dateWithTimeIntervalSinceNow:(double)[next floatValue]]];
    NSDictionary *cached = @{
        @"save_time" : [JCOREUtilities dateToString:[NSDate date]],
        @"next_time" : date,
        @"uid" : [NSString stringWithFormat:@"%llu", [JCOREClientInfo uid]],
        @"isSwitch" : @(enabled)
    };
    [JCORECacheUtilities setCacheObject:cached forKey:@"UaidTokenConfig"];
    if (completion)
        completion(_statusSwitch, response);
}
- (void)pullUaidTokenWithAppkeyAndCompletion:(id)args {
    if (![JCOREThread isMainThread]) {
        UaidLog(96, @"[%s]thread is not JCoreThread", __PRETTY_FUNCTION__);
        return;
    }
    id cached = [JCORECacheUtilities cacheObjectForKey:@"UaidTokenConfig"];
    __weak JCOREUaidTokenConfig *weakSelf = self;
    RecoveryOpaqueCallback completion = ^(id response, NSError *error) {
      JCOREUaidTokenConfig *strongSelf = weakSelf;
      [strongSelf setIsPull:NO];
      if (!strongSelf || error)
          return;
      [strongSelf
          handleConfigContentWithResponse:response
                               completion:^(BOOL enabled, NSDictionary *content) {
                                 if (!enabled) {
                                     UaidLog(113, @"uaid switch is off");
                                     [JCommonService trackUAIDWithCode:1011];
                                     return;
                                 }
                                 id list = [content[@"data"] valueForKey:@"list"];
                                 if (!list || ![list isKindOfClass:[NSArray class]] || ![list count]) {
                                     UaidLog(119, @"no carrier list");
                                     [JCommonService trackUAIDWithCode:1012];
                                     return;
                                 }
                                 [strongSelf getToken:list
                                           completion:^(id token, NSError *tokenError) {
                                             [strongSelf pushUaidTokenWithAppkeyAndCompletion:token];
                                           }];
                               }];
    };
    if (cached && [cached isKindOfClass:[NSDictionary class]]) {
        [NSDate date];
        if ([[cached allKeys] containsObject:@"next_time"]) {
            id next = cached[@"next_time"];
            if (!next) {
                [self pullUaidTokenCofigContentWithCompletion:completion];
                return;
            }
            NSDate *date = [JCOREUtilities transformStrToDate:next];
            if ([[NSDate date] compare:date] == NSOrderedDescending)
                [self pullUaidTokenCofigContentWithCompletion:completion];
            else
                [JCommonService trackUAIDWithCode:1006];
        }
        return;
    }
    [self pullUaidTokenCofigContentWithCompletion:completion];
}
- (void)pullUaidTokenCofigContentWithCompletion:(RecoveryOpaqueCallback)completion {
    if (self.isPull) {
        UaidLog(160, @"[%s] is alreay pull config", __PRETTY_FUNCTION__);
        [JCommonService trackUAIDWithCode:1007];
        return;
    }
    self.isPull = YES;
    JCOREUaidTokenUrlRequest *request = [JCOREUaidTokenUrlRequest new];
    [request sendRequest:completion];
}
- (void)forceUpdateConfig:(id)args {
    if (![JCOREThread isMainThread]) {
        UaidLog(174, @"[%s]thread is not JCoreThread", __PRETTY_FUNCTION__);
        return;
    }
    __weak JCOREUaidTokenConfig *weakSelf = self;
    JCOREUaidTokenUrlRequest *request = [JCOREUaidTokenUrlRequest new];
    [request sendRequest:^(id response, NSError *error) {
      JCOREUaidTokenConfig *strongSelf = weakSelf;
      if (!strongSelf || error)
          [JCommonService trackUAIDWithCode:1008];
      else
          [strongSelf handleConfigContentWithResponse:response completion:nil];
    }];
}
- (void)pushUaidTokenWithAppkeyAndCompletion:(id)info {
    if (![JCOREThread isMainThread]) {
        UaidLog(209, @"[%s]thread is not JCoreThread", __PRETTY_FUNCTION__);
        return;
    }
    NSString *appkey = [JCOREClientInfo appKey];
    if (!appkey.length) {
        UaidLog(215, @"[%s] appkey is bad", __PRETTY_FUNCTION__);
        [JCommonService trackUAIDWithCode:1004];
        return;
    }
    if (![info isKindOfClass:[NSDictionary class]] || ![[info allKeys] containsObject:@"respBody"]) {
        UaidLog(221, @"[%s] result is bad", __PRETTY_FUNCTION__);
        [JCommonService trackUAIDWithCode:1015];
        return;
    }
    [self pushUaidTokenCofigContentWithAppkey:appkey
                                    tokenInfo:[info mutableCopy]
                                   completion:^(id response, NSError *error) {
                                     if (error) {
                                         UaidLog(254, @"pushUaidToken is failed");
                                         [JCommonService trackUAIDWithCode:1016];
                                         return;
                                     }
                                     if (!response || ![response isKindOfClass:[NSData class]]) {
                                         UaidLog(233, @"[%s] reponse is bad", __PRETTY_FUNCTION__);
                                         [JCommonService trackUAIDWithCode:1017];
                                         return;
                                     }
                                     NSString *text = [[NSString alloc] initWithData:response
                                                                            encoding:NSUTF8StringEncoding];
                                     NSData *decoded = [JCOREDataSecurity dataFromBase64:text];
                                     NSData *plain = [JCOREDataSecurity aesDecryptData:decoded
                                                                               withKey:@"P8aQemLpVcPRCsl2"
                                                                                    iv:"IS78SPks2hwCZBLa"];
                                     id content = [JCOREUtilities dataToJSON:plain];
                                     UaidLog(241, @"[%s] token push result is %@", __PRETTY_FUNCTION__,
                                             content);
                                     if (!content || ![content isKindOfClass:[NSDictionary class]]) {
                                         UaidLog(243, @"[%s] content is bad", __PRETTY_FUNCTION__);
                                         [JCommonService trackUAIDWithCode:1017];
                                         return;
                                     }
                                     if ([[content allKeys] containsObject:@"code"] &&
                                         [content[@"code"] integerValue] == 2000) {
                                         [JCommonService trackUAIDWithCode:1000];
                                         return;
                                     }
                                     UaidLog(247, @"[%s] code is bad", __PRETTY_FUNCTION__);
                                     [JCommonService trackUAIDWithCode:1018];
                                   }];
}
- (void)pushUaidTokenCofigContentWithAppkey:(id)appkey
                                  tokenInfo:(id)info
                                 completion:(RecoveryOpaqueCallback)completion {
    id channel = info[@"channelId"], body = info[@"respBody"], header = info[@"respHeader"];
    JCOREUaidTokenPushRequest *request = [JCOREUaidTokenPushRequest new];
    request.appkey = appkey;
    request.channelId = channel;
    request.respBody = body;
    request.respHeader = header;
    request.completionHandler = completion;
    [request send];
}
- (void)getToken:(id)list completion:(RecoveryOpaqueCallback)completion {
    if (![list isKindOfClass:[NSArray class]] || ![list count]) {
        if (completion)
            completion(nil, nil);
        [JCommonService trackUAIDWithCode:1012];
        return;
    }
    for (id entry in list) {
        [JCommonService trackUAIDWithCode:1013];
        [self getToken:entry
               handler:^(id response, NSError *error) {
                 if (completion)
                     completion(error ? nil : response, error);
               }];
    }
}
- (void)getToken:(id)entry handler:(RecoveryOpaqueCallback)completion {
    id headers = entry[@"headers"], body = entry[@"requestBody"], url = entry[@"url"],
       method = entry[@"method"], channel = entry[@"channelId"];
    JCOREUaidTokenGetRequest *request = [[JCOREUaidTokenGetRequest alloc] initWithUrl:url
                                                                               method:method
                                                                              headers:headers
                                                                          requestBody:body];
    [request sendRequest:^(id response, NSError *error) {
      if (error) {
          UaidLog(317, @"get token form url error:%@", error);
          [JCommonService trackUAIDWithCode:1014];
      }
      if (!response || ![response isKindOfClass:[NSDictionary class]]) {
          UaidLog(321, @"get token form url error:%@ response:%@ channelId:%@", error, response, channel);
          if (completion)
              completion(nil, error);
          [JCommonService trackUAIDWithCode:1015];
          return;
      }
      id responseHeaders = response[@"respHeader"], responseBody = response[@"respBody"];
      if (![responseBody isKindOfClass:[NSData class]] || ![responseBody length]) {
          UaidLog(333, @"get token form url error:no respbody channelId:%@", channel);
          if (completion)
              completion(nil, error);
          [JCommonService trackUAIDWithCode:1015];
          return;
      }
      id content = [JCOREUtilities dataToJSON:responseBody];
      if (![content isKindOfClass:[NSDictionary class]]) {
          UaidLog(343, @"get token form url error:no respbody channelId:%@", channel);
          if (completion)
              completion(nil, error);
          [JCommonService trackUAIDWithCode:1015];
          return;
      }
      id text = [JCOREUtilities dictionaryToStr:content];
      if (![text isKindOfClass:[NSString class]] || ![text length]) {
          UaidLog(352, @"get token form url error:no respbody channelId:%@", channel);
          if (completion)
              completion(nil, error);
          [JCommonService trackUAIDWithCode:1015];
          return;
      }
      UaidLog(359, @"get token form url: %@ - channelId:%@ error: %@", text, channel, error);
      NSMutableDictionary *result = [NSMutableDictionary dictionary];
      [result setValue:channel ?: @"" forKey:@"channelId"];
      [result setValue:responseHeaders ?: @{} forKey:@"respHeader"];
      [result setValue:text ?: @"" forKey:@"respBody"];
      if (completion)
          completion(result, nil);
    }];
}
@end
