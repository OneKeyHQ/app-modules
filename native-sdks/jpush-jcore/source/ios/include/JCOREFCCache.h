#pragma once
#import "JCORECacheChannel.h"

typedef void (^RecoveryFCCacheReadCompletion)(NSString *key, id value);
typedef void (^RecoveryFCCacheWriteCompletion)(void);
@interface JCOREFCCacheObj : NSObject <JCORECacheChannelProtocol> {
    BOOL encrypt;
    NSString *key;
    id object;
    NSString *dirName;
    unsigned long long type;
}
@property (copy, nonatomic) NSString *key;
@property (strong, nonatomic) id object;
@property (nonatomic) unsigned long long type;
@property (copy, nonatomic) NSString *dirName;
@property (nonatomic) BOOL encrypt;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
- (instancetype)initWith:(NSString *)key;
- (instancetype)initWithFileName:(NSString *)fileName;
@end
@interface JCOREFCCache : NSObject
+ (id)objectForKey:(NSString *)key;
+ (void)objectForKey:(NSString *)key withBlock:(RecoveryFCCacheReadCompletion)block;
+ (void)setObject:(id)object forKey:(NSString *)key;
+ (void)setObject:(id)object forKey:(NSString *)key withBlock:(RecoveryFCCacheWriteCompletion)block;
+ (void)removeObjectForKey:(NSString *)key;
+ (id)objectForFile:(NSString *)file;
+ (void)setObject:(id)object forFile:(NSString *)file;
+ (void)removeObjectForFile:(NSString *)file;
@end
