#import "ITBBlock.h"
#import "ITBIntent.h"
@interface ICMConversationRatingOption : NSObject {
    NSNumber *_index;
    NSString *_emoji;
    NSString *_unicode;
    NSString *_imageUrl;
}
@property (nonatomic, readonly) NSNumber *index;
@property (nonatomic, readonly, copy) NSString *emoji;
@property (nonatomic, readonly, copy) NSString *unicode;
@property (nonatomic, readonly, copy) NSString *imageUrl;
- (instancetype)initWithIndex:(NSNumber *)index emoji:(NSString *)emoji unicode:(NSString *)unicode;
@end
@interface ICMConversationRatedIntent : NSObject <ITBIntent> {
    NSNumber *_intentConversationRatingIndex;
    NSInteger _intentType;
}
@property (nonatomic) NSInteger intentType;
@property (nonatomic, strong) NSNumber *intentConversationRatingIndex;
- (instancetype)initWithRatingIndex:(NSNumber *)index;
@end
@interface ICMConversationRatingRemarkIntent : NSObject <ITBIntent> {
    NSString *_intentConversationRemark;
    NSInteger _intentType;
}
@property (nonatomic) NSInteger intentType;
@property (nonatomic, strong) NSString *intentConversationRemark;
- (instancetype)initWithRemark:(NSString *)remark;
@end
@interface ICMConversationRatingBlock : ITBBlock {
    BOOL _blockRating;
    NSNumber *_selectedRatingIndex;
    NSString *_remark;
    NSArray *_ratingOptions;
}
@property (nonatomic, strong) NSNumber *selectedRatingIndex;
@property (nonatomic, copy) NSString *remark;
@property (nonatomic) BOOL blockRating;
@property (nonatomic, copy) NSArray<ICMConversationRatingOption *> *ratingOptions;
- (ICMConversationRatingOption *)selectedRating;
- (ICMConversationRatedIntent *)ratingIntent;
- (ICMConversationRatingRemarkIntent *)remarkIntent;
+ (BOOL)validateOptionsArray:(id)options;
+ (BOOL)validateNumberOrNil:(id)value;
+ (BOOL)validateStringOrNil:(id)value;
@end
