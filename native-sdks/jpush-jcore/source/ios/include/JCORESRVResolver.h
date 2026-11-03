#pragma once
#import <Foundation/Foundation.h>
@class JCORESRVResolver;
@protocol JCORESRVResolverDelegate <NSObject>
@optional
- (void)srvResolver:(JCORESRVResolver *)resolver didReceiveResult:(NSDictionary *)result;
- (void)srvResolver:(JCORESRVResolver *)resolver didStopWithError:(NSError *)error;
@end

FOUNDATION_EXPORT NSString *kJCORESRVResolverPriority;
FOUNDATION_EXPORT NSString *kJCORESRVResolverWeight;
FOUNDATION_EXPORT NSString *kJCORESRVResolverPort;
FOUNDATION_EXPORT NSString *kJCORESRVResolverTarget;
FOUNDATION_EXPORT NSString *kJCORESRVResolverErrorDomain;
@interface JCORESRVResolver : NSObject
@property (nonatomic, getter=isFinished) BOOL finished;
@property (copy, nonatomic) NSError *error;
@property (readonly, nonatomic) NSMutableArray *resultsMutable;
@property (strong, nonatomic) NSTimer *timer;
@property (nonatomic) double timeout;
@property (readonly, copy, nonatomic) NSString *srvName;
@property (weak, nonatomic) id<JCORESRVResolverDelegate> delegate;
@property (readonly, copy, nonatomic) NSArray *results;
- (instancetype)initWithSRVName:(NSString *)name;
- (instancetype)initWithSRVName:(NSString *)name timeout:(double)timeout;
- (void)start;
- (void)stop;
- (void)stopWithTimeout;
- (void)stopWithError:(NSError *)error;
- (void)stopWithDNSServiceError:(int)error;
- (void)processRecord:(const void *)record length:(NSUInteger)length;
- (void)startInternal;
@end
