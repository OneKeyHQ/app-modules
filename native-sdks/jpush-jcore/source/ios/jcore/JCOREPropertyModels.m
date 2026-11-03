#import "JCOREPropertyModels.h"

@implementation JCOREConfigModel
@synthesize appKey = appKey;
@synthesize channel = channel;
@synthesize advertisingId = advertisingId;
@synthesize isProduction = isProduction;
@synthesize completionHandler = completionHandler;
@dynamic index, hash, superclass, description, debugDescription;
@end

@implementation JCOREHttpSisCompletion
@synthesize type = _type;
@synthesize timer = _timer;
@synthesize completion = _completion;
@end

@implementation JCOREIntegrateInfo
@synthesize index = _index;
@synthesize property = _property;
@synthesize version = _version;
@synthesize type = _type;
@synthesize regIndex = _regIndex;
@synthesize loginIndex = _loginIndex;
@synthesize business = _business;
@synthesize tcpKeepAlive = _tcpKeepAlive;
@synthesize versionFieldName = _versionFieldName;
@dynamic loginVersion, registerVersion, hash, superclass, description, debugDescription;
@end

@implementation JCORESupersetBaseMoniter
@synthesize uuid = _uuid;
@synthesize moniter_id = _moniter_id;
@synthesize timestamp = _timestamp;
@synthesize moniter_type = _moniter_type;
@end

@implementation JCORETcpConnectModel
@synthesize protocolType = protocolType;
@synthesize statusCode = statusCode;
@synthesize socketConfig = socketConfig;
@dynamic hash, superclass, description, debugDescription;
@end

@implementation JCORETcpEventModel
@synthesize bodyData = _bodyData;
@synthesize statusCode = statusCode;
@synthesize moniter_id = moniter_id;
@synthesize version = version;
@synthesize protocolType = protocolType;
@synthesize algorithm = algorithm;
@synthesize rid = rid;
@synthesize uid = uid;
@synthesize msgData = msgData;
@dynamic hash, superclass, description, debugDescription;
@end
