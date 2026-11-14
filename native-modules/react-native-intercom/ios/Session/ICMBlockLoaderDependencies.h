// Original block/view dependencies; declarations do not close missing implementations.
#import "ICMConversationPartDependencies.h"
#import "ITBTextBlock.h"
#import "ITBListBlock.h"
#import "ITBButtonBlock.h"
#import "ITBImageBlock.h"
#import "ITBAttachmentBlock.h"
#import "ITBLinkBlock.h"
#import "ITBLinkListBlock.h"
#import "ITBTypingBlock.h"
#import "ITBNotificationCardBlock.h"
#import "ITBMessengerCardFallbackBlock.h"
#import "ICMImageTextBlock.h"
#import "ITBVideoBlock.h"
#import "ICMConversationRatingBlock.h"
@interface ITBBlock (OriginalBlockPipeline)
@property (nonatomic, strong) ITBUpload *upload;
@end
@interface ITBUpload (BlockUploadState)
- (BOOL)isImage;
- (BOOL)isVideo;
- (NSString *)cacheKey;
- (CGSize)dimensions;
- (NSString *)filename;
@end
@interface NSObject (OriginalBlockSizeEstimation)
+ (CGSize)estimateSizeForBlock:(ITBBlock *)block withWidth:(CGFloat)width constraintWidth:(BOOL)constraintWidth;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate11TicketBlock")))
__attribute__((swift_name("TicketBlock")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredTicketBlock : ITBBlock @end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate9CodeBlock")))
__attribute__((swift_name("CodeBlock")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredCodeBlock : ITBTextBlock @end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate10TableBlock")))
__attribute__((swift_name("TableBlock")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredTableBlock : ITBBlock @end
