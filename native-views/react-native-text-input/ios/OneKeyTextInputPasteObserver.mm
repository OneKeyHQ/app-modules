#import "OneKeyTextInputPasteObserver.h"

#import <React/RCTBridgeModule.h>
#import <React/RCTUITextField.h>
#import <React/RCTUITextView.h>
#import <React/UIView+React.h>
#import <UniformTypeIdentifiers/UniformTypeIdentifiers.h>
#import <objc/runtime.h>

static NSString *const OneKeyTextInputPasteEvent = @"OneKeyTextInputPaste";
static __weak OneKeyTextInputPasteObserver *OneKeyPasteObserver = nil;
static BOOL OneKeyPasteObserverHasListeners = NO;
static const void *OneKeyPasteInFlightKey = &OneKeyPasteInFlightKey;

@implementation OneKeyTextInputPasteObserver

RCT_EXPORT_MODULE(OneKeyTextInputPasteObserver)

+ (BOOL)requiresMainQueueSetup
{
  return YES;
}

- (instancetype)init
{
  if (self = [super init]) {
    OneKeyPasteObserver = self;
  }
  return self;
}

- (NSArray<NSString *> *)supportedEvents
{
  return @[ OneKeyTextInputPasteEvent ];
}

- (void)startObserving
{
  OneKeyPasteObserverHasListeners = YES;
}

- (void)stopObserving
{
  OneKeyPasteObserverHasListeners = NO;
}

+ (void)emitPasteForTarget:(NSInteger)target type:(NSString *)type data:(NSString *)data
{
  NSCharacterSet *whitespace = NSCharacterSet.whitespaceAndNewlineCharacterSet;
  if (target <= 0 ||
      [type stringByTrimmingCharactersInSet:whitespace].length == 0 ||
      [data stringByTrimmingCharactersInSet:whitespace].length == 0) {
    return;
  }
  dispatch_async(dispatch_get_main_queue(), ^{
    if (!OneKeyPasteObserverHasListeners || OneKeyPasteObserver == nil) {
      return;
    }
    [OneKeyPasteObserver sendEventWithName:OneKeyTextInputPasteEvent
                                      body:@{
                                        @"target" : @(target),
                                        @"items" : @[ @{ @"type" : type, @"data" : data } ],
                                      }];
  });
}

@end

static NSInteger OneKeyReactTagForBackedTextInput(UIView *view)
{
  for (UIView *candidate = view; candidate != nil; candidate = candidate.superview) {
    if (candidate.reactTag != nil) {
      return candidate.reactTag.integerValue;
    }
    if (candidate.tag > 0 && [NSStringFromClass(candidate.class) containsString:@"TextInputComponentView"]) {
      return candidate.tag;
    }
  }
  return 0;
}

static void OneKeyEmitPaste(UIView *view, NSString *type, NSString *data)
{
  [OneKeyTextInputPasteObserver emitPasteForTarget:OneKeyReactTagForBackedTextInput(view) type:type data:data];
}

static BOOL OneKeyProcessImagePaste(UIView *view, UIPasteboard *pasteboard, dispatch_block_t fallbackPaste)
{
  if ([objc_getAssociatedObject(view, OneKeyPasteInFlightKey) boolValue]) {
    return YES;
  }

  for (NSItemProvider *itemProvider in pasteboard.itemProviders) {
    for (NSString *identifier in itemProvider.registeredTypeIdentifiers) {
      UTType *type = [UTType typeWithIdentifier:identifier];
      if (type == nil || ![type conformsToType:UTTypeImage]) {
        continue;
      }

      objc_setAssociatedObject(view, OneKeyPasteInFlightKey, @YES, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
      NSString *clipboardString = pasteboard.hasStrings ? pasteboard.string : nil;
      NSInteger target = OneKeyReactTagForBackedTextInput(view);
      [itemProvider loadDataRepresentationForTypeIdentifier:identifier
                                          completionHandler:^(NSData *data, NSError *error) {
        NSString *mimeType = type.preferredMIMEType ?: @"application/octet-stream";
        NSString *fileExtension = type.preferredFilenameExtension ?: @"bin";
        NSString *fileName = [NSString stringWithFormat:@"%@.%@", NSUUID.UUID.UUIDString, fileExtension];
        NSString *filePath = [NSTemporaryDirectory() stringByAppendingPathComponent:fileName];
        BOOL writeSucceeded = error == nil && data != nil && [data writeToFile:filePath atomically:YES];

        dispatch_async(dispatch_get_main_queue(), ^{
          objc_setAssociatedObject(view, OneKeyPasteInFlightKey, @NO, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
          if (writeSucceeded) {
            [OneKeyTextInputPasteObserver emitPasteForTarget:target
                                                       type:mimeType
                                                       data:[NSURL fileURLWithPath:filePath].absoluteString];
          } else {
            if (clipboardString.length > 0) {
              [OneKeyTextInputPasteObserver emitPasteForTarget:target type:@"text/plain" data:clipboardString];
            }
            fallbackPaste();
          }
        });
      }];
      return YES;
    }
  }
  return NO;
}

static void OneKeySwizzle(Class cls, SEL originalSelector, SEL replacementSelector)
{
  Method originalMethod = class_getInstanceMethod(cls, originalSelector);
  Method replacementMethod = class_getInstanceMethod(cls, replacementSelector);
  if (originalMethod != nil && replacementMethod != nil) {
    method_exchangeImplementations(originalMethod, replacementMethod);
  }
}

@implementation RCTUITextField (OneKeyPaste)

+ (void)load
{
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    OneKeySwizzle(self, @selector(paste:), @selector(onekey_paste:));
    OneKeySwizzle(self, @selector(canPerformAction:withSender:), @selector(onekey_canPerformAction:withSender:));
  });
}

- (void)onekey_paste:(id)sender
{
  UIPasteboard *pasteboard = UIPasteboard.generalPasteboard;
  if (pasteboard.hasImages && OneKeyProcessImagePaste(self, pasteboard, ^{ [self onekey_paste:nil]; })) {
    return;
  }
  if (pasteboard.hasStrings) {
    OneKeyEmitPaste(self, @"text/plain", pasteboard.string);
  }
  [self onekey_paste:sender];
}

- (BOOL)onekey_canPerformAction:(SEL)action withSender:(id)sender
{
  if (action == @selector(paste:) && UIPasteboard.generalPasteboard.hasImages) {
    return YES;
  }
  return [self onekey_canPerformAction:action withSender:sender];
}

@end

@implementation RCTUITextView (OneKeyPaste)

+ (void)load
{
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    OneKeySwizzle(self, @selector(paste:), @selector(onekey_paste:));
    OneKeySwizzle(self, @selector(canPerformAction:withSender:), @selector(onekey_canPerformAction:withSender:));
  });
}

- (void)onekey_paste:(id)sender
{
  UIPasteboard *pasteboard = UIPasteboard.generalPasteboard;
  if (pasteboard.hasImages && OneKeyProcessImagePaste(self, pasteboard, ^{ [self onekey_paste:nil]; })) {
    return;
  }
  if (pasteboard.hasStrings) {
    OneKeyEmitPaste(self, @"text/plain", pasteboard.string);
  }
  [self onekey_paste:sender];
}

- (BOOL)onekey_canPerformAction:(SEL)action withSender:(id)sender
{
  if (action == @selector(paste:) && UIPasteboard.generalPasteboard.hasImages) {
    return YES;
  }
  return [self onekey_canPerformAction:action withSender:sender];
}

@end
