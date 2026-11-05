// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
#import <Foundation/Foundation.h>
@protocol ICMFileUploadConfig <NSObject>
- (NSDictionary *)postParametersDictionary;
- (NSString *)uploadDestination;
- (NSString *)contentType;
@end
