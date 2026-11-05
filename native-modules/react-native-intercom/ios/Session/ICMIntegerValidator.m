// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
#import "ICMIntegerValidator.h"
#import "ICMRootDestinationDependencies.h"
@implementation ICMIntegerValidator
- (NSString *)regexPattern { return @"^[-+]?\\d+$"; }
- (NSString *)errorMessage { return [[ICMTranslationManager messenger] translationForKey:@"that_number_doesnt_look_quite_right"]; }
@end
