#import "JCORELog.h"
#import <stdarg.h>

NSString *const JCORELoggerNameASL = @"JCORE.lumberjack.aslLogger";
NSString *const JCORELoggerNameTTY = @"JCORE.lumberjack.ttyLogger";
NSString *const JCORELoggerNameOS = @"JCORE.lumberjack.osLogger";
NSString *const JCORELoggerNameFile = @"JCORE.lumberjack.fileLogger";

// The business logger owns collection policy and remains a separate SDK implementation.
@interface JCORELogger : NSObject
+ (BOOL)isOpenCollectLog;
@end

static dispatch_queue_t loggingQueue;
static dispatch_group_t loggingGroup;
static NSMutableArray<JCORELoggerNode *> *loggers;
static dispatch_semaphore_t queueSemaphore;
static char globalLoggingQueueIdentityKey;
static NSUInteger processorCount;

@implementation JCORELoggerNode
@synthesize logger = _logger, level = _level, loggerQueue = _loggerQueue;
- (instancetype)initWithLogger:(id<JCORELogger>)logger loggerQueue:(dispatch_queue_t)queue level:(NSUInteger)level {
    self = [super init];
    if (self) {
        _logger = logger;
        if (queue) _loggerQueue = queue;
        _level = level;
    }
    return self;
}
+ (instancetype)nodeWithLogger:(id<JCORELogger>)logger loggerQueue:(dispatch_queue_t)queue level:(NSUInteger)level {
    return [[JCORELoggerNode alloc] initWithLogger:logger loggerQueue:queue level:level];
}
- (void)dealloc {}
@end

@implementation JCORELogMessage
@synthesize message = _message, level = _level, flag = _flag, context = _context;
@synthesize file = _file, fileName = _fileName, function = _function, line = _line;
@synthesize tag = _tag, options = _options, timestamp = _timestamp;
- (instancetype)initWithMessage:(NSString *)message level:(NSUInteger)level flag:(NSUInteger)flag context:(NSInteger)context file:(NSString *)file function:(NSString *)function line:(NSUInteger)line tag:(id)tag options:(NSInteger)options timestamp:(NSDate *)timestamp {
    self = [super init];
    if (self) {
        _message = [message copy];
        _level = level;
        _flag = flag;
        _context = context;
        _file = file;
        _function = function;
        _line = line;
        _tag = tag;
        _options = options;
        _timestamp = timestamp ?: [NSDate new];
        _fileName = [_file lastPathComponent];
        for (NSString *name in @[@"JCORE", @"JPUSH", @"JSHARE", @"JANALYTICS", @"JCommon", @"JVERIFICATION", @"JV"]) {
            _fileName = [_fileName stringByReplacingOccurrencesOfString:name withString:@"JIGUANG"];
        }
        NSRange extension = [_fileName rangeOfString:@"." options:NSBackwardsSearch];
        if (extension.location != NSNotFound) _fileName = [_fileName substringToIndex:extension.location];
    }
    return self;
}
@end

@implementation JCOREAbstractLogger
@synthesize loggerQueue = _loggerQueue;
@dynamic hash, superclass, description, debugDescription;
- (instancetype)init {
    self = [super init];
    if (self) {
        const char *label = [self respondsToSelector:@selector(loggerName)] ? self.loggerName.UTF8String : NULL;
        _loggerQueue = dispatch_queue_create(label, DISPATCH_QUEUE_SERIAL);
        dispatch_queue_set_specific(_loggerQueue, (__bridge void *)self, (__bridge void *)self, NULL);
    }
    return self;
}
- (void)dealloc {}
- (void)logMessage:(JCORELogMessage * __unsafe_unretained)message {
    // The original abstract sink returns without inspecting or retaining its argument.
}
- (id<JCORELogFormatter>)logFormatter {
    __block id<JCORELogFormatter> formatter;
    dispatch_sync([JCORELog loggingQueue], ^{
        dispatch_sync(self->_loggerQueue, ^{ formatter = self->_logFormatter; });
    });
    return formatter;
}
- (void)setLogFormatter:(id<JCORELogFormatter>)formatter {
    dispatch_block_t update = ^{
        @autoreleasepool {
            if (self->_logFormatter != formatter) self->_logFormatter = formatter;
        }
    };
    dispatch_async([JCORELog loggingQueue], ^{ dispatch_async(self->_loggerQueue, update); });
}
- (NSString *)loggerName { return NSStringFromClass([self class]); }
- (BOOL)isOnGlobalLoggingQueue { return dispatch_get_specific(&globalLoggingQueueIdentityKey) != NULL; }
- (BOOL)isOnInternalLoggerQueue { return dispatch_get_specific((__bridge void *)self) != NULL; }
@end

@implementation JCORELog
+ (void)initialize {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        loggingQueue = dispatch_queue_create("org.hxhg.jiguang.log.manager.thread", DISPATCH_QUEUE_SERIAL);
        loggingGroup = dispatch_group_create();
        loggers = [[NSMutableArray alloc] initWithCapacity:4];
        queueSemaphore = dispatch_semaphore_create(1000);
        dispatch_queue_set_specific(loggingQueue, &globalLoggingQueueIdentityKey, &globalLoggingQueueIdentityKey, NULL);
        processorCount = [NSProcessInfo processInfo].processorCount;
        if (!processorCount) processorCount = 1;
    });
}
- (instancetype)init { return [super init]; }
- (void)dealloc {}
+ (NSRecursiveLock *)innerLock {
    static NSRecursiveLock *lock;
    static dispatch_once_t onceToken;
    if (!lock) dispatch_once(&onceToken, ^{ lock = [[NSRecursiveLock alloc] init]; });
    return lock;
}
+ (dispatch_queue_t)loggingQueue { return loggingQueue; }
+ (void)addLogger:(id<JCORELogger>)logger withLevel:(NSUInteger)level {
    if (logger) dispatch_async(loggingQueue, ^{ @autoreleasepool { [self lt_addLogger:logger level:level]; } });
}
+ (void)removeLogger:(id<JCORELogger>)logger {
    if (logger) dispatch_async(loggingQueue, ^{ @autoreleasepool { [self lt_removeLogger:logger]; } });
}
+ (void)removeAllLoggers {
    dispatch_async(loggingQueue, ^{ @autoreleasepool { [self lt_removeAllLoggers]; } });
}
+ (void)queueLogMessage:(JCORELogMessage *)message asynchronously:(BOOL)asynchronous {
    dispatch_semaphore_wait(queueSemaphore, DISPATCH_TIME_FOREVER);
    dispatch_block_t work = ^{ @autoreleasepool { [self lt_log:message]; } };
    if (asynchronous) dispatch_async(loggingQueue, work);
    else dispatch_sync(loggingQueue, work);
}
+ (void)log:(BOOL)asynchronous level:(NSUInteger)level flag:(NSUInteger)flag context:(NSInteger)context file:(const char *)file function:(const char *)function line:(NSUInteger)line tag:(id)tag format:(NSString *)format, ... {
    if ((flag & 0x3e0) && ![JCORELogger isOpenCollectLog]) return;
    if (!format) return;
    NSString *path = [NSString stringWithFormat:@"%@/Documents/JCORE.log", NSHomeDirectory()];
    va_list arguments;
    va_start(arguments, format);
    NSString *message = [[NSString alloc] initWithFormat:format arguments:arguments];
    va_end(arguments);
    [self log:asynchronous message:message level:level flag:flag context:context file:path.UTF8String function:function line:line tag:tag];
}
+ (void)log:(BOOL)asynchronous message:(NSString *)message level:(NSUInteger)level flag:(NSUInteger)flag context:(NSInteger)context file:(const char *)file function:(const char *)function line:(NSUInteger)line tag:(id)tag {
    [[self innerLock] lock];
    JCORELogMessage *entry = [[JCORELogMessage alloc] initWithMessage:message level:level flag:flag context:context
                                                             file:[NSString stringWithFormat:@"%s", file]
                                                         function:[NSString stringWithFormat:@"%s", function]
                                                             line:line tag:tag options:0 timestamp:[NSDate date]];
    [self queueLogMessage:entry asynchronously:asynchronous];
    [[self innerLock] unlock];
}
+ (void)lt_addLogger:(id<JCORELogger>)logger level:(NSUInteger)level {
    for (JCORELoggerNode *node in [loggers copy]) {
        if (node->_logger == logger && node->_level == level) return;
    }
    dispatch_queue_t queue = [logger respondsToSelector:@selector(loggerQueue)] ? logger.loggerQueue : nil;
    if (!queue) {
        const char *label = [logger respondsToSelector:@selector(loggerName)] ? logger.loggerName.UTF8String : NULL;
        queue = dispatch_queue_create(label, DISPATCH_QUEUE_SERIAL);
    }
    [loggers addObject:[JCORELoggerNode nodeWithLogger:logger loggerQueue:queue level:level]];
}
+ (void)lt_removeLogger:(id<JCORELogger>)logger {
    for (JCORELoggerNode *node in [loggers copy]) {
        if (node->_logger == logger) { [loggers removeObject:node]; break; }
    }
}
+ (void)lt_removeAllLoggers { [loggers removeAllObjects]; }
+ (void)lt_log:(JCORELogMessage *)message {
    NSArray<JCORELoggerNode *> *snapshot = [loggers copy];
    if (processorCount > 1) {
        for (JCORELoggerNode *node in snapshot) {
            if (node->_level & message.flag) {
                dispatch_group_async(loggingGroup, node->_loggerQueue, ^{ @autoreleasepool { [node->_logger logMessage:message]; } });
            }
        }
        dispatch_group_wait(loggingGroup, DISPATCH_TIME_FOREVER);
    } else {
        for (JCORELoggerNode *node in snapshot) {
            if (node->_level & message.flag) {
                dispatch_sync(node->_loggerQueue, ^{ @autoreleasepool { [node->_logger logMessage:message]; } });
            }
        }
    }
    dispatch_semaphore_signal(queueSemaphore);
}
@end

@implementation JCOREFormatter
@dynamic hash, superclass, description, debugDescription;
- (instancetype)initWithPrefix:(NSString *)prefix {
    self = [super init];
    if (self) _prefix = prefix;
    return self;
}
- (NSString *)formatLogMessage:(JCORELogMessage *)message {
    NSString *flag;
    switch (message.flag) {
        case 1: flag = @"E"; break;
        case 2: flag = @"W"; break;
        case 4: flag = @"I"; break;
        case 8: flag = @"D"; break;
        case 16: flag = @"V"; break;
        case 32: flag = @"EE"; break;
        case 64: flag = @"WW"; break;
        case 128: flag = @"II"; break;
        case 256: flag = @"DD"; break;
        case 512: flag = @"VV"; break;
        default: flag = nil; break;
    }
    return [NSString stringWithFormat:@" | %@ | %@ - [%@] %@", _prefix, flag, message.fileName, message.message];
}
@end
