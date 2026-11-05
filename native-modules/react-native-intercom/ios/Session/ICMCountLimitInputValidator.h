#import "ICMValidator.h"

@interface ICMCountLimitInputValidator : NSObject <ICMValidatorProtocol>
@property (nonatomic) NSInteger minimumCount;
@property (nonatomic) NSInteger maximumCount;
- (nonnull instancetype)initWithMinimumCount:(NSInteger)minimumCount maximumCount:(NSInteger)maximumCount;
@end
