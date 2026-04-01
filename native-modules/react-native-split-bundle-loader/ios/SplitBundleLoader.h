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

@end
