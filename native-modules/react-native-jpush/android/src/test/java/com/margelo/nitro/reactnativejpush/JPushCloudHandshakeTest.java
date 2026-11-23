package com.margelo.nitro.reactnativejpush;

import static org.junit.Assert.*;
import cn.jiguang.core.JCoreCrypto;
import cn.jiguang.core.JCoreWireCodec;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.junit.Assume;
import org.junit.Test;

public class JPushCloudHandshakeTest {
    @Test
    public void liveCloudHandshake() throws Exception {
        boolean enabled = "1".equals(System.getenv("JPUSH_LIVE_TESTS"));
        if (!enabled) {
            System.out.println("SKIPPED live Jiguang handshake: set JPUSH_LIVE_TESTS=1 to enable");
        }
        Assume.assumeTrue("External cloud handshake requires JPUSH_LIVE_TESTS=1", enabled);
        String sisJson;
        try (DatagramSocket udp = new DatagramSocket()) {
            String request = JCoreWireCodec.buildSisRequestJson(
                1, ReactNativeJpushProtocolTest.DEMO_TEST_APP_KEY, 0L, "",
                Collections.<String>emptyList(), 1
            );
            byte[] packet = JCoreWireCodec.encodeUdpSisPacket(request, JCoreCrypto.nextPositiveSeed24());
            udp.setSoTimeout(2500);
            udp.send(new DatagramPacket(packet, packet.length, InetAddress.getByName("s.jpush.cn"), 19000));
            DatagramPacket response = new DatagramPacket(new byte[4096], 4096);
            udp.receive(response);
            sisJson = JCoreWireCodec.decodeUdpSisPacket(Arrays.copyOf(response.getData(), response.getLength()));
        }
        assertTrue("SIS response must contain endpoints", sisJson.contains("\"ips\""));
        Set<String> endpoints = JCoreWireCodec.extractJsonStringArray(sisJson, "ips");
        assertFalse("SIS endpoints must not be empty", endpoints.isEmpty());
        String endpoint = endpoints.iterator().next();
        int colon = endpoint.lastIndexOf(':');
        try (Socket tcp = new Socket()) {
            tcp.connect(new InetSocketAddress(endpoint.substring(0, colon), Integer.parseInt(endpoint.substring(colon + 1))), 3000);
            tcp.setSoTimeout(5000);
            performHandshake(tcp.getInputStream(), tcp.getOutputStream());
        }
        System.out.println("Live Jiguang register/login handshake passed");
    }

    // Shared by the opt-in cloud test and deterministic failure-propagation regressions.
    static void performHandshake(InputStream in, OutputStream out) throws Exception {
        int seed = JCoreCrypto.nextRegisterSeed();
        JCoreWireCodec.setLastRegisterSeed(seed);
        byte[] regBody = JCoreWireCodec.buildRegisterPayload(
            ReactNativeJpushProtocolTest.DEMO_TEST_APP_KEY,
            "developer-default",
            ReactNativeJpushProtocolTest.DEMO_TEST_PACKAGE_NAME,
            "msgbench_auto_001"
        );
        byte[] regFrame = JCoreWireCodec.encodeFrame(
            JCoreWireCodec.CMD_REGISTER,
            JCoreWireCodec.VER_REGISTER,
            101L,
            seed,
            0L,
            regBody
        );
        out.write(regFrame);
        out.flush();

        JCoreWireCodec.DecodedFrame regRespFrame = JCoreWireCodec.readFrameFromStream(in, 0L);
        assertNotNull("Missing register response", regRespFrame);
        assertEquals("Register command", JCoreWireCodec.CMD_REGISTER, regRespFrame.command);
        assertEquals("Register status", 0, regRespFrame.statusCode);
        JCoreWireCodec.RegisterResponse regResp = JCoreWireCodec.parseRegisterResponse(regRespFrame);
        assertNotNull(regResp);
        assertEquals(0L, (long) regResp.statusCode);
        assertTrue(regResp.uid > 0L);
        assertTrue(regResp.registrationId != null && !regResp.registrationId.isEmpty());

        byte[] loginBody = JCoreWireCodec.buildLoginPayload(
            regResp.password,
            ReactNativeJpushProtocolTest.DEMO_TEST_APP_KEY,
            regResp.deviceId,
            "CN"
        );
        byte[] loginFrame = JCoreWireCodec.encodeFrame(
            JCoreWireCodec.CMD_LOGIN,
            JCoreWireCodec.VER_LOGIN,
            102L,
            0,
            regResp.uid,
            loginBody
        );
        out.write(loginFrame);
        out.flush();

        JCoreWireCodec.DecodedFrame loginRespFrame = JCoreWireCodec.readFrameFromStream(in, regResp.uid);
        assertNotNull("Missing login response", loginRespFrame);
        assertEquals("Login command", JCoreWireCodec.CMD_LOGIN, loginRespFrame.command);
        assertEquals("Login status", 0, loginRespFrame.statusCode);
        JCoreWireCodec.LoginResponse loginResp = JCoreWireCodec.parseLoginResponse(loginRespFrame);
        assertNotNull(loginResp);
        assertEquals(0L, (long) loginResp.statusCode);
        assertTrue(loginResp.sessionId != 0);
    }
}
