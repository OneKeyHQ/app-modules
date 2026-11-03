#pragma once
#import <Foundation/Foundation.h>

@interface JCORETestAddressCache : NSObject {
    NSArray *_sisHostArray;
    NSArray *_sisIpArray;
    NSArray *_reportSisAddress;
    NSString *_defaultConHost;
    NSString *_defaultConnSrv;
    NSString *_defaultSisSrv;
    NSArray *_defaultReportAddress;
    NSArray *_defaultUserReportAddress;
    NSArray *_defaultWhiteListArray;
}
@property (copy, nonatomic) NSArray *sisHostArray;
@property (copy, nonatomic) NSArray *sisIpArray;
@property (copy, nonatomic) NSArray *reportSisAddress;
@property (copy, nonatomic) NSString *defaultConHost;
@property (copy, nonatomic) NSString *defaultConnSrv;
@property (copy, nonatomic) NSString *defaultSisSrv;
@property (copy, nonatomic) NSArray *defaultReportAddress;
@property (copy, nonatomic) NSArray *defaultUserReportAddress;
@property (copy, nonatomic) NSArray *defaultWhiteListArray;
@property (copy, nonatomic) NSString *defaultLastSucceedConnAddress;
+ (instancetype)shared;
+ (BOOL)isDebugMode;
@end
