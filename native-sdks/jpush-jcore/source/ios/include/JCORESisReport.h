#pragma once
#import "RecoveredTransport.h"
@interface JCORESisReportModel : NSObject <NSCopying> {
    unsigned char _type;
    unsigned char _net;
    NSString *_addr;
    unsigned long long _uid;
    unsigned long long _rtime;
    unsigned long long _interval;
    double _lat;
    double _lng;
    unsigned long long _ltime;
    NSInteger _code;
    NSString *_appkey;
}
@property(nonatomic) unsigned char type;
@property(strong, nonatomic) NSString *addr;
@property(nonatomic) unsigned long long uid;
@property(nonatomic) unsigned long long rtime;
@property(nonatomic) unsigned long long interval;
@property(nonatomic) unsigned char net;
@property(nonatomic) double lat;
@property(nonatomic) double lng;
@property(nonatomic) unsigned long long ltime;
@property(nonatomic) NSInteger code;
@property(copy, nonatomic) NSString *appkey;
- (id)copyWithZone:(NSZone *)zone;
- (NSMutableDictionary *)info;
@end
@interface JCORESisReport : NSObject <JCORENetworkDataDelegate> {
    NSArray *_sendingSisReportData;
    NSMutableDictionary *_sisReportDataDic;
    NSMutableDictionary *_connReportDataDic;
    NSMutableDictionary *_tcpReportDataDic;
    JCORENetwork *_udpSocket;
}
@property(copy, nonatomic) NSMutableDictionary *sisReportDataDic;
@property(copy, nonatomic) NSMutableDictionary *connReportDataDic;
@property(copy, nonatomic) NSMutableDictionary *tcpReportDataDic;
@property(strong, nonatomic) JCORENetwork *udpSocket;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
+ (instancetype)shared;
- (instancetype)init;
- (void)addSisReportData:(unsigned char)type ip:(id)ip port:(unsigned short)port;
- (void)updateSisReportDataWithIp:(id)ip
                             port:(unsigned short)port
                             code:(long long)code
                             type:(unsigned char)type;
- (void)reportDGDataWithHost:(id)host port:(unsigned short)port;
- (NSData *)reportData;
- (NSMutableData *)assemble:(id)data magic:(char)magic key:(unsigned int)key;
- (void)resetUdpSocket;
- (void)parse:(id)data ip:(id)ip port:(unsigned short)port;
- (id)didReceivedSisData:(id)data;
- (void)socket:(id)socket recvData:(id)data handler:(void (^)(BOOL))handler;
- (void)socket:(id)socket sentData:(id)data;
- (void)socket:(id)socket noRecvData:(id)data;
- (void)socket:(id)socket unsentData:(id)data;
@end
