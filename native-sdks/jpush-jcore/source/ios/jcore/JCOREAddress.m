#import "JCOREAddress.h"
#import "JCOREUtilities.h"
#include <arpa/inet.h>

@implementation JCOREAddress
@synthesize host = _host, port = _port, ipType = _ipType;
@dynamic hash, superclass, description, debugDescription;
- (id)copyWithZone:(NSZone *)zone {
    JCOREAddress *copy = [[[self class] allocWithZone:zone] init];
    copy->_host = [_host copy]; copy->_port = _port; copy->_ipType = _ipType;
    return copy;
}
- (instancetype)initWithString:(NSString *)string {
    self = [super init];
    if (self) {
        if ([JCOREAddress isValidIpString:string]) {
            _host = [JCOREAddress host:string];
            _port = [[JCOREAddress port:string] intValue];
        } else if ([JCOREAddress isValidHost:string]) _host = string;
        else return self;
        _ipType = ![JCOREAddress isIpv4:_host];
    }
    return self;
}
+ (NSString *)mergeURLStringWithIp:(id)ip localURL:(id)localURL SSL:(BOOL)ssl {
    if (![ip isKindOfClass:NSString.class] || ![localURL isKindOfClass:NSString.class] || ![self isValidIpString:ip]) return nil;
    NSString *path = [NSURL URLWithString:localURL].relativePath;
    if (!path) return nil;
    JCOREAddress *address = [[JCOREAddress alloc] initWithString:ip];
    if (!address.host || !address.port) return nil;
    NSString *format = [self isIpv4:[self host:ip]] ? @"%@://%@:%d%@" : @"%@://[%@]:%d%@";
    return [NSString stringWithFormat:format, ssl ? @"https" : @"http", address.host, address.port, path];
}
+ (BOOL)isIpv4:(NSString *)string {
    struct in_addr address;
    return inet_pton(AF_INET, string.UTF8String, &address) > 0;
}
+ (BOOL)isValidIpString:(id)string {
    if (![string isKindOfClass:NSString.class]) return NO;
    NSRange separator = [string rangeOfString:@":" options:NSBackwardsSearch];
    if (!separator.length) return NO;
    return [self isValidHost:[string substringToIndex:separator.location]] && [self isValidPort:[string substringFromIndex:separator.location + 1]];
}
+ (BOOL)isValidHost:(id)host {
    return [host isKindOfClass:NSString.class] && [host length] && [JCOREUtilities checkIPAddress:host];
}
+ (BOOL)isValidPort:(id)port {
    if (![port isKindOfClass:NSString.class] || ![port length]) return NO;
    return (unsigned int)[port intValue] - 1U <= 0xfffd;
}
+ (NSString *)host:(id)string {
    if (![string isKindOfClass:NSString.class]) return nil;
    NSRange separator = [string rangeOfString:@":" options:NSBackwardsSearch];
    if (!separator.length) return nil;
    NSString *host = [string substringToIndex:separator.location];
    return [self isValidHost:host] ? host : nil;
}
+ (NSString *)port:(id)string {
    if (![string isKindOfClass:NSString.class]) return nil;
    NSRange separator = [string rangeOfString:@":" options:NSBackwardsSearch];
    if (!separator.length) return nil;
    NSString *port = [string substringFromIndex:separator.location + 1];
    return [self isValidPort:port] ? port : nil;
}
@end
