// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMOreoUserJwtReplay.h"
@implementation ICMOreoUserJwtReplay
@synthesize userJwt = _userJwt, fingerprint = _fingerprint, buildRequest = _buildRequest;
- (instancetype)initWithUserJwt:(NSString *)jwt fingerprint:(NSArray *)fingerprint buildRequest:(NSURLRequest *(^)(NSString *))builder {
    if ((self = [super init])) {
        _userJwt = [jwt copy];
        _fingerprint = [fingerprint copy];
        _buildRequest = [builder copy];
    }
    return self;
}
@end
