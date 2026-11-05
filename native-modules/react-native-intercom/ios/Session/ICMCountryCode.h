// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
#import <Foundation/Foundation.h>

@interface ICMCountryCode : NSObject
@property (nonatomic, copy) NSString *code;
@property (nonatomic, copy) NSString *dialCode;
@property (nonatomic, copy) NSString *emoji;
@property (nonatomic) NSInteger priority;
@property (nonatomic, copy) NSArray *areaCodes;
+ (ICMCountryCode *)instanceFromDictionary:(id)dictionary;
- (void)setAttributesFromDictionary:(id)dictionary;
- (BOOL)isUnkown;
@end
