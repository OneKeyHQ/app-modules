#pragma once
#import "ICMActionButton.h"
#import "ICMScreenAction.h"
@interface ICMScreenActionButton : ICMActionButton
@property (nonatomic, strong) ICMScreenAction *screenAction;
@end
