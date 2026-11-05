#import <UIKit/UIKit.h>
@interface ICMAppearanceReset : NSObject
+ (void)resetAppearanceWithin:(Class)container;
+ (void)resetUIView:(Class)container;
+ (void)resetUIAlertController:(Class)container;
@end
