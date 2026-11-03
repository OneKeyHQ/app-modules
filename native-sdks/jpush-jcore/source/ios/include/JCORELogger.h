#pragma once
#import "JCORELog.h"
@class JCOREFileLogger, JCORELogTokenRequest;

@interface JCOREDefaultLogger : JCOREAbstractLogger <JCORELogger>
@property (strong, nonatomic) id<JCORELogFormatter> logFormatter;
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wproperty-attribute-mismatch"
// The original subclass redeclares this inherited property as readonly.
@property (readonly, nonatomic) dispatch_queue_t loggerQueue;
#pragma clang diagnostic pop
@property (readonly, nonatomic) NSString *loggerName;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
+ (instancetype)sharedInstance;
@end

@interface JCORELogger : NSObject {
    BOOL _isOpenCollect;
    JCOREFileLogger *_fileLogger;
    JCORELogTokenRequest *_tokenRequest;
}
@property (strong, nonatomic) JCOREFileLogger *fileLogger;
@property (strong, nonatomic) JCORELogTokenRequest *tokenRequest;
@property (nonatomic) BOOL isOpenCollect;
+ (instancetype)sharedLogger;
+ (void)setupJCoreLogger;
+ (void)initJCoreLogger;
+ (void)setDebugModel;
+ (void)setLogOff;
+ (BOOL)isOpenCollectLog;
- (void)setup;
- (void)handleCmdControl:(NSNotification *)notification;
- (void)openCollectLog;
- (void)closeCollectLog;
- (BOOL)judgeCollectOpenState;
- (BOOL)judgeExpireDate;
- (void)startReportLogFile;
- (void)reportLogFile;
- (void)reqquestToken:(NSString *)path;
- (void)handleTokenContent:(NSData *)response withFilePath:(NSString *)path;
- (void)startFileReport:(NSString *)token withFilePath:(NSString *)path;
- (void)encryptedFilesPath:(NSString *)path;
@end
