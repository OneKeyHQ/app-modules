#pragma once
#import <Foundation/Foundation.h>
@class JCORETcpChannelController, JCOREHttpChannelController, JCORESisManager;
typedef void (^JCOREAddressListCompletion)(NSArray *addresses);
@interface JCORENetChannel : NSObject {
    JCORETcpChannelController *_tcpChannelController;
    JCOREHttpChannelController *_httpChannelController;
    NSUInteger _rid;
    JCORESisManager *_sisManager;
}
@property (strong, nonatomic) JCORETcpChannelController *tcpChannelController;
@property (strong, nonatomic) JCOREHttpChannelController *httpChannelController;
@property (nonatomic) NSUInteger rid;
@property (strong, nonatomic) JCORESisManager *sisManager;
+ (void)setupNetService;
+ (void)send:(id)request;
+ (BOOL)httpRequesting;
+ (void)reportAddressListWithType:(NSString *)type completion:(JCOREAddressListCompletion)completion;
+ (void)forceRequestReportAddress;
+ (NSArray *)addressListWithType:(NSString *)type;
+ (void)stopHttpService;
+ (void)stopTcpService;
+ (instancetype)sharedChannel;
- (void)setupNetService;
- (void)send:(id)request;
- (void)reportAddressListForKey:(NSString *)key completion:(JCOREAddressListCompletion)completion;
- (void)stopTcpService;
- (void)stopHttpService;
@end
