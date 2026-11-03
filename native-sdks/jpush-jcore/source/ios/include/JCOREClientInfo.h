#pragma once
#import <Foundation/Foundation.h>

@interface JCOREClientInfo : NSObject
+ (id)appKey;
+ (void)setAppKey:(id)arg1;
+ (id)channel;
+ (void)setChannel:(id)arg1;
+ (id)advertisingId;
+ (void)setAdvertisingId:(id)arg1;
+ (BOOL)shouldUploadAdvertisingId;
+ (void)setShouldUploadAdvertisingId:(BOOL)arg1;
+ (BOOL)isProduction;
+ (void)setIsProduction:(BOOL)arg1;
+ (long long)apsType;
+ (long long)buildType;
+ (id)password;
+ (void)setPassword:(id)arg1;
+ (unsigned long long)uid;
+ (void)setUid:(unsigned long long)arg1;
+ (id)registrationID;
+ (void)setRegistrationID:(id)arg1;
+ (unsigned short)rid;
+ (void)setSessionID:(unsigned int)arg1;
+ (unsigned int)sessionID;
+ (id)registrationCurrentInfo:(BOOL)arg1;
+ (id)registrationInfo;
+ (void)setRegistrationInfo:(id)arg1;
+ (id)accountID;
+ (void)setAccountID:(id)arg1;
+ (id)deviceToken;
+ (void)setDeviceToken:(id)arg1;
+ (id)voipToken;
+ (void)setVoipToken:(id)arg1;
+ (id)uploadedDeviceToken;
+ (void)setUploadedDeviceToken:(id)arg1;
+ (void)setDeviceTokenUpdated:(BOOL)arg1;
+ (BOOL)deviceTokenUpdated;
+ (void)setUploadedVoipToken:(id)arg1;
+ (id)uploadedVoipToken;
+ (void)setVoipTokenUpdated:(BOOL)arg1;
+ (BOOL)voipTokenUpdated;
+ (id)cuid;
+ (void)setCuid:(id)arg1;
+ (id)orgId;
+ (void)setOrgId:(id)arg1;
+ (id)dataSourceId;
+ (void)setDataSourceId:(id)arg1;
+ (void)setIMFlag:(unsigned char)arg1;
+ (unsigned char)IMFlag;
@end
