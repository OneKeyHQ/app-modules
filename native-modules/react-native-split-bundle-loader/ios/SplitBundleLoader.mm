#import "SplitBundleLoader.h"
#import "SBLLogger.h"
#import <React/RCTBridge.h>
#import <objc/runtime.h>
#include <jsi/jsi.h>

// Bridgeless (New Architecture) support: RCTHost segment registration
@interface RCTHost (SplitBundle)
- (void)registerSegmentWithId:(NSNumber *)segmentId path:(NSString *)path;
@end

@implementation SplitBundleLoader

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    [SBLLogger info:@"SplitBundleLoader module initialized"];
    return std::make_shared<facebook::react::NativeSplitBundleLoaderSpecJSI>(params);
}

+ (NSString *)moduleName
{
    return @"SplitBundleLoader";
}

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

// MARK: - OTA bundle path helper

/// Safely retrieves the OTA bundle path via typed NSInvocation to avoid
/// performSelector ARC/signature issues (#15).
+ (nullable NSString *)otaBundlePath
{
    Class cls = NSClassFromString(@"ReactNativeBundleUpdate.BundleUpdateStore");
    if (!cls) return nil;

    SEL sel = NSSelectorFromString(@"currentBundleMainJSBundle");
    if (![cls respondsToSelector:sel]) return nil;

    NSMethodSignature *sig = [cls methodSignatureForSelector:sel];
    if (!sig || strcmp(sig.methodReturnType, @encode(id)) != 0) {
        [SBLLogger warn:@"OTA method signature mismatch — skipping"];
        return nil;
    }

    NSInvocation *inv = [NSInvocation invocationWithMethodSignature:sig];
    inv.target = cls;
    inv.selector = sel;
    [inv invoke];

    __unsafe_unretained id rawResult = nil;
    [inv getReturnValue:&rawResult];
    if (![rawResult isKindOfClass:[NSString class]]) return nil;

    NSString *result = (NSString *)rawResult;
    if (result.length == 0) return nil;

    if ([result hasPrefix:@"file://"]) {
        result = [[NSURL URLWithString:result] path];
    }
    return result;
}

// MARK: - Segment registration helper

/// Registers a segment with the current runtime, supporting both legacy bridge
/// and bridgeless (RCTHost) architectures (#13).
///
/// Thread safety (#57): This method is called from the TurboModule (JS thread).
/// RCTBridge.registerSegmentWithId:path: internally registers the segment with
/// the Hermes runtime on the JS thread, which is the correct calling context.
/// No queue dispatch is needed.
+ (BOOL)registerSegment:(int)segmentId path:(NSString *)path error:(NSError **)outError
{
    // Try legacy bridge first
    RCTBridge *bridge = [RCTBridge currentBridge];
    if (bridge && [bridge respondsToSelector:@selector(registerSegmentWithId:path:)]) {
        [bridge registerSegmentWithId:@(segmentId) path:path];
        return YES;
    }

    // Try bridgeless RCTHost via AppDelegate
    id<UIApplicationDelegate> appDelegate = [UIApplication sharedApplication].delegate;
    if ([appDelegate respondsToSelector:NSSelectorFromString(@"reactHost")]) {
        id host = [appDelegate performSelector:NSSelectorFromString(@"reactHost")];
        if (host && [host respondsToSelector:@selector(registerSegmentWithId:path:)]) {
            [host registerSegmentWithId:@(segmentId) path:path];
            return YES;
        }
    }

    if (outError) {
        *outError = [NSError errorWithDomain:@"SplitBundleLoader"
                                        code:1
                                    userInfo:@{NSLocalizedDescriptionKey: @"Neither RCTBridge nor RCTHost available for segment registration"}];
    }
    return NO;
}

// MARK: - getRuntimeBundleContext

- (void)getRuntimeBundleContext:(RCTPromiseResolveBlock)resolve
                         reject:(RCTPromiseRejectBlock)reject
{
    @try {
        NSString *runtimeKind = @"main";
        NSString *sourceKind = @"builtin";
        NSString *bundleRoot = @"";
        NSString *nativeVersion = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleShortVersionString"] ?: @"";
        NSString *bundleVersion = @"";

        NSString *otaPath = [SplitBundleLoader otaBundlePath];
        if (otaPath && [[NSFileManager defaultManager] fileExistsAtPath:otaPath]) {
            sourceKind = @"ota";
            bundleRoot = [otaPath stringByDeletingLastPathComponent];
        }

        if ([sourceKind isEqualToString:@"builtin"]) {
            bundleRoot = [[NSBundle mainBundle] resourcePath] ?: @"";
        }

        resolve(@{
            @"runtimeKind": runtimeKind,
            @"sourceKind": sourceKind,
            @"bundleRoot": bundleRoot,
            @"nativeVersion": nativeVersion,
            @"bundleVersion": bundleVersion,
        });
    } @catch (NSException *exception) {
        reject(@"SPLIT_BUNDLE_CONTEXT_ERROR", exception.reason, nil);
    }
}

// MARK: - Path resolution helper

/// Resolves a relative segment path to an absolute path, checking OTA then builtin.
/// Returns nil if the segment file is not found.
+ (nullable NSString *)resolveAbsolutePath:(NSString *)relativePath
{
    // 1. Try OTA bundle root first
    NSString *otaPath = [SplitBundleLoader otaBundlePath];
    if (otaPath) {
        NSString *otaRoot = [otaPath stringByDeletingLastPathComponent];
        NSString *candidate = [[otaRoot stringByAppendingPathComponent:relativePath] stringByStandardizingPath];
        if ([candidate hasPrefix:otaRoot] &&
            [[NSFileManager defaultManager] fileExistsAtPath:candidate]) {
            return candidate;
        }
    }

    // 2. Fallback to builtin resource path
    NSString *builtinRoot = [[NSBundle mainBundle] resourcePath];
    NSString *candidate = [[builtinRoot stringByAppendingPathComponent:relativePath] stringByStandardizingPath];
    if ([candidate hasPrefix:builtinRoot] &&
        [[NSFileManager defaultManager] fileExistsAtPath:candidate]) {
        return candidate;
    }

    return nil;
}

// MARK: - resolveSegmentPath (Phase 3)

- (void)resolveSegmentPath:(NSString *)relativePath
                    sha256:(NSString *)sha256
                   resolve:(RCTPromiseResolveBlock)resolve
                    reject:(RCTPromiseRejectBlock)reject
{
    @try {
        if ([relativePath containsString:@".."]) {
            reject(@"SPLIT_BUNDLE_INVALID_PATH",
                   [NSString stringWithFormat:@"Path traversal rejected: %@", relativePath],
                   nil);
            return;
        }

        NSString *absolutePath = [SplitBundleLoader resolveAbsolutePath:relativePath];
        if (absolutePath) {
            resolve(absolutePath);
        } else {
            reject(@"SPLIT_BUNDLE_NOT_FOUND",
                   [NSString stringWithFormat:@"Segment file not found: %@", relativePath],
                   nil);
        }
    } @catch (NSException *exception) {
        reject(@"SPLIT_BUNDLE_RESOLVE_ERROR", exception.reason, nil);
    }
}

// MARK: - loadEntryBundle (common + entry split loading)

+ (void)loadEntryBundle:(NSString *)bundlePath inHost:(id)host
{
  if (!host || bundlePath.length == 0) {
    [SBLLogger warn:[NSString stringWithFormat:@"loadEntryBundle: invalid arguments (host=%@, path=%@)", host, bundlePath]];
    return;
  }

  Ivar ivar = class_getInstanceVariable([host class], "_instance");
  if (!ivar) {
    [SBLLogger warn:[NSString stringWithFormat:@"loadEntryBundle: _instance ivar not found on %@", [host class]]];
    return;
  }

  id instance = object_getIvar(host, ivar);
  if (!instance) {
    [SBLLogger warn:@"loadEntryBundle: _instance is nil"];
    return;
  }

  NSData *data = [NSData dataWithContentsOfFile:bundlePath];
  if (!data || data.length == 0) {
    [SBLLogger warn:[NSString stringWithFormat:@"loadEntryBundle: failed to read bundle at %@", bundlePath]];
    return;
  }

  NSString *sourceURL = bundlePath.lastPathComponent ?: bundlePath;
  [SBLLogger info:[NSString stringWithFormat:@"loadEntryBundle: evaluating %@ (%lu bytes)", sourceURL, (unsigned long)data.length]];

  [instance callFunctionOnBufferedRuntimeExecutor:^(facebook::jsi::Runtime &runtime) {
    @autoreleasepool {
      auto buffer = std::make_shared<facebook::jsi::StringBuffer>(
        std::string(static_cast<const char *>(data.bytes), data.length));
      runtime.evaluateJavaScript(std::move(buffer), [sourceURL UTF8String]);
    }
  }];
}

// MARK: - loadSegment

- (void)loadSegment:(double)segmentId
         segmentKey:(NSString *)segmentKey
       relativePath:(NSString *)relativePath
             sha256:(NSString *)sha256
            resolve:(RCTPromiseResolveBlock)resolve
             reject:(RCTPromiseRejectBlock)reject
{
    @try {
        int segId = (int)segmentId;

        // Path traversal guard (#45)
        if ([relativePath containsString:@".."]) {
            reject(@"SPLIT_BUNDLE_INVALID_PATH",
                   [NSString stringWithFormat:@"Path traversal rejected: %@", relativePath],
                   nil);
            return;
        }

        NSString *absolutePath = [SplitBundleLoader resolveAbsolutePath:relativePath];

        if (!absolutePath) {
            reject(@"SPLIT_BUNDLE_NOT_FOUND",
                   [NSString stringWithFormat:@"Segment file not found: %@ (key=%@)", relativePath, segmentKey],
                   nil);
            return;
        }

        // Register segment (#13: supports both bridge and bridgeless)
        NSError *regError = nil;
        if ([SplitBundleLoader registerSegment:segId path:absolutePath error:&regError]) {
            [SBLLogger info:[NSString stringWithFormat:@"Loaded segment %@ (id=%d)", segmentKey, segId]];
            resolve(nil);
        } else {
            reject(@"SPLIT_BUNDLE_NO_RUNTIME",
                   regError.localizedDescription ?: @"Runtime not available",
                   regError);
        }
    } @catch (NSException *exception) {
        reject(@"SPLIT_BUNDLE_LOAD_ERROR",
               [NSString stringWithFormat:@"Failed to load segment %@: %@", segmentKey, exception.reason],
               nil);
    }
}

@end
