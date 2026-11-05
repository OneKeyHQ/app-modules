#import <Foundation/Foundation.h>
@class ITBUpload;
NS_ASSUME_NONNULL_BEGIN
@protocol ICMAction <NSObject>
- (NSArray * _Null_unspecified)blocks;
@optional
- (ITBUpload * _Null_unspecified)upload;
@end

@interface ICMTextAction : NSObject <ICMAction> {
    NSString *_text;
    NSArray *_blocks;
}
@property (nonatomic, readonly) NSString * _Null_unspecified text;
@property (nonatomic, readonly) NSArray * _Null_unspecified blocks;
- (instancetype _Null_unspecified)initWithText:(NSString * _Null_unspecified)text;
+ (NSArray *)blockBodyForText:(NSString * _Null_unspecified)text;
@end
NS_ASSUME_NONNULL_END
