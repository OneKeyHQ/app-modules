#import <XCTest/XCTest.h>

#import "RNCCollapsiblePagerReleasedStatePolicy.h"

@interface RNCCollapsiblePagerReleasedStatePolicyTests : XCTestCase
@end

@implementation RNCCollapsiblePagerReleasedStatePolicyTests

- (void)testPrunesRemovedKeysAndKeepsStableKeysAcrossReorders
{
  NSObject *alpha = [NSObject new];
  NSObject *beta = [NSObject new];
  NSMutableDictionary<NSString *, id> *releasedStates = [@{
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

  XCTAssertEqual(releasedStates.count, 0);
}

@end
