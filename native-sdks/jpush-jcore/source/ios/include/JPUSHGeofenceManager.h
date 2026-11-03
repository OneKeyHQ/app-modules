#pragma once
#import "JPUSHService.h"
#import <CoreLocation/CoreLocation.h>
@class JPUSHRegion;
extern long long DefaultControl[4], MinControl[4], MaxControl[4];
@interface JPUSHGeofenceManager : NSObject <CLLocationManagerDelegate> {
  BOOL _pausesLocationUpdatesAutomatically, _allowsBackgroundLocationUpdates,
      _locationEnable, _isSetup;
  BOOL _LBSToRequest, _LBSToUpdateGeofence, _LBSToCompareWithLastLocation,
      _LBSToCheckInsideGeofence;
  __weak id<JPUSHGeofenceDelegate> _delegate;
  long long _monitorCount, _genfenceInsideTimerPeriod;
  CLLocationManager *_locationManager;
  NSMutableArray *_displayRegionArray, *_callbackRegionArray;
  CLLocation *_lastLocation;
  NSTimer *_locationUpdateTimer, *_nextRequestTimer, *_locationCompareTimer,
      *_genfenceInsideTimer;
  NSNumber *_lastRequestTime;
}
@property(nonatomic, strong) CLLocationManager *locationManager;
@property(nonatomic, strong) NSMutableArray *displayRegionArray;
@property(nonatomic, strong) NSMutableArray *callbackRegionArray;
@property(nonatomic) BOOL isSetup;
@property(nonatomic, strong) CLLocation *lastLocation;
@property(nonatomic, strong) NSTimer *locationUpdateTimer;
@property(nonatomic, strong) NSTimer *nextRequestTimer;
@property(nonatomic, strong) NSTimer *locationCompareTimer;
@property(nonatomic, strong) NSTimer *genfenceInsideTimer;
@property(nonatomic) BOOL LBSToRequest, LBSToUpdateGeofence,
    LBSToCompareWithLastLocation, LBSToCheckInsideGeofence;
@property(nonatomic, strong) NSNumber *lastRequestTime;
@property(nonatomic) BOOL pausesLocationUpdatesAutomatically,
    allowsBackgroundLocationUpdates;
@property(nonatomic, weak) id<JPUSHGeofenceDelegate> delegate;
@property(nonatomic) long long monitorCount;
@property(nonatomic) BOOL locationEnable;
@property(nonatomic) long long genfenceInsideTimerPeriod;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description, *debugDescription;
+ (BOOL)existGeofence;
+ (instancetype)sharedGeoFence;
+ (id)allocWithZone:(NSZone *)zone;
- (void)setup;
- (BOOL)authorized;
- (void)addRegion:(JPUSHRegion *)region;
- (void)getLBSToUpdateiOSGeofence;
- (void)updateiOSGeofence;
- (void)checkInsideGeofence;
- (void)caculateInsideGeofence;
- (void)insideTimerTrigger;
- (void)startInsideTimer;
- (void)invalidInsideTimer;
- (BOOL)allowToRequestGeofence;
- (void)startLBS;
- (void)stopLBS;
- (void)invalidLocationTimer;
- (void)getGeofenceList;
- (void)getCompareLocation;
- (void)compareLocation:(CLLocation *)old newLocation:(CLLocation *)location;
- (void)requestGeofenceInfo:(BOOL)force;
- (void)prepareNextRequset;
- (void)prepareCompareLocationRequset;
- (void)cacheValidControl:(id)value index:(long long)index key:(id)key;
- (void)handleGetGeofenceResult:(id)value;
- (void)handleSilentPushGeofenceMessage:(id)value;
- (void)handleCustomPushMessage:(id)value;
- (void)handlePushGeofenceMessage:(id)value;
- (BOOL)checkAndDeleteExpirationRegion:(JPUSHRegion *)region;
- (id)updateSameIdentifierRegion:(JPUSHRegion *)region;
- (void)startMonitor;
- (void)removeGeofenceWithIdentifier:(id)identifier;
- (void)removeRegion:(id)region;
- (BOOL)removeRegionWithIdentifer:(id)identifier;
- (void)removeAllRegion;
- (void)oldVersionCompatible;
- (void)triggerWithRegion:(JPUSHRegion *)region error:(NSError *)error;
- (void)handleCmdControWithThreadHandle:(id)notification;
- (void)handleCmdControl:(id)notification;
- (void)handleDelOperation:(id)value;
- (void)handleAddOperation:(id)value;
- (void)handleUpdOperation:(id)value;
- (id)updateRegion:(JPUSHRegion *)region withRegion:(id)old;
@end
