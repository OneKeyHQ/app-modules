#pragma once
#import <Foundation/Foundation.h>

@interface JCOREDataControlManager : NSObject {
    NSMutableArray *_controls;
}
@property(nonatomic,strong) NSMutableArray *controls;
+ (instancetype)shared;
+ (instancetype)allocWithZone:(NSZone *)zone;
- (id)copyWithZone:(NSZone *)zone;
- (void)setDataControl:(id)control;
@end
