#pragma once
#import "ICMMultipleAvatarsView.h"
@interface ICMActiveAdminsAvatarView : ICMMultipleAvatarsView
@property (nonatomic) BOOL updateWhenTeamPresenceUpdated;
- (instancetype)initWithHeight:(CGFloat)height overlap:(CGFloat)overlap maxCount:(NSInteger)count;
- (void)teamPresenceUpdated;
- (void)updateAvatars;
@end
