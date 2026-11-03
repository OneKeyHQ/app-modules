#pragma once
#import "JPUSHService.h"

@interface JPushLiveActivityTokenVO : NSObject {
    NSUInteger _reportType;
    NSString *_liveActivityAlias;
    NSData *_pushToken;
    NSString *_attributesType;
    NSData *_pushToStartToken;
    NSInteger _seq;
    JPUSHLiveActivityTokenCompletion _completion;
}
@property (nonatomic) NSUInteger reportType;
@property (nonatomic, copy) NSString *liveActivityAlias;
@property (nonatomic, strong) NSData *pushToken;
@property (nonatomic, copy) NSString *attributesType;
@property (nonatomic, strong) NSData *pushToStartToken;
@property (nonatomic) NSInteger seq;
@property (nonatomic, copy) JPUSHLiveActivityTokenCompletion completion;
@end
