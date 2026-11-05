#pragma once
#import <Foundation/Foundation.h>
@protocol ICMScreenDelegate <NSObject>
- (void)goToNextScreenFromContext:(NSInteger)context;
- (void)completeCarouselFromContext:(NSInteger)context;
- (BOOL)shouldAutoProgressScreen;
- (void)permissionWasGrantedForScreen:(NSString *)identifier;
- (void)actionButtonWasTappedForScreen:(NSString *)identifier;
@end
