#pragma once
#import "JPUSHNewReport.h"
@interface JPushWebReport : JPUSHNewReport {
  NSString *_messageID;
  NSString *_dataStr;
  long long _result;
}
@property(nonatomic, copy) NSString *messageID;
@property(nonatomic, copy) NSString *dataStr;
@property(nonatomic) long long result;
- (void)handleResponse;
@end
