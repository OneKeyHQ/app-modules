// Recovered Objective-C entry points implemented by MultipartTaskBuilder.swift.
#import <Foundation/Foundation.h>
NS_ASSUME_NONNULL_BEGIN
@interface ICMMultipartTaskBuilder : NSObject
+ (NSURLSessionUploadTask *)createUploadTaskWith:(NSURLSession *)session url:(NSURL *)url parameters:(NSDictionary<NSString *, NSString *> *)parameters fileData:(NSData *)data fileName:(NSString *)name mimeType:(NSString *)mimeType;
+ (NSURLSessionUploadTask *)createUploadTaskWith:(NSURLSession *)session url:(NSURL *)url parameters:(NSDictionary<NSString *, NSString *> *)parameters fileData:(NSData *)data fileName:(NSString *)name mimeType:(NSString *)mimeType fieldName:(NSString *)fieldName;
+ (NSURLRequest *)createMultipartRequestWithUrl:(NSURL *)url parameters:(NSDictionary<NSString *, NSString *> *)parameters fileData:(NSData *)data fileName:(NSString *)fileName mimeType:(NSString *)mimeType fieldName:(NSString *)fieldName existingHeaders:(NSDictionary<NSString *, NSString *> *)headers;
@end
NS_ASSUME_NONNULL_END
