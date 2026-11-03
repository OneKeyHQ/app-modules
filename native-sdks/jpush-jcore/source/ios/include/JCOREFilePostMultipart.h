#pragma once
#import <Foundation/Foundation.h>
typedef void (^JCOREFilePostCompletion)(NSData *data, NSURLResponse *response, NSError *error);
@interface JCOREFilePostMultipart : NSObject {
    NSMutableURLRequest *_request;
    NSString *_urlString;
    NSMutableDictionary *_headers;
    NSMutableDictionary *_fields;
    NSMutableDictionary *_files;
}
@property(strong, nonatomic) NSMutableURLRequest *request;
@property(strong, nonatomic) NSString *urlString;
@property(strong, nonatomic) NSMutableDictionary *headers;
@property(strong, nonatomic) NSMutableDictionary *fields;
@property(strong, nonatomic) NSMutableDictionary *files;
- (instancetype)initWithUrl:(NSString *)url;
- (void)addHeader:(id)name value:(id)value;
- (void)addFormField:(id)name value:(id)value;
- (void)addFormFile:(id)name path:(id)path;
- (void)finish:(JCOREFilePostCompletion)completion;
- (NSString *)getMimeType:(NSString *)extension;
@end
