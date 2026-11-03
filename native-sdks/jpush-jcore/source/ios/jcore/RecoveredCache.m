#import "RecoveredCache.h"
#import "JCORELog.h"
#import "JCOREDataSecurity.h"
#import "JCOREUtilities.h"
#import "JCOREClientInfo.h"
#import <objc/runtime.h>
#import <CoreFoundation/CoreFoundation.h>

@implementation JCORECacheEntity
@synthesize key = _key, dirName = _dirName, content = _content;
@synthesize encrypt = _encrypt, compress = _compress, type = _type, sync = _sync;
@dynamic hash, superclass, description, debugDescription;
- (id)copyWithZone:(NSZone *)zone {
    JCORECacheEntity *copy = [[[self class] allocWithZone:zone] init];
    copy->_key = [_key copy];
    copy->_dirName = [_dirName copy];
    copy->_content = [_content copy];
    copy->_compress = _compress;
    copy->_encrypt = _encrypt;
    copy->_type = _type;
    // The original deliberately does not copy _sync.
    return copy;
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (self) {
        unsigned int count = 0;
        Ivar *ivars = class_copyIvarList([self class], &count);
        for (unsigned int i = 0; i < count; i++) {
            NSString *key = [NSString stringWithUTF8String:ivar_getName(ivars[i])];
            [self setValue:[coder decodeObjectForKey:key] forKey:key];
        }
        free(ivars);
    }
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder {
    unsigned int count = 0;
    Ivar *ivars = class_copyIvarList([self class], &count);
    for (unsigned int i = 0; i < count; i++) {
        NSString *key = [NSString stringWithUTF8String:ivar_getName(ivars[i])];
        [coder encodeObject:[self valueForKey:key] forKey:key];
    }
    free(ivars);
}
@end

static void CacheRunLoopTimerCallBack(CFRunLoopTimerRef timer, void *info) {}
@implementation JCORECacheThread
+ (instancetype)thread { return [[JCORECacheThread alloc] initWithTarget:self selector:@selector(run) object:nil]; }
+ (void)run {
    @autoreleasepool {
        [NSThread currentThread].name = @"io.jiguang.hxhg.cache";
        CFRunLoopTimerContext context = {0, (__bridge void *)self, NULL, NULL, NULL};
        CFRunLoopTimerRef timer = CFRunLoopTimerCreate(NULL, 0, 1000, 0, 0, CacheRunLoopTimerCallBack, &context);
        CFRunLoopAddTimer(CFRunLoopGetCurrent(), timer, kCFRunLoopDefaultMode);
        CFRunLoopRun();
        CFRelease(timer);
    }
}
@end

static BOOL CacheHasCallback(id block) { return block != nil && ![block isKindOfClass:[NSNull class]]; }
@implementation JCORECache
+ (void)handleWriteCompleteInfo:(id)info {
    if ([info isKindOfClass:[NSDictionary class]]) {
        id result = info[@"result"];
        id completion = info[@"completion"];
        if (CacheHasCallback(completion) && [result isKindOfClass:[NSNumber class]]) ((RecoveryCacheWriteCompletion)completion)([result boolValue]);
    }
}
+ (void)write:(JCORECacheEntity *)object completion:(RecoveryCacheWriteCompletion)completion {
    @autoreleasepool {
        RecoveryCacheWriteCompletion wrapped = ^(BOOL result) { if (CacheHasCallback(completion)) completion(result); };
        __weak NSThread *thread = [NSThread currentThread];
        RecoveryCacheWriteCompletion routed = ^(BOOL result) {
            if ([[NSThread currentThread] isEqual:thread]) wrapped(result);
            else [self performSelector:@selector(handleWriteCompleteInfo:) onThread:thread withObject:@{@"completion":wrapped, @"result":@(result)} waitUntilDone:NO];
        };
        if (object.sync) routed([JCORECacheController write:object]);
        else [JCORECacheController write:object completion:routed];
    }
}
+ (void)handleReadCompleteInfo:(id)info {
    if ([info isKindOfClass:[NSDictionary class]]) {
        id completion = info[@"completion"];
        if (CacheHasCallback(completion)) ((RecoveryCacheReadCompletion)completion)(info[@"result"]);
    }
}
+ (void)read:(JCORECacheEntity *)object resultHandler:(RecoveryCacheReadCompletion)handler {
    @autoreleasepool {
        __weak NSThread *thread = [NSThread currentThread];
        RecoveryCacheReadCompletion routed = ^(id result) {
            if (CacheHasCallback(handler)) {
                if ([[NSThread currentThread] isEqual:thread]) handler(result);
                else {
                    NSDictionary *info = result ? @{@"completion":handler, @"result":result} : @{@"completion":handler};
                    [self performSelector:@selector(handleReadCompleteInfo:) onThread:thread withObject:info waitUntilDone:NO];
                }
            } else {
                [JCORELog log:YES level:0x3ff flag:0x40 context:0 file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Cache/JCORECache.m" function:"+[JCORECache read:resultHandler:]_block_invoke" line:115 tag:nil format:@"read error, resultHandler is nil"];
            }
        };
        if (object.sync) routed([JCORECacheController read:object]);
        else [JCORECacheController read:object resultHandler:routed];
    }
}
@end

#define CacheBasicFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Cache/Model/JCOREBasicCache.m"
#define CacheBasicLog(fn, sourceLine, messageFormat, ...) [JCORELog log:YES level:0x3ff flag:0x40 context:0 file:CacheBasicFile function:fn line:sourceLine tag:nil format:messageFormat, ##__VA_ARGS__]
@implementation JCOREBasicCache
@synthesize persistent = _persistent, dir = _dir, mutex = _mutex;
- (BOOL)createDirectory:(NSString *)path {
    if ([[NSFileManager defaultManager] fileExistsAtPath:path]) return YES;
    NSError *error = nil;
    [[NSFileManager defaultManager] createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:&error];
    if (error) { CacheBasicLog("-[JCOREBasicCache createDirectory:]", 30, @"create file directory error:%@", error.description); return NO; }
    return YES;
}
- (NSString *)path {
    if (!_path) {
        NSString *base = _persistent ? [JCOREUtilities libraryDirectoryPath] : [JCOREUtilities cachesDirectoryPath];
        if (!base) CacheBasicLog("-[JCOREBasicCache path]", 45, @"search file directory error");
        else {
            NSString *appKey = [JCOREClientInfo appKey];
            if (!appKey.length) {
                id stored = [[NSUserDefaults standardUserDefaults] objectForKey:@"JIGUANG_APPKEY"];
                if ([stored isKindOfClass:[NSString class]]) appKey = [stored lowercaseString];
            }
            base = [base stringByAppendingPathComponent:appKey ?: @"JCOREDocument"];
            if ([self createDirectory:base]) {
                if ([_dir isKindOfClass:[NSString class]] && _dir.length) {
                    base = [base stringByAppendingPathComponent:_dir];
                    if (![self createDirectory:base]) return nil;
                }
                _path = base;
            }
        }
    }
    return _path;
}
- (BOOL)write:(id<JCORECacheProtocol>)object { return [self write:object completion:nil]; }
- (BOOL)write:(id<JCORECacheProtocol>)object completion:(RecoveryCacheWriteCompletion)completion {
    if (!object) CacheBasicLog("-[JCOREBasicCache write:completion:]", 81, @"write error, object is nil");
    else if (!object.key || !object.key.length) CacheBasicLog("-[JCOREBasicCache write:completion:]", 88, @"write error, key is nil");
    else {
        pthread_mutex_lock(&_mutex); NSString *path = self.path; pthread_mutex_unlock(&_mutex);
        if (!path) { CacheBasicLog("-[JCOREBasicCache write:completion:]", 102, @"write error, path is nil"); if (CacheHasCallback(completion)) completion(NO); }
        return path != nil;
    }
    if (CacheHasCallback(completion)) completion(NO);
    return NO;
}
- (id)read:(id<JCORECacheProtocol>)object {
    if (!object) CacheBasicLog("-[JCOREBasicCache read:]", 113, @"read error, object is nil");
    else if (object.key && object.key.length) {
        pthread_mutex_lock(&_mutex); NSString *path = self.path; pthread_mutex_unlock(&_mutex);
        if (!path) { CacheBasicLog("-[JCOREBasicCache read:]", 146, @"read error, path is nil"); return nil; }
        return @YES;
    } else if (object.dirName && object.dirName.length) {
        pthread_mutex_lock(&_mutex);
        NSError *error = nil;
        NSArray *files = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:self.path error:&error];
        pthread_mutex_unlock(&_mutex);
        if (error) { CacheBasicLog("-[JCOREBasicCache read:]", 130, @"read dir error:%@", error.description); return nil; }
        return files;
    } else CacheBasicLog("-[JCOREBasicCache read:]", 118, @"read error, key is nil");
    return nil;
}
- (BOOL)read:(id<JCORECacheProtocol>)object resultHandler:(RecoveryCacheReadCompletion)handler {
    if (!CacheHasCallback(handler)) { CacheBasicLog("-[JCOREBasicCache read:resultHandler:]", 154, @"read error, resultHandler is nil"); return NO; }
    if (!object) CacheBasicLog("-[JCOREBasicCache read:resultHandler:]", 158, @"read error, object is nil");
    else if (object.key && object.key.length) {
        pthread_mutex_lock(&_mutex); NSString *path = self.path; pthread_mutex_unlock(&_mutex);
        if (!path) { CacheBasicLog("-[JCOREBasicCache read:resultHandler:]", 180, @"read error, path is nil"); handler(nil); }
        return path != nil;
    } else if (object.dirName && object.dirName.length) { [self readDirectoryWithHandler:handler]; return NO; }
    else CacheBasicLog("-[JCOREBasicCache read:resultHandler:]", 164, @"read error, key is nil");
    handler(nil);
    return NO;
}
- (void)readDirectoryWithHandler:(RecoveryCacheReadCompletion)handler {
    pthread_mutex_lock(&_mutex);
    NSError *error = nil;
    NSArray *files = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:self.path error:&error];
    pthread_mutex_unlock(&_mutex);
    if (error) { CacheBasicLog("-[JCOREBasicCache readDirectoryWithHandler:]", 197, @"read dir error:%@", error.description); files = nil; }
    handler(files);
}
@end

static void CacheCopyModel(id<JCORECacheProtocol> object, JCORECacheModel *model) {
    model.key = object.key; model.dirName = object.dirName; model.type = object.type;
    model.encrypt = object.encrypt; model.compress = object.compress;
}
@implementation JCORECacheController
@synthesize thread = _thread;
+ (instancetype)instance {
    static JCORECacheController *controller;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ controller = [[JCORECacheController alloc] init]; });
    return controller;
}
- (instancetype)init {
    self = [super init];
    if (self) { _thread = [JCORECacheThread thread]; [_thread start]; pthread_mutex_init(&_mutex, NULL); }
    return self;
}
- (void)dealloc { pthread_mutex_destroy(&_mutex); }
- (BOOL)isLargeData:(long long)type { return (type & ~2LL) == 1; }
- (BOOL)isPersistentData:(long long)type { return (type & ~1LL) == 2; }
- (JCOREBasicCache *)cacheObject:(id<JCORECacheProtocol>)object {
    JCOREBasicCache *cache = [self isLargeData:object.type] ? [[JCORELargeCache alloc] init] : [[JCORELightCache alloc] init];
    cache.persistent = [self isPersistentData:object.type]; cache.dir = object.dirName; cache.mutex = _mutex;
    return cache;
}
- (void)asyncHandleCompletion:(void (^)(void))completion { if (completion) completion(); }
- (void)write:(JCORECacheWriteModel *)object {
    RecoveryCacheWriteCompletion block = object.block;
    JCOREBasicCache *cache = [self cacheObject:object];
    if (cache) [cache write:object completion:block];
    else if (CacheHasCallback(block)) block(NO);
}
- (void)read:(JCORECacheReadModel *)object {
    RecoveryCacheReadCompletion block = object.block;
    JCOREBasicCache *cache = [self cacheObject:object];
    if (cache) [cache read:object resultHandler:block];
    else if (CacheHasCallback(block)) block(nil);
}
- (void)performSel:(SEL)selector withObject:(id)object { [self performSelector:selector onThread:self.thread withObject:object waitUntilDone:NO]; }
+ (void)performSel:(SEL)selector withObject:(id)object { [[self instance] performSel:selector withObject:object]; }
+ (void)write:(id<JCORECacheProtocol>)object completion:(RecoveryCacheWriteCompletion)completion {
    JCORECacheWriteModel *model = [JCORECacheWriteModel new]; CacheCopyModel(object, model); model.content = object.content; model.block = completion;
    [self performSel:@selector(write:) withObject:model];
}
+ (void)read:(id<JCORECacheProtocol>)object resultHandler:(RecoveryCacheReadCompletion)handler {
    JCORECacheReadModel *model = [JCORECacheReadModel new]; CacheCopyModel(object, model); model.block = handler;
    [self performSel:@selector(read:) withObject:model];
}
+ (BOOL)write:(id<JCORECacheProtocol>)object {
    JCORECacheWriteModel *model = [JCORECacheWriteModel new]; CacheCopyModel(object, model); model.content = object.content;
    return [[[self instance] cacheObject:model] write:model];
}
+ (id)read:(id<JCORECacheProtocol>)object {
    JCORECacheReadModel *model = [JCORECacheReadModel new]; CacheCopyModel(object, model);
    return [[[self instance] cacheObject:model] read:model];
}
@end

static NSString *CacheSecurityKey(void) {
    return [[NSString alloc] initWithData:[[NSData alloc] initWithBase64EncodedString:@"SlBVU0hfU0VDVVJJVFlfQUVTX0tFWV9PTkVfVEVNUF8=" options:0] encoding:NSUTF8StringEncoding];
}
@implementation JCORELightCache
@synthesize allDatas = _allDatas;
- (NSString *)path {
    if (!_path) { NSString *path = [super path]; if (path) _path = [path stringByAppendingPathComponent:@"io.jiguang.plist"]; }
    return _path;
}
- (NSMutableDictionary *)allDatas {
    if (!_allDatas) {
        NSData *data = [NSData dataWithContentsOfFile:self.path options:0 error:NULL];
        id json = [JCOREUtilities dataToJSON:[JCOREDataSecurity AES256DecryptData:data withKey:CacheSecurityKey()]];
        if ([json isKindOfClass:[NSDictionary class]]) _allDatas = [NSMutableDictionary dictionaryWithDictionary:json];
        if (!_allDatas) _allDatas = [NSMutableDictionary dictionary];
    }
    return _allDatas;
}
- (BOOL)write:(id<JCORECacheProtocol>)object { return [self write:object completion:nil]; }
- (BOOL)write:(id<JCORECacheProtocol>)object completion:(RecoveryCacheWriteCompletion)completion {
    if (![super write:object completion:completion]) return NO;
    pthread_mutex_lock(&_mutex);
    if (object.content) [self.allDatas setObject:object.content forKey:object.key];
    else [self.allDatas removeObjectForKey:object.key];
    NSData *data = [JCOREUtilities jsonToData:[self.allDatas copy]];
    data = [JCOREDataSecurity AES256EncryptData:data withKey:CacheSecurityKey()];
    BOOL result = [data writeToFile:self.path atomically:YES];
    pthread_mutex_unlock(&_mutex);
    if (CacheHasCallback(completion)) completion(result);
    return result;
}
- (id)read:(id<JCORECacheProtocol>)object {
    id result = [super read:object];
    if ([result isKindOfClass:[NSNumber class]]) {
        pthread_mutex_lock(&_mutex); result = [self.allDatas objectForKey:object.key]; pthread_mutex_unlock(&_mutex);
    }
    return result;
}
- (BOOL)read:(id<JCORECacheProtocol>)object resultHandler:(RecoveryCacheReadCompletion)handler {
    BOOL result = [super read:object resultHandler:handler];
    if (result) {
        pthread_mutex_lock(&_mutex); id value = [self.allDatas objectForKey:object.key]; pthread_mutex_unlock(&_mutex); handler(value);
    }
    return result;
}
@end

#define CacheLargeFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Cache/Model/JCORELargeCache.m"
#define CacheLargeLog(fn, sourceLine, messageFormat, ...) [JCORELog log:YES level:0x3ff flag:0x40 context:0 file:CacheLargeFile function:fn line:sourceLine tag:nil format:messageFormat, ##__VA_ARGS__]
@implementation JCORELargeCache
- (NSString *)path { return [super path]; }
- (BOOL)write:(id<JCORECacheProtocol>)object { return [self write:object completion:nil]; }
- (BOOL)write:(id<JCORECacheProtocol>)object completion:(RecoveryCacheWriteCompletion)completion {
    if (![super write:object completion:completion]) return NO;
    pthread_mutex_lock(&_mutex);
    NSString *path = [self.path stringByAppendingPathComponent:object.key];
    NSError *error = nil;
    BOOL result;
    if (!object.content) {
        result = ![[NSFileManager defaultManager] fileExistsAtPath:path] || [[NSFileManager defaultManager] removeItemAtPath:path error:&error];
    } else {
        id content = object.content;
        // The original calls stringValue for NSNumber but continues to inspect the original content.
        if ([content isKindOfClass:[NSNumber class]]) (void)[content stringValue];
        id data;
        if ([content isKindOfClass:[NSString class]]) data = [content dataUsingEncoding:NSUTF8StringEncoding];
        else if ([content isKindOfClass:[NSData class]]) data = content;
        else data = [JCOREUtilities jsonToData:content];
        if (object.encrypt && [data isKindOfClass:[NSData class]]) data = [JCOREDataSecurity AES256EncryptData:data withKey:CacheSecurityKey()];
        if ([data isKindOfClass:[NSData class]]) {
            @try { result = [data writeToFile:path options:0 error:&error]; }
            @catch (NSException *exception) {
                [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:CacheLargeFile function:"-[JCORELargeCache write:completion:]" line:73 tag:nil format:@"[异常] - JCORELargeCache write"];
                result = NO; error = nil;
            }
        }
        else { CacheLargeLog("-[JCORELargeCache write:completion:]", 77, @"write error, data format error"); result = NO; }
    }
    pthread_mutex_unlock(&_mutex);
    if (CacheHasCallback(completion)) {
        completion(result);
        if (error) CacheLargeLog("-[JCORELargeCache write:completion:]", 86, @"write error, file %@, %@", object.key, error.description);
    }
    return result;
}
- (id)read:(id<JCORECacheProtocol>)object {
    id result = [super read:object];
    if ([result isKindOfClass:[NSNumber class]]) {
        pthread_mutex_lock(&_mutex);
        NSString *path = [self.path stringByAppendingPathComponent:object.key];
        if (![[NSFileManager defaultManager] fileExistsAtPath:path]) { pthread_mutex_unlock(&_mutex); return nil; }
        NSError *error = nil;
        result = [NSData dataWithContentsOfFile:path options:0 error:&error];
        if (object.encrypt) result = [JCOREDataSecurity AES256DecryptData:result withKey:CacheSecurityKey()];
        pthread_mutex_unlock(&_mutex);
        if (error) { CacheLargeLog("-[JCORELargeCache read:]", 122, @"read error, %@", error.description); result = nil; }
    }
    return result;
}
- (BOOL)read:(id<JCORECacheProtocol>)object resultHandler:(RecoveryCacheReadCompletion)handler {
    if (![super read:object resultHandler:handler]) return NO;
    pthread_mutex_lock(&_mutex);
    NSString *path = [self.path stringByAppendingPathComponent:object.key];
    if (![[NSFileManager defaultManager] fileExistsAtPath:path]) { pthread_mutex_unlock(&_mutex); handler(nil); return NO; }
    NSError *error = nil;
    NSData *data = [NSData dataWithContentsOfFile:path options:0 error:&error];
    if (object.encrypt) data = [JCOREDataSecurity AES256DecryptData:data withKey:CacheSecurityKey()];
    pthread_mutex_unlock(&_mutex);
    handler(data);
    if (error) CacheLargeLog("-[JCORELargeCache read:resultHandler:]", 162, @"read error, %@", error.description);
    return error == nil;
}
@end

#define CacheUtilitiesFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Interface/JCORECacheUtilities.m"
@implementation JCORECacheUtilities
+ (void)setCacheObject:(id)object forKey:(NSString *)key {
    JCORECacheEntity *entity = [JCORECacheEntity new]; entity.key = key; entity.sync = YES; entity.content = object; entity.type = 0; entity.dirName = @"JCoreDocument";
    [JCORECache write:entity completion:^(BOOL success) {
        if (!success) [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:CacheUtilitiesFile function:"+[JCORECacheUtilities setCacheObject:forKey:]_block_invoke" line:30 tag:nil format:@"cache data error:%@,%@", key, object];
    }];
}
+ (id)cacheObjectForKey:(NSString *)key {
    JCORECacheEntity *entity = [JCORECacheEntity new]; entity.key = key; entity.sync = YES; entity.type = 0; entity.dirName = @"JCoreDocument";
    __block id result = nil; [JCORECache read:entity resultHandler:^(id value) { result = value; }]; return result;
}
+ (void)setPersistentObject:(id)object forKey:(NSString *)key {
    JCORECacheEntity *entity = [JCORECacheEntity new]; entity.key = key; entity.sync = YES; entity.content = object; entity.type = 2;
    [JCORECache write:entity completion:^(BOOL success) {
        if (!success) [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:CacheUtilitiesFile function:"+[JCORECacheUtilities setPersistentObject:forKey:]_block_invoke" line:56 tag:nil format:@"cache persistent data error:%@,%@", key, object];
    }];
}
+ (id)persistentObjectForKey:(NSString *)key {
    JCORECacheEntity *entity = [JCORECacheEntity new]; entity.key = key; entity.sync = YES; entity.type = 2;
    __block id result = nil; [JCORECache read:entity resultHandler:^(id value) { result = value; }]; return result;
}
+ (long long)getMaxCount { return 1000; }
+ (id)clearOldestReportObjects:(id)objects {
    if (![objects isKindOfClass:[NSArray class]]) return objects;
    NSMutableArray *sorted = [[NSMutableArray alloc] initWithArray:objects];
    [sorted sortUsingComparator:^NSComparisonResult(id a, id b) {
        if (![a isKindOfClass:[NSString class]] || ![b isKindOfClass:[NSString class]]) return NSOrderedSame;
        NSArray *aa = [a componentsSeparatedByString:@":"]; NSArray *bb = [b componentsSeparatedByString:@":"];
        long long lastA = [aa.lastObject longLongValue], lastB = [bb.lastObject longLongValue];
        // Original comparator returns only 0 or 1, including for equality and invalid entries.
        return lastA == lastB ? [aa.firstObject longLongValue] < [bb.firstObject longLongValue] : lastA < lastB;
    }];
    NSArray *snapshot = [sorted copy];
    if ((long long)sorted.count > [self getMaxCount]) {
        for (NSUInteger i = (NSUInteger)[self getMaxCount]; i < snapshot.count; i++) {
            id key = snapshot[i];
            [JCORECache write:[self reportCacheEntityWithKey:key content:nil] completion:^(BOOL success) {
                if (success) [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:CacheUtilitiesFile function:"+[JCORECacheUtilities clearOldestReportObjects:]_block_invoke_2" line:122 tag:nil format:@"clear oldest report data:%@", key];
            }];
            [sorted removeObject:key];
        }
        return [sorted copy];
    }
    return snapshot;
}
+ (void)setReportObject:(id)object forKey:(NSString *)key {
    [JCORECache write:[self reportCacheEntityWithKey:key content:object] completion:^(BOOL success) {
        if (!success) [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:CacheUtilitiesFile function:"+[JCORECacheUtilities setReportObject:forKey:]_block_invoke" line:134 tag:nil format:@"cache report data error:%@,%@", key, object];
    }];
}
+ (void)reportObjectsWithBlock:(RecoveryCacheReadCompletion)block {
    [JCORECache read:[self reportCacheEntityWithKey:nil content:nil] resultHandler:^(id value) {
        id objects = [self clearOldestReportObjects:value];
        if (![objects isKindOfClass:[NSArray class]]) { if (block) block(nil); return; }
        NSArray *snapshot = [objects copy];
        __block NSUInteger completed = 0;
        for (id key in snapshot) {
            [JCORECache read:[self reportCacheEntityWithKey:key content:nil] resultHandler:^(id content) {
                completed++;
                if (key && content && block) block(@[@{@"id":key, @"object":content}]);
                if (completed == snapshot.count && block) block(nil);
            }];
        }
        if (!snapshot.count && block) block(nil);
    }];
}
+ (JCORECacheEntity *)reportCacheEntityWithKey:(NSString *)key content:(id)content {
    JCORECacheEntity *entity = [JCORECacheEntity new]; entity.key = key; entity.content = content; entity.dirName = @"JCoreReportDocument"; entity.encrypt = YES; entity.type = 1; return entity;
}
@end
