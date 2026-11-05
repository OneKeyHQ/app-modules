// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "IntercomContent.h"

@interface Article : IntercomContent <IntercomContentProtocol>
@property (nonatomic, copy) NSString *articleId;
@end
@implementation Article
- (ContentType)type { return article; }
- (id)contentId { return self.articleId; }
@end

@interface Carousel : IntercomContent <IntercomContentProtocol>
@property (nonatomic, copy) NSString *carouselId;
@end
@implementation Carousel
- (ContentType)type { return carousel; }
- (id)contentId { return self.carouselId; }
@end

@interface Survey : IntercomContent <IntercomContentProtocol>
@property (nonatomic, copy) NSString *surveyId;
@end
@implementation Survey
- (ContentType)type { return survey; }
- (id)contentId { return self.surveyId; }
@end

@interface HelpCenterCollections : IntercomContent <IntercomContentProtocol>
@property (nonatomic, copy) NSArray<NSString *> *collectionIds;
@end
@implementation HelpCenterCollections
- (ContentType)type { return helpCenterCollections; }
- (id)contentId { return self.collectionIds; }
@end

@interface Conversation : IntercomContent <IntercomContentProtocol>
@property (nonatomic, copy) NSString *conversationId;
@end
@implementation Conversation
- (ContentType)type { return conversation; }
- (id)contentId { return self.conversationId; }
@end

@interface TicketContent : IntercomContent <IntercomContentProtocol>
@property (nonatomic, copy) NSString *ticketId;
@end
@implementation TicketContent
- (ContentType)type { return ticket; }
- (id)contentId { return self.ticketId; }
@end

@implementation IntercomContent
@synthesize type, contentId;
+ (IntercomContent *)articleWithId:(NSString *)identifier {
    Article *content = [Article new];
    content.articleId = identifier;
    return content;
}
+ (IntercomContent *)carouselWithId:(NSString *)identifier {
    Carousel *content = [Carousel new];
    content.carouselId = identifier;
    return content;
}
+ (IntercomContent *)surveyWithId:(NSString *)identifier {
    Survey *content = [Survey new];
    content.surveyId = identifier;
    return content;
}
+ (IntercomContent *)helpCenterCollectionsWithIds:(NSArray<NSString *> *)identifier {
    HelpCenterCollections *content = [HelpCenterCollections new];
    content.collectionIds = identifier;
    return content;
}
+ (IntercomContent *)conversationWithId:(NSString *)identifier {
    Conversation *content = [Conversation new];
    content.conversationId = identifier;
    return content;
}
+ (IntercomContent *)ticketWithId:(NSString *)identifier {
    TicketContent *content = [TicketContent new];
    content.ticketId = identifier;
    return content;
}
@end
