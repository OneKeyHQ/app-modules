#pragma once
#import <CoreLocation/CoreLocation.h>
#import <Foundation/Foundation.h>
@class JCommonReportTimerManager;
typedef NS_ENUM(NSUInteger, JCoreCollectLocation) {
    kJCommonCollectLocationUnknown = 0,
    kJCommonCollectLocationStart = 1,
    kJCommonCollectLocationSuccess = 2,
    kJCommonCollectLocationError = 3
};
@interface JCORECollectLocationManager : NSObject <CLLocationManagerDelegate> {
    JCoreCollectLocation _collectLocationFlag;
    CLAuthorizationStatus _locationAuthorizationStatus;
    NSTimer *_locationUpdateTimer;
    CLLocation *_latestLocation;
    JCommonReportTimerManager *_collectTimerManager;
    NSTimeInterval _lastCollectLocationTimestamp;
    BOOL _alreadyCollect;
    NSMutableArray *_locationInfoArray;
    NSTimeInterval _collectLocationFrequency;
    CLLocationManager *_locationManager;
    void (^_collectLBSLoactionHandle)(BOOL);
}
@property(strong, nonatomic) CLLocationManager *locationManager;
@property(copy, nonatomic) void (^collectLBSLoactionHandle)(BOOL);
@property(nonatomic) NSTimeInterval collectLocationFrequency;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
- (instancetype)initWith:(NSTimeInterval)frequency;
- (CLLocation *)latestLocation;
- (BOOL)authorized;
- (void)setupLocationManager;
- (void)setupLocationManagerWith:(id)info;
- (id)locationReportArray;
- (void)clearReportLoacationData;
- (void)setLBSCollectFrequency:(NSTimeInterval)frequency;
- (void)startCollectLocationTimer;
- (void)resetCollectTimer;
- (void)stopTimer;
- (void)handleCollectLocationBeforeSendReport:(void (^)(BOOL))completion;
- (void)collectLocation;
- (void)saveLatestLocation;
- (void)addLocationInfo:(id)info;
- (id)wifiInfo;
- (id)cellInfo;
- (void)handleChangeAuthorizationStatus:(id)status;
- (void)handleUpdateLocations:(id)locations;
@end
