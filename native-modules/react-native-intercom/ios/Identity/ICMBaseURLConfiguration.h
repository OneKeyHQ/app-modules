// Internal interface recovered from Intercom 19.9.1 Objective-C metadata.
#import <Foundation/Foundation.h>
NS_ASSUME_NONNULL_BEGIN
@interface ICMBaseURLConfiguration : NSObject
+ (nullable NSURL *)baseUrlForAppId:(nullable NSString *)appId;
+ (nullable NSString *)customLinkForBaseURL;
+ (NSString *)getRegion;
@end
NS_ASSUME_NONNULL_END
