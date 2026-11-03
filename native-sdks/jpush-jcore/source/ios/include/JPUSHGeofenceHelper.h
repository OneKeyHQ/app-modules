#pragma once
#import <Foundation/Foundation.h>
@class JPUSHRegion;
@interface JPUSHGeofenceHelper : NSObject
+ (void)localNotification:(JPUSHRegion *)region;
+ (unsigned char)sameDayOrWeek:(double)first withTime:(double)second;
+ (id)triggerTimeListWith:(id)identifier;
+ (void)recordTriggerTimeWith:(id)identifier;
+ (void)removeTriggerTimeWith:(id)identifier;
+ (id)geofenceDictFrom:(JPUSHRegion *)region;
+ (BOOL)detectRepeatTrigger:(id)region;
+ (NSError *)geofenceLocationError:(JPUSHRegion *)region;
+ (id)getValidJPushRegion:(id)region
                    error:(NSError *__autoreleasing *)error
                 displayR:(id)display
                callbackR:(id)callback;
+ (id)findJPushRegion:(id)region displayR:(id)display callbackR:(id)callback;
+ (id)geofenceCachePathWith:(id)name;
+ (id)displayGeofenceCachePath;
+ (id)callbackGeofenceCachePath;
+ (void)saveOrUpdateGeofenceMessage:(id)regions path:(id)path;
+ (void)saveDisplayGeofence:(id)regions;
+ (void)saveCallbackGeofence:(id)regions;
+ (id)creatRegionWith:(id)info;
+ (id)transRegionWith:(id)info;
+ (id)generateRegionWith:(id)info;
+ (id)regionToJson:(JPUSHRegion *)region;
+ (id)jsonToRegion:(id)info;
+ (id)getGeofenceCacheFromLocalPath:(id)path;
+ (id)getDisplayGeofenceCache;
+ (id)getCallbackGeofenceCache;
@end
