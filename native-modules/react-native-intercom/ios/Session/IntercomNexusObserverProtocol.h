#import <Foundation/Foundation.h>
@class IntercomNexusEvent;
@protocol IntercomNexusObserverProtocol <NSObject>
- (void)eventOccurred:(IntercomNexusEvent *)event;
@optional
- (NSString *)observedConversationId;
@end
