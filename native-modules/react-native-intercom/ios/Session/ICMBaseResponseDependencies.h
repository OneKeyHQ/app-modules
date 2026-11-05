// Genuine unread responses plus declarations of remaining original models.
#import <Foundation/Foundation.h>
#import "ICMUnreadConversationsResponse.h"
#import "ICMUnreadTicketsResponse.h"
#import "ICMCarousel.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate6Survey")))
__attribute__((swift_name("Survey")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredSurvey : NSObject
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@property (nonatomic, readonly) NSString *id;
@property (nonatomic, readonly) NSInteger progressId;
@property (nonatomic, readonly) NSString *lastStepId;
- (NSArray<NSDictionary *> *)allResponses;
@end
