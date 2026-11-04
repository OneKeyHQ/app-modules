package com.margelo.nitro.reactnativejpush;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import cn.jiguang.core.JCoreCrypto;
import cn.jiguang.core.JCoreWireCodec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class ReactNativeJpushProtocolTest {
    /**
     * Non-production demo test AppKey used solely for automated native unit and protocol tests.
     * Never used in production environments.
     */
    public static final String DEMO_TEST_APP_KEY = "1f2027d3cfb1b51dac664c42";

    @Test
    public void testCryptoPrimitivesAndSeedDerivation() throws Exception {
        long seed = 123456789L;
        String keyA = JCoreCrypto.generateSeedKey(seed);
        String keyB = JCoreCrypto.generateSeedKey(seed);
        assertEquals(32L, (long) keyA.length());
        assertEquals(keyA, keyB);

        byte[] plain = "OneKey JPush Clean SDK Crypto Payload 2026".getBytes(StandardCharsets.UTF_8);
        byte[] aesEnc = JCoreCrypto.aes256CbcSeedEncrypt(plain, seed);
        byte[] aesDec = JCoreCrypto.aes256CbcSeedDecrypt(aesEnc, seed);
        assertArrayEquals(plain, aesDec);

        byte[] sm4Key = JCoreCrypto.hexDecode("0123456789abcdeffedcba9876543210");
        byte[] sm4Iv = new byte[16];
        byte[] sm4Enc = JCoreCrypto.sm4CbcEncrypt(plain, sm4Key, sm4Iv);
        byte[] sm4Dec = JCoreCrypto.sm4CbcDecrypt(sm4Enc, sm4Key, sm4Iv);
        assertArrayEquals(plain, sm4Dec);

        byte[] gz = JCoreCrypto.gzipCompress(plain);
        byte[] ungz = JCoreCrypto.gzipDecompress(gz);
        assertArrayEquals(plain, ungz);
    }

    @Test
    public void testUdpSis10BytePacketWithDemoTestAppKey() {
        List<String> failedIps = Collections.singletonList("198.51.100.1:7000");
        String sisReqJson = JCoreWireCodec.buildSisRequestJson(
            1,
            DEMO_TEST_APP_KEY,
            900100200L,
            "46000",
            failedIps,
            1
        );
        assertTrue(sisReqJson.contains(DEMO_TEST_APP_KEY));
        assertTrue(sisReqJson.contains("5.1.1"));

        byte[] udpPacket = JCoreWireCodec.encodeUdpSisPacket(
            sisReqJson,
            0x00123456
        );
        assertTrue(udpPacket.length > 10);
        int hdrLen = ((udpPacket[0] & 0xFF) << 8) | (udpPacket[1] & 0xFF);
        assertEquals((long) udpPacket.length, (long) hdrLen);
        assertEquals((long) 'U', (long) ((char) udpPacket[2]));
        assertEquals((long) 'G', (long) ((char) udpPacket[3]));

        String decodedJson = JCoreWireCodec.decodeUdpSisPacket(udpPacket);
        assertEquals(sisReqJson, decodedJson);
    }

    @Test
    public void testAsymmetric24ByteClientAnd20ByteServerFramesWithDemoTestAppKey() {
        byte[] regBody = JCoreWireCodec.buildRegisterPayload(
            DEMO_TEST_APP_KEY,
            "google-play",
            "so.onekey.app.wallet",
            "dev_001"
        );
        int registerSeed = 0x13572468;
        byte[] clientRegFrame = JCoreWireCodec.encodeClientFrame(
            JCoreWireCodec.CMD_REGISTER,
            JCoreWireCodec.VER_REGISTER,
            JCoreWireCodec.ALG_AES_CBC,
            0,
            1L,
            registerSeed,
            0L,
            regBody
        );
        assertTrue(clientRegFrame.length >= 24 && ((clientRegFrame.length - 24) % 16 == 0));

        JCoreWireCodec.DecodedFrame decodedReg = JCoreWireCodec.decodeClientFrame(clientRegFrame, 0L);
        assertNotNull(decodedReg);
        assertEquals((long) JCoreWireCodec.CMD_REGISTER, (long) decodedReg.command);
        assertEquals((long) JCoreWireCodec.VER_REGISTER, (long) decodedReg.version);
        assertEquals((long) registerSeed, (long) decodedReg.sessionId);

        JCoreWireCodec.RegisterRequest parsedReg = JCoreWireCodec.parseRegisterRequest(decodedReg.body);
        assertNotNull(parsedReg);
        assertEquals(DEMO_TEST_APP_KEY, parsedReg.appKey);
        assertEquals("so.onekey.app.wallet", parsedReg.packageName);

        long assignedUid = 900100200L;
        byte[] serverRegBody = JCoreWireCodec.buildRegisterResponsePayload(
            assignedUid,
            "pwd_demo_2026",
            "1a0018970a900100200",
            "dev_001"
        );
        byte[] serverRegFrame = JCoreWireCodec.encodeResponseFrame(
            JCoreWireCodec.CMD_REGISTER,
            JCoreWireCodec.VER_REGISTER,
            JCoreWireCodec.ALG_AES_CBC,
            0,
            1L,
            registerSeed,
            assignedUid,
            0,
            serverRegBody
        );
        assertTrue(serverRegFrame.length >= 20 && ((serverRegFrame.length - 20) % 16 == 0));

        JCoreWireCodec.DecodedFrame decodedServerReg = JCoreWireCodec.decodeServerFrame(serverRegFrame, assignedUid);
        assertNotNull(decodedServerReg);
        JCoreWireCodec.RegisterResponse regResp = JCoreWireCodec.parseRegisterResponse(decodedServerReg);
        assertNotNull(regResp);
        assertEquals(0L, (long) regResp.statusCode);
        assertEquals(assignedUid, regResp.uid);
        assertEquals("1a0018970a900100200", regResp.registrationId);

        byte[] tagPayload = JCoreWireCodec.buildTagOperationPayload(1, 21, Arrays.asList("btc", "eth"));
        byte[] tagFrame = JCoreWireCodec.encodeClientFrame(
            JCoreWireCodec.CMD_TAG_OP,
            JCoreWireCodec.VER_TAG_OP,
            JCoreWireCodec.ALG_SM4_CBC,
            0,
            2L,
            0x22334455,
            assignedUid,
            tagPayload
        );
        JCoreWireCodec.DecodedFrame decodedTag = JCoreWireCodec.decodeClientFrame(tagFrame, assignedUid);
        assertNotNull(decodedTag);
        JCoreWireCodec.OperationPayload tagOp = JCoreWireCodec.parseOperationPayload(
            JCoreWireCodec.CMD_TAG_OP,
            decodedTag.body
        );
        assertNotNull(tagOp);
        assertEquals("add", tagOp.op);

        Map<String, Object> props = new LinkedHashMap<String, Object>();
        props.put("tier", "pro");
        byte[] propPayload = JCoreWireCodec.buildPropertyOperationPayload(1, 23, props);
        assertNotNull(propPayload);
        assertTrue(propPayload.length > 2);
    }
}
