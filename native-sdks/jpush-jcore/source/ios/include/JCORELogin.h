#pragma once
#import "JCORETcpObject.h"
@interface JCORELogin : JCORETcpObject
- (unsigned char)getAppState;
- (void)appendIntegrateVersionTo:(id __autoreleasing *)data;
@end
