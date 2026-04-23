#import <SplitBundleLoaderSpec/SplitBundleLoaderSpec.h>

@interface SplitBundleLoader : NativeSplitBundleLoaderSpecBase <NativeSplitBundleLoaderSpec>

- (void)getRuntimeBundleContext:(RCTPromiseResolveBlock)resolve
                         reject:(RCTPromiseRejectBlock)reject;
- (void)loadSegment:(double)segmentId
         segmentKey:(NSString *)segmentKey
       relativePath:(NSString *)relativePath
             sha256:(NSString *)sha256
            resolve:(RCTPromiseResolveBlock)resolve
             reject:(RCTPromiseRejectBlock)reject;
- (void)resolveSegmentPath:(NSString *)relativePath
                    sha256:(NSString *)sha256
                   resolve:(RCTPromiseResolveBlock)resolve
                    reject:(RCTPromiseRejectBlock)reject;

/// Evaluate a JS bundle file inside the given RCTHost's runtime.
/// Used for the common + entry split-bundle loading strategy:
///   1. RCTHost boots with common.bundle (polyfills + shared modules)
///   2. After the runtime is ready, this evaluates the entry-specific bundle
///      (main.jsbundle or background.bundle) via jsi::Runtime::evaluateJavaScript.
///
/// @param bundlePath Absolute filesystem path to the bundle file.
/// @param host       The RCTHost whose runtime should evaluate the bundle.
+ (void)loadEntryBundle:(NSString *)bundlePath inHost:(id)host;

@end
