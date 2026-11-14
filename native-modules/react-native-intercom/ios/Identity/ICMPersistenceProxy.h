// Internal interface recovered from Intercom 19.9.1 Objective-C metadata.
#import <Foundation/Foundation.h>
NS_ASSUME_NONNULL_BEGIN
@interface ICMPersistenceProxy : NSObject
+ (NSFileManager *)fileManager;
+ (BOOL)supportsSecureCoding;
+ (BOOL)archiveRootObject:(nullable id)object toFile:(nullable NSString *)path;
+ (nullable id)unarchiveObject:(nullable Class)allowedClass withFile:(nullable NSString *)path;
+ (nullable id)unarchiveObjectOfClasses:(nullable NSSet<Class> *)classes withFile:(nullable NSString *)path;
@end
NS_ASSUME_NONNULL_END
