#pragma once
#import <Foundation/Foundation.h>
@interface JCORENetworkQueue : NSObject <NSCoding, NSCopying> {
    NSMutableArray *_itemArray;
    NSMutableDictionary *_itemAndKeyMap;
    long long _maxCount;
    NSString *_keyName;
}
- (instancetype)initWithMaxItem:(NSUInteger)count withKeyProperyName:(NSString *)name;
- (long long)count;
- (void)enqueue:(id)item;
- (void)enqueueWithHighPriority:(id)item;
- (id)dequeue;
- (void)dequeue:(id)item;
- (void)removeQueueItem:(id)item;
- (void)clear;
- (void)copyQueue:(id)queue;
- (void)copyQueue:(id)queue withCondition:(BOOL (^)(id item))condition;
- (id)getObjectFromQueueWithKey:(id)key;
- (void)enqueueItem:(id)item withKey:(id)key;
- (void)enqueueHighPriorityItem:(id)item withKey:(id)key;
- (id)dequeueItemWithKey:(id)key;
- (void)enumerateObjectsUsingBlock:(void (^)(id object, NSUInteger index, BOOL *stop))block;
- (void)enumerateObjectsWithOptions:(NSEnumerationOptions)options usingBlock:(void (^)(id object, NSUInteger index, BOOL *stop))block;
@end
