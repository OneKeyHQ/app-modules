// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBLinkBlock.h"
#import "ITBLinkBlockDependencies.h"
#import "ICMBlockAppearance.h"
@implementation ITBLinkBlock
@synthesize titleAttributedString = _titleAttributedString, linkDescriptionAttributedString = _linkDescriptionAttributedString;
@synthesize avatarRadius = _avatarRadius, url = _url, title = _title, articleId = _articleId;
@synthesize linkDescription = _linkDescription, linkType = _linkType, siteName = _siteName;
@synthesize author = _author, image = _image, data = _data, createdAt = _createdAt, updatedAt = _updatedAt;
@synthesize insets = _insets, textInsets = _textInsets;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    self = [super initWithDictionary:dictionary appearance:appearance];
    if (self) {
        self.author = [[ITBLinkBlockAuthor alloc] initWithDictionary:dictionary[@"author"] appearance:appearance];
        self.image = [[ITBLinkBlockImage alloc] initWithDictionary:dictionary[@"image"] appearance:appearance];
        [self updateAppearance:appearance];
    }
    return self;
}
- (void)updateAppearance:(id)appearance {
    [self.author updateAppearance:appearance];
    [self.image updateAppearance:appearance];
    self.insets = [appearance linkInsets];
    self.textInsets = [appearance textInsets];
    if (self.title) {
        self.titleAttributedString = [[NSAttributedString alloc] initWithString:self.title attributes:@{NSForegroundColorAttributeName:[appearance headingTextColor], NSFontAttributeName:[appearance headingFont]}];
    } else self.titleAttributedString = [NSAttributedString new];
    if (self.linkDescription) {
        NSMutableParagraphStyle *style = [[NSParagraphStyle defaultParagraphStyle] mutableCopy];
        style.lineSpacing = 1;
        self.linkDescriptionAttributedString = [[NSAttributedString alloc] initWithString:self.linkDescription attributes:@{NSForegroundColorAttributeName:[appearance subHeadingTextColor], NSFontAttributeName:[appearance subheadingFont], NSParagraphStyleAttributeName:style}];
    } else self.linkDescription = nil;
    if (self.author && self.updatedAt && [appearance respondsToSelector:@selector(linkUpdatedAtAttributedStringForDate:)]) {
        NSAttributedString *updated = [appearance linkUpdatedAtAttributedStringForDate:self.updatedAt];
        [self.author setLinkUpdatedAtAttributedString:updated.length ? updated : nil];
    } else [self.author setLinkUpdatedAtAttributedString:nil];
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([[self class] isNonEmptyString:value]) {
        if ([key isEqualToString:@"text"] || [key isEqualToString:@"url"] ||
            [key isEqualToString:@"title"] || [key isEqualToString:@"articleId"] ||
            [key isEqualToString:@"linkType"] || [key isEqualToString:@"siteName"]) {
            [super setValue:value forKey:key];
        } else if ([key isEqualToString:@"description"]) {
            [super setValue:value forKey:@"linkDescription"];
        } else if ([key isEqualToString:@"createdAt"]) {
            self.createdAt = [[[self class] iso8601DateFormatter] dateFromString:value];
        } else if ([key isEqualToString:@"updatedAt"]) {
            self.updatedAt = [[[self class] iso8601DateFormatter] dateFromString:value];
        }
    } else if ([value isKindOfClass:[NSDictionary class]]) {
        NSDictionary *dictionary = value;
        if ([key isEqualToString:@"data"]) {
            BOOL valid = YES;
            for (id field in [dictionary allKeys]) {
                if ([[self class] isNonEmptyString:field]) {
                    BOOL validValue = [[self class] isNonEmptyString:dictionary[field]];
                    valid = validValue && valid;
                } else valid = NO;
            }
            if (valid) [super setValue:dictionary forKey:key];
        }
    }
}
- (NSInteger)type { return 12; }
- (Class)viewClass { return [ITBLinkBlockView class]; }
+ (NSDateFormatter *)iso8601DateFormatter {
    static NSDateFormatter *formatter;
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        formatter = [NSDateFormatter new];
        formatter.locale = [NSLocale localeWithLocaleIdentifier:@"en_US_POSIX"];
        formatter.dateFormat = @"yyyy-MM-dd'T'HH:mm:ssZZZZZ";
    });
    return formatter;
}
+ (BOOL)validateBlockDictionary:(NSDictionary *)dictionary {
    return [super validateBlockDictionary:dictionary] && [self isNonEmptyString:dictionary[@"url"]] &&
           [self isNonEmptyString:dictionary[@"text"]] && [self isNonEmptyString:dictionary[@"title"]];
}
- (ITBIntentType)intentType { return ITBIntentTypeArticle; }
- (NSURL *)intentUrl { return [NSURL URLWithString:self.url]; }
- (NSString *)intentArticleId { return self.articleId; }
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[self class]]) return NO;
    if (![super isEqual:object]) return NO;
    ITBLinkBlock *other = object;
    if (!((!other.url && !self.url) || [other.url isEqualToString:self.url])) return NO;
    if (!((!other.title && !self.title) || [other.title isEqualToString:self.title])) return NO;
    if (!((!other.articleId && !self.articleId) || [other.articleId isEqualToString:self.articleId])) return NO;
    if (!((!other.linkDescription && !self.linkDescription) || [other.linkDescription isEqualToString:self.linkDescription])) return NO;
    if (!((!other.linkType && !self.linkType) || [other.linkType isEqualToString:self.linkType])) return NO;
    if (!((!other.siteName && !self.siteName) || [other.siteName isEqualToString:self.siteName])) return NO;
    if (!((!other.author && !self.author) || [other.author isEqual:self.author])) return NO;
    if (!((!other.image && !self.image) || [other.image isEqual:self.image])) return NO;
    if (!((!other.data && !self.data) || [other.data isEqual:self.data])) return NO;
    if (!((!other.createdAt && !self.createdAt) || [other.createdAt isEqualToDate:self.createdAt])) return NO;
    if (!((!other.updatedAt && !self.updatedAt) || [other.updatedAt isEqualToDate:self.updatedAt])) return NO;
    if (!((!other.titleAttributedString && !self.titleAttributedString) || [other.titleAttributedString isEqual:self.titleAttributedString])) return NO;
    return (!other.linkDescriptionAttributedString && !self.linkDescriptionAttributedString) || [other.linkDescriptionAttributedString isEqual:self.linkDescriptionAttributedString];
}
- (id)copyWithZone:(NSZone *)zone {
    ITBLinkBlock *copy = [super copyWithZone:zone];
    copy.url = self.url;
    copy.title = self.title;
    copy.articleId = self.articleId;
    copy.linkDescription = self.linkDescription;
    copy.linkType = self.linkType;
    copy.siteName = self.siteName;
    copy.author = [self.author copy];
    copy.image = [self.image copy];
    copy.data = [self.data copy];
    copy.createdAt = self.createdAt;
    copy.updatedAt = self.updatedAt;
    copy.titleAttributedString = self.titleAttributedString;
    copy.linkDescriptionAttributedString = self.linkDescriptionAttributedString;
    copy.textInsets = self.textInsets;
    copy.insets = self.insets;
    return copy;
}
@end
