#pragma once
#import "JPUSHNewRequest.h"
#import "JPUSHNewResponse.h"
@interface JPUSHUserPropertyRequest : JPUSHNewRequest {
    unsigned char _type;
    NSString *_content;
}
@property(nonatomic) unsigned char type;
@property(nonatomic, copy) NSString *content;
@end
@interface JPUSHUserPropertyResponse : JPUSHNewResponse
- (void)updateUserProperty:(JPUSHUserPropertyRequest *)request;
@end
