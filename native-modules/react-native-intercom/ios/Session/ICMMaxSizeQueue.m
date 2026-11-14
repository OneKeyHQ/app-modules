#import "ICMMaxSizeQueue.h"
@implementation ICMMaxSizeQueue
- (instancetype)initWithMaxSize:(NSUInteger)size {
    self = [super init];
    if (self) { _maxSize = size; _queue = [[NSMutableArray alloc] initWithCapacity:size]; }
    return self;
}
- (void)setMaxSize:(NSUInteger)size {
    @synchronized (self) {
        if (_maxSize == size) return;
        _maxSize = size;
        NSInteger excess = (NSInteger)(_queue.count - size);
        if (excess >= 1) [_queue removeObjectsAtIndexes:[NSIndexSet indexSetWithIndexesInRange:NSMakeRange(0, (NSUInteger)excess)]];
    }
}
- (void)enqueueObject:(id)object {
    @synchronized (self) {
        if (_queue.count == _maxSize && _queue.firstObject) [_queue removeObjectAtIndex:0];
        if (_queue.count < _maxSize) [_queue addObject:object];
    }
}
- (id)front { @synchronized (self) { return _queue.firstObject; } }
- (NSUInteger)size { @synchronized (self) { return _queue.count; } }
@end
