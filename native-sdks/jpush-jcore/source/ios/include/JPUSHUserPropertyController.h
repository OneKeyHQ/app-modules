#pragma once
#import <Foundation/Foundation.h>
@interface JPUSHUserPropertyController : NSObject {
    double _uploadPropertyTime;
    unsigned char _uploadPropertyTimes;
    NSMutableDictionary *_ReqItemQueue;
}
- (id)findRequest:(unsigned long long)seq;
- (void)handleUserPropertyResponse:(id)response;
- (void)setUserProperty:(NSDictionary *)property;
- (BOOL)isUserProperyUpload:(NSDictionary *)property;
- (void)saveUserProperty:(NSDictionary *)property;
- (void)sendUserProperty:(NSDictionary *)property completion:(void (^)(NSError *error))completion;
@end
