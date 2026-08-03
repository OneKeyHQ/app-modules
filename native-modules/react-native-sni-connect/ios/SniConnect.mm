#import <React/RCTBridgeModule.h>
#import <React/RCTUtils.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import <SniConnectSpec/SniConnectSpec.h>
#endif

// Forward declaration of the Swift implementation
@interface SniConnectImpl : NSObject
- (instancetype)init;
- (void)request:(NSDictionary *)config
        resolve:(RCTPromiseResolveBlock)resolve
         reject:(RCTPromiseRejectBlock)reject;
- (void)cancelRequest:(NSString *)requestId
              resolve:(RCTPromiseResolveBlock)resolve
               reject:(RCTPromiseRejectBlock)reject;
- (void)cancelAllRequests:(RCTPromiseResolveBlock)resolve
                   reject:(RCTPromiseRejectBlock)reject;
- (void)clearDNSCache:(RCTPromiseResolveBlock)resolve
               reject:(RCTPromiseRejectBlock)reject;
- (void)getDebugSnapshot:(NSDictionary *)target
                 resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject;
- (void)isProxyActiveForUrl:(NSString *)url
                    resolve:(RCTPromiseResolveBlock)resolve
                     reject:(RCTPromiseRejectBlock)reject;
@end

@interface SniConnect : NSObject
#ifdef RCT_NEW_ARCH_ENABLED
<NativeSniConnectSpec>
#else
<RCTBridgeModule>
#endif
@end

@implementation SniConnect {
  SniConnectImpl *_implementation;
}

RCT_EXPORT_MODULE(SniConnect)

+ (BOOL)requiresMainQueueSetup {
  return NO;
}

- (instancetype)init {
  if (self = [super init]) {
    _implementation = [[SniConnectImpl alloc] init];
  }
  return self;
}

#ifdef RCT_NEW_ARCH_ENABLED
// TurboModule interface implementation
- (void)request:(JS::NativeSniConnect::NativeSniConnectRequest &)config
        resolve:(RCTPromiseResolveBlock)resolve
         reject:(RCTPromiseRejectBlock)reject {
  // Convert Codegen struct to NSDictionary for the Swift implementation.
  // Null-guard every field so a nil value never crashes the dictionary literal,
  // and forward requestId so cancellation works under the new architecture.
  NSMutableDictionary *configDict = [NSMutableDictionary dictionary];
  configDict[@"ip"] = config.ip() ?: @"";
  configDict[@"hostname"] = config.hostname() ?: @"";
  configDict[@"method"] = config.method() ?: @"GET";
  configDict[@"path"] = config.path() ?: @"/";
  configDict[@"headers"] = config.headers() ?: @{};
  configDict[@"timeout"] = @(config.timeout());
  if (config.requestId()) {
    configDict[@"requestId"] = config.requestId();
  }
  if (config.body()) {
    configDict[@"body"] = config.body();
  }

  [_implementation request:configDict resolve:resolve reject:reject];
}

- (void)cancelRequest:(NSString *)requestId
              resolve:(RCTPromiseResolveBlock)resolve
               reject:(RCTPromiseRejectBlock)reject {
  [_implementation cancelRequest:requestId resolve:resolve reject:reject];
}

- (void)cancelAllRequests:(RCTPromiseResolveBlock)resolve
                   reject:(RCTPromiseRejectBlock)reject {
  [_implementation cancelAllRequests:resolve reject:reject];
}

- (void)clearDNSCache:(RCTPromiseResolveBlock)resolve
               reject:(RCTPromiseRejectBlock)reject {
  [_implementation clearDNSCache:resolve reject:reject];
}

- (void)getDebugSnapshot:(JS::NativeSniConnect::SniConnectDebugTarget &)target
                  resolve:(RCTPromiseResolveBlock)resolve
                   reject:(RCTPromiseRejectBlock)reject {
  NSDictionary *targetDict = @{
    @"ip": target.ip() ?: @"",
    @"hostname": target.hostname() ?: @"",
  };
  [_implementation getDebugSnapshot:targetDict resolve:resolve reject:reject];
}

- (void)isProxyActiveForUrl:(NSString *)url
                    resolve:(RCTPromiseResolveBlock)resolve
                     reject:(RCTPromiseRejectBlock)reject {
  [_implementation isProxyActiveForUrl:url resolve:resolve reject:reject];
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
  return std::make_shared<facebook::react::NativeSniConnectSpecJSI>(params);
}
#else
// Bridge module interface implementation
RCT_EXPORT_METHOD(request:(NSDictionary *)config
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
  [_implementation request:config resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(cancelRequest:(NSString *)requestId
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
  [_implementation cancelRequest:requestId resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(cancelAllRequests:(RCTPromiseResolveBlock)resolver
                  rejecter:(RCTPromiseRejectBlock)rejecter) {
  [_implementation cancelAllRequests:resolver reject:rejecter];
}

RCT_EXPORT_METHOD(clearDNSCache:(RCTPromiseResolveBlock)resolver
                  rejecter:(RCTPromiseRejectBlock)rejecter) {
  [_implementation clearDNSCache:resolver reject:rejecter];
}

RCT_EXPORT_METHOD(getDebugSnapshot:(NSDictionary *)target
                  resolver:(RCTPromiseResolveBlock)resolver
                  rejecter:(RCTPromiseRejectBlock)rejecter) {
  [_implementation getDebugSnapshot:target resolve:resolver reject:rejecter];
}

RCT_EXPORT_METHOD(isProxyActiveForUrl:(NSString *)url
                  resolver:(RCTPromiseResolveBlock)resolver
                  rejecter:(RCTPromiseRejectBlock)rejecter) {
  [_implementation isProxyActiveForUrl:url resolve:resolver reject:rejecter];
}
#endif

@end
