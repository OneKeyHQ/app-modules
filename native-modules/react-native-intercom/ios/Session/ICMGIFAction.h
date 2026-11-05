// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
#import "ICMTextAction.h"
@interface ICMGIFAction : NSObject <ICMAction> {
    NSDictionary *_gifBlock;
}
@property (nonatomic, strong) NSDictionary *gifBlock;
- (instancetype)initWithGIFBlock:(NSDictionary *)block;
- (NSArray *)blocks;
@end
