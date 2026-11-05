// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
#import "ICMPersistentStore.h"
#import "../Identity/ICMPersistenceProxy.h"
#import "../Identity/ICMUtils.h"
@implementation ICMPersistentStore
+ (BOOL)storeCarousel:(ICMCarousel *)carousel {
    if (!carousel) return NO;
    return [self archiveObject:carousel forKey:@"icmpersistentstore_carousel.icm"];
}
+ (ICMCarousel *)fetchStoredCarousel {
    return [self unarchiveObject:[ICMCarousel class] forKey:@"icmpersistentstore_carousel.icm"];
}
+ (BOOL)deleteStoredCarousel {
    return [self deleteObjectForKey:@"icmpersistentstore_carousel.icm"];
}
+ (BOOL)archiveObject:(id)object forKey:(NSString *)key {
    NSString *path = [self filePathForKey:key];
    return [ICMPersistenceProxy archiveRootObject:object toFile:path];
}
+ (id)unarchiveObject:(Class)type forKey:(NSString *)key {
    NSString *path = [self filePathForKey:key];
    return [ICMPersistenceProxy unarchiveObject:type withFile:path];
}
+ (BOOL)deleteObjectForKey:(NSString *)key {
    NSFileManager *manager = [ICMPersistenceProxy fileManager];
    NSString *path = [self filePathForKey:key];
    return [manager removeItemAtPath:path error:NULL];
}
+ (NSString *)filePathForKey:(NSString *)key {
    NSString *directory = [ICMUtils intercomDocumentsDirectory];
    return [directory stringByAppendingPathComponent:key];
}
@end
