#import "ICMSheet.h"
@implementation ICMSheet
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMSheet *sheet = [[ICMSheet alloc] init];
    [sheet setAttributesFromDictionary:dictionary];
    return sheet;
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if ([dictionary isKindOfClass:NSDictionary.class]) [self setValuesForKeysWithDictionary:dictionary];
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([key isEqualToString:@"sheet_request_body"] && [value isKindOfClass:NSDictionary.class]) self.requestBody = value;
}
- (BOOL)isEqual:(id)object {
    if (object == self) return YES;
    if (!object || ![[object class] isEqual:[self class]]) return NO;
    return [self isEqualToSheet:object];
}
- (BOOL)isEqualToSheet:(ICMSheet *)sheet {
    if (sheet == self) return YES;
    if (!sheet) return NO;
    return self.requestBody == sheet.requestBody || [self.requestBody isEqualToDictionary:sheet.requestBody];
}
- (NSUInteger)hash { return self.requestBody.hash; }
@end
