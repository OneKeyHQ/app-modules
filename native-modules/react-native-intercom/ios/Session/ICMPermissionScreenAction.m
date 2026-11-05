#import "ICMPermissionScreenAction.h"
@implementation ICMPermissionScreenAction
@synthesize permissionType = _permissionType;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMPermissionScreenAction *action = [ICMPermissionScreenAction new];
    [action setAttributesFromDictionary:dictionary];
    return action;
}
// The original computed getter ignores the separately decoded and hashed stored ivar.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
- (NSInteger)permissionType {
    NSUInteger index = self.type - 7;
    if (index > 4) { return 0; }
    static const NSInteger types[] = {1, 2, 5, 4, 3};
    return types[index];
}
#pragma clang diagnostic pop
- (BOOL)isEqual:(id)object {
    if (object == self) { return YES; }
    if (!object || ![[object class] isEqual:[self class]]) { return NO; }
    return [self isEqualToPermissionScreenAction:object];
}
- (BOOL)isEqualToPermissionScreenAction:(ICMPermissionScreenAction *)action { return self.permissionType == action.permissionType; }
- (NSUInteger)hash { return (NSUInteger)(NSInteger)(int32_t)_permissionType; }
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [super encodeWithCoder:coder];
    [coder encodeInteger:self.permissionType forKey:NSStringFromSelector(@selector(permissionType))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super initWithCoder:coder];
    if (self) { self.permissionType = [coder decodeIntegerForKey:NSStringFromSelector(@selector(permissionType))]; }
    return self;
}
@end
