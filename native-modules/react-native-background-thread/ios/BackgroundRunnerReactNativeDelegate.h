#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#if __has_include(<React-RCTAppDelegate/RCTDefaultReactNativeFactoryDelegate.h>)
#import <React-RCTAppDelegate/RCTDefaultReactNativeFactoryDelegate.h>
#elif __has_include(<React_RCTAppDelegate/RCTDefaultReactNativeFactoryDelegate.h>)
#import <React_RCTAppDelegate/RCTDefaultReactNativeFactoryDelegate.h>
#else
#import "RCTDefaultReactNativeFactoryDelegate.h"
#endif

#import <React/RCTComponent.h>

#include <string>
#include <vector>

NS_ASSUME_NONNULL_BEGIN

@interface BackgroundReactNativeDelegate : RCTDefaultReactNativeFactoryDelegate

@property (nonatomic, assign) BOOL hasOnMessageHandler;
@property (nonatomic, assign) BOOL hasOnErrorHandler;

@property (nonatomic, readwrite) std::string jsBundleSource;

@property (nonatomic, readwrite) std::string origin;

/**
 * Initializes the delegate.
 * @return Initialized delegate instance with filtered module access
 */
- (instancetype)init;

/**
 * Register a HBC segment in the background runtime (Phase 2.5 spike).
 * Uses RCTInstance's registerSegmentWithId:path: API.
 * Must be called after hostDidStart: has completed.
 */
- (BOOL)registerSegmentWithId:(NSNumber *)segmentId path:(NSString *)path;

@end

NS_ASSUME_NONNULL_END
