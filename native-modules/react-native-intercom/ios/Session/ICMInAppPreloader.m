#import "ICMInAppPreloader.h"
#import "ICMConversation.h"
#import "ICMConversationPart.h"
#import "ICMAvatarDependencies.h"
#import "ICMReactions.h"
#import "ITBImageLoader.h"
#import "ICMBlockLoader.h"
@implementation ICMInAppPreloader
+ (void)prepareConversationForInApp:(ICMConversation *)conversation completion:(void (^)(BOOL))completion {
    __block NSInteger remaining = 2;
    ITBImageLoader *loader = [ITBImageLoader defaultLoader];
    NSString *url = [(ICMParticipant *)conversation.lastAdmin avatar].imageUrl.absoluteString;
    [loader loadImageWithURLString:url completion:^(id image) {
        (void)image;
        @synchronized (conversation) {
            --remaining;
            if (remaining == 0 && completion) dispatch_async(dispatch_get_main_queue(), ^{ completion(YES); });
        }
    }];
    [[self class] prepareInAppPart:conversation.conversationParts.lastObject completion:^(BOOL success) {
        @synchronized (conversation) {
            if (!success) {
                remaining = 0;
                if (completion) dispatch_async(dispatch_get_main_queue(), ^{ completion(NO); });
            }
            --remaining;
            if (remaining == 0 && completion) dispatch_async(dispatch_get_main_queue(), ^{ completion(YES); });
        }
    }];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 5000000000LL), dispatch_get_main_queue(), ^{
        @synchronized (conversation) {
            if (remaining != 0 && completion) { remaining = 0; completion(NO); }
        }
    });
}
+ (void)prepareInAppPart:(ICMConversationPart *)part completion:(void (^)(BOOL))completion {
    if (part.partStyle != 1 && part.partStyle != 2) { if (completion) completion(YES); return; }
    NSArray *options = part.reactionSet.options;
    __block NSInteger remaining = options.count + 1;
    for (ICMReactionsOption *option in options) {
        NSString *url = option.imageUrl.absoluteString;
        @synchronized (part) {
            if (url.length == 0) { remaining = 0; if (completion) completion(NO); }
        }
        [[ITBImageLoader longTermLoader] loadImageWithURLString:url completion:^(id image) {
            (void)image;
            @synchronized (part) { --remaining; if (remaining == 0 && completion) completion(YES); }
        }];
    }
    [ITBBlockUtils prepareBlocks:part.blocks completion:^{
        @synchronized (part) { --remaining; if (remaining == 0 && completion) completion(YES); }
    }];
}
@end
