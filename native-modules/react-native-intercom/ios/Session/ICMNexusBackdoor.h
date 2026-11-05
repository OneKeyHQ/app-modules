#import <Foundation/Foundation.h>
@interface ICMNexusBackdoor : NSObject
+ (void)triggerNexusEvent:(NSString *)conversationID;
@end
