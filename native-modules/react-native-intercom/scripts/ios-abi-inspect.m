// Run separately with either source-built code or the pinned original SDK.
#import <Foundation/Foundation.h>
#import <objc/runtime.h>

static NSArray *methods(Class cls) {
    unsigned int count = 0;
    Method *list = class_copyMethodList(cls, &count);
    NSMutableArray *result = [NSMutableArray new];
    for (unsigned int i = 0; i < count; i++) {
        [result addObject:@{@"selector": NSStringFromSelector(method_getName(list[i])),
                            @"encoding": @(method_getTypeEncoding(list[i]))}];
    }
    free(list);
    [result sortUsingComparator:^NSComparisonResult(NSDictionary *a, NSDictionary *b) {
        return [a[@"selector"] compare:b[@"selector"]];
    }];
    return result;
}

int main(int argc, const char **argv) {
    @autoreleasepool {
        NSArray<NSString *> *names = argc > 1 ? [@(argv[1]) componentsSeparatedByString:@","] : @[
            @"IntercomContent", @"Article", @"Survey", @"Carousel", @"HelpCenterCollections", @"Conversation", @"TicketContent",
            @"ICMCompany", @"ICMUserAttributes", @"ICMCustomDataValidation", @"ICMHelpCenterArticle", @"ICMHelpCenterArticleAuthor",
            @"ICMHelpCenterArticleSearchResult", @"ICMHelpCenterCollection", @"ICMHelpCenterCollectionContent",
            @"ICMUserIdentity", @"_TtC18IntercomSDKPrivate11ICMKeychain"
        ];
        NSMutableDictionary *classes = [NSMutableDictionary new];
        for (NSString *name in names) {
            Class cls = NSClassFromString(name);
            if (!cls) { classes[name] = @{@"missing": @YES}; continue; }
            unsigned int count = 0;
            Ivar *ivars = class_copyIvarList(cls, &count);
            NSMutableDictionary *ivarDetails = [NSMutableDictionary new];
            for (unsigned int i = 0; i < count; i++) {
                ivarDetails[@(ivar_getName(ivars[i]))] = @{@"type": @(ivar_getTypeEncoding(ivars[i])), @"offset": @(ivar_getOffset(ivars[i]))};
            }
            free(ivars);
            objc_property_t *properties = class_copyPropertyList(cls, &count);
            NSMutableDictionary *propertyDetails = [NSMutableDictionary new];
            for (unsigned int i = 0; i < count; i++) propertyDetails[@(property_getName(properties[i]))] = @(property_getAttributes(properties[i]));
            free(properties);
            classes[name] = @{@"superclass": NSStringFromClass(class_getSuperclass(cls)), @"instanceSize": @(class_getInstanceSize(cls)),
                              @"instanceMethods": methods(cls), @"classMethods": methods(object_getClass(cls)), @"ivars": ivarDetails, @"properties": propertyDetails};
        }
        NSData *json = [NSJSONSerialization dataWithJSONObject:classes options:NSJSONWritingPrettyPrinted | NSJSONWritingSortedKeys error:nil];
        fwrite(json.bytes, 1, json.length, stdout); fputc('\n', stdout);
    }
    return 0;
}
