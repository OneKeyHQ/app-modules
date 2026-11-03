#pragma once
#import <Foundation/Foundation.h>
#import "JCORESupersetMoniter.h"
#import "RecoveredProtocols.h"
@interface JCORESupersetManager : NSObject {
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
+ (void)sendSupersetMoniter:(id)information completionHandler:(RecoveryOpaqueCallback)completion;
+ (JCORESupersetMoniter *)commonMoniter:(NSString *)type;
- (void)startHttpMoniter:(JCORESupersetMoniter *)monitor;
- (void)startConnectionMoniter:(JCORESupersetMoniter *)monitor;
- (void)endHttpMoniter:(JCORESupersetMoniter *)monitor;
- (void)endConnectionMoniter:(JCORESupersetMoniter *)monitor;
- (void)saveMoniter:(JCORESupersetMoniter *)monitor;
- (void)reportAllSuperset;
@end
