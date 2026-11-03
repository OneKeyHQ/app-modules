#pragma once
#import "JPUSHService.h"

@interface JPUSHPropertyVO : NSObject {
    long long _opearteType;
    long long _seq;
    NSDictionary *_propertiesInfo;
    JPUSHPropertiesOperationCompletion _propertyCompletion;
}
@property (nonatomic) long long opearteType;
@property (nonatomic) long long seq;
@property (nonatomic, strong) NSDictionary *propertiesInfo;
@property (nonatomic, copy) JPUSHPropertiesOperationCompletion propertyCompletion;
@end
