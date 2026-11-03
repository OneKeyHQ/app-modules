#pragma once
#import "JPUSHNewReport.h"
#import "JPUSHNewRequest.h"
#import "JPUSHNewResponse.h"
@interface JPUSHGeofenceInfoMessage : JPUSHNewResponse {
  unsigned char _type;
}
@property(nonatomic, readonly) unsigned char type;
- (instancetype)initWithResponse:(id<JCORETcpRespondProtocol>)response;
- (void)handleReceipt;
@end
@interface JPushGetGeofenceRequest : JPUSHNewRequest {
  double _lat;
  double _lng;
}
@property(nonatomic) double lat;
@property(nonatomic) double lng;
@end
@interface JPushGetGeofenceResponse : JPUSHNewResponse {
  NSDictionary *_geoDic;
}
@property(nonatomic, strong) NSDictionary *geoDic;
- (instancetype)initWithResponse:(id<JCORETcpRespondProtocol>)response;
- (void)handleReceipt;
@end
@interface JPUSHGeofenceReport : JPUSHNewReport {
  NSString *_geofenceId;
  long long _triggerTime;
  NSString *_latitude;
  NSString *_longitude;
}
@property(nonatomic, copy) NSString *geofenceId;
@property(nonatomic) long long triggerTime;
@property(nonatomic, copy) NSString *latitude;
@property(nonatomic, copy) NSString *longitude;
@end
