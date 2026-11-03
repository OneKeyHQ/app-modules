#pragma once
#import <Foundation/Foundation.h>

@interface JCOREDecoder : NSObject
+ (unsigned char)uInt8FromBuffer:(NSData *)buffer offset:(unsigned short *)offset;
+ (char)sInt8FromBuffer:(NSData *)buffer offset:(unsigned short *)offset;
+ (unsigned short)uInt16FromBuffer:(NSData *)buffer offset:(unsigned short *)offset;
+ (unsigned int)uInt32FromBuffer:(NSData *)buffer offset:(unsigned short *)offset;
+ (unsigned long long)uInt64FromBuffer:(NSData *)buffer offset:(unsigned short *)offset;
+ (NSString *)stringFromBuffer:(NSData *)buffer offset:(unsigned short *)offset;
+ (NSData *)dataFromBuffer:(NSData *)buffer offset:(unsigned short *)offset;
@end
