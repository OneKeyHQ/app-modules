#import "ICMPersistenceProxy.h"
#import "ICMUtils.h"

@implementation ICMPersistenceProxy
+ (NSFileManager *)fileManager { return [NSFileManager defaultManager]; }
+ (BOOL)supportsSecureCoding { return YES; }
+ (BOOL)archiveRootObject:(id)object toFile:(NSString *)path {
    if (!object || !path.length) return NO;
    @try {
        NSError *error = nil;
        NSData *data = [NSKeyedArchiver archivedDataWithRootObject:object requiringSecureCoding:YES error:&error];
        if (error) {
            if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] ICMPersistenceProxy: Failed to archive object: %@", error);
            return NO;
        }
        if (!data.length) return NO;
        NSError *writeError = nil;
        BOOL written = [data writeToFile:path options:NSDataWritingAtomic error:&writeError];
        if (!written && writeError && [ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] ICMPersistenceProxy: Failed to write archived data: %@", writeError);
        return written;
    } @catch (NSException *exception) {
        if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] ICMPersistenceProxy: Exception during archiving prevented crash: %@ (reason: %@)", exception.name, exception.reason);
        return NO;
    }
}
+ (id)unarchiveObject:(Class)allowedClass withFile:(NSString *)path {
    if (!allowedClass || !path.length) return nil;
    return [self unarchiveObjectOfClasses:[NSSet setWithObject:allowedClass] withFile:path];
}
+ (id)unarchiveObjectOfClasses:(NSSet<Class> *)classes withFile:(NSString *)path {
    if (!classes.count || !path.length) return nil;
    NSFileManager *manager = [NSFileManager defaultManager];
    if (![manager fileExistsAtPath:path]) return nil;
    @try {
        NSData *data = [NSData dataWithContentsOfFile:path];
        if (!data.length) return nil;
        NSError *error = nil;
        id object = [NSKeyedUnarchiver unarchivedObjectOfClasses:classes fromData:data error:&error];
        if (error) {
            if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] ICMPersistenceProxy: Failed to unarchive object: %@", error);
            return nil;
        }
        return object;
    } @catch (NSException *exception) {
        if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] ICMPersistenceProxy: Exception during unarchiving prevented crash: %@ (reason: %@)", exception.name, exception.reason);
        return nil;
    }
}
@end
