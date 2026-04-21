#import "TcpSocket.h"

#include <arpa/inet.h>
#include <errno.h>
#include <fcntl.h>
#include <netdb.h>
#include <netinet/in.h>
#include <string.h>
#include <sys/select.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <unistd.h>

@implementation TcpSocket

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeTcpSocketSpecJSI>(params);
}

+ (NSString *)moduleName
{
    return @"RNTcpSocket";
}

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

// MARK: - connectWithTimeout

- (void)connectWithTimeout:(NSString *)host
                      port:(double)port
                 timeoutMs:(double)timeoutMs
                   resolve:(RCTPromiseResolveBlock)resolve
                    reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSTimeInterval startTime = [[NSDate date] timeIntervalSince1970] * 1000.0;

        int sockfd = socket(AF_INET, SOCK_STREAM, 0);
        if (sockfd < 0) {
            reject(@"SOCKET_ERROR", @"Failed to create socket", nil);
            return;
        }

        // Set non-blocking mode
        int flags = fcntl(sockfd, F_GETFL, 0);
        if (flags < 0) {
            close(sockfd);
            reject(@"SOCKET_ERROR", @"Failed to get socket flags", nil);
            return;
        }
        if (fcntl(sockfd, F_SETFL, flags | O_NONBLOCK) < 0) {
            close(sockfd);
            reject(@"SOCKET_ERROR", @"Failed to set non-blocking mode", nil);
            return;
        }

        // Resolve hostname
        struct addrinfo hints;
        struct addrinfo *res = NULL;
        memset(&hints, 0, sizeof(hints));
        hints.ai_family = AF_UNSPEC;
        hints.ai_socktype = SOCK_STREAM;

        char portStr[16];
        snprintf(portStr, sizeof(portStr), "%d", (int)port);

        int gaiResult = getaddrinfo([host UTF8String], portStr, &hints, &res);
        if (gaiResult != 0) {
            close(sockfd);
            reject(@"DNS_ERROR",
                   [NSString stringWithFormat:@"%s", gai_strerror(gaiResult)],
                   nil);
            return;
        }

        // Attempt non-blocking connect
        int connectResult = connect(sockfd, res->ai_addr, res->ai_addrlen);
        freeaddrinfo(res);

        if (connectResult < 0 && errno != EINPROGRESS) {
            close(sockfd);
            reject(@"CONNECT_ERROR",
                   [NSString stringWithUTF8String:strerror(errno)],
                   nil);
            return;
        }

        // If already connected (unlikely for non-blocking, but handle it)
        if (connectResult == 0) {
            close(sockfd);
            NSTimeInterval elapsed = [[NSDate date] timeIntervalSince1970] * 1000.0 - startTime;
            resolve(@((NSInteger)elapsed));
            return;
        }

        // Wait for connection with select() and timeout
        fd_set writeSet;
        FD_ZERO(&writeSet);
        FD_SET(sockfd, &writeSet);

        long timeoutLong = (long)timeoutMs;
        struct timeval tv;
        tv.tv_sec = timeoutLong / 1000;
        tv.tv_usec = (int)((timeoutLong % 1000) * 1000);

        int selectResult = select(sockfd + 1, NULL, &writeSet, NULL, &tv);

        if (selectResult == 0) {
            // Timeout
            close(sockfd);
            reject(@"ETIMEDOUT", @"Connection timeout", nil);
            return;
        }

        if (selectResult < 0) {
            close(sockfd);
            reject(@"SELECT_ERROR",
                   [NSString stringWithUTF8String:strerror(errno)],
                   nil);
            return;
        }

        // Check for socket-level error via getsockopt
        int socketError = 0;
        socklen_t optLen = sizeof(socketError);
        if (getsockopt(sockfd, SOL_SOCKET, SO_ERROR, &socketError, &optLen) < 0) {
            close(sockfd);
            reject(@"CONNECT_ERROR", @"Failed to get socket error", nil);
            return;
        }

        close(sockfd);

        if (socketError != 0) {
            reject(@"CONNECT_ERROR",
                   [NSString stringWithUTF8String:strerror(socketError)],
                   nil);
            return;
        }

        NSTimeInterval elapsed = [[NSDate date] timeIntervalSince1970] * 1000.0 - startTime;
        resolve(@((NSInteger)elapsed));
    });
}

@end
