#import <XCTest/XCTest.h>
#import "../include/JPUSHService.h"
#import "../include/JGInforCollectionAuth.h"
#import "../jcore/JCOREConfiguration.h"
#import "../jcore/JCORECrypto.h"
#import "../jcore/JCORESisResolver.h"
#import "../jcore/JCOREWireProtocol.h"

/**
 * Non-production demo test AppKey used solely for automated native unit and protocol tests.
 * Never used in production environments.
 */
static NSString *const kDemoTestAppKey = @"1f2027d3cfb1b51dac664c42";

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
                                                           bundleIdHash:@"so.onekey.wallet"];
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

@end
