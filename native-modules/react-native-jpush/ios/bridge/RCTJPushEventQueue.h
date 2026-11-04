#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface RCTJPushEventQueue : NSObject

+ (nonnull instancetype)sharedInstance;

@property (nonatomic, strong, nullable) NSMutableArray<NSDictionary *> *_notificationQueue;
@property (nonatomic, strong, nullable) NSMutableArray<NSDictionary *> *_localNotificationQueue;

@end

NS_ASSUME_NONNULL_END
