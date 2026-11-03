#pragma once
#import <Foundation/Foundation.h>
@class JCORENetworkThread, JCORESocketController, JCORESocketConfig, JCORESocketData;
typedef void (^JCORETransportResult)(BOOL result);
typedef void (^JCORESessionCompletion)(id task, id identifier, NSData *data, NSError *error);
@protocol JCORENetworkDelegate <NSObject>
- (void)recvData:(id)data;
- (void)sentData:(id)data;
- (void)sendQueueEmpty;
- (void)connectStateChanged:(id)state;
- (void)noRecvData:(id)data;
- (void)unsentData:(id)data;
@end
@protocol JCORENetworkDataDelegate <NSObject>
- (void)socket:(id)socket recvData:(id)data handler:(void (^)(BOOL result))handler;
- (void)socket:(id)socket sentData:(id)data;
@optional
- (void)sendQueueEmpty:(id)socket;
- (void)socket:(id)socket noRecvData:(id)data;
- (void)socket:(id)socket unsentData:(id)data;
- (void)socket:(id)socket connectStateChanged:(NSUInteger)state;
@end
@interface JCORENetworkThread : NSThread
+ (instancetype)thread;
+ (void)run;
@end
@protocol JCORESocketConfigProtocol <NSObject>
@property(nonatomic,copy) NSString *host;
@property(nonatomic) unsigned int port;
@optional
@property(nonatomic) NSUInteger ipType;
@end
@interface JCORESocketConfig : NSObject <JCORESocketConfigProtocol> {
    BOOL _hasMonitored;
    unsigned int _port;
    NSString *_host;
    NSUInteger _ipType;
    NSString *_moniterID;
}
@property(nonatomic,copy) NSString *host;
@property(nonatomic) unsigned int port;
@property(nonatomic) NSUInteger ipType;
@property(nonatomic,strong) NSString *moniterID;
@property(nonatomic) BOOL hasMonitored;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly,copy) NSString *description;
@property(readonly,copy) NSString *debugDescription;
@end
@interface JCORESocketData : NSObject {
    NSData *_data;
    JCORETransportResult _handler;
    double _timeout;
}
@property(nonatomic,strong) NSData *data;
@property(nonatomic,copy) JCORETransportResult handler;
@property(nonatomic) double timeout;
@end
@interface JCORENetworkController : NSObject {
    JCORENetworkThread *_workThread;
}
@property(nonatomic,readonly) JCORENetworkThread *workThread;
+ (instancetype)shared;
+ (void)startThread;
+ (void)performSel:(SEL)selector target:(id)target withObject:(id)object;
+ (id)getSocketWorkThread;
+ (void)destorySocketInThread:(id)socket;
- (void)performSel:(SEL)selector target:(id)target withObject:(id)object;
- (void)destorySocketInThread:(id)socket;
@end
@interface JCORENetwork : NSObject <JCORENetworkDelegate> {
    BOOL _careAllDataSent;
    __weak id<JCORENetworkDataDelegate> _delegate;
    JCORESocketConfig *_config;
    NSUInteger _type;
    __weak NSThread *_reqThread;
    JCORESocketController *_socketController;
}
@property(nonatomic,readonly) NSUInteger type;
@property(nonatomic,weak) NSThread *reqThread;
@property(nonatomic,strong) JCORESocketController *socketController;
@property(nonatomic,weak) id<JCORENetworkDataDelegate> delegate;
@property(nonatomic) BOOL careAllDataSent;
@property(nonatomic,strong) JCORESocketConfig *config;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly,copy) NSString *description;
@property(readonly,copy) NSString *debugDescription;
+ (void)startThread;
+ (instancetype)network:(NSUInteger)type;
+ (instancetype)tcp;
+ (instancetype)udp;
- (instancetype)initWithType:(NSUInteger)type;
- (void)setConfig:(JCORESocketConfig *)config delegate:(id<JCORENetworkDataDelegate>)delegate;
- (void)remove;
- (void)connect;
- (void)disconect;
- (void)asyncHandleResult:(id)result;
- (void)sendData:(JCORESocketData *)data;
- (BOOL)performInThread:(SEL)selector withObj:(id)object;
@end
@interface JCOREURLSessionController : NSObject <NSURLSessionDelegate,NSURLSessionTaskDelegate,NSURLSessionDataDelegate> {
    NSURLSession *_session;
    __weak NSThread *_requestThread;
}
@property(nonatomic,strong) NSURLSession *session;
@property(nonatomic,weak) NSThread *requestThread;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly,copy) NSString *description;
@property(readonly,copy) NSString *debugDescription;
- (void)invalidate;
- (id)sendRequest:(NSURLRequest *)request requestId:(id)identifier completionHandler:(JCORESessionCompletion)completion;
- (void)sessionCompletionWithInfo:(id)info;
@end
@interface NSObject (JCOREURLSessionController)
@property(nonatomic,strong) NSMutableData *jMutableData;
@property(nonatomic,copy) NSString *jIdentifier;
@property(nonatomic,copy) JCORESessionCompletion jCompletionHandler;
@end
