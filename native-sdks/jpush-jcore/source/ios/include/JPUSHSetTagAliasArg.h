#pragma once
#import <Foundation/Foundation.h>

typedef void (^JPUSHSetTagAliasCompletion)(int result, NSSet *tags, NSString *alias);

@interface JPUSHSetTagAliasArg : NSObject <NSCopying> {
    __weak NSTimer *_timeoutTimer;
    NSSet *_tags;
    NSString *_alias;
    NSUInteger _type;
    JPUSHSetTagAliasCompletion _completionHandler;
    NSInvocation *_cbInvocation;
    __weak id _target;
    NSUInteger _sequence;
}
@property(nonatomic,strong) NSSet *tags;
@property(nonatomic,strong) NSString *alias;
@property(nonatomic) NSUInteger type;
@property(nonatomic,copy) JPUSHSetTagAliasCompletion completionHandler;
@property(nonatomic,strong) NSInvocation *cbInvocation;
@property(nonatomic,weak) id target;
@property(nonatomic) NSUInteger sequence;
- (void)beginTimeoutTimer;
- (void)stopTimeoutTimer;
- (void)onTimeout:(NSTimer *)timer;
- (void)p_deQueueFromTcpBlockQueue;
@end
