#import "JCOREConfig.h"
#import "JCOREConfigRequest.h"
#import "JCOREIntegrate.h"
#import "JCORELog.h"
#import "JCOREThread.h"
FOUNDATION_EXPORT NSString *const kJCORENetworkCmdCtrlNotification;
#define UpdateFile                                                                                           \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Controller/JCOREConfigUpdateController.m"
#define UpdateLog(sourceLine, messageFormat, ...)                                                            \
    [JCORELog log:YES                                                                                        \
            level:0x3ff                                                                                      \
             flag:0x100                                                                                      \
          context:0                                                                                          \
             file:UpdateFile                                                                                 \
         function:__PRETTY_FUNCTION__                                                                        \
             line:sourceLine                                                                                 \
              tag:nil                                                                                        \
           format:messageFormat, ##__VA_ARGS__]
@implementation JCOREConfigUpdateController
@synthesize completionHandler = _completionHandler;
- (void)dealloc {
    [self stopMonitoring];
}
- (void)startMonitoringWithCompletion:(void (^)(NSArray *configs))completion {
    self.completionHandler = completion;
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleCmdControlNotification:)
                                                 name:kJCORENetworkCmdCtrlNotification
                                               object:nil];
    UpdateLog(45, @"[ConfigUpdate] Started monitoring for config update command (cmd=84)");
}
- (void)stopMonitoring {
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:kJCORENetworkCmdCtrlNotification
                                                  object:nil];
    self.completionHandler = nil;
    UpdateLog(55, @"[ConfigUpdate] Stopped monitoring for config update command");
}
- (void)handleCmdControlNotification:(NSNotification *)notification {
    if (![JCOREThread isMainThread]) {
        [JCOREThread performSel:@selector(handleCmdControlNotification:) target:self withObject:notification];
        return;
    }
    id command = notification.userInfo[@"cmd_content"];
    if (![command isKindOfClass:[NSDictionary class]]) {
        UpdateLog(71, @"[ConfigUpdate] Invalid command content");
        return;
    }
    id cmd = command[@"cmd"];
    if (![cmd isKindOfClass:[NSNumber class]] || [cmd integerValue] != 84)
        return;
    UpdateLog(82, @"[ConfigUpdate] Received config update command (cmd=84): %@", command);
    id content = command[@"content"];
    if (![content isKindOfClass:[NSDictionary class]]) {
        UpdateLog(87, @"[ConfigUpdate] Invalid command content structure");
        return;
    }
    id configs = content[@"configs"];
    if (![configs isKindOfClass:[NSArray class]] || ![configs count]) {
        UpdateLog(94, @"[ConfigUpdate] No configs found in command");
        return;
    }
    NSMutableArray *updates = [NSMutableArray array];
    for (id config in configs) {
        if (![config isKindOfClass:[NSDictionary class]])
            continue;
        id type = config[@"type"];
        if (![type isKindOfClass:[NSString class]])
            continue;
        BOOL banned = [type isEqualToString:@"banned"];
        if ([self shouldUpdateWithConfig:config
                         protocolVersion:banned ? [JCOREConfigRequest getProtocolVersion] : -1])
            [updates addObject:config];
        else
            UpdateLog(banned ? 115 : 120,
                      @"[ConfigUpdate] Skipping update for type '%@' due to version restrictions", type);
    }
    if (updates.count && self.completionHandler) {
        UpdateLog(131, @"[ConfigUpdate] Found %lu configurations to update", (unsigned long)updates.count);
        self.completionHandler(updates);
    }
}
- (BOOL)shouldUpdateWithConfig:(id)config protocolVersion:(long long)version {
    id range = config[@"ver_range"];
    if (version != -1 && [range isKindOfClass:[NSString class]] && [range length] &&
        ![self isVersion:version inRange:range])
        return NO;
    NSInteger sdk =
        [[[JCOREIntegrate jcoreDisplayVersion] stringByReplacingOccurrencesOfString:@"."
                                                                         withString:@""] integerValue];
    range = config[@"sdk_range"];
    return ![range isKindOfClass:[NSString class]] || ![range length] || [self isVersion:sdk inRange:range];
}
- (BOOL)isVersion:(long long)version inRange:(id)range {
    if (![range hasPrefix:@"["] || ![range hasSuffix:@"]"])
        return NO;
    NSString *body = [range substringWithRange:NSMakeRange(1, [range length] - 2)];
    NSArray *parts = [body componentsSeparatedByString:@","];
    if (parts.count != 2)
        return NO;
    NSString *minimum = [parts[0] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    NSString *maximum = [parts[1] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    return (!minimum.length || [minimum integerValue] <= version) &&
           (!maximum.length || version <= [maximum integerValue]);
}
@end
