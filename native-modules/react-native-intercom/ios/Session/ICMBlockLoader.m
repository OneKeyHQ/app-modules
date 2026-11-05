#import "ICMBlockLoader.h"
#import "ICMBlockLoaderDependencies.h"
@implementation ICMBlockLoader
+ (ITBBlock *)blockFromDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    if (![dictionary isKindOfClass:[NSDictionary class]]) return nil;
    id type = dictionary[@"type"];
    if (![type isKindOfClass:[NSString class]]) type = nil;
    ITBBlock *block = nil;
    if ([type isEqualToString:@"conversationRating"]) block = [[ICMConversationRatingBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"imageText"]) block = [[ICMImageTextBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"createTicketCard"]) block = [[ICMRecoveredTicketBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"code"]) block = [[ICMRecoveredCodeBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"table"]) block = [[ICMRecoveredTableBlock alloc] initWithDictionary:dictionary appearance:appearance];
    if (block == nil) block = [ITBBlockUtils blockFromDictionary:dictionary appearance:appearance];
    return block;
}
@end
