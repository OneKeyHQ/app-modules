// Objective-C aliases for genuine Swift models; DataManager remains unlinked.
#import "ICMConfigDependencies.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate20HomeHeaderBackground")))
@interface ICMRecoveredHomeHeaderBackground : NSObject
@property (nonatomic, readonly) NSURL *imageUrl;
@property (nonatomic, readonly) NSURL *imageDarkUrl;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate10HomeHeader")))
@interface ICMRecoveredHomeHeader : NSObject
@property (nonatomic, readonly) ICMRecoveredHomeHeaderBackground *background;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate5Space")))
@interface ICMRecoveredSpace : NSObject
@property (nonatomic, readonly) NSUInteger spaceType;
@property (nonatomic, readonly) NSString *title;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate10OpenConfig")))
@interface ICMRecoveredOpenConfig : NSObject
@property (nonatomic, readonly) NSArray<ICMRecoveredSpace *> *spaces;
@property (nonatomic, readonly) NSInteger type;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate10HomeConfig")))
@interface ICMRecoveredHomeConfig : NSObject
@property (nonatomic, readonly) ICMRecoveredHomeHeader *header;
@property (nonatomic, readonly) ICMRecoveredOpenConfig *openConfig;
@end
@interface ICMRecoveredConfigModules (OriginalModelAccess)
@property (nonatomic, readonly) ICMRecoveredHomeConfig *home;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate19CustomizationConfig")))
@interface ICMRecoveredCustomizationConfig : NSObject
@property (nonatomic, readonly) NSInteger effectiveThemeMode;
@end
@interface ICMRecoveredConfigModules (OriginalCustomizationAccess)
@property (nonatomic, readonly) ICMRecoveredCustomizationConfig *customizationConfig;
@end
#import "ICMDataManager.h"
