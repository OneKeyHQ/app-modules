#import "ICMSafeNetworkModel.h"
@interface ICMSheet : ICMSafeNetworkModel
@property (nonatomic, strong) NSDictionary *requestBody;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
- (BOOL)isEqualToSheet:(ICMSheet *)sheet;
@end
