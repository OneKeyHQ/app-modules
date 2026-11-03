#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JPushNotiInAppWeakScriptMessageDelegate.h"
@implementation JPushNotiInAppWeakScriptMessageDelegate
@synthesize scriptDelegate = _scriptDelegate;
@dynamic hash, superclass, description, debugDescription;
- (instancetype)initWithDelegate:(id<WKScriptMessageHandler>)delegate {
  if ((self = [super init]))
    _scriptDelegate = delegate;
  return self;
}
- (void)userContentController:(WKUserContentController *)controller
     didReceiveScriptMessage:(WKScriptMessage *)message {
  [self.scriptDelegate userContentController:controller didReceiveScriptMessage:message];
}
@end
#endif
