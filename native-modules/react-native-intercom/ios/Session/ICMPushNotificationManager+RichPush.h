#import "ICMPushNotificationManager.h"
@interface ICMPushNotificationManager (OriginalRichPush)
+ (void)handleNotificationInForeground:(NSDictionary *)payload;
+ (void)handleIntercomRichPushNotificationContent:(UNMutableNotificationContent *)content withContentHandler:(void (^)(UNNotificationContent *))handler;
+ (void)downloadAvatarAndDeliverCommunicationNotification:(UNNotificationContent *)content imageURL:(NSURL *)imageURL authorName:(NSString *)authorName contentHandler:(void (^)(UNNotificationContent *))handler;
+ (void)downloadAndAttachMedia:(NSURL *)imageURL toContent:(UNNotificationContent *)content contentHandler:(void (^)(UNNotificationContent *))handler;
+ (void)deliverCommunicationNotification:(UNNotificationContent *)content authorName:(NSString *)authorName avatarLocationURL:(NSURL *)avatarURL contentHandler:(void (^)(UNNotificationContent *))handler;
+ (UNNotificationAttachment *)attachmentFromFileURL:(NSURL *)url;
@end
