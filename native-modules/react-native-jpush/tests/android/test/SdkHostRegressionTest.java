package test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import cn.jiguang.android.IDataShare;
import cn.jiguang.api.JCoreInterface;
import cn.jiguang.api.MultiSpHelper;
import cn.jiguang.core.JCoreCrypto;
import cn.jiguang.core.JCoreStorage;
import cn.jiguang.core.JCoreTcpEngine;
import cn.jiguang.core.JCoreWireCodec;
import cn.jiguang.internal.JCoreInternalHelper;
import cn.jiguang.net.DefaultHostVerifier;
import cn.jiguang.plugins.core.JCoreModule;
import cn.jiguang.plugins.push.JPushModule;
import cn.jiguang.plugins.push.common.JConstants;
import cn.jiguang.plugins.push.common.JLogger;
import cn.jiguang.plugins.push.helper.JPushHelper;
import cn.jiguang.plugins.push.receiver.JPushModuleReceiver;
import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationCustomButton;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.local.ActionHelper;
import cn.jpush.android.service.DataProvider;
import cn.jpush.android.service.DataShare;
import cn.jpush.android.service.DownloadProvider;
import cn.jpush.android.service.JPushMessageReceiver;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.security.cert.X509Certificate;

public final class SdkHostRegressionTest {
    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Assertion failed: " + message);
        }
    }

    private static void testCryptoParity() throws Exception {
        // 1. Exact generateSeedKey math: idx = abs(seed) % 10; val = seed * multipliers[idx] + (seed % moduli[idx]); md5("JCKP" + val)
        long seed = 123456789L;
        // idx = 9 -> multiplier = 37, modulus = 91 -> 123456789 % 91 = 1 -> val = 123456789 * 37 + 1 = 4567901194
        String expectedSeedKey = JCoreCrypto.md5Hex("JCKP4567901194");
        String seedKey1 = JCoreCrypto.generateSeedKey(seed);
        check(seedKey1.length() == 32 && seedKey1.equals(expectedSeedKey), "generateSeedKey must match exact JCKP formula");

        // 2. AES-256-CBC with 32 ASCII bytes of generateSeedKey(seed) and first 16 ASCII bytes as IV
        byte[] plain = "OneKey-Clean-Room-JPush-Payload-2026".getBytes(StandardCharsets.UTF_8);
        byte[] aes256Cipher = JCoreCrypto.aes256CbcSeedEncrypt(plain, seed);
        check(aes256Cipher.length > 0, "AES-256-CBC seed encryption must succeed");
        byte[] aes256Plain = JCoreCrypto.aes256CbcSeedDecrypt(aes256Cipher, seed);
        check(Arrays.equals(plain, aes256Plain), "AES-256-CBC seed roundtrip must match");

        // 3. Pure-Java SM4-CBC verification against GM/T 0002-2012 standard vector & seed roundtrip
        byte[] sm4StdKey = JCoreCrypto.hexDecode("0123456789abcdeffedcba9876543210");
        byte[] sm4ZeroIv = new byte[16];
        byte[] sm4StdBlock = JCoreCrypto.hexDecode("0123456789abcdeffedcba9876543210");
        byte[] sm4StdEnc = JCoreCrypto.sm4CbcEncrypt(sm4StdBlock, sm4StdKey, sm4ZeroIv);
        String firstBlockHex = JCoreCrypto.hexEncode(Arrays.copyOf(sm4StdEnc, 16));
        check(
            "681edf34d206965e86b3e94f536e4246".equals(firstBlockHex),
            "SM4 block cipher must match GM/T 0002-2012 standard test vector, got: " + firstBlockHex
        );
        byte[] sm4StdDec = JCoreCrypto.sm4CbcDecrypt(sm4StdEnc, sm4StdKey, sm4ZeroIv);
        check(Arrays.equals(sm4StdBlock, sm4StdDec), "SM4-CBC standard vector decrypt must match");

        byte[] sm4SeedEnc = JCoreCrypto.sm4CbcSeedEncrypt(plain, seed);
        check(sm4SeedEnc.length > 0, "SM4-CBC seed encryption must succeed");
        byte[] sm4SeedDec = JCoreCrypto.sm4CbcSeedDecrypt(sm4SeedEnc, seed);
        check(Arrays.equals(plain, sm4SeedDec), "SM4-CBC seed roundtrip must match");

        // 4. HTTP Report AES-256-CBC (IV = Base64Decode("aW9wMjAzMDQwNTA2YVBrIQ==") = "iop203040506aPk!")
        byte[] httpCipher = JCoreCrypto.aes256CbcHttpEncrypt(plain, seedKey1);
        check(httpCipher.length > 0, "HTTP report AES-256-CBC encryption must succeed");
        byte[] httpDec = JCoreCrypto.aes256CbcHttpDecrypt(httpCipher, seedKey1);
        check(Arrays.equals(plain, httpDec), "HTTP report AES-256-CBC roundtrip must match");

        // 5. GZIP, RSA, MD5, SHA-1, HmacSHA256
        byte[] gz = JCoreCrypto.gzipCompress(plain);
        check(Arrays.equals(plain, JCoreCrypto.gzipDecompress(gz)), "GZIP roundtrip must match");

        for (int length : new int[]{65535, 65536, 65537}) {
            byte[] boundaryText = new byte[length];
            java.util.Random random = new java.util.Random(length);
            for (int i = 0; i < length; i++) boundaryText[i] = (byte) ('a' + random.nextInt(26));
            byte[] decoded = JCoreCrypto.gzipDecompress(JCoreCrypto.gzipCompress(boundaryText));
            check(length <= 65536 ? Arrays.equals(boundaryText, decoded) : decoded.length == 0,
                "GZIP must enforce the decoded resource boundary at " + length + " bytes");
        }

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair kp = kpg.generateKeyPair();
        String pubB64 = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
        String privB64 = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
        String rsaCipherB64 = JCoreCrypto.rsaEncryptBase64(seedKey1, pubB64);
        check(seedKey1.equals(JCoreCrypto.rsaDecryptBase64(rsaCipherB64, privB64)), "RSA roundtrip must match");

        check(JCoreCrypto.md5Hex("abc").equals("900150983cd24fb0d6963f7d28e17f72"), "MD5 vector must match");
        check(JCoreCrypto.sha1Hex("abc").equals("a9993e364706816aba3e25717850c26c9cd0d89d"), "SHA-1 vector must match");
    }

    private static void testUdpSis10ByteAndAsymmetricTcpWireCodec() throws Exception {
        // 1. UDP SIS 10-byte wire header verification:
        //    0..1: uint16 totalLen = 10 + encrypted.length
        //    2..3: 'U', 'G'
        //    4..7: uint32 ((0x11 << 24) | (seed & 0xffffff))
        //    8..9: uint16 preEncryptLen = compressed.length
        // Non-production demo test AppKey and sandbox package name used solely for automated native tests.
        final String demoTestAppKey = "1f2027d3cfb1b51dac664c42";
        final String demoTestPackageName = "com.devstudio.msgbench";
        String sisReqJson = JCoreWireCodec.buildSisRequestJson(
            1,
            demoTestAppKey,
            900001L,
            "46000",
            Arrays.asList("10.0.0.1:7000"),
            1
        );
        check(
            sisReqJson.contains("\"sdkver\":\"5.1.1\"")
                && sisReqJson.contains("\"fail_ips\":[\"10.0.0.1:7000\"]")
                && sisReqJson.contains("\"cert_ver\":1"),
            "SIS request JSON must match production fields"
        );
        int sisSeed = 0x123456;
        byte[] sisPacket = JCoreWireCodec.encodeUdpSisPacket(sisReqJson, sisSeed);
        ByteBuffer sisBuf = ByteBuffer.wrap(sisPacket);
        int sisTotalLen = sisBuf.getShort() & 0xFFFF;
        check(sisTotalLen == sisPacket.length, "UDP SIS bytes 0..1 must equal total packet length");
        check(sisBuf.get() == 'U' && sisBuf.get() == 'G', "UDP SIS bytes 2..3 must be 'U','G'");
        int sisKeyField = sisBuf.getInt();
        check(((sisKeyField >>> 24) & 0xFF) == 0x11, "UDP SIS byte 4 flags must be 0x11");
        check((sisKeyField & 0xFFFFFF) == sisSeed, "UDP SIS bytes 5..7 must be 24-bit seed");
        int sisPreEncryptLen = sisBuf.getShort() & 0xFFFF;
        check(sisPreEncryptLen > 0 && sisPreEncryptLen < sisTotalLen, "UDP SIS bytes 8..9 must be compressed length");
        check(sisReqJson.equals(JCoreWireCodec.decodeUdpSisPacket(sisPacket)), "UDP SIS 10-byte decode must match");

        // 2. Asymmetric TCP framing (24-byte Client -> Server header, 20-byte Server -> Client header)
        //    Verify both algorithm=1 (AES-256-CBC) and algorithm=2 (SM4-CBC)
        for (int alg : new int[] { JCoreWireCodec.ALG_AES_CBC, JCoreWireCodec.ALG_SM4_CBC }) {
            JCoreWireCodec.setDefaultAlgorithm(alg);

            // Register (cmd=0, ver=29)
            byte[] regReqBytes = JCoreWireCodec.buildRegisterPayload(
                demoTestAppKey,
                "developer-default",
                demoTestPackageName,
                "dev_001"
            );
            byte[] regClientFrame = JCoreWireCodec.encodeClientFrame(
                JCoreWireCodec.CMD_REGISTER,
                JCoreWireCodec.VER_REGISTER,
                alg,
                0,
                101L,
                7654321,
                0L,
                regReqBytes
            );
            ByteBuffer regHdr = ByteBuffer.wrap(regClientFrame);
            int regRawLen = regHdr.getShort() & 0xFFFF;
            check((regRawLen & 0x8000) != 0 && (regRawLen & 0x7FFF) == regClientFrame.length, "Client frame bit 15 and length");
            check((regHdr.get() & 0xFF) == 29 && (regHdr.get() & 0xFF) == 0 && (regHdr.get() & 0xFF) == alg, "Client Register header fields");

            JCoreWireCodec.DecodedFrame decRegReq = JCoreWireCodec.decodeClientFrame(regClientFrame, 0L);
            check(decRegReq != null && decRegReq.command == JCoreWireCodec.CMD_REGISTER && decRegReq.version == 29, "Decoded Register client frame");
            JCoreWireCodec.RegisterRequest parsedRegReq = JCoreWireCodec.parseRegisterRequest(decRegReq.body);
            check(
                parsedRegReq != null
                    && (" $$ $$" + demoTestPackageName + "$$" + demoTestAppKey).equals(parsedRegReq.key)
                    && demoTestAppKey.equals(parsedRegReq.appKey)
                    && demoTestPackageName.equals(parsedRegReq.packageName)
                    && "developer-default".equals(parsedRegReq.channel)
                    && "dev_001".equals(parsedRegReq.deviceId)
                    && parsedRegReq.regBusiness == 1,
                "Register v29 payload fields must match production spec"
            );

            byte[] regRespBytes = JCoreWireCodec.buildRegisterResponsePayload(900001L, "pw_secret", "reg_id_xyz", "srv_dev_001");
            byte[] regServerFrame = JCoreWireCodec.encodeResponseFrame(
                JCoreWireCodec.CMD_REGISTER,
                JCoreWireCodec.VER_REGISTER,
                alg,
                0,
                101L,
                decRegReq.sessionId,
                900001L,
                0,
                regRespBytes
            );
            JCoreWireCodec.DecodedFrame decRegResp = JCoreWireCodec.decodeServerFrame(regServerFrame, 900001L);
            JCoreWireCodec.RegisterResponse parsedRegResp = JCoreWireCodec.parseRegisterResponse(decRegResp);
            check(
                parsedRegResp != null
                    && parsedRegResp.statusCode == 0
                    && parsedRegResp.uid == 900001L
                    && "pw_secret".equals(parsedRegResp.password)
                    && "reg_id_xyz".equals(parsedRegResp.registrationId)
                    && "srv_dev_001".equals(parsedRegResp.deviceId),
                "RegisterResponse 20-byte server frame must match"
            );

            // Login (cmd=1, ver=27)
            byte[] loginReqBytes = JCoreWireCodec.buildLoginPayload("pw_secret", "appkey_123", "srv_dev_001", "US");
            byte[] loginClientFrame = JCoreWireCodec.encodeClientFrame(
                JCoreWireCodec.CMD_LOGIN,
                JCoreWireCodec.VER_LOGIN,
                alg,
                0,
                102L,
                0,
                900001L,
                loginReqBytes
            );
            JCoreWireCodec.DecodedFrame decLoginReq = JCoreWireCodec.decodeClientFrame(loginClientFrame, 900001L);
            JCoreWireCodec.LoginRequest parsedLoginReq = JCoreWireCodec.parseLoginRequest(decLoginReq.body);
            check(
                parsedLoginReq != null
                    && parsedLoginReq.passwordMd5.equals(JCoreCrypto.md5Hex("pw_secret"))
                    && "5.1.1|5.9.0".equals(parsedLoginReq.sdkVerInfo)
                    && "appkey_123".equals(parsedLoginReq.appKey)
                    && "srv_dev_001".equals(parsedLoginReq.deviceId)
                    && "US".equals(parsedLoginReq.countryCode)
                    && parsedLoginReq.loginFlag == 1
                    && "1".equals(parsedLoginReq.gSdkTypeInfo),
                "Login v27 payload fields must match production spec"
            );

            byte[] loginRespBytes = JCoreWireCodec.buildLoginResponsePayload(4242, 520, "", 1700000000);
            byte[] loginServerFrame = JCoreWireCodec.encodeResponseFrame(
                JCoreWireCodec.CMD_LOGIN,
                JCoreWireCodec.VER_LOGIN,
                alg,
                0,
                102L,
                4242,
                900001L,
                0,
                loginRespBytes
            );
            JCoreWireCodec.LoginResponse parsedLoginResp = JCoreWireCodec.parseLoginResponse(
                JCoreWireCodec.decodeServerFrame(loginServerFrame, 900001L)
            );
            check(
                parsedLoginResp != null
                    && parsedLoginResp.statusCode == 0
                    && parsedLoginResp.sessionId == 4242
                    && parsedLoginResp.serverVersion == 520,
                "LoginResponse 20-byte server frame must match"
            );

            // Heartbeat (cmd=2, ver=8): uint32(1) + uint8(appState) + uint16(290) + uint8(1)
            byte[] hbBytes = JCoreWireCodec.buildHeartbeatPayload(false);
            JCoreWireCodec.HeartbeatRequest hbReq = JCoreWireCodec.parseHeartbeatRequest(hbBytes);
            check(
                hbReq != null
                    && hbReq.loginFlag == 1
                    && hbReq.intervalSeconds == 290
                    && hbReq.sdkType == 1,
                "Heartbeat v8 8-byte payload must match"
            );

            // PushMessage (cmd=3) & MessageReceipt (cmd=4, ver=3)
            String pushJson = "header_line\n{\"_j_msgid\":\"777\",\"n_title\":\"Hello\",\"n_content\":\"World\"}";
            byte[] pushBytes = JCoreWireCodec.buildPushMessagePayload((byte) 0, 777L, pushJson);
            byte[] pushServerFrame = JCoreWireCodec.encodeServerFrame(
                JCoreWireCodec.CMD_PUSH_MESSAGE,
                JCoreWireCodec.VER_PUSH_MESSAGE,
                alg,
                0,
                104L,
                900001L,
                0L,
                pushBytes
            );
            JCoreWireCodec.PushEnvelope env = JCoreWireCodec.parsePushMessage(
                JCoreWireCodec.decodeServerFrame(pushServerFrame, 900001L)
            );
            check(env != null && env.messageId == 777L && pushJson.equals(env.content), "PushMessage roundtrip");

            byte[] ackBytes = JCoreWireCodec.buildMessageAckPayload((byte) 0, 777L);
            byte[] ackClientFrame = JCoreWireCodec.encodeClientFrame(
                JCoreWireCodec.CMD_MESSAGE_ACK,
                JCoreWireCodec.VER_MESSAGE_ACK,
                alg,
                0,
                104L,
                4242,
                900001L,
                ackBytes
            );
            JCoreWireCodec.DecodedFrame decAck = JCoreWireCodec.decodeClientFrame(ackClientFrame, 900001L);
            JCoreWireCodec.MessageAck ack = JCoreWireCodec.parseMessageAck(decAck);
            check(
                decAck.version == 3 && ack != null && ack.statusCode == 0 && ack.messageType == 0 && ack.messageId == 777L,
                "MessageReceipt (cmd=4, ver=3) roundtrip"
            );

            // CtrlCommand (cmd=25, ver=1) & Ack
            byte[] ctrlBytes = JCoreWireCodec.buildCtrlCommandPayload(88801L, "{\"cmd\":5}");
            JCoreWireCodec.CtrlCommand ctrlCmd = JCoreWireCodec.parseCtrlCommand(ctrlBytes);
            check(ctrlCmd != null && ctrlCmd.requestId == 88801L && "{\"cmd\":5}".equals(ctrlCmd.jsonContent), "CtrlCommand parse");
            byte[] ctrlAckBytes = JCoreWireCodec.buildCtrlAckPayload(0, 88801L);
            JCoreWireCodec.CtrlAck ctrlAck = JCoreWireCodec.parseCtrlAck(ctrlAckBytes);
            check(ctrlAck != null && ctrlAck.statusCode == 0 && ctrlAck.requestId == 88801L, "CtrlAck parse");

            // MobileNumber (cmd=26, ver=1): uint8(7) + uint8(1) + string(mobileNumber)
            byte[] mobBytes = JCoreWireCodec.buildMobileNumberPayload(15, "13800138000");
            JCoreWireCodec.MobileNumberPayload mob = JCoreWireCodec.parseMobileNumberPayload(mobBytes);
            check(
                mob != null && mob.typeFlag == 7 && mob.opFlag == 1 && "13800138000".equals(mob.mobileNumber),
                "MobileNumber (cmd=26, ver=1) roundtrip"
            );

            // ThirdToken (cmd=27, ver=2): string(thirdToken) + uint8(platform) + uint8(unbind ? 0 : 1)
            byte[] dtBytes = JCoreWireCodec.buildThirdTokenPayload("fcm_token_abc", (byte) 8, false);
            JCoreWireCodec.DeviceTokenPayload dt = JCoreWireCodec.parseDeviceTokenPayload(dtBytes);
            check(dt != null && "fcm_token_abc".equals(dt.token) && dt.platform == 8 && !dt.unbind, "ThirdToken (cmd=27, ver=2) roundtrip");

            // Tags (cmd=28, ver=1): {"platform":"a","op":"add","tags":["btc","eth"],"curr":1,...}
            byte[] tagBytes = JCoreWireCodec.buildTagOperationPayload(1, 21, Arrays.asList("btc", "eth"));
            JCoreWireCodec.OperationPayload tagOp = JCoreWireCodec.parseOperationPayload(JCoreWireCodec.CMD_TAG_OP, tagBytes);
            check(
                tagOp != null
                    && "add".equals(tagOp.op)
                    && tagOp.action == 1
                    && tagOp.tags.contains("btc")
                    && tagOp.tags.contains("eth"),
                "Tags (cmd=28, ver=1) roundtrip"
            );

            // Alias (cmd=29, ver=1): {"platform":"a","op":"set","alias":"satoshi",...}
            byte[] aliasBytes = JCoreWireCodec.buildAliasOperationPayload(2, 22, "satoshi");
            JCoreWireCodec.OperationPayload aliasOp = JCoreWireCodec.parseOperationPayload(JCoreWireCodec.CMD_ALIAS_OP, aliasBytes);
            check(
                aliasOp != null && "set".equals(aliasOp.op) && aliasOp.action == 2 && "satoshi".equals(aliasOp.alias),
                "Alias (cmd=29, ver=1) roundtrip"
            );

            // UserProperties (cmd=38, ver=1): uint8(1) + string({"op":"set","data":{"tier":"vip"}})
            Map<String, Object> props = new LinkedHashMap<String, Object>();
            props.put("tier", "vip");
            byte[] propBytes = JCoreWireCodec.buildPropertyOperationPayload(1, 23, props);
            check(propBytes[0] == 1, "UserProperties (cmd=38, ver=1) must start with uint8(1)");
            JCoreWireCodec.OperationPayload propOp = JCoreWireCodec.parseOperationPayload(JCoreWireCodec.CMD_PROPERTY_OP, propBytes);
            check(
                propOp != null && "set".equals(propOp.op) && "vip".equals(propOp.properties.get("tier")),
                "UserProperties (cmd=38, ver=1) roundtrip"
            );
        }
        JCoreWireCodec.setDefaultAlgorithm(JCoreWireCodec.ALG_AES_CBC);
    }

    private static void testTcpEngineEndToEndAndProductionResilience() throws Exception {
        final ReactApplicationContext reactContext = new ReactApplicationContext();
        final JCoreModule jcoreModule = new JCoreModule(reactContext);
        final JPushModule jpushModule = new JPushModule(reactContext);
        final JPushModuleReceiver moduleReceiver = new JPushModuleReceiver();
        ActionHelper.getInstance().clearRegisteredReceivers();
        ActionHelper.getInstance().registerReceiver(moduleReceiver);

        final AtomicReference<CmdMessage> thirdTokenCmdRef = new AtomicReference<CmdMessage>();
        final CountDownLatch thirdTokenLatch = new CountDownLatch(1);
        ActionHelper.getInstance().registerReceiver(new JPushMessageReceiver() {
            @Override
            public void onCommandResult(Context context, CmdMessage cmdMessage) {
                if (cmdMessage != null && cmdMessage.cmd == 10000) {
                    thirdTokenCmdRef.set(cmdMessage);
                    thirdTokenLatch.countDown();
                }
            }
        });

        // 1. Allocate a dead TCP port to verify Multi-IP Waterfall Failover & fail_ips recording
        ServerSocket deadSocket = new ServerSocket(0, 1, InetAddress.getByName("127.0.0.1"));
        final int deadPort = deadSocket.getLocalPort();
        deadSocket.close();
        final String deadEndpoint = "127.0.0.1:" + deadPort;

        // 2. Start a real loopback TCP ServerSocket for the live endpoint
        final ServerSocket serverSocket = new ServerSocket(0, 10, InetAddress.getByName("127.0.0.1"));
        final int tcpPort = serverSocket.getLocalPort();
        final String liveEndpoint = "127.0.0.1:" + tcpPort;

        final CountDownLatch firstSessionDoneLatch = new CountDownLatch(1);
        final AtomicReference<JCoreWireCodec.MessageAck> notifyAckRef = new AtomicReference<JCoreWireCodec.MessageAck>();
        final AtomicReference<JCoreWireCodec.MessageAck> customAckRef = new AtomicReference<JCoreWireCodec.MessageAck>();
        final AtomicReference<JCoreWireCodec.CtrlAck> ctrlAckRef = new AtomicReference<JCoreWireCodec.CtrlAck>();
        final AtomicBoolean sawPreLoginAliasOnWire = new AtomicBoolean(false);
        final AtomicBoolean sawThirdTokenOnWire = new AtomicBoolean(false);
        final AtomicReference<Throwable> serverError = new AtomicReference<Throwable>();

        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket client = null;
                try {
                    serverSocket.setSoTimeout(8000);
                    client = serverSocket.accept();
                    client.setSoTimeout(8000);
                    InputStream in = client.getInputStream();
                    OutputStream out = client.getOutputStream();

                    // A. Expect Register (cmd=0, ver=29) 24-byte client frame
                    JCoreWireCodec.DecodedFrame regFrame = JCoreWireCodec.readClientFrameFromStream(in, 0L);
                    check(
                        regFrame != null && regFrame.command == JCoreWireCodec.CMD_REGISTER && regFrame.version == 29,
                        "Server expected Register (cmd=0, ver=29)"
                    );
                    JCoreWireCodec.RegisterRequest regReq = JCoreWireCodec.parseRegisterRequest(regFrame.body);
                    check(
                        regReq != null
                            && "1f2027d3cfb1b51dac664c42".equals(regReq.appKey)
                            && "com.devstudio.msgbench".equals(regReq.packageName),
                        "Server parsed RegisterRequest appKey and sandbox packageName"
                    );

                    long assignedUid = 660001L;
                    byte[] regRespBody = JCoreWireCodec.buildRegisterResponsePayload(
                        assignedUid,
                        "loopback_pwd",
                        "1a0018970a_e2e_reg",
                        "dev_e2e_660001"
                    );
                    byte[] regRespPacket = JCoreWireCodec.encodeResponseFrame(
                        JCoreWireCodec.CMD_REGISTER,
                        JCoreWireCodec.VER_REGISTER,
                        regFrame.requestId,
                        regFrame.sessionId,
                        assignedUid,
                        0,
                        regRespBody
                    );
                    out.write(regRespPacket);
                    out.flush();

                    // B. Expect Login (cmd=1, ver=27) 24-byte client frame
                    JCoreWireCodec.DecodedFrame loginFrame = JCoreWireCodec.readClientFrameFromStream(in, assignedUid);
                    check(
                        loginFrame != null && loginFrame.command == JCoreWireCodec.CMD_LOGIN && loginFrame.version == 27,
                        "Server expected Login (cmd=1, ver=27)"
                    );
                    JCoreWireCodec.LoginRequest loginReq = JCoreWireCodec.parseLoginRequest(loginFrame.body);
                    check(
                        loginReq != null && loginReq.passwordMd5.equals(JCoreCrypto.md5Hex("loopback_pwd")),
                        "Server verified Login password MD5"
                    );

                    int assignedSid = 8801;
                    byte[] loginRespBody = JCoreWireCodec.buildLoginResponsePayload(assignedSid, 520, "", 1700000100);
                    byte[] loginRespPacket = JCoreWireCodec.encodeResponseFrame(
                        JCoreWireCodec.CMD_LOGIN,
                        JCoreWireCodec.VER_LOGIN,
                        loginFrame.requestId,
                        assignedSid,
                        assignedUid,
                        0,
                        loginRespBody
                    );
                    out.write(loginRespPacket);
                    out.flush();

                    // C. Expect Heartbeat (cmd=2, ver=8)
                    JCoreWireCodec.DecodedFrame hbFrame = JCoreWireCodec.readClientFrameFromStream(in, assignedUid);
                    check(
                        hbFrame != null && hbFrame.command == JCoreWireCodec.CMD_HEARTBEAT && hbFrame.version == 8,
                        "Server expected Heartbeat (cmd=2, ver=8)"
                    );
                    byte[] hbRespPacket = JCoreWireCodec.encodeResponseFrame(
                        JCoreWireCodec.CMD_HEARTBEAT,
                        JCoreWireCodec.VER_HEARTBEAT,
                        hbFrame.requestId,
                        assignedSid,
                        assignedUid,
                        0,
                        new byte[0]
                    );
                    out.write(hbRespPacket);
                    out.flush();

                    // D. Send Downlink Notification PushMessage (cmd=3, messageType=0, messageId=50001)
                    String notifyJson = "meta_line\n{\"_j_msgid\":\"50001\",\"n_title\":\"OneKey Security\",\"n_content\":\"Hardware wallet connected\",\"n_extras\":{\"chain\":\"btc\"}}";
                    byte[] notifyPushBody = JCoreWireCodec.buildPushMessagePayload((byte) 0, 50001L, notifyJson);
                    byte[] notifyPushPacket = JCoreWireCodec.encodeServerFrame(
                        JCoreWireCodec.CMD_PUSH_MESSAGE,
                        JCoreWireCodec.VER_PUSH_MESSAGE,
                        JCoreWireCodec.ALG_AES_CBC,
                        0,
                        501L,
                        assignedUid,
                        0L,
                        notifyPushBody
                    );
                    out.write(notifyPushPacket);
                    out.flush();

                    // E. Send Downlink Custom PushMessage (cmd=3, messageType=2, messageId=50002)
                    String customJson = "{\"_j_msgid\":\"50002\",\"title\":\"Sync\",\"message\":\"tx_confirmed\",\"extras\":{\"hash\":\"0x123\"}}";
                    byte[] customPushBody = JCoreWireCodec.buildPushMessagePayload((byte) 2, 50002L, customJson);
                    byte[] customPushPacket = JCoreWireCodec.encodeServerFrame(
                        JCoreWireCodec.CMD_PUSH_MESSAGE,
                        JCoreWireCodec.VER_PUSH_MESSAGE,
                        JCoreWireCodec.ALG_AES_CBC,
                        0,
                        502L,
                        assignedUid,
                        0L,
                        customPushBody
                    );
                    out.write(customPushPacket);
                    out.flush();

                    // F. Send Downlink CtrlCommand (cmd=25, ver=1, requestId=99001)
                    byte[] ctrlPushBody = JCoreWireCodec.buildCtrlCommandPayload(99001L, "{\"cmd\":10,\"enable\":true}");
                    byte[] ctrlPushPacket = JCoreWireCodec.encodeServerFrame(
                        JCoreWireCodec.CMD_CTRL,
                        JCoreWireCodec.VER_CTRL,
                        JCoreWireCodec.ALG_AES_CBC,
                        0,
                        503L,
                        assignedUid,
                        0L,
                        ctrlPushBody
                    );
                    out.write(ctrlPushPacket);
                    out.flush();

                    // G. Read client frames until we have seen:
                    //    1) MessageAck #1 (50001)
                    //    2) MessageAck #2 (50002)
                    //    3) CtrlAck (99001)
                    //    4) Pre-login queued Alias op (cmd=29)
                    //    5) Pre-login queued ThirdToken upload (cmd=27, ver=2)
                    while (notifyAckRef.get() == null
                        || customAckRef.get() == null
                        || ctrlAckRef.get() == null
                        || !sawPreLoginAliasOnWire.get()
                        || !sawThirdTokenOnWire.get()) {
                        JCoreWireCodec.DecodedFrame clientFrame = JCoreWireCodec.readClientFrameFromStream(in, assignedUid);
                        if (clientFrame == null) {
                            break;
                        }
                        if (clientFrame.command == JCoreWireCodec.CMD_MESSAGE_ACK) {
                            JCoreWireCodec.MessageAck ack = JCoreWireCodec.parseMessageAck(clientFrame);
                            if (ack != null && ack.messageId == 50001L) {
                                notifyAckRef.set(ack);
                            } else if (ack != null && ack.messageId == 50002L) {
                                customAckRef.set(ack);
                            }
                        } else if (clientFrame.command == JCoreWireCodec.CMD_CTRL) {
                            ctrlAckRef.set(JCoreWireCodec.parseCtrlAck(clientFrame.body));
                        } else if (clientFrame.command == JCoreWireCodec.CMD_ALIAS_OP) {
                            JCoreWireCodec.OperationPayload op = JCoreWireCodec.parseOperationPayload(
                                JCoreWireCodec.CMD_ALIAS_OP,
                                clientFrame.body
                            );
                            if (op != null && "queued_before_login".equals(op.alias)) {
                                sawPreLoginAliasOnWire.set(true);
                                byte[] aliasResp = JCoreWireCodec.buildJsonCommandPayload("{\"code\":0,\"op\":\"set\"}");
                                out.write(JCoreWireCodec.encodeServerFrame(
                                    JCoreWireCodec.CMD_ALIAS_OP,
                                    JCoreWireCodec.VER_ALIAS_OP,
                                    JCoreWireCodec.ALG_AES_CBC,
                                    0,
                                    clientFrame.requestId,
                                    assignedUid,
                                    0L,
                                    aliasResp
                                ));
                                out.flush();
                            }
                        } else if (clientFrame.command == JCoreWireCodec.CMD_THIRD_TOKEN) {
                            JCoreWireCodec.DeviceTokenPayload dt = JCoreWireCodec.parseDeviceTokenPayload(clientFrame.body);
                            if (dt != null && "fcm_prelogin_token_777".equals(dt.token)) {
                                sawThirdTokenOnWire.set(true);
                                out.write(JCoreWireCodec.encodeResponseFrame(
                                    JCoreWireCodec.CMD_THIRD_TOKEN,
                                    JCoreWireCodec.VER_THIRD_TOKEN,
                                    clientFrame.requestId,
                                    assignedSid,
                                    assignedUid,
                                    0,
                                    new byte[0]
                                ));
                                out.flush();
                            }
                        }
                    }
                    firstSessionDoneLatch.countDown();
                } catch (Throwable t) {
                    serverError.set(t);
                    firstSessionDoneLatch.countDown();
                } finally {
                    try {
                        if (client != null) client.close();
                    } catch (Throwable ignored) {}
                }
            }
        }, "test-jcore-loopback-server-1");
        serverThread.setDaemon(true);
        serverThread.start();

        // Configure candidate endpoints [deadEndpoint, liveEndpoint] to verify Multi-IP Waterfall Failover
        JCoreStorage.clearFailedIps(reactContext);
        JCoreStorage.clearRegistration(reactContext);
        check("".equals(JPushInterface.getRegistrationID(reactContext)), "Registration ID must be empty before server Register (cmd=0)");
        check(JCoreStorage.getUid(reactContext) == 0L, "UID must be 0 before server Register (cmd=0)");
        check("".equals(JCoreStorage.getPassword(reactContext)), "Password must be empty before server Register (cmd=0)");
        JCoreStorage.setAppKey(reactContext, "1f2027d3cfb1b51dac664c42");
        JCoreTcpEngine.getInstance().setCandidateEndpoints(Arrays.asList(deadEndpoint, liveEndpoint));

        // Enqueue Alias (cmd=29) and ThirdToken (cmd=27) BEFORE calling init() to verify pre-login request queue flush!
        JPushInterface.setAlias(reactContext, 91, "queued_before_login");
        Bundle fcmToken = new Bundle();
        fcmToken.putString("token", "fcm_prelogin_token_777");
        fcmToken.putByte("platform", (byte) 8);
        cn.jpush.android.api.JThirdPlatFormInterface.doAction(
            reactContext, cn.jpush.android.api.JThirdPlatFormInterface.ACTION_REGISTER_TOKEN, fcmToken
        );
        check("".equals(JCoreStorage.getThirdToken(reactContext, (byte) 8)), "FCM token must not be persisted before server acknowledgement");
        check(JCoreTcpEngine.getInstance().getPreLoginQueueSize() >= 2, "Pre-login requests must be queued before login");

        jpushModule.init();

        check(JCoreTcpEngine.getInstance().awaitHandshake(5000L), "JCoreTcpEngine handshake must complete within 5s");
        check(firstSessionDoneLatch.await(5000L, TimeUnit.MILLISECONDS), "Server session #1 must receive all expected frames");
        if (serverError.get() != null) {
            throw new AssertionError("Loopback server #1 failed: " + serverError.get().getMessage(), serverError.get());
        }

        // Verify deadEndpoint was recorded in failedIps and liveEndpoint was saved as lastGoodIp
        List<String> failedIps = JCoreStorage.getFailedIps(reactContext);
        check(failedIps.contains(deadEndpoint), "Multi-IP failover must record dead endpoint in failed_ips");
        check(liveEndpoint.equals(JCoreStorage.getLastGoodIp(reactContext)), "Multi-IP failover must save live endpoint as last_good_ip");

        // Verify UDP SIS receives fail_ips containing deadEndpoint and returns full SIS config
        final AtomicReference<String> sisReceivedReqRef = new AtomicReference<String>("");
        final DatagramSocket sisServer = new DatagramSocket(0, InetAddress.getByName("127.0.0.1"));
        final int sisPort = sisServer.getLocalPort();
        Thread sisThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] buf = new byte[2048];
                    DatagramPacket pkt = new DatagramPacket(buf, buf.length);
                    sisServer.receive(pkt);
                    byte[] received = Arrays.copyOf(pkt.getData(), pkt.getLength());
                    String reqJson = JCoreWireCodec.decodeUdpSisPacket(received);
                    sisReceivedReqRef.set(reqJson);
                    String respJson = "{\"ips\":[\"" + liveEndpoint + "\"],\"ssl_ips\":[\"" + liveEndpoint
                        + "\"],\"sis_ips\":[\"127.0.0.1:" + sisPort
                        + "\"],\"http_report\":[\"127.0.0.1:18080\"],\"https_report\":[\"127.0.0.1:18443\"],\"cert_ver\":2,\"data_report\":true}";
                    byte[] respBytes = JCoreWireCodec.encodeUdpSisPacket(respJson, 7777L);
                    DatagramPacket respPkt = new DatagramPacket(respBytes, respBytes.length, pkt.getAddress(), pkt.getPort());
                    sisServer.send(respPkt);
                } catch (Throwable ignored) {
                } finally {
                    sisServer.close();
                }
            }
        }, "test-udp-sis-server");
        sisThread.setDaemon(true);
        sisThread.start();

        String[] resolvedSis = JCoreTcpEngine.getInstance().resolveUdpSisEndpoint(reactContext, "127.0.0.1", sisPort);
        check("127.0.0.1".equals(resolvedSis[0]) && String.valueOf(tcpPort).equals(resolvedSis[1]), "UDP SIS loopback resolution");
        check(sisReceivedReqRef.get().contains(deadEndpoint), "UDP SIS request must include recorded fail_ips");
        check(JCoreStorage.getSisCertVer(reactContext) == 2 && JCoreStorage.getSisDataReport(reactContext), "SIS config must be persisted in JCoreStorage");

        // Verify MessageReceipt (cmd=4, ver=3), CtrlAck (cmd=25, ver=1), and ThirdToken callback (CmdMessage 10000)
        JCoreWireCodec.MessageAck ack1 = notifyAckRef.get();
        JCoreWireCodec.MessageAck ack2 = customAckRef.get();
        JCoreWireCodec.CtrlAck ctrlAck = ctrlAckRef.get();
        check(ack1 != null && ack1.statusCode == 0 && ack1.messageType == 0 && ack1.messageId == 50001L, "MessageReceipt #1");
        check(ack2 != null && ack2.statusCode == 0 && ack2.messageType == 2 && ack2.messageId == 50002L, "MessageReceipt #2");
        check(ctrlAck != null && ctrlAck.statusCode == 0 && ctrlAck.requestId == 99001L, "CtrlCommand Ack (cmd=25, ver=1)");
        check(thirdTokenLatch.await(3000L, TimeUnit.MILLISECONDS) && thirdTokenCmdRef.get() != null && thirdTokenCmdRef.get().errorCode == 0, "ThirdToken CmdMessage(10000) callback");
        check("fcm_prelogin_token_777".equals(JCoreStorage.getThirdToken(reactContext, (byte) 8)), "ThirdToken persisted in JCoreStorage");

        check("1a0018970a_e2e_reg".equals(JPushInterface.getRegistrationID(reactContext)), "Registration ID updated");
        check(JCoreStorage.getUid(reactContext) == 660001L, "UID updated");
        check(JCoreInterface.getSid() == 8801, "Session ID updated");

        // 3. Test Registered Device Fast-Login & Login statusCode == 108 automatic re-registration!
        final AtomicInteger secondSessionFirstCmd = new AtomicInteger(-1);
        final AtomicBoolean reRegisteredAfter108 = new AtomicBoolean(false);
        final CountDownLatch secondSessionLatch = new CountDownLatch(1);
        Thread serverThread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket client = null;
                try {
                    serverSocket.setSoTimeout(8000);
                    client = serverSocket.accept();
                    client.setSoTimeout(8000);
                    InputStream in = client.getInputStream();
                    OutputStream out = client.getOutputStream();

                    // Since device is already registered, first frame MUST be Login (cmd=1, ver=27) (Fast-Login)!
                    JCoreWireCodec.DecodedFrame firstFrame = JCoreWireCodec.readClientFrameFromStream(in, 660001L);
                    check(firstFrame != null, "Fast-login server expected frame");
                    secondSessionFirstCmd.set(firstFrame.command);
                    check(firstFrame.command == JCoreWireCodec.CMD_LOGIN, "Registered device must send Login (cmd=1) directly without Register (cmd=0)");

                    // Respond with statusCode = 108 to trigger automatic clearRegistration + Register(cmd=0) + Login(cmd=1)!
                    out.write(JCoreWireCodec.encodeResponseFrame(
                        JCoreWireCodec.CMD_LOGIN,
                        JCoreWireCodec.VER_LOGIN,
                        firstFrame.requestId,
                        0,
                        660001L,
                        108,
                        new byte[0]
                    ));
                    out.flush();

                    // Now expect Register (cmd=0, ver=29)
                    JCoreWireCodec.DecodedFrame reRegFrame = JCoreWireCodec.readClientFrameFromStream(in, 0L);
                    check(reRegFrame != null && reRegFrame.command == JCoreWireCodec.CMD_REGISTER, "After Login 108, client must immediately send Register (cmd=0)");
                    long newUid = 660002L;
                    byte[] reRegResp = JCoreWireCodec.buildRegisterResponsePayload(newUid, "new_pwd_108", "reg_after_108", "dev_e2e_660002");
                    out.write(JCoreWireCodec.encodeResponseFrame(
                        JCoreWireCodec.CMD_REGISTER,
                        JCoreWireCodec.VER_REGISTER,
                        reRegFrame.requestId,
                        reRegFrame.sessionId,
                        newUid,
                        0,
                        reRegResp
                    ));
                    out.flush();

                    // Now expect Login (cmd=1, ver=27) with newUid
                    JCoreWireCodec.DecodedFrame reLoginFrame = JCoreWireCodec.readClientFrameFromStream(in, newUid);
                    check(reLoginFrame != null && reLoginFrame.command == JCoreWireCodec.CMD_LOGIN, "After re-Register, client must send Login (cmd=1)");
                    byte[] reLoginResp = JCoreWireCodec.buildLoginResponsePayload(8802, 520, "", 1700000200);
                    out.write(JCoreWireCodec.encodeResponseFrame(
                        JCoreWireCodec.CMD_LOGIN,
                        JCoreWireCodec.VER_LOGIN,
                        reLoginFrame.requestId,
                        8802,
                        newUid,
                        0,
                        reLoginResp
                    ));
                    out.flush();

                    // Expect Heartbeat (cmd=2, ver=8)
                    JCoreWireCodec.DecodedFrame hbFrame = JCoreWireCodec.readClientFrameFromStream(in, newUid);
                    if (hbFrame != null && hbFrame.command == JCoreWireCodec.CMD_HEARTBEAT) {
                        out.write(JCoreWireCodec.encodeResponseFrame(
                            JCoreWireCodec.CMD_HEARTBEAT,
                            JCoreWireCodec.VER_HEARTBEAT,
                            hbFrame.requestId,
                            8802,
                            newUid,
                            0,
                            new byte[0]
                        ));
                        out.flush();
                    }
                    reRegisteredAfter108.set(true);
                    secondSessionLatch.countDown();
                } catch (Throwable t) {
                    serverError.set(t);
                    secondSessionLatch.countDown();
                } finally {
                    try {
                        if (client != null) client.close();
                    } catch (Throwable ignored) {}
                    try {
                        serverSocket.close();
                    } catch (Throwable ignored) {}
                }
            }
        }, "test-jcore-loopback-server-2");
        serverThread2.setDaemon(true);
        serverThread2.start();

        JCoreTcpEngine.getInstance().setCustomEndpoint("127.0.0.1", tcpPort);
        JCoreTcpEngine.getInstance().triggerReconnectNow(reactContext);
        check(JCoreTcpEngine.getInstance().awaitHandshake(5000L), "Second handshake (Fast-Login + 108 re-register) must complete");
        check(secondSessionLatch.await(5000L, TimeUnit.MILLISECONDS), "Second session server must complete");
        if (serverError.get() != null) {
            throw new AssertionError("Loopback server #2 failed: " + serverError.get().getMessage(), serverError.get());
        }
        check(secondSessionFirstCmd.get() == JCoreWireCodec.CMD_LOGIN, "Fast-login must skip Register cmd=0");
        check(reRegisteredAfter108.get() && "reg_after_108".equals(JPushInterface.getRegistrationID(reactContext)), "Login 108 must re-register and update registrationId");

        // Verify exponential backoff formula: 3s, 6s, 12s, 24s, 48s, 96s, capped at 120s
        check(JCoreTcpEngine.computeReconnectDelayMs(0) == 3000L, "Backoff attempt 0 = 3s");
        check(JCoreTcpEngine.computeReconnectDelayMs(1) == 6000L, "Backoff attempt 1 = 6s");
        check(JCoreTcpEngine.computeReconnectDelayMs(2) == 12000L, "Backoff attempt 2 = 12s");
        check(JCoreTcpEngine.computeReconnectDelayMs(6) == 120000L, "Backoff attempt 6 capped at 120s");

        // 4. Exercise React Native bridge methods on JCoreModule and JPushModule
        jcoreModule.setAuth(false);
        check(!JCoreStorage.isCollectionAuth(reactContext), "Legacy bridge revokes collection authorization");
        jcoreModule.setAuth(true);
        jcoreModule.enableAutoWakeup(false);
        WritableMap logCfg = Arguments.createMap();
        logCfg.putBoolean("enable", true);
        logCfg.putBoolean("uploadJgToServer", false);
        jcoreModule.enableSDKLocalLog(logCfg);
        final AtomicReference<String> readLogsRef = new AtomicReference<String>("");
        jcoreModule.readNewLogs(new Callback() {
            @Override
            public void invoke(Object... args) {
                if (args != null && args.length > 0 && args[0] instanceof ReadableMap) {
                    readLogsRef.set(((ReadableMap) args[0]).getString("logs"));
                }
            }
        });
        check(readLogsRef.get() != null && readLogsRef.get().contains("SDKLocalLog"), "JCoreModule.readNewLogs");

        jpushModule.setLinkMergeEnable(false);
        WritableMap tagMap = Arguments.createMap();
        tagMap.putInt(JConstants.SEQUENCE, 41);
        WritableArray tagArr = Arguments.createArray();
        tagArr.pushString("hardware_wallet");
        tagMap.putArray(JConstants.TAGS, tagArr);
        jpushModule.setTags(tagMap);

        WritableMap aliasMap = Arguments.createMap();
        aliasMap.putInt(JConstants.SEQUENCE, 42);
        aliasMap.putString(JConstants.ALIAS, "onekey_pro");
        jpushModule.setAlias(aliasMap);

        WritableMap localNotiMap = Arguments.createMap();
        localNotiMap.putString(JConstants.MESSAGE_ID, "901");
        localNotiMap.putString(JConstants.TITLE, "Local Title");
        localNotiMap.putString(JConstants.CONTENT, "Local Content");
        jpushModule.addLocalNotification(localNotiMap);

        boolean sawConnect = false;
        boolean sawRemoteNotification = false;
        boolean sawCustomMessage = false;
        boolean sawTagAlias = false;
        boolean sawLocalNotification = false;

        for (ReactApplicationContext.EmittedEvent ev : reactContext.emittedEvents) {
            if (JConstants.CONNECT_EVENT.equals(ev.name)) {
                sawConnect = true;
            } else if (JConstants.NOTIFICATION_EVENT.equals(ev.name) && ev.data instanceof ReadableMap) {
                ReadableMap m = (ReadableMap) ev.data;
                if ("50001".equals(m.getString(JConstants.MESSAGE_ID))
                    && "OneKey Security".equals(m.getString(JConstants.TITLE))) {
                    sawRemoteNotification = true;
                }
            } else if (JConstants.CUSTOM_MESSAGE_EVENT.equals(ev.name) && ev.data instanceof ReadableMap) {
                ReadableMap m = (ReadableMap) ev.data;
                if ("50002".equals(m.getString(JConstants.MESSAGE_ID))
                    && "tx_confirmed".equals(m.getString(JConstants.CONTENT))) {
                    sawCustomMessage = true;
                }
            } else if (JConstants.TAG_ALIAS_EVENT.equals(ev.name)) {
                sawTagAlias = true;
            } else if (JConstants.LOCAL_NOTIFICATION_EVENT.equals(ev.name)) {
                sawLocalNotification = true;
            }
        }

        check(sawConnect, "JPushModuleReceiver must emit ConnectEvent");
        check(sawRemoteNotification, "JPushModuleReceiver must emit NotificationEvent for downlink cmd=3 notification");
        check(sawCustomMessage, "JPushModuleReceiver must emit CustomMessageEvent for downlink cmd=3 custom message");
        check(sawTagAlias, "JPushModuleReceiver must emit TagAliasEvent");
        check(sawLocalNotification, "JPushModuleReceiver must emit LocalNotificationEvent for local notification");

        JCoreTcpEngine.getInstance().closeSocket();
    }

    private static void testFcmCallbacksAndPayloadBounds() throws Exception {
        Context context = new Context();
        JCoreStorage.setPushStopped(context, false);
        JCoreStorage.setCollectionAuth(context, true);
        String payload = "{\"_jmsgid_\":\"fcm_arrival_1\",\"n_title\":\"Synthetic title\",\"n_content\":\"Synthetic body\",\"n_extras\":{\"route\":\"test\"}}";
        String zipped = "{\"jg_c\":\"" + JCoreCrypto.base64Encode(JCoreCrypto.gzipCompress(payload.getBytes(StandardCharsets.UTF_8))) + "\"}";
        NotificationMessage parsed = cn.jpush.android.api.JThirdPlatFormInterface.parseMessage(context, zipped);
        check("fcm_arrival_1".equals(parsed.msgId) && parsed.platform == 8, "Compressed FCM envelope must retain identity and platform");
        check(parsed.notificationExtras.contains("route"), "FCM extras must survive decoding");
        StringBuilder oversized = new StringBuilder();
        for (int i = 0; i < 70000; i++) oversized.append('a');
        String oversizedZip = "{\"jg_c\":\"" + JCoreCrypto.base64Encode(JCoreCrypto.gzipCompress(oversized.toString().getBytes(StandardCharsets.UTF_8))) + "\"}";
        check(cn.jpush.android.api.JThirdPlatFormInterface.decodeMessageJson(oversizedZip) == null, "FCM inflated payload must be bounded");

        ActionHelper helper = ActionHelper.getInstance();
        helper.clearRegisteredReceivers();
        final AtomicInteger arrivals = new AtomicInteger();
        final AtomicInteger opens = new AtomicInteger();
        final AtomicInteger customs = new AtomicInteger();
        final AtomicReference<NotificationMessage> delivered = new AtomicReference<NotificationMessage>();
        helper.registerReceiver(new JPushMessageReceiver() {
            @Override public void onNotifyMessageArrived(Context c, NotificationMessage message) {
                arrivals.incrementAndGet(); delivered.set(message);
            }
            @Override public void onNotifyMessageOpened(Context c, NotificationMessage message) {
                opens.incrementAndGet();
            }
            @Override public void onMessage(Context c, CustomMessage message) {
                check("fcm_custom_1".equals(message.messageId) && message.platform == 8, "FCM custom message must retain identity and platform");
                check("Synthetic data".equals(message.message), "FCM custom body must reach the callback");
                customs.incrementAndGet();
            }
        });
        Bundle notification = new Bundle();
        notification.putString("data", zipped);
        notification.putInt("noti_id", 321);
        cn.jpush.android.api.JThirdPlatFormInterface.doAction(context, cn.jpush.android.api.JThirdPlatFormInterface.ACTION_NOTIFICATION_ARRIVED, notification);
        check(arrivals.get() == 1 && delivered.get().notificationId == 321, "Vendor-displayed FCM arrival must dispatch exactly once");
        android.content.Intent tap = new android.content.Intent(JPushInterface.ACTION_NOTIFICATION_OPENED);
        tap.putExtra("JMessageExtra", zipped);
        helper.handleNotificationIntent(context, tap);
        check(opens.get() == 1, "FCM PendingIntent extras must reach the opened callback");
        helper.handleNotificationIntent(context, new android.content.Intent("unrelated").putExtra("JMessageExtra", zipped));
        check(opens.get() == 1, "Unrelated intents must not produce opened callbacks");
        Bundle received = new Bundle();
        received.putString("JMessageExtra", zipped);
        cn.jpush.android.api.JThirdPlatFormInterface.sendActionByJCore(context, received, cn.jpush.android.api.JThirdPlatFormInterface.ACTION_PLUGIN_PALTFORM_ON_MESSAGING);
        check(arrivals.get() == 2 && "fcm_arrival_1".equals(delivered.get().msgId), "Source FCM receive entry must dispatch one arrival");
        received.putString("JMessageExtra", "{\"msg_id\":\"fcm_custom_1\",\"message\":\"Synthetic data\"}");
        cn.jpush.android.api.JThirdPlatFormInterface.sendActionByJCore(context, received, cn.jpush.android.api.JThirdPlatFormInterface.ACTION_PLUGIN_PALTFORM_ON_MESSAGING);
        check(customs.get() == 1 && arrivals.get() == 2, "FCM custom data must not become a notification");
        JCoreStorage.setPushStopped(context, true);
        cn.jpush.android.api.JThirdPlatFormInterface.doAction(context, cn.jpush.android.api.JThirdPlatFormInterface.ACTION_NOTIFICATION_ARRIVED, notification);
        check(arrivals.get() == 2, "Stopped push must suppress FCM callbacks");
        check("0".equals(cn.jpush.android.api.JThirdPlatFormInterface.parseMessage(context, payload).displayForeground), "Stopped push must suppress vendor-controlled display");
        JCoreStorage.setPushStopped(context, false);
        JCoreStorage.setCollectionAuth(context, false);
        cn.jpush.android.api.JThirdPlatFormInterface.doAction(context, cn.jpush.android.api.JThirdPlatFormInterface.ACTION_NOTIFICATION_ARRIVED, notification);
        check(arrivals.get() == 2, "Unauthorized collection must suppress FCM callbacks");
        JCoreStorage.setCollectionAuth(context, true);
        helper.clearRegisteredReceivers();


    }

    private static void testHttpReportChannelAndMultiProcessIpc() throws Exception {
        final ReactApplicationContext context = new ReactApplicationContext();
        JCoreStorage.setAppContext(context);
        JCoreStorage.setAppKey(context, "onekey_report_appkey");
        JCoreStorage.updateRegistration(context, 770001L, "report_pwd_123", "reg_report_770001");

        // 1. Start a local loopback HTTP server for /v3/report
        final ServerSocket httpServer = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"));
        final int httpPort = httpServer.getLocalPort();
        final AtomicReference<String> receivedAppKey = new AtomicReference<String>("");
        final AtomicReference<String> decryptedReportJson = new AtomicReference<String>("");
        final AtomicBoolean authSignVerified = new AtomicBoolean(false);

        Thread httpThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket client = null;
                try {
                    httpServer.setSoTimeout(5000);
                    client = httpServer.accept();
                    client.setSoTimeout(5000);
                    InputStream in = client.getInputStream();
                    OutputStream out = client.getOutputStream();

                    // Read HTTP headers byte-by-byte until \r\n\r\n
                    ByteArrayOutputStream headerBuf = new ByteArrayOutputStream();
                    int b;
                    int matchState = 0;
                    while ((b = in.read()) != -1) {
                        headerBuf.write(b);
                        if ((matchState == 0 || matchState == 2) && b == '\r') {
                            matchState++;
                        } else if ((matchState == 1 || matchState == 3) && b == '\n') {
                            matchState++;
                            if (matchState == 4) {
                                break;
                            }
                        } else {
                            matchState = (b == '\r') ? 1 : 0;
                        }
                    }
                    String headersStr = new String(headerBuf.toByteArray(), StandardCharsets.UTF_8);
                    int contentLen = 0;
                    String authHeader = "";
                    for (String line : headersStr.split("\r\n")) {
                        String lower = line.toLowerCase();
                        if (lower.startsWith("content-length:")) {
                            contentLen = Integer.parseInt(line.substring(15).trim());
                        } else if (lower.startsWith("x-app-key:")) {
                            receivedAppKey.set(line.substring(10).trim());
                        } else if (lower.startsWith("authorization:")) {
                            authHeader = line.substring(14).trim();
                        }
                    }

                    byte[] bodyBytes = new byte[contentLen];
                    int readOffset = 0;
                    while (readOffset < contentLen) {
                        int r = in.read(bodyBytes, readOffset, contentLen - readOffset);
                        if (r == -1) break;
                        readOffset += r;
                    }

                    if (authHeader.startsWith("Basic ")) {
                        String decodedAuth = new String(
                            Base64.getDecoder().decode(authHeader.substring(6).trim()),
                            StandardCharsets.UTF_8
                        );
                        String[] parts = decodedAuth.split(":");
                        if (parts.length == 3) {
                            String uidStr = parts[0];
                            String signSha1 = parts[1];
                            String aesKey = parts[2];
                            String expectedSign = JCoreCrypto.sha1Hex(
                                uidStr + JCoreCrypto.md5Hex("report_pwd_123") + JCoreCrypto.sha1Hex(bodyBytes)
                            );
                            if (expectedSign.equals(signSha1)) {
                                authSignVerified.set(true);
                            }
                            byte[] decrypted = JCoreCrypto.aes256CbcHttpDecrypt(bodyBytes, aesKey);
                            byte[] plain = JCoreCrypto.gzipDecompress(decrypted);
                            decryptedReportJson.set(new String(plain, StandardCharsets.UTF_8));
                        }
                    }

                    byte[] httpResp = (
                        "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "Content-Length: 10\r\n\r\n"
                        + "{\"code\":0}"
                    ).getBytes(StandardCharsets.UTF_8);
                    out.write(httpResp);
                    out.flush();
                } catch (Throwable ignored) {
                } finally {
                    try {
                        if (client != null) client.close();
                    } catch (Throwable ignored) {}
                    try {
                        httpServer.close();
                    } catch (Throwable ignored) {}
                }
            }
        }, "test-http-report-server");
        httpThread.setDaemon(true);
        httpThread.start();

        JCoreTcpEngine.getInstance().setCustomReportUrl("http://127.0.0.1:" + httpPort + "/v3/report");
        boolean reportOk = JCoreTcpEngine.getInstance().sendHttpReport(
            context,
            "{\"msg_id\":\"50001\",\"result\":1028,\"itime\":1700000300}"
        );
        check(reportOk, "sendHttpReport must receive HTTP 200 OK");
        check("onekey_report_appkey".equals(receivedAppKey.get()), "HTTP report must send X-App-Key header");
        check(authSignVerified.get(), "HTTP report Authorization Basic signature sha1(uid + md5(pwd) + sha1(body)) must verify");
        check(
            decryptedReportJson.get().contains("\"platform\":\"a\"")
                && decryptedReportJson.get().contains("\"msg_id\":\"50001\"")
                && decryptedReportJson.get().contains("\"core\":\"5.1.1\""),
            "HTTP report decrypted & gunzipped payload must match production envelope"
        );

        // 2. Verify :pushcore Multi-Process IPC (DataShare, DownloadProvider, DataProvider, MultiSpHelper)
        DataShare share = new DataShare();
        DataShare.init(share, ":pushcore");
        check(DataShare.getInstance(":pushcore") == share, "DataShare.getInstance(:pushcore) must return bound instance");
        check("push_aidl".equals(share.bind(share, "push_aidl")), "DataShare.bind must register aidl by type");
        IBinder binder = share.getBinderByType("push_aidl", "exec");
        check(binder == share, "DataShare.getBinderByType must return DataShare binder");

        Bundle setBundle = new Bundle();
        setBundle.putString("key", "ipc_shared_key");
        setBundle.putString("value", "ipc_shared_val_99");
        share.onAction("JPUSH", "sp_set", setBundle);

        Bundle getBundle = new Bundle();
        getBundle.putString("key", "ipc_shared_key");
        Bundle getResult = share.execute("JPUSH", "sp_get", getBundle);
        check(
            getResult != null && "ipc_shared_val_99".equals(getResult.getString("value")),
            "DataShare.onAction/execute must synchronize key-value state via JCoreInternalHelper"
        );

        Bundle regResult = share.execute("INTERNAL_API", "get_reg_id", new Bundle());
        check(
            regResult != null && "reg_report_770001".equals(regResult.getString("reg_id")),
            "DataShare.execute(get_reg_id) must return registration ID"
        );

        DownloadProvider downloadProvider = new DownloadProvider();
        downloadProvider.setContext(context);
        check(downloadProvider.onCreate(), "DownloadProvider.onCreate must succeed");

        ContentValues cv = new ContentValues();
        cv.put("key", "provider_key_1");
        cv.put("value", "provider_val_1");
        downloadProvider.insert(null, cv);

        Cursor cursor = downloadProvider.query(null, null, "provider_key_1", null, null);
        check(
            cursor != null && cursor.moveToFirst() && "provider_val_1".equals(cursor.getString(1)),
            "DownloadProvider.insert & query must synchronize JCoreStorage SP values"
        );

        DataProvider dataProvider = new DataProvider();
        dataProvider.setContext(context);
        dataProvider.onCreate();
        Bundle callOut = dataProvider.call("INTERNAL_API", "get_reg_id", new Bundle());
        check(
            callOut != null && "reg_report_770001".equals(callOut.getString("reg_id")),
            "DataProvider.call must route to JCoreInternalHelper.directHandle"
        );

        MultiSpHelper.commitString(context, "multi_sp_str", "hello_multi_sp");
        check("hello_multi_sp".equals(MultiSpHelper.getString(context, "multi_sp_str", "")), "MultiSpHelper string roundtrip");

        // 3. Verify Per-Request Timeout (6002) and ThirdToken 3x Retry on a real loopback server
        final ServerSocket retryServer = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"));
        final int retryPort = retryServer.getLocalPort();
        final AtomicInteger thirdTokenAttempts = new AtomicInteger(0);
        final AtomicInteger pushCtrlReceived = new AtomicInteger(0);
        final CountDownLatch pushCtrlLatch = new CountDownLatch(1);
        Thread retryServerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket client = null;
                try {
                    retryServer.setSoTimeout(5000);
                    client = retryServer.accept();
                    client.setSoTimeout(5000);
                    InputStream in = client.getInputStream();
                    OutputStream out = client.getOutputStream();

                    // Fast-Login (device is already registered)
                    JCoreWireCodec.DecodedFrame loginFrame = JCoreWireCodec.readClientFrameFromStream(in, 770001L);
                    if (loginFrame != null && loginFrame.command == JCoreWireCodec.CMD_LOGIN) {
                        byte[] loginResp = JCoreWireCodec.buildLoginResponsePayload(9901, 520, "", 1700000400);
                        out.write(JCoreWireCodec.encodeResponseFrame(
                            JCoreWireCodec.CMD_LOGIN,
                            JCoreWireCodec.VER_LOGIN,
                            loginFrame.requestId,
                            9901,
                            770001L,
                            0,
                            loginResp
                        ));
                        out.flush();
                    }

                    while (!client.isClosed()) {
                        JCoreWireCodec.DecodedFrame frame = JCoreWireCodec.readClientFrameFromStream(in, 770001L);
                        if (frame == null) break;
                        if (frame.command == JCoreWireCodec.CMD_HEARTBEAT) {
                            out.write(JCoreWireCodec.encodeResponseFrame(
                                JCoreWireCodec.CMD_HEARTBEAT,
                                JCoreWireCodec.VER_HEARTBEAT,
                                frame.requestId,
                                9901,
                                770001L,
                                0,
                                new byte[0]
                            ));
                            out.flush();
                        } else if (frame.command == JCoreWireCodec.CMD_THIRD_TOKEN) {
                            int attempt = thirdTokenAttempts.incrementAndGet();
                            // Ignore attempt 1 and 2 to force timeout & retry; ack on attempt 3!
                            if (attempt >= 3) {
                                out.write(JCoreWireCodec.encodeResponseFrame(
                                    JCoreWireCodec.CMD_THIRD_TOKEN,
                                    JCoreWireCodec.VER_THIRD_TOKEN,
                                    frame.requestId,
                                    9901,
                                    770001L,
                                    0,
                                    new byte[0]
                                ));
                                out.flush();
                            }
                        } else if (frame.command == JCoreWireCodec.CMD_MOBILE_NUMBER) {
                            // Intentionally do not respond so client triggers 6002 timeout
                        } else if (frame.command == JCoreWireCodec.CMD_PUSH_CTRL) {
                            pushCtrlReceived.incrementAndGet();
                            pushCtrlLatch.countDown();
                        }
                    }
                } catch (Throwable ignored) {
                } finally {
                    try {
                        if (client != null) client.close();
                    } catch (Throwable ignored) {}
                    try {
                        retryServer.close();
                    } catch (Throwable ignored) {}
                }
            }
        }, "test-retry-server");
        retryServerThread.setDaemon(true);
        retryServerThread.start();

        JCoreStorage.setServerRegistered(context, true);
        JCoreTcpEngine.getInstance().setDefaultRequestTimeoutMs(120L);
        JCoreTcpEngine.getInstance().setCustomEndpoint("127.0.0.1", retryPort);
        JCoreTcpEngine.getInstance().triggerReconnectNow(context);
        check(JCoreTcpEngine.getInstance().awaitHandshake(3000L), "Retry server fast-login handshake");

        final CountDownLatch retryCmdLatch = new CountDownLatch(1);
        final AtomicInteger retryFinalCode = new AtomicInteger(-1);
        JCoreTcpEngine.getInstance().enqueueOrSendRequest(
            context,
            JCoreWireCodec.CMD_THIRD_TOKEN,
            JCoreWireCodec.VER_THIRD_TOKEN,
            JCoreStorage.nextRid(),
            JCoreWireCodec.buildThirdTokenPayload("retry_token_xyz", (byte) 2, false),
            120L,
            3,
            new JCoreTcpEngine.RequestCallback() {
                @Override
                public void onResult(JCoreWireCodec.DecodedFrame responseFrame, int statusCode) {
                    retryFinalCode.set(statusCode);
                    retryCmdLatch.countDown();
                }
            }
        );
        check(retryCmdLatch.await(3000L, TimeUnit.MILLISECONDS), "ThirdToken retry request must complete");
        check(thirdTokenAttempts.get() == 3 && retryFinalCode.get() == 0, "ThirdToken must retry and succeed on 3rd attempt");

        // Verify timeout 6002 when server does not respond and maxRetries = 0
        final CountDownLatch timeoutLatch = new CountDownLatch(1);
        final AtomicInteger timeoutCode = new AtomicInteger(0);
        JCoreTcpEngine.getInstance().enqueueOrSendRequest(
            context,
            JCoreWireCodec.CMD_MOBILE_NUMBER,
            JCoreWireCodec.VER_MOBILE_NUMBER,
            JCoreStorage.nextRid(),
            JCoreWireCodec.buildMobileNumberPayload(55, "13900001111"),
            80L,
            0,
            new JCoreTcpEngine.RequestCallback() {
                @Override
                public void onResult(JCoreWireCodec.DecodedFrame responseFrame, int statusCode) {
                    timeoutCode.set(statusCode);
                    timeoutLatch.countDown();
                }
            }
        );
        check(timeoutLatch.await(2000L, TimeUnit.MILLISECONDS), "Unanswered request must time out");
        check(timeoutCode.get() == JPushInterface.ErrorCode.TIMEOUT, "Timed-out request must return ErrorCode.TIMEOUT (6002)");
        JPushInterface.stopPush(context);
        check(
            pushCtrlLatch.await(2000L, TimeUnit.MILLISECONDS) && pushCtrlReceived.get() == 1,
            "JPushInterface.stopPush must transmit CMD_PUSH_CTRL before closing the socket"
        );
        JCoreStorage.setPushStopped(context, false);
        JCoreTcpEngine.getInstance().setDefaultRequestTimeoutMs(5000L);
        JCoreTcpEngine.getInstance().closeSocket();
    }

    private static SSLSession createDummySession(final boolean verifiedPeers) {
        return new SSLSession() {
            @Override public byte[] getId() { return new byte[0]; }
            @Override public SSLSessionContext getSessionContext() { return null; }
            @Override public long getCreationTime() { return 0; }
            @Override public long getLastAccessedTime() { return 0; }
            @Override public void invalidate() {}
            @Override public boolean isValid() { return verifiedPeers; }
            @Override public void putValue(String s, Object o) {}
            @Override public Object getValue(String s) { return null; }
            @Override public void removeValue(String s) {}
            @Override public String[] getValueNames() { return new String[0]; }
            @Override
            public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
                if (!verifiedPeers) {
                    throw new SSLPeerUnverifiedException("Peer not verified");
                }
                return new Certificate[0];
            }
            @Override public Certificate[] getLocalCertificates() { return null; }
            @Override public X509Certificate[] getPeerCertificateChain() { return null; }
            @Override public Principal getPeerPrincipal() { return null; }
            @Override public Principal getLocalPrincipal() { return null; }
            @Override public String getCipherSuite() { return "TLS_AES_128_GCM_SHA256"; }
            @Override public String getProtocol() { return "TLSv1.3"; }
            @Override public String getPeerHost() { return "tsis.jpush.cn"; }
            @Override public int getPeerPort() { return 443; }
            @Override public int getPacketBufferSize() { return 0; }
            @Override public int getApplicationBufferSize() { return 0; }
        };
    }

    private static void testDefaultHostVerifier() {
        DefaultHostVerifier verifier = new DefaultHostVerifier("tsis.jpush.cn");
        check(!verifier.verify("tsis.jpush.cn", null), "DefaultHostVerifier must reject null SSLSession");
        check(!verifier.verify("", createDummySession(true)), "DefaultHostVerifier must reject empty hostname");
        check(!verifier.verify(null, createDummySession(true)), "DefaultHostVerifier must reject null hostname");
        check(!verifier.verify("attacker.example.com", createDummySession(true)), "DefaultHostVerifier must reject mismatched hostname");
        check(!verifier.verify("tsis.jpush.cn", createDummySession(false)), "DefaultHostVerifier must reject unverified SSLSession");

        HostnameVerifier original = HttpsURLConnection.getDefaultHostnameVerifier();
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return false;
                }
            });
            check(!verifier.verify("tsis.jpush.cn", createDummySession(true)), "DefaultHostVerifier must reject when default verifier rejects");
        } finally {
            HttpsURLConnection.setDefaultHostnameVerifier(original);
        }
    }

    private static void testAutoReconnectScheduleAndBinderSecurity() throws Exception {
        final ReactApplicationContext context = new ReactApplicationContext();
        JCoreStorage.setAppContext(context);
        JCoreStorage.setPushStopped(context, false);
        check(JCoreTcpEngine.getInstance().isAutoReconnectEnabled(), "autoReconnectEnabled must default to true");

        // 1. Verify automatic reconnect when remote server drops an established TCP connection
        final ServerSocket dropServer = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"));
        final int dropPort = dropServer.getLocalPort();
        final AtomicInteger loginSessions = new AtomicInteger(0);
        final CountDownLatch secondSessionLatch = new CountDownLatch(1);

        Thread dropServerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dropServer.setSoTimeout(5000);
                    for (int sessionIdx = 0; sessionIdx < 2; sessionIdx++) {
                        Socket client = dropServer.accept();
                        client.setSoTimeout(5000);
                        InputStream in = client.getInputStream();
                        OutputStream out = client.getOutputStream();
                        JCoreWireCodec.DecodedFrame loginFrame = JCoreWireCodec.readClientFrameFromStream(in, 770001L);
                        if (loginFrame != null && loginFrame.command == JCoreWireCodec.CMD_LOGIN) {
                            int sid = 9950 + sessionIdx;
                            byte[] loginResp = JCoreWireCodec.buildLoginResponsePayload(sid, 520, "", 1700000500);
                            out.write(JCoreWireCodec.encodeResponseFrame(
                                JCoreWireCodec.CMD_LOGIN,
                                JCoreWireCodec.VER_LOGIN,
                                loginFrame.requestId,
                                sid,
                                770001L,
                                0,
                                loginResp
                            ));
                            out.flush();
                        }
                        JCoreWireCodec.DecodedFrame hbFrame = JCoreWireCodec.readClientFrameFromStream(in, 770001L);
                        if (hbFrame != null && hbFrame.command == JCoreWireCodec.CMD_HEARTBEAT) {
                            int sid = 9950 + sessionIdx;
                            out.write(JCoreWireCodec.encodeResponseFrame(
                                JCoreWireCodec.CMD_HEARTBEAT,
                                JCoreWireCodec.VER_HEARTBEAT,
                                hbFrame.requestId,
                                sid,
                                770001L,
                                0,
                                new byte[0]
                            ));
                            out.flush();
                        }
                        int count = loginSessions.incrementAndGet();
                        if (count == 1) {
                            // Drop first session immediately from server side to trigger client auto-reconnect!
                            client.close();
                        } else {
                            secondSessionLatch.countDown();
                            Thread.sleep(50L);
                            client.close();
                        }
                    }
                } catch (Throwable ignored) {
                } finally {
                    try {
                        dropServer.close();
                    } catch (Throwable ignored) {}
                }
            }
        }, "test-drop-reconnect-server");
        dropServerThread.setDaemon(true);
        dropServerThread.start();

        JCoreStorage.setServerRegistered(context, true);
        JCoreTcpEngine.getInstance().setReconnectBaseDelayMs(25L);
        JCoreTcpEngine.getInstance().setCustomEndpoint("127.0.0.1", dropPort);
        JCoreTcpEngine.getInstance().triggerReconnectNow(context);
        check(secondSessionLatch.await(3000L, TimeUnit.MILLISECONDS), "Dropped TCP session must automatically reconnect");
        check(loginSessions.get() >= 2, "Engine must complete second handshake after dropped connection");
        JCoreTcpEngine.getInstance().setReconnectBaseDelayMs(3000L);
        JCoreTcpEngine.getInstance().closeSocket();

        // 2. Verify notification schedule (pushTime & silenceTime) and latestNotificationNumber eviction
        java.util.Set<Integer> allowedDays = new java.util.HashSet<Integer>(Arrays.asList(1, 3, 5));
        JPushInterface.setPushTime(context, allowedDays, 9, 18);
        check(JCoreStorage.isPushTimeAllowed(context, 1, 10), "Monday 10:00 must be allowed");
        check(!JCoreStorage.isPushTimeAllowed(context, 1, 20), "Monday 20:00 must be outside push time");
        check(!JCoreStorage.isPushTimeAllowed(context, 2, 10), "Tuesday 10:00 must be disallowed by day filter");
        JPushInterface.setPushTime(context, null, 0, 23);
        check(JCoreStorage.isPushTimeAllowed(context, 2, 22), "Reset pushTime must allow all times");

        JPushInterface.setSilenceTime(context, 22, 30, 7, 0);
        check(JCoreStorage.isInSilenceTime(context, 23, 15), "23:15 must be inside overnight silence window");
        check(JCoreStorage.isInSilenceTime(context, 6, 45), "06:45 must be inside overnight silence window");
        check(!JCoreStorage.isInSilenceTime(context, 12, 0), "12:00 must be outside overnight silence window");
        JPushInterface.setSilenceTime(context, 0, 0, 0, 0);
        check(!JCoreStorage.isInSilenceTime(context, 23, 15), "00:00-00:00 must disable silence time");

        ActionHelper.getInstance().clearActiveNotificationIds();
        JPushInterface.setLatestNotificationNumber(context, 2);
        // This host fixture verifies tracking after a successful post; actual OS display is covered by Robolectric/device tests.
        for (int id = 101; id <= 103; id++) ActionHelper.getInstance().recordNotificationPosted(context, id);
        List<Integer> activeIds = ActionHelper.getInstance().getActiveNotificationIds();
        check(
            activeIds.size() == 2 && !activeIds.contains(101) && activeIds.contains(102) && activeIds.contains(103),
            "setLatestNotificationNumber(2) must evict oldest notification 101"
        );

        // 3. Verify DataShare rejects cross-UID Binder callers
        DataShare share = new DataShare();
        DataShare.setTestCallerUids(20001, 10000);
        boolean rejectedCrossUid = false;
        try {
            share.execute("INTERNAL_API", "get_reg_id", new Bundle());
        } catch (SecurityException expected) {
            rejectedCrossUid = true;
        } finally {
            DataShare.setTestCallerUids(-1, -1);
        }
        check(rejectedCrossUid, "DataShare must throw SecurityException when callingUid != myUid");

        // 4. Verify default logging is enabled and never logs sensitive notification content or PII
        check(JLogger.isLoggerEnable(), "JLogger must be enabled by default");
        JLogger.clearRecentLogs();
        JPushModuleReceiver receiver = new JPushModuleReceiver();

        NotificationMessage secretNotify = new NotificationMessage();
        secretNotify.msgId = "msg_99001";
        secretNotify.notificationId = 8801;
        secretNotify.notificationType = 0;
        secretNotify.notificationTitle = "SECRET_WALLET_TITLE";
        secretNotify.notificationContent = "SECRET_TRANSFER_AMOUNT_100_BTC";
        secretNotify.notificationExtras = "{\"txHash\":\"0xdeadbeef_secret\"}";
        receiver.onNotifyMessageArrived(context, secretNotify);
        receiver.onNotifyMessageOpened(context, secretNotify);
        receiver.onNotifyMessageDismiss(context, secretNotify);

        CustomMessage secretCustom = new CustomMessage();
        secretCustom.messageId = "custom_99002";
        secretCustom.platform = (byte) 0;
        secretCustom.title = "SECRET_CUSTOM_TITLE";
        secretCustom.message = "SECRET_CUSTOM_BODY";
        secretCustom.extra = "{\"privateNote\":\"do_not_log\"}";
        receiver.onMessage(context, secretCustom);

        JPushMessage secretOp = new JPushMessage();
        secretOp.setSequence(77);
        secretOp.setErrorCode(0);
        secretOp.setAlias("SECRET_USER_ALIAS");
        secretOp.setMobileNumber("13899998888");
        secretOp.setTags(new java.util.HashSet<String>(Arrays.asList("SECRET_VIP_TAG")));
        receiver.onAliasOperatorResult(context, secretOp);
        receiver.onTagOperatorResult(context, secretOp);
        receiver.onMobileNumberOperatorResult(context, secretOp);
        receiver.onRegister(context, "1a0018970a900100200");

        String joinedLogs = String.join("\n", JLogger.getRecentLogs());
        check(joinedLogs.contains("notificationArrived") && joinedLogs.contains("notificationType=0"), "Must log notification type");
        check(joinedLogs.contains("msg_99001") && joinedLogs.contains("1a0018970a900100200"), "Must allow msgId and registrationId");
        check(!joinedLogs.contains("SECRET_WALLET_TITLE"), "Must never log notificationTitle");
        check(!joinedLogs.contains("SECRET_TRANSFER_AMOUNT_100_BTC"), "Must never log notificationContent");
        check(!joinedLogs.contains("0xdeadbeef_secret"), "Must never log notificationExtras");
        check(!joinedLogs.contains("SECRET_CUSTOM_TITLE") && !joinedLogs.contains("SECRET_CUSTOM_BODY") && !joinedLogs.contains("do_not_log"), "Must never log custom message content");
        check(!joinedLogs.contains("SECRET_USER_ALIAS") && !joinedLogs.contains("13899998888") && !joinedLogs.contains("SECRET_VIP_TAG"), "Must never log alias, mobileNumber, or tags");

        // 5. Verify cold-start JPushModuleReceiver.onNotifyMessageOpened queues into PENDING_NITRO_EVENTS when JPushModule.reactContext == null
        ReactApplicationContext savedReactContext = JPushModule.reactContext;
        JPushModule.reactContext = null;
        final List<String> coldStartOpenedEvents = new java.util.ArrayList<String>();
        try {
            int drainId = cn.jiguang.plugins.push.helper.JPushHelper.registerNitroListener(
                new cn.jiguang.plugins.push.helper.JPushHelper.NitroEventListener() {
                    @Override
                    public void onEvent(String eventName, String payloadJson) {
                    }
                }
            );
            cn.jiguang.plugins.push.helper.JPushHelper.unregisterNitroListener(drainId);

            NotificationMessage coldStartOpenMsg = new NotificationMessage();
            coldStartOpenMsg.msgId = "msg_cold_open_001";
            coldStartOpenMsg.notificationId = 8809;
            coldStartOpenMsg.notificationType = 0;
            coldStartOpenMsg.notificationTitle = "Cold Start Title";
            coldStartOpenMsg.notificationContent = "Cold Start Body";
            receiver.onNotifyMessageOpened(context, coldStartOpenMsg);

            NotificationMessage coldStartLocalOpenMsg = new NotificationMessage();
            coldStartLocalOpenMsg.msgId = "msg_cold_local_002";
            coldStartLocalOpenMsg.notificationId = 8810;
            coldStartLocalOpenMsg.notificationType = 1;
            coldStartLocalOpenMsg.notificationTitle = "Local Cold Title";
            coldStartLocalOpenMsg.notificationContent = "Local Cold Body";
            receiver.onNotifyMessageOpened(context, coldStartLocalOpenMsg);

            int coldNitroId = cn.jiguang.plugins.push.helper.JPushHelper.registerNitroListener(
                new cn.jiguang.plugins.push.helper.JPushHelper.NitroEventListener() {
                    @Override
                    public void onEvent(String eventName, String payloadJson) {
                        if ("NotificationEvent".equals(eventName) || "LocalNotificationEvent".equals(eventName)) {
                            coldStartOpenedEvents.add(eventName + ":" + payloadJson);
                        }
                    }
                }
            );
            cn.jiguang.plugins.push.helper.JPushHelper.unregisterNitroListener(coldNitroId);
        } finally {
            JPushModule.reactContext = savedReactContext;
        }
        check(
            coldStartOpenedEvents.size() == 2
                && coldStartOpenedEvents.get(0).startsWith("NotificationEvent:")
                && coldStartOpenedEvents.get(0).contains("notificationOpened")
                && coldStartOpenedEvents.get(1).startsWith("LocalNotificationEvent:")
                && coldStartOpenedEvents.get(1).contains("notificationOpened"),
            "JPushModuleReceiver.onNotifyMessageOpened must queue NotificationEvent and LocalNotificationEvent into PENDING_NITRO_EVENTS when JPushModule.reactContext == null"
        );
    }

    private interface AwaitCondition { boolean ready(); }

    private static void awaitCondition(AwaitCondition condition, String message) throws Exception {
        long end = System.currentTimeMillis() + 5000L;
        while (!condition.ready() && System.currentTimeMillis() < end) Thread.sleep(10L);
        check(condition.ready(), message);
    }

    private static void testStoppedInitAndRealLoginFcmRebind() throws Exception {
        final Context context = new Context();
        final JCoreTcpEngine engine = JCoreTcpEngine.getInstance();
        engine.setAutoReconnectEnabled(false);
        engine.stop(context);
        JCoreStorage.setCollectionAuth(context, true);
        JCoreStorage.setPushStopped(context, false);
        JCoreStorage.clearRegistration(context);
        final AtomicInteger tokenRequests = new AtomicInteger(), acknowledgedBinds = new AtomicInteger();
        JPushMessageReceiver bindReceiver = new JPushMessageReceiver() {
            @Override public void onCommandResult(Context c, CmdMessage message) {
                if (message.cmd == 10000 && message.errorCode == 0 && message.extra != null
                    && "loopback_rebind_token".equals(message.extra.getString("token"))) acknowledgedBinds.incrementAndGet();
            }
        };
        ActionHelper.getInstance().registerReceiver(bindReceiver);
        cn.jpush.android.api.JThirdPlatFormInterface adapter = new cn.jpush.android.api.JThirdPlatFormInterface() {
            public void init(Context c) {} public void register(Context c, Bundle b) {}
            public String getToken(Context c) {
                tokenRequests.incrementAndGet();
                ActionHelper.getInstance().setThirdTokenWithCallBack(c, "loopback_rebind_token", (byte) 8, false);
                return "loopback_rebind_token";
            }
            public boolean isSupport(Context c) { return true; } public byte getRomType(Context c) { return 8; }
            public String getRomName() { return "test"; } public String getAppkey(Context c) { return "test"; }
            public String getAppId(Context c) { return "test"; } public boolean isNeedClearToken(Context c) { return false; }
            public void clearNotification(Context c, int id) {} public void clearAllNotification(Context c) {}
        };
        java.lang.reflect.Field plugin = cn.jpush.android.api.JThirdPlatFormInterface.class.getDeclaredField("fcmPlugin");
        java.lang.reflect.Field registered = cn.jpush.android.api.JThirdPlatFormInterface.class.getDeclaredField("fcmRegistered");
        plugin.setAccessible(true); registered.setAccessible(true);
        plugin.set(null, adapter); registered.setBoolean(null, false);
        final AtomicInteger connections = new AtomicInteger(), registrations = new AtomicInteger(), binds = new AtomicInteger();
        final java.util.Set<Long> boundUids = java.util.concurrent.ConcurrentHashMap.<Long>newKeySet();
        final AtomicBoolean failLogin = new AtomicBoolean(false), done = new AtomicBoolean(false);
        final AtomicReference<Throwable> serverError = new AtomicReference<Throwable>();
        final AtomicReference<Socket> active = new AtomicReference<Socket>();
        try (ServerSocket listener = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"))) {
            Thread server = new Thread(() -> {
                while (!done.get()) {
                    try (Socket socket = listener.accept()) {
                        active.set(socket); connections.incrementAndGet();
                        long uid = 0L;
                        while (!done.get()) {
                            JCoreWireCodec.DecodedFrame frame = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), uid);
                            if (frame == null) break;
                            byte[] body = new byte[0]; int status = 0;
                            if (frame.command == JCoreWireCodec.CMD_REGISTER) {
                                int registration = registrations.incrementAndGet(); uid = 880000L + registration;
                                body = JCoreWireCodec.buildRegisterResponsePayload(uid, "loopback_password", "rebind_rid_" + registration, "loopback_device");
                            } else if (frame.command == JCoreWireCodec.CMD_LOGIN) {
                                status = failLogin.get() ? 1005 : 0;
                                body = JCoreWireCodec.buildLoginResponsePayload(9001, 520, "", 1700000000);
                            }
                            socket.getOutputStream().write(JCoreWireCodec.encodeResponseFrame(frame.command, frame.version,
                                frame.requestId, frame.command == JCoreWireCodec.CMD_REGISTER ? frame.sessionId : 9001, uid, status, body));
                            socket.getOutputStream().flush();
                            if (frame.command == JCoreWireCodec.CMD_THIRD_TOKEN) { binds.incrementAndGet(); boundUids.add(uid); }
                        }
                    } catch (java.net.SocketException closed) {
                        // A client stop closes its active session; the next iteration accepts its resume.
                    } catch (Throwable error) { serverError.set(error); return; }
                }
            }, "test-real-login-fcm-rebind");
            server.setDaemon(true); server.start();
            try {
                engine.setCustomEndpoint("127.0.0.1", listener.getLocalPort());
                engine.setDefaultRequestTimeoutMs(500L);
                JPushInterface.stopPush(context);
                JPushInterface.init(context);
                java.lang.reflect.Field started = JCoreTcpEngine.class.getDeclaredField("started"); started.setAccessible(true);
                check(!started.getBoolean(engine), "Stopped init must not reserve started");
                JPushInterface.resumePush(context);
                awaitCondition(() -> engine.isLoggedIn() && tokenRequests.get() == 2 && binds.get() == 2
                    && acknowledgedBinds.get() == 2, "First resume and login must complete both FCM token requests and bind callbacks");
                int initialRequests = tokenRequests.get();
                String initialRid = JCoreStorage.getRegistrationId(context);
                engine.start(context); engine.start(context);
                check(connections.get() == 1, "Repeated init must not establish another connection");
                engine.stop(context); JCoreStorage.clearRegistration(context); engine.start(context);
                awaitCondition(() -> engine.isLoggedIn() && boundUids.contains(880002L)
                    && tokenRequests.get() == initialRequests + 1 && binds.get() == initialRequests + 1
                    && acknowledgedBinds.get() == initialRequests + 1, "Second real registration must complete its new-UID bind acknowledgement callback");
                check(registrations.get() == 2 && !initialRid.equals(JCoreStorage.getRegistrationId(context)), "Rebind must follow a changed RID from real Register");
                check(tokenRequests.get() == initialRequests + 1, "Successful re-registration requests the current FCM token once");
                awaitCondition(() -> "loopback_rebind_token".equals(JCoreStorage.getThirdToken(context, (byte) 8)), "Successful bind acknowledgement persists current token");

                engine.stop(context); JCoreStorage.clearRegistration(context);
                java.lang.reflect.Field handshakeLatch = JCoreTcpEngine.class.getDeclaredField("handshakeLatch");
                handshakeLatch.setAccessible(true);
                final CountDownLatch obsoleteStart, currentStart;
                // Neither queued worker can pass the engine's initial ownership check until this monitor is released.
                synchronized (engine) {
                    engine.start(context);
                    obsoleteStart = (CountDownLatch) handshakeLatch.get(engine);
                    JPushInterface.stopPush(context);
                    JPushInterface.resumePush(context);
                    currentStart = (CountDownLatch) handshakeLatch.get(engine);
                    check(obsoleteStart != currentStart, "Resume must own a new handshake latch");
                }
                check(obsoleteStart.await(3000L, TimeUnit.MILLISECONDS), "Obsolete queued start must release its own latch");
                check(currentStart.await(3000L, TimeUnit.MILLISECONDS), "Current start must complete its handshake");
                awaitCondition(() -> engine.isLoggedIn() && registrations.get() == 3
                    && tokenRequests.get() == initialRequests + 3 && binds.get() == initialRequests + 3
                    && acknowledgedBinds.get() == initialRequests + 3, "Current resume and login must complete both FCM bind callbacks");
                check(connections.get() == 3 && started.getBoolean(engine) && engine.isLoggedIn(),
                    "An obsolete queued start must leave the current owner started and logged in with only one new connection: connections=" + connections.get()
                        + " started=" + started.getBoolean(engine) + " loggedIn=" + engine.isLoggedIn());
                int beforeFailedLogin = tokenRequests.get();
                engine.stop(context); JCoreStorage.clearRegistration(context); failLogin.set(true); engine.start(context);
                check(engine.awaitHandshake(3000L), "Failed login must complete the handshake wait");
                check(!engine.isLoggedIn() && tokenRequests.get() == beforeFailedLogin
                    && binds.get() == beforeFailedLogin && acknowledgedBinds.get() == beforeFailedLogin,
                    "Failed login does not request or acknowledge another FCM bind");
                check(serverError.get() == null, "Loopback registration server must remain healthy");
            } finally {
                done.set(true); engine.stop(context);
                Socket socket = active.get(); if (socket != null) socket.close();
                plugin.set(null, null); registered.setBoolean(null, false);
                ActionHelper.getInstance().unregisterReceiver(bindReceiver);
            }
        }
    }

    private static void disableAndEnableContent(Context context, boolean stop) {
        if (stop) {
            JCoreStorage.setPushStopped(context, true);
            JCoreStorage.setPushStopped(context, false);
        } else {
            JCoreStorage.setCollectionAuth(context, false);
            JCoreStorage.setCollectionAuth(context, true);
        }
    }

    private static void testContentQueueInvalidation() throws Exception {
        Context context = new Context();
        JCoreStorage.setAppContext(context);
        JCoreStorage.setCollectionAuth(context, true);
        JCoreStorage.setPushStopped(context, false);
        JPushModule.clearActiveReactContexts();
        JPushHelper.clearNitroListeners();
        for (boolean stop : new boolean[]{false, true}) {
            JPushHelper.sendEventMap(JConstants.NOTIFICATION_EVENT, Collections.<String, Object>emptyMap());
            JPushHelper.sendEvent(JConstants.CUSTOM_MESSAGE_EVENT, Arguments.createMap());
            JPushHelper.sendEventMap(JConstants.COMMAND_EVENT, Collections.<String, Object>emptyMap());
            disableAndEnableContent(context, stop);
            final List<String> nitro = new java.util.ArrayList<String>();
            int listener = JPushHelper.registerNitroListener((name, payload) -> nitro.add(name));
            check(nitro.equals(Collections.singletonList(JConstants.COMMAND_EVENT)), "Reenabled Nitro must discard content queued before disable and retain control");
            JPushHelper.unregisterNitroListener(listener);
            ReactApplicationContext legacy = new ReactApplicationContext();
            JPushModule.registerReactContext(legacy);
            check(legacy.emittedEvents.size() == 1 && JConstants.COMMAND_EVENT.equals(legacy.emittedEvents.get(0).name), "Reenabled legacy must discard content queued before disable and retain control");
            JPushModule.clearActiveReactContexts();
            JPushHelper.clearNitroListeners();

            JPushHelper.sendEventMap(JConstants.NOTIFICATION_EVENT, Collections.<String, Object>emptyMap());
            JPushHelper.sendEventMap(JConstants.CUSTOM_MESSAGE_EVENT, Collections.<String, Object>emptyMap());
            JPushHelper.sendEventMap(JConstants.COMMAND_EVENT, Collections.<String, Object>emptyMap());
            nitro.clear();
            listener = JPushHelper.registerNitroListener((name, payload) -> {
                nitro.add(name);
                if (JConstants.NOTIFICATION_EVENT.equals(name)) disableAndEnableContent(context, stop);
            });
            check(nitro.equals(Arrays.asList(JConstants.NOTIFICATION_EVENT, JConstants.COMMAND_EVENT)), "Disable while flushing a Nitro snapshot invalidates its remaining content");
            JPushHelper.unregisterNitroListener(listener);
            JPushHelper.clearNitroListeners();

            JPushHelper.sendEvent(JConstants.NOTIFICATION_EVENT, Arguments.createMap());
            JPushHelper.sendEvent(JConstants.CUSTOM_MESSAGE_EVENT, Arguments.createMap());
            JPushHelper.sendEvent(JConstants.CONNECT_EVENT, Arguments.createMap());
            final List<String> legacyEvents = new java.util.ArrayList<String>();
            ReactApplicationContext flushingContext = new ReactApplicationContext() {
                @Override @SuppressWarnings("unchecked")
                public <T extends com.facebook.react.bridge.JavaScriptModule> T getJSModule(Class<T> type) {
                    return (T) new com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter() {
                        @Override public void emit(String name, Object payload) {
                            legacyEvents.add(name);
                            if (JConstants.NOTIFICATION_EVENT.equals(name)) disableAndEnableContent(context, stop);
                        }
                    };
                }
            };
            JPushModule.registerReactContext(flushingContext);
            check(legacyEvents.equals(Arrays.asList(JConstants.NOTIFICATION_EVENT, JConstants.CONNECT_EVENT)), "Disable while flushing a legacy snapshot invalidates its remaining content");
            JPushModule.clearActiveReactContexts();
            JPushHelper.clearNitroListeners();
        }

        final AtomicReference<android.content.Intent> broadcast = new AtomicReference<android.content.Intent>();
        Context broadcastContext = new Context() {
            @Override public void sendBroadcast(android.content.Intent intent) { broadcast.set(intent); }
        };
        java.lang.reflect.Field appContext = JCoreStorage.class.getDeclaredField("sAppContext");
        appContext.setAccessible(true);
        appContext.set(null, broadcastContext);
        ActionHelper helper = ActionHelper.getInstance();
        helper.clearRegisteredReceivers();
        NotificationMessage message = new NotificationMessage();
        message.msgId = "queued_broadcast";
        helper.dispatchNotificationEvent(broadcastContext, "notify_arrived", message);
        check(broadcast.get() != null, "Authorized content is broadcast");
        disableAndEnableContent(broadcastContext, false);
        final AtomicInteger arrivals = new AtomicInteger();
        helper.onJPushMessageReceive(broadcastContext, new JPushMessageReceiver() {
            @Override public void onNotifyMessageArrived(Context c, NotificationMessage m) { arrivals.incrementAndGet(); }
        }, broadcast.get());
        check(arrivals.get() == 0, "A delayed native broadcast from before revocation cannot replay after regrant");
    }

    private static void testQueueBudgetAndListenerOwnership() throws Exception {
        Context context = new Context();
        JCoreTcpEngine engine = JCoreTcpEngine.getInstance();
        engine.stop(context);
        JCoreStorage.setAppKey(context, "queue-budget-fixture");
        JCoreStorage.setCollectionAuth(context, true);
        JCoreStorage.setPushStopped(context, false);
        AtomicInteger cancelled = new AtomicInteger(), overflow = new AtomicInteger();
        for (int i = 0; i < 129; i++) {
            final int index = i;
            engine.enqueueOrSendRequest(context, JCoreWireCodec.CMD_PROPERTY_OP, JCoreWireCodec.VER_PROPERTY_OP,
                7000L + i, new byte[0], 100L, 0, (frame, status) -> {
                    check(frame == null && status == 6002, "Queue rejection and cancellation retain code 6002");
                    if (index == 128) overflow.incrementAndGet(); else cancelled.incrementAndGet();
                });
        }
        check(overflow.get() == 1 && cancelled.get() == 0 && engine.getPreLoginQueueSize() == 128,
            "129th offline request must reject without replacing the first 128");
        engine.stop(context);
        awaitCondition(() -> cancelled.get() == 128, "Stop must complete all accepted offline requests once");
        check(engine.getPreLoginQueueSize() == 0, "Stop must release the offline queue");

        JPushModule.clearActiveReactContexts();
        JPushHelper.clearNitroListeners();
        final int[] ids = new int[2];
        AtomicInteger callbacks = new AtomicInteger();
        ids[0] = JPushHelper.registerNitroListener((name, payload) -> {
            callbacks.incrementAndGet(); JPushHelper.unregisterNitroListener(ids[1]);
        });
        ids[1] = JPushHelper.registerNitroListener((name, payload) -> {
            callbacks.incrementAndGet(); JPushHelper.unregisterNitroListener(ids[0]);
        });
        JPushHelper.sendEventMap(JConstants.CUSTOM_MESSAGE_EVENT, Collections.<String, Object>emptyMap());
        check(callbacks.get() == 1, "A listener removed by an earlier snapshot callback cannot be invoked");
        JPushHelper.clearNitroListeners();

        ReactApplicationContext main = new ReactApplicationContext(), bg = new ReactApplicationContext();
        JPushModule mainPush = new JPushModule(main), bgPush = new JPushModule(bg);
        JCoreModule mainCore = new JCoreModule(main), bgCore = new JCoreModule(bg);
        mainPush.invalidate(); mainPush.invalidate();
        JPushHelper.sendEventMap(JConstants.CUSTOM_MESSAGE_EVENT, Collections.<String, Object>emptyMap());
        check(main.emittedEvents.size() == 1 && bg.emittedEvents.size() == 1, "JCore retains its own context after JPush invalidation");
        mainCore.invalidate(); mainCore.invalidate();
        JPushHelper.sendEventMap(JConstants.CUSTOM_MESSAGE_EVENT, Collections.<String, Object>emptyMap());
        check(main.emittedEvents.size() == 1 && bg.emittedEvents.size() == 2, "Destroying main's two owners must leave bg delivery active");
        bgPush.invalidate(); bgCore.invalidate();
        check(JPushModule.getActiveReactContexts().isEmpty(), "Each context must release after its final owner invalidates");
        JPushHelper.clearNitroListeners();

        AtomicReference<JPushModule> victim = new AtomicReference<JPushModule>();
        AtomicInteger firstCalls = new AtomicInteger();
        ReactApplicationContext first = new ReactApplicationContext() {
            @Override @SuppressWarnings("unchecked")
            public <T extends com.facebook.react.bridge.JavaScriptModule> T getJSModule(Class<T> type) {
                return (T) new com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter() {
                    @Override public void emit(String name, Object payload) { firstCalls.incrementAndGet(); victim.get().invalidate(); }
                };
            }
        };
        ReactApplicationContext removed = new ReactApplicationContext(), survivor = new ReactApplicationContext();
        JPushModule firstOwner = new JPushModule(first);
        victim.set(new JPushModule(removed));
        JPushModule survivorOwner = new JPushModule(survivor);
        JPushHelper.sendEventMap(JConstants.CUSTOM_MESSAGE_EVENT, Collections.<String, Object>emptyMap());
        check(firstCalls.get() == 1 && removed.emittedEvents.isEmpty() && survivor.emittedEvents.size() == 1,
            "Removing one native context inside a snapshot must skip it and continue to later live contexts");
        firstOwner.invalidate(); survivorOwner.invalidate();
        JPushModule.clearActiveReactContexts(); JPushHelper.clearNitroListeners();
    }

    private static void writeLoopbackAck(Socket socket, JCoreWireCodec.DecodedFrame request, long uid, int sid,
        int status, byte[] body) throws Exception {
        socket.getOutputStream().write(JCoreWireCodec.encodeResponseFrame(request.command, request.version,
            request.requestId, sid, uid, status, body));
        socket.getOutputStream().flush();
    }

    private static void testStartedHandshakeCancellationOwnership() throws Exception {
        Context context = new Context();
        JCoreTcpEngine engine = JCoreTcpEngine.getInstance();
        engine.stop(context); engine.setAutoReconnectEnabled(false);
        JCoreStorage.setAppKey(context, "started-owner-fixture");
        JCoreStorage.setCollectionAuth(context, true); JCoreStorage.setPushStopped(context, false);
        JCoreStorage.clearRegistration(context);
        CountDownLatch oldRegisterRead = new CountDownLatch(1), currentRegisterRead = new CountDownLatch(1);
        CountDownLatch releaseCurrent = new CountDownLatch(1), finish = new CountDownLatch(1);
        AtomicReference<Throwable> serverError = new AtomicReference<Throwable>();
        try (ServerSocket listener = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"))) {
            listener.setSoTimeout(3000);
            Thread server = new Thread(() -> {
                try (Socket oldSocket = listener.accept()) {
                    oldSocket.setSoTimeout(3000);
                    JCoreWireCodec.DecodedFrame oldRegister = JCoreWireCodec.readClientFrameFromStream(oldSocket.getInputStream(), 0L);
                    check(oldRegister != null && oldRegister.command == JCoreWireCodec.CMD_REGISTER, "Old attempt must really start Register");
                    oldRegisterRead.countDown();
                    try (Socket currentSocket = listener.accept()) {
                        currentSocket.setSoTimeout(3000);
                        JCoreWireCodec.DecodedFrame currentRegister = JCoreWireCodec.readClientFrameFromStream(currentSocket.getInputStream(), 0L);
                        check(currentRegister != null && currentRegister.command == JCoreWireCodec.CMD_REGISTER, "New attempt must own its Register");
                        currentRegisterRead.countDown();
                        try {
                            writeLoopbackAck(oldSocket, oldRegister, 770001L, oldRegister.sessionId, 0,
                                JCoreWireCodec.buildRegisterResponsePayload(770001L, "old-password", "obsolete-rid", "old-device"));
                        } catch (java.io.IOException cancelledSocket) {
                            // The old peer may observe cancellation before its delayed response can be written.
                        }
                        check(releaseCurrent.await(3000L, TimeUnit.MILLISECONDS), "Current Register release missing");
                        writeLoopbackAck(currentSocket, currentRegister, 770002L, currentRegister.sessionId, 0,
                            JCoreWireCodec.buildRegisterResponsePayload(770002L, "new-password", "current-rid", "new-device"));
                        JCoreWireCodec.DecodedFrame login = JCoreWireCodec.readClientFrameFromStream(currentSocket.getInputStream(), 770002L);
                        check(login != null && login.command == JCoreWireCodec.CMD_LOGIN && login.uid == 770002L, "Current login must use its own registration");
                        writeLoopbackAck(currentSocket, login, 770002L, 7702, 0,
                            JCoreWireCodec.buildLoginResponsePayload(7702, 520, "", 1700000000));
                        JCoreWireCodec.DecodedFrame initialHeartbeat = JCoreWireCodec.readClientFrameFromStream(currentSocket.getInputStream(), 770002L);
                        check(initialHeartbeat != null && initialHeartbeat.command == JCoreWireCodec.CMD_HEARTBEAT, "Login must send its initial heartbeat");
                        writeLoopbackAck(currentSocket, initialHeartbeat, 770002L, 7702, 0, new byte[0]);
                        JCoreWireCodec.DecodedFrame heartbeat = JCoreWireCodec.readClientFrameFromStream(currentSocket.getInputStream(), 770002L);
                        check(heartbeat != null && heartbeat.command == JCoreWireCodec.CMD_HEARTBEAT, "New socket must survive obsolete worker teardown");
                        writeLoopbackAck(currentSocket, heartbeat, 770002L, 7702, 0, new byte[0]);
                        check(finish.await(3000L, TimeUnit.MILLISECONDS), "Ownership fixture finish missing");
                    }
                } catch (Throwable error) { serverError.set(error); }
            }, "test-started-attempt-ownership");
            server.setDaemon(true); server.start();
            try {
                engine.setCustomEndpoint("127.0.0.1", listener.getLocalPort()); engine.setDefaultRequestTimeoutMs(2000L);
                engine.start(context);
                check(oldRegisterRead.await(3000L, TimeUnit.MILLISECONDS), "Old registration did not reach localhost");
                java.lang.reflect.Field latchField = JCoreTcpEngine.class.getDeclaredField("handshakeLatch"); latchField.setAccessible(true);
                CountDownLatch oldLatch = (CountDownLatch) latchField.get(engine);
                engine.stop(context); engine.start(context);
                CountDownLatch currentLatch = (CountDownLatch) latchField.get(engine);
                check(currentRegisterRead.await(3000L, TimeUnit.MILLISECONDS), "Current registration did not reach localhost");
                check(oldLatch.await(1000L, TimeUnit.MILLISECONDS), "Cancelled old handshake must complete its own wait");
                check(!currentLatch.await(100L, TimeUnit.MILLISECONDS) && JCoreStorage.getUid(context) == 0L,
                    "Old success/failure must not finish the current latch or commit an obsolete UID");
                releaseCurrent.countDown();
                check(currentLatch.await(3000L, TimeUnit.MILLISECONDS), "Current handshake must finish after its own ACK");
                check(engine.isLoggedIn() && JCoreStorage.getUid(context) == 770002L && "current-rid".equals(JCoreStorage.getRegistrationId(context)),
                    "Current credentials and connection must survive the old attempt's completion");
                check(engine.sendRequest(JCoreWireCodec.CMD_HEARTBEAT, JCoreWireCodec.VER_HEARTBEAT, new byte[0], 1000L) != null,
                    "Current socket must still exchange a real ACK");
            } finally {
                releaseCurrent.countDown(); finish.countDown(); engine.stop(context); server.join(3000L);
            }
            check(serverError.get() == null && !server.isAlive(), "Started ownership fixture failed: " + serverError.get());
        }
    }

    private static void testPreLoginFifoAndAcknowledgedMutations() throws Exception {
        Context context = new Context();
        JCoreTcpEngine engine = JCoreTcpEngine.getInstance();
        engine.stop(context); engine.setAutoReconnectEnabled(false); engine.setDefaultRequestTimeoutMs(200L);
        JCoreStorage.setAppKey(context, "fifo-operation-fixture"); JCoreStorage.setCollectionAuth(context, true); JCoreStorage.setPushStopped(context, false);
        JCoreStorage.updateRegistration(context, 780001L, "fifo-password", "fifo-rid", "fifo-device");
        JCoreStorage.setServerRegistered(context, true);
        JCoreStorage.saveTags(context, new java.util.HashSet<String>(Arrays.asList("base")));
        JCoreStorage.saveProperties(context, Collections.<String, Object>singletonMap("tier", "old"));
        CountDownLatch loginRead = new CountDownLatch(1), releaseLogin = new CountDownLatch(1);
        CountDownLatch firstRead = new CountDownLatch(1), fourthQueued = new CountDownLatch(1);
        CountDownLatch firstMutationRead = new CountDownLatch(1), releaseMutations = new CountDownLatch(1), finish = new CountDownLatch(1);
        CountDownLatch fifoDone = new CountDownLatch(4), checkWriter = new CountDownLatch(1), noStaleWire = new CountDownLatch(1);
        AtomicInteger writerCancelled = new AtomicInteger();
        AtomicReference<Throwable> serverError = new AtomicReference<Throwable>();
        Map<Integer, Integer> mutationCodes = new java.util.concurrent.ConcurrentHashMap<Integer, Integer>();
        JPushMessageReceiver receiver = new JPushMessageReceiver() {
            private void record(JPushMessage message) { mutationCodes.put(message.getSequence(), message.getErrorCode()); }
            @Override public void onTagOperatorResult(Context c, JPushMessage message) { record(message); }
            @Override public void onPropertyOperatorResult(Context c, JPushMessage message) { record(message); }
        };
        ActionHelper.getInstance().registerReceiver(receiver);
        try (ServerSocket listener = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"))) {
            listener.setSoTimeout(3000);
            Thread server = new Thread(() -> {
                try (Socket socket = listener.accept()) {
                    socket.setSoTimeout(3000);
                    JCoreWireCodec.DecodedFrame login = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 780001L);
                    check(login != null && login.command == JCoreWireCodec.CMD_LOGIN, "FIFO fixture must fast-login");
                    loginRead.countDown(); check(releaseLogin.await(3000L, TimeUnit.MILLISECONDS), "Login release missing");
                    writeLoopbackAck(socket, login, 780001L, 7801, 0, JCoreWireCodec.buildLoginResponsePayload(7801, 520, "", 1700000000));
                    JCoreWireCodec.DecodedFrame initialHeartbeat = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 780001L);
                    check(initialHeartbeat != null && initialHeartbeat.command == JCoreWireCodec.CMD_HEARTBEAT, "FIFO login must send its initial heartbeat");
                    writeLoopbackAck(socket, initialHeartbeat, 780001L, 7801, 0, new byte[0]);
                    for (long rid = 8001L; rid <= 8004L; rid++) {
                        JCoreWireCodec.DecodedFrame request = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 780001L);
                        check(request != null && request.requestId == rid, "Pre-login and concurrent drain must retain FIFO rid " + rid);
                        if (rid == 8001L) { firstRead.countDown(); check(fourthQueued.await(3000L, TimeUnit.MILLISECONDS), "Fourth offer missing"); }
                        writeLoopbackAck(socket, request, 780001L, 7801, 0, new byte[0]);
                    }
                    for (int sequence : new int[]{8201, 8202, 8203, 65537, 8205, 8206}) {
                        JCoreWireCodec.DecodedFrame request = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 780001L);
                        check(request != null && JCoreWireCodec.extractJsonInt(JCoreWireCodec.parseJsonCommandPayload(request.body), "seq", -1) == sequence,
                            "Business requests retain native FIFO order and their public sequence");
                        if (sequence == 65537) check(request.requestId <= 65535L && request.requestId != 65537L, "Wire request ID must remain distinct from a large public sequence");
                        if (sequence == 8201) { firstMutationRead.countDown(); check(releaseMutations.await(3000L, TimeUnit.MILLISECONDS), "Mutation release missing"); }
                        if (sequence != 8206) writeLoopbackAck(socket, request, 780001L, 7801, sequence == 8205 ? 500 : 0, new byte[0]);
                    }
                    check(checkWriter.await(3000L, TimeUnit.MILLISECONDS), "Writer race release missing");
                    socket.setSoTimeout(250);
                    try {
                        socket.getInputStream().read();
                        throw new AssertionError("A request blocked at the writer must not cross a revoked generation after regrant");
                    } catch (java.net.SocketTimeoutException expectedQuiet) { noStaleWire.countDown(); }
                    check(finish.await(3000L, TimeUnit.MILLISECONDS), "FIFO fixture finish missing");
                } catch (Throwable error) { serverError.set(error); }
            }, "test-fifo-acknowledged-mutations");
            server.setDaemon(true); server.start();
            try {
                engine.setCustomEndpoint("127.0.0.1", listener.getLocalPort()); engine.start(context);
                check(loginRead.await(3000L, TimeUnit.MILLISECONDS), "Login must be in flight before enqueuing");
                JCoreTcpEngine.RequestCallback fifoCallback = (frame, status) -> { check(frame != null && status == 0, "FIFO request requires its real ACK"); fifoDone.countDown(); };
                for (long rid = 8001L; rid <= 8002L; rid++) engine.enqueueOrSendRequest(context, JCoreWireCodec.CMD_PROPERTY_OP,
                    JCoreWireCodec.VER_PROPERTY_OP, rid, new byte[0], 1000L, 0, fifoCallback);
                synchronized (engine) {
                    releaseLogin.countDown();
                    engine.enqueueOrSendRequest(context, JCoreWireCodec.CMD_PROPERTY_OP, JCoreWireCodec.VER_PROPERTY_OP, 8003L, new byte[0], 1000L, 0, fifoCallback);
                }
                check(firstRead.await(3000L, TimeUnit.MILLISECONDS), "Login must drain the first queued request");
                engine.enqueueOrSendRequest(context, JCoreWireCodec.CMD_PROPERTY_OP, JCoreWireCodec.VER_PROPERTY_OP, 8004L, new byte[0], 1000L, 0, fifoCallback);
                fourthQueued.countDown(); check(fifoDone.await(3000L, TimeUnit.MILLISECONDS), "Every queued FIFO request must receive an ACK");
                JPushInterface.addTags(context, 8201, Collections.singleton("alpha"));
                check(firstMutationRead.await(3000L, TimeUnit.MILLISECONDS), "First public mutation must reach the server");
                JPushInterface.addTags(context, 8202, Collections.singleton("beta"));
                JPushInterface.deleteTags(context, 8203, Collections.singleton("base"));
                JPushInterface.setProperties(context, 65537, Collections.<String, Object>singletonMap("tier", "accepted"));
                JPushInterface.setProperties(context, 8205, Collections.<String, Object>singletonMap("tier", "rejected"));
                JPushInterface.setProperties(context, 8206, Collections.<String, Object>singletonMap("tier", "timed-out"));
                check(JCoreStorage.getTags(context).equals(Collections.singleton("base")) && "old".equals(JCoreStorage.getProperties(context).get("tier")),
                    "Unacknowledged mutations must not commit optimistic local state");
                releaseMutations.countDown(); awaitCondition(() -> mutationCodes.size() == 6, "All business ACK/rejection/timeout callbacks must finish");
                check(JCoreStorage.getTags(context).equals(new java.util.HashSet<String>(Arrays.asList("alpha", "beta"))),
                    "Concurrent add/delete must commit against the latest acknowledged tags");
                check("accepted".equals(JCoreStorage.getProperties(context).get("tier")) && mutationCodes.get(65537) == 0
                    && mutationCodes.get(8205) == 500 && mutationCodes.get(8206) == 6002,
                    "Rejected or timed-out mutations must not roll back the acknowledged cache value");
                java.lang.reflect.Field attemptField = JCoreTcpEngine.class.getDeclaredField("activeAttempt"); attemptField.setAccessible(true);
                Object attempt = attemptField.get(engine);
                java.lang.reflect.Field writerField = attempt.getClass().getDeclaredField("writerLock"); writerField.setAccessible(true);
                java.lang.reflect.Field slotsField = JCoreTcpEngine.class.getDeclaredField("pendingRequests"); slotsField.setAccessible(true);
                @SuppressWarnings("unchecked") final Map<Long, ?> slots = (Map<Long, ?>) slotsField.get(engine);
                synchronized (writerField.get(attempt)) {
                    engine.enqueueOrSendRequest(context, JCoreWireCodec.CMD_PROPERTY_OP, JCoreWireCodec.VER_PROPERTY_OP, 8300L,
                        JCoreWireCodec.buildPropertyOperationPayload(1, 8300, Collections.<String, Object>singletonMap("tier", "obsolete")),
                        200L, 0, (frame, status) -> {
                            check(frame == null && status == 6002, "Obsolete writer request must complete once with 6002");
                            writerCancelled.incrementAndGet();
                        });
                    awaitCondition(() -> slots.containsKey(8300L), "Request must reach its original connection writer before revocation");
                    cn.jiguang.api.utils.JCollectionAuth.setAuth(context, false);
                    cn.jiguang.api.utils.JCollectionAuth.setAuth(context, true);
                }
                checkWriter.countDown();
                awaitCondition(() -> writerCancelled.get() == 1, "Revoked writer request must finish once after regrant");
                check(noStaleWire.await(2000L, TimeUnit.MILLISECONDS) && engine.isLoggedIn(),
                    "Regrant must preserve the transport while discarding an obsolete blocked write");
            } finally {
                releaseLogin.countDown(); fourthQueued.countDown(); releaseMutations.countDown(); checkWriter.countDown(); finish.countDown(); engine.stop(context);
                ActionHelper.getInstance().unregisterReceiver(receiver); server.join(3000L);
            }
            check(serverError.get() == null && !server.isAlive(), "FIFO fixture failed: " + serverError.get());
        }
    }

    @org.junit.Test
    public void testTerminalConfigurationErrorsRequireAnIdentityChange() throws Exception {
        Context context = new Context();
        JCoreTcpEngine engine = JCoreTcpEngine.getInstance();
        for (int terminalStatus : new int[]{1005, 1008, 1009}) {
            engine.stop(context); engine.setAutoReconnectEnabled(true); engine.setReconnectBaseDelayMs(25L);
            engine.setDefaultRequestTimeoutMs(500L);
            JCoreStorage.setAppKey(context, "terminal-fixture-" + terminalStatus);
            JCoreStorage.setCollectionAuth(context, true); JCoreStorage.setPushStopped(context, false); JCoreStorage.clearRegistration(context);
            CountDownLatch terminalSent = new CountDownLatch(1), quietWindow = new CountDownLatch(1), finish = new CountDownLatch(1);
            AtomicReference<Throwable> serverError = new AtomicReference<Throwable>();
            AtomicInteger cancelledQueue = new AtomicInteger(), queuedCode = new AtomicInteger(-1);
            try (ServerSocket listener = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"))) {
                listener.setSoTimeout(3000);
                Thread server = new Thread(() -> {
                    try {
                        try (Socket rejected = listener.accept()) {
                            rejected.setSoTimeout(2000);
                            JCoreWireCodec.DecodedFrame register = JCoreWireCodec.readClientFrameFromStream(rejected.getInputStream(), 0L);
                            check(register != null && register.command == JCoreWireCodec.CMD_REGISTER, "Terminal fixture must receive Register");
                            writeLoopbackAck(rejected, register, 0L, register.sessionId, terminalStatus, new byte[0]);
                            terminalSent.countDown();
                        }
                        listener.setSoTimeout(250);
                        try (Socket unexpected = listener.accept()) {
                            throw new AssertionError("Terminal configuration must not reconnect before its identity changes");
                        } catch (java.net.SocketTimeoutException expectedQuiet) {
                            quietWindow.countDown();
                        }
                        listener.setSoTimeout(3000);
                        try (Socket recovered = listener.accept()) {
                            recovered.setSoTimeout(2000);
                            JCoreWireCodec.DecodedFrame register = JCoreWireCodec.readClientFrameFromStream(recovered.getInputStream(), 0L);
                            check(register != null && register.command == JCoreWireCodec.CMD_REGISTER, "Changed configuration must start a fresh Register");
                            writeLoopbackAck(recovered, register, 790001L, register.sessionId, 0,
                                JCoreWireCodec.buildRegisterResponsePayload(790001L, "recovered-password", "recovered-rid", "recovered-device"));
                            JCoreWireCodec.DecodedFrame login = JCoreWireCodec.readClientFrameFromStream(recovered.getInputStream(), 790001L);
                            check(login != null && login.command == JCoreWireCodec.CMD_LOGIN, "Changed configuration must complete login");
                            writeLoopbackAck(recovered, login, 790001L, 7901, 0, JCoreWireCodec.buildLoginResponsePayload(7901, 520, "", 1700000000));
                            JCoreWireCodec.DecodedFrame heartbeat = JCoreWireCodec.readClientFrameFromStream(recovered.getInputStream(), 790001L);
                            check(heartbeat != null && heartbeat.command == JCoreWireCodec.CMD_HEARTBEAT, "Recovered login must send its initial heartbeat");
                            writeLoopbackAck(recovered, heartbeat, 790001L, 7901, 0, new byte[0]);
                            check(finish.await(3000L, TimeUnit.MILLISECONDS), "Terminal recovery finish missing");
                        }
                    } catch (Throwable error) { serverError.set(error); }
                }, "test-terminal-configuration-" + terminalStatus);
                server.setDaemon(true); server.start();
                try {
                    engine.setCustomEndpoint("127.0.0.1", listener.getLocalPort());
                    engine.enqueueOrSendRequest(context, JCoreWireCodec.CMD_PROPERTY_OP, JCoreWireCodec.VER_PROPERTY_OP, 64601,
                        JCoreWireCodec.buildPropertyOperationPayload(1, 64601, Collections.<String, Object>singletonMap("value", "terminal-pending")), 1000L, 0,
                        (frame, code) -> { queuedCode.set(code); cancelledQueue.incrementAndGet(); });
                    engine.start(context);
                    check(terminalSent.await(3000L, TimeUnit.MILLISECONDS) && engine.awaitHandshake(2000L), "Terminal handshake must complete its wait");
                    check(!engine.isLoggedIn(), "Terminal handshake must not become logged in");
                    awaitCondition(() -> cancelledQueue.get() == 1, "Terminal configuration must cancel its unbound queue once");
                    check(queuedCode.get() == 6002 && engine.getPreLoginQueueSize() == 0, "Terminal queue cannot be preserved");
                    engine.start(context); engine.triggerReconnectNow(context); engine.onNetworkConnected(context);
                    check(quietWindow.await(2000L, TimeUnit.MILLISECONDS), "Terminal status must suppress automatic and explicit retry");
                    JCoreStorage.setAppKey(context, "terminal-fixture-" + terminalStatus + "-changed");
                    engine.start(context);
                    check(engine.awaitHandshake(2000L) && engine.isLoggedIn() && JCoreStorage.getUid(context) == 790001L,
                        "Changing configuration identity must permit registration and login again");
                    check(cancelledQueue.get() == 1 && engine.getPreLoginQueueSize() == 0, "Terminal work must not replay after configuration recovery");
                } finally {
                    finish.countDown(); engine.stop(context); server.join(3000L);
                }
                check(serverError.get() == null && !server.isAlive(), "Terminal fixture failed: " + serverError.get());
            }
        }
        engine.setAutoReconnectEnabled(true); engine.setReconnectBaseDelayMs(3000L);
    }

    @org.junit.Test
    public void testObsoleteConfigurationFailureCannotBlockNewConfiguration() throws Exception {
        Context context = new Context();
        JCoreTcpEngine engine = JCoreTcpEngine.getInstance();
        engine.stop(context); engine.setAutoReconnectEnabled(false); engine.setDefaultRequestTimeoutMs(200L);
        JCoreStorage.setAppKey(context, "configuration-A-fixture");
        JCoreStorage.setCollectionAuth(context, true); JCoreStorage.setPushStopped(context, false); JCoreStorage.clearRegistration(context);
        CountDownLatch oldRead = new CountDownLatch(1), releaseOldFailure = new CountDownLatch(1), finish = new CountDownLatch(1);
        AtomicReference<Throwable> serverError = new AtomicReference<Throwable>();
        try (ServerSocket listener = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"))) {
            listener.setSoTimeout(3000);
            Thread server = new Thread(() -> {
                try {
                    try (Socket oldSocket = listener.accept()) {
                        oldSocket.setSoTimeout(2000);
                        JCoreWireCodec.DecodedFrame oldRegister = JCoreWireCodec.readClientFrameFromStream(oldSocket.getInputStream(), 0L);
                        check(oldRegister != null && oldRegister.command == JCoreWireCodec.CMD_REGISTER, "Configuration A must really begin Register");
                        oldRead.countDown(); check(releaseOldFailure.await(2000L, TimeUnit.MILLISECONDS), "Configuration change release missing");
                        writeLoopbackAck(oldSocket, oldRegister, 0L, oldRegister.sessionId, 1005, new byte[0]);
                    }
                    try (Socket currentSocket = listener.accept()) {
                        currentSocket.setSoTimeout(2000);
                        JCoreWireCodec.DecodedFrame register = JCoreWireCodec.readClientFrameFromStream(currentSocket.getInputStream(), 0L);
                        check(register != null && register.command == JCoreWireCodec.CMD_REGISTER, "Configuration B must be allowed to register");
                        writeLoopbackAck(currentSocket, register, 795001L, register.sessionId, 0,
                            JCoreWireCodec.buildRegisterResponsePayload(795001L, "configuration-B-password", "configuration-B-rid", "configuration-B-device"));
                        JCoreWireCodec.DecodedFrame login = JCoreWireCodec.readClientFrameFromStream(currentSocket.getInputStream(), 795001L);
                        check(login != null && login.command == JCoreWireCodec.CMD_LOGIN, "Configuration B must complete login");
                        writeLoopbackAck(currentSocket, login, 795001L, 7951, 0, JCoreWireCodec.buildLoginResponsePayload(7951, 520, "", 1700000000));
                        JCoreWireCodec.DecodedFrame heartbeat = JCoreWireCodec.readClientFrameFromStream(currentSocket.getInputStream(), 795001L);
                        check(heartbeat != null && heartbeat.command == JCoreWireCodec.CMD_HEARTBEAT, "Configuration B must send its own heartbeat");
                        writeLoopbackAck(currentSocket, heartbeat, 795001L, 7951, 0, new byte[0]);
                        check(finish.await(3000L, TimeUnit.MILLISECONDS), "Configuration fixture finish missing");
                    }
                } catch (Throwable error) { serverError.set(error); }
            }, "test-configuration-owner-failure");
            server.setDaemon(true); server.start();
            try {
                engine.setCustomEndpoint("127.0.0.1", listener.getLocalPort()); engine.start(context);
                check(oldRead.await(2000L, TimeUnit.MILLISECONDS), "Configuration A Register did not reach localhost");
                JCoreStorage.setAppKey(context, "configuration-B-fixture"); releaseOldFailure.countDown();
                check(engine.awaitHandshake(2000L), "Configuration A must finish its own failed wait");
                check(!engine.isLoggedIn() && JCoreStorage.getUid(context) == 0L, "Obsolete configuration must not commit credentials");
                engine.start(context);
                check(engine.awaitHandshake(2000L) && engine.isLoggedIn() && JCoreStorage.getUid(context) == 795001L,
                    "A terminal reply for configuration A must not mark B terminal or reserve its start");
            } finally {
                releaseOldFailure.countDown(); finish.countDown(); engine.stop(context); server.join(3000L);
                engine.setAutoReconnectEnabled(true); engine.setDefaultRequestTimeoutMs(5000L);
            }
            check(serverError.get() == null && !server.isAlive(), "Configuration fixture failed: " + serverError.get());
        }
    }

    @org.junit.Test
    public void testNetworkReconnectPreservesUnboundPublicOperations() throws Exception {
        Context context = new Context();
        JCoreTcpEngine engine = JCoreTcpEngine.getInstance();
        JCoreStorage.setPushStopped(context, false); engine.stop(context);
        engine.setAutoReconnectEnabled(false); engine.setDefaultRequestTimeoutMs(1000L);
        JCoreStorage.setAppKey(context, "network-prelogin-fixture"); JCoreStorage.setCollectionAuth(context, true);
        JCoreStorage.updateRegistration(context, 840001L, "prelogin-password", "prelogin-rid", "prelogin-device");
        JCoreStorage.setServerRegistered(context, true);
        CountDownLatch oldLoginRead = new CountDownLatch(1), newLoginRead = new CountDownLatch(1);
        CountDownLatch releaseLogin = new CountDownLatch(1), finish = new CountDownLatch(1), callbacks = new CountDownLatch(4);
        AtomicReference<Throwable> serverError = new AtomicReference<Throwable>();
        Map<Integer, Integer> codes = new java.util.concurrent.ConcurrentHashMap<Integer, Integer>();
        Map<Integer, AtomicInteger> counts = new java.util.concurrent.ConcurrentHashMap<Integer, AtomicInteger>();
        List<Integer> commands = new java.util.concurrent.CopyOnWriteArrayList<Integer>();
        JPushMessageReceiver receiver = new JPushMessageReceiver() {
            private void record(JPushMessage message) {
                int sequence = message.getSequence();
                if (sequence < 64001 || sequence > 64004) return;
                counts.computeIfAbsent(sequence, key -> new AtomicInteger()).incrementAndGet();
                codes.put(sequence, message.getErrorCode()); callbacks.countDown();
            }
            @Override public void onAliasOperatorResult(Context c, JPushMessage message) { record(message); }
            @Override public void onTagOperatorResult(Context c, JPushMessage message) { record(message); }
            @Override public void onPropertyOperatorResult(Context c, JPushMessage message) { record(message); }
            @Override public void onMobileNumberOperatorResult(Context c, JPushMessage message) { record(message); }
        };
        ActionHelper.getInstance().registerReceiver(receiver);
        try (ServerSocket listener = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"))) {
            listener.setSoTimeout(3000);
            Thread server = new Thread(() -> {
                try (Socket oldSocket = listener.accept()) {
                    oldSocket.setSoTimeout(3000);
                    JCoreWireCodec.DecodedFrame oldLogin = JCoreWireCodec.readClientFrameFromStream(oldSocket.getInputStream(), 840001L);
                    check(oldLogin != null && oldLogin.command == JCoreWireCodec.CMD_LOGIN, "First connection must hold its real Login");
                    oldLoginRead.countDown();
                    try (Socket socket = listener.accept()) {
                        socket.setSoTimeout(3000);
                        JCoreWireCodec.DecodedFrame login = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 840001L);
                        check(login != null && login.command == JCoreWireCodec.CMD_LOGIN, "Network reconnect must issue its own Login");
                        newLoginRead.countDown();
                        check(releaseLogin.await(3000L, TimeUnit.MILLISECONDS), "Reconnect Login release missing");
                        if (finish.getCount() == 0) return;
                        writeLoopbackAck(socket, login, 840001L, 8401, 0, JCoreWireCodec.buildLoginResponsePayload(8401, 520, "", 1700000000));
                        JCoreWireCodec.DecodedFrame heartbeat = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 840001L);
                        check(heartbeat != null && heartbeat.command == JCoreWireCodec.CMD_HEARTBEAT, "Reconnect must ACK its initial heartbeat");
                        writeLoopbackAck(socket, heartbeat, 840001L, 8401, 0, new byte[0]);
                        for (int index = 0; index < 4; index++) {
                            JCoreWireCodec.DecodedFrame request = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 840001L);
                            check(request != null, "Preserved request must reach the new connection"); commands.add(request.command);
                            if (index < 3) check(JCoreWireCodec.extractJsonInt(JCoreWireCodec.parseJsonCommandPayload(request.body), "seq", -1) == 64001 + index,
                                "Preserved public sequence must remain unchanged");
                            else check("15500000000".equals(JCoreWireCodec.parseMobileNumberPayload(request.body).mobileNumber), "Preserved mobile payload changed");
                            writeLoopbackAck(socket, request, 840001L, 8401, 0, new byte[0]);
                        }
                        check(finish.await(3000L, TimeUnit.MILLISECONDS), "Reconnect fixture finish missing");
                    }
                } catch (Throwable error) { serverError.set(error); }
            }, "test-network-prelogin-preservation");
            server.setDaemon(true); server.start();
            try {
                engine.setCustomEndpoint("127.0.0.1", listener.getLocalPort()); engine.start(context);
                check(oldLoginRead.await(3000L, TimeUnit.MILLISECONDS), "Initial Login did not reach localhost");
                JPushInterface.setAlias(context, 64001, "preserved-alias");
                JPushInterface.addTags(context, 64002, Collections.singleton("preserved-tag"));
                JPushInterface.setProperties(context, 64003, Collections.<String, Object>singletonMap("region", "local"));
                JPushInterface.setMobileNumber(context, 64004, "15500000000");
                check(engine.getPreLoginQueueSize() == 4, "Four public requests must queue before Login");
                engine.onNetworkConnected(context);
                check(newLoginRead.await(3000L, TimeUnit.MILLISECONDS), "Reconnect Login did not reach localhost");
                check(engine.getPreLoginQueueSize() == 4, "Network reconnect must preserve four unbound requests; actual=" + engine.getPreLoginQueueSize());
                check(codes.isEmpty(), "Unbound requests must not fail before the replacement Login");
                releaseLogin.countDown(); check(callbacks.await(3000L, TimeUnit.MILLISECONDS), "Preserved requests need real ACK callbacks");
                check(commands.equals(Arrays.asList(JCoreWireCodec.CMD_ALIAS_OP, JCoreWireCodec.CMD_TAG_OP,
                    JCoreWireCodec.CMD_PROPERTY_OP, JCoreWireCodec.CMD_MOBILE_NUMBER)), "Reconnect must retain public FIFO order");
                for (int sequence = 64001; sequence <= 64004; sequence++) check(codes.get(sequence) == 0 && counts.get(sequence).get() == 1,
                    "Each preserved public request requires exactly one successful ACK callback");
                engine.onNetworkConnected(context);
                check(engine.isLoggedIn() && engine.awaitHandshake(0L), "A network callback after Login must leave the completed owner alone");
            } finally {
                finish.countDown(); releaseLogin.countDown(); engine.stop(context); server.join(3000L);
                ActionHelper.getInstance().unregisterReceiver(receiver);
                engine.setAutoReconnectEnabled(true); engine.setDefaultRequestTimeoutMs(5000L);
            }
            check(serverError.get() == null && !server.isAlive(), "Network reconnect fixture failed: " + serverError.get());
        }
    }

    private static void runReconnectOwnerDrainIsolation(final boolean blockOldCallback) throws Exception {
        Context context = new Context(); JCoreTcpEngine engine = JCoreTcpEngine.getInstance();
        JCoreStorage.setPushStopped(context, false); engine.stop(context);
        engine.setAutoReconnectEnabled(false); engine.setDefaultRequestTimeoutMs(3000L);
        JCoreStorage.setAppKey(context, "drain-handoff-" + blockOldCallback); JCoreStorage.setCollectionAuth(context, true);
        JCoreStorage.updateRegistration(context, 850001L, "drain-password", "drain-rid", "drain-device");
        JCoreStorage.setServerRegistered(context, true);
        CountDownLatch firstRead = new CountDownLatch(1), oldEntered = new CountDownLatch(1), releaseOld = new CountDownLatch(1), oldExited = new CountDownLatch(1);
        CountDownLatch newEntered = new CountDownLatch(1), releaseNew = new CountDownLatch(1), fourthQueued = new CountDownLatch(1);
        CountDownLatch noParallelPoll = new CountDownLatch(1), fourthDone = new CountDownLatch(1), finish = new CountDownLatch(1);
        AtomicReference<Throwable> serverError = new AtomicReference<Throwable>(), callbackError = new AtomicReference<Throwable>();
        Map<Integer, Integer> codes = new java.util.concurrent.ConcurrentHashMap<Integer, Integer>();
        Map<Integer, AtomicInteger> counts = new java.util.concurrent.ConcurrentHashMap<Integer, AtomicInteger>();
        java.util.function.IntFunction<JCoreTcpEngine.RequestCallback> callback = sequence -> (frame, code) -> {
            counts.computeIfAbsent(sequence, key -> new AtomicInteger()).incrementAndGet(); codes.put(sequence, code);
            if (code == 0 && frame == null) callbackError.set(new AssertionError("Success requires a real ACK frame"));
            try {
                if (sequence == 65101) { oldEntered.countDown(); if (blockOldCallback && !releaseOld.await(3000L, TimeUnit.MILLISECONDS)) callbackError.set(new AssertionError("Old callback release missing")); }
                if (sequence == 65103) { newEntered.countDown(); if (!releaseNew.await(3000L, TimeUnit.MILLISECONDS)) callbackError.set(new AssertionError("New callback release missing")); }
            } catch (InterruptedException error) { Thread.currentThread().interrupt(); callbackError.set(error); }
            finally { if (sequence == 65101) oldExited.countDown(); if (sequence == 65104) fourthDone.countDown(); }
        };
        try (ServerSocket listener = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"))) {
            listener.setSoTimeout(3000);
            Thread server = new Thread(() -> {
                try (Socket oldSocket = listener.accept()) {
                    oldSocket.setSoTimeout(3000);
                    JCoreWireCodec.DecodedFrame login = JCoreWireCodec.readClientFrameFromStream(oldSocket.getInputStream(), 850001L);
                    writeLoopbackAck(oldSocket, login, 850001L, 8501, 0, JCoreWireCodec.buildLoginResponsePayload(8501, 520, "", 1700000000));
                    JCoreWireCodec.DecodedFrame heartbeat = JCoreWireCodec.readClientFrameFromStream(oldSocket.getInputStream(), 850001L);
                    check(heartbeat.command == JCoreWireCodec.CMD_HEARTBEAT, "Old drain login heartbeat missing");
                    writeLoopbackAck(oldSocket, heartbeat, 850001L, 8501, 0, new byte[0]);
                    JCoreWireCodec.DecodedFrame first = JCoreWireCodec.readClientFrameFromStream(oldSocket.getInputStream(), 850001L);
                    check(JCoreWireCodec.extractJsonInt(JCoreWireCodec.parseJsonCommandPayload(first.body), "seq", -1) == 65101, "Old owner must send its first request once");
                    if (blockOldCallback) writeLoopbackAck(oldSocket, first, 850001L, 8501, 0, new byte[0]);
                    firstRead.countDown();
                    try (Socket socket = listener.accept()) {
                        socket.setSoTimeout(3000);
                        login = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 850001L);
                        writeLoopbackAck(socket, login, 850001L, 8502, 0, JCoreWireCodec.buildLoginResponsePayload(8502, 520, "", 1700000000));
                        heartbeat = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 850001L);
                        check(heartbeat.command == JCoreWireCodec.CMD_HEARTBEAT, "New drain login heartbeat missing");
                        writeLoopbackAck(socket, heartbeat, 850001L, 8502, 0, new byte[0]);
                        JCoreWireCodec.DecodedFrame third = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 850001L);
                        check(JCoreWireCodec.extractJsonInt(JCoreWireCodec.parseJsonCommandPayload(third.body), "seq", -1) == 65103,
                            "Only the fresh unbound request may reach the new owner; no old request replay");
                        writeLoopbackAck(socket, third, 850001L, 8502, 0, new byte[0]);
                        check(fourthQueued.await(3000L, TimeUnit.MILLISECONDS), "Fourth request was not queued");
                        socket.setSoTimeout(150);
                        try { socket.getInputStream().read(); throw new AssertionError("An obsolete worker must not poll while the new callback owns the drain"); }
                        catch (java.net.SocketTimeoutException expectedQuiet) { noParallelPoll.countDown(); }
                        socket.setSoTimeout(3000);
                        JCoreWireCodec.DecodedFrame fourth = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 850001L);
                        check(JCoreWireCodec.extractJsonInt(JCoreWireCodec.parseJsonCommandPayload(fourth.body), "seq", -1) == 65104, "The current drainer must retain its next FIFO request");
                        writeLoopbackAck(socket, fourth, 850001L, 8502, 0, new byte[0]);
                        check(finish.await(3000L, TimeUnit.MILLISECONDS), "Drain handoff finish missing");
                    }
                } catch (Throwable error) { serverError.set(error); }
            }, "test-drain-owner-handoff-" + blockOldCallback);
            server.setDaemon(true); server.start();
            try {
                engine.setCustomEndpoint("127.0.0.1", listener.getLocalPort()); engine.start(context);
                check(engine.awaitHandshake(3000L) && engine.isLoggedIn(), "Old drain owner must log in");
                engine.enqueueOrSendRequest(context, JCoreWireCodec.CMD_PROPERTY_OP, JCoreWireCodec.VER_PROPERTY_OP, 65101,
                    JCoreWireCodec.buildPropertyOperationPayload(1, 65101, Collections.<String, Object>singletonMap("value", "old-in-flight")), 3000L, 0, callback.apply(65101));
                check(firstRead.await(3000L, TimeUnit.MILLISECONDS), "Old request did not reach its owner");
                if (blockOldCallback) check(oldEntered.await(3000L, TimeUnit.MILLISECONDS), "Old callback must be in progress");
                engine.enqueueOrSendRequest(context, JCoreWireCodec.CMD_PROPERTY_OP, JCoreWireCodec.VER_PROPERTY_OP, 65102,
                    JCoreWireCodec.buildPropertyOperationPayload(1, 65102, Collections.<String, Object>singletonMap("value", "old-bound")), 3000L, 0, callback.apply(65102));
                engine.closeSocket();
                engine.enqueueOrSendRequest(context, JCoreWireCodec.CMD_PROPERTY_OP, JCoreWireCodec.VER_PROPERTY_OP, 65103,
                    JCoreWireCodec.buildPropertyOperationPayload(1, 65103, Collections.<String, Object>singletonMap("value", "new-unbound")), 3000L, 0, callback.apply(65103));
                engine.onNetworkConnected(context);
                check(newEntered.await(3000L, TimeUnit.MILLISECONDS), "New owner must ACK its queue without waiting for the old callback");
                releaseOld.countDown(); check(oldExited.await(1000L, TimeUnit.MILLISECONDS), "Old callback must finish its own request");
                awaitCondition(() -> codes.containsKey(65102), "Bound queued request must be cancelled");
                check(codes.get(65101) == (blockOldCallback ? 0 : 6002) && codes.get(65102) == 6002 && codes.get(65103) == 0,
                    "Old sent/bound work must finish once without replay; the fresh queue must receive its ACK");
                engine.enqueueOrSendRequest(context, JCoreWireCodec.CMD_PROPERTY_OP, JCoreWireCodec.VER_PROPERTY_OP, 65104,
                    JCoreWireCodec.buildPropertyOperationPayload(1, 65104, Collections.<String, Object>singletonMap("value", "new-tail")), 3000L, 0, callback.apply(65104));
                fourthQueued.countDown(); check(noParallelPoll.await(1000L, TimeUnit.MILLISECONDS), "A stale worker must not clear or steal the new drain token");
                releaseNew.countDown(); check(fourthDone.await(3000L, TimeUnit.MILLISECONDS) && codes.get(65104) == 0, "New drainer must complete its own tail");
                for (int sequence = 65101; sequence <= 65104; sequence++) check(counts.get(sequence).get() == 1, "Callbacks must remain exactly once across reconnect");
                check(callbackError.get() == null, "Drain callback failed: " + callbackError.get());
            } finally {
                releaseOld.countDown(); releaseNew.countDown(); fourthQueued.countDown(); finish.countDown();
                engine.stop(context); server.join(3000L); engine.setAutoReconnectEnabled(true); engine.setDefaultRequestTimeoutMs(5000L);
            }
            check(serverError.get() == null && !server.isAlive(), "Drain ownership fixture failed: " + serverError.get());
        }
    }

    @org.junit.Test
    public void testReconnectDoesNotReplaySentOrBoundRequests() throws Exception { runReconnectOwnerDrainIsolation(false); }

    @org.junit.Test
    public void testReconnectHandsOffDrainWhileOldCallbackIsBlocked() throws Exception { runReconnectOwnerDrainIsolation(true); }

    @org.junit.Test
    public void testReconnectPreservationRejectsInvalidPolicyAndExplicitCancellation() throws Exception {
        Context context = new Context(); JCoreTcpEngine engine = JCoreTcpEngine.getInstance();
        for (int scenario = 0; scenario < 5; scenario++) {
            final int mode = scenario;
            JCoreStorage.setPushStopped(context, false); engine.stop(context);
            engine.setAutoReconnectEnabled(false); engine.setDefaultRequestTimeoutMs(1000L);
            JCoreStorage.setCollectionAuth(context, true); JCoreStorage.setAppKey(context, "queue-policy-" + mode);
            JCoreStorage.updateRegistration(context, 860001L, "policy-password", "policy-rid", "policy-device"); JCoreStorage.setServerRegistered(context, true);
            CountDownLatch quiet = new CountDownLatch(1), freshDone = new CountDownLatch(1), finish = new CountDownLatch(1);
            AtomicInteger cancelled = new AtomicInteger(), cancelCode = new AtomicInteger(-1), freshCode = new AtomicInteger(-1);
            AtomicReference<Throwable> serverError = new AtomicReference<Throwable>();
            try (ServerSocket listener = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"))) {
                listener.setSoTimeout(3000);
                Thread server = new Thread(() -> {
                    try (Socket socket = listener.accept()) {
                        socket.setSoTimeout(3000);
                        JCoreWireCodec.DecodedFrame login = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 860001L);
                        check(login != null && login.command == JCoreWireCodec.CMD_LOGIN, "Policy fixture must login normally");
                        writeLoopbackAck(socket, login, 860001L, 8601, 0, JCoreWireCodec.buildLoginResponsePayload(8601, 520, "", 1700000000));
                        JCoreWireCodec.DecodedFrame heartbeat = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 860001L);
                        check(heartbeat.command == JCoreWireCodec.CMD_HEARTBEAT, "Policy fixture initial heartbeat missing");
                        writeLoopbackAck(socket, heartbeat, 860001L, 8601, 0, new byte[0]);
                        if (mode == 0) {
                            JCoreWireCodec.DecodedFrame resume = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 860001L);
                            check(resume.command == JCoreWireCodec.CMD_PUSH_CTRL, "Public resume control must retain its own semantics");
                            writeLoopbackAck(socket, resume, 860001L, 8601, 0, new byte[0]);
                        }
                        socket.setSoTimeout(150);
                        try { socket.getInputStream().read(); throw new AssertionError("Cancelled or mismatched pre-login body must never reach the replacement connection"); }
                        catch (java.net.SocketTimeoutException expectedQuiet) { quiet.countDown(); }
                        socket.setSoTimeout(3000);
                        JCoreWireCodec.DecodedFrame fresh = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 860001L);
                        check(JCoreWireCodec.extractJsonInt(JCoreWireCodec.parseJsonCommandPayload(fresh.body), "seq", -1) == 65202, "Only the current-policy request may send");
                        writeLoopbackAck(socket, fresh, 860001L, 8601, 0, new byte[0]);
                        check(finish.await(3000L, TimeUnit.MILLISECONDS), "Policy fixture finish missing");
                    } catch (Throwable error) { serverError.set(error); }
                }, "test-queue-policy-" + mode);
                server.setDaemon(true); server.start();
                try {
                    engine.setCustomEndpoint("127.0.0.1", listener.getLocalPort());
                    engine.enqueueOrSendRequest(context, JCoreWireCodec.CMD_PROPERTY_OP, JCoreWireCodec.VER_PROPERTY_OP, 65201,
                        JCoreWireCodec.buildPropertyOperationPayload(1, 65201, Collections.<String, Object>singletonMap("value", "obsolete")), 1000L, 0,
                        (frame, code) -> { cancelCode.set(code); cancelled.incrementAndGet(); });
                    check(engine.getPreLoginQueueSize() == 1, "Policy fixture must start with an unbound request");
                    if (mode == 0) { JPushInterface.stopPush(context); JPushInterface.resumePush(context); }
                    else if (mode == 1) {
                        cn.jiguang.api.utils.JCollectionAuth.setAuth(context, false); cn.jiguang.api.utils.JCollectionAuth.setAuth(context, true);
                        engine.onNetworkConnected(context);
                    } else if (mode == 2) { JCoreStorage.setAppKey(context, "queue-policy-" + mode + "-changed"); engine.onNetworkConnected(context); }
                    else if (mode == 3) engine.triggerReconnectNow(context);
                    else { engine.closeSocket(); engine.onNetworkConnected(context); }
                    check(engine.awaitHandshake(3000L) && engine.isLoggedIn(), "Current policy must still log in");
                    awaitCondition(() -> cancelled.get() == 1, "Invalidated request must receive one cancellation callback");
                    check(cancelCode.get() == 6002 && engine.getPreLoginQueueSize() == 0, "Policy/config/explicit cancellation must remain 6002 with no retained old item");
                    check(quiet.await(2000L, TimeUnit.MILLISECONDS), "Old queue must remain absent after the new login: " + serverError.get());
                    engine.enqueueOrSendRequest(context, JCoreWireCodec.CMD_PROPERTY_OP, JCoreWireCodec.VER_PROPERTY_OP, 65202,
                        JCoreWireCodec.buildPropertyOperationPayload(1, 65202, Collections.<String, Object>singletonMap("value", "current")), 1000L, 0,
                        (frame, code) -> { freshCode.set(frame != null ? code : -1); freshDone.countDown(); });
                    check(freshDone.await(3000L, TimeUnit.MILLISECONDS) && freshCode.get() == 0 && cancelled.get() == 1,
                        "A fresh request must ACK while the old request stays cancelled exactly once");
                } finally {
                    finish.countDown(); JCoreStorage.setPushStopped(context, false); engine.stop(context); server.join(3000L);
                    engine.setAutoReconnectEnabled(true); engine.setDefaultRequestTimeoutMs(5000L);
                }
                check(serverError.get() == null && !server.isAlive(), "Policy queue fixture failed: " + serverError.get());
            }
        }
    }

    private static void runPublicStopCancellationFixture(final int mode) throws Exception {
        final boolean holdWriter = mode == 1 || mode == 3;
        final boolean blockOutput = mode == 2;
        final boolean resumeImmediately = mode == 3;
        Context context = new Context();
        JCoreTcpEngine engine = JCoreTcpEngine.getInstance();
        engine.stop(context); engine.setAutoReconnectEnabled(false); engine.setDefaultRequestTimeoutMs(1000L);
        JCoreStorage.setAppKey(context, "public-stop-fixture-" + mode);
        JCoreStorage.setCollectionAuth(context, true); JCoreStorage.setPushStopped(context, false);
        JCoreStorage.updateRegistration(context, 820001L, "stop-password", "stop-rid", "stop-device");
        JCoreStorage.setServerRegistered(context, true);
        CountDownLatch oldClosed = new CountDownLatch(1), outputEntered = new CountDownLatch(1), releaseOutput = new CountDownLatch(1);
        CountDownLatch outputExited = new CountDownLatch(1), stopReturned = new CountDownLatch(1);
        AtomicInteger stoppedControls = new AtomicInteger();
        AtomicReference<Throwable> serverError = new AtomicReference<Throwable>(), stopError = new AtomicReference<Throwable>();
        java.util.concurrent.atomic.AtomicLong returnedNanos = new java.util.concurrent.atomic.AtomicLong(), closedNanos = new java.util.concurrent.atomic.AtomicLong();
        AtomicReference<Thread> stoppingThread = new AtomicReference<Thread>();
        try (ServerSocket listener = new ServerSocket(0, 5, InetAddress.getByName("127.0.0.1"))) {
            listener.setSoTimeout(3000);
            Thread server = new Thread(() -> {
                try {
                    for (int connection = 0; connection < (resumeImmediately ? 2 : 1); connection++) {
                        try (Socket socket = listener.accept()) {
                            socket.setSoTimeout(3000);
                            JCoreWireCodec.DecodedFrame login = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 820001L);
                            check(login != null && login.command == JCoreWireCodec.CMD_LOGIN, "Public stop fixture must use a real fast login");
                            int sid = 8201 + connection;
                            writeLoopbackAck(socket, login, 820001L, sid, 0, JCoreWireCodec.buildLoginResponsePayload(sid, 520, "", 1700000000));
                            JCoreWireCodec.DecodedFrame initialHeartbeat = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 820001L);
                            check(initialHeartbeat != null && initialHeartbeat.command == JCoreWireCodec.CMD_HEARTBEAT, "Public stop login must ACK its initial heartbeat");
                            writeLoopbackAck(socket, initialHeartbeat, 820001L, sid, 0, new byte[0]);
                            JCoreWireCodec.DecodedFrame request;
                            while ((request = JCoreWireCodec.readClientFrameFromStream(socket.getInputStream(), 820001L)) != null) {
                                if (connection == 0) {
                                    check(mode == 0 && request.command == JCoreWireCodec.CMD_PUSH_CTRL
                                        && Arrays.equals(request.body, JCoreWireCodec.buildPushControlPayload(true))
                                        && request.uid == 820001L && request.sessionId == sid,
                                        "Only a writable old owner may send its captured stopped control before EOF");
                                    stoppedControls.incrementAndGet();
                                } else {
                                    check(request.command == JCoreWireCodec.CMD_HEARTBEAT ||
                                        (request.command == JCoreWireCodec.CMD_PUSH_CTRL && Arrays.equals(request.body, JCoreWireCodec.buildPushControlPayload(false))),
                                        "An obsolete stopped control must not be sent through the resumed connection");
                                    writeLoopbackAck(socket, request, 820001L, sid, 0, new byte[0]);
                                }
                            }
                            if (connection == 0) { closedNanos.set(System.nanoTime()); oldClosed.countDown(); }
                        }
                    }
                } catch (Throwable error) { serverError.set(error); }
            }, "test-public-stop-" + mode);
            server.setDaemon(true); server.start();
            try {
                engine.setCustomEndpoint("127.0.0.1", listener.getLocalPort()); engine.start(context);
                check(engine.awaitHandshake(2000L) && engine.isLoggedIn(), "Public stop fixture must complete login before stopping");
                java.lang.reflect.Field attemptField = JCoreTcpEngine.class.getDeclaredField("activeAttempt"); attemptField.setAccessible(true);
                final Object oldAttempt = attemptField.get(engine);
                java.lang.reflect.Field writerField = oldAttempt.getClass().getDeclaredField("writerLock"); writerField.setAccessible(true);
                java.lang.reflect.Field outputField = oldAttempt.getClass().getDeclaredField("output"); outputField.setAccessible(true);
                if (blockOutput) {
                    final OutputStream original = (OutputStream) outputField.get(oldAttempt);
                    outputField.set(oldAttempt, new OutputStream() {
                        @Override public void write(int value) throws java.io.IOException { write(new byte[]{(byte) value}, 0, 1); }
                        @Override public void write(byte[] bytes, int offset, int length) throws java.io.IOException {
                            outputEntered.countDown();
                            try {
                                if (!releaseOutput.await(2000L, TimeUnit.MILLISECONDS)) throw new java.io.IOException("Fixture output release missing");
                                original.write(bytes, offset, length);
                            } catch (InterruptedException interrupted) {
                                Thread.currentThread().interrupt(); throw new java.io.IOException(interrupted);
                            } finally { outputExited.countDown(); }
                        }
                        @Override public void flush() throws java.io.IOException { original.flush(); }
                    });
                }
                java.util.concurrent.Callable<Void> exercise = () -> {
                    final long requestedNanos = System.nanoTime();
                    Thread stop = new Thread(() -> {
                        long enteredNanos = System.nanoTime();
                        try { JPushInterface.stopPush(context); }
                        catch (Throwable error) { stopError.set(error); }
                        finally { returnedNanos.set(System.nanoTime() - enteredNanos); stopReturned.countDown(); }
                    }, "test-public-stop-caller-" + mode);
                    stoppingThread.set(stop); stop.setDaemon(true); stop.start();
                    check(stopReturned.await(150L, TimeUnit.MILLISECONDS) && stopError.get() == null,
                        "Public stop must return without waiting for its writer/output: " + stopError.get());
                    check(returnedNanos.get() < TimeUnit.MILLISECONDS.toNanos(150L) && JPushInterface.isPushStopped(context)
                        && !engine.isLoggedIn() && !JCoreStorage.isConnected(context), "Public stop must disable local connected state immediately");
                    if (blockOutput) check(outputEntered.await(100L, TimeUnit.MILLISECONDS), "Control write must actually enter the blocked fixture output");
                    if (resumeImmediately) {
                        JPushInterface.resumePush(context);
                        check(engine.awaitHandshake(2000L) && engine.isLoggedIn() && attemptField.get(engine) != oldAttempt,
                            "Immediate public resume must establish a new owner while the old writer remains blocked");
                        Thread.sleep(300L);
                        check(engine.isLoggedIn(), "The old stop deadline must not close the resumed owner");
                    }
                    check(oldClosed.await(750L, TimeUnit.MILLISECONDS), "Public stop deadline must close the old peer without writer progress: " + serverError.get());
                    check(closedNanos.get() - requestedNanos < TimeUnit.MILLISECONDS.toNanos(750L),
                        "Old socket close must fit the private 200 ms grace plus host scheduling allowance");
                    check(stoppedControls.get() == (mode == 0 ? 1 : 0), "Stopped control must be sent once only on the writable ordinary path");
                    System.out.println("[PASS] public stop mode=" + mode + " returnMs=" + TimeUnit.NANOSECONDS.toMillis(returnedNanos.get())
                        + " closeMs=" + TimeUnit.NANOSECONDS.toMillis(closedNanos.get() - requestedNanos));
                    return null;
                };
                if (holdWriter) { synchronized (writerField.get(oldAttempt)) { exercise.call(); } }
                else exercise.call();
                releaseOutput.countDown();
                if (blockOutput) check(outputExited.await(1000L, TimeUnit.MILLISECONDS), "Blocked control writer must be released after the deadline check");
                if (resumeImmediately) {
                    Thread.sleep(50L);
                    check(engine.sendRequest(JCoreWireCodec.CMD_HEARTBEAT, JCoreWireCodec.VER_HEARTBEAT, new byte[0], 1000L) != null
                        && engine.isLoggedIn(), "Late old sender cleanup must leave the resumed socket able to exchange a real ACK");
                }
            } finally {
                releaseOutput.countDown();
                JCoreStorage.setPushStopped(context, false); engine.stop(context);
                Thread stop = stoppingThread.get(); if (stop != null) stop.join(1000L);
                server.join(3000L); engine.setAutoReconnectEnabled(true); engine.setDefaultRequestTimeoutMs(5000L);
            }
            check(serverError.get() == null && !server.isAlive(), "Public stop fixture failed: " + serverError.get());
        }
    }

    @org.junit.Test
    public void testPublicStopWritableControlPrecedesClose() throws Exception { runPublicStopCancellationFixture(0); }

    @org.junit.Test
    public void testPublicStopDeadlineDoesNotWaitForWriterLock() throws Exception { runPublicStopCancellationFixture(1); }

    @org.junit.Test
    public void testPublicStopDeadlineDoesNotWaitForControlWrite() throws Exception { runPublicStopCancellationFixture(2); }

    @org.junit.Test
    public void testPublicStopResumeIsolatesOldSenderAndDeadline() throws Exception { runPublicStopCancellationFixture(3); }

    @org.junit.Test
    public void testBoundedQueueAndNativeListenerOwnership() throws Exception {
        try { testQueueBudgetAndListenerOwnership(); }
        finally {
            JCoreTcpEngine.getInstance().stop(null);
            JCoreTcpEngine.getInstance().setAutoReconnectEnabled(true);
            JCoreTcpEngine.getInstance().setDefaultRequestTimeoutMs(5000L);
        }
    }

    @org.junit.Test
    public void testStartedHandshakeOwnerIsolation() throws Exception {
        try { testStartedHandshakeCancellationOwnership(); }
        finally {
            JCoreTcpEngine.getInstance().stop(null);
            JCoreTcpEngine.getInstance().setAutoReconnectEnabled(true);
            JCoreTcpEngine.getInstance().setDefaultRequestTimeoutMs(5000L);
        }
    }

    @org.junit.Test
    public void testPreLoginFifoAndRealBusinessAcknowledgements() throws Exception {
        try { testPreLoginFifoAndAcknowledgedMutations(); }
        finally {
            JCoreTcpEngine.getInstance().stop(null);
            JCoreTcpEngine.getInstance().setAutoReconnectEnabled(true);
            JCoreTcpEngine.getInstance().setDefaultRequestTimeoutMs(5000L);
        }
    }

    @org.junit.Test
    public void testSdkHostRegressions() throws Exception {
        testCryptoParity();
        testUdpSis10ByteAndAsymmetricTcpWireCodec();
        testTcpEngineEndToEndAndProductionResilience();
        testHttpReportChannelAndMultiProcessIpc();
        testFcmCallbacksAndPayloadBounds();
        testDefaultHostVerifier();
        testAutoReconnectScheduleAndBinderSecurity();
        testStoppedInitAndRealLoginFcmRebind();
        testContentQueueInvalidation();

    }
}
