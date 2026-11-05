// Original 24 required methods and 12 properties, metadata 0x364028/0x45eb38.
#import <UIKit/UIKit.h>
#import "ICMConfigDependencies.h"

@protocol ICMConfigProtocol
- (BOOL)receivedFromServer;
- (BOOL)canStartConversation;
- (BOOL)isTicketsSpaceEnabled;
- (NSString *)titleForSpace:(NSInteger)space;
- (BOOL)useDefaultLauncherAlignment;
- (UIImage *)customLauncherImage;
@property (readonly) BOOL inboundMessages;
@property (readonly) BOOL accessToTeammateEnabled;
@property (readonly) BOOL helpCenterRequireSearch;
@property (readonly) BOOL preventMultipleInboundConversations;
@property (readonly) BOOL hasOpenInboundConversations;
@property (nonatomic) BOOL finEnabled;
@property (readonly) BOOL articleAutoReactionEnabled;
@property (nonatomic, copy) NSString *appName;
@property (nonatomic, strong) ICMRecoveredConfigModules *modules;
@property (nonatomic, strong) ICMRecoveredAttachmentSettings *attachmentSettings;
@property (nonatomic, strong) ICMRecoveredConversationStateSyncSettings *conversationStateSyncSettings;
@property (nonatomic, copy) NSString *helpCenterUrlString;
@end
