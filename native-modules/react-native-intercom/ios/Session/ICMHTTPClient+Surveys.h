#import "ICMHTTPClient.h"
#import "ICMBaseResponseDependencies.h"

NS_ASSUME_NONNULL_BEGIN
@interface ICMHTTPClient (Surveys)
+ (void)getSurveyWithInstanceId:(NSString * _Null_unspecified)identifier success:(void (^)(NSDictionary * _Nonnull))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
+ (void)submitSurvey:(ICMRecoveredSurvey * _Null_unspecified)survey success:(void (^)(NSDictionary * _Nonnull))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
+ (void)dismissSurvey:(ICMRecoveredSurvey * _Null_unspecified)survey success:(void (^)(void))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
+ (void)failSurvey:(NSString * _Null_unspecified)identifier progressId:(NSInteger)progressId success:(void (^)(void))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
+ (void)actionButtonClicked:(ICMRecoveredSurvey * _Null_unspecified)survey stepId:(NSString * _Null_unspecified)stepId success:(void (^)(void))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
+ (void)getSurvey:(NSString * _Null_unspecified)identifier success:(void (^)(NSDictionary * _Nonnull))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
@end
NS_ASSUME_NONNULL_END
