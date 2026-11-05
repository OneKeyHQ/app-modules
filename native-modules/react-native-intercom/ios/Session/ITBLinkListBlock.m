// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBLinkListBlock.h"
#import "ITBLinkListBlockView.h"
#import "ICMBlockAppearance.h"
@implementation ITBLinkListBlock
@synthesize links = _links, footerLink = _footerLink, selectedLink = _selectedLink;
@synthesize linkTextColor = _linkTextColor, separatorColor = _separatorColor, linkFont = _linkFont;
@synthesize footerLinkIcon = _footerLinkIcon, defaultLinkHeight = _defaultLinkHeight, maxLinkHeight = _maxLinkHeight;
@synthesize linkFooterIconSize = _linkFooterIconSize, textInsets = _textInsets, linkFooterInsets = _linkFooterInsets;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    if (![appearance respondsToSelector:@selector(headingFont)] ||
        ![appearance respondsToSelector:@selector(headingTextColor)] ||
        ![appearance respondsToSelector:@selector(textInsets)] ||
        ![appearance respondsToSelector:@selector(separatorColor)] ||
        ![appearance respondsToSelector:@selector(linkFooterImage)] ||
        ![appearance respondsToSelector:@selector(linkFooterImageSize)] ||
        ![appearance respondsToSelector:@selector(defaultLinkHeight)] ||
        ![appearance respondsToSelector:@selector(maxLinkHeight)] ||
        ![appearance respondsToSelector:@selector(linkFooterInsets)]) return nil;
    self = [super initWithDictionary:dictionary appearance:appearance];
    if (self) {
        NSMutableArray *links = [NSMutableArray new];
        for (id dictionaryLink in dictionary[@"links"])
            [links addObject:[[ITBLink alloc] initWithDictionary:dictionaryLink]];
        _links = links;
        [self setFooterAttributes:dictionary];
        [self updateAppearance:appearance];
    }
    return self;
}
- (void)setFooterAttributes:(NSDictionary *)dictionary {
    id footer = dictionary[@"footerLink"];
    if (footer) self.footerLink = [[ITBLink alloc] initWithDictionary:footer];
}
- (void)updateAppearance:(id)appearance {
    [super updateAppearance:appearance];
    _linkFont = [appearance headingFont];
    _linkTextColor = [appearance headingTextColor];
    _textInsets = [appearance textInsets];
    _separatorColor = [appearance separatorColor];
    _footerLinkIcon = [appearance linkFooterImage];
    _defaultLinkHeight = [appearance defaultLinkHeight];
    _maxLinkHeight = [appearance maxLinkHeight];
    _linkFooterIconSize = [appearance linkFooterImageSize];
    _linkFooterInsets = [appearance linkFooterInsets];
}
- (NSInteger)type { return 17; }
- (Class)viewClass { return [ITBLinkListBlockView class]; }
- (ITBIntentType)intentType { return (ITBIntentType)self.selectedLink.linkIntentType; }
- (NSString *)intentArticleId { return self.selectedLink.linkId; }
- (NSURL *)intentUrl { return [NSURL URLWithString:self.selectedLink.url]; }
+ (BOOL)validateBlockDictionary:(NSDictionary *)dictionary {
    return [self validateLinksArray:dictionary[@"links"]] && [self validateFooterLink:dictionary[@"footerLink"]];
}
+ (BOOL)validateLinksArray:(NSArray *)links {
    if (![links isKindOfClass:[NSArray class]]) return NO;
    for (id link in links) if (![self validateLink:link]) return NO;
    return links.count != 0;
}
+ (BOOL)validateFooterLink:(NSDictionary *)link {
    return !link || [self validateLink:link];
}
+ (BOOL)validateLink:(NSDictionary *)link {
    return [link isKindOfClass:[NSDictionary class]] &&
           [link[@"url"] isKindOfClass:[NSString class]] &&
           [link[@"title"] isKindOfClass:[NSString class]];
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[self class]]) return NO;
    if (![super isEqual:object]) return NO;
    ITBLinkListBlock *other = object;
    if (!((!other.footerLink && !self.footerLink) || [other.footerLink isEqual:self.footerLink])) return NO;
    return (!other.links && !self.links) || [other.links isEqualToArray:self.links];
}
- (id)copyWithZone:(NSZone *)zone {
    ITBLinkListBlock *copy = [super copyWithZone:zone];
    copy.footerLink = self.footerLink;
    copy.links = self.links;
    return copy;
}
@end
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
@implementation ITBLink
@synthesize url = _url, title = _title, linkType = _linkType, linkId = _linkId, linkIntentType = _linkIntentType;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary {
    self = [super init];
    if (self) {
        _url = dictionary[@"url"];
        _title = dictionary[@"title"];
        _linkId = [self determineLinkId:dictionary];
        _linkType = dictionary[@"linkType"];
    }
    return self;
}
- (NSString *)determineLinkId:(NSDictionary *)dictionary {
    if ([dictionary[@"linkType"] isEqualToString:@"educate.suggestion"] ||
        [dictionary[@"linkType"] isEqualToString:@"educate.article"])
        return dictionary[@"articleId"];
    return nil;
}
- (NSInteger)linkIntentType {
    if ([self.linkType isEqualToString:@"educate.suggestion"] || [self.linkType isEqualToString:@"educate.article"]) return 1;
    return [self.linkType isEqualToString:@"educate.help_center"] ? 2 : 0;
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[self class]]) return NO;
    ITBLink *other = object;
    if (![self.url isEqual:other.url]) return NO;
    if (![self.title isEqual:other.title]) return NO;
    if (!((!self.linkId && !other.linkId) || [self.linkId isEqual:other.linkId])) return NO;
    return [self.linkType isEqual:other.linkType];
}
@end
#pragma clang diagnostic pop
