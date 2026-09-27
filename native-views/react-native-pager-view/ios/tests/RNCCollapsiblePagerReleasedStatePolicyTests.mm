#import <XCTest/XCTest.h>

#import "RNCCollapsiblePagerReleasedStatePolicy.h"

@interface RNCCollapsiblePagerReleasedStatePolicyTests : XCTestCase
@end

@implementation RNCCollapsiblePagerReleasedStatePolicyTests

- (void)testRestorationAfterInactiveContentShrinkCannotReapplyStaleCache
{
  // The list already corrected to -44 while inactive, but the pager still
  // retains the old logical offset captured before rows were removed.
  CGFloat savedLogicalOffset = 513.67;
  CGFloat restored = RNCLimitRestoredScrollOffset(
    savedLogicalOffset - 108, 132, 708, 108, 532);
  XCTAssertEqualWithAccuracy(restored, -44, 0.001);
  XCTAssertEqualWithAccuracy(restored + 108, 64, 0.001);
  XCTAssertEqualWithAccuracy(
    RNCLimitRestoredScrollOffset(restored, 132, 708, 108, 532), restored, 0.001);
}

- (void)testRestorationPreservesValidDeepAndNegativeOffsets
{
  XCTAssertEqualWithAccuracy(
    RNCLimitRestoredScrollOffset(600, 2400, 708, 108, 0), 600, 0.001);
  XCTAssertEqualWithAccuracy(
    RNCLimitRestoredScrollOffset(-160, 132, 708, 108, 532), -160, 0.001);
  XCTAssertEqualWithAccuracy(
    RNCLimitRestoredScrollOffset(600, 0, 0, 108, 0), 600, 0.001);
}

- (void)testPrunesRemovedKeysAndKeepsStableKeysAcrossReorders
{
  NSObject *alpha = [NSObject new];
  NSObject *beta = [NSObject new];
  // A concrete value type in Objective-C++ mirrors the component view call site.
  NSMutableDictionary<NSString *, NSObject *> *releasedStates = [@{
    @"alpha": alpha,
    @"beta": beta,
  } mutableCopy];

  RNCPruneReleasedPageStates(releasedStates, @[@"beta", @"gamma"]);

  XCTAssertEqualObjects(releasedStates, (@{ @"beta": beta }));
}

- (void)testClearsEveryReleasedStateWhenAllPagesUnmount
{
  NSMutableDictionary<NSString *, id> *releasedStates = [@{
    @"page-0": [NSObject new],
    @"page-1": [NSObject new],
  } mutableCopy];

  RNCPruneReleasedPageStates(releasedStates, @[]);

  XCTAssertEqual(releasedStates.count, 0u);
}

@end
