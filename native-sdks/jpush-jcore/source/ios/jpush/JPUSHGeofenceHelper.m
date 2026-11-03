#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JPUSHGeofenceHelper.h"
#import "JPUSHPropertyModels.h"
#import "JPUSHService.h"
#import <CoreLocation/CoreLocation.h>
#define GEO_HELPER_LOG(asyncValue, flagValue, lineValue, formatValue, ...)     \
  [JCORELog log:asyncValue                                                     \
          level:0x3ff                                                          \
           flag:flagValue                                                      \
        context:0                                                              \
           file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"         \
                "ios-jpush/JPush/JPush/Controller/JPUSHGeofenceHelper.m"       \
       function:__PRETTY_FUNCTION__                                            \
           line:lineValue                                                      \
            tag:nil                                                            \
         format:formatValue, ##__VA_ARGS__]
static NSString *const TriggerTimeKey = @"kJPUSHGeofenceTriggerTimeInfo";
@implementation JPUSHGeofenceHelper
+ (void)localNotification:(JPUSHRegion *)region {
  if (!region.apnsDict)
    return;
  JPushNotificationContent *content = [JPushNotificationContent new];
  NSString *identifier =
      [NSString stringWithFormat:@"%@", region.apnsDict[@"_j_msgid"]];
  if (!identifier)
    identifier = region.identifier;
  id extras = [region.apnsDict objectForKey:@"_j_extras"];
  if (!extras) {
    GEO_HELPER_LOG(YES, 0x100, 0x1a, @"_j_extras 为nil");
    return;
  }
  id alert = extras[@"alert"];
  id title, body, subtitle, thread;
  if ([alert isKindOfClass:NSDictionary.class]) {
    title = alert[@"title"];
    body = alert[@"body"];
    subtitle = alert[@"subtitle"];
    thread = alert[@"thread-id"];
    extras = alert;
  } else {
    title = extras[@"title"];
    body = extras[@"alert"];
    subtitle = extras[@"subtitle"];
    thread = extras[@"thread-id"];
  }
  id sound = extras[@"sound"];
  content.title = title ?: @"";
  content.subtitle = subtitle ?: @"";
  content.body = body ?: @"";
  content.threadIdentifier = thread ?: @"";
  content.sound = sound ?: @"";
  content.userInfo = region.apnsDict;
  if (@available(iOS 15.0, *))
    content.interruptionLevel = 1;
  JPushNotificationTrigger *trigger = [JPushNotificationTrigger new];
  if (@available(iOS 10.0, *))
    trigger.timeInterval = 1;
  else {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    trigger.fireDate = [NSDate dateWithTimeIntervalSinceNow:1];
#pragma clang diagnostic pop
  }
  JPushNotificationRequest *request = [JPushNotificationRequest new];
  request.requestIdentifier = identifier;
  request.content = content;
  request.trigger = trigger;
  [JPUSHService addNotification:request];
}
+ (unsigned char)sameDayOrWeek:(double)first withTime:(double)second {
  NSDate *a = [NSDate dateWithTimeIntervalSince1970:first],
         *b = [NSDate dateWithTimeIntervalSince1970:second];
  NSCalendar *calendar = NSCalendar.currentCalendar;
  NSDateComponents *ac = [calendar components:0x1c fromDate:a],
                   *bc = [calendar components:0x1c fromDate:b];
  if (ac.day == bc.day && ac.month == bc.month && ac.year == bc.year)
    return 1;
  ac = [calendar components:0x2004 fromDate:a];
  bc = [calendar components:0x2004 fromDate:b];
  return ac.weekOfYear == bc.weekOfYear && ac.year == bc.year ? 2 : 0;
}
+ (id)triggerTimeListWith:(id)identifier {
  id info = [NSUserDefaults.standardUserDefaults objectForKey:TriggerTimeKey];
  return [info isKindOfClass:NSDictionary.class] ? info[identifier] : nil;
}
+ (void)recordTriggerTimeWith:(id)identifier {
  NSUserDefaults *defaults = NSUserDefaults.standardUserDefaults;
  id info = [defaults objectForKey:TriggerTimeKey];
  NSMutableDictionary *updated =
      [[NSMutableDictionary alloc] initWithDictionary:info];
  NSMutableArray *times =
      [[NSMutableArray alloc] initWithArray:info[identifier]];
  [times addObject:@([JCOREUtilities currentTimestamp])];
  [updated setObject:[times copy] forKey:identifier];
  [defaults setObject:[updated copy] forKey:TriggerTimeKey];
  [defaults synchronize];
}
+ (void)removeTriggerTimeWith:(id)identifier {
  NSUserDefaults *defaults = NSUserDefaults.standardUserDefaults;
  id info = [defaults objectForKey:TriggerTimeKey];
  if ([info isKindOfClass:NSDictionary.class]) {
    NSMutableDictionary *updated =
        [[NSMutableDictionary alloc] initWithDictionary:info];
    [updated removeObjectForKey:identifier];
    [defaults setObject:[updated copy] forKey:TriggerTimeKey];
    [defaults synchronize];
  }
}
+ (id)geofenceDictFrom:(JPUSHRegion *)region {
  if (!region)
    return nil;
  NSMutableDictionary *info = [NSMutableDictionary new];
  [info
      addEntriesFromDictionary:@{@"_j_geofence" : [self regionToJson:region]}];
  if (region.messageDict)
    [info addEntriesFromDictionary:@{@"message" : region.messageDict}];
  if (region.apnsDict)
    [info addEntriesFromDictionary:@{@"apns" : region.apnsDict}];
  return info;
}
+ (BOOL)detectRepeatTrigger:(id)value {
  if (![value isKindOfClass:JPUSHRegion.class])
    return NO;
  JPUSHRegion *region = value;
  if (!region.repeat)
    return YES;
  id times = [JPUSHGeofenceHelper triggerTimeListWith:region.identifier];
  double now = [JCOREUtilities currentTimestamp];
  double last = [[times lastObject] doubleValue];
  double interval = region.repeatTime > 0 ? region.repeatTime : 3600.0;
  if (interval > 0 && interval > now - last) {
    GEO_HELPER_LOG(YES, 0x100, 0xa2, @"repeatTime require %f at least, but %f",
                   interval, now - last);
    return NO;
  }
  int dayCount = 0, weekCount = 0;
  for (NSInteger i = [times count] - 1; i >= 0; i--) {
    unsigned char kind =
        [JPUSHGeofenceHelper sameDayOrWeek:[[times objectAtIndex:i] doubleValue]
                                  withTime:now];
    if (kind == 1) {
      dayCount++;
      weekCount++;
    }
    if (kind == 2)
      weekCount++;
  }
  if (region.weekRepeatNum > 0 && region.weekRepeatNum <= weekCount) {
    GEO_HELPER_LOG(YES, 0x100, 0xb5,
                   @"week repeat count arrive upper limit, %d",
                   region.weekRepeatNum);
    return NO;
  }
  if (region.dayRepeatNum > 0 && region.dayRepeatNum <= dayCount) {
    GEO_HELPER_LOG(YES, 0x100, 0xbb, @"day repeat count arrive upper limit, %d",
                   region.dayRepeatNum);
    return NO;
  }
  return YES;
}
+ (NSError *)geofenceLocationError:(JPUSHRegion *)region {
  if (!region)
    return [NSError errorWithDomain:@"JPushGeofenceDomaim"
                               code:7001
                           userInfo:@{
                             NSLocalizedDescriptionKey :
                                 @"geofence was't found in location service"
                           }];
  if (NSDate.date.timeIntervalSince1970 > region.expirationTime)
    return [NSError
        errorWithDomain:@"JPushGeofenceDomaim"
                   code:7000
               userInfo:@{NSLocalizedDescriptionKey : @"geofence has expired"}];
  return nil;
}
+ (id)getValidJPushRegion:(id)region
                    error:(NSError *__autoreleasing *)error
                 displayR:(id)display
                callbackR:(id)callback {
  id found = [self findJPushRegion:region displayR:display callbackR:callback];
  *error = [self geofenceLocationError:found];
  if (!found)
    GEO_HELPER_LOG(YES, 8, 0xd2, @"no find region, trigger cancel");
  else if ([self detectRepeatTrigger:found])
    return found;
  else
    GEO_HELPER_LOG(YES, 8, 0xd6,
                   @"Restricted by repeated settings, trigger cancel");
  return nil;
}
+ (id)findJPushRegion:(id)region displayR:(id)display callbackR:(id)callback {
  __block id found = nil;
  [display enumerateObjectsUsingBlock:^(id item, NSUInteger index, BOOL *stop) {
    if ([[item identifier] isEqualToString:[region identifier]]) {
      found = item;
      *stop = YES;
    }
  }];
  if (!found)
    [callback
        enumerateObjectsUsingBlock:^(id item, NSUInteger index, BOOL *stop) {
          if ([[item identifier] isEqualToString:[region identifier]]) {
            found = item;
            *stop = YES;
          }
        }];
  return found;
}
+ (id)geofenceCachePathWith:(id)name {
  NSString *documents = NSSearchPathForDirectoriesInDomains(
      NSDocumentDirectory, NSUserDomainMask, YES)[0];
  NSString *directory =
      [NSString stringWithFormat:@"%@/%@", documents, @"JpushDocument"];
  if (![NSFileManager.defaultManager fileExistsAtPath:directory])
    [NSFileManager.defaultManager createDirectoryAtPath:directory
                            withIntermediateDirectories:YES
                                             attributes:nil
                                                  error:NULL];
  return [NSString stringWithFormat:@"%@/%@", directory, name];
}
+ (id)displayGeofenceCachePath {
  return [self geofenceCachePathWith:@"JpushDisplayGeofenceData"];
}
+ (id)callbackGeofenceCachePath {
  return [self geofenceCachePathWith:@"JpushCallbackGeofenceData"];
}
+ (void)saveOrUpdateGeofenceMessage:(id)regions path:(id)path {
  __block NSMutableArray *items =
      [NSMutableArray arrayWithCapacity:[regions count]];
  [regions
      enumerateObjectsUsingBlock:^(id region, NSUInteger index, BOOL *stop) {
        id value = [JPUSHGeofenceHelper geofenceDictFrom:region];
        if (value)
          [items addObject:value];
      }];
  [items writeToFile:path atomically:YES];
}
+ (void)saveDisplayGeofence:(id)regions {
  [self saveOrUpdateGeofenceMessage:regions
                               path:[self displayGeofenceCachePath]];
}
+ (void)saveCallbackGeofence:(id)regions {
  [self saveOrUpdateGeofenceMessage:regions
                               path:[self callbackGeofenceCachePath]];
}
+ (id)creatRegionWith:(id)info {
  JPUSHRegion *region = [self generateRegionWith:info];
  if ([info isKindOfClass:NSDictionary.class]) {
    id message = info[@"message"];
    if ([message isKindOfClass:NSDictionary.class])
      region.messageDict = message;
    else {
      NSMutableDictionary *apns =
          [[NSMutableDictionary alloc] initWithDictionary:info];
      [apns removeObjectForKey:@"_j_geofence"];
      region.apnsDict = [apns copy];
    }
  }
  return region;
}
+ (id)transRegionWith:(id)info {
  JPUSHRegion *region = [self generateRegionWith:info];
  if ([info isKindOfClass:NSDictionary.class]) {
    region.messageDict = info[@"message"];
    region.apnsDict = info[@"apns"];
  }
  return region;
}
+ (id)generateRegionWith:(id)info {
  if ([[info allKeys] containsObject:@"_j_geofence"]) {
    id geo = [info objectForKey:@"_j_geofence"];
    if ([geo isKindOfClass:NSDictionary.class])
      return [self jsonToRegion:geo];
  } else
    GEO_HELPER_LOG(NO, 0x20, 0x13f, @"geoFenceInfo shouldn't be nil");
  return [JPUSHRegion new];
}
+ (id)regionToJson:(JPUSHRegion *)region {
  if (!region)
    return nil;
  NSDictionary *center = @{
    @"lon" : @(region.circularRegion.center.longitude),
    @"lat" : @(region.circularRegion.center.latitude)
  };
  NSString *status = @"";
  if (region.jpushFenceRegionStatus == 1)
    status = @"in";
  else if (region.jpushFenceRegionStatus == 2)
    status = @"out";
  else if (region.jpushFenceRegionStatus == 3)
    status = @"inside";
  return @{
    @"geofenceid" : region.identifier ?: @"",
    @"expiration" : @(region.expirationTime),
    @"center" : center,
    @"radius" : @(region.circularRegion.radius),
    @"status" : status,
    @"type" : @(region.type),
    @"repeat" : @(region.repeat),
    @"repeat_time" : @(region.repeatTime),
    @"repeat_week_num" : @(region.weekRepeatNum),
    @"repeat_day_num" : @(region.dayRepeatNum)
  };
}
+ (id)jsonToRegion:(id)info {
  JPUSHRegion *region = [JPUSHRegion new];
  id identifier = [info objectForKey:@"geofenceid"];
  region.identifier = identifier;
  region.expirationTime = [[info objectForKey:@"expiration"] integerValue];
  id center = [info objectForKey:@"center"];
  double radius = [[info objectForKey:@"radius"] doubleValue];
  if ([center isKindOfClass:NSDictionary.class]) {
    double lat = [[center objectForKey:@"lat"] doubleValue],
           lon = [[center objectForKey:@"lon"] doubleValue];
    region.circularRegion = [[CLCircularRegion alloc]
        initWithCenter:CLLocationCoordinate2DMake(lat, lon)
                radius:radius
            identifier:identifier];
  }
  id status = info[@"status"];
  region.jpushFenceRegionStatus = 0;
  if ([status isEqualToString:@"out"]) {
    region.jpushFenceRegionStatus = 2;
    region.circularRegion.notifyOnExit = YES;
    region.circularRegion.notifyOnEntry = NO;
  } else if ([status isEqualToString:@"in"] ||
             [status isEqualToString:@"inside"]) {
    region.jpushFenceRegionStatus = [status isEqualToString:@"in"] ? 1 : 3;
    region.circularRegion.notifyOnExit = NO;
    region.circularRegion.notifyOnEntry = YES;
  }
  region.type = [info[@"type"] intValue];
  region.repeat = [info[@"repeat"] boolValue];
  if (region.repeat) {
    region.repeatTime = [info[@"repeat_time"] doubleValue];
    region.dayRepeatNum = [info[@"repeat_day_num"] intValue];
    region.weekRepeatNum = [info[@"repeat_week_num"] intValue];
  }
  return region;
}
+ (id)getGeofenceCacheFromLocalPath:(id)path {
  NSMutableArray *entries = [NSMutableArray array];
  if ([NSFileManager.defaultManager fileExistsAtPath:path]) {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    [entries addObjectsFromArray:[NSMutableArray arrayWithContentsOfFile:path]];
#pragma clang diagnostic pop
  }
  NSMutableArray *regions = [NSMutableArray arrayWithCapacity:entries.count];
  for (NSUInteger i = 0; i < entries.count; i++) {
    JPUSHRegion *region = [self transRegionWith:entries[i]];
    if (region.circularRegion)
      [regions addObject:region];
  }
  return regions;
}
+ (id)getDisplayGeofenceCache {
  return [self getGeofenceCacheFromLocalPath:[self displayGeofenceCachePath]];
}
+ (id)getCallbackGeofenceCache {
  return [self getGeofenceCacheFromLocalPath:[self callbackGeofenceCachePath]];
}
@end
#endif
