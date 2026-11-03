#pragma once
#import "RecoveredTransport.h"
@class JCOREConnectIpModel;
@protocol JCOREConnectPoolDelegate <NSObject>
- (void)didSocketSend:(id)socket;
- (void)didAllIpsFailed;
- (void)didPoolExhausted:(id)pool;
@end
@interface JCOREConnectPool : NSObject {
    BOOL _isConnected;
    double _timeout;
    double _interval;
    long long _maxNum;
    __weak id _socketDelegate;
    __weak id<JCOREConnectPoolDelegate> _poolDelegate;
    __weak NSTimer *_conTimer;
    NSMutableArray *_timeoutTimerArray;
    NSMutableArray *_socketArray;
    NSMutableArray *_ipArray;
    NSMutableArray *_ipvxHistoryArray;
    long long _minorIpvxTimes;
}
@property(nonatomic) BOOL isConnected;
@property(nonatomic) double timeout;
@property(nonatomic) double interval;
@property(nonatomic) long long maxNum;
@property(weak, nonatomic) id socketDelegate;
@property(weak, nonatomic) id<JCOREConnectPoolDelegate> poolDelegate;
@property(weak, nonatomic) NSTimer *conTimer;
@property(strong, nonatomic) NSMutableArray *timeoutTimerArray;
@property(strong, nonatomic) NSMutableArray *socketArray;
@property(strong, nonatomic) NSMutableArray *ipArray;
@property(strong, nonatomic) NSMutableArray *ipvxHistoryArray;
@property(nonatomic) long long minorIpvxTimes;
+ (instancetype)instance;
- (instancetype)init;
- (void)asyConnectWithIps:(id)ips;
- (void)synConnectWithIps:(id)ips;
- (void)connectWithIps:(id)ips maxNum:(long long)maxNum;
- (void)launchASocket;
- (void)start;
- (void)stop;
- (void)stopOtherSocketWithout:(id)socket;
- (void)startConTimer;
- (void)stopConTimer;
- (id)arrayAfterTransSomeIpType:(id)ips;
- (NSUInteger)ipType;
- (NSUInteger)getIpv6Num;
- (NSUInteger)getIpv4Num;
- (JCOREConnectIpModel *)getAValidModel;
- (void)removeModelObjectWithIP:(id)ip;
- (JCOREConnectIpModel *)findModelWithIP:(id)ip;
- (void)connectionDidRegisterFailed:(id)config;
- (void)connectionDidLoginFailed:(id)config;
- (void)connectionDidAckTimeout:(id)config;
- (void)connectionLoginSuccess:(id)config;
- (void)connectionDidFailed:(id)socket;
- (void)connectionDidSuccess:(id)socket;
- (void)connectionDidFailedWithConfig:(id)config;
- (void)recodeSuccess:(id)ip costTime:(double)time;
- (void)recodeFailure:(id)ip;
- (void)recodeTimeout:(id)ip;
- (void)poolExhausted;
- (void)socketSend:(id)socket;
- (void)didAllIpsFailed;
@end
