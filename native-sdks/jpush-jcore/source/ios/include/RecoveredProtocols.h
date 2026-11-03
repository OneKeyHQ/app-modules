#pragma once
#import <Foundation/Foundation.h>
// Original JCommonReport callers and JCORE HTTP dispatch pass response plus NSError.
typedef void (^RecoveryOpaqueCallback)(id response, NSError *error);
typedef void (^RecoveryTagsCompletion)(NSInteger resultCode, NSSet *tags, NSInteger seq);
typedef void (^RecoveryTagValidCompletion)(NSInteger resultCode, NSSet *tags, NSInteger seq, BOOL isBind);
typedef void (^RecoveryAliasCompletion)(NSInteger resultCode, NSString *alias, NSInteger seq);

@protocol JCORECacheProtocol <NSObject>
@required
@property (nonatomic) long long type;
@required
@property (strong, nonatomic) NSString * key;
@required
@property (strong, nonatomic) NSString * dirName;
@optional
@property (strong, nonatomic) id content;
@optional
@property (nonatomic) BOOL encrypt;
@optional
@property (nonatomic) BOOL compress;
@end

@protocol JCORENetChannelProtocol <NSObject>
@required
@property (copy, nonatomic) RecoveryOpaqueCallback completionHandler;
@end

@protocol JCOREHttpProtocol <JCORENetChannelProtocol>
@required
@property (strong, nonatomic) NSMutableURLRequest * request;
@required
@property (strong, nonatomic) id requestInfo;
@required
@property (nonatomic) BOOL accountIDRequired;
@required
@property (nonatomic) BOOL cacheRequired;
@required
@property (copy, nonatomic) NSString * reportFlag;
@optional
@property (nonatomic) unsigned char controlFlag;
@optional
@property (strong, nonatomic) NSArray * addressList;
@end

@protocol JCommonCrashSerializableProtocol <NSObject>
@required
- (NSMutableDictionary *)serialize;
@end

@protocol JCOREConfigProtocol;
@protocol JCOREConfigProtocol <NSObject>
@required
@property (nonatomic, copy) NSString *appKey;
@property (nonatomic, copy) NSString *channel;
@property (nonatomic, copy) NSString *advertisingId;
@property (nonatomic) BOOL isProduction;
@property (nonatomic, copy) void (^completionHandler)(NSError *error, id<JCOREConfigProtocol> config);
@optional
@property (nonatomic) long long index;
@end

@class JCORESocketConfig;

@protocol JCOREIntegrateProtocol <NSObject>
@required
@property (nonatomic) long long index;
@required
@property (nonatomic) unsigned char property;
@required
@property (copy, nonatomic) NSString *version;
@required
@property (nonatomic) long long type;
@optional
@property (nonatomic) long long regIndex;
@optional
@property (nonatomic) long long loginIndex;
@optional
@property (nonatomic) long long business;
@optional
@property (nonatomic) unsigned char loginVersion;
@optional
@property (nonatomic) unsigned char registerVersion;
@optional
@property (nonatomic) BOOL tcpKeepAlive;
@optional
@property (copy, nonatomic) NSString *versionFieldName;
@end

@protocol JCORETcpConnectProtocol <NSObject>
@required
@property (nonatomic) unsigned char protocolType;
@required
@property (nonatomic) unsigned short statusCode;
@required
@property (strong, nonatomic) JCORESocketConfig *socketConfig;
@end

@protocol JCORETcpRespondProtocol <NSObject>
@required
@property (nonatomic) unsigned char version;
@required
@property (nonatomic) unsigned char protocolType;
@required
@property (nonatomic) unsigned char algorithm;
@required
@property (nonatomic) unsigned short rid;
@required
@property (nonatomic) unsigned long long uid;
@required
@property (copy, nonatomic) NSString *msgData;
@required
- (NSData *)bodyData;
@end

@protocol JCORETcpReceiptProtocol <JCORETcpRespondProtocol>
@property (nonatomic) unsigned short statusCode;
@end

@protocol JCORETcpResponseProtocol <JCORETcpRespondProtocol>
@required
@property (nonatomic) unsigned short statusCode;
@required
@property (copy, nonatomic) NSString *moniter_id;
@end
