#pragma once
#import <Foundation/Foundation.h>
@class JCORECollectLocationManager, JCommonReportTimerManager;
@interface JCORELocationController : NSObject {
    JCORECollectLocationManager *_collectionManager;
    JCommonReportTimerManager *_reportTimerManager;
    NSTimeInterval _lastLocationTimestamp;
    BOOL _needReport;
    BOOL _locationDisable;
    NSTimeInterval _collectLocationFrequency;
    NSTimeInterval _reportLocationFrequency;
}
@property(nonatomic) BOOL locationDisable;
@property(nonatomic) NSTimeInterval collectLocationFrequency;
@property(nonatomic) NSTimeInterval reportLocationFrequency;
+ (instancetype)sharedInstane;
- (void)setLocation:(id)location;
- (id)currentLocationInfo;
- (id)reportContentWithIpv6Address:(id)address;
- (void)handleLocationOffChange:(NSNotification *)notification;
- (void)handleContrlCommand:(id)command;
- (void)resetReportLocation;
- (void)resetCollectLocationFrequency:(NSTimeInterval)frequency;
- (void)startLocationStrategy;
- (void)stopLocationStrategy;
- (void)reportLocation;
- (void)sendReportRequest;
- (void)requestIpAddress:(void (^)(NSString *))completion;
- (void)startReportTimer;
- (void)stopTimer;
@end
