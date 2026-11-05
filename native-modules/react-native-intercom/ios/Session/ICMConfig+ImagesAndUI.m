// Source recovery modifications: see docs/IOS_PROVENANCE.md.
// UNLINKED until the actual image core and DataManager close.
#import "ICMConfig.h"
#import "ICMConfigUIModels.h"
#import "ITBImageLoader.h"
#import <UIKit/UIKit.h>
// These are the real primary-class protocol methods, split into a source category.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wobjc-protocol-method-implementation"
@implementation ICMConfig (ImagesAndUI)
- (NSString *)resizedCustomLauncherImageCacheKey { return [NSString stringWithFormat:@"%@-resized", self.launcherCustomLogoURLString]; }
- (void)loadCustomLauncherLogoImage {
    if (![[ITBImageLoader longTermLoader] cachedImageForURL:[NSURL URLWithString:self.launcherCustomLogoURLString]]) {
        [[ITBImageLoader longTermLoader] loadImageWithURLString:self.launcherCustomLogoURLString completion:^(id image) {
            [[ITBImageLoader longTermLoader] cacheImage:image ForKey:self.launcherCustomLogoURLString];
            [[NSNotificationCenter defaultCenter] postNotificationName:@"kICMLauncherCustomLogoImageLoadedNotification" object:nil];
        }];
    }
}
- (UIImage *)customLauncherImage {
    id image = [[ITBImageLoader longTermLoader] cachedImageForKey:[self resizedCustomLauncherImageCacheKey]];
    if (!image) {
        image = [[ITBImageLoader longTermLoader] cachedImageForURL:[NSURL URLWithString:self.launcherCustomLogoURLString]];
        if (image) {
            if ([image isKindOfClass:UIImage.class]) image = [self resizeImage:image toSize:CGSizeMake(36, 36)];
            [[ITBImageLoader longTermLoader] cacheImage:image ForKey:[self resizedCustomLauncherImageCacheKey]];
        }
    }
    return image;
}
// Preserve the original context/PNG round trip, including its resulting scale.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
- (UIImage *)resizeImage:(UIImage *)image toSize:(CGSize)size {
    UIGraphicsBeginImageContextWithOptions(size, NO, image.scale);
    [image drawInRect:CGRectMake(0, 0, size.width, size.height)];
    NSData *data = UIImagePNGRepresentation(UIGraphicsGetImageFromCurrentImageContext());
    UIGraphicsEndImageContext();
    return [UIImage imageWithData:data];
}
#pragma clang diagnostic pop
- (void)fetchAndCacheFinThinkingGIFBrandedImage {
    if (![[ITBImageLoader longTermLoader] cachedImageForURL:[NSURL URLWithString:self.finThinkingGIFBrandedURLString]]) {
        [[ITBImageLoader longTermLoader] loadImageWithURLString:self.finThinkingGIFBrandedURLString completion:nil];
    }
}
- (void)fetchAndCacheFinThinkingGIFUnBrandedImage {
    if (![[ITBImageLoader longTermLoader] cachedImageForURL:[NSURL URLWithString:self.finThinkingGIFUnBrandedURLString]]) {
        [[ITBImageLoader longTermLoader] loadImageWithURLString:self.finThinkingGIFUnBrandedURLString completion:nil];
    }
}
- (void)prefetchHomeHeaderImagesIfNeeded {
    ICMRecoveredHomeHeaderBackground *background = self.modules.home.header.background;
    if (!background) return;
    if (background.imageUrl) {
        [[ITBImageLoader longTermLoader] loadImageWithURLString:background.imageUrl.absoluteString completion:^(id image) {
            (void)image;
            [[NSNotificationCenter defaultCenter] postNotificationName:@"kICMHomeHeaderImageLoaded" object:nil];
        }];
    }
    if (background.imageDarkUrl && ![background.imageDarkUrl isEqual:background.imageUrl]) {
        [[ITBImageLoader longTermLoader] loadImageWithURLString:background.imageDarkUrl.absoluteString completion:^(id image) {
            (void)image;
            [[NSNotificationCenter defaultCenter] postNotificationName:@"kICMHomeHeaderImageLoaded" object:nil];
        }];
    }
}
- (BOOL)canStartConversation {
    if (!self.inboundMessages) return NO;
    if (!self.preventMultipleInboundConversations) return YES;
    if (self.hasOpenInboundConversations) return NO;
    return ![[ICMDataManager sharedManager] hasOpenInboundConversation];
}
- (NSString *)titleForSpace:(NSInteger)spaceType {
    for (ICMRecoveredSpace *space in self.modules.home.openConfig.spaces) if (space.spaceType == (NSUInteger)spaceType) return space.title;
    return nil;
}
- (BOOL)isTicketsSpaceEnabled {
    for (ICMRecoveredSpace *space in self.modules.home.openConfig.spaces) if (space.spaceType == 2) return YES;
    return NO;
}
@end
#pragma clang diagnostic pop
