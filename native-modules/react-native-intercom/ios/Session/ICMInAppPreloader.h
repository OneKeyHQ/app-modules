#pragma once
#import <Foundation/Foundation.h>
@class ICMConversation, ICMConversationPart;
@interface ICMInAppPreloader : NSObject
+ (void)prepareConversationForInApp:(ICMConversation *)conversation completion:(void (^)(BOOL))completion;
+ (void)prepareInAppPart:(ICMConversationPart *)part completion:(void (^)(BOOL))completion;
@end
