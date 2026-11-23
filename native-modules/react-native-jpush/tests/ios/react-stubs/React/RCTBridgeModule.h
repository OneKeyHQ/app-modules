#import <Foundation/Foundation.h>
typedef void (^RCTResponseSenderBlock)(NSArray *response);
@class RCTBridge;
@protocol RCTBridgeModule <NSObject>
@optional
@property (nonatomic, weak, readonly) RCTBridge *bridge;
@end
#define RCT_EXPORT_MODULE(...)
#define RCT_EXPORT_METHOD(method) - (void)method
