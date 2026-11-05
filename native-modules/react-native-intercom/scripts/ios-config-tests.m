#import <Foundation/Foundation.h>
#import <objc/runtime.h>
#import "ICMConfig.h"
#import "ICMUtils.h"
#import "ICMPersistenceProxy.h"
static NSMutableArray *passed, *events;
static void check(BOOL value,NSString *name) { if(!value){fprintf(stderr,"FAIL: %s\n",name.UTF8String);exit(1);}[passed addObject:name]; }
static void waitFor(BOOL (^done)(void)) { NSDate *deadline=[NSDate dateWithTimeIntervalSinceNow:3];while(!done()){if(deadline.timeIntervalSinceNow<0)abort();[[NSRunLoop mainRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:0.01]];} }
// This remaining ConfigModules model is a diagnostic fixture, never SDK source.
static char fixtureDictionary;
#define FIXTURE_MODEL(CLASS) \
@implementation CLASS \
+ (BOOL)supportsSecureCoding { return YES; } \
- (instancetype)initWithDictionary:(NSDictionary *)dictionary { if((self=[super init]))objc_setAssociatedObject(self,&fixtureDictionary,dictionary,OBJC_ASSOCIATION_RETAIN_NONATOMIC);return self; } \
- (instancetype)initWithCoder:(NSCoder *)coder { return [self initWithDictionary:[coder decodeObjectOfClasses:[NSSet setWithObjects:NSDictionary.class,NSString.class,NSNumber.class,nil] forKey:@"fixture"]]; } \
- (void)encodeWithCoder:(NSCoder *)coder { [coder encodeObject:objc_getAssociatedObject(self,&fixtureDictionary) forKey:@"fixture"]; } \
- (BOOL)isEqual:(id)object { if([object class]!=[self class])return NO;id left=objc_getAssociatedObject(self,&fixtureDictionary),right=objc_getAssociatedObject(object,&fixtureDictionary);return left==right||[left isEqual:right]; } \
- (NSUInteger)hash { return [objc_getAssociatedObject(self,&fixtureDictionary) hash]; } \
@end
FIXTURE_MODEL(ICMRecoveredConfigModules)
@interface FixtureConfigBundle : NSObject
@end
@implementation FixtureConfigBundle
- (id)objectForInfoDictionaryKey:(NSString *)key { return [@[@"NSCameraUsageDescription",@"NSMicrophoneUsageDescription"] containsObject:key] ? @"synthetic usage" : nil; }
@end
@interface FixtureConfigDefaults : NSObject
@end
@implementation FixtureConfigDefaults
- (BOOL)boolForKey:(NSString *)key { if (![key isEqual:@"isVoiceTranscriptionEnabled"]) abort(); return NO; }
@end
@interface FixtureConfigSubclass : ICMConfig
@end
@implementation FixtureConfigSubclass
@end
static void imageSink(id self,SEL command) { (void)self;[events addObject:NSStringFromSelector(command)]; }
int main(void) {
 @autoreleasepool {
    passed=[NSMutableArray new];events=[NSMutableArray new];
    FixtureConfigBundle *bundle=[FixtureConfigBundle new];FixtureConfigDefaults *defaults=[FixtureConfigDefaults new];
    Method bundleMethod=class_getClassMethod(NSBundle.class,@selector(mainBundle)),defaultsMethod=class_getClassMethod(NSUserDefaults.class,@selector(standardUserDefaults));
    IMP oldBundle=method_setImplementation(bundleMethod,imp_implementationWithBlock(^id(id cls){(void)cls;return bundle;}));
    IMP oldDefaults=method_setImplementation(defaultsMethod,imp_implementationWithBlock(^id(id cls){(void)cls;return defaults;}));
    for(NSString *selector in @[@"loadCustomLauncherLogoImage",@"fetchAndCacheFinThinkingGIFBrandedImage",@"fetchAndCacheFinThinkingGIFUnBrandedImage",@"prefetchHomeHeaderImagesIfNeeded"])check(class_addMethod(ICMConfig.class,NSSelectorFromString(selector),(IMP)imageSink,"v@:"),[NSString stringWithFormat:@"diagnostic-only explicit missing image sink %@",selector]);
    NSString *root=@(getenv("ICM_TEST_DIRECTORY"));[[NSFileManager defaultManager] createDirectoryAtPath:root withIntermediateDirectories:YES attributes:nil error:nil];
    Method directory=class_getClassMethod(ICMUtils.class,@selector(intercomDocumentsDirectory));IMP oldDirectory=method_setImplementation(directory,imp_implementationWithBlock(^id(id cls){(void)cls;return root;}));
    [[NSFileManager defaultManager] removeItemAtPath:[ICMConfig archivePath] error:nil];
    ICMConfig *initial=[ICMConfig sharedConfig];
    check(initial && !initial.isPersistedToDisk && initial.attachmentSettings && initial.conversationStateSyncSettings && !initial.modules,@"initial shared config uses actual disk-missing fallback and original nested default allocations");
    check(!initial.inboundMessages && initial.audioEnabled && initial.metricsEnabled && initial.backgroundRequestsEnabled && initial.newSessionThreshold==20 && initial.userUpdateDupCacheMaxAge==300 && initial.rateLimit==100 && initial.rateLimitPeriod==60 && initial.softResetTimeout==1 && initial.batchUserUpdatePeriod==1 && initial.pingDelay==1,@"all original numeric and boolean defaults retained");
    check([initial.launcherAlignment isEqual:@"right"] && initial.features.count==0 && !initial.appName && !initial.baseColorString && !initial.crashReportingConfig,@"original reference defaults remain right empty features and nil fields");
    check([[ICMConfig archivePath] isEqual:[root stringByAppendingPathComponent:@"config.icm"]] && initial.receivedFromServer==NO && initial.useDefaultLauncherAlignment,@"archive path and persisted/default alignment getters preserve original values");
    [ICMConfig updateSharedInstanceFromDictionary:nil];[ICMConfig updateSharedInstanceFromDictionary:@{}];[ICMConfig updateSharedInstanceFromDictionary:(id)@[]];
    check([ICMConfig sharedConfig]==initial,@"nil empty and wrong-type shared updates do nothing");
    __block NSUInteger notifications=0;__block BOOL notificationMain=NO;
    id observer=[[NSNotificationCenter defaultCenter] addObserverForName:@"kICMIntercomConfigChangedNotification" object:nil queue:nil usingBlock:^(NSNotification *note){(void)note;notifications++;notificationMain=NSThread.isMainThread;}];
    ICMConfig *fresh=[ICMConfig instanceFromDictionary:@{@"name":@"synthetic",@"new_session_threshold":@42.5,@"local_rate_limit":@(-3),@"features":@[@"a",@"a",@"b"],@"locale":@"zh_CN",@"help_center_locale":@"en_GB",@"unknown":@1}];
    check([fresh.appName isEqual:@"synthetic"] && fresh.newSessionThreshold==42.5 && fresh.rateLimit==(NSUInteger)-3 && fresh.features.count==2 && [fresh hasFeature:@"a"] && [fresh.intercomLocale.localeIdentifier isEqual:@"zh_CN"] && [fresh.helpCenterLocale.localeIdentifier isEqual:@"en_GB"],@"original key aliases numeric conversion locale parser and feature set mapping");
    [fresh setValue:[NSNull null] forKey:@"name"];
    check([fresh.appName isEqual:@"synthetic"],@"direct NSNull setter input is ignored by original override");
    [fresh setAttributesFromDictionary:@{@"new_session_threshold":@"wrong",@"name":[NSNull null],@"features":@{}}];
    check(fresh.appName==nil && fresh.newSessionThreshold==42.5 && fresh.features.count==2,@"bulk Foundation KVC converts NSNull to nil while wrong mapped numeric/features types preserve previous values");
    NSMutableString *name=[@"mutable-name" mutableCopy];fresh.appName=name;[name appendString:@"-changed"];
    check([fresh.appName isEqual:@"mutable-name"],@"ordinary original copy property copies mutable strings");
    [events removeAllObjects];NSMutableString *logo=[@"synthetic-logo" mutableCopy];fresh.launcherCustomLogoURLString=logo;[logo appendString:@"-changed"];
    check(fresh.launcherCustomLogoURLString==logo && [events isEqual:@[@"loadCustomLauncherLogoImage"]],@"custom logo setter retains despite copy metadata and calls actual missing side-effect entry");
    fresh.launcherCustomLogoURLString=(id)@42;check(!fresh.launcherCustomLogoURLString && events.count==1,@"invalid logo setter clears prior field without image load");
    [events removeAllObjects];fresh.finThinkingGIFBrandedURLString=nil;fresh.finThinkingGIFUnBrandedURLString=@"synthetic-gif";
    check([events isEqual:@[@"fetchAndCacheFinThinkingGIFBrandedImage",@"fetchAndCacheFinThinkingGIFUnBrandedImage"]],@"GIF setters always invoke side-effect entries including nil input");
    [events removeAllObjects];[fresh setValue:@{@"fixture":@1} forKey:@"modules"];
    check(fresh.modules && [events isEqual:@[@"prefetchHomeHeaderImagesIfNeeded"]],@"module parser invokes real missing prefetch entry immediately");
    fresh.modules=nil;fresh.finThinkingGIFUnBrandedURLString=nil;
    ICMConfig *same=[ICMConfig new];check([same isEqual:[ICMConfig new]],@"equivalent defaults compare equal with distinct nested fixture instances");
    same.isPersistedToDisk=YES;check([same isEqual:[ICMConfig new]],@"persisted flag excluded from equality");
    ICMConfig *nilAlignment=[ICMConfig new];nilAlignment.launcherAlignment=nil;
    check(![nilAlignment isEqualToConfig:nilAlignment] && [nilAlignment isEqual:nilAlignment],@"typed nil alignment is unequal even to itself while generic identity fast path returns YES");
    ICMConfig *nan=[ICMConfig new];nan.newSessionThreshold=NAN;
    check(![nan isEqualToConfig:nan],@"typed floating comparison preserves NaN inequality");
    check(![[ICMConfig new] isEqual:[FixtureConfigSubclass new]],@"generic equality requires exact runtime class");
    initial.isPersistedToDisk=NO;[same saveIfNeeded];
    check([ICMConfig sharedConfig]==same && same.isPersistedToDisk && notifications==0 && [[NSFileManager defaultManager] fileExistsAtPath:[ICMConfig archivePath]],@"equal unpersisted shared state saves synchronously then schedules notification");
    waitFor(^BOOL{return notifications==1;});check(notificationMain,@"save change notification arrives asynchronously on main");
    ICMConfig *equal=[ICMConfig new];[equal saveIfNeeded];
    check([ICMConfig sharedConfig]==same && notifications==1 && !equal.isPersistedToDisk,@"equal persisted shared config skips save and does not adopt fresh instance");
    Method archive=class_getClassMethod(ICMPersistenceProxy.class,@selector(archiveRootObject:toFile:));
    __block BOOL replacedBeforeArchive=NO;
    IMP oldArchive=method_setImplementation(archive,imp_implementationWithBlock(^BOOL(id cls,id object,id path){(void)cls;(void)path;replacedBeforeArchive=[ICMConfig sharedConfig]==object;return NO;}));
    ICMConfig *failed=[ICMConfig new];failed.appName=@"failed synthetic";[failed save];
    check(replacedBeforeArchive && [ICMConfig sharedConfig]==failed && failed.isPersistedToDisk && notifications==1,@"save publishes shared pointer before archive and marks persisted despite returned failure");
    method_setImplementation(archive,oldArchive);waitFor(^BOOL{return notifications==2;});
    ICMConfig *coded=[ICMConfig new];coded.appName=@"archive synthetic";coded.helpCenterUrlStrings=@[@"https://synthetic.invalid"];coded.crashReportingConfig=@{@"fixture":@[@1,@"x"]};coded.features=[NSSet setWithObject:@"fixture"];[ICMConfig saveConfig:coded];
    ICMConfig *decoded=[ICMConfig configFromDisk];
    check(decoded && [coded isEqual:decoded] && decoded.isPersistedToDisk && [decoded.helpCenterUrlStrings isEqual:coded.helpCenterUrlStrings],@"actual secure archive round trip preserves original scalar and allowed collection fields");
    __block BOOL deleteSynchronous=NO;dispatch_semaphore_t deleted=dispatch_semaphore_create(0);
    dispatch_async(dispatch_get_global_queue(0,0),^{NSUInteger before=notifications;[ICMConfig deleteSharedConfig];deleteSynchronous=notifications==before+1 && !notificationMain;dispatch_semaphore_signal(deleted);});
    check(dispatch_semaphore_wait(deleted,dispatch_time(DISPATCH_TIME_NOW,2*NSEC_PER_SEC))==0 && deleteSynchronous && ![ICMConfig sharedConfig].isPersistedToDisk && ![[NSFileManager defaultManager] fileExistsAtPath:[ICMConfig archivePath]],@"delete removes archive replaces defaults and posts synchronously on current background thread");
    [[NSNotificationCenter defaultCenter] removeObserver:observer];method_setImplementation(directory,oldDirectory);method_setImplementation(bundleMethod,oldBundle);method_setImplementation(defaultsMethod,oldDefaults);
    NSData *json=[NSJSONSerialization dataWithJSONObject:@{@"status":@"passed",@"testCount":@(passed.count),@"tests":passed,@"scope":@"Actual Config/Persistence/SafeNetworkModel with real Attachment/SyncSettings and explicit Modules/image diagnostic sinks; product PARTIAL_UNLINKED"} options:NSJSONWritingPrettyPrinted error:nil];fwrite(json.bytes,1,json.length,stdout);fputc('\n',stdout);
 }
 return 0;
}
