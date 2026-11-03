#import "JPUSHGeofenceNetwork.h"
#import "JCORELog.h"
@implementation JPUSHGeofenceReport
@synthesize geofenceId = _geofenceId, triggerTime = _triggerTime,
            latitude = _latitude, longitude = _longitude;
- (instancetype)init {
  if ((self = [super init])) {
    self.cacheRequired = NO;
    self.completionHandler = ^(id response, NSError *error) {
      [JCORELog log:YES
              level:0x3ff
               flag:0x100
            context:0
               file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"
                    "ios-jpush/JPush/JPush/Network/Report/Http/"
                    "JPUSHGeofenceReport.m"
           function:__PRETTY_FUNCTION__
               line:0x12
                tag:nil
             format:@"geofence report error: %@", error];
    };
  }
  return self;
}
- (id)requestInfo {
  return @{
    @"itime" : @(self.reportTimeStamp),
    @"type" : @"geo_fence",
    @"geofence_id" : self.geofenceId ?: @"",
    @"gps" : @{@"lat" : self.latitude ?: @"", @"lng" : self.longitude ?: @""}
  };
}
@end
