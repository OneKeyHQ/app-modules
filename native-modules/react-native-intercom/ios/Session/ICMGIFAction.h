#import "ICMTextAction.h"
@interface ICMGIFAction : NSObject <ICMAction> {
    NSDictionary *_gifBlock;
}
@property (nonatomic, strong) NSDictionary *gifBlock;
- (instancetype)initWithGIFBlock:(NSDictionary *)block;
- (NSArray *)blocks;
@end
