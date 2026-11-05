#import "ICMHTTPClient.h"
@class ICMBaseResponse;
@interface ICMHTTPClient (OriginalEvents)
+ (void)logEventWithName:(NSString *)name metadata:(NSDictionary *)metadata sentFromBackground:(BOOL)background carouselVisible:(BOOL)visible success:(void (^)(ICMBaseResponse *))success error:(void (^)(NSError *))failure;
@end
