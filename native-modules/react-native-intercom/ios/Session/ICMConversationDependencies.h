// Genuine original dependencies. Missing bodies remain strict product link gaps.
#import "ICMOpenResponseImports.h"
#import "ICMConversationPartDependencies.h"
#import "ICMConfig.h"
#import "ICMRootDestinationDependencies.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate7UIFlags")))
__attribute__((swift_name("UIFlags")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredUIFlags : NSObject
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@end
__attribute__((swift_name("FooterNotice")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMFooterNotice : NSObject
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@end
@interface ICMRecoveredTicket (ConversationParser)
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@end
@interface ICMRecoveredConversationHeader (ConversationParser)
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@end
@interface ICMParticipant (ConversationParser)
- (instancetype)initWith:(NSDictionary *)dictionary isGroupParticipant:(BOOL)group;
@end
@interface ICMRecoveredParticipatingAdmin (ConversationState)
@property (nonatomic, readonly) NSString *firstName;
@end
#import "ICMAvatarParticipantImports.h"
@interface ICMRecoveredEventData (ConversationEventState)
@property (nonatomic, readonly) ICMRecoveredEventParticipant *participant;
@end
@interface ICMTranslationManager (ConversationParticipants)
- (NSString *)translationForKey:(NSString *)key params:(NSDictionary *)params;
@end

@interface ICMDataManager (ConversationHeaderState)
- (ICMConversation *)matchedNewConversation;
@end
