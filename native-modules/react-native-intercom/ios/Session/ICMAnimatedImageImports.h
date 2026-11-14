// Objective-C entries implemented by the recovered AnimatedImage.swift.
#import <UIKit/UIKit.h>
__attribute__((objc_runtime_name("ICMAnimatedImage")))
__attribute__((swift_name("AnimatedImage")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMAnimatedImage : NSObject
@property (nonatomic, readonly) UIImage *coverImage;
@property (nonatomic, readonly) CGSize size;
@property (nonatomic, readonly) NSInteger frameCount;
@property (nonatomic, readonly) NSInteger loopCount;
@property (nonatomic, readonly) double duration;
@property (nonatomic, readonly) NSInteger preferredFrameRate;
- (void)prepareForPlayback;
- (NSInteger)frameIndexAtPlayHead:(double)head;
- (UIImage *)frameAtIndex:(NSInteger)index;
@end
