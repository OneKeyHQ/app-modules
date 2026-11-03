#import "JCORESRVResolver.h"
#import <CoreFoundation/CoreFoundation.h>
#include <dns_sd.h>
#include <dns_util.h>

@interface JCORESRVResolver () {
@package
    DNSServiceRef _sdRef;
    CFSocketRef _sdRefSocket;
    CFRunLoopSourceRef _rls;
    BOOL _finished;
    NSString *_srvName;
    __weak id<JCORESRVResolverDelegate> _delegate;
    NSError *_error;
    NSMutableArray *_resultsMutable;
    NSTimer *_timer;
    double _timeout;
}
@end

static void QueryRecordCallback(DNSServiceRef service, DNSServiceFlags flags, uint32_t interfaceIndex, DNSServiceErrorType error, const char *fullname, uint16_t type, uint16_t recordClass, uint16_t length, const void *record, uint32_t ttl, void *context) {
    JCORESRVResolver *resolver = (__bridge id)context;
    if (![resolver isKindOfClass:JCORESRVResolver.class] || resolver->_sdRef != service || !(flags & kDNSServiceFlagsAdd) || type != 33 || recordClass != 1) return;
    if (error) [resolver stopWithDNSServiceError:error];
    else {
        [resolver processRecord:record length:length];
        if (!(flags & kDNSServiceFlagsMoreComing)) [resolver stopWithError:nil];
    }
}
static void SDRefSocketCallback(CFSocketRef socket, CFSocketCallBackType type, CFDataRef address, const void *data, void *context) {
    if (type != kCFSocketReadCallBack) return;
    JCORESRVResolver *resolver = (__bridge id)context;
    if (![resolver isKindOfClass:JCORESRVResolver.class] || resolver->_sdRefSocket != socket) return;
    DNSServiceErrorType error = DNSServiceProcessResult(resolver->_sdRef);
    if (error) [resolver stopWithDNSServiceError:error];
}

@implementation JCORESRVResolver
@synthesize finished = _finished, error = _error, resultsMutable = _resultsMutable, timer = _timer, timeout = _timeout, srvName = _srvName, delegate = _delegate;
- (instancetype)initWithSRVName:(NSString *)name {
    if (!name) return nil;
    self = [super init];
    if (self) {
        _srvName = [name copy];
        if (!_srvName) return nil;
        _resultsMutable = [[NSMutableArray alloc] init];
        if (!_resultsMutable) return nil;
        _timeout = 1.0;
    }
    return self;
}
- (instancetype)initWithSRVName:(NSString *)name timeout:(double)timeout {
    JCORESRVResolver *resolver = [[JCORESRVResolver alloc] initWithSRVName:name];
    if (resolver) resolver.timeout = timeout;
    return resolver;
}
- (void)dealloc { [self stop]; }
- (void)start {
    if (_sdRef) return;
    self.error = nil;
    self.finished = NO;
    if (self.resultsMutable.count) [self.resultsMutable removeAllObjects];
    [self startInternal];
    self.timer = [NSTimer scheduledTimerWithTimeInterval:_timeout target:self selector:@selector(stopWithTimeout) userInfo:nil repeats:NO];
}
- (void)stop {
    if (_rls) { CFRunLoopRemoveSource(CFRunLoopGetCurrent(), _rls, kCFRunLoopDefaultMode); CFRelease(_rls); _rls = NULL; }
    if (_sdRefSocket) { CFSocketInvalidate(_sdRefSocket); CFRelease(_sdRefSocket); _sdRefSocket = NULL; }
    if (_sdRef) { DNSServiceRefDeallocate(_sdRef); _sdRef = NULL; }
    self.finished = YES;
    [self.timer invalidate]; self.timer = nil;
}
- (void)stopWithTimeout {
    NSError *error = [NSError errorWithDomain:kJCORESRVResolverErrorDomain code:-1001 userInfo:@{NSLocalizedDescriptionKey:@"SRV time out"}];
    [self stopWithError:error];
}
- (void)stopWithError:(NSError *)error {
    self.error = error;
    [self stop];
    if (self.delegate && [self.delegate respondsToSelector:@selector(srvResolver:didStopWithError:)]) [self.delegate srvResolver:self didStopWithError:error];
}
- (void)stopWithDNSServiceError:(int)error {
    [self stopWithError:error ? [NSError errorWithDomain:kJCORESRVResolverErrorDomain code:error userInfo:nil] : nil];
}
- (NSArray *)results { return [self.resultsMutable copy]; }
- (void)processRecord:(const void *)record length:(NSUInteger)length {
    if (!record || length > UINT16_MAX) return;
    NSMutableData *data = [NSMutableData data];
    if (!data) return;
    const unsigned char header[11] = {0,0,33,0,1,0,0,2,0x9a,(unsigned char)(length >> 8),(unsigned char)length};
    [data appendBytes:header length:sizeof(header)]; [data appendBytes:record length:length];
    dns_resource_record_t *parsed = dns_parse_resource_record(data.bytes, (uint32_t)data.length);
    if (!parsed) return;
    NSString *target = [NSString stringWithCString:parsed->data.SRV->target encoding:NSASCIIStringEncoding];
    if (target) {
        NSDictionary *result = [NSDictionary dictionaryWithObjectsAndKeys:@((unsigned int)parsed->data.SRV->priority), kJCORESRVResolverPriority, @((unsigned int)parsed->data.SRV->weight), kJCORESRVResolverWeight, @((unsigned int)parsed->data.SRV->port), kJCORESRVResolverPort, target, kJCORESRVResolverTarget, nil];
        if (!result) return;
        NSIndexSet *indexes = [NSIndexSet indexSetWithIndex:self.results.count];
        if (!indexes) return;
        [self willChange:NSKeyValueChangeInsertion valuesAtIndexes:indexes forKey:@"results"];
        [self.resultsMutable addObject:result];
        [self didChange:NSKeyValueChangeInsertion valuesAtIndexes:indexes forKey:@"results"];
        if (self.delegate && [self.delegate respondsToSelector:@selector(srvResolver:didReceiveResult:)]) [self.delegate srvResolver:self didReceiveResult:result];
    }
    dns_free_resource_record(parsed);
}
- (void)startInternal {
    if (_sdRef) return;
    CFSocketContext context = {0, (__bridge void *)self, NULL, NULL, NULL};
    const char *name = self.srvName.UTF8String;
    DNSServiceErrorType error = name ? DNSServiceQueryRecord(&_sdRef, 0x1000, 0, name, 33, 1, QueryRecordCallback, (__bridge void *)self) : kDNSServiceErr_BadParam;
    if (error) { [self stopWithDNSServiceError:error]; return; }
    if (!_sdRef) return;
    int socket = DNSServiceRefSockFD(_sdRef);
    if (socket < 0 || _sdRefSocket) return;
    _sdRefSocket = CFSocketCreateWithNative(NULL, socket, kCFSocketReadCallBack, SDRefSocketCallback, &context);
    if (!_sdRefSocket) return;
    CFSocketSetSocketFlags(_sdRefSocket, CFSocketGetSocketFlags(_sdRefSocket) & ~kCFSocketCloseOnInvalidate);
    _rls = CFSocketCreateRunLoopSource(NULL, _sdRefSocket, 0);
    if (_rls) CFRunLoopAddSource(CFRunLoopGetCurrent(), _rls, kCFRunLoopDefaultMode);
}
@end
