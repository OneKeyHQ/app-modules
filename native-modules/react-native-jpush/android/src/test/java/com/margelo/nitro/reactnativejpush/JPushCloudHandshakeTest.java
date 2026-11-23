package com.margelo.nitro.reactnativejpush;

import static org.junit.Assert.*;
import cn.jiguang.core.JCoreCrypto;
import cn.jiguang.core.JCoreWireCodec;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.Assume;
import org.junit.Test;

public class JPushCloudHandshakeTest {
    private static final String LIVE_TEST_FCM_TOKEN_142B =
        "fcm_token_142_bytes_APA91bH0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef012345";

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
            InputStream in = tcp.getInputStream();
            OutputStream out = tcp.getOutputStream();
            long[] sessionInfo = performHandshakeWithSession(in, out);
            long uid = sessionInfo[0];
            int sessionId = (int) sessionInfo[1];

            // Verify Heartbeat + no msgType=4 msgId=999999 + CMD_THIRD_TOKEN (cmd=27, ver=2) on live JPush server
            byte[] hbFrame = JCoreWireCodec.encodeFrame(
                JCoreWireCodec.CMD_HEARTBEAT,
                JCoreWireCodec.VER_HEARTBEAT,
                103L,
                sessionId,
                uid,
                JCoreWireCodec.buildHeartbeatPayload(false)
            );
            out.write(hbFrame);
            out.flush();

            long thirdTokenRequestId = 104L;
            byte[] thirdTokenFrame = JCoreWireCodec.encodeFrame(
                JCoreWireCodec.CMD_THIRD_TOKEN,
                JCoreWireCodec.VER_THIRD_TOKEN,
                thirdTokenRequestId,
                sessionId,
                uid,
                JCoreWireCodec.buildThirdTokenPayload(LIVE_TEST_FCM_TOKEN_142B, (byte) 8, false)
            );
            out.write(thirdTokenFrame);
            out.flush();

            JCoreWireCodec.DecodedFrame tokenResp = awaitThirdTokenAck(tcp, uid, thirdTokenRequestId, 5000L);
            assertEquals("Third token bind statusCode", 0, tokenResp.statusCode);
        }
        System.out.println("Live Jiguang register/login/heartbeat/third-token handshake passed");
    }

    private static JCoreWireCodec.DecodedFrame awaitThirdTokenAck(Socket socket, long uid, long requestId, long timeoutMs)
        throws Exception {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMs);
        ByteArrayOutputStream pending = new ByteArrayOutputStream();
        InputStream in = socket.getInputStream();
        byte[] chunk = new byte[4096];
        while (System.nanoTime() < deadline) {
            long remainingMs = TimeUnit.NANOSECONDS.toMillis(deadline - System.nanoTime());
            socket.setSoTimeout((int) Math.max(1L, Math.min(100L, remainingMs)));
            int count;
            try {
                count = in.read(chunk);
            } catch (SocketTimeoutException retry) {
                continue;
            }
            assertTrue("Server closed TCP while waiting for token bind ACK", count >= 0);
            pending.write(chunk, 0, count);
            for (JCoreWireCodec.DecodedFrame frame : JCoreWireCodec.decodeFramesFromStreamBuffer(pending, uid)) {
                if (frame.command == JCoreWireCodec.CMD_THIRD_TOKEN && frame.requestId == requestId) return frame;
                if (frame.command == JCoreWireCodec.CMD_PUSH_MESSAGE) {
                    JCoreWireCodec.PushEnvelope envelope = JCoreWireCodec.parsePushMessage(frame);
                    assertFalse("Unexpected legacy internal message",
                        envelope != null && envelope.messageType == 4 && envelope.messageId == 999999L);
                }
            }
        }
        throw new SocketTimeoutException("Timed out waiting for matching CMD_THIRD_TOKEN ACK");
    }

    @Test
    public void waitsForMatchingThirdTokenAckAcrossPartialReads() throws Exception {
        long uid = 101L;
        try (java.net.ServerSocket listener = new java.net.ServerSocket(0);
             Socket client = new Socket("127.0.0.1", listener.getLocalPort());
             Socket server = listener.accept()) {
            byte[] heartbeat = JCoreWireCodec.encodeResponseFrame(19, 8, 103L, 0, uid, 0, new byte[0]);
            byte[] wrongRid = JCoreWireCodec.encodeResponseFrame(27, 2, 105L, 0, uid, 42, new byte[0]);
            byte[] token = JCoreWireCodec.encodeResponseFrame(27, 2, 104L, 0, uid, 0, new byte[0]);
            OutputStream out = server.getOutputStream();
            out.write(heartbeat, 0, 1);
            java.util.concurrent.ScheduledExecutorService executor = java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
            try {
                java.util.concurrent.Future<?> delivery = executor.schedule(() -> {
                    out.write(heartbeat, 1, heartbeat.length - 1);
                    out.write(wrongRid);
                    out.write(token);
                    return null;
                }, 180L, TimeUnit.MILLISECONDS);
                assertEquals(0, awaitThirdTokenAck(client, uid, 104L, 2000L).statusCode);
                delivery.get();
            } finally {
                executor.shutdownNow();
            }
        }
    }

    // Shared by the opt-in cloud test and deterministic failure-propagation regressions.
    static void performHandshake(InputStream in, OutputStream out) throws Exception {
        performHandshakeWithSession(in, out);
    }

    static long[] performHandshakeWithSession(InputStream in, OutputStream out) throws Exception {
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
        return new long[] { regResp.uid, loginResp.sessionId };
    }
}
