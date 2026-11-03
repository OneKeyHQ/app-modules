#pragma once
#import <Foundation/Foundation.h>
@interface JCOREDataControl : NSObject {
    BOOL _model;
    BOOL _osVersionName;
    BOOL _resolution;
    BOOL _language;
    BOOL _systemName;
    BOOL _operatorName;
    BOOL _wifi;
    BOOL _ssid;
    BOOL _bssid;
    BOOL _cell;
    BOOL _gps;
    BOOL _appList;
    int _sdkIndex;
}
@property(nonatomic) BOOL model;
@property(nonatomic) BOOL osVersionName;
@property(nonatomic) BOOL resolution;
@property(nonatomic) BOOL language;
@property(nonatomic) BOOL systemName;
@property(nonatomic) BOOL operatorName;
@property(nonatomic) BOOL wifi;
@property(nonatomic) BOOL ssid;
@property(nonatomic) BOOL bssid;
@property(nonatomic) BOOL cell;
@property(nonatomic) BOOL gps;
@property(nonatomic) BOOL appList;
@property(nonatomic) int sdkIndex;
- (void)setDefaultValue;
@end
