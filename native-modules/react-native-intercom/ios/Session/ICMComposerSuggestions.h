#import "../Identity/ICMSafeNetworkModel.h"
@interface ICMComposerSuggestions : ICMSafeNetworkModel <NSSecureCoding> {
    BOOL _composerDisabled;
    NSString *_prompt;
    NSArray *_suggestions;
    __unsafe_unretained NSNumber *_snapshotId;
}
@property (nonatomic, copy) NSString *prompt;
@property (nonatomic, copy) NSArray *suggestions;
@property (nonatomic) BOOL composerDisabled;
@property (nonatomic, assign) NSNumber *snapshotId;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
- (NSArray *)suggestionsUUIDArray;
- (BOOL)isEmpty;
- (NSArray *)conversationParts;
- (BOOL)isEqualToComposerSuggestions:(ICMComposerSuggestions *)other;
@end
