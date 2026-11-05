// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
#import "ICMGIFAction.h"
@implementation ICMGIFAction
- (instancetype)initWithGIFBlock:(NSDictionary *)block {
    self = [super init];
    if (self) _gifBlock = block;
    return self;
}
- (NSArray *)blocks { return @[self.gifBlock]; }
@end
