#import "JCOREFilePostMultipart.h"
#import <MobileCoreServices/MobileCoreServices.h>
@implementation JCOREFilePostMultipart
- (instancetype)initWithUrl:(NSString *)url {
    self.request = [[NSMutableURLRequest alloc] init];
    self.urlString = url;
    self.headers = [[NSMutableDictionary alloc] init];
    self.fields = [[NSMutableDictionary alloc] init];
    self.files = [[NSMutableDictionary alloc] init];
    return self;
}
- (void)addHeader:(id)name value:(id)value {
    [self.headers setObject:value forKey:name];
}
- (void)addFormField:(id)name value:(id)value {
    [self.fields setObject:value forKey:name];
}
- (void)addFormFile:(id)name path:(id)path {
    [self.files setObject:path forKey:name];
}
- (void)finish:(JCOREFilePostCompletion)completion {
    NSURL *url = [[NSURL alloc] initWithString:self.urlString];
    self.request.URL = url;
    self.request.HTTPMethod = @"POST";
    for (id name in self.headers)
        [self.request setValue:self.headers[name] forHTTPHeaderField:name];
    NSString *type =
        [NSString stringWithFormat:@"multipart/form-data; boundary=%@", @"unique-consistent-string"];
    [self.request setValue:type forHTTPHeaderField:@"Content-Type"];
    NSMutableData *body = [NSMutableData data];
    for (id name in self.fields) {
        id value = self.fields[name];
        [body appendData:[[NSString stringWithFormat:@"--%@\r\n", @"unique-consistent-string"]
                             dataUsingEncoding:NSUTF8StringEncoding]];
        [body appendData:[[NSString stringWithFormat:@"Content-Disposition: form-data; name=\"%@\"\r\n\r\n",
                                                     name] dataUsingEncoding:NSUTF8StringEncoding]];
        [body
            appendData:[[NSString stringWithFormat:@"%@\r\n", value] dataUsingEncoding:NSUTF8StringEncoding]];
    }
    for (id name in self.files) {
        id path = self.files[name];
        id filename = [path lastPathComponent];
        [body appendData:[[NSString stringWithFormat:@"--%@\r\n", @"unique-consistent-string"]
                             dataUsingEncoding:NSUTF8StringEncoding]];
        [body appendData:[[NSString stringWithFormat:
                                        @"Content-Disposition: form-data; name=\"%@\"; filename=\"%@\"\r\n",
                                        name, filename] dataUsingEncoding:NSUTF8StringEncoding]];
        [body appendData:[[NSString stringWithFormat:@"Content-Type: %@\r\n\r\n", type]
                             dataUsingEncoding:NSUTF8StringEncoding]];
        [body appendData:[[NSString stringWithFormat:@"Content-Transfer-Encoding: %@\r\n\r\n", @"binary"]
                             dataUsingEncoding:NSUTF8StringEncoding]];
        [body appendData:[NSData dataWithContentsOfFile:path]];
        [body appendData:[[NSString stringWithFormat:@"\r\n"] dataUsingEncoding:NSUTF8StringEncoding]];
    }
    if (body.length) {
        [body appendData:[[NSString stringWithFormat:@"--%@--\r\n", @"unique-consistent-string"]
                             dataUsingEncoding:NSUTF8StringEncoding]];
        self.request.HTTPBody = body;
        [self.request setValue:[NSString stringWithFormat:@"%ld", (long)body.length]
            forHTTPHeaderField:@"Content-Length"];
    }
    [[NSURLSession.sharedSession
        dataTaskWithRequest:self.request
          completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
            if (completion)
                completion(data, response, error);
          }] resume];
}
// Preserve the original deprecated UTI lookup and ownership of its MIME result.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
- (NSString *)getMimeType:(NSString *)extension {
    CFStringRef identifier = UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension,
                                                                   (__bridge CFStringRef)extension, NULL);
    return CFBridgingRelease(UTTypeCopyPreferredTagWithClass(identifier, kUTTagClassMIMEType));
}
#pragma clang diagnostic pop
@end
