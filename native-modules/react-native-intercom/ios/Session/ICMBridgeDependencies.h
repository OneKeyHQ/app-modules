// Missing selectors/classes are genuine dependencies, not product substitutes.
#import "../Identity/ICMUtils.h"
#import "ICMPushNotificationManager.h"
#import "ICMHTTPBridge.h"
#import "ICMIdentityStoreDependencies.h"
#import "ICMLocalComposerPersistManager.h"
#import "ICMMetric.h"
#import "ICMUtils+Integration.h"
@interface ICMRecoveredErrorProcessor (OriginalRegistrationError)
+ (NSError *)generateIdentifiedUserExists;
+ (NSError *)generateInvalidUserIdOrEmailError;
+ (NSError *)generateInvalidUpdateObjectError;
+ (NSError *)generateInvalidAuthTokenError;
@end
@interface ICMPushNotificationManager (OriginalSwizzling)
+ (void)swizzleReceiveRemoteNotifications;
@end
@interface ICMHTTPBridge (OriginalDiagnosticsAndMetrics)
+ (void)submitMetrics;
+ (void)submitDiagnostics;
@end
#import "ICMDiagnosticSubscriber.h"
