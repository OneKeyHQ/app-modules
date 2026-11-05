#import "ICMScreen.h"
#import "ICMCarouselBlockAppearance.h"
#import "ICMColor.h"
#import "ICMColorHelperImports.h"
#import "ICMBlockLoader.h"
@implementation ICMScreen
@synthesize screenId = _screenId, actions = _actions, backgroundColor = _backgroundColor, alignment = _alignment;
@synthesize blocksList = _blocksList, textColor = _textColor, appearance = _appearance;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMScreen *screen = [ICMScreen new];
    [screen setId:dictionary];
    [screen setCustomAppearance:dictionary];
    [screen setScreenBackgroundColor:dictionary];
    [screen setAttributesFromDictionary:dictionary];
    return screen;
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if ([dictionary isKindOfClass:NSDictionary.class]) { [self setValuesForKeysWithDictionary:dictionary]; }
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([key isEqualToString:@"actions"]) { [self setScreenActions:value]; }
    else if ([key isEqualToString:@"blocks"]) { [self setBlocks:value]; }
    else if ([key isEqualToString:@"vertical_alignment"]) { self.alignment = [ICMScreen alignmentForString:value]; }
}
- (void)setId:(NSDictionary *)dictionary {
    id value = dictionary[@"id"];
    if ([value isKindOfClass:NSString.class]) { self.screenId = value; }
}
- (void)setScreenBackgroundColor:(NSDictionary *)dictionary {
    id value = dictionary[@"background_color"];
    if ([value isKindOfClass:NSString.class]) { self.backgroundColor = value; }
}
- (void)setCustomAppearance:(NSDictionary *)dictionary {
    self.appearance = [ICMCarouselBlockAppearance new];
    id value = dictionary[@"text_color"];
    if (value) {
        UIColor *color = [ICMColor colorWithHexString:value];
        self.appearance.textColor = color;
        self.appearance.headingTextColor = color;
        self.appearance.subHeadingTextColor = color;
        self.appearance.linkTextColor = color;
    }
}
- (void)setBlocks:(NSArray *)blocks {
    if (![blocks isKindOfClass:NSArray.class]) { return; }
    NSMutableArray *models = [NSMutableArray arrayWithCapacity:blocks.count];
    for (id dictionary in blocks) {
        ITBBlock *block = [ICMBlockLoader blockFromDictionary:dictionary appearance:self.appearance];
        if (block) {
            if ([self doesBlockRequiresLoadingView:block]) { [self setLoadingViewColorType:block]; }
            block.displayMode = 1;
            [models addObject:block];
        }
    }
    self.blocksList = models;
}
- (BOOL)doesBlockRequiresLoadingView:(ITBBlock *)block { return block.type == 5 || block.type == 10 || block.type == 19; }
- (void)setLoadingViewColorType:(ITBBlock *)block {
    BOOL light = [ICMRecoveredColorHelper isLightColor:[ICMColor colorWithHexString:self.backgroundColor]];
    self.appearance.loadingViewColorType = !light;
    [block updateAppearance:self.appearance];
}
- (void)setScreenActions:(NSArray *)actions {
    if (![actions isKindOfClass:NSArray.class]) { return; }
    NSMutableArray *models = [NSMutableArray arrayWithCapacity:actions.count];
    for (id dictionary in actions) {
        if (![dictionary isKindOfClass:NSDictionary.class]) { continue; }
        ICMScreenAction *action;
        if ([dictionary[@"type"] hasSuffix:@"_permission"]) { action = [ICMPermissionScreenAction instanceFromDictionary:dictionary]; }
        else { action = [ICMScreenAction instanceFromDictionary:dictionary]; }
        action.screenId = self.screenId;
        [models addObject:action];
    }
    self.actions = models;
}
+ (NSUInteger)alignmentForString:(NSString *)string {
    if ([string isEqualToString:@"top"]) { return 0; }
    if ([string isEqualToString:@"center"]) { return 1; }
    if ([string isEqualToString:@"bottom"]) { return 2; }
    return 0;
}
- (ICMPermissionScreenAction *)fetchPermissionActionIfExists {
    for (ICMScreenAction *action in self.actions) { if ([action isMemberOfClass:ICMPermissionScreenAction.class]) { return (ICMPermissionScreenAction *)action; } }
    return nil;
}
- (BOOL)isEqual:(id)object {
    if (object == self) { return YES; }
    if (!object || ![[object class] isEqual:[self class]]) { return NO; }
    return [self isEqualToScreen:object];
}
- (BOOL)isEqualToScreen:(ICMScreen *)screen { return (self.screenId == nil && screen.screenId == nil) || [self.screenId isEqual:screen.screenId]; }
- (NSUInteger)hash { return self.screenId.hash; }
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.actions forKey:NSStringFromSelector(@selector(actions))];
    [coder encodeInteger:self.alignment forKey:NSStringFromSelector(@selector(alignment))];
    [coder encodeObject:self.backgroundColor forKey:NSStringFromSelector(@selector(backgroundColor))];
    [coder encodeObject:self.blocksList forKey:NSStringFromSelector(@selector(blocksList))];
    [coder encodeObject:self.screenId forKey:NSStringFromSelector(@selector(screenId))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [self init];
    if (self) {
        self.actions = [coder decodeObjectOfClasses:[NSSet setWithObjects:NSArray.class, ICMScreenAction.class, nil] forKey:NSStringFromSelector(@selector(actions))];
        self.alignment = [coder decodeIntegerForKey:NSStringFromSelector(@selector(alignment))];
        self.backgroundColor = [coder decodeObjectOfClass:NSString.class forKey:NSStringFromSelector(@selector(backgroundColor))];
        self.blocksList = [coder decodeObjectOfClasses:[NSSet setWithObjects:NSArray.class, ITBBlock.class, nil] forKey:NSStringFromSelector(@selector(blocksList))];
        self.screenId = [coder decodeObjectOfClass:NSString.class forKey:NSStringFromSelector(@selector(screenId))];
    }
    return self;
}
@end
