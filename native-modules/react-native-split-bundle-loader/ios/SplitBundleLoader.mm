#import "SplitBundleLoader.h"
#import "SBLLogger.h"
#import <React/RCTBridge.h>

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
        NSString *absolutePath = nil;

        // 1. Try OTA bundle root first
        NSString *otaPath = [SplitBundleLoader otaBundlePath];
        if (otaPath) {
            NSString *otaRoot = [otaPath stringByDeletingLastPathComponent];
            NSString *candidate = [otaRoot stringByAppendingPathComponent:relativePath];
            if ([[NSFileManager defaultManager] fileExistsAtPath:candidate]) {
                absolutePath = candidate;
            }
        }

        // 2. Fallback to builtin resource path
        if (!absolutePath) {
            NSString *builtinRoot = [[NSBundle mainBundle] resourcePath];
            NSString *candidate = [builtinRoot stringByAppendingPathComponent:relativePath];
            if ([[NSFileManager defaultManager] fileExistsAtPath:candidate]) {
                absolutePath = candidate;
            }
        }

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
