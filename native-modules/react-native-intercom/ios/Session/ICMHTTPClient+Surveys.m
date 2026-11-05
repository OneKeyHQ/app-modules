#import "ICMHTTPClient+Surveys.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "../Identity/ICMDeviceData.h"

@implementation ICMHTTPClient (Surveys)
+ (void)getSurveyWithInstanceId:(NSString *)identifier success:(void (^)(NSDictionary * _Nonnull))success error:(void (^)(NSError *))failure {
    NSString *action = @"get a survey";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"entity_id"] = identifier;
    parameters[@"entity_type"] = [NSNumber numberWithLongLong:85];
    NSString *path = @"content/fetch_survey";
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:path parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        success(response[@"survey"]);
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}
+ (void)submitSurvey:(ICMRecoveredSurvey *)survey success:(void (^)(NSDictionary * _Nonnull))success error:(void (^)(NSError *))failure {
    NSString *action = @"submit survey";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"current_step_id"] = survey.lastStepId;
    parameters[@"survey_progress_id"] = [NSNumber numberWithInteger:survey.progressId];
    parameters[@"responses"] = [survey allResponses];
    NSString *path = [NSString stringWithFormat:@"surveys/%@/submit", survey.id];
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:path parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        success(response);
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}
+ (void)dismissSurvey:(ICMRecoveredSurvey *)survey success:(void (^)(void))success error:(void (^)(NSError *))failure {
    NSString *action = @"dismiss survey";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"survey_progress_id"] = [NSNumber numberWithInteger:survey.progressId];
    NSString *path = [NSString stringWithFormat:@"surveys/%@/dismiss", survey.id];
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:path parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        (void)response;
        success();
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}
+ (void)failSurvey:(NSString *)identifier progressId:(NSInteger)progressId success:(void (^)(void))success error:(void (^)(NSError *))failure {
    NSString *action = @"fail survey";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"survey_progress_id"] = [NSNumber numberWithInteger:progressId];
    NSString *path = [NSString stringWithFormat:@"surveys/%@/failure", identifier];
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:path parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        (void)response;
        success();
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}
+ (void)actionButtonClicked:(ICMRecoveredSurvey *)survey stepId:(NSString *)stepId success:(void (^)(void))success error:(void (^)(NSError *))failure {
    NSString *action = @"mark survey action button as clicked";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"survey_progress_id"] = [NSNumber numberWithInteger:survey.progressId];
    parameters[@"step_id"] = stepId;
    NSString *path = [NSString stringWithFormat:@"surveys/%@/action_button_clicked", survey.id];
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:path parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        (void)response;
        success();
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}
+ (void)getSurvey:(NSString *)identifier success:(void (^)(NSDictionary * _Nonnull))success error:(void (^)(NSError *))failure {
    NSString *action = @"get survey";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    NSString *path = [NSString stringWithFormat:@"surveys/%@/fetch", identifier];
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:path parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        success(response[@"survey"]);
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}
@end
