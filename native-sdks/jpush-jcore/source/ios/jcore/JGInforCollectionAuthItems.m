#import "JGInforCollectionAuthItems.h"

@implementation JGInforCollectionAuthItems
@synthesize isAuth=_isAuth;
- (instancetype)init {
    self=[super init];
    if(self)_isAuth=YES;
    return self;
}
@end
