#import "../Intercom.h"
#import "../Intercom+Wrapper.h"
#import "ICMBridge+SecureOptions.h"
#import "ICMBridge+Registration.h"
#import "ICMBridge+IdentityLifecycle.h"
#import "ICMBridge+Content.h"
#import "ICMBridge+Application.h"
#import "ICMBridge+Push.h"
#import "../Models/ICMCustomDataValidation.h"

NSString *const IntercomDidStartNewConversationNotification = @"IntercomDidStartNewConversationNotification";
NSString *const IntercomUnreadConversationCountDidChangeNotification = @"IntercomUnreadConversationCountDidChangeNotification";
NSString *const IntercomUnreadTicketCountDidChangeNotification = @"IntercomUnreadTicketCountDidChangeNotification";
NSString *const IntercomWindowDidHideNotification = @"IntercomWindowDidHideNotification";
NSString *const IntercomWindowDidShowNotification = @"IntercomWindowDidShowNotification";
NSString *const IntercomWindowWillHideNotification = @"IntercomWindowWillHideNotification";
NSString *const IntercomWindowWillShowNotification = @"IntercomWindowWillShowNotification";

@interface Intercom ()
+ (BOOL)validateParameterIsStringAndNotEmpty:(id)value;
+ (BOOL)validateParameterIsDictionaryAndNotEmpty:(id)value;
+ (BOOL)validateApiKey:(id)value;
+ (BOOL)validateAuthTokens:(id)value;
+ (NSDictionary *)dictionaryByRemovingInvalidEntries:(NSDictionary *)dictionary;
@end
@implementation Intercom
+ (void)presentIntercom {
    if ([NSThread isMainThread]) {
        [ICMBridge presentHome];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge presentHome]; });
    }
}
+ (void)presentMessageComposer:(NSString *)message {
    if ([NSThread isMainThread]) {
        [ICMBridge presentMessageComposer:message];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge presentMessageComposer:message]; });
    }
}
+ (void)fetchHelpCenterCollectionsWithCompletion:(void (^)(NSArray<ICMHelpCenterCollection *> *, NSError *))completion {
    if ([NSThread isMainThread]) {
        [ICMBridge fetchHelpCenterCollectionsWithCompletion:completion];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge fetchHelpCenterCollectionsWithCompletion:completion]; });
    }
}
+ (void)fetchHelpCenterCollection:(NSString *)identifier withCompletion:(void (^)(ICMHelpCenterCollectionContent *, NSError *))completion {
    if ([NSThread isMainThread]) {
        [ICMBridge fetchHelpCenterCollection:identifier withCompletion:completion];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge fetchHelpCenterCollection:identifier withCompletion:completion]; });
    }
}
+ (void)searchHelpCenter:(NSString *)query withCompletion:(void (^)(NSArray<ICMHelpCenterArticleSearchResult *> *, NSError *))completion {
    if ([NSThread isMainThread]) {
        [ICMBridge searchHelpCenter:query withCompletion:completion];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge searchHelpCenter:query withCompletion:completion]; });
    }
}
+ (void)setInAppMessagesVisible:(BOOL)visible {
    if ([NSThread isMainThread]) {
        [ICMBridge setInAppMessagesVisible:visible];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge setInAppMessagesVisible:visible]; });
    }
}
+ (void)setLauncherVisible:(BOOL)visible {
    if ([NSThread isMainThread]) {
        [ICMBridge setLauncherVisible:visible];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge setLauncherVisible:visible]; });
    }
}
+ (void)hideIntercom {
    if ([NSThread isMainThread]) {
        [ICMBridge hideIntercom];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge hideIntercom]; });
    }
}
+ (void)setBottomPadding:(CGFloat)padding {
    if ([NSThread isMainThread]) {
        [ICMBridge setInAppBottomPadding:padding];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge setInAppBottomPadding:padding]; });
    }
}
+ (void)loginUnidentifiedUserWithSuccess:(void (^)(void))success failure:(void (^)(NSError *))failure {
    if ([NSThread isMainThread]) {
        [ICMBridge registerUnidentifiedUserWithSuccess:success failure:failure];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge registerUnidentifiedUserWithSuccess:success failure:failure]; });
    }
}
+ (void)logout {
    if ([NSThread isMainThread]) {
        [ICMBridge logout];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge logout]; });
    }
}
+ (void)setNeedsStatusBarAppearanceUpdate {
    if ([NSThread isMainThread]) {
        [ICMBridge setNeedsStatusBarAppearanceUpdate];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge setNeedsStatusBarAppearanceUpdate]; });
    }
}
+ (void)setThemeOverride:(ICMThemeOverride)mode {
    if ([NSThread isMainThread]) {
        [ICMBridge setThemeOverride:mode];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge setThemeOverride:mode]; });
    }
}
+ (NSUInteger)unreadConversationCount { return [ICMBridge unreadConversationCount]; }
+ (NSUInteger)unreadTicketCount { return [ICMBridge unreadTicketCount]; }
+ (BOOL)isUserLoggedIn { return [ICMBridge isUserLoggedIn]; }
+ (ICMUserAttributes *)fetchLoggedInUserAttributes { return [ICMBridge fetchLoggedInUserAttributes]; }
+ (void)removeUserJwtProvider { [ICMBridge removeUserJwtProvider]; }
+ (void)enableLogging { [ICMBridge enableLogging]; }
+ (BOOL)isIntercomPushNotification:(NSDictionary *)userInfo { return [ICMBridge isIntercomPushNotification:userInfo]; }
+ (void)setDeviceToken:(NSData *)token failure:(void (^)(NSError *))failure { [ICMBridge registerDeviceToken:token failure:failure]; }
+ (void)setDeviceToken:(NSData *)token success:(void (^)(void))success failure:(void (^)(NSError *))failure { [ICMBridge registerDeviceToken:token success:success failure:failure]; }
+ (void)setCordovaVersion:(NSString *)version { [ICMBridge setWrapperVersion:version forWrapperType:0]; }
+ (void)setReactNativeVersion:(NSString *)version { [ICMBridge setWrapperVersion:version forWrapperType:1]; }
+ (void)setFlutterVersion:(NSString *)version { [ICMBridge setWrapperVersion:version forWrapperType:2]; }
+ (void)setUnityVersion:(NSString *)version { [ICMBridge setWrapperVersion:version forWrapperType:3]; }
+ (void)setApiKey:(NSString *)apiKey forAppId:(NSString *)appId {
    if (![self validateApiKey:apiKey]) {
        NSLog(@"[Intercom] ERROR - 'apiKey' parameter must be a valid Intercom iOS SDK API key.");
        return;
    }
    if (![self validateParameterIsStringAndNotEmpty:appId]) {
        NSLog(@"[Intercom] ERROR - 'appId' parameter must be an NSString and can't be nil or empty");
        return;
    }
    if ([NSThread isMainThread]) [ICMBridge setApiKey:apiKey appId:appId];
    else dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge setApiKey:apiKey appId:appId]; });
}
+ (void)setUserHash:(NSString *)userHash {
    if (![self validateParameterIsStringAndNotEmpty:userHash]) {
        NSLog(@"[Intercom] ERROR - 'userHash' must be a non-empty string.");
        return;
    }
    void (^operation)(void) = ^{ [ICMBridge setSecureOptions:@{@"hmac": userHash}]; };
    if ([NSThread isMainThread]) operation();
    else dispatch_async(dispatch_get_main_queue(), operation);
}
+ (void)setUserJwt:(NSString *)jwt {
    if (![self validateParameterIsStringAndNotEmpty:jwt]) {
        NSLog(@"[Intercom] ERROR - 'jwt' must be a non-empty string.");
        return;
    }
    void (^operation)(void) = ^{ [ICMBridge setSecureOptions:@{@"intercom_user_jwt": jwt}]; };
    if ([NSThread isMainThread]) operation();
    else dispatch_async(dispatch_get_main_queue(), operation);
}
+ (void)setUserJwtProvider:(void (^)(void (^)(NSString *)))provider {
    if (provider != nil) [ICMBridge setUserJwtProvider:provider];
    else NSLog(@"[Intercom] ERROR - 'provider' must not be nil. Call removeUserJwtProvider to remove it.");
}
+ (void)setAuthTokens:(NSDictionary<NSString *, NSString *> *)tokens success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    if ([self validateAuthTokens:tokens]) {
        [ICMBridge setAuthTokens:tokens success:success failure:failure];
    } else {
        NSLog(@"[Intercom] ERROR - 'authTokens' must contain at least one valid key/value pair.");
        void (^operation)(void) = ^{ if (failure) failure([ICMBridge generateInvalidAuthTokenError]); };
        if ([NSThread isMainThread]) operation();
        else dispatch_async(dispatch_get_main_queue(), operation);
    }
}
+ (void)presentIntercom:(Space)space {
    void (^operation)(void) = ^{
        switch (space) {
            case home: [ICMBridge presentHome]; break;
            case helpCenter: [ICMBridge presentHelpCenter]; break;
            case messages: [ICMBridge presentMessages]; break;
            case tickets: [ICMBridge presentTickets]; break;
            default: break;
        }
    };
    if ([NSThread isMainThread]) operation();
    else dispatch_async(dispatch_get_main_queue(), operation);
}
+ (void)presentContent:(IntercomContent *)content {
    void (^operation)(void) = ^{
        switch (content.type) {
            case article:
                if ([ICMCustomDataValidation validateNonEmptyString:content.contentId forKey:@"contentId"]) [ICMBridge presentArticle:content.contentId];
                break;
            case survey:
                if ([ICMCustomDataValidation validateNonEmptyString:content.contentId forKey:@"contentId"]) [ICMBridge presentSurvey:content.contentId];
                break;
            case carousel:
                if ([ICMCustomDataValidation validateNonEmptyString:content.contentId forKey:@"contentId"]) [ICMBridge presentCarousel:content.contentId];
                break;
            case helpCenterCollections:
                if ([ICMCustomDataValidation validateArrayOfStrings:content.contentId forKey:@"contentId"]) [ICMBridge presentHelpCenterCollections:content.contentId];
                break;
            case conversation:
                if ([ICMCustomDataValidation validateNonEmptyString:content.contentId forKey:@"contentId"]) [ICMBridge presentConversation:content.contentId];
                break;
            case ticket:
                if ([ICMCustomDataValidation validateNonEmptyString:content.contentId forKey:@"contentId"]) [ICMBridge presentTicket:content.contentId];
                break;
            default: break;
        }
    };
    if ([NSThread isMainThread]) operation();
    else dispatch_async(dispatch_get_main_queue(), operation);
}
+ (void)suppressProactiveContent:(NSArray<NSNumber *> *)types {
    void (^operation)(void) = ^{
        BOOL suppressCarousels = NO, suppressSurveys = NO;
        for (NSNumber *type in types) {
            NSInteger value = [type integerValue];
            if (value == 0) suppressCarousels = YES;
            else if (value == 1) suppressSurveys = YES;
        }
        [ICMBridge setProactiveVisibleWithCarousels:!suppressCarousels surveys:!suppressSurveys];
    };
    if ([NSThread isMainThread]) operation();
    else dispatch_async(dispatch_get_main_queue(), operation);
}
+ (void)loginUserWithUserAttributes:(ICMUserAttributes *)userAttributes success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    NSDictionary *attributes = [userAttributes attributes];
    BOOL valid;
    if ([self validateParameterIsStringAndNotEmpty:[userAttributes userId]]) {
        valid = [self validateParameterIsDictionaryAndNotEmpty:attributes];
    } else {
        valid = [self validateParameterIsStringAndNotEmpty:[userAttributes email]] && [self validateParameterIsDictionaryAndNotEmpty:attributes];
    }
    if (valid) {
        if ([NSThread isMainThread]) [ICMBridge registerIdentifiedUserWithProperties:userAttributes success:success failure:failure];
        else dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge registerIdentifiedUserWithProperties:userAttributes success:success failure:failure]; });
    } else {
        NSLog(@"[Intercom] ERROR - either 'userId' or 'email' property of 'userAttributes' must be populated as an NSString");
        void (^operation)(void) = ^{ if (failure) failure([ICMBridge generateInvalidUserIdOrEmailError]); };
        if ([NSThread isMainThread]) operation();
        else dispatch_async(dispatch_get_main_queue(), operation);
    }
}
+ (void)updateUser:(ICMUserAttributes *)userAttributes success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    if (![userAttributes isKindOfClass:[ICMUserAttributes class]]) {
        NSLog(@"[Intercom] ERROR - the supplied 'userAttributes' is not an ICMUserAttributes instance");
        void (^operation)(void) = ^{ if (failure) failure([ICMBridge generateInvalidUpdateObjectError]); };
        if ([NSThread isMainThread]) operation();
        else dispatch_async(dispatch_get_main_queue(), operation);
        return;
    }
    NSDictionary *attributes = [userAttributes attributes];
    if (![self validateParameterIsDictionaryAndNotEmpty:attributes]) {
        NSLog(@"[Intercom] ERROR - the supplied ICMUserAttributes object is empty");
        void (^operation)(void) = ^{ if (failure) failure([ICMBridge generateInvalidUpdateObjectError]); };
        if ([NSThread isMainThread]) operation();
        else dispatch_async(dispatch_get_main_queue(), operation);
        return;
    }
    if ([NSThread isMainThread]) [ICMBridge updateUserWithAttributes:attributes success:success failure:failure];
    else dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge updateUserWithAttributes:attributes success:success failure:failure]; });
}
+ (void)logEventWithName:(NSString *)name {
    if (![self validateParameterIsStringAndNotEmpty:name]) {
        NSLog(@"[Intercom] ERROR - 'name' parameter must be an NSString and can't be nil or empty");
        return;
    }
    if ([NSThread isMainThread]) [ICMBridge logEventWithName:name metaData:nil];
    else dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge logEventWithName:name metaData:nil]; });
}
+ (void)logEventWithName:(NSString *)name metaData:(NSDictionary *)metadata {
    if (![self validateParameterIsStringAndNotEmpty:name]) {
        NSLog(@"[Intercom] ERROR - 'name' parameter must be an NSString and can't be nil or empty");
        return;
    }
    if ([self validateParameterIsDictionaryAndNotEmpty:metadata] && [NSJSONSerialization isValidJSONObject:metadata]) {
        if ([NSThread isMainThread]) [ICMBridge logEventWithName:name metaData:metadata];
        else dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge logEventWithName:name metaData:metadata]; });
    } else {
        NSLog(@"[Intercom] INFO - 'metaData' parameter was either nil, empty or not an NSDictionary. The event '%@' will be logged without the metaData.", name);
        if ([NSThread isMainThread]) [ICMBridge logEventWithName:name metaData:nil];
        else dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge logEventWithName:name metaData:nil]; });
    }
}
+ (void)handleIntercomPushNotification:(NSDictionary *)userInfo {
    if (![ICMBridge isIntercomPushNotification:userInfo]) {
        NSLog(@"[Intercom] ERROR - +[Intercom handleIntercomPushNotification:] was called with a non-Intercom push message");
        return;
    }
    if ([NSThread isMainThread]) [ICMBridge handleIntercomPushNotification:userInfo];
    else dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge handleIntercomPushNotification:userInfo]; });
}
+ (void)handleIntercomRichPushNotificationContent:(UNNotificationContent *)content withContentHandler:(void (^)(UNNotificationContent *))handler {
    if (content == nil) {
        NSLog(@"[Intercom] ERROR - +[Intercom handleIntercomRichPushNotificationContent:] was called with empty notificationContent");
        return;
    }
    if (![ICMBridge isIntercomPushNotification:content.userInfo]) {
        NSLog(@"[Intercom] ERROR - +[Intercom handleIntercomRichPushNotificationContent:] was called with a non-Intercom rich push message");
        handler(content);
        return;
    }
    if ([NSThread isMainThread]) [ICMBridge handleIntercomRichPushNotificationContent:(UNMutableNotificationContent *)content withContentHandler:handler];
    else dispatch_async(dispatch_get_main_queue(), ^{ [ICMBridge handleIntercomRichPushNotificationContent:(UNMutableNotificationContent *)content withContentHandler:handler]; });
}
+ (BOOL)validateParameterIsStringAndNotEmpty:(id)value {
    return [value isKindOfClass:[NSString class]] && [[value stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] length] != 0;
}
+ (BOOL)validateParameterIsDictionaryAndNotEmpty:(id)value {
    return [value isKindOfClass:[NSDictionary class]] && [value count] != 0;
}
+ (BOOL)validateApiKey:(id)value {
    return [value isKindOfClass:[NSString class]] && [value rangeOfString:@"ios_sdk-"].location == 0 && [value length] > 47;
}
+ (BOOL)validateAuthTokens:(id)value {
    return [value isKindOfClass:[NSDictionary class]] && [[self dictionaryByRemovingInvalidEntries:value] count] != 0;
}
+ (NSDictionary *)dictionaryByRemovingInvalidEntries:(NSDictionary *)dictionary {
    NSMutableDictionary *filtered = [NSMutableDictionary dictionary];
    [dictionary enumerateKeysAndObjectsUsingBlock:^(id key, id value, BOOL *stop) {
        (void)stop;
        BOOL validKey = [self validateParameterIsStringAndNotEmpty:key];
        BOOL validValue = [self validateParameterIsStringAndNotEmpty:value];
        if (validKey && validValue) filtered[key] = value;
    }];
    return [filtered copy];
}
@end
