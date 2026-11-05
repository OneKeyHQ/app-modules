#import <Foundation/Foundation.h>
// Original Objective-C entry points. The actual LinkOpener implementation is still required.
__attribute__((swift_name("LinkOpener")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMLinkOpener : NSObject
+ (instancetype)shared;
- (void)openLink:(NSURL *)url;
- (BOOL)isUniversalLink:(NSURL *)url;
- (BOOL)isHelpCenterLink:(NSURL *)url;
@end
