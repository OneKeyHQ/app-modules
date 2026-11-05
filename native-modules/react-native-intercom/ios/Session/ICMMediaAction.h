#import "ICMTextAction.h"
@class ITBUpload;
@interface ICMMediaAction : NSObject <ICMAction> {
    NSURL *_mediaURL;
    ITBUpload *_upload;
}
@property (nonatomic, strong) NSURL *mediaURL;
@property (nonatomic, strong) ITBUpload *upload;
- (instancetype)initWithMediaURL:(NSURL *)url;
- (NSArray *)blocks;
@end
