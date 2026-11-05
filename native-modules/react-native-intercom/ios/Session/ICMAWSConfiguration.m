// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
#import "ICMAWSConfiguration.h"

@implementation ICMAWSConfiguration
@synthesize uploadId = _uploadId, key = _key, acl = _acl, contentType = _contentType;
@synthesize AWSAccessKeyId = _AWSAccessKeyId, policy = _policy, signature = _signature;
@synthesize successActionStatus = _successActionStatus, uploadDestination = _uploadDestination;
@synthesize publicURL = _publicURL, safeAppId = _safeAppId;

+ (instancetype)instanceFromDictionary:(id)dictionary {
    ICMAWSConfiguration *configuration = [ICMAWSConfiguration new];
    [configuration setAttributesFromDictionary:dictionary];
    return configuration;
}
- (void)setAttributesFromDictionary:(id)dictionary {
    if ([dictionary isKindOfClass:[NSDictionary class]]) [self setValuesForKeysWithDictionary:dictionary];
}
- (void)setValue:(id)value forUndefinedKey:(NSString *)name {
    if ([name isEqualToString:@"id"] && [value isKindOfClass:[NSNumber class]]) {
        [self setValue:value forKey:@"uploadId"];
    } else if ([name isEqualToString:@"content_type"]) {
        [self setValue:value forKey:@"contentType"];
    } else if ([name isEqualToString:@"aws_access_key"]) {
        [self setValue:value forKey:@"AWSAccessKeyId"];
    } else if ([name isEqualToString:@"success_action_status"]) {
        [self setValue:value forKey:@"successActionStatus"];
    } else if ([name isEqualToString:@"upload_destination"]) {
        [self setValue:value forKey:@"uploadDestination"];
    } else if ([name isEqualToString:@"public_url"]) {
        [self setValue:value forKey:@"publicURL"];
    } else if ([name isEqualToString:@"metadata"] && [value isKindOfClass:[NSDictionary class]]) {
        if (value[@"safe_app_id"] != nil) [self setValue:value[@"safe_app_id"] forKey:@"safeAppId"];
    }
}
- (NSDictionary *)postParametersDictionary {
    NSMutableDictionary *parameters = [NSMutableDictionary dictionary];
    if (self.key != nil) parameters[@"key"] = self.key;
    if (self.acl != nil) parameters[@"acl"] = self.acl;
    if (self.contentType != nil) parameters[@"Content-Type"] = self.contentType;
    if (self.AWSAccessKeyId != nil) parameters[@"AWSAccessKeyId"] = self.AWSAccessKeyId;
    if (self.policy != nil) parameters[@"policy"] = self.policy;
    if (self.signature != nil) parameters[@"signature"] = self.signature;
    if (self.successActionStatus != nil) parameters[@"success_action_status"] = self.successActionStatus;
    if (self.safeAppId != nil) parameters[@"x-amz-meta-safe_app_id"] = self.safeAppId;
    return parameters;
}
@end
