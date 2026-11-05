#import "ICMDeviceTokenData.h"

@implementation ICMDeviceTokenData
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[ICMDeviceTokenData class]]) return NO;
    ICMDeviceTokenData *other = object;
    return ((!other.deviceToken && !self.deviceToken) || [other.deviceToken isEqualToString:self.deviceToken])
        && other.deviceTokenEnvironment == self.deviceTokenEnvironment
        && other.deviceTokenSubmitted == self.deviceTokenSubmitted;
}
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.deviceToken forKey:@"deviceToken"];
    [coder encodeInteger:self.deviceTokenEnvironment forKey:@"deviceTokenEnvironment"];
    [coder encodeBool:self.deviceTokenSubmitted forKey:@"deviceTokenSubmitted"];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    if ((self = [super init])) {
        self.deviceToken = [coder decodeObjectOfClass:[NSString class] forKey:@"deviceToken"];
        self.deviceTokenEnvironment = [coder decodeIntegerForKey:@"deviceTokenEnvironment"];
        self.deviceTokenSubmitted = [coder decodeBoolForKey:@"deviceTokenSubmitted"];
    }
    return self;
}
@end
