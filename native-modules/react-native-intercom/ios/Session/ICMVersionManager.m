#import "ICMVersionManager.h"
#import "ICMHTTPClient+RichPush.h"
#import "../Identity/ICMUtils.h"
@implementation ICMVersionManager
+ (void)checkIfNewVersionOfSDKAvailable {
    [ICMHTTPClient fetchLatestiOSSDKReleaseVersion:^(NSString *latest) {
        NSString *current = [ICMUtils intercomVersion];
        if ([self isNewIntercomVersionAvailable:latest]) NSLog(@"[Intercom] \n\n################################################################################# \n# Intercom %@ is now available 🎉. You are using %@ \n# Update to %@ to receive the latest features and bug fixes. \n# To see all new releases, open https://github.com/intercom/intercom-ios/releases\n#################################################################################\n .", latest, current, latest);
        else NSLog(@"[Intercom] %@ ✅ You are using the latest version of Intercom", current);
    }];
}
+ (BOOL)isNewIntercomVersionAvailable:(NSString *)latest {
    return [latest compare:[ICMUtils intercomVersion] options:NSNumericSearch] == NSOrderedDescending;
}
@end
