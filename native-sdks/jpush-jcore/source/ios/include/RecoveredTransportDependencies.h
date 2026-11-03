#pragma once
#import <Foundation/Foundation.h>
#import "RecoveredCoreService.h"
#import "JCOREThread.h"
#import "JCORELog.h"
// Declarations only: these are SDK dependencies, never replacement implementations.
#import "JCORESocketController.h"
#import "JCORESupersetManager.h"
@interface NSObject (JCORETransportDelegateDeclarations)
- (void)disconect;
- (void)remove;
- (void)socket:(id)socket recvData:(id)data handler:(void (^)(BOOL result))handler;
- (void)socket:(id)socket sentData:(id)data;
- (void)socket:(id)socket noRecvData:(id)data;
- (void)socket:(id)socket unsentData:(id)data;
- (void)socket:(id)socket connectStateChanged:(NSInteger)state;
- (void)sendQueueEmpty:(id)socket;
@end
