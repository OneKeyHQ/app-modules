#import "ICMCarouselService.h"
#import "ICMCarouselMetricService.h"
#import "ICMHTTPClient+Carousels.h"
#import "ICMIdentityStore.h"
#import "ICMPersistentStore.h"
#import "ICMPresentationManager.h"
#import "ICMPermissionHelper.h"
#import <UIKit/UIKit.h>
@implementation ICMCarouselService
+ (void)load {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(loadCachedCarousel:) name:UIApplicationDidBecomeActiveNotification object:nil];
}
+ (void)presentCarousel:(ICMCarousel *)carousel {
    [ICMCarouselService filterScreens:carousel allowApprovedPermissions:NO withCompletionHandler:^(ICMCarousel *filtered) {
        if (filtered.screens.count > 0) [self presentCarouselAndStoreId:filtered];
        else NSLog(@"[Intercom] ⚠️ A Carousel was recieved but not shown. It was not shown as all screen of the carousel were removed.\nScreens are removed for one or more of the following reasons:\n(a) Requesting a permission that required a plist entry but no plist entry was found.\n(b) Requesting a permission that has already been granted.\n(c) Requesting a permission that is not supported by this device.");
    }];
}
+ (void)loadCachedCarousel:(NSNotification *)notification {
    (void)notification;
    if (![ICMIdentityStore currentIdentity].userIdentity.identityExists) return;
    ICMCarousel *carousel = [ICMPersistentStore fetchStoredCarousel];
    if (carousel != nil) {
        carousel.fetchedFromCache = YES;
        [self presentCarouselAndStoreId:carousel];
    }
}
+ (void)presentCarouselFromInstanceID:(NSString *)identifier {
    [[ICMCarouselMetricService sharedInstance] addEventForFetchingACarouselWithInstanceId:identifier];
    [ICMCarouselService getCarouselWithInstanceId:identifier success:^(ICMCarousel *carousel) {
        dispatch_async(dispatch_get_main_queue(), ^{ [self presentCarouselAndStoreId:carousel]; });
    } failure:^(NSError *error) {
        [[ICMCarouselMetricService sharedInstance] addEventForErrorWhileFetchingACarouselWithInstanceId:identifier statusCode:@(error.code)];
    }];
}
+ (void)presentCarouselAndStoreId:(ICMCarousel *)carousel {
    NSString *identifier = carousel.instanceId;
    if ([self isCarouselAlreadyPresented:identifier]) return;
    [[ICMPresentationManager sharedInstance] presentCarousel:carousel];
    [self storePresentedCarouselInstanceId:carousel.instanceId];
}
+ (void)getCarouselWithId:(NSString *)identifier success:(void (^)(ICMCarousel *))success failure:(void (^)(NSError *))failure {
    [ICMHTTPBridge getCarouselWithId:identifier success:^(ICMCarousel *carousel) {
        [ICMCarouselService filterScreens:carousel allowApprovedPermissions:YES withCompletionHandler:^(ICMCarousel *filtered) { success(filtered); }];
    } error:^(NSError *error) { failure(error); }];
}
+ (void)getCarouselWithInstanceId:(NSString *)identifier success:(void (^)(ICMCarousel *))success failure:(void (^)(NSError *))failure {
    [ICMHTTPBridge getCarouselWithInstanceId:identifier success:^(ICMCarousel *carousel) {
        [ICMCarouselService filterScreens:carousel allowApprovedPermissions:YES withCompletionHandler:^(ICMCarousel *filtered) { success(filtered); }];
    } error:^(NSError *error) { failure(error); }];
}
+ (void)filterScreens:(ICMCarousel *)carousel allowApprovedPermissions:(BOOL)allow withCompletionHandler:(void (^)(ICMCarousel *))completion {
    NSArray *screens = carousel.screens;
    [ICMCarouselService filterScreensWithInvalidPermissions:screens allowApprovedPermissions:allow withCompletionHandler:^(NSArray<ICMScreen *> *filtered) {
        carousel.screens = [ICMCarouselService filterScreensWithoutPListEntries:filtered];
        completion(carousel);
    }];
}
+ (void)filterScreensWithInvalidPermissions:(NSArray<ICMScreen *> *)screens allowApprovedPermissions:(BOOL)allow withCompletionHandler:(void (^)(NSArray<ICMScreen *> *))completion {
    NSMutableArray *invalidScreens = [@[] mutableCopy];
    dispatch_group_t group = dispatch_group_create();
    for (ICMScreen *screen in screens) {
        dispatch_group_enter(group);
        [ICMCarouselService checkScreenPermissionStatus:screen allowApprovedPermissions:allow withCompletionHandler:^(BOOL displayable) {
            if (!displayable) [invalidScreens addObject:screen];
            dispatch_group_leave(group);
        }];
    }
    dispatch_group_notify(group, dispatch_get_main_queue(), ^{
        NSMutableArray *filtered = [NSMutableArray arrayWithArray:screens];
        [filtered removeObjectsInArray:invalidScreens];
        completion(filtered);
    });
}
+ (void)checkScreenPermissionStatus:(ICMScreen *)screen allowApprovedPermissions:(BOOL)allow withCompletionHandler:(void (^)(BOOL))completion {
    ICMPermissionScreenAction *action = [screen fetchPermissionActionIfExists];
    if (action == nil) { completion(YES); return; }
    if (action.permissionType == 0) {
        NSLog(@"[Intercom] 🚨 A Carousel screen was removed. An unknown permission type was contained in this Carousel. Please report this to Intercom.");
        completion(NO);
        return;
    }
    [ICMCarouselService isDisplayablePermissionStatus:action.permissionType allowApprovedPermissions:allow withCompletionHandler:^(BOOL displayable) { completion(displayable); }];
}
+ (void)isDisplayablePermissionStatus:(NSInteger)type allowApprovedPermissions:(BOOL)allow withCompletionHandler:(void (^)(BOOL))completion {
    id<ICMPermissionManager> manager = [ICMPermissionHelper permissionManagerForPermissionType:type];
    [manager permissionStatusWithCompletionHandler:^(NSInteger status) {
        if (status == 4) {
            NSString *permission = [ICMCarouselService customerFacingTextForPermissionType:type];
            NSLog(@"[Intercom] ⚠️ A Carousel screen was removed! %@ permission is unsupported on this device.", permission);
            completion(NO);
        } else if (status == 1 && !allow) {
            NSString *permission = [ICMCarouselService customerFacingTextForPermissionType:type];
            NSLog(@"[Intercom] ✅ A Carousel screen for requesting %@ permission was removed. This permission has already been approved.", permission);
            completion(allow);
        } else completion(YES);
    }];
}
+ (NSArray<ICMScreen *> *)filterScreensWithoutPListEntries:(NSArray<ICMScreen *> *)screens {
    NSMutableArray *invalidScreens = [NSMutableArray array];
    for (ICMScreen *screen in screens) {
        ICMPermissionScreenAction *action = [screen fetchPermissionActionIfExists];
        id<ICMPermissionManager> manager = [ICMPermissionHelper permissionManagerForPermissionType:action.permissionType];
        if (manager != nil && ![manager isPermissionInPList]) {
            NSString *permission = [self customerFacingTextForPermissionType:action.permissionType];
            NSLog(@"[Intercom] ‼️ A Carousel screen was removed! %@ permission requires a plist entry. ‼️", permission);
            [invalidScreens addObject:screen];
        }
    }
    NSMutableArray *filtered = [NSMutableArray arrayWithArray:screens];
    [filtered removeObjectsInArray:invalidScreens];
    return filtered;
}
+ (NSString *)customerFacingTextForPermissionType:(NSInteger)type {
    static NSString *const names[] = {@"Notification", @"Location", @"Microphone", @"Camera", @"Photo"};
    NSUInteger index = (NSUInteger)type - 1;
    if (index > 4) return @"Unknown permission type";
    return names[index];
}
+ (void)cleanupCarouselCache { [ICMPersistentStore deleteStoredCarousel]; }
+ (void)storePresentedCarouselInstanceId:(NSString *)identifier { [[NSUserDefaults standardUserDefaults] setObject:identifier forKey:@"presentedCarouselInstanceId"]; }
+ (BOOL)isCarouselAlreadyPresented:(NSString *)identifier {
    NSString *stored = [[NSUserDefaults standardUserDefaults] stringForKey:@"presentedCarouselInstanceId"];
    return stored != nil && [stored isEqualToString:identifier];
}
+ (void)clearPresentedCarouselInstanceId { [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"presentedCarouselInstanceId"]; }
@end
