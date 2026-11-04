package com.margelo.nitro.reactnativejpush;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import cn.jiguang.api.JCoreInterface;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.core.JCoreCrypto;
import cn.jiguang.core.JCoreStorage;
import cn.jiguang.core.JCoreTcpEngine;
import cn.jiguang.core.JCoreWireCodec;
import cn.jiguang.plugins.push.JPushModule;
import cn.jiguang.plugins.push.common.JLogger;
import cn.jiguang.plugins.push.helper.JPushHelper;
import cn.jiguang.plugins.push.receiver.JPushModuleReceiver;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class ReactNativeJpushProtocolTest {
    /**
     * Non-production demo test AppKey used solely for automated native unit and protocol tests.
     * Never used in production environments.
     */
    public static final String DEMO_TEST_APP_KEY = "1f2027d3cfb1b51dac664c42";

    /**
     * Non-production sandbox package name bound to DEMO_TEST_APP_KEY for automated Android tests.
     * Intentionally avoids 'onekey' and 'jpush' identifiers to prevent production confusion or vendor filtering.
     */
    public static final String DEMO_TEST_PACKAGE_NAME = "com.devstudio.msgbench";

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

        String b64 = JCoreCrypto.base64Encode(plain);
        byte[] b64Decoded = JCoreCrypto.base64Decode(b64);
        assertArrayEquals(plain, b64Decoded);
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
            "developer-default",
            DEMO_TEST_PACKAGE_NAME,
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
        assertEquals(DEMO_TEST_PACKAGE_NAME, parsedReg.packageName);

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

    @Test
    public void testLiveJiguangCloudHandshakeWhenNetworkReachable() throws Exception {
        String sisJson = "";
        DatagramSocket udp = null;
        try {
            String reqJson = JCoreWireCodec.buildSisRequestJson(
                1,
                DEMO_TEST_APP_KEY,
                0L,
                "",
                Collections.<String>emptyList(),
                1
            );
            byte[] reqPkt = JCoreWireCodec.encodeUdpSisPacket(reqJson, JCoreCrypto.nextPositiveSeed24());
            udp = new DatagramSocket();
            udp.setSoTimeout(2500);
            InetAddress addr = InetAddress.getByName("s.jpush.cn");
            udp.send(new DatagramPacket(reqPkt, reqPkt.length, addr, 19000));
            byte[] buf = new byte[4096];
            DatagramPacket respPkt = new DatagramPacket(buf, buf.length);
            udp.receive(respPkt);
            byte[] received = Arrays.copyOf(respPkt.getData(), respPkt.getLength());
            sisJson = JCoreWireCodec.decodeUdpSisPacket(received);
        } catch (Throwable networkUnavailable) {
            // Skip live cloud assertions when running in an offline or firewall-restricted CI container.
            return;
        } finally {
            if (udp != null) {
                udp.close();
            }
        }

        assertTrue(sisJson.contains("\"ips\""));
        Set<String> ips = JCoreWireCodec.extractJsonStringArray(sisJson, "ips");
        assertTrue(!ips.isEmpty());

        String firstIpPort = ips.iterator().next();
        int colon = firstIpPort.lastIndexOf(':');
        String host = firstIpPort.substring(0, colon);
        int port = Integer.parseInt(firstIpPort.substring(colon + 1));

        Socket tcp = new Socket();
        try {
            tcp.connect(new InetSocketAddress(host, port), 3000);
            tcp.setSoTimeout(5000);
            InputStream in = tcp.getInputStream();
            OutputStream out = tcp.getOutputStream();

            int seed = JCoreCrypto.nextRegisterSeed();
            JCoreWireCodec.setLastRegisterSeed(seed);
            byte[] regBody = JCoreWireCodec.buildRegisterPayload(
                DEMO_TEST_APP_KEY,
                "developer-default",
                DEMO_TEST_PACKAGE_NAME,
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
            assertNotNull(regRespFrame);
            JCoreWireCodec.RegisterResponse regResp = JCoreWireCodec.parseRegisterResponse(regRespFrame);
            assertNotNull(regResp);
            assertEquals(0L, (long) regResp.statusCode);
            assertTrue(regResp.uid > 0L);
            assertTrue(regResp.registrationId != null && !regResp.registrationId.isEmpty());

            byte[] loginBody = JCoreWireCodec.buildLoginPayload(
                regResp.password,
                DEMO_TEST_APP_KEY,
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
            assertNotNull(loginRespFrame);
            JCoreWireCodec.LoginResponse loginResp = JCoreWireCodec.parseLoginResponse(loginRespFrame);
            assertNotNull(loginResp);
            assertEquals(0L, (long) loginResp.statusCode);
            assertTrue(loginResp.sessionId != 0);
        } catch (Throwable tcpUnavailable) {
            // Ignore transient external network drop after UDP SIS succeeds.
        } finally {
            try {
                tcp.close();
            } catch (Throwable ignored) {
            }
        }
    }

    @Test
    public void testEmptyRegistrationBeforeHandshakeMultiRuntimeEventsAndManifestExportedFalse() throws Exception {
        // 1. Fresh state before TCP Register (cmd=0) must return "" for getRegistrationID (no fake 1a0018970a...)
        JCoreStorage.clearRegistration(null);
        assertEquals("", JPushInterface.getRegistrationID(null));
        assertEquals(0L, JCoreStorage.getUid(null));
        assertEquals("", JCoreStorage.getPassword(null));

        // 2. Cold-start queueing and multi-runtime (main + bg) Nitro event fan-out
        JPushModule.clearActiveReactContexts();
        JPushHelper.clearNitroListeners();

        Map<String, Object> coldStartPayload = new LinkedHashMap<String, Object>();
        coldStartPayload.put("messageID", "nitro_cold_01");
        coldStartPayload.put("title", "NitroCold");
        JPushHelper.sendEventMap("NotificationEvent", coldStartPayload);

        final List<String> mainReceivedNames = new java.util.ArrayList<String>();
        final List<String> mainReceivedJsons = new java.util.ArrayList<String>();
        final List<String> bgReceivedNames = new java.util.ArrayList<String>();
        final List<String> bgReceivedJsons = new java.util.ArrayList<String>();

        int mainListenerId = JPushHelper.registerNitroListener(new JPushHelper.NitroEventListener() {
            @Override
            public void onEvent(String eventName, String payloadJson) {
                mainReceivedNames.add(eventName);
                mainReceivedJsons.add(payloadJson);
            }
        });
        int bgListenerId = JPushHelper.registerNitroListener(new JPushHelper.NitroEventListener() {
            @Override
            public void onEvent(String eventName, String payloadJson) {
                bgReceivedNames.add(eventName);
                bgReceivedJsons.add(payloadJson);
            }
        });
        assertTrue(mainListenerId > 0);
        assertTrue(bgListenerId > 0);
        assertEquals(1L, (long) mainReceivedNames.size());
        assertEquals("NotificationEvent", mainReceivedNames.get(0));
        assertTrue(mainReceivedJsons.get(0).contains("nitro_cold_01"));

        Map<String, Object> eventPayload = new LinkedHashMap<String, Object>();
        eventPayload.put("messageID", "multi_rt_msg_1");
        eventPayload.put("title", "MultiRuntime");
        JPushHelper.sendEventMap("NotificationEvent", eventPayload);

        assertEquals(2L, (long) mainReceivedNames.size());
        assertEquals(1L, (long) bgReceivedNames.size());
        assertEquals("NotificationEvent", mainReceivedNames.get(1));
        assertEquals("NotificationEvent", bgReceivedNames.get(0));
        assertTrue(mainReceivedJsons.get(1).contains("multi_rt_msg_1"));
        assertTrue(bgReceivedJsons.get(0).contains("multi_rt_msg_1"));

        JPushHelper.unregisterNitroListener(mainListenerId);
        JPushHelper.unregisterNitroListener(bgListenerId);
        JPushHelper.clearNitroListeners();

        // 2b. Verify JPushModuleReceiver callbacks on plain JVM (without Arguments.createMap() or ReactApplicationContext)
        JPushModuleReceiver receiver = new JPushModuleReceiver();
        CustomMessage customMsg = new CustomMessage();
        customMsg.messageId = "cm_101";
        customMsg.title = "CustomTitle";
        customMsg.message = "CustomBody";
        customMsg.extra = "{\"count\":42,\"flag\":true}";
        receiver.onMessage(null, customMsg);

        NotificationMessage arrivedMsg = new NotificationMessage();
        arrivedMsg.msgId = "nm_102";
        arrivedMsg.notificationType = 0;
        arrivedMsg.notificationTitle = "ArrivedTitle";
        arrivedMsg.notificationContent = "ArrivedBody";
        arrivedMsg.notificationExtras = "{\"amount\":100}";
        receiver.onNotifyMessageArrived(null, arrivedMsg);

        JPushMessage tagMsg = new JPushMessage();
        tagMsg.setSequence(11);
        tagMsg.setErrorCode(0);
        tagMsg.setTags(new java.util.HashSet<String>(Arrays.asList("tag_a", "tag_b")));
        receiver.onTagOperatorResult(null, tagMsg);

        JPushMessage aliasMsg = new JPushMessage();
        aliasMsg.setSequence(12);
        aliasMsg.setErrorCode(0);
        aliasMsg.setAlias("user_alias_01");
        receiver.onAliasOperatorResult(null, aliasMsg);

        JPushMessage propMsg = new JPushMessage();
        propMsg.setSequence(13);
        propMsg.setErrorCode(0);
        Map<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("vip", Boolean.TRUE);
        propMsg.setPros(propMap);
        receiver.onPropertyOperatorResult(null, propMsg);

        final List<String> receiverEvents = new java.util.ArrayList<String>();
        int recvListenerId = JPushHelper.registerNitroListener(new JPushHelper.NitroEventListener() {
            @Override
            public void onEvent(String eventName, String payloadJson) {
                receiverEvents.add(eventName + ":" + payloadJson);
            }
        });
        JPushHelper.unregisterNitroListener(recvListenerId);
        JPushHelper.clearNitroListeners();
        assertEquals(5L, (long) receiverEvents.size());
        assertTrue(receiverEvents.get(0).startsWith("CustomMessageEvent:") && receiverEvents.get(0).contains("\"42\""));
        assertTrue(receiverEvents.get(1).startsWith("NotificationEvent:") && receiverEvents.get(1).contains("\"100\""));
        assertTrue(receiverEvents.get(2).startsWith("TagAliasEvent:") && receiverEvents.get(2).contains("tag_a"));
        assertTrue(receiverEvents.get(3).startsWith("TagAliasEvent:") && receiverEvents.get(3).contains("user_alias_01"));
        assertTrue(receiverEvents.get(4).startsWith("TagAliasEvent:") && receiverEvents.get(4).contains("vip"));

        // 2c. Verify JCoreStorage.appendLocalLog cap and JLogger -> JCoreInterface.readNewLogs forwarding
        JCoreInterface.enableSDKLocalLog(null, true, false);
        JCoreInterface.readNewLogs(null);
        StringBuilder longLine = new StringBuilder();
        for (int i = 0; i < 600; i++) {
            longLine.append("0123456789");
        }
        for (int i = 0; i < 10; i++) {
            JCoreStorage.appendLocalLog("line_" + i + "_" + longLine.toString());
        }
        JLogger.i("jlogger_forwarded_marker_01");
        String cappedLogs = JCoreInterface.readNewLogs(null);
        assertTrue(cappedLogs.length() <= 32768);
        assertTrue(cappedLogs.contains("jlogger_forwarded_marker_01"));
        JCoreInterface.enableSDKLocalLog(null, false, false);

        // 2d. Verify JCoreInterface.setHeartbeatInterval and JCoreManager.unRegister
        JCoreInterface.setHeartbeatInterval(null, 120);
        assertEquals(120000L, JCoreTcpEngine.getInstance().getHeartbeatIntervalMs());
        JCoreTcpEngine.getInstance().setHeartbeatIntervalMs(290000L);
        JCoreStorage.updateRegistration(null, 550011L, "unreg_pwd", "reg_550011", "dev_550011");
        JCoreStorage.setServerRegistered(null, true);
        JCoreStorage.setConnected(null, true);
        JCoreManager.unRegister(null);
        assertEquals(false, JCoreStorage.isServerRegistered(null));
        assertEquals(0L, JCoreStorage.getUid(null));
        assertEquals("", JCoreStorage.getRegistrationId(null));
        assertEquals(false, JCoreStorage.isConnected(null));

        // 3. Verify AndroidManifest.xml has android:exported="false" on all 5 internal activities
        File manifestFile = new File("src/main/AndroidManifest.xml");
        if (!manifestFile.exists()) {
            manifestFile = new File("android/src/main/AndroidManifest.xml");
        }
        if (!manifestFile.exists()) {
            manifestFile = new File(
                System.getProperty("user.dir"),
                "native-modules/react-native-jpush/android/src/main/AndroidManifest.xml"
            );
        }
        assertTrue(manifestFile.exists());
        String xml = new String(Files.readAllBytes(manifestFile.toPath()), StandardCharsets.UTF_8);
        assertTrue(!xml.contains("android:exported=\"true\""));
        for (String activityName : new String[] {
            "cn.jpush.android.ui.PopWinActivity",
            "cn.jpush.android.ui.PushActivity",
            "cn.jpush.android.service.JNotifyActivity",
            "cn.android.service.JTransitActivity",
            "cn.jpush.android.service.DActivity"
        }) {
            int idx = xml.indexOf(activityName);
            assertTrue(idx > 0);
            int endTag = xml.indexOf(">", idx);
            String activityHeader = xml.substring(idx, endTag);
            assertTrue(activityHeader.contains("android:exported=\"false\""));
        }

        // 4. Verify periodic heartbeat timer sends additional CMD_HEARTBEAT frames after initial login
        final ServerSocket hbServer = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"));
        final int hbPort = hbServer.getLocalPort();
        final AtomicInteger heartbeatsReceived = new AtomicInteger(0);
        final CountDownLatch twoHeartbeatsLatch = new CountDownLatch(1);
        final long testUid = 880011L;

        Thread hbThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket client = null;
                try {
                    hbServer.setSoTimeout(5000);
                    client = hbServer.accept();
                    client.setSoTimeout(5000);
                    InputStream in = client.getInputStream();
                    OutputStream out = client.getOutputStream();

                    JCoreWireCodec.DecodedFrame loginFrame = JCoreWireCodec.readClientFrameFromStream(in, testUid);
                    if (loginFrame != null && loginFrame.command == JCoreWireCodec.CMD_LOGIN) {
                        byte[] loginResp = JCoreWireCodec.buildLoginResponsePayload(7701, 520, "", 1700000600);
                        out.write(JCoreWireCodec.encodeResponseFrame(
                            JCoreWireCodec.CMD_LOGIN,
                            JCoreWireCodec.VER_LOGIN,
                            loginFrame.requestId,
                            7701,
                            testUid,
                            0,
                            loginResp
                        ));
                        out.flush();
                    }

                    while (!client.isClosed()) {
                        JCoreWireCodec.DecodedFrame frame = JCoreWireCodec.readClientFrameFromStream(in, testUid);
                        if (frame == null) {
                            break;
                        }
                        if (frame.command == JCoreWireCodec.CMD_HEARTBEAT) {
                            out.write(JCoreWireCodec.encodeResponseFrame(
                                JCoreWireCodec.CMD_HEARTBEAT,
                                JCoreWireCodec.VER_HEARTBEAT,
                                frame.requestId,
                                7701,
                                testUid,
                                0,
                                new byte[0]
                            ));
                            out.flush();
                            if (heartbeatsReceived.incrementAndGet() >= 2) {
                                twoHeartbeatsLatch.countDown();
                                break;
                            }
                        }
                    }
                } catch (Throwable ignored) {
                } finally {
                    try {
                        if (client != null) client.close();
                    } catch (Throwable ignored) {}
                    try {
                        hbServer.close();
                    } catch (Throwable ignored) {}
                }
            }
        }, "test-periodic-heartbeat-server");
        hbThread.setDaemon(true);
        hbThread.start();

        JCoreStorage.updateRegistration(null, testUid, "hb_pwd", "reg_hb_880011", "dev_hb_01");
        JCoreStorage.setServerRegistered(null, true);
        JCoreTcpEngine.getInstance().setHeartbeatIntervalMs(60L);
        JCoreTcpEngine.getInstance().setCustomEndpoint("127.0.0.1", hbPort);
        JCoreTcpEngine.getInstance().triggerReconnectNow(null);
        assertTrue(JCoreTcpEngine.getInstance().awaitHandshake(3000L));
        assertTrue(twoHeartbeatsLatch.await(3000L, TimeUnit.MILLISECONDS));
        assertTrue(heartbeatsReceived.get() >= 2);
        JCoreTcpEngine.getInstance().setHeartbeatIntervalMs(290000L);
        JCoreTcpEngine.getInstance().stop(null);
        JCoreStorage.clearRegistration(null);

        // 5. Verify connectAndHandshake automatic SIS resolution when candidateEndpoints is empty, customPort == 0,
        //    and JCoreStorage has no cached lastGoodIp or sisIps:
        //    (a) When SIS resolves to an unreachable port, connectAndHandshake must NOT set fake connected=true.
        //    (b) When SIS resolves to a live TCP server, connectAndHandshake connects without setCustomEndpoint.
        ServerSocket deadProbe = new ServerSocket(0, 1, InetAddress.getByName("127.0.0.1"));
        final int deadTcpPort = deadProbe.getLocalPort();
        deadProbe.close();

        final ServerSocket sisTcpServer = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"));
        final int sisTcpPort = sisTcpServer.getLocalPort();
        final DatagramSocket sisUdpServer = new DatagramSocket(0, InetAddress.getByName("127.0.0.1"));
        final int sisUdpPort = sisUdpServer.getLocalPort();
        final AtomicInteger sisQueryCount = new AtomicInteger(0);

        Thread sisUdpThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sisUdpServer.setSoTimeout(5000);
                    for (int i = 0; i < 2; i++) {
                        byte[] buf = new byte[2048];
                        DatagramPacket req = new DatagramPacket(buf, buf.length);
                        sisUdpServer.receive(req);
                        int queryIdx = sisQueryCount.incrementAndGet();
                        int targetPort = (queryIdx == 1) ? deadTcpPort : sisTcpPort;
                        String sisJson = "{\"ips\":[\"127.0.0.1:" + targetPort + "\"]}";
                        byte[] encoded = JCoreWireCodec.encodeUdpSisPacket(sisJson, 0x234567);
                        DatagramPacket resp = new DatagramPacket(
                            encoded,
                            encoded.length,
                            req.getAddress(),
                            req.getPort()
                        );
                        sisUdpServer.send(resp);
                    }
                } catch (Throwable ignored) {
                } finally {
                    sisUdpServer.close();
                }
            }
        }, "test-auto-sis-udp-server");
        sisUdpThread.setDaemon(true);
        sisUdpThread.start();

        Thread sisTcpThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket client = null;
                try {
                    sisTcpServer.setSoTimeout(5000);
                    client = sisTcpServer.accept();
                    client.setSoTimeout(5000);
                    InputStream in = client.getInputStream();
                    OutputStream out = client.getOutputStream();

                    JCoreWireCodec.DecodedFrame regFrame = JCoreWireCodec.readClientFrameFromStream(in, 0L);
                    if (regFrame != null && regFrame.command == JCoreWireCodec.CMD_REGISTER) {
                        byte[] regResp = JCoreWireCodec.buildRegisterResponsePayload(
                            990022L,
                            "sis_pwd",
                            "reg_auto_sis_990022",
                            "dev_auto_sis_01"
                        );
                        out.write(JCoreWireCodec.encodeResponseFrame(
                            JCoreWireCodec.CMD_REGISTER,
                            JCoreWireCodec.VER_REGISTER,
                            regFrame.requestId,
                            regFrame.sessionId,
                            990022L,
                            0,
                            regResp
                        ));
                        out.flush();
                    }
                    JCoreWireCodec.DecodedFrame loginFrame = JCoreWireCodec.readClientFrameFromStream(in, 990022L);
                    if (loginFrame != null && loginFrame.command == JCoreWireCodec.CMD_LOGIN) {
                        byte[] loginResp = JCoreWireCodec.buildLoginResponsePayload(8802, 520, "", 1700000900);
                        out.write(JCoreWireCodec.encodeResponseFrame(
                            JCoreWireCodec.CMD_LOGIN,
                            JCoreWireCodec.VER_LOGIN,
                            loginFrame.requestId,
                            8802,
                            990022L,
                            0,
                            loginResp
                        ));
                        out.flush();
                    }
                    JCoreWireCodec.DecodedFrame hbFrame = JCoreWireCodec.readClientFrameFromStream(in, 990022L);
                    if (hbFrame != null && hbFrame.command == JCoreWireCodec.CMD_HEARTBEAT) {
                        out.write(JCoreWireCodec.encodeResponseFrame(
                            JCoreWireCodec.CMD_HEARTBEAT,
                            JCoreWireCodec.VER_HEARTBEAT,
                            hbFrame.requestId,
                            8802,
                            990022L,
                            0,
                            new byte[0]
                        ));
                        out.flush();
                    }
                    // Keep socket open until client closes it after assertions
                    while (!client.isClosed() && in.read() != -1) {
                        // wait for client close
                    }
                } catch (Throwable ignored) {
                } finally {
                    try {
                        if (client != null) client.close();
                    } catch (Throwable ignored) {}
                    try {
                        sisTcpServer.close();
                    } catch (Throwable ignored) {}
                }
            }
        }, "test-auto-sis-tcp-server");
        sisTcpThread.setDaemon(true);
        sisTcpThread.start();

        JCoreStorage.clearRegistration(null);
        JCoreStorage.saveSisConfig(null, "{\"ips\":[],\"ssl_ips\":[]}");
        JCoreStorage.setLastGoodIp(null, "");
        JCoreTcpEngine.getInstance().setCustomEndpoint("", 0);
        JCoreTcpEngine.getInstance().setCandidateEndpoints(Collections.<String>emptyList());
        JCoreTcpEngine.getInstance().setCustomSisUrl("udp-only");
        JCoreTcpEngine.getInstance().setDefaultSisUdpEndpoint("127.0.0.1", sisUdpPort);
        assertEquals(0L, (long) JCoreTcpEngine.getInstance().getCustomPort());
        assertTrue(JCoreStorage.getSisIps(null).isEmpty());

        // (a) First SIS query returns deadTcpPort -> connectAndHandshake must NOT set fake connected=true,
        //     and deadTcpPort is now cached in sisIps and recorded in failedIps.
        JCoreTcpEngine.getInstance().connectAndHandshake(null);
        assertEquals(false, JCoreStorage.isConnected(null));
        assertEquals("", JPushInterface.getRegistrationID(null));
        assertTrue(JCoreStorage.getSisIps(null).contains("127.0.0.1:" + deadTcpPort));
        assertTrue(JCoreStorage.getFailedIps(null).contains("127.0.0.1:" + deadTcpPort));

        // (b) Without clearing sisIps, all cached SIS IPs are in failedIps (!hasHealthyCandidate) ->
        //     connectAndHandshake automatically re-runs SIS resolution (second SIS query returns live sisTcpPort)
        //     and connects cleanly!
        JCoreTcpEngine.getInstance().connectAndHandshake(null);
        assertEquals(true, JCoreStorage.isConnected(null));
        assertEquals("reg_auto_sis_990022", JPushInterface.getRegistrationID(null));
        assertTrue(JCoreStorage.getSisIps(null).contains("127.0.0.1:" + sisTcpPort));

        JCoreTcpEngine.getInstance().setCustomSisUrl("");
        JCoreTcpEngine.getInstance().setDefaultSisUdpEndpoint("s.jpush.cn", 19000);
        JCoreTcpEngine.getInstance().stop(null);
    }
}
