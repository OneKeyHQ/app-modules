#import "ICMLocalComposerPersistManager.h"

@implementation ICMLocalComposerPersistManager
- (void)persistString:(NSString *)string conversationId:(NSString *)conversationId {
    NSDictionary *data = @{ @"conversationId": conversationId, @"string": string };
    [[NSUserDefaults standardUserDefaults] setObject:data forKey:@"intercom-sdk-composer-persisted-string"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}
- (BOOL)doesPersistedStringExistWithConversationId:(NSString *)conversationId {
    id data = [[NSUserDefaults standardUserDefaults] objectForKey:@"intercom-sdk-composer-persisted-string"];
    if ([data[@"conversationId"] isKindOfClass:[NSString class]]) {
        return [data[@"conversationId"] isEqualToString:conversationId];
    }
    return NO;
}
- (NSString *)persistedStringWithConversationId:(NSString *)conversationId {
    id data = [[NSUserDefaults standardUserDefaults] objectForKey:@"intercom-sdk-composer-persisted-string"];
    if ([data[@"conversationId"] isKindOfClass:[NSString class]] &&
        [data[@"conversationId"] isEqualToString:conversationId]) {
        return data[@"string"];
    }
    return nil;
}
- (void)clearPersistedDataWithConversationId:(NSString *)conversationId {
    if ([self doesPersistedStringExistWithConversationId:conversationId]) [self clearPersistedData];
}
- (void)clearPersistedData {
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"intercom-sdk-composer-persisted-string"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}
@end
