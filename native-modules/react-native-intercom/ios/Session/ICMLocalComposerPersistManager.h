#import <Foundation/Foundation.h>

@protocol ICMComposerPersistManager <NSObject>
- (void)persistString:(NSString *)string conversationId:(NSString *)conversationId;
- (BOOL)doesPersistedStringExistWithConversationId:(NSString *)conversationId;
- (NSString *)persistedStringWithConversationId:(NSString *)conversationId;
- (void)clearPersistedDataWithConversationId:(NSString *)conversationId;
- (void)clearPersistedData;
@end

@interface ICMLocalComposerPersistManager : NSObject <ICMComposerPersistManager>
@end
