#pragma once
#import "ICMInAppAttributes.h"
@class ICMAvatarView, ICMParticipant;
@interface ICMInAppHeaderViewController : UIViewController
@property (nonatomic, strong) ICMAvatarView *avatarView;
@property (nonatomic, strong) UIButton *closeButton;
@property (nonatomic, weak) id<ICMInAppHeaderDelegate> delegate;
@property (nonatomic) CGFloat avatarDiameter;
@property (nonatomic, strong) UITextView *adminTextView;
@property (nonatomic, weak) NSLayoutConstraint *closeButtonRightConstraint;
@property (nonatomic, weak) NSLayoutConstraint *adminTextViewHeightConstraint;
- (instancetype)initWithAvatarDiameter:(CGFloat)diameter;
- (void)closePressed;
- (void)configureForParticipant:(ICMParticipant *)participant nameAttributes:(NSDictionary *)nameAttributes statusAttributes:(NSDictionary *)statusAttributes;
- (void)configureForParticipant:(ICMParticipant *)participant nameAttributes:(NSDictionary *)nameAttributes statusAttributes:(NSDictionary *)statusAttributes appName:(NSString *)appName;
- (void)setCloseButtonImage:(UIImage *)image tintColor:(UIColor *)color;
@end
