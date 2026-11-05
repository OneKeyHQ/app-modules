#import "ICMRootViewController.h"
@interface ICMRootViewController (Presentation)
- (BOOL)shouldSnapshotHostAppWindow;
- (void)insertSnapshotOfHostAppWindow;
- (void)removeSnapshotOfHostAppWindow;
- (UINavigationController *)newNavigationControllerInMessengerNavigationController:(BOOL)messenger;
- (void)presentIntercomModal:(NSArray *)controllers useMessengerNavigationController:(BOOL)messenger;
- (void)presentIntercomWithViewControllers:(NSArray *)controllers;
@end
