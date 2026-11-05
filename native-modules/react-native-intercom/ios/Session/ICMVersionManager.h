#import <Foundation/Foundation.h>
@interface ICMVersionManager : NSObject
+ (void)checkIfNewVersionOfSDKAvailable;
+ (BOOL)isNewIntercomVersionAvailable:(NSString *)latest;
@end
