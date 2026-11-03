#pragma once
#define REC_SERVICE_e6ff8 @"service has setup"
#define REC_SERVICE_e7058 @"checkNeedFingerReRegister action"
#define REC_SERVICE_e7078 @"kJCoreModelMd5"
#define REC_SERVICE_e7098 @"checkNeedFingerReRegister - no open fingerswitch, return"
#define REC_SERVICE_e70b8 @"checkNeedFingerReRegister - model name is not same, reRegister"
#define REC_SERVICE_e70d8 @"[%s] is not obtain M"
#define REC_SERVICE_e70f8 @"Action - getDeviceMacAddress"
#define REC_SERVICE_e7118 @"current device mac address: %@"
#define REC_SERVICE_e7138 @"update device mac address: %@"
#define REC_SERVICE_LOG(flagValue,fun,lineValue,fmt,...) [JCORELog log:YES level:0x3ff flag:(flagValue) context:0 file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Controller/JCOREServiceController.m" function:(fun) line:(lineValue) tag:nil format:(fmt), ##__VA_ARGS__]
