#import "JPUSHCache.h"

@implementation JPUSHCacheObj
@dynamic hash, superclass, description, debugDescription;
@synthesize key = key, object = object, type = type, dirName = dirName;
@dynamic encrypt;
- (instancetype)initWith:(NSString *)value {
    if ((self = [super init])) {
        self.key = value;
        self.dirName = @"JPUSHCacheDir";
    }
    return self;
}
- (instancetype)initWithFileName:(NSString *)value {
    if ((self = [super init])) {
        self.key = value;
        self.dirName = @"JPUSHCacheDir";
        self.type = 1;
    }
    return self;
}
- (void)setObject:(id)value {
    if (object != value)
        object = value;
}
// Metadata says copy, but the original custom setter retains the input by identity.
- (void)setDirName:(NSString *)value {
    if (dirName != value)
        dirName = value;
}
@end
@implementation JPUSHCache
+ (id)objectForKey:(NSString *)key {
    return [JCORECacheChannel read:[[JPUSHCacheObj alloc] initWith:key]];
}
+ (void)objectForKey:(NSString *)key withBlock:(void (^)(NSString *, id))block {
    [JCORECacheChannel read:[[JPUSHCacheObj alloc] initWith:key]
                 completion:^(id result) {
                     if (block)
                         block(key, result);
                 }];
}
+ (void)setObject:(id)object forKey:(NSString *)key {
    JPUSHCacheObj *entry = [[JPUSHCacheObj alloc] initWith:key];
    entry.object = object;
    [JCORECacheChannel write:entry];
}
+ (void)setObject:(id)object forKey:(NSString *)key withBlock:(void (^)(void))block {
    JPUSHCacheObj *entry = [[JPUSHCacheObj alloc] initWith:key];
    entry.object = object;
    [JCORECacheChannel write:entry
                  completion:^(BOOL success) {
                      if (block && success)
                          block();
                  }];
}
+ (void)removeObjectForKey:(NSString *)key {
    [JCORECacheChannel write:[[JPUSHCacheObj alloc] initWith:key]];
}
+ (id)objectForFile:(NSString *)name {
    id result = [JCORECacheChannel read:[[JPUSHCacheObj alloc] initWithFileName:name]];
    if ([result isKindOfClass:NSData.class])
        result = [NSJSONSerialization JSONObjectWithData:result
                                                 options:NSJSONReadingMutableContainers
                                                   error:NULL];
    return result;
}
+ (void)setObject:(id)object forFile:(NSString *)name {
    JPUSHCacheObj *entry = [[JPUSHCacheObj alloc] initWithFileName:name];
    entry.object = object;
    [JCORECacheChannel write:entry];
}
+ (void)removeObjectForFile:(NSString *)name {
    [JCORECacheChannel write:[[JPUSHCacheObj alloc] initWithFileName:name]];
}
@end
