#import <React/RCTUITextField.h>
#import <XCTest/XCTest.h>
#import <objc/runtime.h>

static dispatch_semaphore_t OneKeyTestReadStarted;
static dispatch_semaphore_t OneKeyTestReleaseRead;

@interface UIPasteboard (OneKeyPasteCacheTests)
- (BOOL)onekey_test_hasImages;
@end

@implementation UIPasteboard (OneKeyPasteCacheTests)

- (BOOL)onekey_test_hasImages
{
  dispatch_semaphore_t releaseRead;
  dispatch_semaphore_t readStarted;
  @synchronized (UIPasteboard.class) {
    releaseRead = OneKeyTestReleaseRead;
    readStarted = OneKeyTestReadStarted;
  }
  if (!NSThread.isMainThread && releaseRead != nil) {
    dispatch_semaphore_signal(readStarted);
    dispatch_semaphore_wait(releaseRead, dispatch_time(DISPATCH_TIME_NOW, 5 * NSEC_PER_SEC));
  }
  return [self onekey_test_hasImages];
}

@end

@interface OneKeyTextInputPasteObserverTests : XCTestCase
@end

@implementation OneKeyTextInputPasteObserverTests

- (void)assertImagePasteAvailableWhileRefreshingAfterNotification:(NSNotificationName)name
{
  UIPasteboard *pasteboard = UIPasteboard.generalPasteboard;
  NSArray<NSDictionary<NSString *, id> *> *previousItems = pasteboard.items;
  RCTUITextField *field = [[RCTUITextField alloc] initWithFrame:CGRectZero];
  SEL paste = @selector(paste:);

  @try {
    pasteboard.items = @[];
    NSDate *deadline = [NSDate dateWithTimeIntervalSinceNow:5];
    while ([field canPerformAction:paste withSender:nil] && [deadline timeIntervalSinceNow] > 0) {
      [[NSRunLoop mainRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:0.01]];
    }
    XCTAssertFalse([field canPerformAction:paste withSender:nil]);

    @synchronized (UIPasteboard.class) {
      OneKeyTestReadStarted = dispatch_semaphore_create(0);
      OneKeyTestReleaseRead = dispatch_semaphore_create(0);
    }
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
      Method original = class_getInstanceMethod(UIPasteboard.class, @selector(hasImages));
      Method replacement = class_getInstanceMethod(UIPasteboard.class, @selector(onekey_test_hasImages));
      method_exchangeImplementations(original, replacement);
    });

    UIGraphicsImageRenderer *renderer = [[UIGraphicsImageRenderer alloc] initWithSize:CGSizeMake(1, 1)];
    pasteboard.image = [renderer imageWithActions:^(UIGraphicsImageRendererContext *context) {
      [UIColor.blackColor setFill];
      UIRectFill(CGRectMake(0, 0, 1, 1));
    }];
    XCTAssertEqual(dispatch_semaphore_wait(OneKeyTestReadStarted, dispatch_time(DISPATCH_TIME_NOW, 5 * NSEC_PER_SEC)), 0L);

    [NSNotificationCenter.defaultCenter postNotificationName:name object:field];
    XCTAssertTrue([field canPerformAction:paste withSender:nil]);
  } @finally {
    dispatch_semaphore_t releaseRead;
    @synchronized (UIPasteboard.class) {
      releaseRead = OneKeyTestReleaseRead;
      OneKeyTestReleaseRead = nil;
      OneKeyTestReadStarted = nil;
    }
    if (releaseRead != nil) {
      dispatch_semaphore_signal(releaseRead);
    }
    pasteboard.items = previousItems;
  }
}

- (void)testFirstFocusKeepsImagePasteAvailableDuringRefresh
{
  [self assertImagePasteAvailableWhileRefreshingAfterNotification:UITextFieldTextDidBeginEditingNotification];
}

- (void)testForegroundResumeKeepsImagePasteAvailableDuringRefresh
{
  [self assertImagePasteAvailableWhileRefreshingAfterNotification:UIApplicationDidBecomeActiveNotification];
}

@end
