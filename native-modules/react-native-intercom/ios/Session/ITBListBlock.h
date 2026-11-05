#import "ITBBlock.h"
@class ITBTextBlock;
@interface ITBListBlock : ITBBlock {
    NSArray<ITBTextBlock *> *_textBlocks;
}
@property (nonatomic, strong) NSArray<ITBTextBlock *> *textBlocks;
+ (NSString *)bulletStringForIndex:(NSUInteger)index;
+ (NSArray *)textBlocksFromItems:(NSArray *)items appearance:(id)appearance type:(NSInteger)type;
+ (void)applyAppearance:(id)appearance toTextBlock:(ITBTextBlock *)block type:(NSInteger)type;
+ (NSMutableParagraphStyle *)paragraphStyleForFont:(UIFont *)font;
@end
@interface ITBOrderedListBlock : ITBListBlock
@end
@interface ITBUnorderedListBlock : ITBListBlock
@end
