#pragma once
#import "JCOREPropertyModels.h"

@interface JCOREIntegrate : NSObject
- (id)init;
- (NSArray *)newIntegrateList;
- (id<JCOREIntegrateProtocol>)findInfoOfProperty:(unsigned char)arg1;
- (id<JCOREIntegrateProtocol>)findInfoOfIndex:(long long)arg1;
- (void)cacheOriginIntegrateMark;
- (void)cacheOriginIntegrateVersion;
- (id)integrateVersion;
- (void)updateIntegrateMarkWith:(unsigned char)arg1;
- (id)integrateMarkList;
- (void)registerSDKWithClass:(NSString *)arg1
                     ofIndex:(long long)arg2
                    property:(unsigned char)arg3
                        type:(long long)arg4;
- (id)getSDKWithClassName:(id)arg1;
- (id)toDisplaySDKVersion:(id)arg1;
- (unsigned int)toDecimal:(id)arg1;
+ (void)registerIntegrateInfo:(id)arg1;
+ (void)setAutoExtend:(BOOL)arg1;
+ (BOOL)autoExtend;
+ (instancetype)shared;
+ (NSArray *)newIntegrateList;
+ (id)integrateList;
+ (BOOL)hasMultiBusiness;
+ (BOOL)jpushOnly;
+ (id)sdkVersionOfIndex:(long long)arg1;
+ (BOOL)sdkIsExistOfIndex:(long long)arg1;
+ (void)updateIntegrateMarkWith:(unsigned char)arg1;
+ (id)integrateMarkList;
+ (void)cacheOriginIntegrateMark;
+ (void)cacheOriginIntegrateVersion;
+ (id)toDisplaySDKVersion:(id)arg1;
+ (unsigned int)toDecimal:(id)arg1;
+ (id)displayVersionOfIndex:(long long)arg1;
+ (unsigned int)intVersionOfIndex:(long long)arg1;
+ (long long)typeOfIndex:(long long)arg1;
+ (id)jcoreDisplayVersion;
+ (short)jcoreBuildVersion;
+ (unsigned int)jcoreIntVersion;
+ (long long)jcoreSdkIndex;
+ (id)isHasCustomSDK;
+ (unsigned int)integrateBusiness;
+ (unsigned int)integrateBusinessForLogin;
+ (id)sdkTypes;
+ (int)calculateSdkType:(int)arg1 custom:(int)arg2 dynamic:(int)arg3;
+ (id)integrateInfo;
+ (BOOL)tcpKeepAlive;
+ (BOOL)needUploadSDKVersion;
+ (void)initSdk:(long long)arg1;
+ (BOOL)isSdkInit:(long long)arg1;
@end
