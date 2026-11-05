// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMOreoErrorResponse.h"

@implementation ICMOreoErrorResponse
@synthesize type = _type, message = _message, code = _code, error = _error, oreoShutdownState = _oreoShutdownState;
+ (instancetype)instanceFromDictionary:(id)dictionary networkError:(NSError *)error {
    NSString *type = @"", *message = @"", *code = @"";
    NSTimeInterval period = 0;
    if ([dictionary isKindOfClass:[NSDictionary class]]) {
        id singular = dictionary[@"error"];
        if ([singular isKindOfClass:[NSDictionary class]]) {
            if ([singular[@"type"] isKindOfClass:[NSString class]]) type = singular[@"type"];
            if ([singular[@"message"] isKindOfClass:[NSString class]]) message = singular[@"message"];
            if ([singular[@"code"] isKindOfClass:[NSString class]]) code = singular[@"code"];
            if ([singular[@"shutdown_period"] isKindOfClass:[NSNumber class]]) period = [singular[@"shutdown_period"] doubleValue];
        } else if ([dictionary[@"errors"] isKindOfClass:[NSArray class]]) {
            NSArray *errors = dictionary[@"errors"];
            NSMutableArray *types = [NSMutableArray new], *messages = [NSMutableArray new];
            id first = errors.firstObject;
            if ([first isKindOfClass:[NSDictionary class]] && [first[@"code"] isKindOfClass:[NSString class]]) code = first[@"code"];
            for (id item in errors) {
                if (![item isKindOfClass:[NSDictionary class]]) continue;
                if ([item[@"type"] isKindOfClass:[NSString class]]) [types addObject:item[@"type"]];
                if ([item[@"message"] isKindOfClass:[NSString class]]) [messages addObject:item[@"message"]];
                if ([item[@"shutdown_period"] isKindOfClass:[NSNumber class]]) period = [item[@"shutdown_period"] doubleValue];
            }
            type = [types componentsJoinedByString:@" - "];
            message = [messages componentsJoinedByString:@" - "];
        }
    }
    ICMOreoShutdownState *state = nil;
    if ([type containsString:@"messenger_shutdown_response"] && period != 0) {
        state = [[ICMOreoShutdownState alloc] initWithShutdownPeriod:period shutdownReason:message];
    }
    return [[ICMOreoErrorResponse alloc] initWithType:type messsage:message code:code networkError:error shutdownState:state];
}
- (instancetype)initWithType:(NSString *)type messsage:(NSString *)message code:(NSString *)code networkError:(NSError *)error shutdownState:(ICMOreoShutdownState *)state {
    if ((self = [super init])) {
        _type = type;
        _message = message;
        _code = code;
        _error = [ICMOreoErrorResponse errorWithErrorMesssage:message networkError:error];
        _oreoShutdownState = state;
    }
    return self;
}
+ (NSError *)errorWithErrorMesssage:(NSString *)message networkError:(NSError *)error {
    NSString *description = message;
    if (!message.length && error.localizedDescription) description = error.localizedDescription;
    return [NSError errorWithDomain:@"io.intercom.ios.error.network" code:error.code userInfo:@{
        NSLocalizedDescriptionKey: description ?: [NSNull null],
        NSLocalizedRecoverySuggestionErrorKey: error.localizedRecoverySuggestion ?: [NSNull null],
        NSUnderlyingErrorKey: error ?: [NSNull null],
        @"kIntercomHTTPStatusCode": @(error.code)
    }];
}
@end
