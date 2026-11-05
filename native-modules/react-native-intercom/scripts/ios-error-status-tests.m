#import <Foundation/Foundation.h>
#import "ICMErrorStatus.h"
int main(void) {
    @autoreleasepool {
        NSMutableArray *results = [NSMutableArray new];
        NSArray *codes = @[@(-1011), @(-1010), @(-1009), @(-1008), @(-1007), @(-1006), @(-1005), @(-1004), @(-1003), @(-1002), @(-1001), @(-1000), @(-999), @0, @400, @401, @429, @500, @503, @1000, @1001];
        NSSet *retryable = [NSSet setWithArray:@[@(-1001), @(-1003), @(-1004), @(-1005), @(-1006), @(-1007), @(-1008), @(-1009), @(-1010)]];
        NSUInteger count = 0;
        for (NSString *domain in @[@"fixture.foreign", NSURLErrorDomain]) {
            for (NSNumber *code in codes) {
                NSError *error = [NSError errorWithDomain:domain code:code.integerValue userInfo:nil];
                BOOL retry = [ICMErrorStatus isRetryable:error], cancelled = [ICMErrorStatus wasCancelled:error], app = [ICMErrorStatus isMissingAppIdentity:error], user = [ICMErrorStatus isMissingUserIdentity:error];
                if (retry != [retryable containsObject:code] || cancelled != (code.integerValue == -999) || app != (code.integerValue == 1000) || user != (code.integerValue == 1001)) {
                    fprintf(stderr, "FAIL: classification %ld\n", (long)code.integerValue); return 1;
                }
                [results addObject:@{@"domain":domain, @"code":code, @"retry":@(retry), @"cancelled":@(cancelled), @"missingApp":@(app), @"missingUser":@(user)}];
                count++;
            }
        }
        if ([ICMErrorStatus isRetryable:nil] || [ICMErrorStatus wasCancelled:nil] || [ICMErrorStatus isMissingAppIdentity:nil] || [ICMErrorStatus isMissingUserIdentity:nil]) return 1;
        count++;
        NSData *json = [NSJSONSerialization dataWithJSONObject:@{@"status":@"passed", @"testCount":@(count), @"cases":results} options:NSJSONWritingPrettyPrinted error:nil];
        fwrite(json.bytes, 1, json.length, stdout); fputc('\n', stdout);
    }
    return 0;
}
