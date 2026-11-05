#import <UIKit/UIKit.h>
@interface ICMTeammatePresenceView : UIView {
    BOOL _active;
    UIColor *_borderColor;
}
@property (nonatomic, getter=isActive) BOOL active;
@property (nonatomic, copy) UIColor *borderColor;
- (instancetype)initWithDiameter:(CGFloat)diameter border:(CGFloat)border;
@end
