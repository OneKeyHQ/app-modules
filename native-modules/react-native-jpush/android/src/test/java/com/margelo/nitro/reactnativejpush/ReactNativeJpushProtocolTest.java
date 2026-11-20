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
import cn.jpush.android.data.JPushLocalNotification;
import cn.jpush.android.local.ActionHelper;
import cn.jpush.android.ups.JPushUPSManager;
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

        // 2c. Verify JCoreStorage local log buffer is deprecated/disabled and JLogger outputs directly to OneKeyLog without in-memory storage
        com.margelo.nitro.nativelogger.OneKeyLog.clearCapturedLogs();
        JCoreInterface.enableSDKLocalLog(null, true, false);
        JCoreInterface.readNewLogs(null);
        for (int i = 0; i < 10; i++) {
            JCoreStorage.appendLocalLog("line_" + i);
        }
        JLogger.i("jlogger_forwarded_marker_01");
        String cappedLogs = JCoreInterface.readNewLogs(null);
        assertEquals("", cappedLogs);
        assertTrue(JLogger.getRecentLogs().isEmpty());
        assertTrue(com.margelo.nitro.nativelogger.OneKeyLog.getCapturedLogs().toString().contains("jlogger_forwarded_marker_01"));
        JCoreInterface.enableSDKLocalLog(null, false, false);
        com.margelo.nitro.nativelogger.OneKeyLog.clearCapturedLogs();

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

        // 2e. Verify JPushUPSManager.registerToken resolves appKey when appId is empty
        JCoreStorage.setPushStopped(null, true);
        JPushUPSManager.registerToken(null, "", "ups_appkey_fallback_01", "ups_secret", null);
        assertEquals("ups_appkey_fallback_01", JCoreStorage.getAppKey(null));
        JCoreTcpEngine.getInstance().stop(null);
        JCoreStorage.setPushStopped(null, false);

        // 2f. Verify Java scheduler ownership/cancellation; plain android.jar cannot prove Intent delivery or OS display.
        final ActionHelper localHelper = ActionHelper.getInstance();
        localHelper.clearActiveNotificationIds();
        java.lang.reflect.Field pendingField = ActionHelper.class.getDeclaredField("pendingLocalNotifications");
        pendingField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Integer, java.util.concurrent.ScheduledFuture<?>> pendingLocal =
            (Map<Integer, java.util.concurrent.ScheduledFuture<?>>) pendingField.get(localHelper);
        java.lang.reflect.Field schedulerField = ActionHelper.class.getDeclaredField("localNotificationScheduler");
        schedulerField.setAccessible(true);
        java.util.concurrent.ScheduledExecutorService scheduler =
            (java.util.concurrent.ScheduledExecutorService) schedulerField.get(localHelper);
        final CountDownLatch schedulerHeld = new CountDownLatch(1), releaseScheduler = new CountDownLatch(1);
        scheduler.execute(new Runnable() {
            @Override public void run() {
                schedulerHeld.countDown();
                try { releaseScheduler.await(); }
                catch (InterruptedException interrupted) { Thread.currentThread().interrupt(); }
            }
        });
        try {
            assertTrue(schedulerHeld.await(1000L, TimeUnit.MILLISECONDS));
            JPushLocalNotification cancelledLn = new JPushLocalNotification();
            cancelledLn.setNotificationId(77001L);
            cancelledLn.setTitle("CancelledTitle");
            cancelledLn.setContent("CancelledContent");
            cancelledLn.setBroadcastTime(300L);
            JPushInterface.addLocalNotification(null, cancelledLn);
            java.util.concurrent.ScheduledFuture<?> cancelledFuture = pendingLocal.get(Integer.valueOf(77001));
            assertNotNull(cancelledFuture);
            assertTrue(!cancelledFuture.isDone());
            JPushInterface.removeLocalNotification(null, 77001L);
            assertTrue(cancelledFuture.isCancelled());
            assertTrue(!pendingLocal.containsKey(Integer.valueOf(77001)));

            JPushLocalNotification firedLn = new JPushLocalNotification();
            firedLn.setNotificationId(77002L);
            firedLn.setTitle("FiredTitle");
            firedLn.setContent("FiredContent");
            firedLn.setBroadcastTime(70L);
            JPushInterface.addLocalNotification(null, firedLn);
            java.util.concurrent.ScheduledFuture<?> firedFuture = pendingLocal.get(Integer.valueOf(77002));
            assertNotNull(firedFuture);
            assertTrue(!firedFuture.isDone());
            releaseScheduler.countDown();
            long schedulerDeadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(1000L);
            while (!firedFuture.isDone() && System.nanoTime() < schedulerDeadline) Thread.sleep(5L);
            assertTrue(firedFuture.isDone() && !firedFuture.isCancelled());
            assertTrue(!pendingLocal.containsKey(Integer.valueOf(77002)));
            assertTrue(cancelledFuture.isCancelled());
        } finally {
            releaseScheduler.countDown();
            localHelper.clearActiveNotificationIds();
        }

        // 2g. Verify ActionHelper server response parsing (getAlias, getAllTags, checkTagBindState)
        //     and rollback on non-zero statusCode for Tag/Alias/Property/MobileNumber
        final ServerSocket opServer = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"));
        final int opPort = opServer.getLocalPort();
        final long opUid = 660033L;
        Thread opServerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket client = null;
                try {
                    opServer.setSoTimeout(5000);
                    client = opServer.accept();
                    client.setSoTimeout(5000);
                    InputStream in = client.getInputStream();
                    OutputStream out = client.getOutputStream();

                    JCoreWireCodec.DecodedFrame loginFrame = JCoreWireCodec.readClientFrameFromStream(in, opUid);
                    if (loginFrame != null && loginFrame.command == JCoreWireCodec.CMD_LOGIN) {
                        byte[] loginResp = JCoreWireCodec.buildLoginResponsePayload(6601, 520, "", 1700001000);
                        out.write(JCoreWireCodec.encodeResponseFrame(
                            JCoreWireCodec.CMD_LOGIN,
                            JCoreWireCodec.VER_LOGIN,
                            loginFrame.requestId,
                            6601,
                            opUid,
                            0,
                            loginResp
                        ));
                        out.flush();
                    }

                    while (!client.isClosed()) {
                        JCoreWireCodec.DecodedFrame req = JCoreWireCodec.readClientFrameFromStream(in, opUid);
                        if (req == null) {
                            break;
                        }
                        int sequence = JCoreWireCodec.extractJsonInt(JCoreWireCodec.parseJsonCommandPayload(req.body), "seq", -1);
                        if (req.command == JCoreWireCodec.CMD_HEARTBEAT) {
                            out.write(JCoreWireCodec.encodeResponseFrame(
                                JCoreWireCodec.CMD_HEARTBEAT,
                                JCoreWireCodec.VER_HEARTBEAT,
                                req.requestId,
                                6601,
                                opUid,
                                0,
                                new byte[0]
                            ));
                            out.flush();
                        } else if (req.command == JCoreWireCodec.CMD_ALIAS_OP) {
                            if (sequence == 301) {
                                byte[] body = JCoreWireCodec.buildJsonCommandPayload("{\"alias\":\"srv_alias_301\"}");
                                out.write(JCoreWireCodec.encodeResponseFrame(
                                    JCoreWireCodec.CMD_ALIAS_OP,
                                    JCoreWireCodec.VER_ALIAS_OP,
                                    req.requestId,
                                    6601,
                                    opUid,
                                    0,
                                    body
                                ));
                            } else {
                                byte[] errBody = JCoreWireCodec.buildJsonCommandPayload("{\"code\":6002}");
                                out.write(JCoreWireCodec.encodeResponseFrame(
                                    JCoreWireCodec.CMD_ALIAS_OP,
                                    JCoreWireCodec.VER_ALIAS_OP,
                                    req.requestId,
                                    6601,
                                    opUid,
                                    6002,
                                    errBody
                                ));
                            }
                            out.flush();
                        } else if (req.command == JCoreWireCodec.CMD_TAG_OP) {
                            if (sequence == 303) {
                                byte[] body = JCoreWireCodec.buildJsonCommandPayload("{\"tags\":[\"srv_t1\",\"srv_t2\"]}");
                                out.write(JCoreWireCodec.encodeResponseFrame(
                                    JCoreWireCodec.CMD_TAG_OP,
                                    JCoreWireCodec.VER_TAG_OP,
                                    req.requestId,
                                    6601,
                                    opUid,
                                    0,
                                    body
                                ));
                            } else if (sequence == 305) {
                                byte[] body = JCoreWireCodec.buildJsonCommandPayload("{\"bind\":true}");
                                out.write(JCoreWireCodec.encodeResponseFrame(
                                    JCoreWireCodec.CMD_TAG_OP,
                                    JCoreWireCodec.VER_TAG_OP,
                                    req.requestId,
                                    6601,
                                    opUid,
                                    0,
                                    body
                                ));
                            } else {
                                byte[] errBody = JCoreWireCodec.buildJsonCommandPayload("{\"code\":6002}");
                                out.write(JCoreWireCodec.encodeResponseFrame(
                                    JCoreWireCodec.CMD_TAG_OP,
                                    JCoreWireCodec.VER_TAG_OP,
                                    req.requestId,
                                    6601,
                                    opUid,
                                    6002,
                                    errBody
                                ));
                            }
                            out.flush();
                        } else if (req.command == JCoreWireCodec.CMD_PROPERTY_OP) {
                            out.write(JCoreWireCodec.encodeResponseFrame(
                                JCoreWireCodec.CMD_PROPERTY_OP,
                                JCoreWireCodec.VER_PROPERTY_OP,
                                req.requestId,
                                6601,
                                opUid,
                                sequence >= 401 && sequence <= 403 ? 0 : 6002,
                                new byte[0]
                            ));
                            out.flush();
                        } else if (req.command == JCoreWireCodec.CMD_MOBILE_NUMBER) {
                            out.write(JCoreWireCodec.encodeResponseFrame(
                                JCoreWireCodec.CMD_MOBILE_NUMBER,
                                JCoreWireCodec.VER_MOBILE_NUMBER,
                                req.requestId,
                                6601,
                                opUid,
                                6002,
                                new byte[0]
                            ));
                            out.flush();
                        }
                    }
                } catch (Throwable ignored) {
                } finally {
                    try {
                        if (client != null) client.close();
                    } catch (Throwable ignored) {}
                    try {
                        opServer.close();
                    } catch (Throwable ignored) {}
                }
            }
        }, "test-op-rollback-server");
        opServerThread.setDaemon(true);
        opServerThread.start();

        ActionHelper.getInstance().registerReceiver(receiver);
        final List<String> opEvents = new java.util.ArrayList<String>();
        final CountDownLatch rollbackDoneLatch = new CountDownLatch(4);
        final CountDownLatch queryDoneLatch = new CountDownLatch(3);
        int opListenerId = JPushHelper.registerNitroListener(new JPushHelper.NitroEventListener() {
            @Override
            public void onEvent(String eventName, String payloadJson) {
                if ("TagAliasEvent".equals(eventName) || "MobileNumberEvent".equals(eventName)) {
                    opEvents.add(eventName + ":" + payloadJson);
                    if (payloadJson.contains("302") || payloadJson.contains("304") || payloadJson.contains("306") || payloadJson.contains("307")) {
                        rollbackDoneLatch.countDown();
                    }
                    if (payloadJson.contains("301") || payloadJson.contains("303") || payloadJson.contains("305")) {
                        queryDoneLatch.countDown();
                    }
                }
            }
        });

        JCoreStorage.updateRegistration(null, opUid, "op_pwd", "reg_op_660033", "dev_op_01");
        JCoreStorage.setServerRegistered(null, true);
        JCoreStorage.saveAlias(null, "old_local_alias");
        JCoreStorage.saveTags(null, new java.util.HashSet<String>(Arrays.asList("old_local_tag")));
        JCoreStorage.saveProperties(null, Collections.<String, Object>singletonMap("tier", "gold"));
        JCoreStorage.saveMobileNumber(null, "13800000001");

        JCoreTcpEngine.getInstance().setRequireServerAckForOperations(true);
        JCoreTcpEngine.getInstance().setCustomEndpoint("127.0.0.1", opPort);
        JCoreTcpEngine.getInstance().triggerReconnectNow(null);
        assertTrue(JCoreTcpEngine.getInstance().awaitHandshake(3000L));

        JPushInterface.setAlias(null, 302, "rejected_alias_302");
        JPushInterface.setTags(null, 304, new java.util.HashSet<String>(Arrays.asList("rejected_tag_304")));
        JPushInterface.setProperties(null, 306, Collections.<String, Object>singletonMap("tier", "rejected_tier"));
        JPushInterface.setMobileNumber(null, 307, "13999999999");

        assertTrue(rollbackDoneLatch.await(4000L, TimeUnit.MILLISECONDS));
        assertEquals("old_local_alias", JCoreStorage.getAlias(null));
        assertTrue(JCoreStorage.getTags(null).contains("old_local_tag"));
        assertTrue(!JCoreStorage.getTags(null).contains("rejected_tag_304"));
        assertEquals("gold", JCoreStorage.getProperties(null).get("tier"));
        assertEquals("13800000001", JCoreStorage.getMobileNumber(null));

        JPushInterface.getAlias(null, 301);
        JPushInterface.getAllTags(null, 303);
        JPushInterface.checkTagBindState(null, 305, "unbound_local_tag");

        assertTrue(queryDoneLatch.await(4000L, TimeUnit.MILLISECONDS));
        assertEquals("srv_alias_301", JCoreStorage.getAlias(null));
        assertTrue(JCoreStorage.getTags(null).contains("srv_t1") && JCoreStorage.getTags(null).contains("srv_t2"));
        boolean sawServerBindTrue = false;
        for (String ev : opEvents) {
            if (ev.contains("unbound_local_tag") && ev.contains("true")) {
                sawServerBindTrue = true;
            }
        }
        assertTrue(sawServerBindTrue);

        JPushHelper.unregisterNitroListener(opListenerId);
        JPushHelper.clearNitroListeners();

        // 2h. Verify acknowledged property mutations commit before their TagAliasEvent with pros
        //     via JPushModuleReceiver.onPropertyOperatorResult, and JPushBroadcastReceiver ignores intents with
        //     "jpush_already_dispatched" == true while JPushModuleReceiver.onNotifyMessageOpened clears stale NOTIFICATION_BUNDLE.
        final List<String> propEvents = java.util.Collections.synchronizedList(new java.util.ArrayList<String>());
        final CountDownLatch setPropertyAck = new CountDownLatch(1);
        final CountDownLatch deletePropertyAck = new CountDownLatch(1);
        final CountDownLatch cleanPropertyAck = new CountDownLatch(1);
        int propListenerId = JPushHelper.registerNitroListener(new JPushHelper.NitroEventListener() {
            @Override
            public void onEvent(String eventName, String payloadJson) {
                if (!"TagAliasEvent".equals(eventName)) return;
                propEvents.add(eventName + ":" + payloadJson);
                if (payloadJson.contains("401")) setPropertyAck.countDown();
                if (payloadJson.contains("402")) deletePropertyAck.countDown();
                if (payloadJson.contains("403")) cleanPropertyAck.countDown();
            }
        });
        ActionHelper.getInstance().registerReceiver(receiver);
        Map<String, Object> setPropsInput = new LinkedHashMap<String, Object>();
        setPropsInput.put("region", "apac");
        setPropsInput.put("plan", "enterprise");
        JPushInterface.setProperties(null, 401, setPropsInput);
        assertTrue(setPropertyAck.await(3000L, TimeUnit.MILLISECONDS));
        assertEquals("apac", JCoreStorage.getProperties(null).get("region"));
        assertEquals("enterprise", JCoreStorage.getProperties(null).get("plan"));

        Map<String, Object> delPropsInput = new LinkedHashMap<String, Object>();
        delPropsInput.put("region", "");
        JPushInterface.deleteProperties(null, 402, delPropsInput);
        assertTrue(deletePropertyAck.await(3000L, TimeUnit.MILLISECONDS));
        assertTrue(!JCoreStorage.getProperties(null).containsKey("region"));
        assertEquals("enterprise", JCoreStorage.getProperties(null).get("plan"));

        JPushInterface.cleanProperties(null, 403);
        assertTrue(cleanPropertyAck.await(3000L, TimeUnit.MILLISECONDS));
        assertTrue(JCoreStorage.getProperties(null).isEmpty());

        assertEquals(3L, (long) propEvents.size());
        assertTrue(propEvents.get(0).startsWith("TagAliasEvent:") && propEvents.get(0).contains("401") && propEvents.get(0).contains("enterprise"));
        assertTrue(propEvents.get(1).startsWith("TagAliasEvent:") && propEvents.get(1).contains("402") && propEvents.get(1).contains("region"));
        assertTrue(propEvents.get(2).startsWith("TagAliasEvent:") && propEvents.get(2).contains("403"));
        JPushHelper.unregisterNitroListener(propListenerId);
        JPushHelper.clearNitroListeners();
        JCoreTcpEngine.getInstance().setRequireServerAckForOperations(false);
        JCoreTcpEngine.getInstance().setCustomEndpoint("", 0);
        JCoreTcpEngine.getInstance().stop(null);
        JCoreStorage.clearRegistration(null);


        android.os.Bundle staleBundle = new android.os.Bundle();
        cn.jiguang.plugins.push.receiver.JPushBroadcastReceiver.NOTIFICATION_BUNDLE = staleBundle;
        NotificationMessage openedMsg = new NotificationMessage();
        openedMsg.msgId = "nm_open_404";
        openedMsg.notificationTitle = "OpenTitle";
        openedMsg.notificationContent = "OpenBody";
        receiver.onNotifyMessageOpened(null, openedMsg);
        assertEquals(null, cn.jiguang.plugins.push.receiver.JPushBroadcastReceiver.NOTIFICATION_BUNDLE);

        cn.jiguang.plugins.push.receiver.JPushBroadcastReceiver broadcastReceiver =
            new cn.jiguang.plugins.push.receiver.JPushBroadcastReceiver();
        android.content.Intent alreadyDispatchedIntent = new android.content.Intent(JPushInterface.ACTION_NOTIFICATION_OPENED) {
            @Override
            public String getAction() {
                return JPushInterface.ACTION_NOTIFICATION_OPENED;
            }
            @Override
            public boolean getBooleanExtra(String name, boolean defaultValue) {
                if ("jpush_already_dispatched".equals(name)) {
                    return true;
                }
                return defaultValue;
            }
            @Override
            public android.os.Bundle getExtras() {
                return new android.os.Bundle();
            }
        };
        broadcastReceiver.onReceive(null, alreadyDispatchedIntent);
        assertEquals(null, cn.jiguang.plugins.push.receiver.JPushBroadcastReceiver.NOTIFICATION_BUNDLE);
        ActionHelper.getInstance().unregisterReceiver(receiver);
        JPushHelper.clearNitroListeners();

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
                    sisUdpServer.setSoTimeout(15000);
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
                    sisTcpServer.setSoTimeout(15000);
                    client = sisTcpServer.accept();
                    client.setSoTimeout(10000);
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

    @Test
    public void testCloudParityLoginThirdTokenAndMsgType4Filtering() throws Exception {
        final String fcmToken142 =
            "fcm_token_142_bytes_APA91bH0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef012345";
        final long testUid = 88971093104L;
        final ServerSocket serverSocket = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"));
        final int port = serverSocket.getLocalPort();
        final List<JCoreWireCodec.LoginRequest> logins = new java.util.concurrent.CopyOnWriteArrayList<JCoreWireCodec.LoginRequest>();
        final List<JCoreWireCodec.MessageAck> acks = new java.util.concurrent.CopyOnWriteArrayList<JCoreWireCodec.MessageAck>();
        final CountDownLatch secondLoginLatch = new CountDownLatch(1);
        final CountDownLatch ackLatch = new CountDownLatch(2);

        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket.setSoTimeout(10000);
                    for (int connIdx = 0; connIdx < 2; connIdx++) {
                        Socket client = serverSocket.accept();
                        client.setSoTimeout(5000);
                        try {
                            InputStream in = client.getInputStream();
                            OutputStream out = client.getOutputStream();
                            int sid = 55000 + connIdx;
                            boolean validLogin = false;

                            while (!client.isClosed()) {
                                JCoreWireCodec.DecodedFrame req = JCoreWireCodec.readClientFrameFromStream(in, testUid);
                                if (req == null) {
                                    break;
                                }
                                if (req.command == JCoreWireCodec.CMD_LOGIN) {
                                    JCoreWireCodec.LoginRequest parsedLogin = JCoreWireCodec.parseLoginRequest(req.body);
                                    logins.add(parsedLogin);
                                    validLogin = parsedLogin != null
                                        && "328192|387328".equals(parsedLogin.sdkVerInfo)
                                        && "4|64".equals(parsedLogin.gSdkTypeInfo)
                                        && parsedLogin.romPlatformMask == (connIdx == 0 ? (byte) 8 : (byte) (8 | 32));
                                    byte[] loginResp = JCoreWireCodec.buildLoginResponsePayload(sid, 520, "", 1700002000);
                                    out.write(JCoreWireCodec.encodeResponseFrame(
                                        JCoreWireCodec.CMD_LOGIN,
                                        JCoreWireCodec.VER_LOGIN,
                                        req.requestId,
                                        sid,
                                        testUid,
                                        0,
                                        loginResp
                                    ));
                                    out.flush();
                                    if (connIdx == 1) {
                                        secondLoginLatch.countDown();
                                    }
                                } else if (req.command == JCoreWireCodec.CMD_HEARTBEAT) {
                                    // Match real JPush cloud server: respond with cmd=19 generic heartbeat ACK
                                    out.write(JCoreWireCodec.encodeResponseFrame(
                                        19,
                                        JCoreWireCodec.VER_HEARTBEAT,
                                        req.requestId,
                                        0,
                                        testUid,
                                        513,
                                        new byte[9]
                                    ));
                                    out.flush();
                                    if (connIdx == 0) {
                                        // Push internal msgType=4 msgId=999999 followed by valid msgType=2 custom message with _j_data_
                                        byte[] internalPush = JCoreWireCodec.buildPushMessagePayload(
                                            (byte) 4,
                                            999999L,
                                            "com.example.jpushdemo\n3af172b794896c3e1de43fe7"
                                        );
                                        out.write(JCoreWireCodec.encodeServerFrame(
                                            JCoreWireCodec.CMD_PUSH_MESSAGE,
                                            1,
                                            JCoreWireCodec.ALG_AES_CBC,
                                            0,
                                            10000L,
                                            testUid,
                                            sid,
                                            internalPush
                                        ));
                                        byte[] validCustomPush = JCoreWireCodec.buildPushMessagePayload(
                                            (byte) 2,
                                            888888L,
                                            DEMO_TEST_PACKAGE_NAME + "\n" + DEMO_TEST_APP_KEY
                                                + "\n{\"message\":\"real_user_msg\",\"_j_data_\":\"jdata_unit_01\"}"
                                        );
                                        out.write(JCoreWireCodec.encodeServerFrame(
                                            JCoreWireCodec.CMD_PUSH_MESSAGE,
                                            JCoreWireCodec.VER_PUSH_MESSAGE,
                                            JCoreWireCodec.ALG_AES_CBC,
                                            0,
                                            10001L,
                                            testUid,
                                            sid,
                                            validCustomPush
                                        ));
                                        out.flush();
                                    }
                                } else if (req.command == JCoreWireCodec.CMD_MESSAGE_ACK) {
                                    JCoreWireCodec.MessageAck ack = JCoreWireCodec.parseMessageAck(req.body);
                                    if (ack != null) {
                                        acks.add(ack);
                                        ackLatch.countDown();
                                    }
                                } else if (req.command == JCoreWireCodec.CMD_THIRD_TOKEN) {
                                    // Match real JPush cloud server: if Login sdkVerInfo/romPlatformMask/gSdkTypeInfo was invalid, close socket (EOF)!
                                    if (!validLogin) {
                                        client.close();
                                        break;
                                    }
                                    out.write(JCoreWireCodec.encodeResponseFrame(
                                        JCoreWireCodec.CMD_THIRD_TOKEN,
                                        JCoreWireCodec.VER_THIRD_TOKEN,
                                        req.requestId,
                                        sid,
                                        testUid,
                                        0,
                                        new byte[0]
                                    ));
                                    out.flush();
                                }
                            }
                        } finally {
                            try { client.close(); } catch (Throwable ignored) {}
                        }
                    }
                } catch (Throwable ignored) {
                } finally {
                    try { serverSocket.close(); } catch (Throwable ignored) {}
                }
            }
        }, "test-cloud-parity-unit-server");
        serverThread.setDaemon(true);
        serverThread.start();

        final JCoreTcpEngine engine = JCoreTcpEngine.getInstance();
        final JPushModuleReceiver receiver = new JPushModuleReceiver();
        final List<String> customMessages = new java.util.concurrent.CopyOnWriteArrayList<String>();
        final CountDownLatch customMsgLatch = new CountDownLatch(1);
        final CountDownLatch thirdTokenLatch = new CountDownLatch(3);
        int listenerId = 0;

        try {
            engine.stop(null);
            JCoreStorage.clearRegistration(null);
            JCoreStorage.setAppKey(null, DEMO_TEST_APP_KEY);
            JCoreStorage.setChannel(null, "prod");
            JCoreStorage.saveThirdToken(null, (byte) 8, "");
            JCoreStorage.updateRegistration(null, testUid, "parity_pwd", "100d8559081eb0a0367", "dev_parity_unit");
            JCoreStorage.setServerRegistered(null, true);

            ActionHelper.getInstance().clearRegisteredReceivers();
            ActionHelper.getInstance().registerReceiver(receiver);
            listenerId = JPushHelper.registerNitroListener(new JPushHelper.NitroEventListener() {
                @Override
                public void onEvent(String eventName, String payloadJson) {
                    if ("CustomMessageEvent".equals(eventName)) {
                        customMessages.add(payloadJson);
                        if (payloadJson.contains("888888")) {
                            customMsgLatch.countDown();
                        }
                    } else if ("CommandEvent".equals(eventName)
                        && payloadJson.contains("10000")
                        && payloadJson.contains("\"commandResult\":0")) {
                        thirdTokenLatch.countDown();
                    }
                }
            });

            engine.setCustomEndpoint("127.0.0.1", port);
            engine.triggerReconnectNow(null);
            assertTrue(engine.awaitHandshake(5000L));
            assertTrue(engine.isLoggedIn());

            // Enqueue 3 concurrent 142-byte FCM token bindings (matching Android startup)
            ActionHelper.getInstance().setThirdTokenWithCallBack(null, fcmToken142, (byte) 8, false);
            ActionHelper.getInstance().setThirdTokenWithCallBack(null, fcmToken142, (byte) 8, false);
            ActionHelper.getInstance().setThirdTokenWithCallBack(null, fcmToken142, (byte) 8, false);
            assertTrue("All 3 CMD_THIRD_TOKEN requests must succeed when Login sdkVerInfo matches binary parity",
                thirdTokenLatch.await(5000L, TimeUnit.MILLISECONDS));
            assertEquals(fcmToken142, JCoreStorage.getThirdToken(null, (byte) 8));

            assertTrue("Valid custom message 888888 must be delivered", customMsgLatch.await(5000L, TimeUnit.MILLISECONDS));
            assertTrue("Both downlink frames must be ACKed", ackLatch.await(5000L, TimeUnit.MILLISECONDS));
            assertEquals("Internal msgType=4 msgId=999999 must be filtered out", 1, customMessages.size());
            assertTrue(!customMessages.get(0).contains("999999"));

            boolean sawJDataAck = false;
            for (JCoreWireCodec.MessageAck ack : acks) {
                if (ack.messageId == 888888L && "jdata_unit_01".equals(ack.extData)) {
                    sawJDataAck = true;
                }
            }
            assertTrue("CMD_MESSAGE_ACK must echo _j_data_", sawJDataAck);

            // Trigger Fast-Login reconnect and verify romPlatformMask upgrades from 0x08 to 0x28 (8 | 32)
            engine.triggerReconnectNow(null);
            assertTrue(engine.awaitHandshake(5000L));
            assertTrue(secondLoginLatch.await(5000L, TimeUnit.MILLISECONDS));
            assertEquals(2, logins.size());
            assertEquals("328192|387328", logins.get(0).sdkVerInfo);
            assertEquals("4|64", logins.get(0).gSdkTypeInfo);
            assertEquals((byte) 8, logins.get(0).romPlatformMask);
            assertEquals((byte) (8 | 32), logins.get(1).romPlatformMask);
        } finally {
            if (listenerId > 0) {
                JPushHelper.unregisterNitroListener(listenerId);
            }
            ActionHelper.getInstance().unregisterReceiver(receiver);
            JCoreStorage.saveThirdToken(null, (byte) 8, "");
            engine.setCustomEndpoint("", 0);
            engine.stop(null);
            JCoreStorage.clearRegistration(null);
            try { serverSocket.close(); } catch (Throwable ignored) {}
        }
    }
}
