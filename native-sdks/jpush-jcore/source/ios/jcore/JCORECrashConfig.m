#import "JCORECrashConfig.h"
#import "JCOREClientInfo.h"
#import "JCORECrashConfigRequest.h"
#import "JCORELog.h"
#import "JCOREThread.h"
#import "JCOREUtilities.h"
#import "RecoveredCache.h"
#define CrashFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Interface/JCORECrashConfig.m"
#define CrashLog(lineValue, fmt, ...)                                                                        \
    [JCORELog log:YES                                                                                        \
            level:0x3ff                                                                                      \
             flag:0x100                                                                                      \
          context:0                                                                                          \
             file:CrashFile                                                                                  \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineValue                                                                                  \
              tag:nil                                                                                        \
           format:fmt, ##__VA_ARGS__]
@implementation JCORECrashConfig
- (instancetype)init {
    self = [super init];
    if (self)
        _statusCrashCountSwitch = YES;
    return self;
}
- (void)handleConfigContentWithResponse:(id)response completion:(void (^)(BOOL, BOOL))completion {
    self.isFinish = YES;
    if (!response) {
        CrashLog(63, @"[%s] reponse is bad", __PRETTY_FUNCTION__);
        return;
    }
    if (![JCOREThread isMainThread]) {
        CrashLog(67, @"[%s] thread is not JCoreThread", __PRETTY_FUNCTION__);
        return;
    }
    CrashLog(71, @"[%s] config content is %@", __PRETTY_FUNCTION__, response);
    if (![response isKindOfClass:[NSDictionary class]]) {
        CrashLog(73, @"[%s] content is bad", __PRETTY_FUNCTION__);
        return;
    }
    if (![[response allKeys] containsObject:@"code"] || [response[@"code"] integerValue] != 0) {
        CrashLog(78, @"[%s] values is bad", __PRETTY_FUNCTION__);
        return;
    }
    if (![[response allKeys] containsObject:@"data"]) {
        CrashLog(82, @"[%s] values is bad", __PRETTY_FUNCTION__);
        return;
    }
    id data = response[@"data"];
    BOOL statusSwitch = NO;
    if ([[data allKeys] containsObject:@"crash_switch"]) {
        _statusSwitch = [data[@"crash_switch"] boolValue];
        statusSwitch = _statusSwitch;
    }
    BOOL statusSupersetSwitch = NO;
    if ([[data allKeys] containsObject:@"net_switch"]) {
        _statusSupersetSwitch = [data[@"net_switch"] boolValue];
        statusSupersetSwitch = _statusSupersetSwitch;
    }
    BOOL statusTcpSSL = NO;
    if ([[data allKeys] containsObject:@"use_tcp_ssl"]) {
        _statusTcpSSL = [data[@"use_tcp_ssl"] boolValue];
        statusTcpSSL = _statusTcpSSL;
    }
    BOOL statusCrashCountSwitch = YES;
    if ([[data allKeys] containsObject:@"count_switch"]) {
        _statusCrashCountSwitch = [data[@"count_switch"] boolValue];
        statusCrashCountSwitch = _statusCrashCountSwitch;
    }
    BOOL statusTcpReportSwitch = NO;
    if ([[data allKeys] containsObject:@"ios_tcpreport_switch"]) {
        _statusTcpReportSwitch = [data[@"ios_tcpreport_switch"] boolValue];
        statusTcpReportSwitch = _statusTcpReportSwitch;
    }
    BOOL statusNetLengthSwitch = NO;
    if ([[data allKeys] containsObject:@"net_length_switch"]) {
        _statusNetLengthSwitch = [data[@"net_length_switch"] boolValue];
        statusNetLengthSwitch = _statusNetLengthSwitch;
    }
    if ([[data allKeys] containsObject:@"net_length_threshold"]) {
        _netLengthThreshold = [data[@"net_length_threshold"] doubleValue];
    }
    if ([[data allKeys] containsObject:@"net_count_threshold"]) {
        _netCountThreshold = [data[@"net_count_threshold"] integerValue];
    }
    if ([[data allKeys] containsObject:@"net_conn_length_threshold"]) {
        _netConnLengthThreshold = [data[@"net_conn_length_threshold"] doubleValue];
    }
    if ([[data allKeys] containsObject:@"net_conn_count_threshold"]) {
        _netConnCountThreshold = [data[@"net_conn_count_threshold"] integerValue];
    }
    BOOL newSocketSwitch = NO;
    if ([[data allKeys] containsObject:@"new_socket_switch"]) {
        _newSocketSwitch = [data[@"new_socket_switch"] integerValue] != 0;
        newSocketSwitch = _newSocketSwitch;
    }
    BOOL checkIpv6Switch = NO;
    if ([[data allKeys] containsObject:@"ios_checkIpv6_switch"]) {
        _checkIpv6Switch = [data[@"ios_checkIpv6_switch"] integerValue] != 0;
        checkIpv6Switch = _checkIpv6Switch;
    }
    BOOL statusExceptionSwitch = NO;
    if ([[data allKeys] containsObject:@"exception_switch"]) {
        _statusExceptionSwitch = [data[@"exception_switch"] boolValue];
        statusExceptionSwitch = _statusExceptionSwitch;
    }
    BOOL statusTcpExceptionSwitch = NO;
    if ([[data allKeys] containsObject:@"tcp_exception_switch"]) {
        _statusTcpExceptionSwitch = [data[@"tcp_exception_switch"] boolValue];
        statusTcpExceptionSwitch = _statusTcpExceptionSwitch;
    }
    NSDictionary *cached = @{
        @"createTime" : [JCOREUtilities dateToString:[NSDate date]],
        @"expirationTime" : [JCOREUtilities dateToString:[NSDate dateWithTimeIntervalSinceNow:86400]],
        @"uid" : [NSString stringWithFormat:@"%llu", [JCOREClientInfo uid]],
        @"isSwitch" : @(statusSwitch),
        @"isNetMoniterSwitch" : @(statusSupersetSwitch),
        @"isUseTcpSSLSwitch" : @(statusTcpSSL),
        @"isCrashCountSwitch" : @(statusCrashCountSwitch),
        @"isTcpReportSwitch" : @(statusTcpReportSwitch),
        @"statusNetLengthSwitch" : @(statusNetLengthSwitch),
        @"netLengthThreshold" : @(_netLengthThreshold),
        @"netCountThreshold" : @(_netCountThreshold),
        @"netConnLengthThreshold" : @(_netConnLengthThreshold),
        @"netConnCountThreshold" : @(_netConnCountThreshold),
        @"netSocketSwitch" : @(newSocketSwitch),
        @"checkIpv6Switch" : @(checkIpv6Switch),
        @"exceptionSwitch" : @(statusExceptionSwitch),
        @"tcpExceptionSwitch" : @(statusTcpExceptionSwitch)
    };
    [JCORECacheUtilities setCacheObject:cached forKey:@"CrashConfig"];
    if (completion)
        completion(_statusSwitch, _statusSupersetSwitch);
}
- (void)pullCrashSwitchWithAppkeyAndCompletion:(id)args {
    id appkey = nil;
    void (^completion)(BOOL, BOOL) = nil;
    BOOL force = NO;
    if ([args isKindOfClass:[NSArray class]] && [args count] >= 2) {
        appkey = args[0];
        completion = args[1];
    }
    if ([args isKindOfClass:[NSArray class]] && [args count] >= 3 && [args[2] isKindOfClass:[NSNumber class]])
        force = [args[2] boolValue];
    if (![appkey length]) {
        CrashLog(195, @"[%s] appkey is bad", __PRETTY_FUNCTION__);
        return;
    }
    if (![JCOREThread isMainThread]) {
        CrashLog(199, @"[%s] thread is not JCoreThread", __PRETTY_FUNCTION__);
        return;
    }
    __block id cached = [JCORECacheUtilities cacheObjectForKey:@"CrashConfig"];
    __weak JCORECrashConfig *weakSelf = self;
    RecoveryOpaqueCallback handler = ^(id response, NSError *error) {
      [self setIsPull:NO];
      JCORECrashConfig *strongSelf = weakSelf;
      if (!error) {
          [strongSelf handleConfigContentWithResponse:response completion:completion];
          return;
      }
      if (cached && [cached isKindOfClass:[cached class]]) {
          CrashLog(215, @"sentry config content (cache) is %@", cached);
          [self setIsFinish:YES];
          if (strongSelf) {
              if ([[cached allKeys] containsObject:@"isSwitch"])
                  strongSelf->_statusSwitch = [cached[@"isSwitch"] boolValue];
              if ([[cached allKeys] containsObject:@"isNetMoniterSwitch"])
                  strongSelf->_statusSupersetSwitch = [cached[@"isNetMoniterSwitch"] boolValue];
              if ([[cached allKeys] containsObject:@"isUseTcpSSLSwitch"])
                  strongSelf->_statusTcpSSL = [cached[@"isUseTcpSSLSwitch"] boolValue];
              if ([[cached allKeys] containsObject:@"isCrashCountSwitch"])
                  strongSelf->_statusCrashCountSwitch = [cached[@"isCrashCountSwitch"] boolValue];
              if ([[cached allKeys] containsObject:@"isTcpReportSwitch"])
                  strongSelf->_statusTcpReportSwitch = [cached[@"isTcpReportSwitch"] boolValue];
              if ([[cached allKeys] containsObject:@"statusNetLengthSwitch"])
                  strongSelf->_statusNetLengthSwitch = [cached[@"statusNetLengthSwitch"] boolValue];
              if ([[cached allKeys] containsObject:@"netLengthThreshold"])
                  strongSelf->_netLengthThreshold = [cached[@"netLengthThreshold"] doubleValue];
              if ([[cached allKeys] containsObject:@"netCountThreshold"])
                  strongSelf->_netCountThreshold = [cached[@"netCountThreshold"] integerValue];
              if ([[cached allKeys] containsObject:@"netConnLengthThreshold"])
                  strongSelf->_netConnLengthThreshold = [cached[@"netConnLengthThreshold"] doubleValue];
              if ([[cached allKeys] containsObject:@"netConnCountThreshold"])
                  strongSelf->_netConnCountThreshold = [cached[@"netConnCountThreshold"] integerValue];
              if ([[cached allKeys] containsObject:@"netSocketSwitch"])
                  strongSelf->_newSocketSwitch = [cached[@"netSocketSwitch"] integerValue] != 0;
              if ([[cached allKeys] containsObject:@"checkIpv6Switch"])
                  strongSelf->_checkIpv6Switch = [cached[@"checkIpv6Switch"] integerValue] != 0;
              if ([[cached allKeys] containsObject:@"exceptionSwitch"])
                  strongSelf->_statusExceptionSwitch = [cached[@"exceptionSwitch"] boolValue];
              if ([[cached allKeys] containsObject:@"tcpExceptionSwitch"])
                  strongSelf->_statusTcpExceptionSwitch = [cached[@"tcpExceptionSwitch"] boolValue];
          }
          if (completion)
              completion([strongSelf statusSwitch], [strongSelf statusSupersetSwitch]);
      } else {
          completion(NO, YES);
      }
    };
    if (!force && cached && [cached isKindOfClass:[cached class]]) {
        [NSDate date];
        if ([[cached allKeys] containsObject:@"expirationTime"]) {
            id expiration = cached[@"expirationTime"];
            if (!expiration) {
                [self pullCrashCofigContentWithAppkey:appkey completion:handler];
                return;
            }
            NSDate *date = [JCOREUtilities transformStrToDate:expiration];
            if ([[NSDate date] compare:date] == NSOrderedDescending) {
                [self pullCrashCofigContentWithAppkey:appkey completion:handler];
                return;
            }
            CrashLog(283, @"sentry config content (cache) is %@", cached);
            self.isFinish = YES;
            self.isPull = NO;
            if (![[cached allKeys] containsObject:@"isSwitch"]) {
                [self pullCrashCofigContentWithAppkey:appkey completion:handler];
                return;
            }
            if ([[cached allKeys] containsObject:@"isSwitch"])
                self->_statusSwitch = [cached[@"isSwitch"] boolValue];
            if ([[cached allKeys] containsObject:@"isNetMoniterSwitch"])
                self->_statusSupersetSwitch = [cached[@"isNetMoniterSwitch"] boolValue];
            if ([[cached allKeys] containsObject:@"isUseTcpSSLSwitch"])
                self->_statusTcpSSL = [cached[@"isUseTcpSSLSwitch"] boolValue];
            if ([[cached allKeys] containsObject:@"isCrashCountSwitch"])
                self->_statusCrashCountSwitch = [cached[@"isCrashCountSwitch"] boolValue];
            if ([[cached allKeys] containsObject:@"isTcpReportSwitch"])
                self->_statusTcpReportSwitch = [cached[@"isTcpReportSwitch"] boolValue];
            if ([[cached allKeys] containsObject:@"statusNetLengthSwitch"])
                self->_statusNetLengthSwitch = [cached[@"statusNetLengthSwitch"] boolValue];
            if ([[cached allKeys] containsObject:@"netLengthThreshold"])
                self->_netLengthThreshold = [cached[@"netLengthThreshold"] doubleValue];
            if ([[cached allKeys] containsObject:@"netCountThreshold"])
                self->_netCountThreshold = [cached[@"netCountThreshold"] integerValue];
            if ([[cached allKeys] containsObject:@"netConnLengthThreshold"])
                self->_netConnLengthThreshold = [cached[@"netConnLengthThreshold"] doubleValue];
            if ([[cached allKeys] containsObject:@"netConnCountThreshold"])
                self->_netConnCountThreshold = [cached[@"netConnCountThreshold"] integerValue];
            if ([[cached allKeys] containsObject:@"netSocketSwitch"])
                self->_newSocketSwitch = [cached[@"netSocketSwitch"] integerValue] != 0;
            if ([[cached allKeys] containsObject:@"checkIpv6Switch"])
                self->_checkIpv6Switch = [cached[@"checkIpv6Switch"] integerValue] != 0;
            if ([[cached allKeys] containsObject:@"exceptionSwitch"])
                self->_statusExceptionSwitch = [cached[@"exceptionSwitch"] boolValue];
            if ([[cached allKeys] containsObject:@"tcpExceptionSwitch"])
                self->_statusTcpExceptionSwitch = [cached[@"tcpExceptionSwitch"] boolValue];
            if (completion)
                completion(self.statusSwitch, self.statusSupersetSwitch);
        }
        return;
    }
    [self pullCrashCofigContentWithAppkey:appkey completion:handler];
}
- (void)pullCrashCofigContentWithAppkey:(id)appkey completion:(RecoveryOpaqueCallback)completion {
    if (self.isPull) {
        CrashLog(351, @"[%s] is pulling config, return", __PRETTY_FUNCTION__);
        return;
    }
    self.isPull = YES;
    JCORECrashConfigRequest *request = [JCORECrashConfigRequest new];
    request.appkey = appkey;
    [request sendRequest:completion];
}
- (void)getTcpSSLCache:(void (^)(BOOL))completion {
    if (![JCOREThread isMainThread]) {
        CrashLog(365, @"[%s]thread is not JCoreThread", __PRETTY_FUNCTION__);
        return;
    }
    id cached = [JCORECacheUtilities cacheObjectForKey:@"CrashConfig"];
    BOOL value = NO;
    if (cached && [cached isKindOfClass:[cached class]] &&
        [[cached allKeys] containsObject:@"isUseTcpSSLSwitch"])
        value = [cached[@"isUseTcpSSLSwitch"] boolValue];
    if (completion)
        completion(value);
}
// This selector is an actual empty original method.
- (void)sendAllReports {
}
@end
