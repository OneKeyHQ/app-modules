// Objective-C interface for the genuine recovered ImageLoaderCore.swift.
#import <Foundation/Foundation.h>
@class UIImage;
@interface ICMImageLoaderCore : NSObject
+ (instancetype)makeDefaultCore;
+ (instancetype)makeLongTermCore;
- (NSUUID *)loadImageWithURLString:(NSString *)url completion:(void (^)(id))completion;
- (void)cancelLoad:(NSUUID *)identifier;
- (id)cachedImageForURL:(NSURL *)url;
- (id)cachedImageForKey:(NSString *)key;
- (UIImage *)imageForKey:(NSString *)key generationBlock:(UIImage *(^)(void))block;
- (void)cacheImage:(id)image forKey:(NSString *)key;
- (void)clearCache;
@end
