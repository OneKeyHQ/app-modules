// Exercises real restored request methods without creating or sending a data task.
#import <Foundation/Foundation.h>
#import <math.h>
#import <objc/runtime.h>
#import "ICMOreoEngine.h"

// Link-only dependency for this fixture; no identity singleton is exercised.
@interface ICMIdentityStore : NSObject
+ (id)currentIdentity;
@end
@implementation ICMIdentityStore
+ (id)currentIdentity { return nil; }
@end

@interface FixtureRequestEngine : ICMOreoEngine
@end
@implementation FixtureRequestEngine
- (NSURL *)baseURL { return [NSURL URLWithString:@"https://synthetic.invalid/messenger/mobile/"]; }
- (NSString *)random16CharacterHexString { return @"synthetic-idempotency"; }
@end

static NSMutableArray *passed;
static NSData *serializationFailure(id receiver, SEL selector, id object, NSJSONWritingOptions options, NSError **error) {
    (void)receiver; (void)selector; (void)object; (void)options;
    if (error) *error = [NSError errorWithDomain:NSCocoaErrorDomain code:3840 userInfo:@{NSLocalizedDescriptionKey: @"synthetic serializer failure"}];
    return nil;
}
static void check(BOOL condition, NSString *name) {
    if (!condition) { fprintf(stderr, "FAIL: %s\n", name.UTF8String); exit(1); }
    [passed addObject:name];
}
int main(void) {
    @autoreleasepool {
        passed = [NSMutableArray new];
        check([ICMOreoEngine userJwtInParameters:nil] == nil && [ICMOreoEngine userJwtInParameters:@[]] == nil, @"JWT extraction requires dictionary");
        check([ICMOreoEngine userJwtInParameters:@{@"intercom_user_jwt": @42}] == nil && [ICMOreoEngine userJwtInParameters:@{@"intercom_user_jwt": @""}] == nil, @"JWT extraction requires nonempty string");
        NSMutableString *jwt = [NSMutableString stringWithString:@"synthetic-jwt"];
        check([ICMOreoEngine userJwtInParameters:@{@"intercom_user_jwt": jwt}] == jwt, @"JWT extraction retains original string");
        NSDictionary *parameters = @{@"intercom_user_jwt": @"old", @"other": @1};
        check([[ICMOreoEngine parameters:parameters replacingUserJwt:@"new"] isEqual:@{@"intercom_user_jwt": @"new", @"other": @1}] && [parameters[@"intercom_user_jwt"] isEqual:@"old"], @"JWT replacement copies original parameters");
        check([[ICMOreoEngine parameters:parameters replacingUserJwt:nil] isEqual:@{@"other": @1}], @"nil JWT replacement removes key");
        check([ICMOreoEngine parameters:nil replacingUserJwt:@"new"] == nil, @"nil parameters stay nil");
        check([ICMOreoEngine shouldRetryRequestURL:@"/messenger/mobile/users"] && [ICMOreoEngine shouldRetryRequestURL:@"/messenger/mobile/events"] && [ICMOreoEngine shouldRetryRequestURL:@"/messenger/mobile/device_tokens"], @"original three retry eligible paths");
        check(![ICMOreoEngine shouldRetryRequestURL:@"https://synthetic.invalid/messenger/mobile/users"] && ![ICMOreoEngine shouldRetryRequestURL:@"/messenger/mobile/users/"] && ![ICMOreoEngine shouldRetryRequestURL:nil], @"retry eligibility is exact path equality");
        check([ICMOreoEngine backOffTimeForReplyNumber:0] == 1 && [ICMOreoEngine backOffTimeForReplyNumber:3] == 8 && [ICMOreoEngine backOffTimeForReplyNumber:-1] == 0.5, @"backoff is exp2 without nonnegative clamp");
        check(isinf([ICMOreoEngine backOffTimeForReplyNumber:1024]), @"original backoff is not bounded");
        NSError *ignored = [ICMOreoEngine ignoredResponseError];
        check([ignored.domain isEqual:@"Network request succeeded but the response was ignored"] && ignored.code == 0 && ignored.userInfo.count == 0, @"ignored response error uses original domain");
        FixtureRequestEngine *engine = [FixtureRequestEngine new];
        engine.httpHeaderFields = [@{@"Content-Type": @"application/json", @"Idempotency-Key": @"existing"} mutableCopy];
        [engine setAuthorisationHeaderForAppID:@"synthetic-app" apiKey:@"synthetic-key"];
        check([engine.httpHeaderFields[@"Authorization"] isEqual:@"Basic c3ludGhldGljLWFwcDpzeW50aGV0aWMta2V5"], @"Basic authorization encodes appId colon apiKey");
        __block NSUInteger failures = 0;
        NSMutableURLRequest *request = [engine createRequest:@"POST" urlString:@"users" parameters:@{@"synthetic": @1} failure:^(NSURLResponse *response, NSError *error) { (void)response; (void)error; failures++; }];
        check([request.URL.absoluteString isEqual:@"https://synthetic.invalid/messenger/mobile/users"] && [request.HTTPMethod isEqual:@"POST"], @"request resolves relative URL and HTTP method");
        check([request.allHTTPHeaderFields[@"Authorization"] isEqual:engine.httpHeaderFields[@"Authorization"]] && [request.allHTTPHeaderFields[@"Idempotency-Key"] containsString:@"existing"] && [request.allHTTPHeaderFields[@"Idempotency-Key"] containsString:@"synthetic-idempotency"], @"request copies headers and appends idempotency value");
        check([[NSJSONSerialization JSONObjectWithData:request.HTTPBody options:0 error:nil] isEqual:@{@"synthetic": @1}] && failures == 0, @"request JSON body serialization");
        NSMutableURLRequest *withoutBody = [engine createRequest:@"GET" urlString:@"https://other-synthetic.invalid/path" parameters:nil failure:nil];
        check(withoutBody.HTTPBody == nil && [withoutBody.URL.host isEqual:@"other-synthetic.invalid"], @"nil body omitted and absolute URL preserves original permissiveness");
        __block NSError *serializationError;
        __block NSURLResponse *serializationResponse;
        BOOL threw = NO;
        @try {
            [engine createRequest:@"POST" urlString:@"events" parameters:@{@"nonfinite": @(INFINITY)} failure:nil];
        } @catch (NSException *exception) {
            threw = [exception.name isEqual:NSInvalidArgumentException];
        }
        check(threw, @"invalid JSON preserves original uncaught serializer exception");
        Method serializer = class_getClassMethod([NSJSONSerialization class], @selector(dataWithJSONObject:options:error:));
        IMP originalSerializer = method_setImplementation(serializer, (IMP)serializationFailure);
        NSMutableURLRequest *invalid = [engine createRequest:@"POST" urlString:@"events" parameters:@{@"synthetic": @1} failure:^(NSURLResponse *response, NSError *error) { serializationResponse = response; serializationError = error; failures++; }];
        method_setImplementation(serializer, originalSerializer);
        check(invalid == nil && serializationError != nil && serializationResponse == nil && failures == 1, @"invalid JSON fails with original two argument callback");
        NSMutableURLRequest *multipart = [engine multipartRequest:@"attachments" parameters:@{@"number": @42, @"nil": [NSNull null]} fileData:[NSData dataWithBytes:"\0\377" length:2] fileName:@"fixture" mimeType:@"x/y" fieldName:@"upload"];
        check([multipart.URL.absoluteString isEqual:@"https://synthetic.invalid/messenger/mobile/attachments"] && [multipart.HTTPMethod isEqual:@"POST"], @"multipart Engine resolves URL and uses POST");
        NSString *multipartText = [[NSString alloc] initWithData:multipart.HTTPBody encoding:NSISOLatin1StringEncoding];
        check([multipartText containsString:@"42\r\n"] && [multipartText containsString:@"<null>\r\n"], @"multipart Engine converts arbitrary parameter values with description");
        check([multipart.allHTTPHeaderFields[@"Authorization"] isEqual:engine.httpHeaderFields[@"Authorization"]] && [multipart.allHTTPHeaderFields[@"Idempotency-Key"] containsString:@"synthetic-idempotency"], @"multipart Engine preserves headers and appends idempotency");
        check([multipart.allHTTPHeaderFields[@"Content-Length"] integerValue] == (NSInteger)multipart.HTTPBody.length && [multipartText containsString:@"name=\"upload\"; filename=\"fixture\""], @"multipart Engine forwards exact body and field name");
        ICMOreoEngine *randomEngine = [ICMOreoEngine new];
        NSString *random8 = randomEngine.random8CharacterHexString;
        NSString *random16 = randomEngine.random16CharacterHexString;
        NSCharacterSet *nonHex = [[NSCharacterSet characterSetWithCharactersInString:@"0123456789abcdef"] invertedSet];
        check(random8.length >= 2 && random8.length <= 8 && [random8 rangeOfCharacterFromSet:nonHex].location == NSNotFound && random16.length >= 4 && random16.length <= 16 && [random16 rangeOfCharacterFromSet:nonHex].location == NSNotFound, @"original random helpers preserve variable-width lowercase hexadecimal");
        NSDictionary *receipt = @{@"status": @"passed", @"testCount": @(passed.count), @"tests": passed, @"scope": @"Oreo request helper subset only; no NSURLSession task created; original differential pending"};
        NSData *json = [NSJSONSerialization dataWithJSONObject:receipt options:NSJSONWritingPrettyPrinted error:nil];
        fwrite(json.bytes, 1, json.length, stdout); fputc('\n', stdout);
    }
    return 0;
}
