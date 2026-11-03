#import "JCOREFileLogger.h"
#import <objc/message.h>
#import <sys/mount.h>
#import <sys/xattr.h>
#import <float.h>
#import <errno.h>
#import <math.h>
#import <string.h>

const unsigned long long kJDDDefaultLogMaxFileSize = 5 * 1024 * 1024;
const NSTimeInterval kJDDDefaultLogRollingFrequency = 24 * 60 * 60;
const NSUInteger kJDDDefaultLogMaxNumLogFiles = 5;
const unsigned long long kJDDDefaultLogFilesDiskQuota = 20 * 1024 * 1024;
const NSTimeInterval kJDDRollingLeeway = 1.0;

BOOL jdoesAppRunInBackground(void) {
    for (NSString *mode in [[NSBundle mainBundle] objectForInfoDictionaryKey:@"UIBackgroundModes"]) {
        if (mode.length) return YES;
    }
    return NO;
}

#if TARGET_OS_SIMULATOR
static NSString *JCOREXattrExtension(NSString *name) {
    static NSDictionary *map;
    static dispatch_once_t token;
    dispatch_once(&token, ^{ map = @{@"lumberjack.log.archived": @"archived"}; });
    return map[name];
}
#endif

@implementation JCORELogFileInfo
@synthesize filePath;
@dynamic fileName, fileAttributes, creationDate, modificationDate, fileSize, age, isArchived;
+ (instancetype)logFileWithPath:(NSString *)path {
    return path ? [[self alloc] initWithFilePath:path] : nil;
}
- (instancetype)initWithFilePath:(NSString *)path {
    if ((self = [super init])) filePath = [path copy];
    return self;
}
- (NSDictionary *)fileAttributes {
    if (!_fileAttributes && filePath) {
        NSError *error;
        _fileAttributes = [[NSFileManager defaultManager] attributesOfItemAtPath:filePath error:&error];
        if (error) NSLog(@"DDLogFileInfo: Failed to read file attributes: %@", error);
    }
    return _fileAttributes ?: @{};
}
- (NSString *)fileName {
    if (!_fileName) _fileName = [filePath lastPathComponent];
    return _fileName;
}
- (NSDate *)modificationDate {
    if (!_modificationDate) _modificationDate = self.fileAttributes[NSFileModificationDate];
    return _modificationDate;
}
- (NSDate *)creationDate {
    if (!_creationDate) _creationDate = self.fileAttributes[NSFileCreationDate];
    return _creationDate;
}
- (unsigned long long)fileSize {
    if (!_fileSize) _fileSize = [self.fileAttributes[NSFileSize] unsignedLongLongValue];
    return _fileSize;
}
- (NSTimeInterval)age { return -[self.creationDate timeIntervalSinceNow]; }
- (NSString *)description {
    return [@{@"filePath": self.filePath ?: @"", @"fileName": self.fileName ?: @"",
              @"fileAttributes": self.fileAttributes ?: @"", @"creationDate": self.creationDate ?: @"",
              @"modificationDate": self.modificationDate ?: @"", @"fileSize": @(self.fileSize),
              @"age": @(self.age), @"isArchived": @(self.isArchived)} description];
}
- (BOOL)isArchived { return [self hasExtendedAttributeWithName:@"lumberjack.log.archived"]; }
- (void)setIsArchived:(BOOL)value {
    if (value) [self addExtendedAttributeWithName:@"lumberjack.log.archived"];
    else [self removeExtendedAttributeWithName:@"lumberjack.log.archived"];
}
- (void)reset {
    _fileName = nil;
    _fileAttributes = nil;
    _creationDate = nil;
    _modificationDate = nil;
}
- (void)renameFile:(NSString *)name {
    if ([name isEqualToString:self.fileName]) return;
    NSFileManager *manager = [NSFileManager defaultManager];
    NSString *directory = [filePath stringByDeletingLastPathComponent];
    NSString *newPath = [directory stringByAppendingPathComponent:name];
#if !TARGET_OS_SIMULATOR
    BOOL isDirectory;
    [manager fileExistsAtPath:directory isDirectory:&isDirectory];
#endif
    NSError *error;
    if (![manager removeItemAtPath:newPath error:&error] && error.code != NSFileNoSuchFileError) {
        NSLog(@"DDLogFileInfo: Error deleting archive (%@): %@", self.fileName, error);
    }
    if (![manager moveItemAtPath:filePath toPath:newPath error:&error]) {
#if TARGET_OS_SIMULATOR
        if (error.code != NSFileNoSuchFileError)
#endif
        NSLog(@"DDLogFileInfo: Error renaming file (%@): %@", self.fileName, error);
    }
    filePath = newPath;
    [self reset];
}
#if TARGET_OS_SIMULATOR
- (BOOL)_hasExtensionAttributeWithName:(NSString *)name {
    NSArray *components = [self.fileName componentsSeparatedByString:@"."];
    for (NSUInteger i = 1; i < components.count; i++) {
        if ([name isEqualToString:components[i]]) return YES;
    }
    return NO;
}
- (void)_removeExtensionAttributeWithName:(NSString *)name {
    if (!name.length) return;
    NSArray *components = [self.fileName componentsSeparatedByString:@"."];
    NSMutableString *newName = [NSMutableString stringWithCapacity:self.fileName.length];
    BOOL found = NO;
    if (components.count) {
        [newName appendString:components.firstObject];
        for (NSUInteger i = 1; i < components.count; i++) {
            if ([name isEqualToString:components[i]]) found = YES;
            else {
                [newName appendString:@"."];
                [newName appendString:components[i]];
            }
        }
    }
    if (found) [self renameFile:newName];
}
#endif
- (BOOL)hasExtendedAttributeWithName:(NSString *)name {
    char value;
    ssize_t length = getxattr(filePath.fileSystemRepresentation, name.UTF8String, &value, 1, 0, 0);
    if (length >= 1 && value == 1) return YES;
    if (length < 0) {
#if TARGET_OS_SIMULATOR
        if (![self _hasExtensionAttributeWithName:JCOREXattrExtension(name)]) return NO;
#else
        return NO;
#endif
    }
    [self addExtendedAttributeWithName:name];
    return YES;
}
- (void)addExtendedAttributeWithName:(NSString *)name {
    char value = 1;
    if (setxattr(filePath.fileSystemRepresentation, name.UTF8String, &value, 1, 0, 0) < 0) {
        NSLog(@"DDLogFileInfo: setxattr(%@, %@): error = %s", name, filePath, strerror(errno));
    }
#if TARGET_OS_SIMULATOR
    else [self _removeExtensionAttributeWithName:JCOREXattrExtension(name)];
#endif
}
- (void)removeExtendedAttributeWithName:(NSString *)name {
    if (removexattr(filePath.fileSystemRepresentation, name.UTF8String, 0) < 0 && errno != ENOATTR) {
        NSLog(@"DDLogFileInfo: removexattr(%@, %@): error = %s", name, self.fileName, strerror(errno));
    }
#if TARGET_OS_SIMULATOR
    [self _removeExtensionAttributeWithName:JCOREXattrExtension(name)];
#endif
}
- (BOOL)isEqual:(id)other {
    return [other isKindOfClass:[self class]] && [filePath isEqualToString:[other filePath]];
}
- (NSUInteger)hash { return filePath.hash; }
- (NSComparisonResult)reverseCompareByCreationDate:(JCORELogFileInfo *)other {
    NSDate *date = self.creationDate;
    NSDate *otherDate = other.creationDate;
    return [otherDate compare:date];
}
- (NSComparisonResult)reverseCompareByModificationDate:(JCORELogFileInfo *)other {
    NSDate *date = self.modificationDate;
    NSDate *otherDate = other.modificationDate;
    return [otherDate compare:date];
}
@end

@implementation JCORELogFileManagerDefault
@dynamic hash, superclass, description, debugDescription;
@synthesize maximumNumberOfLogFiles = _maximumNumberOfLogFiles, logFilesDiskQuota = _logFilesDiskQuota;
@dynamic logsDirectory, unsortedLogFilePaths, unsortedLogFileNames, unsortedLogFileInfos;
@dynamic sortedLogFilePaths, sortedLogFileNames, sortedLogFileInfos;
+ (BOOL)automaticallyNotifiesObserversForKey:(NSString *)key {
    if ([key isEqualToString:@"maximumNumberOfLogFiles"] || [key isEqualToString:@"logFilesDiskQuota"]) return NO;
    return [super automaticallyNotifiesObserversForKey:key];
}
- (instancetype)init { return [self initWithLogsDirectory:nil]; }
- (instancetype)initWithLogsDirectory:(NSString *)directory {
    if ((self = [super init])) {
        _maximumNumberOfLogFiles = kJDDDefaultLogMaxNumLogFiles;
        _logFilesDiskQuota = kJDDDefaultLogFilesDiskQuota;
        _fileDateFormatter = [[NSDateFormatter alloc] init];
        _fileDateFormatter.locale = [NSLocale localeWithLocaleIdentifier:@"en_US_POSIX"];
        _fileDateFormatter.timeZone = [NSTimeZone timeZoneForSecondsFromGMT:0];
        _fileDateFormatter.dateFormat = @"yyyy'-'MM'-'dd'--'HH'-'mm'-'ss'-'SSS'";
        _logsDirectory = [(directory.length ? directory : [self defaultLogsDirectory]) copy];
        [self addObserver:self forKeyPath:NSStringFromSelector(@selector(maximumNumberOfLogFiles)) options:NSKeyValueObservingOptionNew | NSKeyValueObservingOptionOld context:NULL];
        [self addObserver:self forKeyPath:NSStringFromSelector(@selector(logFilesDiskQuota)) options:NSKeyValueObservingOptionNew | NSKeyValueObservingOptionOld context:NULL];
    }
    return self;
}
- (instancetype)initWithLogsDirectory:(NSString *)directory defaultFileProtectionLevel:(NSString *)level {
    if ((self = [self initWithLogsDirectory:directory])) {
        if ([level isEqualToString:NSFileProtectionNone] || [level isEqualToString:NSFileProtectionComplete] ||
            [level isEqualToString:NSFileProtectionCompleteUnlessOpen] || [level isEqualToString:NSFileProtectionCompleteUntilFirstUserAuthentication]) {
            _defaultFileProtectionLevel = level;
        }
    }
    return self;
}
- (void)dealloc {
    @try {
        [self removeObserver:self forKeyPath:NSStringFromSelector(@selector(maximumNumberOfLogFiles))];
        [self removeObserver:self forKeyPath:NSStringFromSelector(@selector(logFilesDiskQuota))];
    } @catch (NSException *exception) {
    }
}
- (void)observeValueForKeyPath:(NSString *)path ofObject:(__unsafe_unretained id)object change:(NSDictionary *)change context:(void *)context {
    if (![change[NSKeyValueChangeOldKey] isEqual:change[NSKeyValueChangeNewKey]] &&
        ([path isEqualToString:NSStringFromSelector(@selector(maximumNumberOfLogFiles))] ||
         [path isEqualToString:NSStringFromSelector(@selector(logFilesDiskQuota))])) {
        dispatch_async(dispatch_get_global_queue(0, 0), ^{ @autoreleasepool { [self deleteOldLogFiles]; } });
    }
}
- (NSString *)logFileProtection {
    if (_defaultFileProtectionLevel.length) return _defaultFileProtectionLevel;
    return jdoesAppRunInBackground() ? NSFileProtectionCompleteUntilFirstUserAuthentication : NSFileProtectionCompleteUnlessOpen;
}
- (void)deleteOldLogFiles {
    NSArray<JCORELogFileInfo *> *files = self.sortedLogFileInfos;
    NSUInteger quota = self.logFilesDiskQuota;
    NSUInteger maximum = self.maximumNumberOfLogFiles;
    if (!quota && !maximum) return;
    if (quota) {
        NSUInteger index = NSNotFound;
        unsigned long long total = 0;
        for (NSUInteger i = 0; i < files.count; i++) {
            total += files[i].fileSize;
            if (total > quota) { index = i; break; }
        }
        if (!maximum || (index != NSNotFound && index < maximum)) {
            maximum = index;
            if (!maximum && files.count && !files[0].isArchived) maximum = 1;
        }
    }
    if (maximum == NSNotFound) return;
    for (NSUInteger i = maximum; i < files.count; i++) {
        JCORELogFileInfo *info = files[i];
        NSError *error;
        if (![[NSFileManager defaultManager] removeItemAtPath:info.filePath error:&error]) {
            NSLog(@"DDLogFileManagerDefault: Error deleting file %@", error);
        } else NSLog(@"DDLogFileManagerDefault: Deleting file: %@", info.fileName);
    }
}
- (NSString *)defaultLogsDirectory {
    return [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES).firstObject stringByAppendingPathComponent:@"Logs"];
}
- (NSString *)logsDirectory {
    NSError *error;
    if (![[NSFileManager defaultManager] createDirectoryAtPath:_logsDirectory withIntermediateDirectories:YES attributes:nil error:&error]) {
        NSLog(@"DDFileLogManagerDefault: Error creating logsDirectory: %@", error);
    }
    return _logsDirectory;
}
- (BOOL)isLogFile:(NSString *)name {
    return (unsigned int)[name hasPrefix:[self.applicationName stringByAppendingString:@" "]] & (unsigned int)[name hasSuffix:@".log"];
}
- (NSDateFormatter *)logFileDateFormatter { return _fileDateFormatter; }
- (NSArray *)unsortedLogFilePaths {
    NSString *directory = self.logsDirectory;
    NSArray *names = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:directory error:NULL];
    NSMutableArray *result = [NSMutableArray arrayWithCapacity:names.count];
    for (NSString *name in names) if ([self isLogFile:name]) [result addObject:[directory stringByAppendingPathComponent:name]];
    return result;
}
- (NSArray *)unsortedLogFileNames {
    NSArray *paths = self.unsortedLogFilePaths;
    NSMutableArray *result = [NSMutableArray arrayWithCapacity:paths.count];
    for (NSString *path in paths) [result addObject:path.lastPathComponent];
    return result;
}
- (NSArray *)unsortedLogFileInfos {
    NSArray *paths = self.unsortedLogFilePaths;
    NSMutableArray *result = [NSMutableArray arrayWithCapacity:paths.count];
    for (NSString *path in paths) [result addObject:[[JCORELogFileInfo alloc] initWithFilePath:path]];
    return result;
}
- (NSArray *)sortedLogFilePaths {
    NSArray *infos = self.sortedLogFileInfos;
    NSMutableArray *result = [NSMutableArray arrayWithCapacity:infos.count];
    for (JCORELogFileInfo *info in infos) [result addObject:info.filePath];
    return result;
}
- (NSArray *)sortedLogFileNames {
    NSArray *infos = self.sortedLogFileInfos;
    NSMutableArray *result = [NSMutableArray arrayWithCapacity:infos.count];
    for (JCORELogFileInfo *info in infos) [result addObject:info.fileName];
    return result;
}
- (NSArray *)sortedLogFileInfos {
    return [self.unsortedLogFileInfos sortedArrayUsingComparator:^NSComparisonResult(JCORELogFileInfo *a, JCORELogFileInfo *b) {
        NSDate *dateA = [NSDate new];
        NSDate *dateB = [NSDate new];
        NSArray *components = [a.fileName componentsSeparatedByString:@" "];
        if (components.count) {
            NSString *stamp = [components.lastObject stringByReplacingOccurrencesOfString:@".log" withString:@""];
#if TARGET_OS_SIMULATOR
            stamp = [stamp stringByReplacingOccurrencesOfString:@".archived" withString:@""];
#endif
            dateA = [self.logFileDateFormatter dateFromString:stamp] ?: a.creationDate;
        }
        components = [b.fileName componentsSeparatedByString:@" "];
        if (components.count) {
            NSString *stamp = [components.lastObject stringByReplacingOccurrencesOfString:@".log" withString:@""];
#if TARGET_OS_SIMULATOR
            stamp = [stamp stringByReplacingOccurrencesOfString:@".archived" withString:@""];
#endif
            dateB = [self.logFileDateFormatter dateFromString:stamp] ?: b.creationDate;
        }
        if (!dateA) dateA = [NSDate new];
        return [dateB compare:dateA];
    }];
}
- (NSString *)newLogFileName { return [NSString stringWithFormat:@"%@ %@.log", self.applicationName, [self.logFileDateFormatter stringFromDate:[NSDate date]]]; }
- (NSString *)logFileHeader { return nil; }
- (NSData *)logFileHeaderData {
    NSString *header = self.logFileHeader;
    if (!header.length) return nil;
    if (![header hasSuffix:@"\n"]) header = [header stringByAppendingString:@"\n"];
    return [header dataUsingEncoding:NSUTF8StringEncoding];
}
- (NSString *)createNewLogFileWithError:(NSError **)error {
    NSString *originalName = self.newLogFileName;
    NSString *directory = self.logsDirectory;
    NSData *header = self.logFileHeaderData ?: [NSData new];
    NSUInteger failures = 0;
    NSUInteger attempt = 1;
    NSError *lastError;
    do {
        NSString *name = originalName;
        if (attempt > 1) {
            NSString *extension = name.pathExtension;
            name = [[name stringByDeletingPathExtension] stringByAppendingFormat:@" %lu", (unsigned long)attempt];
            if (extension.length) name = [name stringByAppendingPathExtension:extension];
        }
        NSString *path = [directory stringByAppendingPathComponent:name];
        NSError *currentError;
        if ([header writeToFile:path options:NSDataWritingAtomic error:&currentError] &&
            [[NSFileManager defaultManager] setAttributes:@{NSFileProtectionKey: self.logFileProtection} ofItemAtPath:path error:&currentError]) {
            NSLog(@"DDLogFileManagerDefault: Created new log file: %@", originalName);
            dispatch_async(dispatch_get_global_queue(0, 0), ^{ [self deleteOldLogFiles]; });
            return path;
        }
        if (currentError.code == NSFileWriteFileExistsError) attempt++;
        else {
            NSLog(@"DDLogFileManagerDefault: Critical error while creating log file: %@", currentError);
            failures++;
            lastError = currentError;
        }
    } while (failures < 5);
    NSLog(@"DDLogFileManagerDefault: Bailing file creation, encountered %ld errors.", (long)failures);
    *error = lastError;
    return nil;
}
- (NSString *)applicationName {
    static NSString *name;
    static dispatch_once_t token;
    dispatch_once(&token, ^{
        name = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleIdentifier"];
        if (!name.length) name = [NSProcessInfo processInfo].processName;
        if (!name.length) name = @"";
    });
    return name;
}
@end

@implementation JCORELogFileFormatterDefault
@dynamic hash, superclass, description, debugDescription;
- (instancetype)init { return [self initWithDateFormatter:nil]; }
- (instancetype)initWithDateFormatter:(NSDateFormatter *)formatter {
    if ((self = [super init])) {
        _dateFormatter = formatter;
        if (!formatter) {
            _dateFormatter = [[NSDateFormatter alloc] init];
            _dateFormatter.formatterBehavior = NSDateFormatterBehavior10_4;
            _dateFormatter.locale = [NSLocale localeWithLocaleIdentifier:@"en_US_POSIX"];
            _dateFormatter.timeZone = [NSTimeZone timeZoneForSecondsFromGMT:0];
            _dateFormatter.dateFormat = @"yyyy/MM/dd HH:mm:ss:SSS";
        }
    }
    return self;
}
- (NSString *)formatLogMessage:(JCORELogMessage *)message {
    return [NSString stringWithFormat:@"%@  %@", [_dateFormatter stringFromDate:message.timestamp], message.message];
}
@end

@implementation JCOREFileLogger
@dynamic hash, superclass, description, debugDescription;
@synthesize doNotReuseLogFiles = _doNotReuseLogFiles;
@synthesize logFileManager = _logFileManager;
@synthesize automaticallyAppendNewlineForCustomFormatters = _automaticallyAppendNewlineForCustomFormatters;
@dynamic logFormatter, loggerQueue;
- (instancetype)init { return [self initWithLogFileManager:[[JCORELogFileManagerDefault alloc] init] completionQueue:nil]; }
- (instancetype)initWithLogFileManager:(id<JCORELogFileManager>)manager { return [self initWithLogFileManager:manager completionQueue:nil]; }
- (instancetype)initWithLogFileManager:(id<JCORELogFileManager>)manager completionQueue:(dispatch_queue_t)queue {
    if ((self = [super init])) {
        _completionQueue = queue ?: dispatch_get_global_queue(0, 0);
        _maximumFileSize = kJDDDefaultLogMaxFileSize;
        _rollingFrequency = kJDDDefaultLogRollingFrequency;
        _automaticallyAppendNewlineForCustomFormatters = YES;
        _logFileManager = manager;
        _logFormatter = [JCORELogFileFormatterDefault new];
    }
    return self;
}
- (void)lt_cleanup {
    [_currentLogFileHandle synchronizeFile];
    [_currentLogFileHandle closeFile];
    if (_currentLogFileVnode) { dispatch_source_cancel(_currentLogFileVnode); _currentLogFileVnode = nil; }
    if (_rollingTimer) { dispatch_source_cancel(_rollingTimer); _rollingTimer = nil; }
}
- (void)dealloc {
    if (self.isOnInternalLoggerQueue) [self lt_cleanup];
    else {
        dispatch_sync(self.loggerQueue, ^{ [self lt_cleanup]; });
    }
}
- (unsigned long long)maximumFileSize {
    __block unsigned long long value = 0;
    dispatch_block_t block = ^{ value = self->_maximumFileSize; };
    dispatch_sync([JCORELog loggingQueue], ^{ dispatch_sync(self.loggerQueue, block); });
    return value;
}
- (void)setMaximumFileSize:(unsigned long long)value {
    dispatch_block_t block = ^{ @autoreleasepool { self->_maximumFileSize = value; [self lt_maybeRollLogFileDueToSize]; } };
    dispatch_async([JCORELog loggingQueue], ^{ dispatch_async(self.loggerQueue, block); });
}
- (NSTimeInterval)rollingFrequency {
    __block NSTimeInterval value = 0;
    dispatch_block_t block = ^{ value = self->_rollingFrequency; };
    dispatch_sync([JCORELog loggingQueue], ^{ dispatch_sync(self.loggerQueue, block); });
    return value;
}
- (void)setRollingFrequency:(NSTimeInterval)value {
    dispatch_block_t block = ^{ @autoreleasepool { self->_rollingFrequency = value; [self lt_maybeRollLogFileDueToAge]; } };
    dispatch_async([JCORELog loggingQueue], ^{ dispatch_async(self.loggerQueue, block); });
}
- (void)lt_scheduleTimerToRollLogFileDueToAge {
    if (_rollingTimer) { dispatch_source_cancel(_rollingTimer); _rollingTimer = nil; }
    if (_currentLogFileInfo && _rollingFrequency > 0) {
        NSDate *creation = _currentLogFileInfo.creationDate;
        NSTimeInterval frequency = fmin(_rollingFrequency, DBL_MAX - creation.timeIntervalSinceReferenceDate);
        NSDate *rollDate = [creation dateByAddingTimeInterval:frequency];
        _rollingTimer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, _loggerQueue);
        __weak JCOREFileLogger *logger = self;
        dispatch_source_set_event_handler(_rollingTimer, ^{ @autoreleasepool { [logger lt_maybeRollLogFileDueToAge]; } });
        NSTimeInterval interval = fmin(rollDate.timeIntervalSinceNow, (double)(INT64_MAX / NSEC_PER_SEC));
        dispatch_source_set_timer(_rollingTimer, dispatch_time(DISPATCH_TIME_NOW, (int64_t)(interval * NSEC_PER_SEC)), DISPATCH_TIME_FOREVER, NSEC_PER_SEC);
        if (@available(iOS 10.0, macOS 10.12, *)) dispatch_activate(_rollingTimer);
        else dispatch_resume(_rollingTimer);
    }
}
- (void)rollLogFile { [self rollLogFileWithCompletionBlock:nil]; }
- (void)rollLogFileWithCompletionBlock:(dispatch_block_t)completion {
    dispatch_block_t block = ^{ @autoreleasepool {
        [self lt_rollLogFileNow];
        if (completion) dispatch_async(self->_completionQueue, ^{ completion(); });
    } };
    if (self.isOnInternalLoggerQueue) block();
    else dispatch_async([JCORELog loggingQueue], ^{ dispatch_async(self.loggerQueue, block); });
}
- (void)lt_rollLogFileNow {
    if (!_currentLogFileHandle) return;
    [_currentLogFileHandle synchronizeFile];
    [_currentLogFileHandle closeFile];
    _currentLogFileHandle = nil;
    _currentLogFileInfo.isArchived = YES;
    if ([_logFileManager respondsToSelector:@selector(didRollAndArchiveLogFile:)]) {
        NSString *path = [_currentLogFileInfo.filePath copy];
        _currentLogFileInfo = nil;
        dispatch_async(_completionQueue, ^{ [self->_logFileManager didRollAndArchiveLogFile:path]; });
    } else _currentLogFileInfo = nil;
    if (_currentLogFileVnode) { dispatch_source_cancel(_currentLogFileVnode); _currentLogFileVnode = nil; }
    if (_rollingTimer) { dispatch_source_cancel(_rollingTimer); _rollingTimer = nil; }
}
- (void)lt_maybeRollLogFileDueToAge {
    if (_rollingFrequency <= 0 || _currentLogFileInfo.age + kJDDRollingLeeway < _rollingFrequency) [self lt_scheduleTimerToRollLogFileDueToAge];
    else [self lt_rollLogFileNow];
}
- (void)lt_maybeRollLogFileDueToSize {
    if (_maximumFileSize && [_currentLogFileHandle offsetInFile] >= _maximumFileSize) [self lt_rollLogFileNow];
}
- (BOOL)lt_shouldLogFileBeArchived:(JCORELogFileInfo *)info {
    if (!info.isArchived) {
        if ([self shouldArchiveRecentLogFileInfo:info] || (_maximumFileSize && info.fileSize >= _maximumFileSize) ||
            (_rollingFrequency > 0 && info.age >= _rollingFrequency)) return YES;
        if (jdoesAppRunInBackground()) {
            NSString *protection = info.fileAttributes[NSFileProtectionKey];
            BOOL firstAuthentication = [protection isEqualToString:NSFileProtectionCompleteUntilFirstUserAuthentication];
            BOOL noProtection = [protection isEqualToString:NSFileProtectionNone];
            return protection && !firstAuthentication && !noProtection;
        }
    }
    return NO;
}
- (JCORELogFileInfo *)currentLogFileInfo {
    __block JCORELogFileInfo *value;
    dispatch_block_t block = ^{ value = [self lt_currentLogFileInfo]; };
    dispatch_sync([JCORELog loggingQueue], ^{ dispatch_sync(self->_loggerQueue, block); });
    return value;
}
- (JCORELogFileInfo *)lt_currentLogFileInfo {
    JCORELogFileInfo *info = _currentLogFileInfo;
    if (!info) {
        info = _logFileManager.sortedLogFileInfos.firstObject;
        if (info && [self lt_shouldUseLogFile:info isResuming:YES]) {
            NSLog(@"DDFileLogger: Resuming logging with file %@", info.fileName);
            _currentLogFileInfo = info;
            return info;
        }
    } else if ([self lt_shouldUseLogFile:info isResuming:NO]) {
        _currentLogFileInfo = info;
        return _currentLogFileInfo;
    }
    NSString *path;
    if ([_logFileManager respondsToSelector:@selector(createNewLogFileWithError:)]) {
        NSError *error;
        path = [_logFileManager createNewLogFileWithError:&error];
        if (!path) NSLog(@"DDFileLogger: Failed to create new log file: %@", error);
    } else path = [_logFileManager createNewLogFile];
    _currentLogFileInfo = [JCORELogFileInfo logFileWithPath:path];
    return _currentLogFileInfo;
}
- (BOOL)lt_shouldUseLogFile:(JCORELogFileInfo *)info isResuming:(BOOL)resuming {
    if (info.isArchived) return NO;
    if (resuming && (_doNotReuseLogFiles || [self lt_shouldLogFileBeArchived:info])) {
        info.isArchived = YES;
        if ([_logFileManager respondsToSelector:@selector(didArchiveLogFile:)]) {
            NSString *path = [info.filePath copy];
            dispatch_async(_completionQueue, ^{ [self->_logFileManager didArchiveLogFile:path]; });
        }
        return NO;
    }
    return YES;
}
- (void)lt_monitorCurrentLogFileForExternalChanges {
    _currentLogFileVnode = dispatch_source_create(DISPATCH_SOURCE_TYPE_VNODE, (uintptr_t)_currentLogFileHandle.fileDescriptor,
                                                 DISPATCH_VNODE_DELETE | DISPATCH_VNODE_RENAME | DISPATCH_VNODE_REVOKE, _loggerQueue);
    __weak JCOREFileLogger *logger = self;
    dispatch_source_set_event_handler(_currentLogFileVnode, ^{ @autoreleasepool { [logger lt_rollLogFileNow]; } });
    if (@available(iOS 10.0, macOS 10.12, *)) dispatch_activate(_currentLogFileVnode);
    else dispatch_resume(_currentLogFileVnode);
}
- (NSFileHandle *)lt_currentLogFileHandle {
    if (!_currentLogFileHandle) {
        _currentLogFileHandle = [NSFileHandle fileHandleForWritingAtPath:[self lt_currentLogFileInfo].filePath];
        [_currentLogFileHandle seekToEndOfFile];
        if (_currentLogFileHandle) {
            [self lt_scheduleTimerToRollLogFileDueToAge];
            [self lt_monitorCurrentLogFileForExternalChanges];
        }
    }
    return _currentLogFileHandle;
}
- (void)logMessage:(JCORELogMessage *)message {
    NSData *data = [self lt_dataForMessage:message];
    if (data.length) [self lt_logData:data];
}
- (void)willLogMessage:(__unsafe_unretained JCORELogFileInfo *)info {}
- (void)didLogMessage:(__unsafe_unretained JCORELogFileInfo *)info { [self lt_maybeRollLogFileDueToSize]; }
- (BOOL)shouldArchiveRecentLogFileInfo:(__unsafe_unretained JCORELogFileInfo *)info { return NO; }
- (void)willRemoveLogger { [self lt_rollLogFileNow]; }
- (void)flush {
    dispatch_block_t block = ^{ @autoreleasepool { [self lt_flush]; } };
    if (self.isOnInternalLoggerQueue) block();
    else dispatch_sync([JCORELog loggingQueue], ^{ dispatch_sync(self.loggerQueue, block); });
}
- (void)lt_flush { [_currentLogFileHandle synchronizeFile]; }
- (NSString *)loggerName { return JCORELoggerNameFile; }
- (id)wrapWithBuffer { return [[JCOREBufferedProxy alloc] initWithFileLogger:self]; }
- (id)unwrapFromBuffer { return self; }
- (void)logData:(NSData *)data {
    dispatch_block_t block = ^{ @autoreleasepool { [self lt_logData:data]; } };
    if (self.isOnInternalLoggerQueue) block();
    else dispatch_sync([JCORELog loggingQueue], ^{ dispatch_sync(self.loggerQueue, block); });
}
- (void)dummyMethod {}
- (NSMethodSignature *)methodSignatureForSelector:(SEL)selector {
    if (selector == NSSelectorFromString(@"willLogMessage") || selector == NSSelectorFromString(@"didLogMessage")) return [self methodSignatureForSelector:@selector(dummyMethod)];
    return [super methodSignatureForSelector:selector];
}
- (void)forwardInvocation:(NSInvocation *)invocation {
    if (invocation.selector != @selector(dummyMethod)) [super forwardInvocation:invocation];
}
- (void)lt_logData:(NSData *)data {
    static dispatch_once_t token;
    static BOOL deprecatedWillLog, deprecatedDidLog;
    dispatch_once(&token, ^{
        deprecatedWillLog = [self respondsToSelector:NSSelectorFromString(@"willLogMessage")];
        deprecatedDidLog = [self respondsToSelector:NSSelectorFromString(@"didLogMessage")];
    });
    if (data.length) {
        @try {
            if (deprecatedWillLog) ((void (*)(id, SEL))objc_msgSend)(self, NSSelectorFromString(@"willLogMessage"));
            else [self willLogMessage:_currentLogFileInfo];
            NSFileHandle *handle = [self lt_currentLogFileHandle];
            [handle seekToEndOfFile];
            [handle writeData:data];
            if (deprecatedDidLog) ((void (*)(id, SEL))objc_msgSend)(self, NSSelectorFromString(@"didLogMessage"));
            else [self didLogMessage:_currentLogFileInfo];
        } @catch (NSException *exception) {
            static int exceptionCount;
            if (exceptionCount++ < 10) {
                NSLog(@"DDFileLogger.logMessage: %@", exception);
                if (exceptionCount == 10) NSLog(@"DDFileLogger.logMessage: Too many exceptions -- will not log any more of them.");
            }
        }
    }
}
- (NSData *)lt_dataForMessage:(JCORELogMessage *)message {
    NSString *text = message.message;
    if (_logFormatter) {
        text = [_logFormatter formatLogMessage:message];
        NSString *original = message.message;
        if (!text.length) return nil;
        if (text != original && !_automaticallyAppendNewlineForCustomFormatters) return [text dataUsingEncoding:NSUTF8StringEncoding];
    } else if (!text.length) return nil;
    if (![text hasSuffix:@"\n"]) text = [text stringByAppendingString:@"\n"];
    return [text dataUsingEncoding:NSUTF8StringEncoding];
}
@end

static NSUInteger JCOREDefaultBufferSize(void) {
    static NSUInteger size;
    static dispatch_once_t token;
    dispatch_once(&token, ^{
        size = 4096;
        struct statfs *mounts;
        int count = getmntinfo(&mounts, 0);
        for (int i = 0; i < count; i++) {
            if (strnlen(mounts[i].f_mntonname, 2) == 1 && mounts[i].f_mntonname[0] == '/') { size = mounts[i].f_bsize; break; }
        }
    });
    return size;
}
static NSUInteger JCOREMaximumBufferSize(void) {
    static NSUInteger size;
    static dispatch_once_t token;
    dispatch_once(&token, ^{
        size = 1024 * 1024;
        struct statfs *mounts;
        int count = getmntinfo(&mounts, 0);
        for (int i = 0; i < count; i++) {
            if (strnlen(mounts[i].f_mntonname, 2) == 1 && mounts[i].f_mntonname[0] == '/') { size = (NSUInteger)mounts[i].f_iosize; break; }
        }
    });
    return size;
}
@implementation JCOREBufferedProxy
@synthesize fileLogger = _fileLogger, buffer = _buffer, maxBufferSizeBytes = _maxBufferSizeBytes, currentBufferSizeBytes = _currentBufferSizeBytes;
- (instancetype)initWithFileLogger:(JCOREFileLogger *)logger {
    _fileLogger = logger;
    _maxBufferSizeBytes = JCOREDefaultBufferSize();
    [self flushBuffer];
    return self;
}
- (void)dealloc {
    dispatch_block_t block = ^{ [self lt_sendBufferedDataToFileLogger]; self.fileLogger = nil; };
    if (_fileLogger.isOnInternalLoggerQueue) block();
    else dispatch_sync(_fileLogger.loggerQueue, block);
}
- (void)flushBuffer {
    [_buffer close];
    _buffer = [NSOutputStream outputStreamToMemory];
    [_buffer open];
    _currentBufferSizeBytes = 0;
}
- (void)lt_sendBufferedDataToFileLogger {
    [_fileLogger lt_logData:[_buffer propertyForKey:NSStreamDataWrittenToMemoryStreamKey]];
    [self flushBuffer];
}
- (void)logMessage:(JCORELogMessage *)message {
    NSData *data = [_fileLogger lt_dataForMessage:message];
    if (data.length) {
        [data enumerateByteRangesUsingBlock:^(const void *bytes, NSRange range, BOOL *stop) {
            [self->_buffer write:bytes maxLength:range.length];
            self->_currentBufferSizeBytes += range.length;
            if (self->_currentBufferSizeBytes >= self->_maxBufferSizeBytes) [self lt_sendBufferedDataToFileLogger];
        }];
    }
}
- (void)flush {
    dispatch_block_t block = ^{ @autoreleasepool { [self lt_sendBufferedDataToFileLogger]; [self.fileLogger flush]; } };
    if (self.fileLogger.isOnInternalLoggerQueue) block();
    else dispatch_sync([JCORELog loggingQueue], ^{ dispatch_sync(self.fileLogger.loggerQueue, block); });
}
- (void)setMaxBufferSizeBytes:(NSUInteger)size { _maxBufferSizeBytes = MIN(size, JCOREMaximumBufferSize()); }
- (id)wrapWithBuffer { return self; }
- (id)unwrapFromBuffer { return self.fileLogger; }
- (NSMethodSignature *)methodSignatureForSelector:(SEL)selector { return [self.fileLogger methodSignatureForSelector:selector]; }
- (BOOL)respondsToSelector:(SEL)selector { return [self.fileLogger respondsToSelector:selector]; }
- (void)forwardInvocation:(NSInvocation *)invocation { [invocation invokeWithTarget:self.fileLogger]; }
@end
