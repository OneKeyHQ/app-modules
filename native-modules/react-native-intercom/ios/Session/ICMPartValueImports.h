#import <Foundation/Foundation.h>
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate4Form")))
__attribute__((swift_name("Form")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredForm : NSObject
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@property (nonatomic, readonly) NSArray *formAttributes;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate12PartMetadata")))
__attribute__((swift_name("PartMetadata")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredPartMetadata : NSObject
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@end

__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate9EventData")))
__attribute__((swift_name("EventData")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredEventData : NSObject
- (BOOL)isValidEvent;
- (instancetype)initWithInstanceFrom:(NSDictionary *)dictionary;
@end

__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate11ReplyOption")))
__attribute__((swift_name("ReplyOption")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredReplyOption : NSObject
- (instancetype)initWithInstanceFrom:(NSDictionary *)dictionary;
@property (nonatomic, readonly) NSString *uuid;
@end

