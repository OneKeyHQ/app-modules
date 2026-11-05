#import <Foundation/Foundation.h>
@class ICMImageLoaderCore;
@class UIImage;
@interface ITBImageLoader : NSObject
@property (nonatomic, strong) ICMImageLoaderCore *core;
- (instancetype)initWithCore:(ICMImageLoaderCore *)core;
+ (instancetype)defaultLoader;
+ (instancetype)longTermLoader;
+ (void)clearCache;
- (NSUUID *)loadImageWithURLString:(NSString *)url completion:(void (^)(id))completion;
- (void)cancelImageLoadWithUUID:(NSUUID *)identifier;
- (id)cachedImageForURL:(NSURL *)url;
- (id)cachedImageForKey:(NSString *)key;
- (UIImage *)imageForKey:(NSString *)key withGenerationBlock:(UIImage *(^)(void))block;
- (void)cacheImage:(id)image ForKey:(NSString *)key;
@end
