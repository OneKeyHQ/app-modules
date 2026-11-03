#pragma once
#import "JPUSHPropertyModels.h"
@interface JPUSHTagAliasManagerController : NSObject {
    BOOL _tagsInUse;
    BOOL _aliasInUse;
    BOOL _isLogin;
    NSMutableArray *_requestQueryQueue;
    NSMutableArray *_operateTimeQueue;
}
@property(nonatomic, strong) NSMutableArray *requestQueryQueue;
@property(nonatomic, strong) NSMutableArray *operateTimeQueue;
@property(nonatomic) BOOL tagsInUse;
@property(nonatomic) BOOL aliasInUse;
@property(nonatomic) BOOL isLogin;
+ (instancetype)sharedInstance;
+ (NSString *)getTAOpearteString:(long long)type;
- (void)didLogin;
- (void)handleTcpClose;
- (void)tagOperate:(id)tags type:(long long)type andCompletion:(id)completion andSeq:(long long)seq;
- (void)aliasOperate:(NSString *)alias
                type:(long long)type
       andCompletion:(JPUSHAliasOperationCompletion)completion
              andSeq:(long long)seq;
- (void)doCallBacksForResContent:(id)content andRid:(unsigned long long)rid;
- (void)doTimeoutCallBacksForResSeq:(unsigned long long)rid;
- (void)doOversizeCallBacksForResSeq:(unsigned long long)rid;
- (void)doErrorCallBackWithSeq:(unsigned long long)rid andCode:(int)code;
- (void)handleSuccessResponseWith:(JPUSHTagOrAliasReqItem *)item andContent:(id)content;
- (void)handleInternalErrorResponseWith:(JPUSHTagOrAliasReqItem *)item;
- (void)handleServerUnavaiableResponseWith:(JPUSHTagOrAliasReqItem *)item andContent:(id)content;
- (void)handleOtherResponseWith:(JPUSHTagOrAliasReqItem *)item andNetworkCode:(long long)code;
- (void)handleFailReq:(JPUSHTagOrAliasReqItem *)item andCode:(int)code;
- (void)invokeTagsBlockInMainThread:(id)tags
                                Seq:(unsigned long long)seq
                            andCode:(long long)code
                              Block:(id)completion
                               type:(long long)type;
- (void)invokeTagsBlockInMainThread:(id)tags item:(JPUSHTagOrAliasReqItem *)item andCode:(long long)code;
- (void)invokeAliasBlockInMainThread:(id)alias
                                 Seq:(long long)seq
                             andCode:(long long)code
                               Block:(JPUSHAliasOperationCompletion)completion;
- (BOOL)shouldSendTagOrAliasReq:(double)timestamp;
- (BOOL)isFreezeReport;
@end
