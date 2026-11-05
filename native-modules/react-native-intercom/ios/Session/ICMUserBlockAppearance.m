#import "ICMUserBlockAppearance.h"
#import "ICMBlockAppearanceDependencies.h"
@implementation ICMUserBlockAppearance
- (instancetype)init {
    if ((self = [super init])) {
        self.textColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryOnContainer;
        self.linkTextColor = self.textColor;
    }
    return self;
}
@end
