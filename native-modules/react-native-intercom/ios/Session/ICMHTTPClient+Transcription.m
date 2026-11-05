#import "ICMHTTPClient+Transcription.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"

@implementation ICMHTTPClient (Transcription)
+ (void)transcribeAudio:(NSData *)data conversationId:(NSString *)identifier success:(void (^)(NSString *))success error:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"transcribe audio" withFailureBlock:failure]) return;
    if (data != nil && data.length != 0) {
        [self transcribeAudioWithMultipart:data conversationId:identifier success:success error:failure];
        return;
    }
    if (failure == nil) return;
    void (^callback)(void) = ^{
        failure([NSError errorWithDomain:@"Audio data is required for transcription" code:400 userInfo:nil]);
    };
    if ([NSThread isMainThread]) callback();
    else dispatch_async(dispatch_get_main_queue(), callback);
}
+ (void)transcribeAudioWithMultipart:(NSData *)data conversationId:(NSString *)identifier success:(void (^)(NSString *))success error:(void (^)(NSError *))failure {
    NSMutableDictionary *parameters = [self buildMultipartParametersForCurrentIdentity];
    if (identifier != nil && identifier.length != 0) parameters[@"conversation_id"] = identifier;
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    [engine POSTMultipart:@"conversations/transcribe" parameters:parameters fileData:data fileName:@"recording.m4a" mimeType:@"audio/m4a" fieldName:@"audio" success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (response == nil || ![response isKindOfClass:[NSDictionary class]]) {
            void (^callback)(void) = ^{
                if (failure != nil) failure([NSError errorWithDomain:@"Invalid response format" code:422 userInfo:nil]);
            };
            if ([NSThread isMainThread]) callback();
            else dispatch_async(dispatch_get_main_queue(), callback);
            return;
        }
        id text = response[@"text"];
        if (![text isKindOfClass:[NSString class]] || [text length] == 0) {
            void (^callback)(void) = ^{
                if (failure != nil) failure([NSError errorWithDomain:@"No text in response" code:422 userInfo:nil]);
            };
            if ([NSThread isMainThread]) callback();
            else dispatch_async(dispatch_get_main_queue(), callback);
            return;
        }
        if ([NSThread isMainThread]) { if (success != nil) success(text); }
        else dispatch_async(dispatch_get_main_queue(), ^{ if (success != nil) success(text); });
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"transcribe audio" failureBlock:failure];
    }];
}
@end
