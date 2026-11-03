#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCOREDecoder.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JPUSHGeofenceNetwork.h"
#import "JPUSHGeofenceManager.h"
@implementation JPUSHGeofenceInfoMessage
@synthesize type = _type;
- (instancetype)initWithResponse:(id<JCORETcpRespondProtocol>)response {
  if ((self = [super initWithResponse:response])) {
    unsigned short offset = 0;
    _type = [JCOREDecoder uInt8FromBuffer:response.bodyData offset:&offset];
    if (!_type)
      [JCORELog log:YES
              level:0x3ff
               flag:0x100
            context:0
               file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"
                    "ios-jpush/JPush/JPush/Network/Request/JPUSHGeofenceInfo.m"
           function:__PRETTY_FUNCTION__
               line:0x3b
                tag:nil
             format:@"geofence info doesn't exist"];
  }
  return self;
}
- (void)handleReceipt {
  if (self.type)
    [[JPUSHGeofenceManager sharedGeoFence] getGeofenceList];
}
@end
@implementation JPushGetGeofenceRequest
@synthesize lat = _lat, lng = _lng;
- (instancetype)init {
  if ((self = [super init])) {
    self.controlFlag = 23;
    self.version = 1;
    self.protocolType = 37;
  }
  return self;
}
- (NSData *)bodyData {
  NSMutableData *data = [NSMutableData new];
  NSDictionary *info = @{
    @"lat" : @(self.lat),
    @"lng" : @(self.lng),
    @"time" : @((unsigned int)[JCOREUtilities currentTimestamp])
  };
  [data jcore_appendDataWithLength:[JCOREUtilities jsonToData:info]];
  [JCORELog log:YES
          level:0x3ff
           flag:0x80
        context:0
           file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/"
                "JPush/JPush/Network/Request/JPushGetGeofence.m"
       function:__PRETTY_FUNCTION__
           line:0x1d
            tag:nil
         format:@"try to get geofence:%@", info];
  return data;
}
@end
@implementation JPushGetGeofenceResponse
@synthesize geoDic = _geoDic;
- (instancetype)initWithResponse:(id<JCORETcpRespondProtocol>)response {
  if ((self = [super initWithResponse:response])) {
    unsigned short offset = 0;
    self.statusCode = [JCOREDecoder uInt16FromBuffer:self.bodyData
                                              offset:&offset];
    if (!self.statusCode) {
      self.geoDic =
          [JCOREUtilities dataToJSON:[JCOREDecoder dataFromBuffer:self.bodyData
                                                           offset:&offset]];
      [JCORELog log:YES
              level:0x3ff
               flag:0x100
            context:0
               file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"
                    "ios-jpush/JPush/JPush/Network/Request/JPushGetGeofence.m"
           function:__PRETTY_FUNCTION__
               line:0x33
                tag:nil
             format:@"get geofence %@", self.geoDic];
    } else {
      [JCORELog log:NO
              level:0x3ff
               flag:0x20
            context:0
               file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"
                    "ios-jpush/JPush/JPush/Network/Request/JPushGetGeofence.m"
           function:__PRETTY_FUNCTION__
               line:0x35
                tag:nil
             format:@"geofence get failed"];
    }
  }
  return self;
}
- (void)handleReceipt {
  [[JPUSHGeofenceManager sharedGeoFence] handleGetGeofenceResult:self.geoDic];
}
@end
#endif
