#import "JCORESSLSocket.h"
#import "JCOREBusinessStorage.h"
#import "JCORELog.h"
#import <CFNetwork/CFNetwork.h>
#include <arpa/inet.h>
#include <errno.h>
#include <sys/socket.h>
#include <unistd.h>

#define JCORESSLFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Network/Model/JCORESSLSocket.m"
#define JCORESSLLog(sync, value, sourceLine, ...) [JCORELog log:sync level:0x3ff flag:value context:0 file:JCORESSLFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]
#define JCORESSLCallbackLog(sync, value, sourceLine, ...) [JCORELog log:sync level:0x3ff flag:value context:0 file:JCORESSLFile function:function line:sourceLine tag:nil format:__VA_ARGS__]

static void CFReadStreamCallback(CFReadStreamRef stream, CFStreamEventType event, void *context) {
    JCORESSLSocket *socket = (__bridge id)context;
    const char *function = "void CFReadStreamCallback(CFReadStreamRef, CFStreamEventType, void *)";
    if (event == kCFStreamEventHasBytesAvailable) {
        JCORESSLCallbackLog(YES, 0x100, 680, @"ssl - CFReadStreamCallback - HasBytesAvailable");
        if (socket->readStream != stream) { JCORESSLCallbackLog(NO, 0x20, 683, @"ssl - CFReadStreamCallback - not same readstream"); return; }
        if ((socket->flags & 0xc) == 0xc) {
            JCORESSLCallbackLog(YES, 0x100, 692, @"ssl - CFReadStreamCallback cert check");
            if (!CFReadStreamHasBytesAvailable(socket->readStream)) return;
            JCORESSLCallbackLog(YES, 0x100, 697, @"CFReadStreamCallback - CFReadStreamHasBytesAvailable");
            socket->flags |= 2;
            SecTrustRef trust = (SecTrustRef)CFReadStreamCopyProperty(socket->readStream, kCFStreamPropertySSLPeerTrust);
            if ([socket checkCert:trust]) [socket cf_finishSSLHandshake]; else [socket cf_abortSSLHandshake];
            if (trust) CFRelease(trust);
        } else { socket->flags |= 2; [socket doRead]; }
    } else {
        NSError *error = CFBridgingRelease(CFReadStreamCopyError(stream));
        if (!error && event == kCFStreamEventEndEncountered) error = [NSError errorWithDomain:@"JCORESLLSocketClosedError" code:-1 userInfo:@{NSLocalizedDescriptionKey:@"Socket closed by remote peer"}];
        JCORESSLCallbackLog(NO, 1, 738, @"ssl - CFReadStreamCallback: error: %@", error);
        if (socket->readStream == stream) { if ((socket->flags & 0xc) == 0xc) [socket cf_abortSSLHandshake]; else [socket close]; }
    }
}
static void CFWriteStreamCallback(CFWriteStreamRef stream, CFStreamEventType event, void *context) {
    JCORESSLSocket *socket = (__bridge id)context;
    const char *function = "void CFWriteStreamCallback(CFWriteStreamRef, CFStreamEventType, void *)";
    if (event == kCFStreamEventCanAcceptBytes) {
        JCORESSLCallbackLog(YES, 0x100, 768, @"ssl - CFWriteStreamCallback - CanAcceptBytes");
        if (socket->writeStream != stream) { JCORESSLCallbackLog(YES, 0x100, 771, @"ssl - CFWriteStreamCallback - not same writeStream"); return; }
        if ((socket->flags & 0xc) == 0xc) {
            if (!CFWriteStreamCanAcceptBytes(stream)) return;
            socket->flags |= 1;
            SecTrustRef trust = (SecTrustRef)CFWriteStreamCopyProperty(socket->writeStream, kCFStreamPropertySSLPeerTrust);
            if ([socket checkCert:trust]) [socket cf_finishSSLHandshake]; else [socket cf_abortSSLHandshake];
            if (trust) CFRelease(trust);
        } else { socket->flags |= 1; [socket doSend]; }
    } else {
        NSError *error = CFBridgingRelease(CFWriteStreamCopyError(stream));
        if (!error && event == kCFStreamEventEndEncountered) error = [NSError errorWithDomain:@"JCORESLLSocketClosedError" code:-1 userInfo:@{NSLocalizedDescriptionKey:@"Socket closed by remote peer"}];
        JCORESSLCallbackLog(NO, 1, 821, @"ssl - CFWriteStreamCallback: error: %@", error);
        if (socket->writeStream == stream) { if ((socket->flags & 0xc) == 0xc) [socket cf_abortSSLHandshake]; else [socket close]; }
    }
}

@implementation JCORESSLSocket
- (instancetype)init { if ((self = [super init])) { _ulDefaultReceiveBufferSize = 100240; _receivingBuffer = calloc(1, 100240); } return self; }
- (void)setConfig:(id<JCORESocketConfigProtocol>)config { [super setConfig:config]; _isSetuped = NO; _ulSendingDataSentLen = 0; _ulReceivedLen = 0; }
- (void)dealloc { [self close]; free(_receivingBuffer); }
- (void)connect { [super connect]; [self setup]; }
- (void)disconect { [super disconect]; [self close]; }
- (void)sendData:(id)data {
    [super sendData:data];
    if (!data) { JCORESSLLog(NO, 0x20, 95, @"ssl - %@", @"sendData object nil"); return; }
    JCORESSLLog(YES, 0x100, 98, @"ssl - send data%@", data);
    if (clientSocket == -1) { JCORESSLLog(NO, 0x20, 102, @"ssl - send data error: %@", @"clientSocket -1"); return; }
    if (!writeStream) { JCORESSLLog(NO, 0x20, 106, @"ssl - send data error: %@", @"writeStream null"); return; }
    if (!CFWriteStreamCanAcceptBytes(writeStream)) { JCORESSLLog(NO, 0x20, 114, @"ssl - not can write"); return; }
    JCORESSLLog(NO, 0x20, 112, @"ssl - can write yes");
    [self enqueue:data in:self.sendQueue];
    BOOL connected = [self isConnected];
    if ([data handler]) ((JCORETransportResult)[data handler])(connected);
    if (connected) [self doSend];
}
- (BOOL)setup {
    if ([self isConnected]) return YES;
    if (_isSetuped) { JCORESSLLog(NO, 1, 139, @"%@", @"tcp socket is setup up already"); return NO; }
    clientSocket = socket(self.ipType == 1 ? AF_INET6 : AF_INET, SOCK_STREAM, 0);
    if (clientSocket == -1) { JCORESSLLog(NO, 1, 150, @"ssl - Socket 创建失败"); return NO; }
    struct timeval timeout = {1, 500000};
    setsockopt(clientSocket, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout));
    int size = 100240;
    setsockopt(clientSocket, SOL_SOCKET, SO_RCVBUF, &size, sizeof(size));
    setsockopt(clientSocket, SOL_SOCKET, SO_SNDBUF, &size, sizeof(size));
    int noSignal = 1;
    setsockopt(clientSocket, SOL_SOCKET, SO_NOSIGPIPE, &noSignal, sizeof(noSignal));
    // The original uses this 16-byte IPv4-shaped address even for ipType == 1.
    struct sockaddr_in address = {0};
    address.sin_family = self.ipType == 1 ? AF_INET6 : AF_INET;
    address.sin_port = htons((uint16_t)self.port);
    address.sin_addr.s_addr = inet_addr(self.host.UTF8String);
    _connectTimer = [NSTimer scheduledTimerWithTimeInterval:5 target:self selector:@selector(outTimeAction) userInfo:nil repeats:NO];
    JCORESSLLog(YES, 8, 181, @"ssl - ssl start connect");
    int result = connect(clientSocket, (struct sockaddr *)&address, sizeof(address));
    JCORESSLLog(YES, 8, 183, @"ssl - ssl stop connect");
    if (result) { JCORESSLLog(NO, 1, 186, @"ssl - 连接失败 %d", result); [self p_cleanSocket]; return NO; }
    JCORESSLLog(YES, 8, 191, @"ssl - 连接成功");
    _isSetuped = YES;
    self.status = 1;
    [self startSSL];
    return YES;
}
- (void)outTimeAction { JCORESSLLog(YES, 0x100, 203, @"ssl - Socket:%@ 超时了", self.host); [self close]; }
- (void)close {
    if (_connectTimer && _connectTimer.isValid) { [_connectTimer invalidate]; _connectTimer = nil; }
    JCORESSLLog(YES, 8, 218, @"ssl - %@", @"关闭TCP连接");
    _isSetuped = NO;
    self.status = 0;
    if (clientSocket != -1) { [self p_cleanSocket]; _ulReceivedLen = 0; _ulSendingDataSentLen = 0; }
}
- (void)p_cleanSocket {
    if (clientSocket != -1) { close(clientSocket); clientSocket = -1; }
    if (readStream || writeStream) {
        [self removeStreamsFromRunLoop];
        if (readStream) { CFReadStreamSetClient(readStream, 0, NULL, NULL); CFReadStreamClose(readStream); CFRelease(readStream); readStream = NULL; }
        if (writeStream) { CFWriteStreamSetClient(writeStream, 0, NULL, NULL); CFWriteStreamClose(writeStream); CFRelease(writeStream); writeStream = NULL; }
    }
}
- (void)setStatus:(NSUInteger)status { if (_status != status) { _status = status; if (self.delegate) [self.delegate connectStateChanged:@(status) socket:self]; } }
- (BOOL)isConnected { return self.status == 2; }
- (void)doRead {
    JCORESSLLog(YES, 0x100, 282, @"ssl - doRead action begin");
    if (clientSocket == -1) { JCORESSLLog(YES, 0x100, 285, @"ssl - doRead - socket nil"); return; }
    if (!CFReadStreamHasBytesAvailable(readStream)) { JCORESSLLog(YES, 0x100, 290, @"ssl - doRead - ReadStreamHas no BytesAvailable"); return; }
    void *temporary = calloc(1, 100240);
    while (CFReadStreamHasBytesAvailable(readStream)) {
        // The original dereferences failed allocations and appends beyond a fixed buffer.
        if (!temporary || !_receivingBuffer) { free(temporary); [self resetReadInfo]; [self close]; return; }
        CFIndex received = CFReadStreamRead(readStream, temporary, 100240);
        if (received < 0) {
            NSError *error = CFBridgingRelease(CFReadStreamCopyError(readStream));
            JCORESSLLog(NO, 1, 309, @"ssl - tcp read error occured: errocode %@", error);
            [self resetReadInfo]; [self close]; free(temporary); return;
        }
        if (!received) { if (CFReadStreamGetStatus(readStream) != kCFStreamStatusAtEnd) JCORESSLLog(NO, 1, 320, @"ssl - doread size 0, but not kCFStreamStatusAtEnd"); break; }
        if (_ulReceivedLen > 100240 || (NSUInteger)received > 100240 - _ulReceivedLen) { free(temporary); [self resetReadInfo]; [self close]; return; }
        memmove(_receivingBuffer + _ulReceivedLen, temporary, (size_t)received);
        _ulReceivedLen += (NSUInteger)received;
        free(temporary);
        temporary = calloc(1, 100240);
    }
    free(temporary);
    JCORESSLLog(YES, 0x100, 336, @"ssl - doRead finish");
    flags &= ~2u;
    while (_ulReceivedLen >= 2) {
        uint16_t raw;
        memcpy(&raw, _receivingBuffer, sizeof(raw));
        NSUInteger length = ntohs(raw) & 0x3fff;
        if (!length) { [self resetReadInfo]; [self close]; return; }
        if (_ulReceivedLen < length) return;
        void *packet = malloc(length);
        NSUInteger remainingLength = _ulReceivedLen - length;
        void *remaining = malloc(remainingLength);
        if (!packet || (!remaining && remainingLength)) { free(packet); free(remaining); [self resetReadInfo]; [self close]; return; }
        memmove(packet, _receivingBuffer, length);
        if (remainingLength) memmove(remaining, _receivingBuffer + length, remainingLength);
        bzero(_receivingBuffer, _ulReceivedLen);
        if (remainingLength) memmove(_receivingBuffer, remaining, remainingLength);
        free(remaining);
        _ulReceivedLen -= length;
        [self recvData:[NSData dataWithBytes:packet length:length]];
        free(packet);
        if (!_ulReceivedLen) break;
    }
}
- (void)doSend {
    id object = [self firstObject:self.sendQueue];
    if (!object) { if (self.delegate) [self.delegate sendQueueEmpty:self]; return; }
    NSUInteger length = [[object data] length];
    if (length < _ulSendingDataSentLen) { JCORESSLLog(NO, 1, 404, @"ssl - send data error: data len %zu < sent len %lu", length, _ulSendingDataSentLen); [self close]; return; }
    for (;;) {
        NSData *data = [object data];
        CFIndex sent = CFWriteStreamWrite(writeStream, (const UInt8 *)data.bytes + _ulSendingDataSentLen, (CFIndex)(length - _ulSendingDataSentLen));
        if (sent < 0) { NSError *error = CFBridgingRelease(CFWriteStreamCopyError(writeStream)); JCORESSLLog(YES, 8, 417, @"ssl - tcp doSend data error :%@", error); [self close]; return; }
        if (sent) { _ulSendingDataSentLen += (NSUInteger)sent; if (_ulSendingDataSentLen == length) break; }
        else if (CFWriteStreamGetStatus(writeStream) == kCFStreamStatusAtEnd) break;
    }
    if (length == _ulSendingDataSentLen) { _ulSendingDataSentLen = 0; [self dequeue:object in:self.sendQueue]; if (self.delegate) [self.delegate sentData:object socket:self]; }
}
- (void)resetReadInfo { if (_receivingBuffer) { memset(_receivingBuffer, 0, 8); _ulReceivedLen = 0; } }
- (void)recvData:(id)data { [self performSelector:@selector(enqueueRecvData:) withObject:data afterDelay:0]; }
- (void)enqueueRecvData:(id)data { [self enqueue:data in:self.recvQueue]; if (!_isDispatching) { _isDispatching = YES; [self dispatchRecvData]; } }
- (void)dispatchRecvData {
    id data = [self firstObject:self.recvQueue];
    if (!data) { _isDispatching = NO; return; }
    if ([data length] && self.delegate) [self.delegate recvData:data socket:self];
    [self dequeue:data in:self.recvQueue];
    [self performSelector:@selector(dispatchRecvData) withObject:nil afterDelay:0];
}
- (void)startSSL {
    flags = (flags & ~0xfu) | 0xc;
    if (![self createReadAndWriteStream] || ![self registerForStreamCallbacksIncludingReadWrite:YES] || ![self addStreamsToRunLoop]) { [self close]; return; }
    NSMutableDictionary *settings = [NSMutableDictionary dictionary];
    settings[(__bridge id)kCFStreamSSLValidatesCertificateChain] = @NO;
    BOOL readSet = CFReadStreamSetProperty(readStream, kCFStreamPropertySSLSettings, (__bridge CFTypeRef)settings);
    BOOL writeSet = CFWriteStreamSetProperty(writeStream, kCFStreamPropertySSLSettings, (__bridge CFTypeRef)settings);
    if ((!readSet && !writeSet) || ![self openStreams]) [self close];
    else JCORESSLLog(YES, 0x100, 533, @"ssl - Waiting for SSL Handshake to complete...");
}
- (BOOL)createReadAndWriteStream {
    if (!readStream && !writeStream) {
        if (clientSocket == -1) return NO;
        CFStreamCreatePairWithSocket(NULL, clientSocket, &readStream, &writeStream);
        if (readStream) CFReadStreamSetProperty(readStream, kCFStreamPropertyShouldCloseNativeSocket, kCFBooleanFalse);
        if (writeStream) CFWriteStreamSetProperty(writeStream, kCFStreamPropertyShouldCloseNativeSocket, kCFBooleanFalse);
        if (!readStream || !writeStream) {
            JCORESSLLog(YES, 0x100, 556, @"ssl - Unable to create read and write stream...");
            if (readStream) { CFReadStreamClose(readStream); CFRelease(readStream); readStream = NULL; }
            if (writeStream) { CFWriteStreamClose(writeStream); CFRelease(writeStream); writeStream = NULL; }
            return NO;
        }
    }
    return YES;
}
- (BOOL)registerForStreamCallbacksIncludingReadWrite:(BOOL)includeReadWrite {
    streamContext = (CFStreamClientContext){0, (__bridge void *)self, NULL, NULL, NULL};
    if (!CFReadStreamSetClient(readStream, includeReadWrite ? 0x1a : 0x18, CFReadStreamCallback, &streamContext)) return NO;
    return CFWriteStreamSetClient(writeStream, includeReadWrite ? 0x1c : 0x18, CFWriteStreamCallback, &streamContext);
}
- (BOOL)addStreamsToRunLoop {
    if (!(flags & 0x10)) {
        JCORESSLLog(YES, 0x100, 609, @"ssl - Adding streams to runloop...");
        CFRunLoopRef loop = CFRunLoopGetCurrent();
        if (readStream) CFReadStreamScheduleWithRunLoop(readStream, loop, kCFRunLoopDefaultMode);
        if (writeStream) CFWriteStreamScheduleWithRunLoop(writeStream, loop, kCFRunLoopDefaultMode);
        flags |= 0x10;
    }
    return YES;
}
- (void)removeStreamsFromRunLoop {
    if (flags & 0x10) {
        JCORESSLLog(YES, 0x100, 630, @"ssl - Removing streams from runloop...");
        CFRunLoopRef loop = CFRunLoopGetCurrent();
        if (readStream) CFReadStreamUnscheduleFromRunLoop(readStream, loop, kCFRunLoopDefaultMode);
        if (writeStream) CFWriteStreamUnscheduleFromRunLoop(writeStream, loop, kCFRunLoopDefaultMode);
        flags &= ~0x10u;
    }
}
- (BOOL)openStreams {
    if (!readStream || !writeStream) { JCORESSLLog(NO, 0x20, 648, @"SSL - Read/Write stream is null"); return NO; }
    CFStreamStatus readStatus = CFReadStreamGetStatus(readStream), writeStatus = CFWriteStreamGetStatus(writeStream);
    if (readStatus && writeStatus) return YES;
    JCORESSLLog(YES, 0x100, 657, @"ssl - Opening read and write stream...");
    BOOL readOpen = CFReadStreamOpen(readStream), writeOpen = CFWriteStreamOpen(writeStream);
    if (readOpen && writeOpen) return YES;
    JCORESSLLog(YES, 0x100, 664, @"ssl - Error in CFStreamOpen");
    return NO;
}
- (void)cf_abortSSLHandshake { if ((flags & 0xc) == 0xc) { flags &= ~0xcu; [self close]; } }
- (void)cf_finishSSLHandshake {
    if ((flags & 0xc) != 0xc) return;
    flags &= ~0xcu;
    self.status = 2;
    if (_connectTimer && _connectTimer.isValid) { [_connectTimer invalidate]; _connectTimer = nil; }
    JCORESSLLog(YES, 8, 871, @"ssl - %@", @"ssl成功--连接主机成功");
    JCORESSLLog(YES, 0x100, 872, @"ssl - ssl成功--连接主机成功: %@ 端口: %u", self.host, self.port);
}
- (NSData *)convertCerStringToData:(id)string {
    NSString *body = [[string stringByReplacingOccurrencesOfString:@"-----BEGIN CERTIFICATE-----\n" withString:@""] stringByReplacingOccurrencesOfString:@"-----END CERTIFICATE-----" withString:@""];
    body = [body stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    return [[NSData alloc] initWithBase64EncodedString:body options:0];
}
- (BOOL)checkCert:(SecTrustRef)trust {
    id string = [JCOREBusinessStorage getLocalCert];
    if (!string || ![string isKindOfClass:NSString.class]) return NO;
    NSData *data = [self convertCerStringToData:string];
    SecTrustResultType result = kSecTrustResultDeny;
    if (!data) { JCORESSLLog(YES, 0x100, 919, @"local certificate could't be loaded!"); return NO; }
    SecCertificateRef certificate = SecCertificateCreateWithData(NULL, (__bridge CFDataRef)data);
    if (!certificate) return NO;
    NSArray *anchors = [NSArray arrayWithObjects:(__bridge id)certificate, nil];
    SecTrustSetAnchorCertificates(trust, (__bridge CFArrayRef)anchors);
    NSMutableArray *policies = [NSMutableArray array];
    SecPolicyRef policy = SecPolicyCreateBasicX509();
    [policies addObject:(__bridge id)policy];
    // Preserve the original BasicX509 policy, which does not validate the hostname.
    SecTrustSetPolicies(trust, (__bridge CFArrayRef)policies);
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    OSStatus status = SecTrustEvaluate(trust, &result);
#pragma clang diagnostic pop
    CFRelease(certificate);
    CFRelease(policy);
    if (!status && (result == kSecTrustResultUnspecified || result == kSecTrustResultProceed)) { JCORESSLLog(YES, 0x100, 925, @"ssl - 成功通过验证，证书可信"); return YES; }
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    NSArray *properties = CFBridgingRelease(SecTrustCopyProperties(trust));
#pragma clang diagnostic pop
    JCORESSLLog(YES, 0x100, 929, @"ssl - 证书不可信，连接失败 %@", properties);
    return NO;
}
@end
