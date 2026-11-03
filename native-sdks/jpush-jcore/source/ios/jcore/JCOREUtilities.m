#import "JCOREUtilities.h"
#import "JCOREClientInfo.h"
#import "RecoveredCache.h"
#import "JCOREDataSecurity.h"
#import "JCoreParameter.h"
#import "JCOREThread.h"
#import "JCORELog.h"
#import "JCORENetworkReachabilityManager.h"
#import <UIKit/UIKit.h>
#import <CoreTelephony/CTTelephonyNetworkInfo.h>
#import <CoreTelephony/CTCarrier.h>
#import <CoreLocation/CoreLocation.h>
#import <SystemConfiguration/CaptiveNetwork.h>
#include <sys/sysctl.h>
#include <sys/utsname.h>
#include <sys/mount.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <mach/mach.h>
#include <mach-o/dyld.h>
#include <mach-o/loader.h>
#include <arpa/inet.h>
#include <zlib.h>
#include <time.h>
#include <errno.h>

// These APIs are intentionally retained from the original SDK contract.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
@interface JCORENetUtilities : NSObject
+ (BOOL)isViaWifi;
@end
#define UtilitiesFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Interface/JCOREUtilities.m"
#define UtilitiesLog(fn, sourceLine, messageFormat, ...) [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:UtilitiesFile function:fn line:sourceLine tag:nil format:messageFormat, ##__VA_ARGS__]
NSString *_jwifiName __attribute__((section("__DATA,__common")));
BOOL _jisGetWifiName __attribute__((section("__DATA,__common")));
NSString *_jBSSIDName __attribute__((section("__DATA,__common")));
BOOL _jisGetBSSIDName __attribute__((section("__DATA,__common")));
static NSString *utilitiesFakeImei, *utilitiesSystemVersion;
static BOOL utilitiesObservedNetwork;
static NSString *utilitiesResolution, *utilitiesDeviceId, *utilitiesOldDeviceId, *utilitiesDeviceMac;
static NSNumber *utilitiesResolutionWidth, *utilitiesResolutionHeight, *utilitiesFirstLoad;
static NSInteger utilitiesInstallDate;
static NSDate *utilitiesStartDate;
static CTTelephonyNetworkInfo *utilitiesTelephonyInfo;
static NSString *utilitiesLibraryPath, *utilitiesCachesPath, *utilitiesHomePath;

static int jcoredl_imageNamed(const char *name, BOOL exact) {
    if (name) { uint32_t count = _dyld_image_count(); for (uint32_t i = 0; i < count; i++) { const char *image = _dyld_get_image_name(i); if (exact ? strcmp(image, name) == 0 : strstr(image, name) != NULL) return (int)i; } }
    return -1;
}
static const char *jcoredl_imageUUID(const char *name, BOOL exact) {
    if (!name) return NULL;
    int index = jcoredl_imageNamed(name, exact); if (index == -1) return NULL;
    const struct mach_header *header = _dyld_get_image_header((uint32_t)index); if (!header) return NULL;
    const struct load_command *command;
    switch (header->magic) {
        case MH_MAGIC: case MH_CIGAM: command = (const struct load_command *)(header + 1); break;
        case MH_MAGIC_64: case MH_CIGAM_64: command = (const struct load_command *)((const struct mach_header_64 *)header + 1); break;
        default: return NULL;
    }
    for (uint32_t i = 0; i < header->ncmds; i++) { if (command->cmd == LC_UUID) return (const char *)((const struct uuid_command *)command)->uuid; command = (const struct load_command *)((const char *)command + command->cmdsize); }
    return NULL;
}
static BOOL UtilitiesVMStats(vm_statistics_data_t *stats, vm_size_t *pageSize) {
    host_t host = mach_host_self(); kern_return_t code = host_page_size(host, pageSize);
    if (code) { UtilitiesLog("_Bool VMStats(vm_statistics_data_t *const, vm_size_t *const)", 830, @"host_page_size: %s", mach_error_string(code)); return NO; }
    mach_msg_type_number_t count = HOST_VM_INFO_COUNT; code = host_statistics(host, HOST_VM_INFO, (host_info_t)stats, &count);
    if (code) UtilitiesLog("_Bool VMStats(vm_statistics_data_t *const, vm_size_t *const)", 837, @"host_statistics: %s", mach_error_string(code));
    return code == KERN_SUCCESS;
}
static NSString *UtilitiesTimeString(time_t seconds) {
    char *buffer = malloc(21);
    if (buffer) { struct tm value = {0}; gmtime_r(&seconds, &value); snprintf(buffer, 21, "%04d-%02d-%02dT%02d:%02d:%02dZ", value.tm_year + 1900, value.tm_mon + 1, value.tm_mday, value.tm_hour, value.tm_min, value.tm_sec); }
    NSString *result = [NSString stringWithCString:buffer encoding:NSUTF8StringEncoding]; free(buffer); return result;
}
@implementation JCOREUtilities
+ (id)innerLock { static NSRecursiveLock *lock; static dispatch_once_t once; if (!lock) dispatch_once(&once, ^{ lock = [NSRecursiveLock new]; }); return lock; }
+ (void)observeNetworkChange {
    if (!utilitiesObservedNetwork) { utilitiesObservedNetwork = YES; [[JCORENetworkReachabilityManager sharedManager] setReachabilityStatusChangeBlock:^(NSInteger status) { if ((NSUInteger)(status - 1) < 2) [self networkAvailable]; }]; }
}
+ (void)networkAvailable {
    if ([JCOREThread isMainThread]) { UtilitiesLog("+[JCOREUtilities networkAvailable]", 88, @"hg - network change, try to get ssid/bssid"); [JCOREUtilities macAddress]; [JCOREUtilities ssid]; }
    else [JCOREThread performSel:@selector(networkAvailable) target:self withObject:nil];
}
+ (id)macAddress { if ([JCoreParameter shared].jcoreHasSetUp && [JCoreParameter shared].wifiInfoEnable && [JCoreParameter shared].BSSIDEnable) { [JCOREUtilities observeNetworkChange]; return [self networkInfo:@"BSSID"]; } return @""; }
+ (id)ssid { if ([JCoreParameter shared].jcoreHasSetUp && [JCoreParameter shared].wifiInfoEnable && [JCoreParameter shared].SSIDEnable) { [JCOREUtilities observeNetworkChange]; return [self networkInfo:@"SSID"]; } return @""; }
+ (id)networkInfo:(id)key {
    BOOL ssid = [key isEqualToString:@"SSID"], bssid = [key isEqualToString:@"BSSID"];
    if (!ssid && !bssid) return @"";
    id cached = ssid ? _jwifiName : _jBSSIDName;
    if (cached) return cached;
    if ((ssid ? _jisGetWifiName : _jisGetBSSIDName) || ![self isAccessWifiInfoNew]) return nil;
    if (![JCoreParameter shared].locationEnable || ([CLLocationManager authorizationStatus] != kCLAuthorizationStatusAuthorizedAlways && [CLLocationManager authorizationStatus] != kCLAuthorizationStatusAuthorizedWhenInUse && [CLLocationManager authorizationStatus] != kCLAuthorizationStatusAuthorizedAlways)) return nil;
    UtilitiesLog("+[JCOREUtilities networkInfo:]", ssid ? 181 : 183, ssid ? @"hg - get ssid from api" : @"hg - get bssid from api");
    CFArrayRef interfaces = CNCopySupportedInterfaces();
    id result = nil;
    for (id interface in (__bridge NSArray *)interfaces) {
        CFDictionaryRef info = CNCopyCurrentNetworkInfo((__bridge CFStringRef)interface);
        if (ssid) _jisGetWifiName = YES; else _jisGetBSSIDName = YES;
        if (info) {
            NSDictionary *dictionary = (__bridge NSDictionary *)info;
            if (dictionary.count && [dictionary isKindOfClass:[NSDictionary class]] && [dictionary.allKeys containsObject:key]) result = dictionary[key];
            CFRelease(info);
            if (result) { if (ssid) _jwifiName = result; else _jBSSIDName = result; break; }
        }
    }
    if (interfaces) CFRelease(interfaces);
    return result;
}
+ (BOOL)isAccessWifiInfoNew {
    NSURL *url = [[[NSBundle mainBundle] URLForResource:@"embedded" withExtension:@"mobileprovision"] copy];
    if (!url) { UtilitiesLog("+[JCOREUtilities isAccessWifiInfoNew]", 246, @"读取embedded.mobileprovision文件失败"); return NO; }
    NSError *error = nil; NSString *profile = [NSString stringWithContentsOfURL:url encoding:NSISOLatin1StringEncoding error:&error];
    if (error || !profile) { UtilitiesLog("+[JCOREUtilities isAccessWifiInfoNew]", 255, @"读取文件失败: %@", error); return NO; }
    NSRange range = [profile rangeOfString:@"<key>com.apple.developer.networking.wifi-info</key>"];
    if (range.location == NSNotFound) return NO;
    NSUInteger start = NSMaxRange(range); if ((NSInteger)start < 0 || profile.length <= start) return NO;
    NSString *tail = [profile substringWithRange:NSMakeRange(start, profile.length - start)];
    NSString *first = [[tail componentsSeparatedByString:@"</string>"] firstObject];
    if (first) { (void)[first rangeOfString:@"<true/>"]; return YES; }
    return NO;
}
+ (BOOL)isAccessWifiInfo {
    static BOOL denied; static dispatch_once_t once; dispatch_once(&once, ^{ CFArrayRef interfaces = CNCopySupportedInterfaces(); if (interfaces) { CFDictionaryRef info = CNCopyCurrentNetworkInfo(CFArrayGetValueAtIndex(interfaces, 0)); denied = info == NULL; CFRelease(interfaces); } }); return !denied;
}
+ (id)appVersion { return [[NSBundle mainBundle] infoDictionary][@"CFBundleShortVersionString"] ?: @""; }
+ (id)appName { return [[NSBundle mainBundle] infoDictionary][@"CFBundleDisplayName"] ?: @""; }
+ (id)modelName { if ([JCoreParameter shared].jcoreHasSetUp && [JCoreParameter shared].deviceModelEnable) { UtilitiesLog("+[JCOREUtilities modelName]", 346, @"hg - get model form api"); return [self getModelName]; } return @""; }
// Only the string conversion is inside the original exception region.
+ (id)getModelName {
    struct utsname value;
    if (uname(&value) != 0) return [UIDevice currentDevice].model;
    @try { return [NSString stringWithCString:value.machine encoding:NSUTF8StringEncoding]; }
    @catch (NSException *exception) { [JCORELog log:NO level:0x3ff flag:1 context:0 file:UtilitiesFile function:"+[JCOREUtilities getModelName]" line:361 tag:nil format:@"Action - modelName exeception:%@", exception]; return nil; }
}
+ (id)bundleID { return (__bridge NSString *)CFBundleGetIdentifier(CFBundleGetMainBundle()); }
+ (id)fakeIMEI {
    if (!utilitiesFakeImei) { id value = [JCORECacheUtilities persistentObjectForKey:@"APDeviceId"]; if (value) utilitiesFakeImei = [value copy]; else { utilitiesFakeImei = [@"iPhone" stringByAppendingString:[[[NSUUID UUID] UUIDString] stringByReplacingOccurrencesOfString:@"-" withString:@""]]; [JCORECacheUtilities setPersistentObject:utilitiesFakeImei forKey:@"APDeviceId"]; } } return utilitiesFakeImei;
}
+ (void)removeFakeIMEI { utilitiesFakeImei = nil; [JCORECacheUtilities setPersistentObject:nil forKey:@"APDeviceId"]; }
+ (id)systemVersion {
    if (![JCoreParameter shared].jcoreHasSetUp || ![JCoreParameter shared].systemVersionEnable) return @"";
    if (utilitiesSystemVersion) return utilitiesSystemVersion;
    double now = [JCOREUtilities currentTimestamp]; id cacheTime = [JCORECacheUtilities persistentObjectForKey:@"kJCORECacheSystemVerTimeKey"];
    if ([cacheTime isKindOfClass:[NSNumber class]]) {
        if (now - [cacheTime doubleValue] > 259200) UtilitiesLog("+[JCOREUtilities systemVersion]", 422, @"hg - get systemVersion - cache expire");
        else { id cached = [JCORECacheUtilities persistentObjectForKey:@"kJORESystemVerValueKey"]; if ([cached isKindOfClass:[NSString class]]) { UtilitiesLog("+[JCOREUtilities systemVersion]", 426, @"hg - get systemVersion - from cache: %@", cached); utilitiesSystemVersion = cached; return cached; } }
    } else UtilitiesLog("+[JCOREUtilities systemVersion]", 418, @"hg - get systemVersion - no cache");
    if (now - [[JCORECacheUtilities persistentObjectForKey:@"kJCORELastGetSystemVerApiTimeKey"] doubleValue] < 1800) { UtilitiesLog("+[JCOREUtilities systemVersion]", 436, @"hg - get systemVersion - api less than 30 mins"); return @""; }
    NSString *version = [UIDevice currentDevice].systemVersion; UtilitiesLog("+[JCOREUtilities systemVersion]", 441, @"hg - get systemVersion - from api :%@", version);
    [JCORECacheUtilities setPersistentObject:@(now) forKey:@"kJCORELastGetSystemVerApiTimeKey"];
    if ([version isKindOfClass:[NSString class]] && version.length) { utilitiesSystemVersion = version; [JCORECacheUtilities setPersistentObject:version forKey:@"kJORESystemVerValueKey"]; [JCORECacheUtilities setPersistentObject:@(now) forKey:@"kJCORECacheSystemVerTimeKey"]; return utilitiesSystemVersion; }
    return @"";
}
+ (id)systemLanguage { if (![JCoreParameter shared].jcoreHasSetUp || ![JCoreParameter shared].systemLanguageEnable) return @"-1"; return [[[[NSUserDefaults standardUserDefaults] objectForKey:@"AppleLanguages"] objectAtIndex:0] copy] ?: @"-1"; }
+ (double)systemFontSize { return [[[UIFont preferredFontForTextStyle:UIFontTextStyleBody].fontDescriptor objectForKey:@"NSFontSizeAttribute"] floatValue]; }
+ (id)currentResolution {
    if (![JCoreParameter shared].jcoreHasSetUp || ![JCoreParameter shared].resolutionEnable) return @"";
    if (utilitiesResolution.length) return utilitiesResolution;
    id cached = [JCORECacheUtilities persistentObjectForKey:@"kJCoreResolution"];
    if (cached) { UtilitiesLog("+[JCOREUtilities currentResolution]", 498, @"hg - get resolution from cache"); utilitiesResolution = [cached copy]; return utilitiesResolution; }
    UtilitiesLog("+[JCOREUtilities currentResolution]", 502, @"hg - get resolution from api"); CGSize size = [UIScreen mainScreen].currentMode.size; utilitiesResolution = [NSString stringWithFormat:@"%dx%d", (int)size.width, (int)size.height]; [JCORECacheUtilities setPersistentObject:utilitiesResolution forKey:@"kJCoreResolution"]; return utilitiesResolution;
}
+ (id)currentResolutionWidth {
    if (![JCoreParameter shared].jcoreHasSetUp || ![JCoreParameter shared].resolutionEnable) return @0;
    if (utilitiesResolutionWidth) return utilitiesResolutionWidth;
    id cached = [JCORECacheUtilities persistentObjectForKey:@"kJCoreResolutionWidth"];
    if (cached) { UtilitiesLog("+[JCOREUtilities currentResolutionWidth]", 526, @"hg - get currentResolutionWidth from cache"); utilitiesResolutionWidth = cached; return cached; }
    UtilitiesLog("+[JCOREUtilities currentResolutionWidth]", 530, @"hg - get currentResolutionWidth from api"); utilitiesResolutionWidth = @([UIScreen mainScreen].currentMode.size.width); [JCORECacheUtilities setPersistentObject:utilitiesResolution forKey:@"kJCoreResolutionWidth"]; return utilitiesResolutionWidth;
}
+ (id)currentResolutionHeight {
    if (![JCoreParameter shared].jcoreHasSetUp || ![JCoreParameter shared].resolutionEnable) return @0;
    if (utilitiesResolutionHeight) return utilitiesResolutionHeight;
    id cached = [JCORECacheUtilities persistentObjectForKey:@"kJCoreResolutionHeight"];
    if (cached) { UtilitiesLog("+[JCOREUtilities currentResolutionHeight]", 554, @"hg - get currentResolutionHeight from cache"); utilitiesResolutionHeight = cached; return cached; }
    UtilitiesLog("+[JCOREUtilities currentResolutionHeight]", 558, @"hg - get currentResolutionHeight from api"); utilitiesResolutionHeight = @([UIScreen mainScreen].currentMode.size.height); [JCORECacheUtilities setPersistentObject:utilitiesResolution forKey:@"kJCoreResolutionHeight"]; return utilitiesResolutionHeight;
}
+ (id)deviceID:(id)key {
    id pid = [self devicePID:nil];
    if (!utilitiesDeviceId) {
        id old = [self oldDeviceID];
        if (old) utilitiesDeviceId = [old copy];
        else {
            if (![pid isKindOfClass:[NSString class]] || ![pid length]) { if (!key) return nil; pid = [self createDeviceIdWithKey:key]; }
            utilitiesDeviceId = pid; [self cacheDeviceID:utilitiesDeviceId];
        }
    }
    return utilitiesDeviceId;
}
+ (id)oldDeviceID { if (!utilitiesOldDeviceId) { id value = [JCORECacheUtilities persistentObjectForKey:@"JCoreKeychainDeviceIDKey"]; if (value) utilitiesOldDeviceId = [value copy]; } return utilitiesOldDeviceId; }
+ (id)devicePID:(id)key {
    id value = [self oldDeviceID];
    if (![value isKindOfClass:[NSString class]] || ![value length]) { if (!key) return nil; value = [self createDeviceIdWithKey:key]; [self cacheDeviceID:value]; }
    return value;
}
+ (void)resetDeviceID {
    @try { if ([JCOREThread isMainThread]) [self cacheDeviceID:[self createDeviceIdWithKey:[JCOREClientInfo appKey]]]; }
    @catch (NSException *exception) { UtilitiesLog("+[JCOREUtilities resetDeviceID]", 676, @"[异常] - resetDeviceID"); }
}
+ (id)deviceMacAddress { if (!utilitiesDeviceMac) { id value = [JCORECacheUtilities persistentObjectForKey:@"JCoreKeychainDeviceMacAddressKey"]; if (value) utilitiesDeviceMac = [value copy]; } return utilitiesDeviceMac ?: @""; }
+ (void)cacheDeviceMacAddress:(id)value { [JCORECacheUtilities setPersistentObject:value forKey:@"JCoreKeychainDeviceMacAddressKey"]; }
+ (id)createDeviceIdWithKey:(id)key {
    NSString *bundle = [(__bridge NSString *)CFBundleGetIdentifier(CFBundleGetMainBundle()) copy];
    NSString *uuid = [[[NSUUID UUID] UUIDString] stringByReplacingOccurrencesOfString:@"-" withString:@""];
    NSString *time = [NSString stringWithFormat:@"%.0f", [JCOREUtilities currentTimestamp]];
    NSString *input = [NSString stringWithFormat:@"%@&%@&%@&%@", key, bundle, uuid, time];
    NSString *value = [JCOREDataSecurity stringHashingByMD5:input];
    UtilitiesLog("+[JCOREUtilities createDeviceIdWithKey:]", 713, @"generate deviceID:%@", [NSString stringWithFormat:@"%@&%@&%@&%@", key, bundle, uuid, time]);
    return [value copy];
}
+ (void)cacheDeviceID:(id)value { [JCORECacheUtilities setPersistentObject:value forKey:@"JCoreKeychainDeviceIDKey"]; }
+ (BOOL)amIbeginDeugged {
    int mib[] = {CTL_KERN, KERN_PROC, KERN_PROC_PID, getpid()}; struct kinfo_proc process = {0}; size_t size = sizeof(process); (void)sysctl(mib, 4, &process, &size, NULL, 0); return (process.kp_proc.p_flag & P_TRACED) != 0;
}
+ (void)resetDeviceInfoCache {
    utilitiesSystemVersion = nil; utilitiesResolutionWidth = nil; utilitiesResolutionHeight = nil; utilitiesResolution = @"";
    for (NSString *key in @[@"kJCORECacheSystemVerTimeKey", @"kJCORELastGetSystemVerApiTimeKey", @"kJCoreResolution", @"kJCoreResolutionWidth", @"kJCoreResolutionHeight", @"kAdvertisingIdentifier"]) [JCORECacheUtilities setPersistentObject:nil forKey:key];
}
+ (id)systemName { return [JCoreParameter shared].jcoreHasSetUp && [JCoreParameter shared].systemNameEnable ? [UIDevice currentDevice].systemName : @""; }
+ (BOOL)isJailbroken { uint32_t count = _dyld_image_count(); for (uint32_t i = 0; i < count; i++) if (strstr(_dyld_get_image_name(i), "MobileSubstrate")) return YES; return NO; }
+ (id)dateToString:(id)value { NSDateFormatter *formatter = [NSDateFormatter new]; formatter.dateFormat = @"yyyy-MM-dd HH:mm:ss"; return [formatter stringFromDate:value]; }
+ (id)stringToDate:(id)value { NSDateFormatter *formatter = [NSDateFormatter new]; formatter.dateFormat = @"yyyy-MM-dd HH:mm:ss"; return [formatter dateFromString:value]; }
+ (BOOL)isFirstLoad {
    if (!utilitiesFirstLoad) { id cached = [JCORECacheUtilities cacheObjectForKey:@"kJCoreIsFirstLoad"]; utilitiesFirstLoad = [cached isKindOfClass:[NSNumber class]] ? cached : @YES; if (utilitiesFirstLoad.boolValue) [JCORECacheUtilities setCacheObject:@NO forKey:@"kJCoreIsFirstLoad"]; } return utilitiesFirstLoad.boolValue;
}
+ (BOOL)isFirstDay {
    if (!utilitiesInstallDate) { id cached = [JCORECacheUtilities cacheObjectForKey:@"kJCoreInstallDate"]; if (![cached isKindOfClass:[NSNumber class]]) { utilitiesInstallDate = (NSInteger)[[NSDate date] timeIntervalSince1970]; [JCORECacheUtilities setCacheObject:@(utilitiesInstallDate) forKey:@"kJCoreInstallDate"]; return YES; } utilitiesInstallDate = [cached integerValue]; }
    utilitiesInstallDate = ((utilitiesInstallDate + 86400) / 86400) * 86400; return [[NSDate date] timeIntervalSince1970] < utilitiesInstallDate;
}
+ (id)uuid { return [[[NSUUID UUID] UUIDString] stringByReplacingOccurrencesOfString:@"-" withString:@""]; }
+ (id)cpuArch { return @""; }
+ (id)appStartTime { return UtilitiesTimeString(time(NULL)); }
+ (id)appStartTimeDate { if (!utilitiesStartDate) utilitiesStartDate = [NSDate date]; return utilitiesStartDate; }
+ (id)bootTime { struct timeval value = {0}; size_t size = sizeof(value); if (sysctlbyname("kern.boottime", &value, &size, NULL, 0)) UtilitiesLog("+[JCOREUtilities bootTime]", 967, @"Could not get timeval value for %@: %s", @"kern.boottime", strerror(errno)); return UtilitiesTimeString(value.tv_sec); }
+ (id)model { return [JCOREUtilities modelName]; }
+ (id)jcoreUUIDBytesToString:(const char *)bytes { CFUUIDBytes value; memcpy(&value, bytes, sizeof(value)); CFUUIDRef uuid = CFUUIDCreateFromUUIDBytes(NULL, value); NSString *result = CFBridgingRelease(CFUUIDCreateString(NULL, uuid)); CFRelease(uuid); return result; }
+ (id)appUUID {
    NSString *result = [NSString string]; NSBundle *bundle = [NSBundle mainBundle]; NSString *path = [bundle.bundlePath stringByAppendingPathComponent:bundle.infoDictionary[@"CFBundleExecutable"]];
    if (path) { const char *bytes = jcoredl_imageUUID(path.UTF8String, YES); if (!bytes) bytes = jcoredl_imageUUID(path.lastPathComponent.UTF8String, NO); if (bytes) result = [JCOREUtilities jcoreUUIDBytesToString:bytes]; } return result;
}
+ (id)appBuild { return [NSBundle mainBundle].infoDictionary[@"CFBundleVersion"]; }
+ (id)model_id { size_t size = 0; if (sysctlbyname("hw.model", NULL, &size, NULL, 0) || !size) return nil; void *buffer = malloc(size); if (!buffer) return nil; NSString *result = nil; if (sysctlbyname("hw.model", buffer, &size, NULL, 0) == 0 && size) result = [NSString stringWithCString:buffer encoding:NSUTF8StringEncoding]; free(buffer); return result; }
+ (id)memory_size { unsigned long long memory = 0; size_t size = sizeof(memory); return sysctlbyname("hw.memsize", &memory, &size, NULL, 0) == 0 ? [NSString stringWithFormat:@"%llu", memory] : nil; }
+ (id)free_memory { vm_statistics_data_t stats; vm_size_t size; return UtilitiesVMStats(&stats, &size) ? [NSString stringWithFormat:@"%lld", (long long)stats.free_count * size] : [@0 stringValue]; }
+ (id)usable_memory { vm_statistics_data_t stats; vm_size_t size; return UtilitiesVMStats(&stats, &size) ? [NSString stringWithFormat:@"%lld", (long long)(uint32_t)(stats.free_count + stats.active_count + stats.inactive_count + stats.wire_count) * size] : [@0 stringValue]; }
+ (id)storage_size { NSDictionary *attributes = [[NSFileManager defaultManager] attributesOfFileSystemForPath:NSHomeDirectory() error:NULL]; return [NSString stringWithFormat:@"%f", [attributes[NSFileSystemSize] floatValue]]; }
+ (id)timezone { return [NSTimeZone localTimeZone].abbreviation; }
+ (id)jStringSysctl:(id)name {
    size_t size = 0; if (sysctlbyname([name UTF8String], NULL, &size, NULL, 0) || !size) return nil;
    void *buffer = malloc(size); if (!buffer) size = 0; NSString *result = nil;
    if (sysctlbyname([name UTF8String], buffer, &size, NULL, 0) == 0 && size && buffer) result = [NSString stringWithCString:buffer encoding:NSUTF8StringEncoding]; free(buffer); return result;
}
+ (id)osVersion { return [JCOREUtilities jStringSysctl:@"kern.version"]; }
+ (id)kernelVersion { return [JCOREUtilities jStringSysctl:@"kern.osversion"]; }
+ (id)dateToIso8601String:(id)value { NSDateFormatter *formatter = [NSDateFormatter new]; formatter.locale = [NSLocale localeWithLocaleIdentifier:@"en_US_POSIX"]; formatter.timeZone = [NSTimeZone timeZoneWithAbbreviation:@"UTC"]; formatter.dateFormat = @"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"; return [formatter stringFromDate:value]; }
+ (id)bt { int mib[] = {CTL_KERN, KERN_BOOTTIME}; struct timeval value; size_t size = sizeof(value); (void)sysctl(mib, 2, &value, &size, NULL, 0); return [NSString stringWithFormat:@"%ld", value.tv_sec]; }
+ (id)countryCode { return [[NSLocale currentLocale] objectForKey:NSLocaleCountryCode]; }
+ (id)deviceName { return [UIDevice currentDevice].name.length ? [UIDevice currentDevice].name : nil; }
+ (id)sysFileTime {
    NSData *data = [[NSData alloc] initWithBase64EncodedString:@"L3Zhci9tb2JpbGUvTGlicmFyeS9Vc2VyQ29uZmlndXJhdGlvblByb2ZpbGVzL1B1YmxpY0luZm8vTUNNZXRhLnBsaXN0" options:0]; NSString *path = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]; NSError *error = nil; NSDictionary *attributes = [[NSFileManager defaultManager] attributesOfItemAtPath:path error:&error]; id date = attributes[NSFileCreationDate]; return [date isKindOfClass:[NSDate class]] ? [NSString stringWithFormat:@"%f", [date timeIntervalSince1970]] : nil;
}
+ (id)mntId { struct statfs info; (void)statfs("/", &info); return strstr(info.f_mntfromname, "com.apple.os.update-") ? [NSString stringWithFormat:@"%s", info.f_mntfromname + 20] : @""; }
+ (id)fileInitTime { struct stat info; return stat("/var/mobile", &info) == 0 ? [NSString stringWithFormat:@"%ld.%09ld", info.st_birthtimespec.tv_sec, info.st_birthtimespec.tv_nsec] : @""; }
+ (id)telephonyInfo { if (!utilitiesTelephonyInfo) utilitiesTelephonyInfo = [CTTelephonyNetworkInfo new]; return utilitiesTelephonyInfo; }
+ (id)ctCarrierInfo { if (@available(iOS 16.4, *)) return nil; return [[self telephonyInfo] subscriberCellularProvider]; }
+ (id)ctCarrierName { return [JCoreParameter shared].jcoreHasSetUp && [JCoreParameter shared].operatorNameEnable ? [[[self ctCarrierInfo] carrierName] copy] : @""; }
+ (id)ctCarrierShortName { if (![JCoreParameter shared].jcoreHasSetUp) return @""; NSString *mnc = [JCOREUtilities ctMobileNetworkCode]; if (mnc) mnc = [JCOREUtilities ctMobileNetworkCode]; NSString *mcc = [JCOREUtilities ctCountryCode]; if (mcc) mcc = [JCOREUtilities ctCountryCode]; return [[NSString alloc] initWithFormat:@"%@%@", mcc ?: @"", mnc ?: @""]; }
+ (id)ctMobileNetworkCode { if (![JCoreParameter shared].jcoreHasSetUp || ![JCoreParameter shared].cellEnabel) return @""; UtilitiesLog("+[JCOREUtilities ctMobileNetworkCode]", 1399, @"hg - get mnc from api"); NSString *value = [[[self ctCarrierInfo] mobileNetworkCode] copy]; return value.length ? value : nil; }
+ (id)ctCountryCode { if (![JCoreParameter shared].jcoreHasSetUp || ![JCoreParameter shared].cellEnabel) return @""; UtilitiesLog("+[JCOREUtilities ctCountryCode]", 1418, @"hg - get mcc from api"); NSString *value = [[[self ctCarrierInfo] mobileCountryCode] copy]; return value.length ? value : nil; }
+ (id)ctRadioType {
    if ([[UIDevice currentDevice].systemVersion floatValue] < 7) return nil;
    NSString *radio = [[self telephonyInfo] currentRadioAccessTechnology];
    if ([@[CTRadioAccessTechnologyGPRS, CTRadioAccessTechnologyEdge, CTRadioAccessTechnologyWCDMA, CTRadioAccessTechnologyHSDPA, CTRadioAccessTechnologyHSUPA] containsObject:radio ?: [NSNull null]]) return @"gsm";
    if ([@[CTRadioAccessTechnologyCDMA1x, CTRadioAccessTechnologyCDMAEVDORev0, CTRadioAccessTechnologyCDMAEVDORevA, CTRadioAccessTechnologyCDMAEVDORevB, CTRadioAccessTechnologyeHRPD] containsObject:radio ?: [NSNull null]]) return @"cdma";
    return [radio isEqualToString:CTRadioAccessTechnologyLTE] ? @"lte" : nil;
}
+ (id)ctGeneration {
    if (![JCoreParameter shared].jcoreHasSetUp) return nil;
    if ([JCORENetUtilities isViaWifi]) return @"wifi";
    if ([[UIDevice currentDevice].systemVersion floatValue] < 7) return nil;
    NSString *radio = [[self telephonyInfo] currentRadioAccessTechnology];
    if ([@[CTRadioAccessTechnologyGPRS, CTRadioAccessTechnologyEdge, CTRadioAccessTechnologyCDMA1x] containsObject:radio ?: [NSNull null]]) return @"2g";
    if ([@[CTRadioAccessTechnologyWCDMA, CTRadioAccessTechnologyHSDPA, CTRadioAccessTechnologyHSUPA, CTRadioAccessTechnologyCDMAEVDORev0, CTRadioAccessTechnologyCDMAEVDORevA, CTRadioAccessTechnologyCDMAEVDORevB, CTRadioAccessTechnologyeHRPD] containsObject:radio ?: [NSNull null]]) return @"3g";
    if ([radio isEqualToString:CTRadioAccessTechnologyLTE]) return @"4g";
    if ([radio isEqualToString:@"CTRadioAccessTechnologyNRNSA"] || [radio isEqualToString:@"CTRadioAccessTechnologyNR"]) return @"5g";
    return nil;
}
+ (unsigned char)ctGenerationNumber { NSString *generation = [self ctGeneration]; if ([generation isEqualToString:@"wifi"]) return 1; if ([generation isEqualToString:@"2g"]) return 2; if ([generation isEqualToString:@"3g"]) return 3; return [generation isEqualToString:@"4g"] ? 4 : 0; }
+ (id)ctNetworkType {
    if ([[UIDevice currentDevice].systemVersion floatValue] < 7) return nil;
    NSString *radio = [[CTTelephonyNetworkInfo new] currentRadioAccessTechnology];
    NSArray *technologies = @[CTRadioAccessTechnologyGPRS, CTRadioAccessTechnologyEdge, CTRadioAccessTechnologyWCDMA, CTRadioAccessTechnologyHSDPA, CTRadioAccessTechnologyHSUPA, CTRadioAccessTechnologyCDMA1x, CTRadioAccessTechnologyCDMAEVDORev0, CTRadioAccessTechnologyCDMAEVDORevA, CTRadioAccessTechnologyCDMAEVDORevB, CTRadioAccessTechnologyeHRPD, CTRadioAccessTechnologyLTE];
    NSUInteger index = [technologies indexOfObject:radio ?: [NSNull null]]; return index == NSNotFound ? nil : @[@"GPRS", @"EDGE", @"WCDMA", @"HSDPA", @"HSUPA", @"CDMA1x", @"CDMAEVDORev0", @"CDMAEVDORevA", @"CDMAEVDORevB", @"HRPD", @"LTE"][index];
}
+ (id)ctCurCellularCarrierName {
    if (![JCoreParameter shared].jcoreHasSetUp || ![JCoreParameter shared].operatorNameEnable) return @"";
    if (@available(iOS 16.0, *)) return @"";
    if (@available(iOS 13.0, *)) { CTTelephonyNetworkInfo *info = [self telephonyInfo]; NSString *identifier = info.dataServiceIdentifier; id carriers = info.serviceSubscriberCellularProviders; if ([carriers isKindOfClass:[NSDictionary class]]) { id carrier = [carriers valueForKey:identifier]; if ([carrier isKindOfClass:[CTCarrier class]]) return [[carrier carrierName] copy]; } return @""; }
    return [[[self ctCarrierInfo] carrierName] copy];
}
+ (id)libraryDirectoryPath { if (!utilitiesLibraryPath) utilitiesLibraryPath = [NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES) firstObject]; return utilitiesLibraryPath; }
+ (id)cachesDirectoryPath { if (!utilitiesCachesPath) utilitiesCachesPath = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) firstObject]; return utilitiesCachesPath; }
+ (id)homeDirectoryPath { if (!utilitiesHomePath) utilitiesHomePath = NSHomeDirectory(); return utilitiesHomePath; }
+ (id)gzip:(id)value {
    if (![value isKindOfClass:[NSData class]] || ![value length]) return value;
    z_stream stream = {0}; stream.next_in = (Bytef *)[value bytes]; stream.avail_in = (uInt)[value length];
    if (deflateInit2_(&stream, Z_DEFAULT_COMPRESSION, Z_DEFLATED, 31, 8, Z_DEFAULT_STRATEGY, "1.2.11", sizeof(stream)) != Z_OK) return nil;
    NSMutableData *output = [NSMutableData dataWithLength:16384];
    do { if (stream.total_out >= output.length) [output increaseLengthBy:16384]; stream.next_out = (Bytef *)output.mutableBytes + stream.total_out; stream.avail_out = (uInt)(output.length - stream.total_out); (void)deflate(&stream, Z_FINISH); } while (!stream.avail_out);
    deflateEnd(&stream); output.length = stream.total_out; return [NSData dataWithData:output];
}
+ (id)unzip:(id)value {
    if (![value isKindOfClass:[NSData class]] || ![value length]) return value;
    z_stream stream = {0}; stream.next_in = (Bytef *)[value bytes]; stream.avail_in = (uInt)[value length];
    if (inflateInit2_(&stream, 31, "1.2.11", sizeof(stream)) != Z_OK) return nil;
    NSUInteger half = [value length] >> 1; NSMutableData *output = [NSMutableData dataWithLength:half * 3];
    do { if (stream.total_out >= output.length) [output increaseLengthBy:half]; stream.next_out = (Bytef *)output.mutableBytes + stream.total_out; stream.avail_out = (uInt)(output.length - stream.total_out); (void)inflate(&stream, Z_FINISH); } while (!stream.avail_out);
    if (inflateEnd(&stream) != Z_OK) return nil;
    output.length = stream.total_out; return [NSData dataWithData:output];
}
+ (double)currentTimestamp { struct timeval now = {0}; gettimeofday(&now, NULL); return (double)now.tv_usec / 1000000.0 + now.tv_sec; }
+ (id)currentTimeZone {
    NSString *description = [[[NSTimeZone systemTimeZone] description] copy]; if (!description) return @"-1";
    int offset = [[description componentsSeparatedByString:@"offset "][1] intValue]; return [NSString stringWithFormat:offset >= 3600 ? @"+%d" : @"%d", offset / 3600];
}
+ (void)converTimestamp:(long long)timestamp toDate:(NSString *__autoreleasing *)date andTime:(NSString *__autoreleasing *)time {
    NSTimeZone *zone = [NSTimeZone localTimeZone]; NSDateFormatter *dateFormatter = [NSDateFormatter new], *timeFormatter = [NSDateFormatter new]; dateFormatter.dateFormat = @"yyyy-MM-dd"; timeFormatter.dateFormat = @"HH:mm:ss"; NSLocale *locale = [[NSLocale alloc] initWithLocaleIdentifier:@"en_US"]; dateFormatter.locale = locale; timeFormatter.locale = locale; dateFormatter.timeZone = zone; timeFormatter.timeZone = zone; NSDate *value = [NSDate dateWithTimeIntervalSince1970:(double)timestamp]; *date = [dateFormatter stringFromDate:value]; *time = [timeFormatter stringFromDate:value];
}
+ (id)converTimestampToString:(long long)timestamp { NSTimeZone *zone = [NSTimeZone localTimeZone]; NSDateFormatter *formatter = [NSDateFormatter new]; formatter.dateFormat = @"yyyy-MM-dd HH:mm:ss"; formatter.timeZone = zone; return [formatter stringFromDate:[NSDate dateWithTimeIntervalSince1970:(double)timestamp]]; }
+ (id)transformStrToDate:(id)value {
    if (![value isKindOfClass:[NSString class]] || !value) return nil;
    @try { NSDateFormatter *formatter = [NSDateFormatter new]; formatter.dateFormat = @"yyyy-MM-dd HH:mm:ss"; formatter.locale = [NSLocale currentLocale]; return [formatter dateFromString:value]; }
    @catch (NSException *exception) { UtilitiesLog("+[JCOREUtilities transformStrToDate:]", 1814, @"[bomb] [%s] isception", "+[JCOREUtilities transformStrToDate:]"); return nil; }
}
+ (id)transformDateToStr:(id)value {
    if (!value || ![value isKindOfClass:[NSDate class]]) return nil;
    @try { NSDateFormatter *formatter = [NSDateFormatter new]; formatter.dateFormat = @"yyyy-MM-dd HH:mm:ss"; formatter.locale = [NSLocale currentLocale]; return [formatter stringFromDate:value]; }
    @catch (NSException *exception) { UtilitiesLog("+[JCOREUtilities transformDateToStr:]", 1835, @"[bomb] [%s] isception", "+[JCOREUtilities transformDateToStr:]"); return nil; }
}
+ (id)jsonToData:(id)value {
    [[self innerLock] lock];
    if (!value || !([value isKindOfClass:[NSArray class]] || [value isKindOfClass:[NSDictionary class]])) { [[self innerLock] unlock]; return nil; }
    id snapshot = nil;
    if ([value isKindOfClass:[NSArray class]]) snapshot = [[NSArray alloc] initWithArray:value];
    if ([value isKindOfClass:[NSDictionary class]]) snapshot = [[NSDictionary alloc] initWithDictionary:value];
    if (!snapshot) { [[self innerLock] unlock]; return nil; }
    if (![NSJSONSerialization isValidJSONObject:snapshot]) { [[self innerLock] unlock]; [JCORELog log:NO level:0x3ff flag:1 context:0 file:UtilitiesFile function:"+[JCOREUtilities jsonToData:]" line:1869 tag:nil format:@"%@ serialization unknown type:%@", [snapshot class], snapshot]; return nil; }
    NSError *error = nil; NSData *data = nil;
    @try { data = [NSJSONSerialization dataWithJSONObject:snapshot options:0 error:&error]; }
    @catch (NSException *exception) { [JCORELog log:NO level:0x3ff flag:1 context:0 file:UtilitiesFile function:"+[JCOREUtilities jsonToData:]" line:1876 tag:nil format:@"json serialization exception %@", exception]; }
    if (error) [JCORELog log:NO level:0x3ff flag:1 context:0 file:UtilitiesFile function:"+[JCOREUtilities jsonToData:]" line:1879 tag:nil format:@"json serialization %@ failed with error:%@", snapshot, error];
    [[self innerLock] unlock]; return [data mutableCopy];
}
+ (id)dataToJSON:(id)value {
    if (![value isKindOfClass:[NSData class]] || ![value length]) return nil;
    [[self innerLock] lock];
    NSError *error = nil; id result = nil;
    @try { result = [NSJSONSerialization JSONObjectWithData:value options:0 error:&error]; }
    @catch (NSException *exception) { [JCORELog log:NO level:0x3ff flag:1 context:0 file:UtilitiesFile function:"+[JCOREUtilities dataToJSON:]" line:1896 tag:nil format:@"to json serialization exception %@", exception]; }
    [[self innerLock] unlock];
    if (error) { [JCORELog log:NO level:0x3ff flag:1 context:0 file:UtilitiesFile function:"+[JCOREUtilities dataToJSON:]" line:1900 tag:nil format:@"trans data to obj with error:%ld", (long)error.code]; return nil; }
    return result;
}
+ (id)dataToString:(id)value { if (![value isKindOfClass:[NSData class]]) return nil; const unsigned char *bytes = [value bytes]; NSMutableString *result = [NSMutableString string]; for (NSUInteger i = 0; i < [value length]; i++) [result appendFormat:@"%02.2hhx", bytes[i]]; return result; }
+ (BOOL)checkIPAddress:(id)value { struct in_addr v4; struct in6_addr v6; return inet_pton(AF_INET, [value UTF8String], &v4) > 0 || inet_pton(AF_INET6, [value UTF8String], &v6) > 0; }
+ (id)description:(id)value { return nil; }
+ (id)dictionaryToStr:(id)value { @try { if (!value) return nil; return [[NSString alloc] initWithData:[self jsonToData:value] encoding:NSUTF8StringEncoding]; } @catch (NSException *exception) { UtilitiesLog("+[JCOREUtilities dictionaryToStr:]", 1951, @"[bomb][%s] exception", "+[JCOREUtilities dictionaryToStr:]"); return nil; } }
+ (id)dictionaryWithJsonString:(id)value { @try { if (!value) return nil; id result = [self dataToJSON:[value dataUsingEncoding:NSUTF8StringEncoding]]; return [result isKindOfClass:[NSDictionary class]] ? result : nil; } @catch (NSException *exception) { UtilitiesLog("+[JCOREUtilities dictionaryWithJsonString:]", 1968, @"[%s] is exception", "+[JCOREUtilities dictionaryWithJsonString:]"); return nil; } }
+ (id)stringByMD5:(id)value { return [JCOREDataSecurity stringHashingByMD5:value]; }
+ (id)stringByMD5Data:(id)value { return [JCOREDataSecurity stringHashingByMD5Data:value]; }
+ (id)dataBase64:(id)value { return [value base64EncodedStringWithOptions:0]; }
+ (id)aesEncryptData:(id)value withKey:(NSString *)key iv:(const char *)iv { return [JCOREDataSecurity aesEncryptData:value withKey:key iv:iv]; }
+ (id)aesDecryptData:(id)value withKey:(NSString *)key iv:(const char *)iv { return [JCOREDataSecurity aesDecryptData:value withKey:key iv:iv]; }
+ (id)encryptKeyForHttp { return [JCOREDataSecurity encryptKeyForHttp]; }
+ (id)rsaEncrypt:(id)value publicKey:(id)key { return [JCOREDataSecurity rsaEncrypt:value publicKey:key]; }
+ (id)aes256EncryptData:(id)value withKey:(NSString *)key iv:(const char *)iv { return [JCOREDataSecurity aes256EncryptData:value withKey:key iv:iv]; }
@end
#pragma clang diagnostic pop
