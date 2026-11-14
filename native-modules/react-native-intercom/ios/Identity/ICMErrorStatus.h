// Internal interface recovered from Intercom 19.9.1 metadata.
#import <Foundation/Foundation.h>
@interface ICMErrorStatus : NSObject
+ (BOOL)isRetryable:(NSError *)error;
+ (BOOL)wasCancelled:(NSError *)error;
+ (BOOL)isMissingAppIdentity:(NSError *)error;
+ (BOOL)isMissingUserIdentity:(NSError *)error;
@end
