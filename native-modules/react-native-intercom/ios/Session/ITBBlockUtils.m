#import "ICMBlockLoader.h"
#import "ICMBlockLoaderDependencies.h"
#import <math.h>
#import "ICMUtils+Integration.h"
@implementation ITBBlockUtils
+ (ITBBlock *)blockFromDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    if (![dictionary isKindOfClass:[NSDictionary class]]) return nil;
    id type = dictionary[@"type"];
    if (![type isKindOfClass:[NSString class]]) type = nil;
    ITBBlock *block = nil;
    if ([type isEqualToString:@"paragraph"]) block = [[ITBParagraphBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"heading"]) block = [[ITBHeadingBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"subheading"]) block = [[ITBSubHeadingBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"unorderedList"]) block = [[ITBUnorderedListBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"orderedList"]) block = [[ITBOrderedListBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"image"]) block = [[ITBImageBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"button"]) block = [[ITBButtonBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"twitterFollowButton"]) block = [[ITBTwitterBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"facebookLikeButton"]) block = [[ITBFacebookBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"code"]) block = [[ITBCodeBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"video"]) block = [[ITBVideoBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"attachmentList"]) block = [[ITBAttachmentListBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"internal_attachment"]) block = [[ITBAttachmentBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"link"]) block = [[ITBLinkBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"notificationChannelsCard"]) block = [[ITBNotificationCardBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"internal_typing"]) block = [[ITBTypingBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"linkList"]) block = [[ITBLinkListBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"messengerCard"]) block = [[ITBMessengerCardFallbackBlock alloc] initWithDictionary:dictionary appearance:appearance];
    else if ([type isEqualToString:@"videoFile"]) block = [[ITBVideoFileBlock alloc] initWithDictionary:dictionary appearance:appearance];
    if (block == nil && dictionary[@"text"] != nil) block = [[ITBParagraphBlock alloc] initWithDictionary:dictionary appearance:appearance];
    return block;
}
+ (ITBBlock *)blockForUpload:(ITBUpload *)upload appearance:(id)appearance {
    ITBBlock *block;
    if ([upload isImage]) {
        NSString *url = [upload cacheKey];
        NSNumber *width = @([upload dimensions].width);
        NSNumber *height = @([upload dimensions].height);
        block = [[ITBImageBlock alloc] initWithDictionary:@{@"url": url, @"width": width, @"height": height} appearance:appearance];
    } else {
        // The original calls isVideo but does not branch on the result.
        [upload isVideo];
        NSString *url = [upload filename];
        NSString *text = [upload filename];
        block = [[ITBAttachmentBlock alloc] initWithDictionary:@{@"url": url, @"text": text} appearance:appearance];
    }
    [block setUpload:upload];
    return block;
}
+ (CGSize)calculateSizeForBlocks:(NSArray *)blocks withinWidth:(CGFloat)width constraintWidth:(BOOL)constraintWidth {
    CGFloat maximumWidth = 0;
    for (ITBBlock *block in blocks) {
        [block setEstimatedSize:[[block viewClass] estimateSizeForBlock:block withWidth:width constraintWidth:constraintWidth]];
        CGFloat roundedWidth = ceil([block estimatedSize].width);
        if (maximumWidth < roundedWidth) maximumWidth = roundedWidth;
    }
    CGFloat totalHeight = 0;
    CGFloat roundedMaximum = ceil(maximumWidth);
    for (ITBBlock *block in blocks) {
        totalHeight += ceil([block estimatedSize].height);
        CGFloat height = ceil([block estimatedSize].height);
        [block setEstimatedSize:CGSizeMake(roundedMaximum, height)];
    }
    return CGSizeMake(roundedMaximum, ceil(totalHeight));
}
+ (void)prepareBlocks:(NSArray *)blocks completion:(void (^)(void))completion {
    if ([blocks count] == 0 && completion != nil) completion();
    __block NSUInteger completed = 0;
    __block NSUInteger total = [blocks count];
    for (ITBBlock *block in blocks) {
        [block prepare:^{
            @synchronized (self) {
                completed += 1;
                if (completed == total && completion != nil) completion();
            }
        }];
    }
}
+ (NSBundle *)loadBlocksAssetsBundle {
    Class receiver = [self class];
    if (receiver == [ITBBlockUtils class]) return [ICMUtils intercomBundle];
    return [NSBundle bundleForClass:receiver];
}
@end
