#import "JCommonCrashLog.h"
#import "JCORECacheChannel.h"
#import "JCOREFCCache.h"
#import "JCORELog.h"
#import "JCORENetUtilities.h"
#import "JCOREUtilities.h"
#import "JCommonCrashController.h"
#include <execinfo.h>
#include <signal.h>
#include <unistd.h>
#define CrashLogFile                                                                                         \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/Controller/"                        \
    "JCommonCrashController.m"
static NSUncaughtExceptionHandler *JCommonUncaughtExceptionHandler;
static NSString *JCommonClearMemoryAddress(NSArray *trace) {
    NSMutableString *result = [NSMutableString new];
    [trace enumerateObjectsUsingBlock:^(NSString *line, NSUInteger index, BOOL *stop) {
      if (line.length) {
          NSArray *parts = [line componentsSeparatedByString:@"0x"];
          if (parts)
              [result appendString:parts[0]];
      }
    }];
    return result;
}
static void JCommonHandleException(NSException *exception) {
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:CrashLogFile
         function:"void JCommonHandleException(NSException *__strong _Nonnull)"
             line:208
              tag:nil
           format:@"%s", "void JCommonHandleException(NSException *__strong _Nonnull)"];
    NSArray *addresses = exception.callStackSymbols;
    id trace = JCommonClearMemoryAddress([[JCommonCrashLog shared] backtrace]);
    NSMutableDictionary *info = [NSMutableDictionary dictionaryWithDictionary:exception.userInfo];
    info[@"UncaughtExceptionHandlerAddressesKey"] = addresses;
    info[@"UncaughtExceptionCallStack"] = trace;
    NSException *wrapped = [NSException exceptionWithName:exception.name
                                                   reason:exception.reason
                                                 userInfo:info];
    [[JCommonCrashLog shared] handleException:wrapped];
}
static void JCommonSignalHandler(int signalNumber) {
    NSMutableDictionary *info =
        [NSMutableDictionary dictionaryWithObject:@(signalNumber)
                                           forKey:@"UncaughtExceptionHandlerSignalKey"];
    void *frames[128];
    int count = backtrace(frames, 128);
    char **symbols = backtrace_symbols(frames, count);
    NSMutableArray *addresses = [NSMutableArray arrayWithCapacity:(NSUInteger)count];
    for (int i = 0; i < count; i++)
        [addresses addObject:[NSString stringWithUTF8String:symbols[i]]];
    free(symbols);
    info[@"UncaughtExceptionHandlerAddressesKey"] = addresses;
    info[@"UncaughtExceptionCallStack"] = JCommonClearMemoryAddress([[JCommonCrashLog shared] backtrace]);
    NSDictionary *names = @{
        @SIGABRT : @"SIGABRT",
        @SIGILL : @"SIGILL",
        @SIGSEGV : @"SIGSEGV",
        @SIGFPE : @"SIGFPE",
        @SIGBUS : @"SIGBUS",
        @SIGPIPE : @"SIGPIPE"
    };
    NSString *reason = [NSString stringWithFormat:@"Application received signal %@", names[@(signalNumber)]];
    NSException *exception = [NSException exceptionWithName:@"UncaughtExceptionHandlerSignalExceptionName"
                                                     reason:reason
                                                   userInfo:info];
    [[JCommonCrashLog shared] handleException:exception];
}
void JCommonInstallUncaughtExceptionHandler(void) {
    JCommonUncaughtExceptionHandler = NSGetUncaughtExceptionHandler();
    NSSetUncaughtExceptionHandler(JCommonHandleException);
    signal(SIGABRT, JCommonSignalHandler);
    signal(SIGILL, JCommonSignalHandler);
    signal(SIGSEGV, JCommonSignalHandler);
    signal(SIGFPE, JCommonSignalHandler);
    signal(SIGBUS, JCommonSignalHandler);
    signal(SIGPIPE, JCommonSignalHandler);
    signal(SIGTRAP, JCommonSignalHandler);
}
void JCommonUnInstallUncaughtExceptionHandler(void) {
    NSSetUncaughtExceptionHandler(JCommonUncaughtExceptionHandler);
    signal(SIGABRT, SIG_DFL);
    signal(SIGILL, SIG_DFL);
    signal(SIGSEGV, SIG_DFL);
    signal(SIGFPE, SIG_DFL);
    signal(SIGBUS, SIG_DFL);
    signal(SIGPIPE, SIG_DFL);
    signal(SIGTRAP, SIG_DFL);
}
@implementation JCommonCrashLog
+ (instancetype)shared {
    static JCommonCrashLog *controller;
    static dispatch_once_t once;
    dispatch_once(&once, ^{
      controller = [JCommonCrashLog new];
    });
    return controller;
}
- (id)backtrace {
    void *frames[128];
    int count = backtrace(frames, 128);
    char **symbols = backtrace_symbols(frames, count);
    NSMutableArray *trace = [NSMutableArray arrayWithCapacity:(NSUInteger)MAX(count, 0)];
    // Approved safety deviation: original always indexed symbols[4..8], even on short stacks or NULL.
    // Defined inputs retain the same five frames; undefined paths return the available suffix or empty array.
    if (symbols)
        for (int i = 4; i < count && i < 9; i++)
            [trace addObject:[NSString stringWithUTF8String:symbols[i]]];
    free(symbols);
    return trace;
}
- (void)handleException:(NSException *)exception {
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:CrashLogFile
         function:__PRETTY_FUNCTION__
             line:95
              tag:nil
           format:@"%s", __PRETTY_FUNCTION__];
    __block BOOL matched = NO;
    id records = [JCORECacheChannel crashLog];
    if (![records isKindOfClass:[NSMutableArray class]])
        records = [JCOREFCCache objectForFile:@"__JCOMMONCrashLog"];
    else
        [JCORECacheChannel setCrashLog:nil];
    if (!records)
        records = [NSMutableArray array];
    __block id currentTrace = exception.userInfo[@"UncaughtExceptionCallStack"];
    id stack = exception.userInfo[@"UncaughtExceptionHandlerAddressesKey"];
    __block id message = exception.description;
    [records enumerateObjectsUsingBlock:^(id item, NSUInteger index, BOOL *stop) {
      id trace = item[1];
      NSMutableDictionary *old = [item[0] mutableCopy];
      if ([self p_compareOldExceptionTrace:trace
                          withCurrentTrace:currentTrace
                            withOldMessage:old[@"message"]
                        withCurrentMessage:message]) {
          int32_t count = (int32_t)((uint32_t)[old[@"count"] intValue] + 1);
          old[@"count"] = @(count);
          [records replaceObjectAtIndex:index withObject:@[ old, trace ]];
          matched = YES;
          *stop = YES;
      }
    }];
    if (!matched) {
        int crashTime = (int)[JCOREUtilities currentTimestamp];
        id versionName = [JCOREUtilities appVersion];
        id versionCode = [JCOREUtilities appVersion];
        NSDictionary *device = @{
            @"itime" : @([JCOREUtilities currentTimestamp]),
            @"type" : @"device_info",
            @"os_version" : [JCOREUtilities systemVersion],
            @"timezone" : [JCOREUtilities currentTimeZone],
            @"language" : [JCOREUtilities systemLanguage],
            @"resolution" : [JCOREUtilities currentResolution],
            @"model" : [JCOREUtilities modelName] ?: @""
        };
        NSDictionary *content = @{
            @"stacktrace" : stack,
            @"message" : message,
            @"count" : @1,
            @"crashtime" : @(crashTime),
            @"versionname" : versionName,
            @"versioncode" : versionCode,
            @"device_info" : device,
            @"networktype" : [self networkName:[JCORENetUtilities networkingStatus]]
        };
        [records addObject:@[ content, currentTrace ]];
    }
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:CrashLogFile
         function:__PRETTY_FUNCTION__
             line:156
              tag:nil
           format:@"%s update crash log", __PRETTY_FUNCTION__];
    [JCOREFCCache setObject:records forFile:@"__JCOMMONCrashLog"];
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:CrashLogFile
         function:__PRETTY_FUNCTION__
             line:161
              tag:nil
           format:@"%s report crash", __PRETTY_FUNCTION__];
    [JCommonCrashController reportCrash];
    JCommonUnInstallUncaughtExceptionHandler();
    if ([exception.name isEqual:@"UncaughtExceptionHandlerSignalExceptionName"])
        kill(getpid(), [exception.userInfo[@"UncaughtExceptionHandlerSignalKey"] intValue]);
    else if (JCommonUncaughtExceptionHandler)
        JCommonUncaughtExceptionHandler(exception);
}
- (id)networkName:(int)status {
    NSArray *names = @[ @"NetworkUnknown", @"NotReachable", @"ReachableViaWWAN", @"ReachableViaWiFi" ];
    return (unsigned int)(status + 1) < 4 ? names[(NSUInteger)(status + 1)] : @"";
}
- (BOOL)p_compareOldExceptionTrace:(id)oldTrace
                  withCurrentTrace:(id)currentTrace
                    withOldMessage:(id)oldMessage
                withCurrentMessage:(id)currentMessage {
    return [oldTrace isEqualToString:currentTrace] && [oldMessage isEqualToString:currentMessage];
}
@end
