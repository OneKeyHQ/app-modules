#import "ICMHTTPClient.h"
@interface ICMHTTPClient (OriginalMetrics)
+ (void)submitMetrics:(NSArray *)metrics opsMetrics:(NSArray *)ops success:(void (^)(void))success error:(void (^)(NSError *))failure;
+ (void)submitDiagnostics:(NSArray *)diagnostics success:(void (^)(void))success error:(void (^)(NSError *))failure;
@end
