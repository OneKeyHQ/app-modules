#import <Foundation/Foundation.h>

@interface JCOREFIFOQueue : NSObject <NSCopying, NSCoding> {
    NSMutableArray *_itemArray;
    long long _maxCount;
}
- (instancetype)initWithMaxItem:(NSUInteger)maximum;
- (id)firstObject;
- (long long)count;
- (void)enqueue:(id)object;
- (void)enqueueWithHighPriority:(id)object;
- (id)dequeue;
- (void)dequeue:(id)object;
- (void)removeItem:(id)object;
- (void)clear;
- (void)copyQueue:(JCOREFIFOQueue *)queue;
- (void)copyQueue:(JCOREFIFOQueue *)queue withCondition:(BOOL (^)(id object))condition;
- (void)enumerateObjectsUsingBlock:(void (^)(id object, NSUInteger index, BOOL *stop))block;
- (void)enumerateObjectsWithOptions:(NSEnumerationOptions)options usingBlock:(void (^)(id object, NSUInteger index, BOOL *stop))block;
@end
