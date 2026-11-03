#pragma once
#import <Foundation/Foundation.h>
#import "JCommonSupersetMoniter.h"
#import "RecoveredProtocols.h"
@interface JCommonSupersetManager : NSObject {
    NSString *_uuid;
    NSMutableDictionary *_moniterContainer;
    long long _tcp_id;
    NSString *_tcpHost;
    long long _tcpPort;
}
@property (strong, nonatomic) NSString *uuid;
@property (strong) NSMutableDictionary *moniterContainer;
@property long long tcp_id;
@property (strong) NSString *tcpHost;
@property long long tcpPort;
+ (instancetype)sharedManager;
+ (NSString *)startHttpMoniter:(NSString *)method url:(NSString *)url request_byte:(long long)bytes;
+ (NSString *)startConnectionMoniter:(NSString *)type host:(NSString *)host port:(long long)port;
+ (NSString *)startTcpEventMoniter:(NSString *)type;
+ (void)endHttpMoniter:(NSString *)identifier response_byte:(long long)bytes error_code:(long long)error status_code:(long long)status;
+ (void)endConnectionMoniter:(NSString *)identifier error_code:(long long)error status_code:(long long)status;
+ (void)setSupersetMoniterOn;
+ (void)clearAllCachedDatas;
+ (JCommonSupersetMoniter *)commonMoniter:(NSString *)type;
- (void)startHttpMoniter:(JCommonSupersetMoniter *)monitor;
- (void)startConnectionMoniter:(JCommonSupersetMoniter *)monitor;
- (void)endHttpMoniter:(JCommonSupersetMoniter *)monitor;
- (void)endConnectionMoniter:(JCommonSupersetMoniter *)monitor;
- (void)saveMoniter:(JCommonSupersetMoniter *)monitor;
- (void)reportAllSuperset;
@end
