#import <React/RCTEventEmitter.h>

NS_ASSUME_NONNULL_BEGIN

@interface OneKeyTextInputPasteObserver : RCTEventEmitter

+ (void)emitPasteForTarget:(NSInteger)target type:(NSString *)type data:(NSString *)data;

@end

NS_ASSUME_NONNULL_END
