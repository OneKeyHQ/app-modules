#import <Foundation/Foundation.h>

#if __has_include(<React/RCTBridge.h>)
#import <React/RCTEventEmitter.h>
#import <React/RCTBridge.h>
#elif __has_include("RCTBridge.h")
#import "RCTEventEmitter.h"
#import "RCTBridge.h"
#endif

@interface RCTJCoreModule : RCTEventEmitter <RCTBridgeModule>

@end
