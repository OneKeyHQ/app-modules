#pragma once
#import <Foundation/Foundation.h>
@interface JCOREHttpEventController : NSObject {
    BOOL _isReporting;
    NSMutableArray *_pendingHistoryList;
}
@property (strong, nonatomic) NSMutableArray *pendingHistoryList;
@property (nonatomic) BOOL isReporting;
- (void)reportHistoryList;
- (void)beginReport;
- (void)testConnectionWithHistoryData;
- (void)reportRemainingHistoryData;
- (void)endReportHistoryList;
@end
