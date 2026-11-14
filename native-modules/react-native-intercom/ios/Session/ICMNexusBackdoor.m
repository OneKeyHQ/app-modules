#import "ICMNexusBackdoor.h"
#import "ICMDataManager.h"
#import "IntercomNexusEvent.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate17NexusEventFactory")))
__attribute__((swift_name("NexusEventFactory")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredNexusEventFactory : NSObject
+ (IntercomNexusEvent *)createNexusEventWith:(NSUInteger)type conversationId:(NSString *)conversationId;
@end
@implementation ICMNexusBackdoor
+ (void)triggerNexusEvent:(NSString *)conversationID {
    IntercomNexusEvent *event = [ICMRecoveredNexusEventFactory createNexusEventWith:6 conversationId:conversationID];
    [[ICMDataManager sharedManager] eventOccurred:event];
}
@end
