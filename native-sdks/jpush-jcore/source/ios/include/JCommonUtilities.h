#pragma once
#import <Foundation/Foundation.h>
typedef NSString *const JCOMMON_DATA_TYPE;
FOUNDATION_EXPORT JCOMMON_DATA_TYPE JCOMMON_ADVERTISING_ID;
@interface JCommonUtilities : NSObject
+ (id)objectForKey:(id)key;
+ (NSString *)advertisingId;
+ (BOOL)hasIdfaCache;
+ (BOOL)idfaCacheExpired;
+ (NSString *)getIdfa;
+ (BOOL)judgeIdfaAuthorized;
+ (NSString *)getIDFAWithReflection;
+ (unsigned long long)attAuthStatus;
+ (BOOL)checkFormatOfAllZeroIDFA:(id)identifier;
@end
