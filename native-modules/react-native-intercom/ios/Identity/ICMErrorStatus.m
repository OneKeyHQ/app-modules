// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMErrorStatus.h"
@implementation ICMErrorStatus
+ (BOOL)isRetryable:(NSError *)error {
    if ([ICMErrorStatus wasCancelled:error] || [ICMErrorStatus isMissingAppIdentity:error] || [ICMErrorStatus isMissingUserIdentity:error]) return NO;
    switch (error.code) {
        case -1001: case -1003: case -1004: case -1005: case -1006:
        case -1007: case -1008: case -1009: case -1010: return YES;
        default: return NO;
    }
}
+ (BOOL)wasCancelled:(NSError *)error { return error.code == -999; }
+ (BOOL)isMissingAppIdentity:(NSError *)error { return error.code == 1000; }
+ (BOOL)isMissingUserIdentity:(NSError *)error { return error.code == 1001; }
@end
