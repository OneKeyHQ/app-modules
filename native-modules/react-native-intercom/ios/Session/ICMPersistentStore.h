#import <Foundation/Foundation.h>
#import "ICMBaseResponseDependencies.h"
NS_ASSUME_NONNULL_BEGIN
@protocol ICMPersistentStoreProtocol
+ (BOOL)storeCarousel:(nullable ICMCarousel *)carousel;
+ (nullable ICMCarousel *)fetchStoredCarousel;
+ (BOOL)deleteStoredCarousel;
@end
@interface ICMPersistentStore : NSObject <ICMPersistentStoreProtocol>
+ (BOOL)storeCarousel:(nullable ICMCarousel *)carousel;
+ (nullable ICMCarousel *)fetchStoredCarousel;
+ (BOOL)deleteStoredCarousel;
+ (BOOL)archiveObject:(id _Null_unspecified)object forKey:(NSString * _Null_unspecified)key;
+ (id _Null_unspecified)unarchiveObject:(Class _Null_unspecified)type forKey:(NSString * _Null_unspecified)key;
+ (BOOL)deleteObjectForKey:(NSString * _Null_unspecified)key;
+ (NSString * _Null_unspecified)filePathForKey:(NSString * _Null_unspecified)key;
@end
NS_ASSUME_NONNULL_END
