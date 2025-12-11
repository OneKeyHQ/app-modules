#import "NFCConfig.h"
#import "keys.h"

@implementation NFCConfig

+ (NSDictionary *)env {
    char *result = getInitParams();
    NSData *data = [NSData dataWithBytesNoCopy:result length:strlen(result) freeWhenDone:YES];
    NSDictionary *value = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:NULL];
    return value;
}

+ (NSString *)envFor: (NSString *)key {
    NSString *value = (NSString *)[self.env objectForKey:key];
    return value;
}

@end
