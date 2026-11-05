// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBButtonBlock.h"
#import "ICMBlockAppearance.h"

@implementation ITBFacebookBlock
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    if (![ITBBlock validateBlockDictionary:dictionary]) return nil;
    NSString *title = [appearance facebookFollowString];
    id url = dictionary[@"url"];
    NSDictionary *attributes = @{@"text":title, @"linkUrl":url};
    return [super initWithDictionary:attributes appearance:appearance];
}
- (void)updateAppearance:(id)appearance {
    [super updateAppearance:appearance];
    self.text = [appearance facebookFollowString];
}
- (NSInteger)type { return 8; }
- (NSString *)plainTextRepresentation { return self.text.length == 0 ? nil : self.text; }
@end

@implementation ITBTwitterBlock
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    if (![ITBBlock validateBlockDictionary:dictionary]) return nil;
    if (![[self class] isNonEmptyString:dictionary[@"username"]]) return nil;
    NSString *title = [appearance twitterFollowString];
    NSString *url = [NSString stringWithFormat:@"https://www.twitter.com/%@", dictionary[@"username"]];
    NSDictionary *attributes = @{@"text":title, @"linkUrl":url};
    return [super initWithDictionary:attributes appearance:appearance];
}
- (void)updateAppearance:(id)appearance {
    [super updateAppearance:appearance];
    self.text = [appearance twitterFollowString];
}
- (NSInteger)type { return 7; }
- (NSString *)plainTextRepresentation { return self.text.length == 0 ? nil : self.text; }
@end
