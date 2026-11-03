#pragma once
#import <Foundation/Foundation.h>
#import <dispatch/dispatch.h>

@interface JCoreParameter : NSObject {
    dispatch_semaphore_t _locationLock;
    BOOL _jcoreHasSetUp;
    BOOL _deviceModelEnable;
    BOOL _systemVersionEnable;
    BOOL _resolutionEnable;
    BOOL _systemLanguageEnable;
    BOOL _systemNameEnable;
    BOOL _operatorNameEnable;
    BOOL _wifiInfoEnable;
    BOOL _SSIDEnable;
    BOOL _BSSIDEnable;
    BOOL _cellEnabel;
    BOOL _locationEnable;
    BOOL _applistEnable;
    BOOL _backgroundEnable;
    long long _heartBeatTimeInterval;
}
@property (nonatomic) BOOL jcoreHasSetUp;
@property (nonatomic) BOOL deviceModelEnable;
@property (nonatomic) BOOL systemVersionEnable;
@property (nonatomic) BOOL resolutionEnable;
@property (nonatomic) BOOL systemLanguageEnable;
@property (nonatomic) BOOL systemNameEnable;
@property (nonatomic) BOOL operatorNameEnable;
@property (nonatomic) BOOL wifiInfoEnable;
@property (nonatomic) BOOL SSIDEnable;
@property (nonatomic) BOOL BSSIDEnable;
@property (nonatomic) BOOL cellEnabel;
@property (nonatomic) BOOL locationEnable;
@property (nonatomic) BOOL applistEnable;
@property (nonatomic) long long heartBeatTimeInterval;
@property (nonatomic) BOOL backgroundEnable;
+ (instancetype)shared;
+ (instancetype)allocWithZone:(NSZone *)zone;
- (id)copyWithZone:(NSZone *)zone;
@end
