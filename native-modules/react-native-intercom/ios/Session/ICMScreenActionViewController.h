#pragma once
#import <UIKit/UIKit.h>
#import "ICMScreenActionView.h"
@interface ICMScreenActionViewController : UIViewController
@property (nonatomic, strong) NSArray<ICMScreenAction *> *screenActions;
@property (nonatomic, strong) ICMScreenActionView *screenActionView;
@property (nonatomic, weak) id<ICMScreenDelegate> screenDelegate;
- (instancetype)initWithScreenActions:(NSArray<ICMScreenAction *> *)actions screenDelegate:(id<ICMScreenDelegate>)delegate;
- (void)setupScreenActionView;
- (BOOL)isPermissionAction;
- (void)calculatePreferredSize;
@end
