#import "ICMFeatureChecker.h"
#import "ICMConfig.h"
@implementation ICMFeatureChecker
+ (BOOL)shouldShowTeammatePresenceView { return [[ICMConfig.sharedConfig.intercomLocale localeIdentifier] isEqual:@"en"]; }
@end
