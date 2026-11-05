#import <Foundation/Foundation.h>
@interface ICMMaxSizeQueue : NSObject {
    NSUInteger _maxSize;
    NSMutableArray *_queue;
}
@property(nonatomic) NSUInteger maxSize;
@property(nonatomic, strong) NSMutableArray *queue;
- (instancetype)initWithMaxSize:(NSUInteger)size;
- (void)enqueueObject:(id)object;
- (id)front;
- (NSUInteger)size;
@end
