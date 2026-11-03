#pragma once
#import <Foundation/Foundation.h>

@interface NSString (JPUSHTagAlias)
- (BOOL)isValidTagAlias;
@end

@interface JPUSHTagAliasValidation : NSObject
+ (int)checkTags:(id)tags;
+ (int)checkAlias:(id)alias;
+ (int)validateTagsAlias:(id)tags alias:(id)alias;
+ (int)validateTags:(id)tags isNew:(BOOL)isNew;
+ (NSInteger)byteLength:(id)string;
+ (int)validateAlias:(id)alias;
+ (NSSet *)filterValidTags:(id)tags;
@end
