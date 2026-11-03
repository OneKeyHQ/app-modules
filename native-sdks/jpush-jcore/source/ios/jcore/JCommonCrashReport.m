#import "JCommonCrashReport.h"
#import "JCORELog.h"
#import "JCOREThread.h"
#import "JCommonCrashController.h"
#import "JCommonCrashManager.h"
static JCommonCrashReport *_jcommonCrashReport;
@implementation JCommonCrashReport
+ (instancetype)sharedService {
    static dispatch_once_t once;
    dispatch_once(&once, ^{
      if (!_jcommonCrashReport)
          _jcommonCrashReport = [[JCommonCrashReport alloc] init];
    });
    return _jcommonCrashReport;
}
+ (instancetype)allocWithZone:(NSZone *)zone {
    static dispatch_once_t once;
    dispatch_once(&once, ^{
      if (!_jcommonCrashReport)
          _jcommonCrashReport = [super allocWithZone:zone];
    });
    return _jcommonCrashReport;
}
- (instancetype)init {
    self = [super init];
    if (self) {
        _crashController = [JCommonCrashController new];
        _crashManager = [JCommonCrashManager new];
    }
    return self;
}
+ (void)setupService {
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/JCommonCrashReport.m"
         function:__PRETTY_FUNCTION__
             line:61
              tag:nil
           format:@"JCommonCrashReport setupService"];
    [JCOREThread performSel:@selector(setup) target:[JCommonCrashReport sharedService] withObject:nil];
}
- (void)setup {
    [self startCrashReport];
}
- (void)startCrashReport {
    [_crashController startCrashCheckStrategy];
    [_crashManager startSentryReport];
    [_crashManager startCrashCheckStrategy];
}
+ (void)startSentryLogReport {
    [[JCommonCrashReport sharedService] startSentryLogReport];
}
- (void)startSentryLogReport {
    [_crashManager startSentryLogRecord];
}
+ (void)startSentryCrashCountReport {
    [[JCommonCrashReport sharedService] startSentryCrashCountReport];
}
- (void)startSentryCrashCountReport {
    [_crashManager setCrashCountSwitchOn:YES];
}
@end
