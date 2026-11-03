#import "JCOREFCCache.h"
@implementation JCOREFCCacheObj
@synthesize encrypt, key, object, dirName, type;
@dynamic hash, superclass, description, debugDescription;
- (instancetype)initWith:(NSString *)value { self = [super init]; if (self) { self.key = value; self.dirName = @"JCOREFCCacheDir"; self.encrypt = YES; } return self; }
- (instancetype)initWithFileName:(NSString *)value { self = [super init]; if (self) { self.key = value; self.dirName = @"JCOREFCCacheDir"; self.encrypt = YES; self.type = 1; } return self; }
- (void)setObject:(id)value { if (object != value) object = value; }
// Despite the original copy property metadata, this custom setter retains the object.
- (void)setDirName:(NSString *)value { if (dirName != value) dirName = value; }
@end
@implementation JCOREFCCache
+ (id)objectForKey:(NSString *)key { return [JCORECacheChannel read:[[JCOREFCCacheObj alloc] initWith:key]]; }
+ (void)objectForKey:(NSString *)key withBlock:(RecoveryFCCacheReadCompletion)block { [JCORECacheChannel read:[[JCOREFCCacheObj alloc] initWith:key] completion:^(id value) { if (block) block(key, value); }]; }
+ (void)setObject:(id)object forKey:(NSString *)key { JCOREFCCacheObj *entity = [[JCOREFCCacheObj alloc] initWith:key]; entity.object = object; [JCORECacheChannel write:entity]; }
+ (void)setObject:(id)object forKey:(NSString *)key withBlock:(RecoveryFCCacheWriteCompletion)block { JCOREFCCacheObj *entity = [[JCOREFCCacheObj alloc] initWith:key]; entity.object = object; [JCORECacheChannel write:entity completion:^(BOOL success) { if (block && success) block(); }]; }
+ (void)removeObjectForKey:(NSString *)key { [JCORECacheChannel write:[[JCOREFCCacheObj alloc] initWith:key]]; }
+ (id)objectForFile:(NSString *)file { id value = [JCORECacheChannel read:[[JCOREFCCacheObj alloc] initWithFileName:file]]; if ([value isKindOfClass:[NSData class]] && ![file isEqualToString:@"__JCOMMONSENTRYCrashLog"]) value = [NSJSONSerialization JSONObjectWithData:value options:NSJSONReadingMutableContainers error:NULL]; return value; }
+ (void)setObject:(id)object forFile:(NSString *)file { JCOREFCCacheObj *entity = [[JCOREFCCacheObj alloc] initWithFileName:file]; entity.object = object; [JCORECacheChannel write:entity]; }
+ (void)removeObjectForFile:(NSString *)file { [JCORECacheChannel write:[[JCOREFCCacheObj alloc] initWithFileName:file]]; }
@end
