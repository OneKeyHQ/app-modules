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
  if (!_jsBundleSource.empty()) {
    NSString *jsBundleSourceNS = [NSString stringWithUTF8String:_jsBundleSource.c_str()];
    NSURL *resolvedURL = resolveBundleSourceURL(jsBundleSourceNS);
    if (resolvedURL) {
      return resolvedURL;
    }

    [BTLogger warn:[NSString stringWithFormat:@"Unable to resolve custom jsBundleSource=%@", jsBundleSourceNS]];
  }

  NSURL *defaultBundleURL = resolveMainBundleResourceURL(@"background.bundle");
  if (defaultBundleURL) {
    return defaultBundleURL;
  }
  return [[NSBundle mainBundle] URLForResource:@"background" withExtension:@"bundle"];
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
