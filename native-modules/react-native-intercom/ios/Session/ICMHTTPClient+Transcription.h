#import "ICMHTTPClient.h"
NS_ASSUME_NONNULL_BEGIN
@interface ICMHTTPClient (Transcription)
+ (void)transcribeAudio:(NSData * _Nullable)data conversationId:(NSString * _Nullable)identifier success:(void (^ _Nullable)(NSString *))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
+ (void)transcribeAudioWithMultipart:(NSData *)data conversationId:(NSString * _Nullable)identifier success:(void (^ _Nullable)(NSString *))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
@end
NS_ASSUME_NONNULL_END
