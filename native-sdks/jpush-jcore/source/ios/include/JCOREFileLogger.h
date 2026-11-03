#pragma once
#import "JCORELog.h"
#import <TargetConditionals.h>

FOUNDATION_EXPORT const unsigned long long kJDDDefaultLogMaxFileSize;
FOUNDATION_EXPORT const NSTimeInterval kJDDDefaultLogRollingFrequency;
FOUNDATION_EXPORT const NSUInteger kJDDDefaultLogMaxNumLogFiles;
FOUNDATION_EXPORT const unsigned long long kJDDDefaultLogFilesDiskQuota;
FOUNDATION_EXPORT const NSTimeInterval kJDDRollingLeeway;
FOUNDATION_EXPORT BOOL jdoesAppRunInBackground(void);

@class JCORELogFileInfo, JCOREBufferedProxy;

@protocol JCORELogFileManager <NSObject>
- (NSString *)createNewLogFileWithError:(NSError **)error;
@property NSUInteger maximumNumberOfLogFiles;
@property NSUInteger logFilesDiskQuota;
@property (readonly, copy, nonatomic) NSString *logsDirectory;
@property (readonly, nonatomic) NSArray *unsortedLogFilePaths;
@property (readonly, nonatomic) NSArray *unsortedLogFileNames;
@property (readonly, nonatomic) NSArray *unsortedLogFileInfos;
@property (readonly, nonatomic) NSArray *sortedLogFilePaths;
@property (readonly, nonatomic) NSArray *sortedLogFileNames;
@property (readonly, nonatomic) NSArray *sortedLogFileInfos;
@optional
- (NSString *)createNewLogFile;
- (void)didArchiveLogFile:(NSString *)path;
- (void)didRollAndArchiveLogFile:(NSString *)path;
@end

@interface JCORELogFileInfo : NSObject {
    NSString *_filePath;
    NSString *_fileName;
    NSDictionary *_fileAttributes;
    NSDate *_creationDate;
    NSDate *_modificationDate;
    unsigned long long _fileSize;
    NSString *filePath;
}
@property (readonly, nonatomic) NSString *filePath;
@property (readonly, nonatomic) NSString *fileName;
@property (readonly, nonatomic) NSDictionary *fileAttributes;
@property (readonly, nonatomic) NSDate *creationDate;
@property (readonly, nonatomic) NSDate *modificationDate;
@property (readonly, nonatomic) unsigned long long fileSize;
@property (readonly, nonatomic) NSTimeInterval age;
@property (nonatomic) BOOL isArchived;
+ (instancetype)logFileWithPath:(NSString *)path;
- (instancetype)initWithFilePath:(NSString *)path;
- (void)reset;
- (void)renameFile:(NSString *)name;
- (BOOL)hasExtendedAttributeWithName:(NSString *)name;
- (void)addExtendedAttributeWithName:(NSString *)name;
- (void)removeExtendedAttributeWithName:(NSString *)name;
- (NSComparisonResult)reverseCompareByCreationDate:(JCORELogFileInfo *)other;
- (NSComparisonResult)reverseCompareByModificationDate:(JCORELogFileInfo *)other;
#if TARGET_OS_SIMULATOR
- (BOOL)_hasExtensionAttributeWithName:(NSString *)name;
- (void)_removeExtensionAttributeWithName:(NSString *)name;
#endif
@end

@interface JCORELogFileManagerDefault : NSObject <JCORELogFileManager> {
    NSDateFormatter *_fileDateFormatter;
    NSUInteger _maximumNumberOfLogFiles;
    NSUInteger _logFilesDiskQuota;
    NSString *_logsDirectory;
    NSString *_defaultFileProtectionLevel;
}
@property (readonly, copy) NSString *newLogFileName;
@property (readonly, copy) NSString *logFileHeader;
@property NSUInteger maximumNumberOfLogFiles;
@property NSUInteger logFilesDiskQuota;
@property (readonly, copy, nonatomic) NSString *logsDirectory;
@property (readonly, nonatomic) NSArray *unsortedLogFilePaths;
@property (readonly, nonatomic) NSArray *unsortedLogFileNames;
@property (readonly, nonatomic) NSArray *unsortedLogFileInfos;
@property (readonly, nonatomic) NSArray *sortedLogFilePaths;
@property (readonly, nonatomic) NSArray *sortedLogFileNames;
@property (readonly, nonatomic) NSArray *sortedLogFileInfos;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
- (instancetype)initWithLogsDirectory:(NSString *)directory;
- (instancetype)initWithLogsDirectory:(NSString *)directory defaultFileProtectionLevel:(NSString *)level;
- (NSString *)defaultLogsDirectory;
- (NSString *)logFileProtection;
- (NSDateFormatter *)logFileDateFormatter;
- (NSString *)applicationName;
- (BOOL)isLogFile:(NSString *)name;
- (NSData *)logFileHeaderData;
- (void)deleteOldLogFiles;
@end

@interface JCORELogFileFormatterDefault : NSObject <JCORELogFormatter> {
    NSDateFormatter *_dateFormatter;
}
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
- (instancetype)initWithDateFormatter:(NSDateFormatter *)formatter;
@end

// The original subclass exposes a readonly property over the inherited queue setter.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wproperty-attribute-mismatch"
@interface JCOREFileLogger : JCOREAbstractLogger <JCORELogger> {
    id<JCORELogFileManager> _logFileManager;
    JCORELogFileInfo *_currentLogFileInfo;
    NSFileHandle *_currentLogFileHandle;
    dispatch_source_t _currentLogFileVnode;
    NSTimeInterval _rollingFrequency;
    dispatch_source_t _rollingTimer;
    unsigned long long _maximumFileSize;
    dispatch_queue_t _completionQueue;
    BOOL _doNotReuseLogFiles;
    BOOL _automaticallyAppendNewlineForCustomFormatters;
}
@property unsigned long long maximumFileSize;
@property NSTimeInterval rollingFrequency;
@property BOOL doNotReuseLogFiles;
@property (readonly, nonatomic) id<JCORELogFileManager> logFileManager;
@property (nonatomic) BOOL automaticallyAppendNewlineForCustomFormatters;
@property (readonly, nonatomic) JCORELogFileInfo *currentLogFileInfo;
@property (strong, nonatomic) id<JCORELogFormatter> logFormatter;
@property (readonly, nonatomic) dispatch_queue_t loggerQueue;
@property (readonly, nonatomic) NSString *loggerName;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
- (instancetype)initWithLogFileManager:(id<JCORELogFileManager>)manager;
- (instancetype)initWithLogFileManager:(id<JCORELogFileManager>)manager completionQueue:(dispatch_queue_t)queue;
- (void)lt_cleanup;
- (void)lt_scheduleTimerToRollLogFileDueToAge;
- (void)rollLogFile;
- (void)rollLogFileWithCompletionBlock:(dispatch_block_t)completion;
- (void)lt_rollLogFileNow;
- (void)lt_maybeRollLogFileDueToAge;
- (void)lt_maybeRollLogFileDueToSize;
- (BOOL)lt_shouldLogFileBeArchived:(JCORELogFileInfo *)info;
- (JCORELogFileInfo *)lt_currentLogFileInfo;
- (BOOL)lt_shouldUseLogFile:(JCORELogFileInfo *)info isResuming:(BOOL)resuming;
- (void)lt_monitorCurrentLogFileForExternalChanges;
- (NSFileHandle *)lt_currentLogFileHandle;
- (void)willLogMessage:(JCORELogFileInfo *)info;
- (void)didLogMessage:(JCORELogFileInfo *)info;
- (BOOL)shouldArchiveRecentLogFileInfo:(JCORELogFileInfo *)info;
- (void)willRemoveLogger;
- (void)flush;
- (void)lt_flush;
- (id)wrapWithBuffer;
- (id)unwrapFromBuffer;
- (void)logData:(NSData *)data;
- (void)dummyMethod;
- (void)lt_logData:(NSData *)data;
- (NSData *)lt_dataForMessage:(JCORELogMessage *)message;
@end

#pragma clang diagnostic pop

@interface JCOREBufferedProxy : NSProxy {
    JCOREFileLogger *_fileLogger;
    NSOutputStream *_buffer;
    NSUInteger _maxBufferSizeBytes;
    NSUInteger _currentBufferSizeBytes;
}
@property (strong, nonatomic) JCOREFileLogger *fileLogger;
@property (strong, nonatomic) NSOutputStream *buffer;
@property (nonatomic) NSUInteger maxBufferSizeBytes;
@property (nonatomic) NSUInteger currentBufferSizeBytes;
- (instancetype)initWithFileLogger:(JCOREFileLogger *)logger;
- (void)flushBuffer;
- (void)lt_sendBufferedDataToFileLogger;
- (void)logMessage:(JCORELogMessage *)message;
- (void)flush;
- (id)wrapWithBuffer;
- (id)unwrapFromBuffer;
@end
