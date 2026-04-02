#import <AsyncStorageSpec/AsyncStorageSpec.h>

@interface AsyncStorage : NativeRNCAsyncStorageSpecBase <NativeRNCAsyncStorageSpec>

- (void)multiGet:(NSArray<NSString *> *)keys
         resolve:(RCTPromiseResolveBlock)resolve
          reject:(RCTPromiseRejectBlock)reject;

- (void)multiSet:(NSArray<NSArray<NSString *> *> *)keyValuePairs
         resolve:(RCTPromiseResolveBlock)resolve
          reject:(RCTPromiseRejectBlock)reject;

- (void)multiRemove:(NSArray<NSString *> *)keys
            resolve:(RCTPromiseResolveBlock)resolve
             reject:(RCTPromiseRejectBlock)reject;

- (void)multiMerge:(NSArray<NSArray<NSString *> *> *)keyValuePairs
           resolve:(RCTPromiseResolveBlock)resolve
            reject:(RCTPromiseRejectBlock)reject;

- (void)getAllKeys:(RCTPromiseResolveBlock)resolve
           reject:(RCTPromiseRejectBlock)reject;

- (void)clear:(RCTPromiseResolveBlock)resolve
       reject:(RCTPromiseRejectBlock)reject;

@end
