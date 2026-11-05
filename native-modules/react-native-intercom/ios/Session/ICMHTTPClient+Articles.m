// Source recovery modifications: see docs/ARTICLE_RECOVERY.md.
#import "ICMHTTPClient+Articles.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "../Identity/ICMDeviceData.h"
@implementation ICMHTTPClient (Articles)
+ (void)article:(NSString *)identifier success:(void (^)(ICMArticle *))success error:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"get an article" withFailureBlock:failure]) return;
    if (identifier.length == 0) {
        [self failWithErrorMessage:@"ERROR - Tried to get article with an empty article id" recoverySuggestion:nil errorCode:400 underlyingError:nil failureBlock:failure];
        return;
    }
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    NSString *path = [NSString stringWithFormat:@"articles/%@", identifier];
    [engine POST:path parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success == nil) return;
        ICMArticle *article = [ICMArticle instanceFromDictionary:response[@"article"]];
        if ([NSThread isMainThread]) success(article);
        else dispatch_async(dispatch_get_main_queue(), ^{ success(article); });
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"get an article" failureBlock:failure];
    }];
}
+ (void)reactToArticle:(NSString *)identifier articleContentId:(NSString *)contentId withReactionIndex:(NSUInteger)index allowAutoResponses:(BOOL)allow articleSource:(NSString *)source {
    if ([self bailIfMissingIdentityWhenTryingTo:@"react to an article" withFailureBlock:nil]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"article_content_id"] = contentId;
    parameters[@"reaction_index"] = @(index);
    parameters[@"allow_auto_responses"] = @(allow);
    parameters[@"article_source"] = source;
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    NSString *path = [NSString stringWithFormat:@"articles/%@/react", identifier];
    [engine POST:path parameters:parameters success:nil failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"react to an article" failureBlock:nil];
    }];
}
@end
@implementation ICMHTTPBridge (Articles)
+ (void)article:(NSString *)identifier success:(void (^)(ICMArticle *))success error:(void (^)(NSError *))failure {
    [ICMHTTPClient article:identifier success:success error:failure];
}
+ (void)reactToArticle:(NSString *)identifier articleContentId:(NSString *)contentId reactionIndex:(NSUInteger)index allowAutoResponses:(BOOL)allow articleSource:(NSString *)source {
    [ICMHTTPClient reactToArticle:identifier articleContentId:contentId withReactionIndex:index allowAutoResponses:allow articleSource:source];
}
@end
