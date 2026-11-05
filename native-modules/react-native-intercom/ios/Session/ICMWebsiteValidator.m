#import "ICMWebsiteValidator.h"
#import "ICMRootDestinationDependencies.h"
@implementation ICMWebsiteValidator
- (NSString *)regexPattern { return @"((http|https)://)?((\\w)*|([0-9]*)|([-|_])*)+([\\.|/]((\\w)*|([0-9]*)|([-|_])*))+"; }
- (NSString *)errorMessage { return [[ICMTranslationManager messenger] translationForKey:@"string_is_incorrect"]; }
@end
