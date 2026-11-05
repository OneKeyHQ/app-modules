#pragma once
#import <Foundation/Foundation.h>
#import "ICMCarousel.h"
#import "ICMScreen.h"
@interface ICMCarouselService : NSObject
+ (void)presentCarousel:(ICMCarousel *)carousel;
+ (void)loadCachedCarousel:(NSNotification *)notification;
+ (void)presentCarouselFromInstanceID:(NSString *)identifier;
+ (void)presentCarouselAndStoreId:(ICMCarousel *)carousel;
+ (void)getCarouselWithId:(NSString *)identifier success:(void (^)(ICMCarousel *))success failure:(void (^)(NSError *))failure;
+ (void)getCarouselWithInstanceId:(NSString *)identifier success:(void (^)(ICMCarousel *))success failure:(void (^)(NSError *))failure;
+ (void)filterScreens:(ICMCarousel *)carousel allowApprovedPermissions:(BOOL)allow withCompletionHandler:(void (^)(ICMCarousel *))completion;
+ (void)filterScreensWithInvalidPermissions:(NSArray<ICMScreen *> *)screens allowApprovedPermissions:(BOOL)allow withCompletionHandler:(void (^)(NSArray<ICMScreen *> *))completion;
+ (void)checkScreenPermissionStatus:(ICMScreen *)screen allowApprovedPermissions:(BOOL)allow withCompletionHandler:(void (^)(BOOL))completion;
+ (void)isDisplayablePermissionStatus:(NSInteger)type allowApprovedPermissions:(BOOL)allow withCompletionHandler:(void (^)(BOOL))completion;
+ (NSArray<ICMScreen *> *)filterScreensWithoutPListEntries:(NSArray<ICMScreen *> *)screens;
+ (NSString *)customerFacingTextForPermissionType:(NSInteger)type;
+ (void)cleanupCarouselCache;
+ (void)storePresentedCarouselInstanceId:(NSString *)identifier;
+ (BOOL)isCarouselAlreadyPresented:(NSString *)identifier;
+ (void)clearPresentedCarouselInstanceId;
@end
