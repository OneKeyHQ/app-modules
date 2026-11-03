#pragma once
#import <Foundation/Foundation.h>
@interface JCommonAPNSStateEntity : NSObject {
    BOOL _providesAppNotification;
    NSUInteger _notificationCenter;
    NSUInteger _badge;
    NSUInteger _sound;
    NSUInteger _alert;
    NSInteger _authStatus;
    NSUInteger _lockScreen;
    NSUInteger _carPlay;
    NSUInteger _criticalAlert;
    NSInteger _alertStyle;
    NSUInteger _announcement;
}
@property(nonatomic) NSUInteger notificationCenter;
@property(nonatomic) NSUInteger badge;
@property(nonatomic) NSUInteger sound;
@property(nonatomic) NSUInteger alert;
@property(nonatomic) NSInteger authStatus;
@property(nonatomic) NSUInteger lockScreen;
@property(nonatomic) NSUInteger carPlay;
@property(nonatomic) NSUInteger criticalAlert;
@property(nonatomic) NSInteger alertStyle;
@property(nonatomic) BOOL providesAppNotification;
@property(nonatomic) NSUInteger announcement;
+ (void)currentState:(void (^)(JCommonAPNSStateEntity *state))completion;
@end
@interface JCommonApnsStateController : NSObject
+ (BOOL)isChanged:(JCommonAPNSStateEntity *)state;
+ (void)asyncHandleReport:(id)information;
+ (void)reportAPNSState:(NSUInteger)trigger;
+ (NSMutableDictionary *)infoWithEntity:(JCommonAPNSStateEntity *)state trigger:(NSUInteger)trigger;
@end
