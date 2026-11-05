#import "../Identity/ICMSafeNetworkModel.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate18MessagesEmptyState")))
__attribute__((swift_name("MessagesEmptyState")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredMessagesEmptyState : NSObject
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@end
@interface ICMConversationPageResponse : ICMSafeNetworkModel {
    BOOL _morePagesAvailable;
    NSArray *_conversations;
    ICMRecoveredMessagesEmptyState *_messagesEmptyState;
}
@property(nonatomic, copy) NSArray *conversations;
@property(nonatomic) BOOL morePagesAvailable;
@property(nonatomic, strong) ICMRecoveredMessagesEmptyState *messagesEmptyState;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
- (BOOL)isEqualToPageResponse:(ICMConversationPageResponse *)other;
@end
