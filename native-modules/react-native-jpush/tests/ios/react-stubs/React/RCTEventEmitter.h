#import <Foundation/Foundation.h>
#import <React/RCTBridge.h>
@interface RCTEventEmitter : NSObject <RCTBridgeModule>
@property (nonatomic, weak, nullable) RCTBridge *bridge;
- (NSArray<NSString *> *_Nullable)supportedEvents;
- (void)sendEventWithName:(NSString *_Nullable)name body:(id _Nullable)body;
- (void)startObserving;
- (void)stopObserving;
- (void)addListener:(NSString *_Nullable)eventName;
- (void)removeListeners:(double)count;
@end
