// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBInlineCitation.h"
#import <libxml/HTMLparser.h>
#import <libxml/xpath.h>
#import <string.h>

@implementation ITBInlineCitationParser
+ (NSArray *)parseInlineCitationsFromHTML:(NSString *)html {
    const char *bytes = html.UTF8String;
    htmlDocPtr document = htmlReadMemory(bytes, (int)strlen(bytes), NULL, NULL, 0x61);
    if (!document) return @[];
    xmlXPathContextPtr context = xmlXPathNewContext(document);
    if (!context) {
        xmlFreeDoc(document);
        return @[];
    }
    xmlXPathObjectPtr object = xmlXPathEvalExpression((const xmlChar *)"//a[contains(@class, 'inline-citation')]", context);
    if (!object || !object->nodesetval || object->nodesetval->nodeNr == 0 || !object->nodesetval->nodeTab) {
        // Original early-result path does not free an existing XPath object.
        xmlXPathFreeContext(context);
        xmlFreeDoc(document);
        return @[];
    }
    NSMutableArray *citations = [NSMutableArray array];
    xmlNodeSetPtr nodes = object->nodesetval;
    for (int i = 0; i < nodes->nodeNr; i++) {
        xmlNodePtr node = nodes->nodeTab[i];
        NSMutableDictionary *attributes = [NSMutableDictionary dictionary];
        for (xmlAttrPtr attribute = node->properties; attribute; attribute = attribute->next) {
            xmlChar *value = xmlNodeListGetString(document, attribute->children, 1);
            if (value) {
                NSString *key = [NSString stringWithUTF8String:(const char *)attribute->name];
                NSString *text = [NSString stringWithUTF8String:(const char *)value];
                attributes[key] = text;
                free(value);
            }
        }
        ITBInlineCitation *citation = [[ITBInlineCitation alloc] initWithDictionary:attributes];
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"sourceIndex == %@", citation.sourceIndex];
        if ([citations filteredArrayUsingPredicate:predicate].count == 0) [citations addObject:citation];
    }
    xmlXPathFreeObject(object);
    xmlXPathFreeContext(context);
    xmlFreeDoc(document);
    return citations;
}
@end
