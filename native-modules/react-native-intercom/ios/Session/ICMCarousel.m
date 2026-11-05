#import "ICMCarousel.h"
#import "ICMScreen.h"
@implementation ICMCarousel
@synthesize carouselId = _carouselId, screens = _screens, dismissible = _dismissible;
@synthesize instanceId = _instanceId, viewState = _viewState, fetchedFromCache = _fetchedFromCache;
- (instancetype)init {
    self = [super init];
    if (self) { self.viewState = [ICMCarouselViewState new]; }
    return self;
}
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMCarousel *carousel = [ICMCarousel new];
    [carousel setAttributesFromDictionary:dictionary];
    return carousel;
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if ([dictionary isKindOfClass:NSDictionary.class]) { [self setValuesForKeysWithDictionary:dictionary]; }
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([key isEqualToString:@"screens"]) { [self setCarouselScreens:value]; }
    else if ([key isEqualToString:@"dismissible"]) { self.dismissible = [value boolValue]; }
    else { [self setValue:value forUndefinedKey:key]; }
}
- (void)setValue:(id)value forUndefinedKey:(NSString *)key {
    if ([key isEqualToString:@"instance_id"]) { self.instanceId = value; }
}
- (void)setCarouselScreens:(NSArray *)screens {
    if (![screens isKindOfClass:NSArray.class]) { return; }
    NSMutableArray *models = [NSMutableArray arrayWithCapacity:screens.count];
    for (id value in screens) { [models addObject:[ICMScreen instanceFromDictionary:value]]; }
    self.screens = models;
}
- (NSInteger)carouselType { return self.carouselId != nil ? 1 : 0; }
- (BOOL)isEqual:(id)object {
    if (object == self) { return YES; }
    if (!object || ![[object class] isEqual:[self class]]) { return NO; }
    return [self isEqualToCarousel:object];
}
- (BOOL)isEqualToCarousel:(ICMCarousel *)carousel {
    if ((self.screens != nil || carousel.screens != nil) && ![self.screens isEqual:carousel.screens]) { return NO; }
    if (self.dismissible != carousel.dismissible) { return NO; }
    return (self.instanceId == nil && carousel.instanceId == nil) || [self.instanceId isEqual:carousel.instanceId];
}
- (NSUInteger)hash { return self.instanceId.hash; }
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.screens forKey:NSStringFromSelector(@selector(screens))];
    [coder encodeBool:self.dismissible forKey:NSStringFromSelector(@selector(dismissible))];
    [coder encodeObject:self.instanceId forKey:NSStringFromSelector(@selector(instanceId))];
    [coder encodeObject:self.viewState forKey:NSStringFromSelector(@selector(viewState))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [self init];
    if (self) {
        self.screens = [coder decodeObjectOfClasses:[NSSet setWithObjects:NSArray.class, ICMScreen.class, nil] forKey:NSStringFromSelector(@selector(screens))];
        self.dismissible = [coder decodeBoolForKey:NSStringFromSelector(@selector(dismissible))];
        self.instanceId = [coder decodeObjectOfClass:NSString.class forKey:NSStringFromSelector(@selector(instanceId))];
        self.viewState = [coder decodeObjectOfClass:ICMCarouselViewState.class forKey:NSStringFromSelector(@selector(viewState))];
    }
    return self;
}
@end
