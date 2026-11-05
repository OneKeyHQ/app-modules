#import "ICMValidator.h"
#import "ICMCountryCode.h"

@interface ICMPhoneValidator : NSObject <ICMValidatorProtocol>
@property (nonatomic, strong) NSMutableArray<ICMCountryCode *> *countryCodes;
- (void)loadCountryCodes;
- (NSString *)normalizePhoneNumber:(NSString *)number;
- (BOOL)containsPrefix:(NSString *)number;
- (NSString *)stripPrefix:(NSString *)number;
- (BOOL)containsNonNumericCharacters:(NSString *)number;
- (ICMCountryCode *)countryCodeFromPhoneNumber:(NSString *)number;
- (BOOL)matchesAreaCodeInPhoneNumber:(NSString *)number countryCode:(ICMCountryCode *)countryCode;
- (ICMCountryCode *)countryCodeFromLocale:(NSString *)locale;
@end
