#pragma once
#import <Foundation/Foundation.h>
FOUNDATION_EXPORT void JCommonInstallUncaughtExceptionHandler(void);
FOUNDATION_EXPORT void JCommonUnInstallUncaughtExceptionHandler(void);
@interface JCommonCrashLog : NSObject
+ (instancetype)shared;
- (id)backtrace;
- (void)handleException:(NSException *)exception;
- (id)networkName:(int)status;
- (BOOL)p_compareOldExceptionTrace:(id)oldTrace
                  withCurrentTrace:(id)currentTrace
                    withOldMessage:(id)oldMessage
                withCurrentMessage:(id)currentMessage;
@end
