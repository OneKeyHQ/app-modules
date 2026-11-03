#pragma once
#import "RecoveredTransport.h"

@interface JCOREAddress : NSObject <JCORESocketConfigProtocol, NSCopying> {
    unsigned int _port;
    NSString *_host;
    NSUInteger _ipType;
}
@property (copy, nonatomic) NSString *host;
@property (nonatomic) unsigned int port;
@property (nonatomic) NSUInteger ipType;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
- (instancetype)initWithString:(NSString *)string;
+ (NSString *)mergeURLStringWithIp:(id)ip localURL:(id)localURL SSL:(BOOL)ssl;
+ (BOOL)isIpv4:(NSString *)string;
+ (BOOL)isValidIpString:(id)string;
+ (BOOL)isValidHost:(id)host;
+ (BOOL)isValidPort:(id)port;
+ (NSString *)host:(id)string;
+ (NSString *)port:(id)string;
@end
