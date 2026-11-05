#import "ICMAvatarParticipantImports.h"
#import "ICMBlockAppearanceDependencies.h"
#import "ICMAssets+Avatars.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate6Avatar")))
__attribute__((swift_name("Avatar")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface Avatar : NSObject
@property (nonatomic, readonly) NSInteger shape;
@property (nonatomic, readonly) NSString *label;
@property (nonatomic, readonly) NSString *initials;
@property (nonatomic, readonly) NSURL *imageUrl;
@property (nonatomic, readonly) NSURL *imageDarkUrl;
@end
@interface ICMParticipant (AvatarViewState)
@property (nonatomic, readonly) Avatar *avatar;
@property (nonatomic) BOOL isGroupParticipant;
@end
@interface ICMRecoveredEventParticipant (AvatarViewState)
@property (nonatomic, readonly) Avatar *avatar;
@property (nonatomic, readonly) NSString *initial;
@property (nonatomic, readonly) NSString *label;
@end
@interface ICMRecoveredTheme (AvatarViewColors)
@property (nonatomic, readonly) UIColor *primaryAvatar;
@property (nonatomic, readonly) UIColor *successFill;
@property (nonatomic, readonly) UIColor *warningFill;
@end
#import "ICMColorHelperImports.h"
@interface UIView (AvatarDisplayScale)
@property (nonatomic, readonly) CGFloat icmDisplayScale;
@end
