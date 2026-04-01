#import "SplitBundleLoader.h"
#import "SBLLogger.h"
#import <React/RCTBridge.h>

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

        // Check if OTA bundle is active via BundleUpdateStore
        Class bundleUpdateStoreClass = NSClassFromString(@"ReactNativeBundleUpdate.BundleUpdateStore");
        if (bundleUpdateStoreClass) {
            NSString *otaBundlePath = nil;
            SEL sel = NSSelectorFromString(@"currentBundleMainJSBundle");
            if ([bundleUpdateStoreClass respondsToSelector:sel]) {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Warc-performSelector-leaks"
                id result = [bundleUpdateStoreClass performSelector:sel];
#pragma clang diagnostic pop
                otaBundlePath = [result isKindOfClass:[NSString class]] ? (NSString *)result : nil;
            }
            if (otaBundlePath && otaBundlePath.length > 0) {
                NSString *filePath = otaBundlePath;
                if ([otaBundlePath hasPrefix:@"file://"]) {
                    filePath = [[NSURL URLWithString:otaBundlePath] path];
                }
                if ([[NSFileManager defaultManager] fileExistsAtPath:filePath]) {
                    sourceKind = @"ota";
                    bundleRoot = [filePath stringByDeletingLastPathComponent];
                }
            }
        }

        // Builtin: use main bundle resource path
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

        // Resolve absolute path
        NSString *absolutePath = nil;

        // 1. Try OTA bundle root first
        Class bundleUpdateStoreClass = NSClassFromString(@"ReactNativeBundleUpdate.BundleUpdateStore");
        if (bundleUpdateStoreClass) {
            NSString *otaBundlePath = nil;
            SEL sel = NSSelectorFromString(@"currentBundleMainJSBundle");
            if ([bundleUpdateStoreClass respondsToSelector:sel]) {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Warc-performSelector-leaks"
                id result = [bundleUpdateStoreClass performSelector:sel];
#pragma clang diagnostic pop
                otaBundlePath = [result isKindOfClass:[NSString class]] ? (NSString *)result : nil;
            }
            if (otaBundlePath && otaBundlePath.length > 0) {
                NSString *filePath = otaBundlePath;
                if ([otaBundlePath hasPrefix:@"file://"]) {
                    filePath = [[NSURL URLWithString:otaBundlePath] path];
                }
                NSString *otaRoot = [filePath stringByDeletingLastPathComponent];
                NSString *candidate = [otaRoot stringByAppendingPathComponent:relativePath];
                if ([[NSFileManager defaultManager] fileExistsAtPath:candidate]) {
                    absolutePath = candidate;
                }
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

        // Register segment via RCTBridge
        RCTBridge *bridge = [RCTBridge currentBridge];
        if (bridge) {
            [bridge registerSegmentWithId:@(segId) path:absolutePath];
            [SBLLogger info:[NSString stringWithFormat:@"Loaded segment %@ (id=%d)", segmentKey, segId]];
            resolve(nil);
        } else {
            reject(@"SPLIT_BUNDLE_NO_BRIDGE", @"RCTBridge not available", nil);
        }
    } @catch (NSException *exception) {
        reject(@"SPLIT_BUNDLE_LOAD_ERROR",
               [NSString stringWithFormat:@"Failed to load segment %@: %@", segmentKey, exception.reason],
               nil);
    }
}

@end
