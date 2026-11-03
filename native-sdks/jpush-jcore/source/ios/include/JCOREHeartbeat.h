#pragma once
#import "JCORETcpObject.h"
@interface JCOREHeartbeat : JCORETcpObject {
    unsigned short _heartBeatInterval;
}
@property (nonatomic) unsigned short heartBeatInterval;
- (unsigned char)getAppState;
@end
