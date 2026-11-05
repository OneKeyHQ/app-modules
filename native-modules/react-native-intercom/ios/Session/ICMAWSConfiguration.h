#import "../Identity/ICMSafeNetworkModel.h"
#import "ICMFileUploadConfig.h"

@interface ICMAWSConfiguration : ICMSafeNetworkModel <ICMFileUploadConfig> {
    NSNumber *_uploadId;
    NSString *_key;
    NSString *_acl;
    NSString *_contentType;
    NSString *_AWSAccessKeyId;
    NSString *_policy;
    NSString *_signature;
    NSString *_successActionStatus;
    NSString *_uploadDestination;
    NSString *_publicURL;
    NSString *_safeAppId;
}
@property (nonatomic, strong) NSNumber *uploadId;
@property (nonatomic, copy) NSString *key;
@property (nonatomic, copy) NSString *acl;
@property (nonatomic, copy) NSString *contentType;
@property (nonatomic, copy) NSString *AWSAccessKeyId;
@property (nonatomic, copy) NSString *policy;
@property (nonatomic, copy) NSString *signature;
@property (nonatomic, copy) NSString *successActionStatus;
@property (nonatomic, copy) NSString *uploadDestination;
@property (nonatomic, copy) NSString *publicURL;
@property (nonatomic, copy) NSString *safeAppId;
+ (instancetype)instanceFromDictionary:(id)dictionary;
- (void)setAttributesFromDictionary:(id)dictionary;
- (NSDictionary *)postParametersDictionary;
@end
