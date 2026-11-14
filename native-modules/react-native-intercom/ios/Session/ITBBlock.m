#import "ITBBlock.h"
#import <objc/runtime.h>
#import "ITBBlockView.h"
@implementation ITBBlock
@synthesize text = _text, align = _align, displayMode = _displayMode, estimatedSize = _estimatedSize;
@synthesize isRequired = _isRequired, isPartOfAIAnswerCard = _isPartOfAIAnswerCard;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    BOOL valid = [[self class] validateBlockDictionary:dictionary];
    if (appearance == nil || !valid) return nil;
    if ((self = [super init])) {
        [self setAttributesFromDictionary:dictionary];
        _displayMode = 0;
    }
    return self;
}
- (void)prepare:(void (^)(void))completion { if (completion) completion(); }
// Original base implementation 0x7482c has no body; concrete subclasses own appearance state.
- (void)updateAppearance:(id)appearance { (void)appearance; }
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if ([dictionary isKindOfClass:[NSDictionary class]]) [self setValuesForKeysWithDictionary:dictionary];
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([key isEqualToString:@"type"]) return;
    if ([[[self class] declaredPropertyNames] containsObject:key]) [super setValue:value forKey:key];
}
+ (NSSet *)declaredPropertyNames {
    static NSMapTable *cache;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ cache = [NSMapTable strongToStrongObjectsMapTable]; });
    @synchronized (cache) {
        NSSet *cached = [cache objectForKey:self];
        if (cached) return cached;
        NSMutableSet *names = [NSMutableSet set];
        Class candidate = self;
        while (candidate != Nil) {
            unsigned int count = 0;
            objc_property_t *properties = class_copyPropertyList(candidate, &count);
            for (unsigned int index = 0; index < count; index++) {
                [names addObject:[NSString stringWithUTF8String:property_getName(properties[index])]];
            }
            free(properties);
            if (candidate == [ITBBlock class]) break;
            candidate = class_getSuperclass(candidate);
        }
        NSSet *result = [names copy];
        [cache setObject:result forKey:self];
        return result;
    }
}
// Original KVC sink implementations 0x74b38/0x74b3c deliberately ignore these values.
- (void)setValue:(id)value forUndefinedKey:(NSString *)key { (void)value; (void)key; }
- (void)setNilValueForKey:(NSString *)key { (void)key; }
- (NSInteger)type { return -1; }
- (NSString *)text { return _text ?: @""; }
- (NSString *)align { return [_align length] != 0 ? _align : @"left"; }
- (NSString *)plainTextRepresentation { return nil; }
- (Class)viewClass { return [ITBBlockView class]; }
- (NSString *)blockUniqueIdentifier { return nil; }
+ (BOOL)validateBlockDictionary:(NSDictionary *)dictionary { return [dictionary isKindOfClass:[NSDictionary class]]; }
+ (BOOL)isNonEmptyString:(id)value { return [value isKindOfClass:[NSString class]] && [value length] != 0; }
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[ITBBlock class]]) return NO;
    ITBBlock *other = object;
    if (other.type != self.type) return NO;
    if (![other.text isEqualToString:self.text]) return NO;
    if (![other.align isEqualToString:self.align]) return NO;
    if (other.isRequired != self.isRequired) return NO;
    return other.isPartOfAIAnswerCard == self.isPartOfAIAnswerCard;
}
- (id)copyWithZone:(NSZone *)zone {
    ITBBlock *result = [[[self class] allocWithZone:zone] init];
    result.text = self.text;
    result.align = self.align;
    result.estimatedSize = self.estimatedSize;
    result.displayMode = self.displayMode;
    result.isRequired = self.isRequired;
    result.isPartOfAIAnswerCard = self.isPartOfAIAnswerCard;
    return result;
}
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.text forKey:NSStringFromSelector(@selector(text))];
    [coder encodeObject:self.align forKey:NSStringFromSelector(@selector(align))];
    [coder encodeCGSize:self.estimatedSize forKey:NSStringFromSelector(@selector(estimatedSize))];
    [coder encodeInteger:self.displayMode forKey:NSStringFromSelector(@selector(displayMode))];
    [coder encodeBool:self.isRequired forKey:NSStringFromSelector(@selector(isRequired))];
    [coder encodeBool:self.isPartOfAIAnswerCard forKey:NSStringFromSelector(@selector(isPartOfAIAnswerCard))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    if ((self = [self init])) {
        self.text = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(text))];
        self.align = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(align))];
        self.estimatedSize = [coder decodeCGSizeForKey:NSStringFromSelector(@selector(estimatedSize))];
        self.displayMode = [coder decodeIntegerForKey:NSStringFromSelector(@selector(displayMode))];
        self.isRequired = [coder decodeBoolForKey:NSStringFromSelector(@selector(isRequired))];
        self.isPartOfAIAnswerCard = [coder decodeBoolForKey:NSStringFromSelector(@selector(isPartOfAIAnswerCard))];
    }
    return self;
}
@end
