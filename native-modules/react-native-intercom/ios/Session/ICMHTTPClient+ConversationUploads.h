#import "ICMHTTPClient.h"
#import "ITBUpload.h"

NS_ASSUME_NONNULL_BEGIN
@interface ICMHTTPClient (ConversationUploads)
+ (void)createUploadWithUpload:(ITBUpload * _Nullable)upload success:(void (^ _Nullable)(NSString * _Nullable))success progress:(void (^ _Nullable)(double))progress error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
+ (void)uploadFile:(ITBUpload * _Nullable)upload response:(id _Nullable)response success:(void (^ _Nullable)(NSString * _Nullable))success progress:(void (^ _Nullable)(double))progress failure:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
@end
NS_ASSUME_NONNULL_END
