// Internal interface recovered from Intercom 19.9.1 Objective-C metadata.
#import <Foundation/Foundation.h>
NS_ASSUME_NONNULL_BEGIN
@interface ICMDeviceTokenData : NSObject <NSSecureCoding>
@property (nonatomic) NSUInteger deviceTokenEnvironment;
@property (nonatomic, strong, nullable) NSString *deviceToken;
@property (nonatomic) BOOL deviceTokenSubmitted;
@end
NS_ASSUME_NONNULL_END
