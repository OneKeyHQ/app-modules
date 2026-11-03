#pragma once
#import <Foundation/Foundation.h>
#import "JCORECacheChannel.h"
@interface JPUSHCacheObj : NSObject <JCORECacheChannelProtocol> {
    NSString *key;
    id object;
    NSString *dirName;
    unsigned long long type;
}
@property(nonatomic, copy) NSString *key;
@property(nonatomic, strong) id object;
@property(nonatomic) unsigned long long type;
@property(nonatomic, copy) NSString *dirName;
@property(nonatomic) BOOL encrypt;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
- (instancetype)initWith:(NSString *)key;
- (instancetype)initWithFileName:(NSString *)name;
@end
@interface JPUSHCache : NSObject
+ (id)objectForKey:(NSString *)key;
+ (void)objectForKey:(NSString *)key withBlock:(void (^)(NSString *key, id object))block;
+ (void)setObject:(id)object forKey:(NSString *)key;
+ (void)setObject:(id)object forKey:(NSString *)key withBlock:(void (^)(void))block;
+ (void)removeObjectForKey:(NSString *)key;
+ (id)objectForFile:(NSString *)name;
+ (void)setObject:(id)object forFile:(NSString *)name;
+ (void)removeObjectForFile:(NSString *)name;
@end
