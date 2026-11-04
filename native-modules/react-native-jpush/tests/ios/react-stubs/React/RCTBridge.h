#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
@interface RCTBridge : NSObject
@property (nonatomic, readonly, nullable) NSDictionary *launchOptions;
- (void)enqueueJSCall:(NSString *_Nullable)module method:(NSString *_Nullable)method args:(NSArray *_Nullable)args completion:(dispatch_block_t _Nullable)completion;
@end
