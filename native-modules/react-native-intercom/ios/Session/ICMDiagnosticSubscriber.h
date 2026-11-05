#import <Foundation/Foundation.h>
// Original Swift subscriber remains a required source implementation.
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate20DiagnosticSubscriber")))
@interface ICMRecoveredDiagnosticSubscriber : NSObject
+ (instancetype)shared;
- (void)startReceiving;
- (void)startReceivingWithCompletion:(void (^)(void))completion;
- (void)stopReceiving;
- (NSDictionary *)fetchStoredPayloadsSnapshot;
- (void)clearPayloadsWithIds:(NSArray<NSString *> *)ids;
@end
