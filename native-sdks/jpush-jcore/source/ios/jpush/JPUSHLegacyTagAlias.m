#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCOREClientInfo.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JPUSHLegacyTagAlias.h"
#import "JPUSHTagAliasValidation.h"
#define LEGACY_LOG(asyncValue, flagValue, lineValue, formatValue, ...)         \
  [JCORELog log:asyncValue                                                     \
          level:0x3ff                                                          \
           flag:flagValue                                                      \
        context:0                                                              \
           file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"         \
                "ios-jpush/JPush/JPush/Model Classes/JPUSHTagAlias.m"          \
       function:__PRETTY_FUNCTION__                                            \
           line:lineValue                                                      \
            tag:nil                                                            \
         format:formatValue, ##__VA_ARGS__]
@implementation JPUSHSetTagAlias
@synthesize tags = _tags, alias = _alias, type = _type,
            createTime = _createTime;
- (id)copyWithZone:(NSZone *)zone {
  // The original sends this selector to self; retain its dynamic dispatch.
  JPUSHSetTagAlias *copy = [self copyWithZone:zone];
  if (copy) {
    copy->_tags = [_tags copy];
    copy->_alias = [_alias copy];
    copy->_type = _type;
  }
  return copy;
}
- (instancetype)init {
  if ((self = [super init])) {
    self.protocolType = 10;
    self.version = 4;
    self.controlFlag = 53;
  }
  return self;
}
- (BOOL)isEqualToRequest:(id)request {
  return [request isKindOfClass:JPUSHSetTagAlias.class] &&
         (unsigned int)self.rid == (unsigned int)[request rid];
}
- (NSData *)bodyData {
  NSMutableData *data = [NSMutableData new];
  [data jcore_appendString:[JCOREClientInfo appKey]];
  [data jcore_appendDataWithLength:[self requestJsonData]];
  return data;
}
- (NSData *)requestJsonData {
  NSMutableDictionary *info = [@{@"platform" : @"i"} mutableCopy];
  if (_tags)
    info[@"tags"] = [[_tags allObjects] componentsJoinedByString:@","];
  if (_alias)
    info[@"alias"] = _alias;
  return [JCOREUtilities jsonToData:info];
}
@end
@implementation JPUSHTagAlias
- (instancetype)init {
  if ((self = [super init]))
    _dictSequenceToReq = [NSMutableDictionary new];
  return self;
}
- (void)dealloc {
  _dictSequenceToReq = nil;
}
- (void)sendFailedTagAlias {
  if (_failSendTagAliasRequest) {
    LEGACY_LOG(YES, 8, 0x30,
               @"开始请求失败的background tagAlias\n tags:%@ "
               @"Alias:%@\",args.tags,args.alias]",
               _failSendTagAliasRequest.tags, _failSendTagAliasRequest.alias);
    JPUSHSetTagAliasArg *arg = [JPUSHSetTagAliasArg new];
    arg.tags = [_failSendTagAliasRequest.tags copy];
    arg.alias = [_failSendTagAliasRequest.alias copy];
    arg.type = _failSendTagAliasRequest.type;
    [self setTagsAlias:arg];
  }
}
- (void)setTagsAlias:(JPUSHSetTagAliasArg *)arg {
  switch (arg.type) {
  case 0:
    [self setTagAliasAutomatic:arg];
    break;
  case 1:
    [self setTagAliasUsingBlock:arg];
    break;
  case 2:
    [self setTagAliasUsingCallback:arg];
    break;
  default:
    break;
  }
}
- (void)setTagAliasUsingCallback:(JPUSHSetTagAliasArg *)arg {
  LEGACY_LOG(YES, 0x100, 0x5e, @"开始设置TagAlias callback\n tags:%@ Alias:%@",
             arg.tags, arg.alias);
  JPUSHSetTagAlias *request = [JPUSHSetTagAlias new];
  arg.sequence = (unsigned int)request.rid;
  if (arg.cbInvocation) {
    _dictSequenceToReq[@(request.rid)] = arg;
    [arg beginTimeoutTimer];
  }
  int code = [JPUSHTagAliasValidation validateTagsAlias:arg.tags
                                                  alias:arg.alias];
  if (!code) {
    request.tags = arg.tags;
    request.alias = arg.alias;
    request.type = 2;
    request.createTime = [JCOREUtilities currentTimestamp];
    [self handleBeforeSend:request arg:arg];
    [request send];
  } else
    [self doTagAliasCallbackForSeq:arg.sequence withResCode:code];
}
- (void)setTagAliasUsingBlock:(JPUSHSetTagAliasArg *)arg {
  LEGACY_LOG(YES, 0x100, 0x85, @"开始设置TagAlias block\n tags:%@ Alias:%@",
             arg.tags, arg.alias);
  JPUSHSetTagAlias *request = [JPUSHSetTagAlias new];
  arg.sequence = (unsigned int)request.rid;
  if (arg.completionHandler) {
    _dictSequenceToReq[@(request.rid)] = arg;
    [arg beginTimeoutTimer];
  }
  int code = [JPUSHTagAliasValidation validateTagsAlias:arg.tags
                                                  alias:arg.alias];
  if (!code) {
    request.tags = arg.tags;
    request.alias = arg.alias;
    request.type = 1;
    [self handleBeforeSend:request arg:arg];
    [request send];
  } else
    [self doTagAliasCallbackForSeq:arg.sequence withResCode:code];
}
- (void)handleBeforeSend:(JPUSHSetTagAlias *)request
                     arg:(JPUSHSetTagAliasArg *)arg {
  __weak JPUSHTagAlias *weakSelf = self;
  request.completionHandler = ^(id response, NSError *error) {
    if (error && error.code == 6026) {
      LEGACY_LOG(NO, 1, 0xa4, @"TagAlias block packet Length Exceed");
      [arg stopTimeoutTimer];
      [weakSelf doTagAliasCallbackForSeq:arg.sequence withResCode:6026];
      [arg p_deQueueFromTcpBlockQueue];
    }
  };
}
- (void)setTagAliasAutomatic:(JPUSHSetTagAliasArg *)arg {
  LEGACY_LOG(YES, 0x100, 0xb9,
             @"开始设置TagAlias background\n tags:%@ Alias:%@", arg.tags,
             arg.alias);
  JPUSHSetTagAlias *request = [JPUSHSetTagAlias new];
  arg.sequence = (unsigned int)request.rid;
  _dictSequenceToReq[@(request.rid)] = arg;
  int code = [JPUSHTagAliasValidation validateTagsAlias:arg.tags
                                                  alias:arg.alias];
  if (!code) {
    request.tags = arg.tags;
    request.alias = arg.alias;
    request.type = 0;
    [self p_sendAutomaticRequest:request WithArg:arg];
  } else
    [self doTagAliasCallbackForSeq:arg.sequence withResCode:code];
}
- (void)doSetTagsAliasCallbackWithRetJson:(id)json {
  id info =
      [JCOREUtilities dataToJSON:[json dataUsingEncoding:NSUTF8StringEncoding]];
  if (info) {
    id sequence = info[@"sequence"], code = info[@"code"];
    [self doTagAliasCallbackForSeq:[sequence unsignedLongLongValue]
                       withResCode:[code intValue]];
    if ([code intValue] == 6020 || [code intValue] == 6002)
      _failSendTagAliasRequest = _dictSequenceToReq[sequence];
  }
}
- (void)doTagAliasCallbackForSeq:(unsigned long long)seq withResCode:(int)code {
  NSNumber *key = @(seq);
  JPUSHSetTagAliasArg *arg = _dictSequenceToReq[key];
  LEGACY_LOG(YES, 0x100, 0xea,
             @"doTagAliasCallback seq: %llu, cbFound %d, withResCode %d", seq,
             arg != nil, code);
  if (arg) {
    [arg stopTimeoutTimer];
    switch (arg.type) {
    case 0:
      [self doTagAliasCallbackAutomatic:arg withResCode:code];
      break;
    case 1:
      [self doTagAliasCallbackUsingBlockForArgs:arg withResCode:code];
      break;
    case 2:
      [self doTagAliasCallbackUsingCallbackForArgs:arg withResCode:code];
      break;
    default:
      break;
    }
    if (_failSendTagAliasRequest && !code &&
        arg.sequence == (unsigned int)_failSendTagAliasRequest.rid)
      _failSendTagAliasRequest = nil;
    [_dictSequenceToReq removeObjectForKey:key];
  }
}
- (void)doTagAliasCallbackUsingCallbackForArgs:(JPUSHSetTagAliasArg *)arg
                                   withResCode:(int)code {
  JPUSHSetTagAliasArg *copy = [arg copy];
  dispatch_async(dispatch_get_main_queue(), ^{
    id tags = copy.tags, alias = copy.alias;
    NSInvocation *invocation = copy.cbInvocation;
    if (copy.target) {
      int resultCode = code;
      [invocation setArgument:&resultCode atIndex:2];
      [invocation setArgument:&tags atIndex:3];
      [invocation setArgument:&alias atIndex:4];
      [invocation invoke];
    }
  });
  arg.target = nil;
}
- (void)doTagAliasCallbackUsingBlockForArgs:(JPUSHSetTagAliasArg *)arg
                                withResCode:(int)code {
  dispatch_async(dispatch_get_main_queue(), ^{
    arg.completionHandler(code, arg.tags, arg.alias);
    arg.completionHandler = nil;
  });
}
- (void)doTagAliasCallbackAutomatic:(JPUSHSetTagAliasArg *)arg
                        withResCode:(int)code {
  if (code == 6002)
    return;
  if (!code)
    LEGACY_LOG(YES, 2, 0x144,
               @"setTagsWithAlias (background): succeed.\ntags:%@ Alias:%@",
               arg.tags, arg.alias);
  else
    LEGACY_LOG(YES, 2, 0x147,
               @"setTagsWithAlias (background): failed with Code:%d.\ntags:%@ "
               @"Alias:%@",
               code, arg.tags, arg.alias);
}
- (void)p_sendAutomaticRequest:(JPUSHSetTagAlias *)request
                       WithArg:(JPUSHSetTagAliasArg *)arg {
  if (_failSendTagAliasRequest &&
      [_failSendTagAliasRequest isKindOfClass:JPUSHSetTagAlias.class]) {
    if ([self p_mergeOldTagAliasRequest:_failSendTagAliasRequest
                         WithNewRequest:&request
                              UpdateArg:&arg])
      _dictSequenceToReq[@(request.rid)] = arg;
  }
  if (request && arg) {
    [self handleBeforeSend:request arg:arg];
    [request send];
    LEGACY_LOG(YES, 8, 0x164,
               @"TagAlias background 已经发送\n tags:%@ Alias:%@", arg.tags,
               arg.alias);
  }
}
- (BOOL)p_mergeOldTagAliasRequest:(JPUSHSetTagAlias *)old
                   WithNewRequest:(JPUSHSetTagAlias *__autoreleasing *)request
                        UpdateArg:(JPUSHSetTagAliasArg *__autoreleasing *)arg {
  BOOL merged = NO;
  if (old.tags && !(*request).tags) {
    (*request).tags = [old.tags copy];
    (*arg).tags = [old.tags copy];
    merged = YES;
  }
  if (old.alias && !(*request).alias) {
    (*request).alias = [old.alias copy];
    (*arg).alias = [old.alias copy];
    merged = YES;
  }
  return merged;
}
@end
#endif
