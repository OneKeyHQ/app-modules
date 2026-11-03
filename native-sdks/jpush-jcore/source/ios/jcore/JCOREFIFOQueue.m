#import "JCOREFIFOQueue.h"

@implementation JCOREFIFOQueue
- (instancetype)initWithMaxItem:(NSUInteger)maximum {
    self = [super init];
    if (self) {
        _itemArray = [[NSMutableArray alloc] initWithCapacity:maximum];
        _maxCount = (long long)maximum;
    }
    return self;
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (self) {
        _itemArray = [coder decodeObjectForKey:@"itemArray"];
        _maxCount = [coder decodeIntegerForKey:@"itemMaxCount"];
    }
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:_itemArray forKey:@"itemArray"];
    [coder encodeInteger:_maxCount forKey:@"itemMaxCount"];
}
- (id)copyWithZone:(NSZone *)zone {
    JCOREFIFOQueue *copy = [[[self class] alloc] init];
    copy->_itemArray = [_itemArray mutableCopy];
    copy->_maxCount = _maxCount;
    return copy;
}
- (void)dealloc { _itemArray = nil; }
- (id)firstObject { return [self count] < 1 ? nil : [_itemArray firstObject]; }
- (long long)count { return (long long)[_itemArray count]; }
- (void)enqueue:(id)object {
    if (object) {
        if ([_itemArray containsObject:object]) return;
        [_itemArray addObject:object];
    }
    if (_maxCount > 0 && _maxCount < [self count]) [self dequeue];
}
- (void)enqueueWithHighPriority:(id)object {
    // Preserve the original pre-insertion count-1 comparison, including its overflow behavior.
    if (_maxCount > 0 && _maxCount < [self count] - 1) [self dequeue];
    if (object) [_itemArray insertObject:object atIndex:0];
}
- (id)dequeue {
    if ([self count] == 0) return nil;
    id first = [_itemArray firstObject];
    [_itemArray removeObjectAtIndex:0];
    return first;
}
- (void)dequeue:(id)object { if (object) [_itemArray removeObject:object]; }
- (void)removeItem:(id)object { if ([self count] != 0) [_itemArray removeObject:object]; }
- (void)clear { [_itemArray removeAllObjects]; }
- (void)copyQueue:(JCOREFIFOQueue *)queue {
    if ([queue isKindOfClass:[JCOREFIFOQueue class]]) {
        [[queue copy] enumerateObjectsUsingBlock:^(id object, NSUInteger index, BOOL *stop) { [self enqueue:object]; }];
    }
}
- (void)copyQueue:(JCOREFIFOQueue *)queue withCondition:(BOOL (^)(id))condition {
    if ([queue isKindOfClass:[JCOREFIFOQueue class]]) {
        [[queue copy] enumerateObjectsUsingBlock:^(id object, NSUInteger index, BOOL *stop) {
            if (condition(object)) [self enqueue:object];
            else [queue removeItem:object];
        }];
    }
}
- (void)enumerateObjectsUsingBlock:(void (^)(id, NSUInteger, BOOL *))block {
    [[_itemArray copy] enumerateObjectsUsingBlock:block];
}
- (void)enumerateObjectsWithOptions:(NSEnumerationOptions)options usingBlock:(void (^)(id, NSUInteger, BOOL *))block {
    [[_itemArray copy] enumerateObjectsWithOptions:options usingBlock:block];
}
@end
