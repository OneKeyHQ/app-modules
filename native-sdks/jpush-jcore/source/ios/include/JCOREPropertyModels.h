#pragma once
#import <Foundation/Foundation.h>
#import "RecoveredProtocols.h"

@class JCORESocketConfig, NSData, NSString, NSTimer;

@interface JCOREConfigModel : NSObject <JCOREConfigProtocol> {
    BOOL isProduction;
    NSString *appKey;
    NSString *channel;
    NSString *advertisingId;
    void (^completionHandler)(NSError *error, id<JCOREConfigProtocol> config);
}
@property (copy, nonatomic) NSString *appKey;
@property (copy, nonatomic) NSString *channel;
@property (copy, nonatomic) NSString *advertisingId;
@property (nonatomic) BOOL isProduction;
@property (copy, nonatomic) void (^completionHandler)(NSError *error, id<JCOREConfigProtocol> config);
@property (nonatomic) long long index;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
@end

@interface JCOREHttpSisCompletion : NSObject {
    NSString *_type;
    NSTimer *_timer;
    void (^_completion)(NSArray *addresses);
}
@property (copy, nonatomic) NSString *type;
@property (strong, nonatomic) NSTimer *timer;
@property (copy, nonatomic) void (^completion)(NSArray *addresses);
@end

@interface JCOREIntegrateInfo : NSObject <JCOREIntegrateProtocol> {
    unsigned char _property;
    BOOL _tcpKeepAlive;
    long long _index;
    NSString *_version;
    long long _type;
    long long _regIndex;
    long long _loginIndex;
    long long _business;
    NSString *_versionFieldName;
}
@property (nonatomic) long long index;
@property (nonatomic) unsigned char property;
@property (copy, nonatomic) NSString *version;
@property (nonatomic) long long type;
@property (nonatomic) long long regIndex;
@property (nonatomic) long long loginIndex;
@property (nonatomic) long long business;
@property (nonatomic) BOOL tcpKeepAlive;
@property (copy, nonatomic) NSString *versionFieldName;
@property (nonatomic) unsigned char loginVersion;
@property (nonatomic) unsigned char registerVersion;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
@end

@interface JCORESupersetBaseMoniter : NSObject {
    NSString *_uuid;
    NSString *_moniter_id;
    double _timestamp;
    NSString *_moniter_type;
}
@property (copy, nonatomic) NSString *uuid;
@property (copy, nonatomic) NSString *moniter_id;
@property (nonatomic) double timestamp;
@property (copy, nonatomic) NSString *moniter_type;
@end

@interface JCORETcpConnectModel : NSObject <JCORETcpConnectProtocol> {
    unsigned char protocolType;
    unsigned short statusCode;
    JCORESocketConfig *socketConfig;
}
@property (nonatomic) unsigned char protocolType;
@property (nonatomic) unsigned short statusCode;
@property (strong, nonatomic) JCORESocketConfig *socketConfig;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
@end

@interface JCORETcpEventModel : NSObject <JCORETcpResponseProtocol> {
    unsigned char version;
    unsigned char protocolType;
    unsigned char algorithm;
    unsigned short rid;
    unsigned short statusCode;
    unsigned long long uid;
    NSString *moniter_id;
    NSString *msgData;
    NSData *_bodyData;
}
@property (strong, nonatomic) NSData *bodyData;
@property (nonatomic) unsigned short statusCode;
@property (copy, nonatomic) NSString *moniter_id;
@property (nonatomic) unsigned char version;
@property (nonatomic) unsigned char protocolType;
@property (nonatomic) unsigned char algorithm;
@property (nonatomic) unsigned short rid;
@property (nonatomic) unsigned long long uid;
@property (copy, nonatomic) NSString *msgData;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
@end
