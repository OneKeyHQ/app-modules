#import "ICMBlockViewFactoryImports.h"
@interface ICMContentStackView : UIStackView
@property (nonatomic, strong) NSArray<ITBBlock *> *blocks;
@property (nonatomic, strong) ITBBlock *nextBlock;
@property (nonatomic) NSInteger context;
@property (nonatomic) CGFloat constrainedWidth;
@property (nonatomic, weak) OutboundContentBlockDelegate *blockDelegate;
- (instancetype)initWithBlocks:(NSArray<ITBBlock *> *)blocks context:(NSInteger)context withinWidth:(CGFloat)width blockDelegate:(OutboundContentBlockDelegate *)delegate;
- (instancetype)initWithBlocks:(NSArray<ITBBlock *> *)blocks context:(NSInteger)context blockDelegate:(OutboundContentBlockDelegate *)delegate;
- (instancetype)initWithBlocks:(NSArray<ITBBlock *> *)blocks context:(NSInteger)context withinWidth:(CGFloat)width;
- (instancetype)initWithBlocks:(NSArray<ITBBlock *> *)blocks context:(NSInteger)context;
- (void)configureMainStackView;
- (void)renderContent;
- (void)appendBlock:(ITBBlock *)block;
- (void)insertBlock:(ITBBlock *)block atIndex:(NSInteger)index;
- (void)applyCustomSpacingToBlock:(ITBBlock *)block afterView:(UIView *)view;
- (void)updateFirstBlockWithAppearance:(id<ITBBlockAppearance>)appearance;
@end
