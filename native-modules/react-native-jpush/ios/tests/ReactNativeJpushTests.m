#import <XCTest/XCTest.h>
#import "../include/JPUSHService.h"
#import "../include/JGInforCollectionAuth.h"
#import "../jcore/JCOREConfiguration.h"
#import "../jcore/JCORECrypto.h"
#import "../jcore/JCORESisResolver.h"
#import "../jcore/JCOREWireProtocol.h"
#import "../jpush/JPUSHNotificationManager.h"
#import "../bridge/RCTJPushEventQueue.h"
#import "../bridge/RCTJPushModule.h"

/**
 * Non-production demo test AppKey and sandbox bundle/package identifier used solely for automated native tests.
 * Never used in production environments.
 */
static NSString *const kDemoTestAppKey = @"1f2027d3cfb1b51dac664c42";
static NSString *const kDemoTestPackageName = @"com.devstudio.msgbench";

@interface ReactNativeJpushTests : XCTestCase
@end

@implementation ReactNativeJpushTests

- (void)testCryptoPrimitivesRoundtrip {
    NSString *seedKey = [JCORECrypto generateSeedKey:123456789ULL];
    XCTAssertEqual(seedKey.length, 32u);

    NSData *plain = [@"OneKey JPush iOS XCTest Payload" dataUsingEncoding:NSUTF8StringEncoding];
    const char *iv = "0102030405060708";

    NSData *aesEnc = [JCORECrypto aesCbcEncrypt:plain key:seedKey iv:iv keySize:32];
    NSData *aesDec = [JCORECrypto aesCbcDecrypt:aesEnc key:seedKey iv:iv keySize:32];
    XCTAssertEqualObjects(aesDec, plain);

    NSData *sm4Enc = [JCORECrypto sm4CBCEncryptData:plain withKey:seedKey iv:iv];
    NSData *sm4Dec = [JCORECrypto sm4CBCDecryptData:sm4Enc withKey:seedKey iv:iv];
    XCTAssertEqualObjects(sm4Dec, plain);

    NSData *gz = [JCORECrypto gzipCompress:plain];
    NSData *ungz = [JCORECrypto gzipDecompress:gz];
    XCTAssertEqualObjects(ungz, plain);
}

- (void)testUdpSis10BytePacketAndRegisterWithDemoTestAppKey {
    [[JCOREConfiguration shared] configureWithAppKey:kDemoTestAppKey
                                             channel:@"dev"
                                        isProduction:NO
                               advertisingIdentifier:nil];
    NSDictionary *sisReq = [[JCORESisResolver shared] buildSisRequestPayload];
    XCTAssertEqualObjects(sisReq[@"appkey"], kDemoTestAppKey);
    XCTAssertEqualObjects(sisReq[@"sdkver"], @"5.2.0");

    NSData *udpPacket = [JCORESisResolver assembleUdpSisPacketWithPayload:sisReq
                                                                    magic:'U'
                                                                     seed:0x123456];
    XCTAssertTrue(udpPacket.length > 10);
    NSDictionary *parsedSis = [JCORESisResolver parseUdpSisPacket:udpPacket];
    XCTAssertEqualObjects(parsedSis[@"appkey"], kDemoTestAppKey);

    NSData *regBody = [JCOREWireProtocol buildRegisterPayloadWithAppKey:kDemoTestAppKey
                                                                channel:@"dev"
                                                           bundleIdHash:kDemoTestPackageName];
    NSData *clientFrame = [JCOREWireProtocol encodeClientFrameWithCommand:JCOREWireCommandRegister
                                                                  version:30
                                                                requestId:1
                                                                sessionId:0x12345678
                                                                      uid:0
                                                                algorithm:1
                                                                 bodyData:regBody];
    XCTAssertTrue(clientFrame.length >= 24 && ((clientFrame.length - 24) % 16 == 0));

    NSMutableData *buf = [NSMutableData dataWithData:clientFrame];
    NSArray<JCOREWireFrame *> *decoded = [JCOREWireProtocol decodeClientFramesFromStreamBuffer:buf];
    XCTAssertEqual(decoded.count, 1u);
    XCTAssertEqual(decoded.firstObject.command, JCOREWireCommandRegister);
    XCTAssertEqual(decoded.firstObject.version, 30);
    XCTAssertEqualObjects(decoded.firstObject.bodyData, regBody);
}

- (void)testEmptyRegistrationIDBeforeServerRegistration {
    [[JCOREConfiguration shared] resetRegistration];
    XCTAssertEqualObjects([JPUSHService registrationID], @"");
    XCTAssertEqual([JCOREConfiguration shared].uid, 0ULL);
    XCTAssertEqualObjects([JCOREConfiguration shared].password ?: @"", @"");
}

- (void)testColdStartNotificationArrivedQueuePreservesEventTypeOnNitroFlush {
    [RCTJPushModule clearActiveModules];
    [[RCTJPushEventQueue sharedInstance]._notificationQueue removeAllObjects];
    [[RCTJPushEventQueue sharedInstance]._localNotificationQueue removeAllObjects];
    [RCTJPushModule ensureSharedNativeObservers];

    [[NSNotificationCenter defaultCenter] postNotificationName:J_APNS_NOTIFICATION_ARRIVED_EVENT
                                                        object:@{
                                                            @"_j_msgid": @"apns_arrived_cold_601",
                                                            @"aps": @{@"alert": @{@"title": @"ArrivedApns", @"body": @"Body"}},
                                                            @"customKey": @"customVal",
                                                        }];
    [[NSNotificationCenter defaultCenter] postNotificationName:J_LOCAL_NOTIFICATION_ARRIVED_EVENT
                                                        object:@{
                                                            @"messageID": @"local_arrived_cold_602",
                                                            @"title": @"ArrivedLocal",
                                                            @"content": @"LocalBody",
                                                            @"extras": @{@"k": @"v"},
                                                        }];
    XCTAssertEqual([RCTJPushEventQueue sharedInstance]._notificationQueue.count, 1u);
    XCTAssertEqual([RCTJPushEventQueue sharedInstance]._localNotificationQueue.count, 1u);

    NSMutableArray<NSDictionary *> *received = [NSMutableArray array];
    NSInteger listenerId = [RCTJPushModule registerNitroEventListener:^(NSString *eventName, NSString *payloadJson) {
        [received addObject:@{@"name": eventName ?: @"", @"json": payloadJson ?: @""}];
    }];
    XCTAssertEqual([RCTJPushEventQueue sharedInstance]._notificationQueue.count, 0u);
    XCTAssertEqual([RCTJPushEventQueue sharedInstance]._localNotificationQueue.count, 0u);
    XCTAssertEqual(received.count, 2u);
    XCTAssertEqualObjects(received[0][@"name"], @"NotificationEvent");
    XCTAssertTrue([received[0][@"json"] containsString:@"apns_arrived_cold_601"]);
    XCTAssertTrue([received[0][@"json"] containsString:@"notificationArrived"]);
    XCTAssertFalse([received[0][@"json"] containsString:@"_j_InternalNotificationName"]);
    XCTAssertEqualObjects(received[1][@"name"], @"LocalNotificationEvent");
    XCTAssertTrue([received[1][@"json"] containsString:@"local_arrived_cold_602"]);
    XCTAssertTrue([received[1][@"json"] containsString:@"notificationArrived"]);
    XCTAssertFalse([received[1][@"json"] containsString:@"_j_InternalNotificationName"]);

    [RCTJPushModule unregisterNitroEventListener:listenerId];
    [RCTJPushModule clearActiveModules];
}

- (void)testLocalLogEnabledBuffersWhenLogOffAndClearsWhenDisabled {
    [JCOREConfiguration clearRecentLogs];
    [JCOREConfiguration shared].logOff = YES;
    [JCOREConfiguration setLocalLogEnabled:YES];
    [JCOREConfiguration logInfo:@"local_log_while_off_01"];
    NSArray<NSString *> *logs = [JCOREConfiguration recentLogs];
    XCTAssertEqual(logs.count, 1u);
    XCTAssertTrue([logs.firstObject containsString:@"local_log_while_off_01"]);

    [JCOREConfiguration setLocalLogEnabled:NO];
    XCTAssertEqual([JCOREConfiguration recentLogs].count, 0u);
    [JCOREConfiguration logInfo:@"local_log_disabled_02"];
    XCTAssertEqual([JCOREConfiguration recentLogs].count, 0u);

    [JCOREConfiguration setLocalLogEnabled:YES];
    [JCOREConfiguration shared].logOff = NO;
    [JCOREConfiguration clearRecentLogs];
}

@end
