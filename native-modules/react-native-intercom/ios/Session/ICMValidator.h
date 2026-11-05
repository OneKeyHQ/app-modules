#import <Foundation/Foundation.h>

@protocol ICMValidatorProtocol <NSObject>
- (BOOL)validateText:(nullable NSString *)text error:(NSError * _Nullable * _Nullable)error;
- (BOOL)validateArray:(nullable NSArray *)array error:(NSError * _Nullable * _Nullable)error;
- (BOOL)validateNumber:(nullable NSNumber *)number error:(NSError * _Nullable * _Nullable)error;
@end

@interface ICMValidator : NSObject
+ (BOOL)validateText:(nullable NSString *)text validators:(nonnull NSArray<id<ICMValidatorProtocol>> *)validators error:(NSError * _Nullable * _Nullable)error;
+ (BOOL)validateArray:(nullable NSArray *)array validators:(nonnull NSArray<id<ICMValidatorProtocol>> *)validators error:(NSError * _Nullable * _Nullable)error;
+ (BOOL)validateNumber:(nullable NSNumber *)number validators:(nonnull NSArray<id<ICMValidatorProtocol>> *)validators error:(NSError * _Nullable * _Nullable)error;
@end
