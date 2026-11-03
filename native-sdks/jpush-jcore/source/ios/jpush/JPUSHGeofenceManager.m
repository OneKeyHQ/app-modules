#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JPUSHCache.h"
#import "JPUSHClientController.h"
#import "JPUSHGeofenceHelper.h"
#import "JPUSHGeofenceManager.h"
#import "JPUSHGeofenceNetwork.h"
#import "JPUSHPropertyModels.h"
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
extern NSString *const kJCORENetworkCmdCtrlNotification;
long long DefaultControl[4] = {1800, 10800, 900, 20};
long long MinControl[4] = {600, 3600, 60, 1};
long long MaxControl[4] = {86400, 86400, 86400, 5000};
static NSString *const MinIntervalKey = @"JPUSHGeoMinIntervalKey";
static NSString *const NextIntervalKey = @"JPUSHGeoNextIntervalKey";
static NSString *const MinLBSIntervalKey = @"JPUSHGeoMinLBSIntervalKey";
static NSString *const MinLBSKiloKey = @"JPUSHGeoMinLBSKiloKey";
static NSString *const LastGetKey = @"JPUSHLastGetGeofenceKey";
#define GEO_LOG(asyncValue, flagValue, lineValue, formatValue, ...)            \
  [JCORELog log:asyncValue                                                     \
          level:0x3ff                                                          \
           flag:flagValue                                                      \
        context:0                                                              \
           file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"         \
                "ios-jpush/JPush/JPush/Controller/JPUSHGeofenceManager.m"      \
       function:__PRETTY_FUNCTION__                                            \
           line:lineValue                                                      \
            tag:nil                                                            \
         format:formatValue, ##__VA_ARGS__]
static JPUSHGeofenceManager *SharedGeofence;
@implementation JPUSHGeofenceManager
@dynamic hash, superclass, description, debugDescription;
@synthesize locationManager = _locationManager,
            displayRegionArray = _displayRegionArray,
            callbackRegionArray = _callbackRegionArray, isSetup = _isSetup,
            lastLocation = _lastLocation;
@synthesize locationUpdateTimer = _locationUpdateTimer,
            nextRequestTimer = _nextRequestTimer,
            locationCompareTimer = _locationCompareTimer,
            genfenceInsideTimer = _genfenceInsideTimer;
@synthesize LBSToRequest = _LBSToRequest,
            LBSToUpdateGeofence = _LBSToUpdateGeofence,
            LBSToCompareWithLastLocation = _LBSToCompareWithLastLocation,
            LBSToCheckInsideGeofence = _LBSToCheckInsideGeofence;
@synthesize lastRequestTime = _lastRequestTime,
            pausesLocationUpdatesAutomatically =
                _pausesLocationUpdatesAutomatically,
            allowsBackgroundLocationUpdates = _allowsBackgroundLocationUpdates,
            delegate = _delegate, monitorCount = _monitorCount,
            locationEnable = _locationEnable,
            genfenceInsideTimerPeriod = _genfenceInsideTimerPeriod;
+ (BOOL)existGeofence {
  return [JPUSHGeofenceManager sharedGeoFence].locationEnable;
}
+ (instancetype)sharedGeoFence {
  static dispatch_once_t once;
  dispatch_once(&once, ^{
    SharedGeofence = [[JPUSHGeofenceManager alloc] init];
  });
  return SharedGeofence;
}
+ (id)allocWithZone:(NSZone *)zone {
  static dispatch_once_t once;
  dispatch_once(&once, ^{
    if (!SharedGeofence)
      SharedGeofence = [super allocWithZone:zone];
  });
  return SharedGeofence;
}
- (instancetype)init {
  if ((self = [super init])) {
    id defaults = [NSUserDefaults.standardUserDefaults
        objectForKey:@"GeofenceDefaultControl"];
    id min = [NSUserDefaults.standardUserDefaults
        objectForKey:@"GeofenceMinControl"];
    id max = [NSUserDefaults.standardUserDefaults
        objectForKey:@"GeofenceMaxControl"];
    if (defaults)
      for (NSUInteger i = 0; i < 4; i++)
        DefaultControl[i] = [defaults[i] integerValue];
    if (min)
      for (NSUInteger i = 0; i < 4; i++)
        MinControl[i] = [min[i] integerValue];
    if (max)
      for (NSUInteger i = 0; i < 4; i++)
        MaxControl[i] = [max[i] integerValue];
    self.locationManager = [CLLocationManager new];
    self.locationManager.delegate = self;
    [self oldVersionCompatible];
    self.displayRegionArray = [JPUSHGeofenceHelper getDisplayGeofenceCache];
    self.callbackRegionArray = [JPUSHGeofenceHelper getCallbackGeofenceCache];
    self.monitorCount = 10;
    self.locationEnable = YES;
    self.LBSToRequest = NO;
    self.LBSToUpdateGeofence = NO;
    self.LBSToCheckInsideGeofence = NO;
    [NSNotificationCenter.defaultCenter
        addObserver:self
           selector:@selector(handleCmdControWithThreadHandle:)
               name:kJCORENetworkCmdCtrlNotification
             object:nil];
  }
  return self;
}
- (void)setLocationEnable:(BOOL)value {
  _locationEnable = value;
  if (!value)
    [self removeAllRegion];
}
- (void)setup {
  if (self.callbackRegionArray.count) {
    if ([self allowToRequestGeofence])
      [self getGeofenceList];
    else
      [self prepareNextRequset];
  }
  [self checkInsideGeofence];
}
- (void)setMonitorCount:(long long)value {
  if (value < 1)
    GEO_LOG(NO, 1, 0xae, @"geofence count can't be equal or less than 0");
  else if (value > 20)
    GEO_LOG(NO, 1, 0xb0, @"geofence count no more than %@", @20);
  else
    _monitorCount = value;
}
- (BOOL)authorized {
  if ([CLLocationManager authorizationStatus] == 2 ||
      ![CLLocationManager locationServicesEnabled] || !_locationEnable) {
    GEO_LOG(NO, 0x20, 0xb9, @"lbs is not authorized or closed by user");
    return NO;
  }
  return YES;
}
- (void)addRegion:(JPUSHRegion *)region {
  if (!region || !region.circularRegion) {
    GEO_LOG(NO, 1, 0xc0, @"add region fail,check region config ");
    return;
  }
  NSMutableArray *regions = self.displayRegionArray;
  NSUInteger limit = 20;
  if (region.type == 2) {
    regions = self.callbackRegionArray;
    limit = 200;
  }
  if (![self authorized]) {
    GEO_LOG(NO, 1, 0x101, @"location services is unable");
    return;
  }
  if ([self checkAndDeleteExpirationRegion:region]) {
    GEO_LOG(YES, 0x100, 0xcd, @"delete expiration region");
    return;
  }
  if (NSDate.date.timeIntervalSince1970 > region.expirationTime) {
    GEO_LOG(NO, 1, 0xd1, @"error: expired geofence");
    return;
  }
  __block BOOL exists = NO;
  [regions
      enumerateObjectsUsingBlock:^(id object, NSUInteger index, BOOL *stop) {
        JPUSHRegion *item = object;
        if ([item.identifier isEqualToString:region.identifier]) {
          *stop = YES;
          exists = YES;
        }
      }];
  JPUSHRegion *updated =
      exists ? [self updateSameIdentifierRegion:region] : region;
  if (regions.count >= limit && !exists) {
    GEO_LOG(YES, 0x100, 0xe2,
            @"geofence count more than %@ ,delet the first geofence object",
            @(self.monitorCount));
    [self removeRegion:regions[0]];
  }
  CLCircularRegion *circle = updated.circularRegion;
  if (circle.radius > self.locationManager.maximumRegionMonitoringDistance ||
      circle.radius < 0) {
    GEO_LOG(NO, 1, 0xe8, @"geofence montior radius is  unavailable");
    return;
  }
  [regions addObject:updated];
  [self getLBSToUpdateiOSGeofence];
  if (regions == self.callbackRegionArray)
    [JPUSHGeofenceHelper saveCallbackGeofence:self.callbackRegionArray];
  else
    [JPUSHGeofenceHelper saveDisplayGeofence:self.displayRegionArray];
  if (region.jpushFenceRegionStatus == 3) {
    [self invalidInsideTimer];
    [self startInsideTimer];
  }
  if (exists)
    GEO_LOG(YES, 0x100, 0xfb, @"update a geofence :%@", region.identifier);
  else
    GEO_LOG(YES, 0x100, 0xfd, @"add region success");
}
- (void)getLBSToUpdateiOSGeofence {
  if (self.lastLocation &&
      self.lastLocation.timestamp.timeIntervalSinceNow >= -60)
    [self updateiOSGeofence];
  else {
    self.LBSToUpdateGeofence = YES;
    [self startLBS];
  }
}
- (void)updateiOSGeofence {
  GEO_LOG(YES, 0x80, 0x112, @"update ios geofence ");
  if (!self.lastLocation)
    return;
  double latitude = self.lastLocation.coordinate.latitude,
         longitude = self.lastLocation.coordinate.longitude;
  NSMutableArray *regions =
      [[NSMutableArray alloc] initWithArray:self.callbackRegionArray];
  for (id region in self.displayRegionArray)
    [regions addObject:region];
  if ((NSUInteger)self.monitorCount < regions.count) {
    [regions sortUsingComparator:^NSComparisonResult(JPUSHRegion *a,
                                                     JPUSHRegion *b) {
      double alat = latitude - a.circularRegion.center.latitude,
             alon = longitude - a.circularRegion.center.longitude;
      double blat = latitude - b.circularRegion.center.latitude,
             blon = longitude - b.circularRegion.center.longitude;
      long long adistance = (long long)(alat * alat + alon * alon),
                bdistance = (long long)(blat * blat + blon * blon);
      return adistance < bdistance ? -1 : 1;
    }];
  }
  NSInteger count = regions.count < (NSUInteger)self.monitorCount
                        ? (NSInteger)regions.count
                        : self.monitorCount;
  NSSet *monitored = self.locationManager.monitoredRegions;
  NSMutableArray *current = [NSMutableArray new];
  for (CLRegion *region in monitored)
    [self.locationManager stopMonitoringForRegion:region];
  for (NSInteger i = 0; i < count; i++) {
    [self.locationManager startMonitoringForRegion:[regions[i] circularRegion]];
    [current addObject:[regions[i] circularRegion]];
  }
  GEO_LOG(YES, 8, 0x133, @"current monitor geofence list %@", current);
}
- (void)checkInsideGeofence {
  if ([self authorized] && self.locationManager.monitoredRegions.count) {
    if (self.lastLocation &&
        self.lastLocation.timestamp.timeIntervalSinceNow >= -60) {
      [self caculateInsideGeofence];
      return;
    }
    [self startLBS];
    self.LBSToCheckInsideGeofence = YES;
  }
}
- (void)caculateInsideGeofence {
  BOOL exists = NO;
  for (CLCircularRegion *circle in self.locationManager.monitoredRegions) {
    JPUSHRegion *region =
        [JPUSHGeofenceHelper findJPushRegion:circle
                                    displayR:self.displayRegionArray
                                   callbackR:self.callbackRegionArray];
    if (region && region.jpushFenceRegionStatus == 3) {
      NSError *error = [JPUSHGeofenceHelper geofenceLocationError:region];
      if (![JPUSHGeofenceHelper detectRepeatTrigger:region])
        GEO_LOG(YES, 8, 0x152,
                @"Restricted by repeated settings, trigger cancel");
      else if ([circle containsCoordinate:self.lastLocation.coordinate]) {
        GEO_LOG(YES, 0x100, 0x156, @"gefence inside , genfenceid:%@",
                region.identifier);
        [self triggerWithRegion:region error:error];
      } else
        GEO_LOG(YES, 0x100, 0x159, @"gefence outside, genfenceid:%@",
                region.identifier);
      exists = YES;
    }
  }
  [self invalidInsideTimer];
  if (exists)
    [self startInsideTimer];
  else
    GEO_LOG(YES, 0x100, 0x160, @"inside geofence is not exists");
}
- (void)insideTimerTrigger {
  GEO_LOG(YES, 0x80, 0x165, @"Inside genfence timer trigger");
  [self checkInsideGeofence];
}
- (void)startInsideTimer {
  long long period = self.genfenceInsideTimerPeriod ?: 900;
  GEO_LOG(YES, 0x80, 0x16f, @"Inside genfence timer start, period:%ld",
          (long)period);
  self.genfenceInsideTimer =
      [NSTimer scheduledTimerWithTimeInterval:period
                                       target:self
                                     selector:@selector(insideTimerTrigger)
                                     userInfo:nil
                                      repeats:NO];
}
- (void)invalidInsideTimer {
  GEO_LOG(YES, 0x80, 0x174, @"Inside genfence timer invaild");
  if (self.genfenceInsideTimer && self.genfenceInsideTimer.valid)
    [self.genfenceInsideTimer invalidate];
  self.genfenceInsideTimer = nil;
}
- (BOOL)allowToRequestGeofence {
  if (!self.lastRequestTime)
    self.lastRequestTime = [JPUSHCache objectForKey:LastGetKey];
  if (!self.lastRequestTime)
    return YES;
  id interval =
      [JPUSHCache objectForKey:MinIntervalKey] ?: @(DefaultControl[0]);
  NSDate *date = [NSDate
      dateWithTimeIntervalSince1970:self.lastRequestTime.unsignedIntValue];
  GEO_LOG(YES, 0x100, 0x187, @"last request time: %@, interval: %@",
          [date descriptionWithLocale:NSLocale.systemLocale], interval);
  unsigned int threshold =
      self.lastRequestTime.unsignedIntValue + [interval unsignedIntValue] - 2;
  double now = NSDate.date.timeIntervalSince1970;
  if (now < (double)threshold) {
    id next = [JPUSHCache objectForKey:NextIntervalKey] ?: @(DefaultControl[1]);
    unsigned int nextTime =
        self.lastRequestTime.unsignedIntValue + [next unsignedIntValue];
    NSDate *nextDate = [NSDate dateWithTimeIntervalSince1970:nextTime];
    GEO_LOG(
        YES, 2, 0x18e,
        @"it doesn't allow to request geofence, time is not up, next allow: %@",
        [nextDate descriptionWithLocale:NSLocale.systemLocale]);
  }
  return (double)threshold <= now;
}
- (void)startLBS {
  GEO_LOG(YES, 0x100, 0x197, @"start lbs");
  [self.locationManager startUpdatingLocation];
  [self invalidLocationTimer];
  self.locationUpdateTimer =
      [NSTimer scheduledTimerWithTimeInterval:10
                                       target:self
                                     selector:@selector(stopLBS)
                                     userInfo:nil
                                      repeats:NO];
}
- (void)stopLBS {
  GEO_LOG(YES, 0x100, 0x1a0, @"stop lbs");
  [self.locationManager stopUpdatingLocation];
  [self invalidLocationTimer];
}
- (void)invalidLocationTimer {
  if (self.locationUpdateTimer && self.locationUpdateTimer.valid)
    [self.locationUpdateTimer invalidate];
  self.locationUpdateTimer = nil;
}
- (void)getGeofenceList {
  GEO_LOG(YES, 0x100, 0x1ae, @"try to get geofence");
  if ([self authorized] && [self allowToRequestGeofence]) {
    self.LBSToRequest = YES;
    [self startLBS];
  }
}
- (void)getCompareLocation {
  GEO_LOG(YES, 0x80, 0x1b9,
          @"location compare timer triggered, try to get location");
  if ([self authorized]) {
    self.LBSToCompareWithLastLocation = YES;
    [self startLBS];
  }
}
- (void)compareLocation:(CLLocation *)old newLocation:(CLLocation *)location {
  double distance = [old distanceFromLocation:location];
  id threshold =
      [JPUSHCache objectForKey:MinLBSKiloKey] ?: @(DefaultControl[3]);
  if ([threshold integerValue] < 1) {
    GEO_LOG(YES, 0x80, 0x1d2,
            @"next location changed request is closed because of distance "
            @"value is negative");
    return;
  }
  if (distance > (double)((long long)[threshold integerValue] * 1000)) {
    GEO_LOG(YES, 0x80, 0x1ca,
            @"distance changed %f, exceed %ld kilo, get geofence",
            distance / 1000.0, (long)[threshold integerValue]);
    [self requestGeofenceInfo:YES];
  } else {
    GEO_LOG(YES, 0x80, 0x1ce, @"distance changed %f, too short",
            distance / 1000.0);
    [self prepareCompareLocationRequset];
  }
}
- (void)requestGeofenceInfo:(BOOL)force {
  GEO_LOG(YES, 0x100, 0x1d7, @"send geofence info request");
  @synchronized(self) {
    [self stopLBS];
    if (self.lastLocation && ([self allowToRequestGeofence] || force)) {
      JPushGetGeofenceRequest *request = [JPushGetGeofenceRequest new];
      request.lat = self.lastLocation.coordinate.latitude;
      request.lng = self.lastLocation.coordinate.longitude;
      [request send];
      self.lastRequestTime = @(NSDate.date.timeIntervalSince1970);
      [JPUSHCache setObject:self.lastRequestTime forKey:LastGetKey];
    } else
      GEO_LOG(YES, 0x40, 0x1e5, @"lbs data is expired, give up request");
  }
}
- (void)prepareNextRequset {
  id interval =
      [JPUSHCache objectForKey:NextIntervalKey] ?: @(DefaultControl[1]);
  GEO_LOG(YES, 0x100, 0x1f0, @"current next interval %@", interval);
  if (self.lastRequestTime) {
    unsigned int next =
        self.lastRequestTime.unsignedIntValue + [interval unsignedIntValue];
    interval = @((double)next - NSDate.date.timeIntervalSince1970 + 1.0);
  }
  if ([interval integerValue] < 1) {
    GEO_LOG(YES, 0x100, 0x1fc, @"next timing timer is closed");
    return;
  }
  if (self.nextRequestTimer && self.nextRequestTimer.valid)
    [self.nextRequestTimer invalidate];
  GEO_LOG(YES, 0x100, 0x1f9, @"start next timing timer, interval: %@",
          interval);
  self.nextRequestTimer =
      [NSTimer scheduledTimerWithTimeInterval:[interval integerValue]
                                       target:self
                                     selector:@selector(getGeofenceList)
                                     userInfo:nil
                                      repeats:NO];
}
- (void)prepareCompareLocationRequset {
  id interval =
      [JPUSHCache objectForKey:MinLBSIntervalKey] ?: @(DefaultControl[2]);
  if ([interval integerValue] < 1) {
    GEO_LOG(YES, 0x100, 0x20d, @"next location changed timer is closed");
    return;
  }
  if (self.locationCompareTimer && self.locationCompareTimer.valid)
    [self.locationCompareTimer invalidate];
  GEO_LOG(YES, 0x100, 0x20a, @"start location compare timer, interval: %@",
          interval);
  self.locationCompareTimer =
      [NSTimer scheduledTimerWithTimeInterval:[interval integerValue]
                                       target:self
                                     selector:@selector(getCompareLocation)
                                     userInfo:nil
                                      repeats:NO];
}
- (void)cacheValidControl:(id)value index:(long long)index key:(id)key {
  if ([value integerValue] < MinControl[index])
    value = @(MinControl[index]);
  if ([value integerValue] > MaxControl[index])
    value = @(MaxControl[index]);
  [JPUSHCache setObject:value forKey:key];
}
- (void)handleGetGeofenceResult:(id)value {
  if (!value)
    return;
  if (value[@"minInterval"])
    [self cacheValidControl:value[@"minInterval"] index:0 key:MinIntervalKey];
  if (value[@"nextInterval"])
    [self cacheValidControl:value[@"nextInterval"] index:1 key:NextIntervalKey];
  if (value[@"minLBSInterval"])
    [self cacheValidControl:value[@"minLBSInterval"]
                      index:2
                        key:MinLBSIntervalKey];
  if (value[@"minLBSKilo"])
    [self cacheValidControl:value[@"minLBSKilo"] index:3 key:MinLBSKiloKey];
  NSArray *list = @[];
  if (value[@"geofence"] && [value[@"geofence"] isKindOfClass:NSArray.class])
    list = value[@"geofence"];
  NSMutableArray *regions = [NSMutableArray new];
  for (NSInteger i = 0, count = MIN((NSInteger)list.count, 200); i < count;
       i++) {
    JPUSHRegion *region = [JPUSHGeofenceHelper jsonToRegion:list[i]];
    if (region.type == 2)
      [regions addObject:region];
  }
  self.callbackRegionArray = regions;
  [JPUSHGeofenceHelper saveCallbackGeofence:self.callbackRegionArray];
  [self startMonitor];
  [self updateiOSGeofence];
  if (self.callbackRegionArray.count) {
    [self prepareNextRequset];
    [self prepareCompareLocationRequset];
  }
  [self checkInsideGeofence];
  if (self.delegate && [self.delegate respondsToSelector:@selector
                                      (jpushCallbackGeofenceReceived:)])
    [self.delegate jpushCallbackGeofenceReceived:list];
}
- (void)handleSilentPushGeofenceMessage:(id)value {
  if ([[value allKeys] containsObject:@"_j_geofence"]) {
    GEO_LOG(YES, 0x100, 0x251, @" receive silent geofence info:%@", value);
    [self handlePushGeofenceMessage:value];
  }
}
- (void)handleCustomPushMessage:(id)value {
  GEO_LOG(YES, 0x100, 0x256, @" receive custom geofence info:%@", value);
  id geo = [value objectForKey:kJPGeofenceKeyword];
  NSMutableDictionary *content =
      [[NSMutableDictionary alloc] initWithDictionary:value];
  [content removeObjectForKey:kJPGeofenceKeyword];
  [content addEntriesFromDictionary:@{@"_j_geofence" : geo}];
  [self handlePushGeofenceMessage:content];
}
- (void)handlePushGeofenceMessage:(id)value {
  [self startMonitor];
  [self addRegion:[JPUSHGeofenceHelper creatRegionWith:value]];
}
- (BOOL)checkAndDeleteExpirationRegion:(JPUSHRegion *)region {
  NSMutableArray *regions =
      region.type == 2 ? self.callbackRegionArray : self.displayRegionArray;
  __block id found = nil;
  __block BOOL expired = NO;
  [regions
      enumerateObjectsUsingBlock:^(id object, NSUInteger index, BOOL *stop) {
        JPUSHRegion *item = object;
        if ([item.identifier isEqualToString:region.identifier]) {
          if (region.expirationTime < NSDate.date.timeIntervalSince1970) {
            found = item;
            expired = YES;
          }
          *stop = YES;
        }
      }];
  if (found)
    [self removeRegion:found];
  return expired;
}
- (id)updateSameIdentifierRegion:(JPUSHRegion *)region {
  NSMutableArray *regions =
      region.type == 2 ? self.callbackRegionArray : self.displayRegionArray;
  __block id found = nil;
  [regions
      enumerateObjectsUsingBlock:^(id object, NSUInteger index, BOOL *stop) {
        JPUSHRegion *item = object;
        if ([item.identifier isEqualToString:region.identifier]) {
          found = item;
          *stop = YES;
        }
      }];
  if (found)
    [self removeRegion:found];
  return region;
}
- (void)startMonitor {
  if (self.isSetup)
    return;
  if (@available(iOS 9.0, *)) {
    if ([[NSBundle.mainBundle.infoDictionary objectForKey:@"UIBackgroundModes"]
            containsObject:@"location"])
      self.locationManager.allowsBackgroundLocationUpdates = YES;
    else
      GEO_LOG(YES, 2, 0x298,
              @" you'd better selet location update for geofence in "
              @"UIBackgroundModes");
  }
  self.locationManager.pausesLocationUpdatesAutomatically = NO;
  self.isSetup = YES;
}
- (void)removeGeofenceWithIdentifier:(id)identifier {
  if ([self removeRegionWithIdentifer:identifier])
    GEO_LOG(YES, 8, 0x2a4, @"remove geofence success, id:%@", identifier);
  else
    GEO_LOG(YES, 2, 0x2a7, @"remove geofence failed, id:%@", identifier);
}
- (void)removeRegion:(id)region {
  [self removeRegionWithIdentifer:[region identifier]];
}
- (BOOL)removeRegionWithIdentifer:(id)identifier {
  if (!identifier || ![identifier length])
    return NO;
  __block JPUSHRegion *found = nil;
  [self.displayRegionArray
      enumerateObjectsUsingBlock:^(id object, NSUInteger index, BOOL *stop) {
        JPUSHRegion *item = object;
        if ([identifier isEqualToString:item.identifier]) {
          if (item.circularRegion)
            [self.locationManager stopMonitoringForRegion:item.circularRegion];
          found = item;
          *stop = YES;
        }
      }];
  NSMutableArray *regions = nil;
  if (found)
    regions = self.displayRegionArray;
  else {
    [self.callbackRegionArray enumerateObjectsUsingBlock:^(
                                  id object, NSUInteger index, BOOL *stop) {
      JPUSHRegion *item = object;
      if ([identifier isEqualToString:item.identifier]) {
        if (item.circularRegion)
          [self.locationManager stopMonitoringForRegion:item.circularRegion];
        found = item;
        *stop = YES;
      }
    }];
    if (found)
      regions = self.callbackRegionArray;
  }
  if (!found) {
    GEO_LOG(YES, 0x40, 0x2dc, @"remove region failed, id:%@", identifier);
    return NO;
  }
  if (found.repeat && found.expirationTime < NSDate.date.timeIntervalSince1970)
    [JPUSHGeofenceHelper removeTriggerTimeWith:found.identifier];
  [regions removeObject:found];
  if (regions == self.callbackRegionArray)
    [JPUSHGeofenceHelper saveCallbackGeofence:self.callbackRegionArray];
  else
    [JPUSHGeofenceHelper saveDisplayGeofence:self.displayRegionArray];
  return YES;
}
- (void)removeAllRegion {
  GEO_LOG(YES, 0x100, 0x2e3, @"remoe all region");
  if (self.callbackRegionArray.count)
    [self.callbackRegionArray enumerateObjectsUsingBlock:^(
                                  id object, NSUInteger index, BOOL *stop) {
      JPUSHRegion *region = object;
      if (region.circularRegion)
        [self.locationManager stopMonitoringForRegion:region.circularRegion];
    }];
  if (self.displayRegionArray.count)
    [self.displayRegionArray enumerateObjectsUsingBlock:^(
                                 id object, NSUInteger index, BOOL *stop) {
      JPUSHRegion *region = object;
      if (region.circularRegion)
        [self.locationManager stopMonitoringForRegion:region.circularRegion];
    }];
  self.callbackRegionArray = [NSMutableArray array];
  self.displayRegionArray = [NSMutableArray array];
  [JPUSHGeofenceHelper saveDisplayGeofence:self.displayRegionArray];
  [JPUSHGeofenceHelper saveCallbackGeofence:self.callbackRegionArray];
}
- (void)oldVersionCompatible {
  id path = [JPUSHGeofenceHelper geofenceCachePathWith:@"JpushGeofenceData"];
  NSArray *old = [JPUSHGeofenceHelper getGeofenceCacheFromLocalPath:path];
  if (old && old.count) {
    self.displayRegionArray = [NSMutableArray new];
    self.callbackRegionArray = [NSMutableArray new];
    for (JPUSHRegion *region in old)
      [(region.type == 2 ? self.callbackRegionArray : self.displayRegionArray)
          addObject:region];
    [NSFileManager.defaultManager removeItemAtPath:path error:NULL];
    if (self.displayRegionArray.count)
      [JPUSHGeofenceHelper saveDisplayGeofence:self.displayRegionArray];
    if (self.callbackRegionArray.count)
      [JPUSHGeofenceHelper saveCallbackGeofence:self.callbackRegionArray];
  }
}
- (void)triggerWithRegion:(JPUSHRegion *)region error:(NSError *)error {
  if (error && error.code == 7000) {
    [self removeRegion:region];
    return;
  }
  if (region.type == 2) {
    JPUSHGeofenceReport *report = [JPUSHGeofenceReport new];
    report.geofenceId = region.identifier;
    report.triggerTime = (long long)[JCOREUtilities currentTimestamp];
    if (self.lastLocation) {
      report.latitude = [NSString
          stringWithFormat:@"%f", self.lastLocation.coordinate.latitude];
      report.longitude = [NSString
          stringWithFormat:@"%f", self.lastLocation.coordinate.longitude];
    }
    [report send];
  } else
    [JPUSHGeofenceHelper localNotification:region];
  if (!error && !region.repeat)
    [self removeRegion:region];
  if (self.delegate &&
      [self.delegate respondsToSelector:@selector(jpushGeofenceRegion:error:)])
    [self.delegate jpushGeofenceRegion:[JPUSHGeofenceHelper regionToJson:region]
                                 error:error];
  [JPUSHGeofenceHelper recordTriggerTimeWith:region.identifier];
}
- (void)locationManager:(CLLocationManager *)manager
     didUpdateLocations:(NSArray *)locations {
  [self stopLBS];
  if ([locations isKindOfClass:NSArray.class] && locations.count) {
    CLLocation *location = locations.lastObject;
    if (location && location.horizontalAccuracy >= 0) {
      GEO_LOG(YES, 0x80, 0x33d, @"get new location %@", location);
      CLLocation *old = self.lastLocation;
      self.lastLocation = location;
      if (self.LBSToRequest)
        [self requestGeofenceInfo:NO];
      if (self.LBSToUpdateGeofence)
        [self updateiOSGeofence];
      if (self.LBSToCompareWithLastLocation)
        [self compareLocation:old newLocation:location];
      if (self.LBSToCheckInsideGeofence)
        [self caculateInsideGeofence];
      self.LBSToRequest = NO;
      self.LBSToUpdateGeofence = NO;
      self.LBSToCompareWithLastLocation = NO;
      self.LBSToCheckInsideGeofence = NO;
    }
  }
}
// This delegate hook has an empty body in the original archive.
- (void)locationManager:(CLLocationManager *)manager
      didDetermineState:(CLRegionState)state
              forRegion:(CLRegion *)region {
}
- (void)locationManager:(CLLocationManager *)manager
         didEnterRegion:(CLRegion *)circle {
  GEO_LOG(YES, 0x100, 0x375, @"enter region");
  NSError *error = nil;
  JPUSHRegion *region =
      [JPUSHGeofenceHelper getValidJPushRegion:circle
                                         error:&error
                                      displayR:self.displayRegionArray
                                     callbackR:self.callbackRegionArray];
  if (region) {
    id info = region.messageDict ?: region.apnsDict;
    if (self.delegate && [self.delegate respondsToSelector:@selector
                                        (jpushGeofenceIdentifer:
                                                 didEnterRegion:error:)])
      [self.delegate jpushGeofenceIdentifer:region.identifier
                             didEnterRegion:info
                                      error:error];
    [self triggerWithRegion:region error:error];
  }
}
- (void)locationManager:(CLLocationManager *)manager
          didExitRegion:(CLRegion *)circle {
  GEO_LOG(YES, 0x100, 0x387, @"exit region");
  NSError *error = nil;
  JPUSHRegion *region =
      [JPUSHGeofenceHelper getValidJPushRegion:circle
                                         error:&error
                                      displayR:self.displayRegionArray
                                     callbackR:self.callbackRegionArray];
  if (region) {
    id info = region.messageDict ?: region.apnsDict;
    if (self.delegate && [self.delegate respondsToSelector:@selector
                                        (jpushGeofenceIdentifer:
                                                  didExitRegion:error:)])
      [self.delegate jpushGeofenceIdentifer:region.identifier
                              didExitRegion:info
                                      error:error];
    [self triggerWithRegion:region error:error];
  }
}
- (void)locationManager:(CLLocationManager *)manager
    didChangeAuthorizationStatus:(CLAuthorizationStatus)status {
  if (status == 2)
    GEO_LOG(NO, 1, 0x399, @"Not authorized the use of location service");
}
- (void)locationManager:(CLLocationManager *)manager
    monitoringDidFailForRegion:(CLRegion *)region
                     withError:(NSError *)error {
  if (error)
    GEO_LOG(NO, 1, 0x3a0,
            @"monitoringDidFailForRegion:%@，localizedDescription:%@", error,
            error.localizedDescription);
}
- (void)handleCmdControWithThreadHandle:(id)notification {
  [self performSelectorOnMainThread:@selector(handleCmdControl:)
                         withObject:notification
                      waitUntilDone:YES];
}
- (void)handleCmdControl:(id)notification {
  if (!notification || ![notification userInfo])
    return;
  id info = [notification userInfo];
  id command = [info isKindOfClass:NSDictionary.class]
                   ? [info objectForKey:@"cmd_content"]
                   : nil;
  if (![command isKindOfClass:NSDictionary.class]) {
    GEO_LOG(YES, 0x100, 0x3b3, @"handle cmd control cancel, cmd content nil");
    return;
  }
  id content = command[@"content"];
  if (![content isKindOfClass:NSDictionary.class]) {
    GEO_LOG(YES, 0x40, 0x3b8, @"handle cmd control cancel, content error");
    return;
  }
  if (![content count]) {
    GEO_LOG(YES, 0x100, 0x3bd, @"handle cmd control cancel, content nil");
    return;
  }
  id code = command[@"cmd"];
  if (!code) {
    GEO_LOG(YES, 0x100, 0x3c2, @"handle cmd control cancel, cmd nil");
    return;
  }
  if ([code charValue] != 56)
    return;
  id op = content[@"op"];
  if (![op isKindOfClass:NSString.class] || ![op length]) {
    GEO_LOG(YES, 0x40, 0x3cb, @"handle cmd control cancel, op error");
    return;
  }
  id identifier = content[@"geofenceid"];
  if (![identifier isKindOfClass:NSString.class] || ![identifier length]) {
    GEO_LOG(YES, 0x40, 0x3d0, @"handle cmd control cancel, geofence id error");
    return;
  }
  if ([op isEqualToString:@"del"])
    [self handleDelOperation:identifier];
  else if ([op isEqualToString:@"add"])
    [self handleAddOperation:content];
  else if ([op isEqualToString:@"upd"])
    [self handleUpdOperation:content];
}
- (void)handleDelOperation:(id)value {
  [self removeGeofenceWithIdentifier:value];
}
- (void)handleAddOperation:(id)value {
  [self handlePushGeofenceMessage:@{@"_j_geofence" : value}];
}
- (void)handleUpdOperation:(id)value {
  JPUSHRegion *region =
      [JPUSHGeofenceHelper creatRegionWith:@{@"_j_geofence" : value}];
  __block JPUSHRegion *old = nil;
  [self.displayRegionArray
      enumerateObjectsUsingBlock:^(id object, NSUInteger index, BOOL *stop) {
        JPUSHRegion *item = object;
        if ([item.identifier isEqualToString:region.identifier]) {
          old = item;
          *stop = YES;
        }
      }];
  if (!old)
    [self.callbackRegionArray
        enumerateObjectsUsingBlock:^(id object, NSUInteger index, BOOL *stop) {
          JPUSHRegion *item = object;
          if ([item.identifier isEqualToString:region.identifier]) {
            old = item;
            *stop = YES;
          }
        }];
  if (!old) {
    GEO_LOG(YES, 0x100, 0x402, @"geofenceid not exist, update geofence cancel");
    return;
  }
  if (!value[@"repeat"])
    region.repeat = old.repeat;
  if (!value[@"type"])
    region.type = old.type;
  region = [self updateRegion:region withRegion:old];
  if (region) {
    [self startMonitor];
    [self addRegion:region];
  }
}
- (id)updateRegion:(JPUSHRegion *)region withRegion:(id)value {
  if (![value isKindOfClass:JPUSHRegion.class])
    return region;
  JPUSHRegion *old = value;
  if (region.type != old.type) {
    GEO_LOG(YES, 2, 0x41c, @"update geofence type different, update cancel");
    return nil;
  }
  if (region.expirationTime < 1)
    region.expirationTime = old.expirationTime;
  if (NSDate.date.timeIntervalSince1970 > region.expirationTime) {
    GEO_LOG(YES, 2, 0x423, @"update geofence has expired, update cancel");
    return nil;
  }
  if (!region.circularRegion)
    region.circularRegion = old.circularRegion;
  else if (region.circularRegion.radius >
               self.locationManager.maximumRegionMonitoringDistance ||
           region.circularRegion.radius < 0) {
    GEO_LOG(
        YES, 2, 0x42c,
        @"update geofence montior radius is unavailable, will  use old region");
    region.circularRegion = old.circularRegion;
  }
  if (!region.weekRepeatNum)
    region.weekRepeatNum = old.weekRepeatNum;
  if (!region.dayRepeatNum)
    region.dayRepeatNum = old.dayRepeatNum;
  if (region.repeatTime == 0)
    region.repeatTime = old.repeatTime;
  if (![region.messageDict isKindOfClass:NSDictionary.class])
    region.messageDict = old.messageDict;
  if (![region.apnsDict isKindOfClass:NSDictionary.class])
    region.apnsDict = old.apnsDict;
  if (!region.jpushFenceRegionStatus)
    region.jpushFenceRegionStatus = old.jpushFenceRegionStatus;
  return region;
}
- (void)setGenfenceInsideTimerPeriod:(long long)value {
  if (value < 181)
    value = 180;
  if (value > 86399)
    value = 86400;
  _genfenceInsideTimerPeriod = value;
}
@end
#pragma clang diagnostic pop
#endif
