#import "JCOREUtilities.h"
#import "JPUSHNewReport.h"

#import "JCORENetChannel.h"
@interface JPUSHNewReport () {
    BOOL accountIDRequired;
    BOOL cacheRequired;
    unsigned char controlFlag;
    RecoveryOpaqueCallback completionHandler;
    id requestInfo;
    NSMutableURLRequest *request;
    NSString *reportFlag;
    long long _reportTimeStamp;
}
@end
@implementation JPUSHNewReport
@dynamic hash, superclass, description, debugDescription;
@synthesize reportTimeStamp = _reportTimeStamp, request = request, requestInfo = requestInfo,
            accountIDRequired = accountIDRequired, cacheRequired = cacheRequired, reportFlag = reportFlag,
            controlFlag = controlFlag, completionHandler = completionHandler;
@dynamic addressList;
- (instancetype)init {
    if ((self = [super init])) {
        self.reportTimeStamp = (long long)[JCOREUtilities currentTimestamp];
        self.cacheRequired = YES;
        self.accountIDRequired = YES;
    }
    return self;
}
- (void)send {
    [JCORENetChannel send:self];
}
- (void)sendRequest:(RecoveryOpaqueCallback)completion {
    if (completion)
        self.completionHandler = completion;
    [JCORENetChannel send:self];
}
@end
