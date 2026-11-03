#pragma once
#import <Foundation/Foundation.h>
@interface JPUSHPasteController : NSObject
- (void)start;
- (void)handleCmdControl:(NSNotification *)notification;
- (void)handleBoardContent:(id)value;
- (void)setBoardContent:(id)value;
@end
