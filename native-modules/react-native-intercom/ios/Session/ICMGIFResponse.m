// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
#import "ICMGIFResponse.h"
@implementation ICMGIFResponse
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMGIFResponse *response = [ICMGIFResponse new];
    if (![dictionary isKindOfClass:[NSDictionary class]]) return response;
    if (![dictionary[@"results"] isKindOfClass:[NSArray class]]) return response;
    NSMutableArray *results = [NSMutableArray array];
    for (id item in dictionary[@"results"]) {
        if ([item isKindOfClass:[NSDictionary class]]) [results addObject:item];
    }
    response.results = results;
    return response;
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (object == nil) return NO;
    if (![[object class] isEqual:[self class]]) return NO;
    return [self isEqualToResponse:object];
}
- (BOOL)isEqualToResponse:(ICMGIFResponse *)response {
    if (self == response) return YES;
    if (response == nil) return NO;
    if (self.results == response.results) return YES;
    return [self.results isEqualToArray:response.results];
}
- (NSUInteger)hash { return self.results.hash; }
@end
