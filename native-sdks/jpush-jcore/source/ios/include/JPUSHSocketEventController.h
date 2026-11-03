#pragma once
#import <Foundation/Foundation.h>
@interface JPUSHSocketEventController : NSObject
- (void)receiveTcpEventResponse:(id)notification;
- (void)parseTcpData:(id)response;
- (void)handleMessage:(id)response;
- (void)dispatchCustomPushMessage:(id)message;
- (id)getJPUSHMessageFromInfo:(id)info;
- (id)decodeGeofenceMessage:(id)message;
@end
