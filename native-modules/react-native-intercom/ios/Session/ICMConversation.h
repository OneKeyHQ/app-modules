#import "../Identity/ICMSafeNetworkModel.h"
@class ICMConversationPart, ICMComposerState, ICMComposerSuggestions, ICMFooterNotice, ICMRecoveredParticipatingAdmin, ICMRecoveredTicket, ICMRecoveredArticleMetaData, ICMRecoveredConversationEndedButton, ICMRecoveredUIFlags, ICMRecoveredConversationHeader, ICMRecoveredPoweredBy;
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate15ArticleMetaData")))
__attribute__((swift_name("ArticleMetaData")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredArticleMetaData : NSObject
@property (nonatomic, readonly) NSString *articleId;
@property (nonatomic, readonly) NSString *articleSource;
@property (nonatomic, readonly) NSString *articleTitle;
@property (nonatomic, readonly) NSDate *createdAt;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate31ConversationEndedButtonMetadata")))
__attribute__((swift_name("ConversationEndedButtonMetadata")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredConversationEndedButtonMetadata : NSObject
@property (nonatomic, readonly) NSString *mergedIntoConversationId;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate23ConversationEndedButton")))
__attribute__((swift_name("ConversationEndedButton")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredConversationEndedButton : NSObject
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@property (nonatomic, readonly) ICMRecoveredConversationEndedButtonMetadata *metadata;
@property (nonatomic, readonly) NSString *text;
@property (nonatomic, readonly) NSString *title;
@end
@interface ICMConversation : ICMSafeNetworkModel <NSCopying> {
    BOOL _read;
    BOOL _userParticipated;
    BOOL _preventEndUserReplies;
    BOOL _inboundConversationsDisabled;
    BOOL _showTicketCardView;
    BOOL _isInboundConversation;
    BOOL _eligibleForRecentConversations;
    NSString * _conversationId;
    NSString * _title;
    NSDictionary * _participants;
    NSArray * _groupConversationParticipantIds;
    ICMFooterNotice * _footerNotice;
    ICMRecoveredParticipatingAdmin * _lastAdmin;
    ICMComposerState * _composerState;
    NSArray * _conversationParts;
    NSInteger _partDisplayStyle;
    NSString * _notificationStatus;
    ICMRecoveredTicket * _ticket;
    ICMComposerSuggestions * _composerSuggestions;
    ICMRecoveredArticleMetaData * _articleMetaData;
    NSUInteger _state;
    ICMRecoveredConversationEndedButton * _conversationEndedButton;
    NSString * _initialMessage;
    NSString * _previouslyClosedConversationId;
    ICMRecoveredUIFlags * _uiFlags;
    ICMRecoveredConversationHeader * _header;
    ICMRecoveredPoweredBy * _poweredBy;
    NSString * _teamIntro;
    NSString * _specialNotice;
}
@property (nonatomic, copy) NSString * conversationId;
@property (nonatomic, copy) NSString * title;
@property (nonatomic, copy) NSDictionary * participants;
@property (nonatomic, copy) NSArray * groupConversationParticipantIds;
@property (nonatomic, strong) ICMFooterNotice * footerNotice;
@property (nonatomic, strong) ICMRecoveredParticipatingAdmin * lastAdmin;
@property (nonatomic, strong) ICMComposerState * composerState;
@property (nonatomic, strong) NSArray * conversationParts;
@property (nonatomic) BOOL read;
@property (nonatomic) BOOL userParticipated;
@property (nonatomic) NSInteger partDisplayStyle;
@property (nonatomic) BOOL preventEndUserReplies;
@property (nonatomic) BOOL inboundConversationsDisabled;
@property (nonatomic, copy) NSString * notificationStatus;
@property (nonatomic, strong) ICMRecoveredTicket * ticket;
@property (nonatomic, strong) ICMComposerSuggestions * composerSuggestions;
@property (nonatomic) BOOL showTicketCardView;
@property (nonatomic, strong) ICMRecoveredArticleMetaData * articleMetaData;
@property (nonatomic) NSUInteger state;
@property (nonatomic, strong) ICMRecoveredConversationEndedButton * conversationEndedButton;
@property (nonatomic) BOOL isInboundConversation;
@property (nonatomic, strong) NSString * initialMessage;
@property (nonatomic, strong) NSString * previouslyClosedConversationId;
@property (nonatomic, getter=isEligibleForRecentConversations) BOOL eligibleForRecentConversations;
@property (nonatomic, strong) ICMRecoveredUIFlags * uiFlags;
@property (nonatomic, strong) ICMRecoveredConversationHeader * header;
@property (nonatomic, strong) ICMRecoveredPoweredBy * poweredBy;
@property (nonatomic, strong) NSString * teamIntro;
@property (nonatomic, strong) NSString * specialNotice;
@end
@interface ICMConversation (OriginalLogic)
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary partDisplayStyle:(NSInteger)style;
- (instancetype)initWithInitialMessage:(NSString *)message;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
- (void)setParticipantsFromDictionary:(NSDictionary *)dictionary;
- (void)setConversationPartsFromDictionary:(NSDictionary *)dictionary;
- (void)setConversationState:(id)value;
- (void)setParticipantsOnConversationParts;
- (BOOL)isGroupParticipantID:(NSString *)identifier;
- (BOOL)messageComposerShouldBeDisplayed;
- (BOOL)lastPartContainsReactionSet;
- (NSString *)participantsInConversation;
- (ICMConversationPart *)lastPart;
- (ICMConversationPart *)lastAdminPart;
- (BOOL)shouldDisplayStartNewConversationButton;
- (BOOL)isOpenInboundConversation;
- (NSArray *)adminsToShow;
- (id)activeBot;
- (ICMRecoveredConversationHeader *)headerToDisplay;
- (BOOL)isEqualToConversation:(ICMConversation *)conversation;
@end
