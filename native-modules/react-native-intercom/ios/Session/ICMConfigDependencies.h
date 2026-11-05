// Objective-C aliases for the genuine Swift Config nested models.
#import <Foundation/Foundation.h>
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate13ConfigModules")))
@interface ICMRecoveredConfigModules : NSObject <NSSecureCoding>
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate18AttachmentSettings")))
__attribute__((swift_name("AttachmentSettings")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredAttachmentSettings : NSObject <NSSecureCoding>
@property (nonatomic, readonly) BOOL gifsEnabled;
@property (nonatomic, readonly) BOOL cameraEnabled;
@property (nonatomic, readonly) BOOL filesEnabled;
@property (nonatomic, readonly) BOOL mediaEnabled;
@property (nonatomic, readonly) BOOL voiceTranscriptionEnabled;
@property (nonatomic, readonly) NSInteger uploadSizeLimit;
@property (nonatomic, readonly) NSArray<NSString *> *allowedFileExtensions;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate29ConversationStateSyncSettings")))
__attribute__((swift_name("ConversationStateSyncSettings")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredConversationStateSyncSettings : NSObject <NSSecureCoding>
@property (nonatomic, readonly) BOOL enabled;
@property (nonatomic, readonly) double syncInterval;
@property (nonatomic, readonly) double startTimeout;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@end
