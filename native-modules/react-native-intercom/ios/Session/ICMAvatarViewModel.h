#import "ICMAvatarDependencies.h"
@interface ICMAvatarViewModel : NSObject
@property (nonatomic, strong) Avatar *avatar;
@property (nonatomic) NSInteger activeIndicatorSize;
- (instancetype)initWithAvatar:(Avatar *)avatar activeIndicatorSize:(NSInteger)size;
@end
