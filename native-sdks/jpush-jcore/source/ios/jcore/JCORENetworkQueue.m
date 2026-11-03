#import "JCORENetworkQueue.h"
@implementation JCORENetworkQueue
- (id)copyWithZone:(NSZone *)zone {
    JCORENetworkQueue *queue = [[[self class] alloc] init];
    queue->_itemArray = [_itemArray mutableCopy];
    queue->_itemAndKeyMap = [_itemAndKeyMap mutableCopy];
    queue->_maxCount = _maxCount;
    queue->_keyName = [_keyName copy];
    return queue;
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    if ((self = [super init])) { _itemArray = [coder decodeObjectForKey:@"itemArray"]; _itemAndKeyMap = [coder decodeObjectForKey:@"itemAndKeyMap"]; _maxCount = [coder decodeIntegerForKey:@"itemMaxCount"]; }
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder { [coder encodeObject:_itemArray forKey:@"itemArray"]; [coder encodeObject:_itemAndKeyMap forKey:@"itemAndKeyMap"]; [coder encodeInteger:_maxCount forKey:@"itemMaxCount"]; }
- (instancetype)initWithMaxItem:(NSUInteger)count withKeyProperyName:(NSString *)name {
    if ((self = [super init])) { _itemArray = [[NSMutableArray alloc] initWithCapacity:count]; _itemAndKeyMap = [NSMutableDictionary dictionary]; _maxCount = (long long)count; _keyName = name; }
    return self;
}
- (void)dealloc { _itemAndKeyMap = nil; _itemArray = nil; }
- (long long)count { return (long long)_itemArray.count; }
- (void)enqueue:(id)item {
    if (item) {
        if ([_itemArray containsObject:item]) return;
        [_itemArray addObject:item];
        id key = [item valueForKey:_keyName];
        if (key) [_itemAndKeyMap setObject:item forKey:key];
    }
    if ((NSUInteger)_maxCount < _itemArray.count) [self dequeue];
}
- (void)enqueueWithHighPriority:(id)item {
    if ((NSUInteger)_maxCount < _itemArray.count - 1) [self dequeue];
    if (item) { [_itemArray insertObject:item atIndex:0]; id key = [item valueForKey:_keyName]; if (key) [_itemAndKeyMap setObject:item forKey:key]; }
}
- (id)dequeue {
    if (!_itemArray.count) return nil;
    id item = _itemArray.firstObject;
    if (!item || ![_keyName isKindOfClass:NSString.class] || !_keyName.length || ![item respondsToSelector:NSSelectorFromString(_keyName)]) return nil;
    id key = [item valueForKey:_keyName];
    if (key) [_itemAndKeyMap removeObjectForKey:key];
    if (_itemArray.count) @synchronized (self) { if (_itemArray.count) [_itemArray removeObjectAtIndex:0]; }
    return item;
}
- (void)dequeue:(id)item { if (item) [_itemArray removeObject:item]; }
- (void)removeQueueItem:(id)item {
    if (item && _itemArray.count) { id key = [item valueForKey:_keyName]; if (key) [_itemAndKeyMap removeObjectForKey:key]; [_itemArray removeObject:item]; }
}
- (void)clear { [_itemArray removeAllObjects]; [_itemAndKeyMap removeAllObjects]; }
- (void)copyQueue:(id)queue {
    id snapshot = [queue copy];
    if ([snapshot isKindOfClass:JCORENetworkQueue.class]) [snapshot enumerateObjectsUsingBlock:^(id object, NSUInteger index, BOOL *stop) { [self enqueue:object]; }];
}
- (void)copyQueue:(id)queue withCondition:(BOOL (^)(id))condition {
    id snapshot = [queue copy];
    if ([snapshot isKindOfClass:JCORENetworkQueue.class]) [snapshot enumerateObjectsUsingBlock:^(id object, NSUInteger index, BOOL *stop) { if (condition(object)) [self enqueue:object]; else [queue removeQueueItem:object]; }];
}
- (id)getObjectFromQueueWithKey:(id)key { return key ? [_itemAndKeyMap objectForKey:key] : nil; }
- (void)enqueueItem:(id)item withKey:(id)key {
    if (key && item && ![_itemArray containsObject:item]) { [_itemAndKeyMap setObject:item forKey:key]; [_itemArray addObject:item]; if ((NSUInteger)_maxCount < _itemArray.count) [self dequeue]; }
}
- (void)enqueueHighPriorityItem:(id)item withKey:(id)key {
    if (key && item) {
        [_itemAndKeyMap setObject:item forKey:key];
        if ((NSUInteger)_maxCount < _itemArray.count - 1) [self dequeue];
        [_itemArray insertObject:item atIndex:0];
        [_itemAndKeyMap setObject:item forKey:[item valueForKey:_keyName]];
    }
}
- (id)dequeueItemWithKey:(id)key {
    id item = key ? [_itemAndKeyMap objectForKey:key] : nil;
    if (item) { [_itemAndKeyMap removeObjectForKey:key]; [_itemArray removeObject:item]; }
    return item;
}
- (void)enumerateObjectsUsingBlock:(void (^)(id, NSUInteger, BOOL *))block { [[_itemArray copy] enumerateObjectsUsingBlock:block]; }
- (void)enumerateObjectsWithOptions:(NSEnumerationOptions)options usingBlock:(void (^)(id, NSUInteger, BOOL *))block { [[_itemArray copy] enumerateObjectsWithOptions:options usingBlock:block]; }
@end
