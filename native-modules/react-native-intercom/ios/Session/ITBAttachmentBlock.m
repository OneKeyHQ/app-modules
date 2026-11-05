// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBAttachmentBlock.h"
#import "ITBAttachmentBlockView.h"
#import "ICMBlockAppearance.h"
@implementation ITBAttachmentBlock
@synthesize clipColor = _clipColor, clipMargin = _clipMargin, url = _url, upload = _upload;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    // Original 0x720f8 validates the literal key, only after base validation fails.
    if (![ITBBlock validateBlockDictionary:dictionary] && [[self class] isNonEmptyString:@"url"]) return nil;
    id url = dictionary[@"url"];
    id name = dictionary[@"name"];
    NSString *text = [NSString stringWithFormat:@"<a href=\"%@\">%@</a>", url, name ?: dictionary[@"url"]];
    self = [super initWithDictionary:@{@"text":text} appearance:appearance];
    if (self) {
        _clipColor = [appearance linkTextColor];
        _clipMargin = [appearance textInsets].left;
        _url = dictionary[@"url"];
    }
    return self;
}
- (void)updateAppearance:(id)appearance {
    [super updateAppearance:appearance];
    self.clipColor = [appearance linkTextColor];
    self.clipMargin = [appearance textInsets].left;
}
- (Class)viewClass { return [ITBAttachmentBlockView class]; }
- (NSInteger)type { return 14; }
- (UIEdgeInsets)layoutInsets { return UIEdgeInsetsMake(0, 12, 0, 0); }
- (NSMutableParagraphStyle *)paragraphStyle {
    NSMutableParagraphStyle *style = [[super paragraphStyle] mutableCopy];
    style.alignment = NSTextAlignmentNatural;
    style.lineBreakMode = NSLineBreakByTruncatingTail;
    return style;
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[ITBAttachmentBlock class]]) return NO;
    if (![super isEqual:object]) return NO;
    ITBAttachmentBlock *other = object;
    if (!((!other.clipColor && !self.clipColor) || [other.clipColor isEqual:self.clipColor])) return NO;
    if (!((!other.url && !self.url) || [other.url isEqualToString:self.url])) return NO;
    return other.clipMargin == self.clipMargin;
}
- (id)copyWithZone:(NSZone *)zone {
    ITBAttachmentBlock *copy = [super copyWithZone:zone];
    copy.clipColor = self.clipColor;
    copy.clipMargin = self.clipMargin;
    copy.upload = self.upload;
    copy.url = self.url;
    return copy;
}
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [super encodeWithCoder:coder];
    [coder encodeObject:self.clipColor forKey:NSStringFromSelector(@selector(clipColor))];
    [coder encodeFloat:(float)self.clipMargin forKey:NSStringFromSelector(@selector(clipMargin))];
    [coder encodeObject:self.url forKey:NSStringFromSelector(@selector(url))];
    [coder encodeObject:self.upload forKey:NSStringFromSelector(@selector(upload))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super initWithCoder:coder];
    if (self) {
        self.clipColor = [coder decodeObjectOfClass:[UIColor class] forKey:NSStringFromSelector(@selector(clipColor))];
        self.clipMargin = [coder decodeFloatForKey:NSStringFromSelector(@selector(clipMargin))];
        self.url = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(url))];
        self.upload = [coder decodeObjectOfClass:[ITBUpload class] forKey:NSStringFromSelector(@selector(upload))];
    }
    return self;
}
@end

@implementation ITBAttachmentListBlock
@synthesize attachments = _attachments;
+ (NSArray *)attachmentsFromArray:(NSArray *)array appearance:(id)appearance {
    NSMutableArray *attachments = [NSMutableArray new];
    for (id value in array) {
        if ([value isKindOfClass:[NSDictionary class]]) {
            ITBAttachmentBlock *attachment = [[ITBAttachmentBlock alloc] initWithDictionary:value appearance:appearance];
            if (attachment) [attachments addObject:attachment];
        }
    }
    return attachments;
}
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    self = [super initWithDictionary:dictionary appearance:appearance];
    if (self) {
        _attachments = [ITBAttachmentListBlock attachmentsFromArray:dictionary[@"attachments"] appearance:appearance];
        if (!_attachments.count) return nil;
    }
    return self;
}
- (Class)viewClass { return [ITBAttachmentListBlockView class]; }
- (NSInteger)type { return 11; }
+ (BOOL)validateBlockDictionary:(NSDictionary *)dictionary {
    return [super validateBlockDictionary:dictionary] && [dictionary[@"attachments"] isKindOfClass:[NSArray class]];
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[ITBAttachmentListBlock class]]) return NO;
    if (![super isEqual:object]) return NO;
    ITBAttachmentListBlock *other = object;
    return (!other.attachments && !self.attachments) || [other.attachments isEqualToArray:self.attachments];
}
- (id)copyWithZone:(NSZone *)zone {
    ITBAttachmentListBlock *copy = [super copyWithZone:zone];
    copy.attachments = [[NSArray alloc] initWithArray:self.attachments copyItems:YES];
    return copy;
}
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [super encodeWithCoder:coder];
    [coder encodeObject:self.attachments forKey:NSStringFromSelector(@selector(attachments))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super initWithCoder:coder];
    if (self) self.attachments = [coder decodeObjectOfClasses:[NSSet setWithObjects:[NSArray class], [ITBAttachmentBlock class], nil] forKey:NSStringFromSelector(@selector(attachments))];
    return self;
}
@end
