#import <Foundation/Foundation.h>
#import <objc/runtime.h>
#import <sys/utsname.h>
#import "ICMDeviceData.h"
#import "ICMUtils.h"
@protocol RCTBridgeModule <NSObject>
@end
@protocol FlutterPlugin <NSObject>
@end
static NSMutableArray *passed;
static void check(BOOL value, NSString *name) { if (!value) { fprintf(stderr,"FAIL: %s\n",name.UTF8String);exit(1); } [passed addObject:name]; }
@interface FixtureDeviceBundle : NSObject
@property (nonatomic, strong) NSDictionary *info;
@end
@implementation FixtureDeviceBundle
- (id)objectForInfoDictionaryKey:(NSString *)key { return self.info[key]; }
@end
@interface FixtureStyleDefaults : NSObject
@property (nonatomic) NSInteger style;
@property (nonatomic) NSUInteger reads, writes;
@end
@implementation FixtureStyleDefaults
- (NSInteger)integerForKey:(NSString *)key { if (![key isEqual:@"userInterfaceStyle"]) abort(); self.reads++; return self.style; }
- (void)setInteger:(NSInteger)value forKey:(NSString *)key { if (![key isEqual:@"userInterfaceStyle"]) abort(); self.writes++; self.style=value; }
@end
static Class fixtureClass(const char *name, Class parent, Protocol *protocol) {
    Class cls=objc_allocateClassPair(parent,name,0);
    if (protocol && !class_addProtocol(cls,protocol)) abort();
    objc_registerClassPair(cls); return cls;
}
int main(void) {
 @autoreleasepool {
    passed=[NSMutableArray new];
    FixtureDeviceBundle *bundle=[FixtureDeviceBundle new];
    Method bundleMethod=class_getClassMethod(NSBundle.class,@selector(mainBundle));
    IMP previousBundle=method_setImplementation(bundleMethod,imp_implementationWithBlock(^id(id cls){(void)cls;return bundle;}));
    NSMutableString *app=[@"synthetic-name" mutableCopy]; NSNumber *bundleID=@42; bundle.info=@{@"CFBundleName":app,@"CFBundleIdentifier":bundleID};
    check([ICMUtils applicationName]==app && (id)[ICMUtils bundleIdentifier]==bundleID,@"bundle fields preserve original identity and wrong types without validation");
    bundle.info=@{}; check([ICMUtils applicationName]==nil && [ICMUtils bundleIdentifier]==nil,@"missing bundle fields remain nil");
    [ICMUtils setDebugLoggingEnabled:NO];
    NSArray *values=@[[NSNull null],@NO,@YES,@0,@2,@"NO",@"no",@"false",@"",@[]];
    NSUInteger index=0;
    for(id value in values) {bundle.info=@{@"IntercomAutoIntegratePushNotifications":value};BOOL expected= ![value isEqual:@NO] && ![value isEqual:@"NO"]; check([ICMUtils shouldHandlePushNotificationsAutomatically]==expected,[NSString stringWithFormat:@"auto-push original value classification %lu",(unsigned long)index++]);}
    bundle.info=@{};check([ICMUtils shouldHandlePushNotificationsAutomatically],@"absent auto-push key defaults YES");
    bundle.info=@{@"NSExtension":@{@"NSExtensionPointIdentifier":@"com.apple.usernotifications.service"}};
    check([ICMUtils isRunningNotificationServiceExtension],@"exact notification service extension identifier matches");
    bundle.info=@{@"NSExtension":@{@"NSExtensionPointIdentifier":@"COM.apple.usernotifications.service"}};
    check(![ICMUtils isRunningNotificationServiceExtension],@"extension identifier comparison remains case sensitive");
    BOOL threw=NO;bundle.info=@{@"NSExtension":[NSNull null]}; @try{[ICMUtils isRunningNotificationServiceExtension];}@catch(NSException *exception){threw=[exception.name isEqual:NSInvalidArgumentException];}
    check(threw,@"malformed extension preserves original keyed-subscript exception");
    method_setImplementation(bundleMethod,previousBundle);
    FixtureStyleDefaults *defaults=[FixtureStyleDefaults new];
    Method defaultsMethod=class_getClassMethod(NSUserDefaults.class,@selector(standardUserDefaults));
    IMP previousDefaults=method_setImplementation(defaultsMethod,imp_implementationWithBlock(^id(id cls){(void)cls;return defaults;}));
    // Avoid the iOS initializer in this Foundation-only helper diagnostic.
    ICMDeviceData *device=class_createInstance(ICMDeviceData.class,0);
    for(NSNumber *number in @[@0,@1,@2,@3,@(-1)]) { [ICMDeviceData storeUserInterfaceStyle:number.integerValue]; NSString *expected=number.integerValue==1?@"Light":(number.integerValue==2?@"Dark":@"Unspecified");check([[device fetchUserInterfaceStyle] isEqual:expected],[NSString stringWithFormat:@"stored interface style %@",number]); }
    check(defaults.reads==5 && defaults.writes==5,@"interface style uses original defaults read/write per invocation");
    method_setImplementation(defaultsMethod,previousDefaults);
    struct utsname machine;uname(&machine);check([[ICMDeviceData machineName] isEqual:[NSString stringWithCString:machine.machine encoding:NSUTF8StringEncoding]],@"machine name comes from real uname UTF8 machine field");
    check([ICMUtils fetchSDKType]==4 && [[ICMDeviceData sdkType] isEqual:@"intercom-sdk-native"],@"no wrapper classes means original native SDK type");
    fixtureClass("IntercomUnity",NSObject.class,nil);
    check([ICMUtils fetchSDKType]==3 && [[ICMDeviceData sdkType] isEqual:@"intercom-sdk-unity"],@"Unity presence maps to SDK type three");
    fixtureClass("IntercomFlutterPlugin",NSObject.class,@protocol(FlutterPlugin));
    check([ICMUtils fetchSDKType]==2 && [[ICMDeviceData sdkType] isEqual:@"intercom-sdk-flutter"],@"Flutter protocol-conforming wrapper takes precedence over Unity");
    Class cordova=fixtureClass("CDVPlugin",NSObject.class,nil);fixtureClass("IntercomBridge",cordova,nil);
    check([ICMUtils fetchSDKType]==0 && [[ICMDeviceData sdkType] isEqual:@"intercom-sdk-cordova"],@"Cordova subclass wrapper takes precedence over Flutter");
    fixtureClass("IntercomModule",NSObject.class,@protocol(RCTBridgeModule));
    check([ICMUtils fetchSDKType]==1 && [[ICMDeviceData sdkType] isEqual:@"intercom-sdk-react-native"],@"React Native protocol-conforming wrapper takes first precedence");
    NSData *json=[NSJSONSerialization dataWithJSONObject:@{@"status":@"passed",@"testCount":@(passed.count),@"tests":passed,@"scope":@"Foundation DeviceData/Utils helpers only; iOS initializer/serializer object compiled, product push/identity UNLINKED; no default preferences or push system"} options:NSJSONWritingPrettyPrinted error:nil];fwrite(json.bytes,1,json.length,stdout);fputc('\n',stdout);
 }
 return 0;
}
