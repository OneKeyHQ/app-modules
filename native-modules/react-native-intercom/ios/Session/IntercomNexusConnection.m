#import "IntercomNexusConnection.h"
#import "IntercomNexusEvent.h"
@implementation IntercomNexusConnection
@synthesize monitoringNetwork = _monitoringNetwork, configuration = _configuration, connectionState = _connectionState, websockets = _websockets, delegate = _delegate, networkMonitor = _networkMonitor, networkMonitorQueue = _networkMonitorQueue, backgroundTimer = _backgroundTimer, backgroundTask = _backgroundTask, pingTimer = _pingTimer;
- (instancetype)initWithConfiguration:(IntercomNexusConfiguration *)configuration delegate:(id<IntercomNexusConnectionDelegate>)delegate {
    self = [super init];
    if (self) {
        _delegate = delegate;
        _backgroundTask = UIBackgroundTaskInvalid;
        _connectionState = 0;
        self.configuration = configuration;
        [self addLifecycleNotifications];
    }
    return self;
}
- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    if (self.networkMonitor) nw_path_monitor_cancel(self.networkMonitor);
}
- (void)setConfiguration:(IntercomNexusConfiguration *)configuration {
    BOOL sameEndpoints = [configuration.endpoints isEqualToArray:self.configuration.endpoints];
    _configuration = configuration;
    if (!sameEndpoints) [self createWebsockets];
}
- (void)sendMessage:(NSDictionary *)message {
    for (IntercomNexusSocket *socket in self.websockets) [socket sendWebSocketMessage:message];
}
- (void)createWebsockets {
    [self.websockets makeObjectsPerformSelector:@selector(disconnect)];
    NSMutableArray *sockets = [NSMutableArray array];
    for (NSURL *url in self.configuration.endpoints) {
        IntercomNexusSocket *socket = [[IntercomNexusSocket alloc] initWithURL:url timeout:self.configuration.connectionTimeout delegate:self];
        [sockets addObject:socket];
    }
    self.websockets = sockets;
}
- (NSArray *)validSockets {
    return [self.websockets filteredArrayUsingPredicate:[NSPredicate predicateWithBlock:^BOOL(id socket, NSDictionary *bindings) {
        (void)bindings;
        return socket != nil;
    }]];
}
- (void)reconnectSockets {
    for (IntercomNexusSocket *socket in [self validSockets]) [socket reconnect];
}
- (void)disconnectSockets {
    for (IntercomNexusSocket *socket in [self validSockets]) {
        if ([socket isConnected]) [socket disconnect];
    }
}
- (void)addLifecycleNotifications {
    __weak IntercomNexusConnection *weakSelf = self;
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC), dispatch_get_main_queue(), ^{
        IntercomNexusConnection *current = weakSelf;
        [[NSNotificationCenter defaultCenter] addObserver:current selector:@selector(applicationDidEnterBackground) name:UIApplicationDidEnterBackgroundNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:current selector:@selector(applicationWillEnterForeground) name:UIApplicationWillEnterForegroundNotification object:nil];
    });
}
- (void)applicationDidEnterBackground {
    if (self.configuration.backgroundConnectionTimeout.doubleValue > 0) [self startBackgroundTask];
    else [self finishBackgrounding];
}
- (void)applicationWillEnterForeground {
    [self invalidateBackgroundTask];
    [self tearDownBackgroundTimer];
    for (IntercomNexusSocket *socket in self.websockets) [socket reconnect];
}
- (void)startBackgroundTask {
    __weak IntercomNexusConnection *weakSelf = self;
    self.backgroundTask = [[UIApplication sharedApplication] beginBackgroundTaskWithName:@"io.intercom.ios.nexus" expirationHandler:^{ [weakSelf finishBackgrounding]; }];
    dispatch_async(dispatch_get_main_queue(), ^{
        double timeout = self.configuration.backgroundConnectionTimeout.doubleValue;
        if (timeout > 0) {
            self.backgroundTimer = [NSTimer scheduledTimerWithTimeInterval:timeout repeats:NO block:^(NSTimer *timer) {
                (void)timer;
                [weakSelf finishBackgrounding];
            }];
        }
    });
}
- (void)finishBackgrounding {
    [self tearDownBackgroundTimer];
    for (IntercomNexusSocket *socket in self.websockets) [socket disconnect];
    [self invalidateBackgroundTask];
    [self invalidatePingTimer];
}
- (void)tearDownBackgroundTimer {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.backgroundTimer invalidate];
        self.backgroundTimer = nil;
    });
}
- (void)invalidateBackgroundTask {
    if (self.backgroundTask != UIBackgroundTaskInvalid) {
        [[UIApplication sharedApplication] endBackgroundTask:self.backgroundTask];
        self.backgroundTask = UIBackgroundTaskInvalid;
    }
}
- (void)websocket:(IntercomNexusSocket *)socket didReceiveMessage:(NSDictionary *)message {
    (void)socket;
    [self.delegate connection:self didReceiveMessage:message];
}
- (void)websocketDidConnect:(IntercomNexusSocket *)socket {
    (void)socket;
    [self startNetworkMonitoring];
    for (IntercomNexusSocket *current in self.websockets) if (![current isConnected]) return;
    _connectionState = 1;
    [self.delegate connectionDidConnect:self];
    [self startPingTimer];
}
- (void)websocketDidDisconnect:(IntercomNexusSocket *)socket {
    (void)socket;
    for (IntercomNexusSocket *current in self.websockets) if ([current isConnected]) return;
    _connectionState = 2;
    [self.delegate connectionDidDisconnect:self];
    [self invalidatePingTimer];
}
- (void)startPingTimer {
    __block __weak IntercomNexusConnection *weakSelf = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        double interval = weakSelf.configuration.connectionHeartbeatInterval.doubleValue;
        if (interval > 0) {
            self.pingTimer = [NSTimer scheduledTimerWithTimeInterval:interval repeats:YES block:^(NSTimer *timer) {
                (void)timer;
                [weakSelf pingWebSocketServer];
            }];
        }
    });
}
- (void)invalidatePingTimer {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.pingTimer invalidate];
        self.pingTimer = nil;
    });
}
- (void)pingWebSocketServer {
    IntercomNexusEvent *event = [[IntercomNexusEvent alloc] initWithType:23 topics:nil];
    [self sendMessage:[event dictionaryRepresentation]];
}
- (void)startNetworkMonitoring {
    if (self.monitoringNetwork) return;
    dispatch_queue_attr_t attributes = dispatch_queue_attr_make_with_qos_class(DISPATCH_QUEUE_SERIAL, QOS_CLASS_UTILITY, 0);
    self.networkMonitorQueue = dispatch_queue_create("nexus.reachability", attributes);
    self.networkMonitor = nw_path_monitor_create();
    nw_path_monitor_set_queue(self.networkMonitor, self.networkMonitorQueue);
    __weak IntercomNexusConnection *weakSelf = self;
    nw_path_monitor_set_update_handler(self.networkMonitor, ^(nw_path_t path) {
        nw_path_status_t status = nw_path_get_status(path);
        IntercomNexusConnection *current = weakSelf;
        if (status == nw_path_status_satisfied) [current reconnectSockets];
        else [current disconnectSockets];
    });
    nw_path_monitor_start(self.networkMonitor);
    self.monitoringNetwork = YES;
}
@end
