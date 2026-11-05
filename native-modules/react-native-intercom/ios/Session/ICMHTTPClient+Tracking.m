#import "ICMHTTPClient.h"
#import "ICMEngine.h"
@implementation ICMHTTPClient (Tracking)
+ (void)trackURL:(NSString *)string {
    ICMEngine *engine = [ICMEngine new];
    NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:string]];
    // The original tracking failure block intentionally has an empty body.
    [engine requestWithoutRedirects:request success:nil failure:^(NSError *error) { (void)error; }];
}
@end
