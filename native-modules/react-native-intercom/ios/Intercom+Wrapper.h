#import "Intercom.h"
@interface Intercom (Wrapper)
+ (void)setCordovaVersion:(NSString *)version;
+ (void)setReactNativeVersion:(NSString *)version;
+ (void)setFlutterVersion:(NSString *)version;
+ (void)setUnityVersion:(NSString *)version;
@end
