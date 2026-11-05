// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMConversationPart.h"
#import "ICMConversationPartDependencies.h"
@implementation ICMConversationPart (Appearance)
+ (id)blockAppearanceForPart:(ICMConversationPart *)part {
    NSInteger display = part.displayStyle;
    NSInteger style = part.partStyle;
    if (display != 1) {
        if (style == 2) return [[ICMPostBlockAppearance alloc] init];
        return [[ICMInAppBlockAppearance alloc] init];
    }
    if (style == 7) return [[ICMRecoveredAIAnswerPartBlockAppearance alloc] init];
    if (part.partStyle == 2 || part.partStyle == 1) return [[ICMContainerCardBlockAppearance alloc] init];
    if (part.isLinkCard) return [[ICMLinkCardBlockAppearance alloc] init];
    if (part.isLinkListCard) return [[ICMLinkListBlockAppearance alloc] init];
    if (part.isSingleMessengerCard) return [[ICMMessengerCardBlockAppearance alloc] init];
    if (part.isPersonalPart) return [[ICMRecoveredPersonalPartBlockAppearance alloc] init];
    return [[ICMRecoveredPartBlockAppearance alloc] init];
}
+ (BOOL)isLinkBlock:(NSDictionary *)dictionary {
    if (![dictionary isKindOfClass:[NSDictionary class]]) return NO;
    if (![dictionary[@"type"] isKindOfClass:[NSString class]]) return NO;
    return [dictionary[@"type"] isEqualToString:@"link"];
}
+ (BOOL)isLinkListBlock:(NSDictionary *)dictionary {
    if (![dictionary isKindOfClass:[NSDictionary class]]) return NO;
    if (![dictionary[@"type"] isKindOfClass:[NSString class]]) return NO;
    return [dictionary[@"type"] isEqualToString:@"linkList"];
}
+ (BOOL)isMessengerCardBlock:(NSDictionary *)dictionary {
    if (![dictionary isKindOfClass:[NSDictionary class]]) return NO;
    if (![dictionary[@"type"] isKindOfClass:[NSString class]]) return NO;
    return [dictionary[@"type"] isEqualToString:@"messengerCard"];
}
+ (BOOL)isArticleCardBlock:(NSDictionary *)dictionary {
    return [self isLinkBlock:dictionary] && dictionary[@"articleId"] != nil;
}
+ (id)determineAppearanceForBlock:(NSDictionary *)dictionary {
    if ([[self class] isLinkListBlock:dictionary]) return [[ICMLinkListBlockAppearance alloc] init];
    if ([[self class] isLinkBlock:dictionary]) return [[ICMLinkCardBlockAppearance alloc] init];
    return nil;
}
- (NSDictionary *)removeFallbackURLForFullChats:(NSDictionary *)dictionary {
    if ([[self class] isMessengerCardBlock:dictionary] && self.displayStyle == 0 &&
        self.partStyle == 0 && self.deliveryOption == 1) {
        NSMutableDictionary *copy = [NSMutableDictionary dictionaryWithDictionary:dictionary];
        [copy removeObjectForKey:@"fallbackUrl"];
        return copy;
    }
    return dictionary;
}
@end
