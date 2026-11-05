// Uses the genuine source-built ImageLoaderCore/cache/downloader pipeline.
#import "ITBImageLoader.h"
#import "ICMImageLoaderCore.h"
@implementation ITBImageLoader
- (instancetype)initWithCore:(ICMImageLoaderCore *)core {
    if ((self = [super init])) _core = core;
    return self;
}
+ (instancetype)defaultLoader {
    static ITBImageLoader *loader;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ loader = [[ITBImageLoader alloc] initWithCore:[ICMImageLoaderCore makeDefaultCore]]; });
    return loader;
}
+ (instancetype)longTermLoader {
    static ITBImageLoader *loader;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ loader = [[ITBImageLoader alloc] initWithCore:[ICMImageLoaderCore makeLongTermCore]]; });
    return loader;
}
+ (void)clearCache { [[ITBImageLoader defaultLoader].core clearCache]; [[ITBImageLoader longTermLoader].core clearCache]; }
- (NSUUID *)loadImageWithURLString:(NSString *)url completion:(void (^)(id))completion { return [self.core loadImageWithURLString:url completion:completion]; }
- (void)cancelImageLoadWithUUID:(NSUUID *)identifier { [self.core cancelLoad:identifier]; }
- (id)cachedImageForURL:(NSURL *)url { return [self.core cachedImageForURL:url]; }
- (id)cachedImageForKey:(NSString *)key { return [self.core cachedImageForKey:key]; }
- (UIImage *)imageForKey:(NSString *)key withGenerationBlock:(UIImage *(^)(void))block { return [self.core imageForKey:key generationBlock:block]; }
- (void)cacheImage:(id)image ForKey:(NSString *)key { [self.core cacheImage:image forKey:key]; }
@end
