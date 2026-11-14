#import <UIKit/UIKit.h>
struct _xmlNode;
@interface ITBHTMLParser : NSObject
+ (NSAttributedString *)attributedStringFromHTML:(NSString *)html defaultAttributes:(NSDictionary *)attributes codeAttributes:(NSDictionary *)code;
+ (NSAttributedString *)attributedStringFromNode:(struct _xmlNode *)node attributes:(NSDictionary *)attributes codeAttributes:(NSDictionary *)code;
+ (UIFont *)font:(UIFont *)font withTraits:(UIFontDescriptorSymbolicTraits)traits;
@end
