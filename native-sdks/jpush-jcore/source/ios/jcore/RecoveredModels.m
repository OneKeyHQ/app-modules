#import "RecoveredModels.h"

@implementation JCORECacheModel
@synthesize type = _type;
@synthesize key = _key;
@synthesize dirName = _dirName;
@synthesize encrypt = _encrypt;
@synthesize compress = _compress;
@dynamic content;
@dynamic hash;
@dynamic superclass;
@dynamic description;
@dynamic debugDescription;

@end

@implementation JCORECacheReadModel
@synthesize block = _block;

@end

@implementation JCORECacheWriteModel
@synthesize content = _content;
@synthesize block = _block;

@end

@implementation JCommonCrashContexts
@synthesize app = _app;
@synthesize device = _device;
@synthesize os = _os;
@dynamic hash;
@dynamic superclass;
@dynamic description;
@dynamic debugDescription;
- (NSMutableDictionary *)serialize {
    NSMutableDictionary *result = [NSMutableDictionary new];
    [result setValue:self.app forKey:@"app"];
    [result setValue:self.device forKey:@"device"];
    [result setValue:self.os forKey:@"os"];
    return result;
}
@end

@implementation JCommonCrashException
@synthesize value = _value;
@synthesize type = _type;
@synthesize threadId = _threadId;
@synthesize stacktrace = _stacktrace;
@dynamic hash;
@dynamic superclass;
@dynamic description;
@dynamic debugDescription;
- (instancetype)initWithValue:(NSString *)value type:(NSString *)type {
    self = [super init];
    if (self) { self.value = value; self.type = type; }
    return self;
}
- (NSMutableDictionary *)serialize {
    NSMutableDictionary *result = [NSMutableDictionary new];
    [result setValue:self.value forKey:@"value"];
    [result setValue:self.type forKey:@"type"];
    [result setValue:self.threadId forKey:@"thread_id"];
    [result setValue:[(id<RecoverySerializable>)self.stacktrace serialize] forKey:@"stacktrace"];
    return result;
}
@end

@implementation JCommonCrashFrame
@synthesize symbolAddress = _symbolAddress;
@synthesize function = _function;
@synthesize package = _package;
@synthesize imageAddress = _imageAddress;
@synthesize instructionAddress = _instructionAddress;
@synthesize inApp = _inApp;
@dynamic hash;
@dynamic superclass;
@dynamic description;
@dynamic debugDescription;
- (instancetype)init {
    self = [super init];
    if (self) self.function = @"<redacted>";
    return self;
}
- (NSMutableDictionary *)serialize {
    NSMutableDictionary *result = [NSMutableDictionary new];
    [result setValue:self.symbolAddress forKey:@"symbol_addr"];
    [result setValue:self.function forKey:@"function"];
    [result setValue:self.package forKey:@"package"];
    [result setValue:self.imageAddress forKey:@"image_addr"];
    [result setValue:self.instructionAddress forKey:@"instruction_addr"];
    [result setValue:self.inApp forKey:@"in_app"];
    return result;
}
@end

@implementation JCommonCrashId
@synthesize uuid = _uuid;
static JCommonCrashId *sharedCrashId;
+ (instancetype)shareInstance {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{ sharedCrashId = [[super allocWithZone:NULL] init]; });
    return sharedCrashId;
}
+ (instancetype)allocWithZone:(NSZone *)zone { return [JCommonCrashId shareInstance]; }
- (id)copyWithZone:(NSZone *)zone { return [JCommonCrashId shareInstance]; }
- (instancetype)init { return [self initWithUUID:[NSUUID UUID]]; }
- (instancetype)initWithUUID:(NSUUID *)uuid {
    self = [super init];
    if (self) self.uuid = uuid;
    return self;
}
- (instancetype)initWithUUIDString:(NSString *)uuidString {
    NSString *formatted = uuidString;
    if (uuidString.length == 32) {
        NSMutableString *hyphenated = [uuidString mutableCopy];
        for (NSNumber *index in @[@8, @13, @18, @23]) [hyphenated insertString:@"-" atIndex:index.unsignedIntegerValue];
        formatted = hyphenated;
    } else if (uuidString.length != 36) {
        return [self initWithUUIDString:@"00000000-0000-0000-0000-000000000000"];
    }
    NSUUID *uuid = [[NSUUID alloc] initWithUUIDString:formatted];
    return uuid ? [self initWithUUID:uuid] : [self initWithUUIDString:@"00000000-0000-0000-0000-000000000000"];
}
- (NSString *)sentryIdString { return [[self.uuid.UUIDString stringByReplacingOccurrencesOfString:@"-" withString:@""] lowercaseString]; }
- (NSString *)description { return [self sentryIdString]; }
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    return [self class] == [object class] && [self.uuid isEqual:[object uuid]];
}
- (NSUInteger)hash { return self.uuid.hash; }
@end

@implementation JCommonCrashThread
@synthesize threadId = _threadId;
@synthesize name = _name;
@synthesize stacktrace = _stacktrace;
@synthesize crashed = _crashed;
@synthesize current = _current;
@dynamic hash;
@dynamic superclass;
@dynamic description;
@dynamic debugDescription;
- (instancetype)initWithThreadId:(NSNumber *)threadId {
    self = [super init];
    if (self) self.threadId = threadId;
    return self;
}
- (NSMutableDictionary *)serialize {
    NSMutableDictionary *result = [@{@"id": self.threadId ?: @99} mutableCopy];
    [result setValue:self.crashed forKey:@"crashed"];
    [result setValue:self.current forKey:@"current"];
    [result setValue:self.name forKey:@"name"];
    [result setValue:[(id<RecoverySerializable>)self.stacktrace serialize] forKey:@"stacktrace"];
    return result;
}
@end

@implementation JCommonDataConfig
@synthesize type = _type;
- (instancetype)init { return [super init]; }
@end

@implementation JCommonDataControl
@synthesize configMap = _configMap;
@synthesize idfaSwitch = _idfaSwitch;
static JCommonDataControl *sharedDataControl;
+ (instancetype)sharedManager {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{ sharedDataControl = [[JCommonDataControl alloc] init]; });
    return sharedDataControl;
}
- (NSMutableDictionary *)configMap {
    if (!_configMap) _configMap = [[NSMutableDictionary alloc] init];
    return _configMap;
}
- (void)addConfig:(id)config {
    NSString *key = [NSString stringWithFormat:@"%ld", (long)[(id<RecoveryDataConfiguration>)config type]];
    [self.configMap setValue:config forKey:key];
}
- (void)clearConfig { [self.configMap removeAllObjects]; }
- (BOOL)getEnable:(NSUInteger)type {
    NSString *key = [NSString stringWithFormat:@"%ld", (long)type];
    return [self.configMap valueForKey:key] != nil;
}
// The original getter derives consent from config presence and does not read the setter-backed ivar.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
- (BOOL)idfaSwitch { return [self getEnable:10000]; }
#pragma clang diagnostic pop
@end

@implementation JCommonReport
@synthesize request = _request;
@synthesize requestInfo = _requestInfo;
@synthesize completionHandler = _completionHandler;
@synthesize controlFlag = _controlFlag;
@synthesize accountIDRequired = _accountIDRequired;
@synthesize cacheRequired = _cacheRequired;
@synthesize reportFlag = _reportFlag;
@dynamic addressList;
@dynamic hash;
@dynamic superclass;
@dynamic description;
@dynamic debugDescription;
// Original getters return YES regardless of the setter-backed storage.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
- (BOOL)accountIDRequired { return YES; }
- (BOOL)cacheRequired { return YES; }
#pragma clang diagnostic pop
@end

