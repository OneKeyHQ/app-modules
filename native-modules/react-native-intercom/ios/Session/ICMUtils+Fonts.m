#import "ICMUtils+Fonts.h"
@implementation ICMUtils (OriginalBlockFonts)
+ (UIFont *)fontOfSize:(CGFloat)size weight:(NSUInteger)weight {
    if ([UIFont respondsToSelector:@selector(systemFontOfSize:weight:)]) {
        return [UIFont systemFontOfSize:size weight:[self convertToUIFontWeight:weight]];
    }
    return [UIFont fontWithName:[self convertToFontName:weight] size:size];
}
+ (UIFontWeight)convertToUIFontWeight:(NSUInteger)weight {
    switch (weight) {
        case 1: return UIFontWeightBold;
        case 2: return UIFontWeightMedium;
        case 3: return UIFontWeightLight;
        case 4: return UIFontWeightThin;
        case 5: return UIFontWeightSemibold;
        default: return UIFontWeightRegular;
    }
}
+ (NSString *)convertToFontName:(NSUInteger)weight {
    switch (weight) {
        case 1: return [@"HelveticaNeue" stringByAppendingString:@"-Bold"];
        case 2: return [@"HelveticaNeue" stringByAppendingString:@"-Medium"];
        case 3: return [@"HelveticaNeue" stringByAppendingString:@"-Light"];
        case 4: return [@"HelveticaNeue" stringByAppendingString:@"-Thin"];
        case 5: return [@"HelveticaNeue" stringByAppendingString:@"-Semibold"];
        default: return @"HelveticaNeue";
    }
}
@end
