// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBListBlock.h"
#import "ITBTextBlock.h"
#import "ICMBlockAppearance.h"

@implementation ITBListBlock
@synthesize textBlocks = _textBlocks;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    if (![ITBBlock validateBlockDictionary:dictionary]) return nil;
    self = [super initWithDictionary:dictionary appearance:appearance];
    if (self) {
        NSArray *items = dictionary[@"items"];
        _textBlocks = [[self class] textBlocksFromItems:items appearance:appearance type:self.type];
    }
    return self;
}
// Original abstract base implementation at 0x8bc10 is mov x0,0; ret.
+ (NSString *)bulletStringForIndex:(NSUInteger)index { (void)index; return nil; }
+ (NSArray *)textBlocksFromItems:(NSArray *)items appearance:(id)appearance type:(NSInteger)type {
    NSMutableArray *blocks = [NSMutableArray new];
    for (NSUInteger index = 0; index < items.count; index++) {
        NSString *prefix = index == 0 ? @"" : [@"" stringByAppendingString:@"\n"];
        NSString *bullet = [self bulletStringForIndex:index];
        id item = items[index];
        NSString *line = [NSString stringWithFormat:@"%@\t%@", bullet, item];
        NSString *text = [prefix stringByAppendingString:line];
        ITBTextBlock *block = [[ITBTextBlock alloc] initWithDictionary:@{@"text":text} appearance:appearance];
        [self applyAppearance:appearance toTextBlock:block type:type];
        [blocks addObject:block];
    }
    return blocks;
}
+ (void)applyAppearance:(id)appearance toTextBlock:(ITBTextBlock *)block type:(NSInteger)type {
    UIFont *styleFont = [appearance paragraphFont];
    NSMutableParagraphStyle *style = [self paragraphStyleForFont:styleFont];
    UIFont *font = [appearance paragraphFont];
    UIColor *color = [appearance textColor];
    [block updateAppearance:appearance font:font textColor:color paragraphStyle:style layoutInsets:UIEdgeInsetsMake(0, 10, 0, 0) blockType:type];
}
- (void)updateAppearance:(id)appearance {
    for (ITBTextBlock *block in self.textBlocks) [[self class] applyAppearance:appearance toTextBlock:block type:self.type];
}
+ (NSMutableParagraphStyle *)paragraphStyleForFont:(UIFont *)font {
    NSMutableParagraphStyle *style = [[NSParagraphStyle defaultParagraphStyle] mutableCopy];
    style.headIndent = font.pointSize * 1.5;
    style.firstLineHeadIndent = 0;
    NSTextTab *tab = [[NSTextTab alloc] initWithTextAlignment:NSTextAlignmentNatural location:font.pointSize * 1.5 options:@{}];
    style.tabStops = @[tab];
    return style;
}
- (id)copyWithZone:(NSZone *)zone {
    ITBListBlock *copy = [super copyWithZone:zone];
    copy.textBlocks = [[NSArray alloc] initWithArray:self.textBlocks copyItems:YES];
    return copy;
}
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [super encodeWithCoder:coder];
    [coder encodeObject:self.textBlocks forKey:NSStringFromSelector(@selector(textBlocks))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super initWithCoder:coder];
    if (self) {
        NSSet *classes = [NSSet setWithObjects:[NSArray class], [ITBTextBlock class], nil];
        self.textBlocks = [coder decodeObjectOfClasses:classes forKey:NSStringFromSelector(@selector(textBlocks))];
    }
    return self;
}
@end

@implementation ITBOrderedListBlock
+ (NSString *)bulletStringForIndex:(NSUInteger)index { return [NSString stringWithFormat:@"%lu.", (unsigned long)(index + 1)]; }
- (NSInteger)type { return 4; }
@end

@implementation ITBUnorderedListBlock
+ (NSString *)bulletStringForIndex:(NSUInteger)index { (void)index; return @"•"; }
- (NSInteger)type { return 3; }
@end
