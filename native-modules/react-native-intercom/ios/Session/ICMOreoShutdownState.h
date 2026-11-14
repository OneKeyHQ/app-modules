// Internal interface recovered from Intercom 19.9.1 metadata.
#import <Foundation/Foundation.h>
#import "ICMAppInstallationFingerprint.h"
@interface ICMOreoShutdownState : NSObject <NSSecureCoding>
@property (nonatomic, readonly) NSDate *endTime;
@property (nonatomic, readonly) ICMAppInstallationFingerprint *appFingerprint;
@property (nonatomic, copy, readonly) NSString *shutdownReason;
- (instancetype)initWithShutdownPeriod:(NSTimeInterval)period shutdownReason:(NSString *)reason;
- (BOOL)canSendOreoNetworkRequestsWithFingerprint:(ICMAppInstallationFingerprint *)fingerprint;
- (NSError *)shutdownError;
+ (instancetype)loadFromDisk;
- (void)saveToDisk;
+ (void)saveShutdownState:(ICMOreoShutdownState *)state;
+ (NSString *)archivePath;
+ (NSDate *)currentTime;
- (BOOL)isEqualToShutdownState:(ICMOreoShutdownState *)other;
@end
