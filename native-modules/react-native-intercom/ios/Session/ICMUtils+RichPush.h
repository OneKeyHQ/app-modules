#import "../Identity/ICMUtils.h"
#import <UIKit/UIKit.h>
@interface ICMUtils (OriginalRichPush)
+ (NSString *)getFileExtensionFrom:(NSURL *)url httpResponse:(NSURLResponse *)response;
+ (void)forceLTRLayoutForContainerClass:(Class)containerClass;
@end
