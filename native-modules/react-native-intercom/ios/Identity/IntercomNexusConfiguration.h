// Internal interface recovered from Intercom 19.9.1 metadata.
#import <Foundation/Foundation.h>
@interface IntercomNexusConfiguration : NSObject
@property (nonatomic, strong) NSArray<NSURL *> *endpoints;
@property (nonatomic, strong) NSNumber *connectionTimeout;
@property (nonatomic, strong) NSNumber *connectionHeartbeatInterval;
@property (nonatomic, strong) NSNumber *backgroundConnectionTimeout;
+ (instancetype)instanceFromDictionary:(id)dictionary;
- (void)setAttributesFromDictionary:(id)dictionary;
@end
