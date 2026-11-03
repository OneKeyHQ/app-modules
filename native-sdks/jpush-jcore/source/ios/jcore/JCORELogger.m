#import "JCORELogger.h"
#import "JCOREFileLogger.h"
#import "JCORELogRequests.h"
#import "RecoveredCache.h"
#import "JCOREClientInfo.h"
#import "JCOREUtilities.h"
#import "JCOREDataSecurity.h"

extern NSString *const kJCORENetworkCmdCtrlNotification;

@implementation JCOREDefaultLogger
@dynamic logFormatter, loggerQueue, loggerName, hash, superclass, description, debugDescription;
+ (instancetype)sharedInstance {
    static JCOREDefaultLogger *instance;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ instance = [[[self class] alloc] init]; });
    return instance;
}
- (void)logMessage:(JCORELogMessage *)message {
    NSString *text = _logFormatter ? [_logFormatter formatLogMessage:message] : message.message;
    if (message) NSLog(@"%@", text);
}
- (NSString *)loggerName { return @"org.hxhg.jiguang.log.output.thread"; }
@end

static JCORELogger *JCORESharedBusinessLogger;
@implementation JCORELogger
@synthesize fileLogger = _fileLogger, tokenRequest = _tokenRequest, isOpenCollect = _isOpenCollect;
+ (instancetype)sharedLogger {
    static dispatch_once_t once;
    dispatch_once(&once, ^{ if (!JCORESharedBusinessLogger) JCORESharedBusinessLogger = [[JCORELogger alloc] init]; });
    return JCORESharedBusinessLogger;
}
- (void)setup {
    self.isOpenCollect = NO;
    [self openCollectLog];
    [NSNotificationCenter.defaultCenter addObserver:self selector:@selector(handleCmdControl:) name:kJCORENetworkCmdCtrlNotification object:nil];
    [self startReportLogFile];
}
- (JCORELogTokenRequest *)tokenRequest {
    if (!_tokenRequest) _tokenRequest = [[JCORELogTokenRequest alloc] init];
    return _tokenRequest;
}
- (JCOREFileLogger *)fileLogger {
    if (!_fileLogger) {
        _fileLogger = [[JCOREFileLogger alloc] init];
        _fileLogger.doNotReuseLogFiles = YES;
        _fileLogger.rollingFrequency = 86400.0;
        _fileLogger.logFileManager.maximumNumberOfLogFiles = 0;
    }
    return _fileLogger;
}
+ (void)setupJCoreLogger { [self initJCoreLogger]; }
+ (void)initJCoreLogger {
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        JCOREFormatter *formatter = [[JCOREFormatter alloc] initWithPrefix:@"JIGUANG"];
        [JCOREDefaultLogger sharedInstance].logFormatter = formatter;
        [JCORELog removeLogger:[JCOREDefaultLogger sharedInstance]];
        [JCORELog addLogger:[JCOREDefaultLogger sharedInstance] withLevel:0xe7];
        [[JCORELogger sharedLogger] setup];
    });
}
+ (void)setDebugModel {
    [JCORELogger setupJCoreLogger];
    [JCORELog removeLogger:[JCOREDefaultLogger sharedInstance]];
    [JCORELog addLogger:[JCOREDefaultLogger sharedInstance] withLevel:0x1ef];
}
+ (void)setLogOff {
    [JCORELogger setupJCoreLogger];
    [JCORELog removeLogger:[JCOREDefaultLogger sharedInstance]];
    [JCORELog addLogger:[JCOREDefaultLogger sharedInstance] withLevel:0];
}
+ (BOOL)isOpenCollectLog { return [JCORELogger sharedLogger].isOpenCollect; }
- (void)handleCmdControl:(NSNotification *)notification {
    @try {
        if (!notification || !notification.userInfo) return;
        id info = notification.userInfo;
        id command = [info isKindOfClass:NSDictionary.class] ? [info objectForKey:@"cmd_content"] : nil;
        if (![command isKindOfClass:NSDictionary.class]) { NSLog(@"handle cmd control cancel, cmd content nil"); return; }
        id content = command[@"content"];
        if (![content isKindOfClass:NSDictionary.class]) { NSLog(@"handle cmd control cancel, content error"); return; }
        if (![content count]) { NSLog(@"handle cmd control cancel, content nil"); return; }
        id code = command[@"cmd"];
        if (!code) { NSLog(@"handle cmd control cancel, cmd nil"); return; }
        if ([code charValue] != 0x52) return;
        id expiry = [content objectForKey:@"expire"];
        if (![expiry isKindOfClass:NSNumber.class]) { NSLog(@"[%s] expire is nil", "-[JCORELogger handleCmdControl:]"); return; }
        long long seconds = (long long)((unsigned long long)[expiry integerValue] * 86400ULL);
        NSDate *date = [NSDate dateWithTimeIntervalSinceNow:(double)seconds];
        [JCORECacheUtilities setCacheObject:@((NSInteger)date.timeIntervalSince1970) forKey:@"JCORELOGEXPIRETIME"];
        id delay = [content objectForKey:@"delay_upload"];
        if ([delay isKindOfClass:NSNumber.class]) [JCORECacheUtilities setCacheObject:@([delay integerValue]) forKey:@"JCORELOGDELAY"];
        id enable = [content objectForKey:@"log_enable"];
        if (![enable isKindOfClass:[enable class]]) { NSLog(@"[%s] log_enableNum is nil", "-[JCORELogger handleCmdControl:]"); return; }
        BOOL enabled = [enable boolValue];
        [JCORECacheUtilities setCacheObject:@((NSInteger)enabled) forKey:@"JCORELOGENABLE"];
        if (enabled) [self openCollectLog];
        else { self.isOpenCollect = NO; [self closeCollectLog]; }
    } @catch (NSException *exception) {
        NSLog(@"[%s] is exception", "-[JCORELogger handleCmdControl:]");
    }
}
- (void)openCollectLog {
    if (!self.isOpenCollect) {
        if (self.fileLogger) { [JCORELog removeLogger:self.fileLogger]; self.fileLogger = nil; }
        BOOL enabled = [self judgeCollectOpenState];
        self.isOpenCollect = enabled;
        if (enabled) {
            NSLog(@"start log collect");
            [JCORELog removeLogger:[JCOREDefaultLogger sharedInstance]];
            [JCORELog addLogger:[JCOREDefaultLogger sharedInstance] withLevel:0x1ef];
            [JCORELog addLogger:self.fileLogger withLevel:NSUIntegerMax];
            return;
        }
        if (!self.isOpenCollect) [self closeCollectLog];
    } else NSLog(@"[%s] is already open", "-[JCORELogger openCollectLog]");
}
- (void)closeCollectLog {
    NSLog(@"close log collect");
    if (self.fileLogger) { [JCORELog removeLogger:self.fileLogger]; self.fileLogger = nil; }
}
- (BOOL)judgeCollectOpenState {
    BOOL result = NO;
    @try {
        id enabled = [JCORECacheUtilities cacheObjectForKey:@"JCORELOGENABLE"];
        if ([enabled isKindOfClass:NSNumber.class]) {
            result = [enabled boolValue];
            if (result && [self judgeExpireDate]) {
                result = NO;
                NSLog(@"log collect date is expire");
            }
        }
    } @catch (NSException *exception) {
        NSLog(@"[%s]is exception", "-[JCORELogger judgeCollectOpenState]");
    }
    return result;
}
- (BOOL)judgeExpireDate {
    @try {
        id expiry = [JCORECacheUtilities cacheObjectForKey:@"JCORELOGEXPIRETIME"];
        if (![expiry isKindOfClass:NSNumber.class]) return YES;
        return (double)[expiry integerValue] < [NSDate date].timeIntervalSince1970;
    } @catch (NSException *exception) {
        NSLog(@"[%s]is exception", "-[JCORELogger judgeExpireDate]");
    }
    return YES;
}
- (void)startReportLogFile {
    int64_t delta;
    @try {
        id delay = [JCORECacheUtilities cacheObjectForKey:@"JCORELOGDELAY"];
        delta = [delay isKindOfClass:NSNumber.class] ? (int64_t)((uint64_t)[delay integerValue] * 1000000000ULL) : 5000000000LL;
    } @catch (NSException *exception) {
        NSLog(@"[%s]is exception", "-[JCORELogger startReportLogFile]");
        return;
    }
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, delta), dispatch_get_main_queue(), ^{ [self reportLogFile]; });
}
- (void)reportLogFile {
    if (![self judgeCollectOpenState]) { NSLog(@"reportFile is close"); return; }
    NSArray *paths = self.fileLogger.logFileManager.sortedLogFilePaths;
    NSUInteger minimum = self.isOpenCollect ? 2 : 1;
    if (paths.count < minimum) { NSLog(@"does not exist oldfilePath"); return; }
    for (NSString *path in paths) {
        if (!self.isOpenCollect || ![path isEqualToString:self.fileLogger.currentLogFileInfo.filePath]) [self reqquestToken:path];
    }
}
- (void)reqquestToken:(NSString *)path {
    JCORELogTokenRequest *token = [[JCORELogTokenRequest alloc] init];
    token.filename = path.lastPathComponent;
    [token sendRequest:^(id response, NSError *error) { [self handleTokenContent:response withFilePath:path]; }];
}
- (void)handleTokenContent:(NSData *)response withFilePath:(NSString *)path {
    if (!response || !path) { NSLog(@"[%s]response or path is bad ", "-[JCORELogger handleTokenContent:withFilePath:]"); return; }
    id content = [JCOREUtilities dataToJSON:response];
    if (![content isKindOfClass:NSDictionary.class]) return;
    id data = [content objectForKey:@"data"];
    if (![data isKindOfClass:NSDictionary.class]) return;
    id token = data[@"uploadPreSignedURL"];
    if ([token isKindOfClass:NSString.class]) [self startFileReport:token withFilePath:path];
}
- (void)startFileReport:(NSString *)token withFilePath:(NSString *)path {
    if (!token.length) { NSLog(@"[%s]token is bad", "-[JCORELogger startFileReport:withFilePath:]"); return; }
    NSData *data = [NSData dataWithContentsOfFile:path];
    if (!data) return;
    NSData *encrypted = [JCOREDataSecurity AES256EncryptData:data withKey:[JCOREClientInfo appKey]];
    if (!encrypted) return;
    JCORELogFileUploadRequest *request = [[JCORELogFileUploadRequest alloc] initWithUrl:token fileData:encrypted];
    [request sendRequest:^(id response, NSError *error) {
        const char *function = "-[JCORELogger startFileReport:withFilePath:]_block_invoke";
        if (error) { NSLog(@"[%s] logFileReport failure", function); return; }
        NSError *removeError = nil;
        if ([NSFileManager.defaultManager removeItemAtPath:path error:&removeError]) NSLog(@"[%s] deleOldLogFile is success", function);
        else NSLog(@"[%s] deleOldLogFile is fialure,error is %@", function, removeError);
    }];
}
- (void)encryptedFilesPath:(NSString *)path {
    NSData *data = [NSData dataWithContentsOfFile:path];
    if (!data) return;
    NSData *encrypted = [JCOREDataSecurity AES256EncryptData:data withKey:[JCOREClientInfo appKey]];
    if (encrypted) NSLog([encrypted writeToFile:path atomically:YES] ? @"encryData is success" : @"encryData is failure");
}
@end
