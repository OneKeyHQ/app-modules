#import "JCOREDataControlManager.h"
#import "JCOREDataControl.h"
#import "JCoreParameter.h"
#import "RecoveredTransportDependencies.h"

@implementation JCOREDataControlManager
@synthesize controls=_controls;
+ (instancetype)shared {
    static JCOREDataControlManager *instance;
    static dispatch_once_t once;
    dispatch_once(&once,^{instance=[[super allocWithZone:nil] init];});
    return instance;
}
+ (instancetype)allocWithZone:(NSZone *)zone { return [JCOREDataControlManager shared]; }
- (id)copyWithZone:(NSZone *)zone { return [JCOREDataControlManager shared]; }
- (instancetype)init { self=[super init];if(self)_controls=[NSMutableArray array];return self; }
- (void)setDataControl:(id)control {
    JCOREDataControl *value=[[JCOREDataControl alloc] init];
    if([[control allKeys] containsObject:@"model"])value.model=[control[@"model"] boolValue];
    if([[control allKeys] containsObject:@"osVersionName"])value.osVersionName=[control[@"osVersionName"] boolValue];
    if([[control allKeys] containsObject:@"resolution"])value.resolution=[control[@"resolution"] boolValue];
    if([[control allKeys] containsObject:@"language"])value.language=[control[@"language"] boolValue];
    if([[control allKeys] containsObject:@"systemName"])value.systemName=[control[@"systemName"] boolValue];
    if([[control allKeys] containsObject:@"operatorName"])value.operatorName=[control[@"operatorName"] boolValue];
    if([[control allKeys] containsObject:@"wifi"])value.wifi=[control[@"wifi"] boolValue];
    if([[control allKeys] containsObject:@"ssid"])value.ssid=[control[@"ssid"] boolValue];
    if([[control allKeys] containsObject:@"bssid"])value.bssid=[control[@"bssid"] boolValue];
    if([[control allKeys] containsObject:@"cell"])value.cell=[control[@"cell"] boolValue];
    if([[control allKeys] containsObject:@"gps"])value.gps=[control[@"gps"] boolValue];
    if([[control allKeys] containsObject:@"appList"])value.appList=[control[@"appList"] boolValue];
    if([[control allKeys] containsObject:@"sdkIndex"])value.sdkIndex=[control[@"sdkIndex"] intValue];
    int match=-1;
    for(NSUInteger index=0;index<[_controls count];index++){
        JCOREDataControl *existing=_controls[index];
        if(value.sdkIndex==existing.sdkIndex)match=(int)index;
    }
    if(match>=0)[_controls replaceObjectAtIndex:(NSUInteger)(unsigned int)match withObject:value];
    else [_controls addObject:value];
    JCOREDataControl *first=_controls[0];
    BOOL gps=first.gps,model=first.model,osVersionName=first.osVersionName,resolution=first.resolution,language=first.language,systemName=first.systemName,operatorName=first.operatorName,wifi=first.wifi,ssid=first.ssid,bssid=first.bssid,cell=first.cell,appList=first.appList;
    if([_controls count]>1)for(JCOREDataControl *item in _controls){
        gps=gps||item.gps;
        model=model||item.model;
        osVersionName=osVersionName||item.osVersionName;
        resolution=resolution||item.resolution;
        language=language||item.language;
        systemName=systemName||item.systemName;
        operatorName=operatorName||item.operatorName;
        wifi=wifi||item.wifi;
        ssid=ssid||item.ssid;
        bssid=bssid||item.bssid;
        cell=cell||item.cell;
        appList=appList||item.appList;
    }
    [[JCoreParameter shared] setLocationEnable:gps];
    [[JCoreParameter shared] setDeviceModelEnable:model];
    [[JCoreParameter shared] setSystemVersionEnable:osVersionName];
    [[JCoreParameter shared] setResolutionEnable:resolution];
    [[JCoreParameter shared] setSystemLanguageEnable:language];
    [[JCoreParameter shared] setSystemNameEnable:systemName];
    [[JCoreParameter shared] setOperatorNameEnable:operatorName];
    [[JCoreParameter shared] setWifiInfoEnable:wifi];
    [[JCoreParameter shared] setSSIDEnable:ssid];
    [[JCoreParameter shared] setBSSIDEnable:bssid];
    [[JCoreParameter shared] setCellEnabel:cell];
    [[JCoreParameter shared] setApplistEnable:appList];
    [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Interface/JCOREDataControlManager.m" function:__PRETTY_FUNCTION__ line:0xb2 tag:nil format:@"sdk data collect:gps:%d,model:%d,osVersionName:%d,resolution:%d,language:%d,systemName:%d,operatorName:%d,wifi:%d,SSID:%d,BSSID:%d,cell:%d,appList:%d",gps,model,osVersionName,resolution,language,systemName,operatorName,wifi,ssid,bssid,cell,appList];
}
@end
