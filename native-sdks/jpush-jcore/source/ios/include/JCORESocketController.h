#pragma once
#import "JCORESocket.h"

@interface JCORESocketController : NSObject <JCORENetworkSocketDelegate> {
    NSUInteger _type;
    __weak id<JCORENetworkDelegate> _delegate;
    id<JCORESocketConfigProtocol> _config;
    NSNumber *_careAllDataSent;
    __weak NSThread *_reqThread;
    JCORESocket *_socket;
}
@property (strong, nonatomic) JCORESocket *socket;
@property (readonly, nonatomic) NSUInteger type;
@property (weak, nonatomic) id<JCORENetworkDelegate> delegate;
@property (strong, nonatomic) id<JCORESocketConfigProtocol> config;
@property (strong, nonatomic) NSNumber *careAllDataSent;
@property (weak, nonatomic) NSThread *reqThread;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
+ (instancetype)socket:(NSUInteger)type;
- (instancetype)initWithType:(NSUInteger)type;
- (void)remove;
- (void)connect;
- (void)disconect;
- (void)sendData:(id)data;
@end
