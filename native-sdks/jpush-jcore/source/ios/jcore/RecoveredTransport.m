#import "RecoveredTransport.h"
#import "RecoveredTransportDependencies.h"
#import "RecoveredTransportLiterals.h"
#import <objc/runtime.h>

static void RecoveredNetworkRunLoopTimer(CFRunLoopTimerRef timer,void *info) {}
@implementation JCORENetworkThread
+ (instancetype)thread { return [[JCORENetworkThread alloc] initWithTarget:self selector:@selector(run) object:nil]; }
+ (void)run {
    @autoreleasepool {
        [NSThread currentThread].name=@"io.jiguang.hxhg.network";
        CFRunLoopTimerContext context={0,(__bridge void *)self,NULL,NULL,NULL};
        CFRunLoopTimerRef timer=CFRunLoopTimerCreate(NULL,1000.0,0,0,0,RecoveredNetworkRunLoopTimer,&context);
        CFRunLoopAddTimer(CFRunLoopGetCurrent(),timer,kCFRunLoopDefaultMode);CFRunLoopRun();CFRelease(timer);
    }
}
@end

@implementation JCORESocketConfig
@synthesize host=_host,port=_port,ipType=_ipType,moniterID=_moniterID,hasMonitored=_hasMonitored;
@dynamic hash,superclass,description,debugDescription;
@end
@implementation JCORESocketData
@synthesize data=_data,handler=_handler,timeout=_timeout;
@end

@implementation JCORENetworkController
@synthesize workThread=_workThread;
+ (instancetype)shared {
    static JCORENetworkController *shared;static dispatch_once_t once;
    dispatch_once(&once,^{ shared=[[JCORENetworkController alloc] init]; });return shared;
}
- (instancetype)init { self=[super init];if(self)_workThread=[JCORENetworkThread thread];return self; }
- (void)performSel:(SEL)selector target:(id)target withObject:(id)object {
    if(target)[target performSelector:selector onThread:self.workThread withObject:object waitUntilDone:NO];
}
+ (void)startThread { if(![[self shared].workThread isExecuting])[[self shared].workThread start]; }
+ (void)performSel:(SEL)selector target:(id)target withObject:(id)object { [[self shared] performSel:selector target:target withObject:object]; }
+ (id)getSocketWorkThread { return [self shared].workThread; }
+ (void)destorySocketInThread:(id)socket { [[self shared] performSelector:@selector(destorySocketInThread:) onThread:[self getSocketWorkThread] withObject:socket waitUntilDone:NO]; }
- (void)destorySocketInThread:(id)socket { [socket disconect];[socket remove]; }
@end

@implementation JCORENetwork
@synthesize type=_type,reqThread=_reqThread,socketController=_socketController,delegate=_delegate,careAllDataSent=_careAllDataSent,config=_config;
@dynamic hash,superclass,description,debugDescription;
+ (void)startThread { [JCORENetworkController startThread]; }
+ (instancetype)network:(NSUInteger)type { return [[JCORENetwork alloc] initWithType:type]; }
+ (instancetype)tcp { static JCORENetwork *network;static dispatch_once_t once;dispatch_once(&once,^{network=[JCORENetwork network:0];});return network; }
+ (instancetype)udp { static JCORENetwork *network;static dispatch_once_t once;dispatch_once(&once,^{network=[JCORENetwork network:1];});return network; }
- (instancetype)initWithType:(NSUInteger)type {
    self=[super init];if(self){_type=type;_socketController=[JCORESocketController socket:type];_reqThread=[JCOREThread mainThread];}return self;
}
- (void)setDelegate:(id<JCORENetworkDataDelegate>)delegate {
    if(!delegate)REC_NETWORK_LOG(YES,0x40,"-[JCORENetwork setDelegate:]",0x4f,REC_CF_e5558);
    _delegate=delegate;[JCORENetworkController performSel:@selector(setDelegate:) target:self.socketController withObject:self];
}
- (void)setConfig:(JCORESocketConfig *)config {
    if(!config||!config.host||!config.port){REC_NETWORK_LOG(NO,1,"-[JCORENetwork setConfig:]",0x58,REC_CF_e5578);return;}
    _config=config;[JCORENetworkController performSel:@selector(setReqThread:) target:self.socketController withObject:_reqThread];
    [JCORENetworkController performSel:@selector(setConfig:) target:self.socketController withObject:config];
}
- (void)setConfig:(JCORESocketConfig *)config delegate:(id<JCORENetworkDataDelegate>)delegate {
    // The original stores the config before its validating setter is called.
    _config=config;[self setConfig:config];[self setDelegate:delegate];
}
- (void)remove { [JCORENetworkController performSel:@selector(remove) target:self.socketController withObject:nil]; }
- (void)connect {
    if(![JCOREService getStatusSwitch]){REC_NETWORK_LOG(YES,0x100,"-[JCORENetwork connect]",0x6f,REC_CF_STATUS,"-[JCORENetwork connect]");return;}
    if([JCOREService appkeyIsNotApplyToThisSDK]){REC_NETWORK_LOG(YES,0x100,"-[JCORENetwork connect]",0x74,REC_CF_APPKEY,"-[JCORENetwork connect]");return;}
    if(self.type==0){NSString *monitor=[JCORESupersetManager startConnectionMoniter:REC_CF_db4f8 host:self.config.host port:self.config.port];[self.config setMoniterID:monitor];[self.config setHasMonitored:NO];}
    [JCORENetworkController performSel:@selector(connect) target:self.socketController withObject:nil];
}
- (void)disconect { [JCORENetworkController performSel:@selector(disconect) target:self.socketController withObject:nil]; }
- (void)asyncHandleResult:(id)result {
    if(![result isKindOfClass:NSDictionary.class])return;
    id value=result[@"result"],handler=result[@"handler"];
    if([value isKindOfClass:NSNumber.class]){BOOL flag=[value boolValue];if(handler)((JCORETransportResult)handler)(flag);}
}
- (void)sendData:(JCORESocketData *)data {
    if(![JCOREService getStatusSwitch]){REC_NETWORK_LOG(YES,0x100,"-[JCORENetwork sendData:]",0x98,REC_CF_STATUS,"-[JCORENetwork sendData:]");return;}
    if([JCOREService appkeyIsNotApplyToThisSDK]){REC_NETWORK_LOG(YES,0x100,"-[JCORENetwork sendData:]",0x9d,REC_CF_APPKEY,"-[JCORENetwork sendData:]");return;}
    JCORESocketController *controller=self.socketController;__weak JCORENetwork *weakSelf=self;
    JCORESocketData *packet=[[JCORESocketData alloc] init];packet.data=data.data;packet.timeout=data.timeout;
    packet.handler=^(BOOL result){
        if([JCOREThread isMainThread]){if(data.handler)data.handler(result);}
        else if(data.handler){JCORENetwork *network=weakSelf;[network performSelector:@selector(asyncHandleResult:) onThread:network.reqThread withObject:@{@"handler":data.handler,@"result":@(result)} waitUntilDone:NO];}
    };
    [JCORENetworkController performSel:@selector(sendData:) target:controller withObject:packet];
}
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
// Original setter only forwards a boxed value; it never writes the getter's ivar.
- (void)setCareAllDataSent:(BOOL)value { [JCORENetworkController performSel:@selector(setCareAllDataSent:) target:self.socketController withObject:@(value)]; }
#pragma clang diagnostic pop
- (BOOL)performInThread:(SEL)selector withObj:(id)object {
    if(self.reqThread&&![[NSThread currentThread] isEqual:self.reqThread]){[self performSelector:selector onThread:self.reqThread withObject:object waitUntilDone:NO];return YES;}return NO;
}
- (void)dealloc { if(self.socketController)[JCORENetworkController destorySocketInThread:self.socketController]; }
- (void)recvData:(id)data { if(![self performInThread:_cmd withObj:data]&&self.delegate)[(id)self.delegate socket:self recvData:data handler:^(BOOL result){}]; }
- (void)sentData:(id)data { if(![self performInThread:_cmd withObj:data]&&self.delegate)[(id)self.delegate socket:self sentData:[data data]]; }
- (void)sendQueueEmpty { if(![self performInThread:_cmd withObj:nil]&&self.delegate&&[(id)self.delegate respondsToSelector:@selector(sendQueueEmpty:)])[(id)self.delegate sendQueueEmpty:self]; }
- (void)noRecvData:(id)data { if(![self performInThread:_cmd withObj:data]&&self.delegate&&[(id)self.delegate respondsToSelector:@selector(socket:noRecvData:)])[(id)self.delegate socket:self noRecvData:data]; }
- (void)unsentData:(id)data { if(![self performInThread:_cmd withObj:data]&&self.delegate&&[(id)self.delegate respondsToSelector:@selector(socket:unsentData:)])[(id)self.delegate socket:self unsentData:[data data]]; }
- (void)connectStateChanged:(id)state { if(![self performInThread:_cmd withObj:state]&&self.delegate&&[(id)self.delegate respondsToSelector:@selector(socket:connectStateChanged:)])[(id)self.delegate socket:self connectStateChanged:[state integerValue]]; }
@end

@implementation NSObject (JCOREURLSessionController)
- (NSMutableData *)jMutableData { return objc_getAssociatedObject(self,"jcore_session_task_mutabledata"); }
- (void)setJMutableData:(NSMutableData *)value { objc_setAssociatedObject(self,"jcore_session_task_mutabledata",value,OBJC_ASSOCIATION_RETAIN_NONATOMIC); }
- (NSString *)jIdentifier { return objc_getAssociatedObject(self,"jcore_session_task_identifier"); }
- (void)setJIdentifier:(NSString *)value { objc_setAssociatedObject(self,"jcore_session_task_identifier",value,OBJC_ASSOCIATION_COPY_NONATOMIC); }
- (JCORESessionCompletion)jCompletionHandler { return objc_getAssociatedObject(self,"jcore_session_task_completionhandler"); }
- (void)setJCompletionHandler:(JCORESessionCompletion)value { objc_setAssociatedObject(self,"jcore_session_task_completionhandler",value,OBJC_ASSOCIATION_COPY_NONATOMIC); }
@end

@implementation JCOREURLSessionController
@synthesize session=_session,requestThread=_requestThread;
@dynamic hash,superclass,description,debugDescription;
- (instancetype)init {
    self=[super init];if(self){NSURLSessionConfiguration *configuration=[NSURLSessionConfiguration defaultSessionConfiguration];configuration.allowsCellularAccess=YES;_session=[NSURLSession sessionWithConfiguration:configuration delegate:self delegateQueue:nil];}return self;
}
- (void)invalidate { if(_session){[_session finishTasksAndInvalidate];_session=nil;} }
- (id)sendRequest:(NSURLRequest *)request requestId:(id)identifier completionHandler:(JCORESessionCompletion)completion {
    if(!request){REC_SESSION_LOG(NO,1,"-[JCOREURLSessionController sendRequest:requestId:completionHandler:]",0x4d,REC_CF_e6b58);return nil;}
    NSString *url=request.URL.absoluteString;
    NSArray *statusURLs=@[@"https://config.jpush.cn/v1/status",@"https://config-ipv6.jpush.cn/v1/status"];
    NSString *versionURL=request.URL.absoluteString;
    NSArray *versionURLs=@[@"https://config.jpush.cn/v1/version-check",@"https://config-ipv6.jpush.cn/v1/version-check"];
    if(![JCOREService getStatusSwitch]&&![statusURLs containsObject:url]){REC_SESSION_LOG(YES,0x100,"-[JCOREURLSessionController sendRequest:requestId:completionHandler:]",0x70,REC_CF_STATUS,"-[JCOREURLSessionController sendRequest:requestId:completionHandler:]");return nil;}
    if([JCOREService appkeyIsNotApplyToThisSDK]&&![versionURLs containsObject:versionURL]){REC_SESSION_LOG(YES,0x100,"-[JCOREURLSessionController sendRequest:requestId:completionHandler:]",0x75,REC_CF_APPKEY,"-[JCOREURLSessionController sendRequest:requestId:completionHandler:]");return nil;}
    self.requestThread=[NSThread currentThread];
    REC_SESSION_LOG(YES,0x100,"-[JCOREURLSessionController sendRequest:requestId:completionHandler:]",0x7a,REC_CF_e6b78,request.URL.absoluteString);
    NSMutableURLRequest *copy=[request mutableCopy];[copy setValue:request.URL.host forHTTPHeaderField:@"Host"];
    NSURLSessionDataTask *task=[self.session dataTaskWithRequest:copy];
    if([task respondsToSelector:@selector(setJMutableData:)])task.jMutableData=[NSMutableData data];
    if([task respondsToSelector:@selector(setJIdentifier:)])task.jIdentifier=identifier;
    if([task respondsToSelector:@selector(setJCompletionHandler:)])task.jCompletionHandler=completion;
    [task resume];return task;
}
- (void)sessionCompletionWithInfo:(id)info {
    if(![info isKindOfClass:NSDictionary.class]){REC_SESSION_LOG(YES,8,"-[JCOREURLSessionController sessionCompletionWithInfo:]",0x8f,REC_CF_e6bb8);return;}
    id task=info[@"task"];NSError *error=info[@"error"];NSData *data=nil;
    if([task respondsToSelector:@selector(jMutableData)]&&[task jMutableData]){data=[[task jMutableData] copy];[task setJMutableData:nil];}
    if([task respondsToSelector:@selector(jCompletionHandler)]&&[task respondsToSelector:@selector(jIdentifier)]&&[task jCompletionHandler]&&![(id)[task jCompletionHandler] isKindOfClass:NSNull.class]){
        JCORESessionCompletion handler=[task jCompletionHandler];id identifier=[task jIdentifier];handler(task,identifier,error?nil:data,error);
    }
}
- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error {
    REC_SESSION_LOG(YES,0x100,"-[JCOREURLSessionController URLSession:task:didCompleteWithError:]",0xa8,REC_CF_e6bd8,task.currentRequest.URL.absoluteString);
    NSMutableDictionary *info=[[NSMutableDictionary alloc] init];if(task)info[@"task"]=task;if(error)info[@"error"]=error;
    if([self.requestThread isKindOfClass:NSThread.class])[self performSelector:@selector(sessionCompletionWithInfo:) onThread:self.requestThread withObject:[info copy] waitUntilDone:NO];else [self sessionCompletionWithInfo:[info copy]];
}
- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)task didReceiveData:(NSData *)data { if(data&&[task respondsToSelector:@selector(jMutableData)])[task.jMutableData appendData:data]; }
- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)task didReceiveResponse:(NSURLResponse *)response completionHandler:(void (^)(NSURLSessionResponseDisposition))completionHandler { completionHandler(NSURLSessionResponseAllow); }
@end
