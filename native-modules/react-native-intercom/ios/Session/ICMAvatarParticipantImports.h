#import <Foundation/Foundation.h>
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate11Participant")))
__attribute__((swift_name("Participant")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMParticipant : NSObject
@property (nonatomic, readonly) NSString *id;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate16EventParticipant")))
__attribute__((swift_name("EventParticipant")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredEventParticipant : NSObject
@property (nonatomic, readonly) NSString *participantId;
@property (nonatomic) BOOL isGroupParticipant;
@end
