#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTHTTPRequestHandler.h>
#import <math.h>
#import <stdatomic.h>

static NSString *const OneKeyNetworkThrottleHandledKey = @"OneKeyNetworkThrottleHandled";
static NSString *const OneKeyNetworkThrottleProfileSlow4G = @"slow4g";
static const NSTimeInterval OneKeyNetworkThrottleDefaultLatencyMs = 562.5;

@interface OneKeyNetworkThrottleState : NSObject
+ (NSDictionary *)currentConfig;
+ (BOOL)isEnabled;
+ (NSTimeInterval)latencyMs;
+ (NSDictionary *)setEnabled:(BOOL)enabled latencyMs:(NSTimeInterval)latencyMs;
@end

@implementation OneKeyNetworkThrottleState

static atomic_bool _oneKeyNetworkThrottleEnabled = ATOMIC_VAR_INIT(false);
static atomic_llong _oneKeyNetworkThrottleLatencyMicros = ATOMIC_VAR_INIT(562500);

+ (NSDictionary *)currentConfig
{
  BOOL enabled = atomic_load_explicit(&_oneKeyNetworkThrottleEnabled, memory_order_acquire);
  NSTimeInterval latencyMs =
    ((NSTimeInterval)atomic_load_explicit(&_oneKeyNetworkThrottleLatencyMicros, memory_order_relaxed)) / 1000.0;
  return @{
    @"enabled": @(enabled),
    @"profile": OneKeyNetworkThrottleProfileSlow4G,
    @"latencyMs": @(latencyMs)
  };
}

+ (BOOL)isEnabled
{
  return atomic_load_explicit(&_oneKeyNetworkThrottleEnabled, memory_order_acquire);
}

+ (NSTimeInterval)latencyMs
{
  return ((NSTimeInterval)atomic_load_explicit(&_oneKeyNetworkThrottleLatencyMicros, memory_order_relaxed)) / 1000.0;
}

+ (NSDictionary *)setEnabled:(BOOL)enabled latencyMs:(NSTimeInterval)latencyMs
{
  NSTimeInterval normalizedLatencyMs = latencyMs > 0 ? latencyMs : OneKeyNetworkThrottleDefaultLatencyMs;
  atomic_store_explicit(
    &_oneKeyNetworkThrottleLatencyMicros,
    (long long)llround(normalizedLatencyMs * 1000.0),
    memory_order_relaxed
  );
  atomic_store_explicit(&_oneKeyNetworkThrottleEnabled, enabled, memory_order_release);
  NSLog(
    @"[onekey-network-throttle] native config enabled=%@ profile=%@ latencyMs=%.1f",
    enabled ? @"true" : @"false",
    OneKeyNetworkThrottleProfileSlow4G,
    normalizedLatencyMs
  );
  return [self currentConfig];
}

@end

@interface OneKeyNetworkThrottleURLProtocol : NSURLProtocol <NSURLSessionDataDelegate>
@property (nonatomic, strong) NSURLSessionDataTask *task;
@property (nonatomic, strong) NSURLSession *session;
@property (atomic, assign) BOOL stopped;
@property (nonatomic, assign) CFAbsoluteTime requestStartTime;
@property (nonatomic, assign) NSTimeInterval latencySeconds;
@property (atomic, assign) BOOL responseDelivered;
@property (nonatomic, strong) NSMutableArray<NSData *> *pendingData;
@property (nonatomic, copy) void (^pendingResponseCompletionHandler)(NSURLSessionResponseDisposition disposition);
- (NSTimeInterval)remainingLatencyDelay;
- (void)deliverResponse:(NSURLResponse *)response;
- (void)flushPendingData;
- (void)cancelPendingResponseCompletionHandler;
- (void)invalidateSessionAndClearTaskWithCancel:(BOOL)cancel;
@end

@implementation OneKeyNetworkThrottleURLProtocol

+ (BOOL)canInitWithRequest:(NSURLRequest *)request
{
  if (![OneKeyNetworkThrottleState isEnabled]) {
    return NO;
  }
  if ([NSURLProtocol propertyForKey:OneKeyNetworkThrottleHandledKey inRequest:request]) {
    return NO;
  }
  NSString *scheme = request.URL.scheme.lowercaseString;
  return [scheme isEqualToString:@"http"] || [scheme isEqualToString:@"https"];
}

+ (NSURLRequest *)canonicalRequestForRequest:(NSURLRequest *)request
{
  return request;
}

- (void)startLoading
{
  NSMutableURLRequest *request = [self.request mutableCopy];
  [NSURLProtocol setProperty:@YES forKey:OneKeyNetworkThrottleHandledKey inRequest:request];

  self.requestStartTime = CFAbsoluteTimeGetCurrent();
  self.latencySeconds = [OneKeyNetworkThrottleState latencyMs] / 1000.0;
  self.pendingData = [NSMutableArray array];

  NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];
  NSNumber *useWifiOnly = [[NSBundle mainBundle].infoDictionary objectForKey:@"ReactNetworkForceWifiOnly"];
  if (useWifiOnly) {
    configuration.allowsCellularAccess = ![useWifiOnly boolValue];
  }
  configuration.HTTPShouldSetCookies = YES;
  configuration.HTTPCookieAcceptPolicy = NSHTTPCookieAcceptPolicyAlways;
  configuration.HTTPCookieStorage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
  self.session = [NSURLSession sessionWithConfiguration:configuration delegate:self delegateQueue:nil];
  self.task = [self.session dataTaskWithRequest:request];
  [self.task resume];
}

- (void)stopLoading
{
  self.stopped = YES;
  [self cancelPendingResponseCompletionHandler];
  [self invalidateSessionAndClearTaskWithCancel:YES];
}

- (NSTimeInterval)remainingLatencyDelay
{
  NSTimeInterval elapsed = CFAbsoluteTimeGetCurrent() - self.requestStartTime;
  NSTimeInterval remainingDelay = self.latencySeconds - elapsed;
  return remainingDelay > 0 ? remainingDelay : 0;
}

- (void)deliverResponse:(NSURLResponse *)response
{
  void (^completionHandler)(NSURLSessionResponseDisposition disposition) = nil;
  @synchronized (self) {
    completionHandler = self.pendingResponseCompletionHandler;
    self.pendingResponseCompletionHandler = nil;
  }
  if (!completionHandler) {
    return;
  }
  if (self.stopped) {
    completionHandler(NSURLSessionResponseCancel);
    return;
  }
  self.responseDelivered = YES;
  [self.client URLProtocol:self didReceiveResponse:response cacheStoragePolicy:NSURLCacheStorageNotAllowed];
  [self flushPendingData];
  completionHandler(NSURLSessionResponseAllow);
}

- (void)flushPendingData
{
  NSArray<NSData *> *pendingData = nil;
  @synchronized (self) {
    pendingData = [self.pendingData copy];
    [self.pendingData removeAllObjects];
  }
  for (NSData *data in pendingData) {
    if (self.stopped) {
      return;
    }
    [self.client URLProtocol:self didLoadData:data];
  }
}

- (void)cancelPendingResponseCompletionHandler
{
  void (^completionHandler)(NSURLSessionResponseDisposition disposition) = nil;
  @synchronized (self) {
    completionHandler = self.pendingResponseCompletionHandler;
    self.pendingResponseCompletionHandler = nil;
  }
  if (completionHandler) {
    completionHandler(NSURLSessionResponseCancel);
  }
}

- (void)invalidateSessionAndClearTaskWithCancel:(BOOL)cancel
{
  NSURLSessionDataTask *task = nil;
  NSURLSession *session = nil;
  @synchronized (self) {
    task = self.task;
    session = self.session;
    self.task = nil;
    self.session = nil;
  }
  if (cancel) {
    [task cancel];
    [session invalidateAndCancel];
  } else {
    [session finishTasksAndInvalidate];
  }
}

- (void)URLSession:(NSURLSession *)session
                          task:(NSURLSessionTask *)task
    willPerformHTTPRedirection:(NSHTTPURLResponse *)response
                    newRequest:(NSURLRequest *)request
             completionHandler:(void (^)(NSURLRequest * _Nullable))completionHandler
{
  [self.client URLProtocol:self wasRedirectedToRequest:request redirectResponse:response];
  completionHandler(nil);
}

- (void)URLSession:(NSURLSession *)session
          dataTask:(NSURLSessionDataTask *)dataTask
didReceiveResponse:(NSURLResponse *)response
 completionHandler:(void (^)(NSURLSessionResponseDisposition))completionHandler
{
  @synchronized (self) {
    self.pendingResponseCompletionHandler = completionHandler;
  }
  NSTimeInterval delay = [self remainingLatencyDelay];
  if (delay <= 0) {
    [self deliverResponse:response];
    return;
  }
  dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delay * NSEC_PER_SEC)), dispatch_get_global_queue(QOS_CLASS_UTILITY, 0), ^{
    [self deliverResponse:response];
  });
}

- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveData:(NSData *)data
{
  if (self.stopped) {
    return;
  }
  if (self.responseDelivered) {
    [self.client URLProtocol:self didLoadData:data];
  } else {
    @synchronized (self) {
      [self.pendingData addObject:data];
    }
  }
}

- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error
{
  if (!self.responseDelivered) {
    [self cancelPendingResponseCompletionHandler];
  }
  if (!self.stopped) {
    if (error) {
      [self.client URLProtocol:self didFailWithError:error];
    } else {
      [self.client URLProtocolDidFinishLoading:self];
    }
  }
  [self invalidateSessionAndClearTaskWithCancel:NO];
}

@end

@interface OneKeyNetworkThrottleInstaller : NSObject
@end

@implementation OneKeyNetworkThrottleInstaller

+ (void)load
{
  RCTSetCustomNSURLSessionConfigurationProvider(^NSURLSessionConfiguration *{
    NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];
    NSNumber *useWifiOnly = [[NSBundle mainBundle].infoDictionary objectForKey:@"ReactNetworkForceWifiOnly"];
    if (useWifiOnly) {
      configuration.allowsCellularAccess = ![useWifiOnly boolValue];
    }
    configuration.HTTPShouldSetCookies = YES;
    configuration.HTTPCookieAcceptPolicy = NSHTTPCookieAcceptPolicyAlways;
    configuration.HTTPCookieStorage = [NSHTTPCookieStorage sharedHTTPCookieStorage];

    NSArray<Class> *existingProtocolClasses = configuration.protocolClasses ?: @[];
    if (![existingProtocolClasses containsObject:[OneKeyNetworkThrottleURLProtocol class]]) {
      configuration.protocolClasses = [[@[ [OneKeyNetworkThrottleURLProtocol class] ] arrayByAddingObjectsFromArray:existingProtocolClasses] copy];
    }
    return configuration;
  });
}

@end

@interface OneKeyNetworkThrottle : NSObject <RCTBridgeModule>
@end

@implementation OneKeyNetworkThrottle

RCT_EXPORT_MODULE(OneKeyNetworkThrottle)

+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

RCT_REMAP_METHOD(getConfig, getConfigWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  resolve([OneKeyNetworkThrottleState currentConfig]);
}

RCT_REMAP_METHOD(setConfig, setConfig:(NSDictionary *)config resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  id enabledValue = config[@"enabled"];
  BOOL enabled =
    enabledValue != nil && enabledValue != [NSNull null] ? [enabledValue boolValue] : [OneKeyNetworkThrottleState isEnabled];
  id latencyValue = config[@"latencyMs"];
  NSTimeInterval latencyMs =
    latencyValue != nil && latencyValue != [NSNull null] ? [latencyValue doubleValue] : [OneKeyNetworkThrottleState latencyMs];
  resolve([OneKeyNetworkThrottleState setEnabled:enabled latencyMs:latencyMs]);
}

@end
