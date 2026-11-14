#import "IntercomNexusSocket.h"
#import "../Identity/IntercomNexusConfiguration.h"
#import <Network/Network.h>
#import <UIKit/UIKit.h>
@class IntercomNexusConnection;
@protocol IntercomNexusConnectionDelegate <NSObject>
- (void)connection:(IntercomNexusConnection *)connection didReceiveMessage:(NSDictionary *)message;
- (void)connectionDidConnect:(IntercomNexusConnection *)connection;
- (void)connectionDidDisconnect:(IntercomNexusConnection *)connection;
@end
@interface IntercomNexusConnection : NSObject <IntercomNexusSocketDelegate> {
    BOOL _monitoringNetwork;
    IntercomNexusConfiguration *_configuration;
    NSUInteger _connectionState;
    NSArray *_websockets;
    __weak id<IntercomNexusConnectionDelegate> _delegate;
    nw_path_monitor_t _networkMonitor;
    dispatch_queue_t _networkMonitorQueue;
    __weak NSTimer *_backgroundTimer;
    UIBackgroundTaskIdentifier _backgroundTask;
    __weak NSTimer *_pingTimer;
}
@property (nonatomic, strong) NSArray *websockets;
@property (nonatomic, weak) id<IntercomNexusConnectionDelegate> delegate;
@property (nonatomic) BOOL monitoringNetwork;
@property (nonatomic, strong) nw_path_monitor_t networkMonitor;
@property (nonatomic, strong) dispatch_queue_t networkMonitorQueue;
@property (nonatomic, weak) NSTimer *backgroundTimer;
@property (nonatomic) UIBackgroundTaskIdentifier backgroundTask;
@property (nonatomic, weak) NSTimer *pingTimer;
@property (nonatomic, strong) IntercomNexusConfiguration *configuration;
@property (readonly) NSUInteger connectionState;
- (instancetype)initWithConfiguration:(IntercomNexusConfiguration *)configuration delegate:(id<IntercomNexusConnectionDelegate>)delegate;
- (void)sendMessage:(NSDictionary *)message;
- (void)createWebsockets;
- (NSArray *)validSockets;
- (void)reconnectSockets;
- (void)disconnectSockets;
- (void)addLifecycleNotifications;
- (void)applicationDidEnterBackground;
- (void)applicationWillEnterForeground;
- (void)startBackgroundTask;
- (void)finishBackgrounding;
- (void)tearDownBackgroundTimer;
- (void)invalidateBackgroundTask;
- (void)startPingTimer;
- (void)invalidatePingTimer;
- (void)pingWebSocketServer;
- (void)startNetworkMonitoring;
@end
