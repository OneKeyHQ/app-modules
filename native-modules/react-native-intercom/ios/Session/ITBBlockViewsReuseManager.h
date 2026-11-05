#import "ITBBlockView.h"
@interface ITBBlockViewsReuseManager : NSObject {
    NSMutableDictionary *_reusableDict;
}
+ (instancetype)sharedInstance;
- (void)receivedMemoryWarning;
- (void)tearDown;
- (ITBBlockView *)dequeueReusableViewForBlock:(ITBBlock *)block;
- (void)enqueueReusableViewForReuse:(ITBBlockView *)view;
@end
