#import "BackgroundRunnerReactNativeDelegate.h"
#import "BTLogger.h"

#include <jsi/JSIDynamic.h>
#include <jsi/decorator.h>
#include <react/utils/jsi-utils.h>
#include <map>
#include <memory>
#include <mutex>

#include "SharedStore.h"
#include "SharedRPC.h"

#import <React/RCTBridge.h>
#import <React/RCTBundleURLProvider.h>

#if __has_include(<react/utils/FollyConvert.h>)
  // static libs / header maps (no use_frameworks!)
  #import <react/utils/FollyConvert.h>
#elif __has_include("FollyConvert.h")
  /// `use_frameworks! :linkage => :static` users will need to import FollyConvert this way
#import "FollyConvert.h"
#elif __has_include("RCTFollyConvert.h")
  #import "RCTFollyConvert.h"
#else
  #error "FollyConvert.h not found. Ensure React-utils & RCT-Folly pods are installed."
#endif

#if __has_include(<React-RCTAppDelegate/RCTDefaultReactNativeFactoryDelegate.h>)
#import <React-RCTAppDelegate/RCTDefaultReactNativeFactoryDelegate.h>
#elif __has_include(<React_RCTAppDelegate/RCTDefaultReactNativeFactoryDelegate.h>)
#import React_RCTAppDelegate/RCTDefaultReactNativeFactoryDelegate.h
#endif

#import <ReactAppDependencyProvider/RCTAppDependencyProvider.h>
#import <ReactCommon/RCTTurboModule.h>

#import <objc/runtime.h>

#include <fmt/format.h>

namespace jsi = facebook::jsi;
namespace TurboModuleConvertUtils = facebook::react::TurboModuleConvertUtils;
using namespace facebook::react;

static void stubJsiFunction(jsi::Runtime &runtime, jsi::Object &object, const char *name)
{
  object.setProperty(
      runtime,
      name,
      jsi::Function::createFromHostFunction(
          runtime, jsi::PropNameID::forUtf8(runtime, name), 1, [](auto &, const auto &, const auto *, size_t) {
            return jsi::Value::undefined();
          }));
}

static void invokeOptionalGlobalFunction(jsi::Runtime &runtime, const char *name)
{
  try {
    jsi::Value fnValue = runtime.global().getProperty(runtime, name);
    if (!fnValue.isObject() || !fnValue.asObject(runtime).isFunction(runtime)) {
      return;
    }

    jsi::Function fn = fnValue.asObject(runtime).asFunction(runtime);
    fn.call(runtime);
  } catch (const jsi::JSError &e) {
    [BTLogger error:[NSString stringWithFormat:@"JSError calling global function %s: %s", name, e.getMessage().c_str()]];
  } catch (const std::exception &e) {
    [BTLogger error:[NSString stringWithFormat:@"Error calling global function %s: %s", name, e.what()]];
  }
}

static NSURL *resolveMainBundleResourceURL(NSString *resourceName)
{
  if (resourceName.length == 0) {
    return nil;
  }

  NSURL *directURL = [[NSBundle mainBundle] URLForResource:resourceName withExtension:nil];
  if (directURL) {
    return directURL;
  }

  NSString *normalizedName = [resourceName hasPrefix:@"/"]
      ? resourceName.lastPathComponent
      : resourceName;
  NSString *extension = normalizedName.pathExtension;
  NSString *baseName = normalizedName.stringByDeletingPathExtension;
  if (baseName.length == 0) {
    return nil;
  }

  return [[NSBundle mainBundle] URLForResource:baseName
                                 withExtension:extension.length > 0 ? extension : nil];
}

/// Reflectively query BundleUpdateStore for an OTA-installed bundle path.
/// Mirrors the cross-framework reflection pattern used by SplitBundleLoader
/// (we can't import the Swift module directly because its umbrella header
/// pulls in C++/Nitro headers that break the Clang dependency scanner).
/// Returns nil when the selector is absent (older bundle-update package) or
/// when no OTA is currently active.
static NSString *resolveOtaBundlePath(NSString *selectorName)
{
  Class cls = NSClassFromString(@"ReactNativeBundleUpdate.BundleUpdateStore");
  if (!cls) return nil;
  SEL sel = NSSelectorFromString(selectorName);
  if (![cls respondsToSelector:sel]) return nil;
  NSMethodSignature *sig = [cls methodSignatureForSelector:sel];
  if (!sig || strcmp(sig.methodReturnType, @encode(id)) != 0) return nil;
  NSInvocation *inv = [NSInvocation invocationWithMethodSignature:sig];
  inv.target = cls;
  inv.selector = sel;
  [inv invoke];
  __unsafe_unretained id raw = nil;
  [inv getReturnValue:&raw];
  if (![raw isKindOfClass:[NSString class]]) return nil;
  NSString *result = (NSString *)raw;
  if (result.length == 0) return nil;
  if ([result hasPrefix:@"file://"]) {
    result = [[NSURL URLWithString:result] path];
  }
  if (![[NSFileManager defaultManager] fileExistsAtPath:result]) return nil;
  return result;
}

/// True when an OTA-installed main bundle is currently active. Used to
/// prevent falling back to IPA built-in common/background bundles when the
/// foreground main runtime has already loaded an OTA main: a mixed
/// OTA-main + IPA-built-in pair would moduleId-mismatch and crash on first
/// require(). Without this guard, package skew (split-bundle-loader/
/// background-thread upgraded but bundle-update still on a version that
/// doesn't expose currentBundleCommonJSBundle) would silently reintroduce
/// the very crash this patch was added to fix.
static BOOL isOtaMainBundleActive(void)
{
  return resolveOtaBundlePath(@"currentBundleMainJSBundle") != nil;
}

static NSURL *resolveBundleSourceURL(NSString *jsBundleSourceNS)
{
  if (jsBundleSourceNS.length == 0) {
    return nil;
  }

  NSURL *parsedURL = [NSURL URLWithString:jsBundleSourceNS];
  if (parsedURL.scheme.length > 0) {
    if (parsedURL.isFileURL && parsedURL.path.length > 0) {
      return [NSURL fileURLWithPath:parsedURL.path];
    }
    return parsedURL;
  }

  if ([jsBundleSourceNS hasPrefix:@"/"]) {
    return [NSURL fileURLWithPath:jsBundleSourceNS];
  }

  return resolveMainBundleResourceURL(jsBundleSourceNS);
}

@interface BackgroundReactNativeDelegate () {
  RCTInstance *_rctInstance;
  std::string _origin;
  std::string _jsBundleSource;
  // Set inside `bundleURL` when the resolved common bundle came from an
  // OTA install. `resolveBackgroundEntryBundlePath` and `hostDidStart`
  // consult this to distinguish "background bundle missing because the
  // current bundle was never split" (legitimate, just warn) from "OTA
  // common is loaded but the matching OTA background couldn't be
  // resolved" (fatal — IPA built-in background would moduleId-mismatch
  // the OTA common and crash on first require()).
  BOOL _bundleURLUsedOtaCommon;
}

- (void)cleanupResources;
- (void)setupErrorHandler:(jsi::Runtime &)runtime;

@end

@implementation BackgroundReactNativeDelegate

#pragma mark - Instance Methods

- (instancetype)init
{
  if (self = [super init]) {
    _hasOnMessageHandler = NO;
    _hasOnErrorHandler = NO;
    _bundleURLUsedOtaCommon = NO;
    self.dependencyProvider = [[RCTAppDependencyProvider alloc] init];
  }
  return self;
}

- (void)cleanupResources
{
  _rctInstance = nil;
}

#pragma mark - C++ Property Getters

- (std::string)origin
{
  return _origin;
}

- (std::string)jsBundleSource
{
  return _jsBundleSource;
}

- (void)setJsBundleSource:(std::string)jsBundleSource
{
  _jsBundleSource = jsBundleSource;
}

- (NSURL *)sourceURLForBridge:(RCTBridge *)bridge
{
  return [self bundleURL];
}

- (NSURL *)bundleURL
{
  // Reset on every call so a re-entry (e.g. host restart) can't carry over
  // a stale OTA-common assertion from a previous load.
  _bundleURLUsedOtaCommon = NO;

  // When _jsBundleSource is set (dev mode or explicit override), use it as-is.
  // This is a single full bundle (not split), so DON'T use common+entry strategy.
  if (!_jsBundleSource.empty()) {
    NSString *jsBundleSourceNS = [NSString stringWithUTF8String:_jsBundleSource.c_str()];
    NSURL *resolvedURL = resolveBundleSourceURL(jsBundleSourceNS);
    if (resolvedURL) {
      return resolvedURL;
    }

    [BTLogger warn:[NSString stringWithFormat:@"Unable to resolve custom jsBundleSource=%@", jsBundleSourceNS]];
  }

  // Default: load common bundle (shared polyfills + modules).
  // Prefer OTA-installed common.bundle so a three-bundle OTA update is
  // actually picked up by the background runtime; fall back to the IPA
  // built-in common.jsbundle when no OTA is active. Without this, OTA
  // would push a new common.bundle to disk but the background runtime
  // would keep loading the stale built-in copy and crash on
  // moduleId mismatch with the OTA-loaded background.bundle.
  NSString *otaCommonPath = resolveOtaBundlePath(@"currentBundleCommonJSBundle");
  if (otaCommonPath) {
    [BTLogger info:[NSString stringWithFormat:@"BackgroundRuntime: using OTA common bundle at %@", otaCommonPath]];
    _bundleURLUsedOtaCommon = YES;
    return [NSURL fileURLWithPath:otaCommonPath];
  }

  // Mixed-state guard: if OTA main is loaded but OTA common is unresolvable,
  // refusing the IPA fallback is safer than crashing on moduleId mismatch.
  // Returning nil aborts the background runtime; the foreground main runtime
  // would have crashed anyway, so this just makes the failure mode loud.
  if (isOtaMainBundleActive()) {
    [BTLogger error:@"BackgroundRuntime: OTA main is active but OTA common bundle is unresolvable — refusing IPA fallback to avoid moduleId mismatch crash"];
    return nil;
  }

  NSURL *commonURL = resolveMainBundleResourceURL(@"common.jsbundle");
  if (commonURL) {
    return commonURL;
  }
  return [[NSBundle mainBundle] URLForResource:@"common" withExtension:@"jsbundle"];
}

- (NSString *)resolveBackgroundEntryBundlePath
{
  // Prefer OTA-installed background.bundle; fall back to IPA built-in.
  NSString *otaBackgroundPath = resolveOtaBundlePath(@"currentBundleBackgroundJSBundle");
  if (otaBackgroundPath) {
    [BTLogger info:[NSString stringWithFormat:@"BackgroundRuntime: using OTA background bundle at %@", otaBackgroundPath]];
    return otaBackgroundPath;
  }

  // Mixed-state guard: bundleURL set _bundleURLUsedOtaCommon when it loaded
  // an OTA-installed common.bundle. The IPA built-in background.bundle was
  // built against the IPA common.jsbundle, so its moduleIds won't line up
  // with the OTA common — using it would crash on first require(). Return
  // nil; hostDidStart consults the same flag and aborts loudly instead of
  // continuing with a broken background runtime.
  if (_bundleURLUsedOtaCommon) {
    [BTLogger error:@"BackgroundRuntime: OTA common is loaded but OTA background is unresolvable — refusing IPA fallback to avoid moduleId mismatch crash"];
    return nil;
  }

  NSURL *url = resolveMainBundleResourceURL(@"background.bundle");
  return url.path;
}

- (void)hostDidStart:(RCTHost *)host
{
  if (!host) {
    return;
  }

  // Clear old instance reference before setting new one
  _rctInstance = nil;

  Ivar ivar = class_getInstanceVariable([host class], "_instance");
  _rctInstance = object_getIvar(host, ivar);

  if (!_rctInstance) {
    return;
  }

  // When _jsBundleSource is set, the bundle loaded in bundleURL was already
  // a full single bundle (dev mode / explicit override), so skip entry loading.
  BOOL isSplitBundle = _jsBundleSource.empty();

  // Read the background entry bundle data before entering the executor block
  // (only needed in split-bundle mode).
  NSData *bgBundleData = nil;
  NSString *bgBundleSourceURL = nil;
  if (isSplitBundle) {
    NSString *bgBundlePath = [self resolveBackgroundEntryBundlePath];
    if (bgBundlePath) {
      bgBundleData = [NSData dataWithContentsOfFile:bgBundlePath];
      bgBundleSourceURL = bgBundlePath.lastPathComponent ?: @"background.bundle";
      [BTLogger info:[NSString stringWithFormat:@"Background entry bundle loaded from %@ (%lu bytes)",
                      bgBundlePath, (unsigned long)bgBundleData.length]];
    } else if (_bundleURLUsedOtaCommon) {
      // Fatal: bundleURL loaded an OTA common but resolveBackgroundEntryBundlePath
      // refused the IPA fallback. Setting up SharedStore / SharedRPC and calling
      // __setupBackgroundRPCHandler against a runtime with no entry bundle would
      // leave RPC silently broken. Abort host setup so the failure is loud.
      [BTLogger error:@"BackgroundRuntime: aborting hostDidStart — OTA common loaded but OTA background bundle is unresolvable"];
      return;
    } else {
      [BTLogger warn:@"Background entry bundle not found, __setupBackgroundRPCHandler may not be defined"];
    }
  }

  CFAbsoluteTime bgStartTime = CFAbsoluteTimeGetCurrent();

  [_rctInstance callFunctionOnBufferedRuntimeExecutor:[=](jsi::Runtime &runtime) {
    [self setupErrorHandler:runtime];

    // Install SharedStore into background runtime
    SharedStore::install(runtime);

    // Install SharedRPC with executor for cross-runtime notifications
    RCTInstance *bgInstance = _rctInstance;
    RPCRuntimeExecutor bgExecutor = [bgInstance](std::function<void(jsi::Runtime &)> work) {
        [bgInstance callFunctionOnBufferedRuntimeExecutor:[work](jsi::Runtime &rt) {
            work(rt);
        }];
    };
    SharedRPC::install(runtime, std::move(bgExecutor), "background");
    [BTLogger info:@"SharedStore and SharedRPC installed in background runtime"];

    // In split-bundle mode, evaluate the background entry bundle now.
    // This must happen BEFORE invokeOptionalGlobalFunction since the entry
    // bundle defines __setupBackgroundRPCHandler.
    if (isSplitBundle && bgBundleData && bgBundleData.length > 0) {
      CFAbsoluteTime bgEvalStart = CFAbsoluteTimeGetCurrent();
      auto buffer = std::make_shared<jsi::StringBuffer>(
        std::string(static_cast<const char *>(bgBundleData.bytes), bgBundleData.length));
      runtime.evaluateJavaScript(std::move(buffer), [bgBundleSourceURL UTF8String]);
      double bgEvalMs = (CFAbsoluteTimeGetCurrent() - bgEvalStart) * 1000.0;
      [BTLogger info:[NSString stringWithFormat:@"[SplitBundle] bg entry evaluated in %.1fms", bgEvalMs]];
    }

    double bgTotalMs = (CFAbsoluteTimeGetCurrent() - bgStartTime) * 1000.0;
    [BTLogger info:[NSString stringWithFormat:@"[SplitBundle] bg hostDidStart total setup in %.1fms", bgTotalMs]];

    invokeOptionalGlobalFunction(runtime, "__setupBackgroundRPCHandler");
  }];
}

#pragma mark - Segment Registration (Phase 2.5 spike)

- (BOOL)registerSegmentWithId:(NSNumber *)segmentId path:(NSString *)path
{
  if (!_rctInstance) {
    [BTLogger error:@"Cannot register segment: background RCTInstance not available"];
    return NO;
  }

  @try {
    SEL sel = NSSelectorFromString(@"registerSegmentWithId:path:");
    if ([_rctInstance respondsToSelector:sel]) {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Warc-performSelector-leaks"
      [_rctInstance performSelector:sel withObject:segmentId withObject:path];
#pragma clang diagnostic pop
      [BTLogger info:[NSString stringWithFormat:@"Segment registered in background runtime: id=%@, path=%@", segmentId, path]];
      return YES;
    } else {
      [BTLogger error:@"RCTInstance does not respond to registerSegmentWithId:path:"];
      return NO;
    }
  } @catch (NSException *exception) {
    [BTLogger error:[NSString stringWithFormat:@"Failed to register segment: %@", exception.reason]];
    return NO;
  }
}

#pragma mark - RCTTurboModuleManagerDelegate

- (id<RCTModuleProvider>)getModuleProvider:(const char *)name
{
  return [super getModuleProvider:name];
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const std::string &)name
                                                      jsInvoker:(std::shared_ptr<facebook::react::CallInvoker>)jsInvoker
{
    return [super getTurboModule:name jsInvoker:jsInvoker];
}

- (void)setupErrorHandler:(jsi::Runtime &)runtime
{
  // Get ErrorUtils
  jsi::Object global = runtime.global();
  jsi::Value errorUtilsVal = global.getProperty(runtime, "ErrorUtils");
  if (!errorUtilsVal.isObject()) {
    throw std::runtime_error("ErrorUtils is not available on global object");
  }

  jsi::Object errorUtils = errorUtilsVal.asObject(runtime);

  std::shared_ptr<jsi::Value> originalHandler = std::make_shared<jsi::Value>(
      errorUtils.getProperty(runtime, "getGlobalHandler").asObject(runtime).asFunction(runtime).call(runtime));

  auto handlerFunc = jsi::Function::createFromHostFunction(
      runtime,
      jsi::PropNameID::forAscii(runtime, "customGlobalErrorHandler"),
      2,
      [=, originalHandler = std::move(originalHandler)](
          jsi::Runtime &rt, const jsi::Value &thisVal, const jsi::Value *args, size_t count) -> jsi::Value {
        if (count < 2) {
          return jsi::Value::undefined();
        }

        if (originalHandler->isObject() && originalHandler->asObject(rt).isFunction(rt)) {
          jsi::Function original = originalHandler->asObject(rt).asFunction(rt);
          original.call(rt, args, count);
        }

        return jsi::Value::undefined();
      });

  // Set the new global error handler
  jsi::Function setHandler = errorUtils.getProperty(runtime, "setGlobalHandler").asObject(runtime).asFunction(runtime);
  setHandler.call(runtime, {std::move(handlerFunc)});

  // Disable further setGlobalHandler from sandbox
  stubJsiFunction(runtime, errorUtils, "setGlobalHandler");
}

@end
