#import "../Identity/ICMSafeNetworkModel.h"
@interface ICMReactionsOption : ICMSafeNetworkModel {
    NSNumber *_index;
    NSString *_unicodeEmoticon;
    NSURL *_imageUrl;
}
@property (nonatomic, readonly) NSNumber *index;
@property (nonatomic, readonly) NSString *unicodeEmoticon;
@property (nonatomic, readonly) NSURL *imageUrl;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
- (BOOL)isEqualToOption:(ICMReactionsOption *)other;
@end
@interface ICMReactionsSet : ICMSafeNetworkModel {
    NSArray *_options;
    NSNumber *_selectedIndex;
}
@property (nonatomic, readonly) NSArray *options;
@property (nonatomic, strong) NSNumber *selectedIndex;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
- (BOOL)isEqualToReactionsSet:(ICMReactionsSet *)other;
@end
