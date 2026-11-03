#pragma once
#import "JPUSHPropertyModels.h"
@interface JPUSHPropertyManagerController : NSObject {
    BOOL _isRequesting;
    BOOL _isLogin;
    NSMutableArray *_requestQueryQueue;
    NSMutableArray *_operateTimeQueue;
}
@property(nonatomic, strong) NSMutableArray *requestQueryQueue;
@property(nonatomic, strong) NSMutableArray *operateTimeQueue;
@property(nonatomic) BOOL isRequesting;
@property(nonatomic) BOOL isLogin;
+ (instancetype)sharedInstance;
- (void)didLogin;
- (void)handleTcpClose;
- (void)propertyOperate:(id)properties
                   type:(long long)type
          andCompletion:(id)completion
                 andSeq:(long long)seq;
- (void)doCallBacksForResContent:(id)content andRid:(unsigned long long)rid;
- (void)handleSuccessResponseWith:(JPUSHPropertyReqItem *)item
                   andNetworkCode:(long long)code
                           msgDic:(id)message;
- (void)handleOtherResponseWith:(JPUSHPropertyReqItem *)item
                 andNetworkCode:(long long)code
                         msgDic:(id)message;
- (void)handleInternalErrorResponseWith:(JPUSHPropertyReqItem *)item;
- (void)doTimeoutCallBacksForResSeq:(unsigned long long)rid;
- (void)doOversizeCallBacksForResSeq:(unsigned long long)rid;
- (void)doErrorCallBackWithSeq:(unsigned long long)rid andCode:(long long)code;
- (id)getCallBackPropertyList:(id)properties opearteType:(long long)type;
- (long long)checkProperty:(id)properties opearteType:(long long)type;
- (id)changePropertyValueToString:(id)properties;
- (long long)checkKey:(id)key;
- (BOOL)shouldSendPropertyOperate:(double)timestamp;
- (void)invokePropertyBlockInMainThread:(unsigned long long)seq
                                andCode:(long long)code
                                  Block:(id)completion
                           propertyInfo:(id)properties;
- (void)invokePropertyBlockInMainThread:(JPUSHPropertyReqItem *)item andCode:(long long)code;
- (void)invokePropertyBlockInMainThread:(JPUSHPropertyReqItem *)item
                                andCode:(long long)code
                                 msgDic:(id)message;
@end
