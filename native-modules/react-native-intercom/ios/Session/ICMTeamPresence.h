#import "../Identity/ICMSafeNetworkModel.h"
#import "ICMAvatarParticipantImports.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate9ActiveBot")))
@interface ICMRecoveredActiveBot : NSObject
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@property (nonatomic, readonly) id participant;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate18ParticipatingAdmin")))
__attribute__((swift_name("ParticipatingAdmin")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredParticipatingAdmin : ICMParticipant
- (instancetype)initWith:(NSDictionary *)dictionary;
@end
@interface ICMTeamPresence : ICMSafeNetworkModel {
    NSArray *_activeAdmins;
    NSDate *_lastActive;
    NSString *_responseDelayShort;
    NSString *_responseDelayLong;
    ICMRecoveredActiveBot *_activeBot;
}
@property (nonatomic, copy) NSArray *activeAdmins;
@property (nonatomic, strong) NSDate *lastActive;
@property (nonatomic, copy) NSString *responseDelayShort;
@property (nonatomic, copy) NSString *responseDelayLong;
@property (nonatomic, copy) ICMRecoveredActiveBot *activeBot;
+ (instancetype)sharedInstance;
+ (void)updateSharedInstanceWithDictionary:(NSDictionary *)dictionary;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
- (void)reset;
- (id)copyWithZone:(NSZone *)zone;
@end
