#pragma once
#import <Foundation/Foundation.h>
@interface JCORECertController : NSObject
+ (void)pullCertWithServerVersion:(id)version;
+ (void)pullCert:(id)version
         timeout:(double)timeout
      retryCount:(long long)retryCount
      completion:(void (^)(BOOL))completion;
+ (id)getCert;
+ (id)getCertVersion;
+ (BOOL)certReady;
@end
