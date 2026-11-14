#import "ITBTextBlock.h"
#import "ITBTextBlockDependencies.h"
#import "ICMBlockAppearance.h"

@implementation ITBTextBlock
@synthesize blockType = _blockType, attributedString = _attributedString, font = _font;
@synthesize textColor = _textColor, linkTextColor = _linkTextColor, backgroundColor = _backgroundColor;
@synthesize requiredIndicatorColor = _requiredIndicatorColor, paragraphStyle = _paragraphStyle;
@synthesize layoutInsets = _layoutInsets, textInsets = _textInsets, isTextSelectable = _isTextSelectable;
@synthesize inlineCitations = _inlineCitations;

- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    return [self initWithDictionary:dictionary appearance:appearance font:[appearance paragraphFont] textColor:[appearance textColor]];
}
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance font:(UIFont *)font textColor:(UIColor *)color {
    self = [super initWithDictionary:dictionary appearance:appearance];
    if (self) [self updateAppearance:appearance font:font textColor:color];
    return self;
}
- (void)updateAppearance:(id)appearance {
    [self updateAppearance:appearance font:[appearance paragraphFont] textColor:[appearance textColor]];
}
- (void)updateAppearance:(id)appearance font:(UIFont *)font textColor:(UIColor *)color {
    [self updateAppearance:appearance font:font textColor:color paragraphStyle:self.paragraphStyle layoutInsets:UIEdgeInsetsZero blockType:0];
}
- (void)updateAppearance:(id)appearance font:(UIFont *)font textColor:(UIColor *)color paragraphStyle:(NSMutableParagraphStyle *)style layoutInsets:(UIEdgeInsets)insets blockType:(NSInteger)type {
    _blockType = type;
    _textColor = color;
    _linkTextColor = [appearance linkTextColor];
    _font = font;
    _backgroundColor = UIColor.clearColor;
    _requiredIndicatorColor = [appearance requiredIndicatorColor];
    _layoutInsets = insets;
    _textInsets = [[self class] textInsetsForBlockType:type appearance:appearance];
    _isTextSelectable = [appearance isTextSelectable];
    _attributedString = [self attributedStringForAppearance:appearance font:font textColor:color paragraphStyle:style];
    _inlineCitations = [ITBInlineCitationParser parseInlineCitationsFromHTML:self.text];
}
+ (UIEdgeInsets)textInsetsForBlockType:(NSInteger)type appearance:(id)appearance {
    UIEdgeInsets insets = [appearance textInsets];
    if (type == 3 || type == 4) {
        CGFloat right = [appearance textInsets].right;
        return UIEdgeInsetsMake(0, insets.left, 0, right);
    }
    return insets;
}
- (NSAttributedString *)attributedStringForAppearance:(id)appearance font:(UIFont *)font textColor:(UIColor *)color paragraphStyle:(NSMutableParagraphStyle *)style {
    NSString *text = self.text;
    NSDictionary *defaults = @{NSFontAttributeName:font, NSForegroundColorAttributeName:color, NSParagraphStyleAttributeName:style};
    NSDictionary *code = [self codeAttributesForAppearance:appearance font:font textColor:color paragraphStyle:style];
    NSMutableAttributedString *result = [[ITBHTMLParser attributedStringFromHTML:text defaultAttributes:defaults codeAttributes:code] mutableCopy];
    [self applyInlineCodePadding:[appearance inlineCodePadding] toString:result];
    if (self.isRequired) {
        // The original deliberately reads the previous attributed-string ivar.
        NSMutableDictionary *attributes = [[_attributedString attributesAtIndex:0 effectiveRange:nil] mutableCopy];
        attributes[NSForegroundColorAttributeName] = [appearance requiredIndicatorColor];
        attributes[NSBackgroundColorAttributeName] = nil;
        [result appendAttributedString:[[NSMutableAttributedString alloc] initWithString:@"*" attributes:attributes]];
        self.attributedString = [result copy];
    }
    return [result copy];
}
- (NSDictionary *)codeAttributesForAppearance:(id)appearance font:(UIFont *)font textColor:(UIColor *)color paragraphStyle:(NSMutableParagraphStyle *)style {
    NSMutableDictionary *attributes = [NSMutableDictionary dictionary];
    attributes[NSFontAttributeName] = [appearance codeFont] ?: font;
    attributes[NSForegroundColorAttributeName] = [appearance codeTextColor] ?: color;
    attributes[NSBackgroundColorAttributeName] = [appearance codeBackgroundColor];
    attributes[NSParagraphStyleAttributeName] = style;
    return attributes;
}
- (void)applyInlineCodePadding:(CGFloat)padding toString:(NSMutableAttributedString *)string {
    if (padding <= 0 || string.length == 0) return;
    NSMutableIndexSet *codeIndexes = [NSMutableIndexSet indexSet];
    [string enumerateAttribute:NSBackgroundColorAttributeName inRange:NSMakeRange(0, string.length) options:0 usingBlock:^(id value, NSRange range, BOOL *stop) {
        (void)stop;
        if (value) [codeIndexes addIndexesInRange:range];
    }];
    NSMutableIndexSet *paddingIndexes = [NSMutableIndexSet indexSet];
    [codeIndexes enumerateRangesUsingBlock:^(NSRange range, BOOL *stop) {
        (void)stop;
        if (range.location != 0) [paddingIndexes addIndex:range.location - 1];
        NSUInteger end = range.location + range.length;
        if (end < string.length) [paddingIndexes addIndex:end];
    }];
    [paddingIndexes enumerateRangesUsingBlock:^(NSRange range, BOOL *stop) {
        (void)stop;
        [string addAttribute:NSKernAttributeName value:@(padding) range:range];
    }];
}
- (void)dealloc { self.attributedString = nil; }
- (NSInteger)type { return self.blockType; }
- (Class)viewClass { return [ITBTextBlockView class]; }
+ (BOOL)validateBlockDictionary:(NSDictionary *)dictionary {
    return [super validateBlockDictionary:dictionary] && [self isNonEmptyString:dictionary[@"text"]];
}
// The original getter builds a fresh style and ignores the setter's retained ivar.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
- (NSMutableParagraphStyle *)paragraphStyle {
    NSMutableParagraphStyle *style = [[NSParagraphStyle defaultParagraphStyle] mutableCopy];
    style.alignment = NSTextAlignmentNatural;
    if ([self.align isEqualToString:@"center"]) style.alignment = NSTextAlignmentCenter;
    else if ([self.align isEqualToString:@"right"]) {
        style.alignment = UIApplication.sharedApplication.userInterfaceLayoutDirection == UIUserInterfaceLayoutDirectionLeftToRight ? NSTextAlignmentRight : NSTextAlignmentLeft;
    }
    return style;
}
#pragma clang diagnostic pop
- (NSString *)plainTextRepresentation {
    if (self.attributedString.length == 0) return nil;
    __block NSString *text = self.attributedString.string;
    NSAttributedString *attributed = self.attributedString;
    NSUInteger length = self.attributedString.string.length;
    [attributed enumerateAttributesInRange:NSMakeRange(0, length) options:(NSAttributedStringEnumerationOptions)-1 usingBlock:^(NSDictionary *attributes, NSRange range, BOOL *stop) {
        (void)stop;
        [attributes enumerateKeysAndObjectsUsingBlock:^(id key, id value, BOOL *innerStop) {
            (void)innerStop;
            if (key == NSLinkAttributeName) text = [text stringByReplacingCharactersInRange:range withString:value];
        }];
    }];
    return text;
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[self class]]) return NO;
    if (![super isEqual:object]) return NO;
    ITBTextBlock *other = object;
    if (!((!other.attributedString && !self.attributedString) || [other.attributedString isEqual:self.attributedString])) return NO;
    if (!((!other.font && !self.font) || [other.font isEqual:self.font])) return NO;
    if (!((!other.textColor && !self.textColor) || [other.textColor isEqual:self.textColor])) return NO;
    if (!((!other.linkTextColor && !self.linkTextColor) || [other.linkTextColor isEqual:self.linkTextColor])) return NO;
    if (!((!other.backgroundColor && !self.backgroundColor) || [other.backgroundColor isEqual:self.backgroundColor])) return NO;
    if (!((!other.requiredIndicatorColor && !self.requiredIndicatorColor) || [other.requiredIndicatorColor isEqual:self.requiredIndicatorColor])) return NO;
    if (!((!other.paragraphStyle && !self.paragraphStyle) || [other.paragraphStyle isEqual:self.paragraphStyle])) return NO;
    UIEdgeInsets otherLayout = other.layoutInsets;
    UIEdgeInsets layout = self.layoutInsets;
    if (otherLayout.left != layout.left || otherLayout.top != layout.top || otherLayout.right != layout.right || otherLayout.bottom != layout.bottom) return NO;
    UIEdgeInsets otherText = other.textInsets;
    UIEdgeInsets text = self.textInsets;
    if (otherText.left != text.left || otherText.top != text.top || otherText.right != text.right || otherText.bottom != text.bottom) return NO;
    return other.isTextSelectable == self.isTextSelectable;
}
- (id)copyWithZone:(NSZone *)zone {
    ITBTextBlock *copy = [super copyWithZone:zone];
    copy.attributedString = self.attributedString;
    copy.font = self.font;
    copy.textColor = self.textColor;
    copy.linkTextColor = self.linkTextColor;
    copy.backgroundColor = self.backgroundColor;
    copy.requiredIndicatorColor = self.requiredIndicatorColor;
    copy.paragraphStyle = [self.paragraphStyle copy];
    copy.layoutInsets = self.layoutInsets;
    copy.textInsets = self.textInsets;
    copy.isTextSelectable = self.isTextSelectable;
    return copy;
}
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [super encodeWithCoder:coder];
    [coder encodeObject:self.attributedString forKey:NSStringFromSelector(@selector(attributedString))];
    [coder encodeObject:self.font forKey:NSStringFromSelector(@selector(font))];
    [coder encodeObject:self.textColor forKey:NSStringFromSelector(@selector(textColor))];
    [coder encodeObject:self.linkTextColor forKey:NSStringFromSelector(@selector(linkTextColor))];
    [coder encodeObject:self.backgroundColor forKey:NSStringFromSelector(@selector(backgroundColor))];
    [coder encodeObject:self.requiredIndicatorColor forKey:NSStringFromSelector(@selector(requiredIndicatorColor))];
    [coder encodeObject:self.paragraphStyle forKey:NSStringFromSelector(@selector(paragraphStyle))];
    [coder encodeUIEdgeInsets:self.layoutInsets forKey:NSStringFromSelector(@selector(layoutInsets))];
    [coder encodeUIEdgeInsets:self.textInsets forKey:NSStringFromSelector(@selector(textInsets))];
    [coder encodeBool:self.isTextSelectable forKey:NSStringFromSelector(@selector(isTextSelectable))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super initWithCoder:coder];
    if (self) {
        self.attributedString = [coder decodeObjectOfClass:[NSAttributedString class] forKey:NSStringFromSelector(@selector(attributedString))];
        self.font = [coder decodeObjectOfClass:[UIFont class] forKey:NSStringFromSelector(@selector(font))];
        self.textColor = [coder decodeObjectOfClass:[UIColor class] forKey:NSStringFromSelector(@selector(textColor))];
        self.linkTextColor = [coder decodeObjectOfClass:[UIColor class] forKey:NSStringFromSelector(@selector(linkTextColor))];
        self.backgroundColor = [coder decodeObjectOfClass:[UIColor class] forKey:NSStringFromSelector(@selector(backgroundColor))];
        self.requiredIndicatorColor = [coder decodeObjectOfClass:[UIColor class] forKey:NSStringFromSelector(@selector(requiredIndicatorColor))];
        self.paragraphStyle = [coder decodeObjectOfClass:[NSMutableParagraphStyle class] forKey:NSStringFromSelector(@selector(paragraphStyle))];
        self.layoutInsets = [coder decodeUIEdgeInsetsForKey:NSStringFromSelector(@selector(layoutInsets))];
        self.textInsets = [coder decodeUIEdgeInsetsForKey:NSStringFromSelector(@selector(textInsets))];
        self.isTextSelectable = [coder decodeBoolForKey:NSStringFromSelector(@selector(isTextSelectable))];
    }
    return self;
}
@end

@implementation ITBParagraphBlock
- (NSMutableParagraphStyle *)paragraphStyle {
    NSMutableParagraphStyle *style = [super paragraphStyle];
    style.lineHeightMultiple = 1.1;
    return style;
}
- (NSInteger)type { return 0; }
@end
