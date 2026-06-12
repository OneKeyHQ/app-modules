#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <React/RCTUtils.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import <SniConnectSpec/SniConnectSpec.h>
#endif

// Forward declaration of the Swift implementation
@interface SniConnectImpl : NSObject
- (instancetype)initWithEventSender:(id)eventSender;
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
@end

@interface SniConnect : RCTEventEmitter
#ifdef RCT_NEW_ARCH_ENABLED
<NativeSniConnectSpec>
#else
<RCTBridgeModule>
#endif
@end

@implementation SniConnect {
  SniConnectImpl *_implementation;
  BOOL _hasListeners;
}

RCT_EXPORT_MODULE(SniConnect)

// Expose hasListeners as a property for Swift access
- (BOOL)hasListeners {
  return _hasListeners;
}

+ (BOOL)requiresMainQueueSetup {
  return NO;
}

- (instancetype)init {
  if (self = [super init]) {
    _implementation = [[SniConnectImpl alloc] initWithEventSender:self];
    _hasListeners = NO;
  }
  return self;
}

// Event emitter methods
- (NSArray<NSString *> *)supportedEvents {
  return @[@"SniConnectLog"];
}

- (void)startObserving {
  _hasListeners = YES;
}

- (void)stopObserving {
  _hasListeners = NO;
}

// Method to send log event to JS
- (void)sendLogEvent:(NSDictionary *)logData {
  if (_hasListeners) {
    [self sendEventWithName:@"SniConnectLog" body:logData];
  }
}

#ifdef RCT_NEW_ARCH_ENABLED
// TurboModule interface implementation
- (void)request:(JS::NativeSniConnect::SniConnectRequest &)config
        resolve:(RCTPromiseResolveBlock)resolve
         reject:(RCTPromiseRejectBlock)reject {
  // Convert Codegen struct to NSDictionary for Swift implementation
  NSDictionary *configDict = @{
    @"ip": config.ip(),
    @"hostname": config.hostname(),
    @"method": config.method(),
    @"path": config.path(),
    @"headers": config.headers(),
    @"body": config.body() ?: [NSNull null],
    @"timeout": @(config.timeout())
  };

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
#endif

@end
