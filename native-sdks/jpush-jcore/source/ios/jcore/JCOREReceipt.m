#import "JCOREReceipt.h"
@interface JCOREReceipt () {
    unsigned short _statusCode;
}
@end
@implementation JCOREReceipt
@synthesize statusCode = _statusCode;
- (instancetype)init { self = [super init]; if (self) self.controlFlag = 9; return self; }
- (NSData *)bodyData {
    NSMutableData *data = [[NSMutableData alloc] init];
    [data jcore_appendInt16:_statusCode];
    return [data copy];
}
@end
