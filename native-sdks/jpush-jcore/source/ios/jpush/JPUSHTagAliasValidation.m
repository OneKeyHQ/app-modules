#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JPUSHTagAliasValidation.h"
#import <stdint.h>

@implementation NSString (JPUSHTagAlias)
- (BOOL)isValidTagAlias {
    NSUInteger length=[self length];
    for(NSUInteger index=0;index<length;index++){
        uint32_t character=[self characterAtIndex:index];
        if(character-0x30<=9||((character-0x41)&0xffff)<=0x19||character==0x5f||((character-0x61)&0xffff)<=0x19||(((character-0x4e00)>>9)&0x7f)<=0x28)continue;
        uint32_t punctuation=(character&0xffff)-0x21;
        if((punctuation<=0x1f&&((UINT32_C(1)<<(punctuation&0x1f))&UINT32_C(0x9000262d)))||(character&0xffff)==0x7c)continue;
        return NO;
    }
    return YES;
}
@end

@implementation JPUSHTagAliasValidation
+ (int)checkTags:(id)tags {
    if(!tags||([tags isKindOfClass:NSSet.class]&&![tags count]))return 6001;
    if(![tags isKindOfClass:NSNull.class]){
        int result=[JPUSHTagAliasValidation validateTags:tags isNew:YES];
        if(result)return result;
    }
    return 0;
}

+ (int)checkAlias:(id)alias {
    if(!alias||([alias isKindOfClass:NSString.class]&&![alias length]))return 6001;
    if(![alias isKindOfClass:NSNull.class]){
        int result=[JPUSHTagAliasValidation validateAlias:alias];
        if(result)return result;
    }
    return 0;
}

+ (int)validateTagsAlias:(id)tags alias:(id)alias {
    if(!tags&&!alias)return 6001;
    if(tags&&![tags isKindOfClass:NSNull.class]){
        int result=[JPUSHTagAliasValidation validateTags:tags isNew:NO];
        if(result)return result;
    }
    if(alias&&![alias isKindOfClass:NSNull.class]){
        int result=[JPUSHTagAliasValidation validateAlias:alias];
        if(result)return result;
    }
    return 0;
}

+ (int)validateTags:(id)tags isNew:(BOOL)isNew {
    if(![tags isKindOfClass:NSSet.class])return 6005;
    if([tags count]>1000)return 6007;
    NSUInteger bytes=0;
    for(id tag in tags){
        if(![tag isKindOfClass:NSString.class]||![tag length])return 6005;
        NSUInteger length=[tag lengthOfBytesUsingEncoding:NSUTF8StringEncoding];
        if(length>40)return 6006;
        if(![tag isValidTagAlias])return 6005;
        bytes+=length;
    }
    if(bytes>(isNew?5000:7000))return 6008;
    return 0;
}

+ (NSInteger)byteLength:(id)string {
    NSUInteger length=[string lengthOfBytesUsingEncoding:NSUnicodeStringEncoding];
    const char *bytes=[string cStringUsingEncoding:NSUnicodeStringEncoding];
    uint32_t nonzero=0;
    for(NSUInteger remaining=length;remaining;remaining--)if(*bytes++)nonzero++;
    uint64_t weighted=(length-nonzero)+(uint64_t)(uint32_t)(nonzero<<1);
    int64_t adjusted=(int64_t)(weighted+1);
    if(adjusted<0)adjusted=(int64_t)(weighted+2);
    return (NSInteger)(adjusted>>1);
}

+ (int)validateAlias:(id)alias {
    if(![alias isKindOfClass:NSString.class])return 6003;
    if([alias lengthOfBytesUsingEncoding:NSUTF8StringEncoding]>40)return 6004;
    return [alias isValidTagAlias]?0:6003;
}

+ (NSSet *)filterValidTags:(id)tags {
    if(![tags isKindOfClass:NSSet.class])return nil;
    NSMutableSet *result=[[NSMutableSet alloc] initWithCapacity:[tags count]];
    int count=0;
    NSUInteger length=0;
    for(id tag in tags){
        if(count>999)break;
        if([tag isKindOfClass:NSString.class]&&[tag length]&&[tag lengthOfBytesUsingEncoding:NSUTF8StringEncoding]<41&&[tag isValidTagAlias]){
            length+=[tag length]+2;
            if(length>0x18f7e)break;
            count++;
            [result addObject:tag];
        }
    }
    return result;
}
@end
#endif
