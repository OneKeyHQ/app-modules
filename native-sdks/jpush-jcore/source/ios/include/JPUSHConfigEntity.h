#pragma once
#import <Foundation/Foundation.h>

#import "RecoveredProtocols.h"

@interface JPUSHConfigEntity : NSObject <JCOREConfigProtocol> {
    BOOL isProduction;
    NSString *appKey;
    NSString *channel;
    NSString *advertisingId;
    void (^completionHandler)(NSError *error, id<JCOREConfigProtocol> config);
    long long index;
}
@property (nonatomic, copy) NSString *appKey;
@property (nonatomic, copy) NSString *channel;
@property (nonatomic, copy) NSString *advertisingId;
@property (nonatomic) BOOL isProduction;
@property (nonatomic, copy) void (^completionHandler)(NSError *error, id<JCOREConfigProtocol> config);
@property (nonatomic) long long index;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
@end
