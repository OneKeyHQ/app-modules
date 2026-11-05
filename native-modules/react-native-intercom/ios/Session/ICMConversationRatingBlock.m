#import "ICMConversationRatingBlock.h"
// Original Swift destination; its source implementation remains a separate dependency.
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate27ConversationRatingBlockView")))
@interface ICMRecoveredConversationRatingBlockView : UIView @end
@implementation ICMConversationRatingBlock
@synthesize blockRating = _blockRating, selectedRatingIndex = _selectedRatingIndex, remark = _remark, ratingOptions = _ratingOptions;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    self = [super initWithDictionary:dictionary appearance:appearance];
    if (self) {
        NSMutableArray *options = [NSMutableArray new];
        for (id entry in dictionary[@"options"]) {
            ICMConversationRatingOption *option = [[ICMConversationRatingOption alloc] initWithIndex:entry[@"index"] emoji:entry[@"emoji"] unicode:entry[@"unicode"]];
            [options addObject:option];
        }
        _ratingOptions = options;
        _remark = dictionary[@"remark"];
        NSNumber *index = dictionary[@"ratingIndex"];
        _selectedRatingIndex = index ?: dictionary[@"rating_index"];
        _blockRating = [dictionary[@"block_rating"] boolValue];
        [self updateAppearance:appearance];
    }
    return self;
}
- (Class)viewClass { return [ICMRecoveredConversationRatingBlockView class]; }
- (NSInteger)type { return 15; }
- (ICMConversationRatingOption *)selectedRating {
    for (ICMConversationRatingOption *option in self.ratingOptions) {
        if ([self.selectedRatingIndex isEqual:option.index]) return option;
    }
    return nil;
}
+ (BOOL)validateOptionsArray:(id)options {
    if (![options isKindOfClass:[NSArray class]]) return NO;
    for (id option in options) {
        if (![option isKindOfClass:[NSDictionary class]]) return NO;
        if (![option[@"unicode"] isKindOfClass:[NSString class]]) return NO;
        if (![option[@"emoji"] isKindOfClass:[NSString class]]) return NO;
        if (![option[@"index"] isKindOfClass:[NSNumber class]]) return NO;
    }
    return [options count] != 0;
}
+ (BOOL)validateNumberOrNil:(id)value { return value == nil || [value isKindOfClass:[NSNumber class]]; }
+ (BOOL)validateStringOrNil:(id)value { return value == nil || [value isKindOfClass:[NSString class]]; }
+ (BOOL)validateBlockDictionary:(NSDictionary *)dictionary {
    return [self validateNumberOrNil:dictionary[@"rating_index"]]
        && [self validateNumberOrNil:dictionary[@"ratingIndex"]]
        && [self validateNumberOrNil:dictionary[@"block_rating"]]
        && [self validateStringOrNil:dictionary[@"remark"]]
        && [self validateOptionsArray:dictionary[@"options"]];
}
- (ICMConversationRatedIntent *)ratingIntent {
    return [[ICMConversationRatedIntent alloc] initWithRatingIndex:self.selectedRatingIndex];
}
- (ICMConversationRatingRemarkIntent *)remarkIntent {
    return [[ICMConversationRatingRemarkIntent alloc] initWithRemark:self.remark];
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[self class]]) return NO;
    ICMConversationRatingBlock *other = object;
    if (!((!self.selectedRatingIndex && !other.selectedRatingIndex) || [self.selectedRatingIndex isEqual:other.selectedRatingIndex])) return NO;
    if (![self.ratingOptions isEqual:other.ratingOptions]) return NO;
    return (!self.remark && !other.remark) || [self.remark isEqual:other.remark];
}
- (id)copyWithZone:(NSZone *)zone {
    ICMConversationRatingBlock *copy = [super copyWithZone:zone];
    copy.selectedRatingIndex = self.selectedRatingIndex;
    copy.ratingOptions = self.ratingOptions;
    copy.remark = self.remark;
    copy.blockRating = self.blockRating;
    return copy;
}
@end
@implementation ICMConversationRatingOption
@synthesize index = _index, emoji = _emoji, unicode = _unicode, imageUrl = _imageUrl;
- (instancetype)initWithIndex:(NSNumber *)index emoji:(NSString *)emoji unicode:(NSString *)unicode {
    self = [super init];
    if (self) {
        _index = index;
        _emoji = emoji;
        _unicode = unicode;
        _imageUrl = [[@"https://js.intercomcdn.com/images/stickers/" stringByAppendingString:unicode] stringByAppendingString:@".png"];
    }
    return self;
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[self class]]) return NO;
    ICMConversationRatingOption *other = object;
    return [self.index isEqual:other.index] && [self.emoji isEqual:other.emoji] && [self.unicode isEqual:other.unicode];
}
@end
@implementation ICMConversationRatedIntent
@synthesize intentConversationRatingIndex = _intentConversationRatingIndex, intentType = _intentType;
- (instancetype)initWithRatingIndex:(NSNumber *)index {
    self = [super init];
    if (self) { _intentType = 6; _intentConversationRatingIndex = index; }
    return self;
}
@end
@implementation ICMConversationRatingRemarkIntent
@synthesize intentConversationRemark = _intentConversationRemark, intentType = _intentType;
- (instancetype)initWithRemark:(NSString *)remark {
    self = [super init];
    if (self) { _intentType = 6; _intentConversationRemark = remark; }
    return self;
}
@end
