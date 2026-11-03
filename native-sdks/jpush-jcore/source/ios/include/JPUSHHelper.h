#pragma once
#import <UIKit/UIKit.h>
@interface JPUSHHelper : NSObject
+ (NSString *)JPUSHDisplayVersion;
+ (BOOL)checkFormatOfMobileNumber:(NSString *)number;
+ (NSString *)convertTokenDataToString:(NSData *)data;
+ (void)postNotification:(NSString *)name withUserInfo:(NSDictionary *)info;
+ (NSString *)dictionaryToString:(NSDictionary *)dictionary;
+ (UIWindow *)getCurrentWinow;
+ (UIViewController *)getCurrentVCFromOnMainThread;
+ (UIViewController *)getCurrentVCFrom;
+ (UIViewController *)getCurrentVCFrom:(UIViewController *)controller;
+ (NSDate *)transformStrToDate:(NSString *)string;
+ (NSDate *)transLocaleDate:(NSDate *)date;
+ (NSString *)transformDateToStr:(NSDate *)date;
+ (BOOL)isDarkAppearance;
+ (BOOL)isLandscape;
+ (BOOL)is24Hour;
+ (UIColor *)colorWithHexString:(NSString *)string;
+ (UIColor *)ColorwithHexString:(NSString *)string Alpha:(double)alpha;
+ (BOOL)isPngOrJpgImage:(NSData *)data;
+ (BOOL)containsChinese:(NSString *)string;
@end
