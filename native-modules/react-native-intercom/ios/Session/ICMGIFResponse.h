#import "../Identity/ICMSafeNetworkModel.h"
@interface ICMGIFResponse : ICMSafeNetworkModel {
    NSArray *_results;
}
@property (nonatomic, strong) NSArray *results;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
- (BOOL)isEqualToResponse:(ICMGIFResponse *)response;
@end
