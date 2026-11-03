#import "JCORENetworkModels.h"
#import "JCOREAddress.h"

#import "JCOREUdpSocket.h"
#import "JCORENWUdpSocket.h"

@implementation JCOREConnectIpModel
@synthesize startTime = _startTime, ip = _ip, loading = _loading, host = _host, port = _port, ipType = _ipType;
@dynamic hash, superclass, description, debugDescription;
- (instancetype)initWithIp:(NSString *)ip {
    self = [super init];
    if (self) {
        _ip = ip;
        NSRange separator = [ip rangeOfString:@":" options:NSBackwardsSearch];
        if (separator.length) {
            NSString *host = [ip substringToIndex:separator.location];
            NSString *port = [ip substringFromIndex:separator.location + 1];
            if (host.length) {
                _host = [host copy];
                _port = (unsigned short)port.intValue;
                if (!_port) _port = 80;
                _ipType = ![JCOREAddress isIpv4:_host];
                return self;
            }
        }
    }
    return nil;
}
@end

@implementation JCOREWifiCacheModel
@synthesize mac_address = _mac_address, ssid = _ssid, ipvxType = _ipvxType, cacheTime = _cacheTime, time_expire = _time_expire;
- (instancetype)initWithMacAddress:(NSString *)address ssid:(NSString *)ssid ipvxType:(long long)type {
    self = [super init];
    if (self) { _mac_address = address; _ssid = ssid; _ipvxType = type; _time_expire = 2592000.0; }
    return self;
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (self) {
        _mac_address = [coder decodeObjectForKey:@"mac_address"];
        _ssid = [coder decodeObjectForKey:@"ssid"];
        _ipvxType = [coder decodeIntegerForKey:@"wifiIpvxType"];
        _time_expire = [coder decodeDoubleForKey:@"expireTime"];
        _cacheTime = [coder decodeDoubleForKey:@"cacheTime"];
    }
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:_mac_address forKey:@"mac_address"];
    [coder encodeObject:_ssid forKey:@"ssid"];
    [coder encodeInteger:_ipvxType forKey:@"wifiIpvxType"];
    [coder encodeDouble:_time_expire forKey:@"expireTime"];
    [coder encodeDouble:_cacheTime forKey:@"cacheTime"];
}
- (id)copyWithZone:(NSZone *)zone {
    JCOREWifiCacheModel *copy = [[[self class] allocWithZone:zone] init];
    copy->_mac_address = [_mac_address copy]; copy->_ssid = [_ssid copy]; copy->_ipvxType = _ipvxType;
    copy->_time_expire = _time_expire; copy->_cacheTime = _cacheTime;
    return copy;
}
- (NSString *)description {
    return @{@"mac_address":_mac_address, @"ssid":_ssid, @"ipvxType":@(_ipvxType), @"time_expire":@(_time_expire), @"cacheTime":@(_cacheTime)}.description;
}
@end

@implementation JCOREUdpChannelController @end
@implementation JCOREUdpEventController @end
@implementation JCommonOperationManager @end

@implementation JCOREUdpPacket
@synthesize buffer = _buffer, address = _address, timeout = _timeout, type = _type;
@synthesize timeoutTimer = _timeoutTimer, udpSocket = _udpSocket;
- (instancetype)initWithType:(int)type data:(id<JCORESocketDataProtocol>)data address:(NSData *)address timeout:(double)timeout udpSocket:(JCOREUdpSocket *)socket {
    self = [super init];
    if (self) {
        _type = type; _buffer = data; _address = address; _timeout = timeout; _udpSocket = socket;
        if (_timeout > (double)0.001f) _timeoutTimer = [NSTimer scheduledTimerWithTimeInterval:_timeout target:self selector:@selector(timeoutCallback:) userInfo:nil repeats:NO];
    }
    return self;
}
- (void)timeoutCallback:(NSTimer *)timer {
    if (_timeoutTimer != timer) return;
    _timeoutTimer = nil;
    [(id)_udpSocket timeoutWithUdpPacket:self];
}
- (void)invalidTimeoutTimer {
    if (_timeoutTimer.isValid) [_timeoutTimer invalidate];
    _timeoutTimer = nil;
}
@end

@implementation JCORENWUdpPacket
@synthesize buffer = _buffer, address = _address, timeout = _timeout, type = _type;
@synthesize timeoutTimer = _timeoutTimer, udpSocket = _udpSocket;
- (instancetype)initWithType:(int)type data:(id<JCORESocketDataProtocol>)data address:(NSData *)address timeout:(double)timeout udpSocket:(JCORENWUdpSocket *)socket {
    self = [super init];
    if (self) {
        _type = type; _buffer = data; _address = address; _timeout = timeout; _udpSocket = socket;
        if (_timeout > (double)0.001f) _timeoutTimer = [NSTimer scheduledTimerWithTimeInterval:_timeout target:self selector:@selector(timeoutCallback:) userInfo:nil repeats:NO];
    }
    return self;
}
- (void)timeoutCallback:(NSTimer *)timer {
    if (_timeoutTimer != timer) return;
    _timeoutTimer = nil;
    [(id)_udpSocket timeoutWithUdpPacket:self];
}
- (void)invalidTimeoutTimer {
    if (_timeoutTimer.isValid) [_timeoutTimer invalidate];
    _timeoutTimer = nil;
}
@end
