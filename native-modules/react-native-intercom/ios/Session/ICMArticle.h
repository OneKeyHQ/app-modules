#import "../Identity/ICMSafeNetworkModel.h"
@class ITBBlock, ICMReactionsSet;
@interface ICMArticle : ICMSafeNetworkModel <NSCopying>
@property (nonatomic, copy) NSString *articleId;
@property (nonatomic, copy) NSString *articleTitle;
@property (nonatomic, strong) ITBBlock *card;
@property (nonatomic, strong) NSArray<ITBBlock *> *blocks;
@property (nonatomic, strong) NSDate *createdAt;
@property (nonatomic, strong) NSDate *updatedAt;
@property (nonatomic, strong) ICMReactionsSet *reactionSet;
@property (nonatomic, copy) NSString *relatedConversationId;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
+ (BOOL)isLinkBlock:(id)block;
- (BOOL)isEqualToArticle:(ICMArticle *)article;
@end
