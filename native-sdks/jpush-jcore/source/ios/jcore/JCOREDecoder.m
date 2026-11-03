#import "JCOREDecoder.h"

@implementation JCOREDecoder
+ (unsigned char)uInt8FromBuffer:(NSData *)buffer offset:(unsigned short *)offset {
    const unsigned char *bytes = buffer.bytes;
    unsigned short length = (unsigned short)buffer.length;
    unsigned int next = (unsigned int)*offset + 1;
    unsigned short start = *offset;
    *offset = (unsigned short)next;
    return next <= UINT16_MAX && *offset <= length ? bytes[start] : 0;
}
+ (char)sInt8FromBuffer:(NSData *)buffer offset:(unsigned short *)offset {
    // Preserve the original separate selector rather than dispatching through uInt8.
    const char *bytes = buffer.bytes;
    unsigned short length = (unsigned short)buffer.length;
    unsigned int next = (unsigned int)*offset + 1;
    unsigned short start = *offset;
    *offset = (unsigned short)next;
    return next <= UINT16_MAX && *offset <= length ? bytes[start] : 0;
}
+ (unsigned short)uInt16FromBuffer:(NSData *)buffer offset:(unsigned short *)offset {
    const unsigned char *bytes = buffer.bytes;
    unsigned short length = (unsigned short)buffer.length;
    unsigned short start = *offset;
    *offset = (unsigned short)(start + 2);
    if (start >= 0xfffe || *offset > length) return 0;
    return (unsigned short)(((unsigned short)bytes[start] << 8) | bytes[start + 1]);
}
+ (unsigned int)uInt32FromBuffer:(NSData *)buffer offset:(unsigned short *)offset {
    const unsigned char *bytes = buffer.bytes;
    unsigned short length = (unsigned short)buffer.length;
    unsigned short start = *offset;
    *offset = (unsigned short)(start + 4);
    if (start >= 0xfffc || *offset > length) return 0;
    unsigned int result = 0;
    for (unsigned int i = 0; i < 4; i++) result = (result << 8) | bytes[start + i];
    return result;
}
+ (unsigned long long)uInt64FromBuffer:(NSData *)buffer offset:(unsigned short *)offset {
    const unsigned char *bytes = buffer.bytes;
    unsigned short length = (unsigned short)buffer.length;
    unsigned short start = *offset;
    *offset = (unsigned short)(start + 8);
    if (start >= 0xfff8 || *offset > length) return 0;
    unsigned long long result = 0;
    for (unsigned int i = 0; i < 8; i++) result = (result << 8) | bytes[start + i];
    return result;
}
+ (NSString *)stringFromBuffer:(NSData *)buffer offset:(unsigned short *)offset {
    const unsigned char *bytes = buffer.bytes;
    unsigned short length = (unsigned short)buffer.length;
    unsigned short size = [self uInt16FromBuffer:buffer offset:offset];
    unsigned short start = *offset;
    *offset = (unsigned short)(start + size);
    if (size == 0) return @"";
    if (start >= *offset || *offset > length) return nil;
    NSMutableData *terminated = [NSMutableData dataWithBytes:bytes + start length:size];
    [terminated appendBytes:[@"" UTF8String] length:1];
    return [NSString stringWithUTF8String:terminated.bytes];
}
+ (NSData *)dataFromBuffer:(NSData *)buffer offset:(unsigned short *)offset {
    const unsigned char *bytes = buffer.bytes;
    unsigned short length = (unsigned short)buffer.length;
    unsigned short size = [self uInt16FromBuffer:buffer offset:offset];
    unsigned short start = *offset;
    *offset = (unsigned short)(start + size);
    if (size == 0) return [NSData data];
    if (start >= *offset || *offset > length) return nil;
    return [NSData dataWithBytes:bytes + start length:size];
}
@end
