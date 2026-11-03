#pragma once
#import <Foundation/Foundation.h>
#import <dispatch/dispatch.h>

FOUNDATION_EXPORT NSString *const JCORELoggerNameASL;
FOUNDATION_EXPORT NSString *const JCORELoggerNameTTY;
FOUNDATION_EXPORT NSString *const JCORELoggerNameOS;
FOUNDATION_EXPORT NSString *const JCORELoggerNameFile;

@class JCORELogMessage;
@protocol JCORELogger;

@protocol JCORELogFormatter <NSObject>
- (NSString *)formatLogMessage:(JCORELogMessage *)message;
@optional
- (void)didAddToLogger:(id<JCORELogger>)logger;
- (void)willRemoveFromLogger:(id<JCORELogger>)logger;
@end

@protocol JCORELogger <NSObject>
- (void)logMessage:(JCORELogMessage *)message;
@property (strong, nonatomic) id<JCORELogFormatter> logFormatter;
@optional
@property (readonly, nonatomic) dispatch_queue_t loggerQueue;
@property (readonly, nonatomic) NSString *loggerName;
@end

@interface JCORELoggerNode : NSObject {
@package
    id<JCORELogger> _logger;
    NSUInteger _level;
    dispatch_queue_t _loggerQueue;
}
@property (readonly, nonatomic) id<JCORELogger> logger;
@property (readonly, nonatomic) NSUInteger level;
@property (readonly, nonatomic) dispatch_queue_t loggerQueue;
- (instancetype)initWithLogger:(id<JCORELogger>)logger loggerQueue:(dispatch_queue_t)queue level:(NSUInteger)level;
+ (instancetype)nodeWithLogger:(id<JCORELogger>)logger loggerQueue:(dispatch_queue_t)queue level:(NSUInteger)level;
@end

@interface JCORELogMessage : NSObject {
    NSString *_threadID;
    NSString *_threadName;
    NSString *_queueLabel;
    NSString *_message;
    NSUInteger _level;
    NSUInteger _flag;
    NSInteger _context;
    NSString *_file;
    NSString *_fileName;
    NSString *_function;
    NSUInteger _line;
    id _tag;
    NSInteger _options;
    NSDate *_timestamp;
}
@property (readonly, nonatomic) NSString *message;
@property (readonly, nonatomic) NSUInteger level;
@property (readonly, nonatomic) NSUInteger flag;
@property (readonly, nonatomic) NSInteger context;
@property (readonly, nonatomic) NSString *file;
@property (readonly, nonatomic) NSString *fileName;
@property (readonly, nonatomic) NSString *function;
@property (readonly, nonatomic) NSUInteger line;
@property (readonly, nonatomic) id tag;
@property (readonly, nonatomic) NSInteger options;
@property (readonly, nonatomic) NSDate *timestamp;
- (instancetype)initWithMessage:(NSString *)message level:(NSUInteger)level flag:(NSUInteger)flag context:(NSInteger)context file:(NSString *)file function:(NSString *)function line:(NSUInteger)line tag:(id)tag options:(NSInteger)options timestamp:(NSDate *)timestamp;
@end

@interface JCOREAbstractLogger : NSObject <JCORELogger> {
    id<JCORELogFormatter> _logFormatter;
    dispatch_queue_t _loggerQueue;
}
@property (strong, nonatomic) id<JCORELogFormatter> logFormatter;
@property (strong, nonatomic) dispatch_queue_t loggerQueue;
@property (readonly, nonatomic, getter=isOnGlobalLoggingQueue) BOOL onGlobalLoggingQueue;
@property (readonly, nonatomic, getter=isOnInternalLoggerQueue) BOOL onInternalLoggerQueue;
@property (readonly, nonatomic) NSString *loggerName;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
@end

@interface JCORELog : NSObject
+ (NSRecursiveLock *)innerLock;
+ (dispatch_queue_t)loggingQueue;
+ (void)addLogger:(id<JCORELogger>)logger withLevel:(NSUInteger)level;
+ (void)removeLogger:(id<JCORELogger>)logger;
+ (void)removeAllLoggers;
+ (void)queueLogMessage:(JCORELogMessage *)message asynchronously:(BOOL)asynchronous;
+ (void)log:(BOOL)asynchronous level:(NSUInteger)level flag:(NSUInteger)flag context:(NSInteger)context file:(const char *)file function:(const char *)function line:(NSUInteger)line tag:(id)tag format:(NSString *)format, ...;
+ (void)log:(BOOL)asynchronous message:(NSString *)message level:(NSUInteger)level flag:(NSUInteger)flag context:(NSInteger)context file:(const char *)file function:(const char *)function line:(NSUInteger)line tag:(id)tag;
+ (void)lt_addLogger:(id<JCORELogger>)logger level:(NSUInteger)level;
+ (void)lt_removeLogger:(id<JCORELogger>)logger;
+ (void)lt_removeAllLoggers;
+ (void)lt_log:(JCORELogMessage *)message;
@end

@interface JCOREFormatter : NSObject <JCORELogFormatter> {
    NSString *_prefix;
}
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
- (instancetype)initWithPrefix:(NSString *)prefix;
@end
