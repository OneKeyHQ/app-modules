#pragma once
#import "RecoveredModels.h"
#import <pthread.h>

@interface JCORECacheEntity : NSObject <JCORECacheProtocol, NSCopying, NSCoding> {
    BOOL _encrypt;
    BOOL _compress;
    BOOL _sync;
    NSString *_key;
    NSString *_dirName;
    id _content;
    long long _type;
}
@property (strong, nonatomic) NSString *key;
@property (strong, nonatomic) NSString *dirName;
@property (strong, nonatomic) id content;
@property (nonatomic) BOOL encrypt;
@property (nonatomic) BOOL compress;
@property (nonatomic) long long type;
@property (nonatomic) BOOL sync;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
@end

@interface JCORECacheThread : NSThread
+ (instancetype)thread;
+ (void)run;
@end

@interface JCOREBasicCache : NSObject {
@protected
    pthread_mutex_t _mutex;
    NSString *_path;
    BOOL _persistent;
    NSString *_dir;
}
@property (nonatomic) BOOL persistent;
@property (readonly, nonatomic) NSString *path;
@property (strong, nonatomic) NSString *dir;
@property (nonatomic) pthread_mutex_t mutex;
- (BOOL)createDirectory:(NSString *)path;
- (BOOL)write:(id<JCORECacheProtocol>)object;
- (BOOL)write:(id<JCORECacheProtocol>)object completion:(RecoveryCacheWriteCompletion)completion;
- (id)read:(id<JCORECacheProtocol>)object;
- (BOOL)read:(id<JCORECacheProtocol>)object resultHandler:(RecoveryCacheReadCompletion)handler;
- (void)readDirectoryWithHandler:(RecoveryCacheReadCompletion)handler;
@end

@interface JCORELightCache : JCOREBasicCache {
    NSMutableDictionary *_allDatas;
}
@property (strong, nonatomic) NSMutableDictionary *allDatas;
@end
@interface JCORELargeCache : JCOREBasicCache
@end

@interface JCORECacheController : NSObject {
    pthread_mutex_t _mutex;
    JCORECacheThread *_thread;
}
@property (strong, nonatomic) JCORECacheThread *thread;
+ (instancetype)instance;
- (BOOL)isLargeData:(long long)type;
- (BOOL)isPersistentData:(long long)type;
- (JCOREBasicCache *)cacheObject:(id<JCORECacheProtocol>)object;
- (void)asyncHandleCompletion:(void (^)(void))completion;
- (void)write:(JCORECacheWriteModel *)object;
- (void)read:(JCORECacheReadModel *)object;
- (void)performSel:(SEL)selector withObject:(id)object;
+ (void)performSel:(SEL)selector withObject:(id)object;
+ (void)write:(id<JCORECacheProtocol>)object completion:(RecoveryCacheWriteCompletion)completion;
+ (void)read:(id<JCORECacheProtocol>)object resultHandler:(RecoveryCacheReadCompletion)handler;
+ (BOOL)write:(id<JCORECacheProtocol>)object;
+ (id)read:(id<JCORECacheProtocol>)object;
@end

@interface JCORECache : NSObject
+ (void)handleWriteCompleteInfo:(id)info;
+ (void)write:(JCORECacheEntity *)object completion:(RecoveryCacheWriteCompletion)completion;
+ (void)handleReadCompleteInfo:(id)info;
+ (void)read:(JCORECacheEntity *)object resultHandler:(RecoveryCacheReadCompletion)handler;
@end

@interface JCORECacheUtilities : NSObject
+ (void)setCacheObject:(id)object forKey:(NSString *)key;
+ (id)cacheObjectForKey:(NSString *)key;
+ (void)setPersistentObject:(id)object forKey:(NSString *)key;
+ (id)persistentObjectForKey:(NSString *)key;
+ (long long)getMaxCount;
+ (id)clearOldestReportObjects:(id)objects;
+ (void)setReportObject:(id)object forKey:(NSString *)key;
+ (void)reportObjectsWithBlock:(RecoveryCacheReadCompletion)block;
+ (JCORECacheEntity *)reportCacheEntityWithKey:(NSString *)key content:(id)content;
@end
