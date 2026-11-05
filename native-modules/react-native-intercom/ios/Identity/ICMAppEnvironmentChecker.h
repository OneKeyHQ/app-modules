#import <Foundation/Foundation.h>
@interface ICMAppEnvironmentChecker : NSObject
+ (NSUInteger)appEnvironment;
+ (BOOL)isDevelopmentEnvironment:(NSUInteger)environment;
+ (NSString *)stringForAppEnvironment:(NSUInteger)environment;
+ (NSString *)stringForDeviceTokenEnvironment:(NSUInteger)environment;
+ (NSUInteger)findCurrentAppEnvironment;
+ (BOOL)isRunningOnSimulator;
+ (BOOL)hasEmbeddedMobileProvision;
+ (NSUInteger)environmentFromEmbeddedMobileProvision;
+ (BOOL)isAppStoreReceiptSandbox;
@end
