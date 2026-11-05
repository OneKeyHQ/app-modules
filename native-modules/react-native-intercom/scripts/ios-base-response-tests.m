#import <Foundation/Foundation.h>
#import "ICMBaseResponse.h"
static NSMutableArray *passed, *events;
static void check(BOOL value, NSString *name) { if (!value) {fprintf(stderr,"FAIL: %s\n",name.UTF8String);exit(1);} [passed addObject:name]; }
@interface FixtureValue : NSObject
@property (nonatomic, strong) NSDictionary *dictionary;
@end
@implementation FixtureValue
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary { [events addObject:NSStringFromClass(self)]; FixtureValue *value=[self new];value.dictionary=dictionary;return value; }
- (BOOL)isEqual:(id)value { return [value isKindOfClass:FixtureValue.class] && [self.dictionary isEqual:[value dictionary]]; }
- (NSUInteger)hash { return self.dictionary.hash; }
@end
@implementation ICMUnreadConversationsResponse
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary { return (id)[FixtureValue instanceFromDictionary:dictionary]; }
@end
@implementation ICMUnreadTicketsResponse
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary { return (id)[FixtureValue instanceFromDictionary:dictionary]; }
@end
@implementation ICMCarousel
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary { return (id)[FixtureValue instanceFromDictionary:dictionary]; }
@end
static BOOL failSurvey;
@implementation ICMRecoveredSurvey
- (instancetype)initWithDictionary:(NSDictionary *)dictionary { [events addObject:@"survey.init"];if(failSurvey)return nil; (void)dictionary;return [super init]; }
@end
__attribute__((objc_runtime_name("ICMUserIdentity")))
@interface FixtureUserIdentity : FixtureValue
@property (nonatomic) BOOL hasConversations;
@end
@implementation FixtureUserIdentity
- (void)setHasConversations:(BOOL)value { [events addObject:@"has_conversations"]; _hasConversations=value; }
@end
__attribute__((objc_runtime_name("ICMConfig")))
@interface FixtureConfig : NSObject
+ (void)updateSharedInstanceFromDictionary:(NSDictionary *)dictionary;
+ (instancetype)sharedConfig;
- (double)newSessionThreshold;
@end
static id configInput;
@implementation FixtureConfig
+ (void)updateSharedInstanceFromDictionary:(NSDictionary *)dictionary { configInput=dictionary;[events addObject:@"config.update"]; }
+ (instancetype)sharedConfig { [events addObject:@"config.shared"];return [self new]; }
- (double)newSessionThreshold { [events addObject:@"config.threshold"];return 42.5; }
@end
__attribute__((objc_runtime_name("ICMIdentityStore")))
@interface FixtureIdentityStore : NSObject
@property (nonatomic, strong) FixtureUserIdentity *userIdentity;
+ (instancetype)currentIdentity;
@end
static FixtureIdentityStore *store;
@implementation FixtureIdentityStore
+ (instancetype)currentIdentity { [events addObject:@"identity.current"];return store; }
@end
__attribute__((objc_runtime_name("ICMHTTPBridge")))
@interface FixtureHTTPBridge : NSObject
@end
static NSUInteger surveyFailures;
static id failedIdentifier;
static NSInteger failedProgress;
@implementation FixtureHTTPBridge
+ (void)failSurvey:(id)identifier progressId:(NSInteger)progress success:(void (^)(void))success failure:(void (^)(NSError *))failure {
 [events addObject:@"survey.fail"];surveyFailures++;failedIdentifier=identifier;failedProgress=progress;success();failure(nil);
}
@end
@interface FixtureResponseSubclass : ICMBaseResponse
@end
@implementation FixtureResponseSubclass
@end
int main(void) {
 @autoreleasepool {
    passed=[NSMutableArray new];events=[NSMutableArray new];store=[FixtureIdentityStore new];store.userIdentity=[FixtureUserIdentity new];
    NSDictionary *config=@{@"real_time_config":@{@"endpoints":@[@"https://synthetic.invalid"]},@"is_first_request":@YES};
    ICMBaseResponse *response=[ICMBaseResponse instanceFromDictionary:@{@"config":config,@"has_conversations":@YES}];
    check(events.count>=5 && [events.firstObject isEqual:@"config.update"] && [events.lastObject isEqual:@"has_conversations"] && configInput==config,@"factory applies shared config before parsing and current identity state after parsing");
    check(response.isFirstRequest && [response.realtimeConfig.backgroundConnectionTimeout isEqual:@42.5] && [response.realtimeConfig.endpoints.firstObject.absoluteString isEqual:@"wss://synthetic.invalid?X-Nexus-Version=ios.5.5.0"],@"config parses actual Nexus and overwrites background timeout from shared configuration");
    check(store.userIdentity.hasConversations,@"NSNumber has_conversations updates current identity");
    [events removeAllObjects];[ICMBaseResponse instanceFromDictionary:@{@"has_conversations":@"NO"}];
    check(store.userIdentity.hasConversations && [events isEqual:@[@"config.update"]],@"wrong-type has_conversations leaves current identity untouched but still updates nil config");
    [ICMBaseResponse instanceFromDictionary:@{@"has_conversations":@0}];check(!store.userIdentity.hasConversations,@"numeric false updates original current identity state");
    [events removeAllObjects];ICMBaseResponse *nilResponse=[ICMBaseResponse instanceFromDictionary:nil];
    check(nilResponse && [events isEqual:@[@"config.update"]],@"nil factory input still calls config update and produces empty response");
    BOOL threw=NO; @try { [ICMBaseResponse instanceFromDictionary:(id)[NSObject new]]; } @catch(NSException *exception){threw=[exception.name isEqual:NSInvalidArgumentException];}
    check(threw,@"factory preserves unguarded input keyed-subscript exception");
    check([[FixtureResponseSubclass instanceFromDictionary:nil] class]==ICMBaseResponse.class,@"factory invoked on subclass allocates concrete original response");
    [events removeAllObjects];[response setAttributesFromDictionary:@{@"config":@{@"is_first_request":@"NO",@"real_time_config":@[]}}];
    check(response.isFirstRequest && [response.realtimeConfig.backgroundConnectionTimeout isEqual:@42.5] && events.count==0,@"wrong-type config subfields preserve original values without shared read");
    [response setValue:@{@"fixture":@1} forKey:@"unread_conversations"];[response setValue:@{@"fixture":@2} forKey:@"unread_tickets"];[response setValue:@{@"fixture":@3} forKey:@"user"];[response setValue:@{@"fixture":@4} forKey:@"carousel"];
    check(response.unreadConversations && response.unreadTickets && response.user && response.carousel,@"recognized dictionaries invoke their distinct model dependency factories");
    id previous=response.user;[response setValue:[NSNull null] forKey:@"user"];[response setValue:previous forKey:@"user"];
    check(response.user==previous,@"user KVC branch rejects non-dictionary even existing user instance");
    response.user=(id)previous;check(response.user==previous,@"direct retained setter remains unvalidated");
    [response setValue:@NO forKey:@"isFirstRequest"];[response setValue:nil forKey:@"isFirstRequest"];
    check(!response.isFirstRequest,@"unknown special branches use super KVC and original safe nil scalar handler");
    ICMSafeNetworkModel *arbitrary=(id)[NSObject new];[response setValue:arbitrary forUndefinedKey:@"unread_conversations"];
    check((id)response.unreadConversations==arbitrary,@"explicit undefined unread snake key remaps to unvalidated camel setter");
    [response setValue:@1 forKey:@"unknown_ignored"];
    check(YES,@"unknown KVC field is ignored by original undefined handler");
    failSurvey=NO;[response setValue:@{@"fixture":@1} forKey:@"survey"];
    check(response.survey && surveyFailures==0,@"successful actual survey initializer dependency prevents failure request");
    failSurvey=YES;NSString *surveyID=@"synthetic-survey";[response setValue:@{@"id":surveyID,@"survey_progress_id":@"-7suffix"} forKey:@"survey"];
    check(!response.survey && surveyFailures==1 && failedIdentifier==surveyID && failedProgress==-7,@"nil survey parser invokes original failure with string integerValue and negative nonzero progress");
    [response setValue:@{@"id":surveyID,@"survey_progress_id":@0} forKey:@"survey"];[response setValue:@{@"survey_progress_id":@1} forKey:@"survey"];
    check(surveyFailures==1,@"zero progress or missing ID suppresses survey failure request");
    threw=NO;@try{[response setValue:@{@"id":surveyID,@"survey_progress_id":[NSNull null]} forKey:@"survey"];}@catch(NSException *exception){threw=[exception.name isEqual:NSInvalidArgumentException];}
    check(threw,@"nil survey parser preserves NSNull progress integerValue exception");
    ICMBaseResponse *first=[ICMBaseResponse new],*second=[ICMBaseResponse new];
    check([first isEqualToResponse:nil] && ![first isEqual:nil] && [first isEqual:second],@"typed empty equality may equal nil while generic equality rejects nil");
    first.unreadTickets=(id)[NSObject new];first.survey=(id)[NSObject new];first.realtimeConfig=[IntercomNexusConfiguration new];
    check([first isEqual:second] && first.hash==0,@"equality and hash intentionally ignore tickets realtime and survey");
    first.user=(id)[FixtureValue instanceFromDictionary:@{@"fixture":@1}];second.user=(id)[FixtureValue instanceFromDictionary:@{@"fixture":@1}];
    first.unreadConversations=(id)[FixtureValue instanceFromDictionary:@{@"fixture":@2}];second.unreadConversations=(id)[FixtureValue instanceFromDictionary:@{@"fixture":@2}];
    check([first isEqual:second] && first.hash==first.unreadConversations.hash*31+first.user.hash,@"typed equality uses model isEqual and unsigned original hash relation");
    second.isFirstRequest=YES;check(![first isEqual:second],@"first request bit participates in equality");
    check(![[ICMBaseResponse new] isEqual:[FixtureResponseSubclass new]] && [[ICMBaseResponse new] isEqualToResponse:[FixtureResponseSubclass new]],@"generic equality exact class differs from typed direct-selector comparison");
    NSData *json=[NSJSONSerialization dataWithJSONObject:@{@"status":@"passed",@"testCount":@(passed.count),@"tests":passed,@"scope":@"Actual BaseResponse/Nexus/SafeNetworkModel; explicit Config/Store/model/HTTPBridge diagnostic dependencies, product UNLINKED"} options:NSJSONWritingPrettyPrinted error:nil];fwrite(json.bytes,1,json.length,stdout);fputc('\n',stdout);
 }
 return 0;
}
