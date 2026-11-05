// Internal interface recovered from Intercom 19.9.1 metadata.
#import <Foundation/Foundation.h>
#import "ICMOreoShutdownState.h"
@interface ICMOreoErrorResponse : NSObject
@property (nonatomic, copy, readonly) NSString *type;
@property (nonatomic, copy, readonly) NSString *message;
@property (nonatomic, copy, readonly) NSString *code;
@property (nonatomic, readonly) NSError *error;
@property (nonatomic, readonly) ICMOreoShutdownState *oreoShutdownState;
+ (instancetype)instanceFromDictionary:(id)dictionary networkError:(NSError *)error;
- (instancetype)initWithType:(NSString *)type messsage:(NSString *)message code:(NSString *)code networkError:(NSError *)error shutdownState:(ICMOreoShutdownState *)state;
+ (NSError *)errorWithErrorMesssage:(NSString *)message networkError:(NSError *)error;
@end
