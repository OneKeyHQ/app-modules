#import "../Identity/ICMSafeNetworkModel.h"
@interface ICMComposerState : ICMSafeNetworkModel {
    BOOL _workflowActive;
    NSInteger _displayState;
    NSInteger _version;
    NSString *_placeholder;
}
@property (nonatomic) NSInteger displayState;
@property (nonatomic) BOOL workflowActive;
@property (nonatomic) NSInteger version;
@property (nonatomic, copy) NSString *placeholder;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
- (BOOL)isEqualToComposerState:(ICMComposerState *)other;
- (id)copyWithZone:(NSZone *)zone;
@end
