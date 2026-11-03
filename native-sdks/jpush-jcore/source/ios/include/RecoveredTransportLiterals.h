#pragma once
#define REC_CF_e5558 @"JCORENetworkDataDelegate is nil"
#define REC_CF_e5578 @"参数错误，初始化失败"
#define REC_CF_db4f8 @"NET_TCP_CONN"
#define REC_CF_e6b58 @"invalid request"
#define REC_CF_e6b78 @"start https request, url:%@"
#define REC_CF_e6bb8 @"session completion info nil"
#define REC_CF_e6bd8 @"https request did finish loading, url:%@"
#define REC_CF_STATUS @"[%s] statusSwitch is no pass"
#define REC_CF_APPKEY @"[%s] This Appkey does not apply to this SDK "
#define REC_NETWORK_LOG(asyncValue,flagValue,fun,lineValue,fmt,...) [JCORELog log:(asyncValue) level:0x3ff flag:(flagValue) context:0 file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Network/JCORENetwork.m" function:(fun) line:(lineValue) tag:nil format:(fmt), ##__VA_ARGS__]
#define REC_SESSION_LOG(asyncValue,flagValue,fun,lineValue,fmt,...) [JCORELog log:(asyncValue) level:0x3ff flag:(flagValue) context:0 file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/HttpNetwork/Session/JCOREURLSessionController.m" function:(fun) line:(lineValue) tag:nil format:(fmt), ##__VA_ARGS__]
