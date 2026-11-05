#import "ITBBlock.h"
#import "ITBIntent.h"
@class ITBLinkBlockAuthor, ITBLinkBlockImage;
@interface ITBLinkBlock : ITBBlock <ITBIntent> {
    NSAttributedString *_titleAttributedString;
    NSAttributedString *_linkDescriptionAttributedString;
    CGFloat _avatarRadius;
    NSString *_url;
    NSString *_title;
    NSString *_articleId;
    NSString *_linkDescription;
    NSString *_linkType;
    NSString *_siteName;
    ITBLinkBlockAuthor *_author;
    ITBLinkBlockImage *_image;
    NSDictionary *_data;
    NSDate *_createdAt;
    NSDate *_updatedAt;
    UIEdgeInsets _insets;
    UIEdgeInsets _textInsets;
}
@property (nonatomic, copy) NSAttributedString *titleAttributedString;
@property (nonatomic, copy) NSAttributedString *linkDescriptionAttributedString;
@property (nonatomic) CGFloat avatarRadius;
@property (nonatomic, copy) NSString *url;
@property (nonatomic, copy) NSString *title;
@property (nonatomic, copy) NSString *articleId;
@property (nonatomic, copy) NSString *linkDescription;
@property (nonatomic, copy) NSString *linkType;
@property (nonatomic, copy) NSString *siteName;
@property (nonatomic, strong) ITBLinkBlockAuthor *author;
@property (nonatomic, strong) ITBLinkBlockImage *image;
@property (nonatomic, strong) NSDictionary *data;
@property (nonatomic, strong) NSDate *createdAt;
@property (nonatomic, strong) NSDate *updatedAt;
@property (nonatomic) UIEdgeInsets insets;
@property (nonatomic) UIEdgeInsets textInsets;
+ (NSDateFormatter *)iso8601DateFormatter;
@end
