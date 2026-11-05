// Source recovery modifications: see docs/ARTICLE_RECOVERY.md.
#import "ICMArticle.h"
#import "ITBBlock.h"
#import "ICMReactions.h"
#import "ICMBlockLoader.h"
#import "ICMLinkCardContentBlockAppearance.h"
@implementation ICMArticle
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    if (![dictionary isKindOfClass:[NSDictionary class]]) return nil;
    ICMArticle *article = [self new];
    [article setValuesForKeysWithDictionary:dictionary];
    return article;
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([value isKindOfClass:[NSString class]]) {
        if ([key isEqualToString:@"id"]) self.articleId = value;
        else if ([key isEqualToString:@"related_conversation_id"]) self.relatedConversationId = value;
        return;
    }
    if ([value isKindOfClass:[NSNumber class]]) {
        NSDate *date = [NSDate dateWithTimeIntervalSince1970:[value doubleValue]];
        if ([key isEqualToString:@"updated_at"]) self.updatedAt = date;
        else if ([key isEqualToString:@"created_at"]) self.createdAt = date;
        return;
    }
    if ([value isKindOfClass:[NSArray class]]) {
        if ([key isEqualToString:@"blocks"]) {
            NSMutableArray *blocks = [NSMutableArray array];
            for (id item in value) {
                id candidate = item;
                if ([[self class] isLinkBlock:item]) {
                    NSMutableDictionary *modified = [item mutableCopy];
                    modified[@"type"] = @"paragraph";
                    candidate = modified;
                }
                ITBBlock *block = [ITBBlockUtils blockFromDictionary:candidate appearance:[ICMLinkCardContentBlockAppearance new]];
                if (block != nil) [blocks addObject:block];
            }
            self.blocks = blocks;
        }
        return;
    }
    if ([value isKindOfClass:[NSDictionary class]]) {
        if ([key isEqualToString:@"card"]) {
            ITBBlock *block = [ITBBlockUtils blockFromDictionary:value appearance:[ICMLinkCardContentBlockAppearance new]];
            if ([block type] == 12) self.card = block;
            if ([value[@"title"] isKindOfClass:[NSString class]]) self.articleTitle = value[@"title"];
        } else if ([key isEqualToString:@"reactions_reply"]) {
            self.reactionSet = [ICMReactionsSet instanceFromDictionary:value];
        }
    }
}
+ (BOOL)isLinkBlock:(id)block {
    if (![block isKindOfClass:[NSDictionary class]]) return NO;
    if (![block[@"type"] isKindOfClass:[NSString class]]) return NO;
    return [block[@"type"] isEqualToString:@"link"];
}
- (id)copyWithZone:(NSZone *)zone {
    ICMArticle *article = [[[self class] allocWithZone:zone] init];
    article.articleId = self.articleId;
    article.card = [self.card copy];
    article.createdAt = self.createdAt;
    article.updatedAt = self.updatedAt;
    article.blocks = [[NSArray alloc] initWithArray:self.blocks copyItems:YES];
    return article;
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (object == nil) return NO;
    if (![[object class] isEqual:[self class]]) return NO;
    return [self isEqualToArticle:object];
}
- (BOOL)isEqualToArticle:(ICMArticle *)article {
    return ((!self.articleId && !article.articleId) || [self.articleId isEqual:article.articleId])
        && ((!self.card && !article.card) || [self.card isEqual:article.card])
        && ((!self.blocks && !article.blocks) || [self.blocks isEqual:article.blocks])
        && ((!self.createdAt && !article.createdAt) || [self.createdAt isEqual:article.createdAt])
        && ((!self.updatedAt && !article.updatedAt) || [self.updatedAt isEqual:article.updatedAt]);
}
@end
