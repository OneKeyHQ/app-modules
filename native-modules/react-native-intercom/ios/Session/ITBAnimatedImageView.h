#import <UIKit/UIKit.h>
#import "ICMAnimatedImageImports.h"
@class ITBImageLoader;
@interface ITBAnimatedImageView : UIImageView {
    CADisplayLink *_displayLink;
    double _playHead;
    double _lastFireTime;
    NSInteger _playedLoops;
    NSInteger _lastFrameIndex;
    ICMAnimatedImage *_animatedImage;
    ITBImageLoader *_imageLoader;
    NSUUID *_taskUUID;
    NSUUID *_currentLoadToken;
}
@property (nonatomic, strong) ICMAnimatedImage *animatedImage;
@property (nonatomic, strong) ITBImageLoader *imageLoader;
@property (nonatomic, strong) NSUUID *taskUUID;
@property (nonatomic, strong) NSUUID *currentLoadToken;
- (instancetype)initWithImageLoader:(ITBImageLoader *)loader;
- (void)loadImageWithURLString:(NSString *)url actionsBeforeNetworkLoad:(void (^)(void))before completion:(void (^)(void))completion;
- (void)loadImageWithURLString:(NSString *)url actionsBeforeNetworkLoad:(void (^)(void))before completion:(void (^)(void))completion animationOptions:(UIViewAnimationOptions)options;
- (void)cancelImageLoad;
- (void)displayLinkFired:(CADisplayLink *)link;
@end
@interface ITBAnimatedImageViewDisplayLinkProxy : NSObject {
    __weak ITBAnimatedImageView *_target;
}
- (instancetype)initWithTarget:(ITBAnimatedImageView *)target;
- (void)displayLinkFired:(CADisplayLink *)link;
@end
