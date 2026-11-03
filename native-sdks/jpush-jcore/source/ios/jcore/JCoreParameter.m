#import "JCoreParameter.h"

@implementation JCoreParameter
@synthesize jcoreHasSetUp = _jcoreHasSetUp;
@synthesize deviceModelEnable = _deviceModelEnable;
@synthesize systemVersionEnable = _systemVersionEnable;
@synthesize resolutionEnable = _resolutionEnable;
@synthesize systemLanguageEnable = _systemLanguageEnable;
@synthesize systemNameEnable = _systemNameEnable;
@synthesize operatorNameEnable = _operatorNameEnable;
@synthesize wifiInfoEnable = _wifiInfoEnable;
@synthesize SSIDEnable = _SSIDEnable;
@synthesize BSSIDEnable = _BSSIDEnable;
@synthesize cellEnabel = _cellEnabel;
@synthesize locationEnable = _locationEnable;
@synthesize applistEnable = _applistEnable;
@synthesize heartBeatTimeInterval = _heartBeatTimeInterval;
@synthesize backgroundEnable = _backgroundEnable;

static JCoreParameter *sharedParameter;

+ (instancetype)shared {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedParameter = [[super allocWithZone:NULL] init];
    });
    return sharedParameter;
}

+ (instancetype)allocWithZone:(NSZone *)zone {
    return [JCoreParameter shared];
}

- (id)copyWithZone:(NSZone *)zone {
    return [JCoreParameter shared];
}

- (instancetype)init {
    self = [super init];
    if (self) {
        _deviceModelEnable = YES;
        _systemVersionEnable = YES;
        _resolutionEnable = YES;
        _systemLanguageEnable = YES;
        _systemNameEnable = YES;
        _operatorNameEnable = YES;
        _wifiInfoEnable = YES;
        _SSIDEnable = YES;
        _BSSIDEnable = YES;
        _locationEnable = YES;
        _applistEnable = YES;
        _locationLock = dispatch_semaphore_create(1);
    }
    return self;
}

- (void)setLocationEnable:(BOOL)value {
    dispatch_semaphore_wait(_locationLock, DISPATCH_TIME_FOREVER);
    _locationEnable = value;
    dispatch_semaphore_signal(_locationLock);
    [[NSNotificationCenter defaultCenter] postNotificationName:@"JCoreParameterLocationOff" object:@(value)];
}

- (void)setApplistEnable:(BOOL)value {
    _applistEnable = value;
    [[NSNotificationCenter defaultCenter] postNotificationName:@"JCoreParameterAppListOff" object:@(value)];
}
@end
