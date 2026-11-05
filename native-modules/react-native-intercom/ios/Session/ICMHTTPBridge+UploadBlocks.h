#import "ICMHTTPBridge.h"
#import "ITBUpload.h"
@interface ICMHTTPBridge (OriginalUploadBlocks)
+ (void)createUploadWithUpload:(ITBUpload *)upload success:(void (^)(NSString *))success failure:(void (^)(NSError *))failure;
+ (NSArray *)makeSendableBlocks:(NSArray *)blocks withPublicURL:(NSString *)url upload:(ITBUpload *)upload;
+ (NSArray *)attachmentListBlockForUpload:(ITBUpload *)upload withPublicURL:(NSString *)url;
+ (NSArray *)videoFileBlockForUpload:(ITBUpload *)upload;
@end
