package com.margelo.nitro.reactnativejpush;

import static org.junit.Assert.*;
import cn.jiguang.core.JCoreCrypto;
import cn.jiguang.core.JCoreStorage;
import cn.jiguang.core.JCoreTcpEngine;
import cn.jiguang.core.JCoreWireCodec;
import cn.jiguang.plugins.push.helper.JPushHelper;
import cn.jiguang.plugins.push.receiver.JPushModuleReceiver;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.local.ActionHelper;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assume;
import org.junit.Test;

public class JPushCloudHandshakeTest {
    private static final String LIVE_TEST_FCM_TOKEN_142B =
        "fcm_token_142_bytes_APA91bH0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef012345";

    @Test
    public void liveEngineEndToEndLifecycle() throws Exception {
        boolean enabled = "1".equals(System.getenv("JPUSH_LIVE_TESTS"));
        Assume.assumeTrue("External cloud handshake requires JPUSH_LIVE_TESTS=1", enabled);

        final JCoreTcpEngine engine = JCoreTcpEngine.getInstance();
        final JPushModuleReceiver receiver = new JPushModuleReceiver();
        final List<String> receivedEvents = new CopyOnWriteArrayList<String>();
        final CountDownLatch connectedLatch = new CountDownLatch(1);
        final CountDownLatch thirdTokenLatch = new CountDownLatch(3);
        int listenerId = 0;

        try {
            engine.stop(null);
            engine.setCustomEndpoint("", 0);
            engine.setCustomSisUrl("");
            engine.setDefaultSisUdpEndpoint("s.jpush.cn", 19000);
            engine.setTestConnectionEnabled(false);
            engine.setAutoReconnectEnabled(true);
            JCoreStorage.resetSisCache(null);
            JCoreStorage.clearRegistration(null);
            JCoreStorage.setPushStopped(null, false);
            JCoreStorage.setCollectionAuth(null, true);
            JCoreStorage.setAppKey(null, ReactNativeJpushProtocolTest.DEMO_TEST_APP_KEY);
            JCoreStorage.setChannel(null, "developer-default");
            JCoreStorage.saveThirdToken(null, (byte) 8, "");

            ActionHelper.getInstance().clearRegisteredReceivers();
            ActionHelper.getInstance().registerReceiver(receiver);
            listenerId = JPushHelper.registerNitroListener(new JPushHelper.NitroEventListener() {
                @Override
                public void onEvent(String eventName, String payloadJson) {
                    receivedEvents.add(eventName + ":" + payloadJson);
                    if ("ConnectEvent".equals(eventName) && payloadJson.contains("true")) {
                        connectedLatch.countDown();
                    }
                    if ("CommandEvent".equals(eventName) && payloadJson.contains("10000") && payloadJson.contains("\"commandResult\":0")) {
                        thirdTokenLatch.countDown();
                    }
                }
            });

            android.content.Context testCtx = new android.content.Context() {
                @Override
                public String getPackageName() {
                    return ReactNativeJpushProtocolTest.DEMO_TEST_PACKAGE_NAME;
                }
            };

            JPushInterface.resumePush(testCtx);
            assertTrue("Live JCoreTcpEngine handshake must complete", engine.awaitHandshake(15000L));
            assertTrue("Live JCoreTcpEngine must be logged in", engine.isLoggedIn());
            assertTrue("ConnectEvent(true) must fire", connectedLatch.await(5000L, TimeUnit.MILLISECONDS));

            // Simulate 3 concurrent 142-byte FCM token bindings after login (matching Android cold-start behavior)
            ActionHelper.getInstance().setThirdTokenWithCallBack(testCtx, LIVE_TEST_FCM_TOKEN_142B, (byte) 8, false);
            ActionHelper.getInstance().setThirdTokenWithCallBack(testCtx, LIVE_TEST_FCM_TOKEN_142B, (byte) 8, false);
            ActionHelper.getInstance().setThirdTokenWithCallBack(testCtx, LIVE_TEST_FCM_TOKEN_142B, (byte) 8, false);
            assertTrue("All 3 CMD_THIRD_TOKEN callbacks must succeed with errorCode=0", thirdTokenLatch.await(10000L, TimeUnit.MILLISECONDS));
            assertEquals("Third token must be saved on statusCode == 0", LIVE_TEST_FCM_TOKEN_142B, JCoreStorage.getThirdToken(testCtx, (byte) 8));
            assertTrue("TCP session must remain logged in after CMD_THIRD_TOKEN", engine.isLoggedIn());

            // Simulate reconnect (Fast-Login with persisted uid/password/registrationId and third_token_8 present -> romPlatformMask=0x28)
            engine.triggerReconnectNow(testCtx);
            assertTrue("Fast-Login reconnect handshake must complete", engine.awaitHandshake(15000L));
            assertTrue("Fast-Login must succeed against live server", engine.isLoggedIn());

            for (String ev : receivedEvents) {
                assertFalse("Must never emit spurious msgId=999999 CustomMessageEvent: " + ev, ev.contains("999999"));
                assertFalse("Must never emit CMD_THIRD_TOKEN 6002 timeout: " + ev, ev.contains("6002"));
            }
            System.out.println("Live JCoreTcpEngine register/login/third-token/fast-login lifecycle passed");
        } finally {
            if (listenerId > 0) {
                JPushHelper.unregisterNitroListener(listenerId);
            }
            ActionHelper.getInstance().unregisterReceiver(receiver);
            JCoreStorage.saveThirdToken(null, (byte) 8, "");
            engine.stop(null);
            JCoreStorage.resetSisCache(null);
            JCoreStorage.clearRegistration(null);
        }
    }
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

            tcp.setSoTimeout(1500);
            for (int i = 0; i < 3; i++) {
                try {
                    JCoreWireCodec.DecodedFrame f = JCoreWireCodec.readFrameFromStream(in, uid);
                    assertNotNull("Server closed connection after Login/Heartbeat", f);
                    if (f.command == JCoreWireCodec.CMD_PUSH_MESSAGE) {
                        JCoreWireCodec.PushEnvelope env = JCoreWireCodec.parsePushMessage(f);
                        assertFalse(
                            "Server pushed legacy msgType=4 msgId=999999 due to invalid sdkVerInfo",
                            env != null && env.messageType == (byte) 4 && env.messageId == 999999L
                        );
                    }
                } catch (SocketTimeoutException expectedIdle) {
                    break;
                }
            }

            tcp.setSoTimeout(5000);
            byte[] thirdTokenFrame = JCoreWireCodec.encodeFrame(
                JCoreWireCodec.CMD_THIRD_TOKEN,
                JCoreWireCodec.VER_THIRD_TOKEN,
                104L,
                sessionId,
                uid,
                JCoreWireCodec.buildThirdTokenPayload(LIVE_TEST_FCM_TOKEN_142B, (byte) 8, false)
            );
            out.write(thirdTokenFrame);
            out.flush();

            JCoreWireCodec.DecodedFrame tokenResp = JCoreWireCodec.readFrameFromStream(in, uid);
            assertNotNull("Server closed TCP connection (EOF) on CMD_THIRD_TOKEN", tokenResp);
            assertEquals("Third token response command", JCoreWireCodec.CMD_THIRD_TOKEN, tokenResp.command);
            assertEquals("Third token bind statusCode", 0, tokenResp.statusCode);
        }
        System.out.println("Live Jiguang register/login/heartbeat/third-token handshake passed");
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
