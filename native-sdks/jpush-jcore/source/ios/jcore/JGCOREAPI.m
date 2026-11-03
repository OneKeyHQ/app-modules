#import "JGCOREAPI.h"
#import "RecoveredCoreService.h"

@implementation JGCOREAPI

+ (void)ci:(BOOL)enable {
    [JCOREService setIPv6Preferred:[NSNumber numberWithBool:enable]];
}

@end
