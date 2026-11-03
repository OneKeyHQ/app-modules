#import "JCOREDataControl.h"
@implementation JCOREDataControl
@synthesize model=_model,osVersionName=_osVersionName,resolution=_resolution,language=_language,systemName=_systemName,operatorName=_operatorName,wifi=_wifi,ssid=_ssid,bssid=_bssid,cell=_cell,gps=_gps,appList=_appList,sdkIndex=_sdkIndex;
- (instancetype)init { self=[super init];if(self)[self setDefaultValue];return self; }
- (void)setDefaultValue {
    _model=YES;
    _osVersionName=YES;
    _resolution=YES;
    _language=YES;
    _systemName=YES;
    _operatorName=YES;
    _wifi=YES;
    _ssid=YES;
    _bssid=YES;
    _cell=YES;
    _gps=YES;
    _appList=YES;
}
@end
