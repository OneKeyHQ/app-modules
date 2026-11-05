#import <UIKit/UIKit.h>
#import "ITBBlock.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate28OutboundContentBlockDelegate")))
__attribute__((swift_name("OutboundContentBlockDelegate")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface OutboundContentBlockDelegate : NSObject @end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate20BlockViewFactoryObjC")))
__attribute__((swift_name("BlockViewFactoryObjC")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface BlockViewFactoryObjC : NSObject
+ (UIView *)viewFor:(ITBBlock *)block displayContext:(NSInteger)context blockDelegate:(OutboundContentBlockDelegate *)delegate;
@end
