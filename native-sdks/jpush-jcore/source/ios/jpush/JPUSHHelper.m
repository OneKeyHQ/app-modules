#import "JPUSHHelper.h"
#import "JCORELog.h"
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
#define HELPER_LOG(async, flagValue, lineNo, formatValue, ...)                                               \
    [JCORELog log:async                                                                                      \
            level:0x3ff                                                                                      \
             flag:flagValue                                                                                  \
          context:0                                                                                          \
             file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/JPush/JPush/Utilities/"     \
                  "JPUSHHelper.m"                                                                            \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineNo                                                                                     \
              tag:nil                                                                                        \
           format:formatValue, ##__VA_ARGS__]
static NSString *JPUSHDisplayVersionName;
@implementation JPUSHHelper
+ (NSString *)JPUSHDisplayVersion {
    if (!JPUSHDisplayVersionName.length) {
        NSMutableString *version = [NSMutableString string];
        NSString *raw = @"50900";
        for (NSUInteger i = 0; i < raw.length; i++) {
            NSString *part = [raw substringWithRange:NSMakeRange(i, 1)];
            if (!(i & 1)) {
                [version appendString:part];
                if (i != raw.length - 1)
                    [version appendString:@"."];
            } else if (part.intValue)
                [version appendString:part];
        }
        JPUSHDisplayVersionName = version;
    }
    return JPUSHDisplayVersionName;
}
+ (BOOL)checkFormatOfMobileNumber:(NSString *)number {
    return [number isKindOfClass:NSString.class] &&
           [[NSPredicate predicateWithFormat:@"SELF MATCHES %@", @"^[+]?([0-9]+([-]?[0-9]+)*)+$"]
               evaluateWithObject:number];
}
+ (NSString *)convertTokenDataToString:(NSData *)data {
    if (![data isKindOfClass:NSData.class])
        return nil;
    const unsigned char *bytes = data.bytes;
    NSMutableString *value = [NSMutableString string];
    for (NSUInteger i = 0; i < data.length; i++)
        [value appendFormat:@"%02.2hhx", bytes[i]];
    return value;
}
+ (void)postNotification:(NSString *)name withUserInfo:(NSDictionary *)info {
    HELPER_LOG(YES, 8, 0x3d, @"%@ name: %@ %@", [NSString stringWithUTF8String:__PRETTY_FUNCTION__], name,
               info);
    dispatch_async(dispatch_get_main_queue(), ^{
        @autoreleasepool {
            [[NSNotificationCenter defaultCenter] postNotificationName:name object:nil userInfo:info];
        }
    });
}
+ (NSString *)dictionaryToString:(NSDictionary *)dictionary {
    if (!dictionary) {
        HELPER_LOG(NO, 0x20, 0x4b, @"dictionaryToString dic is nil");
        return @"";
    }
    if (![NSJSONSerialization isValidJSONObject:dictionary]) {
        HELPER_LOG(NO, 0x20, 0x4f, @"dictionaryToString  isValidJSONObject is false");
        return @"";
    }
    @try {
        NSError *error = nil;
        NSData *data = [NSJSONSerialization dataWithJSONObject:dictionary
                                                       options:NSJSONWritingPrettyPrinted
                                                         error:&error];
        if (error)
            return @"";
        return [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    } @catch (NSException *exception) {
        HELPER_LOG(NO, 0x20, 0x59, @"dictionaryToString  error %@", exception);
        return @"";
    }
}
+ (UIWindow *)getCurrentWinow {
    UIWindow *window = nil;
    if (@available(iOS 13.0, *)) {
        for (UIWindowScene *scene in UIApplication.sharedApplication.connectedScenes) {
            if (scene.activationState == UISceneActivationStateForegroundActive) {
                if (!scene.windows) {
                    HELPER_LOG(YES, 0x100, 0x6e, @"[%s] windowScene is nil", __PRETTY_FUNCTION__);
                    continue;
                }
                for (UIWindow *candidate in scene.windows)
                    if (!candidate.hidden && candidate.isKeyWindow) {
                        window = candidate;
                        break;
                    }
            }
        }
    } else
        window = UIApplication.sharedApplication.keyWindow;
    return window ?: UIApplication.sharedApplication.windows.firstObject;
}
+ (UIViewController *)getCurrentVCFromOnMainThread {
    __block UIViewController *controller = nil;
    if (NSThread.isMainThread)
        controller = [JPUSHHelper getCurrentVCFrom];
    else
        dispatch_sync(dispatch_get_main_queue(), ^{
            controller = [JPUSHHelper getCurrentVCFrom];
        });
    return controller;
}
+ (UIViewController *)getCurrentVCFrom {
    UIViewController *root = [self getCurrentWinow].rootViewController;
    return root ? [self getCurrentVCFrom:root] : nil;
}
+ (UIViewController *)getCurrentVCFrom:(UIViewController *)controller {
    if (!controller)
        return nil;
    if (controller.presentedViewController)
        controller = [self getCurrentVCFrom:controller.presentedViewController];
    if ([controller isKindOfClass:UITabBarController.class])
        return [self getCurrentVCFrom:((UITabBarController *)controller).selectedViewController];
    if ([controller isKindOfClass:UINavigationController.class])
        return [self getCurrentVCFrom:((UINavigationController *)controller).visibleViewController];
    return controller;
}
+ (NSDate *)transformStrToDate:(NSString *)string {
    if (![string isKindOfClass:NSString.class])
        return nil;
    @try {
        NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
        formatter.dateFormat = @"yyyy-MM-dd HH:mm:ss";
        formatter.locale = NSLocale.currentLocale;
        return [formatter dateFromString:string];
    } @catch (NSException *exception) {
        HELPER_LOG(YES, 0x100, 0xc1, @"[bomb] [%s] isception", __PRETTY_FUNCTION__);
        return nil;
    }
}
+ (NSDate *)transLocaleDate:(NSDate *)date {
    return [date dateByAddingTimeInterval:[NSTimeZone.systemTimeZone secondsFromGMTForDate:date]];
}
+ (NSString *)transformDateToStr:(NSDate *)date {
    if (![date isKindOfClass:NSDate.class])
        return nil;
    @try {
        NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
        formatter.dateFormat = @"yyyy-MM-dd HH:mm:ss";
        formatter.locale = NSLocale.currentLocale;
        return [formatter stringFromDate:date];
    } @catch (NSException *exception) {
        HELPER_LOG(YES, 0x100, 0xdd, @"[bomb] [%s] isception", __PRETTY_FUNCTION__);
        return nil;
    }
}
+ (BOOL)isDarkAppearance {
    if (@available(iOS 13.0, *))
        return UITraitCollection.currentTraitCollection.userInterfaceStyle == UIUserInterfaceStyleDark;
    return NO;
}
+ (BOOL)isLandscape {
    return UIInterfaceOrientationIsLandscape(UIApplication.sharedApplication.statusBarOrientation);
}
+ (BOOL)is24Hour {
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    formatter.locale = NSLocale.currentLocale;
    formatter.dateStyle = NSDateFormatterNoStyle;
    formatter.timeStyle = NSDateFormatterShortStyle;
    NSString *value = [formatter stringFromDate:NSDate.date];
    return [value rangeOfString:formatter.AMSymbol].location == NSNotFound &&
           [value rangeOfString:formatter.PMSymbol].location == NSNotFound;
}
+ (UIColor *)colorWithHexString:(NSString *)string {
    return [self ColorwithHexString:string Alpha:-1.0];
}
+ (UIColor *)ColorwithHexString:(NSString *)string Alpha:(double)alpha {
    NSString *value = [[string
        stringByTrimmingCharactersInSet:NSCharacterSet.whitespaceAndNewlineCharacterSet] uppercaseString];
    if (value.length < 6)
        return UIColor.clearColor;
    if ([value hasPrefix:@"0X"])
        value = [value substringFromIndex:2];
    if ([value hasPrefix:@"#"])
        value = [value substringFromIndex:1];
    if (value.length != 6 && value.length != 8)
        return UIColor.clearColor;
    unsigned int red = 0, green = 0, blue = 0;
    double parsedAlpha = 1;
    if (value.length == 8) {
        [[NSScanner scannerWithString:[value substringWithRange:NSMakeRange(6, 2)]] scanHexInt:&red];
        parsedAlpha = (double)((float)red / 255.0f);
        value = [value substringToIndex:6];
    }
    if (alpha <= 0)
        alpha = parsedAlpha;
    [[NSScanner scannerWithString:[value substringWithRange:NSMakeRange(0, 2)]] scanHexInt:&red];
    [[NSScanner scannerWithString:[value substringWithRange:NSMakeRange(2, 2)]] scanHexInt:&green];
    [[NSScanner scannerWithString:[value substringWithRange:NSMakeRange(4, 2)]] scanHexInt:&blue];
    return [UIColor colorWithRed:(double)((float)red / 255.0f)
                           green:(double)((float)green / 255.0f)
                            blue:(double)((float)blue / 255.0f)
                           alpha:alpha];
}
+ (BOOL)isPngOrJpgImage:(NSData *)data {
    if (!data)
        return NO;
    unsigned char byte;
    [data getBytes:&byte length:1];
    return byte == 0xff || byte == 0x89;
}
+ (BOOL)containsChinese:(NSString *)string {
    return [string isKindOfClass:NSString.class] && string.length &&
           [string rangeOfString:@".*[一-龥].*" options:NSRegularExpressionSearch].length != 0;
}
@end
#pragma clang diagnostic pop
