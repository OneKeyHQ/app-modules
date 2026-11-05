#import "ITBBlock.h"
#import "ITBIntent.h"
@interface ITBLink : NSObject {
    NSString *_url;
    NSString *_title;
    NSString *_linkType;
    NSString *_linkId;
    NSInteger _linkIntentType;
}
@property (nonatomic, copy) NSString *url;
@property (nonatomic, copy) NSString *title;
@property (nonatomic, copy) NSString *linkType;
@property (nonatomic, copy) NSString *linkId;
@property (nonatomic) NSInteger linkIntentType;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
- (NSString *)determineLinkId:(NSDictionary *)dictionary;
@end
@interface ITBLinkListBlock : ITBBlock <ITBIntent> {
    NSArray<ITBLink *> *_links;
    ITBLink *_footerLink;
    ITBLink *_selectedLink;
    UIColor *_linkTextColor;
    UIColor *_separatorColor;
    UIFont *_linkFont;
    UIImage *_footerLinkIcon;
    CGFloat _defaultLinkHeight;
    CGFloat _maxLinkHeight;
    CGSize _linkFooterIconSize;
    UIEdgeInsets _textInsets;
    UIEdgeInsets _linkFooterInsets;
}
@property (nonatomic, strong) NSArray<ITBLink *> *links;
@property (nonatomic, strong) ITBLink *footerLink;
@property (nonatomic, strong) ITBLink *selectedLink;
@property (nonatomic, strong) UIColor *linkTextColor;
@property (nonatomic, strong) UIColor *separatorColor;
@property (nonatomic, strong) UIFont *linkFont;
@property (nonatomic, strong) UIImage *footerLinkIcon;
@property (nonatomic) CGFloat defaultLinkHeight;
@property (nonatomic) CGFloat maxLinkHeight;
@property (nonatomic) CGSize linkFooterIconSize;
@property (nonatomic) UIEdgeInsets textInsets;
@property (nonatomic) UIEdgeInsets linkFooterInsets;
- (void)setFooterAttributes:(NSDictionary *)dictionary;
+ (BOOL)validateLinksArray:(NSArray *)links;
+ (BOOL)validateFooterLink:(NSDictionary *)link;
+ (BOOL)validateLink:(NSDictionary *)link;
@end
