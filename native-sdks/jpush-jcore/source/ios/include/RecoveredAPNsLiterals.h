#pragma once
#define REC_APNS_TIME_KEY @"__JpushDeviceTokenUpdateTimer"
#define REC_APNS_NOT_READY @"Not get deviceToken yet. After successful login, a custom message can be sent, but the APNs notification cannot. Until the deviceToken is obtained and reported successfully, the APNs notification can be used normally."
#define REC_APNS_CERTIFICATE @"Not get deviceToken yet. Maybe: your certificate not configured APNs? or current network is not so good so APNs registration failed?  or there is no APNs register code? Please refer to JPush docs."
#define REC_APNS_SAME @"the same device token, not be updated again"
#define REC_APNS_EXPIRED @"upload current deviceToken because of time up"
#define REC_APNS_LOG(flagValue,fun,lineValue,fmt,...) [JCORELog log:YES level:0x3ff flag:(flagValue) context:0 file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/JPush/JPush/Controller/JPUSHDeviceTokenController.m" function:(fun) line:(lineValue) tag:nil format:(fmt), ##__VA_ARGS__]
