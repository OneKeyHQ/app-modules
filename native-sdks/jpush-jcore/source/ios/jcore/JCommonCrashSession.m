#import "JCommonCrashSession.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
@implementation JCommonCrashSession
@synthesize flagInit = _init, duration = _duration, distinctId = _distinctId;
@dynamic hash, superclass, description, debugDescription;
- (instancetype)initWithReleaseName:(NSString *)name {
    self = [self initDefault];
    if (self)
        _releaseName = name;
    return self;
}
- (instancetype)initDefault {
    self = [super init];
    if (self) {
        _sessionId = [NSUUID UUID];
        _started = [NSDate date];
        _status = 0;
        _errors = 0;
        _sequence = (NSUInteger)[JCOREUtilities currentTimestamp];
        _init = @YES;
    }
    return self;
}
- (NSMutableDictionary *)serialize {
    @synchronized(self) {
        NSMutableDictionary *result = [@{
            @"sid" : _sessionId.UUIDString,
            @"errors" : @(_errors),
            @"started" : [JCOREUtilities dateToIso8601String:_started]
        } mutableCopy];
        if (_status < 4)
            [result setValue:@[ @"ok", @"exited", @"crashed", @"abnormal" ][_status] forKey:@"status"];
        else
            [JCORELog log:YES
                    level:0x3ff
                     flag:8
                  context:0
                     file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/"
                          "JCommon/JCommon/CrashLib/JCommonCrashSession.m"
                 function:__PRETTY_FUNCTION__
                     line:64
                      tag:nil
                   format:@"Missing string for SessionStatus when serializing "
                          @"SentrySession."];
        if (_status != 2)
            [result setValue:_init forKey:@"init"];
        if (!_timestamp)
            _timestamp = [NSDate date];
        [result setValue:[JCOREUtilities dateToIso8601String:_timestamp] forKey:@"timestamp"];
        [result setValue:@(_sequence) forKey:@"seq"];
        if (_releaseName) {
            NSMutableDictionary *attrs = [NSMutableDictionary new];
            if (_releaseName)
                [attrs setValue:_releaseName forKey:@"release"];
            [result setValue:attrs forKey:@"attrs"];
        }
        [result setValue:self.distinctId forKey:@"did"];
        if (_status != 2 && _timestamp && _started) {
            _duration = @((int)[_timestamp timeIntervalSinceDate:_started]);
            [result setValue:self.duration forKey:@"duration"];
        }
        return result;
    }
}
- (id)copyWithZone:(NSZone *)zone {
    JCommonCrashSession *copy = [[[self class] allocWithZone:zone] init];
    if (copy) {
        copy->_sessionId = _sessionId;
        copy->_started = _started;
        copy->_status = _status;
        copy->_errors = _errors;
        copy->_sequence = _sequence;
        copy->_distinctId = _distinctId;
        copy->_timestamp = _timestamp;
        copy->_releaseName = _releaseName;
    }
    return copy;
}
- (NSString *)distinctId {
    id value = _distinctId;
    if (!value) {
        @synchronized(self) {
            value = _distinctId;
            if (!value) {
                NSString *path =
                    [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES).firstObject
                        stringByAppendingPathComponent:@"INSTALLATION"];
                NSData *data = [NSData dataWithContentsOfFile:path];
                if (!data) {
                    _distinctId = [NSUUID UUID].UUIDString;
                    data = [_distinctId dataUsingEncoding:NSUTF8StringEncoding];
                    [[NSFileManager defaultManager] createFileAtPath:path contents:data attributes:nil];
                } else
                    _distinctId = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
                value = _distinctId;
            }
        }
    }
    return value;
}
@end
