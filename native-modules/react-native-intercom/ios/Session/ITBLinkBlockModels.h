#import "ITBBlock.h"
@interface ITBLinkBlockAuthor : ITBBlock {
    UIImage *_initialsImage;
    NSAttributedString *_writtenByAttributedString;
    NSAttributedString *_linkUpdatedAtAttributedString;
    NSString *_name;
    NSString *_firstName;
    NSString *_lastName;
    NSString *_avatar;
    CGFloat _avatarRadius;
}
@property (nonatomic, strong) UIImage *initialsImage;
@property (nonatomic, copy) NSAttributedString *writtenByAttributedString;
@property (nonatomic, copy) NSAttributedString *linkUpdatedAtAttributedString;
@property (nonatomic, copy) NSString *name;
@property (nonatomic, copy) NSString *firstName;
@property (nonatomic, copy) NSString *lastName;
@property (nonatomic, copy) NSString *avatar;
@property (nonatomic) CGFloat avatarRadius;
@end
@interface ITBLinkBlockImage : ITBBlock {
    NSString *_url;
    NSString *_alt;
    CGSize _size;
}
@property (nonatomic, copy) NSString *url;
@property (nonatomic, copy) NSString *alt;
@property (nonatomic) CGSize size;
@end
