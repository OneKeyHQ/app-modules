// Internal interface recovered from Intercom 19.9.1 metadata.
#import <Foundation/Foundation.h>
@interface ICMOreoUserJwtReplay : NSObject
@property (nonatomic, copy, readonly) NSString *userJwt;
@property (nonatomic, copy, readonly) NSArray *fingerprint;
@property (nonatomic, copy, readonly) NSURLRequest *(^buildRequest)(NSString *jwt);
- (instancetype)initWithUserJwt:(NSString *)jwt fingerprint:(NSArray *)fingerprint buildRequest:(NSURLRequest *(^)(NSString *jwt))builder;
@end
