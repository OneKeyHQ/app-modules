#pragma once
#import <Foundation/Foundation.h>

@interface JCOREThread : NSObject
+ (instancetype)instance;
+ (NSThread *)mainThread;
+ (BOOL)isMainThread;
+ (void)performSel:(SEL)selector target:(id)target withObject:(id)object;
+ (void)performBlock:(void (^)(void))block;
@end
