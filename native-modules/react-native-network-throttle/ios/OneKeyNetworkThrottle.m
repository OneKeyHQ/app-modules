#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTHTTPRequestHandler.h>
#import <math.h>
#import <stdatomic.h>

static NSString *const OneKeyNetworkThrottleHandledKey = @"OneKeyNetworkThrottleHandled";
static NSString *const OneKeyNetworkThrottleProfileSlow4G = @"slow4g";
static const NSTimeInterval OneKeyNetworkThrottleDefaultLatencyMs = 562.5;
static const NSInteger OneKeyNetworkThrottleDefaultThroughputBps = 102 * 1024;
static const NSUInteger OneKeyNetworkThrottleMaxPendingDownloadBytes = 256 * 1024;

static NSString *OneKeyNetworkThrottleNormalizedHost(NSString *value)
{
  NSString *host = [value stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]].lowercaseString;
  return host.length > 0 ? host : nil;
}

// Hosts match as exact names, or as `*.example.com` which matches sub-domains
// at any depth but not the bare apex. This mirrors the URL patterns the
// desktop app installs, so both platforms throttle the same traffic.
static BOOL OneKeyNetworkThrottleHostMatches(NSString *host, NSString *pattern)
{
  if ([pattern hasPrefix:@"*."]) {
    return [host hasSuffix:[pattern substringFromIndex:1]];
  }
  return [host isEqualToString:pattern];
}

@interface OneKeyNetworkThrottleState : NSObject
+ (NSDictionary *)currentConfig;
+ (BOOL)isEnabled;
+ (NSTimeInterval)latencyMs;
+ (NSInteger)downloadBps;
+ (NSInteger)uploadBps;
+ (BOOL)shouldThrottleURL:(NSURL *)url;
+ (NSDictionary *)setEnabled:(BOOL)enabled
                    latencyMs:(NSTimeInterval)latencyMs
                   downloadBps:(NSInteger)downloadBps
                      uploadBps:(NSInteger)uploadBps
                throttleUrlHosts:(NSArray *)throttleUrlHosts;
@end

@implementation OneKeyNetworkThrottleState

static atomic_bool _oneKeyNetworkThrottleEnabled = ATOMIC_VAR_INIT(false);
static atomic_llong _oneKeyNetworkThrottleLatencyMicros = ATOMIC_VAR_INIT(562500);
static atomic_llong _oneKeyNetworkThrottleDownloadBps = ATOMIC_VAR_INIT(102 * 1024);
static atomic_llong _oneKeyNetworkThrottleUploadBps = ATOMIC_VAR_INIT(102 * 1024);
static NSSet<NSString *> *_oneKeyNetworkThrottleHosts;

+ (NSDictionary *)currentConfig
{
  BOOL enabled = atomic_load_explicit(&_oneKeyNetworkThrottleEnabled, memory_order_acquire);
  NSTimeInterval latencyMs =
    ((NSTimeInterval)atomic_load_explicit(&_oneKeyNetworkThrottleLatencyMicros, memory_order_relaxed)) / 1000.0;
  NSArray<NSString *> *throttleUrlHosts = nil;
  @synchronized (self) {
    throttleUrlHosts = [[_oneKeyNetworkThrottleHosts ?: [NSSet set] allObjects]
      sortedArrayUsingSelector:@selector(compare:)];
  }
  return @{
    @"enabled": @(enabled),
    @"profile": OneKeyNetworkThrottleProfileSlow4G,
    @"latencyMs": @(latencyMs),
    @"downloadBps": @([self downloadBps]),
    @"uploadBps": @([self uploadBps]),
    @"throttleUrlHosts": throttleUrlHosts
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

+ (NSInteger)downloadBps
{
  return (NSInteger)atomic_load_explicit(&_oneKeyNetworkThrottleDownloadBps, memory_order_relaxed);
}

+ (NSInteger)uploadBps
{
  return (NSInteger)atomic_load_explicit(&_oneKeyNetworkThrottleUploadBps, memory_order_relaxed);
}

+ (BOOL)shouldThrottleURL:(NSURL *)url
{
  NSString *host = OneKeyNetworkThrottleNormalizedHost(url.host);
  if (host == nil) {
    return NO;
  }
  // An empty allowlist throttles nothing.
  NSSet<NSString *> *hosts = nil;
  @synchronized (self) {
    hosts = _oneKeyNetworkThrottleHosts;
  }
  for (NSString *pattern in hosts) {
    if (OneKeyNetworkThrottleHostMatches(host, pattern)) {
      return YES;
    }
  }
  return NO;
}

+ (NSInteger)normalizeThroughputBps:(NSInteger)throughputBps
{
  return throughputBps > 0 ? throughputBps : OneKeyNetworkThrottleDefaultThroughputBps;
}

+ (NSDictionary *)setEnabled:(BOOL)enabled
                    latencyMs:(NSTimeInterval)latencyMs
                   downloadBps:(NSInteger)downloadBps
                      uploadBps:(NSInteger)uploadBps
                throttleUrlHosts:(NSArray *)throttleUrlHosts
{
  NSTimeInterval normalizedLatencyMs = latencyMs > 0 ? latencyMs : OneKeyNetworkThrottleDefaultLatencyMs;
  NSInteger normalizedDownloadBps = [self normalizeThroughputBps:downloadBps];
  NSInteger normalizedUploadBps = [self normalizeThroughputBps:uploadBps];
  if ([throttleUrlHosts isKindOfClass:[NSArray class]]) {
    NSMutableSet<NSString *> *normalizedHosts = [NSMutableSet set];
    for (id value in throttleUrlHosts) {
      if (![value isKindOfClass:[NSString class]]) {
        continue;
      }
      NSString *host = OneKeyNetworkThrottleNormalizedHost((NSString *)value);
      if (host != nil) {
        [normalizedHosts addObject:host];
      }
    }
    @synchronized (self) {
      NSMutableSet<NSString *> *nextHosts =
        [_oneKeyNetworkThrottleHosts mutableCopy] ?: [NSMutableSet set];
      [nextHosts unionSet:normalizedHosts];
      _oneKeyNetworkThrottleHosts = [nextHosts copy];
    }
  }
  atomic_store_explicit(
    &_oneKeyNetworkThrottleLatencyMicros,
    (long long)llround(normalizedLatencyMs * 1000.0),
    memory_order_relaxed
  );
  atomic_store_explicit(&_oneKeyNetworkThrottleDownloadBps, (long long)normalizedDownloadBps, memory_order_relaxed);
  atomic_store_explicit(&_oneKeyNetworkThrottleUploadBps, (long long)normalizedUploadBps, memory_order_relaxed);
  atomic_store_explicit(&_oneKeyNetworkThrottleEnabled, enabled, memory_order_release);
  NSLog(
    @"[onekey-network-throttle] native config enabled=%@ profile=%@ latencyMs=%.1f downloadBps=%ld uploadBps=%ld",
    enabled ? @"true" : @"false",
    OneKeyNetworkThrottleProfileSlow4G,
    normalizedLatencyMs,
    (long)normalizedDownloadBps,
    (long)normalizedUploadBps
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
@property (nonatomic, assign) NSInteger downloadBps;
@property (nonatomic, assign) NSInteger uploadBps;
@property (nonatomic, assign) CFAbsoluteTime downloadStartTime;
@property (nonatomic, assign) long long downloadedBytes;
@property (atomic, assign) BOOL responseDelivered;
@property (atomic, assign) BOOL flushingData;
@property (atomic, assign) BOOL upstreamCompleted;
@property (nonatomic, strong) NSError *upstreamError;
@property (nonatomic, strong) NSMutableArray<NSData *> *pendingData;
@property (nonatomic, assign) NSUInteger pendingDataBytes;
@property (nonatomic, copy) void (^pendingResponseCompletionHandler)(NSURLSessionResponseDisposition disposition);
@property (atomic, assign) BOOL upstreamSuspendedForBackpressure;
- (NSTimeInterval)remainingLatencyDelay;
- (NSTimeInterval)uploadDelayForRequest:(NSURLRequest *)request;
- (NSTimeInterval)downloadDelayForDataLength:(NSUInteger)dataLength;
- (void)deliverResponse:(NSURLResponse *)response;
- (void)flushPendingData;
- (void)finishIfPossible;
- (void)cancelPendingResponseCompletionHandler;
- (void)applyUpstreamBackpressureIfNeeded;
- (void)clearPendingData;
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
  if (![OneKeyNetworkThrottleState shouldThrottleURL:request.URL]) {
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
  self.downloadBps = [OneKeyNetworkThrottleState downloadBps];
  self.uploadBps = [OneKeyNetworkThrottleState uploadBps];
  self.downloadStartTime = 0;
  self.downloadedBytes = 0;
  self.upstreamCompleted = NO;
  self.upstreamError = nil;
  self.pendingData = [NSMutableArray array];
  self.pendingDataBytes = 0;
  self.upstreamSuspendedForBackpressure = NO;

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
  NSTimeInterval uploadDelay = [self uploadDelayForRequest:request];
  if (uploadDelay <= 0) {
    [self.task resume];
    return;
  }
  dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(uploadDelay * NSEC_PER_SEC)), dispatch_get_global_queue(QOS_CLASS_UTILITY, 0), ^{
    if (!self.stopped) {
      [self.task resume];
    }
  });
}

- (void)stopLoading
{
  self.stopped = YES;
  [self cancelPendingResponseCompletionHandler];
  [self clearPendingData];
  [self invalidateSessionAndClearTaskWithCancel:YES];
}

- (NSTimeInterval)remainingLatencyDelay
{
  NSTimeInterval elapsed = CFAbsoluteTimeGetCurrent() - self.requestStartTime;
  NSTimeInterval remainingDelay = self.latencySeconds - elapsed;
  return remainingDelay > 0 ? remainingDelay : 0;
}

- (NSTimeInterval)uploadDelayForRequest:(NSURLRequest *)request
{
  if (self.uploadBps <= 0) {
    return 0;
  }
  long long bodyLength = (long long)request.HTTPBody.length;
  if (bodyLength <= 0) {
    NSString *contentLength = [request valueForHTTPHeaderField:@"Content-Length"];
    bodyLength = contentLength != nil ? contentLength.longLongValue : 0;
  }
  return bodyLength > 0 ? ((NSTimeInterval)bodyLength) / ((NSTimeInterval)self.uploadBps) : 0;
}

- (NSTimeInterval)downloadDelayForDataLength:(NSUInteger)dataLength
{
  if (self.downloadBps <= 0 || dataLength == 0) {
    return 0;
  }
  if (self.downloadStartTime <= 0) {
    self.downloadStartTime = CFAbsoluteTimeGetCurrent();
  }
  long long bytesAfterData = self.downloadedBytes + (long long)dataLength;
  NSTimeInterval expectedElapsed = ((NSTimeInterval)bytesAfterData) / ((NSTimeInterval)self.downloadBps);
  NSTimeInterval actualElapsed = CFAbsoluteTimeGetCurrent() - self.downloadStartTime;
  NSTimeInterval delay = expectedElapsed - actualElapsed;
  return delay > 0 ? delay : 0;
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
  self.downloadStartTime = CFAbsoluteTimeGetCurrent();
  [self.client URLProtocol:self didReceiveResponse:response cacheStoragePolicy:NSURLCacheStorageNotAllowed];
  completionHandler(NSURLSessionResponseAllow);
  [self flushPendingData];
  [self finishIfPossible];
}

- (void)flushPendingData
{
  if (self.stopped || !self.responseDelivered) {
    return;
  }
  NSData *data = nil;
  @synchronized (self) {
    if (self.flushingData) {
      return;
    }
    data = self.pendingData.firstObject;
    if (data) {
      [self.pendingData removeObjectAtIndex:0];
      self.flushingData = YES;
    }
  }
  if (!data) {
    [self finishIfPossible];
    return;
  }
  NSTimeInterval delay = [self downloadDelayForDataLength:data.length];
  dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delay * NSEC_PER_SEC)), dispatch_get_global_queue(QOS_CLASS_UTILITY, 0), ^{
    if (self.stopped) {
      @synchronized (self) {
        self.flushingData = NO;
      }
      return;
    }
    [self.client URLProtocol:self didLoadData:data];
    @synchronized (self) {
      self.downloadedBytes += (long long)data.length;
      if (self.pendingDataBytes >= data.length) {
        self.pendingDataBytes -= data.length;
      } else {
        self.pendingDataBytes = 0;
      }
      self.flushingData = NO;
    }
    [self applyUpstreamBackpressureIfNeeded];
    [self flushPendingData];
  });
}

- (void)finishIfPossible
{
  NSError *error = nil;
  BOOL shouldFinish = NO;
  @synchronized (self) {
    if (
      self.stopped ||
      !self.upstreamCompleted ||
      !self.responseDelivered ||
      self.flushingData ||
      self.pendingData.count > 0
    ) {
      return;
    }
    error = self.upstreamError;
    self.upstreamCompleted = NO;
    shouldFinish = YES;
  }
  if (!shouldFinish || self.stopped) {
    return;
  }
  if (error) {
    [self.client URLProtocol:self didFailWithError:error];
  } else {
    [self.client URLProtocolDidFinishLoading:self];
  }
  self.stopped = YES;
  [self clearPendingData];
  [self invalidateSessionAndClearTaskWithCancel:NO];
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

- (void)applyUpstreamBackpressureIfNeeded
{
  NSURLSessionDataTask *taskToSuspend = nil;
  NSURLSessionDataTask *taskToResume = nil;
  @synchronized (self) {
    if (self.stopped || !self.task || self.downloadBps <= 0) {
      return;
    }
    BOOL shouldSuspend = self.pendingDataBytes >= OneKeyNetworkThrottleMaxPendingDownloadBytes;
    if (shouldSuspend && !self.upstreamSuspendedForBackpressure) {
      self.upstreamSuspendedForBackpressure = YES;
      taskToSuspend = self.task;
    } else if (!shouldSuspend && self.upstreamSuspendedForBackpressure) {
      self.upstreamSuspendedForBackpressure = NO;
      taskToResume = self.task;
    }
  }
  if (taskToSuspend) {
    [taskToSuspend suspend];
  }
  if (taskToResume) {
    [taskToResume resume];
  }
}

- (void)clearPendingData
{
  @synchronized (self) {
    [self.pendingData removeAllObjects];
    self.pendingDataBytes = 0;
    self.upstreamSuspendedForBackpressure = NO;
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
  @synchronized (self) {
    [self.pendingData addObject:data];
    self.pendingDataBytes += data.length;
  }
  [self applyUpstreamBackpressureIfNeeded];
  if (self.responseDelivered) {
    [self flushPendingData];
  }
}

- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error
{
  if (error) {
    if (!self.responseDelivered) {
      [self cancelPendingResponseCompletionHandler];
    }
    if (!self.stopped) {
      [self.client URLProtocol:self didFailWithError:error];
      self.stopped = YES;
    }
    [self clearPendingData];
    [self invalidateSessionAndClearTaskWithCancel:NO];
    return;
  }

  @synchronized (self) {
    self.upstreamCompleted = YES;
    self.upstreamError = error;
  }
  [self finishIfPossible];
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
  id downloadBpsValue = config[@"downloadBps"];
  NSInteger downloadBps =
    downloadBpsValue != nil && downloadBpsValue != [NSNull null]
      ? [downloadBpsValue integerValue]
      : [OneKeyNetworkThrottleState downloadBps];
  id uploadBpsValue = config[@"uploadBps"];
  NSInteger uploadBps =
    uploadBpsValue != nil && uploadBpsValue != [NSNull null]
      ? [uploadBpsValue integerValue]
      : [OneKeyNetworkThrottleState uploadBps];
  id throttleUrlHostsValue = config[@"throttleUrlHosts"];
  NSArray *throttleUrlHosts = [throttleUrlHostsValue isKindOfClass:[NSArray class]] ? throttleUrlHostsValue : nil;
  resolve([OneKeyNetworkThrottleState
    setEnabled:enabled
    latencyMs:latencyMs
    downloadBps:downloadBps
    uploadBps:uploadBps
    throttleUrlHosts:throttleUrlHosts]);
}

@end
