#pragma once
#import <UIKit/UIKit.h>
#import "ICMScreenDelegate.h"
#import "ICMScreenActionButton.h"
@interface ICMScreenActionView : UIView
@property (nonatomic, strong) UIStackView *stackView;
@property (nonatomic, strong) NSArray<ICMScreenAction *> *screenActions;
@property (nonatomic, weak) id<ICMScreenDelegate> screenDelegate;
- (instancetype)initWithScreenActions:(NSArray<ICMScreenAction *> *)actions screenDelegate:(id<ICMScreenDelegate>)delegate;
- (void)configureView;
- (void)setupStackView;
- (void)setUpScreenActionButtons;
- (ICMScreenActionButton *)addButtonForScreenAction:(ICMScreenAction *)action;
- (NSString *)accessibilityIdentifierForScreenActionType:(NSUInteger)type;
- (void)carouselButtonTapped:(ICMScreenActionButton *)sender;
- (void)intercomWindowDidHide;
@end
