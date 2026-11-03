#pragma once
#import "RecoveredTransport.h"
@class JCOREUdpSocket, JCORENWUdpSocket;
@protocol JCORESocketDataProtocol;

@interface JCOREConnectIpModel : NSObject <JCORESocketConfigProtocol> {
    BOOL _loading;
    unsigned int _port;
    double _startTime;
    NSString *_ip;
    NSString *_host;
    NSUInteger _ipType;
}
@property (nonatomic) double startTime;
@property (strong, nonatomic) NSString *ip;
@property (nonatomic) BOOL loading;
@property (copy, nonatomic) NSString *host;
@property (nonatomic) unsigned int port;
@property (nonatomic) NSUInteger ipType;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
- (instancetype)initWithIp:(NSString *)ip;
@end

@interface JCOREWifiCacheModel : NSObject <NSCoding, NSCopying> {
    NSString *_mac_address;
    NSString *_ssid;
    long long _ipvxType;
    double _cacheTime;
    double _time_expire;
}
@property (copy, nonatomic) NSString *mac_address;
@property (copy, nonatomic) NSString *ssid;
@property (nonatomic) long long ipvxType;
@property (nonatomic) double cacheTime;
@property (nonatomic) double time_expire;
- (instancetype)initWithMacAddress:(NSString *)address ssid:(NSString *)ssid ipvxType:(long long)type;
@end

@interface JCOREUdpPacket : NSObject {
    int _type;
    id<JCORESocketDataProtocol> _buffer;
    NSData *_address;
    double _timeout;
    __weak NSTimer *_timeoutTimer;
    __weak JCOREUdpSocket *_udpSocket;
}
@property (strong, nonatomic) id<JCORESocketDataProtocol> buffer;
@property (strong, nonatomic) NSData *address;
@property (nonatomic) double timeout;
@property (nonatomic) int type;
@property (weak, readonly, nonatomic) NSTimer *timeoutTimer;
@property (weak, readonly, nonatomic) JCOREUdpSocket *udpSocket;
- (instancetype)initWithType:(int)type data:(id<JCORESocketDataProtocol>)data address:(NSData *)address timeout:(double)timeout udpSocket:(JCOREUdpSocket *)socket;
- (void)timeoutCallback:(NSTimer *)timer;
- (void)invalidTimeoutTimer;
@end

@interface JCORENWUdpPacket : NSObject {
    int _type;
    id<JCORESocketDataProtocol> _buffer;
    NSData *_address;
    double _timeout;
    __weak NSTimer *_timeoutTimer;
    __weak JCORENWUdpSocket *_udpSocket;
}
@property (strong, nonatomic) id<JCORESocketDataProtocol> buffer;
@property (strong, nonatomic) NSData *address;
@property (nonatomic) double timeout;
@property (nonatomic) int type;
@property (weak, readonly, nonatomic) NSTimer *timeoutTimer;
@property (weak, readonly, nonatomic) JCORENWUdpSocket *udpSocket;
- (instancetype)initWithType:(int)type data:(id<JCORESocketDataProtocol>)data address:(NSData *)address timeout:(double)timeout udpSocket:(JCORENWUdpSocket *)socket;
- (void)timeoutCallback:(NSTimer *)timer;
- (void)invalidTimeoutTimer;
@end

// These original SDK classes have no ivars or own methods.
@interface JCOREUdpChannelController : NSObject @end
@interface JCOREUdpEventController : NSObject @end
@interface JCommonOperationManager : NSObject @end
