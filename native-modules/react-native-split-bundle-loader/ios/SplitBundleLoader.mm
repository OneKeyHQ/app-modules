#import "SplitBundleLoader.h"
#import "SBLLogger.h"
#import <ReactCommon/RCTHost.h>
#import <ReactCommon/RCTHost+Internal.h>
#import <ReactCommon/RCTInstance.h>
#import <objc/runtime.h>
#import <CommonCrypto/CommonDigest.h>
#include <jsi/jsi.h>

@implementation SplitBundleLoader

// Segment-time SHA-256 verification helper. Returns YES on match, NO on
// mismatch or read failure. Streams the file with a fixed-size buffer so a
// large segment doesn't pull the whole bundle into memory just to hash it.
+ (BOOL)verifySha256OfFile:(NSString *)path expected:(NSString *)expected
{
    if (expected.length == 0) {
        return YES;
    }
    NSFileHandle *handle = [NSFileHandle fileHandleForReadingAtPath:path];
    if (!handle) {
        return NO;
    }
    CC_SHA256_CTX ctx;
    CC_SHA256_Init(&ctx);
    @try {
        const NSUInteger chunk = 64 * 1024;
        while (1) {
            NSData *data = [handle readDataOfLength:chunk];
            if (data.length == 0) break;
            CC_SHA256_Update(&ctx, data.bytes, (CC_LONG)data.length);
        }
    } @finally {
        [handle closeFile];
    }
    unsigned char digest[CC_SHA256_DIGEST_LENGTH];
    CC_SHA256_Final(digest, &ctx);
    NSMutableString *hex = [NSMutableString stringWithCapacity:CC_SHA256_DIGEST_LENGTH * 2];
    for (int i = 0; i < CC_SHA256_DIGEST_LENGTH; i++) {
        [hex appendFormat:@"%02x", digest[i]];
    }
    return [hex caseInsensitiveCompare:expected] == NSOrderedSame;
}

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

/// Registers a segment with the current runtime via bridgeless (RCTHost) architecture (#13).
///
/// Thread safety (#57): This method is called from the TurboModule (JS thread).
/// No queue dispatch is needed.
+ (BOOL)registerSegment:(int)segmentId path:(NSString *)path error:(NSError **)outError
{
    // Bridgeless (New Architecture): get RCTHost via AppDelegate
    id<UIApplicationDelegate> appDelegate = [UIApplication sharedApplication].delegate;
    if ([appDelegate respondsToSelector:NSSelectorFromString(@"reactHost")]) {
        RCTHost *host = [appDelegate performSelector:NSSelectorFromString(@"reactHost")];
        if (host && [host respondsToSelector:@selector(registerSegmentWithId:path:)]) {
            [host registerSegmentWithId:@(segmentId) path:path];
            return YES;
        }
    }

    if (outError) {
        *outError = [NSError errorWithDomain:@"SplitBundleLoader"
                                        code:1
                                    userInfo:@{NSLocalizedDescriptionKey: @"RCTHost not available for segment registration"}];
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
        // Standardize root so hasPrefix matches candidate (iOS resolves /private/var → /var).
        NSString *otaRoot = [[otaPath stringByDeletingLastPathComponent] stringByStandardizingPath];
        NSString *candidate = [[otaRoot stringByAppendingPathComponent:relativePath] stringByStandardizingPath];
        BOOL otaPrefixOk = [candidate hasPrefix:otaRoot];
        BOOL otaExists = [[NSFileManager defaultManager] fileExistsAtPath:candidate];
        [SBLLogger info:[NSString stringWithFormat:@"[resolveAbs] rel=%@ ota root=%@ cand=%@ prefixOk=%d exists=%d",
                         relativePath, otaRoot, candidate, otaPrefixOk, otaExists]];
        if (otaPrefixOk && otaExists) {
            return candidate;
        }
    } else {
        [SBLLogger info:[NSString stringWithFormat:@"[resolveAbs] rel=%@ otaPath=(nil) — skipping OTA", relativePath]];
    }

    // 2. Fallback to builtin resource path
    NSString *builtinRoot = [[[NSBundle mainBundle] resourcePath] stringByStandardizingPath];
    NSString *candidate = [[builtinRoot stringByAppendingPathComponent:relativePath] stringByStandardizingPath];
    BOOL builtinPrefixOk = [candidate hasPrefix:builtinRoot];
    BOOL builtinExists = [[NSFileManager defaultManager] fileExistsAtPath:candidate];
    [SBLLogger info:[NSString stringWithFormat:@"[resolveAbs] rel=%@ builtin root=%@ cand=%@ prefixOk=%d exists=%d",
                     relativePath, builtinRoot, candidate, builtinPrefixOk, builtinExists]];
    if (builtinPrefixOk && builtinExists) {
        return candidate;
    }

    [SBLLogger warn:[NSString stringWithFormat:@"[resolveAbs] rel=%@ → nil (not found in OTA nor builtin)", relativePath]];
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
        if (!absolutePath) {
            reject(@"SPLIT_BUNDLE_NOT_FOUND",
                   [NSString stringWithFormat:@"Segment file not found: %@", relativePath],
                   nil);
            return;
        }
        if (![SplitBundleLoader verifySha256OfFile:absolutePath expected:sha256]) {
            reject(@"SPLIT_BUNDLE_SHA256_MISMATCH",
                   [NSString stringWithFormat:@"Segment SHA-256 mismatch: %@", relativePath],
                   nil);
            return;
        }
        resolve(absolutePath);
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

  RCTInstance *instance = object_getIvar(host, ivar);
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
  CFAbsoluteTime startTime = CFAbsoluteTimeGetCurrent();
  [SBLLogger info:[NSString stringWithFormat:@"[SplitBundle] loadEntryBundle: evaluating %@ (%lu bytes)", sourceURL, (unsigned long)data.length]];

  [instance callFunctionOnBufferedRuntimeExecutor:^(facebook::jsi::Runtime &runtime) {
    @autoreleasepool {
      CFAbsoluteTime evalStart = CFAbsoluteTimeGetCurrent();
      auto buffer = std::make_shared<facebook::jsi::StringBuffer>(
        std::string(static_cast<const char *>(data.bytes), data.length));
      runtime.evaluateJavaScript(std::move(buffer), [sourceURL UTF8String]);
      double evalMs = (CFAbsoluteTimeGetCurrent() - evalStart) * 1000.0;
      [SBLLogger info:[NSString stringWithFormat:@"[SplitBundle] loadEntryBundle: %@ evaluated in %.1fms", sourceURL, evalMs]];
    }
  }];

  double totalMs = (CFAbsoluteTimeGetCurrent() - startTime) * 1000.0;
  [SBLLogger info:[NSString stringWithFormat:@"[SplitBundle] loadEntryBundle: %@ dispatched in %.1fms (eval is async)", sourceURL, totalMs]];
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
        CFAbsoluteTime segStart = CFAbsoluteTimeGetCurrent();

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

        if (![SplitBundleLoader verifySha256OfFile:absolutePath expected:sha256]) {
            reject(@"SPLIT_BUNDLE_SHA256_MISMATCH",
                   [NSString stringWithFormat:@"Segment SHA-256 mismatch: %@ (key=%@)", relativePath, segmentKey],
                   nil);
            return;
        }

        // Register segment (#13: supports both bridge and bridgeless)
        NSError *regError = nil;
        if ([SplitBundleLoader registerSegment:segId path:absolutePath error:&regError]) {
            double segMs = (CFAbsoluteTimeGetCurrent() - segStart) * 1000.0;
            [SBLLogger info:[NSString stringWithFormat:@"[SplitBundle] Loaded segment %@ (id=%d) in %.1fms", segmentKey, segId, segMs]];
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
