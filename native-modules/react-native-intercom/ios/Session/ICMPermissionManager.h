#pragma once
#import <Foundation/Foundation.h>
typedef void (^ICMPermissionRequestCompletion)(NSInteger status, NSInteger operation);
typedef void (^ICMPermissionStatusCompletion)(NSInteger status);
@protocol ICMPermissionManager <NSObject>
- (void)requestPermissionWithCompletionHandler:(ICMPermissionRequestCompletion)completion;
- (void)permissionStatusWithCompletionHandler:(ICMPermissionStatusCompletion)completion;
- (void)subscribeToChangesWithBlock:(ICMPermissionStatusCompletion)block;
- (BOOL)isPermissionInPList;
@end
