#pragma once
#import "JCORETcpObject.h"
@interface JCORERegister : JCORETcpObject {
    BOOL _isNewDevice;
}
@property (nonatomic) BOOL isNewDevice;
- (NSString *)integrateVersionString;
@end
