// Real original parser declarations; missing bodies remain product link gaps.
#import "ICMDataManagerOpenDependencies.h"
@interface ICMComposerSuggestions (OriginalDictionaryFactory)
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
@end
@interface ICMComposerState (OriginalDictionaryFactory)
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
@end
@interface ICMRecoveredNewConversationResponse (OriginalDictionaryInitializer)
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@end
@interface ICMRecoveredPoweredBy (OriginalDictionaryInitializer)
- (instancetype)initWithInstanceFrom:(NSDictionary *)dictionary;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate24MessagesEmptyStateAction")))
@interface ICMRecoveredMessagesEmptyStateAction : NSObject
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@end

@interface ICMConversationPart (OriginalDictionaryFactory)
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary displayStyle:(NSInteger)style;
@end
