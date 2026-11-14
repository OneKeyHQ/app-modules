#pragma once
#import "ICMScreenAction.h"
@interface ICMPermissionScreenAction : ICMScreenAction {
    NSInteger _permissionType;
}
@property (nonatomic) NSInteger permissionType;
- (BOOL)isEqualToPermissionScreenAction:(ICMPermissionScreenAction *)action;
@end
