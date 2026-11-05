// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
#import "ICMValidator.h"
@protocol ICMRegexValidatorProtocol <NSObject>
- (nonnull NSString *)regexPattern;
- (nonnull NSString *)errorMessage;
@end

@interface ICMRegexValidator : NSObject <ICMValidatorProtocol, ICMRegexValidatorProtocol>
@property (nonatomic, strong, nullable) id<ICMRegexValidatorProtocol> regexValidator;
- (nonnull NSString *)regexPattern;
- (nonnull NSString *)errorMessage;
@end
