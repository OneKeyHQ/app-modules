#import "JCORESocket.h"
@implementation JCORENWSocket
- (dispatch_data_t)dispatchDataFromNSData:(NSData *)data {
    if (!data || ![data isKindOfClass:NSData.class]) return nil;
    // Keep the original copy semantics without a data-sized stack allocation.
    NSUInteger length = data.length;
    NSMutableData *buffer = [NSMutableData dataWithLength:length];
    NSUInteger copyLength = data.length;
    // A changing NSData subclass must not write or read beyond the original allocation.
    if (copyLength > length) return nil;
    [data getBytes:buffer.mutableBytes length:copyLength];
    NSUInteger dispatchLength = data.length;
    if (dispatchLength > length) return nil;
    return dispatch_data_create(buffer.bytes, dispatchLength, NULL, DISPATCH_DATA_DESTRUCTOR_DEFAULT);
}
- (NSData *)nsDataFromDispatchData:(dispatch_data_t)data {
    if (!data) return nil;
    const void *bytes = NULL;
    size_t length = 0;
    __attribute__((objc_precise_lifetime)) dispatch_data_t mapped = dispatch_data_create_map(data, &bytes, &length);
    return mapped ? [[NSData alloc] initWithBytes:bytes length:length] : nil;
}
@end
