#import <Foundation/Foundation.h>

#if __has_include(<React-RCTAppDelegate/RCTDefaultReactNativeFactoryDelegate.h>)
#import <React-RCTAppDelegate/RCTDefaultReactNativeFactoryDelegate.h>
#elif __has_include(<React_RCTAppDelegate/RCTDefaultReactNativeFactoryDelegate.h>)
#import <React_RCTAppDelegate/RCTDefaultReactNativeFactoryDelegate.h>
#endif

NS_ASSUME_NONNULL_BEGIN

@interface ReactNativeDelegate : RCTDefaultReactNativeFactoryDelegate
@end

NS_ASSUME_NONNULL_END
