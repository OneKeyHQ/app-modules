// Original required declarations; no replacement implementation bodies.
#import "ICMDataManagerRequestDependencies.h"
#import "ICMComposerSuggestionsDependencies.h"
#import "ITBUpload.h"
#import "ITBBlock.h"
#import "ICMPartValueImports.h"
@interface ITBBlock (OriginalArticleCard)
- (NSString *)articleId;
@end




#import "ICMLinkBlockAppearance.h"

#import "ICMReactions.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate22FinAnswerSourceWrapper")))
@interface ICMRecoveredFinAnswerSourceWrapper : NSObject
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate27AIAnswerPartBlockAppearance")))
@interface ICMRecoveredAIAnswerPartBlockAppearance : NSObject @end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate27PersonalPartBlockAppearance")))
@interface ICMRecoveredPersonalPartBlockAppearance : NSObject @end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate19PartBlockAppearance")))
@interface ICMRecoveredPartBlockAppearance : NSObject @end
#import "ICMPartBlockAppearances.h"

#import "ICMBlockLoader.h"
@interface ICMArticle : ICMSafeNetworkModel
@property (nonatomic, readonly) NSString *articleId;
@property (nonatomic, readonly) NSString *articleTitle;
@property (nonatomic, readonly) NSDate *createdAt;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate13StreamingPart")))
@interface ICMRecoveredStreamingPart : NSObject
@property (nonatomic, readonly) ICMRecoveredPartMetadata *metadata;
@end
@interface ICMRecoveredActiveBot (PartStreamingState)
@property (nonatomic, readonly) ICMRecoveredStreamingPart *streamingPart;
@end
