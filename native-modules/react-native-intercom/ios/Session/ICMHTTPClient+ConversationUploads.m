// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
#import "ICMHTTPClient+ConversationUploads.h"
#import "ICMAWSConfiguration.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "ICMEngine.h"
#import "../Identity/ICMDeviceData.h"

@implementation ICMHTTPClient (ConversationUploads)
+ (void)createUploadWithUpload:(ITBUpload *)upload success:(void (^)(NSString *))success progress:(void (^)(double))progress error:(void (^)(NSError *))failure {
    NSString *action = @"send an attachment";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    if (upload.uploadData == nil || upload.mimeType.length == 0 || upload.filename.length == 0) {
        [self failWithErrorMessage:@"ERROR - Tried to add an attachment without correct file data" recoverySuggestion:nil errorCode:400 underlyingError:nil failureBlock:failure];
        return;
    }
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"upload"] = [upload dictionaryRepresentation];
    parameters[@"include_metadata"] = @YES;
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    NSURLSessionDataTask *task = [engine POST:@"uploads" parameters:parameters success:^(NSURLSessionDataTask *responseTask, id response) {
        (void)responseTask;
        if (upload.cancelled) return;
        [ICMHTTPClient uploadFile:upload response:response success:success progress:progress failure:failure];
    } failure:^(NSURLSessionDataTask *responseTask, NSError *error) {
        (void)responseTask;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
    upload.uploadDataTask = task;
}

+ (void)uploadFile:(ITBUpload *)upload response:(id)response success:(void (^)(NSString *))success progress:(void (^)(double))progress failure:(void (^)(NSError *))failure {
    if (![response isKindOfClass:[NSDictionary class]]) {
        if (failure != nil) {
            if ([NSThread isMainThread]) failure([NSError errorWithDomain:@"Error creating upload" code:400 userInfo:nil]);
            else dispatch_async(dispatch_get_main_queue(), ^{
                failure([NSError errorWithDomain:@"Error creating upload" code:400 userInfo:nil]);
            });
        }
        return;
    }
    ICMAWSConfiguration *configuration = [ICMAWSConfiguration instanceFromDictionary:response];
    if (configuration.publicURL != nil && [upload isImage]) upload.cacheKey = configuration.publicURL;
    upload.uploadId = configuration.uploadId;
    ICMEngine *engine = [ICMEngine new];
    NSString *filename = upload.filename;
    NSData *data = upload.uploadData;
    NSURLSessionUploadTask *task = [engine uploadFileWithFileName:filename data:data uploadConfig:configuration success:^{
        if (success == nil) return;
        if ([NSThread isMainThread]) success(configuration.publicURL);
        else dispatch_async(dispatch_get_main_queue(), ^{ success(configuration.publicURL); });
    } progress:progress failure:^(NSError *error) {
        if (failure == nil) return;
        if ([NSThread isMainThread]) failure(error);
        else dispatch_async(dispatch_get_main_queue(), ^{ failure(error); });
    }];
    upload.uploadDataTask = task;
}
@end
