#import <Foundation/Foundation.h>
#if __has_include("JGCOREAPI.h")
#import "JGCOREAPI.h"
#import "JGInforCollectionAuth.h"
#else
#import "../include/JGCOREAPI.h"
#import "../include/JGInforCollectionAuth.h"
#endif

NS_ASSUME_NONNULL_BEGIN

@interface JCOREConfiguration : NSObject

+ (instancetype)shared;

@property (nonatomic, copy, nullable) NSString *appKey;
@property (nonatomic, copy) NSString *channel;
@property (nonatomic, assign) BOOL isProduction;
@property (nonatomic, copy, nullable) NSString *advertisingIdentifier;
@property (nonatomic, copy, nullable) NSString *accountID;

@property (nonatomic, assign) uint64_t uid;
@property (nonatomic, copy, nullable) NSString *password;
@property (nonatomic, copy, nullable) NSString *registrationID;
@property (nonatomic, copy, nullable) NSString *deviceID;
@property (nonatomic, assign) BOOL isNewDevice;
@property (nonatomic, assign) uint32_t lastRegisterSeed;

@property (nonatomic, copy, nullable) NSString *deviceTokenHex;
@property (nonatomic, copy, nullable) NSString *voipTokenHex;

@property (nonatomic, assign) uint8_t buildType;
@property (nonatomic, assign) uint8_t apsType;
@property (nonatomic, assign) uint8_t encryptionAlgorithm;

@property (nonatomic, assign) BOOL collectionAuthorized;
@property (nonatomic, assign) BOOL pushStopped;
@property (nonatomic, assign) BOOL debugEnabled;
@property (nonatomic, assign) BOOL logOff;
@property (nonatomic, assign) BOOL preferIPv6;
@property (nonatomic, assign) NSTimeInterval heartbeatInterval;

@property (nonatomic, assign) NSTimeInterval reconnectBaseInterval;
@property (nonatomic, assign) NSTimeInterval requestTimeoutInterval;
@property (nonatomic, assign) NSTimeInterval requestResendInterval;
@property (nonatomic, assign) NSTimeInterval connectTimeoutInterval;

@property (nonatomic, copy, nullable) NSString *httpReportUrl;

@property (nonatomic, copy) NSArray<NSString *> *sisHostDomains;
@property (nonatomic, copy) NSArray<NSString *> *sisDefaultIps;
@property (nonatomic, copy) NSArray<NSString *> *httpsSisUrls;
@property (nonatomic, copy) NSString *defaultConnectHost;
@property (nonatomic, copy) NSArray<NSNumber *> *defaultConnectPorts;

@property (nonatomic, copy, nullable, readonly) NSString *customConnectHost;
@property (nonatomic, assign, readonly) uint16_t customConnectPort;
@property (nonatomic, assign, readonly) BOOL customConnectUseSSL;
@property (nonatomic, copy, nullable) NSArray<NSString *> *customConnectAddresses;

// Persisted SIS resolution state
@property (nonatomic, copy, readonly) NSArray<NSString *> *ips;
@property (nonatomic, copy, readonly) NSArray<NSString *> *sslIps;
@property (nonatomic, copy, readonly) NSArray<NSString *> *sisIps;
@property (nonatomic, copy, readonly) NSArray<NSString *> *httpReportAddresses;
@property (nonatomic, copy, readonly) NSArray<NSString *> *httpsReportAddresses;
@property (nonatomic, copy, readonly) NSArray<NSString *> *userAddresses;
@property (nonatomic, copy, readonly) NSArray<NSString *> *sslUserAddresses;
@property (nonatomic, copy, readonly) NSDictionary<NSString *, NSArray<NSString *> *> *tcpReportAddresses;
@property (nonatomic, assign, readonly) NSInteger certVer;
@property (nonatomic, assign, readonly) BOOL dataReport;
@property (nonatomic, copy, readonly) NSArray<NSString *> *lastSuccessSisIps;
@property (nonatomic, copy, readonly) NSArray<NSString *> *failedIps;

- (void)configureWithAppKey:(NSString *)appKey
                    channel:(nullable NSString *)channel
               isProduction:(BOOL)isProduction
      advertisingIdentifier:(nullable NSString *)advertisingId;

- (void)setCustomConnectHost:(nullable NSString *)host
                        port:(uint16_t)port
                      useSSL:(BOOL)useSSL;
- (void)clearCustomConnectEndpoint;

- (void)updateRegistrationWithUid:(uint64_t)uid
                         password:(nullable NSString *)password
                   registrationID:(nullable NSString *)registrationID;
- (void)updateRegistrationWithUid:(uint64_t)uid
                         password:(nullable NSString *)password
                   registrationID:(nullable NSString *)registrationID
                         deviceID:(nullable NSString *)deviceID;
- (void)resetRegistration;

- (void)updateWithSisDictionary:(NSDictionary *)sisDict;
- (void)addFailedIp:(NSString *)ipPort;
- (void)clearFailedIps;
- (void)addLastSuccessSisIp:(NSString *)ipPort;
- (void)resetSisCache;

- (NSString *)fakeIMEI;
- (NSString *)resolveDeviceIDWithOutHasExisting:(BOOL *_Nullable)outHasExisting;
- (NSString *)bundleIdentifierString;
- (NSString *)appVersionString;
- (NSString *)osVersionString;
- (NSString *)modelNameString;

- (NSString *)fallbackRegistrationID;
- (void)ensureLocalRegistrationFallbackIfNeeded;

- (NSSet<NSString *> *)storedTags;
- (void)saveStoredTags:(NSSet<NSString *> *)tags;

- (nullable NSString *)storedAlias;
- (void)saveStoredAlias:(nullable NSString *)alias;

- (NSDictionary<NSString *, id> *)storedProperties;
- (void)saveStoredProperties:(NSDictionary<NSString *, id> *)properties;

- (nullable NSString *)storedMobileNumber;
- (void)saveStoredMobileNumber:(nullable NSString *)mobileNumber;

- (uint16_t)nextRequestId;

- (void)log:(NSString *)format, ... NS_FORMAT_FUNCTION(1, 2);

+ (void)logDebug:(NSString *)message;
+ (void)logInfo:(NSString *)message;
+ (void)logWarn:(NSString *)message;
+ (void)logError:(NSString *)message;
+ (NSArray<NSString *> *)recentLogs;
+ (void)clearRecentLogs;

@end

@interface JCOREPersistence : JCOREConfiguration
@end

NS_ASSUME_NONNULL_END
