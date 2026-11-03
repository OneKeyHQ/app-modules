#pragma once
#import "JCORETcpObject.h"
@interface JCORESDKControl : JCORETcpObject {
    unsigned char _cmdCode;
    unsigned short _statusCode;
    unsigned long long _requestID;
}
@property (nonatomic) unsigned short statusCode;
@property (nonatomic) unsigned long long requestID;
@property (nonatomic) unsigned char cmdCode;
@end
