#pragma once
#import "JCORETcpObject.h"
@interface JCORETcpReport : JCORETcpObject {
    unsigned int _sid;
    NSData *_bodyData;
    NSArray *_reportAddressList;
}
@property (strong, nonatomic) NSData *bodyData;
@property (nonatomic) unsigned int sid;
@property (strong, nonatomic) NSArray *reportAddressList;
- (NSData *)token;
@end
@interface JCORETcpReportController : NSObject
- (void)send:(id)report;
@end
