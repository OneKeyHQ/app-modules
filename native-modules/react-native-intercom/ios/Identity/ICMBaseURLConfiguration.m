#import "ICMBaseURLConfiguration.h"
#import "ICMLogger.h"
@implementation ICMBaseURLConfiguration
+ (NSURL *)baseUrlForAppId:(NSString *)appId {
    NSString *prefix = @"";
    if (appId.length) {
        NSMutableCharacterSet *allowed = [NSMutableCharacterSet alphanumericCharacterSet];
        [allowed addCharactersInString:@"-"];
        NSString *clean = [[appId componentsSeparatedByCharactersInSet:allowed.invertedSet] componentsJoinedByString:@""];
        prefix = [@"" stringByAppendingString:clean];
    }
    if (prefix.length) prefix = [prefix stringByAppendingString:@"-"];
    NSString *subdomain = [prefix stringByAppendingString:@"ios"];
    if (subdomain.length >= 64) {
        subdomain = [[subdomain substringWithRange:NSMakeRange(0, 63)] stringByTrimmingCharactersInSet:[NSCharacterSet characterSetWithCharactersInString:@"-"]];
    }
    NSString *host = [self customLinkForBaseURL];
    if (!host || [host isEqualToString:@""]) host = [self getRegion];
    return [NSURL URLWithString:[NSString stringWithFormat:@"https://%@.%@/messenger/mobile/", subdomain, host]];
}
+ (NSString *)customLinkForBaseURL {
    id value = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"IntercomCustomURL"];
    if (![value isKindOfClass:[NSString class]] || ![value length]) return nil;
    NSCharacterSet *allowed = [NSCharacterSet characterSetWithCharactersInString:@"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-."];
    if ([value rangeOfCharacterFromSet:allowed.invertedSet].location != NSNotFound) {
        [ICMLogger log:@"[Intercom] IntercomCustomURL contains invalid characters. This value should be a bare domain like 'api.example.com'. Falling back to default Intercom endpoint." type:OS_LOG_TYPE_ERROR];
        return nil;
    }
    if ([value hasPrefix:@"."] || [value hasPrefix:@"-"] || [value hasSuffix:@"."] || [value hasSuffix:@"-"] || [value containsString:@".."]) {
        [ICMLogger log:@"[Intercom] IntercomCustomURL is not a valid hostname. This value should be a bare domain like 'api.example.com'. Falling back to default Intercom endpoint." type:OS_LOG_TYPE_ERROR];
        return nil;
    }
    return value;
}
+ (NSString *)getRegion {
    NSString *region = [[[NSBundle mainBundle] objectForInfoDictionaryKey:@"IntercomRegion"] uppercaseString];
    if ([region isEqualToString:@"EU"]) return @"mobile-messenger.eu.intercom.io";
    if ([region isEqualToString:@"AU"]) return @"mobile-messenger.au.intercom.io";
    return @"mobile-messenger.intercom.com";
}
@end
