#import "ICMWebHelpCenterAction.h"
@implementation ICMWebHelpCenterAction
- (instancetype)initWithType:(NSString *)type value:(NSDictionary *)value {
    self = [super init];
    if (self) { _type = type; _value = value; }
    return self;
}
- (NSDictionary *)dictionaryRepresentation { return @{@"type": self.type, @"value": self.value}; }
@end
