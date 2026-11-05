#import "ITBHTMLParser.h"
#import <libxml/HTMLparser.h>
#import <string.h>

@implementation ITBHTMLParser
+ (NSAttributedString *)attributedStringFromHTML:(NSString *)html defaultAttributes:(NSDictionary *)attributes codeAttributes:(NSDictionary *)code {
    NSData *data = [html dataUsingEncoding:NSUTF8StringEncoding];
    const void *bytes = data.bytes;
    htmlDocPtr document = htmlReadMemory(bytes, (int)data.length, NULL, "UTF-8", 0x864);
    if (!document) return [NSAttributedString new];
    NSMutableAttributedString *result = [NSMutableAttributedString new];
    for (xmlNodePtr node = document->children; node; node = node->next) {
        NSAttributedString *text = [self attributedStringFromNode:node attributes:attributes codeAttributes:code];
        if (text) [result appendAttributedString:text];
    }
    xmlFreeDoc(document);
    return result;
}
+ (NSAttributedString *)attributedStringFromNode:(xmlNodePtr)node attributes:(NSDictionary *)attributes codeAttributes:(NSDictionary *)code {
    NSMutableDictionary *current = [attributes mutableCopy];
    NSMutableAttributedString *result = [NSMutableAttributedString new];
    if (node->type == XML_ELEMENT_NODE) {
        const char *name = (const char *)node->name;
        size_t length = strlen(name);
        if (strncmp("b", name, length) == 0) {
            UIFont *font = [self font:attributes[NSFontAttributeName] withTraits:UIFontDescriptorTraitBold];
            if (font) [current setObject:font forKey:NSFontAttributeName];
        } else if (strncmp("i", name, length) == 0) {
            UIFont *font = [self font:attributes[NSFontAttributeName] withTraits:UIFontDescriptorTraitItalic];
            if (font) [current setObject:font forKey:NSFontAttributeName];
        } else if (strncmp("u", name, length) == 0) {
            [current setObject:@1 forKey:NSUnderlineStyleAttributeName];
        } else if (strncmp("a href", name, length) == 0) {
            xmlChar *href = xmlGetProp(node, (const xmlChar *)"href");
            if (href) {
                NSString *link = [NSString stringWithCString:(const char *)href encoding:NSUTF8StringEncoding];
                [current addEntriesFromDictionary:@{NSLinkAttributeName:link, NSUnderlineStyleAttributeName:@1}];
                free(href);
            }
        } else if (strncmp("br", name, length) == 0) {
            [result appendAttributedString:[[NSAttributedString alloc] initWithString:@"\n" attributes:current]];
        } else if (strncmp("code", name, length) == 0) {
            current = [code mutableCopy];
        } else if (strncmp("p", name, length) != 0 && strncmp("body", name, length) != 0 && strncmp("html", name, length) != 0) {
            return nil;
        }
    }
    if ((node->type | 4) != 5 && node->content) {
        NSString *text = [NSString stringWithCString:(const char *)node->content encoding:NSUTF8StringEncoding];
        return [[NSAttributedString alloc] initWithString:text attributes:current];
    }
    for (xmlNodePtr child = node->children; child; child = child->next) {
        NSAttributedString *text = [self attributedStringFromNode:child attributes:current codeAttributes:code];
        if (text) [result appendAttributedString:text];
    }
    return result;
}
+ (UIFont *)font:(UIFont *)font withTraits:(UIFontDescriptorSymbolicTraits)traits {
    if (!font.fontDescriptor) return nil;
    UIFontDescriptor *descriptor = font.fontDescriptor;
    UIFontDescriptorSymbolicTraits combined = font.fontDescriptor.symbolicTraits | traits;
    UIFontDescriptor *modified = [descriptor fontDescriptorWithSymbolicTraits:combined];
    CGFloat size = font.pointSize;
    return [UIFont fontWithDescriptor:modified size:size];
}
@end
