// Objective-C alias for the real UserJwtRefresher.swift implementation.
#import <Foundation/Foundation.h>
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate16UserJwtRefresher")))
@interface ICMRecoveredUserJwtRefresher : NSObject
+ (instancetype)shared;
@property (nonatomic, readonly) BOOL hasProvider;
@property (nonatomic, copy) void (^provider)(void (^)(NSString *jwt));
- (void)userJwtWasSet:(NSString *)jwt;
- (NSArray *)fingerprintForUserJwt:(NSString *)jwt;
- (BOOL)shouldRefreshBeforeSending:(NSString *)jwt;
- (void)refreshWithStaleJwt:(NSString *)jwt fingerprint:(NSArray *)fingerprint completion:(void (^)(NSString *jwt))completion;
@end
