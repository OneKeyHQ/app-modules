#import "ICMFloatValidator.h"
#import "ICMRootDestinationDependencies.h"
@implementation ICMFloatValidator
- (NSString *)regexPattern { return @"^[-+]?\\d*\\.?\\,?\\d*$"; }
- (NSString *)errorMessage { return [[ICMTranslationManager messenger] translationForKey:@"that_number_doesnt_look_quite_right"]; }
@end
