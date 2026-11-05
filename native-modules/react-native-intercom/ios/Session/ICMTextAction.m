#import "ICMTextAction.h"

@implementation ICMTextAction
@synthesize text = _text, blocks = _blocks;
- (instancetype)initWithText:(NSString *)text {
    NSString *retainedText = text;
    self = [super init];
    if (self) {
        _text = retainedText;
        if (retainedText != nil && retainedText.length != 0) _blocks = [ICMTextAction blockBodyForText:retainedText];
    }
    return self;
}
+ (NSArray *)blockBodyForText:(NSString *)text {
    NSString *trimmed = [text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    NSArray *components = [trimmed componentsSeparatedByString:@"\n\n"];
    NSMutableArray *blocks = [NSMutableArray array];
    for (NSString *component in components) {
        if (component.length == 0) continue;
        NSDictionary *block = @{@"type": @"paragraph", @"text": [component stringByReplacingOccurrencesOfString:@"\n" withString:@"<br>"]};
        [blocks addObject:block];
    }
    return blocks;
}
@end
