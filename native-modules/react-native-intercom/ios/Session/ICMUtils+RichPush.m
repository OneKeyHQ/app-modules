#import "ICMUtils+RichPush.h"
#import <UniformTypeIdentifiers/UniformTypeIdentifiers.h>
@implementation ICMUtils (OriginalRichPush)
+ (NSString *)getFileExtensionFrom:(NSURL *)url httpResponse:(NSURLResponse *)response {
    NSString *extension = url.pathExtension;
    if (extension && extension.length != 0) return extension;
    NSString *mime = response.MIMEType;
    return mime ? [UTType typeWithMIMEType:mime].preferredFilenameExtension : nil;
}
+ (void)forceLTRLayoutForContainerClass:(Class)containerClass {
    [UIView appearanceWhenContainedInInstancesOfClasses:@[containerClass, [UIView class]]].semanticContentAttribute = UISemanticContentAttributeForceLeftToRight;
}
@end
