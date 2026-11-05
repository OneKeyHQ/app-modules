#pragma once
#import <UIKit/UIKit.h>
@interface ICMSeparatorView : UIView
@property (nonatomic, strong) NSLayoutConstraint *heightConstraint;
- (instancetype)initWithColor:(UIColor *)color;
- (void)pinToEdge:(NSUInteger)edge ofView:(UIView *)view;
- (NSUInteger)adjacentEdgesWithEdge:(NSUInteger)edge;
- (void)updateHeight;
@end
