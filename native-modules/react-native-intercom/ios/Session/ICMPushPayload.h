#import <Foundation/Foundation.h>
@interface ICMPushPayload : NSObject
@property(nonatomic) NSInteger type;
@property(nonatomic, copy) NSString *instanceId;
@property(nonatomic, copy) NSString *pushOnlyConversationId;
@property(nonatomic, copy) NSString *conversationId;
@property(nonatomic, copy) NSURL *contentImageURL;
@property(nonatomic, copy) NSString *authorName;
@property(nonatomic, copy) NSString *uri;
@property(nonatomic, copy) NSDictionary *content;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
+ (NSInteger)determineNotificationType:(NSString *)type;
- (NSInteger)pushOnlyIdType;
+ (NSString *)stringFromValue:(id)value;
@end
