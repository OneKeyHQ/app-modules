#import "JPUSHPasteController.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import <UIKit/UIKit.h>
extern NSString *const kJCORENetworkCmdCtrlNotification;
#define PASTE_LOG(flagValue, lineValue, formatValue, ...)                      \
  [JCORELog log:YES                                                            \
          level:0x3ff                                                          \
           flag:flagValue                                                      \
        context:0                                                              \
           file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"         \
                "ios-jpush/JPush/JPush/Controller/JPUSHPasteController.m"      \
       function:__PRETTY_FUNCTION__                                            \
           line:lineValue                                                      \
            tag:nil                                                            \
         format:formatValue, ##__VA_ARGS__]
@implementation JPUSHPasteController
- (instancetype)init {
  if ((self = [super init]))
    [NSNotificationCenter.defaultCenter
        addObserver:self
           selector:@selector(handleCmdControl:)
               name:kJCORENetworkCmdCtrlNotification
             object:nil];
  return self;
}
- (void)dealloc {
  [NSNotificationCenter.defaultCenter removeObserver:self];
}
- (void)start {
  PASTE_LOG(0x100, 0x24, @"start paste control support");
}
- (void)handleCmdControl:(NSNotification *)notification {
  if (!notification || !notification.userInfo)
    return;
  id info = notification.userInfo;
  id command = [info isKindOfClass:NSDictionary.class]
                   ? [info objectForKey:@"cmd_content"]
                   : nil;
  if (![command isKindOfClass:NSDictionary.class]) {
    PASTE_LOG(0x100, 0x34, @"handle cmd control cancel, cmd content nil");
    return;
  }
  id content = command[@"content"];
  if (![content isKindOfClass:NSDictionary.class]) {
    PASTE_LOG(0x40, 0x39, @"handle cmd control cancel, content error");
    return;
  }
  if (![content count]) {
    PASTE_LOG(0x100, 0x3e, @"handle cmd control cancel, content nil");
    return;
  }
  id code = command[@"cmd"];
  if (!code) {
    PASTE_LOG(0x100, 0x43, @"handle cmd control cancel, cmd nil");
    return;
  }
  if ([code charValue] == 57)
    [self handleBoardContent:content];
}
- (void)handleBoardContent:(id)value {
  [self setBoardContent:value];
}
- (void)setBoardContent:(id)value {
  id board = value[@"board"];
  if ([board isKindOfClass:NSData.class])
    board = [JCOREUtilities dataToJSON:board];
  UIPasteboard *pasteboard = UIPasteboard.generalPasteboard;
  if ([board isKindOfClass:NSArray.class])
    pasteboard.items = board;
  else if ([board isKindOfClass:NSDictionary.class])
    pasteboard.items = @[ board ];
  PASTE_LOG(0x100, 0x7b, @"board content:%@", board);
}
@end
