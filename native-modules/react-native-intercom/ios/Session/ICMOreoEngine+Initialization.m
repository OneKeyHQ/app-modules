// SOURCE_RESTORED_UNLINKED: the complete identity/refresher/Bridge closure is missing.
#import "ICMOreoEngine.h"
#import "ICMAppInstallationFingerprint.h"
#import "../Identity/ICMBaseURLConfiguration.h"
#import "../Identity/ICMUtils.h"
@interface ICMTranslationManager : NSObject
+ (NSArray<NSString *> *)supportedLocales;
@end

@implementation ICMOreoEngine (Initialization)
- (instancetype)initWithApiKey:(NSString *)apiKey appId:(NSString *)appId {
    self = [self initWithBaseURL:[ICMBaseURLConfiguration baseUrlForAppId:appId]];
    if (self && apiKey.length && appId.length) [self setAuthorisationHeaderForAppID:appId apiKey:apiKey];
    return self;
}
- (instancetype)initWithBaseURL:(NSURL *)url {
    if ((self = [super init])) {
        _baseURL = url;
        _session = [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration] delegate:self delegateQueue:nil];
        _appFingerprint = [ICMAppInstallationFingerprint new];
        _httpHeaderFields = [NSMutableDictionary new];
        _userJwtRefresher = [ICMRecoveredUserJwtRefresher shared];
        [self configureHTTPHeaders];
        self.oreoOperationQueue = [NSOperationQueue new];
    }
    return self;
}
- (void)configureHTTPHeaders {
    [self.httpHeaderFields setValue:@"application/json" forKey:@"Content-Type"];
    [self.httpHeaderFields setValue:@"application/json" forKey:@"Accept"];
    [self.httpHeaderFields setValue:[ICMUtils intercomAgent] forKey:@"X-INTERCOM-AGENT"];
    NSString *environment = [ICMUtils messengerEnvironmentIdentifier];
    if (environment) [self.httpHeaderFields setValue:environment forKey:@"X-INTERCOM-MESSENGER-ENVIRONMENT"];
    [self.httpHeaderFields setValue:[[NSLocale preferredLanguages] componentsJoinedByString:@", "] forKey:@"Accept-Language"];
    [self.httpHeaderFields setValue:[[ICMTranslationManager supportedLocales] componentsJoinedByString:@", "] forKey:@"X-INTERCOM-SUPPORTED-LANGUAGES"];
    NSString *version = [ICMUtils appVersion];
    if ([version isKindOfClass:[NSString class]]) [self.httpHeaderFields setValue:version forKey:@"X-INTERCOM-HOST-APP-VERSION"];
}
@end
