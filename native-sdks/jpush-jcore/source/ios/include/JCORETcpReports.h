#pragma once
#import "JCOREReceipt.h"

@interface JCOREMessageReceipt : JCOREReceipt {
    unsigned char _messageType;
    unsigned long long _messageID;
    NSString *_msgData;
}
@property (nonatomic) unsigned char messageType;
@property (nonatomic) unsigned long long messageID;
@property (copy, nonatomic) NSString *msgData;
@end

@interface JCORESDKVersion : JCORETcpObject {
    unsigned char _sdkProperty;
    NSString *_sdkVersion;
}
@property (copy, nonatomic) NSString *sdkVersion;
@property (nonatomic) unsigned char sdkProperty;
@end

@interface JCOREDeviceTokenReport : JCORETcpObject {
    NSString *_invalidDeviceToken;
}
@property (copy, nonatomic) NSString *invalidDeviceToken;
@end
@interface JCOREVoipTokenReport : JCORETcpObject @end
@interface JCOREIDFAReport : JCORETcpObject @end
