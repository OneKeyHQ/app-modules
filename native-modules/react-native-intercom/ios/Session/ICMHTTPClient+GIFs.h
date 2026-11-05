// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
#import "ICMHTTPClient.h"
#import "ICMHTTPBridge.h"
#import "ICMGIFResponse.h"
NS_ASSUME_NONNULL_BEGIN
@interface ICMHTTPClient (GIFs)
+ (void)loadGIFs:(NSString * _Nullable)query success:(void (^ _Nullable)(ICMGIFResponse *))success error:(void (^ _Nullable)(NSError * _Nullable))failure;
@end
@interface ICMHTTPBridge (GIFs)
+ (void)loadGIFs:(NSString * _Null_unspecified)query success:(void (^ _Nullable)(ICMGIFResponse *))success error:(void (^ _Nullable)(NSError * _Nullable))failure;
@end
NS_ASSUME_NONNULL_END
