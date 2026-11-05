// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
#import "ICMCountryCode.h"

@implementation ICMCountryCode
- (instancetype)init {
    self = [super init];
    if (self) {
        _emoji = @"🌎";
        _priority = 0;
        _areaCodes = [NSMutableArray new];
    }
    return self;
}
+ (ICMCountryCode *)instanceFromDictionary:(id)dictionary {
    ICMCountryCode *country = [ICMCountryCode new];
    [country setAttributesFromDictionary:dictionary];
    return country;
}
- (void)setAttributesFromDictionary:(id)dictionary {
    if ([dictionary isKindOfClass:NSDictionary.class]) [self setValuesForKeysWithDictionary:dictionary];
}
- (BOOL)isUnkown { return self.code.length == 0 && self.dialCode.length == 0; }
@end
