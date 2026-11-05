#pragma once
#import <UIKit/UIKit.h>
// Original anonymous native value layout and field names from the consumer's ivar encoding.
typedef struct {
    CGFloat headerHeight;
    CGFloat footerHeight;
    CGFloat avatarDiameter;
    CGFloat closeButtonPadding;
    CGFloat headerCloseButtonRightPadding;
    CGFloat fakeComposerHorizontalPadding;
    UIEdgeInsets blockListInsets;
} ICMInAppAttributes;
@protocol ICMFullScreenInAppDelegate <NSObject>
- (void)openPressed;
- (void)closePressed;
- (UIColor *)headerSeparatorColor;
- (UIColor *)footerSeparatorColor;
- (UIColor *)fakeComposerInactiveTextColor;
@optional
- (void)didCalculateBlocksSize:(CGSize)size;
@end
@protocol ICMInAppHeaderDelegate <NSObject>
- (CGFloat)headerCloseButtonRightPadding;
- (void)closePressed;
@end
@class ICMReactionsViewController, ICMReactionsOption;
@protocol ICMReactionsViewControllerDelegate <NSObject>
- (void)reactionsViewController:(ICMReactionsViewController *)controller reactedWithOption:(ICMReactionsOption *)option;
@end
