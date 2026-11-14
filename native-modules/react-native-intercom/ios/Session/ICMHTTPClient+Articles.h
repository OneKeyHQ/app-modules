#import "ICMHTTPClient.h"
#import "ICMHTTPBridge.h"
#import "ICMArticle.h"
NS_ASSUME_NONNULL_BEGIN
@interface ICMHTTPClient (Articles)
+ (void)article:(NSString *)identifier success:(void (^ _Nullable)(ICMArticle *))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
+ (void)reactToArticle:(NSString *)identifier articleContentId:(NSString * _Nullable)contentId withReactionIndex:(NSUInteger)index allowAutoResponses:(BOOL)allow articleSource:(NSString * _Nullable)source;
@end
@interface ICMHTTPBridge (Articles)
+ (void)article:(NSString *)identifier success:(void (^ _Nullable)(ICMArticle *))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
+ (void)reactToArticle:(NSString *)identifier articleContentId:(NSString * _Nullable)contentId reactionIndex:(NSUInteger)index allowAutoResponses:(BOOL)allow articleSource:(NSString * _Nullable)source;
@end
NS_ASSUME_NONNULL_END
