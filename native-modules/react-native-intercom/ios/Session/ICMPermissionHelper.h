#pragma once
#import "ICMPermissionManager.h"
@interface ICMPermissionHelper : NSObject
+ (id<ICMPermissionManager>)permissionManagerForPermissionType:(NSInteger)type;
@end
