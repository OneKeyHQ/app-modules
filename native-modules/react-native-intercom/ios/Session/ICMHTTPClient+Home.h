#pragma once
#import "ICMHTTPClient.h"
NS_ASSUME_NONNULL_BEGIN
@interface ICMHTTPClient (Home)
+ (void)fetchHomeWithSuccess:(void (^ _Nullable)(NSDictionary *))success failure:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
@end
NS_ASSUME_NONNULL_END
