// Original mainline dependencies; declarations never substitute missing bodies.
#import "ICMDataManagerRequestDependencies.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate31NewConversationHomeCardResponse")))
__attribute__((swift_name("NewConversationHomeCardResponse")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredNewConversationHomeCardResponse : NSObject @end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate23NewConversationResponse")))
@interface ICMRecoveredNewConversationResponse : NSObject
@property (nonatomic, readonly) ICMRecoveredNewConversationCTAResponse *cta;
@property (nonatomic, readonly) ICMRecoveredNewConversationHomeCardResponse *homeCard;
@property (nonatomic, readonly) NSArray *parts;
@property (nonatomic, readonly) NSArray *participants;
@property (nonatomic, readonly) ICMRecoveredConversationHeader *header;
@property (nonatomic, readonly) NSString *teamIntro;
@property (nonatomic, readonly) NSString *specialNotice;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate12OpenResponse")))
@interface ICMRecoveredOpenResponse : NSObject
@property (nonatomic, readonly) ICMComposerState *composerState;
@property (nonatomic, readonly) ICMComposerSuggestions *composerSuggestions;
@property (nonatomic, readonly) ICMRecoveredNewConversationResponse *newConversation;
@property (nonatomic, readonly) NSString *requestId;
@property (nonatomic, readonly) ICMRecoveredPoweredBy *poweredBy;
@property (nonatomic, readonly) ICMRecoveredPrivacyPolicyNotice *privacyPolicyNotice;
@property (nonatomic, readonly) ICMConversation *recentOpenInboundConversation;
@property (nonatomic, readonly) NSDictionary *teamPresenceDictionary;
@property (nonatomic, readonly) NSNumber *resolutionBotBehaviorVersionId;
@property (nonatomic, readonly) NSString *botIntroId;
@end
@interface ICMRecoveredNewConversationCard (OriginalInitializer)
- (instancetype)initWithNewConversationHomeCard:(ICMRecoveredNewConversationHomeCardResponse *)homeCard;
@end
#import "ICMTeamPresence.h"
@interface ICMHTTPBridge (OpenRequiredPath)
+ (void)openMessenger:(void (^)(ICMRecoveredOpenResponse *))success error:(void (^)(NSError *))failure;
@end
