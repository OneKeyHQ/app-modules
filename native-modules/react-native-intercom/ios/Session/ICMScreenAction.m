#import "ICMScreenAction.h"
#import "ICMColor.h"
@implementation ICMScreenAction
@synthesize type = _type, title = _title, metadata = _metadata, backgroundColor = _backgroundColor;
@synthesize textColor = _textColor, uri = _uri, screenId = _screenId;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMScreenAction *action = [ICMScreenAction new];
    [action setAttributesFromDictionary:dictionary];
    return action;
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if ([dictionary isKindOfClass:NSDictionary.class]) { [self setValuesForKeysWithDictionary:dictionary]; }
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([key isEqualToString:@"type"]) { self.type = [ICMScreenAction actionTypeForString:value]; }
    else if ([key isEqualToString:@"metadata"]) { if ([value isKindOfClass:NSDictionary.class]) { self.metadata = value; } }
    else if ([key isEqualToString:@"title"]) { [super setValue:value forKey:key]; }
    else if ([key isEqualToString:@"background_color"]) { if ([value isKindOfClass:NSString.class]) { self.backgroundColor = [ICMColor colorWithHexString:value]; } }
    else if ([key isEqualToString:@"text_color"]) { if ([value isKindOfClass:NSString.class]) { self.textColor = [ICMColor colorWithHexString:value]; } }
    else if ([key isEqualToString:@"uri"] && value) {
        NSURL *url = [[NSURL alloc] initWithString:value];
        [super setValue:url forKey:key];
    }
}
+ (NSUInteger)actionTypeForString:(NSString *)type {
    if ([type isEqualToString:@"continue"]) { return 2; }
    if ([type isEqualToString:@"skip"]) { return 3; }
    if ([type isEqualToString:@"dismiss"]) { return 5; }
    if ([type isEqualToString:@"push_notifications_permission"]) { return 7; }
    if ([type isEqualToString:@"location_permission"]) { return 8; }
    if ([type isEqualToString:@"microphone_permission"]) { return 11; }
    if ([type isEqualToString:@"camera_permission"]) { return 10; }
    if ([type isEqualToString:@"photos_permission"]) { return 9; }
    if ([type isEqualToString:@"start_chat"]) { return 4; }
    if ([type isEqualToString:@"link"]) { return 6; }
    if ([type hasSuffix:@"_permission"]) {
        NSLog(@"[Intercom] 🚨 An unknown permission type (%@) was contained in this Carousel. Please report this to Intercom.", type);
        return 1;
    }
    NSLog(@"[Intercom] 🚨 An unknown action (%@) was contained in this Carousel. Please report this to Intercom.", type);
    return 2;
}
- (BOOL)isEqual:(id)object {
    if (object == self) { return YES; }
    if (!object || ![[object class] isEqual:[self class]]) { return NO; }
    return [self isEqualToScreenAction:object];
}
- (BOOL)isEqualToScreenAction:(ICMScreenAction *)action {
    if ((self.title != nil || action.title != nil) && ![self.title isEqual:action.title]) { return NO; }
    if (self.type != action.type) { return NO; }
    if ((self.metadata != nil || action.metadata != nil) && ![self.metadata isEqual:action.metadata]) { return NO; }
    if ((self.uri != nil || action.uri != nil) && ![self.uri isEqual:action.uri]) { return NO; }
    return (self.textColor == nil && action.textColor == nil) || [self.textColor isEqual:action.textColor];
}
- (NSUInteger)hash { return self.title.hash ^ (NSUInteger)(NSInteger)(int32_t)_type; }
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeInteger:self.type forKey:NSStringFromSelector(@selector(type))];
    [coder encodeObject:self.title forKey:NSStringFromSelector(@selector(title))];
    [coder encodeObject:self.metadata forKey:NSStringFromSelector(@selector(metadata))];
    [coder encodeObject:self.backgroundColor forKey:NSStringFromSelector(@selector(backgroundColor))];
    [coder encodeObject:self.textColor forKey:NSStringFromSelector(@selector(textColor))];
    [coder encodeObject:self.uri forKey:NSStringFromSelector(@selector(uri))];
    [coder encodeObject:self.screenId forKey:NSStringFromSelector(@selector(screenId))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [self init];
    if (self) {
        self.type = [coder decodeIntegerForKey:NSStringFromSelector(@selector(type))];
        self.title = [coder decodeObjectOfClass:NSString.class forKey:NSStringFromSelector(@selector(title))];
        self.metadata = [coder decodeObjectOfClass:NSDictionary.class forKey:NSStringFromSelector(@selector(metadata))];
        self.backgroundColor = [coder decodeObjectOfClass:UIColor.class forKey:NSStringFromSelector(@selector(backgroundColor))];
        self.textColor = [coder decodeObjectOfClass:UIColor.class forKey:NSStringFromSelector(@selector(textColor))];
        self.uri = [coder decodeObjectOfClass:NSURL.class forKey:NSStringFromSelector(@selector(uri))];
        self.screenId = [coder decodeObjectOfClass:NSString.class forKey:NSStringFromSelector(@selector(screenId))];
    }
    return self;
}
@end
