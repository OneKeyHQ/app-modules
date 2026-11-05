// Partial interface; original endpoint and response orchestration remain unrestored.
#import <Foundation/Foundation.h>
@interface ICMHTTPClient : NSObject
+ (void)failWithNetworkError:(NSError *)error whenTryingTo:(NSString *)action failureBlock:(void (^)(NSError *))failure;
+ (NSError *)httpErrorWithCode:(NSInteger)code fromNetworkError:(NSError *)error;
+ (void)failWithErrorMessage:(NSString *)message recoverySuggestion:(NSString *)suggestion errorCode:(NSInteger)code underlyingError:(NSError *)underlying failureBlock:(void (^)(NSError *))failure;
+ (BOOL)bailIfMissingIdentityWhenTryingTo:(NSString *)action withFailureBlock:(void (^)(NSError *))failure;
@end

@interface ICMHTTPClient (IdentityRequests)
+ (void)setAuthTokens:(NSDictionary *)tokens success:(void (^)(void))success failure:(void (^)(NSError *))failure;
+ (NSMutableDictionary *)buildMultipartParametersForCurrentIdentity;
@end
@class ICMBaseResponse;
@interface ICMHTTPClient (UserUpdate)
+ (void)updateUserWithUserAttributes:(NSDictionary *)attributes newSession:(BOOL)newSession sessionId:(NSString *)sessionId sentFromBackground:(BOOL)background carouselVisible:(BOOL)visible success:(void (^)(ICMBaseResponse *))success error:(void (^)(NSError *))failure;
+ (void)setDeviceTokenSubmitted:(BOOL)submitted;
@end

@class ICMSheet;
@interface ICMHTTPClient (Sheets)
+ (void)fetchMessengerSheetData:(NSDictionary *)parameters success:(void (^)(ICMSheet *))success failure:(void (^)(NSError *))failure;
+ (void)submitMessengerSheet:(NSDictionary *)values uri:(NSString *)uri success:(void (^)(void))success failure:(void (^)(NSError *))failure;
@end

@interface ICMHTTPClient (HelpCenter)
+ (void)getHelpCenterCollections:(void (^)(id))success error:(void (^)(NSError *))failure;
+ (void)getHelpCenterCollection:(NSString *)identifier success:(void (^)(id))success error:(void (^)(NSError *))failure;
+ (void)getHelpCenterSearchResults:(NSString *)phrase source:(NSString *)source success:(void (^)(id))success error:(void (^)(NSError *))failure;
@end

@interface ICMHTTPClient (Tracking)
+ (void)trackURL:(NSString *)string;
@end
