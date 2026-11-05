#import "ITBBlock.h"
@class ITBInlineCitation;

@interface ITBTextBlock : ITBBlock {
    BOOL _isTextSelectable;
    NSAttributedString *_attributedString;
    UIFont *_font;
    UIColor *_textColor;
    UIColor *_linkTextColor;
    UIColor *_backgroundColor;
    UIColor *_requiredIndicatorColor;
    NSMutableParagraphStyle *_paragraphStyle;
    NSArray<ITBInlineCitation *> *_inlineCitations;
    NSInteger _blockType;
    UIEdgeInsets _layoutInsets;
    UIEdgeInsets _textInsets;
}
@property (nonatomic) NSInteger blockType;
@property (nonatomic, copy) NSAttributedString *attributedString;
@property (nonatomic, strong) UIFont *font;
@property (nonatomic, strong) UIColor *textColor;
@property (nonatomic, strong) UIColor *linkTextColor;
@property (nonatomic, strong) UIColor *backgroundColor;
@property (nonatomic, strong) UIColor *requiredIndicatorColor;
@property (nonatomic, strong) NSMutableParagraphStyle *paragraphStyle;
@property (nonatomic) UIEdgeInsets layoutInsets;
@property (nonatomic) UIEdgeInsets textInsets;
@property (nonatomic) BOOL isTextSelectable;
@property (nonatomic, strong) NSArray<ITBInlineCitation *> *inlineCitations;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id<ITBBlockAppearance>)appearance font:(UIFont *)font textColor:(UIColor *)color;
- (void)updateAppearance:(id<ITBBlockAppearance>)appearance font:(UIFont *)font textColor:(UIColor *)color;
- (void)updateAppearance:(id<ITBBlockAppearance>)appearance font:(UIFont *)font textColor:(UIColor *)color paragraphStyle:(NSMutableParagraphStyle *)style layoutInsets:(UIEdgeInsets)insets blockType:(NSInteger)type;
+ (UIEdgeInsets)textInsetsForBlockType:(NSInteger)type appearance:(id)appearance;
- (NSAttributedString *)attributedStringForAppearance:(id)appearance font:(UIFont *)font textColor:(UIColor *)color paragraphStyle:(NSMutableParagraphStyle *)style;
- (NSDictionary *)codeAttributesForAppearance:(id)appearance font:(UIFont *)font textColor:(UIColor *)color paragraphStyle:(NSMutableParagraphStyle *)style;
- (void)applyInlineCodePadding:(CGFloat)padding toString:(NSMutableAttributedString *)string;
@end

@interface ITBParagraphBlock : ITBTextBlock
@end
@interface ITBHeadingBlock : ITBTextBlock
@end
@interface ITBSubHeadingBlock : ITBTextBlock
@end
@interface ITBCodeBlock : ITBTextBlock
@end
