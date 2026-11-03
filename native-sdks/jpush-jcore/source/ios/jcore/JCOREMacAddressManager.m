#import "JCOREMacAddressManager.h"
#import "RecoveredTransportDependencies.h"
#import <arpa/inet.h>
#import <arpa/nameser.h>
#import <ifaddrs.h>
#import <netdb.h>
#import <resolv.h>
#import <stdint.h>
#import <stdlib.h>
#import <string.h>

#define REC_MAC_LOG(lineValue,fmt,...) [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Mac/JCOREMacAddressManager.m" function:__PRETTY_FUNCTION__ line:(lineValue) tag:nil format:(fmt), ##__VA_ARGS__]

@implementation JCOREMacAddressManager
+ (void)getDeviceMacFromMDNS:(void (^ __unsafe_unretained)(NSString *))callback {
    id retainedCallback=(__bridge id)(__bridge void *)callback;
    @try {
    res_init();
    NSString *address=[self currentIPAddressOf:@"en0"];
    if(!address||![address length]){
        REC_MAC_LOG(0x1e,@"could not get current IP address of en0\n");
        ((void (^)(NSString *))retainedCallback)(nil);
        return;
    }
    _res.nsaddr_list[0].sin_family=AF_INET;
    _res.nsaddr_list[0].sin_port=htons(5353);
    _res.nsaddr_list[0].sin_addr.s_addr=[self IPv4Pton:address];
    _res.nscount=1;
    unsigned char answer[512];
    int result=res_query("_apple-mobdev2._tcp.local",ns_c_in,ns_t_ptr,answer,sizeof(answer));
    if(result<0){REC_MAC_LOG(0x2c,@"res_search(): %s\n",hstrerror(h_errno));((void (^)(NSString *))retainedCallback)(nil);return;}
    ns_msg message;
    if(ns_initparse(answer,result,&message)<0){REC_MAC_LOG(0x33,@"ns_initparse(): %s\n",hstrerror(h_errno));((void (^)(NSString *))retainedCallback)(nil);return;}
    NSString *mac=nil;
    for(unsigned int remaining=(unsigned short)ns_msg_count(message,ns_s_an);remaining;remaining--){
        ns_rr record;
        // The original parser repeatedly requests answer zero rather than advancing the index.
        ns_parserr(&message,ns_s_an,0,&record);
        if(record.rr_class==ns_c_in&&record.type==ns_t_ptr&&strcmp(record.name[0]?record.name:".","_apple-mobdev2._tcp.local")==0){
            const char *start=(const char *)record.rdata+1;
            size_t length=strcspn(start,"@");
            void *buffer=calloc((size_t)(int32_t)((uint32_t)length+1),1);
            if(buffer){memmove(buffer,start,(size_t)(int32_t)(uint32_t)length);mac=[NSString stringWithUTF8String:buffer];free(buffer);}
        }
    }
    if(![mac length]||[mac isEqualToString:@"02:00:00:00:00:00"])((void (^)(NSString *))retainedCallback)(nil);
    else ((void (^)(NSString *))retainedCallback)(mac);
    } @catch(NSException *exception) { REC_MAC_LOG(0x58,@"get device mac address error:%@",exception); }
}

+ (NSString *)currentIPAddressOf:(NSString *)interfaceName {
    struct ifaddrs *interfaces;
    if(getifaddrs(&interfaces)!=0)return nil;
    NSString *result=nil;
    for(struct ifaddrs *item=interfaces;item;item=item->ifa_next){
        if(strcmp(item->ifa_name,[interfaceName UTF8String])==0&&item->ifa_addr&&item->ifa_addr->sa_family==AF_INET){
            result=[self IPv4Ntop:((struct sockaddr_in *)item->ifa_addr)->sin_addr.s_addr];
            break;
        }
    }
    freeifaddrs(interfaces);
    return result;
}

+ (NSString *)IPv4Ntop:(unsigned int)address {
    char buffer[16]={0};
    if(inet_ntop(AF_INET,&address,buffer,sizeof(buffer)))return [NSString stringWithUTF8String:buffer];
    return nil;
}

+ (unsigned int)IPv4Pton:(NSString *)address {
    unsigned int value=UINT32_MAX;
    if(address&&[address length]&&inet_pton(AF_INET,[address UTF8String],&value)==1)return value;
    return UINT32_MAX;
}
@end
