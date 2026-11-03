#pragma once
#import "JPUSHNewRequest.h"
#import "JPUSHSetTagAliasArg.h"
@interface JPUSHSetTagAlias : JPUSHNewRequest <NSCopying> {
  NSSet *_tags;
  NSString *_alias;
  unsigned long long _type;
  double _createTime;
}
@property(nonatomic, strong) NSSet *tags;
@property(nonatomic, strong) NSString *alias;
@property(nonatomic) unsigned long long type;
@property(nonatomic) double createTime;
- (id)copyWithZone:(NSZone *)zone;
- (BOOL)isEqualToRequest:(id)request;
- (NSData *)requestJsonData;
@end
@interface JPUSHTagAlias : NSObject {
  NSMutableDictionary *_dictSequenceToReq;
  JPUSHSetTagAlias *_failSendTagAliasRequest;
}
- (void)sendFailedTagAlias;
- (void)setTagsAlias:(JPUSHSetTagAliasArg *)arg;
- (void)setTagAliasUsingCallback:(JPUSHSetTagAliasArg *)arg;
- (void)setTagAliasUsingBlock:(JPUSHSetTagAliasArg *)arg;
- (void)handleBeforeSend:(JPUSHSetTagAlias *)request
                     arg:(JPUSHSetTagAliasArg *)arg;
- (void)setTagAliasAutomatic:(JPUSHSetTagAliasArg *)arg;
- (void)doSetTagsAliasCallbackWithRetJson:(id)json;
- (void)doTagAliasCallbackForSeq:(unsigned long long)seq withResCode:(int)code;
- (void)doTagAliasCallbackUsingCallbackForArgs:(JPUSHSetTagAliasArg *)arg
                                   withResCode:(int)code;
- (void)doTagAliasCallbackUsingBlockForArgs:(JPUSHSetTagAliasArg *)arg
                                withResCode:(int)code;
- (void)doTagAliasCallbackAutomatic:(JPUSHSetTagAliasArg *)arg
                        withResCode:(int)code;
- (void)p_sendAutomaticRequest:(JPUSHSetTagAlias *)request
                       WithArg:(JPUSHSetTagAliasArg *)arg;
- (BOOL)p_mergeOldTagAliasRequest:(JPUSHSetTagAlias *)old
                   WithNewRequest:(JPUSHSetTagAlias *__autoreleasing *)request
                        UpdateArg:(JPUSHSetTagAliasArg *__autoreleasing *)arg;
@end
