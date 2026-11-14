#import <Foundation/Foundation.h>
@interface ICPCardFallbackJSBuilder : NSObject
+ (NSString *)configureBridgeWithActionHandler:(NSString *)handler;
+ (NSString *)viewportScript;
+ (NSString *)disableSelectionScript;
+ (NSString *)disableContentCalloutScript;
@end
