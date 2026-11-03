#pragma once
#import <Foundation/Foundation.h>
#import <dispatch/dispatch.h>
#import "RecoveredProtocols.h"

@class JCommonCrashStacktrace;
@protocol RecoverySerializable <NSObject>
- (NSMutableDictionary *)serialize;
@end
@protocol RecoveryDataConfiguration <NSObject>
- (NSUInteger)type;
@end

@interface JCORECacheModel : NSObject <JCORECacheProtocol> {
    BOOL _encrypt;
    BOOL _compress;
    long long _type;
    NSString * _key;
    NSString * _dirName;
}
@property (nonatomic) long long type;
@property (strong, nonatomic) NSString * key;
@property (strong, nonatomic) NSString * dirName;
@property (nonatomic) BOOL encrypt;
@property (nonatomic) BOOL compress;
@property (strong, nonatomic) id content;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString * description;
@property (readonly, copy) NSString * debugDescription;
@end

typedef void (^RecoveryCacheReadCompletion)(id result);
typedef void (^RecoveryCacheWriteCompletion)(BOOL success);

@interface JCORECacheReadModel : JCORECacheModel {
    RecoveryCacheReadCompletion _block;
}
@property (copy, nonatomic) RecoveryCacheReadCompletion block;
@end

@interface JCORECacheWriteModel : JCORECacheModel {
    id _content;
    RecoveryCacheWriteCompletion _block;
}
@property (strong, nonatomic) id content;
@property (copy, nonatomic) RecoveryCacheWriteCompletion block;
@end

@interface JCommonCrashContexts : NSObject <JCommonCrashSerializableProtocol> {
    NSDictionary * _app;
    NSDictionary * _device;
    NSDictionary * _os;
}
@property (copy, nonatomic) NSDictionary * app;
@property (copy, nonatomic) NSDictionary * device;
@property (copy, nonatomic) NSDictionary * os;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString * description;
@property (readonly, copy) NSString * debugDescription;
- (NSMutableDictionary *)serialize;
@end

@interface JCommonCrashException : NSObject <JCommonCrashSerializableProtocol> {
    NSString * _value;
    NSString * _type;
    NSNumber * _threadId;
    JCommonCrashStacktrace * _stacktrace;
}
@property (copy, nonatomic) NSString * value;
@property (copy, nonatomic) NSString * type;
@property (copy, nonatomic) NSNumber * threadId;
@property (strong, nonatomic) JCommonCrashStacktrace * stacktrace;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString * description;
@property (readonly, copy) NSString * debugDescription;
- (instancetype)initWithValue:(NSString *)value type:(NSString *)type;
- (NSMutableDictionary *)serialize;
@end

@interface JCommonCrashFrame : NSObject <JCommonCrashSerializableProtocol> {
    NSString * _symbolAddress;
    NSString * _function;
    NSString * _package;
    NSString * _imageAddress;
    NSString * _instructionAddress;
    NSNumber * _inApp;
}
@property (copy, nonatomic) NSString * symbolAddress;
@property (copy, nonatomic) NSString * function;
@property (copy, nonatomic) NSString * package;
@property (copy, nonatomic) NSString * imageAddress;
@property (copy, nonatomic) NSString * instructionAddress;
@property (copy, nonatomic) NSNumber * inApp;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString * description;
@property (readonly, copy) NSString * debugDescription;
- (instancetype)init;
- (NSMutableDictionary *)serialize;
@end

@interface JCommonCrashId : NSObject {
    NSUUID * _uuid;
}
@property (strong, nonatomic) NSUUID * uuid;
@property (readonly, copy) NSString * sentryIdString;
+ (instancetype)shareInstance;
+ (instancetype)allocWithZone:(NSZone *)zone;
- (id)copyWithZone:(NSZone *)zone;
- (instancetype)initWithUUID:(NSUUID *)uuid;
- (instancetype)initWithUUIDString:(NSString *)uuidString;
@end

@interface JCommonCrashThread : NSObject <JCommonCrashSerializableProtocol> {
    NSNumber * _threadId;
    NSString * _name;
    JCommonCrashStacktrace * _stacktrace;
    NSNumber * _crashed;
    NSNumber * _current;
}
@property (copy, nonatomic) NSNumber * threadId;
@property (copy, nonatomic) NSString * name;
@property (strong, nonatomic) JCommonCrashStacktrace * stacktrace;
@property (copy, nonatomic) NSNumber * crashed;
@property (copy, nonatomic) NSNumber * current;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString * description;
@property (readonly, copy) NSString * debugDescription;
- (instancetype)initWithThreadId:(NSNumber *)threadId;
- (NSMutableDictionary *)serialize;
@end

@interface JCommonDataConfig : NSObject {
    NSUInteger _type;
}
@property (nonatomic) NSUInteger type;
@end

@interface JCommonDataControl : NSObject {
    BOOL _idfaSwitch;
    NSMutableDictionary * _configMap;
}
@property (strong, nonatomic) NSMutableDictionary * configMap;
@property (nonatomic) BOOL idfaSwitch;
+ (instancetype)sharedManager;
- (void)addConfig:(id)config;
- (void)clearConfig;
- (BOOL)getEnable:(NSUInteger)type;
@end

@interface JCommonReport : NSObject <JCOREHttpProtocol> {
    unsigned char _controlFlag;
    BOOL _accountIDRequired;
    BOOL _cacheRequired;
    NSMutableURLRequest * _request;
    id _requestInfo;
    RecoveryOpaqueCallback _completionHandler;
    NSString * _reportFlag;
}
@property (strong, nonatomic) NSMutableURLRequest * request;
@property (strong, nonatomic) id requestInfo;
@property (copy, nonatomic) RecoveryOpaqueCallback completionHandler;
@property (nonatomic) unsigned char controlFlag;
@property (nonatomic) BOOL accountIDRequired;
@property (nonatomic) BOOL cacheRequired;
@property (copy, nonatomic) NSString * reportFlag;
@property (strong, nonatomic) NSArray * addressList;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString * description;
@property (readonly, copy) NSString * debugDescription;
@end

@interface JPUSHTagAliasVO : NSObject {
    long long _opearteType;
    long long _seq;
    NSSet * _tags;
    NSString * _alias;
    RecoveryTagsCompletion _tagsCompletion;
    RecoveryTagValidCompletion _tagValidCompletion;
    RecoveryAliasCompletion _aliasCompletion;
}
@property (nonatomic) long long opearteType;
@property (nonatomic) long long seq;
@property (copy, nonatomic) NSSet * tags;
@property (copy, nonatomic) NSString * alias;
@property (copy, nonatomic) RecoveryTagsCompletion tagsCompletion;
@property (copy, nonatomic) RecoveryTagValidCompletion tagValidCompletion;
@property (copy, nonatomic) RecoveryAliasCompletion aliasCompletion;
@end
