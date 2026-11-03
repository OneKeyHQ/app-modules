// Preserve the original legacy archive/version API behavior.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
#import "JCommonCrashManager.h"
#import "JCOREFCCache.h"
#import "JCOREIntegrate.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JCommonReportManager.h"
#import "JCommonReportTimerManager.h"
#include <execinfo.h>
#include <signal.h>
#import "JCommonCrashSession.h"
#import "JCommonCrashEvent.h"
#import "JCommonCrashStacktrace.h"
#import "JCommonCrashBacktrace.h"
#define CrashManagerFile                                                                                     \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/CrashLib/JCommonCrashManager.m"
volatile int32_t exceptionCount __attribute__((section("__DATA,__common")));
NSException *_isException __attribute__((section("__DATA,__common")));
static NSMutableArray *_reports;
static BOOL _crashLogReportSwitch;
static BOOL _crashCountSwitch;
static NSUncaughtExceptionHandler *JCommonExceptionHandler;
static void (*JCommonPreviousSignalHandler)(int);
static void creatCrashEvent(int signalNumber, NSException *exception);
static void JCommonExceptionHandle_Sentry(NSException *exception) {
    if (@available(iOS 8.0, *)) {
        if (![NSThread.currentThread.name containsString:@"jiguang"] &&
            ![NSThread.currentThread.name containsString:@"hxhg"]) {
            JCommonUnInstallExceptionHandler(exception);
            return;
        }
    }
    creatCrashEvent(999, exception);
}
static void JCommonSignalHandler_Sentry(int signalNumber) {
    if (@available(iOS 8.0, *)) {
        if (![NSThread.currentThread.name containsString:@"jiguang"] &&
            ![NSThread.currentThread.name containsString:@"hxhg"]) {
            JCommonUnInstallExceptionHandler(nil);
            return;
        }
    }
    creatCrashEvent(signalNumber, nil);
}
void JCommonInstallExceptionHandler(void) {
    JCommonExceptionHandler = NSGetUncaughtExceptionHandler();
    NSSetUncaughtExceptionHandler(JCommonExceptionHandle_Sentry);
    JCommonPreviousSignalHandler = signal(SIGABRT, SIG_IGN);
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:CrashManagerFile
         function:"void JCommonInstallExceptionHandler(void)"
             line:453
              tag:nil
           format:@"%s exist defined previous signalHandler %p", "JCommonInstallExceptionHandler",
                  JCommonPreviousSignalHandler];
    signal(SIGABRT, JCommonSignalHandler_Sentry);
    signal(SIGILL, JCommonSignalHandler_Sentry);
    signal(SIGSEGV, JCommonSignalHandler_Sentry);
    signal(SIGFPE, JCommonSignalHandler_Sentry);
    signal(SIGBUS, JCommonSignalHandler_Sentry);
    signal(SIGPIPE, JCommonSignalHandler_Sentry);
}
void JCommonUnInstallExceptionHandler(NSException *exception) {
    const char *function = "void JCommonUnInstallExceptionHandler(NSException * _Nullable __strong)";
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:CrashManagerFile
         function:function
             line:464
              tag:nil
           format:@"%s", "JCommonUnInstallExceptionHandler"];
    if (JCommonExceptionHandler) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:CrashManagerFile
             function:function
                 line:466
                  tag:nil
               format:@"%s %p", "JCommonUnInstallExceptionHandler", JCommonExceptionHandler];
        if (exception)
            JCommonExceptionHandler(exception);
        NSSetUncaughtExceptionHandler(JCommonExceptionHandler);
    }
    void (*previous)(int) = signal(SIGSEGV, SIG_IGN);
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:CrashManagerFile
         function:function
             line:477
              tag:nil
           format:@"%s previous signalHandler %p", "JCommonUnInstallExceptionHandler", previous];
    void (*restore)(int) = previous ? JCommonPreviousSignalHandler : SIG_DFL;
    signal(SIGABRT, restore);
    signal(SIGILL, restore);
    signal(SIGSEGV, restore);
    signal(SIGFPE, restore);
    signal(SIGBUS, restore);
    signal(SIGPIPE, restore);
}
static void reportEventOrSession(NSDictionary *event, NSDictionary *session, NSString *eventId,
                                 BOOL includeEvent) {
    NSInteger now = (NSInteger)[JCOREUtilities currentTimestamp];
    NSDictionary *content = nil;
    if (includeEvent && event && session) {
        NSDictionary *header =
            @{@"event_id" : eventId ?: [NSNull null], @"sdks" : [JCOREIntegrate integrateInfo]};
        NSDictionary *eventItem = @{
            @"sentry_envelope_item_header" : @{@"content_type" : @"application/json", @"type" : @"event"},
            @"sentry_envelope_item" : event
        };
        NSDictionary *sessionItem = @{
            @"sentry_envelope_item_header" : @{@"content_type" : @"application/json", @"type" : @"session"},
            @"sentry_envelope_item" : session
        };
        content = @{
            @"type" : @"sentry_crash_log",
            @"itime" : @(now),
            @"sentry_envelope_header" : header,
            @"sentry_envelope_items" : @[ eventItem, sessionItem ]
        };
    } else if (session) {
        NSDictionary *header =
            @{@"event_id" : eventId ?: [NSNull null], @"sdks" : [JCOREIntegrate integrateInfo]};
        NSDictionary *item = @{
            @"sentry_envelope_item_header" : @{@"content_type" : @"application/json", @"type" : @"session"},
            @"sentry_envelope_item" : session
        };
        content = @{
            @"type" : @"sentry_crash_log",
            @"itime" : @(now),
            @"sentry_envelope_header" : header,
            @"sentry_envelope_items" : @[ item ]
        };
    }
    NSError *error = nil;
    NSData *data = [NSJSONSerialization dataWithJSONObject:content options:0 error:&error];
    if (!error)
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:CrashManagerFile
             function:__PRETTY_FUNCTION__
                 line:191
                  tag:nil
               format:@"crashInfoData encode success"];
    else
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:CrashManagerFile
             function:__PRETTY_FUNCTION__
                 line:193
                  tag:nil
               format:@"crashInfoData encode failed:%@", error.description];
    if (!_reports)
        _reports = [NSMutableArray
            arrayWithArray:[NSKeyedUnarchiver
                               unarchiveObjectWithData:[JCOREFCCache
                                                           objectForFile:@"__JCOMMONSENTRYCrashLog"]]];
    [_reports addObject:data];
    NSData *archive = [NSKeyedArchiver archivedDataWithRootObject:_reports];
    [JCOREFCCache setObject:archive forFile:@"__JCOMMONSENTRYCrashLog"];
}
static void creatCrashEvent(int signalNumber, NSException *exception) {
    if (_isException) {
        JCommonUnInstallExceptionHandler(exception);
        return;
    }
    if (_crashCountSwitch) {
        id old = [JCOREFCCache objectForKey:@"__JCOMMONSENTRYCrashCountLog"];
        if (old && ![old isKindOfClass:[NSArray class]])
            [JCOREFCCache setObject:nil forKey:@"__JCOMMONSENTRYCrashCountLog"];
        double now = [JCOREUtilities currentTimestamp];
        NSMutableArray *counts = [NSMutableArray array];
        if (old && [old count])
            [counts addObjectsFromArray:old];
        if (!counts.count) {
            [counts addObject:@{
                @"moniter_type" : @"EX_CRASH",
                @"uuid" : [NSUUID UUID].UUIDString ?: @"",
                @"moniter_id" : [NSUUID UUID].UUIDString ?: @"",
                @"timestamp" : @((NSInteger)(now * 1000)),
                @"extra" : @{@"sdk_type" : @"JCore", @"count" : @1}
            }];
        } else {
            id last = [old lastObject];
            id extra = [last valueForKey:@"extra"];
            if ([last isKindOfClass:[NSDictionary class]] && [extra isKindOfClass:[NSDictionary class]]) {
                id count = [extra valueForKey:@"count"], uuid = [last valueForKey:@"uuid"],
                   monitor = [last valueForKey:@"moniter_id"], timestamp = [last valueForKey:@"timestamp"];
                if (timestamp && [timestamp isKindOfClass:[NSNumber class]] && count &&
                    [count isKindOfClass:[NSNumber class]]) {
                    NSString *time =
                        [JCOREUtilities converTimestampToString:(long long)([timestamp doubleValue] / 1000)];
                    NSDate *date = [JCOREUtilities transformStrToDate:time];
                    NSDictionary *entry;
                    if ([[NSCalendar currentCalendar] isDateInToday:date]) {
                        entry = @{
                            @"moniter_type" : @"EX_CRASH",
                            @"uuid" : uuid ?: @"",
                            @"moniter_id" : monitor ?: @"",
                            @"timestamp" : timestamp,
                            @"extra" : @{@"sdk_type" : @"JCore", @"count" : @([count integerValue] + 1)}
                        };
                        [counts removeLastObject];
                    } else
                        entry = @{
                            @"moniter_type" : @"EX_CRASH",
                            @"uuid" : [NSUUID UUID].UUIDString ?: @"",
                            @"moniter_id" : [NSUUID UUID].UUIDString ?: @"",
                            @"timestamp" : @(now * 1000),
                            @"extra" : @{@"sdk_type" : @"JCore", @"count" : @1}
                        };
                    [counts addObject:entry];
                }
            }
        }
        [JCOREFCCache setObject:counts forKey:@"__JCOMMONSENTRYCrashCountLog"];
    }
    if (!_crashLogReportSwitch) {
        JCommonUnInstallExceptionHandler(exception);
        return;
    }
    JCommonCrashSession *session = [[JCommonCrashSession alloc]
        initWithReleaseName:[NSString stringWithFormat:@"%@@%ld+%@", [JCOREUtilities bundleID],
                                                       (long)[JCOREUtilities version],
                                                       [JCOREUtilities appBuild]]];
    [session setStatus:2];
    [session setTimestamp:[NSDate date]];
    [session setStarted:[JCOREUtilities appStartTimeDate]];
    [session setErrors:1];
    if (exception)
        _isException = exception;
    NSDictionary *signals = @{
        @4 : @[ @2, @"EXC_BAD_INSTRUCTION" ],
        @5 : @[ @6, @"EXC_BREAKPOINT" ],
        @6 : @[ @10, @"EXC_CRASH" ],
        @7 : @[ @4, @"EXC_EMULATION" ],
        @8 : @[ @3, @"EXC_ARITHMETIC" ],
        @9 : @[ @65539, @"EXC_SOFT_SIGNAL" ],
        @10 : @[ @1, @"EXC_BAD_ACCESS" ],
        @11 : @[ @1, @"EXC_BAD_ACCESS" ],
        @12 : @[ @65536, @"EXC_UNIX_BAD_SYSCALL" ],
        @13 : @[ @65537, @"EXC_UNIX_BAD_PIPE" ]
    };
    NSArray *signalInfo = signals[@(signalNumber)] ?: @[ @0, @"0" ];
    id type = signalInfo[1];
    if (signalNumber == 999)
        type = exception.name;
    JCommonCrashEvent *event = [JCommonCrashEvent new];
    [event setType:@"sentry_crash_log"];
    NSDate *started = [JCOREUtilities appStartTimeDate];
    NSDateFormatter *formatter = [NSDateFormatter new];
    formatter.locale = [NSLocale localeWithLocaleIdentifier:@"en_US_POSIX"];
    formatter.timeZone = [NSTimeZone timeZoneWithAbbreviation:@"UTC"];
    formatter.dateFormat = @"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    [event setStartTimestamp:[formatter stringFromDate:started]];
    [event setPlatform:@"cocoa"];
    [event
        setReleaseName:[NSString stringWithFormat:@"%@@%ld+%@", [JCOREUtilities bundleID],
                                                  (long)[JCOREUtilities version], [JCOREUtilities appBuild]]];
    JCommonCrashContexts *contexts = [JCommonCrashContexts new];
    [contexts setApp:@{
        @"app_start_time" : [JCOREUtilities appStartTime] ?: [NSNull null],
        @"build_type" : [JCOREUtilities amIbeginDeugged] ? @"debug" : @"release",
        @"app_identifier" : [JCOREUtilities bundleID] ?: [NSNull null],
        @"app_name" : [JCOREUtilities appName] ?: [NSNull null],
        @"app_version" : [JCOREUtilities appVersion] ?: [NSNull null],
        @"app_id" : [JCOREUtilities appUUID] ?: [NSNull null],
        @"app_build" : [JCOREUtilities appBuild] ?: [NSNull null],
        @"type" : @"type"
    }];
    [contexts setDevice:@{
        @"family" : [JCOREUtilities systemName] ?: [NSNull null],
        @"model" : [JCOREUtilities model] ?: [NSNull null],
        @"model_id" : [JCOREUtilities model_id] ?: [NSNull null],
        @"memory_size" : @([[JCOREUtilities memory_size] longLongValue]),
        @"free_memory" : @([[JCOREUtilities free_memory] longLongValue]),
        @"usable_memory" : @([[JCOREUtilities usable_memory] longLongValue]),
        @"storage_size" : @([[JCOREUtilities storage_size] longLongValue]),
        @"boot_time" : [JCOREUtilities bootTime] ?: [NSNull null],
        @"timezone" : [JCOREUtilities timezone] ?: [NSNull null],
        @"type" : @"device"
    }];
    [contexts setOs:@{
        @"name" : @"iOS",
        @"version" : [JCOREUtilities systemVersion] ?: [NSNull null],
        @"build" : [JCOREUtilities osVersion] ?: [NSNull null],
        @"kernel_version" : [JCOREUtilities kernelVersion] ?: [NSNull null],
        @"rooted" : @([JCOREUtilities isJailbroken]),
        @"type" : @"os"
    }];
    [event setContext:[contexts serialize]];
    [event setUser:@{@"id" : [JCOREUtilities appName] ?: [NSNull null]}];
    [event setSdk:@{@"name" : @"sentry.cocoa", @"version" : @"7.1.4"}];
    JCommonCrashException *detail = [JCommonCrashException new];
    [detail setType:type];
    [detail setThreadId:@([JCommonCrashBacktrace getThreadId:[NSThread currentThread]])];
    JCommonCrashStacktrace *stacktrace = [JCommonCrashStacktrace new];
    id frames = [JCommonCrashBacktrace JCommon_backtraceOfCurrentThread];
    if (!frames)
        frames = [NSMutableArray array];
    if (!exception) {
        void *raw[128];
        int count = backtrace(raw, 128);
        char **symbols = backtrace_symbols(raw, count);
        NSMutableArray *stack = [NSMutableArray arrayWithCapacity:(NSUInteger)count];
        for (int i = 0; i < count; i++)
            [stack addObject:[NSString stringWithUTF8String:symbols[i]]];
        free(symbols);
        id extra = [JCommonCrashBacktrace JCommon_CallStackFromException:stack];
        frames = [frames arrayByAddingObjectsFromArray:extra];
        if ((unsigned int)(signalNumber - 1) < 3) {
            NSArray *formats = @[
                @"Attempted to dereference garbage pointer %@",
                @"This is usually caused by an application trying to access a memory space that does not exist %@",
                @"Arithmetic exception %@"
            ];
            type = [NSString stringWithFormat:formats[(NSUInteger)(signalNumber - 1)], type];
        }
        [detail setValue:type];
    } else {
        [detail setValue:exception.reason];
        id extra = [JCommonCrashBacktrace JCommon_CallStackFromException:exception.callStackSymbols];
        frames = [frames arrayByAddingObjectsFromArray:extra];
    }
    [stacktrace setFrames:frames];
    [stacktrace fixDuplicateFrames];
    [detail setStacktrace:stacktrace];
    [event setExceptions:@[ detail ]];
    [event setStacktrace:stacktrace];
    [event setThreads:[JCommonCrashBacktrace JCommon_backtraceOfAllThread]];
    id serializedEvent = [event serialize], serializedSession = [session serialize];
    id eventId = [[JCommonCrashId shareInstance] sentryIdString];
    reportEventOrSession(serializedEvent, serializedSession, eventId, YES);
    JCommonUnInstallExceptionHandler(exception);
}
@implementation JCommonCrashManager
- (id)backtrace {
    void *raw[128];
    int count = backtrace(raw, 128);
    char **symbols = backtrace_symbols(raw, count);
    NSMutableArray *trace = [NSMutableArray arrayWithCapacity:(NSUInteger)MAX(count, 0)];
    // Approved safety deviation: original always indexed symbols[4..8], even on short stacks or NULL.
    // Defined inputs retain the same five frames; undefined paths return the available suffix or empty array.
    if (symbols)
        for (int i = 4; i < count && i < 9; i++)
            [trace addObject:[NSString stringWithUTF8String:symbols[i]]];
    free(symbols);
    return trace;
}
- (void)dealloc {
    [_reports removeAllObjects];
    _reports = nil;
    _crashLogReportSwitch = NO;
    _crashCountSwitch = NO;
}
- (void)setCrashCountSwitchOn:(BOOL)enabled {
    _crashCountSwitch = enabled;
}
- (void)startSentryLogRecord {
    _crashLogReportSwitch = YES;
    JCommonCrashSession *session = [[JCommonCrashSession alloc]
        initWithReleaseName:[NSString stringWithFormat:@"%@@%ld+%@", [JCOREUtilities bundleID],
                                                       (long)[JCOREUtilities version],
                                                       [JCOREUtilities appBuild]]];
    [session setStatus:0];
    [session setTimestamp:[NSDate date]];
    [session setStarted:[JCOREUtilities appStartTimeDate]];
    id content = [session serialize];
    id eventId = [[JCommonCrashId shareInstance] sentryIdString];
    reportEventOrSession(nil, content, eventId, NO);
}
+ (id)reportContent {
    if (!_reports)
        _reports = [NSMutableArray
            arrayWithArray:[NSKeyedUnarchiver
                               unarchiveObjectWithData:[JCOREFCCache
                                                           objectForFile:@"__JCOMMONSENTRYCrashLog"]]];
    if (![_reports isKindOfClass:[NSMutableArray class]] || !_reports || !_reports.count)
        return nil;
    id data = _reports[0];
    NSError *error = nil;
    id content = [NSJSONSerialization JSONObjectWithData:data options:0 error:&error];
    if (error || !content || ![content isKindOfClass:[NSDictionary class]])
        return nil;
    [JCORELog log:YES
            level:0x3ff
             flag:8
          context:0
             file:CrashManagerFile
         function:__PRETTY_FUNCTION__
             line:578
              tag:nil
           format:@"sentry crashReport content %@", content];
    return content;
}
- (void)startSentryReport {
    [JCommonCrashManager reportCrash];
}
+ (void)reportCrashCount {
    id counts = [JCOREFCCache objectForKey:@"__JCOMMONSENTRYCrashCountLog"];
    if (!counts || ![counts isKindOfClass:[NSArray class]] || ![counts count])
        return;
    NSDictionary *content = @{
        @"type" : @"sdk_moniter",
        @"itime" : @((NSInteger)[JCOREUtilities currentTimestamp]),
        @"data" : counts
    };
    [JCommonReportManager sendAppCrashCountReport:content
                                completionHandler:^(id response, NSError *error) {
                                  if (!error)
                                      [JCOREFCCache setObject:nil forKey:@"__JCOMMONSENTRYCrashCountLog"];
                                }];
}
+ (void)reportCrash {
    id content = [JCommonCrashManager reportContent];
    if (!content)
        return;
    [JCommonReportManager sendAppCrashReport:content
                           completionHandler:^(id response, NSError *error) {
                             if (!error) {
                                 [JCORELog log:YES
                                         level:0x3ff
                                          flag:0x100
                                       context:0
                                          file:CrashManagerFile
                                      function:__PRETTY_FUNCTION__
                                          line:612
                                           tag:nil
                                        format:@"sendAppCrashReport success"];
                                 if (_reports.count) {
                                     [_reports removeObjectAtIndex:0];
                                     [JCommonCrashManager reportCrash];
                                     if (_reports.count == 1)
                                         [JCOREFCCache removeObjectForFile:@"__JCOMMONSENTRYCrashLog"];
                                 }
                             } else if (_reports) {
                                 NSData *data = [NSKeyedArchiver archivedDataWithRootObject:_reports];
                                 [JCOREFCCache setObject:data forFile:@"__JCOMMONSENTRYCrashLog"];
                             }
                           }];
}
- (void)startCrashCheckStrategy {
    [self startTimer];
}
- (void)stopCrashCheckStrategy {
    [self stopTimer];
}
- (void)startTimer {
    if (!_timerManager) {
        _timerManager = [JCommonReportTimerManager manager];
        _timerManager.timerFuncBlock = ^{
          [JCommonCrashManager reportCrashCount];
        };
        _timerManager.frequency = 86400;
        _timerManager.saveKey = @"__JCOMMONCrashCountTimer";
        _timerManager.firstLoadReport = NO;
    }
    [_timerManager startTimerCheck];
}
- (void)stopTimer {
    [_timerManager stopTimerCheck];
}
@end

#pragma clang diagnostic pop
