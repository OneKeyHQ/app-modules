// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMSafeNetworkModel.h"
@implementation ICMSafeNetworkModel
// Both original handlers consist of a single ret instruction.
- (void)setNilValueForKey:(NSString *)key { (void)key; }
- (void)setValue:(id)value forUndefinedKey:(NSString *)key { (void)value; (void)key; }
@end
