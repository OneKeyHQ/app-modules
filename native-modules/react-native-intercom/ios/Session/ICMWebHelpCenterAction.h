#import <Foundation/Foundation.h>
@interface ICMWebHelpCenterAction : NSObject {
    NSString *_type;
    NSDictionary *_value;
}
@property (nonatomic, copy, readonly) NSString *type;
@property (nonatomic, readonly) NSDictionary *value;
- (instancetype)initWithType:(NSString *)type value:(NSDictionary *)value;
- (NSDictionary *)dictionaryRepresentation;
@end
