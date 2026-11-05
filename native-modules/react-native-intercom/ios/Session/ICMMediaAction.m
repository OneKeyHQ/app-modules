#import "ICMMediaAction.h"
#import "ITBUpload.h"
@implementation ICMMediaAction
- (instancetype)initWithMediaURL:(NSURL *)url {
    self = [super init];
    if (self) _mediaURL = url;
    return self;
}
- (ITBUpload *)upload {
    if (_upload != nil) return _upload;
    if (_mediaURL == nil) return nil;
    _upload = [ITBUpload createUploadForFileURL:_mediaURL];
    return _upload;
}
- (NSArray *)blocks { return @[]; }
@end
